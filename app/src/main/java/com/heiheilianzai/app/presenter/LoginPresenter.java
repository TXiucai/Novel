package com.heiheilianzai.app.presenter;

import android.app.Activity;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.callback.LoginResultCallback;
import com.heiheilianzai.app.component.push.JPushUtil;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.LoginInfo;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.RegisterLoginWelfareEvent;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.FirstStartActivity;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;

import static com.heiheilianzai.app.constant.ReaderConfig.GETPRODUCT_TYPE;
import static com.heiheilianzai.app.constant.ReaderConfig.syncDevice;

/**
 * 用户登录管理 Presenter
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

    /**
     * 获取手机验证码
     */
    public void getMessage() {
        mLoginModel.countDown(mLoginView.getButtonView());
        mLoginModel.getMessage(mLoginView.getPhoneNum(), mLoginView.getCountryCode(), new LoginResultCallback() {
            @Override
            public void getResult(final String jsonStr) {
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.LoginActivity_getcodeing));
            }
        });
    }
    public void loginUser(final LoginActivity.LoginSuccess loginSuccess){
        mLoginModel.loginUser(mLoginView.getUserName(), mLoginView.getPassword(), new LoginResultCallback() {
            @Override
            public void getResult(String jsonStr) {
                initLoginData(jsonStr, loginSuccess);
            }
        });
    }

    private void initLoginData(String jsonStr, LoginActivity.LoginSuccess loginSuccess) {
        try {
            Gson gson = new Gson();
            LoginInfo loginInfo = gson.fromJson(jsonStr, LoginInfo.class);
            AppPrefs.putSharedString(activity, PrefConst.USER_INFO_KAY, jsonStr);
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
                EventBus.getDefault().post(new RegisterLoginWelfareEvent());
                if (GETPRODUCT_TYPE(activity) != 2) {
                    EventBus.getDefault().post(new RefreshBookSelf(null));
                }
                if (GETPRODUCT_TYPE(activity) != 1) {
                    EventBus.getDefault().post(new RefreshComic(null));
                }
                SensorsDataHelper.profileSet(DateUtils.getTodayTimeHMS());
                loginSuccess.success();
                JPushUtil.setAlias(activity);
                activity.finish();
            }
        } catch (Exception e) {
        }
    }

    /**
     * 登录请求:刷新我的页面;刷新用户数据,及登录有声用户数据;根据配置刷新书架小说、漫画
     */
    public void loginPhone(final LoginActivity.LoginSuccess loginSuccess) {
        mLoginModel.loginPhone(mLoginView.getPhoneNum(), mLoginView.getMessage(),mLoginView.getCountryCode(), new LoginResultCallback() {
            @Override
            public void getResult(final String loginStr) {
                initLoginData(loginStr, loginSuccess);
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