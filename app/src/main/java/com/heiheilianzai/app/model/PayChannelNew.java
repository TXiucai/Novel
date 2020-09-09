package com.heiheilianzai.app.model;

public class PayChannelNew {
    private String id;
    private String name;
    private String localImg;
    private String product;
    private String payInterval;
    private String timeInterval;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocalImg() {
        return localImg;
    }

    public void setLocalImg(String localImg) {
        this.localImg = localImg;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getPayInterval() {
        return payInterval;
    }

    public void setPayInterval(String payInterval) {
        this.payInterval = payInterval;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeInterval) {
        this.timeInterval = timeInterval;
    }
}
