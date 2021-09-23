package com.heiheilianzai.app.model;

import java.util.List;

public class HomeRecommendBean {

    /**
     * id : 18
     * title : 56546546
     * img_icon : http://webp.itrm8z3b.com/banner/4437e38ba51ae9bca454bd140dff67b7.png?c
     * jump_type : 0
     * jump_url : https://www.google.com.tw/
     * platform : 0
     * status : 0
     * recommend_type : 1
     * created_at : 1606929030
     * updated_at : 1606929030
     * redirect_type : 0
     */

    private List<RecommeListBean> recomme_list;

    public List<RecommeListBean> getRecomme_list() {
        return recomme_list;
    }

    public void setRecomme_list(List<RecommeListBean> recomme_list) {
        this.recomme_list = recomme_list;
    }

    public static class RecommeListBean {
        private String id;
        private String title;
        private String img_icon;
        private String jump_type;
        private String jump_url;
        private String platform;
        private String status;
        private String recommend_type;
        private String created_at;
        private String updated_at;
        private String redirect_type;
        private String user_parame_need;

        public String getUser_parame_need() {
            return user_parame_need;
        }

        public void setUser_parame_need(String user_parame_need) {
            this.user_parame_need = user_parame_need;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getImg_icon() {
            return img_icon;
        }

        public void setImg_icon(String img_icon) {
            this.img_icon = img_icon;
        }

        public String getJump_type() {
            return jump_type;
        }

        public void setJump_type(String jump_type) {
            this.jump_type = jump_type;
        }

        public String getJump_url() {
            return jump_url;
        }

        public void setJump_url(String jump_url) {
            this.jump_url = jump_url;
        }

        public String getPlatform() {
            return platform;
        }

        public void setPlatform(String platform) {
            this.platform = platform;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getRecommend_type() {
            return recommend_type;
        }

        public void setRecommend_type(String recommend_type) {
            this.recommend_type = recommend_type;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getRedirect_type() {
            return redirect_type;
        }

        public void setRedirect_type(String redirect_type) {
            this.redirect_type = redirect_type;
        }
    }
}
