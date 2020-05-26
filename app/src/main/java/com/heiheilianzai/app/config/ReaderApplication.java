package com.heiheilianzai.app.config;


import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;

import com.alibaba.sdk.android.push.CloudPushService;
import com.alibaba.sdk.android.push.CommonCallback;
import com.alibaba.sdk.android.push.noonesdk.PushServiceFactory;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.model.AppData;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.domain.PreparedDomain;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.read.ReadingConfig;
import com.heiheilianzai.app.utils.JPushFactory;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.UpdateApp;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.tencent.bugly.Bugly;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;

import org.litepal.LitePalApplication;

import java.io.File;

/**
 * application配置
 */
public class ReaderApplication extends LitePalApplication {
    public static DisplayImageOptions mOptions;
    public static Context context;
    public static PreparedDomain preparedDomain;

    @Override
    public void onCreate() {
        super.onCreate();
        try {
            context = getContext();
            initOpenInstall();
            preparedDomain = new PreparedDomain(context);
            UMConfigure.setLogEnabled(false);
            String getChannelName = UpdateApp.getChannelName(this);
            UMConfigure.init(this, ReaderConfig.UMENG, getChannelName, UMConfigure.DEVICE_TYPE_PHONE, "");
            if (ReaderConfig.USE_WEIXIN) {
                PlatformConfig.setWeixin(ReaderConfig.WEIXIN_PAY_APPID, ReaderConfig.WEIXIN_APP_SECRET);
            }
            if (ReaderConfig.USE_QQ) {
                PlatformConfig.setQQZone(ReaderConfig.QQ_APPID, ReaderConfig.QQ_SECRET);
            }
            initImageLoader(this);
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

    public void initOpenInstall() {
        if (isMainProcess()) {
            OpenInstall.init(this);
        }
    }

    public boolean isMainProcess() {
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
     * 初始化imageloader
     *
     * @param context
     */
    public void initImageLoader(Context context) {
        //缓存文件的目录
        File cacheDir = StorageUtils.getOwnCacheDirectory(context, "heiheilianzai/reader/Cache");
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3) //线程池内线程的数量
                .threadPriority(Thread.NORM_PRIORITY - 2).denyCacheImageMultipleSizesInMemory().diskCacheFileNameGenerator(new Md5FileNameGenerator()) //将保存的时候的URI名称用MD5 加密
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)).memoryCacheSize(2 * 1024 * 1024) // 内存缓存的最大值
                .diskCacheSize(50 * 1024 * 1024) // SD卡缓存的最大值
                .tasksProcessingOrder(QueueProcessingType.LIFO).diskCache(new UnlimitedDiskCache(cacheDir))//自定义缓存路径
                .imageDownloader(new BaseImageDownloader(context, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                //                .writeDebugLogs() // Remove for release app
                .build();
        //全局初始化此配置
        ImageLoader.getInstance().init(config);

        // 使用DisplayImageOptions.Builder()创建DisplayImageOptions
        DisplayImageOptions options = new DisplayImageOptions.Builder()
//                .showImageOnLoading(R.mipmap.ic_launcher) // 设置图片下载期间显示的图片
//                .showImageForEmptyUri(R.mipmap.ic_launcher) // 设置图片Uri为空或是错误的时候显示的图片
//                .showImageOnFail(R.mipmap.ic_launcher) // 设置图片加载或解码过程中发生错误显示的图片
                .cacheInMemory(true) // 设置下载的图片是否缓存在内存中
                .cacheOnDisk(true) // 设置下载的图片是否缓存在SD卡中
                //				.displayer(new RoundedBitmapDisplayer(20)) // 设置成圆角图片
                .build(); // 构建完成
        mOptions = options;
    }


    public static DisplayImageOptions getOptions() {
        return mOptions;
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
}
