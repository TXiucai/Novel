package com.heiheilianzai.app.model.event.comic;

import com.heiheilianzai.app.model.comic.ComicChapter;

public class ComicChapterEventbus {
    public int Flag;
    public ComicChapter comicChapter;

    public ComicChapterEventbus(int flag, ComicChapter comicChapter) {
        Flag = flag;
        this.comicChapter = comicChapter;
    }
}
