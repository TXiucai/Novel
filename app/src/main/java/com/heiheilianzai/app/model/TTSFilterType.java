package com.heiheilianzai.app.model;

import java.util.List;

/**
 * TTS 不支持的 字符格式集合
 * {"list":["0xC2 0xA0"]}
 */
public class TTSFilterType {
    public List<String> list;

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }
}
