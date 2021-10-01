package com.heiheilianzai.app.model.event;

public class NoticeEvent {
    public static final int ACTION_DIALOG_HOME = 1;//首页广告
    public static final int ACTION_DIALOG_HOME_LOCAL = 2;//通知开始加载本地
    public static class DialogEvent {
        public int action;
    }
}