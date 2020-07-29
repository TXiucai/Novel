package com.heiheilianzai.app.activity.presenter;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.activity.FirstStartActivity;
import com.heiheilianzai.app.activity.LoginActivity;
import com.heiheilianzai.app.activity.SplashActivity;
import com.heiheilianzai.app.activity.model.LoginModel;
import com.heiheilianzai.app.activity.view.LoginResultCallback;
import com.heiheilianzai.app.activity.view.LoginView;
import com.heiheilianzai.app.bean.LoginInfo;
import com.heiheilianzai.app.comic.eventbus.RefreshComic;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.BuyLoginSuccess;
import com.heiheilianzai.app.eventbus.RefreshBookSelf;
import com.heiheilianzai.app.eventbus.RefreshDiscoveryFragment;
import com.heiheilianzai.app.eventbus.RefreshMine;
import com.heiheilianzai.app.eventbus.RefreshReadHistory;
import com.heiheilianzai.app.fragment.HomeBoYinFragment;
import com.heiheilianzai.app.push.JPushUtil;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

import static com.heiheilianzai.app.config.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.config.ReaderConfig.syncDevice;

/**
 * Created by scb on 2018/7/14.
 */
public class LoginPresenter {
    private LoginModel mLoginModel;
    private LoginView mLoginView;
    Activity activity;

    public LoginPresenter(LoginView loginView) {
        mLoginView = loginView;
        this.activity = (LoginActivity) mLoginView;
        mLoginModel = new LoginModel((LoginActivity) mLoginView);
    }

    public LoginPresenter(LoginView loginView, Activity activity) {
        mLoginView = loginView;
        this.activity = activity;
        mLoginModel = new LoginModel(activity);
    }

    public void getMessage() {
        mLoginModel.countDown(mLoginView.getButtonView());
        mLoginModel.getMessage(mLoginView.getPhoneNum(), new LoginResultCallback() {
            @Override
            public void getResult(final String jsonStr) {
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.LoginActivity_getcodeing));
            }
        });
    }

    public void loginPhone(final LoginActivity.LoginSuccess loginSuccess) {
        mLoginModel.loginPhone(mLoginView.getPhoneNum(), mLoginView.getMessage(), new LoginResultCallback() {
            @Override
            public void getResult(final String loginStr) {
                Gson gson = new Gson();
                LoginInfo loginInfo = gson.fromJson(loginStr, LoginInfo.class);
                if (loginInfo != null) {
                    AppPrefs.putSharedString(activity, ReaderConfig.TOKEN, loginInfo.getUser_token());
                    AppPrefs.putSharedString(activity, ReaderConfig.UID, String.valueOf(loginInfo.getUid()));
                    EventBus.getDefault().post(new BuyLoginSuccess());
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
                    loginSuccess.success();
                    JPushUtil.setAlias(activity);
                    loginBoYin();
                    if (mLoginView.getBoyinLogin()) {
                        loginBoYin();
                    } else {
                        activity.finish();
                    }
                }
            }
        });
    }

    private void loginBoYin() {
        mLoginModel.loginBoYin(mLoginView.getPhoneNum(), new LoginResultCallback() {
            @Override
            public void getResult(String jsonStr) {
                Intent intent = new Intent();
                intent.putExtra(HomeBoYinFragment.BOYIN_TOKEN_RESULT_KAY, jsonStr);
                activity.onActivityReenter(Activity.RESULT_OK, intent);
                activity.finish();
            }
        });
    }

    /**
     * 取消倒计时
     */
    public void cancelCountDown() {
        mLoginModel.cancel();
    }
}
