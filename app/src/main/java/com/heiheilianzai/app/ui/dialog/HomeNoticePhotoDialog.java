package com.heiheilianzai.app.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.Utils;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HomeNoticePhotoDialog {
    static PopupWindow popupWindow;
    static int mPosition = 0;
    static HomeNotice mHomeNotice;

    public static void showDialog(final Activity activity, View v, List<HomeNotice> homeNotices, boolean isSdkAd) {
        if (popupWindow != null && popupWindow.isShowing() && homeNotices != null && homeNotices.size() > 0) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_home_notice2, null);
        popupWindow = new PopupWindow(view, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ScreenSizeUtils.getInstance(activity).getScreenHeight(), true);
        mPosition = 0;
        mHomeNotice = homeNotices.get(mPosition);
        ImageView homeNoticePhoto = view.findViewById(R.id.home_notice_photo);
        View close = view.findViewById(R.id.home_notice_close);
        AdInfo adInfo = BaseSdkAD.newAdInfo(mHomeNotice);
        if (adInfo != null) {
            MyPicasso.glideSdkAd(activity, adInfo, mHomeNotice.getImg_content(), homeNoticePhoto);
        } else {
            MyPicasso.GlideImageNoSize(activity, mHomeNotice.getImg_content(), homeNoticePhoto);
        }
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeNotices.size() > mPosition + 1) {
                    mPosition++;
                    mHomeNotice = homeNotices.get(mPosition);
                    MyPicasso.GlideImageNoSize(activity, mHomeNotice.getImg_content(), homeNoticePhoto);
                } else {
                    NoticeEvent.DialogEvent dialogEvent = new NoticeEvent.DialogEvent();
                    if (isSdkAd) {
                        dialogEvent.action = NoticeEvent.ACTION_DIALOG_HOME_LOCAL;
                    } else {
                        dialogEvent.action = NoticeEvent.ACTION_DIALOG_HOME;
                    }
                    EventBus.getDefault().post(dialogEvent);
                    popupWindow.dismiss();
                }
            }
        });
        homeNoticePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adInfo != null) {
                    XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                }
                String user_parame_need = mHomeNotice.getUser_parame_need();
                String jump_url = mHomeNotice.getJump_url();
                String redirect_type = mHomeNotice.getRedirect_type();
                if (Utils.isLogin(activity) && TextUtils.equals(user_parame_need, "2") && !jump_url.contains("&uid=")) {
                    jump_url += "&uid=" + Utils.getUID(activity);
                }
                if (TextUtils.equals(redirect_type, "1")) {
                    activity.startActivity(new Intent(activity, AboutActivity.class).putExtra("url", jump_url));
                } else {
                    activity.startActivity(new Intent(activity, AboutActivity.class).
                            putExtra("url", jump_url)
                            .putExtra("style", "4"));
                }
            }
        });
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.setOutsideTouchable(false);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
    }
}