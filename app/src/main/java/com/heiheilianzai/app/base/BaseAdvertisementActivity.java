package com.heiheilianzai.app.base;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.Startpage;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyActivityManager;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.UpdateApp;
import com.mobi.xad.XAdManager;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import androidx.fragment.app.FragmentActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 开屏广告 Base类 主要获取开屏广告数据 及逻辑处理
 */
public abstract class BaseAdvertisementActivity extends FragmentActivity {
    @BindView(R.id.activity_splash_im)
    public ImageView activity_splash_im;
    @BindView(R.id.activity_home_viewpager_sex_next)
    public TextView activity_home_viewpager_sex_next;
    public static Activity activity;
    protected Startpage startpage;
    protected UpdateApp updateApp;
    protected int time = 5;
    protected boolean into;
    protected boolean paused = false;

    @SuppressLint("HandlerLeak")
    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                setMessage();
            } else {
                if (time == 0) {
                    activity_home_viewpager_sex_next.setText(LanguageUtil.getString(getApplicationContext(), R.string.splashactivity_skip));
                } else {
                    if (!paused) {
                        activity_home_viewpager_sex_next.setText(LanguageUtil.getString(getApplicationContext(), R.string.splashactivity_surplus) + " " + (time--) + " ");
                        handler.sendEmptyMessageDelayed(1, 1000);
                    }
                }
                if (activity_home_viewpager_sex_next.getVisibility() != View.VISIBLE) {
                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(initContentView());
        ButterKnife.bind(this);
        activity = this;
        updateApp = new UpdateApp(this);
        onCreateView();
        if (activity_home_viewpager_sex_next != null) {
            activity_home_viewpager_sex_next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (getString(R.string.splashactivity_skip).equals(activity_home_viewpager_sex_next.getText().toString())) {
                        handler.sendEmptyMessageDelayed(0, 0);
                    }
                }
            });
        }
        initData();
    }

    /**
     * 广告图片加载
     *
     * @param imageView
     * @param startpage
     * @param activity
     * @param listener
     */
    public static void setAdImageView(ImageView imageView, Startpage startpage, Activity activity, OnAdImageListener listener) {
        imageView.setVisibility(View.VISIBLE);
        MyPicasso.intoAdImage(imageView, startpage, activity, listener);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClick();
            }
        });
    }

    /**
     * 广告根据参数进行跳转
     *
     * @param startpage
     * @param context
     */
    public static void adSkip(Startpage startpage, Context context) {
        if (startpage.skip_type == 1) {// skip_type 1 书籍 2 外部跳转链接 3 漫画 4 浏览器打开链接 5不操作
            context.startActivity(new Intent(context, BookInfoActivity.class).putExtra("book_id", startpage.content));
        } else if (startpage.skip_type == 2) {
            context.startActivity(new Intent(context, AboutActivity.class)
                    .putExtra("title", startpage.title)
                    .putExtra("url", startpage.content)
                    .putExtra("flag", "splash"));
        } else if (startpage.skip_type == 3) {
            context.startActivity(new Intent(context, ComicInfoActivity.class).putExtra("comic_id", startpage.content));
        } else if (startpage.skip_type == 5) {
        } else {
            context.startActivity(new Intent(context, AboutActivity.class)
                    .putExtra("url", startpage.content)
                    .putExtra("flag", "splash")
                    .putExtra("style", "4"));
        }
    }

    /**
     * 记录开屏次数
     */
    public void startPage() {
        if (App.getDailyStartPageMax() > 0) {//后台设置了每天最多广告开启广告次数
            App.putDailyStartPage();//记录每天广告开启次数
        }
    }

    /**
     * 预加载一次广告图片 将图片缓存下载到本地。
     * 加载广告时可以先用本地图片初始化。
     *
     * @param startpage
     */
    public void preloadAdvertisingImg(Startpage startpage) {
        if (startpage != null && !StringUtils.isEmpty(startpage.image)) {
            Activity activity = MyActivityManager.getInstance().getCurrentActivity();
            if (activity != null) {
                MyPicasso.downloadIMG(startpage.image, activity, new MyPicasso.OnDwnloadImg() {
                    @Override
                    public void onFile(String nameFile) {
                        ShareUitls.putString(App.getContext(), PrefConst.ADVERTISING_IMG_KAY, nameFile);
                    }
                });
            }
        }
    }

    /**
     * 获取开屏广告加载内容，并将数据缓存。图片下载到本地。
     */
    public void getOpenScreen() {
        if (ReaderConfig.OTHER_SDK_AD.getStart_page_index() == 2) {
            sdkSplashAd();
        } else {
            localSplashAd();
        }
    }

    private void sdkSplashAd() {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_SPLASH_DEBUG : BuildConfig.XAD_EVN_POS_SPLASH, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    Startpage startpage = new Startpage();
                    AdInfo adInfo = list.get(0);
                    startpage.setAdId(adInfo.getAdId());
                    startpage.setAdPosId(adInfo.getAdPosId());
                    startpage.setRequestId(adInfo.getRequestId());
                    startpage.setCountdown_second(String.valueOf(adInfo.getCountDown()));
                    startpage.setImage(adInfo.getMaterial().getImageUrl());
                    startpage.setContent(adInfo.getOperation().getValue());
                    int type = adInfo.getOperation().getType();
                    int skip_type = type == 1 ? 2 : 4;
                    startpage.setSkip_type(skip_type);
                    startpage.setAd_show_type(adInfo.getMaterial().getShowType());
                    ShareUitls.putString(App.getContext(), PrefConst.ADVERTISING_JSON_KAY, new Gson().toJson(startpage));
                    preloadAdvertisingImg(startpage);
                } catch (Exception e) {
                    localSplashAd();
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localSplashAd();
            }
        });
    }

    private void localSplashAd() {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mOpenScreen, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String start_pag = jsonObject.getString("start_page");
                            String book_text_api = jsonObject.getString("book_text_api");
                            if (!StringUtils.isEmpty(book_text_api)) {
                                ShareUitls.putString(App.getContext(), PrefConst.NOVEL_API, book_text_api);
                            }
                            if (!StringUtils.isEmpty(start_pag)) {
                                ShareUitls.putString(App.getContext(), PrefConst.ADVERTISING_JSON_KAY, start_pag);
                                Startpage startpage = new Gson().fromJson(start_pag, Startpage.class);
                                preloadAdvertisingImg(startpage);
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (paused) {
            if (handler != null)
                handler.removeCallbacksAndMessages(null);
        }

        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
       /* if (time != 5) {
            next();
        } else if (time == 5 && activity_home_viewpager_sex_next.getVisibility() == View.VISIBLE) {
            if (handler != null) {
                handler.sendEmptyMessageDelayed(1, 0);
            }
        }*/
        if (paused) {
            if (handler != null) {
                handler.sendEmptyMessageDelayed(1, 0);
            }
        }
        paused = false;
    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
        if (handler != null) {
            paused = true;
            handler.removeMessages(1);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
            handler = null;
        }
    }

    public interface OnAdImageListener {
        void onAnimationEnd();

        void onFailed();

        void onClick();
    }

    public abstract void setMessage();

    public abstract void next();

    public abstract void onCreateView();

    public abstract int initContentView();

    public abstract void initData();
}