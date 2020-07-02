package com.heiheilianzai.app.utils;

import android.graphics.drawable.Drawable;
import android.view.View;

import java.text.DecimalFormat;

public class ViewUtils {

    /**
     * 获取View透明度
      * @param v
     * @return
     */
  public static float getViewAlpha(View v){
      DecimalFormat df = new DecimalFormat("0.0");
      return Float.valueOf(df.format(v.getAlpha()));
  }

    public static float getViewAlpha(Drawable drawable){
        DecimalFormat df = new DecimalFormat("0.0");
        return Float.valueOf(df.format(drawable.getAlpha()));
    }

    /**
     * 设置view显示状态
     * @param v
     * @param visibility
     */
    public static void setVisibility(View v,int visibility){
      if(v!=null){
          v.setVisibility(visibility);
      }
    }
}
