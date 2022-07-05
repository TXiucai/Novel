package com.heiheilianzai.app.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.StatusBarUtil;

import butterknife.ButterKnife;

public abstract class BaseButterKnifeActivity extends BaseWarmStartActivity {
    public Activity activity;

    public abstract int initContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //侵染状态栏
        com.jaeger.library.StatusBarUtil.setTransparent(this);
        //注入布局
        setContentView(initContentView());
        // 初始化View注入
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //StatusBarUtil.transparencyBar2(this, R.color.white, true);
    }
}
