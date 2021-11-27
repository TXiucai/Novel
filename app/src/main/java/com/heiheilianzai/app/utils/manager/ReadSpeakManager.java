package com.heiheilianzai.app.utils.manager;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.AudioFormat;
import android.media.AudioTrack;
import android.os.Build;

import com.heiheilianzai.app.utils.AssetZipUtils;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Map;

import kr.co.voiceware.java.vtapi.Constants;
import kr.co.voiceware.java.vtapi.EngineInfo;
import kr.co.voiceware.java.vtapi.Options;
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
    private boolean isUsedLicenseKey = true;// LicenseKey = true, verification.txt = false
    private int licenseType = -1;
    AudioTrack mAudioTrack;
    private int mSampleRate = 16000;
    private int DEFAULT_VOLUME = 100;
    //范围 50-400
    private int readPitch = 100;
    //范围 50-400
    private int readSpeed = 100;
    //范围 0-500
    private int readVolume = 100;

    public void ReadSpeakerManager(Context context) {
        this.context = context;
//        rootPath = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/D16/";
//        LICENESE_PATH = context.getExternalCacheDir().toString() + File.separator + "/ReadSpeaker/licensekey/";
        rootPath = context.getFilesDir().toString() + File.separator + "/ReadSpeaker/D16/";
        LICENESE_PATH = context.getFilesDir().toString() + File.separator + "/ReadSpeaker/licensekey/";
    }

    /**
     * 初始化默认设置
     */
    public void initAudioRead() {
        int outputBufferSize = AudioTrack.getMinBufferSize(mSampleRate, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAudioTrack = new AudioTrack.Builder()
                        .setAudioAttributes(new AudioAttributes.Builder()
                                .setUsage(AudioAttributes.USAGE_UNKNOWN)
                                .setContentType(AudioAttributes.CONTENT_TYPE_UNKNOWN)
                                .build())
                        .setAudioFormat(new AudioFormat.Builder()
                                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                                .setSampleRate(mSampleRate)
                                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                                .build())
                        .setBufferSizeInBytes(outputBufferSize)
                        .build();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mAudioTrack.setVolume(DEFAULT_VOLUME);
            }
            mAudioTrack.play();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 第一步：解压语音库文件
     */
    private void unfileZipFile(Context context) {
        //文件解压
        try {
//            AssetZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", context.getExternalCacheDir().toString());
            AssetZipUtils.UnZipAssetsFolder(context, "ReadSpeaker.zip", context.getFilesDir().toString());
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
        licenseType = licensemodule.getHostType();

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
            public void onSuccess(int type) {
                licenseType = type;
            }

            @Override
            public void onFailure(String s) {

            }

            @Override
            public void onError(String s) {

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
            int rtn = voicetext.vtapiUsingLicensekey(context, true, licenseType);

            if (rtn < 0) {
                return;
            }
        }

        voicetext.vtapiSetCallbackForLogFilter(0);

        try {
            voicetext.vtapiInit(rootPath);
            vtapiHandle = voicetext.vtapiCreateHandle();

            voicetext.vtapiLoadEngineInfo();
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
     * unload
     */
    public void unload() {
        if (null == voicetext) {
            return;
        }
        voicetext.vtapiReleaseHandle(vtapiHandle);
        voicetext.vtapiExit();
        voicetext = null;
        vtapiHandle = 0;
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                //TODO 发送播放状态通知
                try {

                    voicetext.vtapiTextToBuffer(vtapiHandle, bookContent, false, 0, selectedEngine.getSpeaker(), selectedEngine.getSampling(), selectedEngine.getType(), options, Constants.OutputFormat.FORMAT_16PCM, new VoiceTextListener() {
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

                        @Override
                        public void onError(String reason) {

                        }
                    });

                    if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                        //TODO 播放状态
                    } else if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PAUSED) {
                        //TODO 暂停状态
                    } else {
                        //TODO 停止状态
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * stop  停止播放
     */
    public void stopBook() {

        if (null != voicetext) {
            voicetext.vtapiStopBuffer(vtapiHandle);
        }

        if (mAudioTrack != null) {
            if (mAudioTrack.getPlayState() == AudioTrack.PLAYSTATE_PLAYING) {
                mAudioTrack.pause();
            }
            mAudioTrack.flush();
        }

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

}
