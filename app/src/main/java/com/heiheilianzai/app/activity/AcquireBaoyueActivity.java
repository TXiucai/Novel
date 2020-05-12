package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.AcquireBaoyuePayAdapter;
import com.heiheilianzai.app.adapter.AcquireBaoyuePrivilegeAdapter;
import com.heiheilianzai.app.bean.AcquirePayItem;
import com.heiheilianzai.app.bean.AcquirePrivilegeItem;
import com.heiheilianzai.app.book.config.BookConfig;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.http.ResultCallback;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;

import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.CircleImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import com.heiheilianzai.app.R2;

/**
 * 包月购买页
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyueActivity extends BaseActivity implements ShowTitle {
    @Override
    public int initContentView() {
        return R.layout.activity_acquire;
    }

    @BindView(R2.id.titlebar_back)
    public LinearLayout mBack;
    @BindView(R2.id.titlebar_text)
    public TextView mTitle;
    private String mAvatar;
    @BindView(R2.id.activity_acquire_avatar)
    public CircleImageView activity_acquire_avatar;
    @BindView(R2.id.activity_acquire_avatar_name)
    public TextView activity_acquire_avatar_name;

    @BindView(R2.id.activity_acquire_pay_gridview)
    public AdaptionGridViewNoMargin activity_acquire_pay_gridview;
    @BindView(R2.id.activity_acquire_privilege_gridview)
    public AdaptionGridViewNoMargin activity_acquire_privilege_gridview;
    @BindView(R2.id.activity_acquire_avatar_desc)
    public TextView activity_acquire_avatar_desc;


    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.AcquireBaoyueActivity_title));
    }

    @Override
    public void initData() {
        mAvatar = getIntent().getStringExtra("avatar");
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BookConfig.mPayBaoyueCenterUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {

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
        Gson gson = new Gson();
        try {
            JSONObject jsonObj = new JSONObject(json);
            if (Utils.isLogin(this)) {
                JSONObject userObj = jsonObj.getJSONObject("user");
                String nickName = userObj.getString("nickname");
                //    String avatar = userObj.getString("avatar");
                activity_acquire_avatar_name.setText(nickName);

                activity_acquire_avatar_desc.setText(userObj.getString("display_date"));
                MyPicasso.IoadImage(this, mAvatar, R.mipmap.hold_user_avatar, activity_acquire_avatar);

                // ImageLoader.getInstance().displayImage(TextUtils.isEmpty(avatar) ? mAvatar : avatar, activity_acquire_avatar, ReaderApplication.getOptions());
            } else {
                activity_acquire_avatar.setBackgroundResource(R.mipmap.hold_user_avatar);
                activity_acquire_avatar_name.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_nologin));
            }

            List<AcquirePayItem> payList = new ArrayList<>();
            JSONArray listArray = jsonObj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                AcquirePayItem item = gson.fromJson(listArray.getString(i), AcquirePayItem.class);
                payList.add(item);
            }

            List<AcquirePrivilegeItem> privilegeList = new ArrayList<>();
            JSONArray privilegeArray = jsonObj.getJSONArray("privilege");
            for (int i = 0; i < privilegeArray.length(); i++) {
                AcquirePrivilegeItem item = gson.fromJson(privilegeArray.getString(i), AcquirePrivilegeItem.class);
                privilegeList.add(item);
            }

            AcquireBaoyuePayAdapter baoyuePayAdapter = new AcquireBaoyuePayAdapter(this, payList, payList.size());
            activity_acquire_pay_gridview.setAdapter(baoyuePayAdapter);

            AcquireBaoyuePrivilegeAdapter baoyuePrivilegeAdapter = new AcquireBaoyuePrivilegeAdapter(this, privilegeList, privilegeList.size());
            activity_acquire_privilege_gridview.setAdapter(baoyuePrivilegeAdapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initTitleBarView(String text) {
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        initData();

        super.onActivityResult(requestCode, resultCode, data);
    }

}
