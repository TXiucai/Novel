package com.heiheilianzai.app.component.pay;

import android.app.Activity;
import android.util.Log;

import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;

/**
 * Created by scb on 2018/8/12.
 */
public abstract class ReaderPay implements GoPay {

    public Activity context;

    public ReaderPay(Activity context) {
        this.context = context;
    }

    public void requestPayOrder(String url, String goodsId) {

        ReaderParams params = new ReaderParams(context);
        params.putExtraParams("goods_id", goodsId);

        String json = params.generateParamsJson();

        HttpUtils.getInstance((Activity) context).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                        handleOrderInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    public void requestPayOrder(String url, String goodsId, String channel, int pay_type) {
        ReaderParams params = new ReaderParams(context);
        params.putExtraParams("goods_id", goodsId);
        params.putExtraParams("channel", channel);
        params.putExtraParams("pay_type", String.valueOf(pay_type));
        String json = params.generateParamsJson();
        HttpUtils.getInstance((Activity) context).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        handleOrderInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        Log.e("", ex);
                    }
                }

        );

    }


    public abstract void startPay(String orderInfo);

    public abstract void handleOrderInfo(String result);
}
