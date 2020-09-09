package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.RabbitConfig;


/**
 *自定义toash与log
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

    /**
     *加载更多提示显示，及判断参数赋值
     * @param context
     * @param isLoadingData (是否有数据正在加载)默认 false
     * @param isLoadOver
     * @param listener
     */
    public static void toashSuccessLoadMore(Context context, boolean isLoadingData, boolean isLoadOver, MyToashLoadMoreListener listener) {
        if (!isLoadingData) {
            isLoadingData = true;
            isLoadOver = false;
            listener.onLoadingData();
            MyToash.ToashSuccess(context, context.getString(R.string.load_more));
        } else {
            if (!isLoadOver) {
                isLoadOver = true;
                MyToash.ToashSuccess(context, context.getString(R.string.load_over));
            }
        }
        listener.onState(isLoadingData, isLoadOver);
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

    /**
     * 加载更多提示回调
     */
    public interface MyToashLoadMoreListener {
        void onLoadingData();

        void onState(boolean isLoadingData, boolean isLoadOver);
    }
}
