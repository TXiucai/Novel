package com.heiheilianzai.app.model;

import java.util.List;

/**
 * Created by scb on 2018/8/12.
 */
public class AcquirePayItem {


    /**
     * privilege_list_name : ["测试test","测试重复","测试1"]
     * goods_id : 41
     * title : 两日会员.
     * price : 10
     * original_price : 0
     * goods_label : 新人专享
     * apple_id :
     * default_select : 0
     * sub_title : 限时体验
     * end_time : 1643641505
     * pay_channel : ["alipay","weixin"]
     * note_text :
     * note : 限时体验
     * tag : []
     */

    private String goods_id;
    private String title;
    private int price;
    private int original_price;
    private String goods_label;
    private String apple_id;
    private String default_select;
    private String sub_title;
    private int end_time;
    private String note_text;
    private String note;
    private List<String> privilege_list_name;
    private List<String> pay_channel;
    private List<?> tag;
    private String content;
    private String giving;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getGiving() {
        return giving;
    }

    public void setGiving(String giving) {
        this.giving = giving;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(int original_price) {
        this.original_price = original_price;
    }

    public String getGoods_label() {
        return goods_label;
    }

    public void setGoods_label(String goods_label) {
        this.goods_label = goods_label;
    }

    public String getApple_id() {
        return apple_id;
    }

    public void setApple_id(String apple_id) {
        this.apple_id = apple_id;
    }

    public String getDefault_select() {
        return default_select;
    }

    public void setDefault_select(String default_select) {
        this.default_select = default_select;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public int getEnd_time() {
        return end_time;
    }

    public void setEnd_time(int end_time) {
        this.end_time = end_time;
    }

    public String getNote_text() {
        return note_text;
    }

    public void setNote_text(String note_text) {
        this.note_text = note_text;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public List<String> getPrivilege_list_name() {
        return privilege_list_name;
    }

    public void setPrivilege_list_name(List<String> privilege_list_name) {
        this.privilege_list_name = privilege_list_name;
    }

    public List<String> getPay_channel() {
        return pay_channel;
    }

    public void setPay_channel(List<String> pay_channel) {
        this.pay_channel = pay_channel;
    }

    public List<?> getTag() {
        return tag;
    }

    public void setTag(List<?> tag) {
        this.tag = tag;
    }
}
