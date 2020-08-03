package com.heiheilianzai.app.activity;

import android.app.Activity;
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
import com.heiheilianzai.app.adapter.PayChannelAdapter;
import com.heiheilianzai.app.alipay.AlipayGoPay;
import com.heiheilianzai.app.bean.PayChannel;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.view.GridViewForScrollView;
import com.heiheilianzai.app.wxpay.WXGoPay;
import com.heiheilianzai.app.wxpay.WXPayResult;

import java.util.List;

import butterknife.BindView;
import cn.jmessage.support.qiniu.android.utils.StringUtils;

/**
 * 支付页面，可选择微信支付和支付宝支付
 * Created by scb on 2018/8/9.
 */
public class PayActivity extends BaseActivity implements ShowTitle, View.OnClickListener {
    private final String TAG = PayActivity.class.getSimpleName();
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
    private int mPayType = 1;//0：微信支付 1：支付宝支付
    private String channel;
    private String payType;
    private int pay_type;
    private String mGoodsId;
    private String mPrice;
    public static Activity activity;
    Gson gson = new Gson();
    List<PayChannel> mPayChannelList;
    PayChannelAdapter payChannelAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_pay;
    }

    @Override
    public void initView() {
        activity = this;
        initTitleBarView(LanguageUtil.getString(activity, R.string.PayActivity_title));
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weixin_pay_layout:
                mPayType = 0;
                weixin_paytype_img.setImageResource(R.mipmap.pay_selected);
                alipay_paytype_img.setImageResource(R.mipmap.pay_unselected);
                break;
            case R.id.alipay_pay_layout:
                mPayType = 1;
                weixin_paytype_img.setImageResource(R.mipmap.pay_unselected);
                alipay_paytype_img.setImageResource(R.mipmap.pay_selected);
                break;
            case R.id.pay_confirm_btn:
                if (!MainHttpTask.getInstance().Gotologin(activity)) {
                    return;
                }
                if ("1".equals(payType)) {
                    WXGoPay wxGoPay = new WXGoPay(this);
                    wxGoPay.requestPayOrder(ReaderConfig.getBaseUrl() + ReaderConfig.payUrl, mGoodsId, channel, pay_type);
                } else {
                    AlipayGoPay alipayGoPay = new AlipayGoPay(this);
                    alipayGoPay.requestPayOrder(ReaderConfig.getBaseUrl() + ReaderConfig.payUrl, mGoodsId, channel, pay_type);
                    return;
                }
                break;
        }
    }

    private void infoPayChanne(String result) {
        if (!StringUtils.isNullOrEmpty(result)) {
            mPayChannelList = gson.fromJson(result, new TypeToken<List<PayChannel>>() {
            }.getType());
            channel = mPayChannelList.get(0).getChannel();
            payType = mPayChannelList.get(0).getType();
            pay_type = mPayChannelList.get(0).getPay_type();
            payChannelAdapter = new PayChannelAdapter(this, mPayChannelList, mPayChannelList.size());
            pay_channel_gridview.setAdapter(payChannelAdapter);
            pay_channel_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    channel = mPayChannelList.get(position).getChannel();
                    payType = mPayChannelList.get(position).getType();
                    pay_type = mPayChannelList.get(position).getPay_type();
                    payChannelAdapter.setSelectPosition(position);
                    payChannelAdapter.notifyDataSetChanged();
                }
            });
        }
    }
}
