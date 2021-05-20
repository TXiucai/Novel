package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.PrefConst;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogBecomeVip {

    public Dialog getDialogVipPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip_becomer, null);
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
        AppPrefs.putSharedBoolean(activity, PrefConst.ORDER, false);
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    class VipHolder {
        @BindView(R.id.dialog_close)
        public ImageView dialog_close;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

}
