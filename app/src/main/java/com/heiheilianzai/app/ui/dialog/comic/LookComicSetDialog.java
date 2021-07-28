package com.heiheilianzai.app.ui.dialog.comic;

import android.app.Activity;
import android.app.Dialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.BrightnessUtil;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.zcw.togglebutton.ToggleButton;

public class LookComicSetDialog {
    public static OnBackSmallTypeListener mOnBackSmallTypeListener;

    public static void setmOnBackSmallTypeListener(OnBackSmallTypeListener smallTypeListener) {
        mOnBackSmallTypeListener = smallTypeListener;
    }

    public static void getLookComicSetDialog(Activity activity) {
        Dialog bottomDialog = new Dialog(activity, R.style.BottomDialog);
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_lookcomicset, null);
        ToggleButton dialog_lookcomicset_fanye_ToggleButton = view.findViewById(R.id.dialog_lookcomicset_fanye_ToggleButton);
        ToggleButton dialog_lookcomicset_yejian_ToggleButton = view.findViewById(R.id.dialog_lookcomicset_yejian_ToggleButton);
        ToggleButton dialog_lookcomicset_small_ToggleButton = view.findViewById(R.id.dialog_lookcomicset_small_ToggleButton);
        ToggleButton dialog_lookcomicset_open_ToggleButton = view.findViewById(R.id.dialog_lookcomicset_open_ToggleButton);
        if (AppPrefs.getSharedBoolean(activity, "comicOpen_ToggleButton", false)) {
            dialog_lookcomicset_open_ToggleButton.setToggleOn();
        } else {
            dialog_lookcomicset_open_ToggleButton.setToggleOff();
        }
        if (AppPrefs.getSharedBoolean(activity, "fanye_ToggleButton", true)) {
            dialog_lookcomicset_fanye_ToggleButton.setToggleOn();
        } else {
            dialog_lookcomicset_fanye_ToggleButton.setToggleOff();
        }
        if (AppPrefs.getSharedBoolean(activity, "yejian_ToggleButton", false)) {
            dialog_lookcomicset_yejian_ToggleButton.setToggleOn();
        } else {
            dialog_lookcomicset_yejian_ToggleButton.setToggleOff();
        }
        if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
            dialog_lookcomicset_small_ToggleButton.setToggleOn();
        } else {
            dialog_lookcomicset_small_ToggleButton.setToggleOff();
        }
        dialog_lookcomicset_open_ToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(activity, "comicOpen_ToggleButton", on);
            }
        });
        dialog_lookcomicset_small_ToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(activity, "small_ToggleButton", on);
                if (mOnBackSmallTypeListener != null) {
                    mOnBackSmallTypeListener.backSmallType(on);
                }
            }
        });
        dialog_lookcomicset_fanye_ToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(activity, "fanye_ToggleButton", on);
            }
        });
        dialog_lookcomicset_yejian_ToggleButton.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(activity, "yejian_ToggleButton", on);
                if (on) {
                    BrightnessUtil.setBrightness(activity, 0);
                } else {
                    BrightnessUtil.setBrightness(activity, 255);
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

    public interface OnBackSmallTypeListener {
        void backSmallType(boolean isOn);
    }

}
