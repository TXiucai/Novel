package com.heiheilianzai.app.model;

import java.util.List;

/**
 * Created by scb on 2018/7/7.
 */
public class RankItem {
    String rank_type;
    String list_name;
    String description;
    String bg_img;
    List<String> icon;
    String statist_way;//1浏览量 2下载量 3收藏量 4更新时间

    public String getStatist_way() {
        return statist_way;
    }

    public void setStatist_way(String statist_way) {
        this.statist_way = statist_way;
    }

    public String getBg_img() {
        return bg_img;
    }

    public void setBg_img(String bg_img) {
        this.bg_img = bg_img;
    }

    public String getRank_type() {
        return rank_type;
    }

    public void setRank_type(String rank_type) {
        this.rank_type = rank_type;
    }

    public String getList_name() {
        return list_name;
    }

    public void setList_name(String list_name) {
        this.list_name = list_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getIcon() {
        return icon;
    }

    public void setIcon(List<String> icon) {
        this.icon = icon;
    }
}
