package com.heiheilianzai.app.model;

import java.util.List;

public class MarqueeVipBean {

    /**
     * goods_id : 44
     * uid : 100
     * mobile : 1816****686
     * good_title : 的广告
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String goods_id;
        private String uid;
        private String mobile;
        private String good_title;

        public String getGoods_id() {
            return goods_id;
        }

        public void setGoods_id(String goods_id) {
            this.goods_id = goods_id;
        }

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getGood_title() {
            return good_title;
        }

        public void setGood_title(String good_title) {
            this.good_title = good_title;
        }
    }
}
