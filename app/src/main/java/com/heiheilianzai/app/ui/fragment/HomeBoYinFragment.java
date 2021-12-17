package com.heiheilianzai.app.ui.fragment;

import android.content.Intent;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.ReadNovelService;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.BoyinPlayerEvent;
import com.heiheilianzai.app.model.event.LoginBoYinEvent;
import com.heiheilianzai.app.model.event.LogoutBoYinEvent;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.ui.activity.boyin.BoyinDownActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.zhengjt.floatingball.FloatBall;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import androidx.annotation.Nullable;

import butterknife.BindView;

import static com.heiheilianzai.app.constant.ReaderConfig.DOWN;
import static com.heiheilianzai.app.constant.ReaderConfig.READHISTORY;

/**
 * 首页波音页面
 */
public class HomeBoYinFragment extends BaseButterKnifeFragment {
    public static final String BUNDLE_URL_KAY = "url";
    boolean isLoadUrl = false;
    @BindView(R.id.home_boyin_webview)
    public WebView mWebView;
    @BindView(R.id.home_boyin_layout)
    RelativeLayout home_boyin_layout;
    private String mBoyinUrl;

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
        mBoyinUrl = getArguments().getString(BUNDLE_URL_KAY);
        WebSettings settings = mWebView.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setJavaScriptEnabled(true);
        settings.setBlockNetworkImage(false);//解决图片不显示
        settings.setMediaPlaybackRequiresUserGesture(false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        //setFloatBall(home_boyin_layout);
        mWebView.setDownloadListener(new MyWebViewDownLoadListener());  //在前面加入下载监听器
        mWebView.addJavascriptInterface(new JavascriptObject() {
            @JavascriptInterface
            @Override
            public void thirdpartyLogin() {//h5跳转原生登录
                if (getActivity() != null) {
                    if (Utils.isLogin(getActivity())) {
                        MyToash.ToashSuccess(getActivity(), getString(R.string.boyin_login_warn));
                    }
                    MainHttpTask.getInstance().Gotologin(activity);
                }
            }

            @JavascriptInterface
            @Override
            public String getToken() {//h5主动获取客户端保存的波音登录数据
                String token = AppPrefs.getSharedString(getActivity(), ReaderConfig.BOYIN_LOGIN_TOKEN, "");
                return token;
            }

            @JavascriptInterface
            @Override
            public void pay(int fromspot) {//h5跳转原生支付
                if (activity != null) {
                    Intent intent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_vip_dialog), fromspot);
                    intent.putExtra("isvip", true);
                    startActivity(intent);
                }
            }

            @JavascriptInterface
            @Override
            public void callExplorer(String url) {//h5跳转外部浏览器
                if (mWebView != null) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    getContext().startActivity(browserIntent);
                }
            }

            @JavascriptInterface
            @Override
            public void callWebviewExplorer(String url) {//内部浏览器
                activity.startActivity(new Intent(activity, AboutActivity.class).putExtra("url", url));
            }

            @JavascriptInterface
            @Override
            public void showBookInfoGuide() {//h5第一次进入有声小说播放详情，显示新手引导悬浮层。
                //showFragmentYouShengGuide();
            }

            @JavascriptInterface
            @Override
            public String getSaServerAppId() {//获取神策埋点AppId
                return BuildConfig.sa_server_app_id;
            }

            @JavascriptInterface
            @Override
            public void getDownBoyinChapter(int nid) {
                if (activity != null) {
                    Intent intent = new Intent(activity, BoyinDownActivity.class);
                    intent.putExtra("nid", nid);
                    startActivity(intent);
                }
            }

            @Override
            public void isPlaying(boolean isPlay) {//保留方法 获取有声页面播放状态
                if (ReadNovelService.SERVICE_IS_LIVE) {
                    Intent intentService = new Intent(activity, ReadNovelService.class);
                    activity.stopService(intentService);
                }
            }

            @JavascriptInterface
            @Override
            public void toNativeMineAudioHistory() {
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", READHISTORY).putExtra("title", LanguageUtil.getString(activity, R.string.noverfragment_yuedulishi)));
            }

            @JavascriptInterface
            @Override
            public void toNativeMineAudioDownload() {
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", DOWN).putExtra("title", LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger)));
            }

            @Override
            public String getH5Cache(String key) {
                return AppPrefs.getSharedString(activity, key);
            }

            @Override
            public void setH5Cache(String key, String value) {
                AppPrefs.putSharedString(activity, key, value);
            }
        }, "android");
        mWebView.loadUrl(mBoyinUrl);
        isLoadUrl = true;
    }

    interface JavascriptObject {
        @JavascriptInterface
        void thirdpartyLogin();

        @JavascriptInterface
        String getToken();

        @JavascriptInterface
        void pay(int fromspot);

        @JavascriptInterface
        void callExplorer(String url);

        @JavascriptInterface
        void callWebviewExplorer(String url);

        @JavascriptInterface
        void showBookInfoGuide();

        @JavascriptInterface
        String getSaServerAppId();

        @JavascriptInterface
        void getDownBoyinChapter(int nid);

        @JavascriptInterface
        void isPlaying(boolean isPlay);

        @JavascriptInterface
        void toNativeMineAudioHistory();

        @JavascriptInterface
        void toNativeMineAudioDownload();

        @JavascriptInterface
        String getH5Cache(String key);

        @JavascriptInterface
        void setH5Cache(String key, String value);
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
    public void loginBoYin(LoginBoYinEvent event) {
        String json = event.getLoginJson();
        if (mWebView != null && !StringUtils.isEmpty(json) && isLoadUrl) {
            mWebView.loadUrl("javascript:thirdpartyLoginResponse('" + json + "')");
            mWebView.reload();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void logoutBoYin(LogoutBoYinEvent event) {
        if (mWebView != null) {
            mWebView.loadUrl("javascript:thirdpartyLogout()");
            mWebView.reload();
        }
    }

    /**
     * 跳转波音交互
     *
     * @param skipToBoYinEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void bannerAdChangeBoyin(SkipToBoYinEvent skipToBoYinEvent) {
        if (mWebView != null) {
            String js = "";
            if (!StringUtils.isEmpty(skipToBoYinEvent.getContent())) {//首页banner跳转波音
                js = skipToBoYinEvent.getContent();
            } else if (skipToBoYinEvent.getPhonicSkipInfo() != null) {
                SkipToBoYinEvent.PhonicSkipInfo phonicSkipInfo = skipToBoYinEvent.getPhonicSkipInfo();
                js = "'" + phonicSkipInfo.getNcid() + "','" + phonicSkipInfo.getNid() + "','" + phonicSkipInfo.getAcnme() + "','" + phonicSkipInfo.getCurrent_time() + "'";
            }
            if (!StringUtils.isEmpty(js)) {
                mWebView.loadUrl("javascript:toPlayDetail(" + js + ")");
            }
        }
    }

    /**
     * player与H5交互
     *
     * @param boyinPlayerEvent
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void boyinPlayerBoyin(BoyinPlayerEvent boyinPlayerEvent) {
        if (mWebView != null) {
            if (boyinPlayerEvent.isIsplay()) {
                mWebView.loadUrl("javascript:playPause()");
            } else {
                mWebView.loadUrl("javascript:playRun()");
            }
        }
    }

    /**
     * 首页选中波音回调（避免之后有新需求保留了该方法）
     */
    public void onMyResume() {
    }

    /**
     * 首页离开波音回调（避免之后有新需求保留了该方法）
     */
    public void onMyPause() {
    }

    /**
     * 可拖动悬浮刷新按钮
     */
    void setFloatBall(ViewGroup rootView) {
        FloatBall floatBall = new FloatBall.Builder(activity, rootView)
                .setBottomMargin(ImageUtil.dp2px(activity, 70))//悬浮球初始位置BottomMargin
                .setRightMargin(0)//悬浮球初始位置RightMargin
                .setHeight(ImageUtil.dp2px(activity, 45))//悬浮球高度
                .setWidth(ImageUtil.dp2px(activity, 45))//悬浮球宽度
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

    /**
     * 波音小说详情页引导
     */
    private void showFragmentYouShengGuide() {
        if (activity != null) {
            NewbieGuide.with(activity)
                    .setLabel("guide1")
                    .setShowCounts(3)//控制次数
                    .addGuidePage(GuidePage.newInstance().addHighLight(new RectF(
                            ImageUtil.dp2px(activity, 8), ImageUtil.dp2px(activity, 205),
                            ImageUtil.dp2px(activity, 160),
                            ImageUtil.dp2px(activity, 275)))
                            .setLayoutRes(R.layout.view_guide_home_yousheng_three, R.id.next)
                            .setEverywhereCancelable(false)).show();
        }
    }
}