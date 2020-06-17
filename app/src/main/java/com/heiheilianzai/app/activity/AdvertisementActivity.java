package com.heiheilianzai.app.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.AppUpdate;
import com.heiheilianzai.app.comic.activity.ComicInfoActivity;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.UpdateApp;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 热启动广告
 */
public class AdvertisementActivity extends Activity {
    @BindView(R2.id.activity_splash_im)
    public ImageView activity_splash_im;
    @BindView(R2.id.activity_home_viewpager_sex_next)
    public TextView activity_home_viewpager_sex_next;
    public static Activity activity;
    AppUpdate.Startpage startpage;
    UpdateApp updateApp;
    boolean skip = false;
    int time = 5;
    boolean into;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_advertisement);
        ButterKnife.bind(this);
        activity = AdvertisementActivity.this;
        updateApp = new UpdateApp(AdvertisementActivity.this);
        next();
        activity_home_viewpager_sex_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getString(R.string.splashactivity_skip).equals(activity_home_viewpager_sex_next.getText().toString())) {
                    skip = true;
                    handler.sendEmptyMessageDelayed(0, 0);
                }
            }
        });
        if (ReaderApplication.getDailyStartPageMax() > 0) {//后台设置了每天最多广告开启广告次数
            ReaderApplication.putDailyStartPage();//记录每天广告开启次数
        }
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                if (!into) {
                    into = true;
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                if (time == 0) {
                    activity_home_viewpager_sex_next.setText(LanguageUtil.getString(getApplicationContext(), R.string.splashactivity_skip));
                } else {
                    activity_home_viewpager_sex_next.setText(LanguageUtil.getString(getApplicationContext(), R.string.splashactivity_surplus) + " " + (time--) + " ");
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
                        startpage = dataBean.start_page;
                        ReaderApplication.putDailyStartPageMax(dataBean.daily_max_start_page);
                        if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                            setAdImageView(activity_splash_im, startpage, activity, new OnAdImageListener() {
                                @Override
                                public void onAnimationEnd() {
                                    activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                                    handler.sendEmptyMessageDelayed(1, 0);
                                }

                                @Override
                                public void onClick() {
                                    skip = false;
                                    if (!skip) {
                                        handler.removeMessages(1);
                                        handler.removeMessages(0);
                                        adSkip(startpage, activity);
                                    }
                                }
                            });
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
        }, true);
    }

    /**
     * 广告图片加载
     *
     * @param imageView
     * @param startpage
     * @param activity
     * @param listener
     */
    public static void setAdImageView(ImageView imageView, AppUpdate.Startpage startpage, Activity activity, OnAdImageListener listener) {
        imageView.setAlpha(0f);
        imageView.setVisibility(View.VISIBLE);
        MyPicasso.GlideImageNoSize(activity, startpage.image, imageView);
        imageView.animate()
                .alpha(1f)
                .setDuration(500)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        listener.onAnimationEnd();
                    }
                });
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
    public static void adSkip(AppUpdate.Startpage startpage, Context context) {
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
                    .putExtra("style", "4")
            );
        }
    }

    public static void setReaderConfig() {

    }

    public interface OnAdImageListener {
        void onAnimationEnd();

        void onClick();
    }
}
