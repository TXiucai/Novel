package com.heiheilianzai.app.component.pay;

/**
 * Created by scb on 2018/8/12.
 */
public interface GoPay {
    void handleOrderInfo(String result);

    void startPay(String orderInfo);
}
