package com.heiheilianzai.app.wxapi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.LoginInfo;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.RefreshUserInfo;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.FirstStartActivity;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyShare;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.weixin.view.WXCallbackActivity;

import org.greenrobot.eventbus.EventBus;

import static com.heiheilianzai.app.constant.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.constant.ReaderConfig.syncDevice;
import static com.heiheilianzai.app.utils.AppPrefs.putSharedString;

/**
 * 微信登录相关
 */
public class WXEntryActivity extends WXCallbackActivity implements IWXAPIEventHandler {
    private final int RETURN_MSG_TYPE_LOGIN = 1;
    private final int RETURN_MSG_TYPE_SHARE = 2;
    public IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        if (Build.VERSION.SDK_INT != Build.VERSION_CODES.O) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        setContentView(R.layout.login_result);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hintKeyboard();
        iwxapi = WXAPIFactory.createWXAPI(this, ReaderConfig.WEIXIN_PAY_APPID, true);
        iwxapi.handleIntent(getIntent(), this);
    }

    private void hintKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive() && getCurrentFocus() != null) {
            if (getCurrentFocus().getWindowToken() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    @Override
    public void onReq(BaseReq baseReq) {
    }

    @Override
    public void onResp(BaseResp baseResp) {
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_BAN:
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (RETURN_MSG_TYPE_SHARE == baseResp.getType()) {
                } else {
                }
                break;
            case BaseResp.ErrCode.ERR_OK:
                switch (baseResp.getType()) {
                    case RETURN_MSG_TYPE_LOGIN:
                        String code = ((SendAuth.Resp) baseResp).code;
                        getWeiXinAppUserInfo(this, code);
                        break;
                    case RETURN_MSG_TYPE_SHARE:
                        MyShare.getGold(this);
                        break;
                }
                break;
        }
    }

    //该方法执行umeng登陆的回调的处理
    @Override
    public void a(com.umeng.weixin.umengwx.b b) {
    }

    @Override
    protected void a(Intent intent) {
        super.a(intent);
    }

    //在onResume中处理从微信授权通过以后不会自动跳转的问题，手动结束该页面
    @Override
    protected void onResume() {
        super.onResume();
        finish();
    }

    public void getWeiXinAppUserInfo(final Activity activity, final String code) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("code", code);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + "/user/app-login-wechat", json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        LoginInfo loginInfo = new Gson().fromJson(result, LoginInfo.class);
                        if (loginInfo != null) {
                            putSharedString(activity, ReaderConfig.TOKEN, loginInfo.getUser_token());
                            putSharedString(activity, ReaderConfig.UID, String.valueOf(loginInfo.getUid()));
                            EventBus.getDefault().post(new RefreshMine(loginInfo));
                            EventBus.getDefault().post(new RefreshUserInfo(loginInfo));
                            EventBus.getDefault().post(new BuyLoginSuccessEvent());
                            if (GETPRODUCT_TYPE(activity) != 2) {
                                EventBus.getDefault().post(new RefreshBookSelf(null));
                            }
                            if (GETPRODUCT_TYPE(activity) != 1) {
                                EventBus.getDefault().post(new RefreshComic(null));
                            }
                            syncDevice(activity);
                            FirstStartActivity.save_recommend(activity, new FirstStartActivity.Save_recommend() {
                                @Override
                                public void saveSuccess() {
                                }
                            });
                            if (LoginActivity.activity != null) {
                                LoginActivity.activity.finish();
                            }
                            finish();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
