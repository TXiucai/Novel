package com.heiheilianzai.app.eventbus;
/**
 * 首页漫画 滑动改变头部搜索框样式
 */
public class StoreComicEvent extends StoreEvent {
    public StoreComicEvent(boolean chooseWho, int y) {
        super(chooseWho, y);
    }
}
