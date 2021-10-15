package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;

import android.content.Intent;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.RecommendAppBean;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 版本更新 工具类
 * Created by scb on 2018/11/28.
 */
public class RecomendApp {
    Activity activity;

    public RecomendApp(Activity activity) {
        this.activity = activity;
    }


    /**
     * 获取数据
     */
    public void getRequestData() {
        if (ReaderConfig.OTHER_SDK_AD.getApp_index() == 2) {
            sdkAppAd();
        } else {
            localAppAd();
        }
    }

    private void sdkAppAd() {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_APP_RECOMMEND_DEBUG : BuildConfig.XAD_EVN_POS_APP_RECOMMEND, AdType.CUSTOM_TYPE_DEFAULT, 99, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    List<RecommendAppBean.AppListBean> appListBeans = new ArrayList<>();
                    RecommendAppBean recommendAppBean = new RecommendAppBean();
                    for (int i = 0; i < list.size(); i++) {
                        RecommendAppBean.AppListBean appListBean = new RecommendAppBean.AppListBean();
                        AdInfo adInfo = list.get(i);
                        appListBean.setApp_logo(adInfo.getMaterial().getImageUrl());
                        appListBean.setUser_parame_need(adInfo.getAdExtra().get("user_parame_need"));
                        appListBean.setDown_link(adInfo.getAdExtra().get("down_url"));
                        appListBean.setApp_name(adInfo.getMaterial().getTitle());
                        if (App.isShowSdkAd(activity, adInfo.getAdExtra().get("ad_show_type"))) {
                            appListBeans.add(appListBean);
                        }
                    }
                    recommendAppBean.setApp_list(appListBeans);
                    getAppUpdatePop(activity, recommendAppBean);
                } catch (Exception e) {
                    localAppAd();
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localAppAd();
            }
        });
    }


    private void localAppAd() {
        ReaderParams readerParams = new ReaderParams(activity);
        String json = readerParams.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mRecommendApp, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        RecommendAppBean recommendAppBean = new Gson().fromJson(response, RecommendAppBean.class);
                        getAppUpdatePop(activity, recommendAppBean);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    class UpdateHolder {
        @BindView(R.id.ry)
        public RecyclerView ry;
        @BindView(R.id.img_close)
        public ImageView dialog_close;

        public UpdateHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public Dialog popupWindow;

    public Dialog getAppUpdatePop(final Activity activity, RecommendAppBean recommendAppBean) {
        this.activity = activity;
        if (recommendAppBean.getApp_list() == null || recommendAppBean.getApp_list().size() == 0) {
            return null;
        }
        long recommendTime = ShareUitls.getRecommendAppTime(activity, "recommendTime", 0);
        long currentTimeDifferenceSecond = DateUtils.getCurrentTimeDifferenceSecond(recommendTime);
        long expiredTime = currentTimeDifferenceSecond / 60 / 60;
//        if (expiredTime <= 1) {
//            return null; //小于1个小时不进行展示
//        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_recommend_app, null);
        popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        final UpdateHolder updateHolder = new UpdateHolder(view);
        RecyclerView.LayoutManager layoutManager;
        switch (recommendAppBean.getApp_list().size()) {
            case 1:
                layoutManager = new GridLayoutManager(activity, 1);
                break;
            case 2:
                layoutManager = new GridLayoutManager(activity, 2);
                break;
            default:
                layoutManager = new GridLayoutManager(activity, 3);
        }

        //设置布局管理器为线性布局管理器
        updateHolder.ry.setLayoutManager(layoutManager);
        RecommendAppAdapter appAdapter = new RecommendAppAdapter(activity, recommendAppBean.getApp_list());
        updateHolder.ry.setAdapter(appAdapter);
        updateHolder.dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        ShareUitls.putRecommendAppTime(activity, "recommendTime", DateUtils.currentTime());
        return popupWindow;
    }

    class RecommendAppAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Activity context;
        private List<RecommendAppBean.AppListBean> appListBeans;

        public RecommendAppAdapter(Activity context, List<RecommendAppBean.AppListBean> appListBeans) {
            this.context = context;
            this.appListBeans = appListBeans;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(context).inflate(R.layout.item_recommend_app, null, false);
            return new RecommendAppHolder(inflate);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            RecommendAppHolder appHolder = (RecommendAppHolder) holder;
            RecommendAppBean.AppListBean appListBean = appListBeans.get(position);
            MyPicasso.GlideImageNoSize(context, appListBean.getApp_logo(), appHolder.ivLogo);
            appHolder.tvName.setText(appListBean.getApp_name());
            String user_parame_need = appListBean.getUser_parame_need();
            appHolder.tvInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String down_link = appListBean.getDown_link();
                    if (!StringUtils.isEmpty(down_link)) {
                        if (Utils.isLogin(activity) && TextUtils.equals(user_parame_need, "2") && !down_link.contains("&uid=")) {
                            down_link += "&uid=" + Utils.getUID(activity);
                        }
                        activity.startActivity(new Intent(activity, AboutActivity.class).
                                putExtra("url", down_link)
                                .putExtra("style", "4"));
                    }
                }
            });
        }

        @Override
        public int getItemCount() {
            return appListBeans.size();
        }
    }

    class RecommendAppHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_recommend_name)
        public TextView tvName;
        @BindView(R.id.tv_recommend_install)
        public TextView tvInstall;
        @BindView(R.id.iv_recommend_logo)
        public ImageView ivLogo;

        public RecommendAppHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
