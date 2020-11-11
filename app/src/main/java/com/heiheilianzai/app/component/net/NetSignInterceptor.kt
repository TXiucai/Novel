package com.heiheilianzai.app.component.net

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.heiheilianzai.app.BuildConfig
import com.heiheilianzai.app.base.App
import com.heiheilianzai.app.base.getApp
import com.heiheilianzai.app.constant.RabbitConfig
import com.heiheilianzai.app.constant.ReaderConfig
import com.heiheilianzai.app.utils.UpdateApp
import com.heiheilianzai.app.utils.Utils
import com.heiheilianzai.app.utils.decode.AESUtil
import com.yanzhenjie.kalle.JsonBody
import com.yanzhenjie.kalle.Response
import com.yanzhenjie.kalle.connect.Interceptor
import com.yanzhenjie.kalle.connect.http.Chain
import com.yanzhenjie.kalle.simple.SimpleBodyRequest
import com.yanzhenjie.kalle.simple.SimpleUrlRequest

class NetSignInterceptor : Interceptor {

    override fun intercept(chain: Chain): Response {

        val request = chain.request()
        if (request is SimpleUrlRequest) {
            return chain.proceed(request)
        }

        val params = request.copyParams()
        val mapParams = hashMapOf<String, String>()

        val appId = BuildConfig.app_id
        val osType = Utils.getOsType()
        val product = Utils.getProduct()
        val sysVer = Utils.getSystemVersion()
        val time: String = (System.currentTimeMillis() / 1000).toString()
        val token = Utils.getToken(getApp())
        val uuid = Utils.getUUID(getApp())
        val ver = Utils.getAppVersionName(getApp())
        val marketChannel = UpdateApp.getChannelName(getApp())

        val listSign = mutableListOf<String>().apply {
            add("appId=$appId")
            add("osType=$osType")
            add("product=$product")
            add("sysVer=$sysVer")
            add("time=$time")
            add("token=$token")
            add("udid=$uuid")
            add("ver=$ver")
            add("marketChannel=$marketChannel")
            add("packageName=${BuildConfig.APPLICATION_ID}")
        }

        params.entrySet().forEach {
            val adjustValue = it.value[0] as? String ?: ""
            listSign.add("${it.key}=$adjustValue")
            mapParams[it.key] = adjustValue
        }

        val sign = listSign.sorted().joinToString("&", RabbitConfig.mAppkey, RabbitConfig.mAppSecretKey)

        val md5OfSign = Utils.MD5(sign)
        mapParams["appId"] = appId
        mapParams["osType"] = osType
        mapParams["product"] = product
        mapParams["sysVer"] = sysVer
        mapParams["time"] = time
        mapParams["token"] = token
        mapParams["udid"] = uuid
        mapParams["ver"] = ver
        mapParams["packageName"] = BuildConfig.APPLICATION_ID
        mapParams["marketChannel"] = marketChannel
        mapParams["sign"] = md5OfSign

        val isEncrypt = ReaderConfig.API_CRYPTOGRAPHY == App.getCipherApi()
        val requestJson = JSON.toJSON(mapParams).toString()

        val jsonBody = if (isEncrypt) {
            val encryptJson = JSONObject()
            encryptJson["c"] = AESUtil.encrypt(requestJson, AESUtil.API_ASE_KEY, AESUtil.API_IV)
            JsonBody(encryptJson.toString())
        } else {
            JsonBody(requestJson)
        }

        val newRequest = SimpleBodyRequest.newBuilder(request.url(), request.method())
                .body(jsonBody)
                .setHeaders(request.headers())
                .tag(request.tag())
                .build()

        return chain.proceed(newRequest)
    }

}
