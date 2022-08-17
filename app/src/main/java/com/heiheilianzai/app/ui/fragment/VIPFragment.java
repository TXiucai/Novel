package com.heiheilianzai.app.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.AcquireBaoyuePrivilegeAdapter;
import com.heiheilianzai.app.adapter.CommentVipAdapter;
import com.heiheilianzai.app.adapter.VipBaoyuePayAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AcquirePayItem;
import com.heiheilianzai.app.model.AcquirePrivilegeItem;
import com.heiheilianzai.app.model.Announce;
import com.heiheilianzai.app.model.MarqueeVipBean;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
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
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.MarqueeTextView;
import com.heiheilianzai.app.view.MarqueeTextViewClickListener;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
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

public class VIPFragment extends BaseButterKnifeFragment {
    @BindView(R.id.activity_acquire_pay_gridview)
    public RecyclerView activity_acquire_pay_gridview;
    @BindView(R.id.tx_privilege)
    public TextView mTxPrivilege;
    @BindView(R.id.activity_acquire_privilege_gridview)
    public RecyclerView activity_acquire_privilege_gridview;
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
    @BindView(R.id.scrollView)
    public ObservableScrollView mScrollView;
    private static final String ARG_PARAM = "content";
    private static final String ARG_PARAM2 = "goodsId";
    private static final String ARG_PARAM3 = "originCode";
    private String mContent;
    private int mSelectPayItemPos;
    private VipBaoyuePayAdapter vipBaoyuePayAdapter;
    private AcquirePayItem selectAcquirePayItem;
    private WaitDialog mWaitDialog;
    private String mKeFuOnline;//客服链接
    private int mGoodsId;
    private int mOriginCode = 13;
    private int WIDTH, HEIGHT;
    private String mInternetIp;//用户IP
    private SlideListener mSlideListener;
    private List<AcquirePayItem> mPayItems;

    public interface SlideListener {
        void slide(int slide);
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_vip;
    }

    public static VIPFragment newInstance(String content, int goodsId, int originCode) {
        VIPFragment fragment = new VIPFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, content);
        args.putInt(ARG_PARAM2, goodsId);
        args.putInt(ARG_PARAM3, originCode);
        fragment.setArguments(args);
        return fragment;
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
            mContent = getArguments().getString(ARG_PARAM);
            mGoodsId = getArguments().getInt(ARG_PARAM2);
            mOriginCode = getArguments().getInt(ARG_PARAM3);
        }
        WIDTH = ScreenSizeUtils.getInstance(getActivity()).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(getActivity(), 50)) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        activity_acquire_pay_gridview.setLayoutManager(linearLayoutManager);
        getVipMarque();
        initContent();
        initComentData();
        getIpTerritory();//获取用户IP
        mScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> mSlideListener.slide(scrollY));
    }

    @OnClick(value = {R.id.tx_open_vip})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.tx_open_vip:
                pay();
                break;
        }
    }

    private void initContent() {
        JSONObject jsonObj = null;
        Gson gson = new Gson();
        try {
            jsonObj = new JSONObject(mContent);
            mKeFuOnline = jsonObj.getString("kefu_online");
            mPayItems = new ArrayList<>();
            JSONArray listArray = jsonObj.getJSONArray("list");
            for (int i = 0; i < listArray.length(); i++) {
                AcquirePayItem item = gson.fromJson(listArray.getString(i), AcquirePayItem.class);
                //默认选中和再次唤起优先再次唤起
                if (TextUtils.equals(item.getGoods_id(), String.valueOf(mGoodsId))) {
                    selectAcquirePayItem = item;
                    mSelectPayItemPos = i;
                } else {
                    if (TextUtils.equals(item.getDefault_select(), "1") && selectAcquirePayItem == null) {
                        selectAcquirePayItem = item;
                        mSelectPayItemPos = i;
                    }
                }
                mPayItems.add(item);
            }
            List<AcquirePrivilegeItem> privilegeList = new ArrayList<>();
            JSONArray privilegeArray = jsonObj.getJSONArray("privilege");
            for (int i = 0; i < privilegeArray.length(); i++) {
                AcquirePrivilegeItem item = gson.fromJson(privilegeArray.getString(i), AcquirePrivilegeItem.class);
                privilegeList.add(item);
            }
            AcquireBaoyuePrivilegeAdapter baoyuePrivilegeAdapter = new AcquireBaoyuePrivilegeAdapter(getActivity());
            MyContentLinearLayoutManager linearLayoutManager = new MyContentLinearLayoutManager(getActivity());
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            activity_acquire_privilege_gridview.setLayoutManager(linearLayoutManager);
            activity_acquire_privilege_gridview.setAdapter(baoyuePrivilegeAdapter);
            baoyuePrivilegeAdapter.setNewData(privilegeList);
            if (privilegeList.size() > 0) {
                mTxPrivilege.setVisibility(View.VISIBLE);
            } else {
                mTxPrivilege.setVisibility(View.GONE);
            }
            if (mPayItems.size() > 0) {
                activity_acquire_pay_gridview.setVisibility(View.VISIBLE);
                vipBaoyuePayAdapter = new VipBaoyuePayAdapter(getActivity(), mPayItems, 0);
                activity_acquire_pay_gridview.setAdapter(vipBaoyuePayAdapter);
                if (selectAcquirePayItem == null) {
                    selectAcquirePayItem = mPayItems.get(0);
                    vipBaoyuePayAdapter.setSelectPosition(0);
                } else {
                    wakeOrder(mGoodsId);
                }
                vipBaoyuePayAdapter.setOnPayItemClickListener(new VipBaoyuePayAdapter.OnPayItemClickListener() {

                    @Override
                    public void onPayItemClick(AcquirePayItem item, int position) {
                        vipBaoyuePayAdapter.setSelectPosition(position);
                        selectAcquirePayItem = item;
                        initBottomPay(selectAcquirePayItem);
                    }
                });
                initBottomPay(selectAcquirePayItem);
            } else {
                activity_acquire_pay_gridview.setVisibility(View.GONE);
            }
        } catch (JSONException e) {
        }
    }

    public void wakeOrder(int goodId) {
        mGoodsId = goodId;
        if (mPayItems != null) {
            for (int i = 0; i < mPayItems.size(); i++) {
                AcquirePayItem acquirePayItem = mPayItems.get(i);
                if (TextUtils.equals(acquirePayItem.getGoods_id(), String.valueOf(mGoodsId))) {
                    selectAcquirePayItem = acquirePayItem;
                    mSelectPayItemPos = i;
                    break;
                }
            }
            if (mGoodsId != 0) {//再次唤醒该订单
                if (TextUtils.equals(selectAcquirePayItem.getGoods_id(), String.valueOf(mGoodsId))) {
                    pay();
                } else {
                    MyToash.ToashError(getActivity(), getString(R.string.string_vip_vip_date_off));
                }
                mGoodsId = 0;
            }
            vipBaoyuePayAdapter.setSelectPosition(mSelectPayItemPos);
        }
    }

    private void getVipMarque() {
        ReaderParams params = new ReaderParams(getActivity());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mMarqueeVip, json, false, new HttpUtils.ResponseListener() {
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
                    announce.setContent(String.format(getString(R.string.string_success_vip_marquee), list.get(i).getMobile()) + list.get(i).getGood_title());
                    announceList.add(announce);
                }
                mMarquee.setTextArraysAndClickListener(announceList, new MarqueeTextViewClickListener() {
                    @Override
                    public void onClick(View view, int position) {
                    }
                });
            } else {
                mLlMarquee.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            mLlMarquee.setVisibility(View.GONE);
        }
    }

    private void pay() {
        if (selectAcquirePayItem != null) {
            if (Utils.isLogin(getActivity())) {
                pay(selectAcquirePayItem);
            } else {
                GetDialog.IsOperation(getActivity(), getString(R.string.MineNewFragment_nologin_prompt), "", new GetDialog.IsOperationInterface() {
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
            setVIPChoiceEvent(item.getGoods_id());
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
                                    pay(selectAcquirePayItem);
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
                    DialogWakeVip dialogWakeVip = new DialogWakeVip();
                    dialogWakeVip.getDialogVipPop(getActivity());
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
                    dialogErrorVip.getDialogVipPop(getActivity());
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
     * 完成支付订单，关闭支付Dialog后显示该提示弹框 根据订单刷新用户信息
     */
    private void showPayFinishDialog() {
        AppPrefs.putSharedBoolean(getActivity(), PrefConst.ORDER, true);
        DialogVipComfirm dialogVipComfirm = new DialogVipComfirm();
        dialogVipComfirm.getDialogVipPop(getActivity());
        dialogVipComfirm.setmOnOpenKefuListener(() -> skipKeFuOnline());
        dialogVipComfirm.setmOnRepeatListener(() -> pay(selectAcquirePayItem));
    }

    /**
     * 客服链接跳转浏览器
     */
    private void skipKeFuOnline() {
        if (!com.heiheilianzai.app.utils.StringUtils.isEmpty(mKeFuOnline)) {
            startActivity(new Intent(getActivity(), AboutActivity.class).putExtra("url", mKeFuOnline).putExtra("flag", "notitle"));
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

    private void initComentData() {
        ReaderParams params = new ReaderParams(getActivity());
        params.putExtraParams("channel_id", "1");
        params.putExtraParams("page_num", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mCommentVip, json, false, new HttpUtils.ResponseListener() {
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

    private void initComment(String result) {
        try {
            OptionItem optionItem = new Gson().fromJson(result, OptionItem.class);
            List<OptionBeen> firstList;
            List<OptionBeen> list = optionItem.getList();
            if (list.size() > 6) {
                firstList = list.subList(0, 6);
            } else {
                firstList = list;
            }
            CommentVipAdapter verticalAdapter = new CommentVipAdapter(getActivity(), firstList, WIDTH, HEIGHT);
            mGv.setAdapter(verticalAdapter);
            mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String comic_id = list.get(position).comic_id;
                    String book_id = list.get(position).book_id;
                    if (comic_id != null) {
                        startActivity(ComicInfoActivity.getMyIntent(getActivity(), LanguageUtil.getString(getActivity(), R.string.refer_page_info) + " " + comic_id, comic_id));
                    } else {
                        startActivity(BookInfoActivity.getMyIntent(getActivity(), LanguageUtil.getString(getActivity(), R.string.refer_page_info) + " " + book_id, book_id));
                    }
                }
            });
        } catch (Exception e) {
            mGv.setVisibility(View.GONE);
        }
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

    @Override
    public void onDestroyView() {
        if (vipBaoyuePayAdapter != null) {
            vipBaoyuePayAdapter.cancelAllTimers();
        }
        super.onDestroyView();
    }
}
