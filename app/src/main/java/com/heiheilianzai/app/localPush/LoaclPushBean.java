package com.heiheilianzai.app.localPush;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LoaclPushBean implements Serializable {

    /**
     * id : 2
     * push_type : 0
     * push_title : 特斯拉44
     * push_content : 受到广泛的是
     * push_target : 2
     * push_way : 0
     * jump_url :
     * redirect_type : 1
     * user_parame_need : 1
     * jump_page_type : 3
     * jump_content_type : 1
     * jump_content_id : 0
     * status : 0
     * release_date_start : 2022-03-04
     * release_date_end : 2022-03-26
     * release_time_start_push_way : 00:00:00
     * release_time_start_redundant_week : 13:40:00
     */

    private String id;
    private String push_type;
    private String push_title;
    private String push_content;
    private String push_target;
    private String push_way;
    private String jump_url;
    private String redirect_type;
    private String user_parame_need;
    private String jump_page_type;
    private String jump_content_type;
    private String jump_content_id;
    private String status;
    private String release_date_start;
    private String release_date_end;
    private String release_time_start_push_way;
    private String release_time_start_redundant_week;

    public static LoaclPushBean from(String content) throws IOException, ClassNotFoundException {
        ByteArrayInputStream bin = new ByteArrayInputStream(content.getBytes("ISO-8859-1"));
        ObjectInputStream ois = null;
        LoaclPushBean obj = null;
        ois = new ObjectInputStream(bin);
        obj = (LoaclPushBean) ois.readObject();
        ois.close();
        bin.close();
        return obj;
    }

    public static String to(LoaclPushBean obj) throws IOException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = null;
        String content = null;
        oos = new ObjectOutputStream(bout);
        oos.writeObject(obj);
        oos.close();
        byte[] bytes = bout.toByteArray();
        content = new String(bytes, "ISO-8859-1");
        bout.close();
        return content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPush_type() {
        return push_type;
    }

    public void setPush_type(String push_type) {
        this.push_type = push_type;
    }

    public String getPush_title() {
        return push_title;
    }

    public void setPush_title(String push_title) {
        this.push_title = push_title;
    }

    public String getPush_content() {
        return push_content;
    }

    public void setPush_content(String push_content) {
        this.push_content = push_content;
    }

    public String getPush_target() {
        return push_target;
    }

    public void setPush_target(String push_target) {
        this.push_target = push_target;
    }

    public String getPush_way() {
        return push_way;
    }

    public void setPush_way(String push_way) {
        this.push_way = push_way;
    }

    public String getJump_url() {
        return jump_url;
    }

    public void setJump_url(String jump_url) {
        this.jump_url = jump_url;
    }

    public String getRedirect_type() {
        return redirect_type;
    }

    public void setRedirect_type(String redirect_type) {
        this.redirect_type = redirect_type;
    }

    public String getUser_parame_need() {
        return user_parame_need;
    }

    public void setUser_parame_need(String user_parame_need) {
        this.user_parame_need = user_parame_need;
    }

    public String getJump_page_type() {
        return jump_page_type;
    }

    public void setJump_page_type(String jump_page_type) {
        this.jump_page_type = jump_page_type;
    }

    public String getJump_content_type() {
        return jump_content_type;
    }

    public void setJump_content_type(String jump_content_type) {
        this.jump_content_type = jump_content_type;
    }

    public String getJump_content_id() {
        return jump_content_id;
    }

    public void setJump_content_id(String jump_content_id) {
        this.jump_content_id = jump_content_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRelease_date_start() {
        return release_date_start;
    }

    public void setRelease_date_start(String release_date_start) {
        this.release_date_start = release_date_start;
    }

    public String getRelease_date_end() {
        return release_date_end;
    }

    public void setRelease_date_end(String release_date_end) {
        this.release_date_end = release_date_end;
    }

    public String getRelease_time_start_push_way() {
        return release_time_start_push_way;
    }

    public void setRelease_time_start_push_way(String release_time_start_push_way) {
        this.release_time_start_push_way = release_time_start_push_way;
    }

    public String getRelease_time_start_redundant_week() {
        return release_time_start_redundant_week;
    }

    public void setRelease_time_start_redundant_week(String release_time_start_redundant_week) {
        this.release_time_start_redundant_week = release_time_start_redundant_week;
    }
}
