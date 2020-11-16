package com.heiheilianzai.app.component.net

import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLSession

/**
 * 实现HostnameVerifier接口(信任所有的 https 证书。)
 */
class TrustAllHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean {
        return true
    }
}