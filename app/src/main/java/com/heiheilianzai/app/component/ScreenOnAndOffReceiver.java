package com.heiheilianzai.app.component;

import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.heiheilianzai.app.R;

public class ScreenOnAndOffReceiver extends DeviceAdminReceiver {

    public ScreenOnAndOffReceiver() {
    }

    private void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onEnabled(Context context, Intent intent) {
        showToast(context, context.getResources().getString(R.string.string_read_equipment_enable));
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        showToast(context, context.getResources().getString(R.string.string_read_equipment_unenable));
    }
}