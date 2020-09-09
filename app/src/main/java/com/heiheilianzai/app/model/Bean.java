package com.heiheilianzai.app.model;

import java.util.List;

public class Bean {


    /**
     * code : 0
     * msg : 获取动态域名
     * data : [{"name":"heathcote.com"},{"name":"hilpert.org"},{"name":"hoppe.com"}]
     */

    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * name : heathcote.com
         */

        public String name;


        @Override
        public String toString() {
            return "DataBean{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "Bean{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
