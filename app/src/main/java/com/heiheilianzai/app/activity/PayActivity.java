package com.heiheilianzai.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.PayChannelNewAdapter;
import com.heiheilianzai.app.bean.PayChannelNew;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.dialog.GetDialog;
import com.heiheilianzai.app.eventbus.CreateVipPayOuderEvent;
import com.heiheilianzai.app.eventbus.RefreshMine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.view.GridViewForScrollView;
import com.heiheilianzai.app.wxpay.WXPayResult;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import cn.jmessage.support.qiniu.android.utils.StringUtils;

/**
 * 支付页面，可选择微信支付和支付宝支付(更换支付渠道，统一跳浏览器)
 * Created by scb on 2018/8/9.
 */
public class PayActivity extends BaseActivity implements ShowTitle, View.OnClickListener {
    @BindView(R2.id.weixin_pay_layout)
    RelativeLayout weixin_pay_layout;
    @BindView(R2.id.alipay_pay_layout)
    RelativeLayout alipay_pay_layout;
    @BindView(R2.id.pay_confirm_btn)
    Button pay_confirm_btn;
    @BindView(R2.id.weixin_paytype_img)
    ImageView weixin_paytype_img;
    @BindView(R2.id.alipay_paytype_img)
    ImageView alipay_paytype_img;
    @BindView(R2.id.pay_channel_gridview)
    GridViewForScrollView pay_channel_gridview;
    private String id;
    private String mGoodsId;
    private String payName;
    private String mPrice;
    private String mKeFuOnline;
    public static Activity activity;
    Gson gson = new Gson();
    List<PayChannelNew> mPayChannelList;
    PayChannelNewAdapter payChannelAdapter;
    private boolean isCreatePayOuder = false;

    @Override
    public int initContentView() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView() {
        activity = this;
        weixin_paytype_img.setImageResource(R.mipmap.pay_selected);
        alipay_paytype_img.setImageResource(R.mipmap.pay_unselected);
        weixin_pay_layout.setOnClickListener(this);
        alipay_pay_layout.setOnClickListener(this);
        pay_confirm_btn.setOnClickListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //此处先设置一下微信的支付结果标识
        WXPayResult.WXPAY_CODE = 1;
    }

    @Override
    public void initData() {
        mGoodsId = getIntent().getStringExtra("goods_id");
        mPrice = getIntent().getStringExtra("price");
        mKeFuOnline = getIntent().getStringExtra("kefu_online");
        initTitleBarView(LanguageUtil.getString(activity, R.string.PayActivity_title));
        pay_confirm_btn.setText(LanguageUtil.getString(activity, R.string.PayActivity_querenzhifu) + mPrice);
        ReaderParams params2 = new ReaderParams(this);
        params2.putExtraParams("is_v2", "1");
        String json2 = params2.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mPayChannelList, json2, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        infoPayChanne(result);
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
        View titlebar_right,titlebar_right_img;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
        if(!StringUtils.isNullOrEmpty(mKeFuOnline)){
            titlebar_right =  findViewById(R.id.titlebar_right);
            titlebar_right_img =  findViewById(R.id.titlebar_right_img);
            titlebar_right.setVisibility(View.VISIBLE);
            titlebar_right_img.setBackgroundResource(R.mipmap.ic_customer_service);
            titlebar_right_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    skipKeFuOnline();
                }
            });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weixin_pay_layout:
                weixin_paytype_img.setImageResource(R.mipmap.pay_selected);
                alipay_paytype_img.setImageResource(R.mipmap.pay_unselected);
                break;
            case R.id.alipay_pay_layout:
                weixin_paytype_img.setImageResource(R.mipmap.pay_unselected);
                alipay_paytype_img.setImageResource(R.mipmap.pay_selected);
                break;
            case R.id.pay_confirm_btn:
                if (!MainHttpTask.getInstance().Gotologin(activity)) {
                    return;
                }
                pay();
                break;
        }
    }

    private void infoPayChanne(String result) {
        if (!StringUtils.isNullOrEmpty(result)) {
            try {
                JSONObject jsonObj = new JSONObject(result);
                mPayChannelList = gson.fromJson(jsonObj.getString("pay_list"), new TypeToken<List<PayChannelNew>>() {
                }.getType());
                id = mPayChannelList.get(0).getId();
                payName= mPayChannelList.get(0).getName();
                payChannelAdapter = new PayChannelNewAdapter(this, mPayChannelList, mPayChannelList.size());
                pay_channel_gridview.setAdapter(payChannelAdapter);
                pay_channel_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        PayActivity.this.id = mPayChannelList.get(position).getId();
                        PayActivity.this.payName= mPayChannelList.get(position).getName();
                        payChannelAdapter.setSelectPosition(position);
                        payChannelAdapter.notifyDataSetChanged();
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void pay() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("goods_id", mGoodsId);
        params.putExtraParams("pay_list_id", id);
        params.putExtraParams("pay_name", payName);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mPayVip, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!StringUtils.isNullOrEmpty(result)) {
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                String pay_url = jsonObj.getString("pay_link");
                                Uri uri = Uri.parse(pay_url);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                                isCreatePayOuder = true;
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
            GetDialog.IsOperationPositiveNegative(PayActivity.this, getString(R.string.PayActivity_order_remind), "", getString(R.string.MineNewFragment_lianxikefu), new GetDialog.IsOperationInterface() {
                @Override
                public void isOperation() {
                   finish();
                }
            },new GetDialog.IsNegativeInterface(){

                @Override
                public void isNegative() {
                    skipKeFuOnline();
                }
            },true,true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().post(new CreateVipPayOuderEvent());
    }

    private void  skipKeFuOnline(){
        if (!com.heiheilianzai.app.utils.StringUtils.isEmpty(mKeFuOnline)) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(mKeFuOnline));
            startActivity(browserIntent);
        }
    }
}
