package com.heiheilianzai.app.push;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.heiheilianzai.app.utils.MyToash;

import cn.jpush.android.api.JPushInterface;

public class MyPushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle bundle = intent.getExtras();

        switch (intent.getAction()) {
            case "cn.jpush.android.intent.REGISTRATION":
                MyToash.Log("用户注册");
                break;
            case "cn.jpush.android.intent.MESSAGE_RECEIVED":
                MyToash.Log("用户接收SDK消息");
                break;
            case "cn.jpush.android.intent.NOTIFICATION_RECEIVED":
                MyToash.Log("用户收到通知栏信息");
                onReceiveNotification(context, intent.getExtras());
                break;
            case "cn.jpush.android.intent.NOTIFICATION_OPENED":
                MyToash.Log("用户打开通知栏信息");
                onOpenNotification(context, bundle);
                break;
        }
    }

    private void onReceiveNotification(Context context, Bundle bundle) {
        String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
        MyToash.Log(" title : " + title);
        String message = bundle.getString(JPushInterface.EXTRA_ALERT);
        MyToash.Log("message : " + message);
        String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);
        MyToash.Log("extras : " + extras);

        try {
            JSONObject obj = JSON.parseObject(extras);
            String content = obj.getString("content");
            String skip_type = obj.getString("skip_type");
            ReaderMessageReceiver.sendNotification(context, title, message, skip_type, content);
        } catch (Exception ig) {
            ReaderMessageReceiver.sendNotification(context, title, message, null, null);
        }

    }

    private void onOpenNotification(Context context, Bundle bundle) {

        String msgid = bundle.getString(JPushInterface.EXTRA_MSG_ID);
        MyToash.Log("notify msgid:" + msgid);
        JPushInterface.reportNotificationOpened(context, msgid);

        /*Intent intent = new Intent(context,SplashActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);*/
    }
}
