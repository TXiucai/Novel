package com.heiheilianzai.app.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class ShareRecordBean implements Serializable {


    @SerializedName("list")
    private List<ShareRecordList> list;

    public List<ShareRecordList> getList() {
        return list;
    }

    public void setList(List<ShareRecordList> list) {
        this.list = list;
    }

    public static class ShareRecordList implements Serializable {
        /**
         * uid : 466003
         * mobile : 188****1111
         * nickname : 测试帐号18800001111
         * created_at : 2021-02-03 15:11:40
         * head_pic : /images/qr_img/head.png
         */

        @SerializedName("uid")
        private String uid;
        @SerializedName("mobile")
        private String mobile;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("created_at")
        private String createdAt;
        @SerializedName("head_pic")
        private String headPic;

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

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getHeadPic() {
            return headPic;
        }

        public void setHeadPic(String headPic) {
            this.headPic = headPic;
        }
    }
}