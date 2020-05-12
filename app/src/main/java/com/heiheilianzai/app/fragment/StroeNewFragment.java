/*
package com.heiheilianzai.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.SearchActivity;

import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.book.fragment.StoreBookFragment;
import com.heiheilianzai.app.comic.eventbus.RefreshStoreComic;
import com.heiheilianzai.app.comic.eventbus.StoreEventbus;
import com.heiheilianzai.app.comic.fragment.DiscoveryComicFragment;
import com.heiheilianzai.app.comic.fragment.StoreComicFragment;
import com.heiheilianzai.app.eventbus.ToStore;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.view.SizeAnmotionTextview;
import com.heiheilianzai.app.view.UnderlinePageIndicatorHalf;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.heiheilianzai.app.config.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.config.ReaderConfig.MANHAU;
import static com.heiheilianzai.app.config.ReaderConfig.MANHAUXIAOSHUO;
import static com.heiheilianzai.app.config.ReaderConfig.REFRESH_HEIGHT;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUOMAHUA;
import static com.heiheilianzai.app.config.ReaderConfig.fragment_store_manhau_dp;
import static com.heiheilianzai.app.config.ReaderConfig.fragment_store_xiaoshuo_dp;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

*/
/**
 * Created by scb on 2018/12/21.
 *//*



public class StroeNewFragment extends BaseButterKnifeFragment {
    @Override
    public int initContentView() {
        return R.layout.fragment_storenew;
    }

    @BindView(R2.id.fragment_store_viewpage)
    public ViewPager fragment_store_viewpage;

    @BindView(R2.id.fragment_store_top)
    public RelativeLayout fragment_newbookself_top;


    @BindView(R2.id.fragment_store_manorwoman)
    public RelativeLayout fragment_store_manorwoman;


    @BindView(R2.id.fragment_store_indicator)
    public UnderlinePageIndicatorHalf indicator;

    @BindView(R2.id.fragment_store_search_bookname)
    public TextView fragment_store_search_bookname;
    @BindView(R2.id.fragment_store_search_bookname2)
    public TextView fragment_store_search_bookname2;


    @BindView(R2.id.fragment_store_search_img)
    public ImageView fragment_store_search_img;
    @BindView(R2.id.fragment_store_sex)
    public ImageView fragment_store_sex;
    @BindView(R2.id.fragment_store_search)
    public RelativeLayout fragment_store_search;
    @BindView(R2.id.fragment_store_xiaoshuo)
    public SizeAnmotionTextview fragment_store_xiaoshuo;
    @BindView(R2.id.fragment_store_manhau)
    public SizeAnmotionTextview fragment_store_manhau;
    FragmentManager fragmentManager;

    public static boolean SEX;
    public String hot_word_book[], hot_word_comic[];
    //String SearchName = "";
    int hot_word_bookSize, hot_word_bookPosition, hot_word_comicSize, hot_word_comicPosition;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ++hot_word_bookPosition;
                if (hot_word_bookPosition == hot_word_bookSize) {
                    hot_word_bookPosition = 0;
                }
                fragment_store_search_bookname.setText(hot_word_book[hot_word_bookPosition]);
                handler.sendEmptyMessageDelayed(0, 10000);
            } else {
                ++hot_word_comicPosition;
                if (hot_word_comicPosition == hot_word_comicSize) {
                    hot_word_comicPosition = 0;
                }
                fragment_store_search_bookname2.setText(hot_word_comic[hot_word_comicPosition]);
                handler.sendEmptyMessageDelayed(1, 10000);
            }


        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
        handler.removeMessages(1);
    }

    public interface Hot_word {
        void hot_word(String[] hot_word);
    }

    public class Hot_word_Book implements Hot_word {
        @Override
        public void hot_word(String[] hot_word) {
            if (hot_word != null && hot_word.length > 0) {
                hot_word_book = hot_word;
                hot_word_bookSize = hot_word_book.length;
                handler.sendEmptyMessage(0);
            }
        }
    }

    public class Hot_word_Comic implements Hot_word {
        @Override
        public void hot_word(String[] hot_word) {
            if (hot_word != null && hot_word.length > 0) {
                hot_word_comic = hot_word;
                hot_word_comicSize = hot_word_comic.length;
                handler.sendEmptyMessage(1);
            }
        }
    }

    @OnClick(value = {R.id.fragment_store_manorwoman, R.id.fragment_store_xiaoshuo, R.id.fragment_store_manhau, R.id.fragment_store_search})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_store_manorwoman:
                //  EventBus.getDefault().post(new RefreshStoreComic(!SEX));
                boolean sex = SEX;
                if (GETPRODUCT_TYPE(activity) == 3) {
                    ((StoreBookFragment) fragment1).RefreshStore(!sex);
                    ((StoreComicFragment) fragment2).RefreshStore(!sex);
                } else {
                    ((StoreBookFragment) fragment2).RefreshStore(!sex);
                    ((StoreComicFragment) fragment1).RefreshStore(!sex);
                }

                break;
            case R.id.fragment_store_xiaoshuo:
                if (chooseWho) {
                    fragment_store_viewpage.setCurrentItem(0);
                    chooseWho = false;
                }
                break;
            case R.id.fragment_store_manhau:
                if (!chooseWho) {
                    fragment_store_viewpage.setCurrentItem(1);
                    chooseWho = true;
                }
                break;
            case R.id.fragment_store_search:
                boolean PRODUCT;
                String name;
                if (fragment_store_search_bookname.getVisibility() == View.VISIBLE) {
                    name = fragment_store_search_bookname.getText().toString();
                    PRODUCT = true;
                } else {
                    name = fragment_store_search_bookname2.getText().toString();
                    PRODUCT = false;
                }
                Intent intent = new Intent(activity, SearchActivity.class).putExtra("PRODUCT", PRODUCT).putExtra("mKeyWord", name);
                startActivity(intent);
                break;

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToStore(ToStore toStore) {
        if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA) {
            if (toStore.PRODUCT == 1) {
                if (chooseWho) {
                    fragment_store_viewpage.setCurrentItem(0);
                    chooseWho = false;
                }
            } else if (toStore.PRODUCT == 2) {
                if (!chooseWho) {
                    fragment_store_viewpage.setCurrentItem(1);
                    chooseWho = true;
                }
            }
        } else if (GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
            if (toStore.PRODUCT == 1) {
                if (!chooseWho) {
                    fragment_store_viewpage.setCurrentItem(1);
                    chooseWho = true;
                }
            } else if (toStore.PRODUCT == 2) {
                if (chooseWho) {
                    fragment_store_viewpage.setCurrentItem(0);
                    chooseWho = false;
                }
            }
        }

    }
    // boolean position = true;

    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;


    public boolean chooseWho;
    public static boolean IS_NOTOP;
    public boolean IS_LEFTNOTOP;
    public boolean IS_RIGHTNOTOP;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StoreEventbus(StoreEventbus storeEventbus) {

        float ratio = Math.min(Math.max(storeEventbus.Y, 0), REFRESH_HEIGHT) / REFRESH_HEIGHT;
        float alpha = (int) (ratio * 255);
        fragment_newbookself_top.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
        if (storeEventbus.Y > REFRESH_HEIGHT) {
        */
/*    if (storeEventbus.isChooseWho()) {
                IS_LEFTNOTOP = true;
            } else {
                IS_RIGHTNOTOP = true;
            }*//*

            setBgBlack();

        } else if (storeEventbus.Y <= REFRESH_HEIGHT) {
        */
/*    if (storeEventbus.isChooseWho()) {
                IS_LEFTNOTOP = false;
            } else {
                IS_RIGHTNOTOP = false;
            }*//*

            setBgWhite();
        }
    }

    private void setBgWhite() {
        if (IS_NOTOP) {
            IS_NOTOP = false;
            fragment_store_xiaoshuo.setTextColor(Color.WHITE);
            fragment_store_manhau.setTextColor(Color.WHITE);
            fragment_store_search_bookname.setTextColor(Color.WHITE);
            fragment_store_search_bookname2.setTextColor(Color.WHITE);
            indicator.setSelectedColor(Color.WHITE);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_white);
            setStatusTextColor(false, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search);

            if (GETPRODUCT_TYPE(activity) == 3 || GETPRODUCT_TYPE(activity) == 4) {
                if (!SEX) {
                    fragment_store_sex.setImageResource(R.mipmap.comic_mall_boy_dark);
                } else {
                    fragment_store_sex.setImageResource(R.mipmap.comic_mall_girl_dark);
                }
            }
        }
    }

    private void setBgBlack() {
        if (!IS_NOTOP) {
            IS_NOTOP = true;
            fragment_store_xiaoshuo.setTextColor(Color.BLACK);
            fragment_store_manhau.setTextColor(Color.BLACK);
            fragment_store_search_bookname.setTextColor(Color.GRAY);
            fragment_store_search_bookname2.setTextColor(Color.GRAY);
            indicator.setSelectedColor(Color.BLACK);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_dark);
            setStatusTextColor(true, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search_dark);
            if (GETPRODUCT_TYPE(activity) == 3 || GETPRODUCT_TYPE(activity) == 4) {
                if (!SEX) {
                    fragment_store_sex.setImageResource(R.mipmap.comic_mall_boy);
                } else {
                    fragment_store_sex.setImageResource(R.mipmap.comic_mall_girl);
                }
            }
        }
    }


    Fragment fragment1, fragment2;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        // fragment_store_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
        // fragment_store_manhau.setTextSize(fragment_store_manhau_dp);
        fragmentManager = getChildFragmentManager();

        if (NotchScreen.hasNotchScreen(getActivity())) {
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) (fragment_newbookself_top.getLayoutParams());
            layoutParams.height = ImageUtil.dp2px(activity, 90);
            fragment_newbookself_top.setLayoutParams(layoutParams);
        }
        try {
            initOption();
        } catch (Exception e) {
        }
    }

    private void initOption() {
        fragmentList = new ArrayList<>();
        int sex = ShareUitls.getInt(activity, "SEX", 1);
        switch (GETPRODUCT_TYPE(activity)) {
            case XIAOSHUO:
                fragment1 = new <Fragment>StoreBookFragment(1, fragment_newbookself_top, fragment_store_sex, new Hot_word_Book());
                fragment2 = new <Fragment>StoreBookFragment(2, fragment_newbookself_top, fragment_store_sex, null);
                fragment_store_manhau.setText(LanguageUtil.getString(activity, R.string.storeFragment_gril));
                fragment_store_xiaoshuo.setText(LanguageUtil.getString(activity, R.string.storeFragment_boy));
                fragment_store_manorwoman.setVisibility(View.GONE);
                break;
            case MANHAU:
                fragment1 = new <Fragment>StoreComicFragment(1, fragment_newbookself_top, fragment_store_sex, new Hot_word_Comic());
                fragment2 = new <Fragment>StoreComicFragment(2, fragment_newbookself_top, fragment_store_sex, new Hot_word_Comic());
                fragment_store_xiaoshuo.setText(LanguageUtil.getString(activity, R.string.storeFragment_boy));
                fragment_store_manhau.setText(LanguageUtil.getString(activity, R.string.storeFragment_gril));
                fragment_store_manorwoman.setVisibility(View.GONE);
                fragment_store_search_bookname.setVisibility(View.GONE);
                fragment_store_search_bookname2.setVisibility(View.VISIBLE);
                break;
            case XIAOSHUOMAHUA:

                fragment1 = new <Fragment>StoreBookFragment(sex, fragment_newbookself_top, fragment_store_sex, new Hot_word_Book());
                fragment2 = new <Fragment>StoreComicFragment(sex, fragment_newbookself_top, fragment_store_sex, new Hot_word_Comic());

                fragment_store_xiaoshuo.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                fragment_store_manhau.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                break;
            case MANHAUXIAOSHUO:

                fragment2 = new <Fragment>StoreBookFragment(sex, fragment_newbookself_top, fragment_store_sex, new Hot_word_Book());
                fragment1 = new <Fragment>StoreComicFragment(sex, fragment_newbookself_top, fragment_store_sex, new Hot_word_Comic());
                fragment_store_xiaoshuo.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                fragment_store_manhau.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));

                break;

        }
        fragmentList.add(fragment1);
        fragmentList.add(fragment2);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_store_viewpage.setAdapter(myFragmentPagerAdapter);
        if (GETPRODUCT_TYPE(activity) == XIAOSHUO || GETPRODUCT_TYPE(activity) == MANHAU) {
            if (sex == 2) {
                fragment_store_viewpage.setCurrentItem(1);
                chooseWho = true;
                fragment_store_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                fragment_store_xiaoshuo.setTextSize(fragment_store_manhau_dp);
            } else {
                fragment_store_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                fragment_store_manhau.setTextSize(fragment_store_manhau_dp);
            }
        } else {
            int LastFragment = ShareUitls.getTab(activity, "StroeNewFragment", 0);
            if (LastFragment == 1) {
                fragment_store_viewpage.setCurrentItem(1);
                chooseWho = true;
                if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA) {
                    fragment_store_search_bookname.setVisibility(View.GONE);
                    fragment_store_search_bookname2.setVisibility(View.VISIBLE);
                } else {
                    fragment_store_search_bookname.setVisibility(View.VISIBLE);
                    fragment_store_search_bookname2.setVisibility(View.GONE);
                }
                fragment_store_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                fragment_store_manhau.setTextSize(fragment_store_xiaoshuo_dp);
            } else {
                if (GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
                    fragment_store_search_bookname.setVisibility(View.GONE);
                    fragment_store_search_bookname2.setVisibility(View.VISIBLE);
                } else {
                    fragment_store_search_bookname.setVisibility(View.VISIBLE);
                    fragment_store_search_bookname2.setVisibility(View.GONE);
                }
                fragment_store_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                fragment_store_manhau.setTextSize(fragment_store_manhau_dp);
            }
        }
        indicator.setViewPager(fragment_store_viewpage);
        indicator.setFades(false);
        fragment_store_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                chooseWho = position == 1;
                if (!chooseWho) {
                    if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA) {
                        ShareUitls.putTab(activity, "StroeNewFragment", position);
                        fragment_store_search_bookname.setVisibility(View.VISIBLE);
                        fragment_store_search_bookname2.setVisibility(View.GONE);
                        fragment_store_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                        fragment_store_manhau.setTextSize(fragment_store_manhau_dp);
                    } else if (GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
                        ShareUitls.putTab(activity, "StroeNewFragment", position);
                        fragment_store_search_bookname2.setVisibility(View.VISIBLE);
                        fragment_store_search_bookname.setVisibility(View.GONE);
                        fragment_store_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                        fragment_store_manhau.setTextSize(fragment_store_manhau_dp);
                    } else {
                        ShareUitls.putInt(activity, "SEX", 1);
                    }
                } else {
                    if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA) {
                        ShareUitls.putTab(activity, "StroeNewFragment", position);
                        fragment_store_search_bookname2.setVisibility(View.VISIBLE);
                        fragment_store_search_bookname.setVisibility(View.GONE);
                        fragment_store_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                        fragment_store_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                    } else if (GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
                        ShareUitls.putTab(activity, "StroeNewFragment", position);
                        fragment_store_search_bookname.setVisibility(View.VISIBLE);
                        fragment_store_search_bookname2.setVisibility(View.GONE);
                        fragment_store_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                        fragment_store_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                    } else {
                        ShareUitls.putInt(activity, "SEX", 2);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

}
*/
