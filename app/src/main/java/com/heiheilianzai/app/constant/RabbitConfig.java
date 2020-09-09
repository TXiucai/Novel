package com.heiheilianzai.app.constant;

import com.heiheilianzai.app.BuildConfig;

/**
 * 上线前检查 ONLINE 是否为 true  configs 配置文件里相对应参数是否正确。
 *  当前版本记录: name=2.2.7  code=20200828
 */
public class RabbitConfig {

    public static final boolean ONLINE = true;

    private static final String mAppkey_online = BuildConfig.api_key;
    private static final String mAppkey_uat = BuildConfig.api_key_uat;

    private static final String mAppSecretKey_online = BuildConfig.api_secret_key;
    private static final String mAppSecretKey_uat = BuildConfig.api_secret_key_uat;

    private static final String BASE_URL_online = BuildConfig.api_host;
    private static final String BASE_URL_uat = BuildConfig.api_host_uat;
    // 本应用的appkey
    public static final String mAppkey = ONLINE ? mAppkey_online : mAppkey_uat;
    // 本应用的appsecret
    public static final String mAppSecretKey = ONLINE ? mAppSecretKey_online : mAppSecretKey_uat;
    // 本应用的IP域名
    public static final String BASE_URL = ONLINE ? BASE_URL_online : BASE_URL_uat;
}