package com.heiheilianzai.app.wxpay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.heiheilianzai.app.pay.ReaderPay;

import org.json.JSONObject;

import cn.jmessage.support.qiniu.android.utils.StringUtils;


/**
 * Created by scb on 2018/8/12.
 */
public class WXGoPay extends ReaderPay {
    private Activity mActivity;

    public WXGoPay(Activity context) {
        super(context);
        mActivity = context;
    }

    class WeixinPay {
        public String appid;
        public String partnerid;
        public String prepayid;
        public String noncestr;
        public String timestamp;
        public String paySign;
        public String prepay_id;
    }

    @Override
    public void startPay(String prepayId) {
        try {
            JSONObject jsonObject = new JSONObject(prepayId);
            if (jsonObject.toString().contains("pay_url")) {
                String pay_url = jsonObject.getString("pay_url");
                if (!StringUtils.isNullOrEmpty(pay_url)) {
                    Uri uri = Uri.parse(pay_url);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    mActivity.startActivity(intent);
                    return;
                }
            }
            IWXAPI msgApi = WXAPIFactory.createWXAPI(mActivity, jsonObject.getString("appid"), false);
            msgApi.registerApp(jsonObject.getString("appid"));
            PayReq req = new PayReq();
            req.appId = jsonObject.getString("appid");
            req.partnerId = jsonObject.getString("partnerid");
            req.packageValue = jsonObject.getString("package");
            req.prepayId = jsonObject.getString("prepayid");
            req.nonceStr = jsonObject.getString("noncestr");
            req.timeStamp = jsonObject.getString("timestamp");
            req.sign = jsonObject.getString("paySign");
            msgApi.sendReq(req);
        } catch (Exception E) {
        }
    }

    @Override
    public void handleOrderInfo(String result) {
        startPay(result);
    }
}
