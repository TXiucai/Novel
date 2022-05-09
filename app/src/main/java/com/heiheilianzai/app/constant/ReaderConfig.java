package com.heiheilianzai.app.constant;

import android.app.Activity;
import android.content.Context;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.comic.ComicChapter;
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
    public static String BASE_URL = App.getBaseUrl();
    //appid 微信分配的APPID
    public static final String WEIXIN_PAY_APPID = BuildConfig.app_wx_id;
    //appid 微信分配的SECRET
    public static final String WEIXIN_APP_SECRET = BuildConfig.app_wx_secret;
    //友盟统计
    public static final String UMENG = BuildConfig.umeng_key;
    // tinstall key
    public static final String TINSTALL_KEY = BuildConfig.tinstall_key;
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

    public static int display_ad_days_novel = 0;//几天之后从新展示
    public static int display_ad_days_comic = 0;//几天之后从新展示
    public static int display_second = 5;//听书按钮引导提示默认5秒
    public static String guide_text = "";//听书文案
    public static String novel_tips = "";//小说底部中间文案

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
    public static final int MANHAUXIAOSHUO = 4;//漫画小

    public static final int fragment_store_xiaoshuo_dp = 23;
    public static final int fragment_store_manhau_dp = 15;
    public static final int REFRESH_HEIGHT = 120;//书城 发现 向下滑动距离 改变bar 背景
    public static int MAXheigth;//获取手机能显示图片的最高高度

    public static final String tinstall_code = "TINSTALL_CODE";

    public static final String API_CRYPTOGRAPHY = "1";//api加密标识
    public static boolean BANG_SCREEN = false;//刘海屏

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

    public static List<AppUpdate.ListBean> NOVEL_SDK_AD = new ArrayList<>();
    public static List<AppUpdate.ListBean> COMIC_SDK_AD = new ArrayList<>();
    public static AppUpdate.AdPositionOtherBean.ListBean OTHER_SDK_AD = new AppUpdate.AdPositionOtherBean.ListBean();
    public static String TTS_OPEN = "";
    public static final int READBUTTOM_HEIGHT = 60;//yeudulibu AD
    public static final String BASE_PAY = "";
    public static final String IMG_CRYPTOGRAPHIC_POSTFIX = "?c";
    public static BaseAd BOTTOM_READ_AD;
    public static BaseAd TOP_READ_AD;
    public static BaseAd BOTTOM_COMIC_AD;
    public static BaseAd TOP_COMIC_AD;
    public static ComicChapter CHAPTER_COMIC_AD;
    public static List<String> pay_lunxun_domain;
    public static String pay_lunxun_domain_switch = "0";//0关闭 1开启

    /**
     * 分享成功增加金币
     */
    public static final String ShareAddGold = "/user/share-reward";

    public static final String APP_SHARE = "/user/app-share";

    public static final String task_read = "/user/task-read";

    public static final String mBookStoreUrl = "/book/store";

    public static final String mBookChannelUrl = "/book-channel-list/index";

    public static final String mBookChannelDetailUrl = "/book-channel-list/label";

    public static final String mTopYearBookUrl = "/home-recommen/annuallist";

    public static final String mTopYearComicUrl = "/home-recommen/annualcomic";

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
     * 作品详情记录
     */
    public static final String mBookInfoRecordUrl = "/book/add-book-behavior-log";


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
     * 填写邀请码
     */
    public static final String sIginInVite = "/qrcode/invite-code";


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
     * 作品章节书卷解锁
     */
    public static final String mChapterOpenCoupon = "/chapter/unlock-text";

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
     * 分享记录
     */
    public static final String mShareRecord = "/qrcode/success-list";

    /**
     * 分享详情
     */
    public static final String mShareDetails = "/qrcode/index";

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
     * 新用户名登录
     */
    public static final String mUsernameLoginUrlNew = "/user-new/user-name-login";

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
     * 新修改昵称
     */
    public static final String mUserSetNickname = "/user-new/edit-user-name";
    /**
     * 新修密码
     */
    public static final String mUserSetPassword = "/user-new/edit-user-password";

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
     * 新会员充值
     */
    public static final String mNewPayVip = "/paynew/start-pay";

    /**
     * 会员订单成功的跑马灯
     */
    public static final String mMarqueeVip = "/success-order/index";
    /**
     * 金币订单成功的跑马灯
     */
    public static final String mMarqueeGold = "/success-order/gold";
    /**
     * vip猜你喜欢
     */
    public static final String mCommentVip = "/guessuserlike/index";
    /**
     * 上传阅读时间
     */
    public static final String mReadTime = "/usergrandtotalread/minute";

    /**
     * 上传阅读记录
     */
    public static final String mReadRecord = "/chapter/add-book-behavior-read-log";

    /**
     * 获取当期最新一条充值订单
     */
    public static final String mPayLastOrder = "/user/lastpay-order";

    /**
     * 用户最后登录时间
     */
    public static final String mLastTime = "/user/edit-last-login-time";

    /**
     * 获取充值订单
     */
    public static final String mPayOrder = "/user/get-today-success-pay";


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
     * 推荐app
     */
    public static final String mRecommendApp = "/home-recommen/app";

    /**
     * 广告
     */
    public static final String mAdvert = "/advert/info";

    /**
     * 国家code
     */
    public static final String mCountryCode = "/message/area-list";

    /**
     * 首页推荐
     */
    public static final String mHomeRecomment = "/home-recommen/index";

    /**
     * 首页悬浮图标
     */
    public static final String mHomeFloat = "/user/index-window";

    /**
     * 读取意见反馈类型
     */
    public static final String mFeedBackType = "/feedback/index";

    /**
     * 上传图片
     */
    public static final String mUpPicture = "/feedback/upload";

    /**
     * 上传反馈信息
     */
    public static final String mUpAll = "/user/post-feedback";

    /**
     * 金币获取记录
     */
    public static final String COUPON_ACCEPT = "/user/shuquan-gethistory";

    /**
     * 金币消费
     */
    public static final String COUPON_USE = "/user/shuquan-usehistory";

    /**
     * 金币说明
     */
    public static final String COUPON_DESCRIPTION = "/user/book-description";

    /**
     * 阅读时长
     */
    public static final String READ_TIME = "/read-daily-task/index";

    /**
     * 阅读时长领奖
     */
    public static final String READ_TIME_ACCEPT = "/read-daily-task/add";

    /**
     * 兑换周边
     */
    public static final String EXCHANGE_GIFT = "/exchange-manage-info/list";

    /**
     * 兑换周边
     */
    public static final String EXCHANGE_GIFT_ACCEPT = "/exchange-manage-info/add-exchange";

    /**
     * 查看地址
     */
    public static final String ACCEPT_ADDRESS = "/user-receiver-info/user-address";

    /**
     * 编辑地址
     */
    public static final String EDIT_ADDRESS = "/user-receiver-info/user-edit-address";

    /**
     * 添加地址
     */
    public static final String ADD_ADDRESS = "/user-receiver-info/user-add-address";

    /**
     * 一键注册 name
     */
    public static final String REGISTERNAME = "/user-new/auto-user-name";

    /**
     * 一键注册
     */
    public static final String REGISTERFAST = "/user-new/quick-register";

    /**
     * 上次听记录
     */
    public static final String LISTENBOOKRECODE = "/book-listen/update-listen";

    /**
     * tts 语音库 不支持的 字符格式
     */
    public static final String TTS_FILTER = "/tts/filter";

    /**
     * 统计同一个设备下载tts 认证次数 因为三方没有给这个次数
     */
    public static final String TTS_DOWNLOAD_COUNT = "/book-listen/add-device-times";

    /**
     * 本地推送
     */
    public static final String LOCAL_PUSH = "/local-push-content/index";

    /**
     * 本地推送点击上报
     */
    public static final String LOCAL_PUSH_ClICK = "/local-push-content/add-click-log";

    /**
     * 用户订单记录
     */
    public static final String USER_ORDER_RECORD = "/user-order/order-history";

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
            String cu = ShareUitls.getString(activity, "subUnit", "金币");
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
        return App.getBaseUrl();
    }
}