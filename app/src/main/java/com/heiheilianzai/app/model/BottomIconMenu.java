package com.heiheilianzai.app.model;

import java.util.List;

public class BottomIconMenu {

    public List<RBIcons> getList() {
        return list;
    }

    public void setList(List<RBIcons> list) {
        this.list = list;
    }

    public List<RBIcons> list;

    public static class RBIcons {
        String icon_selected;
        String icon_normal;
        String title;

        public String getIcon_selected() {
            return icon_selected;
        }

        public void setIcon_selected(String icon_selected) {
            this.icon_selected = icon_selected;
        }

        public String getIcon_normal() {
            return icon_normal;
        }

        public void setIcon_normal(String icon_normal) {
            this.icon_normal = icon_normal;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }
    }
}
