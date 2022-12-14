package com.heiheilianzai.app.model;

/**
 * 开屏广告
 */
public class Startpage extends BaseSdkAD{
    public String image;
    public String content;
    public int skip_type;
    public String title;
    public String countdown_second;
    public String ad_show_type;

    public String getAd_show_type() {
        return ad_show_type;
    }

    public void setAd_show_type(String ad_show_type) {
        this.ad_show_type = ad_show_type;
    }

    public String getCountdown_second() {
        return countdown_second;
    }

    public void setCountdown_second(String countdown_second) {
        this.countdown_second = countdown_second;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSkip_type() {
        return skip_type;
    }

    public void setSkip_type(int skip_type) {
        this.skip_type = skip_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
