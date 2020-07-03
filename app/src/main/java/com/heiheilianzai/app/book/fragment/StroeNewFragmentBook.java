package com.heiheilianzai.app.book.fragment;

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
import com.heiheilianzai.app.activity.LoginActivity;
import com.heiheilianzai.app.activity.SearchActivity;
import com.heiheilianzai.app.activity.TaskCenterActivity;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.book.been.StoreEventbusBook;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.Utils;

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


public class StroeNewFragmentBook extends BaseButterKnifeFragment {
    @Override
    public int initContentView() {
        return R.layout.fragment_storenew;
    }

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


    public String hot_word_book[];
    //String SearchName = "";
    int hot_word_bookSize, hot_word_bookPosition;

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
            }


        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
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


    @OnClick(value = {R.id.fragment_store_fili, R.id.fragment_store_search})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_store_fili:
                if (!Utils.isLogin(activity)) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.MineNewFragment_nologin));
                    Intent intent = new Intent();
                    intent.setClass(activity, LoginActivity.class);
                    activity.startActivity(intent);
                    return;
                }
                startActivity(new Intent(activity, TaskCenterActivity.class));
                break;
            case R.id.fragment_store_search:
                boolean PRODUCT;
                String name;
                name = fragment_store_search_bookname.getText().toString();
                PRODUCT = true;
                Intent intent = new Intent(activity, SearchActivity.class).putExtra("PRODUCT", PRODUCT).putExtra("mKeyWord", name);
                startActivity(intent);
                break;

        }
    }

    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    public static boolean IS_NOTOP_BOOK;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void StoreEventbus(StoreEventbusBook storeEventbus) {
        if (storeEventbus.Y > REFRESH_HEIGHT) {
            setBgBlack();
        } else if (storeEventbus.Y <= REFRESH_HEIGHT) {
            setBgWhite();
        }
    }

    private void setBgWhite() {
        if (IS_NOTOP_BOOK) {
            IS_NOTOP_BOOK = false;
            fragment_store_search_bookname.setTextColor(Color.WHITE);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_white);
            setStatusTextColor(false, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search);
        }
    }

    private void setBgBlack() {
        if (!IS_NOTOP_BOOK) {
            IS_NOTOP_BOOK = true;
            fragment_store_search_bookname.setTextColor(Color.GRAY);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_dark);
            setStatusTextColor(true, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search_dark);
        }
    }


    Fragment fragment1;

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
        fragment1 = new <Fragment>NewStoreBookFragment( fragment_newbookself_top, new Hot_word_Book());
        fragmentList.add(fragment1);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, fragmentList);
        fragment_store_viewpage.setAdapter(myFragmentPagerAdapter);

    }

}
