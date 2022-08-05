package com.heiheilianzai.app.model.comic;

import android.app.Activity;

import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.Utils;

import java.util.List;

//.http.RequestParams;

public class ComicReadHistory {
    public int total_page;
    public int current_page;
    public int page_size;
    public int total_count;
    public List<ReadHistoryComic> list;


    @Override
    public String toString() {
        return "ReadHistory{" +
                "total_page=" + total_page +
                ", current_page=" + current_page +
                ", page_size=" + page_size +
                ", total_count=" + total_count +
                ", list=" + list +

                '}';
    }

    public static class ReadHistoryComic  extends BaseAd {
        public String log_id;
        public String chapter_id;
        public String chapter_title;
        public int chapter_index;
        public String description;
        public String name;//": "女神的诱惑",    // 漫画名称
        public String vertical_cover;// 竖图
        public int total_chapters;//": 128,  // 总章节数
        public String last_chapter_time;//": "更新于3个月前", // 更新时间

        public String getLog_id() {
            return log_id;
        }

        public void setLog_id(String log_id) {
            this.log_id = log_id;
        }

        public String getChapter_id() {
            return chapter_id;
        }

        public void setChapter_id(String chapter_id) {
            this.chapter_id = chapter_id;
        }

        public String getChapter_title() {
            return chapter_title;
        }

        public void setChapter_title(String chapter_title) {
            this.chapter_title = chapter_title;
        }

        public int getChapter_index() {
            return chapter_index;
        }

        public void setChapter_index(int chapter_index) {
            this.chapter_index = chapter_index;
        }

        public int getAd_type() {
            return ad_type;
        }

        public void setAd_type(int ad_type) {
            this.ad_type = ad_type;
        }

        public String getComic_id() {
            return comic_id;
        }

        public void setComic_id(String comic_id) {
            this.comic_id = comic_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVertical_cover() {
            return vertical_cover;
        }

        public void setVertical_cover(String vertical_cover) {
            this.vertical_cover = vertical_cover;
        }

        public int getTotal_chapters() {
            return total_chapters;
        }

        public void setTotal_chapters(int total_chapters) {
            this.total_chapters = total_chapters;
        }

        public String getLast_chapter_time() {
            return last_chapter_time;
        }

        public void setLast_chapter_time(String last_chapter_time) {
            this.last_chapter_time = last_chapter_time;
        }
    }

    public static void addReadHistory(boolean FORM_READHISTORY, final Activity activity, final String comic_id, String chapterid) {
        if (!Utils.isLogin(activity)) {
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("chapter_id", chapterid);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_read_log_add, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                       /* if (FORM_READHISTORY) {
                            EventBus.getDefault().post(new RefreshReadHistory(false));
                        }*/
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );

    }

}
