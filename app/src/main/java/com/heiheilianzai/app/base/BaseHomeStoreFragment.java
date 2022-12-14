package com.heiheilianzai.app.base;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.HomeRecommendAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.HomeRecommendBean;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.MyShareActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.TopNewActivity;
import com.heiheilianzai.app.ui.activity.TopYearBookActivity;
import com.heiheilianzai.app.ui.activity.TopYearComicActivity;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.StroeNewFragment;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.ConvenientBanner;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.constant.ReaderConfig.CARTOON;
import static com.heiheilianzai.app.constant.ReaderConfig.MANHAU;
import static com.heiheilianzai.app.constant.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.constant.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.constant.ReaderConfig.WANBEN;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.constant.ReaderConfig.mAdvert;

/**
 * ??????????????????????????????????????????
 * max_edit_time????????????????????????????????????????????????????????????????????????????????????max_edit_time ???????????????????????????????????????
 * ??????????????????????????????????????????????????????page=1????????????????????????????????????
 * label_total????????????????????????????????????page=1????????????
 *
 * @param <T>
 */
public abstract class BaseHomeStoreFragment<T> extends BaseButterKnifeFragment {
    @BindView(R.id.store_comic_refresh_layout)
    public SHSwipeRefreshLayout store_comic_refresh_layout;//?????????????????????????????????????????????????????????????????????
    @BindView(R.id.fragment_store_comic_content_commend)
    public RecyclerView recyclerView;
    protected RelativeLayout fragment_newbookself_top;
    protected StroeNewFragment.MyHotWord hot_word;
    protected boolean postAsyncHttpEngine_ing;//??????????????????
    protected Gson gson = new Gson();
    protected List<T> listData = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected SmartRecyclerAdapter smartRecyclerAdapter;
    protected LinearLayoutManager layoutManager;
    protected View headerView;
    boolean isScollYspill = false;
    int page = 1;//????????????
    String max_edit_time;//???????????????????????????
    boolean isEdit = false;//???????????????????????????????????????
    boolean isLoadMore = true;//??????????????????
    int mFirstIndex = -1;//??????????????????????????????
    private boolean mIsNovelLabelSdk;
    private boolean mIsComicLabelSdk;
    private boolean mIsCartoonLabelSdk;
    private String mChannelId = "";
    private String mTopChannelId = "";
    private int mPosition;
    private boolean mIsFirstLoadNoLimit = true;//?????????????????????????????????????????????
    public boolean mIsFresh = true;//????????????

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        initViews();
    }

    @Override
    protected void initData() {
        getData();
    }

    protected void initViews() {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        fragment_newbookself_top = ((StroeNewFragment) getParentFragment()).fragment_newbookself_top;
        hot_word = ((StroeNewFragment) getParentFragment()).myHotWord;
        layoutManager = new MyContentLinearLayoutManager(getContext());
        smartRecyclerAdapter = new SmartRecyclerAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        smartRecyclerAdapter.setHeaderView(headerView);
        recyclerView.setAdapter(smartRecyclerAdapter);
        //?????????????????????????????????
        /*recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case MotionEvent.ACTION_UP://?????????????????????????????????????????????????????????
                        if (ViewUtils.getViewAlpha(fragment_newbookself_top) > 0.0) {
                            fragment_newbookself_top.setAlpha(1);
                        }
                        break;
                }
                return false;
            }
        });*/
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // ????????????????????????????????????
                    int newPosition = layoutManager.findFirstVisibleItemPosition();
                    if (mFirstIndex != newPosition) {
                        onMyScrollStateChanged(newPosition);
                        mFirstIndex = newPosition;
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float scollY = getScollYDistance();
                if (scollY <= 100) {//??????????????????????????????????????????????????????
                    isScollYspill = false;
                    try {
                        fragment_newbookself_top.getBackground().setAlpha((int) ((scollY / 100.0f) * 255.0f));
                    } catch (Exception e) {
                    }
                    float alpha = ViewUtils.getViewAlpha(fragment_newbookself_top.getBackground());
                    MyToash.LogE("postEvent", " alpha:" + alpha + " scollY:" + scollY);
                    if (scollY <= 0.0f) {
                        postEvent(ViewUtils.getViewAlpha(fragment_newbookself_top.getBackground()));
                    }
                } else {
                    float alpha = ViewUtils.getViewAlpha(fragment_newbookself_top.getBackground());
                    MyToash.LogE("postEvent", " alpha:" + alpha + " scollY:" + scollY);
                    if (alpha < 255) {
                        isScollYspill = true;
                    }
                    if (isScollYspill) {
                        isScollYspill = false;
                        fragment_newbookself_top.getBackground().setAlpha(255);
                        postEvent(ViewUtils.getViewAlpha(fragment_newbookself_top.getBackground()));
                    }
                }
            }
        });*/
        store_comic_refresh_layout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mFirstIndex = 0;
                store_comic_refresh_layout.setLoadmoreEnable(true);
                isLoadMore = true;
                mIsFirstLoadNoLimit = true;
                mIsFresh = true;
                setChannelId();
            }

            @Override
            public void onLoading() {
                if (isLoadMore()) {
                    page += 1;
                    if (mIsFirstLoadNoLimit && isLabelNoLimit()) {//???????????????????????????page
                        page = 1;
                        mIsFirstLoadNoLimit = false;
                    }
                    mIsFresh = false;
                    getChannelDetailData();
                } else {
                    finishLoadmore();
                }
            }

            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        store_comic_refresh_layout.setRefreshViewText(getString(R.string.pull_to_refresh));
                        //fragment_newbookself_top.setAlpha(1.0f - percent);
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        store_comic_refresh_layout.setRefreshViewText(getString(R.string.release_to_refresh));
                        break;
                    case SHSwipeRefreshLayout.START:
                        store_comic_refresh_layout.setRefreshViewText(getString(R.string.refreshing));
                        break;
                }
            }

            @Override
            public void onLoadmorePullStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        store_comic_refresh_layout.setLoaderViewText(getString(isLoadMore() ? R.string.pullup_to_load : R.string.no_load));
                        break;
                    case SHSwipeRefreshLayout.OVER_TRIGGER_POINT:
                        store_comic_refresh_layout.setLoaderViewText(getString(isLoadMore() ? R.string.release_to_load : R.string.no_load));
                        break;
                    case SHSwipeRefreshLayout.START:
                        store_comic_refresh_layout.setLoaderViewText(getString(isLoadMore() ? R.string.loading : R.string.no_load));
                        break;
                }
            }
        });
    }

    public void getHeaderView(String result, int flag) {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        }
        ConvenientBanner<BannerItemStore> mStoreBannerMale = headerView.findViewById(R.id.store_banner_male);
        ViewGroup.LayoutParams layoutParams = mStoreBannerMale.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        layoutParams.height = ScreenSizeUtils.getInstance(activity).getScreenWidth() * 3 / 5;
        ConvenientBanner.initbanner(activity, gson, result, mStoreBannerMale, 5000, flag);
        smartRecyclerAdapter.setHeaderView(headerView);
    }

    public void initWaterfall(String jsonObject, Type typeOfT) {
        if (!StringUtils.isEmpty(jsonObject)) {
            if (page == 1 && mIsFresh) {
                listData.clear();
            }
            listData.addAll(gson.fromJson(jsonObject, typeOfT));
            adapter.notifyDataSetChanged();
            //smartRecyclerAdapter.notifyDataSetChanged();
        }
    }

    protected void getData() {
        setChannelId();
        setPosition();
    }

    protected void getCacheData() {
        getCacheBannerData();
        getCacheStockData();
    }

    protected abstract boolean isLabelNoLimit();

    protected abstract void setPosition();

    protected abstract void setChannelId();

    protected abstract void getChannelDetailData();

    protected abstract void getSdkLableAd();

    protected abstract void initLable(Object type);

    protected abstract void getBannerData();

    protected abstract void getStockData();

    protected abstract void getCacheBannerData();

    protected abstract void getCacheStockData();

    protected abstract void initInfo(String data);

    protected abstract void initWaterfall(String jsonObject);

    protected abstract void postEvent(float alpha);

    protected abstract void onMyScrollStateChanged(int position);//???????????????????????????

    protected abstract void getHomeRecommend();

    protected void setPosition(int position) {
        mPosition = position;
    }

    protected void setmChannelId(String channelId, String channelRecommendId) {
        mChannelId = channelRecommendId;
        mTopChannelId = channelId;
        getHomeAds();
        getChannelDetailData();
    }

    protected void getChannelDetailData(String url, int type, String recommendId) {
        mChannelId = recommendId;
        getChannelDetailData(url, type);
    }

    protected void getChannelDetailData(String url, int type) {
        if (TextUtils.isEmpty(mChannelId)) {
            listData.clear();
            adapter.notifyDataSetChanged();
            smartRecyclerAdapter.notifyDataSetChanged();
            if (page == 1) {//??????????????????????????????????????????????????????
                store_comic_refresh_layout.finishRefresh();
            } else {
                store_comic_refresh_layout.finishLoadmore();
            }
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        if (type == 1) {
            params.putExtraParams("book_channel_id", mTopChannelId);
        } else if (type == 2) {
            params.putExtraParams("comic_channel_id", mTopChannelId);
        } else {
            params.putExtraParams("video_channel_id", mTopChannelId);
        }
        params.putExtraParams("recommend_id", mChannelId);
        params.putExtraParams("page", "" + page);
        params.putExtraParams("limit", "4"); //??????4??????????????????
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) {
                if (!StringUtils.isEmpty(response)) {
                    try {
                        setIsLoadMore(response);
                        JSONObject jsonObject = new JSONObject(response);
                        String edit_time = jsonObject.getString("max_edit_time");//??????????????????????????????
                        if (page == 1 && mIsFresh) {//??????????????????????????????????????????????????????
                            max_edit_time = edit_time;
                            finishRefresh(true);
                        } else {
                            if (isLoadMore) {
                                boolean isEdit = !edit_time.equals(max_edit_time);
                                if (isEdit) {//??????????????????
                                    getEditData();
                                    return;
                                }
                            }
                            finishLoadmore(isLoadMore);
                        }
                        initInfo(response);
                        getSdkLableAd();//?????????????????????
                    } catch (Exception e) {
                        finishLoadmore();
                        e.printStackTrace();
                    }
                } else {
                    if (page > 1) {
                        isLoadMore = false;
                    }
                    if (page == 1) {
                        finishRefresh(false);
                    } else {
                        finishLoadmore(false);
                    }
                }
            }

            @Override
            public void onErrorResponse(String ex) {
                if (page == 1) {
                    finishRefresh(false);
                } else {
                    finishLoadmore(false);
                }
            }
        });
    }

    public String getRecommendChannelId(List<String> list) {
        String recommendChannelId = "";
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                String s = list.get(i);
                if (i < list.size() - 1) {
                    recommendChannelId += s + ",";
                } else {
                    recommendChannelId += s;
                }
            }
        }
        return recommendChannelId;
    }

    protected void getSdkLableAd(int recommendType) {
        String type;
        if (recommendType == 1) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_COLUMN_NOVLE_DEBUG : BuildConfig.XAD_EVN_POS_HOME_COLUMN_NOVLE;
            for (int i = 0; i < ReaderConfig.NOVEL_SDK_AD.size(); i++) {
                AppUpdate.ListBean listBean = ReaderConfig.NOVEL_SDK_AD.get(i);
                if (TextUtils.equals(listBean.getPosition(), "1") && TextUtils.equals(listBean.getSdk_switch(), "2")) {//????????????????????? ???????????????
                    mIsNovelLabelSdk = true;
                    sdkLableAd(recommendType, type);
                    return;
                }
            }
            if (!mIsNovelLabelSdk) {
                localLabelAd(recommendType);
            }
        } else if (recommendType == 2) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_COLUMN_COMIC_DEBUG : BuildConfig.XAD_EVN_POS_HOME_COLUMN_COMIC;
            for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
                AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
                if (TextUtils.equals(listBean.getPosition(), "1") && TextUtils.equals(listBean.getSdk_switch(), "2")) {//????????????????????? ???????????????
                    mIsComicLabelSdk = true;
                    sdkLableAd(recommendType, type);
                    return;
                }
            }
            if (!mIsComicLabelSdk) {
                localLabelAd(recommendType);
            }
        } else {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_COLUMN_CARTOON_DEBUG : BuildConfig.XAD_EVN_POS_HOME_COLUMN_CARTOON;
            for (int i = 0; i < ReaderConfig.VIDEO_SDK_AD.size(); i++) {
                AppUpdate.ListBean listBean = ReaderConfig.VIDEO_SDK_AD.get(i);
                if (TextUtils.equals(listBean.getPosition(), "30") && TextUtils.equals(listBean.getSdk_switch(), "2")) {//????????????????????? ???????????????
                    mIsCartoonLabelSdk = true;
                    sdkLableAd(recommendType, type);
                    return;
                }
            }
            if (!mIsCartoonLabelSdk) {
                localLabelAd(recommendType);
            }
        }
    }

    private void localLabelAd(int recommendType) {
        String json = "";
        String requestParams = ReaderConfig.getBaseUrl() + mAdvert;
        if (recommendType == 1) {
            ReaderParams params = new ReaderParams(activity);
            params.putExtraParams("type", XIAOSHUO + "");
            params.putExtraParams("position", "1");
            json = params.generateParamsJson();
        } else if (recommendType == 2) {
            ReaderParams params = new ReaderParams(activity);
            params.putExtraParams("type", MANHAU + "");
            params.putExtraParams("position", "1");
            json = params.generateParamsJson();
        } else {
            ReaderParams params = new ReaderParams(activity);
            params.putExtraParams("position", "30");
            params.putExtraParams("type", CARTOON + "");
            json = params.generateParamsJson();
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            if (recommendType == 1) {//??????
                                BaseAd baseAd = new Gson().fromJson(result, StroreBookcLable.class);
                                StroreBookcLable lableAd = (StroreBookcLable) baseAd;
                                initLable(lableAd);
                            } else if (recommendType == 2) {//??????
                                BaseAd baseAd = new Gson().fromJson(result, StroreComicLable.class);
                                StroreComicLable lableAd = (StroreComicLable) baseAd;
                               /* lableAd.setAd_image(baseAd.getAd_image());
                                lableAd.setAd_title(baseAd.getAd_title());
                                lableAd.setAd_type(baseAd.getAd_type());
                                lableAd.setAd_url_type(baseAd.getAd_url_type());
                                lableAd.setAd_skip_url(baseAd.getAd_skip_url());*/
                                initLable(lableAd);
                            } else {
                                BaseAd baseAd = new Gson().fromJson(result, StroreCartoonLable.class);
                                StroreCartoonLable lableAd = (StroreCartoonLable) baseAd;
                               /* lableAd.setAd_image(baseAd.getAd_image());
                                lableAd.setAd_title(baseAd.getAd_title());
                                lableAd.setAd_type(baseAd.getAd_type());
                                lableAd.setAd_url_type(baseAd.getAd_url_type());
                                lableAd.setAd_skip_url(baseAd.getAd_skip_url());*/
                                initLable(lableAd);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void sdkLableAd(int recommendType, String type) {
        XRequestManager.INSTANCE.requestAd(activity, type, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    if (recommendType == 1) {//??????
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                            StroreBookcLable lableAd = new StroreBookcLable();
                            lableAd.setRequestId(adInfo.getRequestId());
                            lableAd.setAdId(adInfo.getAdId());
                            lableAd.setAdPosId(adInfo.getAdPosId());
                            lableAd.setAd_image(adInfo.getMaterial().getImageUrl());
                            lableAd.setAd_title(adInfo.getMaterial().getTitle());
                            lableAd.setAd_type(1);
                            lableAd.setAd_url_type(adInfo.getOperation().getType());
                            lableAd.setAd_skip_url(adInfo.getOperation().getValue());
                            initLable(lableAd);
                        }
                    } else if (recommendType == 2) {//??????
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                            StroreComicLable lableAd = new StroreComicLable();
                            lableAd.setRequestId(adInfo.getRequestId());
                            lableAd.setAdId(adInfo.getAdId());
                            lableAd.setAdPosId(adInfo.getAdPosId());
                            lableAd.setAd_image(adInfo.getMaterial().getImageUrl());
                            lableAd.setAd_title(adInfo.getMaterial().getTitle());
                            lableAd.setAd_type(1);
                            lableAd.setAd_url_type(adInfo.getOperation().getType());
                            lableAd.setAd_skip_url(adInfo.getOperation().getValue());
                            initLable(lableAd);
                        }
                    } else {
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                            StroreCartoonLable lableAd = new StroreCartoonLable();
                            lableAd.setRequestId(adInfo.getRequestId());
                            lableAd.setAdId(adInfo.getAdId());
                            lableAd.setAdPosId(adInfo.getAdPosId());
                            lableAd.setAd_image(adInfo.getMaterial().getImageUrl());
                            lableAd.setAd_title(adInfo.getMaterial().getTitle());
                            lableAd.setAd_type(1);
                            lableAd.setAd_url_type(adInfo.getOperation().getType());
                            lableAd.setAd_skip_url(adInfo.getOperation().getValue());
                            initLable(lableAd);
                        }
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localLabelAd(recommendType);
            }
        });
    }

    protected void getHomeRecommend(int recommendType) {
        localIconAd(recommendType);
    }

    private void localIconAd(int recommendType) {
        if (TextUtils.isEmpty(mTopChannelId)) {
            return;
        }
        RecyclerView ryRecommend = headerView.findViewById(R.id.ry_recommend);
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_type", String.valueOf(recommendType - 1));
        params.putExtraParams("icon_channel_id", mTopChannelId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(App.getBaseUrl() + ReaderConfig.mHomeRecomment, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                Log.e("icon  ??????", "response:" + response);
                HomeRecommendBean homeRecommendBean = new Gson().fromJson(response, HomeRecommendBean.class);
                List<HomeRecommendBean.RecommeListBean> recomme_list = homeRecommendBean.getRecomme_list();
                boolean isCartoonIconSDK = false;
                for (int i = 0; i < ReaderConfig.VIDEO_SDK_AD.size(); i++) {
                    AppUpdate.ListBean listBean = ReaderConfig.VIDEO_SDK_AD.get(i);
                    if (TextUtils.equals(listBean.getPosition(), "29") && TextUtils.equals(listBean.getSdk_switch(), "2")) {//??????icon
                        isCartoonIconSDK = true;
                        break;
                    }
                }
                //???????????????????????????????????????icon
                if ((ReaderConfig.OTHER_SDK_AD.getIcon_index() == 2 || (recommendType == 3 && isCartoonIconSDK)) && mPosition == 0) {//????????????
                    sdkIconAd(ryRecommend, recommendType, recomme_list);
                } else {
                    initRecommend(ryRecommend, recomme_list, recommendType);
                }
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });
    }

    private void sdkIconAd(RecyclerView recyclerView, int recommendType, List<HomeRecommendBean.RecommeListBean> localList) {
        String type;
        if (recommendType == 1) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_ICON_NOVEL_DEBUG : BuildConfig.XAD_EVN_POS_HOME_ICON_NOVEL;
        } else if (recommendType == 2) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_ICON_COMIC_DEBUG : BuildConfig.XAD_EVN_POS_HOME_ICON_COMIC;
        } else {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_ICON_CARTOON_DEBUG : BuildConfig.XAD_EVN_POS_HOME_ICON_CARTOON;
        }
        XRequestManager.INSTANCE.requestAd(activity, type, AdType.CUSTOM_TYPE_DEFAULT, 99, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    List<HomeRecommendBean.RecommeListBean> recomme_list = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        AdInfo adInfo = list.get(i);
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                            HomeRecommendBean.RecommeListBean recommeListBean = new HomeRecommendBean.RecommeListBean();
                            recommeListBean.setRequestId(adInfo.getRequestId());
                            recommeListBean.setAdPosId(adInfo.getAdPosId());
                            recommeListBean.setAdId(adInfo.getAdId());
                            recommeListBean.setImg_icon(adInfo.getMaterial().getImageUrl());
                            recommeListBean.setJump_url(adInfo.getOperation().getValue());
                            recommeListBean.setJump_type("0");
                            recommeListBean.setRecommend_type("0");
                            recommeListBean.setRedirect_type(String.valueOf(adInfo.getOperation().getType()));
                            recommeListBean.setUser_parame_need("1");
                            recommeListBean.setTitle(adInfo.getMaterial().getTitle());
                            recommeListBean.setWeight(adInfo.getMaterial().getSubtitle());
                            recomme_list.add(recommeListBean);
                        }
                    }
                    for (int i = 0; i < recomme_list.size(); i++) {
                        HomeRecommendBean.RecommeListBean sdkRecomenBean = recomme_list.get(i);
                        if (!TextUtils.isEmpty(sdkRecomenBean.getWeight())) {
                            Integer position = Integer.valueOf(sdkRecomenBean.getWeight());
                            if (localList.size() >= position) {
                                if (position == 0) {
                                    localList.add(0, sdkRecomenBean);
                                } else {
                                    localList.add(position - 1, sdkRecomenBean);
                                }
                            } else {
                                localList.add(sdkRecomenBean);
                            }
                        }
                    }
                    initRecommend(recyclerView, localList, recommendType);
                } catch (Exception e) {
                    initRecommend(recyclerView, localList, recommendType);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                initRecommend(recyclerView, localList, recommendType);
            }
        });
    }

    private void initRecommend(RecyclerView recyclerView, List<HomeRecommendBean.RecommeListBean> recomme_list, int recommendType) {
        if (recomme_list == null || recomme_list.size() == 0) {
            return;
        }
        recyclerView.setAdapter(null);
        Log.e("icon  ??????", "recome_list:" + recomme_list.toString());
        int spanCount = recomme_list.size() >= 5 ? 5 : recomme_list.size();
        recyclerView.setLayoutManager(new GridLayoutManager(activity, spanCount));
        HomeRecommendAdapter homeRecommendAdapter = new HomeRecommendAdapter(activity, recomme_list);
        recyclerView.setAdapter(homeRecommendAdapter);
        homeRecommendAdapter.setOnItemRecommendListener(new HomeRecommendAdapter.OnItemRecommendListener() {
            @Override
            public void onItemRecommendListener(HomeRecommendBean.RecommeListBean recommeListBean) {
                int jump_type = Integer.valueOf(recommeListBean.getJump_type());///1=>'??????-?????????',2=>'??????-?????????',
                //3=>'??????-?????????',4=>'VIP?????????', 6=>'??????-??????',7=>'????????????????????????',8=>'????????????????????????',
                //9=>'?????????',10=>'???????????????',11=>'????????????',12=>'????????????',13=>'????????????',14=>'????????????'
                String jump_url = recommeListBean.getJump_url();
                int recommend_type = Integer.valueOf(recommeListBean.getRecommend_type());//0=>'??????',1=>'??????',2=>'????????????'
                int redirect_type = Integer.valueOf(recommeListBean.getRedirect_type());//?????????0  ????????????  1??????????????????   2??????????????????
                int user_parame_need = Integer.valueOf(recommeListBean.getUser_parame_need());//?????????????????????????????? 1????????????   2?????????????????????
                Intent intent = new Intent(activity, BaseOptionActivity.class);
                intent.putExtra("PRODUCT", recommendType);
                if (jump_type == 0 || jump_type == 5) {
                    if (Utils.isLogin(activity) && user_parame_need == 2 && !jump_url.contains("&uid=")) {
                        jump_url += "&uid=" + Utils.getUID(activity);
                    }
                    if (redirect_type == 1) {
                        activity.startActivity(new Intent(activity, AboutActivity.class).
                                putExtra("url", jump_url));
                    } else {
                        activity.startActivity(new Intent(activity, AboutActivity.class).
                                putExtra("url", jump_url)
                                .putExtra("style", "4"));
                    }
                } else if (jump_type == 1) {
                    intent.putExtra("OPTION", MIANFEI);
                    intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
                    startActivity(intent);
                } else if (jump_type == 2) {
                    intent.putExtra("OPTION", WANBEN);
                    intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_wanben));
                    startActivity(intent);
                } else if (jump_type == 3) {
                    /*intent.putExtra("OPTION", PAIHANGINSEX);
                    intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_paihang));
                    startActivity(intent);*/
                    startActivity(new Intent(activity, TopNewActivity.class).putExtra("PRODUCT", recommend_type == 0));
                } else if (jump_type == 4) {
                    int originCode = recommend_type == 0 ? 1 : 2;
                    Intent myIntent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), originCode);
                    myIntent.putExtra("isvip", Utils.isLogin(activity));
                    startActivity(myIntent);
                } else if (jump_type == 6) {
                    intent.putExtra("OPTION", SHUKU);
                    intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
                    startActivity(intent);
                } else if (jump_type == 7) {
                    activity.startActivity(new Intent(activity, TopYearBookActivity.class));
                } else if (jump_type == 8) {
                    activity.startActivity(new Intent(activity, TopYearComicActivity.class));
                } else if (jump_type == 9) {
                    activity.startActivity(new Intent(activity, MyShareActivity.class));
                } else if (jump_type == 10) {
                    if (recommend_type == 0) {
                        activity.startActivity(BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), recommeListBean.getBook_id()));
                    } else if (recommend_type == 1) {
                        activity.startActivity(ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), recommeListBean.getComic_id()));
                    } else {
                        activity.startActivity(CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), recommeListBean.getVideo_id()));
                    }
                } else if (jump_type == 12) {
                    EventBus.getDefault().post(new SkipToBoYinEvent(""));
                } else if (jump_type == 13) {
                    if (Utils.isLogin(activity)) {
                        String panda_game_link = ShareUitls.getString(activity, "game_link", "");
                        if (!TextUtils.isEmpty(panda_game_link)) {
                            activity.startActivity(new Intent(activity, AboutActivity.class).putExtra("url", panda_game_link));
                        }
                    } else {
                        MainHttpTask.getInstance().Gotologin(activity);
                    }
                } else if (jump_type == 14) {
                    if (Utils.isLogin(activity)) {
                        activity.startActivity(new Intent(activity, TaskCenterActivity.class));
                    } else {
                        MainHttpTask.getInstance().Gotologin(activity);
                    }
                }
            }
        });
    }

    protected void finishRefresh(boolean isResponse) {
        if (store_comic_refresh_layout.isRefreshing()) {
            store_comic_refresh_layout.setRefreshViewText(getString(isResponse ? R.string.refresh_succeed : R.string.refresh_fail));
            store_comic_refresh_layout.finishRefresh();
            //fragment_newbookself_top.setAlpha(1);
        } else {
            if (isEdit) {
                MyToash.ToashError(activity, getString(R.string.home_store_edit_data_refresh));
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(BuyLoginSuccessEvent buyLoginSuccessEvent) {
        getHomeAds();
    }


    private void getHomeAds() {
        getBannerData();
        getHomeRecommend();
    }

    public int getScollYDistance() {
        if (layoutManager != null) {
            int position = layoutManager.findFirstVisibleItemPosition();
            View firstVisiableChildView = layoutManager.findViewByPosition(position);
            int itemHeight = firstVisiableChildView.getHeight();
            return (position) * itemHeight - firstVisiableChildView.getTop();
        }
        return 0;
    }

    /**
     * ???????????????????????????????????????????????????
     */
    protected void getStockData(String kayCache, String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("channel_id", mChannelId);
        params.putExtraParams("page", "" + page);
        params.putExtraParams("limit", "4"); //??????4??????????????????
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(final String result) {
                if (!StringUtils.isEmpty(result)) {
                    try {
                        getSdkLableAd();//?????????????????????
                        setIsLoadMore(result);
                        JSONObject jsonObject = new JSONObject(result);
                        String edit_time = jsonObject.getString("max_edit_time");//??????????????????????????????
                        if (page == 1) {//??????????????????????????????????????????????????????
                            max_edit_time = edit_time;
                            ShareUitls.putMainHttpTaskString(activity, kayCache, result);
                            finishRefresh(true);
                        } else {
                            if (isLoadMore) {
                                boolean isEdit = !edit_time.equals(max_edit_time);
                                if (isEdit) {//??????????????????
                                    getEditData();
                                    return;
                                }
                            }
                            finishLoadmore(isLoadMore);
                        }
                        initInfo(result);
                    } catch (Exception e) {
                        finishLoadmore();
                        e.printStackTrace();
                    }
                } else {
                    if (page > 1) {
                        isLoadMore = false;
                    }
                }
            }

            @Override
            public void onErrorResponse(String ex) {
            }
        });
    }

    /**
     * ??????????????????????????????????????????????????????
     */
    protected void getCacheStockData(String kay) {
        String cacheData = "";
        if (activity != null) {
            cacheData = ShareUitls.getMainHttpTaskString(activity, kay, null);
        }
        if (!StringUtils.isEmpty(cacheData)) {
            initInfo(cacheData);
        }
    }

    /**
     * ?????????????????? ?????????????????????????????????????????????????????????
     */
    void getEditData() {
        recyclerView.scrollToPosition(0);
        page = 1;
        mFirstIndex = 0;
        getData();
        MyToash.ToashError(activity, getString(R.string.home_store_edit_data));
    }

    /**
     * ??????Banner?????????????????????
     */
    protected void getBannerData(String kayCache, String url, int recommendType) {
        localBannerAd(kayCache, url, recommendType);
    }

    private void sdkBannerAd(String kayCache, String url, String json, int flag) {
        String type;
        if (flag == 1) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_BANNER_NOVEL_DEBUG : BuildConfig.XAD_EVN_POS_BANNER_NOVEL;
        } else if (flag == 2) {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_BANNER_COMIC_DEBUG : BuildConfig.XAD_EVN_POS_BANNER_COMIC;
        } else {
            type = BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_BANNER_CARTOON_DEBUG : BuildConfig.XAD_EVN_POS_BANNER_CARTOON;
        }
        XRequestManager.INSTANCE.requestAd(activity, type, AdType.CUSTOM_TYPE_DEFAULT, 99, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    List<BannerItemStore> bannerItemStores = new ArrayList<>();
                    for (int i = 0; i < list.size(); i++) {
                        AdInfo adInfo = list.get(i);
                        //????????????????????????????????????id?????????
                        if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType()) && TextUtils.equals(mTopChannelId, adInfo.getMaterial().subtitle)) {
                            BannerItemStore bannerItemStore = new BannerItemStore();
                            bannerItemStore.setAdPosId(adInfo.getAdPosId());
                            bannerItemStore.setRequestId(adInfo.getRequestId());
                            bannerItemStore.setAdId(adInfo.getAdId());
                            bannerItemStore.setAction(3);
                            bannerItemStore.setContent(adInfo.getOperation().getValue());
                            bannerItemStore.setRedirect_type(String.valueOf(adInfo.getOperation().getType()));
                            bannerItemStore.setImage(adInfo.getMaterial().getImageUrl());
                            bannerItemStores.add(bannerItemStore);
                        }
                    }
                    List<BannerItemStore> localList = new ArrayList<>();
                    JsonParser jsonParser = new JsonParser();
                    JsonArray jsonElements = jsonParser.parse(json).getAsJsonArray();//??????JsonArray??????
                    for (JsonElement jsonElement : jsonElements) {
                        BannerItemStore bannerItemStore = gson.fromJson(jsonElement, BannerItemStore.class);//??????
                        localList.add(bannerItemStore);
                    }
                    localList.addAll(0, bannerItemStores);
                    String sdkJson = new Gson().toJson(localList);
                    if (!StringUtils.isEmpty(sdkJson)) {
                        ShareUitls.putMainHttpTaskString(activity, kayCache, sdkJson);
                        getHeaderView(sdkJson, flag);
                    }
                } catch (Exception e) {
                    getHeaderView(json, flag);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                getHeaderView(json, flag);
            }
        });
    }

    private void localBannerAd(String kayCache, String url, int flag) {
        ReaderParams params = new ReaderParams(activity);
        if (TextUtils.isEmpty(mTopChannelId)) {
            return;
        }
        if (flag == 1) {
            params.putExtraParams("book_channel_id", mTopChannelId);
        } else if (flag == 2) {
            params.putExtraParams("comic_channel_id", mTopChannelId);
        } else {
            params.putExtraParams("video_channel_id", mTopChannelId);
        }
        params.putExtraParams("channel_id", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!StringUtils.isEmpty(result)) {
                            ShareUitls.putMainHttpTaskString(activity, kayCache, result);
                            boolean isCartoonBannerSDK = false;
                            for (int i = 0; i < ReaderConfig.VIDEO_SDK_AD.size(); i++) {
                                AppUpdate.ListBean listBean = ReaderConfig.VIDEO_SDK_AD.get(i);
                                if (TextUtils.equals(listBean.getPosition(), "28") && TextUtils.equals(listBean.getSdk_switch(), "2")) {//??????banner
                                    isCartoonBannerSDK = true;
                                    break;
                                }
                            }
                            if (ReaderConfig.OTHER_SDK_AD.getBook_banner_index() == 2
                                    || ReaderConfig.OTHER_SDK_AD.getComic_banner_index() == 2 || isCartoonBannerSDK) {
                                sdkBannerAd(kayCache, url, result, flag);
                            } else {
                                getHeaderView(result, flag);
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * ??????Banner ????????????
     */
    protected void getCacheBannerData(String kay, int flag) {
        String cacheData = "";
        if (activity != null) {
            cacheData = ShareUitls.getMainHttpTaskString(activity, kay, null);
        }
        if (!StringUtils.isEmpty(cacheData)) {
            getHeaderView(cacheData, flag);
        }
    }

    /**
     * ??????????????????????????????
     */
    private boolean isLoadMore() {
        return isLoadMore;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * ?????????????????????????????????????????????????????????????????????label?????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????
     *
     * @param json
     */
    private void setIsLoadMore(String json) {
        if (!StringUtils.isEmpty(json)) {
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(json);
                JSONArray jsonArray = jsonObject.getJSONArray("label");
                if (jsonArray.length() <= 0 && page > 1) {
                    isLoadMore = false;
                } else {
                    isLoadMore = true;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * ???????????????loading
     */
    private void finishLoadmore() {
        store_comic_refresh_layout.finishRefresh();
        store_comic_refresh_layout.finishLoadmore();
    }

    /**
     * ???????????????loading
     *
     * @param isResponse ?????????????????????
     */
    protected void finishLoadmore(boolean isResponse) {
        if (isAdded()) {
            store_comic_refresh_layout.setRefreshViewText(getString(isResponse ? R.string.load_succeed : R.string.load_fail));
            finishLoadmore();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    /**
     * ???????????? ???????????????
     * ????????????????????????????????????????????????ID?????????
     *
     * @param works_type ?????? MH ??????XS
     * @param column_id  ??????ID
     */
    protected void setHomeRecommendationEvent(String works_type, List<String> column_id) {
        try {
            SensorsDataHelper.setHomeRecommendationEvent(works_type, column_id);
        } catch (Exception e) {
        }
    }
}