package com.heiheilianzai.app.ui.activity.setting;

import android.app.Activity;
import android.content.ClipData;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.ui.dialog.WaitDialog;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 内嵌WebView Activity
 * flag：splash-点返回跳到首页   notitle-隐藏头部titlebar
 * style ：  4-跳转到外部浏览器，关闭当前页面    其他-使用在当前页面
 */
public class AboutActivity extends BaseActivity implements ShowTitle {
    private ValueCallback<Uri> mUploadMessage;// 表单的数据信息
    private ValueCallback<Uri[]> mUploadCallbackAboveL;
    private final static int FILECHOOSER_RESULTCODE = 1;// 表单的结果回调
    private Uri imageUri;
    private WebView mWebView;
    String flag;
    WaitDialog waitDialog;

    @Override
    public int initContentView() {
        return R.layout.activity_about;
    }

    @Override
    public void initView() {
        String str = ReaderConfig.getBaseUrl() + ReaderConfig.privacy;
        Intent intent = getIntent();
        try {
            flag = intent.getStringExtra("flag");
            if (intent != null && intent.getStringExtra("url") != null) {
                str = intent.getStringExtra("url");
                String title = intent.getStringExtra("title");
                if (title != null) {
                    initTitleBarView(title);
                } else {
                    initTitleBarView("");//ceshi
                }
                String style = intent.getStringExtra("style");
                MyToash.Log("style_", style + "  ");
                if (style != null && style.equals("4")) {
                    Intent intent2 = new Intent();
                    intent2.setAction(Intent.ACTION_VIEW);
                    Uri content_uri_browsers = Uri.parse(str);
                    intent2.setData(content_uri_browsers);
                    if (!this.isFinishing() && !this.isDestroyed()) {
                        this.startActivity(intent2);
                    }
                    finish();
                }
                if (!StringUtils.isEmpty(flag) && "notitle".equals(flag)) {
                    findViewById(R.id.titlebar_layout).setVisibility(View.GONE);
                }
            } else {
                initTitleBarView(LanguageUtil.getString(this, R.string.AboutActivity_title));
                str = "file:///android_asset/web/notify.html";
            }
            mWebView = findViewById(R.id.software_webview);
            WebSettings settings = mWebView.getSettings();
            settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
            settings.setJavaScriptEnabled(true);
            settings.setBlockNetworkImage(false);//解决图片不显示
            mWebView.addJavascriptInterface(new AndroidToJs(), "AndroidClient");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
            }
            mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //在前面加入下载监听器
            mWebView.setWebViewClient(new DemoWebViewClient());
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
                    mUploadCallbackAboveL = filePathCallback;
                    take();
                    return true;
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                    mUploadMessage = uploadMsg;
                    take();
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                    mUploadMessage = uploadMsg;
                    take();
                }

                public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                    mUploadMessage = uploadMsg;
                    take();
                }
            });
            mWebView.loadUrl(str);
        } catch (Exception e) {
        }
    }

    class DemoWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            } catch (Exception s) {
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            initDialog();
            waitDialog.showDailog();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (waitDialog != null) {
                waitDialog.dismissDialog();
            }
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            Uri uri = Uri.parse(url);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        }
    }

    @Override
    public void initData() {
    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
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
                if (flag != null && flag.equals("splash")) {
                    startActivity(new Intent(AboutActivity.this, MainActivity.class));
                }
                finish();
            }
        });
        mTitle.setText(text);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (flag != null && flag.equals("splash")) {
                startActivity(new Intent(AboutActivity.this, MainActivity.class));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage && null == mUploadCallbackAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (mUploadCallbackAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (mUploadMessage != null) {
                if (result == null) {
                    mUploadMessage.onReceiveValue(imageUri);
                    mUploadMessage = null;
                } else {
                    mUploadMessage.onReceiveValue(result);
                    mUploadMessage = null;
                }
            }
        }
    }

    private void onActivityResultAboveL(int requestCode, int resultCode, Intent data) {
        if (requestCode != FILECHOOSER_RESULTCODE || mUploadCallbackAboveL == null) {
            return;
        }
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                results = new Uri[]{imageUri};
            } else {
                String dataString = data.getDataString();
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    results = new Uri[]{Uri.parse(dataString)};
                }
            }
        }
        if (results != null) {
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        } else {
            results = new Uri[]{imageUri};
            mUploadCallbackAboveL.onReceiveValue(results);
            mUploadCallbackAboveL = null;
        }
        return;
    }

    private void take() {
        File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyApp");
        if (!imageStorageDir.exists()) {
            imageStorageDir.mkdirs();
        }
        File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
        imageUri = Uri.fromFile(file);
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(captureIntent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            i.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
    }

    public class AndroidToJs {

        @JavascriptInterface
        public void backToHome() {
            finish();
        }
    }

    private void initDialog() {
        if (waitDialog != null) {
            waitDialog.dismissDialog();
        }
        waitDialog = null;
        waitDialog = new WaitDialog(this);
        waitDialog.setCancleable(true);
    }

    @Override
    protected void onDestroy() {//关闭页面后清空所有状态，缓存。
        super.onDestroy();
        waitDialog = null;
        try {
            CookieSyncManager.createInstance(App.getContext());
            CookieManager cookieManager = CookieManager.getInstance();
            cookieManager.removeAllCookie();
            CookieSyncManager.getInstance().sync();
            mWebView.setWebChromeClient(null);
            mWebView.setWebViewClient(null);
            mWebView.getSettings().setJavaScriptEnabled(false);
            mWebView.clearCache(true);
        } catch (Exception e) {
        }
    }
}