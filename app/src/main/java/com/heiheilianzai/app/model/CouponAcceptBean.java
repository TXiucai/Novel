package com.heiheilianzai.app.model;

import java.util.List;

public class CouponAcceptBean {

    /**
     * total_page : 1
     * current_page : 1
     * page_size : 10
     * total_count : 2
     * list : [{"article":"签到获取","gold_cost":"+","silver_cost":"+20","date":"05月07日 15:36","detail_type":1},{"article":"任务获取","gold_cost":"+","silver_cost":"+60","date":"05月07日 15:35","detail_type":1}]
     */

    private int total_page;
    private int current_page;
    private int page_size;
    private int total_count;
    /**
     * article : 签到获取
     * gold_cost : +
     * silver_cost : +20
     * date : 05月07日 15:36
     * detail_type : 1
     */

    private List<ListBean> list;

    public int getTotal_page() {
        return total_page;
    }

    public void setTotal_page(int total_page) {
        this.total_page = total_page;
    }

    public int getCurrent_page() {
        return current_page;
    }

    public void setCurrent_page(int current_page) {
        this.current_page = current_page;
    }

    public int getPage_size() {
        return page_size;
    }

    public void setPage_size(int page_size) {
        this.page_size = page_size;
    }

    public int getTotal_count() {
        return total_count;
    }

    public void setTotal_count(int total_count) {
        this.total_count = total_count;
    }

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String article;
        private String gold_cost;
        private String silver_cost;
        private String date;
        private int detail_type;

        public String getArticle() {
            return article;
        }

        public void setArticle(String article) {
            this.article = article;
        }

        public String getGold_cost() {
            return gold_cost;
        }

        public void setGold_cost(String gold_cost) {
            this.gold_cost = gold_cost;
        }

        public String getSilver_cost() {
            return silver_cost;
        }

        public void setSilver_cost(String silver_cost) {
            this.silver_cost = silver_cost;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public int getDetail_type() {
            return detail_type;
        }

        public void setDetail_type(int detail_type) {
            this.detail_type = detail_type;
        }
    }
}
