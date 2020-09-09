package com.heiheilianzai.app.component.http;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpHelper {


    private OkHttpClient okHttpClient;
    private RequestBody requestBody;
    private Request request;
    private Handler mHandler;
    private String url;
    private Context mContext;
    private String method = "get";

    private volatile static OkHttpHelper instance;
    public static final int HTTP_SUCCESS = 1;
    public static final int HTTP_FAILURE = 0;
    public static class ReqMethod {
        public static String POST = "post";
        public static String GET = "get";
    }

    public OkHttpHelper(Context context) {
        this.mContext = context;
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(10000, TimeUnit.MICROSECONDS);

        X509TrustManager trustManager;
        SSLSocketFactory sslSocketFactory;
        try {
            trustManager = trustManagerForCertificates(trustedCertificatesInputStream());
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[] { trustManager }, null);
            sslSocketFactory = sslContext.getSocketFactory();
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e);
        }
        builder.sslSocketFactory(sslSocketFactory, trustManager);
        builder.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                Certificate[] localCertificates = new Certificate[0];
                try {
                    //获取证书链中的所有证书
                    localCertificates = session.getPeerCertificates();
                } catch (SSLPeerUnverifiedException e) {
                    e.printStackTrace();
                }
                //打印所有证书内容
                for (Certificate c : localCertificates) {
                    Log.d(" OkHttpHelper", "verify: "+c.toString());
                }
                return true;
            }
        });
        okHttpClient = builder.build();
    }

    public static OkHttpHelper newInstance(Context context){
        if (instance == null){
            synchronized (OkHttpHelper.class){
                if (instance == null){
                    instance = new OkHttpHelper(context);
                }
            }
        }
        return instance;
    }

    public OkHttpHelper addRequestBody(RequestBody body){
        this.requestBody = body;
        return this;
    }

    public OkHttpHelper addUrl(String url){
        this.url = url;
        return this;
    }

    public OkHttpHelper setMethod(String method) {
        this.method = method;
        return this;
    }

    public OkHttpHelper addHandler(Handler handler){
        this.mHandler = handler;
        return this;
    }

    public void start(){
        if (method.equals(ReqMethod.POST)){
            if (requestBody==null){
                return;
            }
            request = new Request.Builder()
                    .url(url)
                    .addHeader("Accept","application/json; charset=utf-8")
                    .post(requestBody)
                    .tag(url)
                    .build();
        } else {
            request = new Request.Builder()
                    .url(url)
                    .tag(url)
                    .build();
        }

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                if (mHandler!=null){
                    Message message = new Message();
                    message.what = 0;
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("IOException",e);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }

            @Override
            public void onResponse(final Call call, Response response) throws IOException {
                final String rep = response.body().string();
                if (rep!=null&&mHandler!=null){
                    Message message = new Message();
                    message.what = 1;
                    Bundle bundle = new Bundle();
                    bundle.putString("Response",rep);
                    message.setData(bundle);
                    mHandler.sendMessage(message);
                }
            }
        });
    }

    private InputStream trustedCertificatesInputStream() {
        InputStream inputStream = null;
        try {
            inputStream = mContext.getAssets().open("yghq.cer");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return inputStream;
    }

    private X509TrustManager trustManagerForCertificates(InputStream in)
            throws GeneralSecurityException {
        CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
        Collection<? extends Certificate> certificates = certificateFactory.generateCertificates(in);
        if (certificates.isEmpty()) {
            throw new IllegalArgumentException("expected non-empty set of trusted certificates");
        }

        char[] password = "password".toCharArray();
        // Put the certificates a key store.
        KeyStore keyStore = newEmptyKeyStore(password);
        int index = 0;
        for (Certificate certificate : certificates) {
            Log.d("OkHttpHelper", "trustManagerForCertificates: "+certificate.toString());
            String certificateAlias = Integer.toString(index++);
            keyStore.setCertificateEntry(certificateAlias, certificate);
        }
        // Use it to build an X509 trust manager.
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(
                KeyManagerFactory.getDefaultAlgorithm());
        keyManagerFactory.init(keyStore, password);
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(
                TrustManagerFactory.getDefaultAlgorithm());
        trustManagerFactory.init(keyStore);
        TrustManager[] trustManagers = trustManagerFactory.getTrustManagers();
        if (trustManagers.length != 1 || !(trustManagers[0] instanceof X509TrustManager)) {
            throw new IllegalStateException("Unexpected default trust managers:"
                    + Arrays.toString(trustManagers));
        }
        return (X509TrustManager) trustManagers[0];
    }

    private KeyStore newEmptyKeyStore(char[] password) throws GeneralSecurityException {
        try {
            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            InputStream in = null;
            keyStore.load(in,password);
            return keyStore;
        } catch (IOException e) {
            throw new AssertionError(e);
        }
    }

}
