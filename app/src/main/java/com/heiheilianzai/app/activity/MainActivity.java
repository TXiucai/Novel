package com.heiheilianzai.app.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.bean.BaseAd;
import com.heiheilianzai.app.bean.HomeNotice;
import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.book.fragment.StroeNewFragmentBook;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.comic.fragment.StroeNewFragmentComic;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.dialog.HomeNoticeDialog;
import com.heiheilianzai.app.dialog.MyPoPwindow;
import com.heiheilianzai.app.eventbus.AppUpdateLoadOverEvent;
import com.heiheilianzai.app.eventbus.ToStore;
import com.heiheilianzai.app.fragment.BookshelfFragment;
import com.heiheilianzai.app.fragment.DiscoveryNewFragment;
import com.heiheilianzai.app.fragment.MineNewFragment;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.read.ReadActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.CustomScrollViewPager;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.book.fragment.StroeNewFragmentBook.IS_NOTOP_BOOK;
import static com.heiheilianzai.app.comic.fragment.StroeNewFragmentComic.IS_NOTOP_COMIC;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.config.ReaderConfig.syncDevice;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

public class MainActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R2.id.RadioGroup)
    public RadioGroup mRadioGroup;
    @BindView(R2.id.fragment_home_container)
    public CustomScrollViewPager customScrollViewPage;
    public long mExitTime = 0;
    @BindView(R2.id.main_menu_layout)
    public LinearLayout mNavigationView;
    @BindView(R2.id.home_novel_layout)
    public RadioButton home_novel_layout;
    @BindView(R2.id.home_store_layout)
    public RadioButton home_store_layout;
    @BindView(R2.id.home_discovery_layout)
    public RadioButton home_discovery_layout;
    @BindView(R2.id.home_mine_layout)
    public RadioButton home_mine_layout;
    @BindView(R2.id.home_store_layout_comic)
    public RadioButton home_store_layout_comic;
    @BindView(R2.id.shelf_book_delete_btn)
    public LinearLayout shelf_book_delete_btn;
    private List<android.support.v4.app.Fragment> mFragmentList;

    @Override
    public int initContentView() {
        return R.layout.activity_main;
    }

    public static Activity activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        Drawable drawable_novel = getResources().getDrawable(R.drawable.selector_home_novel);
        drawable_novel.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
        home_novel_layout.setCompoundDrawables(null, drawable_novel, null, null);
        Drawable drawable_store = getResources().getDrawable(R.drawable.selector_home_store);
        drawable_store.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
        home_store_layout.setCompoundDrawables(null, drawable_store, null, null);
        Drawable drawable_comic = getResources().getDrawable(R.drawable.selector_home_store_comic);
        drawable_comic.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
        home_store_layout_comic.setCompoundDrawables(null, drawable_comic, null, null);
        Drawable drawable_discovery = getResources().getDrawable(R.drawable.selector_home_discovery);
        drawable_discovery.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
        home_discovery_layout.setCompoundDrawables(null, drawable_discovery, null, null);
        Drawable drawable_mine = getResources().getDrawable(R.drawable.selector_home_mine);
        drawable_mine.setBounds(0, 0, ImageUtil.dp2px(activity, 28), ImageUtil.dp2px(activity, 28));
        home_mine_layout.setCompoundDrawables(null, drawable_mine, null, null);
        EventBus.getDefault().register(this);
        permission(activity);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {                                  //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));                   //需要在setContentView()方法后面执行
        }
        initData();
        syncDevice(activity);
        mRadioGroup.post(new Runnable() {
            @Override
            public void run() {
                try {
                    getNotice(activity);
                    ShowPOP();
                    getReadButtomWebViewAD(activity);
                } catch (Exception e) {
                }
            }
        });
        startAdvertisementActivity();
    }

    private AppUpdate mAppUpdate;
    List<BaseBook> bookLists;
    List<BaseComic> comicList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    private int possition = 5;
    BookshelfFragment bookshelfFragment;

    public void initData() {
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
        StroeNewFragmentBook stroeNewFragmentBook = new StroeNewFragmentBook();
        mFragmentList.add(stroeNewFragmentBook);
        StroeNewFragmentComic stroeNewFragmentComic = new StroeNewFragmentComic();
        mFragmentList.add(stroeNewFragmentComic);
        DiscoveryNewFragment discoveryFragment = new DiscoveryNewFragment();
        mFragmentList.add(discoveryFragment);
        MineNewFragment mineFragment = new MineNewFragment();
        mFragmentList.add(mineFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(fragmentManager, mFragmentList);
        customScrollViewPage.setAdapter(myFragmentPagerAdapter);
        customScrollViewPage.post(new Runnable() {
            @Override
            public void run() {
                customScrollViewPage.setOffscreenPageLimit(5);
            }
        });
        setOption();
    }

    private void setOption() {
        int LastFragment = ShareUitls.getTab(activity, "LastFragment", 1);
        MyToash.Log("LastFragment", LastFragment);
        switch (LastFragment) {
            case 0:
                home_novel_layout.setChecked(true);
                customScrollViewPage.setCurrentItem(0, false);
                break;
            case 1:
                setStatusTextColor(IS_NOTOP_BOOK, activity);
                home_store_layout.setChecked(true);
                customScrollViewPage.setCurrentItem(1, false);
                break;
            case 2:
                setStatusTextColor(IS_NOTOP_COMIC, activity);
                home_store_layout_comic.setChecked(true);
                customScrollViewPage.setCurrentItem(2, false);
                break;
            case 3:
                home_discovery_layout.setChecked(true);
                customScrollViewPage.setCurrentItem(3, false);
                break;
            case 4:
                home_mine_layout.setChecked(true);
                customScrollViewPage.setCurrentItem(4, false);
                break;
        }
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.home_novel_layout:
                        if (possition != 0) {
                            setStatusTextColor(true, activity);
                            IntentFragment(0);
                        }
                        break;
                    case R.id.home_store_layout:
                        if (possition != 1) {
                            IntentFragment(1);
                            setStatusTextColor(IS_NOTOP_BOOK, activity);
                        }
                        break;
                    case R.id.home_store_layout_comic:
                        if (possition != 2) {
                            IntentFragment(2);
                            setStatusTextColor(IS_NOTOP_COMIC, activity);
                        }
                        break;
                    case R.id.home_discovery_layout:
                        if (possition != 3) {
                            setStatusTextColor(true, activity);
                            IntentFragment(3);
                        }
                        break;
                    case R.id.home_mine_layout:
                        if (possition != 4) {
                            setStatusTextColor(true, activity);
                            IntentFragment(4);
                            setStatusTextColor(true, activity);
                        }
                        break;
                }
            }
        });
    }

    private void IntentFragment(int i) {
        customScrollViewPage.setCurrentItem(i);
        ShareUitls.putTab(activity, "LastFragment", i);
        MyToash.Log("LastFragment t", i);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Utils.mDialog = null;
        OkHttpEngine.mInstance = null;
    }

    public View getNavigationView() {
        return mNavigationView;
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

    //为了解决弹出PopupWindow后外部的事件不会分发,既外部的界面不可以点击
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return false;
        }
        return super.dispatchTouchEvent(event);
    }

    //权限管理
    public static void permission(Activity activity) {
        List<String> permissionLists = new ArrayList<>();
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionLists.add(Manifest.permission.READ_PHONE_STATE);
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

    private Dialog popupWindow;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                popupWindow = new UpdateApp().getAppUpdatePop(MainActivity.this, mAppUpdate);
                ReaderConfig.AppUpdate = null;
            } else {
                new MyPoPwindow().getSignPop(activity);
            }
        }
    };


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    public void ShowPOP() {
        if (!ReaderApplication.isAppUpdateLoadOver) {
            return;
        }
        String str = ShareUitls.getString(activity, "Update", "");
        if (str.length() > 0) {
            mAppUpdate = new Gson().fromJson(str, AppUpdate.class);
            if (mAppUpdate != null && (mAppUpdate.getUpdate() == 1 || mAppUpdate.getUpdate() == 2)) {
                mRadioGroup.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessage(0);
                    }
                });
            } else {
                final String result = ShareUitls.getString(activity, "sign_pop", "");//签到弹框
                if (result.length() != 0) {
                    mRadioGroup.post(new Runnable() {
                        @Override
                        public void run() {
                            handler.sendEmptyMessageDelayed(1, 1000);
                        }
                    });
                }
            }
        } else {
            final String result = ShareUitls.getString(activity, "sign_pop", "");//签到弹框
            if (result.length() != 0) {
                mRadioGroup.post(new Runnable() {
                    @Override
                    public void run() {
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                });
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void ToStore(ToStore toStore) {
        if (toStore.PRODUCT == 1) {
            home_store_layout.setChecked(true);
        } else {
            home_store_layout_comic.setChecked(true);
        }
    }

    public void getReadButtomWebViewAD(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", XIAOSHUO + "");
        params.putExtraParams("position", "12");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
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
                                showHomeNotice(noticeList.get(0));
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
    public void showHomeNotice(HomeNotice homeNotice) {
        if ("0".equals(homeNotice.os_type) || "2".equals(homeNotice.os_type)) {//0、所有，1、IOS，2、Android
            View view = this.getWindow().getDecorView();
            if (view != null) {
                HomeNoticeDialog.showDialog(MainActivity.this, view, homeNotice);
            }
        }
    }

    /**
     * 启动广告页
     */
    public void startAdvertisementActivity() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            if (bundle.getBoolean("advertisement")) {
                startActivity(new Intent(this, AdvertisementActivity.class));
            }
        }
    }

    /**
     * 防止未加载开屏广告，先跳入首页而初始化参数信息还未刷新缓存
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventAppUpdateLoadOver(AppUpdateLoadOverEvent event) {
        if (event != null) {
            ShowPOP();
        }
    }
}
