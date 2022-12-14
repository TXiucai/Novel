package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.HomeShelfRefreshEvent;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.fragment.book.NewNovelFragment;
import com.heiheilianzai.app.ui.fragment.comic.ComicshelfFragment;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.SizeAnmotionTextview;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

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
    @BindView(R.id.fragment_shelf_xiaoshuo)
    public SizeAnmotionTextview fragment_shelf_xiaoshuo;
    @BindView(R.id.fragment_shelf_manhau)
    public SizeAnmotionTextview fragment_shelf_manhau;
    LinearLayout shelf_book_delete_btn;
    @BindView(R.id.fragment_bookself_sign)
    public TextView fragment_book_sign;
    @BindView(R.id.fragment_manhua_select)
    public View fragment_comic_select;
    @BindView(R.id.fragment_xiaoshuo_select)
    public View fragment_novel_select;

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
        if (BuildConfig.free_charge) {
            fragment_book_sign.setVisibility(View.GONE);
        }
        try {
            initOption();
        } catch (Exception e) {
        }
    }

    @OnClick(value = {R.id.fragment_shelf_xiaoshuo, R.id.fragment_shelf_manhau, R.id.fragment_bookself_sign})
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
            case R.id.fragment_bookself_sign:
                if (Utils.isLogin(activity)) {
                    startActivity(new Intent(activity, TaskCenterActivity.class));
                } else {
                    MainHttpTask.getInstance().Gotologin(activity);
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
     */
    private void initOption() {
        fragmentList = new ArrayList<>();
        addNovelFragmentNew(bookLists, shelf_book_delete_btn);
        addComicshelfFragment(baseComics, shelf_book_delete_btn);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_newbookself_viewpager.setAdapter(myFragmentPagerAdapter);
        if (NotchScreen.hasNotchScreen(getActivity()) || android.os.Build.VERSION.SDK_INT <= 23) {
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_bookself_topbar.getLayoutParams();
            layoutParams.height = ImageUtil.dp2px(activity, 60);
            fragment_bookself_topbar.setLayoutParams(layoutParams);
        }
        int LastFragment = ShareUitls.getTab(activity, "BookshelfFragment", 0);
        if (LastFragment == 1) {
            fragment_newbookself_viewpager.setCurrentItem(1);
            fragment_comic_select.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
            fragment_novel_select.setVisibility(View.GONE);
            fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.color_ff8350));
            if (BuildConfig.free_charge) {
                fragment_shelf_manhau.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_5));
            }
            fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.black));
            fragment_shelf_xiaoshuo.setBackground(null);
            chooseWho = true;
        } else {
            fragment_comic_select.setVisibility(View.GONE);
            fragment_novel_select.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
            fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.color_ff8350));
            if (BuildConfig.free_charge) {
                fragment_shelf_xiaoshuo.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_5));
            }
            fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.black));
            fragment_shelf_manhau.setBackground(null);
        }
        position = true;
        fragment_newbookself_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                chooseWho = position == 1;

                ShareUitls.putTab(activity, "BookshelfFragment", position);
                novelFragment.AllchooseAndCancleOnclick(false);
                comicshelfFragment.AllchooseAndCancleOnclick(false);
                if (!chooseWho) {
                    fragment_comic_select.setVisibility(View.GONE);
                    fragment_novel_select.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
                    fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.color_ff8350));
                    if (BuildConfig.free_charge) {
                        fragment_shelf_xiaoshuo.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_5));
                    }
                    fragment_shelf_manhau.setBackground(null);
                    fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.black));
                } else {
                    fragment_comic_select.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
                    fragment_novel_select.setVisibility(View.GONE);
                    fragment_shelf_manhau.setTextColor(getResources().getColor(R.color.color_ff8350));
                    if (BuildConfig.free_charge) {
                        fragment_shelf_manhau.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_5));
                    }
                    fragment_shelf_xiaoshuo.setTextColor(getResources().getColor(R.color.black));
                    fragment_shelf_xiaoshuo.setBackground(null);
                }
                setBookshelfRecommendationEvent();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
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
        setBookshelfRecommendationEvent();
    }

    /**
     * 神策埋点
     */
    private void setBookshelfRecommendationEvent() {
        if (!chooseWho) {
            if (novelFragment != null) {
                novelFragment.setBookshelfRecommendationEvent();
            }
        } else {
            if (comicshelfFragment != null) {
                comicshelfFragment.setBookshelfRecommendationEvent();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void addNovelFragmentNew(List<BaseBook> bookLists, LinearLayout shelf_book_delete_btn) {
        novelFragment = new <Fragment>NewNovelFragment();
        novelFragment.setBookLists(bookLists);
        novelFragment.setShelf_book_delete_btn(shelf_book_delete_btn);
        fragmentList.add(novelFragment);
    }

    private void addComicshelfFragment(List<BaseComic> bookLists, LinearLayout shelf_book_delete_btn) {
        comicshelfFragment = new <Fragment>ComicshelfFragment();
        comicshelfFragment.setBookLists(baseComics);
        comicshelfFragment.setShelf_book_delete_btn(shelf_book_delete_btn);
        fragmentList.add(comicshelfFragment);
    }
}