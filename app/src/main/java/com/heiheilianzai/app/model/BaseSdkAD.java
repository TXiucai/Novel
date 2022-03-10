package com.heiheilianzai.app.model;

import android.text.TextUtils;

import com.mobi.xad.bean.AdInfo;

public class BaseSdkAD {
    public String requestId;
    public String adId;
    public String adPosId;

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getAdId() {
        return adId;
    }

    public void setAdId(String adId) {
        this.adId = adId;
    }

    public String getAdPosId() {
        return adPosId;
    }

    public void setAdPosId(String adPosId) {
        this.adPosId = adPosId;
    }

    /**
     * sdk上报事件用
     * @param baseSdkAD
     * @return
     */
    public static AdInfo newAdInfo(BaseSdkAD baseSdkAD) {
        if (baseSdkAD != null && TextUtils.isEmpty(baseSdkAD.getAdId())) {
            AdInfo adInfo = new AdInfo();
            adInfo.setAdId(baseSdkAD.getAdId());
            adInfo.setAdPosId(baseSdkAD.getAdPosId());
            adInfo.setAdPosId(baseSdkAD.getRequestId());
            return adInfo;
        } else {
            return null;
        }
    }
}
