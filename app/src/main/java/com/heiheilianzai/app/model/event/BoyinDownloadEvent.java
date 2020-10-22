package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.model.boyin.BoyinChapterBean;

import java.util.List;

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

    private List<BoyinChapterBean> mDownloadTaskList;
    private int downComplete;

    public int getDownComplete() {
        return downComplete;
    }

    public void setDownComplete(int downComplete) {
        this.downComplete = downComplete;
    }

    public BoyinDownloadEvent(EventTag tag, List<BoyinChapterBean> downloadTaskList) {
        mTag = tag;
        mDownloadTaskList = downloadTaskList;
    }

    public List<BoyinChapterBean> getDownloadTaskList() {
        return mDownloadTaskList;
    }

    public void setDownloadTaskList(List<BoyinChapterBean> downloadTaskList) {
        mDownloadTaskList = downloadTaskList;
    }

    public BoyinDownloadEvent(EventTag tag) {
        mTag = tag;
    }

    public EventTag getTag() {
        return mTag;
    }
}