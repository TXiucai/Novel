package com.heiheilianzai.app.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.heiheilianzai.app.book.fragment.NewStoreBookFragment;
import com.heiheilianzai.app.comic.fragment.NewStoreComicFragment;
import com.heiheilianzai.app.eventbus.StoreEvent;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.config.ReaderConfig.REFRESH_HEIGHT;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

/**
 * 首页小说，首页漫画外层搜索
 */
public abstract class StroeNewFragment extends BaseButterKnifeFragment {
    @BindView(R2.id.fragment_store_top)
    public RelativeLayout fragment_newbookself_top;
    @BindView(R2.id.fragment_store_search_bookname)
    public TextView fragment_store_search_bookname;
    @BindView(R2.id.fragment_store_search_img)
    public ImageView fragment_store_search_img;
    @BindView(R2.id.fragment_store_search)
    public RelativeLayout fragment_store_search;
    FragmentManager fragmentManager;
    public String hot_word[];
    int hot_word_size, hot_word_position;
    public boolean IS_NOTOP;
    public StroeNewFragment.MyHotWord myHotWord = new MyHotWord();
    BaseButterKnifeFragment fragment;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                ++hot_word_position;
                if (hot_word_position == hot_word_size) {
                    hot_word_position = 0;
                }
                fragment_store_search_bookname.setText(hot_word[hot_word_position]);
                handler.sendEmptyMessageDelayed(0, 10000);
            }
        }
    };

    @Override
    public int initContentView() {
        return R.layout.fragment_storenew;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeMessages(0);
    }

    public interface HotWord {
        void hot_word(String[] hotWord);
    }

    public class MyHotWord implements HotWord {
        @Override
        public void hot_word(String[] hotWord) {
            if (hotWord != null && hotWord.length > 0) {
                hot_word = hotWord;
                hot_word_size = hot_word.length;
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
                String name;
                name = fragment_store_search_bookname.getText().toString();
                Intent intent = new Intent(activity, SearchActivity.class).putExtra("PRODUCT", getProduct()).putExtra("mKeyWord", name);
                startActivity(intent);
                break;
        }
    }

    protected void setStoreSearchView(StoreEvent storeEvent) {
        if (storeEvent.Y > REFRESH_HEIGHT) {
            setBgBlack();
        } else if (storeEvent.Y <= REFRESH_HEIGHT) {
            setBgWhite();
        }
    }

    protected abstract boolean getProduct();

    protected void setBgWhite() {
        if (IS_NOTOP) {
            IS_NOTOP = false;
            fragment_store_search_bookname.setTextColor(Color.WHITE);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_white);
            setStatusTextColor(false, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search);
        }
    }

    protected void setBgBlack() {
        if (!IS_NOTOP) {
            IS_NOTOP = true;
            fragment_store_search_bookname.setTextColor(Color.GRAY);
            fragment_store_search_img.setImageResource(R.mipmap.main_search_dark);
            setStatusTextColor(true, activity);
            fragment_store_search.setBackgroundResource(R.drawable.shape_comic_store_search_dark);
        }
    }

    @Override
    protected void initView() {
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

    @Override
    protected void initData() {
        if (fragment != null) {
            fragment.initData();
        }
    }

    private void initOption() {
        fragment = getProduct() ? new NewStoreBookFragment() : new NewStoreComicFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_store_fragment, fragment).commit();
    }
}
