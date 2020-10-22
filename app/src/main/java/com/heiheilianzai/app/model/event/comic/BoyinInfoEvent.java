package com.heiheilianzai.app.model.event.comic;

import com.heiheilianzai.app.model.boyin.BoyinInfoBean;

public class BoyinInfoEvent {
    BoyinInfoBean boyinInfoBean;

    public BoyinInfoEvent(BoyinInfoBean boyinInfoBean) {
        this.boyinInfoBean = boyinInfoBean;
    }
    public BoyinInfoEvent( ) {
    }
    public BoyinInfoBean getBoyinInfoBean() {
        return boyinInfoBean;
    }

    public void setBoyinInfoBean(BoyinInfoBean boyinInfoBean) {
        this.boyinInfoBean = boyinInfoBean;
    }
}
