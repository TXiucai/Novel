package com.heiheilianzai.app.localPush;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Map;

/**
 * 闹钟定时工具类
 */

public class AlarmTimerUtil {
    /**
     * 设置定时闹钟
     *
     * @param context
     * @param alarmId
     * @param map     要传递的参数
     */
    public static void setAlarmTimer(Context context, int alarmId, long time, Map<String, String> map) {
        Intent myIntent = new Intent(context, AlarmNotifyReceive.class);
        myIntent.setAction("alarm_local_notify");
        if (map != null) {
            for (String key : map.keySet()) {
                myIntent.putExtra(key, map.get(key));
            }
        }
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, myIntent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { //MARSHMALLOW OR ABOVE
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time, sender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) { //LOLLIPOP 21 OR ABOVE
            AlarmManager.AlarmClockInfo alarmClockInfo = new AlarmManager.AlarmClockInfo(time, sender);
            alarmManager.setAlarmClock(alarmClockInfo, sender);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) { //KITKAT 19 OR ABOVE
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, sender);
        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, time, sender);
        }
    }

    /**
     * 取消闹钟
     *
     * @param context
     */
    public static void cancelAlarmTimer(Context context, int alarmId) {
        Intent myIntent = new Intent(context, AlarmNotifyReceive.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, alarmId, myIntent, 0);
        AlarmManager alarm = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(sender);
    }
}