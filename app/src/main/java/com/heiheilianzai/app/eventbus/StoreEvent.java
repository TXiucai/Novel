package com.heiheilianzai.app.eventbus;

/**
 * 滑动改变头部搜索框样式(首页漫画，首页小说)专用
 */
public class StoreEvent {
    public boolean chooseWho;

    public int Y;

    public StoreEvent(boolean chooseWho, int y) {
        this.chooseWho = chooseWho;
        Y = y;
    }

    public boolean isChooseWho() {
        return chooseWho;
    }

    public void setChooseWho(boolean chooseWho) {
        this.chooseWho = chooseWho;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
