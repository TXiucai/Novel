package com.heiheilianzai.app.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.shehuan.niv.Utils;

import java.util.ArrayList;
import java.util.List;

/**
 * description: 自定义签到View.
 */
public class StepView extends View {

    /**
     * 线段的高度
     */
    private float mCompletedLineHeight = Utils.dp2px(getContext(), 4f);
    /**
     * 图标宽度
     */
    private float mIconWidth = Utils.dp2px(getContext(), 20f);
    /**
     * 图标的高度
     */
    private float mIconHeight = Utils.dp2px(getContext(), 20f);
    /**
     * 大奖的图标的宽度
     */
    private float mUpWidth = Utils.dp2px(getContext(), 30f);
    /**
     * 大奖的图标的高度
     */
    private float mUpHeight = Utils.dp2px(getContext(), 30f);
    /**
     * 线段长度
     */
    private float mLineWidth = (ScreenSizeUtils.getInstance(getContext()).getScreenWidth() - mIconHeight * 5 - mUpWidth * 2 - Utils.dp2px(getContext(), 50f)) / 6;
    /**
     * 已经完成的图标
     */
    private Drawable mCompleteIcon;
    /**
     * 正在进行的图标
     */
    private Drawable mAttentionIcon;
    /**
     * 第三天图标
     */
    private Drawable mThreeUnCompleteIcon;
    /**
     * 第三天图标
     */
    private Drawable mThreeCompleteIcon;
    /**
     * 第七天图标
     */
    private Drawable mSevenUnCompleteIcon;
    /**
     * 第七天图标
     */
    private Drawable mSevenCompleteIcon;
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
    private List<String> mStepBeanList;
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
     * 完成的线段paint
     */
    private Paint mTextUnCompletePaint;
    private Paint mTextUnBigCompletePaint;
    private Paint mTextCompletePaint;
    /**
     * 未完成颜色
     */
    private int mUnCompletedLineColor = ContextCompat.getColor(getContext(), R.color.color_e6e6e6);
    /**
     * 积分颜色
     */
    private int mUnCompletedTextColor = ContextCompat.getColor(getContext(), R.color.color_9a9a9a);
    /**
     * 天数颜色
     */
    private int mBigUnCompletedTextColor = ContextCompat.getColor(getContext(), R.color.color_4b4242);
    /**
     * 完成的颜色
     */
    private int mCompletedLineColor = ContextCompat.getColor(getContext(), R.color.color_ff8350);

    /**
     * 签到几天
     */
    private int mSignDay;


    private int mDp2 = Utils.dp2px(getContext(), 2f);

    public StepView(Context context) {
        this(context, null);
    }

    public StepView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public StepView(Context context, AttributeSet attrs, int defStyle) {
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
        //积分画笔未完成
        mTextUnCompletePaint = new Paint();
        mTextUnCompletePaint.setAntiAlias(true);
        mTextUnCompletePaint.setColor(mUnCompletedTextColor);
        mTextUnCompletePaint.setStyle(Paint.Style.FILL);
        mTextUnCompletePaint.setTextSize(Utils.dp2px(getContext(), 9));
        //积分画笔未完成大礼物
        mTextUnBigCompletePaint = new Paint();
        mTextUnBigCompletePaint.setAntiAlias(true);
        mTextUnBigCompletePaint.setColor(mBigUnCompletedTextColor);
        mTextUnBigCompletePaint.setStyle(Paint.Style.FILL);
        mTextUnBigCompletePaint.setTextSize(Utils.dp2px(getContext(), 9));
        //积分画笔完成
        mTextCompletePaint = new Paint();
        mTextCompletePaint.setAntiAlias(true);
        mTextCompletePaint.setColor(mCompletedLineColor);
        mTextCompletePaint.setStyle(Paint.Style.FILL);
        mTextCompletePaint.setTextSize(Utils.dp2px(getContext(), 9));
        //已经完成的icon
        mCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_complete);
        //正在进行的icon
        mAttentionIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_uncomplete);
        // 第三天图标未完成
        mThreeUnCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_three_uncomplete);
        // 第三天图标完成
        mThreeCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_three_complete);
        // 第七天图标未完成
        mSevenUnCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_seven_uncomplete);
        // 第七天图标完成
        mSevenCompleteIcon = ContextCompat.getDrawable(getContext(), R.mipmap.sign_seven_complete);
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
        mCenterY = mUpHeight / 2;
        //获取左上方Y的位置，获取该点的意义是为了方便画矩形左上的Y位置
        mLeftY = mCenterY - (mCompletedLineHeight / 2);
        //获取右下方Y的位置，获取该点的意义是为了方便画矩形右下的Y位置
        mRightY = mCenterY + mCompletedLineHeight / 2;
        //计算图标中心点
        mCircleCenterPointPositionList.clear();
        //第一个点距离父控件左边14.5dp
        float size = mIconWidth / 2;
        mCircleCenterPointPositionList.add(size);
        for (int i = 1; i < mStepNum; i++) {
            //从第二个点开始，每个点距离上一个点为图标的宽度加上线段的长度
            if (i == 3 || i == 6) {
                size = size + mUpWidth / 2 + mIconWidth / 2 + mLineWidth;
            } else {
                size = size + mIconWidth + mLineWidth;
            }
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
     * 绘制签到
     */
    @SuppressLint("DrawAllocation")
    private void drawSign(Canvas canvas) {
        for (int i = 0; i < (mCircleCenterPointPositionList.size() - 1) * 2; i++) {
            float preComplectedXPosition;
            //绘制线段
            if (i / 2 == 3) {
                preComplectedXPosition = mCircleCenterPointPositionList.get(i / 2) + mUpWidth / 2;
            } else {
                preComplectedXPosition = mCircleCenterPointPositionList.get(i / 2) + mIconWidth / 2;
            }

            if (i < mSignDay * 2 - 1) {
                //下一个是已完成，当前才需要绘制
                if (i % 2 == 0) {
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth / 2,
                            mRightY, mCompletedPaint);
                } else {
                    canvas.drawRect(preComplectedXPosition + mLineWidth / 2, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mCompletedPaint);
                }

            } else {
                //其余绘制灰色
                if (i % 2 == 0) {
                    canvas.drawRect(preComplectedXPosition, mLeftY, preComplectedXPosition + mLineWidth / 2,
                            mRightY, mUnCompletedPaint);
                } else {
                    canvas.drawRect(preComplectedXPosition + mLineWidth / 2, mLeftY, preComplectedXPosition + mLineWidth,
                            mRightY, mUnCompletedPaint);
                }
            }
        }
        for (int i = 0; i < mCircleCenterPointPositionList.size(); i++) {
            //绘制图标
            float currentComplectedXPosition = mCircleCenterPointPositionList.get(i);
            Rect rect;
            Paint dayPaint;
            if (i == 3 || i == 6) {
                rect = new Rect((int) (currentComplectedXPosition - mUpWidth / 2),
                        (int) (mCenterY - mUpHeight / 2),
                        (int) (currentComplectedXPosition + mUpWidth / 2),
                        (int) (mCenterY + mUpHeight / 2));
            } else {
                rect = new Rect((int) (currentComplectedXPosition - mIconWidth / 2),
                        (int) (mCenterY - mIconHeight / 2),
                        (int) (currentComplectedXPosition + mIconWidth / 2),
                        (int) (mCenterY + mIconHeight / 2));
            }

            if (i < mSignDay) {
                if (i == 3) {
                    mThreeCompleteIcon.setBounds(rect);
                    mThreeCompleteIcon.draw(canvas);
                } else if (i == 6) {
                    mSevenCompleteIcon.setBounds(rect);
                    mSevenCompleteIcon.draw(canvas);
                } else {
                    mCompleteIcon.setBounds(rect);
                    mCompleteIcon.draw(canvas);
                }
                dayPaint = mTextCompletePaint;
            } else {
                if (i == 3) {
                    mThreeUnCompleteIcon.setBounds(rect);
                    mThreeUnCompleteIcon.draw(canvas);
                    dayPaint = mTextUnBigCompletePaint;
                } else if (i == 6) {
                    mSevenUnCompleteIcon.setBounds(rect);
                    mSevenUnCompleteIcon.draw(canvas);
                    dayPaint = mTextUnBigCompletePaint;
                } else {
                    mAttentionIcon.setBounds(rect);
                    mAttentionIcon.draw(canvas);
                    dayPaint = mTextUnCompletePaint;
                }
            }
            String couponText = mStepBeanList.get(i);
            int xCouponText;
            if (couponText.length() > 3) {
                xCouponText = Utils.dp2px(getContext(), 10f);
            } else {
                xCouponText = Utils.dp2px(getContext(), 8f);
            }
            canvas.drawText(couponText,
                    currentComplectedXPosition - xCouponText,
                    mCenterY + Utils.dp2px(getContext(), 27f),
                    dayPaint);
        }
    }

    /**
     * 设置流程步数
     *
     * @param stepsBeanList 流程步数
     */
    public void setStepNum(List<String> stepsBeanList, int signDay) {
        if (stepsBeanList == null && stepsBeanList.size() == 0) {
            return;
        }
        mSignDay = signDay;
        mStepBeanList = stepsBeanList;
        mStepNum = mStepBeanList.size();
        setChange();//重新绘制
        //引起重绘
        postInvalidate();
    }
}