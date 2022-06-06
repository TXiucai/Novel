package com.live.eggplant.widget.video

import android.text.TextUtils
import com.heiheilianzai.app.constant.ReaderConfig
import com.live.eggplant.player.builder.GSYVideoOptionBuilder
import com.live.eggplant.player.player.IjkPlayerManager
import java.net.URI
import java.util.*

/**
 * @date on 2020/10/7
 * @description:
 */
object GSYVideoOptionHelper {

    /**
     * 配置播放器
     *
     * @param url       播放地址
     * @param title     视频标题
     * @param isEncrypt m3u8文件是否加密，只有老版本下载的本地视频播放时为非加密的，其他时候为true
     */
    fun getGSYVideoOptionBuilder(
        url: String?, title: String?,
        isEncrypt: Boolean
    ): GSYVideoOptionBuilder? {
        return if (!TextUtils.isEmpty(url)) {
            val mMapHeadDat: Map<String, String> = HashMap()
            IjkPlayerManager.sIsEncrypt = isEncrypt
            GSYVideoOptionBuilder()
                .setUrl(url)
                .setMapHeadData(mMapHeadDat)
                .setCacheWithPlay(true)
                .setVideoTitle(title)
                .setIsTouchWiget(true)
                .setAutoFullWithSize(true)
                .setRotateViewAuto(true)
                .setLockLand(true) // .setAutoFullWithSize(true) //单向旋转屏幕
                .setShowFullAnimation(false) //打开动画
                .setNeedLockFull(true)
                .setSeekRatio(1f)
        } else {
            GSYVideoOptionBuilder()
                .setCacheWithPlay(true)
                .setVideoTitle(title)
                .setIsTouchWiget(true)
                .setLockLand(true)
                .setSeekRatio(1f)
        }
    }
}