package com.heiheilianzai.app.model;

import static com.heiheilianzai.app.constant.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.constant.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.constant.ReaderConfig.WANBEN;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.MyShareActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.TopNewActivity;
import com.heiheilianzai.app.ui.activity.TopYearBookActivity;
import com.heiheilianzai.app.ui.activity.TopYearComicActivity;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;

public class BaseAd extends BaseSdkAD implements Serializable {
    public String advert_id;//
    public int ad_type;//": 1,   // 广告类型
    public String ad_title;//": "测试一下25",  // 标题
    public String ad_image; // 广告图
    public String ad_skip_url;//skip_url":"http://www.baidu.com", // 跳转地址
    public int ad_url_type;//'//'":1   // 跳转地址类型（1-内部跳转，2-外部跳转 3内置应用）
    public int advert_interval;//小说底部广告每间隔多少页显示
    public String ad_subtitle;
    public String user_parame_need;
    public int display_ad_days;
    public int ad_action;//1小说作品详情页 2漫画作品详情页
    //3动画作品详情页 4首页-推荐页（小说） 5首页-完结页（小说） 6 首页-榜单页（小说） 7 首页-分类（小说）
    //8 首页-推荐页（漫画） 9 首页-完结页（漫画） 10 首页-榜单页（漫画） 11 首页-分类（漫画）
    //12 VIP充值页 13 年度榜单（小说） 14 年度榜单（漫画） 15 分享页 16 活动页面 17 波音有声 18 熊猫游戏 19 福利中心）
    public String book_id;
    public String comic_id;
    public String video_id;
    public String panda_game_link;

    public String getPanda_game_link() {
        return panda_game_link;
    }

    public void setPanda_game_link(String panda_game_link) {
        this.panda_game_link = panda_game_link;
    }

    public int getAd_action() {
        return ad_action;
    }

    public void setAd_action(int ad_action) {
        this.ad_action = ad_action;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getComic_id() {
        return comic_id;
    }

    public void setComic_id(String comic_id) {
        this.comic_id = comic_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getDisplay_ad_days() {
        return display_ad_days;
    }

    public void setDisplay_ad_days(int display_ad_days) {
        this.display_ad_days = display_ad_days;
    }

    public String getUser_parame_need() {
        return user_parame_need;
    }

    public void setUser_parame_need(String user_parame_need) {
        this.user_parame_need = user_parame_need;
    }

    public String getAd_subtitle() {
        return ad_subtitle;
    }

    public void setAd_subtitle(String ad_subtitle) {
        this.ad_subtitle = ad_subtitle;
    }

    public int getAdvert_interval() {
        return advert_interval;
    }

    public void setAdvert_interval(int advert_interval) {
        this.advert_interval = advert_interval;
    }

    public String getAd_title() {
        return ad_title;
    }

    public void setAd_title(String ad_title) {
        this.ad_title = ad_title;
    }

    public String getAd_image() {
        return ad_image;
    }

    public void setAd_image(String ad_image) {
        this.ad_image = ad_image;
    }

    public String getAd_skip_url() {
        return ad_skip_url;
    }

    public void setAd_skip_url(String ad_skip_url) {
        this.ad_skip_url = ad_skip_url;
    }

    public int getAd_url_type() {
        return ad_url_type;
    }

    public void setAd_url_type(int ad_url_type) {
        this.ad_url_type = ad_url_type;
    }

    public String getAdvert_id() {
        return advert_id;
    }

    public void setAdvert_id(String advert_id) {
        this.advert_id = advert_id;
    }

    public int getAd_type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public static void jumpADInfo(BaseAd baseAd, Activity context) {
        try {
            int user_parame_need = Integer.valueOf(baseAd.getUser_parame_need());//用户参数是否需要拼接 1为不需要   2为强制需要拼接
            Intent intent = new Intent(context, BaseOptionActivity.class);
            int ad_url_type = baseAd.getAd_url_type();
            int ad_action = baseAd.ad_action;
            String jump_url = baseAd.getAd_skip_url();
            if (Utils.isLogin(context) && user_parame_need == 2 && !jump_url.contains("&uid=")) {
                jump_url += "&uid=" + Utils.getUID(context);
            }
            if (ad_url_type == 1) {
                context.startActivity(new Intent(context, AboutActivity.class).
                        putExtra("url", jump_url));
            } else if (ad_url_type == 2) {
                context.startActivity(new Intent(context, AboutActivity.class).
                        putExtra("url", jump_url)
                        .putExtra("style", "4"));
            } else {
                switch (ad_action) {
                    case 1:
                        context.startActivity(BookInfoActivity.getMyIntent(context, LanguageUtil.getString(context, R.string.refer_page_home_ad), baseAd.book_id));
                        break;
                    case 2:
                        context.startActivity(ComicInfoActivity.getMyIntent(context, LanguageUtil.getString(context, R.string.refer_page_home_ad), baseAd.comic_id));
                        break;
                    case 3:
                        context.startActivity(CartoonInfoActivity.getMyIntent(context, LanguageUtil.getString(context, R.string.refer_page_home_ad), baseAd.video_id));
                        break;
                    case 4:
                        intent.putExtra("PRODUCT", 1);
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_xianmian));
                        context.startActivity(intent);
                        break;
                    case 8:
                        intent.putExtra("PRODUCT", 2);
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_xianmian));
                        context.startActivity(intent);
                        break;
                    case 5:
                        intent.putExtra("PRODUCT", 1);
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_wanben));
                        context.startActivity(intent);
                        break;
                    case 9:
                        intent.putExtra("PRODUCT", 2);
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_wanben));
                        context.startActivity(intent);
                        break;
                    case 6:
                        intent.putExtra("PRODUCT", 1);
                        context.startActivity(new Intent(context, TopNewActivity.class));
                        break;
                    case 10:
                        intent.putExtra("PRODUCT", 2);
                        context.startActivity(new Intent(context, TopNewActivity.class));
                        break;
                    case 7:
                        intent.putExtra("PRODUCT", 1);
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_fenlei));
                        context.startActivity(intent);
                        break;
                    case 11:
                        intent.putExtra("PRODUCT", 2);
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(context, R.string.storeFragment_fenlei));
                        context.startActivity(intent);
                        break;
                    case 12:
                        Intent myIntent = AcquireBaoyueActivity.getMyIntent(context, LanguageUtil.getString(context, R.string.refer_page_mine), 13);
                        myIntent.putExtra("isvip", Utils.isLogin(context));
                        context.startActivity(myIntent);
                        break;
                    case 13:
                        context.startActivity(new Intent(context, TopYearBookActivity.class));
                        break;
                    case 14:
                        context.startActivity(new Intent(context, TopYearComicActivity.class));
                        break;
                    case 15:
                        context.startActivity(new Intent(context, MyShareActivity.class));
                        break;
                    case 16:
                        break;
                    case 17:
                        EventBus.getDefault().post(new SkipToBoYinEvent(""));
                        break;
                    case 18:
                        if (Utils.isLogin(context)) {
                            String panda_game_link = ShareUitls.getString(context, "game_link", "");
                            if (!TextUtils.isEmpty(panda_game_link)) {
                                context.startActivity(new Intent(context, AboutActivity.class).putExtra("url", panda_game_link));
                            }
                        } else {
                            MainHttpTask.getInstance().Gotologin(context);
                        }
                        break;
                    case 19:
                        if (Utils.isLogin(context)) {
                            context.startActivity(new Intent(context, TaskCenterActivity.class));
                        } else {
                            MainHttpTask.getInstance().Gotologin(context);
                        }
                        break;
                }
            }
        } catch (Exception e) {

        }
    }
}
