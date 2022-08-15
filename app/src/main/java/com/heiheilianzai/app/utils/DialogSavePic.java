package com.heiheilianzai.app.utils;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.ui.activity.BindPhoneActivity;
import com.heiheilianzai.app.ui.activity.UserInfoActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogSavePic {

    private boolean isFinish = false;

    public void setFinish(boolean finish) {
        isFinish = finish;
    }

    public Dialog getDialogLoginPop(Activity activity, String name, String password) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_save_pic, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        vipHolder.mTxName.setText(name);
        vipHolder.mTxNamePic.setText(name);
        vipHolder.mTxPassword.setText(password);
        vipHolder.mTxPasswordPic.setText(password);
        if (!BuildConfig.free_charge) {
            vipHolder.mTxTitle.setText(activity.getString(R.string.string_heihei_acount));
        } else {
            vipHolder.mTxTitle.setText(activity.getString(R.string.string_jk_acount));
        }

        vipHolder.mTxRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                vipHolder.mTxRegister.setClickable(false);
                vipHolder.mTxRegister.setBackground(activity.getDrawable(R.drawable.shape_e6e6e6_20));
                saveImage(activity, vipHolder.mLlPic);
            }
        });
        vipHolder.mTxLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                Intent intent = new Intent();
                intent.setClass(activity, BindPhoneActivity.class);
                activity.startActivity(intent);
            }
        });
        vipHolder.mVClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (isFinish) {
                    activity.finish();
                }
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    private void saveImage(Activity activity, LinearLayout mLlPic) {
        String name = "";
        if (BuildConfig.free_charge) {
            name = "heiheilianzai" + activity.getString(R.string.pic_name);
        } else {
            name = "jk" + activity.getString(R.string.pic_name);
        }
        FileManager.saveBitmap(activity, ImageUtil.viewToBitmap(mLlPic), name);
        MyToash.ToashSuccess(activity, "您的账号信息已保存至本地相册！");
    }


    class VipHolder {
        @BindView(R.id.tx_title)
        public TextView mTxTitle;
        @BindView(R.id.tx_save_title)
        public TextView mTxTitlePic;
        @BindView(R.id.tx_name)
        public TextView mTxName;
        @BindView(R.id.tx_password)
        public TextView mTxPassword;
        @BindView(R.id.tx_save_name)
        public TextView mTxNamePic;
        @BindView(R.id.tx_save_password)
        public TextView mTxPasswordPic;
        @BindView(R.id.tx_register)
        public TextView mTxRegister;
        @BindView(R.id.tx_login)
        public TextView mTxLogin;
        @BindView(R.id.home_notice_close)
        public View mVClose;
        @BindView(R.id.ll_pic)
        public LinearLayout mLlPic;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
