package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.model.book.BaseBook;

import java.util.List;

public class RefreshBookSelf {
    public List<BaseBook> baseBooks;
    public boolean flag;
    public RefreshBookSelf(List<BaseBook> baseBooks) {
        this.baseBooks = baseBooks;
    }

    public RefreshBookSelf(boolean flag) {
        this.flag = flag;
    }
}
