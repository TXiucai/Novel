package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.FloatMainBean;
import com.heiheilianzai.app.model.event.StoreEvent;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.ui.activity.SearchActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.book.NewStoreBookFragment;
import com.heiheilianzai.app.ui.fragment.comic.NewStoreComicFragment;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.REFRESH_HEIGHT;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

/**
 * 首页小说，首页漫画外层搜索
 */
public abstract class StroeNewFragment extends BaseButterKnifeFragment {
    @BindView(R.id.fragment_store_top)
    public RelativeLayout fragment_newbookself_top;
    @BindView(R.id.fragment_store_search_bookname)
    public TextView fragment_store_search_bookname;
    @BindView(R.id.fragment_store_search_img)
    public ImageView fragment_store_search_img;
    @BindView(R.id.fragment_store_search)
    public RelativeLayout fragment_store_search;
    @BindView(R.id.fragment_store_point)
    public ImageView mRedPointImg;
    @BindView(R.id.main_float_img)
    ImageView mFloatImg;
    FragmentManager fragmentManager;
    public String hot_word[];
    int hot_word_size, hot_word_position;
    public boolean IS_NOTOP = true;
    public StroeNewFragment.MyHotWord myHotWord = new MyHotWord();
    BaseButterKnifeFragment fragment;
    private FloatMainBean mFloatMainBean;

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

    @OnClick(value = {R.id.fragment_store_fili, R.id.fragment_store_search, R.id.main_float_img})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_store_fili:
                mRedPointImg.setVisibility(View.GONE);
                ShareUitls.putRecommendAppTime(activity, "taskPointTime", DateUtils.currentTime());
                if (!Utils.isLogin(activity)) {
                    MainHttpTask.getInstance().Gotologin(activity);
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
            case R.id.main_float_img:
                if (mFloatMainBean != null) {
                    String url_type = mFloatMainBean.getUrl_type();// 1 内置浏览器  2外部浏览器  3 内置应用
                    String link_url = mFloatMainBean.getLink_url();
                    if (!TextUtils.isEmpty(url_type)) {
                        if (!TextUtils.isEmpty(link_url) && TextUtils.equals(url_type, "1")) {
                            startActivity(new Intent(activity, AboutActivity.class).
                                    putExtra("url", link_url));
                        } else if (!TextUtils.isEmpty(link_url) && TextUtils.equals(url_type, "2")) {
                            startActivity(new Intent(activity, AboutActivity.class).
                                    putExtra("url", link_url)
                                    .putExtra("style", "4"));
                        } else {
                            if (Utils.isLogin(activity)) {
                                startActivity(new Intent(activity, TaskCenterActivity.class));
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                        }
                        mFloatImg.setVisibility(View.GONE);
                        ShareUitls.putRecommendAppTime(activity, "floatTime", DateUtils.currentTime());
                    }
                }
                break;
        }
    }

    protected void setStoreSearchView(StoreEvent storeEvent) {
       /* if (storeEvent.Y > REFRESH_HEIGHT) {
            setBgBlack();
        } else if (storeEvent.Y <= REFRESH_HEIGHT) {
            setBgWhite();
        }*/
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
            ViewGroup.LayoutParams layoutParams = fragment_newbookself_top.getLayoutParams();
            layoutParams.height = ImageUtil.dp2px(activity, 90);
            fragment_newbookself_top.setLayoutParams(layoutParams);
        }
        getFloat(activity);
        showIsGiftPoint();
        try {
            initOption();
        } catch (Exception e) {
        }
    }

    private void showIsGiftPoint() {
        long floatTime = ShareUitls.getRecommendAppTime(activity, "taskPointTime", 0);
        long currentTimeDifferenceSecond = DateUtils.getCurrentTimeDifferenceSecond(floatTime);
        long expiredTime = currentTimeDifferenceSecond / 60 / 60;
        if (expiredTime <= 24) {
            mRedPointImg.setVisibility(View.GONE);
        } else {
            mRedPointImg.setVisibility(View.VISIBLE);
        }
    }

    private void initOption() {
        fragment = getProduct() ? new NewStoreBookFragment() : new NewStoreComicFragment();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_store_fragment, fragment).commit();
    }

    private void getFloat(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mHomeFloat, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        showFloat(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void showFloat(String result) {
        try {
            mFloatMainBean = new Gson().fromJson(result, FloatMainBean.class);
            long floatTime = ShareUitls.getRecommendAppTime(activity, "floatTime", 0);
            long currentTimeDifferenceSecond = DateUtils.getCurrentTimeDifferenceSecond(floatTime);
            long expiredTime = currentTimeDifferenceSecond / 60 / 60;
            if (expiredTime <= 24) {
                mFloatImg.setVisibility(View.GONE);
                return; //小于一天不进行展示
            }
            //内置应用且已签到不显示
            if (TextUtils.equals(mFloatMainBean.getUrl_type(), "3") && mFloatMainBean.getToday_sign_status() == 1) {
                mFloatImg.setVisibility(View.GONE);
                return;
            }
            mFloatImg.setVisibility(View.VISIBLE);
            String icon_img = mFloatMainBean.getIcon_img();
            if (!TextUtils.isEmpty(icon_img)) {
                MyPicasso.GlideImageNoSize(activity, App.getBaseUrl() + icon_img, mFloatImg);
            }
        } catch (Exception e) {

        }
    }
}
