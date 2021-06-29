package com.heiheilianzai.app.ui.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
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
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.DownloadBoyinService;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.VipOrderBean;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.AcceptMineFragment;
import com.heiheilianzai.app.model.event.CreateVipPayOuderEvent;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.model.event.ExitAppEvent;
import com.heiheilianzai.app.model.event.HomeShelfRefreshEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.model.event.comic.BoyinInfoEvent;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.ui.dialog.HomeNoticeDialog;
import com.heiheilianzai.app.ui.dialog.HomeNoticePhotoDialog;
import com.heiheilianzai.app.ui.dialog.MyPoPwindow;
import com.heiheilianzai.app.ui.fragment.BookshelfFragment;
import com.heiheilianzai.app.ui.fragment.DiscoveryNewFragment;
import com.heiheilianzai.app.ui.fragment.HomeBoYinFragment;
import com.heiheilianzai.app.ui.fragment.MineNewFragment;
import com.heiheilianzai.app.ui.fragment.book.StroeNewFragmentBook;
import com.heiheilianzai.app.ui.fragment.comic.StroeNewFragmentComic;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.DialogBecomeVip;
import com.heiheilianzai.app.utils.DialogExpirerdVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.RecomendApp;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.CustomScrollViewPager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.constant.ReaderConfig.syncDevice;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

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

    private List<Fragment> mFragmentList;
    private AppUpdate mAppUpdate;
    List<BaseBook> bookLists;
    List<BaseComic> comicList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    private int possition = 5;
    BookshelfFragment bookshelfFragment;
    public static Activity activity;
    StroeNewFragmentBook stroeNewFragmentBook;//首页漫画
    StroeNewFragmentComic stroeNewFragmentComic;//首页小说
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
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {    //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));  //需要在setContentView()方法后面执行
        }
        EventBus.getDefault().register(this);
        activity = this;
        permission(activity);
        initView();
        initData();
        setOpenTimeEvent();
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
        setBottomButtonImg(home_novel_layout, R.drawable.selector_home_novel);
        setBottomButtonImg(home_store_layout, R.drawable.selector_home_store);
        setBottomButtonImg(home_store_layout_comic, R.drawable.selector_home_store_comic);
        if (getAppUpdate() != null && getBoyinSwitch() == 1) {
            setBottomButtonImg(home_discovery_layout, R.drawable.selector_home_boyin);
            home_discovery_layout.setText(getString(R.string.MainActivity_boyin));
        } else {
            setBottomButtonImg(home_discovery_layout, R.drawable.selector_home_discovery);
        }
        setBottomButtonImg(home_mine_layout, R.drawable.selector_home_mine);
    }

    private void setBottomButtonImg(RadioButton button, int drawable) {
        Drawable drawable_novel = getResources().getDrawable(drawable);
        drawable_novel.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
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
        bookshelfFragment = new BookshelfFragment(bookLists, comicList, shelf_book_delete_btn);
        mFragmentList.add(bookshelfFragment);
        stroeNewFragmentBook = new StroeNewFragmentBook();
        mFragmentList.add(stroeNewFragmentBook);
        stroeNewFragmentComic = new StroeNewFragmentComic();
        mFragmentList.add(stroeNewFragmentComic);
        if (getAppUpdate() != null && getBoyinSwitch() == 1) {
            homeBoYinFragment = new HomeBoYinFragment();
            Bundle bundle = new Bundle();
            bundle.putString(HomeBoYinFragment.BUNDLE_URL_KAY, getBoYinUrl());
            homeBoYinFragment.setArguments(bundle);
            mFragmentList.add(homeBoYinFragment);
            loadYouSheng = true;
            //showYouShengGuideOne();
        } else {
            discoveryFragment = new DiscoveryNewFragment();
            mFragmentList.add(discoveryFragment);
            loadYouSheng = false;
        }
        mineFragment = new MineNewFragment();
        mFragmentList.add(mineFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, mFragmentList);
        customScrollViewPage.setAdapter(myFragmentPagerAdapter);
        customScrollViewPage.setOffscreenPageLimit(5);
        setOption();
    }

    private void setOption() {
        int LastFragment = ShareUitls.getTab(activity, "LastFragment", 1);
        switch (LastFragment) {
            case 0:
                initViewPageChecked(home_novel_layout, 0, true);
                break;
            case 1:
                initViewPageChecked(home_store_layout, 1, true);
                break;
            case 2:
                initViewPageChecked(home_store_layout_comic, 2, true);
                break;
            case 3:
                initViewPageChecked(home_discovery_layout, 3, true);
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
                            setChangedView(0, true);
                            EventBus.getDefault().post(new HomeShelfRefreshEvent());
                        }
                        break;
                    case R.id.home_store_layout:
                        if (possition != 1) {
                            setChangedView(1, stroeNewFragmentBook.IS_NOTOP);
                        }
                        break;
                    case R.id.home_store_layout_comic:
                        if (possition != 2) {
                            setChangedView(2, stroeNewFragmentComic.IS_NOTOP);
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
                        if (possition != 4) {
                            setChangedView(4, true);
                            EventBus.getDefault().post(new AcceptMineFragment());
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
    }

    private void initData() {
        syncDevice(activity);
        getNotice(activity);
        getReadButtomWebViewAD(activity);
    }

    /**
     * 更新小说广告
     *
     * @param activity
     */
    public void getReadButtomWebViewAD(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("type", XIAOSHUO + "");
        params.putExtraParams("position", "12");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mAdvert, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            BaseAd baseAd = new Gson().fromJson(result, BaseAd.class);
                            if (baseAd.ad_type == 1) {
                                ReadActivity.USE_BUTTOM_AD = true;
                            } else {
                                ReadActivity.USE_BUTTOM_AD = false;
                            }
                        } catch (Exception e) {
                            ReadActivity.USE_BUTTOM_AD = false;
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
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
                    HomeNoticePhotoDialog.showDialog(MainActivity.this, view, homeNotice);
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
                HomeNoticePhotoDialog.showDialog(MainActivity.this, view, mHomeNoticePhoto.get(0));
                mIsShowTwoNotice = false;
            }
        } else {
            if (mHomeNoticeText.size() > 0) {
                HomeNoticeDialog.showDialog(MainActivity.this, view, mHomeNoticeText);
                mIsShowTwoNotice = false;
            }
        }
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
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToStore(ToStore toStore) {
        if (toStore.PRODUCT == 1) {//小说
            initViewPageChecked(home_store_layout, 1, true);
        } else if (toStore.PRODUCT == 2) {//漫画
            initViewPageChecked(home_store_layout_comic, 2, true);
        } else if (toStore.PRODUCT == 3) {//有声 or 推荐
            initViewPageChecked(home_discovery_layout, 3, true);
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
    private int getBoyinSwitch() {
        return getAppUpdate().boyin_switch;
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
}