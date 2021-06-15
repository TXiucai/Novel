package com.heiheilianzai.app.model;

import java.util.List;

public class OptionBeen  extends  BaseAd {
    public String book_id;//": 100,
    public String comic_id;//": 10086, //漫画id
    public String name;//"": "乔乔的奇妙冒险", //漫画名称
    public String cover;//水平封面
    public String author;//"": "黎明C", //作者
    public String description;//"": "美貌千金与帅气王爷", //描述
    public int is_finish;//"": 1, //是否完结 1已完结 0连载中
    public String flag;//"": "更新至32话", //角标
    public List<BaseTag> tag;
    public int total_views;
    public int total_downs;
    public String total_favors;
    public long updated_at;
  //  public String finished;
   // public int is_vip;//": 1,
 //   public int is_baoyue;//": 0,
 //   public String enable_time;//": "06月11日",
  //  public String expire_time;//": "06月30日"


    public int getTotal_downs() {
        return total_downs;
    }

    public void setTotal_downs(int total_downs) {
        this.total_downs = total_downs;
    }

    public long getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(long updated_at) {
        this.updated_at = updated_at;
    }

    public String getTotal_favors() {
        return total_favors;
    }

    public void setTotal_favors(String total_favors) {
        this.total_favors = total_favors;
    }

    public int getIs_finish() {
        return is_finish;
    }

    public void setIs_finish(int is_finish) {
        this.is_finish = is_finish;
    }

    public int getTotal_views() {
        return total_views;
    }

    public void setTotal_views(int total_views) {
        this.total_views = total_views;
    }

    public String getComic_id() {
        return comic_id;
    }

    public void setComic_id(String comic_id) {
        this.comic_id = comic_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public List<BaseTag> getTag() {
        return tag;
    }

    public void setTag(List<BaseTag> tag) {
        this.tag = tag;
    }

    @Override
    public String toString() {
        return "OptionBeen{" +
                "book_id='" + book_id + '\'' +
                ", comic_id='" + comic_id + '\'' +
                ", name='" + name + '\'' +
                ", cover='" + cover + '\'' +
                ", author='" + author + '\'' +
                ", description='" + description + '\'' +
                ", flag='" + flag + '\'' +
                ", tag=" + tag +
                ", advert_id='" + advert_id + '\'' +
                ", ad_type=" + ad_type +
                ", ad_title='" + ad_title + '\'' +
                ", ad_image='" + ad_image + '\'' +
                ", ad_skip_url='" + ad_skip_url + '\'' +
                ", ad_url_type=" + ad_url_type +
                '}';
    }
}
