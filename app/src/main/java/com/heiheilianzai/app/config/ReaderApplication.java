package com.heiheilianzai.app.config;


import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.domain.PreparedDomain;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.read.ReadingConfig;
import com.heiheilianzai.app.utils.JPushFactory;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.UpdateApp;
import com.tencent.bugly.Bugly;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.umcrash.UMCrash;

import org.litepal.LitePalApplication;

/**
 * application配置
 */
public class ReaderApplication extends LitePalApplication {
    public static Context context;
    public static PreparedDomain preparedDomain;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            context = getContext();
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
            //初始化BigImageViewer图片加载方式为Glide
            BigImageViewer.initialize(GlideImageLoader.with(getAppContext()));
            ReadingConfig.createConfig(this);
            Bugly.init(this, "75adbf1bdc", false);
            JPushUtil.init(this);
            // 阿里云推送初始化会报错，导致其他插件初始化失败。由于暂时没有用到阿里云，放最下面了。
            initCloudChannel(this);
        } catch (Exception E) {
        } catch (Error e) {
        }
        JPushFactory.testpush(this);
    }

    private void initCloudChannel(final Context applicationContext) {
        // 创建notificaiton channel
        this.createNotificationChannel();
        PushServiceFactory.init(applicationContext);
        final CloudPushService pushService = PushServiceFactory.getCloudPushService();
        pushService.register(applicationContext, new CommonCallback() {
            @Override
            public void onSuccess(String response) {
                ReaderConfig.PUSH_TOKEN = PushServiceFactory.getCloudPushService().getDeviceId();
                MyToash.Log("PUSH_TOKEN", ReaderConfig.PUSH_TOKEN);
                ShareUitls.putString(applicationContext, "PUSH_TOKEN", ReaderConfig.PUSH_TOKEN);
            }

            @Override
            public void onFailed(String errorCode, String errorMessage) {
            }
        });

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
        return preparedDomain.getString("base_url", BuildConfig.api_host);
    }

    public static void setBaseUrl(String url) {
        preparedDomain.putString("base_url", url);
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
}
