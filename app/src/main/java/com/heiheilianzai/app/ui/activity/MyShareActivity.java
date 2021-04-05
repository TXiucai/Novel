package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ShareDetailsBean;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.SaveImageUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.ToastUtil;
import com.heiheilianzai.app.utils.Utils;

import butterknife.BindView;


/**
 * 个人中心-意见反馈
 * Created by scb on 2018/7/14.
 */
public class MyShareActivity extends BaseActivity implements ShowTitle, View.OnClickListener {

    @BindView(R.id.tv_share_rule)
    TextView mShareRules;
    TextView mTitlebarFinish;
    LinearLayout mCommentMenu;
    @BindView(R.id.qr_code_img)
    ImageView mQRCodeImg;
    @BindView(R.id.tv_code)
    TextView mQRCode;
    @BindView(R.id.tv_link)
    TextView mShareLink;
    @BindView(R.id.tv_shareNum)
    TextView mShareNum;
    @BindView(R.id.share_copy)
    TextView mShareCopy;
    @BindView(R.id.share_save)
    TextView mShareSave;
    @BindView(R.id.rl_qr_code)
    RelativeLayout mRlQRCode;
    @BindView(R.id.qr_code_layout)
    LinearLayout mQRCodeLayout;

    private ShareDetailsBean mShareDetailsBean;

    @Override
    public int initContentView() {
        return R.layout.activity_myshare;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.ShareActivity_title));
        mTitlebarFinish = findViewById(R.id.titlebar_finish);
        mCommentMenu = findViewById(R.id.comment_titlebar_add_comment);

        if (Utils.isLogin(this)) {
            mTitlebarFinish.setText(LanguageUtil.getString(this, R.string.share_detail));
            mCommentMenu.setVisibility(View.VISIBLE);
            mTitlebarFinish.setOnClickListener(v -> {
                startActivity(new Intent(this, ShareRecordActivity.class));
            });
        } else {
            mCommentMenu.setVisibility(View.GONE);
        }

        mShareSave.setOnClickListener(this);
        mShareCopy.setOnClickListener(this);
    }

    @Override
    public void initData() {
        addShare();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.share_copy:
                if (mShareDetailsBean.getLink() != null) {
                    StringUtils.setStringInClipboard(this, mShareDetailsBean.getLink());
                    ToastUtil.getInstance().showShortT(R.string.ShareActivity_save_toast);
                }
                break;
            case R.id.share_save:
                mQRCodeLayout.setDrawingCacheEnabled(true);
                mQRCodeLayout.buildDrawingCache();
                new Thread(() -> {
                    if (mShareDetailsBean.getQrLink() != null) {
                        //Bitmap bp = SaveImageUtils.returnBitMap(ReaderConfig.getBaseUrl() + mShareDetailsBean.getQrLink());
                        Bitmap bp = mQRCodeLayout.getDrawingCache();
                        SaveImageUtils.saveImageToGallerys(this, bp);
                    } else {
                        ToastUtil.getInstance().showShortT(R.string.save_default);
                    }
                }).start();
                break;
        }
    }

    /**
     * 发请求
     */

    public void addShare() {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mShareDetails, json, true, new HttpUtils.ResponseListener() {
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
        ShareDetailsBean data = new Gson().fromJson(json, ShareDetailsBean.class);
        if (data != null) {
            mShareDetailsBean = data;
            if (Utils.isLogin(this)) {
                mShareNum.setVisibility(View.VISIBLE);
                if (!data.isShareStatus()) {
                    mQRCode.setVisibility(View.GONE);
                    mRlQRCode.setVisibility(View.GONE);
                    mShareNum.setText(String.format(LanguageUtil.getString(this, R.string.share_friend2), data.getShareSuccessNum()));
                } else {
                    mQRCode.setVisibility(View.VISIBLE);
                    mRlQRCode.setVisibility(View.VISIBLE);
                    mShareNum.setText(String.format(LanguageUtil.getString(this, R.string.share_friend), data.getShareSuccessNum()));
                    mQRCode.setText(data.getShareCode());
                }

            } else {
                mQRCode.setVisibility(View.GONE);
                mShareNum.setVisibility(View.GONE);
                mRlQRCode.setVisibility(View.GONE);
            }

            if (data.getQrLink() != null && !TextUtils.isEmpty(data.getQrLink())) {
                MyPicasso.GlideImageNoSize(this, ReaderConfig.getBaseUrl() + data.getQrLink(), mQRCodeImg);
            }

            if (data.getLink() != null) {
                mShareLink.setText(data.getLink());
            }

            if (data.getShareRuleContent() != null) {
                mShareRules.setText(data.getShareRuleContent());
            }
        }
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