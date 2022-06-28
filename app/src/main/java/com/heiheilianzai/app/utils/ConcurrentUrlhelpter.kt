package com.heiheilianzai.app.utils

import com.drake.net.Get
import com.drake.net.NetConfig
import com.drake.net.tag.RESPONSE
import com.drake.net.transform.transform
import com.drake.net.utils.fastest
import com.drake.net.utils.scopeNet
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.heiheilianzai.app.BuildConfig
import com.heiheilianzai.app.base.App
import com.heiheilianzai.app.constant.RabbitConfig
import com.heiheilianzai.app.model.ThreeDomainEntity
import com.heiheilianzai.app.utils.decode.AESUtil
import kotlinx.coroutines.CoroutineScope
import okhttp3.*
import java.io.IOException
import java.util.concurrent.TimeUnit


var threeDomains: List<String>? = null
var localDomains: List<String>? = null


/**
 * 并发请求域名
 */
private suspend fun requestDomains(
    scope: CoroutineScope,
    domains: List<String>,
    onComplete: OnCompletUrl
) {

    val listTask = domains.map { domain ->
        scope.Get<String>(
            "${domain}/domain/check",
            absolutePath = true,
            tag = RESPONSE,
            uid = 0
        ).transform {
            App.setBaseUrl(domain)
            NetConfig.host = domain
            onComplete.onComplteApi(domain)
            it
        }
    }
    scope.fastest(listTask, 0)
}

/**
 * 并发请求H5域名
 */
private suspend fun requestH5Domains(scope: CoroutineScope, domains: List<String>) {
    val listTask = domains.map { domain ->
        scope.Get<String>(
            "${domain}/images/default-loading.jpg",
            absolutePath = true,
            tag = RESPONSE,
            uid = 0
        ).transform {
            App.setBaseh5Url(domain)
            NetConfig.host = domain
            it
        }
    }
    scope.fastest(listTask, 0)
}

@JvmOverloads
fun getFastUrl(onComplete: OnCompletUrl, onError: OnError) {
    //本地Baseurl
    if (BuildConfig.DEBUG) {
        localDomains = BuildConfig.api_host_uat.asList()
    } else {
        localDomains = BuildConfig.api_host.asList()
    }
    //服务端baseUrl
    val hostJson = ShareUitls.getDonminString(App.getAppContext(), "Donmain", "") as String
    val gson = Gson()
    val listType = object : TypeToken<List<String?>?>() {}.type
    val cacheDomains = gson.fromJson<List<String>>(hostJson, listType) ?: localDomains
    scopeNet {
        try {
            // 请求本地或者缓存域名列表
            if (cacheDomains != null) {
                requestDomains(this, cacheDomains, onComplete)
            }
        } catch (e: Exception) {
            try {
                // 请求本地域名列表
                requestDomains(this, localDomains!!, onComplete)
            } catch (e: Exception) {
                // 请求第三方域名列表
                threeDomains?.let { requestDomains(this, it, onComplete) }
            }
        }
    }.catch {
        onError.onError()
    }
}

@JvmOverloads
fun getFastH5Url(h5domains: List<String>) {
    scopeNet {
        requestH5Domains(this, h5domains)
    }.catch {

    }
}

@JvmOverloads
fun requestThree(onThirdComlete: OnThirdComlete, onThirdError: OnThirdError) {
    val path = if (BuildConfig.DEBUG) {
        if (BuildConfig.free_charge) {
            RabbitConfig.JK_DOMAIN_DEBUGG
        } else {
            RabbitConfig.DOMAIN_DEBUGG
        }
    } else {
        if (BuildConfig.free_charge) {
            RabbitConfig.JK_DOMAIN_RELEASE
        } else {
            RabbitConfig.DOMAIN_RELEASE
        }
    }
    val okHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)
        .build()
    val request = Request.Builder()
        .url(path)
        .get()
        .addHeader("Accept", "application/json")
        .addHeader("Connection", "close").build()
    okHttpClient
        .newCall(request)
        .enqueue(object : Callback {
            override fun onResponse(arg0: Call, response: Response) {
                onThirdResponse(response, onThirdComlete)
            }

            override fun onFailure(arg0: Call, arg1: IOException) {
                val s = arg1.toString()
                onThirdError.onThirdCError()
            }
        })
}

private fun onThirdResponse(response: Response, onThirdComlete: OnThirdComlete) {
    var result: String? = null
    if (response.body() != null) {
        result = response.body()?.string()
    }
    if (result == null) {
        return
    }
    val decryptThirdDomainString =
        AESUtil.decrypt(result.trim(), AESUtil.API_ASE_KEY, AESUtil.API_IV)
    if (decryptThirdDomainString != null) {
        val bootStrap = Gson().fromJson(decryptThirdDomainString, ThreeDomainEntity::class.java)
        if (bootStrap != null && bootStrap.api_domains.size > 0) {
            threeDomains = bootStrap.api_domains
            onThirdComlete.onThirdComplete()
        }
    }
}


interface OnCompletUrl {
    fun onComplteApi(api: String)
}

interface OnError {
    fun onError()
}

interface OnThirdComlete {
    fun onThirdComplete()
}

interface OnThirdError {
    fun onThirdCError()
}