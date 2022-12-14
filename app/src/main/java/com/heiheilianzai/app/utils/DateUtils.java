package com.heiheilianzai.app.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
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

    public static String getStringToDate(String time) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd");
            String format = simpleDateFormat.format(new Date(Long.valueOf(time)));
            return format;
        } catch (Exception e) {
        }
        return "";
    }

    /**
     * 获取当天时间
     *
     * @return
     */
    public static String getTodayTime() {
        return getTodayTime("yyyy-MM-dd");
    }

    public static String getTodayTimeHMS() {
        return getTodayTime("yyyy-MM-dd HH:mm:ss");
    }

    public static String getTodayTimeHM() {
        return getTodayTime("yyyy-MM-dd HH:mm");
    }

    public static String getTodayTime(String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(System.currentTimeMillis());
        return simpleDateFormat.format(date);
    }

    public static String timeStampToDate(long time, String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = new Date(time);
        return simpleDateFormat.format(date);
    }

    public static long dateToTime(String date) {
        return (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")).parse(date, new ParsePosition(0)).getTime();
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


    public static String formatTime(Object timeTemp) {
        int timeParam = 0;
        if (timeTemp instanceof Integer) {
            timeParam = (Integer) timeTemp;
        }
        if (timeTemp instanceof String) {
            timeParam = Integer.valueOf((String) timeTemp);
        }

        int second = timeParam % 60;
        int minuteTemp = timeParam / 60;
        if (minuteTemp > 0) {
            int minute = minuteTemp % 60;
            int hour = minuteTemp / 60;
            if (hour > 0) {
                return (hour >= 10 ? (hour + "") : ("0" + hour)) + ":" + (minute >= 10 ? (minute + "") : ("0" + minute))
                        + ":" + (second >= 10 ? (second + "") : ("0" + second));
            } else {
                return (minute >= 10 ? (minute + "") : ("0" + minute)) + ":"
                        + (second >= 10 ? (second + "") : ("0" + second));
            }
        } else {
            return "00:" + (second >= 10 ? (second + "") : ("0" + second));
        }
    }

    /**
     * 两个时间相差距离多少天多少小时多少分多少秒
     *
     * @return String 返回值为：xx天xx小时xx分xx秒
     */
    public static String getDistanceTime(String starttime, String endtime) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date one;
        Date two;
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;
        try {
            one = df.parse(starttime);
            two = df.parse(endtime);
            long time1 = one.getTime();
            long time2 = two.getTime();
            long diff;
            if (time1 < time2) {
                diff = time2 - time1;
            } else {
                diff = time1 - time2;
            }
            day = diff / (24 * 60 * 60 * 1000);
            hour = (diff / (60 * 60 * 1000) - day * 24);
            min = ((diff / (60 * 1000)) - day * 24 * 60 - hour * 60);
            sec = diff / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return day + "天" + hour + "小时" + min + "分" + sec + "秒";
    }
}
