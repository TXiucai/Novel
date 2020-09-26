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

    /**
     * 神策 漫画下载 埋点事件
     *
     * @param work_id       漫画作品ID
     * @param download_list 下载章节 list
     */
    public static void setMHWorkDownloadEvent(int work_id, List<String> download_list) {
        setWorkDownloadEvent(SaEventConfig.MH_DOWNLOAD_EVENT, work_id, download_list);
    }

    /**
     * 神策 小说下载 埋点事件
     *
     * @param work_id       漫画作品ID
     * @param download_list 下载章节 list
     */
    public static void setXSWorkDownloadEvent(int work_id, List<String> download_list) {
        setWorkDownloadEvent(SaEventConfig.XS_DOWNLOAD_EVENT, work_id, download_list);
    }

    public static void setWorkDownloadEvent(String event, int work_id, List<String> download_list) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORK_ID_VAR, work_id);
            properties.put(SaVarConfig.D_LIST_VAR, download_list != null ? new JSONArray(download_list) : null);
            SensorsDataAPI.sharedInstance().track(event, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 APP 进入到首页时间
     *
     * @param openTime App打开开屏页到首页时间
     */
    public static void setOpenTimeEvent(int openTime) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.OPEN_TIME_VAR, openTime);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.OPEN_TIME_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 漫画评论
     *
     * @param work_id 漫画ID
     */
    public static void setMHDiscussEvent(int work_id) {
        setDiscussEvent(SaEventConfig.MH_DISCUSS_EVENT, work_id);
    }

    /**
     * 神策埋点 小说评论
     *
     * @param work_id 小说ID
     */
    public static void setXSDiscussEvent(int work_id) {
        setDiscussEvent(SaEventConfig.XS_DISCUSS_EVENT, work_id);
    }

    public static void setDiscussEvent(String event, int work_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORK_ID_VAR, work_id);
            SensorsDataAPI.sharedInstance().track(event, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 MH弹幕  (漫画)
     *
     * @param page_id    页面ID
     * @param chapter_id 章节ID
     * @param work_id    作品ID
     */
    public static void setMHDMEvent(int page_id, int chapter_id, int work_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.PAGE_ID_VAR, page_id);
            properties.put(SaVarConfig.CHAPTER_ID_VAR, chapter_id);
            properties.put(SaVarConfig.WORK_ID_VAR, work_id);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.MH_DM_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 进入VIP弹窗提示时
     */
    public static void setVIPPopEvent() {
        try {
            SensorsDataAPI.sharedInstance().track(SaEventConfig.VIP_POP_EVENT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 进入VIP页时
     *
     * @param refer_page 前向页面
     */
    public static void setVIPConfirmEvent(String refer_page) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.REFER_PAGE_VAR, refer_page);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.VIP_CONFIRM_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 选购vip套餐类型
     *
     * @param set_id 前向页面
     */
    public static void setVIPChoiceEvent(int set_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.SET_ID_VAR, set_id);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.VIP_CHOICE_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 漫画简介页
     *
     * @param prop_id           属性ID
     * @param tag_id            分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setMHIntroPageEvent(List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int total_chapter_num, int author_id) {
        setIntroPageEvent(SaEventConfig.MH_INTRO_PAGE_EVENT, prop_id, tag_id, refer_page, work_id, total_chapter_num, author_id);
    }

    /**
     * 神策埋点 小说简介页
     *
     * @param prop_id           属性ID
     * @param tag_id            分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setXSIntroPageEvent(List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int total_chapter_num, int author_id) {
        setIntroPageEvent(SaEventConfig.XS_INTRO_PAGE_EVENT, prop_id, tag_id, refer_page, work_id, total_chapter_num, author_id);
    }

    public static void setIntroPageEvent(String event, List<String> prop_id, List<String> tag_id, String refer_page, int work_id, int total_chapter_num, int author_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.PROP_ID_VAR, prop_id != null ? new JSONArray(prop_id) : null);
            properties.put(SaVarConfig.TAG_ID_VAR, tag_id != null ? new JSONArray(tag_id) : null);
            properties.put(SaVarConfig.REFER_PAGE_VAR, refer_page);
            properties.put(SaVarConfig.WORK_ID_VAR, work_id);
            properties.put(SaVarConfig.TOTAL_CHAPTER_NUM_VAR, total_chapter_num);
            properties.put(SaVarConfig.AUTHOR_ID_VAR, author_id);
            SensorsDataAPI.sharedInstance().track(event, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点  书架推荐
     *
     * @param works_type 作品类型
     * @param work_id    作品ID
     */
    public static void setBookshelfRecommendationEvent(String works_type, List<String> work_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.WORK_ID_VAR, work_id != null ? new JSONArray(work_id) : null);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.BOOKSHELF_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点  搜索推荐
     *
     * @param works_type
     * @param work_id
     */
    public static void setSearchRecommendationEvent(String works_type, String recommend_type, List<String> work_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.RECOMMEND_TYPE_VAR, recommend_type);
            properties.put(SaVarConfig.WORK_ID_VAR, work_id != null ? new JSONArray(work_id) : null);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.SEARCH_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点  首页子页推荐
     *
     * @param works_type
     * @param subpages_type
     */
    public static void setSubpagesRecommendationEvent(String works_type, String subpages_type) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.SUBPAGES_TYPE_VAR, subpages_type);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.SUBPAGES_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 首页"换一换"
     *
     * @param works_type
     * @param column_id
     * @param work_id
     */
    public static void setChangeRecommendationEvent(String works_type, int column_id, List<String> work_id) {
        setColumnRecommendationEvent(SaEventConfig.CHANGE_RECOMMENDATION_EVENT, works_type, column_id, work_id);
    }

    /**
     * 神策埋点 首页推荐
     *
     * @param works_type
     * @param column_id
     * @param work_id
     */
    public static void setHomeRecommendationEvent(String works_type, int column_id, List<String> work_id) {
        setColumnRecommendationEvent(SaEventConfig.HOME_RECOMMENDATION_EVENT, works_type, column_id, work_id);
    }

    public static void setColumnRecommendationEvent(String event, String works_type, int column_id, List<String> work_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.COLUMN_ID_VAR, column_id);
            if (SaEventConfig.CHANGE_RECOMMENDATION_EVENT.equals(event)) {
                properties.put(SaVarConfig.WORK_ID_VAR, work_id != null ? new JSONArray(work_id) : null);
            }
            SensorsDataAPI.sharedInstance().track(event, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}