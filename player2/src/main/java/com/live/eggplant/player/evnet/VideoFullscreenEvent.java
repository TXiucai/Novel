package com.live.eggplant.player.evnet;

/**
 * 监听 视频切换全屏状态  （在视频详情页赋值的回调值第二次就为空了 才这样写的）
 */
public class VideoFullscreenEvent {
    private boolean isFullscreen;

    public VideoFullscreenEvent(boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        isFullscreen = fullscreen;
    }

    public boolean isFullscreen() {
        return isFullscreen;
    }
}
