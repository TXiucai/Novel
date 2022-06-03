package com.heiheilianzai.app.view.video;

import static android.view.animation.Animation.INFINITE;
import static android.view.animation.Animation.RESTART;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.view.video.view.FullPlaySpeedSelectDialog;

import com.heiheilianzai.app.view.video.view.PlaySpeedSelectDialog;
import com.live.eggplant.player.listener.GSYVideoGifSaveListener;
import com.live.eggplant.player.utils.FileUtils;
import com.live.eggplant.player.utils.GifCreateHelper;
import com.live.eggplant.player.video.StandardGSYVideoPlayer;
import com.live.eggplant.player.video.base.GSYBaseVideoPlayer;

import java.io.File;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

public class CustomGSYVideoPlayer extends StandardGSYVideoPlayer {

    private long mSeekOnStartTime = 0;

    /**
     * 快退按钮
     */
    private TextView mVideoBackward;
    /**
     * 截图按钮
     */
    private TextView tvTakePicture;
    /**
     * 录制gif按钮
     */
    private TextView tvTakeGif;
    private RelativeLayout layoutGif;
    /**
     * 退出录制gif
     */
    private ImageView ivExitGif;
    /**
     * 录制gif title
     */
    private TextView tvGifSaveTitTle;
    /**
     * 录制gif tips
     */
    private TextView tvGifSaveTip;
    /**
     * 下一步
     */
    private TextView tvGifFinish;
    /**
     * GIF生成中
     */
    private TextView tvGifSaveLoading;
    /**
     * gif保存进度
     */
    private ProgressBar progressBarGif;
    /**
     * 用于展示已经生成的gif
     */
    private ImageView ivGif;
    /**
     * GIF相册保存成功，分享给朋友吧
     */
    private TextView tvGifSaveSuccess;
    /**
     * 保存gif状态布局
     */
    private RelativeLayout layoutGifImg;
    /**
     * 当前X秒，最多可录制8秒
     */
    private TextView tvGifSaveTime;
    /**
     * gif 保存时的加载圈
     */
    private ImageView ivGifSaveLoading;
    /**
     * gif 保存失败布局
     */
    private LinearLayout layoutGifSaveFail;
    /**
     * 截屏分享布局
     */
    public RelativeLayout layoutScreenshot;
    /**
     * 截屏分享预览图片
     */
    public ImageView ivScreenshot;
    /**
     * 快进按钮
     */
    private TextView mVideoForward;

    /**
     * 右侧关闭按钮
     */
    private View mSmallCloseRight;

    /**
     * 悬浮框放大按钮
     */
    private ImageView mFloatScalingFullscreen;


    private TextView mPlaySpeed;

    private GifCreateHelper mGifCreateHelper;
    private File filegif;
    private int gifCurrentPositionWhenPlaying;
    private CustomGSYVideoPlayer currentPlayer;
    private String[] permission = {
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};

    /**
     * 是否为本地视频播放
     */
    private boolean mIsLocalVideo = false;

    /**
     * 是否为第一次缓冲
     */
    private boolean mIsFirstBuffer = true;

    /**
     * 是否开始缓冲
     */
    private boolean mIsStartBuffer = false;


    /**
     * 全屏的播放倍速选择弹窗
     */
    private FullPlaySpeedSelectDialog mFullPlaySpeedSelectDialog;

    /**
     * 现在视频播放的位置
     */
    public int startPlayTime = -1;
    public int totalPlayTime = -1;
    public boolean fcT = false;//总共播放视频的时长是否已已经达到条件


    private final Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void init(Context context) {
        super.init(context);
    }

    public CustomGSYVideoPlayer(Context context) {
        super((Context) new SoftReference(context).get());
        initCustom(context);
    }

    public CustomGSYVideoPlayer(Context context, AttributeSet attrs) {
        super((Context) new SoftReference(context).get(), attrs);
        initCustom(context);
    }

    public CustomGSYVideoPlayer(Context context, Boolean fullFlag) {
        super((Context) new SoftReference(context).get(), fullFlag);
        initCustom(context);
    }

    @Override
    public int getLayoutId() {
        return R.layout.layout_video_play_custom;
    }

    @SuppressLint("SetTextI18n")
    private void initCustom(Context context) {
      /*  this.layoutGifSaveFail = findViewById(R.id.layout_gif_save_fail);
        this.ivGifSaveLoading = findViewById(R.id.iv_gif_save_loading);
        this.tvGifSaveSuccess = findViewById(R.id.tv_gif_save_success);
        this.layoutGifImg = findViewById(R.id.layout_gif_img);
        this.tvGifSaveTime = findViewById(R.id.tv_gif_save_time);
        this.ivGif = findViewById(R.id.iv_gif);
        this.progressBarGif = findViewById(R.id.progress_bar_gif);
        this.tvGifSaveLoading = findViewById(R.id.tv_gif_save_loading);
        this.tvGifFinish = findViewById(R.id.tv_gif_finish);
        this.tvGifSaveTitTle = findViewById(R.id.tv_gif_save_title);
        this.tvGifSaveTip = findViewById(R.id.tv_gif_save_tip);
        this.ivExitGif = findViewById(R.id.iv_exit_gif);
        this.layoutGif = findViewById(R.id.layout_gif);
        this.tvTakePicture = findViewById(R.id.tv_take_picture);
        this.tvTakeGif = findViewById(R.id.tv_take_gif);
        this.layoutScreenshot = findViewById(R.id.layout_screenshot);
        this.ivScreenshot = findViewById(R.id.iv_screenshot);
        this.mSmallCloseRight = findViewById(R.id.small_close_right);
        this.mFloatScalingFullscreen = findViewById(R.id.float_scaling_fullscreen);
        this.mPlaySpeed = findViewById(R.id.tv_play_speed);
        this.mVideoBackward=findViewById(R.id.for)
        setListener();
        if (CartoonConfig.SPEED != 1.0f) {
            mPlaySpeed.setText(CartoonConfig.SPEED + "X");
        }
        initGifHelper();*/
    }


    private void setListener() {
        tvGifFinish.setOnClickListener(v -> {
            makeGifProgress(getCurrentPositionWhenPlaying(), true);
        });
        //视频播放进度监听
        setGSYVideoProgressListener((progress, secProgress, currentPosition, duration) -> {
            if (startPlayTime <= 0) {
                //这一版注释播放页浮层广告 播放视频时长和累计播放视频时长条件
//                recordTime();
            }
            makeGifProgress(currentPosition, false);
        });
        this.ivExitGif.setOnClickListener(v -> {
            initMakeGif();
        });
        this.layoutScreenshot.setOnClickListener(v -> layoutScreenshot.setVisibility(GONE));
        this.tvTakePicture.setOnClickListener(v -> {
            getShotOrGif(0);
        });
        this.tvTakeGif.setOnClickListener(v -> {
            currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
            getShotOrGif(1);
        });
        mVideoBackward.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mUrl)) {
                onProgressChangeByManual();
                changeSeekTimePosition(-FASTPOSITIONTIME);
            }
        });
        mVideoForward.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(mUrl)) {
                onProgressChangeByManual();
                changeSeekTimePosition(FASTPOSITIONTIME);
            }
        });
        mSmallCloseRight.setOnClickListener(v -> {
            setVisibility(View.GONE);
            clickStartIcon();
        });
        mFloatScalingFullscreen.setOnClickListener(v -> {

        });


        mPlaySpeed.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIfCurrentIsFullscreen) {
                    mFullPlaySpeedSelectDialog = new FullPlaySpeedSelectDialog(mContext, CartoonConfig.SPEED, new FullPlaySpeedSelectDialog.OnFullPlaySpeedSelectListener() {
                        @Override
                        public void playSpeed(float speed) {

                            CartoonConfig.SPEED=speed;
                            mPlaySpeed.setText(speed + "X");
                            setSpeed(speed, true);
                        }
                    });
                    mFullPlaySpeedSelectDialog.show();
                    //全屏模式下倍速选择框，打开后2秒自动关闭
                    mHandler.postDelayed(mRunnable, 2 * 1000);
                } else {
                    PlaySpeedSelectDialog dialog = new PlaySpeedSelectDialog(mContext, CartoonConfig.SPEED, new PlaySpeedSelectDialog.OnPlaySpeedSelectListener() {
                        @Override
                        public void playSpeed(float speed) {
                            CartoonConfig.SPEED = speed;
                            mPlaySpeed.setText(speed + "X");
                            setSpeed(speed, true);
                        }
                    });
                    dialog.show();
                }
            }
        });
    }

    @Override
    protected void onChangeSeekPause() {
        super.onChangeSeekPause();
        getGSYVideoManager().pause(true);
    }

    @Override
    protected void cloneParams(GSYBaseVideoPlayer from, GSYBaseVideoPlayer to) {
        super.cloneParams(from, to);
        CustomGSYVideoPlayer videoFrom = (CustomGSYVideoPlayer) from;
        CustomGSYVideoPlayer videoTo = (CustomGSYVideoPlayer) to;
        if (videoFrom.mTitleTextView != null && videoTo.mTitleTextView != null) {
            videoTo.mTitleTextView.setText(videoFrom.mTitleTextView.getText());
        }
        videoTo.mGifCreateHelper = videoFrom.mGifCreateHelper;
        onCloneParamsCommon(videoFrom, videoTo);
        onCloneSeekPosition(videoFrom, videoTo);
        onCloneFloatWindowIcon(videoFrom, videoTo);
    }

    private void onCloneParamsCommon(CustomGSYVideoPlayer videoFrom, CustomGSYVideoPlayer videoTo) {
        videoTo.mVideoForward.setVisibility(videoFrom.mVideoForward.getVisibility());
        videoTo.mVideoBackward.setVisibility(videoFrom.mVideoBackward.getVisibility());
        // 只有网络视频才往下处理
        if (mIsLocalVideo) {
            return;
        }
        videoTo.mPlaySpeed.setText(videoFrom.mPlaySpeed.getText());
    }

    private void onCloneFloatWindowIcon(CustomGSYVideoPlayer videoFrom,
                                        CustomGSYVideoPlayer videoTo) {
        if (videoFrom == this) {
            this.mIsLocalVideo = videoFrom.mIsLocalVideo;
        } else if (videoTo == this) {
            this.mIsLocalVideo = videoTo.mIsLocalVideo;
        }
    }

    private void onCloneSeekPosition(CustomGSYVideoPlayer videoFrom, CustomGSYVideoPlayer videoTo) {
        videoTo.mSeekOnStart = videoFrom.mSeekOnStart;
        videoTo.mSeekOnStartTime = videoFrom.mSeekOnStartTime;
    }

    @Override
    protected void onAudioManagerFocusChange(int focusChange) {
        super.onAudioManagerFocusChange(focusChange);
    }

    public void onDestroy() {
        getCurrentPlayer().getMapHeadData().clear();
        mAudioManager = null;
        mContext = null;
        getCurrentPlayer().cancelDismissControlViewTimer();
        getCurrentPlayer().cancelProgressTimer();
        getCurrentPlayer().unListenerNetWorkState();
        if (currentPlayer != null && currentPlayer.mGifCreateHelper != null) {
            currentPlayer.mGifCreateHelper.cancelTask();
        }
    }

    /**
     * 旋转画面
     */
    public void rotation() {
        if ((mTextureView.getRotation() - mRotate) == 270) {
            mTextureView.setRotation(mRotate);
            mTextureView.requestLayout();
        } else {
            mTextureView.setRotation(mTextureView.getRotation() + 90);
            mTextureView.requestLayout();
        }
    }

    @Override
    protected void rePlay() {
        getCurrentPlayer().onPrepared();
        getCurrentPlayer().setSeekOnStart(0);
        getCurrentPlayer().startPlayLogic();
    }


    /**
     * 快进或快退 2019 06
     *
     * @param time
     */
    public void playSeekTimePosition(int time) {
        onChangeSeekPause();
        int totalTimeDuration = getDuration();
        mCurrentPosition = time;
        int prosess = 0;
        if (totalTimeDuration != 0) {
            prosess = (int) (time * 100) / totalTimeDuration;
        }
        if (mBottomProgressBar != null) {
            mBottomProgressBar.setProgress(prosess);
        }
        if (mProgressBar != null) {
            mProgressBar.setProgress(prosess);
        }
        seekTo(time);
        setSeekOnStart(time);
        cancelDismissControlViewTimer();
        startDismissControlViewTimer();
        changeUiToPauseClear();
        changeUiToPauseShow();
        setTextAndProgress(prosess);
    }

    Handler mHandler = new Handler();
    Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (mFullPlaySpeedSelectDialog != null && mFullPlaySpeedSelectDialog.isShowing()) {
                mFullPlaySpeedSelectDialog.dismiss();
            }
        }
    };

    /**
     * 保存截图
     */
    public void taskShotPicScreen() {
        taskShotPic(bitmap -> {
            if (bitmap != null) {
                // 保存图片至指定路径
                String storePath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        + File.separator;
                Intent intent = com.heiheilianzai.app.utils.FileUtils.saveImageToGallery(bitmap, storePath);
                getContext().sendBroadcast(intent);
                layoutScreenshot.setVisibility(VISIBLE);
                ivScreenshot.setImageBitmap(bitmap);
            }
        });
    }

    /**
     * 展示录制GIF界面
     */
    public void taskGif() {
        onVideoPause();
        setStateAndUi(CURRENT_STATE_PAUSE);
        currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
        ViewGroup.LayoutParams layoutParams = getRenderProxy().getLayoutParams();
        //9:16竖版视频 下一版本优化
//        if (getCurrentVideoHeight() > getCurrentVideoWidth()) {
//            layoutParams.height = (int) getResources().getDimension(R.dimen.dimens_350dp);
//            layoutParams.width = (int) getResources().getDimension(R.dimen.dimens_196dp);
//        } else {
//            layoutParams.height = (int) getResources().getDimension(R.dimen.dimens_196dp);
//            layoutParams.width = (int) getResources().getDimension(R.dimen.dimens_350dp);
//        }
        layoutParams.height = (int) getResources().getDimension(R.dimen.dimens_196dp);
        layoutParams.width = (int) getResources().getDimension(R.dimen.dimens_360dp);
        getRenderProxy().setLayoutParams(layoutParams);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mBottomProgressBar, VISIBLE);
        setViewShowState(mLockScreen, GONE);
        mVideoBackward.setVisibility(View.GONE);
        mVideoForward.setVisibility(View.GONE);
        layoutGif.setVisibility(VISIBLE);
        tvGifSaveTime.setVisibility(VISIBLE);
        tvGifSaveTime.postDelayed(new Runnable() {
            @Override
            public void run() {
                //开始缓存各个帧
                currentPlayer.clickStartIcon();
                setViewShowState(mTopContainer, INVISIBLE);
                setViewShowState(mBottomContainer, INVISIBLE);
                setViewShowState(mStartButton, INVISIBLE);
                setViewShowState(mLoadingProgressBar, INVISIBLE);
                setViewShowState(mBottomProgressBar, INVISIBLE);
                setViewShowState(mLockScreen, GONE);
                currentPlayer.mGifCreateHelper.startGif(new File(FileUtils.getPath()));
                currentPlayer.gifCurrentPositionWhenPlaying = getCurrentPositionWhenPlaying();
            }
        }, 700);
    }

    /**
     * 生成gif
     *
     * @param currentPosition 视频当前播放进度
     * @param isNextBtn       是否是点击了下一步按钮 调用此方法
     */
    public void makeGifProgress(int currentPosition, boolean isNextBtn) {
        currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
        if (currentPlayer.tvGifSaveTime.getVisibility() == VISIBLE) {
            int i = (currentPosition - currentPlayer.gifCurrentPositionWhenPlaying) / 1000;
            if (isNextBtn) {
                if (i < 1) {
                    currentPlayer.mGifCreateHelper.cancelTask();
                    initMakeGif();
                } else {
                    makeGif();
                }
            }
            if (i < 8) {
                String s = getContext().getString(R.string.save_gif_max_second, (i + 1) + "");
                currentPlayer.tvGifSaveTime.setText(s);
                if (i == 7) {
                    makeGif();
                }
            }
        }
    }

    /**
     * 保存gif
     */
    private void makeGif() {
        String gifPath = Environment.getExternalStorageDirectory().getAbsolutePath()
                + File.separator;
        filegif = new File(gifPath, System.currentTimeMillis() + ".gif");
        currentPlayer.mGifCreateHelper.stopGif(filegif);
        onVideoPause();
        setStateAndUi(CURRENT_STATE_PAUSE);
        setViewShowState(mTopContainer, INVISIBLE);
        setViewShowState(mBottomContainer, INVISIBLE);
        setViewShowState(mStartButton, INVISIBLE);
        setViewShowState(mLoadingProgressBar, INVISIBLE);
        setViewShowState(mBottomProgressBar, INVISIBLE);
        setViewShowState(mLockScreen, GONE);
        RotateAnimation rotate = new RotateAnimation(
                0,
                360,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        rotate.setRepeatCount(INFINITE);
        rotate.setRepeatMode(RESTART);
        rotate.setDuration(800);
        rotate.setInterpolator(new LinearInterpolator());
        currentPlayer.ivGifSaveLoading.setVisibility(VISIBLE);
        currentPlayer.ivGifSaveLoading.setAnimation(rotate);
        currentPlayer.ivGifSaveLoading.startAnimation(rotate);
        currentPlayer.tvGifSaveLoading.setVisibility(VISIBLE);
        currentPlayer.layoutGifImg.setVisibility(VISIBLE);
        currentPlayer.progressBarGif.setVisibility(VISIBLE);
        currentPlayer.tvGifSaveTitTle.setText(R.string.make_gif);
        currentPlayer.tvGifSaveTime.setVisibility(GONE);
        currentPlayer.tvGifFinish.setVisibility(GONE);
    }

    /**
     * 关闭录制gif
     */
    private void initMakeGif() {
        currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
        currentPlayer.mGifCreateHelper.cancelTask();
        layoutGif.setVisibility(GONE);
        tvGifSaveTime.setVisibility(GONE);
        currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
        currentPlayer.tvGifFinish.setVisibility(VISIBLE);
        currentPlayer.tvGifSaveTitTle.setText(R.string.record_gif);
        currentPlayer.tvGifSaveTime.setText("");
        currentPlayer.ivGif.setImageBitmap(null);
        currentPlayer.progressBarGif.setProgress(0);
        currentPlayer.tvGifSaveSuccess.setVisibility(INVISIBLE);
        currentPlayer.layoutGifImg.setVisibility(GONE);
        ViewGroup.LayoutParams layoutParams = currentPlayer.getRenderProxy().getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.FILL_PARENT;
        layoutParams.width = ViewGroup.LayoutParams.FILL_PARENT;
        currentPlayer.getRenderProxy().setLayoutParams(layoutParams);
        currentPlayer.clickStartIcon();
        currentPlayer.mVideoAllCallBack.onClickBlankFullscreen(mOriginUrl, mTitle, this);
        //手动调用ontouch事件
        MotionEvent downEvent = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_DOWN,
                0,
                0,
                0
        );
        MotionEvent upEvent = MotionEvent.obtain(
                SystemClock.uptimeMillis(),
                SystemClock.uptimeMillis(),
                MotionEvent.ACTION_UP,
                0,
                0,
                0
        );
        currentPlayer.mTextureViewContainer.onTouchEvent(downEvent);
        currentPlayer.mTextureViewContainer.onTouchEvent(upEvent);
        currentPlayer.mTextureViewContainer.callOnClick();
        downEvent.recycle();
        upEvent.recycle();
    }

    /**
     * 录制gif工具类
     */
    private void initGifHelper() {
        mGifCreateHelper = new GifCreateHelper(this, new GSYVideoGifSaveListener() {
            @Override
            public void result(boolean success, File file) {
                tvTakeGif.post(new Runnable() {
                    @Override
                    public void run() {
                        currentPlayer = (CustomGSYVideoPlayer) getCurrentPlayer();
                        currentPlayer.tvGifSaveSuccess.setVisibility(VISIBLE);
                        currentPlayer.ivGifSaveLoading.setVisibility(GONE);
                        currentPlayer.ivGifSaveLoading.clearAnimation();
                        currentPlayer.tvGifSaveLoading.setVisibility(GONE);
                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                        Uri uri = Uri.fromFile(file);
                        intent.setData(uri);
                        getContext().sendBroadcast(intent);
                        Glide.with(new WeakReference<>(getContext()).get())
                                .asGif()
                                .load(file)
                                .into(currentPlayer.ivGif);
                    }
                });
            }

            @Override
            public void process(int curPosition, int total) {
                currentPlayer.progressBarGif.setMax(total);
                currentPlayer.progressBarGif.setProgress(curPosition);
            }
        });
    }

    private void getShotOrGif(int type) {
        if (getCurrentState() == CURRENT_STATE_PLAYING) {
        }
    }

    public void showPermissionDialogs(String message, boolean never, int requestCode, int type) {

    }
}