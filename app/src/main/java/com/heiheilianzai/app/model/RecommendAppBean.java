package com.heiheilianzai.app.model;

import java.util.List;

public class RecommendAppBean {

    /**
     * id : 1
     * app_name : wechat222
     * osType : 2
     * app_bag_name : com.app.123
     * down_link : https://www.google.com.tw
     * release_status : 1
     * app_version : 7.4.1
     * app_size : 8000
     * app_logo : http://webp.itrm8z3b.com/banner/9ea115744207578a44a7b8fe2fbeafd4.png?c
     * app_descript : wechatwechatwech
     * display_order : 3
     * created_at : 1609927448
     * updated_at : 1609929646
     */

    private List<AppListBean> app_list;

    public List<AppListBean> getApp_list() {
        return app_list;
    }

    public void setApp_list(List<AppListBean> app_list) {
        this.app_list = app_list;
    }

    public static class AppListBean {
        private String id;
        private String app_name;
        private String osType;
        private String app_bag_name;
        private String down_link;
        private String release_status;
        private String app_version;
        private String app_size;
        private String app_logo;
        private String app_descript;
        private String display_order;
        private String created_at;
        private String updated_at;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getApp_name() {
            return app_name;
        }

        public void setApp_name(String app_name) {
            this.app_name = app_name;
        }

        public String getOsType() {
            return osType;
        }

        public void setOsType(String osType) {
            this.osType = osType;
        }

        public String getApp_bag_name() {
            return app_bag_name;
        }

        public void setApp_bag_name(String app_bag_name) {
            this.app_bag_name = app_bag_name;
        }

        public String getDown_link() {
            return down_link;
        }

        public void setDown_link(String down_link) {
            this.down_link = down_link;
        }

        public String getRelease_status() {
            return release_status;
        }

        public void setRelease_status(String release_status) {
            this.release_status = release_status;
        }

        public String getApp_version() {
            return app_version;
        }

        public void setApp_version(String app_version) {
            this.app_version = app_version;
        }

        public String getApp_size() {
            return app_size;
        }

        public void setApp_size(String app_size) {
            this.app_size = app_size;
        }

        public String getApp_logo() {
            return app_logo;
        }

        public void setApp_logo(String app_logo) {
            this.app_logo = app_logo;
        }

        public String getApp_descript() {
            return app_descript;
        }

        public void setApp_descript(String app_descript) {
            this.app_descript = app_descript;
        }

        public String getDisplay_order() {
            return display_order;
        }

        public void setDisplay_order(String display_order) {
            this.display_order = display_order;
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
    }
}
