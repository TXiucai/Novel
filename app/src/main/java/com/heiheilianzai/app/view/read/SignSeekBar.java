package com.heiheilianzai.app.view.read;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Html;
import android.text.Layout;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.IntDef;
import androidx.core.content.ContextCompat;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.SignUtils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.math.BigDecimal;
import java.text.NumberFormat;

public class SignSeekBar extends View {
    static final int NONE = -1;

    @IntDef({NONE, TextPosition.SIDES, TextPosition.BOTTOM_SIDES, TextPosition.BELOW_SECTION_MARK})
    @Retention(RetentionPolicy.SOURCE)
    public @interface TextPosition {
        int SIDES = 0, BOTTOM_SIDES = 1, BELOW_SECTION_MARK = 2;
    }

    private float mMin; // min
    private float mMax; // max
    private float mProgress; // real time value
    private boolean isFloatType; // support for float type output
    private int mTrackSize; // height of right-track(on the right of thumb)
    private int mSecondTrackSize; // height of left-track(on the left of thumb)
    private int mThumbRadius; // radius of thumb
    private int mThumbRadiusOnDragging; // radius of thumb when be dragging
    private int mTrackColor; // color of right-track
    private int mSecondTrackColor; // color of left-track
    private int mThumbColor; // color of thumb
    private int mSectionCount; // shares of whole progress(max - min)
    private boolean isShowSectionMark; // show demarcation points or not
    private boolean isAutoAdjustSectionMark; // auto scroll to the nearest section_mark or not
    private boolean isShowSectionText; // show section-text or not
    private int mSectionTextSize; // text size of section-text
    private int mSectionTextColor; // text color of section-text
    @TextPosition
    private int mSectionTextPosition = NONE; // text position of section-text relative to track
    private int mSectionTextInterval; // the interval of two section-text
    private boolean isShowThumbText; // show real time progress-text under thumb or not
    private int mThumbTextSize; // text size of progress-text
    private int mThumbTextColor; // text color of progress-text
    private boolean isShowProgressInFloat; // show sign-progress in float or not
    private boolean isTouchToSeek; // touch anywhere on track to quickly seek
    private boolean isSeekBySection; // seek by section, the progress may not be linear
    private long mAnimDuration; // duration of animation

    private int mSignBorderSize; // border size
    private boolean isShowSignBorder; // show sign border
    private int mSignBorderColor;// color of border color
    private int mUnusableColor;// color of border color
    private int mSignColor;// color of sign
    private int mSignTextSize; // text size of sign-progress
    private int mSignTextColor; // text color of sign-progress
    private int mSignHeight; //sign Height
    private int mSignWidth; //sign width

    private float mDelta; // max - min
    private float mSectionValue; // (mDelta / mSectionCount)
    private float mThumbCenterX; // X coordinate of thumb's center
    private float mTrackLength; // pixel length of whole track
    private float mSectionOffset; // pixel length of one section
    private boolean isThumbOnDragging; // is thumb on dragging or not
    private int mTextSpace; // space between text and track
    private boolean triggerSeekBySection;

    private OnProgressChangedListener mProgressListener; // progress changing listener
    private float mLeft; // space between left of track and left of the view
    private float mRight; // space between right of track and left of the view
    private Paint mPaint;
    private Rect mRectText;

    private boolean isTouchToSeekAnimEnd = true;
    private float mPreSecValue; // previous SectionValue
    private SignConfigBuilder mConfigBuilder; // config attributes
    private String[] mSidesLabels;
    private boolean isSidesLabels;
    private float mThumbBgAlpha; //  alpha of thumb shadow
    private float mThumbRatio; // ratio of thumb shadow
    private boolean isShowThumbShadow;
    private boolean isShowSign;
    private boolean isSignArrowAutofloat;

    private String mStartText;
    private String mEndText;

    private Rect valueSignBounds;
    private RectF roundRectangleBounds;
    private int mSignArrowHeight;   //????????????????????????
    private int mSignArrowWidth;   //????????????????????????
    private int mSignRound;      //????????????????????????
    private int barRoundingRadius = 0;
    private Point point1;
    private Point point2;
    private Point point3;
    private Paint signPaint;
    private Paint signborderPaint;
    private StaticLayout valueTextLayout;
    private Path trianglePath;
    private Path triangleboderPath;
    private String unit;
    private boolean mReverse;
    private TextPaint valueTextPaint;  //??????????????????
    private NumberFormat mFormat;
    private OnValueFormatListener mValueFormatListener;

    public SignSeekBar(Context context) {
        this(context, null);
    }

    public SignSeekBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SignSeekBar, defStyleAttr, 0);
        mMin = a.getFloat(R.styleable.SignSeekBar_ssb_min, 0.0f);
        mMax = a.getFloat(R.styleable.SignSeekBar_ssb_max, 100.0f);
        mProgress = a.getFloat(R.styleable.SignSeekBar_ssb_progress, mMin);
        isFloatType = a.getBoolean(R.styleable.SignSeekBar_ssb_is_float_type, false);
        mTrackSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_track_size, SignUtils.dp2px(2));
        mTextSpace = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_text_space, SignUtils.dp2px(2));
        mSecondTrackSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_second_track_size, mTrackSize + SignUtils.dp2px(2));
        mThumbRadius = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_thumb_radius, mSecondTrackSize + SignUtils.dp2px(2));
        mThumbRadiusOnDragging = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_thumb_radius, mSecondTrackSize * 2);
        mSignBorderSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_border_size, SignUtils.dp2px(1));
        mSectionCount = a.getInteger(R.styleable.SignSeekBar_ssb_section_count, 10);
        mTrackColor = a.getColor(R.styleable.SignSeekBar_ssb_track_color, ContextCompat.getColor(context, R.color.colorPrimary));
        mSecondTrackColor = a.getColor(R.styleable.SignSeekBar_ssb_second_track_color, ContextCompat.getColor(context, R.color.colorAccent));
        mThumbColor = a.getColor(R.styleable.SignSeekBar_ssb_thumb_color, mSecondTrackColor);
        isShowSectionText = a.getBoolean(R.styleable.SignSeekBar_ssb_show_section_text, false);
        mSectionTextSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_section_text_size, SignUtils.sp2px(14));
        mSectionTextColor = a.getColor(R.styleable.SignSeekBar_ssb_section_text_color, mTrackColor);
        isSeekBySection = a.getBoolean(R.styleable.SignSeekBar_ssb_seek_by_section, false);
        int pos = a.getInteger(R.styleable.SignSeekBar_ssb_section_text_position, NONE);
        if (pos == 0) {
            mSectionTextPosition = TextPosition.SIDES;
        } else if (pos == 1) {
            mSectionTextPosition = TextPosition.BOTTOM_SIDES;
        } else if (pos == 2) {
            mSectionTextPosition = TextPosition.BELOW_SECTION_MARK;
        } else {
            mSectionTextPosition = NONE;
        }
        mSectionTextInterval = a.getInteger(R.styleable.SignSeekBar_ssb_section_text_interval, 1);
        isShowThumbText = a.getBoolean(R.styleable.SignSeekBar_ssb_show_thumb_text, false);
        mThumbTextSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_thumb_text_size, SignUtils.sp2px(14));
        mThumbTextColor = a.getColor(R.styleable.SignSeekBar_ssb_thumb_text_color, mSecondTrackColor);
        mSignColor = a.getColor(R.styleable.SignSeekBar_ssb_sign_color, mSecondTrackColor);
        mSignBorderColor = a.getColor(R.styleable.SignSeekBar_ssb_sign_border_color, mSecondTrackColor);
        mUnusableColor = a.getColor(R.styleable.SignSeekBar_ssb_unusable_color, getResources().getColor(R.color.color_c8c8c8));
        mSignTextSize = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_text_size, SignUtils.sp2px(14));
        mSignHeight = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_height, SignUtils.dp2px(32));
        mSignWidth = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_width, SignUtils.dp2px(72));
        mSignArrowHeight = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_arrow_height, SignUtils.dp2px(3));
        mSignArrowWidth = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_arrow_width, SignUtils.dp2px(5));
        mSignRound = a.getDimensionPixelSize(R.styleable.SignSeekBar_ssb_sign_round, SignUtils.dp2px(3));
        mSignTextColor = a.getColor(R.styleable.SignSeekBar_ssb_sign_text_color, Color.WHITE);
        isShowSectionMark = a.getBoolean(R.styleable.SignSeekBar_ssb_show_section_mark, false);
        isAutoAdjustSectionMark = a.getBoolean(R.styleable.SignSeekBar_ssb_auto_adjust_section_mark, false);
        isShowProgressInFloat = a.getBoolean(R.styleable.SignSeekBar_ssb_show_progress_in_float, false);
        int duration = a.getInteger(R.styleable.SignSeekBar_ssb_anim_duration, -1);
        mAnimDuration = duration < 0 ? 200 : duration;
        isTouchToSeek = a.getBoolean(R.styleable.SignSeekBar_ssb_touch_to_seek, false);
        isShowSignBorder = a.getBoolean(R.styleable.SignSeekBar_ssb_sign_show_border, false);

        int labelsResId = a.getResourceId(R.styleable.SignSeekBar_ssb_sides_labels, 0);
        mThumbBgAlpha = a.getFloat(R.styleable.SignSeekBar_ssb_thumb_bg_alpha, 0.2f);
        mThumbRatio = a.getFloat(R.styleable.SignSeekBar_ssb_thumb_ratio, 0.8f);
        isShowThumbShadow = a.getBoolean(R.styleable.SignSeekBar_ssb_show_thumb_shadow, false);
        isShowSign = a.getBoolean(R.styleable.SignSeekBar_ssb_show_sign, false);
        isSignArrowAutofloat = a.getBoolean(R.styleable.SignSeekBar_ssb_sign_arrow_autofloat, true);
        a.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextAlign(Paint.Align.CENTER);

        mRectText = new Rect();
        if (labelsResId > 0) {
            mSidesLabels = getResources().getStringArray(labelsResId);
        }
        isSidesLabels = mSidesLabels != null && mSidesLabels.length > 0;

        //init sign
        roundRectangleBounds = new RectF();
        valueSignBounds = new Rect();

        point1 = new Point();
        point2 = new Point();
        point3 = new Point();

        trianglePath = new Path();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);

        triangleboderPath = new Path();

        init();

        initConfigByPriority();
    }

    private void init() {
        signPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        signPaint.setStyle(Paint.Style.FILL);
        signPaint.setAntiAlias(true);
        signPaint.setColor(mSignColor);

        signborderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        signborderPaint.setStyle(Paint.Style.STROKE);
        signborderPaint.setStrokeWidth(mSignBorderSize);
        signborderPaint.setColor(mSignBorderColor);
        signborderPaint.setAntiAlias(true);

        valueTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        valueTextPaint.setStyle(Paint.Style.FILL);
        valueTextPaint.setTextSize(mSignTextSize);
        valueTextPaint.setColor(mSignTextColor);
    }

    private void initConfigByPriority() {
        if (mMin == mMax) {
            mMin = 0.0f;
            mMax = 100.0f;
        }
        if (mMin > mMax) {
            float tmp = mMax;
            mMax = mMin;
            mMin = tmp;
        }
        if (mProgress < mMin) {
            mProgress = mMin;
        }
        if (mProgress > mMax) {
            mProgress = mMax;
        }
        if (mSecondTrackSize < mTrackSize) {
            mSecondTrackSize = mTrackSize + SignUtils.dp2px(2);
        }
        if (mThumbRadius <= mSecondTrackSize) {
            mThumbRadius = mSecondTrackSize + SignUtils.dp2px(2);
        }
        if (mThumbRadiusOnDragging <= mSecondTrackSize) {
            mThumbRadiusOnDragging = mSecondTrackSize * 2;
        }
        if (mSectionCount <= 0) {
            mSectionCount = 10;
        }
        mDelta = mMax - mMin;
        mSectionValue = mDelta / mSectionCount;

        if (mSectionValue < 1) {
            isFloatType = true;
        }
        if (isFloatType) {
            isShowProgressInFloat = true;
        }
        if (mSectionTextPosition != NONE) {
            isShowSectionText = true;
        }
        if (isShowSectionText) {
            if (mSectionTextPosition == NONE) {
                mSectionTextPosition = TextPosition.SIDES;
            }
            if (mSectionTextPosition == TextPosition.BELOW_SECTION_MARK) {
                isShowSectionMark = true;
            }
        }
        if (mSectionTextInterval < 1) {
            mSectionTextInterval = 1;
        }
        if (isAutoAdjustSectionMark && !isShowSectionMark) {
            isAutoAdjustSectionMark = false;
        }
        if (isSeekBySection) {
            mPreSecValue = mMin;
            if (mProgress != mMin) {
                mPreSecValue = mSectionValue;
            }
            isShowSectionMark = true;
            isAutoAdjustSectionMark = true;
            isTouchToSeek = false;
        }

        setProgress(mProgress);

        mThumbTextSize = isFloatType || isSeekBySection || (isShowSectionText && mSectionTextPosition ==
                TextPosition.BELOW_SECTION_MARK) ? mSectionTextSize : mThumbTextSize;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = mThumbRadiusOnDragging * 2; // ????????????????????????thumb????????????
        if (isShowThumbText) {
            mPaint.setTextSize(mThumbTextSize);
            mPaint.getTextBounds("j", 0, 1, mRectText); // ???j??????????????????????????????????????????
            height += mRectText.height() + mTextSpace; // ??????????????????????????????????????????????????????????????????????????????
        }
        if (isShowSectionText && mSectionTextPosition >= TextPosition.BOTTOM_SIDES) { // ??????Section??????track?????????????????????????????????
            //???????????????????????????????????????lable??????????????????lable???????????????????????????????????????????????????????????????j?????????????????????
            String measuretext = isSidesLabels ? mSidesLabels[0] : "j";
            mPaint.setTextSize(mSectionTextSize);
            mPaint.getTextBounds(measuretext, 0, measuretext.length(), mRectText);
            height = Math.max(height, mThumbRadiusOnDragging * 2 + mRectText.height() + mTextSpace);
        }

        height += mSignHeight;//????????????????????????
        if (isShowSignBorder) height += mSignBorderSize;//???????????????????????????
        setMeasuredDimension(resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec), height);

        mLeft = getPaddingLeft() + mThumbRadiusOnDragging;
        mRight = getMeasuredWidth() - getPaddingRight() - mThumbRadiusOnDragging;

        if (isShowSectionText) {
            mPaint.setTextSize(mSectionTextSize);

            if (mSectionTextPosition == TextPosition.SIDES) {
                String text = getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                mLeft += (mRectText.width() + mTextSpace);

                text = getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                mRight -= (mRectText.width() + mTextSpace);
            } else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                String text = isSidesLabels ? mSidesLabels[0] : getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                float max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
                mLeft = getPaddingLeft() + max + mTextSpace;

                text = isSidesLabels ? mSidesLabels[mSidesLabels.length - 1] : getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
                mRight = getMeasuredWidth() - getPaddingRight() - max - mTextSpace;
            }
        } else if (isShowThumbText && mSectionTextPosition == NONE) {
            mPaint.setTextSize(mThumbTextSize);

            String text = getMinText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            float max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mLeft = getPaddingLeft() + max + mTextSpace;

            text = getMaxText();
            mPaint.getTextBounds(text, 0, text.length(), mRectText);
            max = Math.max(mThumbRadiusOnDragging, mRectText.width() / 2f);
            mRight = getMeasuredWidth() - getPaddingRight() - max - mTextSpace;
        }

        if (isShowSign && !isSignArrowAutofloat) {//????????? ??????????????????????????????
            mLeft = Math.max(mLeft, getPaddingLeft() + mSignWidth / 2 + mSignBorderSize);
            mRight = Math.min(mRight, getMeasuredWidth() - getPaddingRight() - mSignWidth / 2 - mSignBorderSize);
        }

        mTrackLength = mRight - mLeft;
        mSectionOffset = mTrackLength * 1f / mSectionCount;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float xLeft = getPaddingLeft();
        float xRight = getMeasuredWidth() - getPaddingRight();
        float yTop = getPaddingTop() + mThumbRadiusOnDragging;
        if (isShowSign) {//?????????????????????
            yTop += mSignHeight;
        }
        if (isShowSignBorder) {//???????????????????????????
            yTop += mSignBorderSize;
        }
        if (isShowSign && !isSignArrowAutofloat) {//???????????????????????????????????????????????????????????????????????????
            xLeft += (mSignWidth / 2 + mSignBorderSize);
            xRight -= (mSignWidth / 2 + mSignBorderSize);
        }
        // draw sectionText SIDES or BOTTOM_SIDES
        if (isShowSectionText) {
            mPaint.setTextSize(mSectionTextSize);
            mPaint.setColor(isEnabled() ? mSectionTextColor : mUnusableColor);

            if (mSectionTextPosition == TextPosition.SIDES) {
                float y_ = yTop + mRectText.height() / 2f;

                //String text = getMinText();
                String text = isSidesLabels ? mSidesLabels[0] : getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                canvas.drawText(text, xLeft + mRectText.width() / 2f, y_, mPaint);
                xLeft += mRectText.width() + mTextSpace;

                //text = getMaxText();
                text = isSidesLabels && mSidesLabels.length > 1 ? mSidesLabels[mSidesLabels.length - 1] : getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                canvas.drawText(text, xRight - mRectText.width() / 2f, y_, mPaint);
                xRight -= (mRectText.width() + mTextSpace);

            } else if (mSectionTextPosition >= TextPosition.BOTTOM_SIDES) {
                float y_ = yTop + mThumbRadiusOnDragging + mTextSpace;

                // String text = getMinText();
                String text = isSidesLabels ? mSidesLabels[0] : getMinText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                y_ += mRectText.height();
                xLeft = mLeft;
                if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xLeft, y_, mPaint);
                }

                //text = getMaxText();
                text = isSidesLabels && mSidesLabels.length > 1 ? mSidesLabels[mSidesLabels.length - 1] : getMaxText();
                mPaint.getTextBounds(text, 0, text.length(), mRectText);
                xRight = mRight;
                if (mSectionTextPosition == TextPosition.BOTTOM_SIDES) {
                    canvas.drawText(text, xRight, y_, mPaint);
                }
            }
        } else if (isShowThumbText && mSectionTextPosition == NONE) {
            xLeft = mLeft;
            xRight = mRight;
        }

        if ((!isShowSectionText && !isShowThumbText) || mSectionTextPosition == TextPosition.SIDES) {
            xLeft += mThumbRadiusOnDragging;
            xRight -= mThumbRadiusOnDragging;
        }

        boolean isShowTextBelowSectionMark = isShowSectionText && mSectionTextPosition ==
                TextPosition.BELOW_SECTION_MARK;
        //boolean conditionInterval = mSectionCount % 2 == 0;
        boolean conditionInterval = true;

        // draw sectionMark & sectionText BELOW_SECTION_MARK
        if (isShowTextBelowSectionMark || isShowSectionMark) {
            drawMark(canvas, xLeft, yTop, isShowTextBelowSectionMark, conditionInterval);
        }

        if (!isThumbOnDragging) {
            mThumbCenterX = mTrackLength / mDelta * (mProgress - mMin) + xLeft;
        }

        // draw thumbText
        if (isShowThumbText && !isThumbOnDragging && isTouchToSeekAnimEnd) {
            drawThumbText(canvas, yTop);
        }

        // draw track
        mPaint.setColor(mSecondTrackColor);
        mPaint.setStrokeWidth(mSecondTrackSize);
        canvas.drawLine(xLeft, yTop, mThumbCenterX, yTop, mPaint);

        // draw second track
        mPaint.setColor(mTrackColor);
        mPaint.setStrokeWidth(mTrackSize);
        canvas.drawLine(mThumbCenterX, yTop, xRight, yTop, mPaint);

        // draw thumb
        mPaint.setColor(mThumbColor);
        //draw thumb shadow
        if (isShowThumbShadow) {
            // mThumbRadiusOnDragging * mThumbRatio ????????????????????????
            // mThumbRadius * mThumbRatio
//            float shadowR = isThumbOnDragging ? mThumbRadiusOnDragging * mThumbRatio : mThumbRadius * mThumbRatio;
            float shadowR = mThumbRadius * mThumbRatio;
            canvas.drawCircle(mThumbCenterX, yTop, shadowR, mPaint);
            mPaint.setColor(getColorWithAlpha(mThumbColor, mThumbBgAlpha));
        }
        //Paint paint = new Paint();
        //Shader shader = new RadialGradient(mThumbCenterX, yTop, isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius, mThumbColor, getColorWithAlpha(mThumbColor, mThumbBgAlpha), Shader.TileMode.CLAMP);
        //paint.setShader(shader);

        // ?????????????????????
//        float r2 = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        float r2 = mThumbRadius;
        canvas.drawCircle(mThumbCenterX, yTop, r2, mPaint);
        mPaint.setColor(getResources().getColor(R.color.color_ff7a45));
        canvas.drawCircle(mThumbCenterX, yTop, 10.0f, mPaint);

        //draw progress text ????????????????????????????????????????????????
        //drawProgressText(canvas);

        // start end word
        String sText = mStartText;
        String eText = mEndText;
        float startX = mTrackLength / mDelta * (0 - mMin) + xLeft;
        float endX = xRight;
        drawStarEndText(canvas, startX, endX, getPaddingTop() + mThumbRadiusOnDragging, sText, eText);

        //draw sign
        if (!isShowSign) return;
        drawValueSign(canvas, mSignHeight, (int) mThumbCenterX);
    }

    private void drawStarEndText(Canvas canvas, float sx, float ex, float y, String stext, String etext) {
        if (TextUtils.isEmpty(stext) || TextUtils.isEmpty(etext)) {
            return;
        }
        Paint paint = new Paint();
        paint.setColor(getResources().getColor(R.color.color_969696));
        paint.setTextSize(24);
        paint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        paint.getTextBounds("???", 0, 1, bounds);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float radius = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        float baseline = y - radius + (2 * radius - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        float sxc = sx - radius + radius - bounds.width() / 2;
        float exc = ex - radius + radius - bounds.width() / 2;
        canvas.drawText(stext, sxc, baseline, paint);
        canvas.drawText(etext, exc, baseline, paint);
    }

    //draw mark
    private void drawMark(Canvas canvas, float xLeft, float yTop, boolean isShowTextBelowSectionMark, boolean conditionInterval) {
        // track circle radio
        float r = (mThumbRadiusOnDragging - SignUtils.dp2px(4)) / 2f;

        float junction = mTrackLength / mDelta * Math.abs(mProgress - mMin) + mLeft; // ?????????
        mPaint.setTextSize(mSectionTextSize);
        mPaint.getTextBounds("j", 0, 1, mRectText); // compute solid height

        float x_;
        float y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;

        for (int i = 0; i <= mSectionCount; i++) {
            x_ = xLeft + i * mSectionOffset;
            mPaint.setColor(x_ <= junction ? mSecondTrackColor : mTrackColor);
            // sectionMark
            canvas.drawCircle(x_, yTop, r, mPaint);

            // sectionText belows section
            if (isShowTextBelowSectionMark) {
                float m = mMin + mSectionValue * i;

                // ????????? ????????????????????????
//                mPaint.setColor(isEnabled() ? mSectionTextColor : Math.abs(mProgress - m) <= 0 ? mSectionTextColor : mUnusableColor);
                int color1 = getResources().getColor(R.color.black);
                int color2 = getResources().getColor(R.color.color_c8c8c8);
                mPaint.setColor(color2);
                if (mSectionTextInterval > 1) {
                    if (conditionInterval && i % mSectionTextInterval == 0) {
                        if (isSidesLabels) {
                            canvas.drawText(mSidesLabels[i], x_, y_, mPaint);
                        } else {
                            canvas.drawText(isFloatType ? float2String(m) : (int) m + "", x_, y_, mPaint);
                        }
                    }
                } else {
                    if (conditionInterval && i % mSectionTextInterval == 0) {
                        if (isSidesLabels && i / mSectionTextInterval <= mSidesLabels.length) {
                            canvas.drawText(mSidesLabels[i / mSectionTextInterval], x_, y_, mPaint);
                        } else {
                            canvas.drawText(isFloatType ? float2String(m) : (int) m + "", x_, y_, mPaint);
                        }
                    }
                }
            }
        }
    }

    //draw thumb text
    private void drawThumbText(Canvas canvas, float yTop) {
        mPaint.setColor(mThumbTextColor);
        mPaint.setTextSize(mThumbTextSize);
        mPaint.getTextBounds("j", 0, 1, mRectText); // compute solid height
        float y_ = yTop + mRectText.height() + mThumbRadiusOnDragging + mTextSpace;

        if (isFloatType || (isShowProgressInFloat && mSectionTextPosition == TextPosition.BOTTOM_SIDES &&
                mProgress != mMin && mProgress != mMax)) {
            float progress = getProgressFloat();
            String value = String.valueOf(progress);
            if (mFormat != null) {
                value = mFormat.format(progress);
            }
            if (value != null && unit != null && !unit.isEmpty()) {
                if (!mReverse) {
                    value += String.format("%s", unit);
                } else {
                    value = String.format("%s", unit) + value;
                }
            }
            if (mValueFormatListener != null) value = mValueFormatListener.format(progress);
            drawSignText(canvas, value, mThumbCenterX, y_, mPaint);
        } else {
            int progress = getProgress();
            String value = String.valueOf(progress);
            if (mFormat != null) {
                value = mFormat.format(progress);
            }
            if (value != null && unit != null && !unit.isEmpty()) {
                if (!mReverse) {
                    value += String.format("%s", unit);
                } else {
                    value = String.format("%s", unit) + value;
                }
            }
            if (mValueFormatListener != null) value = mValueFormatListener.format(progress);
//            drawSignText(canvas, value, mThumbCenterX, y_, mPaint);

            // ????????????????????????????????? ??????????????????mSidesLabels ????????????????????????????????????????????????
            String text = mSidesLabels[progress];
            drawSignText(canvas, text, mThumbCenterX, y_, mPaint);
        }
    }

    public void drawSignText(Canvas canvas, String text, float x, float y, Paint paint) {
        paint.setColor(getResources().getColor(R.color.black));
        canvas.drawText(text, x, y, paint);
    }

    //draw value sign
    private void drawValueSign(Canvas canvas, int valueSignSpaceHeight, int valueSignCenter) {
        valueSignBounds.set(valueSignCenter - mSignWidth / 2, getPaddingTop(), valueSignCenter + mSignWidth / 2, mSignHeight - mSignArrowHeight + getPaddingTop());

        int bordersize = isShowSignBorder ? mSignBorderSize : 0;
        // Move if not fit horizontal
        if (valueSignBounds.left < getPaddingLeft()) {
            int difference = -valueSignBounds.left + getPaddingLeft() + bordersize;
            roundRectangleBounds.set(valueSignBounds.left + difference, valueSignBounds.top, valueSignBounds.right +
                    difference, valueSignBounds.bottom);
        } else if (valueSignBounds.right > getMeasuredWidth() - getPaddingRight()) {
            int difference = valueSignBounds.right - getMeasuredWidth() + getPaddingRight() + bordersize;
            roundRectangleBounds.set(valueSignBounds.left - difference, valueSignBounds.top, valueSignBounds.right -
                    difference, valueSignBounds.bottom);
        } else {
            roundRectangleBounds.set(valueSignBounds.left, valueSignBounds.top, valueSignBounds.right,
                    valueSignBounds.bottom);
        }

        canvas.drawRoundRect(roundRectangleBounds, mSignRound, mSignRound, signPaint);
        if (isShowSignBorder) {
            roundRectangleBounds.top = roundRectangleBounds.top + mSignBorderSize / 2;
            canvas.drawRoundRect(roundRectangleBounds, mSignRound, mSignRound, signborderPaint);
        }

        // Draw arrow
        barRoundingRadius = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        int difference = 0;
        if (valueSignCenter - mSignArrowWidth / 2 < barRoundingRadius + getPaddingLeft() + mTextSpace + bordersize) {
            difference = barRoundingRadius - valueSignCenter + getPaddingLeft() + bordersize + mTextSpace;
        } else if (valueSignCenter + mSignArrowWidth / 2 > getMeasuredWidth() - barRoundingRadius - getPaddingRight() - mTextSpace - bordersize) {
            difference = (getMeasuredWidth() - barRoundingRadius) - valueSignCenter - getPaddingRight() - bordersize - mTextSpace;
        }

        point1.set(valueSignCenter - mSignArrowWidth / 2 + difference, valueSignSpaceHeight - mSignArrowHeight + getPaddingTop());
        point2.set(valueSignCenter + mSignArrowWidth / 2 + difference, valueSignSpaceHeight - mSignArrowHeight + getPaddingTop());
        point3.set(valueSignCenter + difference, valueSignSpaceHeight + getPaddingTop());

        drawTriangle(canvas, point1, point2, point3, signPaint);
        if (isShowSignBorder) {
            drawTriangleBoder(canvas, point1, point2, point3, signborderPaint);
        }

        createValueTextLayout();
        // Draw value text
        if (valueTextLayout != null) {
            canvas.translate(roundRectangleBounds.left, roundRectangleBounds.top + roundRectangleBounds.height() / 2 - valueTextLayout.getHeight() / 2);
            valueTextLayout.draw(canvas);
        }
    }

    private void drawTriangle(Canvas canvas, Point point1, Point point2, Point point3, Paint paint) {
        trianglePath.reset();
        trianglePath.moveTo(point1.x, point1.y);
        trianglePath.lineTo(point2.x, point2.y);
        trianglePath.lineTo(point3.x, point3.y);
        trianglePath.lineTo(point1.x, point1.y);
        trianglePath.close();

        canvas.drawPath(trianglePath, paint);
    }

    /**
     * ????????????????????????????????????????????????
     */
    private void drawTriangleBoder(Canvas canvas, Point point1, Point point2, Point point3, Paint paint) {
        triangleboderPath.reset();
        triangleboderPath.moveTo(point1.x, point1.y);
        triangleboderPath.lineTo(point2.x, point2.y);
        paint.setColor(signPaint.getColor());
        float value = mSignBorderSize / 6;
        paint.setStrokeWidth(mSignBorderSize + 1f);
        canvas.drawPath(triangleboderPath, paint);
        triangleboderPath.reset();
        paint.setStrokeWidth(mSignBorderSize);
        triangleboderPath.moveTo(point1.x - value, point1.y - value);
        triangleboderPath.lineTo(point3.x, point3.y);
        triangleboderPath.lineTo(point2.x + value, point2.y - value);
        paint.setColor(mSignBorderColor);
        canvas.drawPath(triangleboderPath, paint);
    }

    /**
     * ????????????
     */
    public void setUnit(String unit) {
        this.unit = unit;
        createValueTextLayout();
        invalidate();
        requestLayout();
    }

    public void setProgressWithUnit(float progress, String unitHtml) {
        setProgress(progress);
        this.unit = unitHtml;
        createValueTextLayout();
        invalidate();
        requestLayout();
    }

    private void createValueTextLayout() {
        String value = "";
        if (isShowProgressInFloat) {
            float progress = getProgressFloat();
            value = String.valueOf(progress);
            if (mFormat != null) {
                value = mFormat.format(progress);
            }
        } else {
            int progress = getProgress();
            value = String.valueOf(progress);
            if (mFormat != null) {
                value = mFormat.format(progress);
            }
        }
        if (mValueFormatListener == null) {
            if (value != null && unit != null && !unit.isEmpty()) {
                if (!mReverse) {
                    value += String.format(" <small>%s</small> ", unit);
                    //value += String.format("%s", unit);
                } else {
                    value = String.format(" %s ", unit) + value;
                }
            }
        } else {
            value = mValueFormatListener.format(Float.parseFloat(value));
        }
        Spanned spanned = Html.fromHtml(value);
        valueTextLayout = new StaticLayout(spanned, valueTextPaint, mSignWidth, Layout.Alignment.ALIGN_CENTER, 1, 0, false);
    }

    //draw progress text
    private void drawProgressText(Canvas canvas) {
        String value = isShowProgressInFloat ? String.valueOf(getProgressFloat()) : String.valueOf(getProgress());
        //String text = value != null ? formatter.format(value) : valueSegmentText;
        if (value != null && unit != null && !unit.isEmpty())
            value += String.format("%s", unit);
        float mCircle_r = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        Paint mPartTextPaint = mPaint;
        mPartTextPaint.setColor(Color.BLACK);
        mPartTextPaint.setTextSize(25);
        //???????????????????????????????????????????????????????????????Paint.Align.LEFT????????????????????????
        drawCircleText(canvas, mPartTextPaint, mThumbCenterX, getPaddingTop() + mThumbRadiusOnDragging, mCircle_r, value);
    }

    /**
     * ????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     *
     * @param canvas  ??????
     * @param paint   ??????panit
     * @param centerX ????????????X??????
     * @param centerY ????????????Y??????
     * @param radius  ??????
     * @param text    ???????????????
     */
    private void drawCircleText(Canvas canvas, Paint paint, float centerX, float centerY, float radius, String text) {
        paint.setTextAlign(Paint.Align.LEFT);
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        float baseline = centerY - radius + (2 * radius - fontMetrics.bottom + fontMetrics.top) / 2 - fontMetrics.top;
        canvas.drawText(text, centerX - radius + radius - bounds.width() / 2, baseline, paint);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        post(new Runnable() {
            @Override
            public void run() {
                requestLayout();
            }
        });
    }

    float dx;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) return false;
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                getParent().requestDisallowInterceptTouchEvent(true);

                isThumbOnDragging = isThumbTouched(event);
                if (isThumbOnDragging) {
                    if (isSeekBySection && !triggerSeekBySection) {
                        triggerSeekBySection = true;
                    }
                    invalidate();
                } else if (isTouchToSeek && isTrackTouched(event)) {
                    isThumbOnDragging = true;
                    mThumbCenterX = event.getX();
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    invalidate();
                }

                dx = mThumbCenterX - event.getX();

                break;
            case MotionEvent.ACTION_MOVE:
                if (isThumbOnDragging) {
                    mThumbCenterX = event.getX() + dx;
                    if (mThumbCenterX < mLeft) {
                        mThumbCenterX = mLeft;
                    }
                    if (mThumbCenterX > mRight) {
                        mThumbCenterX = mRight;
                    }
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat(), true);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);

                if (isAutoAdjustSectionMark) {
                    if (isTouchToSeek) {
                        postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                isTouchToSeekAnimEnd = false;
                                autoAdjustSection();
                            }
                        }, isThumbOnDragging ? 0 : 300);
                    } else {
                        autoAdjustSection();
                    }
                } else if (isThumbOnDragging || isTouchToSeek) {
                    animate()
                            .setDuration(mAnimDuration)
                            .setStartDelay(!isThumbOnDragging && isTouchToSeek ? 300 : 0)
                            .setListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();

                                    if (mProgressListener != null) {
                                        mProgressListener.onProgressChanged(SignSeekBar.this,
                                                getProgress(), getProgressFloat(), true);
                                    }
                                }

                                @Override
                                public void onAnimationCancel(Animator animation) {
                                    isThumbOnDragging = false;
                                    invalidate();
                                }
                            })
                            .start();
                }

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnActionUp(this, getProgress(), getProgressFloat());
                }

                break;
        }

        return isThumbOnDragging || isTouchToSeek || super.onTouchEvent(event);
    }

    /**
     * ???????????????????????????
     *
     * @param color ?????????
     * @param ratio ???????????????
     */
    public int getColorWithAlpha(int color, float ratio) {
        int newColor = 0;
        int alpha = Math.round(Color.alpha(color) * ratio);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        newColor = Color.argb(alpha, r, g, b);
        return newColor;
    }

    /**
     * Detect effective touch of thumb
     */
    private boolean isThumbTouched(MotionEvent event) {
        if (!isEnabled())
            return false;
        float mCircleR = isThumbOnDragging ? mThumbRadiusOnDragging : mThumbRadius;
        float x = mTrackLength / mDelta * (mProgress - mMin) + mLeft;
        float y = getMeasuredHeight() / 2f;
        return (event.getX() - x) * (event.getX() - x) + (event.getY() - y) * (event.getY() - y)
                <= (mLeft + mCircleR) * (mLeft + mCircleR);
    }

    /**
     * Detect effective touch of track
     */
    private boolean isTrackTouched(MotionEvent event) {
        return isEnabled() && event.getX() >= getPaddingLeft() && event.getX() <= getMeasuredWidth() - getPaddingRight()
                && event.getY() >= getPaddingTop() && event.getY() <= getMeasuredHeight() - getPaddingBottom();
    }

    /**
     * Auto scroll to the nearest section mark
     */
    private void autoAdjustSection() {
        int i;
        //???????????????????????????mSectionCount??????????????????mSectionOffset??????????????????????????????mThumbCenterX????????????????????????
        float x = 0;
        for (i = 0; i <= mSectionCount; i++) {
            x = i * mSectionOffset + mLeft;
            if (x <= mThumbCenterX && mThumbCenterX - x <= mSectionOffset) {
                break;
            }
        }

        BigDecimal bigDecimal = BigDecimal.valueOf(mThumbCenterX);
        //BigDecimal setScale??????1???????????????????????????2.35??????2.4
        float x_ = bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
        boolean onSection = x_ == x; // ??????section????????????valueAnim???????????????

        AnimatorSet animatorSet = new AnimatorSet();
        ValueAnimator valueAnim = null;
        if (!onSection) {
            if (mThumbCenterX - x <= mSectionOffset / 2f) {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, x);
            } else {
                valueAnim = ValueAnimator.ofFloat(mThumbCenterX, (i + 1) * mSectionOffset + mLeft);
            }
            valueAnim.setInterpolator(new LinearInterpolator());
            valueAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    mThumbCenterX = (float) animation.getAnimatedValue();
                    mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                    invalidate();

                    if (mProgressListener != null) {
                        mProgressListener.onProgressChanged(SignSeekBar.this, getProgress(), getProgressFloat(), true);
                    }
                }
            });
        }
        if (!onSection) {
            animatorSet.setDuration(mAnimDuration).playTogether(valueAnim);
        }
        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();

                if (mProgressListener != null) {
                    mProgressListener.getProgressOnFinally(SignSeekBar.this, getProgress(), getProgressFloat(), true);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                mProgress = (mThumbCenterX - mLeft) * mDelta / mTrackLength + mMin;
                isThumbOnDragging = false;
                isTouchToSeekAnimEnd = true;
                invalidate();
            }
        });
        animatorSet.start();
    }

    private String getMinText() {
        return isFloatType ? float2String(mMin) : String.valueOf((int) mMin);
    }

    private String getMaxText() {
        return isFloatType ? float2String(mMax) : String.valueOf((int) mMax);
    }

    public float getMin() {
        return mMin;
    }

    public float getMax() {
        return mMax;
    }

    public void setProgress(float progress) {
        mProgress = progress;
        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat(), false);
            mProgressListener.getProgressOnFinally(this, getProgress(), getProgressFloat(), false);
        }
        postInvalidate();
    }

    public int getProgress() {
        if (isSeekBySection && triggerSeekBySection) {
            float half = mSectionValue / 2;

            if (mProgress >= mPreSecValue) { // increasing
                if (mProgress >= mPreSecValue + half) {
                    mPreSecValue += mSectionValue;
                    return Math.round(mPreSecValue);
                } else {
                    return Math.round(mPreSecValue);
                }
            } else { // reducing
                if (mProgress >= mPreSecValue - half) {
                    return Math.round(mPreSecValue);
                } else {
                    mPreSecValue -= mSectionValue;
                    return Math.round(mPreSecValue);
                }
            }
        }

        return Math.round(mProgress);
    }

    public float getProgressFloat() {
        return formatFloat(mProgress);
    }

    public void setOnProgressChangedListener(OnProgressChangedListener onProgressChangedListener) {
        mProgressListener = onProgressChangedListener;
    }

    public void setValueFormatListener(OnValueFormatListener valueFormatListener) {
        mValueFormatListener = valueFormatListener;
    }

    void config(SignConfigBuilder builder) {
        mMin = builder.min;
        mMax = builder.max;
        mProgress = builder.progress;
        isFloatType = builder.floatType;
        mTrackSize = builder.trackSize;
        mSecondTrackSize = builder.secondTrackSize;
        mThumbRadius = builder.thumbRadius;
        mThumbRadiusOnDragging = builder.thumbRadiusOnDragging;
        mTrackColor = builder.trackColor;
        mSecondTrackColor = builder.secondTrackColor;
        mThumbColor = builder.thumbColor;
        mSectionCount = builder.sectionCount;
        isShowSectionMark = builder.showSectionMark;
        isAutoAdjustSectionMark = builder.autoAdjustSectionMark;
        isShowSectionText = builder.showSectionText;
        mSectionTextSize = builder.sectionTextSize;
        mSectionTextColor = builder.sectionTextColor;
        mSectionTextPosition = builder.sectionTextPosition;
        mSectionTextInterval = builder.sectionTextInterval;
        isShowThumbText = builder.showThumbText;
        mThumbTextSize = builder.thumbTextSize;
        mThumbTextColor = builder.thumbTextColor;
        isShowProgressInFloat = builder.showProgressInFloat;
        mAnimDuration = builder.animDuration;
        isTouchToSeek = builder.touchToSeek;
        isSeekBySection = builder.seekBySection;
        mSidesLabels = mConfigBuilder.bottomSidesLabels;
        isSidesLabels = mSidesLabels != null && mSidesLabels.length > 0;
        mThumbBgAlpha = mConfigBuilder.thumbBgAlpha;
        mThumbRatio = mConfigBuilder.thumbRatio;
        isShowThumbShadow = mConfigBuilder.showThumbShadow;
        unit = mConfigBuilder.unit;
        mReverse = mConfigBuilder.reverse;
        mFormat = mConfigBuilder.format;
        mSignColor = builder.signColor;
        mSignTextSize = builder.signTextSize;
        mSignTextColor = builder.signTextColor;
        isShowSign = builder.showSign;
        mSignArrowWidth = builder.signArrowWidth;
        mSignArrowHeight = builder.signArrowHeight;
        mSignRound = builder.signRound;
        mSignHeight = builder.signHeight;
        mSignWidth = builder.signWidth;
        isShowSignBorder = builder.showSignBorder;
        mSignBorderSize = builder.signBorderSize;
        mSignBorderColor = builder.signBorderColor;
        isSignArrowAutofloat = builder.signArrowAutofloat;
        mStartText = builder.startText;
        mEndText = builder.endText;

        init();
        initConfigByPriority();
        createValueTextLayout();
        if (mProgressListener != null) {
            mProgressListener.onProgressChanged(this, getProgress(), getProgressFloat(), false);
            mProgressListener.getProgressOnFinally(this, getProgress(), getProgressFloat(), false);
        }

        mConfigBuilder = null;

        requestLayout();
    }

    public SignConfigBuilder getConfigBuilder() {
        if (mConfigBuilder == null) {
            mConfigBuilder = new SignConfigBuilder(this);
        }
        mConfigBuilder.min = mMin;
        mConfigBuilder.max = mMax;
        mConfigBuilder.progress = mProgress;
        mConfigBuilder.floatType = isFloatType;
        mConfigBuilder.trackSize = mTrackSize;
        mConfigBuilder.secondTrackSize = mSecondTrackSize;
        mConfigBuilder.thumbRadius = mThumbRadius;
        mConfigBuilder.thumbRadiusOnDragging = mThumbRadiusOnDragging;
        mConfigBuilder.trackColor = mTrackColor;
        mConfigBuilder.secondTrackColor = mSecondTrackColor;
        mConfigBuilder.thumbColor = mThumbColor;
        mConfigBuilder.sectionCount = mSectionCount;
        mConfigBuilder.showSectionMark = isShowSectionMark;
        mConfigBuilder.autoAdjustSectionMark = isAutoAdjustSectionMark;
        mConfigBuilder.showSectionText = isShowSectionText;
        mConfigBuilder.sectionTextSize = mSectionTextSize;
        mConfigBuilder.sectionTextColor = mSectionTextColor;
        mConfigBuilder.sectionTextPosition = mSectionTextPosition;
        mConfigBuilder.sectionTextInterval = mSectionTextInterval;
        mConfigBuilder.showThumbText = isShowThumbText;
        mConfigBuilder.thumbTextSize = mThumbTextSize;
        mConfigBuilder.thumbTextColor = mThumbTextColor;
        mConfigBuilder.showProgressInFloat = isShowProgressInFloat;
        mConfigBuilder.animDuration = mAnimDuration;
        mConfigBuilder.touchToSeek = isTouchToSeek;
        mConfigBuilder.seekBySection = isSeekBySection;
        mConfigBuilder.bottomSidesLabels = mSidesLabels;
        mConfigBuilder.thumbBgAlpha = mThumbBgAlpha;
        mConfigBuilder.thumbRatio = mThumbRatio;
        mConfigBuilder.showThumbShadow = isShowThumbShadow;
        mConfigBuilder.unit = unit;
        mConfigBuilder.reverse = mReverse;
        mConfigBuilder.format = mFormat;
        mConfigBuilder.signColor = mSignColor;
        mConfigBuilder.signTextSize = mSignTextSize;
        mConfigBuilder.signTextColor = mSignTextColor;
        mConfigBuilder.showSign = isShowSign;
        mConfigBuilder.signArrowHeight = mSignArrowHeight;
        mConfigBuilder.signArrowWidth = mSignArrowWidth;
        mConfigBuilder.signRound = mSignRound;
        mConfigBuilder.signHeight = mSignHeight;
        mConfigBuilder.signWidth = mSignWidth;
        mConfigBuilder.showSignBorder = isShowSignBorder;
        mConfigBuilder.signBorderSize = mSignBorderSize;
        mConfigBuilder.signBorderColor = mSignBorderColor;
        mConfigBuilder.signArrowAutofloat = isSignArrowAutofloat;
        mConfigBuilder.startText = mStartText;
        mConfigBuilder.endText = mEndText;

        return mConfigBuilder;
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        bundle.putParcelable("save_instance", super.onSaveInstanceState());
        bundle.putFloat("progress", mProgress);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            mProgress = bundle.getFloat("progress");
            super.onRestoreInstanceState(bundle.getParcelable("save_instance"));
            setProgress(mProgress);
            return;
        }
        super.onRestoreInstanceState(state);
    }

    private String float2String(float value) {
        return String.valueOf(formatFloat(value));
    }

    private float formatFloat(float value) {
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        return bigDecimal.setScale(1, BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * Listen to progress onChanged, onActionUp, onFinally
     */
    public interface OnProgressChangedListener {

        void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser);

        void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat);

        void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser);
    }

    public interface OnValueFormatListener {
        String format(float progress);
    }
}
