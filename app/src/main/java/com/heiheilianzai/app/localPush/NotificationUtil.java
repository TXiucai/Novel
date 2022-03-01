package com.heiheilianzai.app.localPush;

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
import android.util.Log;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.DateUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息通知工具
 */

public class NotificationUtil {
    private static final String TAG = "NotificationUtil";

    /**
     * 通过定时闹钟发送通知
     */
    public static void notifyByAlarm(Context context, List<LoaclPushBean> lists) {
//将数据存储起来
        int count = 0;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (lists.size() <= 0) {
            //默认推送
                   /* try {
                        Map map = new HashMap<>();
                        map.put("KEY_NOTIFY_ID", obj.type);
                        map.put("KEY_NOTIFY", NotifyObject.to(obj));
                        AlarmTimerUtil.setAlarmTimer(context, ++count, obj.firstTime, "TIMER_ACTION", map);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/
        }
        for (LoaclPushBean loaclPushBean : lists) {
            String start_time = loaclPushBean.getStart_time();
            if (TextUtils.isEmpty(start_time)) {
                continue;
            }
            if (loaclPushBean == null) {
                continue;
            }
            long time = DateUtils.dateToTime(start_time);
            if (time > 0) {
                try {
                    Map map = new HashMap<>();
                    map.put("KEY_NOTIFY_ID", String.valueOf(loaclPushBean.getId()));
                    map.put("KEY_NOTIFY", loaclPushBean.to(loaclPushBean));
                    AlarmTimerUtil.setAlarmTimer(context, ++count, System.currentTimeMillis() + 10 * 1000, "TIMER_ACTION", map);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION", Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = mPreferences.edit();
        edit.putInt("KEY_MAX_ALARM_ID", count);
        edit.commit();
    }

    public static void notifyByAlarmByReceiver(Context context, LoaclPushBean obj) {
        if (context == null || obj == null) return;
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifyMsg(context, obj, obj.getId(), System.currentTimeMillis(), manager);
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
        PendingIntent pi = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        //版本兼容
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {//兼容Android8.0
            String id = "my_channel_01";
            int importance = NotificationManager.IMPORTANCE_LOW;
            CharSequence name = "notice";
            NotificationChannel mChannel = new NotificationChannel(id, name, importance);
            mChannel.enableLights(true);
            mChannel.setDescription("just show notice");
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.GREEN);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mNotifyMgr.createNotificationChannel(mChannel);
            Notification.Builder builder = new Notification.Builder(context, id);
            builder.setAutoCancel(true)
                    .setContentIntent(pi)
                    .setContentTitle(obj.getPush_title())
                    .setContentText(obj.getPush_content())
                    .setOngoing(false)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setWhen(System.currentTimeMillis());
            notification = builder.build();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN &&
                Build.VERSION.SDK_INT <= Build.VERSION_CODES.LOLLIPOP_MR1) {
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

    public static void clearAllNotifyMsg(Context context) {
        try {
            NotificationManager mNotifyMgr =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyMgr.cancelAll();
            SharedPreferences mPreferences = context.getSharedPreferences("SHARE_PREFERENCE_NOTIFICATION", Context.MODE_PRIVATE);
            int max_id = mPreferences.getInt("KEY_MAX_ALARM_ID", 0);
            for (int i = 1; i <= max_id; i++) {
                AlarmTimerUtil.cancelAlarmTimer(context, "TIMER_ACTION", i);
            }
//清除数据
            mPreferences.edit().remove("KEY_MAX_ALARM_ID").commit();
        } catch (Exception e) {
            Log.e(TAG, "取消通知失败", e);
        }
    }
}