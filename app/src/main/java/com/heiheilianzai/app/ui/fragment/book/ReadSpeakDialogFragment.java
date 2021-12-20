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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.DialogFragment;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.utils.ListUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.manager.ReadSpeakManager;
import com.heiheilianzai.app.view.read.SignSeekBar;

import java.util.Objects;

import butterknife.BindView;

public class ReadSpeakDialogFragment extends DialogFragment {
    private View mView;
    private RelativeLayout mCancel;
    private View mDecorView;
    private Animation mIntoSlide;
    private Animation mOutSlide;
    public DialogCallback diglogCallback;
    private SignSeekBar sbSpeed;
    private SignSeekBar sbYindiao;

    private RadioButton rb_yingse_01;
    private RadioButton rb_yingse_02;
    @BindView(R.id.rb_dingshi_01)
    public RadioButton rb_dingshi_01;
    @BindView(R.id.rb_dingshi_02)
    public RadioButton rb_dingshi_02;
    @BindView(R.id.rb_dingshi_03)
    public RadioButton rb_dingshi_03;
    @BindView(R.id.rb_dingshi_04)
    public RadioButton rb_dingshi_04;

    private static final int[] beiSu = {50, 75, 100, 125, 150, 175, 200};

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

        //取消读书
        void cancelRead();
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

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.fragment_dialog_read, container, false);
        //初始化Dialog
        initDialogView();
        mCancel = mView.findViewById(R.id.btn_cancle);
        sbSpeed = mView.findViewById(R.id.sb_read_speed);
        sbYindiao = mView.findViewById(R.id.sb_read_yindiao);
        rb_yingse_01 = mView.findViewById(R.id.rb_yingse_01);
        rb_yingse_02 = mView.findViewById(R.id.rb_yingse_02);
        rb_dingshi_01 = mView.findViewById(R.id.rb_dingshi_01);
        rb_dingshi_02 = mView.findViewById(R.id.rb_dingshi_02);
        rb_dingshi_03 = mView.findViewById(R.id.rb_dingshi_03);
        rb_dingshi_04 = mView.findViewById(R.id.rb_dingshi_04);

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
        ReadSpeakDialogFragment.this.getDialog().setCanceledOnTouchOutside(true);
        int optionsSpeed = ShareUitls.getInt(App.getAppContext(), ReadSpeakManager.READ_SPEED, 100);
        int speedIndex = ListUtils.Companion.getElementIndex(optionsSpeed);
        int speedProgress = (speedIndex < 0 || speedIndex > 6) ? 2 : speedIndex;

        sbSpeed.getConfigBuilder()
                .min(0)
                .max(6)
                .progress(speedProgress)
                .sectionCount(6)
                .thumbColor(getResources().getColor(R.color.white))
                .sectionTextColor(getResources().getColor(R.color.black))
                .sectionTextSize(10)
                .setStartText("慢")
                .setEndText("快")
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        sbSpeed.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                int speed = beiSu[progress];
                diglogCallback.readSpeed(speed);
            }
        });

        int optionsPitch = ShareUitls.getInt(App.getAppContext(), ReadSpeakManager.READ_PITCH, 100);
        int pitchIndex = ListUtils.Companion.getElementIndex(optionsPitch);
        int pitchProgress = (pitchIndex < 0 || pitchIndex > 6) ? 2 : pitchIndex;
        sbYindiao.getConfigBuilder()
                .min(0)
                .max(6)
                .progress(pitchProgress)
                .sectionCount(6)
                .thumbColor(getResources().getColor(R.color.white))
                .sectionTextColor(getResources().getColor(R.color.black))
                .sectionTextSize(10)
                .setStartText("低")
                .setEndText("高")
                .sectionTextPosition(SignSeekBar.TextPosition.BELOW_SECTION_MARK)
                .build();
        sbYindiao.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {

            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat, boolean fromUser) {
                int yindiao = beiSu[progress];
                diglogCallback.readDiao(yindiao);
            }
        });

        mCancel.setOnClickListener(view -> {
            diglogCallback.cancelRead();
            dimissDialog();
        });

        int yinse = ShareUitls.getInt(App.getAppContext(), ReadSpeakManager.READ_YINSE, 0);
        if (yinse == 0) {
            rb_yingse_01.setSelected(true);
            rb_yingse_01.setChecked(true);
            rb_yingse_02.setSelected(false);
            rb_yingse_02.setChecked(false);
            rb_yingse_01.setBackgroundResource(R.drawable.rb_stroke_selected);
            rb_yingse_02.setBackgroundResource(R.drawable.rb_stroke_unselect);
            rb_yingse_01.setTextColor(getResources().getColor(R.color.black));
            rb_yingse_02.setTextColor(getResources().getColor(R.color.color_969696));
        } else {
            rb_yingse_01.setSelected(false);
            rb_yingse_01.setChecked(false);
            rb_yingse_02.setSelected(true);
            rb_yingse_02.setSelected(true);
            rb_yingse_01.setBackgroundResource(R.drawable.rb_stroke_unselect);
            rb_yingse_02.setBackgroundResource(R.drawable.rb_stroke_selected);
            rb_yingse_01.setTextColor(getResources().getColor(R.color.color_969696));
            rb_yingse_02.setTextColor(getResources().getColor(R.color.black));
        }

        rb_yingse_01.setOnClickListener(view -> {
            diglogCallback.readSe(0);
            rb_yingse_01.setSelected(true);
            rb_yingse_01.setChecked(true);
            rb_yingse_02.setSelected(false);
            rb_yingse_02.setChecked(false);
            rb_yingse_01.setBackgroundResource(R.drawable.rb_stroke_selected);
            rb_yingse_02.setBackgroundResource(R.drawable.rb_stroke_unselect);
            rb_yingse_01.setTextColor(getResources().getColor(R.color.black));
            rb_yingse_02.setTextColor(getResources().getColor(R.color.color_969696));
        });
        rb_yingse_02.setOnClickListener(view -> {
            diglogCallback.readSe(1);
            rb_yingse_01.setSelected(false);
            rb_yingse_01.setChecked(false);
            rb_yingse_02.setSelected(true);
            rb_yingse_02.setChecked(true);
            rb_yingse_01.setBackgroundResource(R.drawable.rb_stroke_unselect);
            rb_yingse_02.setBackgroundResource(R.drawable.rb_stroke_selected);
            rb_yingse_01.setTextColor(getResources().getColor(R.color.color_969696));
            rb_yingse_02.setTextColor(getResources().getColor(R.color.black));
        });
        rb_dingshi_04.setOnClickListener(view -> diglogCallback.readTimer(60));
        rb_dingshi_03.setOnClickListener(view -> diglogCallback.readTimer(30));
        rb_dingshi_02.setOnClickListener(view -> diglogCallback.readTimer(15));
        rb_dingshi_01.setOnClickListener(view -> diglogCallback.readTimer(-1));

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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
