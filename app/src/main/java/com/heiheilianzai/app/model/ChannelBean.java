package com.heiheilianzai.app.model;

import java.util.List;

public class ChannelBean {

    /**
     * channel_icon : http://webp.itrm8z3b.com/channel_icon/img/775657c23a8af7729bc1cbc58c7ff76d.jpg
     * channel_name : test-02
     * id : 2
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean {
        private String channel_icon;
        private String channel_name;
        private String id;

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
    }
}
