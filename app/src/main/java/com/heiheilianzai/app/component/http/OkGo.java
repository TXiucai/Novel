package com.heiheilianzai.app.component.http;




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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkGo {

    private  static OkHttpClient client;

    public static OkHttpClient getClient(){
        if(null == client){
           client = new OkHttpClient();
            OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
            mBuilder .connectTimeout(10,TimeUnit.SECONDS).writeTimeout(15, TimeUnit.SECONDS).readTimeout(15, TimeUnit.SECONDS);
            mBuilder.sslSocketFactory(createSSLSocketFactory(), mMyTrustManager)
                    .hostnameVerifier(new TrustAllHostnameVerifier());
            client =mBuilder.build();
        }
        return client;
    }

    public static Response post(String url, String name, String status) throws IOException {
        RequestBody requestBody = new FormBody.Builder().add("name",name).add("status",status).build();
        Request request = new Request.Builder().url(url).post(requestBody).build();
        return getClient().newCall(request).execute();
    }

    public static String get(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return getClient().newCall(request).execute().body().string();
    }

    public static Response getResponse(String url) throws IOException {
        Request request = new Request.Builder().url(url).get().build();
        return getClient().newCall(request).execute();
    }


    private static MyTrustManager mMyTrustManager;
    private static SSLSocketFactory createSSLSocketFactory() {
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
    public static class MyTrustManager implements X509TrustManager {

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
    private static class TrustAllHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    }

}
