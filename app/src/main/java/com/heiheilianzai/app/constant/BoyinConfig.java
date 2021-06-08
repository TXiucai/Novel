package com.heiheilianzai.app.constant;

public class BoyinConfig {
    /**
     * 下载音频的接口
     * 参数 nid   小说ID   mobile  手机号   user_source  用户渠道来源
     */
    public static final String DOWN_BOYIN = "/auth/down-audio";

    /**
     * 有声历史播放记录
     */
    public static final String PHONIC_AUDIO_READ_LOG = "/auth/my-audio";

    /**
     * 有声历史播放记录删除
     */
    public static final String PHONIC_REMOVE_AUDIO_READ_LOG = "/auth/remove-audio";

    /**
     * 有声历史播放记录删除多个
     */
    public static final String PHONIC_REMOVE_AUDIO_READ_LOG_MORE= "/auth/removeall-audio";


}
