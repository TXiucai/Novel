package com.heiheilianzai.app.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by scb on 2019/1/3.
 */

public class ViewToBitmapUtil {
    public static Bitmap convertViewToBitmap(View v, int y) {
        try {
            if (y <= 0) {
                return null;
            }
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);

            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), y, v.getRight(), y + v.getHeight());
            Drawable bgDrawable = v.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(c);
            else
                c.drawColor(Color.WHITE);
            v.draw(c);
        } catch (Error e) {
            Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ALPHA_8);
            Canvas c = new Canvas(b);
            v.layout(v.getLeft(), y, v.getRight(), y + v.getHeight());
            Drawable bgDrawable = v.getBackground();
            if (bgDrawable != null)
                bgDrawable.draw(c);
            else
                c.drawColor(Color.WHITE);
            v.draw(c);
        }
        return null;
    }

    public static Bitmap convertViewToBitmap(View v, int y, int AD_HEIGHT) {
        Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.RGB_565);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), y, v.getRight(), y + AD_HEIGHT);
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }

    public static void getScreenRectOfView(View view, Rect outRect) {
        int pos[] = new int[2];
        view.getLocationOnScreen(pos);
        outRect.set(pos[0], pos[1], pos[0] + view.getWidth(), pos[1] + view.getHeight());
    }
}
