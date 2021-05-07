package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.CouponAcceptFragment;
import com.heiheilianzai.app.ui.fragment.CouponUseFragment;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.view.AndroidWorkaround;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

public class CouponRecordActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.activity_coupon_my)
    public TextView mTxCouponNum;
    @BindView(R.id.activity_coupon_vp)
    public ViewPager mVpCoupon;
    @BindView(R.id.activity_coupon_accept_name)
    public TextView mTxAcceptName;
    @BindView(R.id.activity_coupon_use_name)
    public TextView mTxUseName;
    @BindView(R.id.activity_coupon_accept_view)
    public View mVAccept;
    @BindView(R.id.activity_coupon_use_view)
    public View mVUse;
    private Activity mActivity;
    private FragmentManager mFragmentManager;
    private List<Fragment> mFragmentList;
    private MyFragmentPagerAdapter myFragmentPagerAdapter;
    private CouponAcceptFragment mCouponAcceptFragment;
    private CouponUseFragment mCouponUseFragment;
    private boolean mChoseFragment = true;
    private String mUrlDescriptiton;

    @Override
    public int initContentView() {
        return R.layout.activity_coupon_record;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setStatusTextColor(false, mActivity);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        getDescrition();
        initOption();
    }

    private void getDescrition() {
        ReaderParams params = new ReaderParams(mActivity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.COUPON_DESCRIPTION, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            mUrlDescriptiton = jsonObject.getString("book_info_show");
                            int silverRemain = jsonObject.getInt("silverRemain");
                            mTxCouponNum.setText(String.valueOf(silverRemain));
                        } catch (JSONException e) {

                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @OnClick(value = {R.id.activity_coupon_accept, R.id.activity_coupon_use, R.id.titlebar_back, R.id.activity_coupon_description})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_coupon_accept:
                mVpCoupon.setCurrentItem(0);
                break;
            case R.id.activity_coupon_use:
                mVpCoupon.setCurrentItem(1);
                break;
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.activity_coupon_description:
                startActivity(new Intent(mActivity, AboutActivity.class).
                        putExtra("url", App.getBaseUrl() + "/" + mUrlDescriptiton).putExtra("title", getString(R.string.string_coupon_description)));
                break;
        }
    }

    private void initOption() {
        mFragmentManager = getSupportFragmentManager();
        mFragmentList = new ArrayList<>();
        if (mCouponAcceptFragment == null) {
            mCouponAcceptFragment = new CouponAcceptFragment();
        }
        if (mCouponUseFragment == null) {
            mCouponUseFragment = new CouponUseFragment();
        }
        mFragmentList.add(mCouponAcceptFragment);
        mFragmentList.add(mCouponUseFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(mFragmentManager, mFragmentList);
        mVpCoupon.setAdapter(myFragmentPagerAdapter);
        mVpCoupon.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    mVUse.setVisibility(View.GONE);
                    mVAccept.setVisibility(View.VISIBLE);
                    mTxAcceptName.setTextColor(getResources().getColor(R.color.color_1a1a1a));
                    mTxUseName.setTextColor(getResources().getColor(R.color.color_666666));
                    mTxAcceptName.setTextSize(16);
                    mTxUseName.setTextSize(14);
                    mChoseFragment = true;
                } else {
                    mVUse.setVisibility(View.VISIBLE);
                    mVAccept.setVisibility(View.GONE);
                    mTxAcceptName.setTextColor(getResources().getColor(R.color.color_666666));
                    mTxUseName.setTextColor(getResources().getColor(R.color.color_1a1a1a));
                    mTxAcceptName.setTextSize(14);
                    mTxUseName.setTextSize(16);
                    mChoseFragment = false;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}
