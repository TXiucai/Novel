package com.heiheilianzai.app.ui.activity;

import static com.heiheilianzai.app.constant.ReaderConfig.syncDevice;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.core.Controller;
import com.app.hubert.guide.model.GuidePage;
import com.app.hubert.guide.model.RelativeGuide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.DownloadBoyinService;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.localPush.LoaclPushBean;
import com.heiheilianzai.app.localPush.NotificationUtil;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BottomIconMenu;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.PrivilegeWelfare;
import com.heiheilianzai.app.model.VipOrderBean;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.AcceptMineFragment;
import com.heiheilianzai.app.model.event.CreateVipPayOuderEvent;
import com.heiheilianzai.app.model.event.ExitAppEvent;
import com.heiheilianzai.app.model.event.FreshUrlEvent;
import com.heiheilianzai.app.model.event.HomeShelfRefreshEvent;
import com.heiheilianzai.app.model.event.LogoutEvent;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.RegisterLoginWelfareEvent;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.model.event.comic.BoyinInfoEvent;
import com.heiheilianzai.app.ui.dialog.HomeNoticeDialog;
import com.heiheilianzai.app.ui.dialog.HomeNoticePhotoDialog;
import com.heiheilianzai.app.ui.dialog.MyPoPwindow;
import com.heiheilianzai.app.ui.fragment.BookshelfFragment;
import com.heiheilianzai.app.ui.fragment.DiscoveryNewFragment;
import com.heiheilianzai.app.ui.fragment.HomeBoYinFragment;
import com.heiheilianzai.app.ui.fragment.MineNewFragment;
import com.heiheilianzai.app.ui.fragment.book.StroeNewFragmentBook;
import com.heiheilianzai.app.ui.fragment.cartoon.StroeNewFragmentCartoon;
import com.heiheilianzai.app.ui.fragment.comic.StroeNewFragmentComic;
import com.heiheilianzai.app.utils.ADHelper;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.DialogBecomeVip;
import com.heiheilianzai.app.utils.DialogExpirerdVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ListDomainSort;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.RecomendApp;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.utils.decode.AESUtil;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.CustomScrollViewPager;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

public class MainActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.RadioGroup)
    public RadioGroup mRadioGroup;
    @BindView(R.id.fragment_home_container)
    public CustomScrollViewPager customScrollViewPage;
    public long mExitTime = 0;
    @BindView(R.id.main_menu_layout)
    public LinearLayout mNavigationView;
    @BindView(R.id.home_novel_layout)
    public RadioButton home_novel_layout;
    @BindView(R.id.home_store_layout)
    public RadioButton home_store_layout;
    @BindView(R.id.home_discovery_layout)
    public RadioButton home_discovery_layout;
    @BindView(R.id.home_mine_layout)
    public RadioButton home_mine_layout;
    @BindView(R.id.home_store_layout_comic)
    public RadioButton home_store_layout_comic;
    @BindView(R.id.shelf_book_delete_btn)
    public LinearLayout shelf_book_delete_btn;
    @BindView(R.id.view_guide_down)
    View view_guide_down;
    @BindView(R.id.fl_welfare)
    FrameLayout fl_welfare;
    @BindView(R.id.iv_welfare_close)
    ImageView iv_welfare_close;
    @BindView(R.id.tv_welfare_text)
    TextView tv_welfare_text;
    @BindView(R.id.tv_welfare_go)
    TextView tv_welfare_go;

    private List<Fragment> mFragmentList;
    private AppUpdate mAppUpdate;
    List<BaseBook> bookLists;
    List<BaseComic> comicList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    private int possition = 5;

    public static Activity activity;
    StroeNewFragmentBook stroeNewFragmentBook;//首页漫画
    StroeNewFragmentComic stroeNewFragmentComic;//首页小说
    StroeNewFragmentCartoon stroeNewFragmentCartoon;//首页动漫
    HomeBoYinFragment homeBoYinFragment;
    DiscoveryNewFragment discoveryFragment;
    MineNewFragment mineFragment;
    Controller controller;
    boolean loadYouSheng;
    private List<HomeNotice> mHomeNoticeText = new ArrayList<>();
    private List<HomeNotice> mHomeNoticePhoto = new ArrayList<>();
    private boolean mIsShowTwoNotice = true;
    private boolean mIsFirstTextNotice;
    private Dialog popupWindow;

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {//版本更新
                popupWindow = new UpdateApp().getAppUpdatePop(MainActivity.this, mAppUpdate);
                ReaderConfig.AppUpdate = null;
            } else {//签到弹框
                new MyPoPwindow().getSignPop(activity);
            }
        }
    };

    @Override
    public int initContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        activity = this;
        permission(activity);
        initView();
        initData();
        setOpenTimeEvent();
        getLocalPushData();
        getPayUrl();
    }

    /**
     * 支付url测速
     */
    private void getPayUrl() {
        if (TextUtils.equals(ReaderConfig.pay_lunxun_domain_switch, "1")) {
            List<String> pay_lunxun_domain = ReaderConfig.pay_lunxun_domain;
            ListDomainSort.getSortUtil().startSort(pay_lunxun_domain, 1, true, ListDomainSort.DOMAIN_TYPE, new ListDomainSort.CallHostsDomain() {
                @Override
                public void callHostList(List<String> newList) {
                    if (newList != null && newList.size() > 0) {
                        ShareUitls.putString(activity, "payUrl", newList.get(0));
                    }
                }
            });
        }
    }

    private void getAd() {
        ADHelper adHelper = new ADHelper();
        adHelper.getComicBottomAD(activity);
        adHelper.getWebTopAD(activity);
        adHelper.getWebViewAD(activity);
        adHelper.getComicTopAD(activity);
        adHelper.getComicChapterAd(activity);
    }

    private void initView() {
        setBottomButtonImgs();
        initFragmentView();
        ShowPOP();
    }

    /**
     * 设置底部按钮图片
     */
    private void setBottomButtonImgs() {
        setBottomButtonImg(home_store_layout, R.drawable.selector_home_store);
        setBottomButtonImg(home_store_layout_comic, R.drawable.selector_home_store_comic);
        setBottomButtonImg(home_novel_layout, R.drawable.selector_home_novel);
        if (getBoyinSwitch()) {
            home_discovery_layout.setVisibility(View.GONE);
        } else {
            home_discovery_layout.setVisibility(View.VISIBLE);
            setBottomButtonImg(home_discovery_layout, R.drawable.selector_home_boyin);
            home_discovery_layout.setText(getString(R.string.MainActivity_boyin));
        }
        setBottomButtonImg(home_mine_layout, R.drawable.selector_home_mine);
    }

    private void setBottomButtonImg(RadioButton button, int drawable) {
        Drawable drawable_novel = getResources().getDrawable(drawable);
        drawable_novel.setBounds(0, 0, ImageUtil.dp2px(activity, 20), ImageUtil.dp2px(activity, 20));
        button.setCompoundDrawables(null, drawable_novel, null, null);
    }

    public void initFragmentView() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        //判断本地书架是否有数据
        Intent intent = getIntent();
        if (intent != null) {
            bookLists = (List<BaseBook>) (intent.getSerializableExtra("mBaseBooks"));
            comicList = (List<BaseComic>) (intent.getSerializableExtra("mBaseComics"));
        }
        mFragmentList = new ArrayList<>();

        stroeNewFragmentBook = new StroeNewFragmentBook();
        mFragmentList.add(stroeNewFragmentBook);
        stroeNewFragmentComic = new StroeNewFragmentComic();
        mFragmentList.add(stroeNewFragmentComic);
        stroeNewFragmentCartoon = new StroeNewFragmentCartoon();
        mFragmentList.add(stroeNewFragmentCartoon);
        if (getBoyinSwitch()) {
            loadYouSheng = false;
        } else {
            homeBoYinFragment = new HomeBoYinFragment();
            Bundle bundle = new Bundle();
            bundle.putString(HomeBoYinFragment.BUNDLE_URL_KAY, getBoYinUrl());
            homeBoYinFragment.setArguments(bundle);
            mFragmentList.add(homeBoYinFragment);
            loadYouSheng = true;
            //showYouShengGuideOne();
        }
        mineFragment = new MineNewFragment();
        mineFragment.setBookLists(bookLists);
        mineFragment.setComicList(comicList);
        mFragmentList.add(mineFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, mFragmentList);
        customScrollViewPage.setAdapter(myFragmentPagerAdapter);
        customScrollViewPage.setOffscreenPageLimit(mFragmentList.size());
        setOption();
    }

    private void setOption() {
        int LastFragment = ShareUitls.getTab(activity, "LastFragment", 1);
        switch (LastFragment) {
            case 0:
                initViewPageChecked(home_store_layout, 0, true);
                break;
            case 1:
                initViewPageChecked(home_store_layout_comic, 1, true);
                break;
            case 2:
                initViewPageChecked(home_novel_layout, 2, true);
                break;
            case 3:
                if (getBoyinSwitch()) {
                    initViewPageChecked(home_mine_layout, 3, true);
                } else {
                    initViewPageChecked(home_discovery_layout, 3, true);
                }
                break;
            case 4:
                initViewPageChecked(home_mine_layout, 4, true);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home_novel_layout:
                        if (possition != 0) {
                            setChangedView(2, true);
                            EventBus.getDefault().post(new HomeShelfRefreshEvent());
                        }
                        break;
                    case R.id.home_store_layout:
                        if (possition != 1) {
                            setChangedView(0, stroeNewFragmentBook.IS_NOTOP);
                        }
                        break;
                    case R.id.home_store_layout_comic:
                        if (possition != 2) {
                            setChangedView(1, stroeNewFragmentComic.IS_NOTOP);
                        }
                        break;
                    case R.id.home_discovery_layout:
                        if (possition != 3) {
                            setChangedView(3, true);
                            if (loadYouSheng) {
                                //showYouShengGuideTwo();
                            }
                        }
                        break;
                    case R.id.home_mine_layout:
                        if (getBoyinSwitch()) {
                            if (possition != 3) {
                                setChangedView(3, true);
                                EventBus.getDefault().post(new AcceptMineFragment());
                            }
                        } else {
                            if (possition != 4) {
                                setChangedView(4, true);
                                EventBus.getDefault().post(new AcceptMineFragment());
                            }
                        }
                        break;
                }
            }
        });
    }

    void initViewPageChecked(RadioButton radioButton, int item, boolean useDart) {
        setStatusTextColor(useDart, activity);
        radioButton.setChecked(true);
        customScrollViewPage.setCurrentItem(item, false);
    }

    void setChangedView(int possition, boolean useDart) {
        setStatusTextColor(useDart, activity);
        IntentFragment(possition);
        getVipPayOrder();
        getCurrentVIPOrder();
        upLastTime();
        if (homeBoYinFragment != null) {
            if (possition == 3) {
                homeBoYinFragment.onMyResume();
            } else {
                homeBoYinFragment.onMyPause();
            }
        }
    }

    private void upLastTime() {
        if (!Utils.isLogin(activity)) {
            return;
        }
        ReaderParams params = new ReaderParams(MainActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(MainActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mLastTime, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }


    /**
     * 记录退出前选择的位置再次进入还原
     *
     * @param i
     */
    private void IntentFragment(int i) {
        customScrollViewPage.setCurrentItem(i);
        ShareUitls.putTab(activity, "LastFragment", i);
    }

    /**
     * 版本更新或签到弹框 开启波音显示波音未开启 显示发现。
     */
    public void ShowPOP() {
        String str = ReaderConfig.newInstance().AppUpdate;
        if (StringUtils.isEmpty(str)) {
            RecomendApp recomendApp = new RecomendApp(activity);
            recomendApp.getRequestData();
            new DialogExpirerdVip().getUserInfo(activity);
            return;
        }
        if (str.length() > 0) {
            mAppUpdate = new Gson().fromJson(str, AppUpdate.class);
            if (mAppUpdate != null && (mAppUpdate.getUpdate() == 1 || mAppUpdate.getUpdate() == 2)) {//1、普通更新 2、强制更新
                handler.sendEmptyMessage(0);
            } else {
                signPop();
            }
        }
    }

    private void signPop() {
        final String result = ShareUitls.getString(activity, PrefConst.SIGN_POP_KAY, "");
        if (result.length() != 0) {
            handler.sendEmptyMessageDelayed(1, 1000);
        }
        RecomendApp recomendApp = new RecomendApp(activity);
        recomendApp.getRequestData();
        new DialogExpirerdVip().getUserInfo(activity);
    }

    private void initData() {
        syncDevice(activity);
        getSdkAndLocalNotice(activity);
        getAd();
    }

    private void getSdkAndLocalNotice(Activity activity) {
        if (ReaderConfig.OTHER_SDK_AD.getAlert_index() == 2) {
            XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_HOME_DIALOG_DEBUG : BuildConfig.XAD_EVN_POS_HOME_DIALOG, AdType.CUSTOM_TYPE_DEFAULT, 99, new XAdRequestListener() {
                @Override
                public void onRequestOk(List<AdInfo> list) {
                    try {
                        List<AdInfo> adInfoList;
                        List<HomeNotice> homeNotices = new ArrayList<>();
                        if (list != null && list.size() > 10) {
                            adInfoList = list.subList(0, 10);
                        } else {
                            adInfoList = list;
                        }
                        for (int i = 0; i < adInfoList.size(); i++) {
                            AdInfo adInfo = list.get(i);
                            if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                                HomeNotice homeNotice = new HomeNotice();
                                homeNotice.setAdId(adInfo.getAdId());
                                homeNotice.setRequestId(adInfo.getRequestId());
                                homeNotice.setAdPosId(adInfo.getAdPosId());
                                homeNotice.setImg_content(adInfo.getMaterial().getImageUrl());
                                homeNotice.setTitle(adInfo.getMaterial().getTitle());
                                homeNotice.setUser_parame_need("1");
                                homeNotice.setJump_url(adInfo.getOperation().getValue());
                                homeNotice.setRedirect_type(String.valueOf(adInfo.getOperation().getType()));
                                homeNotices.add(homeNotice);
                            }
                        }
                        HomeNoticePhotoDialog.showDialog(activity, activity.getWindow().getDecorView(), homeNotices, true);
                    } catch (Exception e) {
                        getNotice(activity);
                    }
                }

                @Override
                public void onRequestFailed(int i, String s) {
                    getNotice(activity);
                }
            });
        } else {
            getNotice(activity);
        }

        /**
         * 动态获取主页底部菜单栏图标
         */
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = ReaderConfig.getBaseUrl() + BookConfig.bottom_icon_menu;
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                BottomIconMenu bottomIconMenu = new Gson().fromJson(response, BottomIconMenu.class);
                if (bottomIconMenu != null && bottomIconMenu.list != null && bottomIconMenu.list.size() > 0) {
                    getUrlDownload(bottomIconMenu.getList());
                } else {
                    deleteBottomIcons();
                }
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });

    }

    /**
     * 请求首页公告数据
     *
     * @param activity
     */
    public void getNotice(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mHomeNotice, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            Gson gson = new Gson();
                            List<HomeNotice> noticeList = gson.fromJson(result, new TypeToken<List<HomeNotice>>() {
                            }.getType());
                            if (noticeList != null && noticeList.size() > 0) {
                                for (int i = 0; i < noticeList.size(); i++) {
                                    HomeNotice homeNotice = noticeList.get(i);
                                    if (TextUtils.equals(homeNotice.getAnnoun_type(), "1")) {
                                        mHomeNoticeText.add(homeNotice);
                                    } else if (TextUtils.equals(homeNotice.getAnnoun_type(), "3")) {
                                        mHomeNoticePhoto.add(homeNotice);
                                    }
                                }
                                showHomeOneNotice(noticeList.get(0));
                            }
                        } catch (Exception e) {
                            MyToash.LogE("noticelist", e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * 显示首页公告弹框
     *
     * @param homeNotice
     */
    public void showHomeOneNotice(HomeNotice homeNotice) {
        if ("0".equals(homeNotice.os_type) || "2".equals(homeNotice.os_type)) {//0、所有，1、IOS，2、Android
            View view = this.getWindow().getDecorView();
            if (view != null) {
                if (TextUtils.equals(homeNotice.getAnnoun_type(), "1")) {
                    mIsFirstTextNotice = true;
                    HomeNoticeDialog.showDialog(MainActivity.this, view, mHomeNoticeText);
                } else if (TextUtils.equals(homeNotice.getAnnoun_type(), "3")) {
                    mIsFirstTextNotice = false;
                    HomeNoticePhotoDialog.showDialog(MainActivity.this, view, mHomeNoticePhoto, false);
                }
            }
        }
    }

    /**
     * 显示首页公告弹框图片
     */
    public void showHomeTwoNotice() {
        View view = this.getWindow().getDecorView();
        if (mIsFirstTextNotice) {
            if (mHomeNoticePhoto.size() > 0) {
                HomeNoticePhotoDialog.showDialog(MainActivity.this, view, mHomeNoticePhoto, false);
                mIsShowTwoNotice = false;
            }
        } else {
            if (mHomeNoticeText.size() > 0) {
                HomeNoticeDialog.showDialog(MainActivity.this, view, mHomeNoticeText);
                mIsShowTwoNotice = false;
            }
        }
        // 专享福利
        getWelfareConfig(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(NoticeEvent.DialogEvent event) {
        switch (event.action) {
            case 1:
                showHomeTwoNotice();
                break;
            case 2:
                getNotice(activity);
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToStore(ToStore toStore) {
        if (toStore.PRODUCT == 1) {//小说
            initViewPageChecked(home_store_layout, 0, true);
        } else if (toStore.PRODUCT == 2) {//漫画
            initViewPageChecked(home_store_layout_comic, 1, true);
        } else if (toStore.PRODUCT == 3) {//有声
            initViewPageChecked(home_discovery_layout, 3, true);
        } else {// 动漫
            if (BuildConfig.free_charge) {
                initViewPageChecked(home_novel_layout, 2, true);
            } else {
                initViewPageChecked(home_novel_layout, 2, true);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void exitAppEvent(ExitAppEvent exitAppEvent) {//退出APP监听
        exitAPP();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downComplete(BoyinInfoEvent boyinInfoEvent) {//退出APP监听
        exitService();
    }

    /**
     * bannen 点击切换有声fragment
     *
     * @param skipToBoYinEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bannerAdChangeBoyin(SkipToBoYinEvent skipToBoYinEvent) {
        initViewPageChecked(home_discovery_layout, 3, true);
    }

    /**
     * 由于现在支付跳转浏览器，没有回调可以告诉客户端用户已经支付成功。经过与产品协商使用下面方案：
     * 用户发起支付生成订单退出页面后，客户端通过拿订单信息，判断该订单有没有支付成功；
     * 由于第三方支付成功后服务器还需要一个处理时间。
     * 发送刷新个人中心Event。退出递归。
     * 切换tab，获取一次信息，如果未支付显示在小说漫画，成功则显示在mainactivity
     * status： 2支付成功  1待支付
     */
    public void getVipPayOrder() {
        boolean sharedBoolean = AppPrefs.getSharedBoolean(activity, PrefConst.ORDER, false);
        if (!sharedBoolean) {
            return;
        }
        ReaderParams params = new ReaderParams(MainActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(MainActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mPayLastOrder, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            int code = jsonObj.getInt("status");
                            int goodes_id = jsonObj.getInt("goods_id");
                            CreateVipPayOuderEvent createVipPayOuderEvent = new CreateVipPayOuderEvent();
                            if (code == 2) {
                                new DialogBecomeVip().getDialogVipPop(activity);
                                createVipPayOuderEvent.setCloseFlag(true);
                                EventBus.getDefault().post(createVipPayOuderEvent);
                                EventBus.getDefault().post(new RefreshMine(null));
                            } else if (code == 1) {
                                createVipPayOuderEvent.setCloseFlag(false);
                                createVipPayOuderEvent.setGoods_id(goodes_id);
                                EventBus.getDefault().post(createVipPayOuderEvent);
                            }

                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void getCurrentVIPOrder() {
        ReaderParams params = new ReaderParams(MainActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(MainActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mPayOrder, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        Gson gson = new Gson();
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                VipOrderBean vipOrderBean = gson.fromJson(String.valueOf(jsonArray.getJSONObject(i)), VipOrderBean.class);
                                setVIPSuccessOrderEvent(vipOrderBean);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * 获取本地推送数据
     */
    private void getLocalPushData() {
        ReaderParams params = new ReaderParams(MainActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(MainActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.LOCAL_PUSH, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        List<LoaclPushBean> loaclPushBeans = new ArrayList<>();
                        Gson gson = new Gson();
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                LoaclPushBean loaclPushBean = gson.fromJson(String.valueOf(jsonArray.getJSONObject(i)), LoaclPushBean.class);
                                loaclPushBeans.add(loaclPushBean);
                            }
                            String local_alarm = AppPrefs.getSharedString(activity, "local_alarm", "");
                            NotificationUtil.clearAllNotifyMsg(activity, local_alarm);
                            AppPrefs.putSharedString(activity, "local_alarm", result);
                            NotificationUtil.notifyByAlarm(activity, loaclPushBeans);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * 神策埋点 vip订单成功
     *
     * @param vipOrderBean
     */
    private void setVIPSuccessOrderEvent(VipOrderBean vipOrderBean) {
        String sharedString = AppPrefs.getSharedString(activity, PrefConst.VIP_SUCCESS_ORDER, "");
        if (!sharedString.contains(vipOrderBean.getTrade_no())) {
            sharedString = sharedString + vipOrderBean.getTrade_no();
            SensorsDataHelper.setVIPOrderSuccessEvent(vipOrderBean.getPayment_source_id());
            AppPrefs.putSharedString(activity, PrefConst.VIP_SUCCESS_ORDER, sharedString);
        }
    }

    //为了解决弹出PopupWindow后外部的事件不会分发,既外部的界面不可以点击
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (popupWindow != null && popupWindow.isShowing()) {
                return true;
            }
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.MainActivity_exit));
                mExitTime = System.currentTimeMillis();
            } else {
                exitAPP();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void exitAPP() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            ActivityManager activityManager = (ActivityManager) getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.AppTask> appTaskList = null;
            appTaskList = activityManager.getAppTasks();
            for (ActivityManager.AppTask appTask : appTaskList) {
                appTask.finishAndRemoveTask();
            }
        } else {
            if (SplashActivity.activity != null) {
                SplashActivity.activity.finish();
            }
            Intent startMain = new Intent(Intent.ACTION_MAIN);
            startMain.addCategory(Intent.CATEGORY_HOME);
            startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(startMain);
            System.exit(0);
        }
    }

    @Override
    protected void onDestroy() {
        exitService();
        super.onDestroy();
        Utils.mDialog = null;
        OkHttpEngine.mInstance = null;
    }

    private void exitService() {
        if (DownloadBoyinService.isServiceRunning(this, "DownloadBoyinService")) {
            Intent intent = new Intent(MainActivity.this, DownloadBoyinService.class);
            stopService(intent);// 关闭服务
        }
    }

    public View getNavigationView() {
        return mNavigationView;
    }

    /**
     * 权限管理申请
     *
     * @param activity
     */
    public void permission(Activity activity) {
        List<String> permissionLists = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.READ_PHONE_STATE);
        } else {
            trackInstallation();
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.REQUEST_INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                permissionLists.add(Manifest.permission.REQUEST_INSTALL_PACKAGES);
            }
        }
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.INSTALL_PACKAGES) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.INSTALL_PACKAGES);
        }
        if (!permissionLists.isEmpty()) {//说明肯定有拒绝的权限
            ActivityCompat.requestPermissions(activity, permissionLists.toArray(new String[permissionLists.size()]), 1);
        }
        if (!Utils.canShowNotification(activity)) {
            Utils.showNotificationPermissionTip(activity);
        }
    }

    /**
     * 加载首页有声新手引导层 第一层
     */
    private void showYouShengGuideOne() {
        controller = NewbieGuide.with(MainActivity.this)
                .setLabel("guide1")
                .setShowCounts(1)//控制次数
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(home_discovery_layout)
                        .addHighLight(view_guide_down,
                                new RelativeGuide(R.layout.view_guide_home_yousheng_one, Gravity.TOP, 0) {
                                    @Override
                                    protected void offsetMargin(MarginInfo marginInfo, ViewGroup viewGroup, View view) {
                                        view.findViewById(R.id.next).setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if (controller != null) {
                                                    controller.remove();
                                                }
                                            }
                                        });
                                    }
                                })
                        .setEverywhereCancelable(false)).show();
    }

    /**
     * 加载首页有声新手引导层 第二层
     */
    private void showYouShengGuideTwo() {
        NewbieGuide.with(MainActivity.this)
                .setLabel("guide1")
                .setShowCounts(2)
                .addGuidePage(GuidePage.newInstance().addHighLight(new RectF(
                        ImageUtil.dp2px(getApplicationContext(), 8), 0,
                        ImageUtil.dp2px(getApplicationContext(), 90),
                        ImageUtil.dp2px(getApplicationContext(), 64)))
                        .setLayoutRes(R.layout.view_guide_home_yousheng_two, R.id.next)
                        .setEverywhereCancelable(false)).show();
    }

    /**
     * 获取缓存系统参数
     */
    private AppUpdate getAppUpdate() {
        String str = ShareUitls.getString(MainActivity.this, "Update", "");
        return new Gson().fromJson(str, AppUpdate.class);
    }

    /**
     * 波音是否开启
     */
    private Boolean getBoyinSwitch() {
        return BuildConfig.free_charge;
    }

    /**
     * 获取波音h5 url
     */
    private String getBoYinUrl() {
        return App.getBaseH5Url().equals("") ? getAppUpdate().boyin_h5 : App.getBaseH5Url();
    }

    /**
     * 神策埋点事件 APP打开时长（从开屏页到首页）
     */
    public void setOpenTimeEvent() {
        try {
            long openTime = getIntent().getLongExtra(SplashActivity.OPEN_TIME_KAY, -1);
            if (openTime != -1) {
                SensorsDataHelper.setOpenTimeEvent(new Long(DateUtils.getCurrentTimeDifferenceSecond(openTime)).intValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            trackInstallation();
        }
    }

    /**
     * 记录激活事件
     */
    private void trackInstallation() {
        SensorsDataHelper.trackInstallation();
    }

    /**
     * 动态设置radio button selector
     */
    private void setRBSelectedState() {
        String rootPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/";
        RadioButton[] bytes;
        String[] titles;
        if (getBoyinSwitch()) {
            bytes = new RadioButton[]{home_novel_layout, home_store_layout, home_store_layout_comic, home_mine_layout};
            titles = new String[]{getString(R.string.noverfragment_xiaoshuo), getString(R.string.noverfragment_manhua), getString(R.string.MainActivity_cartoon), getString(R.string.MainActivity_my)};
        } else {
            bytes = new RadioButton[]{home_novel_layout, home_store_layout, home_store_layout_comic, home_discovery_layout, home_mine_layout};
            titles = new String[]{getString(R.string.noverfragment_xiaoshuo), getString(R.string.noverfragment_manhua), getString(R.string.MainActivity_cartoon), getString(R.string.MainActivity_boyin), getString(R.string.MainActivity_my)};
        }

        for (int i = 0; i < bytes.length; i++) {
            String picNameNormal = "rb_btn_normal_" + i + ".png";
            String picNameSelected = "rb_btn_selected_" + i + ".png";

            Bitmap bitmap_normal = imgToBitmap(rootPath + picNameNormal);
            Bitmap bitmap_selected = imgToBitmap(rootPath + picNameSelected);
            StateListDrawable arriveddrawable = new StateListDrawable();

            if (bitmap_normal == null || bitmap_selected == null) {
                setBottomButtonImgs();
                break;
            } else {
                Drawable drawNormal = new BitmapDrawable(bitmap_normal);
                Drawable drawSelected = new BitmapDrawable(bitmap_selected);
                arriveddrawable.addState(new int[]{android.R.attr.state_checked},
                        drawSelected);
                arriveddrawable.addState(new int[]{-android.R.attr.state_checked},
                        drawNormal);
                arriveddrawable.setBounds(0, 0, ImageUtil.dp2px(activity, 20), ImageUtil.dp2px(activity, 20));
                bytes[i].setCompoundDrawables(null, arriveddrawable, null, null);
                String title = ShareUitls.getString(App.getContext(), "tab_main_menu_" + i, titles[i]);
                bytes[i].setText(title);
            }
        }
    }

    /**
     * 从本地拿取图片，并转bitmap
     *
     * @param picPath
     * @return
     */
    private Bitmap imgToBitmap(String picPath) {
        FileInputStream fis = null;
        File file = new File(picPath);
        if (!file.exists()) {
            return null;
        }
        try {
            fis = new FileInputStream(picPath);
        } catch (FileNotFoundException e) {

        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        //指定图标尺寸 会出现缩放拉伸
        int wh = ImageUtil.dp2px(MainActivity.this, 70);
        return ImageUtil.scaleWithWH(bitmap, wh, wh);
    }

    /**
     * 获取首页福利展示
     * status 0 不展示，1 展示
     *
     * @param activity
     */
    private void getWelfareConfig(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url = ReaderConfig.getBaseUrl() + BookConfig.privilege_welfare_config;
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) throws JSONException {
                PrivilegeWelfare welfare = new Gson().fromJson(response, PrivilegeWelfare.class);
                WelfareStatus(welfare);
            }

            @Override
            public void onErrorResponse(String ex) {

            }
        });
    }

    /**
     * 专享福利
     *
     * @param welfare
     */
    private void WelfareStatus(PrivilegeWelfare welfare) {
        if (welfare != null) {
            String status = welfare.getStatus();
            String context = welfare.getWelfare_title();
            if (status.equalsIgnoreCase("1")) {
                fl_welfare.setVisibility(View.VISIBLE);
                tv_welfare_text.setText(context);
                iv_welfare_close.setOnClickListener(view -> {
                    fl_welfare.setVisibility(View.GONE);
                });
                tv_welfare_go.setOnClickListener(view -> {
                    fl_welfare.setVisibility(View.GONE);
                    Intent intent = new Intent();
                    intent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 1);
                    intent.putExtra("isvip", Utils.isLogin(activity));
                    startActivity(intent);
                });

            } else {
                fl_welfare.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 注册或登录，需要刷新 专享福利
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshWelfare(RegisterLoginWelfareEvent event) {
        getWelfareConfig(activity);
    }

    /**
     * 退出登录
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void dismissWelfare(LogoutEvent event) {
        fl_welfare.setVisibility(View.GONE);
    }


    /**
     * 主动删掉 之前保存的 bottom icons
     */
    private void deleteBottomIcons() {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/decode/";
        String outPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/";
        File file1 = new File(dirPath);
        File file2 = new File(outPath);
        if (file1.exists()) {
            deleteDirectory(file1);
        }
        if (file2.exists()) {
            deleteDirectory(file2);
        }
    }

    private void deleteDirectory(File directory) {
        if (!directory.isDirectory()) {
            directory.delete();
        } else {
            File[] files = directory.listFiles();
            //空文件夹，直接删除
            if (files.length == 0) {
                directory.delete();
                return;
            }

            for (File file : files) {
                if (file.isDirectory()) {
                    deleteDirectory(file);
                } else {
                    file.delete();
                }
            }
        }
        directory.delete();
    }

    /**
     * 获取 底部 radio button 图片地址
     *
     * @param list
     */
    private void getUrlDownload(List<BottomIconMenu.RBIcons> list) {
        for (int i = 0; i < list.size(); i++) {
            BottomIconMenu.RBIcons rbIcons = list.get(i);
            String iconSelected = rbIcons.getIcon_selected();
            String iconNormal = rbIcons.getIcon_normal();
            saveImg2SD(i, "selected", iconSelected);
            saveImg2SD(i, "normal", iconNormal);
            String title = rbIcons.getIcon_title();
            ShareUitls.putString(App.getAppContext(), "tab_main_menu_" + i, title);
        }
    }

    /**
     * 使用glide下载并转换成bitmap
     *
     * @param i
     * @param type
     * @param url
     */
    private void saveImg2SD(int i, String type, String url) {
        String fileName = "rb_btn_" + type + "_" + i + ".png";
        downloadMenus(url, fileName);
    }

    private void downloadMenus(String url, String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/decode/";
        File dest = new File(dirPath, fileName);
        Request request = new Request.Builder().url(url).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    File imgFile = new File(dirPath);
                    if (!imgFile.exists()) {
                        imgFile.mkdir();
                    }
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(response.body().source());
                    bufferedSink.close();

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (bufferedSink != null) {
                        bufferedSink.close();
                    }
                    decryptFile(fileName);
                }
            }
        });
    }

    /**
     * 解密文件成 png
     *
     * @param fileName
     */
    int iconCount = 0;

    private void decryptFile(String fileName) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/decode/";
        String outPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/hhlz/";
        File dest = new File(dirPath, fileName);
        File outp = new File(outPath);
        if (!outp.exists()) {
            outp.mkdir();
        }
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(dest);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (inputStream != null) {
            AESUtil.decryptFile(AESUtil.key, inputStream, outPath + fileName);
        }
        iconCount++;
        if (getBoyinSwitch()) {
            if (iconCount == 8) {
                MainActivity.this.runOnUiThread(this::setRBSelectedState);
            }
        } else {
            if (iconCount == 10) {
                MainActivity.this.runOnUiThread(this::setRBSelectedState);
            }
        }

    }
}