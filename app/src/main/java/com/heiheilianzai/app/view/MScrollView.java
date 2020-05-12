package com.heiheilianzai.app.view;

import android.content.Context;
import android.support.v4.widget.NestedScrollView;
import android.util.AttributeSet;

public class MScrollView  extends NestedScrollView {
    public MScrollView(Context context) {
        super(context);
    }

    public MScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
    }

    public OnScrollChangedListener onScrollChangedListener;

    public void setonScrollChangedListener(OnScrollChangedListener onScrollChangedListener) {
        this.onScrollChangedListener=onScrollChangedListener;
    }

    public interface OnScrollChangedListener {
        void top();
        void bottom();
    }
}
