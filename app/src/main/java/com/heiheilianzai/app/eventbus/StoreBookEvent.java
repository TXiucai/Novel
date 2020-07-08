package com.heiheilianzai.app.eventbus;

/**
 * 首页小说 滑动改变头部搜索框样式
 */
public class StoreBookEvent extends StoreEvent {
    public StoreBookEvent(boolean chooseWho, int y) {
        super(chooseWho, y);
    }
}
