package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.presenter.LoginPresenter;
import com.heiheilianzai.app.presenter.LoginView;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.USE_WEIXIN;

/**
 * 用户登录页
 * Created by wudeyan on 2018/7/14.
 */
public class LoginActivity extends BaseActivity implements LoginView {
    @BindView(R.id.activity_login_close)
    LinearLayout activity_login_close;
    @BindView(R.id.activity_login_title)
    TextView activity_login_title;
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
    @BindView(R.id.activity_login_contract)
    TextView activity_login_contract;
    @BindView(R.id.activity_login_weixin)
    LinearLayout activity_login_weixin;
    @BindView(R.id.tx_code)
    TextView mTxCode;

    public static final String BOYIN_LOGIN_KAY = "boyin_login";
    private boolean boyinLogin = false;
    private LoginPresenter mPresenter;
    public static Activity activity;
    public IWXAPI iwxapi;
    private int mCode = 86;
    private final static int REQUESTCODE = 1; // 返回的结果码

    @Override
    public int initContentView() {
        return R.layout.activity_login;
    }

    @OnClick(value = {R.id.activity_login_phone_get_message_btn,
            R.id.activity_login_phone_btn
            , R.id.activity_login_phone_clear
            , R.id.activity_login_contract,
            R.id.activity_login_weixin
            , R.id.activity_login_close,
            R.id.tx_code
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_login_close:
                finish();
                break;
            case R.id.activity_login_phone_get_message_btn:
                mPresenter.getMessage();
                break;
            case R.id.activity_login_phone_btn:
                mPresenter.loginPhone(new LoginSuccess() {
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
            case R.id.activity_login_contract:
                startActivity(new Intent(LoginActivity.this, AboutActivity.class).putExtra("url", ReaderConfig.getBaseUrl() + ReaderConfig.privacy).putExtra("flag", "privacy"));
                break;
            case R.id.activity_login_weixin:
                //微信登录
                weixinLogin(activity, true, null);
                break;
            case R.id.tx_code:
                startActivityForResult(new Intent(LoginActivity.this, CountryActivity.class), 1);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, ReaderConfig.WEIXIN_PAY_APPID, true);
        iwxapi.registerApp(ReaderConfig.WEIXIN_PAY_APPID);
        if (!USE_WEIXIN) {
            activity_login_weixin.setVisibility(View.GONE);
        }
    }

    class ASD {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ASD asd) {
    }

    @Override
    public void initView() {
        activity = this;
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(BOYIN_LOGIN_KAY)) {
            boyinLogin = intent.getBooleanExtra(BOYIN_LOGIN_KAY, false);
        }
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
        mPresenter = new LoginPresenter(this);
    }

    @Override
    public void initData() {
    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
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
        return boyinLogin;
    }

    @Override
    public int getCountryCode() {
        return mCode;
    }

    public interface LoginSuccess {
        void success();

        void cancle();
    }

    public void weixinLogin(Activity activity, final boolean isfinsh, LoginSuccess loginSuccess) {
        UMShareAPI.get(activity).deleteOauth(activity, SHARE_MEDIA.WEIXIN, authListener);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (iwxapi == null) {
                iwxapi = WXAPIFactory.createWXAPI(activity, ReaderConfig.WEIXIN_PAY_APPID, true);
            }
            if (!iwxapi.isWXAppInstalled()) {
                return;
            }
            iwxapi.registerApp(ReaderConfig.WEIXIN_PAY_APPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
            iwxapi.sendReq(req);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
    }

    @Override
    protected void onDestroy() {
        mPresenter.cancelCountDown();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE && requestCode ==2) {
            mCode = data.getExtras().getInt("code", 0);
            mTxCode.setText("+" + mCode);
        }
    }
}
