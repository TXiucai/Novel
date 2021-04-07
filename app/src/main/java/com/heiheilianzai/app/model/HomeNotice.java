package com.heiheilianzai.app.model;

/**
 * 首页公告
 */
public class HomeNotice {
    public String title;
    public String content;
    public String os_type;
    private String img_content;
    private String jump_url;
    private String announ_type;

    public String getImg_content() {
        return img_content;
    }

    public void setImg_content(String img_content) {
        this.img_content = img_content;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getAnnoun_type() {
        return announ_type;
    }

    public void setAnnoun_type(String announ_type) {
        this.announ_type = announ_type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getOs_type() {
        return os_type;
    }

    public void setOs_type(String os_type) {
        this.os_type = os_type;
    }
}