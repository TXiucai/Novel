package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Application;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.KITKAT;

public class BooActivityManager {
    private static BooActivityManager sInstance = new BooActivityManager();
    private WeakReference<Activity> sCurrentActivityWeakRef;
    private Map<String, WeakReference<Activity>> mAllActivity;


    private BooActivityManager() {
        mAllActivity = new HashMap<>();
    }

    public static BooActivityManager getInstance() {
        return sInstance;
    }

    public boolean isActive(String activitySimpleName) {
        if (mAllActivity.get(activitySimpleName) == null) return false;
        Activity activity = mAllActivity.get(activitySimpleName).get();
        return activity != null;
    }

    public Activity getCurrentActivity() {
        Activity currentActivity = null;
        if (sCurrentActivityWeakRef != null) {
            currentActivity = sCurrentActivityWeakRef.get();
        }
        return currentActivity;
    }

    public void setCurrentActivity(Activity activity) {
        sCurrentActivityWeakRef = new WeakReference<>(activity);
        mAllActivity.put(activity.getClass().getSimpleName(), sCurrentActivityWeakRef);
        for (Map.Entry<String, WeakReference<Activity>> entry :
                mAllActivity.entrySet()) {
            WeakReference<Activity> wr = entry.getValue();
            if (wr.get() == null) continue;
        }

    }

    public void removeThisActivity(Activity activity) {
        mAllActivity.remove(activity.getClass().getSimpleName());
    }

    /**
     * 通过Activity的名字干掉这个Activity
     *
     * @param activityName
     */
    public void finishActivityByName(String activityName) {
        WeakReference<Activity> wr = mAllActivity.get(activityName);
        if (wr == null) return;
        Activity activity = wr.get();
        if (
                activity == null
                        || activity.isFinishing()
                        || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && activity.isDestroyed())
        )
            return;
        activity.finish();
    }

    /**
     * 添加对Activity的监听
     */
    public static void addActivityListener(Application app) {

        InputMethodManager inputMethodManager = null;
        Field mServedViewField = null;
        Field mHField = null;
        Method finishInputLockedMethod = null;
        Method focusInMethod = null;
        // Don't know about other versions yet.
        if (SDK_INT > KITKAT && SDK_INT < 22) {
            inputMethodManager =
                    (InputMethodManager) app.getSystemService(INPUT_METHOD_SERVICE);

            try {
                mServedViewField = InputMethodManager.class.getDeclaredField("mServedView");
                mServedViewField.setAccessible(true);
                mHField = InputMethodManager.class.getDeclaredField("mServedView");
                mHField.setAccessible(true);
                finishInputLockedMethod = InputMethodManager.class.getDeclaredMethod("finishInputLocked");
                finishInputLockedMethod.setAccessible(true);
                focusInMethod = InputMethodManager.class.getDeclaredMethod("focusIn", View.class);
                focusInMethod.setAccessible(true);
            } catch (NoSuchMethodException | NoSuchFieldException unexpected) {
                Log.e("IMMLeaks", "Unexpected reflection exception", unexpected);
                return;
            }
        }

        final InputMethodManager finalInputMethodManager = inputMethodManager;
        final Field finalMHField = mHField;
        final Field finalMServedViewField = mServedViewField;
        final Method finalFinishInputLockedMethod = finishInputLockedMethod;
        app.registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                if (SDK_INT > KITKAT && SDK_INT < 22) {
                    IMMLeaks.ReferenceCleaner cleaner =
                            new IMMLeaks.ReferenceCleaner(finalInputMethodManager, finalMHField, finalMServedViewField,
                                    finalFinishInputLockedMethod);
                    View rootView = activity.getWindow().getDecorView().getRootView();
                    ViewTreeObserver viewTreeObserver = rootView.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalFocusChangeListener(cleaner);
                }
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {
                BooActivityManager.getInstance().setCurrentActivity(activity);
            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                BooActivityManager.getInstance().removeThisActivity(activity);
            }
        });
    }

}