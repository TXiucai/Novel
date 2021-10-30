package com.heiheilianzai.app.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

public class ReadScreenSetDialog {
    private OnCheckScreenTimeListener mOnCheckScreenTimeListener;
    private String time;

    public void setmOnCheckScreenTimeListener(OnCheckScreenTimeListener mOnCheckScreenTimeListener) {
        this.mOnCheckScreenTimeListener = mOnCheckScreenTimeListener;
    }

    public void getReadScreenSetDialog(Context activity) {
        Dialog bottomDialog = new Dialog(activity, R.style.BottomDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_read_screen_set, null);
        RadioGroup radioGroup = view.findViewById(R.id.dialog_read_screen_rg);
        RadioButton radioButtonFive = view.findViewById(R.id.dialog_read_screen_rb_five);
        RadioButton radioButtonFivty = view.findViewById(R.id.dialog_read_screen_rb_fivty);
        RadioButton radioButtonHalf = view.findViewById(R.id.dialog_read_screen_rb_half);
        RadioButton radioButtonSystem = view.findViewById(R.id.dialog_read_screen_rb_system);
        String novelTime_screen = AppPrefs.getSharedString(activity, "novelTime_Screen", "0");

        if (TextUtils.equals(novelTime_screen, "0")) {//跟随系统时间
            radioButtonSystem.setChecked(true);
        } else if (TextUtils.equals(novelTime_screen, "5")) {
            radioButtonFive.setChecked(true);
        } else if (TextUtils.equals(novelTime_screen, "15")) {
            radioButtonFivty.setChecked(true);
        } else if (TextUtils.equals(novelTime_screen, "30")) {
            radioButtonHalf.setChecked(true);
        } else {
            radioButtonSystem.setChecked(true);
        }
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.dialog_read_screen_rb_five:
                        AppPrefs.putSharedString(activity, "novelTime_Screen", "5");
                        time = "5";
                        break;
                    case R.id.dialog_read_screen_rb_fivty:
                        AppPrefs.putSharedString(activity, "novelTime_Screen", "15");
                        time = "15";
                        break;
                    case R.id.dialog_read_screen_rb_half:
                        AppPrefs.putSharedString(activity, "novelTime_Screen", "30");
                        time = "30";
                        break;
                    case R.id.dialog_read_screen_rb_system:
                        AppPrefs.putSharedString(activity, "novelTime_Screen", "0");
                        time = "0";
                        break;
                }
                if (mOnCheckScreenTimeListener != null) {
                    mOnCheckScreenTimeListener.onCheckScreenTime(time);
                }
                if (bottomDialog != null && bottomDialog.isShowing()) {
                    bottomDialog.dismiss();
                }
            }
        });

        bottomDialog.setContentView(view);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        view.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        bottomDialog.onWindowFocusChanged(false);
        bottomDialog.setCanceledOnTouchOutside(true);
    }

    public interface OnCheckScreenTimeListener {
        void onCheckScreenTime(String time);
    }
}
