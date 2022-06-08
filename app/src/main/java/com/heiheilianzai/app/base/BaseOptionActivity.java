package com.heiheilianzai.app.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.event.EditEvent;
import com.heiheilianzai.app.ui.fragment.LiushuijiluFragment;
import com.heiheilianzai.app.ui.fragment.MyCommentFragment;
import com.heiheilianzai.app.ui.fragment.OptionFragment;
import com.heiheilianzai.app.ui.fragment.RankIndexFragment;
import com.heiheilianzai.app.ui.fragment.boyin.DownMangerPhonicFragment;
import com.heiheilianzai.app.ui.fragment.boyin.ReadHistoryPhonicFragment;
import com.heiheilianzai.app.ui.fragment.book.DownMangerBookFragment;
import com.heiheilianzai.app.ui.fragment.book.ReadHistoryBookFragment;
import com.heiheilianzai.app.ui.fragment.cartoon.ReadHistoryCartoonFragment;
import com.heiheilianzai.app.ui.fragment.comic.DownMangerComicFragment;
import com.heiheilianzai.app.ui.fragment.comic.ReadHistoryComicFragment;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.view.UnderlinePageIndicatorHalf;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.PrefConst.UPDATE_JSON_KAY;
import static com.heiheilianzai.app.constant.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.constant.ReaderConfig.BAOYUE_SEARCH;
import static com.heiheilianzai.app.constant.ReaderConfig.DOWN;
import static com.heiheilianzai.app.constant.ReaderConfig.LIUSHUIJIELU;
import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.constant.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.constant.ReaderConfig.MYCOMMENT;
import static com.heiheilianzai.app.constant.ReaderConfig.PAIHANG;
import static com.heiheilianzai.app.constant.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.constant.ReaderConfig.READHISTORY;
import static com.heiheilianzai.app.constant.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.constant.ReaderConfig.WANBEN;
import static com.heiheilianzai.app.constant.ReaderConfig.getCurrencyUnit;
import static com.heiheilianzai.app.constant.ReaderConfig.getSubUnit;
import static com.heiheilianzai.app.ui.fragment.comic.ReadHistoryComicFragment.RefarchrequestCodee;

public class BaseOptionActivity extends BaseButterKnifeActivity {
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.channel_bar_male_text)
    public TextView channel_bar_male_text;
    @BindView(R.id.channel_bar_female_text)
    public TextView channel_bar_female_text;
    @BindView(R.id.channel_bar_yousheng_text)
    public TextView channel_bar_yousheng_text;
    @BindView(R.id.channel_bar_cartoon_text)
    public TextView channel_bar_cartoon_text;
    @BindView(R.id.top_channel_layout)
    public LinearLayout top_channel_layout;
    @BindView(R.id.channel_bar_indicator)
    public UnderlinePageIndicatorHalf channel_bar_indicator;
    @BindView(R.id.activity_baseoption_viewpage)
    public ViewPager activity_baseoption_viewpage;
    @BindView(R.id.activity_baseoption_edit)
    public TextView mTxEdit;

    FragmentManager fragmentManager;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    Fragment baseButterKnifeFragment1, baseButterKnifeFragment2, baseButterKnifeFragment3, baseButterKnifeFragment4;
    int OPTION;
    int PRODUCT;// 2漫画  1 小说 3
    Intent IntentFrom;
    private boolean is_top_year;
    private boolean mIsEditOpen = false;

    @Override
    public int initContentView() {
        return R.layout.activity_baseoption;
    }

    @OnClick(value = {R.id.titlebar_back, R.id.channel_bar_male_text, R.id.channel_bar_female_text, R.id.channel_bar_yousheng_text, R.id.channel_bar_cartoon_text, R.id.activity_baseoption_edit})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.channel_bar_male_text:
                setTabColor(0);
                activity_baseoption_viewpage.setCurrentItem(0);
                break;
            case R.id.channel_bar_female_text:
                setTabColor(1);
                activity_baseoption_viewpage.setCurrentItem(1);
                break;
            case R.id.channel_bar_yousheng_text:
                setTabColor(2);
                activity_baseoption_viewpage.setCurrentItem(2);
                break;
            case R.id.channel_bar_cartoon_text:
                setTabColor(3);
                activity_baseoption_viewpage.setCurrentItem(3);
                break;
            case R.id.activity_baseoption_edit:
                mIsEditOpen = !mIsEditOpen;
                if (mIsEditOpen) {
                    mTxEdit.setText(getString(R.string.splashactivity_permissions_cancle));
                } else {
                    mTxEdit.setText(getString(R.string.string_edit));
                }
                EventBus.getDefault().post(new EditEvent(mIsEditOpen));
                break;
        }
    }

    private void setTabColor(int i) {
        switch (i) {
            case 0:
                channel_bar_male_text.setTextColor(getResources().getColor(R.color.mainColor));
                channel_bar_female_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_yousheng_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_cartoon_text.setTextColor(getResources().getColor(R.color.color_606060));
                break;
            case 1:
                channel_bar_male_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_female_text.setTextColor(getResources().getColor(R.color.mainColor));
                channel_bar_yousheng_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_cartoon_text.setTextColor(getResources().getColor(R.color.color_606060));
                break;
            case 2:
                channel_bar_male_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_female_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_yousheng_text.setTextColor(getResources().getColor(R.color.mainColor));
                channel_bar_cartoon_text.setTextColor(getResources().getColor(R.color.color_606060));
                break;
            case 3:
                channel_bar_male_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_female_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_yousheng_text.setTextColor(getResources().getColor(R.color.color_606060));
                channel_bar_cartoon_text.setTextColor(getResources().getColor(R.color.mainColor));
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        IntentFrom = getIntent();
        OPTION = IntentFrom.getIntExtra("OPTION", 0);
        PRODUCT = IntentFrom.getIntExtra("PRODUCT", 0);
        is_top_year = IntentFrom.getBooleanExtra("IS_TOP_YEAR", false);
        if (OPTION != LOOKMORE) {
            String title = IntentFrom.getStringExtra("title");
            titlebar_text.setTextColor(getResources().getColor(R.color.color_3b3b3b));
            titlebar_text.setText(title);
        }
        init();
        setSubpagesRecommendationEvent();
    }


    private void init() {
        String str = ShareUitls.getString(this, UPDATE_JSON_KAY, "");
        int boYinSwitch = 0;//系统参数配置波音开关  0关  1开
        if (str.length() > 0) {
            AppUpdate mAppUpdate = new Gson().fromJson(str, AppUpdate.class);
            boYinSwitch = mAppUpdate.getBoyin_switch();
        }
        fragmentList = new ArrayList<>();
        switch (OPTION) {
            case MIANFEI:
            case WANBEN:
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
            case PAIHANG:
                int SEX = IntentFrom.getIntExtra("SEX", 1);
                String rank_type = IntentFrom.getStringExtra("rank_type");
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, rank_type, SEX);
                break;
            case PAIHANGINSEX:
                baseButterKnifeFragment1 = new RankIndexFragment(PRODUCT, OPTION, 1);
                break;
            case BAOYUE_SEARCH:
            case SHUKU:
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
                break;
            case BAOYUE:
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
                break;
            case DOWN:
                baseButterKnifeFragment1 = new DownMangerBookFragment();
                baseButterKnifeFragment2 = new DownMangerComicFragment();
                if (boYinSwitch != 0) {
                    baseButterKnifeFragment3 = new DownMangerPhonicFragment();
                    channel_bar_yousheng_text.setVisibility(View.VISIBLE);
                }
                channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                mTxEdit.setVisibility(View.VISIBLE);
                break;
            case READHISTORY:
                baseButterKnifeFragment1 = new ReadHistoryBookFragment();
                baseButterKnifeFragment2 = new ReadHistoryComicFragment();
                channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                if (boYinSwitch != 0) {
                    baseButterKnifeFragment3 = new ReadHistoryPhonicFragment();
                    channel_bar_yousheng_text.setVisibility(View.VISIBLE);
                }
                baseButterKnifeFragment4 = new ReadHistoryCartoonFragment();
                mTxEdit.setVisibility(View.VISIBLE);
                break;
            case LIUSHUIJIELU:
                channel_bar_male_text.setText(getCurrencyUnit(activity));
                channel_bar_female_text.setText(getSubUnit(activity));
                if (BuildConfig.APPLICATION_ID.equals("com.heiheilianzai.app")) {
                    baseButterKnifeFragment1 = new LiushuijiluFragment("currencyUnit");
                }
                baseButterKnifeFragment2 = new LiushuijiluFragment("subUnit");
                break;
            case LOOKMORE:
                String recommend_id = IntentFrom.getStringExtra("recommend_id");
                if (recommend_id.equals("-1")) {
                    titlebar_text.setText(LanguageUtil.getString(activity, R.string.StoreFragment_xianshimianfei));
                    baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
                    baseButterKnifeFragment2 = new OptionFragment(PRODUCT, OPTION, 2);
                } else {
                    baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, recommend_id, titlebar_text, is_top_year);
                }
                break;
            case MYCOMMENT:
                baseButterKnifeFragment2 = new MyCommentFragment(true);
                baseButterKnifeFragment1 = new MyCommentFragment(false);
                channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
        }
        if (baseButterKnifeFragment1 != null) {
            fragmentList.add(baseButterKnifeFragment1);
        } else {
            top_channel_layout.setVisibility(View.GONE);
        }
        if (baseButterKnifeFragment2 != null) {
            fragmentList.add(baseButterKnifeFragment2);
        } else {
            top_channel_layout.setVisibility(View.GONE);
        }
        if (baseButterKnifeFragment3 != null) {
            fragmentList.add(baseButterKnifeFragment3);
        }else {
            top_channel_layout.setVisibility(View.GONE);
        }
        if (baseButterKnifeFragment4 != null) {
            fragmentList.add(baseButterKnifeFragment4);
        }else {
            top_channel_layout.setVisibility(View.GONE);
        }
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        activity_baseoption_viewpage.setAdapter(myFragmentPagerAdapter);
        activity_baseoption_viewpage.setOffscreenPageLimit(fragmentList.size());
        if (OPTION == LIUSHUIJIELU) {
            boolean Extra = IntentFrom.getBooleanExtra("Extra", false);
            if (Extra) {
                activity_baseoption_viewpage.setCurrentItem(1);
            }
        } else if (OPTION == READHISTORY || OPTION == DOWN) {
            setTabColor(0);
        }
        if (baseButterKnifeFragment2 != null) {
            channel_bar_indicator.setViewPager(activity_baseoption_viewpage);
            channel_bar_indicator.setFades(false);
        }
        activity_baseoption_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                setTabColor(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RefarchrequestCodee) {//登录后刷新 阅读历史
            ((ReadHistoryBookFragment) (baseButterKnifeFragment1)).initdata();
            ((ReadHistoryComicFragment) (baseButterKnifeFragment2)).initdata();
            ((ReadHistoryPhonicFragment) (baseButterKnifeFragment3)).initdata();
            ((ReadHistoryCartoonFragment) (baseButterKnifeFragment4)).initdata();
        }
    }

    /**
     * 神策埋点 只有从首页小说、漫画进入 才埋点
     */
    private void setSubpagesRecommendationEvent() {
        try {
            switch (OPTION) {
                case MIANFEI:
                case SHUKU:
                case PAIHANGINSEX:
                case WANBEN:
                case LOOKMORE:
                    SensorsDataHelper.setSubpagesRecommendationEvent(PRODUCT == 1 ? SaVarConfig.WORKS_TYPE_BOOK : SaVarConfig.WORKS_TYPE_COMICS, IntentFrom.getStringExtra("title"));
                    break;
            }
        } catch (Exception e) {
        }
    }
}