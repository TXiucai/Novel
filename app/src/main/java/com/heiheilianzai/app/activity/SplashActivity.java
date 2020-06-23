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
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.AppUpdate;
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
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.activity.TaskCenterActivity.SignHttp;
import static com.heiheilianzai.app.config.ReaderConfig.USE_AD_FINAL;

/**
 * 闪屏页
 */
public class SplashActivity extends Activity {
    public static Activity activity;
    String isfirst;
    UpdateApp updateApp;
    @BindView(R2.id.activity_splash_im)
    public ImageView activity_splash_im;
    @BindView(R2.id.activity_home_viewpager_sex_next)
    public TextView activity_home_viewpager_sex_next;
    AppUpdate.Startpage startpage;
    boolean skip = false;
    int time = 5;
    boolean into;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        JPushUtil.setAlias(getApplicationContext());
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
        activity = SplashActivity.this;
        updateApp = new UpdateApp(activity);
        activity_home_viewpager_sex_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getString(R.string.splashactivity_skip).equals(activity_home_viewpager_sex_next.getText().toString())) {//只有跳过才跳转，其他状态点击无响应（不跳广告）
                    skip = true;
                    handler.sendEmptyMessageDelayed(0, 0);
                }
            }
        });
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
                    } else {
                        startActivity(new Intent(activity, MainActivity.class));
                    }
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                if (time == 0) {
                    activity_home_viewpager_sex_next.setText(getString(R.string.splashactivity_skip));
                } else {
                    activity_home_viewpager_sex_next.setText(LanguageUtil.getString(activity, R.string.splashactivity_surplus) + " " + (time--) + " ");
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
                    if (response.length() != 0) {
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        if (dataBean.getUnit_tag() != null) {
                            ReaderConfig.currencyUnit = dataBean.getUnit_tag().getCurrencyUnit();
                            ReaderConfig.subUnit = dataBean.getUnit_tag().getSubUnit();
                        }
                        if (USE_AD_FINAL) {
                            ReaderConfig.ad_switch = dataBean.ad_switch;
                            ReaderConfig.USE_AD = dataBean.ad_switch == 1;
                        }
                        ReaderApplication.putDailyStartPageMax(dataBean.daily_max_start_page);
                        if (!isfirst.equals("yes")) {
                            if (ReaderApplication.getDailyStartPageMax() == 0 || ReaderApplication.getDailyStartPage() < ReaderApplication.getDailyStartPageMax()) {//是否超过后台设置的每天启动次数
                                startpage = dataBean.start_page;
                                if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putBoolean("advertisement", true);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                } else {
                                    handler.sendEmptyMessageDelayed(0, 500);
                                }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }
}
