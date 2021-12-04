package com.heiheilianzai.app.ui.fragment.book;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.heiheilianzai.app.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadSpeakDialogFragment extends DialogFragment {
    private View mView;
    private RelativeLayout mCancel;
    private View mDecorView;
    private Animation mIntoSlide;
    private Animation mOutSlide;
    private boolean isClick = false;//过滤重复点击
    public DialogCallback diglogCallback;

    @BindView(R.id.rb_yingsu_02)
    public RadioButton rb_yingsu_02;
    @BindView(R.id.rb_yingsu_03)
    public RadioButton rb_yingsu_03;
    @BindView(R.id.rb_yingsu_04)
    public RadioButton rb_yingsu_04;
    @BindView(R.id.rb_yingsu_05)
    public RadioButton rb_yingsu_05;
    @BindView(R.id.rb_yingsu_06)
    public RadioButton rb_yingsu_06;
    @BindView(R.id.rb_yingsu_07)
    public RadioButton rb_yingsu_07;
    @BindView(R.id.rb_yingsu_08)
    public RadioButton rb_yingsu_08;
    @BindView(R.id.rb_yingsu_12)
    public TextView rb_yingsu_12;
    @BindView(R.id.rb_yingsu_13)
    public TextView rb_yingsu_13;
    @BindView(R.id.rb_yingsu_14)
    public TextView rb_yingsu_14;
    @BindView(R.id.rb_yingsu_15)
    public TextView rb_yingsu_15;
    @BindView(R.id.rb_yingsu_16)
    public TextView rb_yingsu_16;
    @BindView(R.id.rb_yingsu_17)
    public TextView rb_yingsu_17;
    @BindView(R.id.rb_yingsu_18)
    public TextView rb_yingsu_18;
    @BindView(R.id.rb_shengdiao_02)
    public RadioButton rb_shengdiao_02;
    @BindView(R.id.rb_shengdiao_03)
    public RadioButton rb_shengdiao_03;
    @BindView(R.id.rb_shengdiao_04)
    public RadioButton rb_shengdiao_04;
    @BindView(R.id.rb_shengdiao_05)
    public RadioButton rb_shengdiao_05;
    @BindView(R.id.rb_shengdiao_06)
    public RadioButton rb_shengdiao_06;
    @BindView(R.id.rb_shengdiao_07)
    public RadioButton rb_shengdiao_07;
    @BindView(R.id.rb_shengdiao_08)
    public RadioButton rb_shengdiao_08;
    @BindView(R.id.rb_yingdiao_12)
    public TextView rb_yingdiao_12;
    @BindView(R.id.rb_yingdiao_13)
    public TextView rb_yingdiao_13;
    @BindView(R.id.rb_yingdiao_14)
    public TextView rb_yingdiao_14;
    @BindView(R.id.rb_yingdiao_15)
    public TextView rb_yingdiao_15;
    @BindView(R.id.rb_yingdiao_16)
    public TextView rb_yingdiao_16;
    @BindView(R.id.rb_yingdiao_17)
    public TextView rb_yingdiao_17;
    @BindView(R.id.rb_yingdiao_18)
    public TextView rb_yingdiao_18;
    @BindView(R.id.rb_yingse_01)
    public RadioButton rb_yingse_01;
    @BindView(R.id.rb_yingse_02)
    public RadioButton rb_yingse_02;
    @BindView(R.id.rb_dingshi_01)
    public RadioButton rb_dingshi_01;
    @BindView(R.id.rb_dingshi_02)
    public RadioButton rb_dingshi_02;
    @BindView(R.id.rb_dingshi_03)
    public RadioButton rb_dingshi_03;
    @BindView(R.id.rb_dingshi_04)
    public RadioButton rb_dingshi_04;

    /**
     * 通讯回调接口
     */
    public interface DialogCallback {
        // 音速
        void readSpeed(int speed);

        // 音调
        void readDiao(int diao);

        // 音色
        void readSe(int se);

        // 定时
        void readTimer(int mins);

    }

    public void setDialogCallback(DialogCallback diglogCallback) {
        this.diglogCallback = diglogCallback;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        super.onActivityCreated(savedInstanceState);
        // 全屏显示设置
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        int height = getDialog().getWindow().getAttributes().height;
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, height);
        // 初始化View注入
        ButterKnife.bind(this.getDialog());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dialog_read, container, false);

        mCancel = mView.findViewById(R.id.btn_cancle);
        //初始化Dialog
        initDialogView();
        return mView;
    }

    /**
     * 根据业务需求，更改弹窗的一些样式
     */
    public void initDialogView() {
        mDecorView = getDialog().getWindow().getDecorView();
        mDecorView.setBackground(new ColorDrawable(Color.TRANSPARENT));

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.gravity = Gravity.BOTTOM | Gravity.FILL_HORIZONTAL;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
//        layoutParams.dimAmount = 0.0f ;
        window.setAttributes(layoutParams);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //弹窗弹入屏幕的动画
        initIntAnimation();
        //初始化监听
        initListener();
        //手指点击弹窗外处理
        touchOutShowDialog();
        //back键处理
        getFocus();

    }

    public void initListener() {
        /**
         * "取消"条目的点击事件
         * */
        mCancel.setOnClickListener(view -> dimissDialog());
        ReadSpeakDialogFragment.this.getDialog().setCanceledOnTouchOutside(true);
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.rb_yingsu_02, R.id.rb_yingsu_03, R.id.rb_yingsu_04, R.id.rb_yingsu_05, R.id.rb_yingsu_06,
            R.id.rb_yingsu_07, R.id.rb_yingsu_08, R.id.rb_shengdiao_02, R.id.rb_shengdiao_03, R.id.rb_shengdiao_04,
            R.id.rb_shengdiao_05, R.id.rb_shengdiao_06, R.id.rb_shengdiao_07, R.id.rb_shengdiao_08, R.id.rb_yingse_01,
            R.id.rb_yingse_02, R.id.rb_dingshi_01, R.id.rb_dingshi_02, R.id.rb_dingshi_03, R.id.rb_dingshi_04})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rb_yingsu_02:
                diglogCallback.readSpeed(50);
                changeSpeedTVColor(rb_yingsu_12);
                break;
            case R.id.rb_yingsu_03:
                diglogCallback.readSpeed(75);
                changeSpeedTVColor(rb_yingsu_13);
                break;
            case R.id.rb_yingsu_04:
                diglogCallback.readSpeed(100);
                changeSpeedTVColor(rb_yingsu_14);
                break;
            case R.id.rb_yingsu_05:
                diglogCallback.readSpeed(125);
                changeSpeedTVColor(rb_yingsu_15);
                break;
            case R.id.rb_yingsu_06:
                diglogCallback.readSpeed(150);
                changeSpeedTVColor(rb_yingsu_16);
                break;
            case R.id.rb_yingsu_07:
                diglogCallback.readSpeed(175);
                changeSpeedTVColor(rb_yingsu_17);
                break;
            case R.id.rb_yingsu_08:
                diglogCallback.readSpeed(200);
                changeSpeedTVColor(rb_yingsu_18);
                break;
            case R.id.rb_shengdiao_02:
                diglogCallback.readDiao(50);
                changeDiaoTVColor(rb_yingdiao_12);
                break;
            case R.id.rb_shengdiao_03:
                diglogCallback.readDiao(75);
                changeDiaoTVColor(rb_yingdiao_13);
                break;
            case R.id.rb_shengdiao_04:
                diglogCallback.readDiao(100);
                changeDiaoTVColor(rb_yingdiao_14);
                break;
            case R.id.rb_shengdiao_05:
                diglogCallback.readDiao(125);
                changeDiaoTVColor(rb_yingdiao_15);
                break;
            case R.id.rb_shengdiao_06:
                diglogCallback.readDiao(150);
                changeDiaoTVColor(rb_yingdiao_16);
                break;
            case R.id.rb_shengdiao_07:
                diglogCallback.readDiao(175);
                changeDiaoTVColor(rb_yingdiao_17);
                break;
            case R.id.rb_shengdiao_08:
                diglogCallback.readDiao(200);
                changeDiaoTVColor(rb_yingdiao_18);
                break;
            case R.id.rb_yingse_01:
                diglogCallback.readSe(1);
                rb_yingse_01.setTextColor(getResources().getColor(R.color.red));
                rb_yingse_02.setTextColor(getResources().getColor(R.color.black));
                break;
            case R.id.rb_yingse_02:
                diglogCallback.readSe(2);
                rb_yingse_01.setTextColor(getResources().getColor(R.color.black));
                rb_yingse_02.setTextColor(getResources().getColor(R.color.red));
                break;
            case R.id.rb_dingshi_01:
                diglogCallback.readTimer(0);
                break;
            case R.id.rb_dingshi_02:
                diglogCallback.readTimer(15);
                break;
            case R.id.rb_dingshi_03:
                diglogCallback.readTimer(30);
                break;
            case R.id.rb_dingshi_04:
                diglogCallback.readTimer(60);
                break;
        }
    }

    /**
     * 弹窗弹入屏幕的动画
     */
    private void initIntAnimation() {
        if (mIntoSlide != null) {
            mIntoSlide.cancel();
        }
        mIntoSlide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.0f
        );
        mIntoSlide.setDuration(200);
        mIntoSlide.setFillAfter(true);
        mIntoSlide.setFillEnabled(true);
        mView.startAnimation(mIntoSlide);
    }

    /**
     * 过滤重复点击
     */
    public void dimissDialog() {
        if (isClick) {
            return;
        }
        isClick = true;
        initOutAnimation();
    }

    /**
     * 弹窗弹出屏幕的动画
     */
    private void initOutAnimation() {
        if (mOutSlide != null) {
            mOutSlide.cancel();
        }
        mOutSlide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f);
        mOutSlide.setDuration(200);
        mOutSlide.setFillEnabled(true);
        mOutSlide.setFillAfter(true);
        mView.startAnimation(mOutSlide);

        //弹出屏幕动画的监听
        mOutSlide.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //过滤重复点击的标记
                isClick = false;

                ReadSpeakDialogFragment.this.dismiss();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

    }

    /**
     * 拦截手势(弹窗外区域)
     */
    @SuppressLint("ClickableViewAccessibility")
    public void touchOutShowDialog() {
        mDecorView.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                dimissDialog();
            }
            return true;
        });
    }

    /**
     * 监听主界面back键
     * 当点击back键时，执行弹窗动画
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void getFocus() {
        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            // 监听到back键(悬浮手势)返回按钮点击事件
            if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                //判断弹窗是否显示
                if (ReadSpeakDialogFragment.this.getDialog().isShowing()) {
                    dimissDialog();
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 修改 字颜色状态
     *
     * @param yinSpeed
     */
    private void changeSpeedTVColor(TextView yinSpeed) {
        TextView[] speed = {rb_yingsu_12, rb_yingsu_13, rb_yingsu_14, rb_yingsu_15, rb_yingsu_16,
                rb_yingsu_17, rb_yingsu_18};
        for (TextView rb : speed) {
            if (rb == yinSpeed) {
                rb.setTextColor(getResources().getColor(R.color.red));
            } else {
                rb.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

    private void changeDiaoTVColor(TextView yinDiao) {
        TextView[] diao = {rb_yingdiao_12, rb_yingdiao_13, rb_yingdiao_14, rb_yingdiao_15,
                rb_yingdiao_16, rb_yingdiao_17, rb_yingdiao_18};
        for (TextView rb : diao) {
            if (rb == yinDiao) {
                rb.setTextColor(getResources().getColor(R.color.red));
            } else {
                rb.setTextColor(getResources().getColor(R.color.black));
            }
        }
    }

}
