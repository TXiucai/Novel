package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.config.RabbitConfig;
import com.heiheilianzai.app.dialog.ZToast;


/**
 * Created by abc on 2017/8/30.
 */
public class MyToash {
    public static void Toash(Context activity, String Message) {
        ZToast.MakeText((Activity) activity, Message, 1500, R.mipmap.tips_success).show();
    }

    public static void ToashSuccess(Context activity, String Message) {
        try {
            ZToast.MakeText((Activity) activity, Message, 1500, R.mipmap.tips_success).show();
        } catch (Exception e) {
        }
    }

    public static void ToashError(Context activity, String Message) {
        ZToast.MakeText((Activity) activity, Message, 1500, R.mipmap.tips_error).show();
    }


    public static void Log(Object Message) {
        if (SUE_LOG) {
            Log.i("heiheilianzai", Message.toString());
        }
    }

    public static void Log(String Message, String message) {
        if (SUE_LOG) {
            Log.i(Message, message);
        }
    }

    public static void LogJson(String Message, String message) {
        if (SUE_LOG) {
            Log.i(Message, message);
        }
    }

    public static void LogE(String Message, String message) {
        if (SUE_LOG) {
            Log.e(Message, message);
        }
    }

    public static void Log(String Message, int message) {
        if (SUE_LOG) {
            Log.i(Message, message + "");
        }
    }

    public static boolean SUE_LOG = !RabbitConfig.ONLINE;
}
