package com.heiheilianzai.app.activity;

import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.activity.view.BaseAdvertisementActivity;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;

import java.io.File;

/**
 * 热启动广告
 */
public class AdvertisementActivity extends BaseAdvertisementActivity {

    @Override
    public int initContentView() {
        return R.layout.activity_advertisement;
    }

    @Override
    public void onCreateView() {
        String flieName = ShareUitls.getString(getApplicationContext(), "advertising_img", "");
        if (!StringUtils.isEmpty(flieName)) {
            Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
        }
        startPage();
    }

    @Override
    public void initData() {
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

    public void next() {
        updateApp.getRequestData(new UpdateApp.UpdateAppInterface() {
            @Override
            public void Next(String response) {
                try {
                    if (response.length() != 0) {
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        startpage = dataBean.start_page;
                        ReaderApplication.putDailyStartPageMax(dataBean.daily_max_start_page);
                        if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                            setAdImageView(activity_splash_im, startpage, activity, new OnAdImageListener() {
                                @Override
                                public void onAnimationEnd() {
                                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                                    handler.sendEmptyMessageDelayed(1, 0);
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
                    } else {
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(0, 500);
                }
            }
        }, true);
    }
}