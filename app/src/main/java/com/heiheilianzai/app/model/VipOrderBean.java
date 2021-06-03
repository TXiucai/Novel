package com.heiheilianzai.app.model;

public class VipOrderBean {

    /**
     * trade_no : 202106021133549017465964
     * updated_at : 1622604834
     * status : 2
     * goods_id : 41
     * payment_source_id : 0
     */

    private String trade_no;
    private int updated_at;
    private int status;
    private int goods_id;
    private int payment_source_id;

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public int getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(int updated_at) {
        this.updated_at = updated_at;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(int goods_id) {
        this.goods_id = goods_id;
    }

    public int getPayment_source_id() {
        return payment_source_id;
    }

    public void setPayment_source_id(int payment_source_id) {
        this.payment_source_id = payment_source_id;
    }
}
