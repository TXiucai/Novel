package com.heiheilianzai.app.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.observe.SMSContentObserver;
import com.heiheilianzai.app.presenter.LoginPresenter;
import com.heiheilianzai.app.presenter.LoginView;
import com.heiheilianzai.app.ui.activity.CountryActivity;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.MyToash;

import butterknife.BindView;
import butterknife.OnClick;

public class PhoneLoginFragment extends BaseButterKnifeFragment implements LoginView {
    @BindView(R.id.activity_login_phone_username)
    EditText activity_login_phone_username;
    @BindView(R.id.activity_login_phone_message)
    EditText activity_login_phone_message;
    @BindView(R.id.activity_login_phone_get_message_btn)
    Button activity_login_phone_get_message_btn;
    @BindView(R.id.activity_login_phone_btn)
    Button activity_login_phone_btn;
    @BindView(R.id.activity_login_phone_clear)
    ImageView activity_login_phone_clear;
    @BindView(R.id.tx_code)
    TextView mTxCode;
    private int mCode = 86;
    private final static int REQUESTCODE = 1; // 返回的结果码
    private SMSContentObserver smsContentObserver;
    private LoginPresenter mPresenter;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    String outbox = smsContentObserver.getStrContent();//(String) msg.obj;
                    activity_login_phone_message.setText(outbox);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public int initContentView() {
        return R.layout.activity_login_phone;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        requestReadPhoneState();
        activity_login_phone_get_message_btn.setEnabled(false);
        activity_login_phone_btn.setEnabled(false);
        activity_login_phone_message.setEnabled(false);
        activity_login_phone_username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    activity_login_phone_get_message_btn.setEnabled(true);
                    activity_login_phone_get_message_btn.setTextColor(Color.parseColor("#E7554F"));
                    activity_login_phone_message.setEnabled(true);
                    activity_login_phone_clear.setVisibility(View.VISIBLE);
                    if (!TextUtils.isEmpty(activity_login_phone_message.getText().toString())) {
                        activity_login_phone_btn.setEnabled(true);
                        activity_login_phone_btn.setBackgroundResource(R.drawable.shape_login_enable_bg);
                        activity_login_phone_btn.setTextColor(Color.WHITE);
                    } else {
                        activity_login_phone_btn.setEnabled(false);
                        activity_login_phone_btn.setBackgroundResource(R.drawable.shape_login_bg);
                        activity_login_phone_btn.setTextColor(Color.GRAY);
                    }
                } else {
                    activity_login_phone_get_message_btn.setEnabled(false);
                    activity_login_phone_get_message_btn.setTextColor(Color.parseColor("#D3D3D3"));
                    activity_login_phone_message.setEnabled(false);
                    activity_login_phone_clear.setVisibility(View.GONE);
                    activity_login_phone_btn.setEnabled(false);
                    activity_login_phone_btn.setBackgroundResource(R.drawable.shape_login_bg);
                    activity_login_phone_btn.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        activity_login_phone_message.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!TextUtils.isEmpty(s)) {
                    activity_login_phone_btn.setEnabled(true);
                    activity_login_phone_btn.setBackgroundResource(R.drawable.shape_login_enable_bg);
                    activity_login_phone_btn.setTextColor(Color.WHITE);
                } else {
                    activity_login_phone_btn.setEnabled(false);
                    activity_login_phone_btn.setBackgroundResource(R.drawable.shape_login_bg);
                    activity_login_phone_btn.setTextColor(Color.GRAY);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        activity_login_phone_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    activity_login_phone_clear.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(activity_login_phone_username.getText().toString())) {
                        activity_login_phone_clear.setVisibility(View.VISIBLE);
                    } else {
                        activity_login_phone_clear.setVisibility(View.GONE);
                    }
                }
            }
        });
        mPresenter = new LoginPresenter(this, activity);
    }

    @OnClick(value = {R.id.activity_login_phone_get_message_btn, R.id.activity_login_phone_btn,
            R.id.activity_login_phone_clear, R.id.tx_code})
    public void getEvent(View view) {
        switch (view.getId()) {

            case R.id.activity_login_phone_get_message_btn:
                mPresenter.getMessage();
                break;
            case R.id.activity_login_phone_btn:
                mPresenter.loginPhone(new LoginActivity.LoginSuccess() {
                    @Override
                    public void success() {
                    }

                    @Override
                    public void cancle() {
                    }
                });
                break;
            case R.id.activity_login_phone_clear:
                activity_login_phone_username.setText("");
                break;

            case R.id.tx_code:
                PhoneLoginFragment.this.startActivityForResult(new Intent(activity, CountryActivity.class), 1);
                break;
        }
    }

    @Override
    public void onDestroyView() {
        mPresenter.cancelCountDown();
        if (activity.getContentResolver() != null && smsContentObserver != null) {
            activity.getContentResolver().unregisterContentObserver(smsContentObserver);
            smsContentObserver = null;
        }
        super.onDestroyView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && resultCode == 2) {
            mCode = data.getExtras().getInt("code", 0);
            mTxCode.setText("+" + mCode);
        }
    }

    private void requestReadPhoneState() {
        if (PermissionsUtil.hasPermission(activity, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS)) {
            initRegisterContentObserver();
        } else {
            PermissionsUtil.requestPermission(activity, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    initRegisterContentObserver();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    MyToash.Toash(activity, getString(R.string.no_permission));
                }
            }, new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, true, new PermissionsUtil.TipInfo(activity.getString(R.string.splashactivity_permissions_t), activity.getString(R.string.splashactivity_permissions_c1) + activity.getString(R.string.app_name) + activity.getString(R.string.login_permissions_c2), activity.getString(R.string.splashactivity_permissions_cancle), activity.getString(R.string.splashactivity_permissions_set)));
        }
    }

    private void initRegisterContentObserver() {
        smsContentObserver = new SMSContentObserver(activity, mHandler);
        Uri smsUri = Uri.parse("content://sms");
        activity.getContentResolver().registerContentObserver(smsUri, true, smsContentObserver);
    }

    @Override
    public String getUserName() {
        return "";
    }

    @Override
    public String getPassword() {
        return "";
    }

    @Override
    public String getPhoneNum() {
        return activity_login_phone_username.getText().toString();
    }

    @Override
    public String getMessage() {
        return activity_login_phone_message.getText().toString();
    }

    @Override
    public View getButtonView() {
        return activity_login_phone_get_message_btn;
    }

    @Override
    public boolean getBoyinLogin() {
        return false;
    }

    @Override
    public int getCountryCode() {
        return mCode;
    }
}
