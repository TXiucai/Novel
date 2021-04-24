package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogLogin {

    public Dialog getDialogLoginPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_is_login, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        vipHolder.dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        vipHolder.dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                MainHttpTask.getInstance().Gotologin(activity);
                activity.finish();
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    class VipHolder {

        @BindView(R.id.dialog_no)
        public TextView dialog_no;
        @BindView(R.id.dialog_yes)
        public TextView dialog_yes;
        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
