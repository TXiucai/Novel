package com.heiheilianzai.app.localPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AlarmNotifyReceive extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            String str = intent.getStringExtra("KEY_NOTIFY");
            if (str == null || str.trim().length() == 0)
                return;
            try {
                LoaclPushBean obj = LoaclPushBean.from(str);
                List<LoaclPushBean> loaclPushBeans = new ArrayList<>();
                NotificationUtil.notifyByAlarmByReceiver(context, obj);
                //闹钟响起设置明天闹钟
//                obj.setStart_time("");
//                NotificationUtil.notifyByAlarm(context, loaclPushBeans);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}