package com.heiheilianzai.app.read;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.umeng.analytics.MobclickAgent;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by scb on 2018/5/26.
 */
public abstract class BaseReadActivity extends Activity {
    public final int SUCCESS = 0x00;
    public final int FAILURE = 0x01;
    Activity activity;
    int mScreenHeight;
    public Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCESS:
                    initInfo((String) msg.obj);
                    break;
                case FAILURE:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ActionBar a = getActionBar();
        if (a != null) {
            a.hide();
        }
        activity = this;
        mScreenHeight = ScreenSizeUtils.getInstance(activity).getScreenHeight();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(initContentView());
        // 初始化View注入
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        initData();
    }

    /**
     * 配置布局文件
     *
     * @return
     */
    public abstract int initContentView();

    /**
     * 初始化各个视图
     */
    public abstract void initView();

    /**
     * 发起网络请求
     */
    public abstract void initData();

    /**
     * 处理网络请求数据
     *
     * @param json
     */
    public void initInfo(String json) {
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void test(String entity) {

    }

    protected void uiFreeCharge(View... views) {
        for (View view : views) {
            if (view != null) {
                view.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
            }
        }
    }

    protected void dismissAllDialog(Dialog... dialogs) {
        for (Dialog dialog : dialogs) {
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }
}
