package com.heiheilianzai.app.view.read;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.ui.dialog.read.AutoProgressBar;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.PageFactory;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.animation.AnimationProvider;
import com.heiheilianzai.app.view.animation.CoverAnimation;
import com.heiheilianzai.app.view.animation.NoneAnimation;
import com.heiheilianzai.app.view.animation.PageAnimation;
import com.heiheilianzai.app.view.animation.SimulationAnimation;
import com.heiheilianzai.app.view.animation.SlideAnimation;

/**
 * Created by scb on 2018/8/29 0029.
 */
public class PageWidget extends View {
    private final static String TAG = "BookPageWidget";
    private int mScreenWidth = 0; // 屏幕宽
    public int mScreenHeight = 0; // 屏幕高
    private Context mContext;
    //是否移动了
    private Boolean isMove = false;
    //是否翻到下一页
    private Boolean isNext = false;
    //是否取消翻页
    private Boolean cancelPage = false;
    //是否没下一页或者上一页
    private Boolean noNext = false;
    private int downX = 0;
    private int downY = 0;
    private int moveX = 0;
    private int moveY = 0;
    private int downXPre = 0;
    //翻页动画是否在执行
    private Boolean isRuning = false;
    Bitmap mCurPageBitmap = null; // 当前页
    Bitmap mNextPageBitmap = null;
    private AnimationProvider mAnimationProvider;
    private PageAnimation mPageAnim;
    Scroller mScroller;
    public int mBgColor = 0xFFCEC29C;
    private TouchListener mTouchListener;
    FrameLayout ADview;
    public int Current_Page;
    boolean onTouchEventing;
    private boolean mLeftScreen = false;
    private boolean mIsOpenService = false;

    public void setmIsOpenService(boolean mIsOpenService) {
        this.mIsOpenService = mIsOpenService;
    }

    public boolean ismIsOpenService() {
        return mIsOpenService;
    }

    public void setmLeftScreen(boolean mLeftScreen) {
        this.mLeftScreen = mLeftScreen;
    }

    public void setADview(FrameLayout ADview) {
        this.ADview = ADview;
    }

    public PageWidget(Context context) {
        this(context, null);
    }

    public PageWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PageWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initPage();
        mScroller = new Scroller(getContext(), new LinearInterpolator());
        mAnimationProvider = new SimulationAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
    }

    private void initPage() {
        mScreenWidth = ScreenSizeUtils.getInstance(mContext).getScreenWidth();
        mScreenHeight = ScreenSizeUtils.getInstance(mContext).getScreenHeight();  //RGB_565
        if (ReaderConfig.TOP_READ_AD != null) {
            mScreenHeight = mScreenHeight - ImageUtil.dp2px(mContext, 60);
        }
        if (ReaderConfig.BOTTOM_READ_AD != null) {
            mScreenHeight = mScreenHeight - ImageUtil.dp2px(mContext, 60);
        }
        MyToash.Log("mScreenHeight", mScreenHeight + "");
        try {
            mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);      //android:LargeHeap=true  use in  manifest application
            mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.RGB_565);
        } catch (Error e) {
            mCurPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ALPHA_8);      //android:LargeHeap=true  use in  manifest application
            mNextPageBitmap = Bitmap.createBitmap(mScreenWidth, mScreenHeight, Bitmap.Config.ALPHA_8);
        }
    }

    public void setPageMode(int pageMode) {
        switch (pageMode) {
            case ReadingConfig.PAGE_MODE_SIMULATION:
                mAnimationProvider = new SimulationAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case ReadingConfig.PAGE_MODE_COVER:
                mAnimationProvider = new CoverAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case ReadingConfig.PAGE_MODE_SLIDE:
                mAnimationProvider = new SlideAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            case ReadingConfig.PAGE_MODE_NONE:
                mAnimationProvider = new NoneAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
                break;
            default:
                mAnimationProvider = new SimulationAnimation(mCurPageBitmap, mNextPageBitmap, mScreenWidth, mScreenHeight);
        }
    }

    public Bitmap getCurPage() {
        return mCurPageBitmap;
    }

    public Bitmap getNextPage() {
        return mNextPageBitmap;
    }

    public interface OnSwitchPreListener {
        void switchPreChapter();
    }

    private OnSwitchPreListener mOnSwitchPreListener;

    public void setOnSwitchPreListener(OnSwitchPreListener listener) {
        mOnSwitchPreListener = listener;
    }

    public interface OnSwitchNextListener {
        void switchNextChapter();
    }

    private OnSwitchNextListener mOnSwitchNextListener;

    public void setOnSwitchNextListener(OnSwitchNextListener listener) {
        mOnSwitchNextListener = listener;
    }

    public void setBgColor(int color) {
        mBgColor = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawColor(mBgColor);
        Utils.printLog("onDraw", "isNext:" + isNext + "          isRuning:" + isRuning);
        if (isRuning) {
            try {
                mAnimationProvider.drawMove(canvas);
            } catch (Exception e) {
            }
        } else {
            mAnimationProvider.drawStatic(canvas);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (ADview != null) {
            ADview.setVisibility(INVISIBLE);
        }
        if (PageFactory.getStatus() == PageFactory.Status.OPENING) {
            onTouchEventing = false;
            return false;
        }
        int x = (int) event.getX();
        int y = (int) event.getY();
        mAnimationProvider.setTouchPoint(x, y);
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) event.getX();
            downY = (int) event.getY();
            moveX = 0;
            moveY = 0;
            isMove = false;
            noNext = false;
            isNext = false;
            isRuning = false;
            mAnimationProvider.setStartPoint(downX, downY);
            abortAnimation();
            mTouchListener.down();
            Utils.printLog(TAG, "ACTION_DOWN");
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            final int slop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
            if (mIsOpenService) {
                return true;
            }
            //判断是否移动了
            if (!isMove) {
                isMove = Math.abs(downX - x) > slop || Math.abs(downY - y) > slop;
            }
            if (isMove) {
                isMove = true;
                if (moveX == 0 && moveY == 0) {
                    //判断翻得是上一页还是下一页
                    isNext = x - downX <= 0;
                    cancelPage = false;
                    if (isNext) {
                        Boolean isNext = mTouchListener.nextPage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                        if (!isNext) {
                            onTouchEventing = false;
                            noNext = true;
                            return true;
                        }
                    } else {
                        Boolean isPre = mTouchListener.prePage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                        if (!isPre) {
                            noNext = true;
                            onTouchEventing = false;
                            return true;
                        }
                    }
                    Utils.printLog(TAG, "isNext:" + isNext);
                } else {
                    //判断是否取消翻页
                    if (isNext) {
                        if (x - moveX > 0) {
                            cancelPage = true;
                            mAnimationProvider.setCancel(true);
                        } else {
                            cancelPage = false;
                            mAnimationProvider.setCancel(false);
                        }
                    } else {
                        if (x - moveX < 0) {
                            mAnimationProvider.setCancel(true);
                            cancelPage = true;
                        } else {
                            mAnimationProvider.setCancel(false);
                            cancelPage = false;
                        }
                    }
                    Utils.printLog(TAG, "cancelPage:" + cancelPage);
                }
                moveX = x;
                moveY = y;
                isRuning = true;
                this.postInvalidate();
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            mTouchListener.up();
            Utils.printLog(TAG, "ACTION_UP");
            if (!isMove) {
                cancelPage = false;
                //是否点击了中间
                if (downX > mScreenWidth / 5 && downX < mScreenWidth * 4 / 5) {//&& downY > mScreenHeight / 3 && downY < mScreenHeight * 2 / 3
                    if (mTouchListener != null) {
                        mTouchListener.center();
                    }
                    onTouchEventing = false;
                    return true;
                } else isNext = x >= mScreenWidth / 2;
                if (mIsOpenService) {
                    return true;
                }
                if (isNext) {
                    Boolean isNext = mTouchListener.nextPage();
                    mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                    if (!isNext) {
                        onTouchEventing = false;
                        return true;
                    }
                } else {
                    if (mLeftScreen) {
                        Boolean isNext = mTouchListener.nextPage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                        if (!isNext) {
                            onTouchEventing = false;
                            return true;
                        }
                    } else {
                        Boolean isPre = mTouchListener.prePage();
                        mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                        if (!isPre) {
                            onTouchEventing = false;
                            return true;
                        }
                    }
                }
            }
            if (cancelPage && mTouchListener != null) {
                mTouchListener.cancel();
            }
            Utils.printLog(TAG, "isNext:" + isNext);
            if (!noNext) {
                isRuning = true;
                mAnimationProvider.startAnimation(mScroller, new AnimationProvider.OnAnimationStopped() {
                    @Override
                    public void nextStop() {
                        ++Current_Page;
                        onTouchEventing = false;
                        if (AutoProgressBar.getInstance().isStarted()) {
                            AutoProgressBar.getInstance().restart();
                        }
                        if (mOnSwitchNextListener != null) {
                            mOnSwitchNextListener.switchNextChapter();
                            Utils.printLog("onDrawaaa", "开始加载下一章");
                            mOnSwitchNextListener = null;
                        }
                    }

                    @Override
                    public void preStop() {
                        --Current_Page;
                        onTouchEventing = false;
                        if (AutoProgressBar.getInstance().isStarted()) {
                            AutoProgressBar.getInstance().restart();
                        }
                        if (mOnSwitchPreListener != null) {
                            mOnSwitchPreListener.switchPreChapter();
                            Utils.printLog("onDrawaaa", "开始加载上一章");
                            mOnSwitchPreListener = null;
                        }
                    }
                });
                this.postInvalidate();
            }
        }
        onTouchEventing = false;
        return true;
    }

    public void next_page(boolean isNextOrPre) {
        if (PageFactory.getStatus() == PageFactory.Status.OPENING) {
            return;
        }
        downX = mScreenWidth * 4 / 5;
        downXPre = mScreenWidth / 5;
        downY = mScreenHeight / 2;
        moveX = 0;
        moveY = 0;
        isMove = false;
        noNext = false;
        isNext = false;
        isRuning = false;
        if (isNextOrPre) {
            mAnimationProvider.setTouchPoint(downX, downY);
            mAnimationProvider.setStartPoint(downX, downY);
        } else {
            mAnimationProvider.setTouchPoint(downXPre, downY);
            mAnimationProvider.setStartPoint(downXPre, downY);
        }
        abortAnimation();
        if (!isMove) {
            cancelPage = false;
            isNext = true;
            if (ADview != null) {
                ADview.setVisibility(INVISIBLE);
            }
            if (isNext && isNextOrPre) {
                Boolean isNext = mTouchListener.nextPage();
                mAnimationProvider.setDirection(AnimationProvider.Direction.next);
                Utils.printLog("onDrawaaa", "isNext:" + isNext);
            }
            if (!isNextOrPre) {
                Boolean isPre = mTouchListener.prePage();
                mAnimationProvider.setDirection(AnimationProvider.Direction.pre);
                Utils.printLog("onDrawaaa", "isPre:" + isPre);
            }
        }
        if (cancelPage && mTouchListener != null) {
            mTouchListener.cancel();
        }
        if (!noNext) {
            isRuning = true;
            mAnimationProvider.startAnimation(mScroller, new AnimationProvider.OnAnimationStopped() {
                @Override
                public void nextStop() {
                    ++Current_Page;
                    onTouchEventing = false;

                    if (mOnSwitchNextListener != null) {
                        mOnSwitchNextListener.switchNextChapter();
                        Utils.printLog("onDrawaaa", "开始加载下一章");
                        mOnSwitchNextListener = null;
                    }
                }

                @Override
                public void preStop() {
                    --Current_Page;
                    onTouchEventing = false;
                    if (mOnSwitchPreListener != null) {
                        mOnSwitchPreListener.switchPreChapter();
                        Utils.printLog("onDrawaaa", "开始加载上一章");
                        mOnSwitchPreListener = null;
                    }
                }
            });
            this.postInvalidate();
        }
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            float x = mScroller.getCurrX();
            float y = mScroller.getCurrY();
            mAnimationProvider.setTouchPoint(x, y);
            if (mScroller.getFinalX() == x && mScroller.getFinalY() == y) {
                isRuning = false;
            }
            postInvalidate();
        }
        super.computeScroll();
    }

    public void abortAnimation() {
        if (!mScroller.isFinished()) {
            mScroller.abortAnimation();
            mAnimationProvider.setTouchPoint(mScroller.getFinalX(), mScroller.getFinalY());
            postInvalidate();
        }
    }

    public boolean isRunning() {
        return isRuning;
    }

    public void setTouchListener(TouchListener mTouchListener) {
        this.mTouchListener = mTouchListener;
    }

    public interface TouchListener {
        void center();

        Boolean prePage();

        Boolean nextPage();

        void cancel();

        void down();

        void up();
    }

}
