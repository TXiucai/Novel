package com.heiheilianzai.app.view.comic;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class DanmuRelativeLayout extends RelativeLayout {
    public DanmuRelativeLayout(Context context) {
        super(context);
    }

    public DanmuRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DanmuRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public DanmuRelativeLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
