package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.model.FloatMainBean;
import com.heiheilianzai.app.model.event.CreateVipPayOuderEvent;
import com.heiheilianzai.app.model.event.StoreEvent;
import com.heiheilianzai.app.model.event.TaskRedPointEvent;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.ChannelActivity;
import com.heiheilianzai.app.ui.activity.SearchActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.fragment.book.NewStoreBookFragment;
import com.heiheilianzai.app.ui.fragment.comic.NewStoreComicFragment;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    public ImageView mFloatImg;
    @BindView(R.id.fragment_order)
    public RelativeLayout mRlOrder;
    @BindView(R.id.fragment_order_go)
    public TextView mTxOrderGo;
    @BindView(R.id.fragment_order_close)
    public ImageView mImgOrderClose;
    @BindView(R.id.tb_tittle)
    public TabLayout mTbChannel;
    @BindView(R.id.vp_channel)
    public ViewPager mVpChannel;
    @BindView(R.id.rl_channel)
    public RelativeLayout mRlChannel;
    FragmentManager fragmentManager;
    public String hot_word[];
    int hot_word_size, hot_word_position;
    public boolean IS_NOTOP = true;
    public StroeNewFragment.MyHotWord myHotWord = new MyHotWord();
    BaseButterKnifeFragment fragment;
    private FloatMainBean mFloatMainBean;
    private int mGoodId;
    private List<String> mTittlesList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ChannelHomeHolder mHolder;
    private ChannelBean mChannelBean;
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

    @OnClick(value = {R.id.fragment_store_fili, R.id.fragment_store_search, R.id.main_float_img, R.id.fragment_order_close, R.id.fragment_order_go, R.id.img_channel_more})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.img_channel_more:
                Intent intentChannel = new Intent(activity, ChannelActivity.class);
                intentChannel.putExtra("PRODUCE", getProduct());
                intentChannel.putExtra("CHANNEL", mChannelBean);
                startActivityForResult(intentChannel, 1);
                break;
            case R.id.fragment_store_fili:
                ShareUitls.putRecommendAppTime(activity, "taskPointTime", DateUtils.currentTime());
                if (!Utils.isLogin(activity)) {
                    MainHttpTask.getInstance().Gotologin(activity);
                    return;
                }
                EventBus.getDefault().post(new TaskRedPointEvent(false));
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
                    String url_type = mFloatMainBean.getUrl_type();// 1 内置浏览器  2外部浏览器（活动中心）  3 内置应用
                    String link_url = mFloatMainBean.getLink_url();
                    String link_type = mFloatMainBean.getLink_type();//1 配置连接 2活动中心  3福利中心
                    String user_parame_need = mFloatMainBean.getUser_parame_need();
                    if (Utils.isLogin(activity) && TextUtils.equals(user_parame_need, "2") && !link_url.contains("&uid=")) {
                        link_url += "&uid=" + Utils.getUID(activity);
                    }
                    if (!TextUtils.isEmpty(url_type)) {
                        if (!TextUtils.isEmpty(link_url) && TextUtils.equals(url_type, "1")) {
                            startActivity(new Intent(activity, AboutActivity.class).
                                    putExtra("url", link_url));
                        } else if (!TextUtils.isEmpty(link_url) && TextUtils.equals(url_type, "2")) {
                            startActivity(new Intent(activity, AboutActivity.class).
                                    putExtra("url", link_url)
                                    .putExtra("style", "4"));
                        } else if (!TextUtils.isEmpty(link_url) && TextUtils.equals(url_type, "3")) {
                            if (Utils.isLogin(activity)) {
                                startActivity(new Intent(activity, TaskCenterActivity.class));
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                        }
                    }
                }
                break;
            case R.id.fragment_order_close:
                SensorsDataHelper.setVIPWaitEvent(getString(R.string.string_pay_close));
                closeVipOrder();
                break;
            case R.id.fragment_order_go:
                SensorsDataHelper.setVIPWaitEvent(getString(R.string.string_pay_go));
                closeVipOrder();
                Intent intentVip = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 12);
                intentVip.putExtra("isvip", Utils.isLogin(activity));
                intentVip.putExtra("goodsId", mGoodId);
                startActivity(intentVip);
                break;
        }
    }

    private void closeVipOrder() {
        CreateVipPayOuderEvent createVipPayOuderEvent = new CreateVipPayOuderEvent();
        createVipPayOuderEvent.setCloseFlag(true);
        EventBus.getDefault().post(createVipPayOuderEvent);
        mRlOrder.setVisibility(View.GONE);
        AppPrefs.putSharedBoolean(activity, PrefConst.ORDER, false);
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
        mTbChannel.setSelectedTabIndicatorHeight(0);
        fragmentManager = getChildFragmentManager();
        if (NotchScreen.hasNotchScreen(getActivity())) {
            ViewGroup.LayoutParams layoutParams = fragment_newbookself_top.getLayoutParams();
            layoutParams.height = ImageUtil.dp2px(activity, 90);
            fragment_newbookself_top.setLayoutParams(layoutParams);
        }
        SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.string_order_go));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTxOrderGo.setText(spannableString);
        getFloat(activity, getProduct());
        getChannelData();
        showIsGiftPoint();
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

    /**
     * 未完成order显示在小说漫画页面
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showOrderUndeal(CreateVipPayOuderEvent createVipPayOuderEvent) {
        if (!createVipPayOuderEvent.isCloseFlag()) {
            mRlOrder.setVisibility(View.VISIBLE);
            mGoodId = createVipPayOuderEvent.getGoods_id();
        } else {
            mRlOrder.setVisibility(View.GONE);
        }
    }

    private void getFloat(Activity activity, boolean product) {
        ReaderParams params = new ReaderParams(activity);
        if (product) {
            params.putExtraParams("window_type", "1");
        } else {
            params.putExtraParams("window_type", "2");
        }
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
            //内置应用且已签到不显示
            if (TextUtils.equals(mFloatMainBean.getUrl_type(), "3") && mFloatMainBean.getToday_sign_status() == 1) {
                mFloatImg.setVisibility(View.GONE);
                return;
            }
            mFloatImg.setVisibility(View.VISIBLE);
            String icon_img = mFloatMainBean.getIcon_img();
            if (!TextUtils.isEmpty(icon_img)) {
                MyPicasso.GlideImageNoSize(activity, icon_img, mFloatImg);
            }
        } catch (Exception e) {
            mFloatImg.setVisibility(View.GONE);
        }
    }

    protected void getChannelData() {
        String url;
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        boolean product = getProduct();
        if (product) {
            url = ReaderConfig.mBookChannelUrl;
        } else {
            url = ComicConfig.COMIC_channel;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) {
                try {
                    mChannelBean = new Gson().fromJson(response, ChannelBean.class);
                    if (mChannelBean.getList() != null && mChannelBean.getList().size() > 0) {
                        mRlChannel.setVisibility(View.VISIBLE);
                        initChannel(mChannelBean);
                    } else {
                        mRlChannel.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    mRlChannel.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse(String ex) {
                mRlChannel.setVisibility(View.GONE);
            }
        });
    }

    private void initChannel(ChannelBean channelBean) {
        int selectChannel = 0;
        if (channelBean.getList() != null) {
            for (int i = 0; i < channelBean.getList().size(); i++) {
                ChannelBean.ListBean listBean = channelBean.getList().get(i);
                mTittlesList.add(listBean.getChannel_name());
                if (getProduct()) {
                    NewStoreBookFragment newStoreBookFragment = NewStoreBookFragment.newInstance(listBean, i);
                    mFragmentList.add(newStoreBookFragment);
                    String novel_channel_id = ShareUitls.getString(getContext(), "NOVEL_CHANNEL_ID", "");
                    if (TextUtils.equals(novel_channel_id, listBean.getId())) {
                        selectChannel = i;
                    }
                } else {
                    NewStoreComicFragment newStoreComicFragment = NewStoreComicFragment.newInstance(listBean, i);
                    mFragmentList.add(newStoreComicFragment);
                    String comic_channel_id = ShareUitls.getString(getContext(), "COMIC_CHANNEL_ID", "");
                    if (TextUtils.equals(comic_channel_id, listBean.getId())) {
                        selectChannel = i;
                    }
                }
            }
            mVpChannel.setOffscreenPageLimit(channelBean.getList().size());
            mVpChannel.setAdapter(new FragmentPagerAdapter(fragmentManager) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return mFragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return mFragmentList.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return mTittlesList.get(position);
                }
            });
            mTbChannel.setupWithViewPager(mVpChannel);
            int tabCount = mTbChannel.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tabAt = mTbChannel.getTabAt(i);
                tabAt.setCustomView(R.layout.item_channel_home);
                View customView = tabAt.getCustomView();
                mHolder = new ChannelHomeHolder(customView);
                mHolder.mTxChannel.setText(mTittlesList.get(i));
                if (i == 0) {
                    mHolder.mTxChannel.setTextSize(21);
                    mHolder.mTxChannel.setSelected(true);
                    mHolder.mTxChannel.setTypeface(Typeface.DEFAULT_BOLD);
                    mHolder.mImgBackground.setVisibility(View.VISIBLE);
                } else {
                    mHolder.mTxChannel.setSelected(false);
                    mHolder.mTxChannel.setTextSize(15);
                    mHolder.mTxChannel.setTypeface(Typeface.DEFAULT);
                    mHolder.mImgBackground.setVisibility(View.GONE);
                }
            }

            mTbChannel.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mVpChannel.setCurrentItem(tab.getPosition());
                    mHolder = new ChannelHomeHolder(tab.getCustomView());
                    mHolder.mTxChannel.setSelected(true);
                    mHolder.mTxChannel.setTextSize(21);
                    mHolder.mTxChannel.setTypeface(Typeface.DEFAULT_BOLD);
                    mHolder.mImgBackground.setVisibility(View.VISIBLE);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    mHolder = new ChannelHomeHolder(tab.getCustomView());
                    mHolder.mTxChannel.setSelected(false);
                    mHolder.mTxChannel.setTextSize(15);
                    mHolder.mTxChannel.setTypeface(Typeface.DEFAULT);
                    mHolder.mImgBackground.setVisibility(View.GONE);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            mTbChannel.getTabAt(selectChannel).select();
        }

    }

    private class ChannelHomeHolder {
        TextView mTxChannel;
        View mImgBackground;

        public ChannelHomeHolder(View itemView) {
            mTxChannel = itemView.findViewById(R.id.tx_channel);
            mImgBackground = itemView.findViewById(R.id.img_background);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == 2) {
            int position = data.getExtras().getInt("POSITION", 0);
            mTbChannel.getTabAt(position).select();
        }
    }
}
