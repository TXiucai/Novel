package com.heiheilianzai.app.ui.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.presenter.LoginPresenter;
import com.heiheilianzai.app.presenter.LoginView;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.MyToash;

import butterknife.BindView;

public class NickNameLoginFragment extends BaseButterKnifeFragment implements LoginView {
    @BindView(R.id.activity_login_phone_username)
    EditText mEtNickName;
    @BindView(R.id.activity_login_nick_password)
    EditText mEtPassword;
    @BindView(R.id.activity_login_nick_password_eye)
    ImageView mImgEye;
    @BindView(R.id.activity_login_phone_btn)
    Button mBtLogin;
    @BindView(R.id.activity_login_phone_clear)
    ImageView mImgClear;
    boolean mIsEye = true;
    private LoginPresenter mPresenter;

    @Override
    public int initContentView() {
        return R.layout.activity_login_nick;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPresenter = new LoginPresenter(this, activity);
        mImgEye.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsEye) {
                    mEtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mImgEye.setImageDrawable(activity.getResources().getDrawable(R.mipmap.login_password_eye_off));
                    mIsEye = false;
                } else {
                    mEtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    mImgEye.setImageDrawable(activity.getResources().getDrawable(R.mipmap.login_password_eye));
                    mIsEye = true;
                }
            }
        });
        String name = "[\\u4e00-\\u9fa5_a-zA-Z0-9_]{2,20}";
        String password = "[a-zA-Z\\d]{4,20}";

        mEtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!TextUtils.isEmpty(s)) {
                    mImgClear.setVisibility(View.VISIBLE);
                } else {
                    mImgClear.setVisibility(View.GONE);
                }
                if (s.toString().matches(name) && mEtPassword.getText().toString() != null && mEtPassword.getText().toString().matches(password)) {
                    mBtLogin.setEnabled(true);
                    mBtLogin.setBackgroundResource(R.drawable.shape_login_enable_bg);
                    mBtLogin.setTextColor(Color.WHITE);
                }
            }
        });
        mEtNickName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mImgClear.setVisibility(View.GONE);
                } else {
                    if (!TextUtils.isEmpty(mEtNickName.getText().toString())) {
                        if (!mEtNickName.getText().toString().matches(name)) {
                            MyToash.Toash(activity, activity.getResources().getString(R.string.string_register_error));
                        }
                        mImgClear.setVisibility(View.VISIBLE);
                    } else {
                        mImgClear.setVisibility(View.GONE);
                    }
                }
            }
        });
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(password) && mEtNickName.getText().toString() != null && mEtNickName.getText().toString().matches(name)) {
                    mBtLogin.setEnabled(true);
                    mBtLogin.setBackgroundResource(R.drawable.shape_login_enable_bg);
                    mBtLogin.setTextColor(Color.WHITE);
                }
            }
        });
        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!mEtPassword.getText().toString().matches(password)) {
                        MyToash.Toash(activity, activity.getResources().getString(R.string.string_password_error));
                    }
                }
            }
        });
        mBtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEtPassword.getText().toString().matches(password)) {
                    MyToash.Toash(activity, activity.getResources().getString(R.string.string_password_error));
                    return;
                }
                mPresenter.loginUser(new LoginActivity.LoginSuccess() {
                    @Override
                    public void success() {

                    }

                    @Override
                    public void cancle() {

                    }
                });
            }
        });
    }

    @Override
    public String getUserName() {
        return mEtNickName.getText().toString();
    }

    @Override
    public String getPassword() {
        return mEtPassword.getText().toString();
    }

    @Override
    public String getPhoneNum() {
        return null;
    }

    @Override
    public String getMessage() {
        return null;
    }

    @Override
    public View getButtonView() {
        return null;
    }

    @Override
    public boolean getBoyinLogin() {
        return false;
    }

    @Override
    public int getCountryCode() {
        return 0;
    }
}
