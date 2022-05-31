package com.heiheilianzai.app.ui.activity.cartoon;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.cartoon.CartoonChapterAdapter;
import com.heiheilianzai.app.adapter.cartoon.StoreCartoonAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseWarmStartActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.cartoon.CartoonChapter;
import com.heiheilianzai.app.model.cartoon.CartoonInfo;
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogCouponNotMore;
import com.heiheilianzai.app.utils.DialogRegister;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jaeger.library.StatusBarUtil;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartoonInfoActivity extends BaseWarmStartActivity {
    private static String CARTOON_ID_EXT_KAY = "CARTOON_ID";
    @BindView(R.id.titlebar_text)
    public TextView mTxTitle;
    @BindView(R.id.ll_gold)
    public LinearLayout mLlGold;
    @BindView(R.id.tx_gold_num)
    public TextView mTxGoldNum;
    @BindView(R.id.tx_gold_open)
    public TextView mTxGoldOpen;
    @BindView(R.id.tb_open)
    public ToggleButton mTbOpen;
    @BindView(R.id.ll_vip)
    public LinearLayout mLlVip;
    @BindView(R.id.ry_chapter)
    public RecyclerView mRyChapter;
    @BindView(R.id.tx_title_cartoon)
    public TextView mTxCartoonTitle;
    @BindView(R.id.tx_description_cartoon)
    public TextView mTxCartoonDes;
    @BindView(R.id.tx_tag_cartoon)
    public TextView mTxCartoonTag;
    @BindView(R.id.tx_time_cartoon)
    public TextView mTxCartoonTime;
    @BindView(R.id.gv_guess)
    public AdaptionGridView mGv;
    private String mCartoonId;
    private Activity mActivity;
    private CartoonInfo mCartoonInfo;
    private Gson mGson;
    private List<CartoonChapter> mCartoonChapters;
    private CartoonChapterAdapter mCartoonChapterAdapter;
    private String mPrice;
    private int mGoldNum;
    private CartoonChapter mChapterItem;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //侵染状态栏
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_cartooninfo);
        ButterKnife.bind(this);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {                                  //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));                   //需要在setContentView()方法后面执行
        }
        mGson = new Gson();
        mCartoonChapters = new ArrayList<>();
        mActivity = this;
        mCartoonId = getIntent().getStringExtra(CARTOON_ID_EXT_KAY);
        initView();
        getInfo();
    }

    private void initView() {
        mGoldNum = AppPrefs.getSharedInt(mActivity, PrefConst.COUPON, 0);
        mTxGoldNum.setText(String.valueOf(mGoldNum));
        if (AppPrefs.getSharedBoolean(mActivity, "comicOpen_ToggleButton", false)) {
            mTbOpen.setToggleOn();
        } else {
            mTbOpen.setToggleOff();
        }
        MyContentLinearLayoutManager linearLayoutManager = new MyContentLinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRyChapter.setLayoutManager(linearLayoutManager);
        mCartoonChapterAdapter = new CartoonChapterAdapter(mCartoonChapters, mActivity);
        mRyChapter.setAdapter(mCartoonChapterAdapter);
    }

    @OnClick(value = {R.id.tx_vip_charge, R.id.tx_gold_charge, R.id.tx_gold_open, R.id.titlebar_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.tx_vip_charge:
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(mActivity, LanguageUtil.getString(mActivity, R.string.refer_page_mine), 4);
                myIntent.putExtra("isvip", Utils.isLogin(mActivity));
                myIntent.putExtra("type", 0);
                mActivity.startActivity(myIntent);
                break;
            case R.id.tx_gold_charge:
                Intent intent = AcquireBaoyueActivity.getMyIntent(mActivity, LanguageUtil.getString(mActivity, R.string.refer_page_mine), 4);
                intent.putExtra("isvip", Utils.isLogin(mActivity));
                intent.putExtra("type", 1);
                mActivity.startActivity(intent);
                break;
            case R.id.tx_gold_open:
                openCoupon(mChapterItem, mPrice, mGoldNum);
                break;
        }
    }

    private void getChapter() {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_chapter, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initChapter(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void initChapter(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            mPrice = jsonObject.getString("coupon_pay_price");
            mCartoonChapters.addAll(mGson.fromJson(jsonObject.getString("chapter_list"), new TypeToken<List<CartoonChapter>>() {
            }.getType()));
            if (mCartoonChapters != null && mCartoonChapters.size() > 0) {
                mChapterItem = mCartoonChapters.get(0);
                mChapterItem.setSelect(true);
                mCartoonChapterAdapter.notifyDataSetChanged();
                mCartoonChapterAdapter.setmOnBackChapterListener((cartoonChapter, position) -> {
                    for (int i = 0; i < mCartoonChapters.size(); i++) {
                        CartoonChapter chapter = mCartoonChapters.get(i);
                        if (i != position) {
                            chapter.setSelect(false);
                        } else {
                            chapter.setSelect(true);
                        }
                    }
                    mChapterItem = cartoonChapter;
                    mCartoonChapterAdapter.notifyDataSetChanged();
                    checkIsCoupon(cartoonChapter);
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playVideo(CartoonChapter cartoonChapter) {
        updateRecord();
    }

    private void checkIsVip(CartoonChapter chapterItem) {
        String is_vip = chapterItem.getIs_vip();
        if (is_vip != null && is_vip.equals("1") && !App.isVip(mActivity)) {
            mLlVip.setVisibility(View.VISIBLE);
            mLlGold.setVisibility(View.GONE);
        } else {
            playVideo(chapterItem);
        }
    }

    private void checkIsCoupon(CartoonChapter chapterItem) {
        String is_book_coupon_pay = chapterItem.getIs_book_coupon_pay();
        String is_vip = chapterItem.getIs_vip();
        String is_limited_free = chapterItem.getIs_limited_free();
        if (!StringUtils.isEmpty(is_limited_free) && TextUtils.equals(is_limited_free, "1")) {
            playVideo(chapterItem);
        } else {
            if (Utils.isLogin(mActivity)) {
                if (chapterItem.isIs_buy_status()) {
                    playVideo(chapterItem);
                } else {
                    if ((is_book_coupon_pay != null && is_book_coupon_pay.equals("1"))) {//需要金币
                        if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {//需要会员
                            if (App.isVip(mActivity)) {
                                playVideo(chapterItem);
                            } else {
                                showLimitDialog(chapterItem);
                            }
                        } else {
                            showLimitDialog(chapterItem);
                        }
                    } else {
                        checkIsVip(chapterItem);
                    }
                }
            } else {
                if (TextUtils.equals(is_book_coupon_pay, "1") || TextUtils.equals(is_vip, "1")) {
                    DialogRegister dialogRegister = new DialogRegister();
                    dialogRegister.setFinish(true);
                    dialogRegister.getDialogLoginPop(mActivity);
                    dialogRegister.setmRegisterBackListener(new DialogRegister.RegisterBackListener() {
                        @Override
                        public void onRegisterBack(boolean isSuccess) {
                            if (isSuccess) {
                                checkIsCoupon(chapterItem);
                            }
                        }
                    });
                } else {
                    playVideo(chapterItem);
                }
            }
        }
    }

    private void showLimitDialog(CartoonChapter chapterItem) {

        String format = String.format(mActivity.getResources().getString(R.string.dialog_coupon_open), mPrice);
        SpannableString spannableString = new SpannableString(format);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTxGoldOpen.setText(spannableString);

        //开启自动解锁并需要金币时
        if (AppPrefs.getSharedBoolean(mActivity, "comicOpen_ToggleButton", false)) {
            if (mGoldNum >= Integer.valueOf(mPrice)) {
                openCoupon(chapterItem, mPrice, mGoldNum);
            } else {
                DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                dialogCouponNotMore.getDialogVipPop(mActivity, true);
            }
        } else {
            mLlGold.setVisibility(View.VISIBLE);
            mLlVip.setVisibility(View.GONE);
        }
        mTbOpen.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(mActivity, "comicOpen_ToggleButton", on);
                if (on) {
                    if (!Utils.isLogin(mActivity)) {
                        MainHttpTask.getInstance().Gotologin(mActivity);
                    }
                }
            }
        });
    }

    private void getInfo() {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_info, json, true, new HttpUtils.ResponseListener() {
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

    private void initInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject video = jsonObject.getJSONObject("video");
            mCartoonInfo = mGson.fromJson(video.toString(), CartoonInfo.class);
            if (mCartoonInfo != null) {
                getChapter();
                mTxTitle.setText(mCartoonInfo.getName());
                mTxCartoonTitle.setText(mCartoonInfo.getName());
                mTxCartoonDes.setText(mCartoonInfo.getDescription());
                mTxCartoonTime.setText(mCartoonInfo.getUpdated_at());
                String tags = "";
                if (mCartoonInfo.getTag() != null && mCartoonInfo.getTag().size() > 0) {
                    for (int i = 0; i < mCartoonInfo.getTag().size(); i++) {
                        String tag = mCartoonInfo.getTag().get(i);
                        tags = tags + (tag + " ");
                    }
                    mTxCartoonTag.setText(tags);
                    mTxCartoonTag.setVisibility(View.VISIBLE);
                } else {
                    mTxCartoonTag.setVisibility(View.GONE);
                }
            }
            List<StroreCartoonLable.Cartoon> cartoonList = mGson.fromJson(jsonObject.getString("list"), new TypeToken<List<StroreCartoonLable.Cartoon>>() {
            }.getType());
            if (cartoonList != null) {
                int width = ScreenSizeUtils.getInstance(mActivity).getScreenWidth() / 2;
                int height = width * 2 / 3;
                StoreCartoonAdapter storeCartoonAdapter = new StoreCartoonAdapter(cartoonList, mActivity, width, height);
                mGv.setAdapter(storeCartoonAdapter);
                mGv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mCartoonId = cartoonList.get(position).video_id;
                        getInfo();
                    }
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 进入动漫简介页必传参数
     *
     * @param context
     * @param referPage 神策埋点数据从哪个页面进入
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String videoId) {
        Intent intent = new Intent(context, CartoonInfoActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(CARTOON_ID_EXT_KAY, videoId);
        return intent;
    }

    public void openCoupon(CartoonChapter chapterItem, String couponPrice, int couponNum) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_chapter_unlock, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                        AppPrefs.putSharedInt(mActivity, PrefConst.COUPON, couponNum - Integer.valueOf(couponPrice));
                        playVideo(chapterItem);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void updateRecord() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("video", mCartoonId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_play_log, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
