package com.heiheilianzai.app.domain;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.config.RabbitConfig;
import com.heiheilianzai.app.config.ReaderApplication;
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

    public interface OnCompleteListener {
        public void onComplete(String domain);
    }

    public DynamicDomainManager(Context context, OnCompleteListener listener) {
        onCompleteListener = listener;
        handler = new Handler(Looper.getMainLooper());
        preparedDomain = new PreparedDomain(context);
        this.context = context;
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
                preparedDomain.setDefaultDomain(domain);
                onCompleteListener.onComplete(domain);
            }
        });
    }

    private void runmyrun() {
        String text = "";
        try {
            text = getPublicDomainJson();
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
                String theDomain = "";
                boolean theSuccess = false;
                for (int index = 0; index < bean.data.size(); index++) {
                    String domain = bean.data.get(index).name;
                    boolean success = ApiTest.synTestConnected(domain);
                    if (stop) {
                        return;
                    }
                    if (success) {
                        theSuccess = true;
                        theDomain = domain;
                        postRecord(success, domain);
                        break;
                    } else {
                        postRecord(success, domain);
                    }
                }
                if (stop) {
                    return;
                }
                shouldTestPreparedDomain = !theSuccess;
                if (theSuccess) {
                    onCompleteUI(theDomain);
                    return;
                } else {
                    List<String> domains = preparedDomain.getDomain();
                    for (String domainUrl : domains) {
                        if (stop) {
                            return;
                        }
                        if (ApiTest.synTestConnected(domainUrl)) {
                            onCompleteUI(domainUrl);
                            return;
                        }
                    }
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
            ReaderApplication.setdDomainHostsUrl(domain_host);
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
