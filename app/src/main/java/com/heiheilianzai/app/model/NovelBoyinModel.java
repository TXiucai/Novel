package com.heiheilianzai.app.model;

public class NovelBoyinModel {

    /**
     * nid : 76
     * name : 香村花娇
     * first_chapter_id : 225
     */

    private SoundBookInfoBean sound_book_info;

    public SoundBookInfoBean getSound_book_info() {
        return sound_book_info;
    }

    public void setSound_book_info(SoundBookInfoBean sound_book_info) {
        this.sound_book_info = sound_book_info;
    }

    public static class SoundBookInfoBean {
        private int nid;
        private String name;
        private int first_chapter_id;

        public int getNid() {
            return nid;
        }

        public void setNid(int nid) {
            this.nid = nid;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getFirst_chapter_id() {
            return first_chapter_id;
        }

        public void setFirst_chapter_id(int first_chapter_id) {
            this.first_chapter_id = first_chapter_id;
        }
    }
}
