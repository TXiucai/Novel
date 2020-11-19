package com.heiheilianzai.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.view.View;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseAdvertisementActivity;
import com.heiheilianzai.app.component.http.DynamicDomainManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.push.JPushUtil;
import com.heiheilianzai.app.constant.AppConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.H5UrlBean;
import com.heiheilianzai.app.model.Startpage;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.utils.ConcurrentUrlhelpterKt;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.OnCompletUrl;
import com.heiheilianzai.app.utils.OnError;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;

import static com.heiheilianzai.app.constant.ReaderConfig.USE_AD_FINAL;

/**
 * 开屏页
 * 域名加载 查看 {@link DynamicDomainManager}
 */
public class SplashActivity extends BaseAdvertisementActivity {
    public static final String OPEN_TIME_KAY = "open_time";//传参首页打开app时间戳KAY
    private String isfirst;
    private int reconnect_num = 0;
    private long mOpenCurrentTime;//进入开屏页记录时间戳

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
        saAppLogin();//神策登录
        mOpenCurrentTime = DateUtils.currentTime();
        isfirst = ShareUitls.getString(activity, "isfirst", "yes");
        compatibleOldUpdate();
        if (isfirst.equals("yes")) {//首次使用删除文件
            FileManager.deleteFile(FileManager.getSDCardRoot());
        }
        DynamicDomainManager dynamicDomainManager = new DynamicDomainManager(this, new DynamicDomainManager.OnCompleteListener() {
            @Override
            public void onComplete(List<String> apiUrl) {
                ConcurrentUrlhelpterKt.getFastUrl(apiUrl, new OnCompletUrl() {
                    @Override
                    public void onComplteApi(@NotNull String api) {
                        requestReadPhoneState();
                        getH5Domins();
                    }
                }, new OnError() {
                    @Override
                    public void onError() {
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                });
            }
        });
        dynamicDomainManager.start();
    }

    public void getH5Domins() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = App.getBaseUrl() + AppConfig.SERVER_H5_DOMAIN;
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                H5UrlBean h5UrlBean = new Gson().fromJson(response, H5UrlBean.class);
                if (h5UrlBean != null && h5UrlBean.getDomin_list().size() > 0) {
                    ConcurrentUrlhelpterKt.getFastH5Url(h5UrlBean.getDomin_list());
                }
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });

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
                        App.putDailyStartPageMax(dataBean.daily_max_start_page);
                        getOpenScreen();
                        if (!isfirst.equals("yes")) {
                            if (App.getDailyStartPageMax() == 0 || App.getDailyStartPage() < App.getDailyStartPageMax()) {//是否超过后台设置的每天启动次数
                                String json = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_JSON_KAY, "");
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
                        saAppFailedLoad();
                    }
                }
            }
        });
    }

    @Override
    public void setMessage() {
        if (!into) {
            into = true;
            Intent intent = new Intent();
            intent.putExtra(OPEN_TIME_KAY, mOpenCurrentTime);
            if (InternetUtils.internett(activity) && isfirst.equals("yes")) {
                intent.setClass(activity, FirstStartActivity.class);
            } else {
                intent.setClass(activity, MainActivity.class);
            }
            startActivity(intent);
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
            String flieName = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_IMG_KAY, "");
            if (!StringUtils.isEmpty(flieName)) {
                Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
            }
            setAdImageView(activity_splash_im, startpage, activity, new AdvertisementActivity.OnAdImageListener() {
                @Override
                public void onAnimationEnd() {
                    findViewById(R.id.findchannel).setVisibility(View.GONE);
                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(1, time == 5 ? 0 : 1000);
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
        String flieName = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_IMG_KAY, "");
        if (!StringUtils.isEmpty(flieName)) {
            try {
                String str = ShareUitls.getString(SplashActivity.this, "Update", "");
                JSONObject jsonObject = new JSONObject(str);
                String start_pag = jsonObject.getString("start_page");
                if (!StringUtils.isEmpty(start_pag)) {
                    ShareUitls.putString(App.getContext(), PrefConst.ADVERTISING_JSON_KAY, start_pag);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * app加载失败上报神策
     */
    public void saAppFailedLoad() {
        try {
            SensorsDataHelper.setOpenTimeEvent(new Long(DateUtils.getCurrentTimeDifferenceSecond(-1)).intValue());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入app神策登录，有用户信息时用户信息登录，没有时 设备ID登录
     */
    public void saAppLogin() {
        try {
            UserInfoItem userInfo = App.getUserInfoItem(activity);
            String loginId = userInfo != null ? BuildConfig.sa_server_app_id + "_" + userInfo.getUid() : Utils.getUUID(activity);
            SensorsDataHelper.login(loginId);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}