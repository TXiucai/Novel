package com.heiheilianzai.app.eventbus;

import com.heiheilianzai.app.book.been.BaseBook;

import java.util.List;

/**
 * Created by scb on 2018/8/8.
 */
public class RefreshList {

    public List<BaseBook> baseBooks;
    public RefreshList(List<BaseBook> baseBooks) {
        this.baseBooks = baseBooks;
    }
}
