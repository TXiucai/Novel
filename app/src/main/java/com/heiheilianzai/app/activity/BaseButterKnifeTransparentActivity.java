package com.heiheilianzai.app.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.WindowManager;

import com.heiheilianzai.app.BuildConfig;
import com.jaeger.library.StatusBarUtil;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

import butterknife.ButterKnife;

//支持侵染状态栏
public abstract class BaseButterKnifeTransparentActivity extends FragmentActivity {

    public abstract int initContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
      /*  if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }*/
        //侵染状态栏
        StatusBarUtil.setTransparent(this);
        //注入布局
        setContentView(initContentView());
        // 初始化View注入
        ButterKnife.bind(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
    }

    @Override
    protected void onDestroy() {
        try {
            EventBus.getDefault().unregister(this);
        } catch (Exception e) {
        }
        super.onDestroy();
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
