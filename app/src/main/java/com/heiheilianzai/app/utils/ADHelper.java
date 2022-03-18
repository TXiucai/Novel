package com.heiheilianzai.app.utils;

import static com.heiheilianzai.app.constant.ReaderConfig.MANHAU;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;

import android.app.Activity;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;

import java.util.List;

public class ADHelper {
    private boolean mIsSdkAd = false;
    private boolean mIsSdkTopAd = false;
    private boolean mIsSdkTopComicAd = false;
    private boolean mIsSdkComicBottomAd = false;

    /**
     * 小说顶部广告
     *
     * @param activity
     */
    public void getWebViewAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.NOVEL_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.NOVEL_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "12") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkAd = true;
                sdkAd(activity);
                return;
            }
        }
        if (!mIsSdkAd) {
            localAd(activity);
        }
    }

    /**
     * 小说底部广告
     *
     * @param activity
     */
    public void getWebTopAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.NOVEL_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.NOVEL_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "17") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkTopAd = true;
                sdkTopAd(activity);
                return;
            }
        }
        if (!mIsSdkTopAd) {
            localTopAd(activity);
        }
    }

    /**
     * 漫画底部广告
     *
     * @param activity
     */
    public void getComicBottomAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "8") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkComicBottomAd = true;
                sdkComicBottomAd(activity);
                return;
            }
        }
        if (!mIsSdkComicBottomAd) {
            localComicBotoomAd(activity);
        }
    }

    /**
     * 漫画顶部广告
     *
     * @param activity
     */
    public void getComicTopAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "13") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkTopComicAd = true;
                sdkComicTopSdkAd(activity);
                return;
            }
        }
        if (!mIsSdkTopComicAd) {
            localComicTopAd(activity);
        }
    }

    private void sdkTopAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_NOVEL_TOP_DEBUG : BuildConfig.XAD_EVN_POS_NOVEL_TOP, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    ReaderConfig.TOP_READ_AD = new BaseAd();
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        ReaderConfig.TOP_READ_AD.setAd_skip_url(adInfo.getOperation().getValue());
                        ReaderConfig.TOP_READ_AD.setAd_title(adInfo.getMaterial().getTitle());
                        ReaderConfig.TOP_READ_AD.setAd_image(adInfo.getMaterial().getImageUrl());
                        ReaderConfig.TOP_READ_AD.setUser_parame_need("1");
                        ReaderConfig.TOP_READ_AD.setAd_url_type(adInfo.getOperation().getType());
                        ReaderConfig.TOP_READ_AD.setAd_type(1);
                        ReaderConfig.TOP_READ_AD.setAdId(adInfo.getAdId());
                        ReaderConfig.TOP_READ_AD.setAdPosId(adInfo.getAdPosId());
                        ReaderConfig.TOP_READ_AD.setRequestId(adInfo.getRequestId());
                        if (!TextUtils.isEmpty(adInfo.getMaterial().getSubtitle())) {
                            ReaderConfig.TOP_READ_AD.setDisplay_ad_days(Integer.valueOf(adInfo.getMaterial().getSubtitle()));
                        }
                        if (ReaderConfig.BOTTOM_READ_AD != null) {
                            ReaderConfig.display_ad_days_novel = ReaderConfig.BOTTOM_READ_AD.display_ad_days > ReaderConfig.TOP_READ_AD.display_ad_days ? ReaderConfig.BOTTOM_READ_AD.display_ad_days : ReaderConfig.TOP_READ_AD.display_ad_days;
                        } else {
                            ReaderConfig.display_ad_days_novel = ReaderConfig.TOP_READ_AD.display_ad_days;
                        }
                    }
                } catch (Exception e) {
                    localTopAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localTopAd(activity);
            }
        });
    }

    private void sdkAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_NOVEL_BOTTOM_DEEBUG : BuildConfig.XAD_EVN_POS_NOVEL_BOTTOM, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    ReaderConfig.BOTTOM_READ_AD = new BaseAd();
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        ReaderConfig.BOTTOM_READ_AD.setAd_skip_url(adInfo.getOperation().getValue());
                        ReaderConfig.BOTTOM_READ_AD.setAd_title(adInfo.getMaterial().getTitle());
                        ReaderConfig.BOTTOM_READ_AD.setAd_image(adInfo.getMaterial().getImageUrl());
                        ReaderConfig.BOTTOM_READ_AD.setUser_parame_need("1");
                        ReaderConfig.BOTTOM_READ_AD.setAd_url_type(adInfo.getOperation().getType());
                        ReaderConfig.BOTTOM_READ_AD.setAd_type(1);
                        ReaderConfig.BOTTOM_READ_AD.setAdId(adInfo.getAdId());
                        ReaderConfig.BOTTOM_READ_AD.setAdPosId(adInfo.getAdPosId());
                        ReaderConfig.BOTTOM_READ_AD.setRequestId(adInfo.getRequestId());
                        if (!TextUtils.isEmpty(adInfo.getMaterial().getSubtitle())) {
                            ReaderConfig.BOTTOM_READ_AD.setDisplay_ad_days(Integer.valueOf(adInfo.getMaterial().getSubtitle()));
                        }
                        if (ReaderConfig.TOP_READ_AD != null) {
                            ReaderConfig.display_ad_days_novel = ReaderConfig.BOTTOM_READ_AD.display_ad_days > ReaderConfig.TOP_READ_AD.display_ad_days ? ReaderConfig.BOTTOM_READ_AD.display_ad_days : ReaderConfig.TOP_READ_AD.display_ad_days;
                        } else {
                            ReaderConfig.display_ad_days_novel = ReaderConfig.BOTTOM_READ_AD.display_ad_days;
                        }
                    }
                } catch (Exception e) {
                    localAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localAd(activity);
            }
        });
    }

    private void localAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", XIAOSHUO + "");
        params.putExtraParams("position", "12");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            BaseAd baseAd = new Gson().fromJson(result, BaseAd.class);
                            ReaderConfig.BOTTOM_READ_AD = baseAd;
                            ReaderConfig.display_ad_days_novel = baseAd.getDisplay_ad_days();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void localTopAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", XIAOSHUO + "");
        params.putExtraParams("position", "17");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            BaseAd baseAd = new Gson().fromJson(result, BaseAd.class);
                            ReaderConfig.TOP_READ_AD = baseAd;
                            ReaderConfig.display_ad_days_novel = baseAd.getDisplay_ad_days();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void sdkComicBottomAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_END_DEBUG : BuildConfig.XAD_EVN_POS_COMIC_END, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    ReaderConfig.BOTTOM_COMIC_AD = new BaseAd();
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        ReaderConfig.BOTTOM_COMIC_AD.setAdId(adInfo.getAdId());
                        ReaderConfig.BOTTOM_COMIC_AD.setRequestId(adInfo.getRequestId());
                        ReaderConfig.BOTTOM_COMIC_AD.setAdPosId(adInfo.getAdPosId());
                        ReaderConfig.BOTTOM_COMIC_AD.setAd_skip_url(adInfo.getOperation().getValue());
                        ReaderConfig.BOTTOM_COMIC_AD.setAd_title(adInfo.getMaterial().getTitle());
                        ReaderConfig.BOTTOM_COMIC_AD.setAd_image(adInfo.getMaterial().getImageUrl());
                        ReaderConfig.BOTTOM_COMIC_AD.setUser_parame_need("1");
                        ReaderConfig.BOTTOM_COMIC_AD.setAd_url_type(adInfo.getOperation().getType());
                        ReaderConfig.BOTTOM_COMIC_AD.setAd_type(1);
                        if (!TextUtils.isEmpty(adInfo.getMaterial().getSubtitle())) {
                            ReaderConfig.BOTTOM_COMIC_AD.setDisplay_ad_days(Integer.valueOf(adInfo.getMaterial().getSubtitle()));
                        }
                        if (ReaderConfig.TOP_COMIC_AD != null) {
                            ReaderConfig.display_ad_days_comic = ReaderConfig.TOP_COMIC_AD.display_ad_days > ReaderConfig.BOTTOM_COMIC_AD.display_ad_days ? ReaderConfig.TOP_COMIC_AD.display_ad_days : ReaderConfig.BOTTOM_COMIC_AD.display_ad_days;
                        } else {
                            ReaderConfig.display_ad_days_comic = ReaderConfig.BOTTOM_COMIC_AD.getDisplay_ad_days();
                        }
                    }
                } catch (Exception e) {
                    localComicBotoomAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localComicBotoomAd(activity);
            }
        });
    }

    private void localComicBotoomAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", MANHAU + "");
        params.putExtraParams("position", "8");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            ReaderConfig.BOTTOM_COMIC_AD= new Gson().fromJson(result, BaseAd.class);
                            ReaderConfig.display_ad_days_comic = ReaderConfig.BOTTOM_COMIC_AD.getDisplay_ad_days();
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void sdkComicTopSdkAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_TOP_DEEBUG : BuildConfig.XAD_EVN_POS_COMIC_TOP, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        ReaderConfig.TOP_COMIC_AD = new BaseAd();
                        ReaderConfig.TOP_COMIC_AD.setAdId(adInfo.getAdId());
                        ReaderConfig.TOP_COMIC_AD.setAdPosId(adInfo.getAdPosId());
                        ReaderConfig.TOP_COMIC_AD.setRequestId(adInfo.getRequestId());
                        ReaderConfig.TOP_COMIC_AD.setAd_image(adInfo.getMaterial().getImageUrl());
                        ReaderConfig.TOP_COMIC_AD.setAd_skip_url(adInfo.getOperation().getValue());
                        ReaderConfig.TOP_COMIC_AD.setAd_title(adInfo.getMaterial().getTitle());
                        ReaderConfig.TOP_COMIC_AD.setUser_parame_need("1");
                        ReaderConfig.TOP_COMIC_AD.setAd_url_type(adInfo.getOperation().getType());
                        ReaderConfig.TOP_COMIC_AD.setAd_type(1);
                        if (!TextUtils.isEmpty(adInfo.getMaterial().getSubtitle())) {
                            ReaderConfig.TOP_COMIC_AD.setDisplay_ad_days(Integer.valueOf(adInfo.getMaterial().getSubtitle()));
                        }
                        if (ReaderConfig.BOTTOM_COMIC_AD != null) {
                            ReaderConfig.display_ad_days_comic = ReaderConfig.TOP_COMIC_AD.display_ad_days > ReaderConfig.BOTTOM_COMIC_AD.display_ad_days ? ReaderConfig.TOP_COMIC_AD.display_ad_days : ReaderConfig.BOTTOM_COMIC_AD.display_ad_days;
                        } else {
                            ReaderConfig.display_ad_days_comic = ReaderConfig.TOP_COMIC_AD.getDisplay_ad_days();
                        }
                    } else {
                        localTopAd(activity);
                    }
                } catch (Exception e) {
                    localTopAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localTopAd(activity);
            }
        });
    }

    private void localComicTopAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", MANHAU + "");
        params.putExtraParams("position", "13");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                           ReaderConfig.TOP_COMIC_AD= new Gson().fromJson(result, BaseAd.class);
                            ReaderConfig.display_ad_days_comic = ReaderConfig.TOP_COMIC_AD.getDisplay_ad_days();
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

}
