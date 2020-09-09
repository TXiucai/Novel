package com.heiheilianzai.app.model.event;

/**
 * Created by scb on 2018/8/8.
 */
public class RefreshBookInfoEvent {
    public boolean isSave;

    public RefreshBookInfoEvent(boolean isSave) {
        this.isSave = isSave;
    }

    public RefreshBookInfoEvent() {
    }
}
