package com.heiheilianzai.app.base;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.WindowManager;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.StatusBarUtil;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.umeng.analytics.MobclickAgent;

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
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {    //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));  //需要在setContentView()方法后面执行
        }
        //侵染状态栏
        StatusBarUtil.setFullScreen(activity,true,true);
        //com.jaeger.library.StatusBarUtil.setTransparent(activity);

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
