package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.utils.UpdateApp;

/**
 * 开屏页请求启动数据完成并已经写入缓存中。
 * {@link com.heiheilianzai.app.utils.UpdateApp#getRequestData(UpdateApp.UpdateAppInterface, boolean, boolean)}
 */
public class AppUpdateLoadOverEvent {
    String response;

    public AppUpdateLoadOverEvent(String response) {
        this.response = response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
