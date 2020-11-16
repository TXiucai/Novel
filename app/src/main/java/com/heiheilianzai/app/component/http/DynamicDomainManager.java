package com.heiheilianzai.app.component.http;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.constant.AppConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.RabbitConfig;
import com.heiheilianzai.app.model.Bean;
import com.heiheilianzai.app.utils.MyToash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.Response;

/**
 * 轮询获取api域名相关
 */
public class DynamicDomainManager {
    public static final boolean JUMP = false;
    public static boolean shouldTestPreparedDomain = false;
    private Handler handler;
    private OnCompleteListener onCompleteListener;
    private PreparedDomain preparedDomain;
    private Context context;
    private boolean stop = false;
    private boolean completed = false;
    private List<String> mApiUrl;

    public interface OnCompleteListener {
        public void onComplete(List<String> apiUrl);
    }

    public DynamicDomainManager(Context context, OnCompleteListener listener) {
        onCompleteListener = listener;
        handler = new Handler(Looper.getMainLooper());
        preparedDomain = new PreparedDomain(context);
        this.context = context;
        mApiUrl = new ArrayList<>();
    }

    public boolean isCompleted() {
        return completed;
    }

    public String getDefDomain() {
        return preparedDomain.getDefaultDomain();
    }

    public void stop() {
        onCompleteListener = null;
        stop = true;
        context = null;
    }

    public void start() {
        completed = false;
        shouldTestPreparedDomain = false;
        if (JUMP) {
            if (null != onCompleteListener) {
                onCompleteUI(getDefDomain());
            }
            return;
        }
        if (!RabbitConfig.ONLINE) {
            if (null != onCompleteListener) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        onCompleteUI(RabbitConfig.BASE_URL);
                    }
                }, 3000);
            }
            return;
        }
        if (!NetworkUtil.isConnected(context)) {
            if (null != onCompleteListener) {
                onCompleteUI(getDefDomain());
            }
            return;
        }
        new Thread() {
            @Override
            public void run() {
                runmyrun();
            }
        }.start();
    }

    private void onCompleteUI(final String domain) {
        completed = true;
        if (null == onCompleteListener) {
            return;
        }
        handler.post(new Runnable() {
            @Override
            public void run() {
                mApiUrl.add(domain);
                preparedDomain.setDefaultDomain(domain);
                onCompleteListener.onComplete(mApiUrl);
            }
        });
    }

    /**
     * 如果服务器需要全部替换Api为新域名 赋予 {@link PrefConst#DYNAMIC_DOMAIN_KAY} 一个新值
     */
    private void runmyrun() {
        String text = "";
        try {
            if (expiredNow() || "".equals(preparedDomain.getString(PrefConst.DYNAMIC_DOMAIN_KAY, ""))) {
                text = getPublicDomainJson();
                preparedDomain.putString(PrefConst.DYNAMIC_DOMAIN_KAY, text);
                markTimeNow();
            } else {
                text = preparedDomain.getString(PrefConst.DYNAMIC_DOMAIN_KAY, "");
                htttpCachePublicDomain();
            }
            if (stop) {
                return;
            }
            Bean bean = null;
            try {
                bean = JSON.parseObject(text, Bean.class);
            } catch (JSONException e) {
                return;
            }
            if (!bean.data.isEmpty()) {
                if (stop) {
                    return;
                }
                if (onCompleteListener != null) {
                    for (int i = 0; i < bean.data.size(); i++) {
                        String domain = bean.data.get(i).name;
                        mApiUrl.add(domain);
                    }
                    onCompleteListener.onComplete(mApiUrl);
                }
            }
        } catch (Exception e) {
            MyToash.LogE("dynamic domain error", e.getMessage());
        }
        if (stop) {
            return;
        }
        onCompleteUI(preparedDomain.getDefaultDomain());
    }

    /**
     * 有缓存情况下，异步替换缓存数据
     */
    private void htttpCachePublicDomain() {
        new Thread() {
            @Override
            public void run() {
                preparedDomain.putString(PrefConst.DYNAMIC_DOMAIN_KAY, getPublicDomainJson());
            }
        }.start();
    }

    private void postRecord(boolean success, String domain) {
        try {
            Response response = OkGo.post(AppConfig.getServerUrl() + AppConfig.SEVER_DYNAMIC_RECORD, domain, success ? "success" : "fail");
        } catch (IOException e) {
            MyToash.LogE(domain + " postRecord error", e.getMessage());
        }
    }

    private boolean expiredNow() {
        long now = System.currentTimeMillis();
        long before = preparedDomain.getLong("time", 0);
        if (0 == before) {
            return true;
        }
        return (now - before) / 1000 / 60 / 60 >= 12;
    }

    public void markTimeNow() {
        preparedDomain.putLong("time", System.currentTimeMillis());
    }

    private String getPublicDomainJson() {
        List<String> strings = new ArrayList<>();
        strings.add(BuildConfig.domain_host);
        String[] reserves = BuildConfig.domain_host_reserve.split("@");
        if (reserves != null && reserves.length != 0) {
            Collections.addAll(strings, reserves);
        }
        for (String domain_host : strings) {
            App.setdDomainHostsUrl(domain_host);
            String json = null;
            Bean bean = null;
            try {
                String url = AppConfig.getServerUrl() + AppConfig.SERVER_DYNAMIC_DOMAIN;
                json = OkGo.get(url);
                bean = JSON.parseObject(json, Bean.class);
            } catch (Exception e) {
                bean = null;
            }
            if (bean != null) {
                return json;
            }
        }
        return "";
    }
}
