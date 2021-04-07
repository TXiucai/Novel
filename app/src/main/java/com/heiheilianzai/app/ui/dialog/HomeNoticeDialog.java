package com.heiheilianzai.app.ui.dialog;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import org.greenrobot.eventbus.EventBus;

public class HomeNoticeDialog {
    static PopupWindow popupWindow;

    public static void showDialog(final Activity activity, View v, HomeNotice homeNotice) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_home_notice, null);
        popupWindow = new PopupWindow(view, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ScreenSizeUtils.getInstance(activity).getScreenHeight(), true);

        TextView homeNoticeTitle = view.findViewById(R.id.home_notice_title);
        TextView homeNoticeContent = view.findViewById(R.id.home_notice_content);
        View close = view.findViewById(R.id.home_notice_close);
        homeNoticeTitle.setText(homeNotice.getTitle());
        homeNoticeContent.setText(homeNotice.getContent());
        close.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                NoticeEvent.DialogEvent dialogEvent = new NoticeEvent.DialogEvent();
                dialogEvent.action = NoticeEvent.ACTION_DIALOG_HOME;
                EventBus.getDefault().post(dialogEvent);
                popupWindow.dismiss();
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}