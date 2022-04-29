package com.heiheilianzai.app.model;

public class OrderRecordBean {
    /**
     * {
     * "id": "45873",
     * "trade_no": "202201201509501258466485",
     * "created_at": "021-06-02 17:38:49",
     * "updated_at": "021-06-02 17:42:07",
     * "goods_id": "40",
     * "total_fee": 30,
     * "status": "2",
     * "user_vip_name": "月卡会员"
     * ”pay_channel_name“:“支付宝”
     * }
     *
     * status      订单状态 1未付款  2付款成功  3付款失败  4 已过期
     */

    private String id;
    private String trade_no;
    private String created_at;
    private String updated_at;
    private String goods_id;

    private String total_fee;
    //status  订单状态 1未付款  2付款成功  3付款失败  4 已过期
    private String status;
    private String user_vip_name;
    private String pay_channel_name;
    private int order_type;

    public int getOrder_type() {
        return order_type;
    }

    public void setOrder_type(int order_type) {
        this.order_type = order_type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTrade_no() {
        return trade_no;
    }

    public void setTrade_no(String trade_no) {
        this.trade_no = trade_no;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getGoods_id() {
        return goods_id;
    }

    public void setGoods_id(String goods_id) {
        this.goods_id = goods_id;
    }

    public String getTotal_fee() {
        return total_fee;
    }

    public void setTotal_fee(String total_fee) {
        this.total_fee = total_fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUser_vip_name() {
        return user_vip_name;
    }

    public void setUser_vip_name(String user_vip_name) {
        this.user_vip_name = user_vip_name;
    }

    public String getPay_channel_name() {
        return pay_channel_name;
    }

    public void setPay_channel_name(String pay_channel_name) {
        this.pay_channel_name = pay_channel_name;
    }
}
