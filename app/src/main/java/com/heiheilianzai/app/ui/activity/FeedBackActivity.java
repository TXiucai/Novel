package com.heiheilianzai.app.ui.activity;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.FeedBackTypeAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ComplaitTypeBean;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import java.util.List;

import butterknife.BindView;


/**
 * 个人中心-意见反馈
 * Created by scb on 2018/7/14.
 */
public class FeedBackActivity extends BaseActivity implements ShowTitle {
    private final String TAG = FeedBackActivity.class.getSimpleName();
    /**
     * 意见内容
     */
    private EditText mEditTextContent;

    /**
     * "提交"外层布局
     */
    private TextView mSubmit;

    /**
     * "提交"外层布局
     */
    private LinearLayout comment_titlebar_add_feedback;

    @BindView(R.id.tabRecycler)
    RecyclerView mTbRecycleView;

    private FeedBackTypeAdapter mTypeAdapter;

    @Override
    public int initContentView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return R.layout.activity_feedback;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.FeedBackActivity_title));
        mEditTextContent = findViewById(R.id.activity_feedback_content);
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFeedback();
            }
        });

        comment_titlebar_add_feedback = findViewById(R.id.comment_titlebar_add_comment);
        comment_titlebar_add_feedback.setVisibility(View.GONE);

        mTbRecycleView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mTypeAdapter = new FeedBackTypeAdapter(this);
        mTbRecycleView.setAdapter(mTypeAdapter);
    }

    @Override
    public void initData() {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mFeedBackType, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ComplaitTypeBean complaitTypeBean = new Gson().fromJson(result, ComplaitTypeBean.class);
                        List<ComplaitTypeBean.ComplaitListBean> typeBeanList = complaitTypeBean.getList();
                        mTypeAdapter.setNewData(typeBeanList);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    /**
     * 发请求
     */

    public void addFeedback() {
        if (!MainHttpTask.getInstance().Gotologin(FeedBackActivity.this)) {
            return;
        }
        ;
        if (TextUtils.isEmpty(mEditTextContent.getText())) {
            MyToash.ToashError(FeedBackActivity.this, LanguageUtil.getString(this, R.string.FeedBackActivity_some));
            return;
        }


        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("content", mEditTextContent.getText().toString() + "");

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
        MyToash.ToashSuccess(FeedBackActivity.this, "反馈成功");
        finish();

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