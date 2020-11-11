/*
 * Copyright (C) 2018, Umbrella CompanyLimited All rights reserved.
 * Project：Engine
 * Author：Drake
 * Date：9/11/19 7:25 PM
 */

package com.heiheilianzai.app.base

import android.app.Application
import com.drake.net.initNet
import com.heiheilianzai.app.component.net.MyTrustManager
import com.heiheilianzai.app.component.net.NetConvert
import com.heiheilianzai.app.component.net.NetSignInterceptor
import com.heiheilianzai.app.component.net.TrustAllHostnameVerifier
import com.heiheilianzai.app.constant.RabbitConfig
import com.yanzhenjie.kalle.connect.http.RetryInterceptor
import java.security.SecureRandom
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager


private lateinit var application: Application

fun Application.initEngine() {
    application = this
    initNet(RabbitConfig.BASE_URL) {
        converter(NetConvert())
        val trustManager = MyTrustManager()
        val sc = SSLContext.getInstance("TLS")
        sc.init(null, arrayOf<TrustManager>(trustManager), SecureRandom())
        setLogRecord(true)
        connectionTimeout(1, TimeUnit.MINUTES)
        readTimeout(1, TimeUnit.MINUTES)
        hostnameVerifier(TrustAllHostnameVerifier())
        addInterceptor(RetryInterceptor(3))
        addInterceptor(NetSignInterceptor())
    }
}

fun getApp(): Application {
    return application
}

