package com.heiheilianzai.app.model;

import java.util.List;

public class CouponUseBean {

    /**
     * total_page : 1
     * current_page : 1
     */

    private PageInfoBean page_info;
    /**
     * uid : 466036
     * comic_id : 1673
     * chapter_id : 73825
     * silver_cost : 4
     * pay_time : 1620380605
     * data_type_name : 漫画
     * note : 解锁 第11话
     * date_name : 05月 07 17:43
     * title_name : [BL]屁屁医生的目标是我的后庭!
     */

    private List<ListBean> list;

    public PageInfoBean getPage_info() {
        return page_info;
    }

    public void setPage_info(PageInfoBean page_info) {
        this.page_info = page_info;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class PageInfoBean {
        private int total_page;
        private String current_page;

        public int getTotal_page() {
            return total_page;
        }

        public void setTotal_page(int total_page) {
            this.total_page = total_page;
        }

        public String getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(String current_page) {
            this.current_page = current_page;
        }
    }

    public static class ListBean {
        private String uid;
        private String comic_id;
        private String chapter_id;
        private String silver_cost;
        private String pay_time;
        private String data_type_name;
        private String note;
        private String date_name;
        private String title_name;

        public String getUid() {
            return uid;
        }

        public void setUid(String uid) {
            this.uid = uid;
        }

        public String getComic_id() {
            return comic_id;
        }

        public void setComic_id(String comic_id) {
            this.comic_id = comic_id;
        }

        public String getChapter_id() {
            return chapter_id;
        }

        public void setChapter_id(String chapter_id) {
            this.chapter_id = chapter_id;
        }

        public String getSilver_cost() {
            return silver_cost;
        }

        public void setSilver_cost(String silver_cost) {
            this.silver_cost = silver_cost;
        }

        public String getPay_time() {
            return pay_time;
        }

        public void setPay_time(String pay_time) {
            this.pay_time = pay_time;
        }

        public String getData_type_name() {
            return data_type_name;
        }

        public void setData_type_name(String data_type_name) {
            this.data_type_name = data_type_name;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }

        public String getDate_name() {
            return date_name;
        }

        public void setDate_name(String date_name) {
            this.date_name = date_name;
        }

        public String getTitle_name() {
            return title_name;
        }

        public void setTitle_name(String title_name) {
            this.title_name = title_name;
        }
    }
}
