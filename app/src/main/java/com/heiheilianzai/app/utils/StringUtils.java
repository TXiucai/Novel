package com.heiheilianzai.app.utils;

import android.content.ClipboardManager;
import android.content.Context;

import com.heiheilianzai.app.constant.ReaderConfig;

import java.util.Arrays;
import java.util.List;

/**
 * 与字符串相关工具类
 */
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
     * 图片url 是否是 gif
     *
     * @param imgeUrl
     * @return
     */
    public static boolean isImgeUrlGifEncryptPostfix(String imgeUrl) {
        if (isEmpty(imgeUrl)) {
            return false;
        }
        return imgeUrl.contains(".gif");
    }

    /**
     * String前缀是否是http
     *
     * @param imgeUrl
     * @return
     */
    public static boolean isHttpPrefix(String imgeUrl) {
        if (isEmpty(imgeUrl) || imgeUrl.length() < 4) {
            return false;
        }
        return imgeUrl.substring(0, 4).equals("http");
    }

    /**
     * 设置内容到粘贴板 并弹出相关提示
     *
     * @param context
     * @param str     放入粘切板内容
     */
    public static void setStringInClipboard(Context context, String str) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(str);
    }

    /**
     * String 转 List
     */
    public static List<String> getStringToList(String c, String regex) {
        if (!isEmpty(c)) {
            return Arrays.asList(c.split(regex));
        }
        return null;
    }
}