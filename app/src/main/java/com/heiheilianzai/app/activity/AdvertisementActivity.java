package com.heiheilianzai.app.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.UpdateApp;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.config.ReaderConfig.USE_AD_FINAL;

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
        activity_home_viewpager_sex_next.setClickable(false);
        next();
        activity_home_viewpager_sex_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                skip = true;
                handler.sendEmptyMessageDelayed(0, 0);
            }
        });
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
                    activity_home_viewpager_sex_next.setClickable(true);
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
                    if (response.length() != 0) {//
                        AppUpdate dataBean = new Gson().fromJson(response, AppUpdate.class);
                        if (dataBean.getUnit_tag() != null) {
                            ReaderConfig.currencyUnit = dataBean.getUnit_tag().getCurrencyUnit();
                            ReaderConfig.subUnit = dataBean.getUnit_tag().getSubUnit();
                        }
                        if (USE_AD_FINAL) {
                            ReaderConfig.ad_switch = dataBean.ad_switch;
                            ReaderConfig.USE_AD = dataBean.ad_switch == 1;
                        }
                        startpage = dataBean.start_page;
                        if (startpage != null && startpage.image != null && startpage.image.length() != 0) {
                            activity_splash_im.setAlpha(0f);
                            activity_splash_im.setVisibility(View.VISIBLE);
                            MyPicasso.GlideImageNoSize(activity, startpage.image, activity_splash_im);
                            activity_splash_im.animate()
                                    .alpha(1f)
                                    .setDuration(500)
                                    .setListener(new AnimatorListenerAdapter() {
                                        @Override
                                        public void onAnimationEnd(Animator animation) {
                                            activity_home_viewpager_sex_next.setVisibility(View.VISIBLE);
                                            handler.sendEmptyMessageDelayed(1, 0);
                                        }
                                    });
                            activity_splash_im.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    skip = false;
                                    if (!skip) {
                                        handler.removeMessages(1);
                                        handler.removeMessages(0);
                                        if (startpage.skip_type == 1) {
                                            startActivity(new Intent(activity, BookInfoActivity.class).putExtra("book_id", startpage.content));
                                        } else if (startpage.skip_type == 2) {
                                            startActivity(new Intent(activity, AboutActivity.class)
                                                    .putExtra("title", startpage.title)
                                                    .putExtra("url", startpage.content)
                                                    .putExtra("flag", "splash"));
                                        } else if (startpage.skip_type == 3) {
                                            startActivity(new Intent(activity, ComicInfoActivity.class).putExtra("comic_id", startpage.content));
                                        } else {
                                            startActivity(new Intent(activity, AboutActivity.class)
                                                    .putExtra("url", startpage.content)
                                                    .putExtra("flag", "splash")
                                                    .putExtra("style", "4")
                                            );
                                        }
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
        });
    }
}
