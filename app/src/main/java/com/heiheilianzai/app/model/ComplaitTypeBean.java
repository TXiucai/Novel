package com.heiheilianzai.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ComplaitTypeBean implements Serializable {

    @SerializedName("list")
    private List<ComplaitListBean> list;

    public List<ComplaitListBean> getList() {
        return list;
    }

    public void setList(List<ComplaitListBean> list) {
        this.list = list;
    }

    public static class ComplaitListBean implements Serializable {
        /**
         * id : 4
         * title : 反馈问题4
         */

        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("children_feed_back_type")
        private List<ComplaitSubListBean> subList;

        public List<ComplaitSubListBean> getSubList() {
            return subList;
        }

        public void setSubList(List<ComplaitSubListBean> subList) {
            this.subList = subList;
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
    }

    public static class ComplaitSubListBean implements Serializable{
        @SerializedName("id")
        private String id;
        @SerializedName("title")
        private String title;
        @SerializedName("pid")
        private String pid;
        @SerializedName("is_need")
        private String isNeed;
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
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

        public String getPid() {
            return pid;
        }

        public void setPid(String pid) {
            this.pid = pid;
        }

        public String getIsNeed() {
            return isNeed;
        }

        public void setIsNeed(String isNeed) {
            this.isNeed = isNeed;
        }
    }
}