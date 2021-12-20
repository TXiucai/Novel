package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;

public class NovelOpenOtherEvent {
    private ChapterItem mChapterItem;
    private BaseBook mBaseBook;

    public NovelOpenOtherEvent(ChapterItem mChapterItem, BaseBook mBaseBook) {
        this.mChapterItem = mChapterItem;
        this.mBaseBook = mBaseBook;
    }

    public ChapterItem getmChapterItem() {
        return mChapterItem;
    }

    public void setmChapterItem(ChapterItem mChapterItem) {
        this.mChapterItem = mChapterItem;
    }

    public BaseBook getmBaseBook() {
        return mBaseBook;
    }

    public void setmBaseBook(BaseBook mBaseBook) {
        this.mBaseBook = mBaseBook;
    }
}
