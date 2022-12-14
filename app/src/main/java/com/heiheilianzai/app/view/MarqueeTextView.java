package com.heiheilianzai.app.view;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.Announce;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28.
 */
public class MarqueeTextView extends LinearLayout {
    private static final String TAG = MarqueeTextView.class.getSimpleName();
    private Context mContext;
    private ViewFlipper viewFlipper;
    private View marqueeTextView;
    private List<Announce> textArrays;
    private MarqueeTextViewClickListener marqueeTextViewClickListener;
    int mWidth;
    int mHeight;
    private Handler mHandler;
    private boolean isLeftToRight = false;
    private boolean isSelectColor = false;

    public void setSelectColor(boolean selectColor) {
        isSelectColor = selectColor;
    }

    public void setLeftToRight(boolean leftToRight) {
        isLeftToRight = leftToRight;
    }

    public MarqueeTextView(Context context) {
        super(context);
        mContext = context;
        initBasicView();
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initBasicView();
    }

    public void setTextArraysAndClickListener(List<Announce> textArrays, MarqueeTextViewClickListener marqueeTextViewClickListener) {
        this.textArrays = textArrays;
        this.marqueeTextViewClickListener = marqueeTextViewClickListener;
        initMarqueeTextView(textArrays, marqueeTextViewClickListener);
    }

    public void initBasicView() {
        mHandler = new Handler(Looper.getMainLooper());
        marqueeTextView = LayoutInflater.from(mContext).inflate(R.layout.marquee_textview_layout, null);
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        addView(marqueeTextView, layoutParams);
        viewFlipper = marqueeTextView.findViewById(R.id.viewFlipper);
    }

    public void initMarqueeTextView(List<Announce> textArrays, final MarqueeTextViewClickListener marqueeTextViewClickListener) {
        if (!isLeftToRight) {
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_top));
            viewFlipper.setFlipInterval(3000);
        } else {
            viewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_in_left));
            viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.slide_out_right));
            viewFlipper.setFlipInterval(6000);
        }

        viewFlipper.startFlipping();
        if (textArrays.size() == 0) {
            return;
        }
        viewFlipper.removeAllViews();
        checkInit();
    }

    /**
     * ?????????????????????????????????
     */
    public void checkInit() {
        Runnable checkRunnable = new Runnable() {
            @Override
            public void run() {
                if (mHeight > 0) {
                    int i = 0;
                    while (i < textArrays.size()) {
                        final int j = i;
                        TextView textView = new TextView(mContext);
                        textView.setSingleLine(true);
                        textView.setEllipsize(TextUtils.TruncateAt.END);
                        textView.setText(textArrays.get(i).getContent());
                        textView.setTextSize(12);
                        textView.setGravity(Gravity.CENTER_VERTICAL);
                        if (isSelectColor) {
                            textView.setTextColor(mContext.getResources().getColor(R.color.color_ff8350));
                        }
                        textView.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                marqueeTextViewClickListener.onClick(v, j);
                            }
                        });
                        ViewFlipper.LayoutParams lp = new ViewFlipper.LayoutParams(ViewFlipper.LayoutParams.MATCH_PARENT, mHeight);
                        if (viewFlipper != null) {
                            viewFlipper.addView(textView, lp);
                        }
                        i++;
                    }
                    mHandler.removeCallbacks(this);
                } else {
                    mHandler.postDelayed(this, 5);
                }
            }
        };
        // ????????????
        mHandler.post(checkRunnable);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
    }

    public void releaseResources() {
        if (marqueeTextView != null) {
            if (viewFlipper != null) {
                viewFlipper.stopFlipping();
                viewFlipper.removeAllViews();
                viewFlipper = null;
            }
            marqueeTextView = null;
        }
    }
}