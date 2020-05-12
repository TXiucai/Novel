package com.heiheilianzai.app.book.been;

public class StoreEventbusBook {
    public  boolean chooseWho;

    public  int Y;

    public StoreEventbusBook(boolean chooseWho, int y) {
        this.chooseWho = chooseWho;
        Y = y;
    }

    public boolean isChooseWho() {
        return chooseWho;
    }

    public void setChooseWho(boolean chooseWho) {
        this.chooseWho = chooseWho;
    }

    public int getY() {
        return Y;
    }

    public void setY(int y) {
        Y = y;
    }
}
