package com.heiheilianzai.app.model.cartoon;

import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseTag;

import java.io.Serializable;
import java.util.List;

public class CartoonChapter extends BaseAd implements Serializable {

    /**
     * chapter_id : 911
     * chapter_title : 2
     * is_vip : 0
     * is_book_coupon_pay : 0
     * can_read : 1
     * is_preview : 0
     * tag : {"tab":"免费","color":"#778899"}
     * update_time : 2022-02-17 11:08:08
     * display_order : 0
     */
    private String video_id;
    private String chapter_id;
    private String chapter_title;
    private String is_vip;
    private String is_book_coupon_pay;
    private int can_read;
    private int is_preview;
    private String is_limited_free;
    private boolean is_buy_status;
    private String cover;
    private String name;//历史记录
    private String log_id;
    private String description;
    private int play_node;

    public int getPlay_node() {
        return play_node;
    }

    public void setPlay_node(int play_node) {
        this.play_node = play_node;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLog_id() {
        return log_id;
    }

    public void setLog_id(String log_id) {
        this.log_id = log_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public int getAd_type() {
        return ad_type;
    }

    public void setAd_type(int ad_type) {
        this.ad_type = ad_type;
    }

    public boolean isIs_buy_status() {
        return is_buy_status;
    }

    public void setIs_buy_status(boolean is_bug_status) {
        this.is_buy_status = is_bug_status;
    }

    public String getIs_limited_free() {
        return is_limited_free;
    }

    public void setIs_limited_free(String is_limited_free) {
        this.is_limited_free = is_limited_free;
    }

    /**
     * tab : 免费
     * color : #778899
     */

    private List<BaseTag> tag;
    private String update_time;
    private String display_order;
    private String content;
    private boolean select;

    public List<BaseTag> getTag() {
        return tag;
    }

    public void setTag(List<BaseTag> tag) {
        this.tag = tag;
    }

    public boolean isSelect() {
        return select;
    }

    public void setSelect(boolean select) {
        this.select = select;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(String chapter_id) {
        this.chapter_id = chapter_id;
    }

    public String getChapter_title() {
        return chapter_title;
    }

    public void setChapter_title(String chapter_title) {
        this.chapter_title = chapter_title;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public String getIs_book_coupon_pay() {
        return is_book_coupon_pay;
    }

    public void setIs_book_coupon_pay(String is_book_coupon_pay) {
        this.is_book_coupon_pay = is_book_coupon_pay;
    }

    public int getCan_read() {
        return can_read;
    }

    public void setCan_read(int can_read) {
        this.can_read = can_read;
    }

    public int getIs_preview() {
        return is_preview;
    }

    public void setIs_preview(int is_preview) {
        this.is_preview = is_preview;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

}
