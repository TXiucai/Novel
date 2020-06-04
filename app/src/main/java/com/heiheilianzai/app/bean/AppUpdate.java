package com.heiheilianzai.app.bean;

/**
 * apk更新
 */
public class AppUpdate {
    public int update;
    public String msg;
    public String url;
    public SettingBean setting;
    public Startpage start_page;
    public String magic_state;
    public int ad_switch;
    public Unit_tag unit_tag;
    public String share_icon;
    public String share_read_url;
    public int cipher_api;
    public int cipher_img;
    public int  hot_start_time;

    public Startpage getStart_page() {
        return start_page;
    }

    public void setStart_page(Startpage start_page) {
        this.start_page = start_page;
    }

    public String getMagic_state() {
        return magic_state;
    }

    public void setMagic_state(String magic_state) {
        this.magic_state = magic_state;
    }

    public int getAd_switch() {
        return ad_switch;
    }

    public void setAd_switch(int ad_switch) {
        this.ad_switch = ad_switch;
    }

    public String getShare_icon() {
        return share_icon;
    }

    public void setShare_icon(String share_icon) {
        this.share_icon = share_icon;
    }

    public String getShare_read_url() {
        return share_read_url;
    }

    public void setShare_read_url(String share_read_url) {
        this.share_read_url = share_read_url;
    }

    public int getUpdate() {
        return update;
    }

    public void setUpdate(int update) {
        this.update = update;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public SettingBean getSetting() {
        return setting;
    }

    public void setSetting(SettingBean setting) {
        this.setting = setting;
    }

    public int getCipher_api() {
        return cipher_api;
    }

    public void setCipher_api(int cipher_api) {
        this.cipher_api = cipher_api;
    }

    public int getCipher_img() {
        return cipher_img;
    }

    public void setCipher_img(int cipher_img) {
        this.cipher_img = cipher_img;
    }

    public Unit_tag getUnit_tag() {
        return unit_tag;
    }

    public void setUnit_tag(Unit_tag unit_tag) {
        this.unit_tag = unit_tag;
    }

    public int getHot_start_time() {
        return hot_start_time;
    }

    public void setHot_start_time(int hot_start_time) {
        this.hot_start_time = hot_start_time;
    }

    public class Unit_tag {
        private String currencyUnit;
        private String subUnit;

        public String getCurrencyUnit() {
            return currencyUnit;
        }

        public void setCurrencyUnit(String currencyUnit) {
            this.currencyUnit = currencyUnit;
        }

        public String getSubUnit() {
            return subUnit;
        }

        public void setSubUnit(String subUnit) {
            this.subUnit = subUnit;
        }
    }

    public class Startpage {
        public String image;
        public String content;
        public int skip_type;
        public String title;
    }

    public class SettingBean {
        /**
         * "money_unit": "金币" //资产单位：例：书豆、阅读币等
         * "is_ad": 0,     // 去掉广告(1-去掉，0-未去掉)
         * "share_icon",   // 分享阅读icon（有值，就显示，没有不显示）
         * <p>
         * money_unit : 金币
         */
        private String money_unit;

        public String getMoney_unit() {
            return money_unit;
        }

        public void setMoney_unit(String money_unit) {
            this.money_unit = money_unit;
        }
    }
}




