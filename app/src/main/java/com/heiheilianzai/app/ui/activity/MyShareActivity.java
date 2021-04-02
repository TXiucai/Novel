package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
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
public class MyShareActivity extends BaseActivity implements ShowTitle {

    @BindView(R.id.tv_share_friends)
    TextView mTvShareFriends;
    @BindView(R.id.titlebar_finish)
    TextView mTitlebarFinish;
    @BindView(R.id.tv_link)
    TextView mTvLink;

    @Override
    public int initContentView() {
        return R.layout.activity_myshare;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.ShareActivity_title));
        mTitlebarFinish.setText(LanguageUtil.getString(this, R.string.share_detail));
        mTitlebarFinish.setOnClickListener(v -> {
            startActivity(new Intent(this, ShareRecordActivity.class));
        });
    }

    @Override
    public void initData() {
        mTvLink.setText(String.format(LanguageUtil.getString(this, R.string.share_friend), 1));
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
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }
}