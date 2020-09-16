package com.heiheilianzai.app.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.sa.SaEventConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.UserInfoItem;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 神策接入工具类
 */
public class SensorsDataHelper {

    /**
     * 设置动态公共属性
     */
    public static void setDynamicPublicProperty(Context context) {
        // 初始化 SDK 后，设置动态公共属性
        SensorsDataAPI.sharedInstance().registerDynamicSuperProperties(() -> {
            try {
                String userInfoStr = AppPrefs.getSharedString(context, PrefConst.USER_INFO_KAY, "");
                UserInfoItem userInfo = null;
                if (!StringUtils.isEmpty(userInfoStr)) {
                    Gson gson = new Gson();
                    userInfo = gson.fromJson(userInfoStr, UserInfoItem.class);
                }
                JSONObject properties = new JSONObject();
                properties.put(SaVarConfig.LOGIN_STATUS_VAR, Utils.isLogin(context) ? "登录中" : "已登出");//登录状态
                properties.put(SaVarConfig.LOGIN_USERID_VAR, userInfo == null ? "" : userInfo.getUid());//登录账号ids
                properties.put(SaVarConfig.LOGIN_PHONE_VAR, userInfo == null ? "" : userInfo.getMobile());//登录手机号
                properties.put(SaVarConfig.BINDING_TYPE_VAR, Utils.isLogin(context) ? "1" : "0");//绑定类型
                properties.put(SaVarConfig.VIP_TYPE_VAR, userInfo == null ? "0" : userInfo.getIs_vip());//VIP类型
                properties.put(SaVarConfig.REG_TYPE_VAR, Utils.isLogin(context) ? "1" : "0");//注册类型
                return properties;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 设置静态公共属性appId
     */
    public static void setAppId() {
        try {
            JSONObject properties = new JSONObject();
            properties.put("app_id", BuildConfig.sa_server_app_id);
            SensorsDataAPI.sharedInstance().registerSuperProperties(properties);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录 事件
     *
     * @param is_success   是否成功
     * @param fail_reason  失败原因
     * @param login_logout 登录/登出   "登录" "登出"
     */
    public static void setLoginSuccessEvent(boolean is_success, String fail_reason, String login_logout) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.LOGIN_METHOD_VAR, "手机号");//登录方式  嘿嘿只有手机号登录
            properties.put(SaVarConfig.IS_SUCCESS_VAR, is_success);
            properties.put(SaVarConfig.FAIL_REASONS_VAR, fail_reason);
            properties.put(SaVarConfig.LOGIN_LOGOUT_VAR, login_logout);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.LOGIN_SUCCESS_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}