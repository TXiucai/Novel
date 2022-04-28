package com.heiheilianzai.app.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.model.event.LogoutBoYinEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.GoldFragment;
import com.heiheilianzai.app.ui.fragment.VIPFragment;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DisplayUtils;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StatusBarUtil;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
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
public class AcquireBaoyueActivity extends BaseButterKnifeTransparentActivity implements VIPFragment.SlideListener {
    private String mAvatar;
    @BindView(R.id.activity_acquire_avatar)
    public CircleImageView activity_acquire_avatar;
    @BindView(R.id.activity_acquire_avatar2)
    public CircleImageView activity_acquire_avatar2;
    @BindView(R.id.activity_acquire_avatar_name)
    public TextView activity_acquire_avatar_name;
    @BindView(R.id.activity_acquire_avatar_name2)
    public TextView activity_acquire_avatar_name2;
    @BindView(R.id.activity_acquire_gold_balance)
    public TextView activity_acquire_gold_balance;
    @BindView(R.id.activity_acquire_avatar_desc)
    public TextView activity_acquire_avatar_desc;
    @BindView(R.id.activity_acquire_customer_service)
    public LinearLayout activity_acquire_customer_service;
    @BindView(R.id.activity_acquire_customer_service2)
    public TextView activity_acquire_customer_service2;
    @BindView(R.id.activity_acquire_avatar_isvip)
    public ImageView activity_acquire_avatar_isvip;
    @BindView(R.id.activity_acquire_avatar_isvip2)
    public ImageView activity_acquire_avatar_isvip2;
    @BindView(R.id.ll_simple)
    public LinearLayout mLlSimple;
    @BindView(R.id.rl_complete)
    public RelativeLayout mRlComplete;
    @BindView(R.id.activity_acquire_tab)
    public TabLayout activity_acquire_tab;
    @BindView(R.id.activity_acquire_vp)
    public ViewPager activity_acquire_vp;
    String mKeFuOnline;//客服链接
    private static final String ORIGIN_CODE = "origin_code";
    private int mGoodsId;
    private int mOriginCode = 13;
    private VipGoldHolder mHolder;


    @Override
    public int initContentView() {
        return R.layout.activity_acquire;
    }

    public void initView() {
        StatusBarUtil.setStatusTextColor(false, AcquireBaoyueActivity.this);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        mGoodsId = getIntent().getIntExtra("goodsId", 0);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mGoodsId = intent.getIntExtra("goodsId", 0);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
    }


    public void initData() {
        setVIPConfirmEvent();
        Intent intent = getIntent();
        mAvatar = intent.getStringExtra("avatar");
        if (TextUtils.isEmpty(mAvatar)) {
            UserInfoItem userInfoItem = App.getUserInfoItem(this);
            if (userInfoItem == null) {
            } else {
                mAvatar = userInfoItem.getAvatar();
            }
        }
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
    }

    public void initInfos(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            mKeFuOnline = jsonObj.getString("kefu_online");
            if (Utils.isLogin(this)) {
                if (!jsonObj.isNull("user")) {
                    JSONObject userObj = jsonObj.getJSONObject("user");
                    String nickName = userObj.getString("nickname");
                    String is_vip = userObj.getString("is_vip");
                    int gold = jsonObj.getInt("silver_remain");
                    activity_acquire_avatar_isvip.setVisibility(View.VISIBLE);
                    if (TextUtils.equals(is_vip, "1")) {
                        activity_acquire_avatar_isvip.setImageResource(R.mipmap.icon_isvip);
                    } else {
                        activity_acquire_avatar_isvip.setImageResource(R.mipmap.icon_novip);
                    }
                    activity_acquire_avatar_name.setText(nickName);
                    activity_acquire_avatar_name2.setText(nickName);
                    activity_acquire_avatar_desc.setText(userObj.getString("display_date"));
                    activity_acquire_gold_balance.setText(String.format(getString(R.string.BaoyueActivity_gold), gold));
                    int onlineIsNew = jsonObj.getInt("kefu_online_is_new");
                    if (onlineIsNew == 0) {//1 新客服系统 0为久客户系统
                        mKeFuOnline += "?uid=" + userObj.getString("uid");
                    }
                    MyPicasso.IoadImage(this, mAvatar, R.mipmap.hold_user_avatar, activity_acquire_avatar);
                    MyPicasso.IoadImage(this, mAvatar, R.mipmap.hold_user_avatar, activity_acquire_avatar2);
                } else {
                    activity_acquire_gold_balance.setVisibility(View.GONE);
                    activity_acquire_avatar.setBackgroundResource(R.mipmap.hold_user_avatar);
                    activity_acquire_avatar2.setBackgroundResource(R.mipmap.hold_user_avatar);
                    activity_acquire_avatar_name.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                    activity_acquire_avatar_name2.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                    resetLogin(this);
                }
            } else {
                activity_acquire_avatar.setBackgroundResource(R.mipmap.hold_user_avatar);
                activity_acquire_avatar2.setBackgroundResource(R.mipmap.hold_user_avatar);
                activity_acquire_avatar_name.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                activity_acquire_avatar_name2.setText(LanguageUtil.getString(this, R.string.BaoyueActivity_no_login));
                activity_acquire_avatar_isvip.setVisibility(View.GONE);
                activity_acquire_avatar_isvip2.setVisibility(View.GONE);
                activity_acquire_gold_balance.setVisibility(View.GONE);
            }
            initFragment(json);
            ShareUitls.putString(AcquireBaoyueActivity.this, "kefu_online", mKeFuOnline);
            if (!StringUtils.isEmpty(mKeFuOnline)) {
                activity_acquire_customer_service.setVisibility(View.VISIBLE);
                activity_acquire_customer_service2.setVisibility(View.VISIBLE);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            resetLogin(this);
        }
    }

    private List<Fragment> mFragmentList = new ArrayList<>();

    private void initFragment(String json) {
        VIPFragment vipFragment = VIPFragment.newInstance(json, mGoodsId, mOriginCode);
        mFragmentList.add(vipFragment);
        GoldFragment goldFragment = GoldFragment.newInstance(mGoodsId, mOriginCode);
        mFragmentList.add(goldFragment);
        List<String> mTittlesList = new ArrayList<>();
        mTittlesList.add(getString(R.string.AcquireBaoyueActivity_title));
        mTittlesList.add(getString(R.string.AcquireBaoyueActivity_title_gold));
        activity_acquire_vp.setOffscreenPageLimit(2);
        activity_acquire_vp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
            @NonNull
            @Override
            public Fragment getItem(int position) {
                return mFragmentList.get(position);
            }

            @Override
            public int getCount() {
                return mFragmentList.size();
            }

            @Nullable
            @Override
            public CharSequence getPageTitle(int position) {
                return mTittlesList.get(position);
            }
        });
        activity_acquire_tab.setupWithViewPager(activity_acquire_vp);
        int tabCount = activity_acquire_tab.getTabCount();
        for (int i = 0; i < tabCount; i++) {
            TabLayout.Tab tabAt = activity_acquire_tab.getTabAt(i);
            tabAt.setCustomView(R.layout.item_vip_gold);
            View customView = tabAt.getCustomView();
            mHolder = new VipGoldHolder(customView);
            mHolder.mTxChannel.setText(mTittlesList.get(i));
            if (i == 0) {
                mHolder.mTxChannel.setTextSize(17);
                mHolder.mTxChannel.setSelected(true);
                mHolder.mTxChannel.setTypeface(Typeface.DEFAULT_BOLD);
                mHolder.mTxChannel.setTextColor(getResources().getColor(R.color.color_ef966B));
            } else {
                mHolder.mTxChannel.setSelected(false);
                mHolder.mTxChannel.setTextSize(14);
                mHolder.mTxChannel.setTypeface(Typeface.DEFAULT);
                mHolder.mTxChannel.setTextColor(getResources().getColor(R.color.color_bfbfbf));
            }
        }

        activity_acquire_tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mHolder = new VipGoldHolder(tab.getCustomView());
                activity_acquire_vp.setCurrentItem(tab.getPosition());
                mHolder.mTxChannel.setSelected(true);
                mHolder.mTxChannel.setTextSize(17);
                mHolder.mTxChannel.setTypeface(Typeface.DEFAULT_BOLD);
                mHolder.mTxChannel.setTextColor(getResources().getColor(R.color.color_ef966B));
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                mHolder = new VipGoldHolder(tab.getCustomView());
                mHolder.mTxChannel.setSelected(false);
                mHolder.mTxChannel.setTextSize(14);
                mHolder.mTxChannel.setTypeface(Typeface.DEFAULT);
                mHolder.mTxChannel.setTextColor(getResources().getColor(R.color.color_bfbfbf));
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    @OnClick(value = {R.id.activity_acquire_customer_service, R.id.activity_acquire_customer_service2, R.id.tv_order_record, R.id.titlebar_back})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_acquire_customer_service:
                skipKeFuOnline();
                break;
            case R.id.activity_acquire_customer_service2:
                skipKeFuOnline();
                break;
            case R.id.tv_order_record:
                startActivity(new Intent(AcquireBaoyueActivity.this, OrderRecordActivity.class));
                break;
            case R.id.titlebar_back:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        initData();
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void slide(int slide) {
        if (slide > DisplayUtils.dp2px(AcquireBaoyueActivity.this, 70)) {
            mLlSimple.setVisibility(View.VISIBLE);
            mRlComplete.setVisibility(View.GONE);
        } else if (slide == 0) {
            mLlSimple.setVisibility(View.GONE);
            mRlComplete.setVisibility(View.VISIBLE);
        }
    }

    private class VipGoldHolder {
        TextView mTxChannel;

        public VipGoldHolder(View itemView) {
            mTxChannel = itemView.findViewById(R.id.tx_channel);
        }
    }
}