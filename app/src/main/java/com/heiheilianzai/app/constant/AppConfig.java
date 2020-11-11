package com.heiheilianzai.app.constant;

import com.heiheilianzai.app.base.App;

/**
 * Created by cxf on 2017/8/4.
 */

public class AppConfig {

    // 动态域名服务器
    public static final   String SERVER_DYNAMIC_DOMAIN = "/v1/domain/dynamic/data";

    public static final   String SERVER_H5_DOMAIN = "/service/h5-domin";

    public static final  String SEVER_DYNAMIC_RECORD = "/v1/domain/dynamic/count";
    // 埋点域名服务器
    public static final  String SERVER_PREPARED_DOMAIN = "/v1/domain/buried/data";

    // 埋点记录
    public static  final String SERVER_PREPARED_RECORD = "/v1/domain/buried/count";

    public static final String SERVER_PUBLIC_DOMAIN = "https://raw.githubusercontent.com/leolee001/PPText/master/androidText.txt";

    public static  String  getServerUrl(){
        return App.getDomainHostsUrl();
    }
}