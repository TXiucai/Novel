package com.heiheilianzai.app.view.video.view;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.heiheilianzai.app.R;

import java.util.ArrayList;
import java.util.List;

public class FullPlaySpeedSelectDialog extends Dialog implements View.OnClickListener {

    private List<TextView> mTextViewList;
    private float mSpeed;
    private OnFullPlaySpeedSelectListener mListener;

    public FullPlaySpeedSelectDialog(@NonNull Context context, float speed, OnFullPlaySpeedSelectListener listener) {
        super(context);
        this.mListener = listener;
        this.mSpeed = speed;
        mTextViewList = new ArrayList<>();
        init();
    }

    private void init() {
        Window win = this.getWindow();
        win.requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_full_play_speed_select);
        initView();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.windowAnimations = R.style.BottomInAndOutStyle;
        lp.gravity = Gravity.CENTER;
        win.setAttributes(lp);
        win.setBackgroundDrawableResource(android.R.color.transparent);
    }

    private void initView() {
        TextView tv_speed_0_5 = findViewById(R.id.tv_speed_0_5);
        tv_speed_0_5.setTag(0.5f);
        mTextViewList.add(tv_speed_0_5);
        tv_speed_0_5.setOnClickListener(this);

        TextView tv_speed_1 = findViewById(R.id.tv_speed_1);
        tv_speed_1.setTag(1.0f);
        mTextViewList.add(tv_speed_1);
        tv_speed_1.setOnClickListener(this);

        TextView tv_speed_1_5 = findViewById(R.id.tv_speed_1_5);
        tv_speed_1_5.setTag(1.5f);
        mTextViewList.add(tv_speed_1_5);
        tv_speed_1_5.setOnClickListener(this);

        TextView tv_speed_2 = findViewById(R.id.tv_speed_2);
        tv_speed_2.setTag(2.0f);
        mTextViewList.add(tv_speed_2);
        tv_speed_2.setOnClickListener(this);

        switchSelect();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_speed_0_5:
                mSpeed = 0.5f;
                break;
            case R.id.tv_speed_1:
                mSpeed = 1.0f;
                break;
            case R.id.tv_speed_1_5:
                mSpeed = 1.5f;
                break;
            case R.id.tv_speed_2:
                mSpeed = 2.0f;
                break;
        }
        mListener.playSpeed(mSpeed);
        dismiss();
    }

    private void switchSelect() {
        for (int i = 0; i < mTextViewList.size(); i++) {
            Float tag = (Float) mTextViewList.get(i).getTag();
            if (tag == mSpeed) {
                mTextViewList.get(i).setTextColor(getContext().getResources().getColor(R.color.color_FF9900));
            } else {
                mTextViewList.get(i).setTextColor(getContext().getResources().getColor(R.color.white));
            }
        }
    }

    public interface OnFullPlaySpeedSelectListener{
        void playSpeed(float mSpeed);
    }
}
