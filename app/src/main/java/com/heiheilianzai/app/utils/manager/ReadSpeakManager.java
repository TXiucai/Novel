package com.heiheilianzai.app.utils.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.utils.AssetZipUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.ToastUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
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

public class ReadSpeakManager {
    private static final String ROOT_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/D16/";
    private static final String LICENSE_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/licensekey/";
    private final static String type = "d16";
    //购买的，3个月过期，需要续费更换。有接口动态获取
    private static String LICENSE_KEY;
    private Context context;

    private VoiceText voicetext = null;
    private long vtapiHandle = 0;
    private Options mOptions = null;
    private EngineInfo selectedEngine = null;
    private VtLicenseSetting licensemodule = null;
    private List<SyncWordInfo> mWordInfo = new ArrayList<>();
    private List<SyncWordInfo> fWordInfo = new ArrayList<>();
    private List<SyncWordInfo> mWordInfoPause = new ArrayList<>();
    private int addFrame = 0;
    private int idxInfo = 0;
    private int totalFrame = 0;

    AudioTrack mAudioTrack;
    private final int mSampleRate = 16000;
    private int DEFAULT_VOLUME = 100;
    //范围 50-400
    private int readPitch = 100;
    //范围 50-400
    private int readSpeed = 100;
    // 0 台湾，1 普通话
    private int readYinSe = 0;

    private int retryDownload = 3;

    private ReadSpeakStateCallback readSpeakStateCallback;
    ExecutorService readThreadPool;

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
     * 语音读书阅读器状态回调
     * 1 停止读书
     * 2 暂停读书 由于暂停按钮在通知栏，所以这里不做暂停状态回调处理
     * 3 正在读书
     * 4 读完了
     */
    public interface ReadSpeakStateCallback {
        void readSpeakState(int state);
    }

    public void setReadSpeakStateCallback(ReadSpeakStateCallback readSpeakStateCallback) {
        this.readSpeakStateCallback = readSpeakStateCallback;
    }

    public ReadSpeakManager initReadSetting() {
        initAudioRead();
        checkLicense();
        return this;
    }

    /**
     * 初始化默认设置
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
                        } else if (track.getPlaybackHeadPosition() > 0 && (addFrame / 2) > track.getPlaybackHeadPosition()) {
                            SyncWordInfo tmpinfo = mWordInfo.get(idxInfo - 1);

                        } else if ((addFrame / 2) <= track.getPlaybackHeadPosition()) {
                            if (idxInfo < mWordInfo.size()) {
                                SyncWordInfo tmpinfo = mWordInfo.get(idxInfo);
                                if (tmpinfo.getStartPosInText() != 0) {
                                    addFrame += tmpinfo.getLength();

                                    idxInfo++;
                                }
                            }
                        }
                    }
                }
            });

        }
    }

    /**
     * 第一步：解压语音库文件
     */
    private void unfileZipFile(Context context, String outPath) {
        //文件解压
        try {
            AssetZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", outPath);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二步：license key 认证
     * 如果没有，需要下载
     */
    private void checkLicense() {
        if (context == null) {
            context = App.getAppContext();
        }
        //判断D16文件是否存在，不存在就解压
        File D16File = new File(ROOT_PATH);
        if (!D16File.exists() || !D16File.isDirectory()) {
            String outPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ReadSpeaker/";
            unfileZipFile(context, outPath);
        }

        licensemodule = new VtLicenseSetting(context);
        //retry download license
//        licensemodule.resetLicenseFileDownload();

        if (licensemodule.getLicensed()) {
            // licensekey 下的认证文件，如果不小心被删掉了，重新下载。
            String verifyTxtPath = LICENSE_PATH + "verification.txt";
            File verifyFile = new File(verifyTxtPath);
            if (!verifyFile.exists()) {
                //retry download license
                licensemodule.resetLicenseFileDownload();
                downloadLicense();
            }
        } else {
            downloadLicense();
        }
    }

    /**
     * 下载license
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
            downloadLicense();
        } else {
            ToastUtil.getInstance().showShortT(context.getString(R.string.read_license_download_error));
        }
    }

    /**
     * 设置音色 台湾女声 普通话女生
     * selected = yafang-d16, name = yafang , type = d16   language = twn
     * selected = hui-d16, name = hui , type = d16         language = chi
     *
     * @param position
     */
    public void setYingSe(int position) {
        if (null == voicetext) {
            return;
        }
        // SDK 目前就2个语音库，固定的
        String select = "hui";
        if (position == 0) {
            select = "hui";
        } else {
            select = "yafang";
        }

        //目前是只有这个 D16库。写死
        for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
            if (e.getValue().getSpeaker().equals(select) && e.getValue().getType().equals(type)) {
                selectedEngine = e.getValue();
                break;
            }
        }
    }

    private EngineInfo getYingSe() {
        if (selectedEngine == null) {
            String yinse = "hui";
            for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
                if (e.getValue().getSpeaker().equals(yinse) && e.getValue().getType().equals(type)) {
                    selectedEngine = e.getValue();
                    break;
                }
            }
        }
        return selectedEngine;
    }

    /**
     * load 初始化语音读书
     * 每次读书，只需设置一次
     */
    public ReadSpeakManager load() {
        if (null != voicetext) {
            return this;
        }
        voicetext = new VoiceText();
        if (licensemodule == null) {
            voicetext.vtapiSetLicenseFolder(ROOT_PATH + "verify/");
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

        return this;
    }

    /**
     * 读书音频设置
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
     * play 开始播放
     */
    public void playReadBook(String bookContent) {
        if (null == voicetext) {
            return;
        }

        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.play();
        }

        // 设置音色 和语音属性
        selectedEngine = getYingSe();
        mOptions = getOptions();

        if (readThreadPool == null) {
            readThreadPool = Executors.newSingleThreadExecutor();
        }
        readThreadPool.execute(new Runnable() {
            @Override
            public void run() {

                readSpeakStateCallback.readSpeakState(3);

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
                                //首次加载计算内容长度 不可变
                                mWordInfo.addAll(wordInfo);
                                fWordInfo.addAll(wordInfo);

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
                    readSpeakStateCallback.readSpeakState(4);

                } catch (Exception e) {

                }

            }

        });

    }

    /**
     * stop  停止播放
     */
    public void stopReadBook() {
        if (null != voicetext) {
            voicetext.vtapiStopBuffer(vtapiHandle);
        }

        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.stop();
            }
            mAudioTrack.flush();
        }

        mWordInfo.clear();
        addFrame = 0;
        idxInfo = 0;
        totalFrame = 0;

        if (null != readThreadPool) {
            readThreadPool.shutdown();
            readThreadPool.shutdownNow();
            try {
                readThreadPool.awaitTermination(50, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            readThreadPool = null;
        }

    }

    /**
     * 暂停时 主线程事件 处理
     */
    private void setPause() {
        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.pause();
            }
        }
    }

    /**
     * 已读高亮显示
     *
     * @param sPos 起始位置
     * @param ePos 结束位置
     */
    public void uiHighlightText(int sPos, int ePos, String content) {
//        SpannableString hSpanStr = new SpannableString(content);
//        //设置字体颜色
//        hSpanStr.setSpan(new ForegroundColorSpan(Color.RED), sPos, ePos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        //设置字体背景色
//        hSpanStr.setSpan(new BackgroundColorSpan(Color.RED), sPos, ePos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
//        tvBook.setText(hSpanStr);

    }

    /**
     * 音调
     *
     * @return
     */
    public int getReadPitch() {
        return readPitch;
    }

    public void setReadPitch(int readPitch) {
        this.readPitch = readPitch;
    }

    /**
     * 音速
     *
     * @return
     */
    public int getReadSpeed() {
        return readSpeed;
    }

    public void setReadSpeed(int readSpeed) {
        this.readSpeed = readSpeed;
    }

    /**
     * 音色
     * 0 台湾 TWN
     * 1 普通话 CHN
     *
     * @return
     */
    public int getReadYinSe() {
        return readYinSe;
    }

    public void setReadYinSe(int readYinSe) {
        this.readYinSe = readYinSe;
    }

    /**
     * 把未读的词组集合拼成string
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

}
