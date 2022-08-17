package com.heiheilianzai.app.base;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.utils.StatusBarUtil;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.umeng.analytics.MobclickAgent;

import butterknife.ButterKnife;

//支持侵染状态栏
public abstract class BaseButterKnifeTransparentActivity extends BaseWarmStartActivity {
    public Activity activity;

    public abstract int initContentView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {    //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));  //需要在setContentView()方法后面执行
        }
        //侵染状态栏
        StatusBarUtil.setFullScreen(this,true,true);
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
