package com.live.eggplant.base.encrypt;

import java.io.Serializable;

/**
 * @author Baoger
 * @date on 2019/6/29 9:59
 * @description:
 */
public class EncryptUtils implements Serializable {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("encryptLib");
    }

    /**
     * 从第三方途径获取接口域名数据加解密
     *
     * @return
     */
    public static native String getGameKey();

    public static native String getUmengMv();

    /**
     * 用于接口加密
     * @return
     */
    public static native String getUmengIv();

    public static native String getUmengKv();

    /**
     * 网易云信聊天加密
     * @return
     */
    public static native String getNimKv();

    public static native String getNimIv();

    /**
     * 用于m3u8文件加解密、webp图片解密
     * @return
     */
    public static native String getVideoKv();

    /**
     * webp加密图片的偏移量
     * @return
     */
    public static native String getWebpImageIv();
}