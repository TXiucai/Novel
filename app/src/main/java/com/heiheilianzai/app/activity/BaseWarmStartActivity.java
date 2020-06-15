package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.utils.Utils;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;

/**
 * 广告热启动需求添加相关的生命周期
 */
public class BaseWarmStartActivity extends FragmentActivity {
    public boolean isActive = true; //是否从后台唤醒

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
        if (!isActive && ReaderApplication.getValidBackgroundTime() > 0) {
            if (Utils.getTimeDifference(ReaderApplication.getBackgroundTime()) >= ReaderApplication.getValidBackgroundTime()) {
                if (ReaderApplication.getDailyStartPageMax() == 0 || ReaderApplication.getDailyStartPage() <= ReaderApplication.getDailyStartPageMax()) {
                    startActivity(new Intent(this, AdvertisementActivity.class));
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            }
            ReaderApplication.setBackgroundTime(0);
        }
        isActive = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
    }


    @Override
    protected void onStop() {
        if (!Utils.isAppOnForeground(getApplicationContext())) {
            isActive = false;//记录当前已经进入后台
            if (ReaderApplication.getValidBackgroundTime() > 0) {
                ReaderApplication.setBackgroundTime(System.currentTimeMillis());
            }
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
