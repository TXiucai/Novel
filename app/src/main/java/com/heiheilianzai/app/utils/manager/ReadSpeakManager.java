package com.heiheilianzai.app.utils.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.utils.AssetZipUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.ToastUtil;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import kr.co.voiceware.java.vtapi.Constants;
import kr.co.voiceware.java.vtapi.EngineInfo;
import kr.co.voiceware.java.vtapi.Options;
import kr.co.voiceware.java.vtapi.SyncMarkInfo;
import kr.co.voiceware.java.vtapi.SyncWordInfo;
import kr.co.voiceware.java.vtapi.VoiceText;
import kr.co.voiceware.java.vtapi.VoiceTextListener;
import kr.co.voiceware.vtlicensemodule.LicenseDownloadListener;
import kr.co.voiceware.vtlicensemodule.VtLicenseSetting;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class ReadSpeakManager {
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/D16/";
    private static final String LICENSE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/licensekey/";
    private final static String type = "d16";
    public static final String READ_PITCH = "options_pitch";
    public static final String READ_SPEED = "options_speed";
    public static final String READ_YINSE = "options_yinse";
    private static final String RSM_LICENSE_KEY = "rsm_license_key";
    //????????????3?????????????????????????????????????????????????????????
    private static String LICENSE_KEY;
    private Context context;

    private VoiceText voicetext = null;
    private long vtapiHandle = 0;
    private Options mOptions = null;
    private EngineInfo selectedEngine = null;
    private VtLicenseSetting licensemodule = null;
    private List<SyncWordInfo> mWordInfo = new ArrayList<>();
    private int addFrame = 0;
    private int idxInfo = 0;
    private int totalFrame = 0;
    private int endPos = 0;
    private String bookText = null;
    private List<String> ttsFilterList = null;

    AudioTrack mAudioTrack;
    private final int mSampleRate = 16000;
    private int DEFAULT_VOLUME = 100;
    //?????? 50-400
    private int readPitch;
    //?????? 50-400
    private int readSpeed;
    // 1 ?????????0 ?????????
    private int readYinSe;

    public static final int BTN_PLAY = 3;
    public static final int BTN_NEXT = 1;
    public static final int BTN_STOP = 2;
    public static final int TXT_POSITION = 4;
    private int isPause = 1;

    private int retryDownload = 3;

    private ReadSpeakStateCallback readSpeakStateCallback;

    @SuppressLint("StaticFieldLeak")
    private static ReadSpeakManager instance;

    private ReadSpeakManager() {
        context = App.getAppContext();
    }

    public static ReadSpeakManager getInstance() {
        if (instance == null) {
            instance = new ReadSpeakManager();
        }
        return instance;
    }

    /**
     * ?????????????????????????????????
     * 1 ????????????
     * 2 ???????????? ???????????????????????????????????????????????????????????????????????????
     * 3 ????????????
     * 4 ?????????
     */
    public interface ReadSpeakStateCallback {
        void readSpeakState(int state);
    }

    public void setReadSpeakStateCallback(ReadSpeakStateCallback readSpeakStateCallback) {
        this.readSpeakStateCallback = readSpeakStateCallback;
    }

    private final Handler uiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BTN_PLAY) {
                setPlay();
            } else if (msg.what == BTN_NEXT) {
                setNext();
            } else if (msg.what == BTN_STOP) {
                setStop(msg.arg1);
            } else {
                uiHighlightText(msg.arg1, msg.arg2, (String) msg.obj);
            }
        }
    };

    public ReadSpeakManager initReadSetting() {
        initAudioRead();
        checkLicense();
        return this;
    }

    /**
     * ?????????????????????
     */
    private void initAudioRead() {
        int outputBufferSize = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
        if (mAudioTrack != null) {
            return;
        }
        try {
            mAudioTrack = new AudioTrack(AudioManager.USE_DEFAULT_STREAM_TYPE, mSampleRate, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT, outputBufferSize, AudioTrack.MODE_STREAM);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAudioTrack.setVolume(DEFAULT_VOLUME);
            }
            mAudioTrack.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mAudioTrack != null) {

            mAudioTrack.setPlaybackPositionUpdateListener(new AudioTrack.OnPlaybackPositionUpdateListener() {
                @Override
                public void onMarkerReached(AudioTrack track) {
                    mAudioTrack.pause();
                    mAudioTrack.flush();
                    mWordInfo.clear();
                }

                @Override
                public void onPeriodicNotification(AudioTrack track) {
                    if (mWordInfo.size() > 0) {
                        if (idxInfo == 0) {
                            SyncWordInfo tmpinfo = mWordInfo.get(idxInfo);
                            addFrame += tmpinfo.getLength();
                            idxInfo++;
                            Message positionMessage = uiHandler.obtainMessage();
                            positionMessage.what = TXT_POSITION;
                            positionMessage.arg1 = tmpinfo.getStartPosInText();
                            positionMessage.arg2 = tmpinfo.getEndPosInText();
                            uiHandler.sendMessage(positionMessage);
                        } else if (track.getPlaybackHeadPosition() > 0 && (addFrame / 2) > track.getPlaybackHeadPosition()) {
                            SyncWordInfo tmpinfo = mWordInfo.get(idxInfo - 1);
                            Message positionMessage = uiHandler.obtainMessage();
                            positionMessage.what = TXT_POSITION;
                            positionMessage.arg1 = tmpinfo.getStartPosInText();
                            positionMessage.arg2 = tmpinfo.getEndPosInText();
                            uiHandler.sendMessage(positionMessage);
                        } else if ((addFrame / 2) <= track.getPlaybackHeadPosition()) {
                            if (idxInfo < mWordInfo.size()) {
                                SyncWordInfo tmpinfo = mWordInfo.get(idxInfo);
                                if (tmpinfo.getStartPosInText() != 0) {
                                    addFrame += tmpinfo.getLength();
                                    Message positionMessage = uiHandler.obtainMessage();
                                    positionMessage.what = TXT_POSITION;
                                    positionMessage.arg1 = tmpinfo.getStartPosInText();
                                    positionMessage.arg2 = tmpinfo.getEndPosInText();
                                    uiHandler.sendMessage(positionMessage);
                                    idxInfo++;
                                }
                            }
                        }
                    }
                }
            });

        }

        readYinSe = ShareUitls.getInt(App.getAppContext(), READ_YINSE, 0);
        readPitch = ShareUitls.getInt(App.getAppContext(), READ_PITCH, 100);
        readSpeed = ShareUitls.getInt(App.getAppContext(), READ_SPEED, 100);
    }

    /**
     * ?????????????????????????????????
     */
    private void unfileZipFile(Context context, String outPath) {
        //????????????
        try {
            AssetZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * ????????????license key ??????
     * ???????????????????????????
     */
    private void checkLicense() {
        if (context == null) {
            context = App.getAppContext();
        }
        //??????D16???????????????????????????????????????
        File D16File = new File(ROOT_PATH);
        if (!D16File.exists() || !D16File.isDirectory()) {
            String outPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/";
            unfileZipFile(context, outPath);
        }

//        licensemodule = new VtLicenseSetting(context);
        //retry download license
//        licensemodule.resetLicenseFileDownload();

        /*        *//**
         * ?????? license key ???????????????????????? ????????????
         *//*
        LICENSE_KEY = ShareUitls.getString(context, "vtapi_license_key", null);
        String rsmLiscenseKey = ShareUitls.getString(App.getAppContext(), RSM_LICENSE_KEY, null);
        if (TextUtils.isEmpty(LICENSE_KEY)) {
            ToastUtil.getInstance().showShortT(context.getString(R.string.read_license_key_null));
            return;
        }
        if (TextUtils.isEmpty(rsmLiscenseKey) || !rsmLiscenseKey.equalsIgnoreCase(LICENSE_KEY)) {
            licensemodule.resetLicenseFileDownload();
        }

        if (licensemodule.getLicensed()) {
            // licensekey ??????????????????????????????????????????????????????????????????
            String verifyTxtPath = LICENSE_PATH + "verification.txt";
            File verifyFile = new File(verifyTxtPath);
            if (!verifyFile.exists()) {
                //retry download license
                licensemodule.resetLicenseFileDownload();
                downloadLicense();
            }
        } else {
            downloadLicense();
        }*/
        downloadVerification();

    }

    /**
     * ??????license
     */
    private void downloadLicense() {
        LICENSE_KEY = ShareUitls.getString(context, "vtapi_license_key", null);
        if (TextUtils.isEmpty(LICENSE_KEY)) {
            ToastUtil.getInstance().showShortT(context.getString(R.string.read_license_key_null));
            return;
        }
        licensemodule.vtLicenseDownload(LICENSE_KEY, LICENSE_PATH, new LicenseDownloadListener() {
            @Override
            public void onSuccess() {
                ShareUitls.putString(App.getAppContext(), RSM_LICENSE_KEY, LICENSE_KEY);
            }

            @Override
            public void onFailure(String s) {
                retryDownloadLicense();
            }

            @Override
            public void onError(String s) {
                retryDownloadLicense();
            }
        });
    }

    private void retryDownloadLicense() {
        if (retryDownload > 0) {
            retryDownload--;
//            downloadLicense();
            downloadVerification();
        } else {
            ToastUtil.getInstance().showShortT(context.getString(R.string.read_license_download_error));
        }
    }

    /**
     * ???????????? ???????????? ???????????????
     * selected = yafang-d16, name = yafang , type = d16   language = twn
     * selected = hui-d16, name = hui , type = d16         language = chi
     *
     * @param position
     */
    @Deprecated
    public void setYingSe(int position) {
        ShareUitls.putInt(App.getAppContext(), READ_YINSE, position);
        if (null == voicetext) {
            return;
        }
        // SDK ?????????2????????????????????????
        String select = "hui";
        if (position == 0) {
            select = "hui";
        } else {
            select = "yafang";
        }

        //????????????????????? D16????????????
        for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
            if (e.getValue().getSpeaker().equals(select) && e.getValue().getType().equals(type)) {
                selectedEngine = e.getValue();
                break;
            }
        }
    }

    private EngineInfo getYingSe() {
        int yinseInt = getReadYinSe();
        String yinse = yinseInt == 0 ? "hui" : "yafang";

        for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
            if (e.getValue().getSpeaker().equals(yinse) && e.getValue().getType().equals(type)) {
                selectedEngine = e.getValue();
                break;
            }
        }

        return selectedEngine;
    }

    /**
     * load ?????????????????????
     * ?????????????????????????????????
     */
    public ReadSpeakManager load() {
        if (null != voicetext) {
            return this;
        }
        voicetext = new VoiceText();
        if (licensemodule == null) {
            voicetext.vtapiSetLicenseFolder(ROOT_PATH);
        } else {
            // License Module
            voicetext.vtapiSetLicenseFolder(LICENSE_PATH);
            voicetext.vtapiUsingLicensekey(true);
        }
        voicetext.vtapiSetCallbackForLogFilter(0);

        try {
            voicetext.vtapiInit(ROOT_PATH);
            vtapiHandle = voicetext.vtapiCreateHandle();

        } catch (Exception e) {
            e.printStackTrace();
        }

        EngineInfo engineInfo = getYingSe();
        String licensePath = ROOT_PATH + "verification.txt";
        try {
            int isExpired = voicetext.vtapiCheckLicenseFile(engineInfo, licensePath);
            if (isExpired == -51) {
                File file = new File(licensePath);
                if (file.exists()) {
                    file.delete();
                }
                downloadVerification();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return this;
    }

    /**
     * ??????????????????
     */
    public void setReadOptions(Options options) {
        if (options == null) {
            mOptions = new Options();
            try {
                mOptions.setPitch(readPitch);
                mOptions.setSpeed(readSpeed);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            mOptions = options;
        }
    }

    public Options setReadOptionsSetting(Options options) {
        if (options == null) {
            options = new Options();
            try {
                options.setSpeed(getReadSpeed());
                options.setPitch(getReadPitch());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return options;
    }

    private Options getOptions() {
        if (mOptions == null) {
            mOptions = new Options();
        }
        try {
            mOptions.setPitch(readPitch);
            mOptions.setSpeed(readSpeed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mOptions;
    }

    /**
     * resetReader
     */
    public void resetReader() {
        if (null == voicetext) {
            return;
        }
        voicetext.vtapiReleaseHandle(vtapiHandle);
        voicetext.vtapiExit();
        voicetext = null;
        vtapiHandle = 0;

        addFrame = 0;
        idxInfo = 0;
        totalFrame = 0;
    }

    /**
     * play ????????????
     */
    public void playReadBook(String bookContent) {
        if (null == voicetext) {
            return;
        }
        bookText = bookContent;

        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.play();
        }

        // ???????????? ???????????????
        selectedEngine = getYingSe();
        mOptions = getOptions();
        idxInfo = 0;

        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {

                Message msg0 = uiHandler.obtainMessage();
                msg0.what = BTN_PLAY;
                uiHandler.sendMessage(msg0);

                mAudioTrack.setPositionNotificationPeriod(1600);

                totalFrame = 0;
                addFrame = 0;
                idxInfo = 0;

                try {
                    voicetext.vtapiTextToBufferWithSyncWordInfo(vtapiHandle, bookContent, false, false, 0, selectedEngine.getSpeaker(), selectedEngine.getSampling(), selectedEngine.getType(), mOptions, Constants.OutputFormat.FORMAT_16PCM, new VoiceTextListener() {
                        @Override
                        public void onReadBuffer(byte[] output, int outputSize) {
                            if (outputSize > 0) {
                                final ByteBuffer audioData = ByteBuffer.wrap(output);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mAudioTrack.write(audioData, audioData.remaining(), AudioTrack.WRITE_BLOCKING);
                                } else {
                                    mAudioTrack.write(output, 0, outputSize);
                                }
                            }
                        }

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onReadBufferWithWordInfo(byte[] output, int outputSize, List<SyncWordInfo> wordInfo) {
                            if (output != null) {
                                totalFrame += outputSize;
                                mAudioTrack.setNotificationMarkerPosition(totalFrame / 2);
                                //?????????????????????????????? ?????????
                                mWordInfo.addAll(wordInfo);

                                final ByteBuffer audioData = ByteBuffer.wrap(output);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mAudioTrack.write(audioData, audioData.remaining(), AudioTrack.WRITE_BLOCKING);
                                } else {
                                    mAudioTrack.write(output, 0, outputSize);
                                }
                            }
                        }

                        @Override
                        public void onReadBufferWithMarkInfo(byte[] output, int outputSize, List<SyncMarkInfo> markInfo) {

                        }

                        @Override
                        public void onError(String reason) {
                        }
                    });

                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        Message msg1 = uiHandler.obtainMessage();
                        msg1.what = BTN_NEXT;
                        uiHandler.sendMessage(msg1);
                    } else {
                        Message msg2 = uiHandler.obtainMessage();
                        msg2.what = BTN_STOP;
                        msg2.arg1 = isPause;
                        uiHandler.sendMessage(msg2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg2 = uiHandler.obtainMessage();
                    msg2.what = BTN_STOP;
                    msg2.arg1 = 1;
                    uiHandler.sendMessage(msg2);
                }

            }

        });

    }

    /**
     * stop  ????????????
     */
    public void stopReadBook(int from) {
        isPause = from;
        if (from == 1) {
            readSpeakStateCallback.readSpeakState(1);
        }
        if (null != voicetext) {
            voicetext.vtapiStopBuffer(vtapiHandle);
        }
        setPause();
    }

    /**
     * ????????? ??????????????? ??????
     */
    private void setPause() {
        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.pause();
            }
            mAudioTrack.flush();
        }

        mWordInfo.clear();
        addFrame = 0;
        idxInfo = 0;
        totalFrame = 0;

    }

    /**
     * ??????????????????
     *
     * @param sPos ????????????
     * @param ePos ????????????
     */
    public void uiHighlightText(int sPos, int ePos, String content) {
//        SpannableString hSpanStr = new SpannableString(content);
//        //??????????????????
//        hSpanStr.setSpan(new ForegroundColorSpan(Color.RED), sPos, ePos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        //?????????????????????
//        hSpanStr.setSpan(new BackgroundColorSpan(Color.RED), sPos, ePos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvBook.setText(hSpanStr);
        endPos = sPos;

    }

    /**
     * ??????
     *
     * @return
     */
    public int getReadPitch() {
        return readPitch;
    }

    public void setReadPitch(int readPitch) {
        ShareUitls.putInt(App.getAppContext(), ReadSpeakManager.READ_PITCH, readPitch);
        this.readPitch = readPitch;
    }

    /**
     * ??????
     *
     * @return
     */
    public int getReadSpeed() {
        return readSpeed;
    }

    public void setReadSpeed(int readSpeed) {
        ShareUitls.putInt(App.getAppContext(), ReadSpeakManager.READ_SPEED, readSpeed);
        this.readSpeed = readSpeed;
    }

    /**
     * ??????
     * 0 ?????? TWN
     * 1 ????????? CHN
     *
     * @return
     */
    public int getReadYinSe() {
        return readYinSe;
    }

    public void setReadYinSe(int readYinSe) {
        ShareUitls.putInt(App.getAppContext(), READ_YINSE, readYinSe);
        this.readYinSe = readYinSe;
    }

    /**
     * ??????????????????????????????string
     *
     * @param list
     * @return
     */
    public String list2String(List<SyncWordInfo> list) {

        String collect = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            collect = list.stream().map(SyncWordInfo::getWord).collect(Collectors.joining(""));
        }

        return collect;
    }

    private void setPlay() {
        isPause = 0;
        readSpeakStateCallback.readSpeakState(3);
    }

    private void setNext() {
        setPause();
        readSpeakStateCallback.readSpeakState(4);
    }

    /**
     * ??????
     * ????????????????????????
     * ???????????????
     */
    private void setStop(int arg1) {

        setPause();
        if (arg1 == 3) {
            resetOptions();
        }
    }

    private void resetOptions() {
        mOptions = getOptions();
        selectedEngine = getYingSe();

        if (TextUtils.isEmpty(bookText)) {
            return;
        }
        String newText = bookText.substring(endPos);
        if (!TextUtils.isEmpty(newText)) {
            optionsReadBook(newText);
        }
    }

    public void optionsReadBook(String newText) {
        if (null == voicetext) {
            return;
        }

        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.play();
        }

        bookText = newText;

        // ???????????? ???????????????
        selectedEngine = getYingSe();
        mOptions = getOptions();
        idxInfo = 0;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Message msg0 = uiHandler.obtainMessage();
                msg0.what = BTN_PLAY;
                uiHandler.sendMessage(msg0);

                mAudioTrack.setPositionNotificationPeriod(1600);

                totalFrame = 0;
                addFrame = 0;
                idxInfo = 0;
                try {
                    voicetext.vtapiTextToBufferWithSyncWordInfo(vtapiHandle, newText, false, false, 0, selectedEngine.getSpeaker(), selectedEngine.getSampling(), selectedEngine.getType(), mOptions, Constants.OutputFormat.FORMAT_16PCM, new VoiceTextListener() {
                        @Override
                        public void onReadBuffer(byte[] output, int outputSize) {
                            if (outputSize > 0) {
                                final ByteBuffer audioData = ByteBuffer.wrap(output);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mAudioTrack.write(audioData, audioData.remaining(), AudioTrack.WRITE_BLOCKING);
                                } else {
                                    mAudioTrack.write(output, 0, outputSize);
                                }
                            }
                        }

                        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                        @Override
                        public void onReadBufferWithWordInfo(byte[] output, int outputSize, List<SyncWordInfo> wordInfo) {
                            if (output != null) {
                                totalFrame += outputSize;
                                mAudioTrack.setNotificationMarkerPosition(totalFrame / 2);
                                //?????????????????????????????? ?????????
                                mWordInfo.addAll(wordInfo);

                                final ByteBuffer audioData = ByteBuffer.wrap(output);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    mAudioTrack.write(audioData, audioData.remaining(), AudioTrack.WRITE_BLOCKING);
                                } else {
                                    mAudioTrack.write(output, 0, outputSize);
                                }
                            }
                        }

                        @Override
                        public void onReadBufferWithMarkInfo(byte[] output, int outputSize, List<SyncMarkInfo> markInfo) {

                        }

                        @Override
                        public void onError(String reason) {
                        }
                    });

                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        Message msg1 = uiHandler.obtainMessage();
                        msg1.what = BTN_NEXT;
                        uiHandler.sendMessage(msg1);
                    } else {
                        Message msg2 = uiHandler.obtainMessage();
                        msg2.what = BTN_STOP;
                        msg2.arg1 = isPause;
                        uiHandler.sendMessage(msg2);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg2 = uiHandler.obtainMessage();
                    msg2.what = BTN_STOP;
                    msg2.arg1 = 1;
                    uiHandler.sendMessage(msg2);
                }

            }

        }).start();

    }

    /**
     * ???????????????
     */
    public void readPauseBook() {
        resetOptions();
    }

    /**
     * TTS ???????????? ???????????? ??????
     */
    public void setTTSFilterList(List<String> list) {
        this.ttsFilterList = list;
    }

    public List<String> getTtsFilterList() {
        return ttsFilterList;
    }

    private void downloadVerification() {
        final String url = "https://d.iqt.ai/tts/Android/verification.txt";
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                retryDownloadLicense();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    String licensePath = ROOT_PATH;
                    File fileLicense = new File(licensePath);
                    if (!fileLicense.exists()) {
                        fileLicense.mkdir();
                    }
                    String fileName = "verification.txt";
                    File dest = new File(licensePath, fileName);
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }
                }
            }

        });
    }
}
