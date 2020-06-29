package com.heiheilianzai.app.comic.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.SearchActivity;
import com.heiheilianzai.app.activity.TaskCenterActivity;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.comic.eventbus.StoreEventbus;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.NotchScreen;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.config.ReaderConfig.REFRESH_HEIGHT;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

/**
 * Created by scb on 2018/12/21.
 */
public class StroeNewFragmentComic extends BaseButterKnifeFragment {
    @BindView(R2.id.fragment_store_viewpage)
    public ViewPager fragment_store_viewpage;
    @BindView(R2.id.fragment_store_top)
    public RelativeLayout fragment_newbookself_top;
    @BindView(R2.id.fragment_store_search_bookname)
    public TextView fragment_store_search_bookname;
    @BindView(R2.id.fragment_store_search_img)
    public ImageView fragment_store_search_img;
    @BindView(R2.id.fragment_store_search)
    public RelativeLayout fragment_store_search;
    FragmentManager fragmentManager;
    public String hot_word_comic[];
    int hot_word_comicSize, hot_word_comicPosition;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    public static boolean IS_NOTOP_COMIC;
    Fragment fragment1;

    public interface Hot_word {
        void hot_word(String[] hot_word);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ++hot_word_comicPosition;
            if (hot_word_comicPosition == hot_word_comicSize) {
                hot_word_comicPosition = 0;
            }
            fragment_store_search_bookname.setText(hot_word_comic[hot_word_comicPosition]);
            handler.sendEmptyMessageDelayed(1, 10000);
        }
    };

    @Override
    public int initContentView() {
        return R.layout.fragment_storenew;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(1);
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

    @OnClick(value = {R.id.fragment_store_fili, R.id.fragment_store_search})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_store_fili:
                if (!MainHttpTask.getInstance().Gotologin(activity)) {
                    return;
                }
                ;
                startActivity(new Intent(activity, TaskCenterActivity.class));
                break;
            case R.id.fragment_store_search:
                boolean PRODUCT;
                String name;
                name = fragment_store_search_bookname.getText().toString();
                PRODUCT = false;
                Intent intent = new Intent(activity, SearchActivity.class).putExtra("PRODUCT", PRODUCT).putExtra("mKeyWord", name);
                startActivity(intent);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StoreEventbus(StoreEventbus storeEventbus) {
        float ratio = Math.min(Math.max(storeEventbus.Y, 0), REFRESH_HEIGHT) / REFRESH_HEIGHT;
        float alpha = (int) (ratio * 255);
        fragment_newbookself_top.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
        if (storeEventbus.Y > REFRESH_HEIGHT) {
            setBgBlack();
        } else if (storeEventbus.Y <= REFRESH_HEIGHT) {
            setBgWhite();
        }
    }

    private void setBgWhite() {
        if (IS_NOTOP_COMIC) {
            IS_NOTOP_COMIC = false;
            fragment_store_search_bookname.setTextColor(Color.WHITE);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_white);
            setStatusTextColor(false, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search);
        }
    }

    private void setBgBlack() {
        if (!IS_NOTOP_COMIC) {
            IS_NOTOP_COMIC = true;
            fragment_store_search_bookname.setTextColor(Color.GRAY);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_dark);
            setStatusTextColor(true, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search_dark);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
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
        fragment1 = new <Fragment>StoreComicFragment(fragment_newbookself_top, new Hot_word_Comic());
        fragmentList.add(fragment1);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_store_viewpage.setAdapter(myFragmentPagerAdapter);
    }
}
