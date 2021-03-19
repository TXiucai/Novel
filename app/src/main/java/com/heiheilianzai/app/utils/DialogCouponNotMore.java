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
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogCouponNotMore {

    public Dialog getDialogVipPop(Activity activity, Boolean isExit) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        vipHolder.dialog_no.setText(activity.getResources().getString(R.string.splashactivity_cancle));
        vipHolder.dialog_yes.setText(activity.getResources().getString(R.string.string_accept_coupon));
        vipHolder.dialog_tittle.setText(activity.getResources().getString(R.string.string_accept_coupon_tittle));
        vipHolder.dialog_tittle.setTextSize(16);
        vipHolder.dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (isExit) {
                    activity.finish();
                }
            }
        });
        vipHolder.dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                Intent myIntent = new Intent(activity, TaskCenterActivity.class);
                activity.startActivity(myIntent);
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
        @BindView(R.id.dialog_updateapp_version)
        public TextView dialog_tittle;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
