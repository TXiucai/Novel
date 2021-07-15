package com.heiheilianzai.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.ReadTimeBean;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.shehuan.niv.Utils;

import java.util.ArrayList;
import java.util.List;

public class ReadTimeView extends View {

    /**
     * 动画执行的时间 230毫秒
     */
    private final static int ANIMATION_TIME = 230;
    /**
     * 动画执行的间隔次数
     */
    private final static int ANIMATION_INTERVAL = 10;

    /**
     * 线段的高度
     */
    private float mCompletedLineHeight = Utils.dp2px(getContext(), 2f);

    /**
     * 图标宽度
     */
    private float mIconWidth = Utils.dp2px(getContext(), 28f);
    /**
     * 图标的高度
     */
    private float mIconHeight = Utils.dp2px(getContext(), 28f);
    /**
     * UP宽度
     */
    private float mUpWidth = Utils.dp2px(getContext(), 100f);
    /**
     * up的高度
     */
    private float mUpHeight = Utils.dp2px(getContext(), 22.5f);

    /**
     * 线段长度
     */
    private float mLineWidth = (ScreenSizeUtils.getInstance(getContext()).getScreenWidth() - mIconHeight * 7 - Utils.dp2px(getContext(), 50f)) / 6;

    /**
     * 已经完成的图标
     */
    private Drawable mCompleteIcon;
    /**
     * 正在进行的图标
     */
    private Drawable mAttentionIcon;
    /**
     * 默认的图标
     */
    private Drawable mDefaultIcon;
    /**
     * UP图标
     */
    private Drawable mUpIcon;
    /**
     * 图标中心点Y
     */
    private float mCenterY;
    /**
     * 线段的左上方的Y
     */
    private float mLeftY;
    /**
     * 线段的右下方的Y
     */
    private float mRightY;

    /**
     * 数据源
     */
    private List<ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean> mStepBeanList;
    private int mStepNum = 0;

    /**
     * 图标中心点位置
     */
    private List<Float> mCircleCenterPointPositionList;
    /**
     * 未完成的线段Paint
     */
    private Paint mUnCompletedPaint;
    /**
     * 完成的线段paint
     */
    private Paint mCompletedPaint;
    /**
     * 未完成颜色
     */
    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.color_e6e6e6);
    /**
     * 积分颜色
     */
    private int mUnCompletedTextColor = ContextCompat.getColor(getContext(), R.color.color_e6e6e6);

    /**
     * 天数颜色
     */
    private int mUnCompletedDayTextColor = ContextCompat.getColor(getContext(), R.color.color_e6e6e6);

    /**
     * up魅力值颜色
     */
    private int mCurrentTextColor = ContextCompat.getColor(getContext(), R.color.white);
    /**
     * 完成的颜色
     */
    private int mCompletedLineColor = ContextCompat.getColor(getContext(), R.color.color_323334);

    private TextPaint mTextNumberPaint;
    private TextPaint mTextCompleteNumberPaint;


    private Paint mTextDayPaint;

    private Paint mUpPaint;

    /**
     * 是否执行动画
     */
    private boolean isAnimation = false;

    /**
     * 记录重绘次数
     */
    private int mCount = 0;

    /**
     * 执行动画线段每次绘制的长度，线段的总长度除以总共执行的时间乘以每次执行的间隔时间
     */
    private float mAnimationWidth = (mLineWidth / ANIMATION_TIME) * ANIMATION_INTERVAL;

    /**
     * 执行动画的位置
     */
    private int mStepNums;
    private int mMins;

    public ReadTimeView(Context context) {
        this(context, null);
    }

    public ReadTimeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ReadTimeView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    /**
     * init
     */
    private void init() {
        mStepBeanList = new ArrayList<>();

        mCircleCenterPointPositionList = new ArrayList<>();

        //未完成文字画笔
        mUnCompletedPaint = new Paint();
        mUnCompletedPaint.setAntiAlias(true);
        mUnCompletedPaint.setColor(mUnCompletedLineColor);
        mUnCompletedPaint.setStrokeWidth(2);
        mUnCompletedPaint.setStyle(Paint.Style.FILL);

        //已完成画笔文字
        mCompletedPaint = new Paint();
        mCompletedPaint.setAntiAlias(true);
        mCompletedPaint.setColor(mCompletedLineColor);
        mCompletedPaint.setStrokeWidth(2);
        mCompletedPaint.setStyle(Paint.Style.FILL);

        //number paint
        mTextNumberPaint = new TextPaint();
        mTextNumberPaint.setAntiAlias(true);
        mTextNumberPaint.setColor(mUnCompletedTextColor);
        mTextNumberPaint.setStyle(Paint.Style.FILL);
        mTextNumberPaint.setTextSize(Utils.dp2px(getContext(), 12f));

        mTextCompleteNumberPaint = new TextPaint();
        mTextCompleteNumberPaint.setAntiAlias(true);
        mTextCompleteNumberPaint.setColor(mCompletedLineColor);
        mTextCompleteNumberPaint.setStyle(Paint.Style.FILL);
        mTextCompleteNumberPaint.setTextSize(Utils.dp2px(getContext(), 12f));

        //number paint
        mTextDayPaint = new Paint();
        mTextDayPaint.setAntiAlias(true);
        mTextDayPaint.setColor(mUnCompletedDayTextColor);
        mTextDayPaint.setStyle(Paint.Style.FILL);
        mTextDayPaint.setTextSize(Utils.dp2px(getContext(), 10f));

        mUpPaint = new Paint();
        mUpPaint.setAntiAlias(true);
        mUpPaint.setColor(mCurrentTextColor);
        mUpPaint.setStyle(Paint.Style.FILL);
        mUpPaint.setTextSize(Utils.dp2px(getContext(), 10f));

        //已经完成的icon
        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.read_time_unlock);
        //正在进行的icon
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.mipmap.read_time_lock);
        //未完成的icon
        mDefaultIcon = ContextCompat.getDrawable(getContext(), R.mipmap.read_time_zero);
        //UP的icon
        mUpIcon = ContextCompat.getDrawable(getContext(), R.mipmap.read_time_tip);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        setChange();
    }

    private void setChange() {
        //图标的中中心Y点
        mCenterY = Utils.dp2px(getContext(), 28f) + mIconHeight / 2;
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;

        //计算图标中心点
        mCircleCenterPointPositionList.clear();
        //第一个点距离父控件左边14.5dp
        float size = mIconWidth / 2 ;
        mCircleCenterPointPositionList.add(size);

        for (int i = 1; i < mStepNum; i++) {
            //从第二个点开始，每个点距离上一个点为图标的宽度加上线段的23dp的长度
            size = size + mIconWidth + mLineWidth;
            mCircleCenterPointPositionList.add(size);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mStepBeanList.size() != 0) {
            drawSign(canvas);
        }
    }


    /**
     * 绘制初始状态的view
     */
    @SuppressLint("DrawAllocation")
    private void drawSign(Canvas canvas) {

        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            //绘制线段
            float preComplectedXPosition = mCircleCenterPointPositionList.get(i) + mIconWidth / 2;
            if (i != mCircleCenterPointPositionList.size() - 1) {
                //最后一条不需要绘制
                if (i < mStepNums) {
                    //下一个是已完成，当前才需要绘制
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mCompletedPaint);
                } else {
                    //其余绘制灰色
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mUnCompletedPaint);
                }
            }

        }
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            //绘制图标
            float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect;
            Paint dayPaint;

            rect = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                    (int) (mCenterY - mIconHeight / 2),
                    (int) (currentComplectedXPosition + mIconWidth / 2),
                    (int) (mCenterY + mIconHeight / 2));
            if (i == 0) {
                mDefaultIcon.setBounds(rect);
                mDefaultIcon.draw(canvas);
                dayPaint = mTextCompleteNumberPaint;
            } else if (i < mStepNums) {
                mCompleteIcon.setBounds(rect);
                mCompleteIcon.draw(canvas);
                dayPaint = mTextCompleteNumberPaint;
            } else {
                mAttentionIcon.setBounds(rect);
                mAttentionIcon.draw(canvas);
                dayPaint = mTextNumberPaint;
            }
            String couponText = mStepBeanList.get(i).getMinute();
            int xCouponText;
            xCouponText = Utils.dp2px(getContext(), 12f);
            canvas.drawText(String.format(couponText),
                    currentComplectedXPosition - xCouponText,
                    mCenterY + Utils.dp2px(getContext(), 40f),
                    dayPaint);
//            StaticLayout myStaticLayout = new StaticLayout(couponText, (TextPaint) dayPaint, canvas.getWidth(), Layout.Alignment.ALIGN_NORMAL, 1.0f, 0.0f, false);
//            canvas.translate(currentComplectedXPosition - xCouponText,
//                    mCenterY + Utils.dp2px(getContext(), 40f));
//            myStaticLayout.draw(canvas);
            //绘制UP
            if (i == mStepNums + 1) {
                //需要UP才进行绘制
                Rect rectUp =
                        new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                                (int) (mCenterY - mIconHeight / 2  - mUpHeight),
                                (int) (currentComplectedXPosition + mUpWidth / 2),
                                (int) (mCenterY - mIconHeight / 2));
                mUpIcon.setBounds(rectUp);
                mUpIcon.draw(canvas);
                canvas.drawText(String.format(getResources().getString(R.string.string_continue_accept_coupon), String.valueOf(Math.abs(10 - mMins % 10))),
                        currentComplectedXPosition - mUpWidth / 2 + 18f,
                        mCenterY - Utils.dp2px(getContext(), 23f),
                        mUpPaint);
            }
        }
    }

    /**
     * 设置流程步数
     *
     * @param stepsBeanList 流程步数
     */
    public void setStepNum(List<ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean> stepsBeanList, int mStepNums, int mMins) {

        if (stepsBeanList == null && stepsBeanList.size() == 0) {
            return;
        }
        this.mStepNums = mStepNums;
        this.mMins = mMins;
        mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();
        setChange();//重新绘制
        //引起重绘
        postInvalidate();
    }

    /**
     * 执行签到动画
     *
     * @param position 执行的位置
     */
    public void startSignAnimation(int position) {
        //线条从灰色变为绿色
        isAnimation = true;
        //引起重绘
        postInvalidate();
    }
}