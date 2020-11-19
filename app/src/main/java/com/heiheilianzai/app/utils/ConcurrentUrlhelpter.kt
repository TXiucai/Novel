package com.heiheilianzai.app.utils

import com.drake.net.Get
import com.drake.net.NetConfig
import com.drake.net.tag.RESPONSE
import com.drake.net.transform.transform
import com.drake.net.utils.fastest
import com.drake.net.utils.scopeNet
import com.heiheilianzai.app.base.App
import kotlinx.coroutines.CoroutineScope

/**
 * 并发请求域名
 */
private suspend fun requestDomains(scope: CoroutineScope, domains: List<String>, onComplete: OnCompletUrl) {
    val listTask = domains.map { domain ->
        scope.Get<String>(
                "${domain}/site/check",
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
fun getFastUrl(domains: List<String>, onComplete: OnCompletUrl, onError: OnError) {
    scopeNet {
        requestDomains(this, domains, onComplete)
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

interface OnCompletUrl {
    fun onComplteApi(api: String)
}

interface OnError {
    fun onError()
}