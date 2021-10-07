package com.heiheilianzai.app.ui.dialog;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.HomeNotice;
import com.heiheilianzai.app.model.event.NoticeEvent;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class HomeNoticeDialog {
    static PopupWindow popupWindow;
    static int mPosition = 0;

    public static void showDialog(final Activity activity, View v, List<HomeNotice> homeNotice) {
        if (popupWindow != null && popupWindow.isShowing() && homeNotice != null && homeNotice.size() > 0) {
            return;
        }
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_home_notice, null);
        popupWindow = new PopupWindow(view, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ScreenSizeUtils.getInstance(activity).getScreenHeight(), true);

        TextView homeNoticeTitle = view.findViewById(R.id.home_notice_title);
        TextView homeNoticeContent = view.findViewById(R.id.home_notice_content);
        TextView homeNoticePre = view.findViewById(R.id.tx_pre);
        TextView homeNoticeLast = view.findViewById(R.id.tx_last);
        LinearLayout homeNoticeMore = view.findViewById(R.id.ll_more);
        View close = view.findViewById(R.id.home_notice_close);
        if (homeNotice.size() > 1) {
            homeNoticeMore.setVisibility(View.VISIBLE);
        } else {
            homeNoticeMore.setVisibility(View.GONE);
        }
        setPreLastView(activity, homeNotice, mPosition, homeNoticePre, homeNoticeLast);
        setContentView(activity, homeNotice, homeNoticeTitle, homeNoticeContent);
        homeNoticePre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition == 0) {
                    MyToash.Toash(activity, activity.getResources().getString(R.string.string_no_pre));
                } else {
                    mPosition--;
                    setContentView(activity, homeNotice, homeNoticeTitle, homeNoticeContent);
                    setPreLastView(activity, homeNotice, mPosition, homeNoticePre, homeNoticeLast);
                }
            }
        });
        homeNoticeLast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mPosition == homeNotice.size() - 1) {
                    MyToash.Toash(activity, activity.getResources().getString(R.string.string_no_last));
                } else {
                    mPosition++;
                    setContentView(activity, homeNotice, homeNoticeTitle, homeNoticeContent);
                    setPreLastView(activity, homeNotice, mPosition, homeNoticePre, homeNoticeLast);
                }
            }
        });
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

    private static void setContentView(Activity activity, List<HomeNotice> homeNotice, TextView homeNoticeTitle, TextView homeNoticeContent) {
        homeNoticeTitle.setText(homeNotice.get(mPosition).getTitle());
        String text_content = homeNotice.get(mPosition).getText_content();
        String content = homeNotice.get(mPosition).getContent() + "\n";
        SpannableStringBuilder style = new SpannableStringBuilder();
        //设置文字
        style.append(content + text_content);
        //设置部分文字点击事件
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String user_parame_need = homeNotice.get(mPosition).getUser_parame_need();
                String jump_url = homeNotice.get(mPosition).getJump_url();
                if (Utils.isLogin(activity) && TextUtils.equals(user_parame_need, "2") && !jump_url.contains("&uid=")) {
                    jump_url += "&uid=" + Utils.getUID(activity);
                }
                Intent intent = new Intent();
                intent.setClass(activity, WebViewActivity.class);
                intent.putExtra("url", jump_url);
                activity.startActivity(intent);
            }
        };
        homeNoticeContent.scrollTo(0, 0);
        style.setSpan(clickableSpan, content.length(), text_content.length() + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        homeNoticeContent.setText(style);
        //设置部分文字颜色
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#0000FF"));
        style.setSpan(foregroundColorSpan, content.length(), text_content.length() + content.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        //配置给TextView
        homeNoticeContent.setMovementMethod(LinkMovementMethod.getInstance());
        homeNoticeContent.setText(style);

    }

    private static void setPreLastView(Activity activity, List<HomeNotice> homeNotice, int position, TextView homeNoticePre, TextView homeNoticeLast) {
        if (homeNotice.size() > 1) {
            if (position == 0) {
                homeNoticePre.setTextColor(activity.getResources().getColor(R.color.gray));
            } else {
                homeNoticePre.setTextColor(activity.getResources().getColor(R.color.color_ff8350));
            }
            if (position == homeNotice.size() - 1) {
                homeNoticeLast.setTextColor(activity.getResources().getColor(R.color.gray));
            } else {
                homeNoticeLast.setTextColor(activity.getResources().getColor(R.color.color_ff8350));
            }
        }
    }
}