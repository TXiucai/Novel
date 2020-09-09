package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.LiushuijiluRecyclerViewAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.PayGoldDetail;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class LiushuijiluFragment extends BaseButterKnifeFragment {


    @Override
    public int initContentView() {
        return R.layout.fragment_option;
    }


    @BindView(R2.id.fragment_option_noresult)
    public LinearLayout fragment_option_noresult;

    /*   @BindView(R2.id.fragment_option_listview)
       public PullToRefreshListView fragment_option_listview;
       */
    @BindView(R2.id.fragment_option_listview)
    public XRecyclerView fragment_option_listview;

    Gson gson = new Gson();
    int total_page, current_page = 1;
    boolean LoadingListener;
    LinearLayout temphead;
    LayoutInflater layoutInflater;

    String unit;
    private List<PayGoldDetail> optionBeenList;
    private LiushuijiluRecyclerViewAdapter optionAdapter;

    @SuppressLint("ValidFragment")
    public LiushuijiluFragment(String unit) {

        this.unit = unit;

    }

    public LiushuijiluFragment() {
    }


    private void initHttpUrl() {
        optionBeenList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(activity);
        temphead = (LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null);

        optionAdapter = new LiushuijiluRecyclerViewAdapter(activity, optionBeenList, layoutInflater, null);
        fragment_option_listview.setAdapter(optionAdapter);


        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_option_listview.setLayoutManager(layoutManager);

        fragment_option_listview.addHeaderView(temphead);

        HttpData();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        fragment_option_listview.setPullRefreshEnabled(false);
        fragment_option_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadMore() {
                LoadingListener = true;
                HttpData();
            }
        });


        initHttpUrl();
    }

    int Size;

    private void initInfo(String result) {
        try {
            OptionItem optionItem = gson.fromJson(result, OptionItem.class);
            total_page = optionItem.total_page;
            MyToash.Log("optionBeenList22", current_page + "   " + optionBeenList.size() + "");
            int optionItem_list_size = optionItem.list.size();
            if (current_page <= total_page && optionItem_list_size != 0) {
                MyToash.Log("optionBeenList33", current_page + "   " + optionBeenList.size() + "");

                if (current_page == 1) {
                    optionBeenList.clear();
                    optionBeenList.addAll(optionItem.list);
                    Size = optionItem_list_size;
                    optionAdapter.notifyDataSetChanged();
                } else {
                    MyToash.Log("optionBeenList44", current_page + "   " + optionBeenList.size() + "");
                    optionBeenList.addAll(optionItem.list);
                    int t = Size + optionItem_list_size;
                    optionAdapter.notifyItemRangeInserted(Size + 2, optionItem_list_size);
                    Size = t;

                    // optionAdapter.notifyDataSetChanged();
                }
                MyToash.Log("optionBeenList55", current_page + "   " + optionBeenList.size() + "");
                current_page = optionItem.current_page;
                ++current_page;

            } else {
                if (optionBeenList.isEmpty()) {
                    fragment_option_noresult.setVisibility(View.VISIBLE);
                }
                if(current_page>1) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        }catch (Exception e){
            if (optionBeenList.isEmpty()) {
                fragment_option_noresult.setVisibility(View.VISIBLE);
            }
            if(current_page>1) {
                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
            }
        }

    }


    private void HttpData() {

        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("unit", unit);
        params.putExtraParams("page_num", current_page + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl()+ReaderConfig.mPayGoldDetailUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initInfo(result);
                        if (LoadingListener) {
                            LoadingListener = false;
                            fragment_option_listview.loadMoreComplete();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        if (LoadingListener) {
                            LoadingListener = false;
                            fragment_option_listview.loadMoreComplete();
                        }
                    }
                }

        );
    }


    class OptionItem {
        public int total_page;//": 3,
        public int current_page;//": 2,
        public int page_size;//": 2,
        public int total_count;//,
        public List<PayGoldDetail> list;

        @Override
        public String toString() {
            return "DiscoveryItem{" +
                    "total_page=" + total_page +
                    ", current_page=" + current_page +
                    ", page_size=" + page_size +
                    ", total_count=" + total_count +
                    ", list=" + list +
                    '}';
        }
    }

}
