package com.heiheilianzai.app.utils.manager;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.RequiresApi;

import com.heiheilianzai.app.utils.ListUtils;

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
    private final String VOICE_TWN = "yafang";
    private final String VOICE_CHI = "hui";
    //购买的，3个月过期，需要续费更换。后期 需要有接口动态获取
    private static final String LICENSE_KEY = "I3T2-MCCN-LOMG-8IDT-PJ7R";
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
    private int mSampleRate = 16000;
    private int DEFAULT_VOLUME = 100;
    //范围 50-400
    private int readPitch = 100;
    //范围 50-400
    private int readSpeed = 100;
    //范围 0-500
    private int readVolume = 100;

    public static final int BTN_PLAY = 3;
    public static final int BTN_STOP = 1;
    public static final int BTN_PAUSE = 2;
    public static final int TXT_HIGHLIGHT = 4;
    private boolean isPause = false;
    private String pauseText = null;

    private Thread playVoiceThread = null;

    private final Handler uiHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == BTN_PLAY) {
                uiButtonSetPlay();
            } else if (msg.what == BTN_STOP) {
                uiButtonSetStop();
            }
//            else if (msg.what == BTN_PAUSE) {
//                uiButtonSetPause();
//            }
            else {
                uiHighlightText(msg.arg1, msg.arg2, (String) msg.obj);
            }
        }
    };

    public ReadSpeakManager(Context context) {
        this.context = context;
        rootPath = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/D16/";
        LICENESE_PATH = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/licensekey/";
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
//            AssetsZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", context.getExternalCacheDir().toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第二步：license key 认证
     * 如果没有，需要下载
     */
    public void checkLicense(Context context) {

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
        licensemodule.vtLicenseDownload(LICENSE_KEY, LICENESE_PATH, new LicenseDownloadListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String s) {
                //TODO 认证license下载失败处理

            }

            @Override
            public void onError(String s) {
                //TODO 认证license下载失败处理

            }
        });
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
                select = VOICE_TWN;
            case 1:
                select = VOICE_CHI;
        }

        //目前是只有这个 D16库。写死
        String type = "d16";
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
        //TODO 日志 正式环境请注释，不容许日志
        voicetext.vtapiSetCallbackForLogFilter(0);

        try {
            voicetext.vtapiInit(rootPath);
            vtapiHandle = voicetext.vtapiCreateHandle();

            for (Map.Entry<String, EngineInfo> e : voicetext.vtapiGetEngineInfo().entrySet()) {
//                voices.add(e.getValue().getSpeaker() + "-" + e.getValue().getType());
            }

            options = new Options();
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
    public void playBook(String bookContent) {
        if (null == voicetext) {
            return;
        }

        if (mAudioTrack.getPlayState() != AudioTrack.PLAYSTATE_PLAYING) {
            mAudioTrack.play();
        }

        if (null == playVoiceThread) {

            playVoiceThread = new Thread(new Runnable() {
                @Override
                public void run() {

                    Message msg = uiHandler.obtainMessage();
                    msg.what = BTN_PLAY;
                    uiHandler.sendMessage(msg);

                    mAudioTrack.setPositionNotificationPeriod(1600);

                    //不是暂停后继续播放
                    if (!isPause) {
                        totalFrame = 0;
                        addFrame = 0;
                        idxInfo = 0;

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

                            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                                Message msg1 = uiHandler.obtainMessage();
                                msg1.what = BTN_STOP;
                                uiHandler.sendMessage(msg1);
                            }
//                                    else {
//                                        Message msg1 = uiHandler.obtainMessage();
//                                        msg1.what = BTN_STOP;
//                                        uiHandler.sendMessage(msg1);
//                                    }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
                        }


                    } else {
                        totalFrame = 0;
                        addFrame = 0;
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

                            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                                Message msg1 = uiHandler.obtainMessage();
                                msg1.what = BTN_STOP;
                                uiHandler.sendMessage(msg1);
                            }
//                                    else {
//                                        Message msg1 = uiHandler.obtainMessage();
//                                        msg1.what = BTN_STOP;
//                                        uiHandler.sendMessage(msg1);
//                                    }

                        } catch (Exception e) {
                            e.printStackTrace();
                            Message msg1 = uiHandler.obtainMessage();
                            msg1.what = BTN_STOP;
                            uiHandler.sendMessage(msg1);
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
    public void stopBook() {

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
    }

    /**
     * 暂停读书
     */
    public void pauseBook() {
        isPause = true;
        if (null != fWordInfo && idxInfo < mWordInfo.size()) {

            //还未读的内容词组集合
            mWordInfoPause = ListUtils.Companion.sListResult(mWordInfo, idxInfo);
            //还未读的内容string
            if (null != mWordInfoPause && mWordInfoPause.size() > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    pauseText = list2String(mWordInfoPause);
                } else {
                    pauseText = ListUtils.Companion.getEString(mWordInfoPause);
                }
            }

        } else {
            //没有内容可读了，停止读书
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

            idxInfo = 0;
        }

        if (null != voicetext) {
            voicetext.vtapiStopBuffer(vtapiHandle);
        }
        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                pausePlay();
//                    mAudioTrack.pause();
            }
            mAudioTrack.flush();
            uiButtonSetPause();
        }
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
    private void uiButtonSetPlay() {
    }

    /**
     * 停止时 主线程事件 处理
     */
    private void uiButtonSetStop() {
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
    private void uiHighlightText(int sPos, int ePos, String content) {
        //TODO 这里只返回 起始位置index 业务做字体颜色改变。以下为修改颜色示例
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
     * 把未读的词组集合拼成string
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
