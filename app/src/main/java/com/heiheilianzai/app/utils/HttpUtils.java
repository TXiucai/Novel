package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.content.Intent;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.ReadNovelService;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.constant.RabbitConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.ui.activity.RechargeActivity;
import com.heiheilianzai.app.ui.dialog.WaitDialog;
import com.heiheilianzai.app.utils.decode.AESUtil;
import com.umeng.umcrash.UMCrash;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jmessage.support.qiniu.android.utils.StringUtils;
import okhttp3.Request;

/**
 * 网络请求 post
 * 使用get请求参考 {@link OkHttpEngine#getAsyncHttp}
 * Created by abc on 2016/7/18.
 */
public class HttpUtils {
    private static HttpUtils httpUtils;
    private static Activity context;
    private WaitDialog waitDialog;

    public interface ResponseListener {
        void onResponse(String response) throws JSONException;

        void onErrorResponse(String ex);
    }

    public interface ResponseListenerDialog {
        void onResponse(String response, WaitDialog waitDialog);

        void onErrorResponse(String ex);
    }

    public HttpUtils() {
    }

    public static HttpUtils getInstance(Activity activity) {
        if (activity != null) {
            context = activity;
            if (httpUtils == null) {
                synchronized (HttpUtils.class) {
                    if (httpUtils == null) {
                        httpUtils = new HttpUtils();
                    }
                }
            }
        }
        return httpUtils;
    }


    private void initDialog() {
        if (waitDialog != null) {
            waitDialog.dismissDialog();
        }
        waitDialog = null;
        waitDialog = new WaitDialog(context);
        waitDialog.setCancleable(true);
    }

    public void sendRequestRequestParams3(final String url, final String body, final boolean dialog, final ResponseListener responseListener) {
        if (context == null) {
            responseListener.onErrorResponse(null);
            return;
        }
        if (waitDialog != null) {
            waitDialog.dismissDialog();
        }
        if (InternetUtils.internet(context)) {
            if (dialog) {
                initDialog();
                waitDialog.showDailog();
            }
            if (!RabbitConfig.ONLINE) {
                MyToash.Log("httpUtils_log21", url + "   " + body);
            }
            OkHttpEngine.getInstance(context).postAsyncHttp(url, body, new ResultCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToash.ToashError(context, LanguageUtil.getString(context, R.string.splashactivity_nonet));
                            UMCrash.generateCustomLog(LanguageUtil.getString(context, R.string.splashactivity_nonet) + ":" + " " + body, deletDomain(url));
                            hideProgress(dialog);
                            responseListener.onErrorResponse(null);
                        }
                    });
                }

                @Override
                public void onResponse(final String result) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                hideProgress(dialog);
                                if (!RabbitConfig.ONLINE) {
                                    MyToash.LogJson("http_utils  " + url, result);
                                }
                                JSONObject jsonObj = new JSONObject(result);
                                int code = jsonObj.getInt("code");
                                String msg = jsonObj.getString("msg");
                                String capi = getResultCapi(result);
                                switch (code) {
                                    case 0:
                                        if (!RabbitConfig.ONLINE) {
                                            MyToash.LogJson("http_utils  " + url, ReaderConfig.API_CRYPTOGRAPHY.equals(capi) ? AESUtil.decrypt(jsonObj.getString("data"), AESUtil.API_ASE_KEY, AESUtil.API_IV) : jsonObj.getString("data"));
                                        }
                                        responseListener.onResponse(ReaderConfig.API_CRYPTOGRAPHY.equals(capi) ? AESUtil.decrypt(jsonObj.getString("data"), AESUtil.API_ASE_KEY, AESUtil.API_IV) : jsonObj.getString("data"));
                                        break;
                                    case 315:
                                        MyToash.ToashError(context, msg);
                                        responseListener.onResponse(code + "");
                                        break;
                                    case 301://登录已过期
                                        if (Utils.isLogin(context)) {
                                            LoginModel.resetLogin(context);
                                            MyToash.ToashError(context, msg);
                                            Intent intentService = new Intent(App.getAppContext(), ReadNovelService.class);
                                            App.getAppContext().stopService(intentService);
                                        }
                                        responseListener.onErrorResponse(null);
                                        break;
                                    case 802://余额不足 充值
                                        MyToash.ToashError(context, msg);
                                        context.startActivity(new Intent(context, RechargeActivity.class));
                                        responseListener.onErrorResponse(null);
                                        break;
                                    case 701://余额不足 充值
                                        MyToash.ToashError(context, msg);
                                        responseListener.onErrorResponse("701");
                                        break;
                                    default:
                                        if (code != 311 && code != 300) {//今日已签到//用户不存在
                                            MyToash.ToashError(context, msg);
                                            UMCrash.generateCustomLog(msg, deletDomain(url));
                                        }
                                        responseListener.onErrorResponse(null);
                                        break;
                                }
                            } catch (Exception j) {
                                UMCrash.generateCustomLog(j.toString(), deletDomain(url));
                            } catch (Error e) {
                                UMCrash.generateCustomLog(e.toString(), deletDomain(url));
                            }
                        }
                    });

                }
            });
        } else {
            MyToash.Log("getCurrentComicChapter", "  sss");
            responseListener.onErrorResponse("nonet");
        }
    }

    /**
     * 关闭dialog
     *
     * @param dialog
     */
    private void hideProgress(boolean dialog) {
        if (dialog) {
            if (waitDialog != null) {
                waitDialog.dismissDialog();
            }
        }
    }

    public void sendRequestRequestParamsDialog(final String url, final String body, final ResponseListenerDialog responseListener) {
        if (context == null) {
            responseListener.onErrorResponse(null);
            return;
        }
        if (waitDialog != null) {
            waitDialog.dismissDialog();
        }
        if (InternetUtils.internet(context)) {
            initDialog();
            waitDialog.showDailog();
            OkHttpEngine.getInstance(context).postAsyncHttp(url, body, new ResultCallback() {
                @Override
                public void onError(Request request, Exception e) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            MyToash.ToashError(context, LanguageUtil.getString(context, R.string.splashactivity_nonet));
                            UMCrash.generateCustomLog(LanguageUtil.getString(context, R.string.splashactivity_nonet) + ":" + " " + body, deletDomain(url));
                            hideProgress(true);
                            responseListener.onErrorResponse(null);
                        }
                    });
                }

                @Override
                public void onResponse(final String result) {
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int code = 0;
                            try {
                                hideProgress(true);
                                if (!RabbitConfig.ONLINE) {
                                    MyToash.LogJson("http_utils  " + url, result);
                                }
                                JSONObject jsonObj = new JSONObject(result);
                                code = jsonObj.getInt("code");
                                String msg = jsonObj.getString("msg");
                                String capi = getResultCapi(result);
                                switch (code) {
                                    case 0:
                                        responseListener.onResponse(ReaderConfig.API_CRYPTOGRAPHY.equals(capi) ? AESUtil.decrypt(jsonObj.getString("data"), AESUtil.API_ASE_KEY, AESUtil.API_IV) : jsonObj.getString("data"), waitDialog);
                                        break;
                                    case 301://登录已过期
                                        if (Utils.isLogin(context)) {
                                            LoginModel.resetLogin(context);
                                            MyToash.ToashError(context, msg);
                                            Intent intentService = new Intent(App.getAppContext(), ReadNovelService.class);
                                            App.getAppContext().stopService(intentService);
                                        }
                                        responseListener.onErrorResponse(null);
                                        break;
                                    case 802://余额不足 充值
                                        MyToash.ToashError(context, msg);
                                        context.startActivity(new Intent(context, RechargeActivity.class));
                                        responseListener.onErrorResponse(null);
                                        break;
                                    case 701://余额不足 充值
                                        MyToash.ToashError(context, msg);
                                        responseListener.onErrorResponse("701");
                                        break;
                                    default:
                                        if (code != 311 && code != 300) {//今日已签到//用户不存在
                                            MyToash.ToashError(context, msg);
                                        }
                                        responseListener.onErrorResponse(null);
                                        break;
                                }
                            } catch (JSONException j) {
                            } catch (Exception j) {
                            }
                        }
                    });
                }
            });
        } else {
            responseListener.onErrorResponse("nonet");
        }
    }

    private String getResultCapi(String result) throws JSONException {
        String capi = null;
        JSONObject jsonObj = new JSONObject(result);
        if (result.contains("capi")) {
            capi = jsonObj.getString("capi");
        }
        App.setCipherApi(StringUtils.isNullOrEmpty(capi) ? "0" : capi);
        return capi;
    }

    /**
     * 删除请求url域名
     *
     * @param url
     * @return
     */
    private String deletDomain(String url) {
        if (!StringUtils.isNullOrEmpty(url)) {
            return url.replace(ReaderConfig.getBaseUrl(), "");
        }
        return url;
    }
}


