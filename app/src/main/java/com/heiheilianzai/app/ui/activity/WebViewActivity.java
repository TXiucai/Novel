package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.webview.ReaderWebView;

import butterknife.BindView;
import butterknife.OnClick;

/**
 *
 */
public class WebViewActivity extends BaseButterKnifeActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_about;
    }

    @BindView(R.id.software_webview)
    public ReaderWebView mWebView;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;

    @OnClick(value = {R.id.titlebar_back
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }


    public void initView() {
        Intent intent = getIntent();
        String url = intent.getStringExtra("url");
        String title = intent.getStringExtra("title");
        if (title != null) {
            titlebar_text.setText(title);
        }
        Log.e("====","web  url:"+url);
        System.out.println("======web  url:"+url);
        if (url == null) {
            return;
        }
        String advert_id = intent.getStringExtra("advert_id");
        if (advert_id != null) {
            onclicAD(advert_id);
        }
        int style = intent.getIntExtra("ad_url_type", 1);
        if (style == 2) {
            Intent intent2 = new Intent();
            intent2.setAction(Intent.ACTION_VIEW);
            Uri content_uri_browsers = Uri.parse(url);
            intent2.setData(content_uri_browsers);
            this.startActivity(intent2);
            finish();
        }

        WebSettings settings = mWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//?????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        MyToash.Log(url);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //??????????????????????????????
        mWebView.setWebViewClient(new DemoWebViewClient());
        mWebView.loadUrl(url);

    }

    class DemoWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            try {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } catch (Exception e) {
            }
            return true;
        }
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        //????????????????????????
        public void onDownloadStart(String url,
                                    String userAgent, String contentDisposition,
                                    String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    //??????webview ??????
    private void onclicAD(String advert_id) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("advert_id", advert_id + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + "/advert/click", json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ReaderWebView.mDialog != null && ReaderWebView.mDialog.isShowing()) {
            ReaderWebView.mDialog.dismiss();
        }
    }
}
