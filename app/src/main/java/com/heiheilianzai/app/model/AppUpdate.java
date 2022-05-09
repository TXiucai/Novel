package com.heiheilianzai.app.model;

import java.util.List;

/**
 * 系统参数
 */
public class AppUpdate {
    public int update;
    public String msg;
    public String url;
    public SettingBean setting;
    public String magic_state;
    public int ad_switch;
    public Unit_tag unit_tag;
    public String share_icon;
    public String share_read_url;
    public int cipher_api;
    public int cipher_img;
    public int hot_start_time;
    public int daily_max_start_page;
    public int boyin_switch;//波音开关
    public int pay_switch;
    public String boyin_h5;
    public Version update_version;
    public String website_android;
    public AdPositionBookBean ad_position_book;
    public AdPositionComicBean ad_position_comic;
    public AdPositionOtherBean ad_position_other;
    public String book_text_api;
    public String vtapi_license_key;
    public String tts_open_switch;//2开启 1关闭
    public String guide_text;
    public int display_second;
    public String book_read_tip_title;
    public String pay_lunxun_domain_switch;
    public PayLunxunDomainBean pay_lunxun_domain;

    public String getGuide_text() {
        return guide_text;
    }

    public void setGuide_text(String guide_text) {
        this.guide_text = guide_text;
    }

    public int getDisplay_second() {
        return display_second;
    }

    public void setDisplay_second(int display_second) {
        this.display_second = display_second;
    }

    public String getVtapi_license_key() {
        return vtapi_license_key;
    }

    public void setVtapi_license_key(String vtapi_license_key) {
        this.vtapi_license_key = vtapi_license_key;
    }

    public String getTts_open_switch() {
        return tts_open_switch;
    }

    public void setTts_open_switch(String tts_open_switch) {
        this.tts_open_switch = tts_open_switch;
    }

    public String getBook_text_api() {
        return book_text_api;
    }

    public void setBook_text_api(String book_text_api) {
        this.book_text_api = book_text_api;
    }

    public AdPositionBookBean getAd_position_book() {
        return ad_position_book;
    }

    public void setAd_position_book(AdPositionBookBean ad_position_book) {
        this.ad_position_book = ad_position_book;
    }

    public AdPositionComicBean getAd_position_comic() {
        return ad_position_comic;
    }

    public void setAd_position_comic(AdPositionComicBean ad_position_comic) {
        this.ad_position_comic = ad_position_comic;
    }

    public AdPositionOtherBean getAd_position_other() {
        return ad_position_other;
    }

    public void setAd_position_other(AdPositionOtherBean ad_position_other) {
        this.ad_position_other = ad_position_other;
    }

    public String website_android_title;

    public String getWebsite_android_title() {
        return website_android_title;
    }

    public void setWebsite_android_title(String website_android_title) {
        this.website_android_title = website_android_title;
    }

    public String getWebsite_android() {
        return website_android;
    }

    public void setWebsite_android(String website_android) {
        this.website_android = website_android;
    }

    public int getDaily_max_start_page() {
        return daily_max_start_page;
    }

    public void setDaily_max_start_page(int daily_max_start_page) {
        this.daily_max_start_page = daily_max_start_page;
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

    public int getBoyin_switch() {
        return boyin_switch;
    }

    public String getBoyin_h5() {
        return boyin_h5;
    }

    public void setBoyin_h5(String boyin_h5) {
        this.boyin_h5 = boyin_h5;
    }

    public void setBoyin_switch(int boyin_switch) {
        this.boyin_switch = boyin_switch;
    }

    public class PayLunxunDomainBean{
        private List<String> list;

        public List<String> getList() {
            return list;
        }

        public void setList(List<String> list) {
            this.list = list;
        }
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

    public class Version {
        public String apk;
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

    public static class AdPositionBookBean {
        /**
         * id : 1
         * type : 1
         * ad_type : 1
         * position : 1
         * product : 1
         * sdk_switch : 2
         */

        private List<AppUpdate.ListBean> list;

        public List<AppUpdate.ListBean> getList() {
            return list;
        }

        public void setList(List<AppUpdate.ListBean> list) {
            this.list = list;
        }

    }

    public static class AdPositionComicBean {
        /**
         * id : 25
         * type : 2
         * ad_type : 1
         * position : 1
         * product : 1
         * sdk_switch : 2
         */

        private List<AppUpdate.ListBean> list;

        public List<AppUpdate.ListBean> getList() {
            return list;
        }

        public void setList(List<AppUpdate.ListBean> list) {
            this.list = list;
        }


    }

    public static class ListBean {
        //1为关闭中   2为开启了加载第三方
        private String id;
        private String type;
        private String ad_type;
        private String position;
        private String product;
        private String sdk_switch;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getAd_type() {
            return ad_type;
        }

        public void setAd_type(String ad_type) {
            this.ad_type = ad_type;
        }

        public String getPosition() {
            return position;
        }

        public void setPosition(String position) {
            this.position = position;
        }

        public String getProduct() {
            return product;
        }

        public void setProduct(String product) {
            this.product = product;
        }

        public String getSdk_switch() {
            return sdk_switch;
        }

        public void setSdk_switch(String sdk_switch) {
            this.sdk_switch = sdk_switch;
        }
    }

    public static class AdPositionOtherBean {
        /**
         * icon_index : 2
         * app_index : 2
         * start_page_index : 2
         * alert_index : 2
         * book_banner_index : 2
         * comic_banner_index : 2
         */

        private AdPositionOtherBean.ListBean list;

        public AdPositionOtherBean.ListBean getList() {
            return list;
        }

        public void setList(AdPositionOtherBean.ListBean list) {
            this.list = list;
        }

        public static class ListBean {
            //1为关闭中   2为开启了加载第三方
            private int icon_index;
            private int app_index;
            private int start_page_index;
            private int alert_index;
            private int book_banner_index;
            private int comic_banner_index;

            public int getIcon_index() {
                return icon_index;
            }

            public void setIcon_index(int icon_index) {
                this.icon_index = icon_index;
            }

            public int getApp_index() {
                return app_index;
            }

            public void setApp_index(int app_index) {
                this.app_index = app_index;
            }

            public int getStart_page_index() {
                return start_page_index;
            }

            public void setStart_page_index(int start_page_index) {
                this.start_page_index = start_page_index;
            }

            public int getAlert_index() {
                return alert_index;
            }

            public void setAlert_index(int alert_index) {
                this.alert_index = alert_index;
            }

            public int getBook_banner_index() {
                return book_banner_index;
            }

            public void setBook_banner_index(int book_banner_index) {
                this.book_banner_index = book_banner_index;
            }

            public int getComic_banner_index() {
                return comic_banner_index;
            }

            public void setComic_banner_index(int comic_banner_index) {
                this.comic_banner_index = comic_banner_index;
            }
        }
    }
}




