package com.heiheilianzai.app.ui.dialog;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.widget.DatePicker;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * 系统自带 操作提示弹框（确定，取消）、时间选择弹框
 * Created by Administrator on 2018/7/6.
 */
public class GetDialog {
    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public interface IsOperationInterface {
        void isOperation();
    }

    public interface IsNegativeInterface {
        void isNegative();
    }

    public static void IsOperation(final Activity activity, String title, String suretext, final IsOperationInterface isOperationInterface) {
        IsOperationPositiveNegative(activity, title, suretext, isOperationInterface, true, true);
    }

    public static void IsOperationPositive(final Activity activity, String title, String suretext, final IsOperationInterface isOperationInterface) {
        IsOperationPositiveNegative(activity, title, suretext, isOperationInterface, true, false);
    }

    public static void IsOperationPositiveNegative(final Activity activity, String title, String suretext, final IsOperationInterface isOperationInterface, boolean isPositive, boolean isNegative) {
        IsOperationPositiveNegative(activity, title, suretext, null, isOperationInterface, null, isPositive, isNegative);
    }

    public static void IsOperationPositiveNegative(final Activity activity, String title, String suretext, String negativeText, final IsOperationInterface isOperationInterface, final IsNegativeInterface isNegativeInterface, boolean isPositive, boolean isNegative) {
        IsOperationPositiveNegative(activity, title, suretext, negativeText, isOperationInterface, isNegativeInterface, isPositive, isNegative, true);
    }

    public static void IsOperationPositiveNegative(final Activity activity, String title, String suretext, String negativeText, final IsOperationInterface isOperationInterface, final IsNegativeInterface isNegativeInterface, boolean isPositive, boolean isNegative, boolean cancelable) {
        if (activity == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                .setTitle(title)
                .setMessage(suretext);
        if (isPositive) {
            builder.setPositiveButton(LanguageUtil.getString(activity, R.string.public_sure), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    isOperationInterface.isOperation();
                }
            });
        }
        builder.setCancelable(cancelable);
        if (isNegative) {
            String str = StringUtils.isEmpty(negativeText) ? LanguageUtil.getString(activity, R.string.splashactivity_cancle) : negativeText;
            builder.setNegativeButton(str, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    if (isNegativeInterface != null) {
                        isNegativeInterface.isNegative();
                    }
                }
            });
        }
        builder.create().show();
    }

    public interface GetTimeDialogInterface {
        void getTimeDialogInterface(int year, int monthOfYear, int dayOfMonth);
    }

    public static void getTimeDialog(Activity activity, String minTime, Calendar calendar, final GetTimeDialogInterface getTimeDialogInterface) {
        int style = AlertDialog.THEME_HOLO_LIGHT;
        DatePickerDialog datePickerDialog = new DatePickerDialog(activity, style,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        getTimeDialogInterface.getTimeDialogInterface(year, monthOfYear, dayOfMonth);
                    }
                },
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE));
        DatePicker datePicker = datePickerDialog.getDatePicker();
        datePickerDialog.setTitle("选择查询日期");
        try {
            //设置最小日期
            datePicker.setMinDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(minTime).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        } //设置最大日期
        datePicker.setMaxDate(System.currentTimeMillis());
        datePickerDialog.show();
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static boolean isNotificationEnabled(Context context) {
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;
        Class appOpsClass = null;
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE, String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);
            int value = (int) opPostNotificationValue.get(Integer.class);
            return ((int) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
