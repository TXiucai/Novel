package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
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
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.HomeShelfRefreshEvent;
import com.heiheilianzai.app.ui.fragment.book.NewNovelFragment;
import com.heiheilianzai.app.ui.fragment.comic.ComicshelfFragment;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.view.SizeAnmotionTextview;
import com.heiheilianzai.app.view.UnderlinePageIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.constant.ReaderConfig.MANHAU;
import static com.heiheilianzai.app.constant.ReaderConfig.MANHAUXIAOSHUO;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUOMAHUA;
import static com.heiheilianzai.app.constant.ReaderConfig.fragment_store_manhau_dp;
import static com.heiheilianzai.app.constant.ReaderConfig.fragment_store_xiaoshuo_dp;

/**
 * 首页书架 界面
 * 为解决书架公告及推荐漫画小说，长时间不刷新，又避免每次切换界面刷新数据频繁访问服务器
 * 经过商议，通过时间戳记录第一次打开并刷新的时间，每次时间间隔5分钟后，再次进来只刷新推荐小说漫画和公告数据。
 * Created by scb on 2018/12/21.
 */
public class BookshelfFragment extends BaseButterKnifeFragment {
    private static final long CHILL_TIME = 300;//秒 5分钟
    @BindView(R.id.fragment_shelf_viewpage)
    public ViewPager fragment_newbookself_viewpager;
    @BindView(R.id.fragment_bookself_topbar)
    public RelativeLayout fragment_bookself_topbar;
    @BindView(R.id.fragment_shelf_indicator)
    public UnderlinePageIndicator indicator;
    @BindView(R.id.fragment_shelf_xiaoshuo)
    public SizeAnmotionTextview fragment_shelf_xiaoshuo;
    @BindView(R.id.fragment_shelf_manhau)
    public SizeAnmotionTextview fragment_shelf_manhau;
    LinearLayout shelf_book_delete_btn;

    List<BaseBook> bookLists;
    List<BaseComic> baseComics;
    boolean position = true;
    public boolean chooseWho;//true漫画  false小说
    FragmentManager fragmentManager;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    NewNovelFragment novelFragment = null; //首页书架小说
    ComicshelfFragment comicshelfFragment = null;  //首页书架漫画
    long refreshTime;//毫秒时间戳

    public BookshelfFragment() {
    }

    @SuppressLint("ValidFragment")
    public BookshelfFragment(List<BaseBook> bookLists, List<BaseComic> baseComics, LinearLayout shelf_book_delete_btn) {
        this.bookLists = bookLists;
        this.baseComics = baseComics;
        this.shelf_book_delete_btn = shelf_book_delete_btn;
        MyToash.Log("shelf_book_delete_btn", (shelf_book_delete_btn == null) + "");
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_new_bookshelf;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        fragmentManager = getChildFragmentManager();
        refreshTime = DateUtils.currentTime();
        try {
            initOption();
        } catch (Exception e) {
        }
    }

    @OnClick(value = {R.id.fragment_shelf_xiaoshuo, R.id.fragment_shelf_manhau})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_shelf_xiaoshuo:
                if (chooseWho) {
                    fragment_newbookself_viewpager.setCurrentItem(0);
                    chooseWho = false;
                }
                break;
            case R.id.fragment_shelf_manhau:
                if (!chooseWho) {
                    fragment_newbookself_viewpager.setCurrentItem(1);
                    chooseWho = true;
                }
                break;
        }
    }

    public void refarsh() {
        if (novelFragment != null) {
            novelFragment.refarsh();
        }
        if (comicshelfFragment != null) {
            comicshelfFragment.refarsh();
        }
    }

    /**
     * 这里写的有点复杂可能和之前产品变动有关
     * 参考{@link ReaderConfig#GETPRODUCT_TYPE}
     */
    private void initOption() {
        fragmentList = new ArrayList<>();
        switch (GETPRODUCT_TYPE(activity)) {
            case XIAOSHUO:
                addNovelFragmentNew(bookLists,shelf_book_delete_btn);
                break;
            case MANHAU:
                addComicshelfFragment(baseComics, shelf_book_delete_btn);
                break;
            case XIAOSHUOMAHUA:
                addNovelFragmentNew(bookLists,shelf_book_delete_btn);
                addComicshelfFragment(baseComics, shelf_book_delete_btn);
                break;
            case MANHAUXIAOSHUO:
                addComicshelfFragment(baseComics, shelf_book_delete_btn);
                addNovelFragmentNew(bookLists,shelf_book_delete_btn);
                fragment_shelf_xiaoshuo.setText(LanguageUtil.getString(activity, R.string.noverfragment_manhua));
                fragment_shelf_manhau.setText(LanguageUtil.getString(activity, R.string.noverfragment_xiaoshuo));
                break;
        }
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_newbookself_viewpager.setAdapter(myFragmentPagerAdapter);
        if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA || GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
            if (NotchScreen.hasNotchScreen(getActivity()) || android.os.Build.VERSION.SDK_INT <= 23) {
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_bookself_topbar.getLayoutParams();
                layoutParams.height = ImageUtil.dp2px(activity, 90);
                fragment_bookself_topbar.setLayoutParams(layoutParams);
            }
            int LastFragment = ShareUitls.getTab(activity, "BookshelfFragment", 0);
            if (LastFragment == 1) {
                fragment_newbookself_viewpager.setCurrentItem(1);
                fragment_shelf_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                fragment_shelf_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                chooseWho = true;
            } else {
                fragment_shelf_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                fragment_shelf_manhau.setTextSize(fragment_store_manhau_dp);
            }
            indicator.setViewPager(fragment_newbookself_viewpager);
            indicator.setFades(false);
            position = true;
            fragment_newbookself_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                }

                @Override
                public void onPageSelected(int position) {
                    chooseWho = position == 1;
                    if (GETPRODUCT_TYPE(activity) == XIAOSHUOMAHUA || GETPRODUCT_TYPE(activity) == MANHAUXIAOSHUO) {
                        ShareUitls.putTab(activity, "BookshelfFragment", position);
                        novelFragment.AllchooseAndCancleOnclick(false);
                        comicshelfFragment.AllchooseAndCancleOnclick(false);
                    }
                    if (!chooseWho) {
                        fragment_shelf_xiaoshuo.setTextSize(fragment_store_xiaoshuo_dp);
                        fragment_shelf_manhau.setTextSize(fragment_store_manhau_dp);
                    } else {
                        fragment_shelf_xiaoshuo.setTextSize(fragment_store_manhau_dp);
                        fragment_shelf_manhau.setTextSize(fragment_store_xiaoshuo_dp);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {
                }
            });
        } else {
            fragment_bookself_topbar.setVisibility(View.GONE);
        }
    }

    public interface DeleteBook {
        void success();

        void fail();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void homeShelfRefresh(HomeShelfRefreshEvent refreshBookInfo) {
        if (DateUtils.getCurrentTimeDifferenceSecond(refreshTime) >= CHILL_TIME) {
            refreshTime = DateUtils.currentTime();
            refarsh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

   private void addNovelFragmentNew(List<BaseBook> bookLists, LinearLayout shelf_book_delete_btn){
       novelFragment = new <Fragment>NewNovelFragment();
       novelFragment.setBookLists(bookLists);
       novelFragment.setShelf_book_delete_btn(shelf_book_delete_btn);
       fragmentList.add(novelFragment);
   }

   private void addComicshelfFragment(List<BaseComic> bookLists, LinearLayout shelf_book_delete_btn){
       comicshelfFragment = new <Fragment>ComicshelfFragment();
       comicshelfFragment.setBookLists(baseComics);
       comicshelfFragment.setShelf_book_delete_btn(shelf_book_delete_btn);
       fragmentList.add(comicshelfFragment);
   }
}
