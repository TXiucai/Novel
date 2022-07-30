package com.heiheilianzai.app.model;

import com.heiheilianzai.app.model.book.StroreBookcLable;

import java.util.List;

public class InfoBookItem {
    public InfoBook book;
    public List<BookInfoComment> comment;
    public List<StroreBookcLable> label;
    public BaseAd advert;

    public BaseAd getAdvert() {
        return advert;
    }

    public void setAdvert(BaseAd advert) {
        this.advert = advert;
    }

    public InfoBook getBook() {
        return book;
    }

    public void setBook(InfoBook book) {
        this.book = book;
    }

    public List<BookInfoComment> getComment() {
        return comment;
    }

    public void setComment(List<BookInfoComment> comment) {
        this.comment = comment;
    }

    public List<StroreBookcLable> getLabel() {
        return label;
    }

    public void setLabel(List<StroreBookcLable> label) {
        this.label = label;
    }
}
