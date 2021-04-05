package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogErrorVip {
    private VipErrorListener vipErrorListener;

    public void setVipWakeListener(VipErrorListener vipErrorListener) {
        this.vipErrorListener = vipErrorListener;
    }

    public Dialog getDialogVipPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip_error, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        vipHolder.dialog_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (vipErrorListener != null) {
                    vipErrorListener.vipErrorBack();
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
        @BindView(R.id.dialog_again)
        public TextView dialog_again;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface VipErrorListener {
        void vipErrorBack();
    }
}
