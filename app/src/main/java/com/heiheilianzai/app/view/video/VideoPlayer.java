package com.heiheilianzai.app.view.video;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.live.eggplant.base.encrypt.EncryptUtils;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * @author dragon
 */
public class VideoPlayer extends FrameLayout {


    // 由ijkplayer提供，用于播放视频，需要给他传入一个surfaceView。
    private IMediaPlayer mMediaPlayer;

    // 视频文件地址
    private String mPath;

    // 视频请求header
    private Map<String, String> mHeader;

    private SurfaceView mSurfaceView;

    private Context mContext;
    private boolean mEnableMediaCodec;

    private VideoListener mListener;
    private AudioManager mAudioManager;
    private AudioFocusHelper mAudioFocusHelper;

    // 是否需要显示画面，预加载控件隐藏显示画面
    private boolean mNeedSurfaceHolder = true;

    // 左右声道的音量，默认是最大
    private float mLeftValue = 1.0f;
    private float mRightValue = 1.0f;

    // 播放速度
    private float mSpeed = 1.0f;

    private boolean mNeedDecrypt;

    public VideoPlayer(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs, 0);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        setBackgroundColor(Color.BLACK);
        createSurfaceView();
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mAudioFocusHelper = new AudioFocusHelper();
    }

    private void createSurfaceView() {
        mSurfaceView = new SurfaceView(mContext);
        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {
                if (mMediaPlayer != null && mNeedSurfaceHolder) {
                    mMediaPlayer.setDisplay(surfaceHolder);
                }
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT,
                Gravity.CENTER);

        mSurfaceView.setLayoutParams(layoutParams);

        addView(mSurfaceView, 0);
    }

    private IMediaPlayer createPlayer() {
        IjkMediaPlayer ijkMediaPlayer = new IjkMediaPlayer();

        // 基础设置
        setupIjkplayer(ijkMediaPlayer);

        // 硬解码设置
        setEnableMediaCodec(ijkMediaPlayer, mEnableMediaCodec);

        // 解密设置
        setEnableDecrypt(ijkMediaPlayer);

        // 缓存设置
        setEnableCached(ijkMediaPlayer);

        // 监听回调设置
        setListener(ijkMediaPlayer);

        return ijkMediaPlayer;
    }

    // 播放器基础设置
    private void setupIjkplayer(IjkMediaPlayer ijkMediaPlayer) {

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_RV32);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "framedrop", 5L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "max-buffer-size", 1024 * 1024L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "start-on-prepared", 0L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "reconnect", 1);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "dns_cache_clear", 1);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "http-detect-range-support", 1L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "analyzeduration", 1L);

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", 48L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "min-frames", 100L);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "enable-accurate-seek", 1L);
        //设置seekTo能够快速seek到指定位置并播放
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_FORMAT, "fflags", "fastseek");

        ijkMediaPlayer.setVolume(mLeftValue, mRightValue);

        ijkMediaPlayer.setSpeed(mSpeed);
    }

    // 设置是否开启视频缓存
    private void setEnableCached(IjkMediaPlayer ijkMediaPlayer) {
        if (mContext.getExternalCacheDir() == null) return;

        // 设置缓存路径
        String path = mContext.getExternalCacheDir().getAbsolutePath() + "/cache2";

        File outputFolder = new File(path);
        if (!outputFolder.exists()) {
            outputFolder.mkdirs();
        }

        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "cache2-dir", path);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "cache2-dir-max-size",
                1024 * 1024 * 512);
    }

    // 设置是否开启视频解密
    private void setEnableDecrypt(IjkMediaPlayer ijkMediaPlayer) {
        String suffix = getSuffix();
        if (TextUtils.equals(suffix, "M3U8") || mNeedDecrypt) {
            // 设置解密key
            String decryptKey = it.sauronsoftware.base64.Base64.encode(EncryptUtils.getVideoKv());

            ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                    "hls-base64-key",
                    decryptKey);
        }
    }

    // 设置是否开启硬解码
    private void setEnableMediaCodec(IjkMediaPlayer ijkMediaPlayer, boolean isEnable) {
        int value = isEnable ? 1 : 0;
        // 开启硬解码
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "mediacodec",
                value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "mediacodec-auto-rotate",
                value);
        ijkMediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER,
                "mediacodec-handle-resolution-change",
                value);
    }

    public void setEnableMediaCodec(boolean isEnable) {
        mEnableMediaCodec = isEnable;
    }

    // 设置播放器的监听
    private void setListener(IMediaPlayer player) {
        player.setOnPreparedListener(mPreparedListener);
        player.setOnVideoSizeChangedListener(mVideoSizeChangedListener);
        player.setOnCompletionListener(mOnCompletionListener);
        player.setOnErrorListener(mOnErrorListener);
        player.setOnInfoListener(mOnInfoListener);
        player.setOnBufferingUpdateListener(mOnBufferingUpdateListener);
        player.setOnSeekCompleteListener(mOnSeekCompleteListener);
    }

    public void setNeedSurfaceHolder(boolean needSurfaceHolder) {
        mNeedSurfaceHolder = needSurfaceHolder;
    }

    // 设置播放器的监听回调
    public void setVideoListener(VideoListener listener) {
        mListener = listener;
    }

    // 设置播放地址
    public void setPath(String path, boolean needDecrypt) {
        setPath(path, null, needDecrypt);
    }

    public void setPath(String path, Map<String, String> header, boolean needDecrypt) {
        mPath = path;
        mHeader = header;
        mNeedDecrypt = needDecrypt;
    }

    // 开始加载视频
    public void load() throws IOException {
        if (mSurfaceView != null) {
            resetIjkplayer();
            mMediaPlayer = createPlayer();
            createSurfaceView();
            mMediaPlayer.setDataSource(mContext, Uri.parse("cache2:" + mPath), null);
            mMediaPlayer.prepareAsync();
        }
    }

    public void switchUrl() {
        release();
        createSurfaceView();
    }

    public void start() {
        if (mMediaPlayer != null
                && mSurfaceView != null
                && mSurfaceView.getVisibility() == VISIBLE) {
            mMediaPlayer.start();
            if (mNeedSurfaceHolder) {
                mAudioFocusHelper.requestFocus();
            }
        }
    }

    public void release() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mSurfaceView.setVisibility(GONE);
            mSurfaceView = null;
            mMediaPlayer = null;
            mAudioFocusHelper.abandonFocus();
        }
    }

    public void pause() {
        if (mMediaPlayer != null) {
            mMediaPlayer.pause();
            mAudioFocusHelper.abandonFocus();
        }
    }

    public void hideSurfaceView(boolean hide) {
        if (mSurfaceView != null) {
            mSurfaceView.setVisibility(hide ? INVISIBLE : VISIBLE);
        }
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mAudioFocusHelper.abandonFocus();
        }
    }

    public void reset() {
        if (mMediaPlayer != null) {
            mMediaPlayer.reset();
        }
    }

    public long getDuration() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getDuration();
    }

    public long getCurrentPosition() {
        return mMediaPlayer == null ? 0 : mMediaPlayer.getCurrentPosition();
    }

    public void seekTo(long position) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(position);
        }
    }

    private void resetIjkplayer() {
        if (mMediaPlayer != null) {
            mAudioFocusHelper.abandonFocus();
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer.setDisplay(null);
            mMediaPlayer = null;
            mSurfaceView.setVisibility(GONE);
            mSurfaceView = null;
            removeAllViews();
        }
    }

    public void setVolume(float left, float right) {
        mLeftValue = left;
        mRightValue = right;
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(mLeftValue, mRightValue);
        }
    }

    public void setSpeed(float speed) {
        mSpeed = speed;
    }

    public boolean isPlaying() {
        return mMediaPlayer == null ? false : mMediaPlayer.isPlaying();
    }

    //------------------listener---------------------//
    private IMediaPlayer.OnPreparedListener mPreparedListener = iMediaPlayer -> {
        if (mListener != null) {
            mListener.onPrepared(iMediaPlayer);
        }
    };

    private IMediaPlayer.OnCompletionListener mOnCompletionListener = iMediaPlayer -> {
        VideoPlayCompletedRecorder.getInstance().record(mPath);

        if (mListener != null) {
            mListener.onCompletion(iMediaPlayer);
        }
    };

    private IMediaPlayer.OnBufferingUpdateListener mOnBufferingUpdateListener = (iMediaPlayer, i) -> {
        if (mListener != null) {
            mListener.onBufferingUpdate(iMediaPlayer, i);
        }
    };

    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = iMediaPlayer -> {
        if (mListener != null) {
            mListener.onSeekComplete(iMediaPlayer);
        }
    };

    private IMediaPlayer.OnErrorListener mOnErrorListener = (iMediaPlayer, i, i1) -> {
        if (mListener != null) {
            mListener.onError(iMediaPlayer, i, i1);
        }
        return false;
    };

    private IMediaPlayer.OnInfoListener mOnInfoListener = (iMediaPlayer, i, i1) -> {
        if (mListener != null) {
            mListener.onInfo(iMediaPlayer, i, i1);
        }
        return false;
    };

    private IMediaPlayer.OnVideoSizeChangedListener mVideoSizeChangedListener =
            (iMediaPlayer, i, i1, i2, i3) -> {
                int videoWidth = iMediaPlayer.getVideoWidth();
                int videoHeight = iMediaPlayer.getVideoHeight();

                if (videoWidth != 0 && videoHeight != 0) {
                    int finalWidth = ScreenSizeUtils.getInstance(mContext).getScreenWidth();
                    int finalHeight = finalWidth * videoHeight / videoWidth;

                    LayoutParams layoutParams = new LayoutParams(finalWidth,
                            finalHeight,
                            Gravity.CENTER);

                    mSurfaceView.setLayoutParams(layoutParams);
                }
            };

    /**
     * 音频焦点改变监听
     */
    private class AudioFocusHelper implements AudioManager.OnAudioFocusChangeListener {
        private boolean mStartRequested;
        private boolean mPausedForLoss;
        private int mCurrentFocus;

        @Override
        public void onAudioFocusChange(int focusChange) {
            if (mCurrentFocus == focusChange) {
                return;
            }

            mCurrentFocus = focusChange;
            switch (focusChange) {
                case AudioManager.AUDIOFOCUS_GAIN:// 获得焦点
                case AudioManager.AUDIOFOCUS_GAIN_TRANSIENT:// 暂时获得焦点
                    if (mStartRequested || mPausedForLoss) {
                        start();
                        mStartRequested = false;
                        mPausedForLoss = false;
                    }

                    if (mMediaPlayer != null) {
                        // 恢复音量
                        mMediaPlayer.setVolume(1.0f, 1.0f);
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS:// 焦点丢失
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT:// 焦点暂时丢失
                    if (isPlaying()) {
                        mPausedForLoss = true;
                        pause();
                    }
                    break;
                case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK:// 此时需降低音量
                    if (mMediaPlayer != null && isPlaying()) {
                        mMediaPlayer.setVolume(0.1f, 0.1f);
                    }
                    break;
            }
        }

        boolean requestFocus() {
            if (mCurrentFocus == AudioManager.AUDIOFOCUS_GAIN) {
                return true;
            }

            if (mAudioManager == null) {
                return false;
            }

            int status = mAudioManager.requestAudioFocus(this,
                    AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);

            if (AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status) {
                mCurrentFocus = AudioManager.AUDIOFOCUS_GAIN;
                return true;
            }

            mStartRequested = true;
            return false;
        }

        boolean abandonFocus() {

            if (mAudioManager == null) {
                return false;
            }

            mStartRequested = false;
            int status = mAudioManager.abandonAudioFocus(this);
            return AudioManager.AUDIOFOCUS_REQUEST_GRANTED == status;
        }
    }

    private String getSuffix() {
        String result = "";
        if (!TextUtils.isEmpty(mPath)) {
            int indexLastDot = mPath.lastIndexOf(".");
            if (indexLastDot > 0 && indexLastDot < mPath.length() - 1) {
                result = mPath.substring(indexLastDot + 1).toUpperCase();
            }
        }
        return result;
    }
}