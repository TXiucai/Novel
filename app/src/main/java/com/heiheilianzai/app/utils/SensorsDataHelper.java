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
import org.json.JSONArray;

import java.util.List;

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

    /**
     * 神策 漫画内容 埋点事件
     *
     * @param total_page_num    当前章总页数
     * @param read_page_num     当前章已读页数
     * @param stay_time         停留时长
     * @param prop_id           属性ID
     * @param tag_id            分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param chapter_id        漫画当前章节ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setMHContentPageEvent(int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        setContentPageEvent(SaEventConfig.MH_CONTENT_PAGE_EVENT, total_page_num, read_page_num, stay_time, prop_id, tag_id, refer_page, work_id, chapter_id, total_chapter_num, author_id);
    }

    /**
     * 神策 小说内容 埋点事件
     *
     * @param total_page_num    当前章总页数
     * @param read_page_num     当前章已读页数
     * @param stay_time         停留时长
     * @param prop_id           属性ID
     * @param tag_id            分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param chapter_id        漫画当前章节ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setXSContentPageEvent(int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        setContentPageEvent(SaEventConfig.XS_CONTENT_PAGE_EVENT, total_page_num, read_page_num, stay_time, prop_id, tag_id, refer_page, work_id, chapter_id, total_chapter_num, author_id);
    }

    public static void setContentPageEvent(String event, int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.TOTAL_PAGE_NUM_VAR, total_page_num);
            properties.put(SaVarConfig.READ_PAGE_NUM_VAR, read_page_num);
            properties.put(SaVarConfig.STAY_TIME_VAR, stay_time);
            properties.put(SaVarConfig.PROP_ID_VAR, prop_id != null ? new JSONArray(prop_id) : null);
            properties.put(SaVarConfig.TAG_ID_VAR, tag_id != null ? new JSONArray(tag_id) : null);
            properties.put(SaVarConfig.REFER_PAGE_VAR, refer_page);
            properties.put(SaVarConfig.WORK_ID_VAR, work_id);
            properties.put(SaVarConfig.CHAPTER_ID_VAR, chapter_id);
            properties.put(SaVarConfig.TOTAL_CHAPTER_NUM_VAR, total_chapter_num);
            properties.put(SaVarConfig.AUTHOR_ID_VAR, author_id);
            SensorsDataAPI.sharedInstance().track(event, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}