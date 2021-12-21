package com.heiheilianzai.app.utils;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.WindowManager;

/**
 * Created by scb on 2018/8/9.
 */
public class ScreenSizeUtils {
    private static ScreenSizeUtils instance = null;
    private int screenWidth, screenHeight, screenWidthDP, screenHeightDP;

    public static ScreenSizeUtils getInstance(Context mContext) {
        if (instance == null) {
            synchronized (ScreenSizeUtils.class) {
                if (instance == null)
                    instance = new ScreenSizeUtils(mContext);
            }
        }
        return instance;
    }

    private ScreenSizeUtils(Context mContext) {
        WindowManager manager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        manager.getDefaultDisplay().getMetrics(dm);
        screenWidth = dm.widthPixels;// 获取屏幕分辨率宽度
        screenHeight = dm.heightPixels;// 获取屏幕分辨率高度
        screenWidthDP = ImageUtil.dp2px(mContext, screenHeightDP);// 获取屏幕分辨率宽度
        screenHeightDP = ImageUtil.dp2px(mContext, screenHeight);// 获取屏幕分辨率高度
    }

    //获取屏幕宽度
    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenWidthDP() {
        return screenWidthDP;
    }

    public void setScreenWidthDP(int screenWidthDP) {
        this.screenWidthDP = screenWidthDP;
    }

    public int getScreenHeightDP() {
        return screenHeightDP;
    }

    public void setScreenHeightDP(int screenHeightDP) {
        this.screenHeightDP = screenHeightDP;
    }

    //获取屏幕高度
    public int getScreenHeight() {
        return screenHeight;
    }

    /**
     * 获取手机品牌
     */
    public static String getBrand() {
        return Build.BRAND;
    }

    /**
     * 判断是否是 一加 手机
     *
     * @return
     */
    public static boolean isOnePlus() {
        return ("oneplus").equalsIgnoreCase(getBrand().replace(" ", ""));
    }
}
