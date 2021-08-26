package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.push.JPushUtil;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.LoginInfo;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.FirstStartActivity;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.constant.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.constant.ReaderConfig.syncDevice;

public class DialogRegister {
    private RegisterBackListener mRegisterBackListener;
    private String mMath = "[\\u4e00-\\u9fa5_a-zA-Z0-9_]{2,20}";

    public void setmRegisterBackListener(RegisterBackListener mRegisterBackListener) {
        this.mRegisterBackListener = mRegisterBackListener;
    }

    public Dialog getDialogLoginPop(Activity activity) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_registe, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        getName(activity, vipHolder.mEdName, vipHolder.mTxRegister);
        vipHolder.mEdName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(mMath)) {
                    vipHolder.mTxRegister.setClickable(true);
                    vipHolder.mTxRegister.setBackground(activity.getDrawable(R.drawable.shape_ff8350_20));
                }
            }
        });
        vipHolder.mTxRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register(activity, vipHolder.mEdName, vipHolder.mTxRegister, popupWindow);
            }
        });
        vipHolder.mTxLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                MainHttpTask.getInstance().GoLogin(activity);
            }
        });
        vipHolder.mVClose.setOnClickListener(new View.OnClickListener() {
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

    private void register(Activity activity, EditText mEdName, TextView mTxRegister, Dialog popupWindow) {
        mTxRegister.setClickable(false);
        mTxRegister.setBackground(activity.getDrawable(R.drawable.shape_e6e6e6_20));
        String name = mEdName.getText().toString();

        if (name != null && !TextUtils.equals(name, "") && name.matches(mMath)) {
            ReaderParams params = new ReaderParams(activity);
            params.putExtraParams("user_name", name);
            String json = params.generateParamsJson();
            HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.REGISTERFAST, json, true, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                            try {
                                String user_default_password = new JSONObject(result).getString("user_default_password");
                                Gson gson = new Gson();
                                LoginInfo loginInfo = gson.fromJson(result, LoginInfo.class);
                                AppPrefs.putSharedString(activity, PrefConst.USER_INFO_KAY, result);
                                if (loginInfo != null) {
                                    AppPrefs.putSharedString(activity, ReaderConfig.TOKEN, loginInfo.getUser_token());
                                    AppPrefs.putSharedString(activity, ReaderConfig.UID, String.valueOf(loginInfo.getUid()));
                                    AppPrefs.putSharedInt(activity, PrefConst.COUPON, loginInfo.getSilverRemain());
                                    EventBus.getDefault().post(new BuyLoginSuccessEvent());
                                    syncDevice(activity);
                                    FirstStartActivity.save_recommend(activity, new FirstStartActivity.Save_recommend() {
                                        @Override
                                        public void saveSuccess() {
                                        }
                                    });
                                    EventBus.getDefault().post(new RefreshMine(loginInfo));
                                    if (GETPRODUCT_TYPE(activity) != 2) {
                                        EventBus.getDefault().post(new RefreshBookSelf(null));
                                    }
                                    if (GETPRODUCT_TYPE(activity) != 1) {
                                        EventBus.getDefault().post(new RefreshComic(null));
                                    }
                                    SensorsDataHelper.profileSet(DateUtils.getTodayTimeHMS());
                                    JPushUtil.setAlias(activity);
                                }
                                if (mRegisterBackListener != null) {
                                    mRegisterBackListener.onRegisterBack(true);
                                }
                                MyToash.Toash(activity, String.format(activity.getResources().getString(R.string.string_register_success), user_default_password));
                                popupWindow.dismiss();
                            } catch (JSONException e) {

                            }
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                            if (mRegisterBackListener != null) {
                                mRegisterBackListener.onRegisterBack(false);
                            }
                        }
                    }
            );
        } else {
            MyToash.Toash(activity, activity.getResources().getString(R.string.string_register_error));
        }

    }

    private void getName(Activity activity, EditText mEdName, TextView mTxRegister) {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.REGISTERNAME, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            String user_name = new JSONObject(result).getString("user_name");
                            mEdName.setText(user_name);
                            mTxRegister.setClickable(true);
                            mTxRegister.setBackground(activity.getDrawable(R.drawable.shape_ff8350_20));
                        } catch (Exception e) {
                            getName(activity, mEdName, mTxRegister);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    class VipHolder {

        @BindView(R.id.ed_name)
        public EditText mEdName;
        @BindView(R.id.tx_register)
        public TextView mTxRegister;
        @BindView(R.id.tx_login)
        public TextView mTxLogin;
        @BindView(R.id.home_notice_close)
        public View mVClose;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface RegisterBackListener {
        void onRegisterBack(boolean isSuccess);
    }
}
