package com.heiheilianzai.app.utils;

import com.heiheilianzai.app.config.ReaderConfig;

public class StringUtils {

    public static boolean isEmpty(String var0) {
        return var0 == null || "".equals(var0);
    }

    /**
     * 图片url 是否使用了加密后缀
     *
     * @param imgeUrl
     * @return
     */
    public static boolean isImgeUrlEncryptPostfix(String imgeUrl) {
        if (isEmpty(imgeUrl)) {
            return false;
        }
        return imgeUrl.substring(imgeUrl.length() - 2, imgeUrl.length()).equals(ReaderConfig.IMG_CRYPTOGRAPHIC_POSTFIX);
    }

    /**
     * String前缀是否是http
     * @param imgeUrl
     * @return
     */
    public static boolean isHttpPrefix(String imgeUrl) {
        if (isEmpty(imgeUrl)||imgeUrl.length()<4) {
            return false;
        }
        return imgeUrl.substring(0, 4).equals("http");
    }
}
