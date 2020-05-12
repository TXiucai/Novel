package com.heiheilianzai.app.comic.eventbus;

import com.heiheilianzai.app.comic.been.ComicChapter;

public class ComicChapterEventbus {
    public int Flag;
    public ComicChapter comicChapter;

    public ComicChapterEventbus(int flag, ComicChapter comicChapter) {
        Flag = flag;
        this.comicChapter = comicChapter;
    }
}
