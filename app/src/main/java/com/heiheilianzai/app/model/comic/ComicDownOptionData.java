package com.heiheilianzai.app.model.comic;

import java.util.List;

public class ComicDownOptionData {

    public static class Base_info {
        public int total_chapters;//": 50, //总章节数
        public String display_label;//": "连载中 共214话" //上方label条

    }
    public Base_info base_info;
    public List<ComicChapter> down_list;

}
