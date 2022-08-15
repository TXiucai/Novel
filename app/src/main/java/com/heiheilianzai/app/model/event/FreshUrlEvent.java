package com.heiheilianzai.app.model.event;

public class FreshUrlEvent {
    String url;

    public FreshUrlEvent(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
