package com.heiheilianzai.app.view.video;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.heiheilianzai.app.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoPlayView extends FrameLayout {
    private VideoPlayer mPlayerView;
    private View mPlayBtn;
    private String mUrl;
    private Context mContext;
    private boolean mDestroyed;
    private ObjectAnimator mAnimator;
    private VideoPlayWrap mPlayWrap;
    private VideoListener mListener;
    private RelativeLayout mRoot;

    // 是否能手动停止播放
    private boolean mCanStopPlay = true;

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
        mContext = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_play, this, false);

        addView(view);
        mRoot = view.findViewById(R.id.root);
        mPlayBtn = view.findViewById(R.id.btn_play);
        mRoot.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDestroyed && mPlayerView == null) {
                    return;
                }
                playOrPause(!mPlayerView.isPlaying(), false);
            }
        });
        mPlayerView = view.findViewById(R.id.id_view_video_player_view);

        mAnimator = ObjectAnimator.ofPropertyValuesHolder(mPlayBtn,
                PropertyValuesHolder.ofFloat("scaleX", 3f, 1f),
                PropertyValuesHolder.ofFloat("scaleY", 3f, 1f),
                PropertyValuesHolder.ofFloat("alpha", 0f, 1f));
        mAnimator.setDuration(120);
        mAnimator.setInterpolator(new AccelerateInterpolator());
    }

    public void playOrPause(boolean play, boolean manualTrigger) {
        if (!mDestroyed && mPlayerView != null) {
            if (play && !mPlayerView.isPlaying()) {
                mPlayerView.start();
                if (mPlayBtn != null) {
                    mPlayBtn.setVisibility(GONE);
                }
            } else if (!play && mPlayerView.isPlaying()) {
                // manualTrigger为true代表点击屏幕触发pause事件。
                if (manualTrigger) {
                    if (mCanStopPlay) {
                        pause();
                    }
                } else {
                    // manualTrigger为false代表因为切换页面，
                    // 或者因为程序进入后台触发pause事件。
                    pause();
                }
            }
        }
    }


    // 用于处理当APP在同一个Activity中有两个视频流时覆盖的问题，
    // 当发生页面切换时隐藏之前正在显示的播放器。
    public void hideVideoPlayer(boolean hide) {
        if (mPlayerView != null) {
            mPlayerView.hideSurfaceView(hide);
        }
    }

    private void pause() {
        mPlayerView.pause();
        if (mPlayBtn != null) {
            mPlayBtn.setVisibility(VISIBLE);
            mAnimator.start();
        }
    }

    public void setCanStopPlay(boolean canStopPlay) {
        mCanStopPlay = canStopPlay;
    }

    public void play(String url, boolean needDecrypt) {
        if (!mDestroyed && mPlayerView != null) {
            if (TextUtils.isEmpty(url) || url.equals(mUrl)) {
                return;
            }
            mUrl = url;
            if (mPlayerView.isPlaying()) {
                playOrPause(false, false);
            }
        }

        resetPlayView();

        mPlayerView.setVideoListener(new VideoListener() {
            @Override
            public void onBufferingUpdate(IMediaPlayer iMediaPlayer, int i) {
                if (mListener != null) {
                    mListener.onBufferingUpdate(iMediaPlayer, i);
                }
            }

            @Override
            public void onCompletion(IMediaPlayer iMediaPlayer) {
                // 播放完成后循环播放
                playOrPause(true, false);
                if (mListener != null) {
                    mListener.onCompletion(iMediaPlayer);
                }
            }

            @Override
            public boolean onError(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mListener != null) {
                    mListener.onError(iMediaPlayer, i, i1);
                }
//                if (mContext != null) {
//                    ToastHelper.showShort(mContext, WordUtil.getString(R.string.string_play_failed));
//                }
                return false;
            }

            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {
                if (mListener != null) {
                    mListener.onInfo(iMediaPlayer, i, i1);
                }
                return false;
            }

            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                if (mListener != null) {
                    mListener.onPrepared(iMediaPlayer);
                }

                playOrPause(true, false);

            }

            @Override
            public void onSeekComplete(IMediaPlayer iMediaPlayer) {
                if (mListener != null) {
                    mListener.onSeekComplete(iMediaPlayer);
                }
            }

            @Override
            public void onVideoSizeChanged(IMediaPlayer iMediaPlayer, int i, int i1, int i2,
                                           int i3) {
                if (mListener != null) {
                    mListener.onVideoSizeChanged(iMediaPlayer, i, i1, i2, i3);
                }
            }
        });
        Map<String, String> header = new HashMap<>();
        mPlayerView.setPath(url, header, needDecrypt);
        try {
            mPlayerView.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void destroy() {
        if (!mDestroyed) {
            mDestroyed = true;
            if (mPlayerView != null) {
                mPlayerView.stop();
                mPlayerView.release();
            }
        }
    }

    public boolean isPlaying() {
        return mPlayerView != null && mPlayerView.isPlaying();
    }

    public void setPlayWrap(VideoPlayWrap wrap) {
        mPlayWrap = wrap;
    }

    public void setListener(VideoListener listener) {
        mListener = listener;
    }

    /**
     * 获取当前播放的时间
     *
     * @return
     */
    public float getCurrentPlaybackTime() {
        return mPlayerView.getCurrentPosition();
    }

    /**
     * 获取该视频的总时间
     *
     * @param
     */
    public long getAllPlayTimes() {
        return mPlayerView.getDuration();
    }

    public void seekToProgress(long position) {
        mPlayerView.seekTo(position);
    }

    private void resetPlayView() {
        if (mPlayBtn != null) {
            mPlayBtn.setVisibility(GONE);
        }
    }

}