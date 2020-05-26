package com.heiheilianzai.app.activity;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppInstallAdapter;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.listener.AppWakeUpListener;
import com.fm.openinstall.model.AppData;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.comic.activity.ComicInfoActivity;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.domain.DynamicDomainManager;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.UpdateApp;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.activity.TaskCenterActivity.SignHttp;
import static com.heiheilianzai.app.config.ReaderConfig.USE_AD_FINAL;

/**
 * 闪屏页
 */
public class SplashActivity extends Activity {
    private AppWakeUpAdapter mWakeUpAdapter;
    public static Activity activity;
    String isfirst;
    UpdateApp updateApp;
    @BindView(R2.id.activity_splash_im)
    public ImageView activity_splash_im;
    @BindView(R2.id.activity_home_viewpager_sex_next)
    public TextView activity_home_viewpager_sex_next;
    AppUpdate.Startpage startpage;
    boolean skip = false;
    String splashactivity_skip;
    int time = 5;
    boolean into;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPushUtil.setAlias(getApplicationContext());
        //首次启动 Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT 为 0，再次点击图标启动时就不为零了
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        OpenInstall.getWakeUp(getIntent(), getmWakeUpAdapter());
        initOpenInstall();
        activity_home_viewpager_sex_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip = true;
                handler.sendEmptyMessageDelayed(0, 0);
            }
        });
        activity = SplashActivity.this;
        splashactivity_skip = LanguageUtil.getString(activity, R.string.splashactivity_skip);
        updateApp = new UpdateApp(activity);
        //首次启动 flag
        isfirst = ShareUitls.getString(activity, "isfirst", "yes");
        if (isfirst.equals("yes")) {//首次使用删除文件
            FileManager.deleteFile(FileManager.getSDCardRoot());
        }
        DynamicDomainManager dynamicDomainManager = new DynamicDomainManager(this, new DynamicDomainManager.OnCompleteListener() {
            @Override
            public void onComplete(String domain) {
                findViewById(R.id.findchannel).setVisibility(View.GONE);
                ReaderApplication.setBaseUrl(domain);
                requestReadPhoneState();
            }
        });
        dynamicDomainManager.start();
    }

    private void requestReadPhoneState() {
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            hasPermission();
        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    //判断是否开启壳子
                    hasPermission();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {//@Nullable String title,  @Nullable String content,  @Nullable String cancel,  @Nullable String ensure
                    finish();
                }
            }, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, true, new PermissionsUtil.TipInfo(activity.getString(R.string.splashactivity_permissions_t), activity.getString(R.string.splashactivity_permissions_c1) + activity.getString(R.string.app_name) + activity.getString(R.string.splashactivity_permissions_c2), activity.getString(R.string.splashactivity_permissions_cancle), activity.getString(R.string.splashactivity_permissions_set)));
        }
    }

    private void hasPermission() {
        //判断是否开启壳子
        updateApp.getCheck_switch(new UpdateApp.UpdateAppInterface() {
            @Override
            public void Next(String response) {
                next();
                //自动签到
                SignHttp(activity);
                //主界面接口缓存
                MainHttpTask.getInstance().InitHttpData(activity);
            }
        });
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (!into) {
                    into = true;
                    if (InternetUtils.internett(activity) && isfirst.equals("yes")) {
                        startActivity(new Intent(activity, FirstStartActivity.class));
                    } else startActivity(new Intent(activity, MainActivity.class));
                }
            } else {
                if (time == 0) {
                    activity_home_viewpager_sex_next.setText(splashactivity_skip + (0) + " ");
                    handler.sendEmptyMessageDelayed(0, 0);
                } else {
                    activity_home_viewpager_sex_next.setText(splashactivity_skip + (time--) + " ");
                    handler.sendEmptyMessageDelayed(1, 1000);
                }
            }
        }
    };

    public void next() {
        updateApp.getRequestData(new UpdateApp.UpdateAppInterface() {
            @Override
            public void Next(String response) {
                try {
                    if (response.length() != 0) {//
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        if (dataBean.getUnit_tag() != null) {
                            ReaderConfig.currencyUnit = dataBean.getUnit_tag().getCurrencyUnit();
                            ReaderConfig.subUnit = dataBean.getUnit_tag().getSubUnit();
                        }
                        if (USE_AD_FINAL) {
                            ReaderConfig.ad_switch = dataBean.ad_switch;
                            ReaderConfig.USE_AD = dataBean.ad_switch == 1;
                        }
                        if (!isfirst.equals("yes")) {
                            startpage = dataBean.start_page;
                            if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                                ImageLoader.getInstance().displayImage(startpage.image, activity_splash_im, ReaderApplication.getOptions());
                                activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                                handler.sendEmptyMessageDelayed(1, 0);
                                activity_splash_im.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        skip = false;
                                        if (!skip) {
                                            handler.removeMessages(1);
                                            handler.removeMessages(0);
                                            if (startpage.skip_type == 1) {
                                                startActivity(new Intent(activity, BookInfoActivity.class).putExtra("book_id", startpage.content));
                                            } else if (startpage.skip_type == 2) {
                                                startActivity(new Intent(activity, AboutActivity.class)
                                                        .putExtra("title", startpage.title)
                                                        .putExtra("url", startpage.content)
                                                        .putExtra("flag", "splash"));
                                            } else if (startpage.skip_type == 3) {
                                                startActivity(new Intent(activity, ComicInfoActivity.class).putExtra("comic_id", startpage.content));
                                            } else {
                                                startActivity(new Intent(activity, AboutActivity.class)
                                                        .putExtra("url", startpage.content)
                                                        .putExtra("flag", "splash")
                                                        .putExtra("style", "4")
                                                );
                                            }
                                        }
                                    }
                                });
                            } else {
                                handler.sendEmptyMessageDelayed(0, 500);
                            }
                        } else {
                            handler.sendEmptyMessageDelayed(0, 500);
                        }
                    } else {
                        handler.sendEmptyMessageDelayed(0, 500);
                    }
                } catch (Exception e) {
                    handler.sendEmptyMessageDelayed(0, 500);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
        if (time != 5) {
            next();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
    }

    private void initOpenInstall() {
        if (ReaderApplication.getNeedInstall()) {  //是否是第一次启动
            //获取OpenInstall数据，推荐每次需要的时候调用，而不是自己保存数据
            OpenInstall.getInstall(new AppInstallAdapter() {
                @Override
                public void onInstall(AppData appData) {
                    String channel = appData.getChannel();
                    if (channel == null || channel.length() <= 0) {
                        channel = BuildConfig.umeng_name;
                    }
                    UMConfigure.init(getApplicationContext(), BuildConfig.umeng_key, channel, UMConfigure.DEVICE_TYPE_PHONE, null);
                    MobclickAgent.setPageCollectionMode(MobclickAgent.PageMode.AUTO);
                    ReaderApplication.setNeedInstall(false);
                }
            });

        }
    }

    public AppWakeUpListener getmWakeUpAdapter() {
        if (mWakeUpAdapter == null) {
            mWakeUpAdapter = new AppWakeUpAdapter() {
                @Override
                public void onWakeUp(AppData appData) {
                    // 获取渠道数据
                    String channelCode = appData.getChannel();
                    UMConfigure.init(SplashActivity.this, BuildConfig.umeng_key,
                            TextUtils.isEmpty(channelCode) ? BuildConfig.umeng_name : channelCode,
                            UMConfigure.DEVICE_TYPE_PHONE,
                            null);
                }
            };
        }
        return mWakeUpAdapter;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWakeUpAdapter = null;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, getmWakeUpAdapter());
    }
}
