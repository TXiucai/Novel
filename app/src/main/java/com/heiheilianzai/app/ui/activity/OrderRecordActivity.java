package com.heiheilianzai.app.ui.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
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
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.itemdiv.VerticalItemDecoration;
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

        srl_refresh.setEnableRefresh(true);
        srl_refresh.setOnRefreshListener(this);
        srl_refresh.setEnableLoadMore(false);
        srl_refresh.setEnableOverScrollDrag(false);

        recyclerView.setLayoutManager(new LinearLayoutManager(OrderRecordActivity.this));
        orderRecordAdapter = new OrderRecordAdapter(OrderRecordActivity.this, new ArrayList<>());
        recyclerView.setAdapter(orderRecordAdapter);
        recyclerView.addItemDecoration(new VerticalItemDecoration(OrderRecordActivity.this, 10));

        initEvent();
    }

    private void initEvent() {
        rl_order_back.setOnClickListener(view -> OrderRecordActivity.this.finish());

        orderRecordAdapter.setOrderRecordKeFuListener(new OrderRecordAdapter.OrderRecordListener() {
            @Override
            public void goKeFuOnline(String kefuUrl) {
                if (!TextUtils.isEmpty(kefuUrl)) {
                    startActivity(new Intent(OrderRecordActivity.this, AboutActivity.class).putExtra("url", kefuUrl).putExtra("flag", "notitle"));
                }
            }

            @Override
            public void goPay(String goodsId) {
                Intent intentVip = new Intent(OrderRecordActivity.this,AcquireBaoyueActivity.class);
                intentVip.putExtra("goodsId", Integer.valueOf(goodsId));
                startActivity(intentVip);
                finish();
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
                        srl_refresh.finishRefresh();
                        srl_refresh.finishLoadMore();
                        try {
                            List<OrderRecordBean> list = new Gson().fromJson(result, new TypeToken<List<OrderRecordBean>>() {
                            }.getType());
                            OrderRecordActivity.this.runOnUiThread(() -> adapterView(list));
                        } catch (Exception e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        OrderRecordActivity.this.runOnUiThread(() -> adapterInvisibility());
                    }
                }
        );

    }

    @SuppressLint("NotifyDataSetChanged")
    private void adapterView(List<OrderRecordBean> list) {
        if (list != null && list.size() > 0) {
            orderRecordAdapter.setNewData(list);
            order_no_data.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            orderRecordAdapter.notifyDataSetChanged();
        } else {
            adapterInvisibility();
        }
    }

    private void adapterInvisibility() {
        orderRecordAdapter.setNewData(null);
        order_no_data.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onRefresh(@NonNull RefreshLayout refreshLayout) {
        srl_refresh.finishRefresh(2000);
        initData();
    }

    @Override
    public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
        srl_refresh.finishLoadMore(2000);
        initData();
    }

}
