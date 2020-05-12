package com.heiheilianzai.app.http;

import android.content.Context;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.utils.UpdateApp;
import com.heiheilianzai.app.utils.Utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by scb on 2018/6/9.
 */
public class ReaderParams {
    private final String TAG = ReaderParams.class.getSimpleName();
    List<String> paramList = new ArrayList<>();
    ReaderNameValuePair nameValuePair = new ReaderNameValuePair();

    public ReaderParams(Context context) {

        //公共参数在构造方法里处理
        String appId = BuildConfig.app_id;
        String osType = Utils.getOsType();
        String product = Utils.getProduct();
        String sysVer = Utils.getSystemVersion();
        String time = System.currentTimeMillis() / 1000 + "";
        String token = Utils.getToken(context);
        String udid = Utils.getUUID(context);
        String ver = Utils.getAppVersionName(context);
        paramList.add("appId=" + appId);
        paramList.add("osType=" + osType);
        paramList.add("product=" + product);
        paramList.add("sysVer=" + sysVer);
        paramList.add("time=" + time);
        paramList.add("token=" + token);
        paramList.add("udid=" + udid);
        paramList.add("ver=" + ver);

        String marketChannel=UpdateApp.getChannelName(context);
        paramList.add("marketChannel=" + marketChannel);
        paramList.add("packageName="+ BuildConfig.APPLICATION_ID);
        nameValuePair.put("appId", appId);
        nameValuePair.put("osType", osType);
        nameValuePair.put("product", product);
        nameValuePair.put("sysVer", sysVer);
        nameValuePair.put("time", time);
        nameValuePair.put("token", token);
        nameValuePair.put("udid", udid);
        nameValuePair.put("ver", ver);
        nameValuePair.put("packageName", BuildConfig.APPLICATION_ID);
        nameValuePair.put("marketChannel", marketChannel);

    }

    /**
     * 额外参数另行添加
     *
     * @param key
     * @param value
     */
    public void putExtraParams(String key, String value) {
        paramList.add(key + "=" + value);
        nameValuePair.put(key, value);
    }

    /**
     * 生成最终的json参数
     *
     * @return
     */
    public String generateParamsJson() {
        String sortedParamString = getSortedParams(paramList);
        String sign = Utils.MD5(sortedParamString);
        nameValuePair.put("sign", sign);
        String json = nameValuePair.toJson();
        return json;
    }

    /**
     * 参数按照字典顺讯排序
     *
     * @param list
     * @return
     */
    public String getSortedParams(List<String> list) {

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ReaderConfig.mAppkey);
        Collections.sort(list);

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                stringBuilder.append(list.get(i));
            } else {
                stringBuilder.append("&").append(list.get(i));
            }
        }
        stringBuilder.append(ReaderConfig.mAppSecretKey);
        Utils.printLog(TAG, "getSortedParams:" + stringBuilder.toString());

        return stringBuilder.toString();
    }
}
