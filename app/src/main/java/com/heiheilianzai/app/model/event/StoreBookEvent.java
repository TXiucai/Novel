package com.heiheilianzai.app.model.event;

/**
 * 首页小说 滑动改变头部搜索框样式
 */
public class StoreBookEvent extends StoreEvent {
    public StoreBookEvent(boolean chooseWho, int y) {
        super(chooseWho, y);
    }
}
