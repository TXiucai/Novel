package com.heiheilianzai.app.model.event;

/**
 * 控制首页有声 暂停 播放 获取状态
 * javascript:playPause()暂停
 * javascript:playRun()播放
 * javascript:isPlaying() 获取状态 需要H5再次回调客户端将状态值返回
 */
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