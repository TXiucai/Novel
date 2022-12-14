package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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
import com.heiheilianzai.app.model.BaseSdkAD;
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
    private List<RecommendAppBean.AppListBean> mRecomendAppLocalLists;

    public RecomendApp(Activity activity) {
        this.activity = activity;
    }

    private boolean mIsShowSd = false;

    /**
     * 获取数据
     */
    public void getRequestData() {
        if (ReaderConfig.OTHER_SDK_AD.getApp_index() == 2) {
            mIsShowSd = true;
        }
        localAppAd();
    }

    private void sdkAppAd() {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_APP_RECOMMEND_DEBUG : BuildConfig.XAD_EVN_POS_APP_RECOMMEND, AdType.CUSTOM_TYPE_DEFAULT, 99, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    List<RecommendAppBean.AppListBean> appListBeans = new ArrayList<>();
                    RecommendAppBean recommendAppBean = new RecommendAppBean();
                    for (int i = 0; i < list.size(); i++) {
                        AdInfo adInfo = list.get(i);
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                            RecommendAppBean.AppListBean appListBean = new RecommendAppBean.AppListBean();
                            appListBean.setAdId(adInfo.getAdId());
                            appListBean.setRequestId(adInfo.getRequestId());
                            appListBean.setAdPosId(adInfo.getAdPosId());
                            appListBean.setApp_logo(adInfo.getMaterial().getImageUrl());
                            appListBean.setUser_parame_need("1");
                            appListBean.setDown_link(adInfo.getOperation().getValue());
                            appListBean.setApp_name(adInfo.getMaterial().getTitle());
                            appListBeans.add(appListBean);
                        }
                    }
                    appListBeans.addAll(mRecomendAppLocalLists);
                    getAppUpdatePop(activity, appListBeans);
                } catch (Exception e) {
                    mIsShowSd = false;
                    localAppAd();
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                mIsShowSd = false;
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
                        mRecomendAppLocalLists = recommendAppBean.getApp_list();
                        if (!mIsShowSd) {
                            getAppUpdatePop(activity, mRecomendAppLocalLists);
                        } else {
                            sdkAppAd();
                        }
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

    public Dialog getAppUpdatePop(final Activity activity, List<RecommendAppBean.AppListBean> appListBean) {
        this.activity = activity;
        if (appListBean == null || appListBean.size() == 0) {
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
        switch (appListBean.size()) {
            case 1:
                layoutManager = new GridLayoutManager(activity, 1);
                break;
            case 2:
                layoutManager = new GridLayoutManager(activity, 2);
                break;
            default:
                layoutManager = new GridLayoutManager(activity, 3);
        }
        updateHolder.ry.setLayoutManager(layoutManager);
        ItemOffsetDecoration itemOffsetDecoration = new ItemOffsetDecoration(activity, R.dimen.dimens_10dp);
        updateHolder.ry.addItemDecoration(itemOffsetDecoration);
        RecommendAppAdapter appAdapter = new RecommendAppAdapter(activity, appListBean);
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

    public class ItemOffsetDecoration extends RecyclerView.ItemDecoration {

        private int mItemOffset;

        public ItemOffsetDecoration(int itemOffset) {
            mItemOffset = itemOffset;
        }

        public ItemOffsetDecoration(Context context, int itemOffsetId) {
            this(context.getResources().getDimensionPixelSize(itemOffsetId));
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                                   RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(mItemOffset, mItemOffset, mItemOffset, mItemOffset);
        }
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
            appHolder.tvName.setText(appListBean.getApp_name());
            String user_parame_need = appListBean.getUser_parame_need();
            AdInfo adInfo = BaseSdkAD.newAdInfo(appListBean);
            if (adInfo != null) {
                MyPicasso.glideSdkAd(context, adInfo, appListBean.getApp_logo(), appHolder.ivLogo);
            } else {
                MyPicasso.GlideImageNoSize(context, appListBean.getApp_logo(), appHolder.ivLogo);
            }
            appHolder.tvInstall.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adInfo != null) {
                        XRequestManager.INSTANCE.requestEventClick(context, adInfo);
                    }
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
            return appListBeans == null ? 0 : appListBeans.size();
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
