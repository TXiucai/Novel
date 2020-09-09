package com.heiheilianzai.app.base;

import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import com.heiheilianzai.app.BuildConfig;
import com.jaeger.library.StatusBarUtil;

import butterknife.ButterKnife;

//支持侵染状态栏
public abstract class BaseButterKnifeTransparentActivity extends BaseWarmStartActivity {

    public abstract int initContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        //侵染状态栏
        StatusBarUtil.setTransparent(this);
        //注入布局
        setContentView(initContentView());
        // 初始化View注入
        ButterKnife.bind(this);
    }

    /**
     * 根据免费参数判断哪些View隐藏
     */
    protected void uiFreeCharge(View... views) {
        for (View view : views) {
            view.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
        }
    }
}
