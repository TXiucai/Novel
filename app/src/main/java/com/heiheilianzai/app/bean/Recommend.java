package com.heiheilianzai.app.bean;

import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.comic.been.BaseComic;

import java.util.List;

/**
 * Created by scb on 2018/11/19.
 */

public class Recommend {
   public List<RecommendProduc> book;
    public  List<RecommendProduc> comic;

    public class RecommendProduc {
        public String comic_id;//": 536,
        public String book_id;//": 536,
        public String name;//": "带着萌宝向前冲",
        public String cover;
        public String description;//": "五年前，她稀里糊涂地走错了房间爬到了别人的床上，连男人的样貌都没有记住却留了种……五年后，处处和她作对的林安冲出来说，要抢了她孩子的父亲，笑话，她连孩子他爹都不知道是谁....",
        public int total_chapter;//": 93
        public  boolean isChoose;

    }

}
