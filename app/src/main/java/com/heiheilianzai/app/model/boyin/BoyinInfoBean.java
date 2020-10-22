package com.heiheilianzai.app.model.boyin;

import com.google.gson.annotations.SerializedName;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class BoyinInfoBean extends LitePalSupport implements Serializable, Comparable<BoyinInfoBean> {

    /**
     * 播音小说详情
     *
     * anchor_id : 4
     * anchor_name : 小仙仙
     * anchor_img : http://img.i9bc0rmy.com/uploads/data_img/20200114/5e1d813faa4299.77161749.jpg
     * id : 2
     * name : 被姑丈给弄上床
     * numbers : 3
     * img : http://img.i9bc0rmy.com/uploads/data_img/20200114/5e1d829394de86.30536533.jpg
     * introduction : 我穿上那丝质的白色的内衣裤后，赶忙穿回睡裙走出浴室时，不禁有些犹疑，因为我这套睡裙是一件紧身而白色半透明的丝质迷你的短裙，我知道细姑丈一定可以看到我的身裁，甚至可以看到我的白色内衣裤呢！
     * update_status : 2
     * listen_type : 2
     * listen : 101678
     * listen_true : 1863
     * category_name : 现代
     */
    private int anchor_id;
    private String anchor_name;
    private String anchor_img;
    @SerializedName("id")
    private int nid;
    @Column (unique = true, defaultValue = "0")
    private String name;
    private int numbers;
    private String img;
    private String introduction;
    private int update_status;
    private int listen_type;
    private int listen;
    private int listen_true;
    private String category_name;
    private int down_chapter;

    public int getDown_chapter() {
        return down_chapter;
    }

    public void setDown_chapter(int down_chapter) {
        this.down_chapter = down_chapter;
    }

    public int getAnchor_id() {
        return anchor_id;
    }

    public void setAnchor_id(int anchor_id) {
        this.anchor_id = anchor_id;
    }

    public String getAnchor_name() {
        return anchor_name;
    }

    public void setAnchor_name(String anchor_name) {
        this.anchor_name = anchor_name;
    }

    public String getAnchor_img() {
        return anchor_img;
    }

    public void setAnchor_img(String anchor_img) {
        this.anchor_img = anchor_img;
    }

    public int getId() {
        return nid;
    }

    public void setId(int id) {
        this.nid = nid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public int getUpdate_status() {
        return update_status;
    }

    public void setUpdate_status(int update_status) {
        this.update_status = update_status;
    }

    public int getListen_type() {
        return listen_type;
    }

    public void setListen_type(int listen_type) {
        this.listen_type = listen_type;
    }

    public int getListen() {
        return listen;
    }

    public void setListen(int listen) {
        this.listen = listen;
    }

    public int getListen_true() {
        return listen_true;
    }

    public void setListen_true(int listen_true) {
        this.listen_true = listen_true;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    @Override
    public int compareTo(BoyinInfoBean o) {
        return 0;
    }
}
