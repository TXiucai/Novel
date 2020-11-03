package com.heiheilianzai.app.model.event;

/**
 * 删除所有章节后对下载缓存列表刷新。
 */
public class DownMangerDeleteAllChapterEvent {
    public int type; // 页面参数  参考 BaseDownMangerFragment 1-小说 2-漫画 3-有声
    public int id;//作品ID

    public DownMangerDeleteAllChapterEvent(int type) {
        this.type = type;
    }
}