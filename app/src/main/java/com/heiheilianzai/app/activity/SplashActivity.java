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
import com.heiheilianzai.app.bean.Startpage;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.constants.SharedPreferencesConstant;
import com.heiheilianzai.app.domain.DynamicDomainManager;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ResultCallback;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.umeng.umcrash.UMCrash;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;

import okhttp3.Request;
import okhttp3.Response;

import static com.heiheilianzai.app.config.ReaderConfig.USE_AD_FINAL;

/**
 * 开屏页
 * 域名加载 查看 {@link DynamicDomainManager}
 */
public class SplashActivity extends BaseAdvertisementActivity {
    private String isfirst;
    private int reconnect_num = 0;

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
        compatibleOldUpdate();
        if (isfirst.equals("yes")) {//首次使用删除文件
            FileManager.deleteFile(FileManager.getSDCardRoot());
        }
        DynamicDomainManager dynamicDomainManager = new DynamicDomainManager(this, new DynamicDomainManager.OnCompleteListener() {
            @Override
            public void onComplete(String domain) {
                ReaderApplication.setBaseUrl(domain);
                requestReadPhoneState();
            }
        });
        dynamicDomainManager.start();
    }

    private void requestReadPhoneState() {
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            next();
            getIpTerritory();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    next();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
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
                        getOpenScreen();
                        if (!isfirst.equals("yes")) {
                            if (ReaderApplication.getDailyStartPageMax() == 0 || ReaderApplication.getDailyStartPage() < ReaderApplication.getDailyStartPageMax()) {//是否超过后台设置的每天启动次数
                                String json = ShareUitls.getString(getApplicationContext(), SharedPreferencesConstant.ADVERTISING_JSON_KAY, "");
                                if (!StringUtils.isEmpty(json)) {
                                    Startpage startpage = new Gson().fromJson(json, Startpage.class);
                                    setOpenScreenView(startpage);
                                } else {
                                    String str = ShareUitls.getString(SplashActivity.this, "Update", "");
                                    JSONObject jsonObject = new JSONObject(str);
                                    String start_pag = jsonObject.getString("start_page");
                                    if (!StringUtils.isEmpty(start_pag)) {
                                        Startpage startpage = new Gson().fromJson(start_pag, Startpage.class);
                                        setOpenScreenView(startpage);
                                    } else {
                                        handler.sendEmptyMessageDelayed(0, 500);
                                    }
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

            @Override
            public void onError(String e) {
                if (InternetUtils.internet(SplashActivity.this)) {
                    reconnect_num += 1;
                    if (reconnect_num < 3) {
                        next();
                    } else {
                        MyToash.ToashError(SplashActivity.this, getString(R.string.splashactivity_cannot_link));
                        getIpTerritory();
                    }
                }
            }
        });
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

    /**
     * 设置开屏广告
     *
     * @param startpage
     */
    public void setOpenScreenView(Startpage startpage) {
        this.startpage = startpage;
        if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
            String flieName = ShareUitls.getString(getApplicationContext(), SharedPreferencesConstant.ADVERTISING_IMG_KAY, "");
            if (!StringUtils.isEmpty(flieName)) {
                Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
            }
            setAdImageView(activity_splash_im, startpage, activity, new AdvertisementActivity.OnAdImageListener() {
                @Override
                public void onAnimationEnd() {
                    findViewById(R.id.findchannel).setVisibility(View.GONE);
                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(1, 0);
                    startPage();
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
                        AdvertisementActivity.adSkip(startpage, activity);
                    }
                }
            });
        } else {
            handler.sendEmptyMessageDelayed(0, 500);
        }
    }

    /**
     * 2.2.6版本将系统参数接口和获取开屏广告接口进行拆分。
     * 该方法为了兼容2.2.6之前版本的缓存数据。
     */
    public void compatibleOldUpdate() {
        String flieName = ShareUitls.getString(getApplicationContext(), SharedPreferencesConstant.ADVERTISING_IMG_KAY, "");
        if (!StringUtils.isEmpty(flieName)) {
            try {
                String str = ShareUitls.getString(SplashActivity.this, "Update", "");
                JSONObject jsonObject = new JSONObject(str);
                String start_pag = jsonObject.getString("start_page");
                if (!StringUtils.isEmpty(start_pag)) {
                    ShareUitls.putString(ReaderApplication.getContext(), SharedPreferencesConstant.ADVERTISING_JSON_KAY, start_pag);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 通过第三方提供查询IP方法。
     * 获取用户网络运营地域上报友盟自定义异常。
     * http://pv.sohu.com/cityjson?ie=utf-8 获取用户使用网络的所在地域
     */
    public void getIpTerritory() {
        OkHttpEngine.getInstance(activity).getAsyncHttp(ReaderConfig.thirdpartyGetCity, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onResponse(Response response) {
                try {
                    String body = response.body().string();
                    if (!StringUtils.isEmpty(body)) {
                        UMCrash.generateCustomLog(body, "getIpTerritoryError");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
