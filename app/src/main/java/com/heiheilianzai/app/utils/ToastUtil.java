package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.widget.Toast;


/**
 * Toast工具类
 */
public final class ToastUtil {
    private static volatile ToastUtil mToastUtil = null;

    /**
     * 获取实例
     */
    public static ToastUtil getInstance() {
        if (mToastUtil == null) {
            synchronized (ToastUtil.class) {
                mToastUtil = new ToastUtil();
            }
        }
        return mToastUtil;
    }

    /**
     * 显示Toast，多次调用此函数时，Toast显示的时间不会累计，并且显示内容为最后一次调用时传入的内容
     * 持续时间默认为short
     *
     * @param tips 要显示的内容
     *             {@link Toast#LENGTH_LONG}
     */
    public void showShortT(final String tips) {
        showToast(tips, -1, Toast.LENGTH_SHORT);
    }

    public void showShortT(final int tips) {
        showToast(null, tips, Toast.LENGTH_SHORT);
    }

    public void showLongT(final String tips) {
        showToast(tips, -1, Toast.LENGTH_LONG);
    }

    public void showLongT(final int tips) {
        showToast(null, tips, Toast.LENGTH_LONG);
    }

    /**
     * 显示Toast，多次调用此函数时，Toast显示的时间不会累计，并且显示内容为最后一次调用时传入的内容
     *
     * @param tips     要显示的内容
     * @param duration 持续时间，参见{@link Toast#LENGTH_SHORT}和
     *                 {@link Toast#LENGTH_LONG}
     */
    public void showToast(final String tips, final int tipi, final int duration) {
        if (android.text.TextUtils.isEmpty(tips) && tipi <= 0) {
            return;
        }
        Activity activity = BooActivityManager.getInstance().getCurrentActivity();
        if (activity == null) return;
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (android.text.TextUtils.isEmpty(tips)) {
                    Toast.makeText(BooActivityManager.getInstance().getCurrentActivity(), tipi, duration).show();
                } else {
                    Toast.makeText(BooActivityManager.getInstance().getCurrentActivity(), tips, duration).show();
                }
            }
        });
    }
}