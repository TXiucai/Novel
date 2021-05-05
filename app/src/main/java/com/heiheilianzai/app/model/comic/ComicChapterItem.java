package com.heiheilianzai.app.model.comic;

import java.util.List;

public class ComicChapterItem {
    public String comic_id;//": 1007, //漫画id
    public String chapter_id;//": 486380, //章节id
    public String chapter_title;//": 章节title
    public String next_chapter;//": 621914, //下一章id
    public String last_chapter;//": 0, //上一章id
    public int   total_comment;
    public int is_preview;//": 1, //是否预览章节 1是 0不是
    public  int  display_order;
    public List<BaseComicImage> image_list;
    public ComicChapterTopAd advert;
    public String is_vip;
    public String is_book_coupon_pay;
    private boolean is_buy_status;
    String is_limited_free;//0否1是

    public String getIs_limited_free() {
        return is_limited_free;
    }

    public void setIs_limited_free(String is_limited_free) {
        this.is_limited_free = is_limited_free;
    }

    public boolean isIs_buy_status() {
        return is_buy_status;
    }

    public void setIs_buy_status(boolean is_buy_status) {
        this.is_buy_status = is_buy_status;
    }

    public String getIs_book_coupon_pay() {
        return is_book_coupon_pay;
    }

    public void setIs_book_coupon_pay(String is_book_coupon_pay) {
        this.is_book_coupon_pay = is_book_coupon_pay;
    }

    public String getIs_vip() {
        return is_vip;
    }

    public void setIs_vip(String is_vip) {
        this.is_vip = is_vip;
    }

    public ComicChapterTopAd getAdvert() {
        return advert;
    }

    public void setAdvert(ComicChapterTopAd advert) {
        this.advert = advert;
    }

    public String getComic_id() {
        return comic_id;
    }

    public void setComic_id(String comic_id) {
        this.comic_id = comic_id;
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

    public String getNext_chapter() {
        return next_chapter;
    }

    public void setNext_chapter(String next_chapter) {
        this.next_chapter = next_chapter;
    }

    public String getLast_chapter() {
        return last_chapter;
    }

    public void setLast_chapter(String last_chapter) {
        this.last_chapter = last_chapter;
    }

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

    public int getIs_preview() {
        return is_preview;
    }

    public void setIs_preview(int is_preview) {
        this.is_preview = is_preview;
    }

    public int getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(int display_order) {
        this.display_order = display_order;
    }

    public List<BaseComicImage> getImage_list() {
        return image_list;
    }

    public void setImage_list(List<BaseComicImage> image_list) {
        this.image_list = image_list;
    }

    @Override
    public String toString() {
        return "ComicChapterItem{" +
                "comic_id='" + comic_id + '\'' +
                ", chapter_id='" + chapter_id + '\'' +
                ", next_chapter='" + next_chapter + '\'' +
                ", last_chapter='" + last_chapter + '\'' +
                ", is_preview=" + is_preview +
                ", image_list=" + image_list +
                '}';
    }
}
