package com.heiheilianzai.app.model.event;

public class BoyinPlayerEvent {
    boolean isplay;

    public BoyinPlayerEvent(boolean isplay) {
        this.isplay = isplay;
    }

    public boolean isIsplay() {
        return isplay;
    }

    public void setIsplay(boolean isplay) {
        this.isplay = isplay;
    }
}
