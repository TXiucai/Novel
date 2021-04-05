package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogWakeVip {
    private VipWakeListener vipWakeListener;

    public void setVipWakeListener(VipWakeListener vipWakeListener) {
        this.vipWakeListener = vipWakeListener;
    }

    public Dialog getDialogVipPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip_wake, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        vipHolder.dialog_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });
        vipHolder.dialog_contine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (vipWakeListener != null) {
                    vipWakeListener.vipWakeBack();
                }
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    class VipHolder {
        @BindView(R.id.dialog_continue)
        public TextView dialog_contine;
        @BindView(R.id.img_close)
        public ImageView dialog_close;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface VipWakeListener {
        void vipWakeBack();
    }
}
