package com.heiheilianzai.app.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String secToTime(int time) {
        String timeStr = null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        if (time <= 0)
            return "00:00:00";
        else {
            minute = time / 60;
            if (minute < 60) {
                second = time % 60;
                timeStr = "00:" + unitFormat(minute) + ":" + unitFormat(second);
            } else {
                hour = minute / 60;
                minute = minute % 60;
                second = time - hour * 3600 - minute * 60;
                timeStr = unitFormat(hour) + ":" + unitFormat(minute) + ":" + unitFormat(second);
            }
        }
        return timeStr;
    }

    public static String unitFormat(int i) {
        String retStr = null;
        if (i >= 0 && i < 10)
            retStr = "0" + Integer.toString(i);
        else
            retStr = "" + i;
        return retStr;
    }

    /**
     * 获取添加后缀的当前时间
     *
     * @param flavour 自定义后缀
     * @return
     */
    public static String getFlavourTodayTime(String flavour) {
        return new StringBuffer().append(getTodayTime()).append(flavour).toString();
    }

    /**
     * 获取当天时间
     *
     * @return
     */
    public static String getTodayTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    /**
     * 获取系统时间戳
     *
     * @return 毫秒级
     */
    public static long currentTime() {
        return System.currentTimeMillis();
    }

    /**
     * 获取时间戳与当前时间差
     *
     * @param timeMillis 毫秒级
     * @return 毫秒级
     */
    public static long getCurrentTimeDifference(long timeMillis) {
        return currentTime() - timeMillis;
    }

    /**
     * 获取时间戳与当前时间差 返回秒级
     *
     * @param timeMillis 毫秒级
     * @return 秒级
     */
    public static long getCurrentTimeDifferenceSecond(long timeMillis) {
        return getCurrentTimeDifference(timeMillis) / 1000;
    }
}
