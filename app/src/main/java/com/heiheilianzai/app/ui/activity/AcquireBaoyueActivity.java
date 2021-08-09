package com.heiheilianzai.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.AcquireBaoyuePayAdapter;
import com.heiheilianzai.app.adapter.AcquireBaoyuePrivilegeAdapter;
import com.heiheilianzai.app.adapter.CommentVipAdapter;
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.adapter.VipBaoyuePayAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.component.pay.alipay.PayResult;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.AcquirePayItem;
import com.heiheilianzai.app.model.AcquirePrivilegeItem;
import com.heiheilianzai.app.model.Announce;
import com.heiheilianzai.app.model.MarqueeVipBean;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.model.PaymentWebBean;
import com.heiheilianzai.app.model.WxPayBean;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.event.LogoutBoYinEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.ui.dialog.PayDialog;
import com.heiheilianzai.app.ui.dialog.WaitDialog;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogErrorVip;
import com.heiheilianzai.app.utils.DialogVipComfirm;
import com.heiheilianzai.app.utils.DialogVipOrderError;
import com.heiheilianzai.app.utils.DialogWakeVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StatusBarUtil;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.MarqueeTextView;
import com.heiheilianzai.app.view.MarqueeTextViewClickListener;
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
import okhttp3.Request;
import okhttp3.Response;

/**
 * 包月购买页
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyueActivity extends BaseButterKnifeTransparentActivity implements ShowTitle {
    @BindView(R.id.titlebar_back)
    public LinearLayout mBack;
    @BindView(R.id.titlebar_text)
    public TextView mTitle;
    private String mAvatar;
    @BindView(R.id.activity_acquire_avatar)
    public CircleImageView activity_acquire_avatar;
    @BindView(R.id.activity_acquire_avatar_name)
    public TextView activity_acquire_avatar_name;
    @BindView(R.id.activity_acquire_pay_gridview)
    public RecyclerView activity_acquire_pay_gridview;
    @BindView(R.id.activity_acquire_privilege_gridview)
    public AdaptionGridViewNoMargin activity_acquire_privilege_gridview;
    @BindView(R.id.activity_acquire_avatar_desc)
    public TextView activity_acquire_avatar_desc;
    @BindView(R.id.activity_acquire_customer_service)
    public LinearLayout activity_acquire_customer_service;
    @BindView(R.id.activity_acquire_avatar_isvip)
    public ImageView activity_acquire_avatar_isvip;
    @BindView(R.id.marquee)
    public MarqueeTextView mMarquee;
    @BindView(R.id.ll_announce_layout)
    public LinearLayout mLlMarquee;
    @BindView(R.id.tx_price)
    public TextView mTxPrice;
    @BindView(R.id.tx_price_tip)
    public TextView mTxPriceTip;
    @BindView(R.id.gv)
    public AdaptionGridView mGv;
    @BindView(R.id.tx_bottom_tip)
    public TextView mTxBottomTip;

    String mKeFuOnline;//客服链接
    AcquireBaoyuePayAdapter baoyuePayAdapter;
    private static final int SDK_PAY_FLAG = 1;
    private static final String ORIGIN_CODE = "origin_code";
    private String ALIPAY_SUCCESS = "9000";//支付宝支付成功回调
    private String ALIPAY = "2";
    private String WECHAT = "1";
    private String mInternetIp;//用户IP
    private VipBaoyuePayAdapter vipBaoyuePayAdapter;
    private AcquirePayItem selectAcquirePayItem;
    private WaitDialog mWaitDialog;
    private int mGoodsId;
    private int mSelectPayItemPos;
    private int mOriginCode = 13;
    private int WIDTH, HEIGHT;

    @Override
    public int initContentView() {
        return R.layout.activity_acquire;
    }

    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.AcquireBaoyueActivity_title));
        StatusBarUtil.setStatusTextColor(false, AcquireBaoyueActivity.this);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        mGoodsId = getIntent().getIntExtra("goodsId", 0);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AcquireBaoyueActivity.this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        activity_acquire_pay_gridview.setLayoutManager(linearLayoutManager);
        WIDTH = ScreenSizeUtils.getInstance(this).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(this, 50)) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
        initComentData();
    }

    private void initComentData() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("channel_id", "1");
        params.putExtraParams("page_num", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mCommentVip, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initComment(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mGv.setVisibility(View.GONE);
                    }
                }
        );
    }

    public void initData() {
        getIpTerritory();//获取用户IP
        setVIPConfirmEvent();
        Intent intent = getIntent();
        mAvatar = intent.getStringExtra("avatar");
        mOriginCode = intent.getIntExtra(ORIGIN_CODE, 13);
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BookConfig.mPayBaoyueCenterUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfos(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mMarqueeVip, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initMarquee(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mLlMarquee.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void initComment(String result) {
        try {
            OptionItem optionItem = new Gson().fromJson(result, OptionItem.class);
            List<OptionBeen> list = optionItem.getList();
            List<OptionBeen> firstList = list.subList(0, 6);
            CommentVipAdapter verticalAdapter = new CommentVipAdapter(this, firstList, WIDTH, HEIGHT);
            mGv.setAdapter(verticalAdapter);
            mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String comic_id = list.get(position).comic_id;
                    String book_id = list.get(position).book_id;
                    if (comic_id != null) {
                        startActivity(ComicInfoActivity.getMyIntent(AcquireBaoyueActivity.this, LanguageUtil.getString(AcquireBaoyueActivity.this, R.string.refer_page_info) + " " + comic_id, comic_id));
                    } else {
                        startActivity(BookInfoActivity.getMyIntent(AcquireBaoyueActivity.this, LanguageUtil.getString(AcquireBaoyueActivity.this, R.string.refer_page_info) + " " + book_id, book_id));
                    }
                }
            });
        } catch (Exception e) {
            mGv.setVisibility(View.GONE);
        }

    }

    private void initMarquee(String result) {
        try {
            MarqueeVipBean marqueeVipBean = new Gson().fromJson(result, MarqueeVipBean.class);
            List<MarqueeVipBean.ListBean> list = marqueeVipBean.getList();
            if (list != null && list.size() > 0) {
                mLlMarquee.setVisibility(View.VISIBLE);
                mMarquee.setSelectColor(true);
                final List<Announce> announceList = new ArrayList<>();
                for (int i = 0; i < list.size(); i++) {
                    Announce announce = new Announce();
                    announce.setTitle(String.format(getString(R.string.string_success_vip_marquee), list.get(i).getMobile()) + list.get(i).getGood_title());
                    announceList.add(announce);
                }
                mMarquee.setTextArraysAndClickListener(announceList, new MarqueeTextViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                        Intent intent = new Intent(AcquireBaoyueActivity.this, AnnounceActivity.class);
                        intent.putExtra("announce_content", announceList.get(position).getTitle() + "/-/" + announceList.get(position).getContent());
                        startActivity(intent);
                    }
                });
            } else {
                mLlMarquee.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            mLlMarquee.setVisibility(View.GONE);
        }
    }

    public void initInfos(String json) {
        Gson gson = new Gson();
        try {
            JSONObject jsonObj = new JSONObject(json);
            mKeFuOnline = jsonObj.getString("kefu_online");
            if (Utils.isLogin(this)) {
                if (!jsonObj.isNull("user")) {
                    JSONObject userObj = jsonObj.getJSONObject("user");
                    String nickName = userObj.getString("nickname");
                    String is_vip = userObj.getString("is_vip");
                    activity_acquire_avatar_isvip.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(is_vip, "1")) {
                        activity_acquire_avatar_isvip.setImageResource(R.mipmap.icon_isvip);
                    } else {
                        activity_acquire_avatar_isvip.setImageResource(R.mipmap.icon_novip);
                    }
                    activity_acquire_avatar_name.setText(nickName);
                    activity_acquire_avatar_desc.setText(userObj.getString("display_date"));
                    int onlineIsNew = jsonObj.getInt("kefu_online_is_new");
                    if (onlineIsNew == 0) {//1 新客服系统 0为久客户系统
                        mKeFuOnline += "?uid=" + userObj.getString("uid");
                    }
                    MyPicasso.IoadImage(this, mAvatar, R.mipmap.hold_user_avatar, activity_acquire_avatar);
                } else {
                    activity_acquire_avatar.setBackgroundResource(R.mipmap.hold_user_avatar);
                    activity_acquire_avatar_name.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                    resetLogin(this);
                }
            } else {
                activity_acquire_avatar.setBackgroundResource(R.mipmap.hold_user_avatar);
                activity_acquire_avatar_name.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                activity_acquire_avatar_isvip.setVisibility(View.GONE);
            }
            List<AcquirePayItem> payList = new ArrayList<>();
            JSONArray listArray = jsonObj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                AcquirePayItem item = gson.fromJson(listArray.getString(i), AcquirePayItem.class);
                if (TextUtils.equals(item.getGoods_id(), String.valueOf(mGoodsId))) {
                    selectAcquirePayItem = item;
                    mSelectPayItemPos = i;
                }
                payList.add(item);
            }
            if (!StringUtils.isEmpty(mKeFuOnline)) {
                activity_acquire_customer_service.setVisibility(View.VISIBLE);
            }
            List<AcquirePrivilegeItem> privilegeList = new ArrayList<>();
            JSONArray privilegeArray = jsonObj.getJSONArray("privilege");
            for (int i = 0; i < privilegeArray.length(); i++) {
                AcquirePrivilegeItem item = gson.fromJson(privilegeArray.getString(i), AcquirePrivilegeItem.class);
                privilegeList.add(item);
            }
            vipBaoyuePayAdapter = new VipBaoyuePayAdapter(this, payList);
            if (selectAcquirePayItem == null) {
                selectAcquirePayItem = payList.get(0);
                vipBaoyuePayAdapter.setSelectPosition(0);
                initBottomTip();
            } else {
                vipBaoyuePayAdapter.setSelectPosition(mSelectPayItemPos);
            }
            initBottomPay(selectAcquirePayItem);
            vipBaoyuePayAdapter.setOnPayItemClickListener(new VipBaoyuePayAdapter.OnPayItemClickListener() {

                @Override
                public void onPayItemClick(AcquirePayItem item, int position) {
                    vipBaoyuePayAdapter.setSelectPosition(position);
                    selectAcquirePayItem = item;
                    initBottomTip();
                    initBottomPay(selectAcquirePayItem);
                }
            });
            activity_acquire_pay_gridview.setAdapter(vipBaoyuePayAdapter);
            AcquireBaoyuePrivilegeAdapter baoyuePrivilegeAdapter = new AcquireBaoyuePrivilegeAdapter(this, privilegeList, privilegeList.size());
            activity_acquire_privilege_gridview.setAdapter(baoyuePrivilegeAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
            resetLogin(this);
        }
    }

    private void initBottomTip() {
        if (selectAcquirePayItem.getNote_text() != null && !TextUtils.equals(selectAcquirePayItem.getNote_text(), "")) {
            mTxBottomTip.setText(selectAcquirePayItem.getNote_text());
            mTxBottomTip.setVisibility(View.VISIBLE);
        } else {
            mTxBottomTip.setVisibility(View.GONE);
        }
    }

    private void initBottomPay(AcquirePayItem selectAcquirePayItem) {
        String original_price = selectAcquirePayItem.getOriginal_price();
        String price = selectAcquirePayItem.getPrice();
        int i = Integer.parseInt(original_price) - Integer.parseInt(price);
        if (original_price != null && !TextUtils.equals(original_price, "0")) {
            mTxPriceTip.setText(String.format(getString(R.string.string_vip_price_tip), original_price, i));
            mTxPriceTip.setVisibility(View.VISIBLE);
        } else {
            mTxPriceTip.setVisibility(View.GONE);
        }
        mTxPrice.setText(price);
    }

    @OnClick(value = {R.id.activity_acquire_customer_service, R.id.tx_open_vip})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_acquire_customer_service:
                skipKeFuOnline();
                break;
            case R.id.tx_open_vip:
                if (selectAcquirePayItem != null) {
                    if (Utils.isLogin(AcquireBaoyueActivity.this)) {
                        pay(selectAcquirePayItem);
                    } else {
                        GetDialog.IsOperation(AcquireBaoyueActivity.this, getString(R.string.MineNewFragment_nologin_prompt), "", new GetDialog.IsOperationInterface() {
                            @Override
                            public void isOperation() {
                                MainHttpTask.getInstance().Gotologin(AcquireBaoyueActivity.this);
                            }
                        });
                    }
                }
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
        initDialog();
        mWaitDialog.setMessage(getString(R.string.string_order_create));
        mWaitDialog.showDailog();
        if (Utils.isLogin(this)) {
            setVIPChoiceEvent(item.getGoods_id());
            ReaderParams params = new ReaderParams(this);
            params.putExtraParams("goods_id", item.getGoods_id());
            params.putExtraParams("mobile", ShareUitls.getString(AcquireBaoyueActivity.this, PrefConst.USER_MOBILE_KAY, ""));
            params.putExtraParams("user_client_ip", StringUtils.isEmpty(mInternetIp) ? "" : mInternetIp);
            params.putExtraParams("phoneModel", Build.MANUFACTURER + "-" + Build.MODEL);
            params.putExtraParams("payment_source_id", String.valueOf(mOriginCode));
            String json = params.generateParamsJson();
            HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mNewPayVip, json, false, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            if (mWaitDialog != null) {
                                mWaitDialog.dismissDialog();
                            }
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
                            if (mWaitDialog != null) {
                                mWaitDialog.dismissDialog();
                            }
                            DialogVipOrderError dialogVipOrderError = new DialogVipOrderError();
                            dialogVipOrderError.getDialogVipPop(AcquireBaoyueActivity.this);
                            dialogVipOrderError.setmOnRepeatListener(() -> pay(selectAcquirePayItem));
                        }
                    }
            );
        } else {
            MainHttpTask.getInstance().Gotologin(this);
        }
    }


    private void initDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismissDialog();
        }
        mWaitDialog = null;
        mWaitDialog = new WaitDialog(AcquireBaoyueActivity.this, "");
        mWaitDialog.setCancleable(true);
    }

    /**
     * 跳转到支付Dialog
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

                @Override
                public void onWake() {
                    DialogWakeVip dialogWakeVip = new DialogWakeVip();
                    dialogWakeVip.getDialogVipPop(AcquireBaoyueActivity.this);
                    dialogWakeVip.setVipWakeListener(new DialogWakeVip.VipWakeListener() {
                        @Override
                        public void vipWakeBack() {
                            pay(selectAcquirePayItem);
                        }
                    });
                }

                @Override
                public void onError() {
                    DialogErrorVip dialogErrorVip = new DialogErrorVip();
                    dialogErrorVip.getDialogVipPop(AcquireBaoyueActivity.this);
                    dialogErrorVip.setVipWakeListener(new DialogErrorVip.VipErrorListener() {
                        @Override
                        public void vipErrorBack() {
                            pay(selectAcquirePayItem);
                        }
                    });
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
        AppPrefs.putSharedBoolean(AcquireBaoyueActivity.this, PrefConst.ORDER, true);
        DialogVipComfirm dialogVipComfirm = new DialogVipComfirm();
        dialogVipComfirm.getDialogVipPop(this);
        dialogVipComfirm.setmOnOpenKefuListener(() -> skipKeFuOnline());
        dialogVipComfirm.setmOnRepeatListener(() -> pay(selectAcquirePayItem));
    }

    /**
     * 跳入原生支付 微信或支付宝
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
     *
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
     *
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

    /**
     * 进入VIP会员购买必传参数
     *
     * @param context
     * @param referPage 神策埋点数据从哪个页面进入
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, int originCode) {

        /*
        originCode
        小说首页开通icon   ==》  1
        漫画首页开通icon  ==》  2
        小说章节开通弹窗  ==》  3
        漫画章节开通弹窗  ==》  4
        漫画简介页开通引导  ==》 5
        有声首页开通icon  ==》 6
        有声简介页开通按钮  ==》  7
        有声章节开通弹窗  ==》  8
        有声播放页下载开通弹窗  ==》  9
        有声播放页试听次数用完引导开通弹窗  ==》  10
        个人中心-开通按钮  ==》  11
        小说、漫画首页引导提示  ==》  12
        未知  ==》  13*/

        Intent intent = new Intent(context, AcquireBaoyueActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(ORIGIN_CODE, originCode);
        return intent;
    }

    /**
     * 神策埋点 进入VIP购买页时
     */
    private void setVIPConfirmEvent() {
        SensorsDataHelper.setVIPConfirmEvent(getIntent().getStringExtra(SaVarConfig.REFER_PAGE_VAR));
    }

    /**
     * 神策埋点 选购vip套餐类型
     *
     * @param goodsId 套餐ID
     */
    private void setVIPChoiceEvent(String goodsId) {
        try {
            SensorsDataHelper.setVIPChoiceEvent(Integer.valueOf(goodsId));
        } catch (Exception e) {
        }
    }

    /**
     * 获取用户IP
     */
    public void getIpTerritory() {
        OkHttpEngine.getInstance(AcquireBaoyueActivity.this).getAsyncHttp(ReaderConfig.thirdpartyGetCity, new ResultCallback() {

            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onResponse(Response response) {
                try {
                    String body = response.body().string();
                    if (!StringUtils.isEmpty(body)) {
                        StringBuilder builder = new StringBuilder();
                        builder.append(body);
                        int satrtIndex = builder.indexOf("{");//包含[
                        int endIndex = builder.indexOf("}");//包含]
                        String json = builder.substring(satrtIndex, endIndex + 1);
                        JSONObject jo = new JSONObject(json);
                        mInternetIp = jo.getString("cip");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 清空登录信息
     */
    public static void resetLogin(Context context) {
        AppPrefs.putSharedString(context, ReaderConfig.TOKEN, "");
        AppPrefs.putSharedString(context, ReaderConfig.UID, "");
        AppPrefs.putSharedString(context, ReaderConfig.BOYIN_LOGIN_TOKEN, "");
        AppPrefs.putSharedString(context, PrefConst.USER_INFO_KAY, "");
        ReaderConfig.REFREASH_USERCENTER = true;
        EventBus.getDefault().post(new RefreshMine(null));
        EventBus.getDefault().post(new LogoutBoYinEvent());
        SensorsDataHelper.profileSet(context);
    }
}