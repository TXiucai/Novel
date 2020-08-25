package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.AcquireBaoyuePayAdapter;
import com.heiheilianzai.app.adapter.AcquireBaoyuePrivilegeAdapter;
import com.heiheilianzai.app.bean.AcquirePayItem;
import com.heiheilianzai.app.bean.AcquirePrivilegeItem;
import com.heiheilianzai.app.book.config.BookConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.constants.SharedPreferencesConstant;
import com.heiheilianzai.app.dialog.GetDialog;
import com.heiheilianzai.app.eventbus.CreateVipPayOuderEvent;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 包月购买页
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyueActivity extends BaseActivity implements ShowTitle {
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
    @BindView(R2.id.activity_acquire_customer_service)
    public View activity_acquire_customer_service;

    String mKeFuOnline;
    AcquireBaoyuePayAdapter baoyuePayAdapter;
    private boolean isCreatePayOuder = false;

    @Override
    public int initContentView() {
        return R.layout.activity_acquire;
    }

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
                activity_acquire_avatar_name.setText(nickName);
                activity_acquire_avatar_desc.setText(userObj.getString("display_date"));
                MyPicasso.IoadImage(this, mAvatar, R.mipmap.hold_user_avatar, activity_acquire_avatar);
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
            mKeFuOnline = jsonObj.getString("kefu_online");
            if (!StringUtils.isEmpty(mKeFuOnline)) {
                activity_acquire_customer_service.setVisibility(View.VISIBLE);
            }
            List<AcquirePrivilegeItem> privilegeList = new ArrayList<>();
            JSONArray privilegeArray = jsonObj.getJSONArray("privilege");
            for (int i = 0; i < privilegeArray.length(); i++) {
                AcquirePrivilegeItem item = gson.fromJson(privilegeArray.getString(i), AcquirePrivilegeItem.class);
                privilegeList.add(item);
            }
            baoyuePayAdapter = new AcquireBaoyuePayAdapter(this, payList, payList.size());
            baoyuePayAdapter.setOnPayItemClickListener(new AcquireBaoyuePayAdapter.OnPayItemClickListener() {
                @Override
                public void onPayItemClick(AcquirePayItem item) {
                    pay(item);
                }
            });
            activity_acquire_pay_gridview.setAdapter(baoyuePayAdapter);
            AcquireBaoyuePrivilegeAdapter baoyuePrivilegeAdapter = new AcquireBaoyuePrivilegeAdapter(this, privilegeList, privilegeList.size());
            activity_acquire_privilege_gridview.setAdapter(baoyuePrivilegeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @OnClick(value = {R.id.activity_acquire_customer_service})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_acquire_customer_service:
                skipKeFuOnline();
                break;
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

    /**
     *获取支付渠道url,跳转浏览器
     */
    private void pay(AcquirePayItem item){
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("goods_id", item.getGoods_id());
        params.putExtraParams("mobile",  ShareUitls.getString(AcquireBaoyueActivity.this, SharedPreferencesConstant.USER_MOBILE_KAY,""));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mNewPayVip, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!cn.jmessage.support.qiniu.android.utils.StringUtils.isNullOrEmpty(result)) {
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                String pay_url = jsonObj.getString("pay_link");
                                Uri uri = Uri.parse(pay_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                isCreatePayOuder=true;
                            } catch (Exception e) {
                            }
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isCreatePayOuder) {
            isCreatePayOuder=false;
            GetDialog.IsOperationPositiveNegative(AcquireBaoyueActivity.this, getString(R.string.PayActivity_order_remind), "", getString(R.string.MineNewFragment_lianxikefu), new GetDialog.IsOperationInterface() {
                @Override
                public void isOperation() {
                    EventBus.getDefault().post(new CreateVipPayOuderEvent());
                }
            },new GetDialog.IsNegativeInterface(){

                @Override
                public void isNegative() {
                    skipKeFuOnline();
                }
            },true,true);
        }
    }

    /**
     * 客服链接跳转浏览器
     */
    private void  skipKeFuOnline(){
        if (!com.heiheilianzai.app.utils.StringUtils.isEmpty(mKeFuOnline)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mKeFuOnline));
            startActivity(browserIntent);
        }
    }
}
