package com.heiheilianzai.app.model;

import com.othershe.groupindexlib.bean.BaseItem;

import java.util.List;

public class CountryBean {

    /**
     * country_name : 中国
     * counter_code : 86
     */

    private List<ListBean> list;

    public List<ListBean> getList() {
        return list;
    }

    public void setList(List<ListBean> list) {
        this.list = list;
    }

    public static class ListBean extends BaseItem {
        private String country_name;
        private int counter_code;

        public String getCountry_name() {
            return country_name;
        }

        public void setCountry_name(String country_name) {
            this.country_name = country_name;
        }

        public int getCounter_code() {
            return counter_code;
        }

        public void setCounter_code(int counter_code) {
            this.counter_code = counter_code;
        }
    }
}
