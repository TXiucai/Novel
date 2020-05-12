package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.book.fragment.DownMangerBookFragment;
import com.heiheilianzai.app.book.fragment.ReadHistoryBookFragment;
import com.heiheilianzai.app.comic.fragment.DownMangerComicFragment;
import com.heiheilianzai.app.comic.fragment.ReadHistoryComicFragment;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.fragment.BookshelfFragment;
import com.heiheilianzai.app.fragment.LiushuijiluFragment;
import com.heiheilianzai.app.fragment.MyCommentFragment;
import com.heiheilianzai.app.fragment.OptionFragment;
import com.heiheilianzai.app.fragment.RankIndexFragment;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.UnderlinePageIndicatorHalf;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.comic.fragment.ReadHistoryComicFragment.RefarchrequestCodee;
import static com.heiheilianzai.app.config.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.config.ReaderConfig.BAOYUE_SEARCH;
import static com.heiheilianzai.app.config.ReaderConfig.DOWN;
import static com.heiheilianzai.app.config.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.config.ReaderConfig.LIUSHUIJIELU;
import static com.heiheilianzai.app.config.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.config.ReaderConfig.MANHAU;
import static com.heiheilianzai.app.config.ReaderConfig.MANHAUXIAOSHUO;
import static com.heiheilianzai.app.config.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.config.ReaderConfig.MYCOMMENT;
import static com.heiheilianzai.app.config.ReaderConfig.PAIHANG;
import static com.heiheilianzai.app.config.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.config.ReaderConfig.READHISTORY;
import static com.heiheilianzai.app.config.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.config.ReaderConfig.WANBEN;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUOMAHUA;
import static com.heiheilianzai.app.config.ReaderConfig.getCurrencyUnit;
import static com.heiheilianzai.app.config.ReaderConfig.getSubUnit;

public class BaseOptionActivity extends BaseButterKnifeActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_baseoption;
    }

    @BindView(R2.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R2.id.channel_bar_male_text)
    public TextView channel_bar_male_text;
    @BindView(R2.id.channel_bar_female_text)
    public TextView channel_bar_female_text;

    @BindView(R2.id.top_channel_layout)
    public LinearLayout top_channel_layout;


    @BindView(R2.id.channel_bar_indicator)
    public UnderlinePageIndicatorHalf channel_bar_indicator;


    @BindView(R2.id.activity_baseoption_viewpage)
    public ViewPager activity_baseoption_viewpage;


    FragmentManager fragmentManager;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    Fragment baseButterKnifeFragment1, baseButterKnifeFragment2;
    int OPTION;
    boolean PRODUCT;// false 漫画  true  小说


    @OnClick(value = {R.id.titlebar_back, R.id.channel_bar_male_text, R.id.channel_bar_female_text})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.channel_bar_male_text:
                activity_baseoption_viewpage.setCurrentItem(0);
                break;
            case R.id.channel_bar_female_text:
                activity_baseoption_viewpage.setCurrentItem(1);
                break;
        }
    }

    Intent IntentFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        IntentFrom = getIntent();

        OPTION = IntentFrom.getIntExtra("OPTION", 0);
        PRODUCT = IntentFrom.getBooleanExtra("PRODUCT", false);
        if (OPTION != LOOKMORE) {
            String title = IntentFrom.getStringExtra("title");
            titlebar_text.setText(title);
        }
        init();
    }


    private void init() {
        fragmentList = new ArrayList<>();
        switch (OPTION) {
            case MIANFEI:
            case WANBEN:

                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
             //   baseButterKnifeFragment2 = new OptionFragment(PRODUCT, OPTION, 2);
            case PAIHANG:
                int SEX = IntentFrom.getIntExtra("SEX", 1);
                String rank_type = IntentFrom.getStringExtra("rank_type");
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, rank_type, SEX);
                break;
            case PAIHANGINSEX:
                baseButterKnifeFragment1 = new RankIndexFragment(PRODUCT, OPTION, 1);
               // baseButterKnifeFragment2 = new RankIndexFragment(PRODUCT, OPTION, 2);
                break;
            case BAOYUE_SEARCH:
            case SHUKU:
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
                break;
            case BAOYUE:
                baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, 1);
                break;
            case DOWN:
                switch (GETPRODUCT_TYPE(activity)) {
                    case XIAOSHUO:
                        baseButterKnifeFragment1 = new DownMangerBookFragment();
                        break;
                    case MANHAU:
                        baseButterKnifeFragment1 = new DownMangerComicFragment();
                        break;
                    case XIAOSHUOMAHUA:
                        baseButterKnifeFragment1 = new DownMangerBookFragment();
                        baseButterKnifeFragment2 = new DownMangerComicFragment();
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;
                    case MANHAUXIAOSHUO:
                        baseButterKnifeFragment2 = new DownMangerBookFragment();
                        baseButterKnifeFragment1 = new DownMangerComicFragment();
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;
                }
                break;
            case READHISTORY:
                switch (GETPRODUCT_TYPE(activity)) {
                    case XIAOSHUO:
                        baseButterKnifeFragment1 = new ReadHistoryBookFragment();
                        break;
                    case MANHAU:
                        baseButterKnifeFragment1 = new ReadHistoryComicFragment();
                        break;
                    case XIAOSHUOMAHUA:
                        baseButterKnifeFragment1 = new ReadHistoryBookFragment();
                        baseButterKnifeFragment2 = new ReadHistoryComicFragment();
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;
                    case MANHAUXIAOSHUO:
                        baseButterKnifeFragment2 = new ReadHistoryBookFragment();
                        baseButterKnifeFragment1 = new ReadHistoryComicFragment();
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;

                }
                break;
            case LIUSHUIJIELU:
                channel_bar_male_text.setText(getCurrencyUnit(activity));
                channel_bar_female_text.setText(getSubUnit(activity));
                if(BuildConfig.APPLICATION_ID.equals("com.heiheilianzai.app")){
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
                    baseButterKnifeFragment1 = new OptionFragment(PRODUCT, OPTION, recommend_id, titlebar_text);
                }
                break;

            case MYCOMMENT:

                switch (GETPRODUCT_TYPE(activity)) {
                    case XIAOSHUO:
                        baseButterKnifeFragment1 = new MyCommentFragment(true);
                        break;
                    case MANHAU:
                        baseButterKnifeFragment1 = new MyCommentFragment(false);
                        break;
                    case XIAOSHUOMAHUA:
                        baseButterKnifeFragment1 = new MyCommentFragment(true);
                        baseButterKnifeFragment2 = new MyCommentFragment(false);
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;
                    case MANHAUXIAOSHUO:
                        baseButterKnifeFragment2 = new MyCommentFragment(true);
                        baseButterKnifeFragment1 = new MyCommentFragment(false);
                        channel_bar_female_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                        channel_bar_male_text.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                        break;
                }
                break;
        }
        if(baseButterKnifeFragment1!=null){
            fragmentList.add(baseButterKnifeFragment1);
        }else{
            top_channel_layout.setVisibility(View.GONE);
        }
        if (baseButterKnifeFragment2 != null) {
            fragmentList.add(baseButterKnifeFragment2);
        } else {
            top_channel_layout.setVisibility(View.GONE);
        }
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        activity_baseoption_viewpage.setAdapter(myFragmentPagerAdapter);
        if (OPTION == LIUSHUIJIELU) {
            boolean Extra = IntentFrom.getBooleanExtra("Extra",false);
            if (Extra) {
                activity_baseoption_viewpage.setCurrentItem(1);
            }
        }
        if (baseButterKnifeFragment2 != null) {
            channel_bar_indicator.setViewPager(activity_baseoption_viewpage);
            channel_bar_indicator.setFades(false);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RefarchrequestCodee) {//登录后刷新 阅读历史
            switch (GETPRODUCT_TYPE(activity)) {
                case XIAOSHUO:
                    ((ReadHistoryBookFragment) (baseButterKnifeFragment1)).initdata();
                    break;
                case MANHAU:
                    ((ReadHistoryComicFragment) (baseButterKnifeFragment1)).initdata();
                    break;
                case XIAOSHUOMAHUA:
                    ((ReadHistoryBookFragment) (baseButterKnifeFragment1)).initdata();
                    ((ReadHistoryComicFragment) (baseButterKnifeFragment2)).initdata();
                    break;
                case MANHAUXIAOSHUO:
                    ((ReadHistoryBookFragment) (baseButterKnifeFragment2)).initdata();
                    ((ReadHistoryComicFragment) (baseButterKnifeFragment1)).initdata();
                    break;
            }
        }
    }
}
