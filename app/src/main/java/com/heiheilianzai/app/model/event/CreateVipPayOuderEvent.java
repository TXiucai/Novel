package com.heiheilianzai.app.model.event;

/**
 * 支付Vip订单创建
 */
public class CreateVipPayOuderEvent {
    private boolean isCloseFlag;

    public boolean isCloseFlag() {
        return isCloseFlag;
    }

    public void setCloseFlag(boolean closeFlag) {
        isCloseFlag = closeFlag;
    }
}