package com.heiheilianzai.app.model;

/**
 * 首页banner的Item
 */
public class BannerItemStore {
    /**
     * bookid
     */
    public   int action;
    /**
     * 名称
     */
    public   String content;
    /**
     * 图片地址
     */
    public String image;

    public String  color;

    public String weight;

    public String redirect_type;

    public String getRedirect_type() {
        return redirect_type;
    }

    public void setRedirect_type(String redirect_type) {
        this.redirect_type = redirect_type;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
