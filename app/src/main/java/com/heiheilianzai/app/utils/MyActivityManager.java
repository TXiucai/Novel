package com.heiheilianzai.app.utils;

import android.app.Activity;

import java.lang.ref.WeakReference;

/**
 * 管理Activity 获取当前正在栈顶的Activity
 * 配合 {@link com.heiheilianzai.app.config.ReaderApplication#initGlobeActivity } 使用
 */
public class MyActivityManager {
    private static MyActivityManager sInstance = new MyActivityManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;

    private MyActivityManager() {
    }

    public static MyActivityManager getInstance() {
        return sInstance;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<Activity>(activity);
    }
}
