package com.heiheilianzai.app.base;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.core.view.MotionEventCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.HomeRecommendAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.model.HomeRecommendBean;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.MyShareActivity;
import com.heiheilianzai.app.ui.activity.TopActivity;
import com.heiheilianzai.app.ui.activity.TopYearBookActivity;
import com.heiheilianzai.app.ui.activity.TopYearComicActivity;
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
import com.heiheilianzai.app.utils.ViewUtils;
import com.heiheilianzai.app.view.ConvenientBanner;
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

import static com.heiheilianzai.app.constant.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.constant.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.constant.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.constant.ReaderConfig.WANBEN;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

/**
 * 首页小说，首页漫画内容基类。
 * max_edit_time说明：后台会改变数据位置。加载更多的时候根据第一页加载的max_edit_time 与服务器返回的时间戳比较。
 * 如果相等数据没有改变，如果不相等设置page=1刷新数据，清空原有数据。
 * label_total说明：推荐位总条数，根据page=1时为准。
 *
 * @param <T>
 */
public abstract class BaseHomeStoreFragment<T> extends BaseButterKnifeFragment {
    @BindView(R.id.store_comic_refresh_layout)
    public SHSwipeRefreshLayout store_comic_refresh_layout;//该控件支持自定义动画但不支持自动刷新，待替换。
    @BindView(R.id.fragment_store_comic_content_commend)
    public RecyclerView recyclerView;
    protected RelativeLayout fragment_newbookself_top;
    protected StroeNewFragment.MyHotWord hot_word;
    protected boolean postAsyncHttpEngine_ing;//正在刷新数据
    protected Gson gson = new Gson();
    protected List<T> listData = new ArrayList<>();
    protected RecyclerView.Adapter adapter;
    protected SmartRecyclerAdapter smartRecyclerAdapter;
    protected LinearLayoutManager layoutManager;
    protected View headerView;
    boolean isScollYspill = false;
    int page = 1;//分页页数
    String max_edit_time;//推荐位最后编辑时间
    boolean isEdit = false;//后台是否修改了推荐列表数据
    boolean isLoadMore = true;//是否加载更多
    int mFirstIndex = -1;//上次列表滚动到的位置

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
        EventBus.getDefault().register(this);
        headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        fragment_newbookself_top = ((StroeNewFragment) getParentFragment()).fragment_newbookself_top;
        hot_word = ((StroeNewFragment) getParentFragment()).myHotWord;
        fragment_newbookself_top.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.white));
        layoutManager = new LinearLayoutManager(getContext());
        smartRecyclerAdapter = new SmartRecyclerAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(smartRecyclerAdapter);
        //暂时注释滑动状态栏改变
        /*recyclerView.setOnTouchListener(new View.OnTouchListener() {
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
        });*/
      /*  recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    // 获取当前滚动到的条目位置
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
        });*/
        store_comic_refresh_layout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mFirstIndex = 0;
                store_comic_refresh_layout.setLoadmoreEnable(true);
                isLoadMore = true;
                getData();//刷新banner、推荐列表
            }

            @Override
            public void onLoading() {
                if (isLoadMore()) {
                    page += 1;
                    getStockData();//推荐列表
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

    public void getHeaderView(String result) {
        if (headerView == null) {
            headerView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_store_comic_content_head, null);
        }
        ConvenientBanner<BannerItemStore> mStoreBannerMale = headerView.findViewById(R.id.store_banner_male);
        ViewGroup.LayoutParams layoutParams = mStoreBannerMale.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        layoutParams.height = ScreenSizeUtils.getInstance(activity).getScreenWidth() * 3 / 5;
        ConvenientBanner.initbanner(activity, gson, result, mStoreBannerMale, 5000, 1);
        RecyclerView recyclerView = headerView.findViewById(R.id.ry_recommend);
        getHomeRecommend(recyclerView);
        smartRecyclerAdapter.setHeaderView(headerView);
    }

    public void initWaterfall(String jsonObject, Type typeOfT) {
        if (!StringUtils.isEmpty(jsonObject)) {
            if (page == 1) {
                listData.clear();
            }
            listData.addAll(gson.fromJson(jsonObject, typeOfT));
            adapter.notifyDataSetChanged();
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

    protected abstract void onMyScrollStateChanged(int position);//停止滑动后列表位置

    protected abstract void getHomeRecommend(RecyclerView recyclerView);

    protected void getHomeRecommend(RecyclerView recyclerView, int recommendType) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_type", String.valueOf(recommendType));//男频
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(App.getBaseUrl() + ReaderConfig.mHomeRecomment, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                HomeRecommendBean homeRecommendBean = new Gson().fromJson(response, HomeRecommendBean.class);
                List<HomeRecommendBean.RecommeListBean> recomme_list = homeRecommendBean.getRecomme_list();
                initRecommend(recyclerView, recomme_list);
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });
    }

    private void initRecommend(RecyclerView recyclerView, List<HomeRecommendBean.RecommeListBean> recomme_list) {
        recyclerView.setLayoutManager(new GridLayoutManager(activity, 5));
        HomeRecommendAdapter homeRecommendAdapter = new HomeRecommendAdapter(activity, recomme_list);
        recyclerView.setAdapter(homeRecommendAdapter);
        homeRecommendAdapter.setOnItemRecommendListener(new HomeRecommendAdapter.OnItemRecommendListener() {
            @Override
            public void onItemRecommendListener(HomeRecommendBean.RecommeListBean recommeListBean) {
                int jump_type = Integer.valueOf(recommeListBean.getJump_type());//0跳转链接 1首页-推荐页   2首页完结页  3首页-榜单页  4VIP充值页   5活动中心
                String jump_url = recommeListBean.getJump_url();
                int recommend_type = Integer.valueOf(recommeListBean.getRecommend_type());//默认为0  小说   1为漫画',
                int redirect_type = Integer.valueOf(recommeListBean.getRedirect_type());//默认为0  内置应用  1为内置浏览器   2为外部浏览器
                Intent intent = new Intent(activity, BaseOptionActivity.class);
                intent.putExtra("PRODUCT", recommend_type == 0);
                if (jump_type == 0 || jump_type == 5) {
                    if (Utils.isLogin(activity)) {
                        jump_url += "&uid=" + Utils.getUID(activity);
                    }
                    if (redirect_type == 0) {
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
                    startActivity(new Intent(activity, TopActivity.class).putExtra("PRODUCT", recommend_type == 0));
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

    /**
     * 加载推荐位列表数据，缓存第一页数据
     */
    protected void getStockData(String kayCache, String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("channel_id", "1");//男频
        params.putExtraParams("page", "" + page);
        params.putExtraParams("limit", "4"); //返回4条（已协商）
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(final String result) {
                if (!StringUtils.isEmpty(result)) {
                    try {
                        setIsLoadMore(result);
                        JSONObject jsonObject = new JSONObject(result);
                        String edit_time = jsonObject.getString("max_edit_time");//获取服务器修改时间戳
                        if (page == 1) {//第一页保存修改时间戳，保存列表总条数
                            max_edit_time = edit_time;
                            ShareUitls.putMainHttpTaskString(activity, kayCache, result);
                            finishRefresh(true);
                        } else {
                            if (isLoadMore) {
                                boolean isEdit = !edit_time.equals(max_edit_time);
                                if (isEdit) {//编辑了推荐位
                                    getEditData();
                                    return;
                                }
                            }
                            finishLoadmore(isLoadMore);
                        }
                        initInfo(result);
                    } catch (Exception e) {
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
                if (page == 1) {
                    getCacheStockData();
                    finishRefresh(false);
                } else {
                    finishLoadmore(false);
                }
            }
        });
    }

    /**
     * 加载推荐位列表缓存数据（第一页缓存）
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
     * 加载编辑数据 如果后台编辑了数据，返回顶部加载第一页
     */
    void getEditData() {
        recyclerView.scrollToPosition(0);
        page = 1;
        mFirstIndex = 0;
        getData();
        MyToash.ToashError(activity, getString(R.string.home_store_edit_data));
    }

    /**
     * 加载Banner数据并写入缓存
     */
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
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        getCacheBannerData();
                    }
                }
        );
    }

    /**
     * 加载Banner 缓存数据
     */
    protected void getCacheBannerData(String kay) {
        String cacheData = "";
        if (activity != null) {
            cacheData = ShareUitls.getMainHttpTaskString(activity, kay, null);
        }
        if (!StringUtils.isEmpty(cacheData)) {
            getHeaderView(cacheData);
        }
    }

    /**
     * 是否可以加载更多数据
     */
    private boolean isLoadMore() {
        return isLoadMore;
    }

    /**
     * 因为现在后台返回的推荐数据总数，是实际返回数据不匹配。服务器那里暂时处理不了。
     * 经过和服务器商议用：数据返回成功，非第一页，且label数据为空时。为无更多数据加载。
     * 根据返回数据判断是否是第一页，是否还有更多数据加载。
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
     * 关闭加更多loading
     */
    private void finishLoadmore() {
        store_comic_refresh_layout.finishRefresh();
        store_comic_refresh_layout.finishLoadmore();
    }

    /**
     * 关闭加更多loading
     *
     * @param isResponse 是否有数据返回
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
     * 神策埋点 首页推荐位
     * 每次滑动列表位置改变，上报推荐位ID到神策
     *
     * @param works_type 漫画 MH 小说XS
     * @param column_id  推荐ID
     */
    protected void setHomeRecommendationEvent(String works_type, List<String> column_id) {
        try {
            SensorsDataHelper.setHomeRecommendationEvent(works_type, column_id);
        } catch (Exception e) {
        }
    }
}