package com.heiheilianzai.app.model;

import com.heiheilianzai.app.model.book.StroreBookcLable;

public class InfoBook extends StroreBookcLable.Book {
    public String views;//": 0,
    public String display_label;//": "连载中 | 网游竞技 | 141万字",
    public String total_words;//": "141万字",
    public String words_price;//": 10,
    public String chapter_pric;//e": 0,
    public int total_comment;//": "6",
    public String hot_num;
    public String total_favors;
    public int total_chapter;//": 542,
    public String last_chapter_time;//": "更新于4个月前",
    public String last_chapter;//": "第542 鬼门",
    public String total_chapters;//总章节数
    public String cid1;//分类ID
    public String is_new;//是否是新书 1:是 0:否
    public String is_hot;//是否是热门 1:是 0:否
    public String is_yy;//是否是爽文 1:是 0:否
    public String is_greatest;//是否是精选 1:是 0:否
    public String is_god;//是否是大神 1:是 0:否
    public String is_limited_free;

    public String getIs_limited_free() {
        return is_limited_free;
    }

    public void setIs_limited_free(String is_limited_free) {
        this.is_limited_free = is_limited_free;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDisplay_label() {
        return display_label;
    }

    public void setDisplay_label(String display_label) {
        this.display_label = display_label;
    }

    public String getTotal_words() {
        return total_words;
    }

    public void setTotal_words(String total_words) {
        this.total_words = total_words;
    }

    public String getWords_price() {
        return words_price;
    }

    public void setWords_price(String words_price) {
        this.words_price = words_price;
    }

    public String getChapter_pric() {
        return chapter_pric;
    }

    public void setChapter_pric(String chapter_pric) {
        this.chapter_pric = chapter_pric;
    }

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

    public int getTotal_chapter() {
        return total_chapter;
    }

    public void setTotal_chapter(int total_chapter) {
        this.total_chapter = total_chapter;
    }

    public String getLast_chapter_time() {
        return last_chapter_time;
    }

    public void setLast_chapter_time(String last_chapter_time) {
        this.last_chapter_time = last_chapter_time;
    }

    public String getLast_chapter() {
        return last_chapter;
    }

    public void setLast_chapter(String last_chapter) {
        this.last_chapter = last_chapter;
    }
}
