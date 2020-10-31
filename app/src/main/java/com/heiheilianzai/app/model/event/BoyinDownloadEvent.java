package com.heiheilianzai.app.model.event;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

/**
 * 有声小说下载  Event
 */
public class BoyinDownloadEvent {

    public enum EventTag {
        START_DOWNLOAD,
        WAITING_DOWNLOAD,
        COMPLETE_DOWNLOAD,
        TASK_STATUS,
        ERROR,
        INTERRUPT
    }

    private EventTag mTag;
    private int downComplete;
    private String json;

    public BoyinDownloadEvent(EventTag tag, List<BoyinChapterBean> downloadTaskList) {
        mTag = tag;
        setDownloadTaskList(downloadTaskList);
    }

    public int getDownComplete() {
        return downComplete;
    }

    public void setDownComplete(int downComplete) {
        this.downComplete = downComplete;
    }


    public List<BoyinChapterBean> getDownloadTaskList() {
        if (StringUtils.isEmpty(json)) {
            return null;
        }
        return new Gson().fromJson(json, new TypeToken<List<BoyinChapterBean>>() {
        }.getType());
    }

    public void setDownloadTaskList(List<BoyinChapterBean> downloadTaskList) {
        json = downloadTaskList == null || downloadTaskList.size() < 1 ? "" : new Gson().toJson(downloadTaskList);
    }

    public BoyinDownloadEvent(EventTag tag) {
        mTag = tag;
    }

    public EventTag getTag() {
        return mTag;
    }
}