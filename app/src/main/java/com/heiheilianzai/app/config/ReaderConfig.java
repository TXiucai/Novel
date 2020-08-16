package com.heiheilianzai.app.config;

import android.app.Activity;
import android.content.Context;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.http.ResultCallback;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.ShareUitls;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;


/**
 * Created by scb on 2018/5/27.
 */
public class ReaderConfig {
    //本应用的appkey
    public static final String mAppkey = RabbitConfig.mAppkey;
    //本应用的appsecret
    public static final String mAppSecretKey = RabbitConfig.mAppSecretKey;
    //本应用的IP域名
    public static String BASE_URL = RabbitConfig.BASE_URL;
    //appid 微信分配的APPID
    public static final String WEIXIN_PAY_APPID = BuildConfig.app_wx_id;
    //appid 微信分配的SECRET
    public static final String WEIXIN_APP_SECRET = BuildConfig.app_wx_secret;
    //友盟统计
    public static final String UMENG = BuildConfig.umeng_key;
    //QQ分享
    public static final String QQ_APPID = BuildConfig.app_qq_id;
    //QQ_SECRET
    public static final String QQ_SECRET = BuildConfig.app_qq_secret;
    // 当前产品类型 1: 只使用小说
    //              2: 只使用漫画
    //              3: 小说 漫画
    //              4: 漫画小说
    private static int PRODUCT_TYPE = 3;

    public static boolean app_free_charge = true;//免费频道

    public static int GETPRODUCT_TYPE(Activity activity) {
        if (PRODUCT_TYPE == 0) {
            PRODUCT_TYPE = ShareUitls.getInt(activity, "PRODUCT_TYPE", 3);
        }
        return PRODUCT_TYPE;
    }

    public static void PUTPRODUCT_TYPE(Activity activity, int UI) {
        PRODUCT_TYPE = UI;
        ShareUitls.putInt(activity, "PRODUCT_TYPE", PRODUCT_TYPE);
    }

    public static final int MIANFEI = 0;//免费频道
    public static final int WANBEN = 1;//完本频道
    public static final int SHUKU = 2;//书库频道
    public static final int PAIHANG = 3;//排行频道
    public static final int PAIHANGINSEX = 10;//排行首页频道
    public static final int BAOYUE = 4;//包月
    public static final int BAOYUE_SEARCH = 5;//包月赛选列表

    public static final int DOWN = 6;//下载频道
    public static final int READHISTORY = 7;//阅读历史频道
    public static final int LIUSHUIJIELU = 8;//流水记录
    public static final int LOOKMORE = 9;//查看更多
    public static final int MYCOMMENT = 11;//我的评论

    public static final int XIAOSHUO = 1;//只有小说
    public static final int MANHAU = 2;//只有漫画
    public static final int XIAOSHUOMAHUA = 3;//小说漫画
    public static final int MANHAUXIAOSHUO = 4;//漫画小说

    public static final int fragment_store_xiaoshuo_dp = 23;
    public static final int fragment_store_manhau_dp = 15;
    public static final int REFRESH_HEIGHT = 120;//书城 发现 向下滑动距离 改变bar 背景
    public static int MAXheigth;//获取手机能显示图片的最高高度

    public static final String API_CRYPTOGRAPHY = "1";//api加密标识

    public static int getMAXheigth() {
        if (MAXheigth == 0) {
            MAXheigth = ImageUtil.getOpenglRenderLimitValue();
        }
        return MAXheigth;
    }

    // 是否 启用支付 功能
    public static boolean USE_PAY = true;
    //是否使用微信 包括微信登录和微信分享
    public static boolean USE_WEIXIN = false;
    //是否使用QQ 分享
    public static boolean USE_QQ = false;
    //是否使用分享
    public static boolean USE_SHARE = false;
    //是否使用广告
    public static boolean USE_AD = true;
    public static final boolean USE_AD_FINAL = true;
    public static final int READBUTTOM_HEIGHT = 55;//yeudulibu AD
    public static final String BASE_PAY = "";
    public static final String IMG_CRYPTOGRAPHIC_POSTFIX = "?c";

    /**
     * 分享成功增加金币
     */
    public static final String ShareAddGold = "/user/share-reward";

    public static final String APP_SHARE = "/user/app-share";

    public static final String task_read = "/user/task-read";

    public static final String mBookStoreUrl = "/book/store";

    /**
     * 获取分享APP接口
     */
    public static final String mQrcodeIndex = "/qrcode/index";

    /**
     * 首页小说 banner
     */
    public static final String BOOK_STORE_BANNER = "/book/index-banner";

    public static final String privacy = "/site/privacy";
    /**
     * 作品详情url
     */
    public static final String mBookInfoUrl = "/book/info";

    /**
     * 关于我们
     */
    public static final String aBoutUs = "/service/about";

    /**
     * 任务中心
     */
    public static final String taskcenter = "/task/center";

    /**
     * 签到
     */
    public static final String sIgnin = BASE_URL + "/user/sign-center";
    /**
     * 签到
     */
    public static final String sIgninhttp = "/user/sign";

    /**
     * 完本作品列表
     */
    public static final String mFinishUrl = "/book/finish";

    public static final String check_switch = "/service/check-data";

    /**
     * 作品章节目录
     */
    public static final String mChapterCatalogUrl = "/chapter/catalog";

    /**
     * 作品评论列表
     */
    public static final String mCommentListUrl = "/comment/list";

    /**
     * 发布作品评论
     */
    public static final String mCommentPostUrl = "/comment/post";

    /**
     * 发现页接口
     */
    public static final String mDiscoveryUrl = "/book/new-featured";

    /**
     * 个人中心首页接口
     */
    public static final String mUserCenterUrl = "/user/center";

    /**
     * 提交意见反馈
     */
    public static final String mFeedbackUrl = "/user/post-feedback";

    /**
     * 发送短信获取验证码
     */
    public static final String mMessageUrl = "/message/send";

    /**
     * 手机号登录
     */
    public static final String mMobileLoginUrl = "/user/mobile-login";

    /**
     * 波音登录
     */
    public static final String mBoYinLoginUrl = "/auth/index";

    /**
     * 用户名登录
     */
    public static final String mUsernameLoginUrl = "/user/account-login";

    /**
     * 添加书架作品
     */
    public static final String mBookAddCollectUrl = "/user/collect-add";

    /**
     * 添加书架作品
     */
    public static final String mBookDelCollectUrl = "/user/collect-del";

    /**
     * 章节下载
     */
    public static final String mChapterDownUrl = "/chapter/down";

    public static final String auto_sub = "/user/auto-sub";

    public static final String chapter_text = "/chapter/text";

    /**
     * 消费充值记录
     */
    public static final String mPayGoldDetailUrl = "/pay/gold-detail";

    /**
     * 查询用户个人资料
     */
    public static final String mUserInfoUrl = "/user/info";

    /**
     * 用户设置头像
     */
    public static final String mUserSetAvatarUrl = "/user/set-avatar";

    /**
     * 修改昵称
     */
    public static final String mUserSetNicknameUrl = "/user/set-nickname";

    /**
     * 修改性别
     */
    public static final String mUserSetGender = "/user/set-gender";
    /**
     * 绑定手机号
     */
    public static final String mUserBindPhoneUrl = "/user/bind-mobile";

    /**
     * 充值页面
     */
    public static final String mPayRechargeCenterUrl = "/pay/center";

    /**
     * 获取支付渠道列表
     */
    public static final String mPayChannelList = "/pay/get-pay-list";

    /**
     * 会员充值
     */
    public static final String mPayVip = "/pay/start-pay";

    /**
     * 获取当期最新一条充值订单
     */
    public static final String mPayLastOrder = "/pay/last-order";


    public static final String payUrl = "/pay/common-pay";

    /**
     * 更新deviceId接口
     */
    public static final String mSyncDeviceIdUrl = "/user/sync-device";

    /**
     * 章节购买
     */
    public static final String mChapterBuy = "/chapter/buy";

    /**
     * 章节购买预览
     */
    public static final String mChapterBuyIndex = "/chapter/buy-index";

    public static final String bind_wechat = "/user/bind-wechat";

    public static final String start_recommend = "/user/start-recommend";

    /**
     * 获取系统配置
     */
    public static final String mSystemParam = "/service/system-param";

    /**
     * 获取开屏广告接口
     */
    public static final String mOpenScreen = "/service/open-screen";

    /**
     * 首页公告
     */
    public static final String mHomeNotice = "/site/notice";

    /**
     * 广告
     */
    public static final String mAdvert = "/advert/info";

    /**
     * 通过第三方获取用户使用网络所在区域
     */
    public static final String thirdpartyGetCity = "http://pv.sohu.com/cityjson?ie=utf-8";

    public static final String mWebviewCacheDir = "webviewCache";

    /**
     * webview settings.setAppCachePath(appCacheDir)的路径
     */
    public static final String mAppCacheDir = "appCache";

    /**
     * 用户登录token
     */
    public static final String TOKEN = "token";

    /**
     * 波音登录token
     */
    public static final String BOYIN_LOGIN_TOKEN = "boyin_login_token";

    /**
     * 用户唯一标识
     */
    public static final String UID = "uid";

    /**
     * 3G4G
     */
    public static final String IS3G4G = "is3G4G";

    /**
     * wifi download
     */
    public static final String WIFIDOWNLOAD = "wifidownload";

    /**
     * auto buy
     */
    public static final String AUTOBUY = "autobuy";

    public static String PUSH_TOKEN = "";

    private static ReaderConfig mReaderConfig = null;

    public static List<Integer> integerList = new ArrayList<>();

    /**
     * 单例模式：获得WuyeConfig对象
     *
     * @return
     */
    public static ReaderConfig newInstance() {
        if (mReaderConfig == null) {
            mReaderConfig = new ReaderConfig();
        }
        return mReaderConfig;
    }

    /**
     * 本地log日志文件存储路径
     */
    private String mLocalLogDir = "ReaderAppLog/";

    /**
     * 获取本地日志文件存储路径
     *
     * @return
     */
    public String getLocalLogDir() {
        return mLocalLogDir;
    }

    /**
     * 使用3G/4G同步书架
     */
    private boolean is3G4G = true;

    public boolean is3G4G() {
        return is3G4G;
    }

    public void setIs3G4G(boolean is3G4G) {
        this.is3G4G = is3G4G;
    }

    /**
     * WiFi下自动下载已购章节，由于和预下载上／下章有冲突，暂不处理
     */
    private boolean isWiFiDownload = true;

    public boolean isWiFiDownload() {
        return isWiFiDownload;
    }

    public void setWiFiDownload(boolean wiFiDownload) {
        isWiFiDownload = wiFiDownload;
    }

    /**
     * 自动购买下一章
     */
    private boolean autoBuy = true;

    public boolean isAutoBuy() {
        return autoBuy;
    }

    public void setAutoBuy(boolean autoBuy) {
        this.autoBuy = autoBuy;
    }

    //从阅读页直接进入目录页再打开阅读 不改变 广告是否显示 的布尔值
    public static boolean CatalogInnerActivityOpen;
    //是是否显示广告的 接口控制开光
    public static int ad_switch;
    //个人中心界面是否需要刷新 （章节阅读发生过购买充值 导致个人账户 书币变化 需要刷新个人中心界面）
    public static boolean REFREASH_USERCENTER = false;
    //版本控制接口
    public static String AppUpdate;
    //一级单位
    public static String currencyUnit;
    //二级单位
    public static String subUnit;

    public static String getCurrencyUnit(Activity activity) {
        if (currencyUnit != null) {
            return currencyUnit;
        } else {
            String cu = ShareUitls.getString(activity, "currencyUnit", "书币");
            return cu;
        }
    }

    public static String getSubUnit(Activity activity) {
        if (subUnit != null) {
            return subUnit;
        } else {
            String cu = ShareUitls.getString(activity, "subUnit", "书券");
            return cu;
        }
    }

    /**
     * 调用设备同步接口
     */
    public static void syncDevice(Context activity) {
        String device_id = ShareUitls.getString(activity, "PUSH_TOKEN", ReaderConfig.PUSH_TOKEN);
        if (device_id.length() == 0) {
            return;
        }
        final ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("device_id", device_id);
        String json = params.generateParamsJson();
        OkHttpEngine.getInstance(activity).postAsyncHttp(ReaderConfig.getBaseUrl() + ReaderConfig.mSyncDeviceIdUrl, json, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(final String result) {
            }
        });
    }

    public static String getBaseUrl() {
        return ReaderApplication.getBaseUrl();
    }
}
