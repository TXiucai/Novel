package com.heiheilianzai.app.model.boyin;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.Objects;

public class BoyinChapterBean extends LitePalSupport implements Serializable, Comparable<BoyinChapterBean> {
    public static final int STATUS_WAITING = 0;
    public static final int STATUS_COMPLETE = 1;
    public static final int STATUS_DOWNLOADING = 2;
    public static final int STATUS_DOWNLOAD_ERROR = 3;


    /**
     * 播音章节
     * <p>
     * nid : 2
     * numbers : 1
     * url : http://compressdownload.i9bc0rmy.com/我是哥哥的情人(下).mp3
     * chapter_name : 第一章
     * chapter_id : 67
     * chapter_play_time : 67
     */

    private int nid;
    private int numbers;
    private String url;
    private String chapter_name;
    @Column(unique = true)
    private int chapter_id;
    private int chapter_play_time;
    private String savePath = "";
    private int downloadStatus; //0 未下载  1 下载成功 2 下载中 3下载失败
    private int downloadId;
    private boolean isPlay;

    public boolean isPlay() {
        return isPlay;
    }

    public void setPlay(boolean play) {
        isPlay = play;
    }

    public int getDownloadId() {
        return downloadId;
    }

    public void setDownloadId(int downloadId) {
        this.downloadId = downloadId;
    }

    public String getSavePath() {
        return savePath;
    }

    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    public int getDownloadStatus() {
        return downloadStatus;
    }

    public void setDownloadStatus(int downloadStatus) {
        this.downloadStatus = downloadStatus;
    }

    public int getNid() {
        return nid;
    }

    public void setNid(int nid) {
        this.nid = nid;
    }

    public int getNumbers() {
        return numbers;
    }

    public void setNumbers(int numbers) {
        this.numbers = numbers;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getChapter_name() {
        return chapter_name;
    }

    public void setChapter_name(String chapter_name) {
        this.chapter_name = chapter_name;
    }

    public int getChapter_id() {
        return chapter_id;
    }

    public void setChapter_id(int chapter_id) {
        this.chapter_id = chapter_id;
    }

    public int getChapter_play_time() {
        return chapter_play_time;
    }

    public void setChapter_play_time(int chapter_play_time) {
        this.chapter_play_time = chapter_play_time;
    }

    @Override
    public int compareTo(BoyinChapterBean boyinChapterBean) {
        return boyinChapterBean.chapter_id - chapter_id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BoyinChapterBean that = (BoyinChapterBean) o;
        return nid == that.nid &&
                chapter_id == that.chapter_id &&
                Objects.equals(chapter_name, that.chapter_name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nid, chapter_name, chapter_id);
    }
}