package com.heiheilianzai.app.base;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.FloatImageViewShow;
import com.heiheilianzai.app.utils.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.ButterKnife;

/**
 * Created by scb on 2018/5/26.
 */
public abstract class BaseActivity extends BaseWarmStartActivity {
    public final int SUCCESS = 0x00;
    public final int FAILURE = 0x01;
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
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(initContentView());
        // 初始化View注入
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        FloatImageViewShow.showFloatImage(this, R.id.activity_main_vitualkey);
        initData();
    }

    @Override
    protected void onStart() {
        super.onStart();
        StatusBarUtil.transparencyBar2(this, R.color.white, true);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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

    public void handleResult(String result) {
        Message msg = new Message();
        msg.what = SUCCESS;
        msg.obj = result;
        mHandler.sendMessage(msg);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void test(String entity) {
    }

    protected void uiFreeCharge(View... views) {
        for (View view : views) {
            view.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
        }
    }
}