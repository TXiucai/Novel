package com.heiheilianzai.app.model;

public class BaseAd {
    public String advert_id;//
    public int ad_type;//": 1,   // 广告类型
    public String ad_title;//": "测试一下25",  // 标题
    public String ad_image; // 广告图
    public String ad_skip_url;//skip_url":"http://www.baidu.com", // 跳转地址
    public int ad_url_type;//'//'":1   // 跳转地址类型（1-内部跳转，2-外部跳转）
    public int advert_interval;//小说底部广告每间隔多少页显示
    public String ad_subtitle;

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





}
