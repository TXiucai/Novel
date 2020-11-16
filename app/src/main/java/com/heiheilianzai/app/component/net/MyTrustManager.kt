package com.heiheilianzai.app.component.net

import java.security.cert.X509Certificate
import javax.net.ssl.X509TrustManager

/**
 * 实现X509TrustManager接口
 */
class MyTrustManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
    override fun getAcceptedIssuers(): Array<X509Certificate> {
        return emptyArray()
    }
}