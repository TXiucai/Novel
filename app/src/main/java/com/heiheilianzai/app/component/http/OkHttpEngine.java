package com.heiheilianzai.app.component.http;

import android.content.Context;

import com.google.gson.Gson;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.decode.AESUtil;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Administrator on 2016/4/16.
 */
public class OkHttpEngine {
    public static OkHttpEngine mInstance;
    private OkHttpClient mOkHttpClient;
    private static Context mContext;
    private static boolean mShowDialog = true;
    private Gson mGson;

    public static OkHttpEngine getInstance(Context context) {
        return getInstance(context, true);
    }

    public static OkHttpEngine getInstance(Context context, boolean showDialog) {
        mContext = context;
        mShowDialog = showDialog;
        if (mInstance == null) {
            synchronized (OkHttpEngine.class) {
                if (mInstance == null) {
                    mInstance = new OkHttpEngine(context);
                }
            }
        }
        return mInstance;
    }

    OkHttpClient.Builder mBuilder;

    private OkHttpEngine(Context context) {
        mContext = context;
        mGson = new Gson();
        mBuilder = new OkHttpClient.Builder();
        mBuilder.sslSocketFactory(createSSLSocketFactory(), mMyTrustManager).hostnameVerifier(new TrustAllHostnameVerifier());
        mBuilder.connectTimeout(50, TimeUnit.SECONDS).writeTimeout(50, TimeUnit.SECONDS).readTimeout(50, TimeUnit.SECONDS);
        mOkHttpClient = mBuilder.build();
    }

    public OkHttpEngine setCache(Context mContext) {
        File sdcache = mContext.getExternalCacheDir();
        int cacheSize = 10 * 1024 * 1024;
        mBuilder.cache(new Cache(sdcache.getAbsoluteFile(), cacheSize));
        return mInstance;
    }

    /**
     * 异步get请求
     *
     * @param url
     * @param callback
     */
    public void getAsyncHttp(String url, ResultCallback callback) {
        if (InternetUtils.internet(mContext)) {
            final Request request = new Request.Builder()
                    .url(url)
                    .build();
            Call call = mOkHttpClient.newCall(request);
            dealResultForCover(call, callback);
        } else {
            callback.onError(null, null);
        }
    }

    public void postAsyncHttp(String url, String json, ResultCallback callback) {
        postAsyncHttp(url, json, callback, false);
    }

    /**
     * 异步post请求
     *
     * @param url
     * @param json
     * @param callback
     * @param keepGoing 没网情况下是否还是继续往下走
     */
    public void postAsyncHttp(String url, String json, ResultCallback callback, boolean keepGoing) {
        if (InternetUtils.internet(mContext)) {
            if (ReaderConfig.API_CRYPTOGRAPHY.equals(App.getCipherApi())) {
                ReaderNameValuePair readerNameValuePair = new ReaderNameValuePair();
                readerNameValuePair.put("c", AESUtil.encrypt(json, AESUtil.API_ASE_KEY, AESUtil.API_IV));
                json = readerNameValuePair.toJson();
                MyToash.Log("encryptPost:" + json);
            }
            final Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
                    .build();
            Call call = mOkHttpClient.newCall(request);
            dealResult(call, callback);
        } else {
            callback.onError(null, null);
        }
    }

    private void dealResultForCover(Call call, final ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                sendFailedCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallback(response, callback);
            }

            private void sendSuccessCallback(final Response object, final ResultCallback callback) {
                if (callback != null) {
                    callback.onResponse(object);
                } else {
                    callback.onError(null, null);
                }
            }

            private void sendFailedCallback(final Request request, final Exception e, final ResultCallback callback) {
                if (callback != null) {
                    callback.onError(request, e);
                } else {
                    callback.onError(null, null);
                }
            }
        });
    }

    private void dealResult(Call call, final ResultCallback callback) {
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                sendFailedCallback(call.request(), e, callback);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                sendSuccessCallback(response, callback);
            }

            private void sendSuccessCallback(final Response object, final ResultCallback callback) {
                try {
                    callback.onResponse(object.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                    callback.onError(null, null);
                }

            }

            private void sendFailedCallback(final Request request, final Exception e, final ResultCallback callback) {
                if (callback != null) {
                    callback.onError(request, e);
                }
            }
        });
    }


    private MyTrustManager mMyTrustManager;

    private SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;
        try {
            mMyTrustManager = new MyTrustManager();
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{mMyTrustManager}, new SecureRandom());
            ssfFactory = sc.getSocketFactory();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }

        return ssfFactory;
    }

    /**
     * 实现X509TrustManager接口
     */
    public class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }

    /**
     * 实现HostnameVerifier接口(信任所有的 https 证书。)
     */
    private class TrustAllHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }
}



