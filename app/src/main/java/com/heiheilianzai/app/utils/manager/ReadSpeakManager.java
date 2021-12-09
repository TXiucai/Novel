package com.heiheilianzai.app.utils.manager;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import androidx.annotation.RequiresApi;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.AssetZipUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.ToastUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
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
    private final static String VOICE_TWN = "yafang";
    private final static String VOICE_CHI = "hui";
    private final static String type = "d16";
    //购买的，3个月过期，需要续费更换。有接口动态获取
    private static String LICENSE_KEY;
    Context context;

    public static String rootPath;
    public static String LICENESE_PATH;
    private VoiceText voicetext = null;
    private long vtapiHandle = 0;
    private Options options = null;
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
    //范围 0-500
    private int readVolume = 100;
    // 0 台湾，1 普通话
    private int readYinSe = 0;

    public static final int BTN_PLAY = 3;
    public static final int BTN_STOP = 1;
    public static final int BTN_PAUSE = 2;
    public static final int TXT_HIGHLIGHT = 4;
    private boolean isPause = false;
    private String pauseText = null;
    private int retryDownload = 3;

    private Thread playVoiceThread = null;
    private ReadSpeakStateCallback readSpeakStateCallback;

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

    private final Handler uiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BTN_PLAY) {
                setPlay();
            } else if (msg.what == BTN_STOP) {
                setStop();
            } else {
                uiHighlightText(msg.arg1, msg.arg2, (String) msg.obj);
            }
        }
    };

    public ReadSpeakManager(Context context) {
        this.context = context;
        rootPath = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/D16/";
        LICENESE_PATH = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/licensekey/";
        initReadSetting();
    }

    private void initReadSetting() {
        initAudioRead();
        checkLicense(context);

    }

    /**
     * 初始化默认设置
     */
    public void initAudioRead() {
        int outputBufferSize = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
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
                            Message msgHighlight = uiHandler.obtainMessage();
                            msgHighlight.what = TXT_HIGHLIGHT;
                            //仅仅高亮当前所读词组
//                            msgHighlight.arg1 = tmpinfo.getStartPosInText();
                            msgHighlight.arg1 = 0;
                            msgHighlight.arg2 = tmpinfo.getEndPosInText();
                            uiHandler.sendMessage(msgHighlight);
                        } else if (track.getPlaybackHeadPosition() > 0 && (addFrame / 2) > track.getPlaybackHeadPosition()) {
                            SyncWordInfo tmpinfo = mWordInfo.get(idxInfo - 1);
                            Message msgHighlight = uiHandler.obtainMessage();
                            msgHighlight.what = TXT_HIGHLIGHT;
                            //仅仅高亮当前所读词组
//                            msgHighlight.arg1 = tmpinfo.getStartPosInText();
                            msgHighlight.arg1 = 0;
                            msgHighlight.arg2 = tmpinfo.getEndPosInText();
                            uiHandler.sendMessage(msgHighlight);
                        } else if ((addFrame / 2) <= track.getPlaybackHeadPosition()) {
                            if (idxInfo < mWordInfo.size()) {
                                SyncWordInfo tmpinfo = mWordInfo.get(idxInfo);
                                if (tmpinfo.getStartPosInText() != 0) {
                                    addFrame += tmpinfo.getLength();
                                    Message msgHighlight = uiHandler.obtainMessage();
                                    msgHighlight.what = TXT_HIGHLIGHT;
                                    //仅仅高亮当前所读词组
//                                    msgHighlight.arg1 = tmpinfo.getStartPosInText();
                                    msgHighlight.arg1 = 0;
                                    msgHighlight.arg2 = tmpinfo.getEndPosInText();
                                    uiHandler.sendMessage(msgHighlight);
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
    private void unfileZipFile(Context context) {
        //文件解压
        try {
            AssetZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", context.getExternalCacheDir().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二步：license key 认证
     * 如果没有，需要下载
     */
    public void checkLicense(Context context) {
        //判断D16文件是否存在，不存在就解压
        File D16File = new File(rootPath);
        if (!D16File.exists() || !D16File.isDirectory()) {
            unfileZipFile(context);
        }

        licensemodule = new VtLicenseSetting(context);
        //retry download license
//        licensemodule.resetLicenseFileDownload();

        if (licensemodule.getLicensed()) {
            String verifyTxtPath = LICENESE_PATH + "verification.txt";
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
    public void downloadLicense() {
        LICENSE_KEY = ShareUitls.getString(context, "vtapi_license_key", null);
        if (TextUtils.isEmpty(LICENSE_KEY)) {
            ToastUtil.getInstance().showShortT(context.getString(R.string.read_license_key_null));
            return;
        }
        licensemodule.vtLicenseDownload(LICENSE_KEY, LICENESE_PATH, new LicenseDownloadListener() {
            @Override
            public void onSuccess() {
                load();
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
        String select = VOICE_TWN;

        switch (position) {
            case 0:
                select = VOICE_CHI;
            case 1:
                select = VOICE_TWN;
            default:
                select = VOICE_CHI;
        }

        //目前是只有这个 D16库。写死
        for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
            if (e.getValue().getSpeaker().equals(select) && e.getValue().getType().equals(type)) {
                selectedEngine = e.getValue();
                break;
            }
        }

    }

    /**
     * 语音文件load
     */
    public void load() {
        if (null != voicetext) {
            return;
        }

        voicetext = new VoiceText();
        if (licensemodule == null) {
            voicetext.vtapiSetLicenseFolder(rootPath + "verify/");
        } else {
            // License Module
            voicetext.vtapiSetLicenseFolder(LICENESE_PATH);
            voicetext.vtapiUsingLicensekey(true);
        }

        try {
            voicetext.vtapiInit(rootPath);
            vtapiHandle = voicetext.vtapiCreateHandle();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 读书音频设置
     */
    public void setReadOptions() {
        if (options == null) {
            options = new Options();
        }

        try {
            options.setPitch(readPitch);
            options.setSpeed(readSpeed);
            options.setVolume(readVolume);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
            load();
//            return;
        }

        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.play();
        }

        // 设置音色 和语音属性
        setYingSe(getReadYinSe());
        setReadOptions();

        if (null == playVoiceThread) {

            playVoiceThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Message msg = uiHandler.obtainMessage();
                    msg.what = BTN_PLAY;
                    uiHandler.sendMessage(msg);
                    readSpeakStateCallback.readSpeakState(3);

                    mAudioTrack.setPositionNotificationPeriod(1600);

                    totalFrame = 0;
                    addFrame = 0;
                    idxInfo = 0;

                    if (!isPause) {
                        pauseText = bookContent;
                        isPause = false;

                        try {
                            voicetext.vtapiTextToBufferWithSyncWordInfo(vtapiHandle, bookContent, false, false, 0, selectedEngine.getSpeaker(), selectedEngine.getSampling(), selectedEngine.getType(), options, Constants.OutputFormat.FORMAT_16PCM, new VoiceTextListener() {
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

                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
                            readSpeakStateCallback.readSpeakState(4);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
                            readSpeakStateCallback.readSpeakState(1);
                        }
                    } else {
                        isPause = false;

                        try {
                            voicetext.vtapiTextToBufferWithSyncWordInfo(vtapiHandle, pauseText, false, false, 0, selectedEngine.getSpeaker(), selectedEngine.getSampling(), selectedEngine.getType(), options, Constants.OutputFormat.FORMAT_16PCM, new VoiceTextListener() {
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

                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
                            readSpeakStateCallback.readSpeakState(4);

                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
                            readSpeakStateCallback.readSpeakState(1);
                        }
                    }

                }

            });

        }
        playVoiceThread.start();
    }

    /**
     * stop  停止播放
     */
    public void stopReadBook() {

        if (null != voicetext) {
            voicetext.vtapiStopBuffer(vtapiHandle);
        }

        if (null != playVoiceThread) {
            playVoiceThread.interrupt();
            playVoiceThread = null;
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

        readSpeakStateCallback.readSpeakState(1);
    }

    /**
     * 暂停读书
     * 业务再把这一行文字重新读
     */
    public void pauseReadBook() {
        isPause = true;
        stopReadBook();

    }

    private void pausePlay() {
        //播放线程启动了才允许暂停
        if (playVoiceThread != null) {
            isPause = true;
            mAudioTrack.pause();
        }
    }

    /**
     * 播放时 主线程事件 处理
     */
    private void setPlay() {

    }

    /**
     * 停止时 主线程事件 处理
     * 同时回调
     */
    private void setStop() {
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
    }

    /**
     * 暂停时 主线程事件 处理
     */
    private void uiButtonSetPause() {
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
     * 声音大小 可随系统更改
     *
     * @return
     */
    public int getReadVolume() {
        return readVolume;
    }

    public void setReadVolume(int readVolume) {
        this.readVolume = readVolume;
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
