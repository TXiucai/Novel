package com.heiheilianzai.app.utils;

import android.content.Context;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.sa.SaEventConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.UserInfoItem;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * 神策接入工具类
 */
public class SensorsDataHelper {

    /**
     * 设置动态公共属性
     */
    public static void setDynamicPublicProperty(Context context) {
        SensorsDataAPI.sharedInstance().registerDynamicSuperProperties(() -> {
            try {
                return getUserJSONObject(context, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 神策获取用户相关信息数据
     *
     * @param context
     * @param profileSet 是否是  神策埋点profileSet方法。
     * @return
     * @throws JSONException
     */
    private static JSONObject getUserJSONObject(Context context, boolean profileSet) throws JSONException {
        UserInfoItem userInfo = App.getUserInfoItem(context);
        if (profileSet) {
            login(userInfo == null ? Utils.getUUID(context) : BuildConfig.sa_server_app_id + "_" + userInfo.getUid());
        }
        JSONObject properties = new JSONObject();
        properties.put(SaVarConfig.LOGIN_STATUS_VAR, Utils.isLogin(context) ? "登录中" : "已登出");//登录状态
        properties.put(SaVarConfig.LOGIN_USERID_VAR, userInfo == null ? "" : BuildConfig.sa_server_app_id + "_" + userInfo.getUid());//登录账号ids
        properties.put(SaVarConfig.LOGIN_PHONE_VAR, userInfo == null ? "" : userInfo.getMobile());//登录手机号
        properties.put(SaVarConfig.BINDING_TYPE_VAR, Utils.isLogin(context) ? "1" : "0");//绑定类型
        properties.put(SaVarConfig.VIP_TYPE_VAR, userInfo == null ? "0" : userInfo.getIs_vip());//VIP类型
        properties.put(SaVarConfig.REG_TYPE_VAR, Utils.isLogin(context) ? "1" : "0");//注册类型
        return properties;
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
     * @param tag_id_list       分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param chapter_id        漫画当前章节ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setMHContentPageEvent(int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id_list, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        setContentPageEvent(SaEventConfig.MH_CONTENT_PAGE_EVENT, total_page_num, read_page_num, stay_time, prop_id, tag_id_list, refer_page, work_id, chapter_id, total_chapter_num, author_id);
    }

    /**
     * 神策 小说内容 埋点事件
     *
     * @param total_page_num    当前章总页数
     * @param read_page_num     当前章已读页数
     * @param stay_time         停留时长
     * @param prop_id           属性ID
     * @param tag_id_list       分类ID
     * @param refer_page        前向页面
     * @param work_id           漫画ID
     * @param chapter_id        漫画当前章节ID
     * @param total_chapter_num 漫画总章节
     * @param author_id         作者ID
     */
    public static void setXSContentPageEvent(int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id_list, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        setContentPageEvent(SaEventConfig.XS_CONTENT_PAGE_EVENT, total_page_num, read_page_num, stay_time, prop_id, tag_id_list, refer_page, work_id, chapter_id, total_chapter_num, author_id);
    }

    public static void setContentPageEvent(String event, int total_page_num, int read_page_num, int stay_time, List<String> prop_id, List<String> tag_id_list, String refer_page, int work_id, int chapter_id, int total_chapter_num, int author_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.TOTAL_PAGE_NUM_VAR, total_page_num);
            properties.put(SaVarConfig.READ_PAGE_NUM_VAR, read_page_num);
            properties.put(SaVarConfig.STAY_TIME_VAR, stay_time);
            properties.put(SaVarConfig.PROP_ID_VAR, prop_id != null ? new JSONArray(prop_id) : null);
            properties.put(SaVarConfig.TAG_ID_VAR_LIST, tag_id_list != null ? new JSONArray(tag_id_list) : null);
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
            properties.put(SaVarConfig.TAG_ID_VAR_LIST, tag_id != null ? new JSONArray(tag_id) : null);
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
     * @param works_id   作品ID
     */
    public static void setBookshelfRecommendationEvent(String works_type, List<String> works_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.WORKS_ID_VAR, works_id != null ? new JSONArray(works_id) : null);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.BOOKSHELF_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点  搜索推荐
     *
     * @param works_type     作品类型   MH XS
     * @param recommend_type 推荐类型   热搜榜 热门推荐
     * @param works_id       （产品说先去掉works_id防止反复修改先保留数据，只删除了上报）
     */
    public static void setSearchRecommendationEvent(String works_type, String recommend_type, List<String> works_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.RECOMMEND_TYPE_VAR, recommend_type);
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
     * @param works_id
     */
    public static void setChangeRecommendationEvent(String works_type, int column_id, List<String> works_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.COLUMN_ID_VAR, column_id);
            properties.put(SaVarConfig.WORKS_ID_VAR, works_id != null ? new JSONArray(works_id) : null);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.CHANGE_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点 首页推荐
     *
     * @param works_type
     * @param columns_id
     */
    public static void setHomeRecommendationEvent(String works_type, List<String> columns_id) {
        try {
            JSONObject properties = new JSONObject();
            properties.put(SaVarConfig.WORKS_TYPE_VAR, works_type);
            properties.put(SaVarConfig.COLUMN_ID_LIST_VAR, columns_id != null ? new JSONArray(columns_id) : null);
            SensorsDataAPI.sharedInstance().track(SaEventConfig.HOME_RECOMMENDATION_EVENT, properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置用户属性
     *
     * @param last_login_time //最近登录时间
     */
    public static void profileSet(String last_login_time) {
        try {
            JSONObject properties = new JSONObject();
            properties.put("last_login_time", last_login_time);
            SensorsDataAPI.sharedInstance().profileSet(properties);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置神策用户属性
     *
     * @param context
     */
    public static void profileSet(Context context) {
        try {
            SensorsDataAPI.sharedInstance().profileSet(getUserJSONObject(context, true));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 神策埋点用户登录
     * 应用打开时调用，用户登录时调用
     *
     * @param loginId 用户登录时 神策应用ID_用户ID 未登录时 手机唯一识别码IMEI
     */
    public static void login(String loginId) {
        SensorsDataAPI.sharedInstance().login(loginId);
    }

    /**
     * 神策激活事件
     */
    public static void trackInstallation() {
        try {
            SensorsDataAPI.sharedInstance().trackInstallation("AppInstall");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}