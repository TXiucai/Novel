package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.CountryAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.CountryBean;
import com.heiheilianzai.app.utils.HttpUtils;
import com.othershe.groupindexlib.decoration.DivideItemDecoration;
import com.othershe.groupindexlib.decoration.GroupHeaderItemDecoration;
import com.othershe.groupindexlib.helper.SortHelper;
import com.othershe.groupindexlib.listener.OnSideBarTouchListener;
import com.othershe.groupindexlib.weiget.SideBar;

import org.json.JSONException;

import java.util.List;

import butterknife.BindView;

public class CountryActivity extends BaseButterKnifeActivity {
    @BindView(R.id.ry_country)
    public RecyclerView mRyCountry;
    @BindView(R.id.tx_tip)
    public TextView mTxTip;
    @BindView(R.id.side_bar)
    public SideBar mSideBar;
    @BindView(R.id.ll_close)
    public LinearLayout mLlClose;

    @Override
    public int initContentView() {
        return R.layout.activity_country;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        mLlClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //获取数据
    private void initData() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = App.getBaseUrl() + ReaderConfig.mCountryCode;
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                CountryBean countryBean = new Gson().fromJson(response, CountryBean.class);
                List<CountryBean.ListBean> countryBeanList = countryBean.getList();
                initTags(countryBeanList);
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });

    }

    private void initTags(List<CountryBean.ListBean> countryBeanList) {
        SortHelper<CountryBean.ListBean> sortHelper = new SortHelper<CountryBean.ListBean>() {
            @Override
            public String sortField(CountryBean.ListBean data) {
                return data.getCountry_name();
            }
        };
        sortHelper.sortByLetter(countryBeanList);
        final List<String> tags = sortHelper.getTags(countryBeanList);

        CountryAdapter adapter = new CountryAdapter(this, countryBeanList);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRyCountry.setLayoutManager(layoutManager);

        mRyCountry.addItemDecoration(new GroupHeaderItemDecoration(this)
                .setTags(tags)
                .setGroupHeaderHeight(30)
                .setGroupHeaderLeftPadding(20));
        mRyCountry.addItemDecoration(new DivideItemDecoration().setTags(tags));
        mRyCountry.setAdapter(adapter);

        mSideBar.setOnSideBarTouchListener(tags, new OnSideBarTouchListener() {
            @Override
            public void onTouch(String text, int position) {
                mTxTip.setVisibility(View.VISIBLE);
                mTxTip.setText(text);
                if ("↑".equals(text)) {
                    layoutManager.scrollToPositionWithOffset(0, 0);
                    return;
                }
                if (position != -1) {
                    layoutManager.scrollToPositionWithOffset(position, 0);
                }
            }

            @Override
            public void onTouchEnd() {
                mTxTip.setVisibility(View.GONE);
            }
        });
        adapter.setOnItemListener(new CountryAdapter.OnItemListener() {
            @Override
            public void onItemListener(CountryBean.ListBean listBean, int position) {
                setResult(1, new Intent().putExtra("code", listBean.getCounter_code()));
                finish();
            }
        });
    }
}
