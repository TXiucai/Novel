package com.heiheilianzai.app.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ShareRecordAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;

import butterknife.BindView;


/**
 * 个人中心-意见反馈
 * Created by scb on 2018/7/14.
 */
public class ShareRecordActivity extends BaseActivity implements ShowTitle {

    @BindView(R.id.recycler)
    RecyclerView mRecycleView;
    @BindView(R.id.titlebar_finish)
    TextView mTitlebar;

    private ShareRecordAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_share_record;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.ShareActivity_title));
        mTitlebar.setVisibility(View.GONE);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this,  LinearLayoutManager.VERTICAL, false));
        mAdapter = new ShareRecordAdapter(this);
        mRecycleView.setAdapter(mAdapter);
    }

    @Override
    public void initData() {
    }

    /**
     * 发请求
     */

    public void addFeedback() {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mFeedbackUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                        initInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );

    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(v -> finish());
        mTitle.setText(text);
    }
}