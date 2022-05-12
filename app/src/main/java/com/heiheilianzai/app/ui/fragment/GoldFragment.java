package com.heiheilianzai.app.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.VipBaoyuePayAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AcquirePayItem;
import com.heiheilianzai.app.model.Announce;
import com.heiheilianzai.app.model.MarqueeVipBean;
import com.heiheilianzai.app.ui.activity.CouponRecordActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.ui.dialog.PayDialog;
import com.heiheilianzai.app.ui.dialog.WaitDialog;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogErrorVip;
import com.heiheilianzai.app.utils.DialogVipComfirm;
import com.heiheilianzai.app.utils.DialogWakeVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.MarqueeTextView;
import com.heiheilianzai.app.view.ObservableScrollView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;
import okhttp3.Response;

public class GoldFragment extends BaseButterKnifeFragment {
    @BindView(R.id.activity_acquire_pay_gridview)
    public RecyclerView activity_acquire_pay_gridview;
    @BindView(R.id.marquee)
    public MarqueeTextView mMarquee;
    @BindView(R.id.ll_announce_layout)
    public LinearLayout mLlMarquee;
    @BindView(R.id.tx_price)
    public TextView mTxPrice;
    @BindView(R.id.tx_price_tip)
    public TextView mTxPriceTip;
    @BindView(R.id.scrollView)
    public ObservableScrollView mScrollView;
    private static final String ARG_PARAM = "goodsId";
    private static final String ARG_PARAM2 = "originCode";
    private String mKeFuOnline;//客服链接
    private int mGoodsId, mGoldBalance;
    private int mOriginCode = 13;
    private int WIDTH, HEIGHT;
    private String mInternetIp;//用户IP
    private SlideListener mSlideListener;
    private AcquirePayItem mSelectAcquirePayItem;
    private WaitDialog mWaitDialog;
    private int mSelectPayItemPos;
    private VipBaoyuePayAdapter mVipBaoyuePayAdapter;
    private List<AcquirePayItem> mPayItems;

    public static GoldFragment newInstance(int goodsId, int originCode) {
        GoldFragment fragment = new GoldFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM, goodsId);
        args.putInt(ARG_PARAM2, originCode);
        fragment.setArguments(args);
        return fragment;
    }

    public interface SlideListener {
        void slide(int slide);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mSlideListener = (SlideListener) context;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoodsId = getArguments().getInt(ARG_PARAM);
            mOriginCode = getArguments().getInt(ARG_PARAM2);
        }
        WIDTH = ScreenSizeUtils.getInstance(getActivity()).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(getActivity(), 50)) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_gold;
    }


    @OnClick(value = {R.id.tx_open_vip, R.id.activity_acquire_gold})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.tx_open_vip:
                pay();
                break;
            case R.id.activity_acquire_gold:
                if (Utils.isLogin(getActivity())) {
                    Intent intent = new Intent();
                    intent.setClass(activity, CouponRecordActivity.class).putExtra("COUPON", mGoldBalance + "");
                    startActivity(intent);
                } else {
                    GetDialog.IsOperation(getActivity(), getString(R.string.MineNewFragment_nologin_prompt_gold), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            MainHttpTask.getInstance().Gotologin(getActivity());
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getGoldMarque();
        getGoldData();
        getIpTerritory();//获取用户IP
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> mSlideListener.slide(scrollY));
    }

    private void getGoldData() {
        ReaderParams params = new ReaderParams(getActivity());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BookConfig.mPayGoldCenterUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfos(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void initInfos(String result) {
        JSONObject jsonObj = null;
        Gson gson = new Gson();
        try {
            jsonObj = new JSONObject(result);
            if (Utils.isLogin(getActivity())) {
                JSONObject userObj = jsonObj.getJSONObject("user");
                mGoldBalance = userObj.getInt("silver_remain");
            }
            mKeFuOnline = jsonObj.getString("kefu_online");
            mPayItems = new ArrayList<>();
            JSONArray listArray = jsonObj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                AcquirePayItem item = gson.fromJson(listArray.getString(i), AcquirePayItem.class);
                //默认选中和再次唤起优先再次唤起
                if (TextUtils.equals(item.getGoods_id(), String.valueOf(mGoodsId))) {
                    mSelectAcquirePayItem = item;
                    mSelectPayItemPos = i;
                } else {
                    if (TextUtils.equals(item.getDefault_select(), "1") && mSelectAcquirePayItem == null) {
                        mSelectAcquirePayItem = item;
                        mSelectPayItemPos = i;
                    }
                }
                mPayItems.add(item);
            }
            mVipBaoyuePayAdapter = new VipBaoyuePayAdapter(getActivity(), mPayItems, 1);
            activity_acquire_pay_gridview.setLayoutManager(new GridLayoutManager(activity, 3));
            activity_acquire_pay_gridview.setAdapter(mVipBaoyuePayAdapter);
            if (mSelectAcquirePayItem == null) {
                mSelectAcquirePayItem = mPayItems.get(0);
                mVipBaoyuePayAdapter.setSelectPosition(0);
            } else {
                wakeOrder(mGoodsId);
            }
            mVipBaoyuePayAdapter.setOnPayItemClickListener(new VipBaoyuePayAdapter.OnPayItemClickListener() {

                @Override
                public void onPayItemClick(AcquirePayItem item, int position) {
                    mVipBaoyuePayAdapter.setSelectPosition(position);
                    mSelectAcquirePayItem = item;
                    initBottomPay(mSelectAcquirePayItem);
                }
            });
            initBottomPay(mSelectAcquirePayItem);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void wakeOrder(int goodId) {
        mGoodsId = goodId;
        if (mPayItems != null) {
            for (int i = 0; i < mPayItems.size(); i++) {
                AcquirePayItem acquirePayItem = mPayItems.get(i);
                if (TextUtils.equals(acquirePayItem.getGoods_id(), String.valueOf(mGoodsId))) {
                    mSelectAcquirePayItem = acquirePayItem;
                    mSelectPayItemPos = i;
                    break;
                }
            }
            if (mGoodsId != 0) {//再次唤醒该订单
                if (TextUtils.equals(mSelectAcquirePayItem.getGoods_id(), String.valueOf(mGoodsId))) {
                    pay();
                } else {
                    MyToash.ToashError(getActivity(), getString(R.string.string_vip_vip_date_off));
                }
                mGoodsId = 0;
            }
            mVipBaoyuePayAdapter.setSelectPosition(mSelectPayItemPos);
        }
    }

    private void initBottomPay(AcquirePayItem selectAcquirePayItem) {
        int original_price = selectAcquirePayItem.getOriginal_price();
        int price = selectAcquirePayItem.getPrice();
        if (original_price != 0) {
            mTxPriceTip.setText(String.format(getString(R.string.string_vip_price_tip), original_price, original_price - price));
            mTxPriceTip.setVisibility(View.VISIBLE);
        } else {
            mTxPriceTip.setVisibility(View.GONE);
        }
        mTxPrice.setText(String.valueOf(price));
    }

    private void getGoldMarque() {
        ReaderParams params = new ReaderParams(getActivity());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mMarqueeGold, json, false, new HttpUtils.ResponseListener() {
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
                    announce.setContent(String.format(getString(R.string.string_success_vip_marquee_gold), list.get(i).getMobile()) + list.get(i).getGood_title());
                    announceList.add(announce);
                }
                mMarquee.setTextArraysAndClickListener(announceList, (view, position) -> {
                });
            } else {
                mLlMarquee.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            mLlMarquee.setVisibility(View.GONE);
        }
    }

    private void pay() {
        if (mSelectAcquirePayItem != null) {
            if (Utils.isLogin(getActivity())) {
                pay(mSelectAcquirePayItem);
            } else {
                GetDialog.IsOperation(getActivity(), getString(R.string.MineNewFragment_nologin_prompt_gold), "", new GetDialog.IsOperationInterface() {
                    @Override
                    public void isOperation() {
                        MainHttpTask.getInstance().Gotologin(getActivity());
                    }
                });
            }
        }
    }

    /**
     * 获取支付渠道url,跳转支付Dialog
     */
    private void pay(AcquirePayItem item) {
        initDialog();
        mWaitDialog.setMessage(getString(R.string.string_order_create));
        mWaitDialog.showDailog();
        if (Utils.isLogin(getActivity())) {
            ReaderParams params = new ReaderParams(getActivity());
            params.putExtraParams("goods_id", item.getGoods_id());
            params.putExtraParams("mobile", ShareUitls.getString(getActivity(), PrefConst.USER_MOBILE_KAY, ""));
            params.putExtraParams("user_client_ip", StringUtils.isEmpty(mInternetIp) ? "" : mInternetIp);
            params.putExtraParams("phoneModel", Build.MANUFACTURER + "-" + Build.MODEL);
            params.putExtraParams("payment_source_id", String.valueOf(mOriginCode));
            String json = params.generateParamsJson();
            HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mNewPayVip, json, false, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            if (mWaitDialog != null) {
                                mWaitDialog.dismissDialog();
                            }
                            if (!cn.jmessage.support.qiniu.android.utils.StringUtils.isNullOrEmpty(result)) {
                                try {
                                    JSONObject jsonObj = new JSONObject(result);
                                    String url = "";
                                    String pay_url = jsonObj.getString("pay_link");
                                    String pay_link_lunxun = jsonObj.getString("pay_link_lunxun");
                                    String payUrl = ShareUitls.getString(activity, "payUrl", "");
                                    if (TextUtils.equals(ReaderConfig.pay_lunxun_domain_switch, "1") && !TextUtils.isEmpty(payUrl)) {
                                        url = payUrl + pay_link_lunxun;
                                    } else {
                                        url = pay_url;
                                    }
                                    showPayDialog(url);
                                } catch (Exception e) {
                                }
                            }
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                            if (mWaitDialog != null) {
                                mWaitDialog.dismissDialog();
                            }
                            DialogErrorVip dialogErrorVip = new DialogErrorVip();
                            dialogErrorVip.getDialogVipPop(getActivity());
                            dialogErrorVip.setVipWakeListener(new DialogErrorVip.VipErrorListener() {
                                @Override
                                public void vipErrorBack() {
                                    pay(mSelectAcquirePayItem);
                                }
                            });
                        }
                    }
            );
        } else {
            MainHttpTask.getInstance().Gotologin(getActivity());
        }
    }


    private void initDialog() {
        if (mWaitDialog != null) {
            mWaitDialog.dismissDialog();
        }
        mWaitDialog = null;
        mWaitDialog = new WaitDialog(getActivity(), "");
        mWaitDialog.setCancleable(true);
    }

    /**
     * 跳转到支付Dialog
     */
    void showPayDialog(String url) {
        View view = getActivity().getWindow().getDecorView();
        if (view != null) {
            PayDialog payDialog = new PayDialog();
            payDialog.showDialog(getActivity(), view, url);
            payDialog.setPayInterface(new PayDialog.PayInterface() {
                @Override
                public void onPayFinish() {
                    showPayFinishDialog();
                }

                @Override
                public void nativePay(String payType, String jsonData) {//跳入原生支付，（现在H5支付中并没有原生渠道）
                }

                @Override
                public void onWake() {
                }

                @Override
                public void onError() {
                    DialogErrorVip dialogErrorVip = new DialogErrorVip();
                    dialogErrorVip.getDialogVipPop(getActivity());
                    dialogErrorVip.setVipWakeListener(new DialogErrorVip.VipErrorListener() {
                        @Override
                        public void vipErrorBack() {
                            pay(mSelectAcquirePayItem);
                        }
                    });
                }
            });
        }
    }

    /**
     * 完成支付订单，关闭支付Dialog后显示该提示弹框 根据订单刷新用户信息
     */
    private void showPayFinishDialog() {
        AppPrefs.putSharedBoolean(getActivity(), PrefConst.ORDER, true);
        DialogVipComfirm dialogVipComfirm = new DialogVipComfirm();
        dialogVipComfirm.getDialogVipPop(getActivity());
        dialogVipComfirm.setmOnOpenKefuListener(() -> skipKeFuOnline());
        dialogVipComfirm.setmOnRepeatListener(() -> pay(mSelectAcquirePayItem));
    }

    /**
     * 客服链接跳转浏览器
     */
    private void skipKeFuOnline() {
        if (!com.heiheilianzai.app.utils.StringUtils.isEmpty(mKeFuOnline)) {
            startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("url", mKeFuOnline).putExtra("flag", "notitle"));

        }
    }

    /**
     * 获取用户IP
     */
    public void getIpTerritory() {
        OkHttpEngine.getInstance(getActivity()).getAsyncHttp(ReaderConfig.thirdpartyGetCity, new ResultCallback() {

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
}