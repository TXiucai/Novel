package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.OrderRecordAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.OrderRecordBean;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.listener.OnLoadMoreListener;
import com.scwang.smart.refresh.layout.listener.OnRefreshListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class OrderRecordActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {

    @BindView(R.id.rl_order_back)
    public LinearLayout rl_order_back;
    @BindView(R.id.srl_refresh)
    public SmartRefreshLayout srl_refresh;
    @BindView(R.id.recyclerView)
    public RecyclerView recyclerView;
    @BindView(R.id.order_no_data)
    public RelativeLayout order_no_data;

    OrderRecordAdapter orderRecordAdapter;

    @Override
    public int initContentView() {
        return R.layout.fragment_order_record;
    }

    @Override
    public void initView() {

        srl_refresh.setOnRefreshListener(this);
//        srl_refresh.setOnLoadMoreListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(OrderRecordActivity.this));
        orderRecordAdapter = new OrderRecordAdapter(OrderRecordActivity.this, new ArrayList<>());
        recyclerView.setAdapter(orderRecordAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(OrderRecordActivity.this, DividerItemDecoration.VERTICAL));

        initEvent();
    }

    private void initEvent() {
        rl_order_back.setOnClickListener(view -> OrderRecordActivity.this.finish());

        orderRecordAdapter.setOrderRecordKeFuListener(kefuUrl -> {
            if (!TextUtils.isEmpty(kefuUrl)) {
                startActivity(new Intent(OrderRecordActivity.this, AboutActivity.class).putExtra("url", kefuUrl).putExtra("flag", "notitle"));
            }
        });

    }

    /**
     * 获取历史订单详情
     */
    @Override
    public void initData() {
        ReaderParams params = new ReaderParams(OrderRecordActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.USER_ORDER_RECORD, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            List<OrderRecordBean> list = new Gson().fromJson(result, new TypeToken<List<OrderRecordBean>>() {
                            }.getType());
                            adapterView(list);
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );

    }

    private void adapterView(List<OrderRecordBean> list) {
        if (list != null && list.size() > 0) {
            orderRecordAdapter.setNewData(list);
            order_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            orderRecordAdapter.notifyDataSetChanged();
        } else {
            order_no_data.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        srl_refresh.finishRefresh(2000);
        srl_refresh.setEnableLoadMore(true);
        initData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        srl_refresh.finishLoadMore(2000);
        initData();
    }

}
