package com.heiheilianzai.app.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import org.greenrobot.eventbus.EventBus;

public class HomeNoticePhotoDialog {
    static PopupWindow popupWindow;

    public static void showDialog(final Activity activity, View v, HomeNotice homeNotice) {
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_home_notice2, null);
        popupWindow = new PopupWindow(view, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ScreenSizeUtils.getInstance(activity).getScreenHeight(), true);

        ImageView homeNoticePhoto = view.findViewById(R.id.home_notice_photo);
        View close = view.findViewById(R.id.home_notice_close);
        MyPicasso.GlideImageNoSize(activity, homeNotice.getImg_content(), homeNoticePhoto);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NoticeEvent.DialogEvent dialogEvent = new NoticeEvent.DialogEvent();
                dialogEvent.action = NoticeEvent.ACTION_DIALOG_HOME;
                EventBus.getDefault().post(dialogEvent);
                popupWindow.dismiss();
            }
        });
        homeNoticePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(activity, WebViewActivity.class);
                intent.putExtra("url", homeNotice.getJump_url());
                activity.startActivity(intent);
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}