package com.heiheilianzai.app.activity;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.activity.view.BaseAdvertisementActivity;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.domain.DynamicDomainManager;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;

import java.io.File;

import static com.heiheilianzai.app.config.ReaderConfig.USE_AD_FINAL;

/**
 * 开屏页
 */
public class SplashActivity extends BaseAdvertisementActivity {
    String isfirst;

    @Override
    public int initContentView() {
        return R.layout.activity_splash;
    }

    @Override
    public void onCreateView() {
        JPushUtil.setAlias(getApplicationContext());
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        isfirst = ShareUitls.getString(activity, "isfirst", "yes");
        if (isfirst.equals("yes")) {//首次使用删除文件
            FileManager.deleteFile(FileManager.getSDCardRoot());
        }
        DynamicDomainManager dynamicDomainManager = new DynamicDomainManager(this, new DynamicDomainManager.OnCompleteListener() {
            @Override
            public void onComplete(String domain) {
                findViewById(R.id.findchannel).setVisibility(View.GONE);
                ReaderApplication.setBaseUrl(domain);
                requestReadPhoneState();
            }
        });
        dynamicDomainManager.start();
    }

    private void requestReadPhoneState() {
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            next();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    next();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {//@Nullable String title,  @Nullable String content,  @Nullable String cancel,  @Nullable String ensure
                    finish();
                }
            }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, true, new PermissionsUtil.TipInfo(activity.getString(R.string.splashactivity_permissions_t), activity.getString(R.string.splashactivity_permissions_c1) + activity.getString(R.string.app_name) + activity.getString(R.string.splashactivity_permissions_c2), activity.getString(R.string.splashactivity_permissions_cancle), activity.getString(R.string.splashactivity_permissions_set)));
        }
    }


    public void next() {
        updateApp.getRequestData(new UpdateApp.UpdateAppInterface() {
            @Override
            public void Next(String response) {
                try {
                    if (response.length() != 0) {
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        if (dataBean.getUnit_tag() != null) {
                            ReaderConfig.currencyUnit = dataBean.getUnit_tag().getCurrencyUnit();
                            ReaderConfig.subUnit = dataBean.getUnit_tag().getSubUnit();
                        }
                        if (USE_AD_FINAL) {
                            ReaderConfig.ad_switch = dataBean.ad_switch;
                            ReaderConfig.USE_AD = dataBean.ad_switch == 1;
                        }
                        ReaderApplication.putDailyStartPageMax(dataBean.daily_max_start_page);
                        if (dataBean.start_page != null && !StringUtils.isEmpty(dataBean.start_page.image)) {

                            MyPicasso.downloadIMG(dataBean.start_page.image, activity, new MyPicasso.OnDwnloadImg() {
                                @Override
                                public void onFile(String nameFile) {
                                    ShareUitls.putString(ReaderApplication.getContext(), "advertising_img", nameFile);
                                }
                            });
                        }
                        if (!isfirst.equals("yes")) {
                            if (ReaderApplication.getDailyStartPageMax() == 0 || ReaderApplication.getDailyStartPage() < ReaderApplication.getDailyStartPageMax()) {//是否超过后台设置的每天启动次数
                                startpage = dataBean.start_page;
                                if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                                    String flieName = ShareUitls.getString(getApplicationContext(), "advertising_img", "");
                                    if (!StringUtils.isEmpty(flieName)) {
                                        Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
                                    }
                                    setAdImageView(activity_splash_im, startpage, activity, new AdvertisementActivity.OnAdImageListener() {
                                        @Override
                                        public void onAnimationEnd() {
                                            activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                                            handler.sendEmptyMessageDelayed(1, 0);
                                            startPage();
                                        }

                                        @Override
                                        public void onClick() {
                                            skip = false;
                                            if (!skip) {
                                                handler.removeMessages(1);
                                                handler.removeMessages(0);
                                                AdvertisementActivity.adSkip(startpage, activity);
                                            }
                                        }
                                    });
                                } else {
                                    handler.sendEmptyMessageDelayed(0, 500);
                                }
                            } else {
                                handler.sendEmptyMessageDelayed(0, 500);
                            }
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
        }, true, true);
    }

    @Override
    public void setMessage() {
        if (!into) {
            into = true;
            if (InternetUtils.internett(activity) && isfirst.equals("yes")) {
                startActivity(new Intent(activity, FirstStartActivity.class));
            } else {
                startActivity(new Intent(activity, MainActivity.class));
            }
        }
    }

    @Override
    public void initData() {
    }
}
