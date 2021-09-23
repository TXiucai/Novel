package com.heiheilianzai.app.model;

public class AdType {
    /**
     * 开屏广告
     */
    public static final int TYPE_SPLASH = 1;
    /**
     * Banner广告
     */
    public static final int TYPE_BANNER = 2;
    /**
     * 轮播广告
     */
    public static final int TYPE_SLIDESHOW = 3;
    /**
     * 贴片图广告
     */
    public static final int TYPE_IMAGE = 4;
    /**
     * 信息流广告
     */
    public static final int TYPE_NATIVE_EXPRESS = 5;
    /**
     * 插屏广告
     */
    public static final int TYPE_INTERSTITIAL = 6;
    /**
     * App推荐广告
     */
    public static final int TYPE_APP = 7;
    /**
     * 悬浮广告
     */
    public static final int TYPE_FLOAT = 8;
    /**
     * Draw视频流广告
     */
    public static final int TYPE_DRAW_FEED = 9;

    /**
     * 默认，广告数据的处理由接入APP自己实现
     * 自定义广告类型，展示效果由接入APP自己实现
     */
    public static final int CUSTOM_TYPE_DEFAULT = 99;
}
