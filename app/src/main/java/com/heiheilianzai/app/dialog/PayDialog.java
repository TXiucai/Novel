package com.heiheilianzai.app.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.webview.ReaderWebView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 新支付H5嵌入WebView使用统一支付渠道 Dialog
 */
public class PayDialog {
    PopupWindow popupWindow;
    Activity activity;
    PayInterface payInterface;
    View loading;
    boolean isPay;

    public void showDialog(final Activity activity, View v, String url) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        isPay = false;
        this.activity = activity;
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_pay, null);
        ReaderWebView webview = view.findViewById(R.id.webview);
        loading = view.findViewById(R.id.loading_layout);
        webview.setBackgroundColor(1);
        webview.setWebViewClient(new MyWebViewClient());
        WebSettings settings = webview.getSettings();
        settings.setAllowFileAccessFromFileURLs(true);
        settings.setJavaScriptEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setDatabaseEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        webview.addJavascriptInterface(new JavaScriptInterface(), "xmyydb");//约定字段
        webview.loadUrl(url);
        popupWindow = new PopupWindow(view, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ScreenSizeUtils.getInstance(activity).getScreenHeight(), true);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.setAnimationStyle(R.style.userInfo_avatar);
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
    }

    public class JavaScriptInterface {
        @JavascriptInterface
        public void depositFinish(String depositJson) {//点击左上角返回回调
            MyToash.LogE(" depositFinish", depositJson);
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (popupWindow.isShowing()) {
                            popupWindow.dismiss();
                        }
                        if (payInterface != null && isPay) {
                            payInterface.onPayFinish();
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void nativePay(String payType, String jsonData) {//调用原生支付宝、微信SDK
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (payInterface != null) {
                        payInterface.nativePay(payType, jsonData);
                    }
                }
            });
        }

        @JavascriptInterface
        public void openOutSideUrl(String url) {//打开外部浏览器回调
            MyToash.LogE("PayDialog openOutSideUrl", url);
            if (StringUtils.isEmpty(url)) {
                return;
            }
            try {
                url = new JSONObject(url).getString("url");
                if (!StringUtils.isEmpty(url) && url.startsWith("http")) {
                    agselfBrowser(url);
                } else {
                    MyToash.ToashError(activity, "支付渠道不可用");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        @JavascriptInterface
        public void openPage(int page) {
        }

        @JavascriptInterface
        public void loadingFinish(String s) {// H5支付页面加载完成回调
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (loading != null) {
                            loading.setVisibility(View.GONE);
                        }
                    }
                });
            }
        }

        @JavascriptInterface
        public void openNewPayUrl(String url) {
        }
    }

    private class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView webview, String url) {//拦截H5重定向跳转外部URL，跳转浏览
            MyToash.LogE("PayDialog shouldOverrideUrlLoading", url);
            agselfBrowser(url);
            return true;
        }
    }

    /**
     * 跳转到浏览器
     */
    public void agselfBrowser(String url) {
        if (TextUtils.isEmpty(url))
            return;
        try {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            activity.startActivity(intent);
            isPay = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Activity支付操作回调
     */
    public interface PayInterface {
        void onPayFinish();

        void nativePay(String payType, String jsonData);
    }

    public void setPayInterface(PayInterface payInterface) {
        this.payInterface = payInterface;
    }
}
