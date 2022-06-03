package com.live.eggplant.player.player;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.view.Surface;

import com.live.eggplant.base.encrypt.EncryptUtils;
import com.live.eggplant.player.cache.ICacheManager;
import com.live.eggplant.player.model.GSYModel;
import com.live.eggplant.player.model.VideoOptionModel;
import com.live.eggplant.player.utils.Debuger;
import com.live.eggplant.player.utils.GSYVideoType;
import com.live.eggplant.player.utils.RawDataSourceProvider;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkLibLoader;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * IJKPLayer
 * Created by guoshuyu on 2018/1/11.
 */

public class IjkPlayerManager implements IPlayerManager {

    /**
     * log level
     */
    private static int logLevel = IjkMediaPlayer.IJK_LOG_DEFAULT;

    private static IjkLibLoader ijkLibLoader;

    public static String headersReferer;

    private IjkMediaPlayer mediaPlayer;

    private List<VideoOptionModel> optionModelList;

    private Surface surface;

    public static boolean sIsEncrypt = true;

    @Override
    public IMediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    @Override
    public void initVideoPlayer(Context context, Message msg,
                                List<VideoOptionModel> optionModelList,
                                ICacheManager cacheManager) {
        mediaPlayer = (ijkLibLoader == null) ? new IjkMediaPlayer() :
                new IjkMediaPlayer(ijkLibLoader);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnNativeInvokeListener(new IjkMediaPlayer.OnNativeInvokeListener() {
            @Override
            public boolean onNativeInvoke(int i, Bundle bundle) {
                return true;
            }
        });

        GSYModel gsyModel = (GSYModel) msg.obj;
        String url = gsyModel.getUrl();
        try {
            setupIjkPlayer(context, optionModelList, cacheManager, gsyModel, url);
            setEnableMediaCodec();
            setMediaDecrypt(url);
            setEnableCached(context);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 播放器基础设置
     *
     * @param context
     * @param optionModelList
     * @param cacheManager
     * @param gsyModel
     * @param url
     * @throws IOException
     */
    private void setupIjkPlayer(Context context, List<VideoOptionModel> optionModelList,
                                ICacheManager cacheManager, GSYModel gsyModel, String url) throws IOException {
        if (gsyModel.isCache() && cacheManager != null) {
            cacheManager.doCacheLogic(context, mediaPlayer, url, gsyModel.getMapHeadData(),
                    gsyModel.getCachePath());
        } else {
            if (!TextUtils.isEmpty(url)) {
                Uri uri = Uri.parse(url);
                if (uri.getScheme().equals(ContentResolver.SCHEME_ANDROID_RESOURCE)) {
                    RawDataSourceProvider rawDataSourceProvider =
                            RawDataSourceProvider.create(context, uri);
                    mediaPlayer.setDataSource(rawDataSourceProvider);
                } else {
                    mediaPlayer.setDataSource(url, gsyModel.getMapHeadData());
                }
            } else {
                mediaPlayer.setDataSource(url, gsyModel.getMapHeadData());
            }
        }

        mediaPlayer.setLooping(gsyModel.isLooping());
        if (gsyModel.getSpeed() != 1 && gsyModel.getSpeed() > 0) {
            mediaPlayer.setSpeed(gsyModel.getSpeed());
        }
        IjkMediaPlayer.native_setLogLevel(logLevel);
        initIJKOption(mediaPlayer, optionModelList);
        if (!TextUtils.isEmpty(headersReferer)) {
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "headers",
                    headersReferer);
        }
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_timeout", -1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "timeout", 10000000);
    }

    /**
     * 设置缓存路径，不设置下面代码会导致播放异常崩溃
     *
     * @param context
     */
    private void setEnableCached(Context context) {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/"
                + context.getPackageName()
                + "/ijkplayer/cache";

        File outputFolder = new File(path);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "cache2-dir", path);
        mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "cache2-dir-max-size", 200 * 1024 * 1024);
    }

    /**
     * 开启硬解码
     */
    private void setEnableMediaCodec() {
        if (GSYVideoType.isMediaCodec()) {
            Debuger.printfLog("enable mediaCodec");
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec", 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-auto-rotate"
                    , 1);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "mediacodec-handle" +
                    "-resolution-change", 1);
        }
    }

    /**
     * 设置m3u8视频解密
     *
     * @param url
     */
    private void setMediaDecrypt(String url) {
        if (sIsEncrypt && isM3u8File(url)) {
//             设置解密key
            String decryptKey =
                    it.sauronsoftware.base64.Base64.encode(EncryptUtils.getVideoKv());
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "hls-base64-key",
                    decryptKey);
        }
    }

    private boolean isM3u8File(String url) {
        return url.toUpperCase().contains(".M3U8");
    }

    @Override
    public void showDisplay(Message msg) {
        if (msg.obj == null && mediaPlayer != null) {
            mediaPlayer.setSurface(null);
        } else {
            Surface holder = (Surface) msg.obj;
            surface = holder;
            if (mediaPlayer != null && holder.isValid()) {
                mediaPlayer.setSurface(holder);
            }
        }
    }

    @Override
    public void setSpeed(float speed, boolean soundTouch) {
        if (speed > 0) {
            try {
                if (mediaPlayer != null) {
                    mediaPlayer.setSpeed(speed);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (soundTouch) {
                VideoOptionModel videoOptionModel =
                        new VideoOptionModel(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", 1);
                List<VideoOptionModel> list = getOptionModelList();
                if (list != null) {
                    list.add(videoOptionModel);
                } else {
                    list = new ArrayList<>();
                    list.add(videoOptionModel);
                }
                setOptionModelList(list);
            }

        }
    }

    @Override
    public void setNeedMute(boolean needMute) {
        if (mediaPlayer != null) {
            if (needMute) {
                mediaPlayer.setVolume(0, 0);
            } else {
                mediaPlayer.setVolume(1, 1);
            }
        }
    }


    @Override
    public void releaseSurface() {
        if (surface != null) {
            surface.release();
            surface = null;
        }
    }

    @Override
    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
    }

    @Override
    public int getBufferedPercentage() {
        return -1;
    }

    @Override
    public long getNetSpeed() {
        if (mediaPlayer != null) {
            return mediaPlayer.getTcpSpeed();
        }
        return 0;
    }

    @Override
    public void setSpeedPlaying(float speed, boolean soundTouch) {
        if (mediaPlayer != null) {
            mediaPlayer.setSpeed(speed);
            mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "soundtouch", (soundTouch)
                    ? 1 : 0);
        }
    }

    @Override
    public void start() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
        }
    }

    @Override
    public void stop() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    @Override
    public void pause() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
        }
    }

    @Override
    public int getVideoWidth() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoWidth();
        }
        return 0;
    }

    @Override
    public int getVideoHeight() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoHeight();
        }
        return 0;
    }

    @Override
    public boolean isPlaying() {
        if (mediaPlayer != null) {
            return mediaPlayer.isPlaying();
        }
        return false;
    }

    @Override
    public void seekTo(long time) {
        if (mediaPlayer != null) {
            mediaPlayer.seekTo(time);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mediaPlayer != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    @Override
    public int getVideoSarNum() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarNum();
        }
        return 1;
    }

    @Override
    public int getVideoSarDen() {
        if (mediaPlayer != null) {
            return mediaPlayer.getVideoSarDen();
        }
        return 1;
    }


    @Override
    public boolean isSurfaceSupportLockCanvas() {
        return true;
    }

    private void initIJKOption(IjkMediaPlayer ijkMediaPlayer,
                               List<VideoOptionModel> optionModelList) {
        if (optionModelList != null && optionModelList.size() > 0) {
            for (VideoOptionModel videoOptionModel : optionModelList) {
                if (videoOptionModel.getValueType() == VideoOptionModel.VALUE_TYPE_INT) {
                    ijkMediaPlayer.setOption(videoOptionModel.getCategory(),
                            videoOptionModel.getName(), videoOptionModel.getValueInt());
                } else {
                    ijkMediaPlayer.setOption(videoOptionModel.getCategory(),
                            videoOptionModel.getName(), videoOptionModel.getValueString());
                }
            }
        }
    }

    public List<VideoOptionModel> getOptionModelList() {
        return optionModelList;
    }

    public void setOptionModelList(List<VideoOptionModel> optionModelList) {
        this.optionModelList = optionModelList;
    }

    public static IjkLibLoader getIjkLibLoader() {
        return ijkLibLoader;
    }

    public static void setIjkLibLoader(IjkLibLoader ijkLibLoader) {
        IjkPlayerManager.ijkLibLoader = ijkLibLoader;
    }

    public static int getLogLevel() {
        return logLevel;
    }

    public static void setLogLevel(int logLevel) {
        IjkPlayerManager.logLevel = logLevel;
    }
}