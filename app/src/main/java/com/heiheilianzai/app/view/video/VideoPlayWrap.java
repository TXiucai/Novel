package com.heiheilianzai.app.view.video;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import com.heiheilianzai.app.R;

public class VideoPlayWrap extends FrameLayout implements View.OnClickListener {

   private Context mContext;
    public VideoPlayWrap(Context context) {
        this(context, null);
        mContext =context;
    }

    public VideoPlayWrap(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VideoPlayWrap(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
//        View view = LayoutInflater.from(mContext).inflate(R.layout.view_video_wrap, this, false);
//        addView(view);

    }


    @Override
    public void onClick(View v) {

    }
}