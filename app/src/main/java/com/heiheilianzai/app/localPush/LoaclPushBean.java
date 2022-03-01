package com.heiheilianzai.app.localPush;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class LoaclPushBean implements Serializable {
    /**
     * id : 1
     * push_type : 0
     * push_title : 特斯拉
     * push_content : 的非官方的规划
     * push_target : 0
     * push_way : 0
     * jump_url :
     * redirect_type : null
     * jump_page_type : null
     * jump_content_type : null
     * jump_content_id : 0
     * osType : 0
     * min_version : 2.2.0
     * max_version : 3.1.1
     * user_type : 0
     * user_vip_type : 0
     * account_type : 0
     * status : 0
     * release_date_start : 2022-02-25
     * release_date_end : 2022-03-10
     * release_time_start_push_way : 00:00:00
     * release_time_end_push_way : 16:48:30
     * release_time_start_redundant_week : null
     * release_time_end_redundant_week : null
     * start_time : 2022-02-25 00:00:00
     * end_time : 2022-03-10 16:48:30
     * redundant_week_list :
     * created_at : 1645778916
     * updated_at : 1646014822
     */

    private int id;
    private String push_type;
    private String push_title;
    private String push_content;
    private String push_target;
    private String push_way;
    private String jump_url;
    private int redirect_type;
    private String jump_page_type;
    private String jump_content_type;
    private String jump_content_id;
    private String osType;
    private String min_version;
    private String max_version;
    private String user_type;
    private String user_vip_type;
    private String account_type;
    private String status;
    private String release_date_start;
    private String release_date_end;
    private String release_time_start_push_way;
    private String release_time_end_push_way;
    private Object release_time_start_redundant_week;
    private Object release_time_end_redundant_week;
    private String start_time;
    private String end_time;
    private String redundant_week_list;
    private String created_at;
    private String updated_at;
    private String user_parame_need;

    public String getUser_parame_need() {
        return user_parame_need;
    }

    public void setUser_parame_need(String user_parame_need) {
        this.user_parame_need = user_parame_need;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public int getRedirect_type() {
        return redirect_type;
    }

    public void setRedirect_type(int redirect_type) {
        this.redirect_type = redirect_type;
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

    public String getOsType() {
        return osType;
    }

    public void setOsType(String osType) {
        this.osType = osType;
    }

    public String getMin_version() {
        return min_version;
    }

    public void setMin_version(String min_version) {
        this.min_version = min_version;
    }

    public String getMax_version() {
        return max_version;
    }

    public void setMax_version(String max_version) {
        this.max_version = max_version;
    }

    public String getUser_type() {
        return user_type;
    }

    public void setUser_type(String user_type) {
        this.user_type = user_type;
    }

    public String getUser_vip_type() {
        return user_vip_type;
    }

    public void setUser_vip_type(String user_vip_type) {
        this.user_vip_type = user_vip_type;
    }

    public String getAccount_type() {
        return account_type;
    }

    public void setAccount_type(String account_type) {
        this.account_type = account_type;
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

    public String getRelease_time_end_push_way() {
        return release_time_end_push_way;
    }

    public void setRelease_time_end_push_way(String release_time_end_push_way) {
        this.release_time_end_push_way = release_time_end_push_way;
    }

    public Object getRelease_time_start_redundant_week() {
        return release_time_start_redundant_week;
    }

    public void setRelease_time_start_redundant_week(Object release_time_start_redundant_week) {
        this.release_time_start_redundant_week = release_time_start_redundant_week;
    }

    public Object getRelease_time_end_redundant_week() {
        return release_time_end_redundant_week;
    }

    public void setRelease_time_end_redundant_week(Object release_time_end_redundant_week) {
        this.release_time_end_redundant_week = release_time_end_redundant_week;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getRedundant_week_list() {
        return redundant_week_list;
    }

    public void setRedundant_week_list(String redundant_week_list) {
        this.redundant_week_list = redundant_week_list;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
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
}
