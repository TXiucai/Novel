package com.heiheilianzai.app.model;

import java.io.Serializable;

public class BaseTag implements Serializable {
    public String tab;
    public String color;

    public String getTab() {
        return tab;
    }

    public void setTab(String tab) {
        this.tab = tab;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return "Tag{" +
                "tab='" + tab + '\'' +
                ", color='" + color + '\'' +
                '}';
    }
}
