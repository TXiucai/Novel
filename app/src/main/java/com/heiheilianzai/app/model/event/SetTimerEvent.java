package com.heiheilianzai.app.model.event;

public class SetTimerEvent {
    private int time;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public SetTimerEvent(int time) {
        this.time = time;
    }
}
