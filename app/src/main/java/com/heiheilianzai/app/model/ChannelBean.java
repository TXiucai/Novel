package com.heiheilianzai.app.model;

import java.io.Serializable;
import java.util.List;

public class ChannelBean implements Serializable {


    /**
     * channel_icon : http://webp.itrm8z3b.com/channel_icon/img/2d57f745b92faf64b9531513c0910198.jpg
     * channel_name : 我
     * id : 1
     * recommend_id_list : ["2","21"]
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean implements Serializable {
        private String channel_icon;
        private String channel_name;
        private String id;
        private List<String> recommend_id_list;

        public String getChannel_icon() {
            return channel_icon;
        }

        public void setChannel_icon(String channel_icon) {
            this.channel_icon = channel_icon;
        }

        public String getChannel_name() {
            return channel_name;
        }

        public void setChannel_name(String channel_name) {
            this.channel_name = channel_name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public List<String> getRecommend_id_list() {
            return recommend_id_list;
        }

        public void setRecommend_id_list(List<String> recommend_id_list) {
            this.recommend_id_list = recommend_id_list;
        }
    }
}
