package com.heiheilianzai.app.model.boyin;

import com.heiheilianzai.app.model.BaseAd;

import java.util.List;

public class PhonicReadHistory {
    public int total_page;
    public int current_page;
    public int total_count;
    public List<PhonicReadHistory.PhonicInfo> data_list;

    public static class PhonicInfo extends BaseAd {
        private int anchor_id; //主播id
        private String anchor_name;//主播名
        private String anchor_img;//主播照片
        private int id; //小说id
        private String name;//小说名
        private int total_numbers;//小说总章节数
        private String img;//封面
        private String introduction;
        private int update_status;
        private int listen_type;
        private int listen;
        private int listen_true;
        private String category_name;//类别
        private int last_read_chapter_id;//当前章节id
        private int current_numbers;//当前章节位置
        private String current_time;//当前章节播放进度
        private String record_title;//章节名
        private String last_chapter_time;//小说更新时间


        public int getAnchor_id() {
            return anchor_id;
        }

        public void setAnchor_id(int anchor_id) {
            this.anchor_id = anchor_id;
        }

        public String getAnchor_name() {
            return anchor_name;
        }

        public void setAnchor_name(String anchor_name) {
            this.anchor_name = anchor_name;
        }

        public String getAnchor_img() {
            return anchor_img;
        }

        public void setAnchor_img(String anchor_img) {
            this.anchor_img = anchor_img;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImg() {
            return img;
        }

        public void setImg(String img) {
            this.img = img;
        }

        public String getIntroduction() {
            return introduction;
        }

        public void setIntroduction(String introduction) {
            this.introduction = introduction;
        }

        public int getUpdate_status() {
            return update_status;
        }

        public void setUpdate_status(int update_status) {
            this.update_status = update_status;
        }

        public int getListen_type() {
            return listen_type;
        }

        public void setListen_type(int listen_type) {
            this.listen_type = listen_type;
        }

        public int getListen() {
            return listen;
        }

        public void setListen(int listen) {
            this.listen = listen;
        }

        public int getListen_true() {
            return listen_true;
        }

        public void setListen_true(int listen_true) {
            this.listen_true = listen_true;
        }

        public String getCategory_name() {
            return category_name;
        }

        public void setCategory_name(String category_name) {
            this.category_name = category_name;
        }

        public int getTotal_numbers() {
            return total_numbers;
        }

        public void setTotal_numbers(int total_numbers) {
            this.total_numbers = total_numbers;
        }

        public int getCurrent_numbers() {
            return current_numbers;
        }

        public void setCurrent_numbers(int current_numbers) {
            this.current_numbers = current_numbers;
        }

        public int getLast_read_chapter_id() {
            return last_read_chapter_id;
        }

        public void setLast_read_chapter_id(int last_read_chapter_id) {
            this.last_read_chapter_id = last_read_chapter_id;
        }

        public String getCurrent_time() {
            return current_time;
        }

        public void setCurrent_time(String current_time) {
            this.current_time = current_time;
        }

        public String getRecord_title() {
            return record_title;
        }

        public void setRecord_title(String record_title) {
            this.record_title = record_title;
        }

        public String getLast_chapter_time() {
            return last_chapter_time;
        }

        public void setLast_chapter_time(String last_chapter_time) {
            this.last_chapter_time = last_chapter_time;
        }
    }
}