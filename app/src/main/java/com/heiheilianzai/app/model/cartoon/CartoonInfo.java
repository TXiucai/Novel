package com.heiheilianzai.app.model.cartoon;

import java.util.List;

public class CartoonInfo {

    /**
     * video_id : 389
     * name : 樋乃岛かなえ (姊) [有眼镜]
     * cover : https://akjimi.f7996e.comhttps://akjimi.f7996e.com/2021/11/11/4f27cb2c8936c7550e9aa392a4231338.jpg?c?c
     * author :
     * description : [アトリエつばき]樋乃岛かなえ (姐姐)[BIG5][有眼镜] [中文字幕]
     * status : 1
     * view_counts : 6
     * total_favors : 收藏数 0
     * updated_at : 2021-11-11 14:53:20
     * views : 6
     * display_label :
     * hot : 10
     * is_free : 0
     * tag : []
     * property : []
     * flag :
     * total_comment : 0
     */

    private String video_id;
    private String name;
    private String cover;
    private String author;
    private String description;
    private String status;
    private String view_counts;
    private String total_favors;
    private String updated_at;
    private String views;
    private String display_label;
    private int hot;
    private int is_free;
    private String flag;
    private int total_comment;
    private List<String> tag;


    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getView_counts() {
        return view_counts;
    }

    public void setView_counts(String view_counts) {
        this.view_counts = view_counts;
    }

    public String getTotal_favors() {
        return total_favors;
    }

    public void setTotal_favors(String total_favors) {
        this.total_favors = total_favors;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getViews() {
        return views;
    }

    public void setViews(String views) {
        this.views = views;
    }

    public String getDisplay_label() {
        return display_label;
    }

    public void setDisplay_label(String display_label) {
        this.display_label = display_label;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }

    public int getIs_free() {
        return is_free;
    }

    public void setIs_free(int is_free) {
        this.is_free = is_free;
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        this.flag = flag;
    }

    public int getTotal_comment() {
        return total_comment;
    }

    public void setTotal_comment(int total_comment) {
        this.total_comment = total_comment;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }

}
