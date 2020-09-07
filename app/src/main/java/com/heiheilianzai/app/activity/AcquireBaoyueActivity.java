package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.AcquireBaoyuePayAdapter;
import com.heiheilianzai.app.adapter.AcquireBaoyuePrivilegeAdapter;
import com.heiheilianzai.app.alipay.PayResult;
import com.heiheilianzai.app.bean.AcquirePayItem;
import com.heiheilianzai.app.bean.AcquirePrivilegeItem;
import com.heiheilianzai.app.bean.PaymentWebBean;
import com.heiheilianzai.app.bean.WxPayBean;
import com.heiheilianzai.app.book.config.BookConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.constants.SharedPreferencesConstant;
import com.heiheilianzai.app.dialog.GetDialog;
import com.heiheilianzai.app.dialog.PayDialog;
import com.heiheilianzai.app.eventbus.CreateVipPayOuderEvent;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.CircleImageView;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    private static final int SDK_PAY_FLAG = 1;
    private String ALIPAY_SUCCESS = "9000";//支付宝支付成功回调
    private String ALIPAY = "2";
    private String WECHAT = "1";

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
     * 获取支付渠道url,跳转支付Dialog
     */
    private void pay(AcquirePayItem item) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("goods_id", item.getGoods_id());
        params.putExtraParams("mobile", ShareUitls.getString(AcquireBaoyueActivity.this, SharedPreferencesConstant.USER_MOBILE_KAY, ""));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mNewPayVip, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!cn.jmessage.support.qiniu.android.utils.StringUtils.isNullOrEmpty(result)) {
                            try {
                                JSONObject jsonObj = new JSONObject(result);
                                String pay_url = jsonObj.getString("pay_link");
                                showPayDialog(pay_url);
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

    /**
     *跳转到支付Dialog
     */
    void showPayDialog(String url) {
        View view = this.getWindow().getDecorView();
        if (view != null) {
            PayDialog payDialog = new PayDialog();
            payDialog.showDialog(this, view, url);
            payDialog.setPayInterface(new PayDialog.PayInterface() {
                @Override
                public void onPayFinish() {
                    showPayFinishDialog();
                }

                @Override
                public void nativePay(String payType, String jsonData) {//跳入原生支付，（现在H5支付中并没有原生渠道）
                    AcquireBaoyueActivity.this.nativePay(payType, jsonData);
                }
            });
        }
    }

    /**
     * 客服链接跳转浏览器
     */
    private void skipKeFuOnline() {
        if (!com.heiheilianzai.app.utils.StringUtils.isEmpty(mKeFuOnline)) {
            startActivity(new Intent(this, AboutActivity.class).putExtra("url", mKeFuOnline).putExtra("flag", "notitle"));
        }
    }

    /**
     * 完成支付订单，关闭支付Dialog后显示该提示弹框 根据订单刷新用户信息
     */
    private void showPayFinishDialog() {
        EventBus.getDefault().post(new CreateVipPayOuderEvent());
        GetDialog.IsOperationPositiveNegative(AcquireBaoyueActivity.this, getString(R.string.PayActivity_order_remind), "", getString(R.string.MineNewFragment_lianxikefu), new GetDialog.IsOperationInterface() {
            @Override
            public void isOperation() {
            }
        }, new GetDialog.IsNegativeInterface() {

            @Override
            public void isNegative() {
                skipKeFuOnline();
            }
        }, true, true);
    }

    /**
     *跳入原生支付 微信或支付宝
     */
    public void nativePay(String payType, String jsonData) {
        PaymentWebBean bean = new Gson().fromJson(jsonData, PaymentWebBean.class);
        String payInfo = bean.data;
        if (TextUtils.isEmpty(payInfo)) {
            return;
        }
        if (payType.equals(ALIPAY)) {
            aliPay(payInfo);
        } else if (payType.equals(WECHAT)) {
            wechatPay(payInfo);
        }
    }

    /**
     * 原生支付宝支付
     * @param orderInfo
     */
    private void aliPay(String orderInfo) {
        final Runnable payRunnable = new Runnable() {
            @Override
            public void run() {
                PayTask alipay = new PayTask(AcquireBaoyueActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * 原生微信支付
     * @param payInfo
     */
    private void wechatPay(String payInfo) {
        WxPayBean wxPayBean = new Gson().fromJson(payInfo, WxPayBean.class);
        IWXAPI api = WXAPIFactory.createWXAPI(this, wxPayBean.getAppid());
        api.registerApp(wxPayBean.getAppid());
        if (!api.isWXAppInstalled()) {
            MyToash.ToashError(this, "请安装微信");
        }
        PayReq payRequest = new PayReq();
        payRequest.appId = wxPayBean.getAppid();
        payRequest.partnerId = wxPayBean.getPartnerid();
        payRequest.prepayId = wxPayBean.getPrepayid();
        payRequest.packageValue = "Sign=WXPay";//固定值
        payRequest.nonceStr = wxPayBean.getNoncestr();
        payRequest.timeStamp = wxPayBean.getTimestamp();
        payRequest.sign = wxPayBean.getSign();
        api.sendReq(payRequest);
    }

    //支付宝原生支付，回调监听
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    if (TextUtils.equals(resultStatus, ALIPAY_SUCCESS)) {
                    } else {
                        MyToash.ToashError(AcquireBaoyueActivity.this, payResult.getMemo());
                    }
                    break;
                }
                default:
                    break;
            }
        }
    };
}