package com.heiheilianzai.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

/**
 * Created by scb on 2018/11/20.
 */
public class NoLeftRightViewPager extends ViewPager {
    public NoLeftRightViewPager(Context context) {
        super(context);
    }

    public NoLeftRightViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return false;
    }
}
