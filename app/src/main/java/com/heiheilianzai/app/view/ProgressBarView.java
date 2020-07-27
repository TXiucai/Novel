package com.heiheilianzai.app.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ProgressBar;

/**
 * 版本更新下载进度自定义控件
 */
public class ProgressBarView extends ProgressBar {
    //字体大小
    protected int mTextSize = 12;
    //字体颜色
    protected int mTextColor = Color.BLACK;
    //没有到达(右边progressbar的颜色)
    protected int mUnReachColor = Color.WHITE;
    //progressbar的高度
    protected int mProgressHeight = 6;
    //progressbar进度的颜色
    protected int mReachColor = mTextColor;
    //字体间距
    protected int mTextOffset = 0;
    protected Paint mPaint;
    Paint textPaint;
    //progressbar真正的宽度
    protected int mRealWith;

    public ProgressBarView(Context context) {
        this(context, null);
    }

    public ProgressBarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ProgressBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取自定义属性
        obtainStyledAttrs(attrs);
        mPaint = new Paint();
        mPaint.setTextSize(mTextSize);
        textPaint = new Paint();
        textPaint.setColor(mTextColor);
        textPaint.setTextSize(mTextSize);
    }

    /**
     * 获取自定义属性
     *
     * @param attrs
     */
    private void obtainStyledAttrs(AttributeSet attrs) {
        mTextSize = dp2px(10);
        mTextColor = Color.parseColor("#FF574C");
        mUnReachColor = Color.WHITE;
        mProgressHeight = dp2px(6);
        mReachColor = Color.parseColor("#FF574C");
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthVal = MeasureSpec.getSize(widthMeasureSpec);
        int height = measureHeight(heightMeasureSpec);
        setMeasuredDimension(widthVal, height);
        //计算progressbar真正宽度=控件的宽度-paddingleft-paddingright
        mRealWith = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        //保存画布
        canvas.save();
        //移动画布
        canvas.translate(getPaddingLeft(), getHeight() / 2);
        //计算左边进度在整个控件宽度的占比
        float radio = getProgress() * 1.0f / getMax();
        //获取左边进度的宽度
        float progressX = radio * mRealWith;
        //中间文字
        String text = getProgress() + "%";
        //获取文字的宽度
        int textWidth = (int) mPaint.measureText(text);
        if (progressX + textWidth + dp2px(3) > mRealWith) {//加偏移量微调，进度文字背景不被挤出去
            //左边进度+文字的宽度超过progressbar的宽度 5dp偏移量 重新计算左边进度的宽度 这个时候也就意味着不需要绘制右边进度
            progressX = mRealWith - textWidth - dp2px(5);
        }
        //绘制外部矩形背景
        mPaint.setStyle(Paint.Style.STROKE);//空心矩形框
        mPaint.setColor(Color.parseColor("#DFDFDF"));
        mPaint.setStrokeWidth(dp2px(1)); //线宽
        canvas.drawRoundRect(new RectF(dp2px(1), dp2px(-5), mRealWith - dp2px(2), dp2px(5)), dp2px(4), dp2px(4), mPaint);
        //计算左边进度结束的位置 如果结束的位置小于0就不需要绘制左边的进度
        float endX = progressX - mTextOffset / 2;
        if (endX > 0) {
            //绘制左边矩形加载进度
            mPaint.setColor(mReachColor);
            mPaint.setStrokeWidth(mProgressHeight);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(new RectF(dp2px(1), dp2px(-4), progressX + dp2px(4), dp2px(4)), dp2px(4), dp2px(4), mPaint);
        }
        mPaint.setColor(mTextColor);
        if (getProgress() != 0) {
            //计算文字基线
            int y = (int) (-(mPaint.descent() + mPaint.ascent()) / 2);
            //绘制进度文字底部白色背景
            mPaint.setStyle(Paint.Style.FILL);
            mPaint.setColor(Color.WHITE);
            canvas.drawRoundRect(new RectF(progressX, dp2px(-8), progressX + textWidth + dp2px(4), dp2px(8)), dp2px(4), dp2px(4), mPaint);
            //绘制进度文字底部外框
            mPaint.setStyle(Paint.Style.STROKE);//空心矩形框
            mPaint.setColor(Color.parseColor("#FF574C"));
            mPaint.setStrokeWidth(dp2px(1));//线宽
            canvas.drawRoundRect(new RectF(progressX, dp2px(-8), progressX + textWidth + dp2px(4), dp2px(8)), dp2px(4), dp2px(4), mPaint);
            //绘制进度文字
            canvas.drawText(text, progressX + dp2px(2), y, textPaint);
        }
        canvas.restore();
    }

    private int measureHeight(int heightMeasureSpec) {
        int result = 0;
        //获取高度模式
        int mode = MeasureSpec.getMode(heightMeasureSpec);
        //获取宽度模式
        int size = MeasureSpec.getSize(heightMeasureSpec);
        if (mode == MeasureSpec.EXACTLY) {
            //精准模式 用户设置为 比如80dp  match_parent fill_parent
            result = size;
        } else {
            //计算中间文字的高度
            int textHeight = (int) (mPaint.descent() - mPaint.ascent());
            //paddingTop+paddingBottom+ progressbar高度和文字高度的最大值
            result = getPaddingTop() + getPaddingBottom() + Math.max(mProgressHeight, Math.abs(textHeight));
            if (mode == MeasureSpec.AT_MOST) {
                result = Math.min(result, size);
            }
        }
        return result;
    }

    protected int dp2px(int dip) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, getResources().getDisplayMetrics());
    }
}