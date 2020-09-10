package com.heiheilianzai.app.ui.activity;

import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseAdvertisementActivity;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.Startpage;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;

import java.io.File;

/**
 * 热启动广告（根据后台配置是否显示）
 */
public class AdvertisementActivity extends BaseAdvertisementActivity {

    @Override
    public int initContentView() {
        return R.layout.activity_advertisement;
    }

    @Override
    public void onCreateView() {
        String flieName = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_IMG_KAY, "");
        if (!StringUtils.isEmpty(flieName)) {
            Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
        }
        startPage();
    }

    @Override
    public void initData() {
        setStartpageView();
        getOpenScreen();
        next();
    }

    @Override
    public void setMessage() {
        if (!into) {
            into = true;
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    /**
     * 获取系统参数，获取系统最新热更新最大启动次数。
     */
    public void next() {
        updateApp.getRequestData(new UpdateApp.UpdateAppInterface() {
            @Override
            public void Next(String response) {
                try {
                    if (response.length() != 0) {
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        App.putDailyStartPageMax(dataBean.daily_max_start_page);
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onError(String e) {
            }
        });
    }

    /**
     * 加载缓存开屏广告到控件。
     */
    public void setStartpageView() {
        String json = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_JSON_KAY, "");
        if (!StringUtils.isEmpty(json)) {
            Startpage startpage = new Gson().fromJson(json, Startpage.class);
            setStartpageView(startpage);
        } else {
            handler.sendEmptyMessageDelayed(0, 500);
        }
    }

    public void setStartpageView(Startpage startpage) {
        if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
            setAdImageView(activity_splash_im, startpage, activity, new OnAdImageListener() {
                @Override
                public void onAnimationEnd() {
                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(1, 0);
                }

                @Override
                public void onFailed() {
                    handler.sendEmptyMessageDelayed(0, 500);
                }

                @Override
                public void onClick() {
                    skip = false;
                    if (!skip) {
                        handler.removeMessages(1);
                        handler.removeMessages(0);
                        adSkip(startpage, activity);
                    }
                }
            });
        } else {
            handler.sendEmptyMessageDelayed(0, 500);
        }
    }
}