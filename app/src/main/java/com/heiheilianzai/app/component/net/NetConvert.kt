package com.heiheilianzai.app.component.net

import com.alibaba.fastjson.JSON
import com.drake.logcat.LogCat
import com.drake.net.convert.DefaultConvert
import com.drake.net.error.RequestParamsException
import com.drake.net.error.ResponseException
import com.drake.net.error.ServerResponseException
import com.drake.net.tag.RESPONSE
import com.drake.net.tag.TAG
import com.google.gson.Gson
import com.heiheilianzai.app.base.getApp
import com.heiheilianzai.app.model.LoginModel
import com.heiheilianzai.app.utils.decode.AESUtil
import com.umeng.umcrash.UMCrash
import com.yanzhenjie.kalle.Request
import com.yanzhenjie.kalle.Response
import com.yanzhenjie.kalle.simple.Result
import org.json.JSONObject
import java.lang.reflect.Type


/**
 * 传入标签[RESPONSE]则打印请求参数
 */
class NetConvert(success: String = "0") : DefaultConvert(success) {

    private val gson = Gson()

    override fun <S, F> convert(succeed: Type,
                                failed: Type,
                                request: Request,
                                response: Response,
                                result: Result<S, F>) {

        var body = response.body().string()
        val code = response.code()

        when {
            code in 200..299 -> { // 请求成功
                val jsonObject = JSONObject(body) // 获取JSON中后端定义的错误码和错误信息
                val serveCode = jsonObject.getString(this.code)
                when (serveCode) {
                    success -> {
                        if (jsonObject.optString("capi") == "1") { // 是否解密
                            body = AESUtil.decrypt(jsonObject.getString("data"), AESUtil.API_ASE_KEY, AESUtil.API_IV)
                        }
                        result.logResponseBody = JSON.parse(body).toString()
                        val tag = request.tag()
                        if (tag is TAG && tag.contains(RESPONSE)) LogCat.json(body)
                        result.success = if (succeed === String::class.java) body as S else body.parseBody(succeed)
                        return
                    }
                    "301" -> LoginModel.resetLogin(getApp())
                    else -> UMCrash.generateCustomLog(body, request.url().path) // 后端错误上传至友盟统计
                }
                result.failure = ResponseException(serveCode.toInt(), jsonObject.getString(message), request) as F
            }
            code in 400..499 -> throw RequestParamsException(code, request) // 请求参数错误
            code >= 500 -> throw ServerResponseException(code, request) // 服务器异常错误
        }
    }

    override fun <S> String.parseBody(succeed: Type): S? {
        return JSON.parseObject(this, succeed)
    }
}
