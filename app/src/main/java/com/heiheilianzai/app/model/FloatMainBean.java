package com.heiheilianzai.app.model;

public class FloatMainBean {

    /**
     * id : 1
     * icon_img : banner/2f4859724bdd594a93ea259f4c8805b5.jpg
     * link_type : 3
     * status : 1
     * type : 1
     * url_type : 3
     * link_url :
     * created_at : 1619081674
     * updated_at : 1619081674
     * today_sign_status : 0
     */

    private String id;
    private String icon_img;
    private String link_type;//3=>'福利中心', 4 => '小说作品详情页',5 => '漫画作品详情页',6 => '动画作品详情页', 7 => '首页-推荐页（小说）',
    //8 => '首页-完结页（小说）', 9 => '首页-榜单页（小说）', 10 => '首页-分类（小说）',
    //11 => '首页-推荐页（漫画）', 12 => '首页-完结页（漫画）', 13 => '首页-榜单页（漫画）', 14 => '首页-分类（漫画）',
    //15 => 'VIP充值页', 16 => '年度榜单（小说）', 17 => '年度榜单（漫画）', 18 => '分享页', 19 => '活动页面',
    // 20 => '波音有声', 21 => '熊猫游戏', 22=>'福利中心'
    private String status;
    private String type;
    private String url_type;//1 内置浏览器  2外部浏览器  3 内置应用
    private String link_url;
    private String created_at;
    private String updated_at;
    private int today_sign_status;
    private String user_parame_need;;
    public String book_id;
    public String comic_id;
    public String video_id;
    public String panda_game_link;

    public String getBook_id() {
        return book_id;
    }

    public void setBook_id(String book_id) {
        this.book_id = book_id;
    }

    public String getComic_id() {
        return comic_id;
    }

    public void setComic_id(String comic_id) {
        this.comic_id = comic_id;
    }

    public String getVideo_id() {
        return video_id;
    }

    public void setVideo_id(String video_id) {
        this.video_id = video_id;
    }

    public String getPanda_game_link() {
        return panda_game_link;
    }

    public void setPanda_game_link(String panda_game_link) {
        this.panda_game_link = panda_game_link;
    }

    public String getUser_parame_need() {
        return user_parame_need;
    }

    public void setUser_parame_need(String user_parame_need) {
        this.user_parame_need = user_parame_need;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIcon_img() {
        return icon_img;
    }

    public void setIcon_img(String icon_img) {
        this.icon_img = icon_img;
    }

    public String getLink_type() {
        return link_type;
    }

    public void setLink_type(String link_type) {
        this.link_type = link_type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl_type() {
        return url_type;
    }

    public void setUrl_type(String url_type) {
        this.url_type = url_type;
    }

    public String getLink_url() {
        return link_url;
    }

    public void setLink_url(String link_url) {
        this.link_url = link_url;
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

    public int getToday_sign_status() {
        return today_sign_status;
    }

    public void setToday_sign_status(int today_sign_status) {
        this.today_sign_status = today_sign_status;
    }
}
