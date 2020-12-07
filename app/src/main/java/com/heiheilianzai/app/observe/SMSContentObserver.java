package com.heiheilianzai.app.observe;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SMSContentObserver extends ContentObserver {
    private static String TAG = "SMSContentObserver";
    private String strContent; //验证码内容
    private Context mContext;
    private Handler mHandler; //更新UI线程

    public SMSContentObserver(Context context, Handler handler) {
        super(handler);
        mContext = context;
        mHandler = handler;
    }

    /**
     * 当所监听的Uri发生改变时，就会回调此方法
     *
     * @param selfChange 此值意义不大 一般情况下该回调值false
     */
    @Override
    public void onChange(boolean selfChange) {
        Uri inboxUri = Uri.parse("content://sms/inbox");
        final String[] projection = new String[]{"address", "body"};
        Cursor c = mContext.getContentResolver().query(inboxUri, projection, "read=?", new String[]{"0"}, "date desc");
        if (c != null) {
            if (c.moveToFirst()) {
                String address = c.getString(c.getColumnIndex("address"));
                String body = c.getString(c.getColumnIndex("body"));
                if (!body.startsWith("【hhlz科技】")){
                    return;
                }
                Pattern pattern = Pattern.compile("(\\d{4,6})");
                Matcher matcher = pattern.matcher(body);
                if (matcher.find()) {
                    strContent = matcher.group(0);
                    mHandler.obtainMessage(1, strContent).sendToTarget();
                }
            }
            c.close();
        }
    }

    public String getStrContent() {
        return strContent;
    }
}