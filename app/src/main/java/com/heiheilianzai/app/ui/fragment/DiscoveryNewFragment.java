package com.heiheilianzai.app.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.ui.fragment.book.DiscoveryBookFragment;
import com.heiheilianzai.app.ui.fragment.comic.DiscoveryComicFragment;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.view.SizeAnmotionTextview;
import com.heiheilianzai.app.view.UnderlinePageIndicatorHalf;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.fragment_store_manhau_dp;
import static com.heiheilianzai.app.constant.ReaderConfig.fragment_store_xiaoshuo_dp;

/**
 * Created by scb on 2018/12/21.
 * 发现
 */


public class DiscoveryNewFragment extends BaseButterKnifeFragment {
    @Override
    public int initContentView() {
        return R.layout.fragment_discoverynew;
    }

    @BindView(R.id.fragment_discovery_viewpage)
    public ViewPager fragment_discovery_viewpage;

    @BindView(R.id.fragment_discovery_top)
    public RelativeLayout fragment_discovery_top;


    @BindView(R.id.fragment_discovery_indicator)
    public UnderlinePageIndicatorHalf fragment_discovery_indicator;


    @BindView(R.id.fragment_discovery_xiaoshuo)
    public SizeAnmotionTextview fragment_discovery_xiaoshuo;

    @BindView(R.id.fragment_discovery_manhau)
    public SizeAnmotionTextview fragment_discovery_manhau;

    FragmentManager fragmentManager;


    @OnClick(value = {R.id.fragment_discovery_xiaoshuo, R.id.fragment_discovery_manhau})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_discovery_xiaoshuo:
                if (chooseWho) {
                    fragment_discovery_viewpage.setCurrentItem(0);
                    chooseWho = false;
                }
                break;
            case R.id.fragment_discovery_manhau:
                if (!chooseWho) {
                    fragment_discovery_viewpage.setCurrentItem(1);
                    chooseWho = true;
                }
                break;
        }
    }

    //  boolean position = true;

    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    public boolean chooseWho;
    Fragment fragment1, fragment2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragmentManager = getChildFragmentManager();
        fragmentList = new ArrayList<>();
        fragment1 = new <Fragment>DiscoveryBookFragment();
        fragment2 = new <Fragment>DiscoveryComicFragment();
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);

        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_discovery_viewpage.setAdapter(myFragmentPagerAdapter);
        fragment_discovery_indicator.setViewPager(fragment_discovery_viewpage);
        fragment_discovery_indicator.setFades(false);

        if (NotchScreen.hasNotchScreen(activity)) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) (fragment_discovery_top.getLayoutParams());
            layoutParams.height = ImageUtil.dp2px(activity, 90);
            fragment_discovery_top.setLayoutParams(layoutParams);
        }
        int LastFragment = ShareUitls.getTab(activity, "DiscoveryNewFragment", 0);
        if (LastFragment == 1) {
            fragment_discovery_viewpage.setCurrentItem(1);
            chooseWho = true;
        }
        if (LastFragment == 1) {
            fragment_discovery_viewpage.setCurrentItem(1);
            fragment_discovery_xiaoshuo.setTextSize(fragment_store_manhau_dp);
            fragment_discovery_manhau.setTextSize(fragment_store_xiaoshuo_dp);
            chooseWho = true;
        } else {
            fragment_discovery_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
            fragment_discovery_manhau.setTextSize(fragment_store_manhau_dp);
        }
        fragment_discovery_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                chooseWho = position == 1;


                ShareUitls.putTab(activity, "DiscoveryNewFragment", position);

                if (!chooseWho) {
                    fragment_discovery_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                    fragment_discovery_manhau.setTextSize(fragment_store_manhau_dp);
                } else {
                    fragment_discovery_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                    fragment_discovery_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }
}
