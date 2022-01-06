package com.heiheilianzai.app.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseAdvertisementActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.push.JPushUtil;
import com.heiheilianzai.app.constant.AppConfig;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ApiDomainBean;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BottomIconMenu;
import com.heiheilianzai.app.model.H5UrlBean;
import com.heiheilianzai.app.model.Startpage;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.ConcurrentUrlhelpterKt;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.OnCompletUrl;
import com.heiheilianzai.app.utils.OnError;
import com.heiheilianzai.app.utils.OnThirdComlete;
import com.heiheilianzai.app.utils.OnThirdError;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;
import com.mobi.xad.XAdManager;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;

/**
 * 开屏页
 */
public class SplashActivity extends BaseAdvertisementActivity {
    @BindView(R.id.activity_splash_website)
    public TextView mTxWebsite;
    @BindView(R.id.activity_splash_website_pre)
    public TextView mTxWebsitePre;
    @BindView(R.id.activity_splash_bottom)
    public LinearLayout mLlBottom;
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
        showWebsite();
        compatibleOldUpdate();
        if (isfirst.equals("yes")) {//首次使用删除文件
            FileManager.deleteFile(FileManager.getSDCardRoot());
        }
        ConcurrentUrlhelpterKt.requestThree(new OnThirdComlete() {
            @Override
            public void onThirdComplete() {
                checkHost();
            }
        }, new OnThirdError() {
            @Override
            public void onThirdCError() {
                checkHost();
            }
        });
    }

    private void showWebsite() {
        String website = ShareUitls.getString(activity, "website", "");
        String website_title = ShareUitls.getString(activity, "website_title", "");
        if (!StringUtils.isEmpty(website_title)) {
            mTxWebsite.setVisibility(View.VISIBLE);
            mTxWebsite.setText(String.format(getString(R.string.splash_website), website_title));
            mTxWebsitePre.setVisibility(View.VISIBLE);
            mTxWebsitePre.setText(String.format(getString(R.string.splash_website), website_title));
        } else {
            mTxWebsite.setVisibility(View.GONE);
            mTxWebsitePre.setVisibility(View.GONE);
        }
        mTxWebsite.setOnClickListener(v -> {
            startActivity(new Intent(activity, AboutActivity.class).
                    putExtra("url", website)
                    .putExtra("style", "4"));
        });
        mTxWebsitePre.setOnClickListener(v -> {
            startActivity(new Intent(activity, AboutActivity.class).
                    putExtra("url", website)
                    .putExtra("style", "4"));
        });
    }

    private void checkHost() {
        ConcurrentUrlhelpterKt.getFastUrl(new OnCompletUrl() {
            @Override
            public void onComplteApi(@NotNull String api) {
                initXad(api);
                requestReadPhoneState();
                getH5Domins();
                getDomainHost();
            }
        }, new OnError() {
            @Override
            public void onError() {
                handler.sendEmptyMessageDelayed(0, 500);
            }
        });
    }

    private void initXad(String api) {
        XAdManager.INSTANCE.init(activity.getApplication(), BuildConfig.DEBUG ? BuildConfig.XAD_ENV_APP_ID_DEBUG : BuildConfig.XAD_ENV_APP_ID,
                BuildConfig.DEBUG ? BuildConfig.XAD_EVN_APP_SECRET_DEBUG : BuildConfig.XAD_EVN_APP_SECRET, BuildConfig.XAD_EVN_APP_CHANNEL, api, BuildConfig.DEBUG);
    }


    /**
     * 获取服务端url
     */
    private void getDomainHost() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = ReaderConfig.getBaseUrl() + AppConfig.SERVER_DYNAMIC_DOMAIN;
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                ApiDomainBean apiDomainBean = new Gson().fromJson(response, ApiDomainBean.class);
                ShareUitls.putDominString(activity, "Donmain", new Gson().toJson(apiDomainBean.getApi_domins()));
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });
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
                        if (!StringUtils.isEmpty(dataBean.getVtapi_license_key())) {
                            ShareUitls.putString(SplashActivity.this, "vtapi_license_key", dataBean.getVtapi_license_key());
                        }
                        if (!StringUtils.isEmpty(dataBean.getWebsite_android())) {
                            ShareUitls.putString(SplashActivity.this, "website", dataBean.getWebsite_android());
                        }
                        if (!StringUtils.isEmpty(dataBean.getWebsite_android_title())) {
                            ShareUitls.putString(SplashActivity.this, "website_title", dataBean.getWebsite_android_title());
                        }
                        if (dataBean.getUnit_tag() != null) {
                            ReaderConfig.currencyUnit = dataBean.getUnit_tag().getCurrencyUnit();
                            ReaderConfig.subUnit = dataBean.getUnit_tag().getSubUnit();
                        }
                        if (!StringUtils.isEmpty(dataBean.getBook_text_api())) {
                            ShareUitls.putString(App.getContext(), PrefConst.NOVEL_API, dataBean.getBook_text_api());
                        }
                        if (!StringUtils.isEmpty(dataBean.getTts_open_switch())) {
                            ReaderConfig.TTS_OPEN = dataBean.getTts_open_switch();
                        }
                        ReaderConfig.NOVEL_SDK_AD.clear();
                        ReaderConfig.NOVEL_SDK_AD.addAll(dataBean.getAd_position_book().getList());
                        ReaderConfig.COMIC_SDK_AD.clear();
                        ReaderConfig.COMIC_SDK_AD.addAll(dataBean.getAd_position_comic().getList());
                        ReaderConfig.OTHER_SDK_AD = dataBean.getAd_position_other().getList();
                        App.putDailyStartPageMax(dataBean.daily_max_start_page);
                        getOpenScreen();
                        if (!isfirst.equals("yes")) {
                            if (App.getDailyStartPageMax() == 0 || App.getDailyStartPage() < App.getDailyStartPageMax()) {//是否超过后台设置的每天启动次数
                                String json = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_JSON_KAY, "");
                                if (!StringUtils.isEmpty(json)) {
                                    Startpage startpage = new Gson().fromJson(json, Startpage.class);
                                    if (App.isShowSdkAd(activity, startpage.getAd_show_type())) {
                                        setOpenScreenView(startpage);
                                    } else {
                                        handler.sendEmptyMessageDelayed(0, 500);
                                    }
                                } else {
                                    String str = ShareUitls.getString(SplashActivity.this, "Update", "");
                                    JSONObject jsonObject = new JSONObject(str);
                                    String start_pag = jsonObject.getString("start_page");
                                    if (!StringUtils.isEmpty(start_pag)) {
                                        Startpage startpage = new Gson().fromJson(start_pag, Startpage.class);
                                        if (App.isShowSdkAd(activity, startpage.getAd_show_type())) {
                                            setOpenScreenView(startpage);
                                        } else {
                                            handler.sendEmptyMessageDelayed(0, 500);
                                        }
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

        /**
         * 动态获取主页底部菜单栏图标
         */
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = ReaderConfig.getBaseUrl() + BookConfig.bottom_icon_menu;
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                BottomIconMenu bottomIconMenu = new Gson().fromJson(response, BottomIconMenu.class);
                if (bottomIconMenu != null && bottomIconMenu.list != null || bottomIconMenu.list.size() > 0) {
                    getUrlDownload(bottomIconMenu.getList());
                }
            }

            @Override
            public void onErrorResponse(String ex) {

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
            time = Integer.valueOf(startpage.getCountdown_second());
            String flieName = ShareUitls.getString(getApplicationContext(), PrefConst.ADVERTISING_IMG_KAY, "");
            if (!StringUtils.isEmpty(flieName)) {
                Glide.with(activity).load(new File(flieName)).into(activity_splash_im);
            }
            setAdImageView(activity_splash_im, startpage, activity, new AdvertisementActivity.OnAdImageListener() {
                @Override
                public void onAnimationEnd() {
                    findViewById(R.id.findchannel).setVisibility(View.GONE);
                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                    mLlBottom.setVisibility(View.VISIBLE);
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

    /**
     * 获取 底部 radio button 图片地址
     *
     * @param list
     */
    private void getUrlDownload(List<BottomIconMenu.RBIcons> list) {

        for (int i = 0; i < list.size(); i++) {
            BottomIconMenu.RBIcons rbIcons = list.get(i);
            String iconSelected = rbIcons.getIcon_selected();
            String iconNormal = rbIcons.getIcon_normal();
            saveImg2SD(i, "selected", iconSelected);
            saveImg2SD(i, "normal", iconNormal);
        }

    }

    /**
     * 使用glide下载并转换成bitmap
     *
     * @param i
     * @param type
     * @param url
     */
    private void saveImg2SD(int i, String type, String url) {
        Glide.get(SplashActivity.this).clearMemory();
        Glide.with(SplashActivity.this)
                .asBitmap()
                .load(url)
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                        saveToSystemGallery(i, type, bitmap);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {

                    }
                });
    }

    /**
     * 保存到本地
     *
     * @param i
     * @param type
     * @param bitmap
     */
    public void saveToSystemGallery(int i, String type, Bitmap bitmap) {
        File fileDir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Pictures/", "hhlz");
        if (!fileDir.exists()) {
            fileDir.mkdir();
        }

        String fileName = "rb_btn_" + type + "_" + i + ".png";

        File file = new File(fileDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}