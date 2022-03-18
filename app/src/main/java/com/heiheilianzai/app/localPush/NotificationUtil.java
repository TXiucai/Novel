package com.heiheilianzai.app.localPush;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DateUtils;

import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知工具
 */

public class NotificationUtil {
    /**
     * 通过定时闹钟发送通知
     */
    public static void notifyByAlarm(Context context, List<LoaclPushBean> lists) {
        for (LoaclPushBean loaclPushBean : lists) {
            if (loaclPushBean == null) {
                continue;
            }
            String push_way = loaclPushBean.getPush_way();
            String start_time;
            if (TextUtils.equals(push_way, "1")) {
                start_time = loaclPushBean.getRelease_time_start_redundant_week();
            } else {
                start_time = loaclPushBean.getRelease_time_start_push_way();
            }
            if (TextUtils.isEmpty(start_time)) {
                continue;
            }
            String alarm_time = DateUtils.getTodayTime() + " " + start_time;
            long time = DateUtils.dateToTime(alarm_time);
            try {
                //时间过去的闹钟加一天设置进去
                if (time > 0 && time > System.currentTimeMillis()) {
                    Map map = new HashMap<>();
                    map.put("KEY_NOTIFY_ID", loaclPushBean.getId());
                    map.put("KEY_NOTIFY", LoaclPushBean.to(loaclPushBean));
                    AlarmTimerUtil.setAlarmTimer(context, Integer.parseInt(loaclPushBean.getId()), time, map);
                } else {
                    Map map = new HashMap<>();
                    time += 24 * 60 * 60 * 1000;
                    map.put("KEY_NOTIFY_ID", loaclPushBean.getId());
                    map.put("KEY_NOTIFY", LoaclPushBean.to(loaclPushBean));
                    AlarmTimerUtil.setAlarmTimer(context, Integer.parseInt(loaclPushBean.getId()), time, map);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyByAlarmByReceiver(Context context, LoaclPushBean obj) {
        if (context == null || obj == null) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMsg(context, obj, Integer.parseInt(obj.getId()), System.currentTimeMillis(), manager);
    }

    /**
     * 消息通知
     *
     * @param context
     * @param obj
     */
    private static void notifyMsg(Context context, LoaclPushBean obj, int nid, long time, NotificationManager mNotifyMgr) {
        if (context == null || obj == null) return;
        if (mNotifyMgr == null) {
            mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (time <= 0) return;
        //准备intent
        Intent intent = new Intent(context, AlarmReceive.class);
        intent.putExtra("localPush", obj);
        //notification
        Notification notification = null;
        // 构建 PendingIntent
        PendingIntent pi = PendingIntent.getBroadcast(context, nid, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            @SuppressLint("WrongConstant") NotificationChannel mChannel = new NotificationChannel("local_push_id", "heihei", NotificationManager.IMPORTANCE_HIGH);
            mChannel.setDescription("local_push");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.canBypassDnd();
            mChannel.enableVibration(true);
            mNotifyMgr.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(context, "local_push_id");
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.getPush_title())
                    .setContentText(obj.getPush_content())
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else {
            Notification.Builder builder = new Notification.Builder(context);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.getPush_title())
                    .setContentText(obj.getPush_content())
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        }
        if (notification != null) {
            mNotifyMgr.notify(nid, notification);
        }
    }

    /**
     * 取消所有通知 同时取消定时闹钟
     *
     * @param context
     */

    public static void clearAllNotifyMsg(Context context, String result) {
        try {
            if (!TextUtils.isEmpty(result)) {
                NotificationManager mNotifyMgr = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotifyMgr.cancelAll();
                List<LoaclPushBean> localLists = new ArrayList<>();
                Gson gson = new Gson();
                JSONArray jsonArray = new JSONArray(result);
                for (int i = 0; i < jsonArray.length(); i++) {
                    LoaclPushBean loaclPushBean = gson.fromJson(String.valueOf(jsonArray.getJSONObject(i)), LoaclPushBean.class);
                    localLists.add(loaclPushBean);
                }
                for (int i = 0; i < localLists.size(); i++) {
                    AlarmTimerUtil.cancelAlarmTimer(context, Integer.parseInt(localLists.get(i).getId()));
                }
            }
        } catch (Exception ignore) {
        }
    }
}