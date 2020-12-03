package com.heiheilianzai.app.presenter;

import android.app.Activity;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.callback.LoginResultCallback;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.BindPhoneActivity;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import org.greenrobot.eventbus.EventBus;

import static com.heiheilianzai.app.constant.ReaderConfig.GETPRODUCT_TYPE;


/**
 * 绑定手机号
 * Created by scb on 2018/8/10.
 */
public class BindPresenter {
    private LoginModel mLoginModel;
    private LoginView mLoginView;

    public BindPresenter(LoginView loginView) {
        mLoginView = loginView;
        mLoginModel = new LoginModel((BindPhoneActivity) mLoginView);
    }

    public void getMessage() {


        mLoginModel.countDown(mLoginView.getButtonView());

        mLoginModel.getMessage(mLoginView.getPhoneNum(), mLoginView.getCountryCode(), new LoginResultCallback() {
            @Override
            public void getResult(final String jsonStr) {
                final Activity activity = (Activity) mLoginView;
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.LoginActivity_getcodeing));
            }
        });
    }

    public void bindPhone() {
        mLoginModel.bindPhone(mLoginView.getPhoneNum(), mLoginView.getMessage(), new LoginResultCallback() {
            @Override
            public void getResult(final String jsonStr) {
                EventBus.getDefault().post(new RefreshMine(null));
                if (GETPRODUCT_TYPE((Activity) mLoginView) != 2) {
                    EventBus.getDefault().post(new RefreshBookSelf(null));
                }
                if (GETPRODUCT_TYPE((Activity) mLoginView) != 1) {
                    EventBus.getDefault().post(new RefreshComic(null));
                }
                ((Activity) mLoginView).finish();
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
