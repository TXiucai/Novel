package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.ScrollingMovementMethod;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.DownloadUtil;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.event.ExitAppEvent;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.view.ProgressBarView;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 版本更新 工具类
 * Created by scb on 2018/11/28.
 */
public class UpdateApp {
    Activity activity;

    public UpdateApp(Activity activity) {
        this.activity = activity;
    }

    public UpdateApp() {
    }

    public interface UpdateAppInterface {
        void Next(String str);

        void onError(String e);
    }

    /**
     * 请求 系统版本更新及初始化参数。（是否使用缓存，并更新缓存）
     *
     * @param updateAppInterface
     */
    public void getRequestData(final UpdateAppInterface updateAppInterface) {
        ReaderParams readerParams = new ReaderParams(activity);
        String json = readerParams.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mSystemParam, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        ShareUitls.putString(activity, "Update", response);
                        ReaderConfig.newInstance().AppUpdate = response;
                        try {
                            AppUpdate mAppUpdate = new Gson().fromJson(response, AppUpdate.class);
                            ReaderConfig.newInstance().app_free_charge = mAppUpdate.pay_switch == 1 ? false : true;
                            ReaderConfig.guide_text = mAppUpdate.getGuide_text();
                            ReaderConfig.display_second = mAppUpdate.getDisplay_second();
                            ReaderConfig.novel_tips = mAppUpdate.book_read_tip_title;
                        } catch (Exception e) {
                        }
                        updateAppInterface.Next(response);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        updateAppInterface.onError(ex);
                    }
                }
        );
    }

    /**
     * 获取当前 的 渠道号
     *
     * @param activity
     * @return
     */
    public static String getChannelName(Context activity) {
        String channelName = null;
        try {
            PackageManager packageManager = activity.getPackageManager();
            if (packageManager != null) {
                //注意此处为ApplicationInfo 而不是 ActivityInfo,因为友盟设置的meta-data是在application标签中，而不是某activity标签中，所以用ApplicationInfo
                ApplicationInfo applicationInfo = packageManager.
                        getApplicationInfo(activity.getPackageName(), PackageManager.GET_META_DATA);
                if (applicationInfo != null) {
                    if (applicationInfo.metaData != null) {
                        channelName = String.valueOf(applicationInfo.metaData.get("UMENG_CHANNEL"));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (channelName == null || channelName.equals("null")) ? "yingyongbao" : channelName;
    }

    class UpdateHolder {
        @BindView(R.id.dialog_updateapp_sec)
        public TextView dialog_updateapp_sec;
        @BindView(R.id.dialog_updateapp_no)
        public TextView dialog_updateapp_no;
        @BindView(R.id.dialog_updateapp_yes)
        public TextView dialog_updateapp_yes;
        @BindView(R.id.dialog_updateapp_layout)
        public LinearLayout dialog_updateapp_layout;
        @BindView(R.id.dialog_updateapp_view)
        public View dialog_updateapp_view;
        @BindView(R.id.materialSeekBar)
        public ProgressBarView materialSeekBar;
        @BindView(R.id.dialog_updateapp_version)
        public TextView dialog_updateapp_version;
        @BindView(R.id.img_close)
        public ImageView dialog_close;
        @BindView(R.id.dialog_ll_website)
        public LinearLayout dialog_ll_website;
        @BindView(R.id.dialog_tx_web)
        public TextView dialog_tx_web;

        public UpdateHolder(View view) {
            ButterKnife.bind(this, view);
        }

    }

    public interface SuccessInstal {
        void success(String address);
    }

    public Dialog popupWindow;
    boolean flag = false;
    String urlmd5;
    String applocal;

    public Dialog getAppUpdatePop(final Activity activity, final AppUpdate mAppUpdate) {
        this.activity = activity;
        if (mAppUpdate.getUrl() == null) {
            return null;
        }
        urlmd5 = Utils.MD5(mAppUpdate.getUrl() + Utils.getAppVersionName(activity));
        applocal = FileManager.getSDCardRoot() + "/Apk/" + urlmd5 + "/heiheilianzai.apk";
        File tempfile = new File(applocal);
        if (tempfile.exists()) {
            GetDialog.IsOperationPositiveNegative(activity, String.valueOf(activity.getText(R.string.app_updatefile_exist_dialog_title)), String.valueOf(activity.getText(R.string.app_updatefile_exist_dialog_suretext)), null, new GetDialog.IsOperationInterface() {
                @Override
                public void isOperation() {//确定安装app
                    installApp(activity, tempfile);
                }
            }, new GetDialog.IsNegativeInterface() {
                @Override
                public void isNegative() {//取消删除本地文件。（如果是强制更新退出app）
                    FileManager.deleteFile(applocal);
                    if (mAppUpdate.getUpdate() == 2) {
                        Toast.makeText(activity.getApplicationContext(), String.valueOf(activity.getText(R.string.app_updatefile_exist_dialog_negative_warn)), Toast.LENGTH_LONG).show();
                        EventBus.getDefault().post(new ExitAppEvent());
                    }
                }
            }, true, true, mAppUpdate.getUpdate() == 1);
            return null;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_update_app, null);
        popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        final UpdateHolder updateHolder = new UpdateHolder(view);
        updateHolder.dialog_updateapp_version.setText(activity.getText(R.string.app_update));
        updateHolder.dialog_updateapp_sec.setText(mAppUpdate.getMsg());
        updateHolder.dialog_updateapp_sec.setMovementMethod(ScrollingMovementMethod.getInstance());
        SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.string_go_web));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        updateHolder.dialog_tx_web.setText(spannableString);
        if (mAppUpdate.getUpdate() == 1) {
            updateHolder.dialog_close.setVisibility(View.VISIBLE);
            updateHolder.dialog_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismissPop();
                }
            });
        } else {
            updateHolder.dialog_close.setVisibility(View.GONE);
        }
        updateHolder.dialog_updateapp_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(mAppUpdate.getWebsite_android())) {
                    if (mAppUpdate.getUpdate() == 1) {//强制更新即使去官网下载也不能关闭
                        dismissPop();
                    }
                    activity.startActivity(new Intent(activity, AboutActivity.class)
                            .putExtra("url", mAppUpdate.getWebsite_android())
                            .putExtra("style", "4"));
                }
            }
        });
        updateHolder.dialog_updateapp_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateHolder.materialSeekBar.setVisibility(View.VISIBLE);
                updateHolder.dialog_updateapp_layout.setVisibility(View.GONE);
                updateHolder.dialog_ll_website.setVisibility(View.VISIBLE);
                MyToash.Log("onDownloading", "111 " + flag);
                if (!flag) {
                    flag = true;
                    downloadApk(activity, mAppUpdate, updateHolder);
                }
            }
        });
        updateHolder.dialog_tx_web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!StringUtils.isEmpty(mAppUpdate.getWebsite_android())) {
                    activity.startActivity(new Intent(activity, AboutActivity.class)
                            .putExtra("url", mAppUpdate.getWebsite_android())
                            .putExtra("style", "4"));
                }
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    private void dismissPop() {
        if (popupWindow != null) {
            popupWindow.dismiss();
            RecomendApp recomendApp = new RecomendApp(activity);
            recomendApp.getRequestData();
            new DialogExpirerdVip().getUserInfo(activity);
        }
    }

    /**
     * 下载新版本
     */
    protected void downloadApk(final Activity activity, final AppUpdate mAppUpdate, final UpdateHolder updateHolder) {
        File tempfile = new File(FileManager.getSDCardRoot() + "/Apk/" + urlmd5 + "/");
        if (!tempfile.exists()) {
            tempfile.mkdirs();
        }
        new Thread(new Runnable() {
            @Override
            public void run() {
                DownloadUtil.get().download(mAppUpdate.getUrl(), tempfile.getAbsolutePath(), "heiheilianzaitemp.apk",
                        new DownloadUtil.OnDownloadListener() {
                            @Override
                            public void onDownloadSuccess(File file) {
                                updateHolder.materialSeekBar.setProgress(100);
                                try {
                                    File file1 = new File(applocal);
                                    file.renameTo(file1);
                                    installApp(activity, file1);
                                } catch (Exception e) {
                                }
                                installApp(activity, file);
                            }

                            @Override
                            public void onDownloading(int total, int progress) {
                                updateHolder.materialSeekBar.setProgress((int) ((float) progress * 100 / (float) total));
                            }

                            @Override
                            public void onDownloadFailed(Exception e) {
                                activity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        MyToash.ToashError(activity, "网络异常,升级失败");
                                    }
                                });
                            }
                        });
            }
        }).start();
    }

    public static void installApp(Context pContext, File pFile) {
        if (null == pFile)
            return;
        if (!pFile.exists())
            return;
        MyToash.Log("installApp", pFile.toString());
        Intent _Intent = new Intent();
        _Intent.setAction(Intent.ACTION_VIEW);
        Uri _uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            _Intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            _uri = FileProvider.getUriForFile(pContext, pContext.getPackageName() + ".fileProvider", pFile);
        } else {
            _uri = Uri.fromFile(pFile);
            _Intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        _Intent.setDataAndType(_uri, "application/vnd.android.package-archive");
        pContext.startActivity(_Intent);
    }
}
