package com.heiheilianzai.app.ui.activity.setting;

import android.app.Activity;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.ClearCacheManager;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;
import com.umeng.socialize.UMShareAPI;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

import butterknife.BindView;


/**
 * 设置详情
 */
public class SettingsActivity extends BaseActivity implements View.OnClickListener, ShowTitle {
    @BindView(R.id.activity_settings_same)
    ToggleButton activity_settings_same;
    @BindView(R.id.activity_settings_wifi)
    ToggleButton activity_settings_wifi;
    @BindView(R.id.activity_settings_auto)
    ToggleButton activity_settings_auto;
    @BindView(R.id.activity_settings_switch_container)
    View activity_settings_switch_container;
    @BindView(R.id.activity_settings_logout)
    View activity_settings_logout;
    @BindView(R.id.activity_settings_clear_cache)
    View activity_settings_clear_cache;
    @BindView(R.id.activity_settings_support)
    View activity_settings_support;
    @BindView(R.id.activity_settings_about)
    View activity_settings_about;
    @BindView(R.id.activity_settings_language)
    View activity_settings_language;
    @BindView(R.id.automation_buy_next)
    View automation_buy_next;
    @BindView(R.id.automation_buy_next_lv)
    View automation_buy_next_lv;
    @BindView(R.id.activity_settings_version)
    View activity_settings_version;
    @BindView(R.id.activity_settings_version_test)
    TextView activity_settings_version_test;
    Activity activity;
    public static boolean chengeLangaupage;
    private AppUpdate mAppUpdate;
    private Dialog popupWindow;

    @Override
    public int initContentView() {
        return R.layout.activity_settings;
    }

    @Override
    public void initView() {
        activity = this;
        initTitleBarView(LanguageUtil.getString(this, R.string.MineNewFragment_set));
        setVersionView();
        if (ReaderConfig.USE_PAY) {
            activity_settings_switch_container.setVisibility(Utils.isLogin(this) ? View.VISIBLE : View.GONE);
            activity_settings_logout.setVisibility(Utils.isLogin(this) ? View.VISIBLE : View.GONE);
            if (AppPrefs.getSharedBoolean(this, ReaderConfig.IS3G4G, true)) {
                activity_settings_same.setToggleOn();
            } else {
                activity_settings_same.setToggleOff();
            }
            if (AppPrefs.getSharedBoolean(this, ReaderConfig.WIFIDOWNLOAD, true)) {
                activity_settings_wifi.setToggleOn();
            } else {
                activity_settings_wifi.setToggleOff();
            }
            if (AppPrefs.getSharedBoolean(this, ReaderConfig.AUTOBUY, true)) {
                activity_settings_auto.setToggleOn();
            } else {
                activity_settings_auto.setToggleOff();
            }
            activity_settings_same.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    AppPrefs.putSharedBoolean(activity, ReaderConfig.IS3G4G, on);
                }
            });
            activity_settings_wifi.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    AppPrefs.putSharedBoolean(activity, ReaderConfig.WIFIDOWNLOAD, on);
                }
            });
            activity_settings_auto.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                @Override
                public void onToggle(boolean on) {
                    Auto_sub(activity, new Auto_subSuccess() {
                        @Override
                        public void success(boolean open) {
                            if (open) {
                                activity_settings_auto.setToggleOn();
                            } else {
                                activity_settings_auto.setToggleOff();
                            }
                        }
                    });
                }
            });
        }
        activity_settings_clear_cache.setOnClickListener(this);
        activity_settings_support.setOnClickListener(this);
        activity_settings_about.setOnClickListener(this);
        activity_settings_logout.setOnClickListener(this);
        findViewById(R.id.activity_settings_share).setOnClickListener(this);
        activity_settings_language.setOnClickListener(this);
        activity_settings_version.setOnClickListener(this);
        uiFreeCharge();
    }

    @Override
    public void initData() {
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_settings_clear_cache:
                customDialog();
                break;
            case R.id.activity_settings_support:
                support(this);
                break;
            case R.id.activity_settings_about:
                startActivity(new Intent(this, AboutUsActivity.class));
                break;
            case R.id.activity_settings_share:
                MyShare.ShareAPP(activity);
                break;
            case R.id.activity_settings_logout:
                //所有登录用户置为none用户
                exitUser(this);
                EventBus.getDefault().post(new RefreshMine(null));
                //EventBus.getDefault().post(new RefreshDiscoveryFragment());
                finish();
                break;
            case R.id.activity_settings_language:
                //所有登录用户置为none用户
                ChengeLangaupage();
                break;
            case R.id.activity_settings_version://版本更新
                showVersionDialog();
                break;
        }
    }

    public static void exitUser(Activity activity) {
        if (!Utils.isLogin(activity)) {
            return;
        }
        LoginModel.resetLogin(activity);
    }


    /**
     * 好评支持
     */
    public static void support(Activity activity) {
        try {
            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.SettingsActivity_nomark));
        }
    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (chengeLangaupage) {
                    startActivity(new Intent(activity, MainActivity.class));
                }
                finish();
            }
        });
        mTitle.setText(text);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void clearCache(ClearCacheManager manager) {
        MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.SettingsActivity_clearSeccess));
    }

    /**
     * 自定义对话框
     */
    private void customDialog() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_clear_cache_tip, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FileManager.deleteFile(FileManager.getSDCardRoot());
                ClearCacheManager.clearAllCache(activity);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }


    public interface Auto_subSuccess {
        void success(boolean open);
    }

    public static void Auto_sub(final Activity activity, final Auto_subSuccess auto_subSuccess) {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.auto_sub, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(result);
                            String flag = jsonObject.getString("auto_sub");
                            if (flag.equals("0")) {
                                auto_subSuccess.success(false);
                                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, false);
                            } else {
                                auto_subSuccess.success(true);
                                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, true);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void ChengeLangaupage() {
        final Dialog dialog = new Dialog(this, R.style.userInfo_avatar);
        View view = View.inflate(this, R.layout.user_img_dialog, null);
        TextView checkImgGallery = view.findViewById(R.id.checkimg_gallery);
        TextView checkImgCamera = view.findViewById(R.id.checkimg_camera);
        View checkImgCancel = view.findViewById(R.id.checkimg_cancel);
        View checkimg_3_view = view.findViewById(R.id.checkimg_3_view);
        TextView checkimg_3 = view.findViewById(R.id.checkimg_3);
        checkimg_3_view.setVisibility(View.VISIBLE);
        checkimg_3.setVisibility(View.VISIBLE);
        checkImgGallery.setText(LanguageUtil.getString(activity, R.string.SettingsActivity_zhhk));
        checkImgCamera.setText(LanguageUtil.getString(activity, R.string.SettingsActivity_zhcn));
        checkimg_3.setText(LanguageUtil.getString(activity, R.string.SettingsActivity_EN));
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        checkImgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LanguageUtil.reFreshLanguage(Locale.TRADITIONAL_CHINESE, activity, SettingsActivity.class);
            }
        });
        checkImgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LanguageUtil.reFreshLanguage(Locale.SIMPLIFIED_CHINESE, activity, SettingsActivity.class);
            }
        });
        checkimg_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                LanguageUtil.reFreshLanguage(Locale.UK, activity, SettingsActivity.class);
            }
        });
        checkImgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 根据是否免费调整UI变化
     */
    private void uiFreeCharge() {
        uiFreeCharge(automation_buy_next, automation_buy_next_lv);
    }

    /**
     * 版本相关ui初始化
     */
    private void setVersionView() {
        String str = ShareUitls.getString(activity, "Update", "");
        if (str.length() > 0) {
            mAppUpdate = new Gson().fromJson(str, AppUpdate.class);
            if (mAppUpdate != null && (mAppUpdate.getUpdate() == 1 || mAppUpdate.getUpdate() == 2)) {
                activity_settings_version_test.setText(getString(R.string.SettingsActivity_version_new));
            } else {
                activity_settings_version_test.setText(getString(R.string.SettingsActivity_v) + BuildConfig.VERSION_NAME);
            }
        }
    }

    /**
     * 版本更新弹框
     */
    private void showVersionDialog() {
        if (mAppUpdate != null) {//从本地
            showVersionDialog(mAppUpdate);
        } else {//从网络
            new UpdateApp(SettingsActivity.this).getRequestData(new UpdateApp.UpdateAppInterface() {
                @Override
                public void Next(String response) {
                    try {
                        if (response.length() != 0) {
                            mAppUpdate = new Gson().fromJson(response, AppUpdate.class);
                            showVersionDialog(mAppUpdate);
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onError(String e) {
                }
            });
        }
    }

    private void showVersionDialog(AppUpdate mAppUpdate) {
        if (mAppUpdate != null) {
            if (mAppUpdate.getUpdate() == 1 || mAppUpdate.getUpdate() == 2) {
                popupWindow = new UpdateApp().getAppUpdatePop(SettingsActivity.this, mAppUpdate);
            } else {
                MyToash.ToashSuccess(this, getString(R.string.SettingsActivity_version_now));
            }
        }
    }
}
