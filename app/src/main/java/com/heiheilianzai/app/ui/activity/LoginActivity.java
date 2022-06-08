package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.NickNameLoginFragment;
import com.heiheilianzai.app.ui.fragment.PhoneLoginFragment;
import com.heiheilianzai.app.view.SizeAnmotionTextview;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 用户登录页
 * Created by wudeyan on 2018/7/14.
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.vp_login)
    ViewPager mVp;
    @BindView(R.id.fragment_shelf_xiaoshuo)
    public SizeAnmotionTextview fragment_shelf_xiaoshuo;
    @BindView(R.id.fragment_shelf_manhau)
    public SizeAnmotionTextview fragment_shelf_manhau;
    @BindView(R.id.fragment_manhua_select)
    public View fragment_comic_select;
    @BindView(R.id.fragment_xiaoshuo_select)
    public View fragment_novel_select;
    private List<Fragment> mFragmentList = new ArrayList<>();
    public static final String BOYIN_LOGIN_KAY = "boyin_login";
    public static Activity activity;
    private PhoneLoginFragment mPhoneLoginFragment;

    @Override
    public int initContentView() {
        return R.layout.activity_login_new;
    }

    @Override
    public void initView() {
        mPhoneLoginFragment = new PhoneLoginFragment();
        mFragmentList.add(mPhoneLoginFragment);
        mFragmentList.add(new NickNameLoginFragment());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), mFragmentList);
        mVp.setAdapter(myFragmentPagerAdapter);
        mVp.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    fragment_comic_select.setVisibility(View.GONE);
                    fragment_novel_select.setVisibility(View.VISIBLE);
                    fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.color_ff8350));
                    fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.black));
                } else {
                    fragment_comic_select.setVisibility(View.VISIBLE);
                    fragment_novel_select.setVisibility(View.GONE);
                    fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.color_ff8350));
                    fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.black));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void initData() {

    }

    @OnClick(value = {R.id.fragment_shelf_xiaoshuo, R.id.fragment_shelf_manhau,
            R.id.activity_login_close, R.id.activity_login_contract})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_login_close:
                finish();
                break;
            case R.id.fragment_shelf_xiaoshuo:
                mVp.setCurrentItem(0);
                break;
            case R.id.fragment_shelf_manhau:
                mVp.setCurrentItem(1);
                break;
            case R.id.activity_login_contract:
                startActivity(new Intent(this, AboutActivity.class));
                break;
        }
    }

    /**
     * 点击空白位置 隐藏软键盘
     */
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {

            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    public interface LoginSuccess {
        void success();

        void cancle();
    }
}
