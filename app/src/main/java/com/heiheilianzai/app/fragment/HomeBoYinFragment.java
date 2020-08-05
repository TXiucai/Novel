package com.heiheilianzai.app.fragment;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.activity.LoginActivity;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.LoginBoYinEvent;
import com.heiheilianzai.app.eventbus.LogoutBoYinEvent;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.zhengjt.floatingball.FloatBall;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;

/**
 * 首页波音页面
 */
public class HomeBoYinFragment extends BaseButterKnifeFragment {
    public static final String BUNDLE_URL_KAY = "url";
    public static final int LOGIN_REQUESTCODE = 0x000001f;
    public static final String BOYIN_TOKEN_RESULT_KAY = "boyin_token";
    boolean isLoadUrl = false;
    @BindView(R2.id.home_boyin_webview)
    public WebView mWebView;
    @BindView(R2.id.home_boyin_layout)
    RelativeLayout home_boyin_layout;
    //获取AudioManager
    AudioManager audioManager;
    boolean isPause=false;

    @Override
    public int initContentView() {
        return R.layout.fragment_home_boyin;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initView() {
        String url = getArguments().getString(BUNDLE_URL_KAY);
        WebSettings settings = mWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//解决图片不显示
        settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        setFloatBall(home_boyin_layout);
        audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //在前面加入下载监听器
        mWebView.addJavascriptInterface(new JavascriptObject() {
            @JavascriptInterface
            @Override
            public void thirdpartyLogin() {
                if (getActivity() != null) {
                    if (Utils.isLogin(getActivity())) {
                        MyToash.ToashSuccess(getActivity(), getString(R.string.boyin_login_warn));
                    }
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    intent.putExtra(LoginActivity.BOYIN_LOGIN_KAY, true);
                    getActivity().startActivityForResult(intent, LOGIN_REQUESTCODE);
                }
            }

            @JavascriptInterface
            @Override
            public String getToken() {
                String token = AppPrefs.getSharedString(getActivity(), ReaderConfig.BOYIN_LOGIN_TOKEN, "");
                return token;
            }

            @JavascriptInterface
            @Override
            public void pay() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), AcquireBaoyueActivity.class);
                intent.putExtra("isvip", true);
                startActivity(intent);
            }

            @JavascriptInterface
            @Override
            public void callExplorer(String url) {
                if (mWebView != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(browserIntent);
                }
            }
        }, "android");
        mWebView.setWebViewClient(new DemoWebViewClient());
        mWebView.loadUrl(url);
        isLoadUrl = true;
    }

    interface JavascriptObject {
        @JavascriptInterface
        void thirdpartyLogin();

        @JavascriptInterface
        String getToken();

        @JavascriptInterface
        void pay();

        @JavascriptInterface
        void callExplorer(String url);
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
            try {
                Uri uri = Uri.parse(url);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(LoginBoYinEvent event) {
        String json = event.getLoginJson();
        if (mWebView != null && !StringUtils.isEmpty(json) && isLoadUrl) {
            mWebView.loadUrl("javascript:thirdpartyLoginResponse('" + json + "')");
            if(!isPause){
                mWebView.reload();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(LogoutBoYinEvent event) {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:thirdpartyLogout()");
            if(!isPause){
                mWebView.reload();
            }
        }
    }

    class DemoWebViewClient extends WebViewClient {//跳转外部浏览器

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            getContext().startActivity(browserIntent);
            return true;
        }
    }

    public void onMyResume() {
        isPause=false;
        audioManager.abandonAudioFocus(new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
            }
        });
        mWebView.reload();
    }

    public void onMyPause() {
        isPause=true;
        audioManager.requestAudioFocus(
                new AudioManager.OnAudioFocusChangeListener() {
                    @Override
                    public void onAudioFocusChange(int focusChange) {
                    }
                }, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
    }

    void setFloatBall(ViewGroup rootView) {
        FloatBall floatBall = new FloatBall.Builder(activity, rootView)
                .setBottomMargin(140)//悬浮球初始位置BottomMargin
                .setRightMargin(0)//悬浮球初始位置RightMargin
                .setHeight(80)//悬浮球高度
                .setWidth(80)//悬浮球宽度
                .setRes(R.mipmap.comic_refresh)//图片资源
                .setDuration(500)//靠边动画时间
                .setOnClickListener(new View.OnClickListener() {//悬浮球点击事件
                    @Override
                    public void onClick(View v) {
                        if (mWebView != null) {
                            mWebView.reload();
                        }
                    }
                })
                .build();
    }
}
