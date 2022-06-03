package com.heiheilianzai.app.view.video;

import android.text.TextUtils;

import java.util.HashMap;
import java.util.Map;

public class VideoPlayCompletedRecorder {
    private static VideoPlayCompletedRecorder sInstance;

    private Map<String, Boolean> mAlreadyCompleted = new HashMap();

    private VideoPlayCompletedRecorder() {
    }

    public static VideoPlayCompletedRecorder getInstance() {
        if (sInstance == null) {
            sInstance = new VideoPlayCompletedRecorder();
        }

        return sInstance;
    }

    public void record(String key) {
        synchronized (mAlreadyCompleted) {
            if (!TextUtils.isEmpty(key)) {
                mAlreadyCompleted.put(key, true);
            }
        }
    }

    public boolean isAlreadyCompleted(String key) {
        boolean result = true;
        synchronized (mAlreadyCompleted) {
            if (!TextUtils.isEmpty(key)) {
                result = mAlreadyCompleted.get(key) == null ? false : true;
            }
        }
        return result;
    }
}
