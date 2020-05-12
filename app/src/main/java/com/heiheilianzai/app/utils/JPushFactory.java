package com.heiheilianzai.app.utils;

import android.content.Context;

import com.xiaomi.mipush.sdk.MiPushClient;

import cn.jiguang.api.JCoreInterface;
import cn.jpush.android.api.JPushInterface;

public class JPushFactory {
    public static void testpush(Context context) {
        if (!MyProcess.isMainProcess(context)) {
            return;
        }
        String regid = JPushInterface.getRegistrationID(context);
        MyToash.Log("push regid:" + regid);
        MyToash.Log("xiaomi regid:" + MiPushClient.getRegId(context));
        JCoreInterface.testCountryCode("ph");
    }
}
