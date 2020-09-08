package com.heiheilianzai.app.utils;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.text.DecimalFormat;

public class ViewUtils {

    /**
     * 获取View透明度
     */
    public static float getViewAlpha(View v) {
        DecimalFormat df = new DecimalFormat("0.0");
        String str = df.format(v.getAlpha());
        if (!StringUtils.isEmpty(str) && str.contains(",")) {
            str = str.replace(",", ".");
        }
        return Float.valueOf(str);
    }

    /**
     * 获取Drawable透明度
     */
    public static float getViewAlpha(Drawable drawable) {
        DecimalFormat df = new DecimalFormat("0.0");
        String str = df.format(drawable.getAlpha());
        if (!StringUtils.isEmpty(str) && str.contains(",")) {
            str = str.replace(",", ".");
        }
        return Float.valueOf(str);
    }

    /**
     * 设置view显示状态
     */
    public static void setVisibility(View v, int visibility) {
        if (v != null) {
            v.setVisibility(visibility);
        }
    }
}
