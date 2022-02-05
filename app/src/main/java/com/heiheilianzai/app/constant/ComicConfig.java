package com.heiheilianzai.app.constant;

import android.app.Activity;

import com.heiheilianzai.app.utils.ShareUitls;


/**
 * Created by scb on 2018/5/27.
 */
public class ComicConfig {

    /**
     * 漫画书架
     */
    public static final String COMIC_SHELF = "/fav/index";

    /**
     * 漫画新增书架
     */
    public static final String COMIC_SHELF_ADD = "/fav/add";

    /**
     * 漫画删除书架
     */
    public static final String COMIC_SHELF_DEL = "/fav/del";

    /**
     * 漫画书城
     */
    public static final String COMIC_home_stock = "/comic/home-stock";

    /**
     * 漫画频道
     */
    public static final String COMIC_channel = "/comic-channel-list/index";

    public static final String COMIC_Detail_channel = "/comic-channel-list/label";

    /**
     * 漫画换一换
     */
    public static final String COMIC_home_refresh = "/comic/refresh";

    /**
     * 漫画年度榜单换一换
     */
    public static final String COMIC_TOP_YEAR_refresh = "/home-recommen/comic-refresh";

    /**
     * 漫画发现
     */
    public static final String COMIC_featured = "/comic/featured";

    /**
     * 漫画详情
     */
    public static final String COMIC_info = "/comic/info";

    /**
     * 漫画详情记录
     */
    public static final String COMIC_info_record = "/comic/add-comic-behavior-log";

    /**
     * 漫画目录
     */
    public static final String COMIC_catalog = "/comic/catalog";

    /**
     * 漫画章节
     */
    public static final String COMIC_chapter = "/comic/chapter";

    /**
     * 漫画章节解锁
     */
    public static final String COMIC_chapter_open = "/comic/unlock-content";

    /**
     * 漫画吐槽
     */
    public static final String COMIC_tucao = "/comic/tucao";

    /**
     * 漫画分类
     */
    public static final String COMIC_list = "/comic/list";

    /**
     * 漫画 排行首页
     */
    public static final String COMIC_rank_index = "/rank/comic-index";

    /**
     * 漫画 排行 列表
     */
    public static final String COMIC_rank_list = "/rank/comic-list";

    /**
     * 漫画搜索列表
     */
    public static final String COMIC_search = "/comic/search";

    /**
     * 上次漫画热词
     */
    public static final String mUpComicWord =  "/comic/add-hotwords";

    /**
     * 漫画搜索首页
     */
    public static final String COMIC_search_index = "/comic/search-index";

    /**
     * 漫画评论类表
     */
    public static final String COMIC_comment_list = "/comic/comment-list";

    /**
     * WODE漫画评论类表
     */
    public static final String COMIC_comment_list_MY = "/user/comic-comments";

    /**
     * 漫画发评论
     */
    public static final String COMIC_sendcomment = "/comic/post";

    /**
     * 漫画下载选项
     */
    public static final String COMIC_down_option = "/comic/down-option";

    /**
     * 漫画下载
     */
    public static final String COMIC_down = "/comic/down";

    /**
     * 漫画购买预览
     */
    public static final String COMIC_buy_index = "/comic-chapter/buy-index";

    /**
     * 漫画购买
     */
    public static final String COMIC_buy_buy = "/comic-chapter/buy";

    /**
     * 漫画 获取阅读历史
     */
    public static final String COMIC_read_log = "/user/comic-read-log";

    /**
     * 漫画删除阅读历史
     */
    public static final String COMIC_read_log_del = "/user/del-comic-read-log";

    /**
     * 漫画删除阅读历史多个
     */
    public static final String COMIC_read_log_del_MORE = "/user/batch-del-comic-read-log";

    /**
     * 漫画 新增阅读历史
     */
    public static final String COMIC_read_log_add = "/user/add-comic-read-log";

    /**
     * 漫画限免
     */
    public static final String COMIC_free_time = "/comic/free";

    /**
     * 漫画 完本
     */
    public static final String COMIC_finish = "/comic/finish";

    /**
     * 漫画会员首页
     */
    public static final String COMIC_baoyue = "/comic/baoyue";

    /**
     * 漫画 会员列表
     */
    public static final String COMIC_baoyue_list = "/comic/baoyue-list";

    /**
     * 首页漫画 BANNER
     */
    public static final String COMIC_STORE_BANNER = "/comic/index-banner";

    /**
     * 漫画 查看更多
     */
    public static final String COMIC_recommend = "/comic/recommend";

    /**
     * 漫画 年度榜单查看更多
     */
    public static final String COMIC_TOP_YEAR_recommend = "/home-recommen/comic-recommend";

    private static boolean IS_OPEN_DANMU;

    public static boolean IS_OPEN_DANMU(Activity activity) {
        if (!IS_OPEN_DANMU) {
            IS_OPEN_DANMU = ShareUitls.getBoolean(activity, "IS_OPEN_DANMU", true);
        }
        return IS_OPEN_DANMU;
    }

    public static void SET_OPEN_DANMU(Activity activity, boolean flag) {
        IS_OPEN_DANMU = flag;
        ShareUitls.putBoolean(activity, "IS_OPEN_DANMU", flag);
    }
}
