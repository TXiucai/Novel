package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.heiheilianzai.app.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogVipComfirm {
    private OnRepeatListener mOnRepeatListener;
    private OnOpenKefuListener mOnOpenKefuListener;

    public void setmOnRepeatListener(OnRepeatListener mOnRepeatListener) {
        this.mOnRepeatListener = mOnRepeatListener;
    }

    public void setmOnOpenKefuListener(OnOpenKefuListener mOnOpenKefuListener) {
        this.mOnOpenKefuListener = mOnOpenKefuListener;
    }

    public Dialog getDialogVipPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_vip_comfirm, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.MineNewFragment_lianxikefu));
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vipHolder.dialog_kefu.setText(spannableString);

        SpannableString spannableStringError = new SpannableString(activity.getResources().getString(R.string.vip_error));
        UnderlineSpan underlineSpanError = new UnderlineSpan();
        spannableStringError.setSpan(underlineSpanError, 0, spannableStringError.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vipHolder.dialog_repeat.setText(spannableStringError);

        vipHolder.dialog_kefu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (mOnOpenKefuListener != null) {
                    mOnOpenKefuListener.onOpenKefu();
                }
            }
        });

        vipHolder.dialog_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                if (mOnRepeatListener != null) {
                    mOnRepeatListener.onRepeat();
                }

            }
        });

        vipHolder.dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
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

        @BindView(R.id.dialog_kefu)
        public TextView dialog_kefu;
        @BindView(R.id.dialog_yes)
        public TextView dialog_yes;
        @BindView(R.id.dialog_repeat)
        public TextView dialog_repeat;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnOpenKefuListener {
        void onOpenKefu();
    }

    public interface OnRepeatListener {
        void onRepeat();
    }
}
