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
}