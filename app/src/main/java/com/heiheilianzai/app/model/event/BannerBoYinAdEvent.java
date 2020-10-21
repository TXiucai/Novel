package com.heiheilianzai.app.model.event;

/**
 * banner公告跳转到有声事件
 */
public class BannerBoYinAdEvent {
    String content;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public BannerBoYinAdEvent(String content) {
        this.content = content;
    }
}