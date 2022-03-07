package com.heiheilianzai.app.base;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;

import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.PreparedDomain;
import com.heiheilianzai.app.component.push.JPushUtil;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.RabbitConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.BooActivityManager;
import com.heiheilianzai.app.utils.JPushFactory;
import com.heiheilianzai.app.utils.MyActivityManager;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.connection.FileDownloadUrlConnection;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import com.scwang.smart.refresh.footer.ClassicsFooter;
import com.scwang.smart.refresh.header.MaterialHeader;
import com.scwang.smart.refresh.layout.SmartRefreshLayout;
import com.sensorsdata.analytics.android.sdk.SAConfigOptions;
import com.sensorsdata.analytics.android.sdk.SensorsAnalyticsAutoTrackEventType;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.tencent.bugly.Bugly;
import com.tinstall.tinstall.TInstall;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.umcrash.UMCrash;

import org.litepal.LitePalApplication;

import java.net.Proxy;

/**
 * application配置
 */
public class App extends LitePalApplication {
    public static Context context;
    public static PreparedDomain preparedDomain;

    static {
        // 设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator((context, layout) -> {
            layout.setPrimaryColorsId(R.color.black, R.color.black, R.color.black);// 全局设置主题颜色
            layout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
            layout.setEnableHeaderTranslationContent(false);//是否下拉Header的时候向下平移列表或者内容
            layout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
            layout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
            layout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
            layout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
            return new MaterialHeader(context)
                    .setColorSchemeResources(R.color.black, R.color.black, R.color.black);// 指定为经典Header，默认是 贝塞尔雷达Header
        });
        // 设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator((context, layout) -> {
            layout.setEnableAutoLoadMore(true);//是否启用列表惯性滑动到底部时自动加载更多
            layout.setEnableHeaderTranslationContent(false);//是否下拉Header的时候向下平移列表或者内容
            layout.setEnableFooterTranslationContent(true);//是否上拉Footer的时候向上平移列表或者内容
            layout.setDisableContentWhenRefresh(true);//是否在刷新的时候禁止列表的操作
            layout.setDisableContentWhenLoading(true);//是否在加载的时候禁止列表的操作
            layout.setEnableScrollContentWhenLoaded(true);//是否在加载完成时滚动列表显示新的内容
            // 指定为经典Footer，默认是 BallPulseFooter
            return new ClassicsFooter(context).setDrawableSize(20);
        });
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //liulishuo 初始化
        setupFileDownload();
        try {
            context = getContext();
            initGlobeActivity();
            preparedDomain = new PreparedDomain(context);
            UMConfigure.setLogEnabled(false);
            String getChannelName = UpdateApp.getChannelName(this);
            UMConfigure.init(this, ReaderConfig.UMENG, getChannelName, UMConfigure.DEVICE_TYPE_PHONE, "");
            UMCrash.init(this, ReaderConfig.UMENG, getChannelName);
            if (ReaderConfig.USE_WEIXIN) {
                PlatformConfig.setWeixin(ReaderConfig.WEIXIN_PAY_APPID, ReaderConfig.WEIXIN_APP_SECRET);
            }
            if (ReaderConfig.USE_QQ) {
                PlatformConfig.setQQZone(ReaderConfig.QQ_APPID, ReaderConfig.QQ_SECRET);
            }
            BooActivityManager.addActivityListener(this);
            //初始化神策
            initSensors();
            //初始化BigImageViewer图片加载方式为Glide
            BigImageViewer.initialize(GlideImageLoader.with(getAppContext()));
            ReadingConfig.createConfig(this);
            Bugly.init(this, "75adbf1bdc", false);
            JPushUtil.init(this);

            if (isMainProgress()) {
                TInstall.init(this, BuildConfig.tinstall_key);//tinstall_key 为申请的应用KEY
            }

        } catch (Exception E) {
        } catch (Error e) {
        }
        JPushFactory.testpush(this);
    }

    private boolean isMainProgress() {
        int pid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningAppProcessInfo appProcess : activityManager.getRunningAppProcesses()) {
            if (appProcess.pid == pid) {
                return getApplicationInfo().packageName.equals(appProcess.processName);
            }
        }
        return false;
    }

    /**
     * 初始化云推送通道
     */
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            // 通知渠道的id
            String id = BuildConfig.APPLICATION_ID;
            // 用户可以看到的通知渠道的名字.
            CharSequence name = "notification channel";
            // 用户可以看到的通知渠道的描述
            String description = "notification description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            // 配置通知渠道的属性
            mChannel.setDescription(description);
            // 设置通知出现时的闪灯（如果 android 设备支持的话）
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            // 设置通知出现时的震动（如果 android 设备支持的话）
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            //最后在notificationmanager中创建该通知渠道
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    /**
     * 初始化神策
     */
    private void initSensors() {
        // 初始化配置
        SAConfigOptions saConfigOptions = new SAConfigOptions(RabbitConfig.ONLINE ? BuildConfig.sa_server_url : BuildConfig.sa_server_url_uat);
        // 开启全埋点
        saConfigOptions.setAutoTrackEventType(SensorsAnalyticsAutoTrackEventType.APP_CLICK |
                SensorsAnalyticsAutoTrackEventType.APP_START |
                SensorsAnalyticsAutoTrackEventType.APP_END |
                SensorsAnalyticsAutoTrackEventType.APP_VIEW_SCREEN);
        saConfigOptions.enableTrackAppCrash();
        //与H5打通
        saConfigOptions.enableJavaScriptBridge(true);
        if (!RabbitConfig.ONLINE) {
            saConfigOptions.enableVisualizedAutoTrack(true);//开启可视化全埋点
            saConfigOptions.enableLog(true);//开启Log
        }
        saConfigOptions.enableTrackScreenOrientation(true);//屏幕方向
        // 需要在主线程初始化神策 SDK
        if (context != null) {
            SensorsDataAPI.startWithConfigOptions(context, saConfigOptions);
            SensorsDataHelper.setAppId();//设置Appid
            SensorsDataHelper.setDynamicPublicProperty(context);//设置神策公共参数
        }
    }

    public static Context getAppContext() {
        return context;
    }

    public static String getDomainHostsUrl() {
        return preparedDomain.getString("domain_host", BuildConfig.domain_host);
    }

    public static void setdDomainHostsUrl(String url) {
        preparedDomain.putString("domain_host", url);
    }

    public static String getBaseUrl() {
        return preparedDomain.getString("base_url", BuildConfig.api_host[0]);
    }

    public static void setBaseUrl(String url) {
        preparedDomain.putString("base_url", url);
    }

    public static String getBaseH5Url() {
        return preparedDomain.getString("base_h5_url", "");
    }

    public static void setBaseh5Url(String url) {
        preparedDomain.putString("base_h5_url", url);
    }

    public static boolean getNeedInstall() {
        return preparedDomain.getBoolean("needInstall", true);
    }

    public static void setNeedInstall(boolean needInstall) {
        preparedDomain.putBoolean("needInstall", needInstall);
    }

    //接口加密
    public static String getCipherApi() {
        return preparedDomain.getString("cipher_api", "0");
    }

    public static void setCipherApi(String cipher) {
        preparedDomain.putString("cipher_api", cipher);
    }

    //app切换到后台的时间
    public static long getBackgroundTime() {
        return preparedDomain.getLong("background_time", 0);
    }

    public static void setBackgroundTime(long time) {
        preparedDomain.putLong("background_time", time);
    }

    public static void putDailyStartPageMax(int mix) {
        preparedDomain.putInt("daily_max_start_page", mix);
    }

    public static int getDailyStartPageMax() {
        return preparedDomain.getInt("daily_max_start_page", 0);
    }

    //记录当天广告热启动次数
    public static void putDailyStartPage() {
        PreparedDomain.putDailyStartPage(preparedDomain);
    }

    //获取当天广告热启动次数
    public static int getDailyStartPage() {
        return PreparedDomain.getDailyStartPage(preparedDomain);
    }

    //切换时后台多少秒开启可以开启广告
    public static int getValidBackgroundTime() {
        String str = ShareUitls.getString(context, "Update", "");
        if (str.length() > 0) {
            AppUpdate mAppUpdate = new Gson().fromJson(str, AppUpdate.class);
            if (mAppUpdate.getHot_start_time() > 0) {
                return mAppUpdate.getHot_start_time();
            }
        }
        return -1;
    }

    /**
     * 登录后获得缓存用户信息
     *
     * @param context
     * @return
     */
    public static UserInfoItem getUserInfoItem(Context context) {
        String userInfoStr = AppPrefs.getSharedString(context, PrefConst.USER_INFO_KAY, "");
        UserInfoItem userInfo = null;
        if (!StringUtils.isEmpty(userInfoStr)) {
            Gson gson = new Gson();
            return gson.fromJson(userInfoStr, UserInfoItem.class);
        }
        return null;
    }

    public static Boolean isVip(Context context) {
        UserInfoItem userInfoItem = getUserInfoItem(context);
        if (userInfoItem != null) {
            int is_vip = userInfoItem.getIs_vip();
            if (is_vip == 1) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    //判断第三方广告给什么用户开启
    public static boolean isShowSdkAd(Context context, String showType) {
        //0 全部，1非会员 2会员  展示
        if (showType != null) {
            if (TextUtils.equals(showType, "0")) {
                return true;
            } else if (TextUtils.equals(showType, "1")) {
                if (isVip(context)) {
                    return false;
                } else {
                    return true;
                }
            } else if (TextUtils.equals(showType, "2")) {
                if (isVip(context)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    /**
     * 注册Activity生命周期 放入MyActivityManager进行管理。
     */
    private void initGlobeActivity() {
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
            }

            @Override
            public void onActivityStarted(Activity activity) {
            }

            @Override
            public void onActivityResumed(Activity activity) {
                MyActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {
            }

            @Override
            public void onActivityStopped(Activity activity) {
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
            }
        });
    }

    /**
     * 初始化流利说下载库
     */
    private void setupFileDownload() {
        // 下载相关设置
        FileDownloadLog.NEED_LOG = BuildConfig.DEBUG;
        FileDownloader.setupOnApplicationOnCreate(this)
                .connectionCreator(new FileDownloadUrlConnection
                        .Creator(new FileDownloadUrlConnection.Configuration()
                        // set connection timeout.
                        .connectTimeout(15_000)
                        // set read timeout.
                        .readTimeout(15_000)
                        // set proxy
                        .proxy(Proxy.NO_PROXY)
                )).commit();
        FileDownloader.getImpl().setMaxNetworkThreadCount(1);
    }

}