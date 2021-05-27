package com.heiheilianzai.app.model.event;

/**
 * 支付Vip订单创建
 */
public class CreateVipPayOuderEvent {
    private boolean isCloseFlag;
    private int goods_id;

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public boolean isCloseFlag() {
        return isCloseFlag;
    }

    public void setCloseFlag(boolean closeFlag) {
        isCloseFlag = closeFlag;
    }
}