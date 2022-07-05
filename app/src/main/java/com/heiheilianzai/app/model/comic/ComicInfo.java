package com.heiheilianzai.app.model.comic;

import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BookInfoComment;

import java.util.List;

public class ComicInfo {
    public StroreComicLable.Comic comic;
    public List<BookInfoComment> comment;
    public List<StroreComicLable> label;
    public BaseAd advert;
    public int is_pure_gold_read;
    public String introduce_text;
}
