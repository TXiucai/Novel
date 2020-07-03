package com.heiheilianzai.app.fragment;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.BaseOptionActivity;
import com.heiheilianzai.app.adapter.EntranceAdapter;
import com.heiheilianzai.app.adapter.ReaderBaseAdapter;
import com.heiheilianzai.app.banner.ConvenientBanner;
import com.heiheilianzai.app.bean.BannerItemStore;
import com.heiheilianzai.app.bean.EntranceItem;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.BuyLoginSuccess;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.ViewUtils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.config.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.config.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.config.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.config.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.config.ReaderConfig.WANBEN;

public abstract class BaseHomeStoreFragment<T> extends BaseButterKnifeFragment {
    @BindView(R2.id.store_comic_refresh_layout)
    public SHSwipeRefreshLayout store_comic_refresh_layout;
    @BindView(R2.id.fragment_store_comic_content_commend)
    public RecyclerView recyclerView;

    protected RelativeLayout fragment_newbookself_top;
    protected boolean postAsyncHttpEngine_ing;//正在刷新数据
    protected Gson gson = new Gson();
    protected List<T> listData = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected SmartRecyclerAdapter smartRecyclerAdapter;
    protected LinearLayoutManager layoutManager;
    protected View headerView;
    boolean isScollYspill = false;

    @Override
    protected void initView() {
        initViews();
    }

    @Override
    protected void initData() {
        getData();
        getCacheData();
    }

    protected void initViews() {
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        fragment_newbookself_top.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        layoutManager = new LinearLayoutManager(getContext());
        smartRecyclerAdapter = new SmartRecyclerAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(smartRecyclerAdapter);
        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = MotionEventCompat.getActionMasked(event);
                switch (action) {
                    case MotionEvent.ACTION_UP://上拉加载时如果未触发加载还原头部透明度
                        if (ViewUtils.getViewAlpha(fragment_newbookself_top) > 0.0) {
                            fragment_newbookself_top.setAlpha(1);
                        }
                        break;
                }
                return false;
            }
        });
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                float scollY = getScollYDistance();
                if (scollY <= 100) {//下滑时根据高度改变头部搜索背景透明度
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
        });
        store_comic_refresh_layout.setLoadmoreEnable(false);
        store_comic_refresh_layout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                getData();
            }

            @Override
            public void onLoading() {
            }

            @Override
            public void onRefreshPulStateChange(float percent, int state) {
                switch (state) {
                    case SHSwipeRefreshLayout.NOT_OVER_TRIGGER_POINT:
                        store_comic_refresh_layout.setRefreshViewText(getString(R.string.pull_to_refresh));
                        fragment_newbookself_top.setAlpha(1.0f - percent);
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
            }
        });
    }

    public void getHeaderView(String result) {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        }
        ConvenientBanner<BannerItemStore> mStoreBannerMale = headerView.findViewById(R.id.store_banner_male);
        ConvenientBanner.initbanner(activity, gson, result, mStoreBannerMale, 5000, 1);
        AdaptionGridView mEntranceGridMale = headerView.findViewById(R.id.store_entrance_grid_male);
        initEntranceGrid(mEntranceGridMale);
        smartRecyclerAdapter.setHeaderView(headerView);
    }

    public void initWaterfall(String jsonObject, Type typeOfT) {
        if (!StringUtils.isEmpty(jsonObject)) {
            listData.clear();
            listData.addAll(gson.fromJson(jsonObject, typeOfT));//
            smartRecyclerAdapter.notifyDataSetChanged();
        }
    }

    protected void getData() {
        getBannerData();
        getStockData();
    }

    protected void getCacheData() {
        getCacheBannerData();
        getCacheStockData();
    }

    protected abstract void getBannerData();

    protected abstract void getStockData();

    protected abstract void getCacheBannerData();

    protected abstract void getCacheStockData();

    protected abstract void initInfo(String data);

    protected abstract void initWaterfall(String jsonObject);

    protected abstract void postEvent(float alpha);

    protected abstract void initEntranceGrid(AdaptionGridView mEntranceGridMale);

    /**
     *
     * @param mEntranceGridMale
     * @param  isProduct 参考 {@link BaseOptionActivity.PRODUCT } isProduct false 漫画  true  小说
     * @param resId1
     * @param resId2
     * @param resId3
     * @param resId4
     * @param resId5
     */
    protected void initEntranceGrid(AdaptionGridView mEntranceGridMale,boolean isProduct,int resId1,int resId2,int resId3,int resId4,int resId5){
        List<EntranceItem> mEntranceItemListMale = new ArrayList<>();
        if (ReaderConfig.USE_PAY) {
            EntranceItem entranceItem1 = new EntranceItem();
            entranceItem1.setName(LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
            entranceItem1.setResId(resId1);
            EntranceItem entranceItem2 = new EntranceItem();
            entranceItem2.setName(LanguageUtil.getString(activity, R.string.storeFragment_paihang));
            entranceItem2.setResId(resId2);
            EntranceItem entranceItem3 = new EntranceItem();
            entranceItem3.setName(LanguageUtil.getString(activity, R.string.storeFragment_baoyue));
            entranceItem3.setResId(resId3);
            EntranceItem entranceItem4 = new EntranceItem();
            entranceItem4.setName(LanguageUtil.getString(activity, R.string.storeFragment_wanben));
            entranceItem4.setResId(resId4);
            EntranceItem entranceItem5 = new EntranceItem();
            entranceItem5.setName(LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
            entranceItem5.setResId(resId5);
            mEntranceItemListMale.add(entranceItem5);
            mEntranceItemListMale.add(entranceItem4);
            mEntranceItemListMale.add(entranceItem1);
            mEntranceItemListMale.add(entranceItem2);
            if (!BuildConfig.free_charge) {
                mEntranceItemListMale.add(entranceItem3);
            } else {
                mEntranceGridMale.setNumColumns(4);
            }
        } else {
            EntranceItem entranceItem5 = new EntranceItem();
            entranceItem5.setName(LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
            entranceItem5.setResId(resId5);
            mEntranceItemListMale.add(entranceItem5);
            EntranceItem entranceItem1 = new EntranceItem();
            entranceItem1.setName(LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
            entranceItem1.setResId(resId1);
            EntranceItem entranceItem2 = new EntranceItem();
            entranceItem2.setName(LanguageUtil.getString(activity, R.string.storeFragment_paihang));
            entranceItem2.setResId(resId2);
            EntranceItem entranceItem4 = new EntranceItem();
            entranceItem4.setName(LanguageUtil.getString(activity, R.string.storeFragment_wanben));
            entranceItem4.setResId(resId4);
            mEntranceItemListMale.add(entranceItem1);
            mEntranceItemListMale.add(entranceItem2);
            mEntranceItemListMale.add(entranceItem4);
            mEntranceGridMale.setNumColumns(4);
        }
        ReaderBaseAdapter entranceAdapter = new EntranceAdapter(activity, mEntranceItemListMale, mEntranceItemListMale.size());
        mEntranceGridMale.setAdapter(entranceAdapter);
        mEntranceGridMale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, BaseOptionActivity.class);
                intent.putExtra("PRODUCT", isProduct);
                if (!ReaderConfig.USE_PAY) {
                    if (position == 0) {
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
                    } else if (position == 1) {//分类
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
                    } else if (position == 2) {
                        //排行
                        intent.putExtra("OPTION", PAIHANGINSEX);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_paihang));
                    } else if (position == 3) {
                        //完本
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_wanben));
                    }
                } else {
                    MyToash.Log("position", position + "");
                    if (position == 0) {
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
                    } else if (position == 1) {
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_wanben));
                    } else if (position == 2) {
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
                    } else if (position == 3) {
                        intent.putExtra("OPTION", PAIHANGINSEX);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_paihang));
                    } else if (position == 4) {
                        intent.putExtra("OPTION", BAOYUE);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.BaoyueActivity_title));
                    }
                }
                startActivity(intent);
            }
        });
    }

    protected void finishRefresh() {
        if (store_comic_refresh_layout.isRefreshing()) {
            store_comic_refresh_layout.setRefreshViewText(getString(R.string.refresh_succeed));
            store_comic_refresh_layout.finishRefresh();
            fragment_newbookself_top.setAlpha(1);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(BuyLoginSuccess buyLoginSuccess) {
        getBannerData();
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

    protected void getStockData(String kayCache, String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("channel_id", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(final String result) {
                if (!StringUtils.isEmpty(result)) {
                    ShareUitls.putMainHttpTaskString(activity, kayCache, result);
                    initInfo(result);
                }
                finishRefresh();
            }

            @Override
            public void onErrorResponse(String ex) {
                getCacheStockData();
                finishRefresh();
            }
        });
    }

    protected void getCacheStockData(String kay) {
        String cacheData = ShareUitls.getMainHttpTaskString(activity, kay, null);
        if (!StringUtils.isEmpty(cacheData)) {
            initInfo(cacheData);
        }
    }

    protected void getBannerData(String kayCache, String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("channel_id", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!StringUtils.isEmpty(result)) {
                            ShareUitls.putMainHttpTaskString(activity, kayCache, result);
                            getHeaderView(result);
                        }
                        finishRefresh();
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        getCacheBannerData();
                        finishRefresh();
                    }
                }
        );
    }

    protected void getCacheBannerData(String kay) {
        String cacheData = ShareUitls.getMainHttpTaskString(activity, kay, null);
        if (!StringUtils.isEmpty(cacheData)) {
            getHeaderView(cacheData);
        }
    }
}
