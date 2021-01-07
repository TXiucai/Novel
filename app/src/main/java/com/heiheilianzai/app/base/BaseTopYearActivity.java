package com.heiheilianzai.app.base;

import android.view.LayoutInflater;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.headerfooter.songhang.library.SmartRecyclerAdapter;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.scu.miomin.shswiperefresh.core.SHSwipeRefreshLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public abstract class BaseTopYearActivity<T> extends BaseActivity{
    @BindView(R.id.store_comic_refresh_layout)
    public SHSwipeRefreshLayout store_comic_refresh_layout;//该控件支持自定义动画但不支持自动刷新，待替换。
    @BindView(R.id.fragment_store_comic_content_commend)
    public RecyclerView recyclerView;
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
    public void initView() {
        initViews();
    }

    @Override
    public void initData() {
        getCacheData();
        getData();
    }

    protected void initViews() {
        headerView = LayoutInflater.from(this).inflate(R.layout.top_year_image_head, null);
        layoutManager = new LinearLayoutManager(this);
        smartRecyclerAdapter = new SmartRecyclerAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(smartRecyclerAdapter);
        smartRecyclerAdapter.setHeaderView(headerView);
        store_comic_refresh_layout.setOnRefreshListener(new SHSwipeRefreshLayout.SHSOnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                mFirstIndex = 0;
                store_comic_refresh_layout.setLoadmoreEnable(true);
                isLoadMore = true;
                getData();
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
        getStockData();
    }

    protected void getCacheData() {
        getCacheStockData();
    }


    protected abstract void getStockData();

    protected abstract void getCacheStockData();

    protected abstract void dealData(String data);

    protected abstract void initWaterfall(String jsonObject);

    /**
     * 加载推荐位列表数据，缓存第一页数据
     */
    protected void getStockData(String kayCache, String url) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("channel_id", "1");//男频
        params.putExtraParams("page", "" + page);
        params.putExtraParams("limit", "4"); //返回4条（已协商）
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(final String result) {
                if (!StringUtils.isEmpty(result)) {
                    try {
                        setIsLoadMore(result);
                        JSONObject jsonObject = new JSONObject(result);
                        String edit_time = jsonObject.getString("max_edit_time");//获取服务器修改时间戳
                        if (page == 1) {//第一页保存修改时间戳，保存列表总条数
                            max_edit_time = edit_time;
                            ShareUitls.putMainHttpTaskString(BaseTopYearActivity.this, kayCache, result);
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
                        dealData(result);
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
     * 加载编辑数据 如果后台编辑了数据，返回顶部加载第一页
     */
    void getEditData() {
        recyclerView.scrollToPosition(0);
        page = 1;
        mFirstIndex = 0;
        getData();
        MyToash.ToashError(this, getString(R.string.home_store_edit_data));
    }

    /**
     * 加载推荐位列表缓存数据（第一页缓存）
     */
    protected void getCacheStockData(String kay) {
        String cacheData = "";
        if (this != null) {
            cacheData = ShareUitls.getMainHttpTaskString(this, kay, null);
        }
        if (!StringUtils.isEmpty(cacheData)) {
            initInfo(cacheData);
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
        store_comic_refresh_layout.setRefreshViewText(getString(isResponse ? R.string.load_succeed : R.string.load_fail));
        finishLoadmore();
    }

    protected void finishRefresh(boolean isResponse) {
        if (store_comic_refresh_layout.isRefreshing()) {
            store_comic_refresh_layout.setRefreshViewText(getString(isResponse ? R.string.refresh_succeed : R.string.refresh_fail));
            store_comic_refresh_layout.finishRefresh();
        } else {
            if (isEdit) {
                MyToash.ToashError(this, getString(R.string.home_store_edit_data_refresh));
            }
        }
    }
}
