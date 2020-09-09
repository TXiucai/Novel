package com.heiheilianzai.app.model.event;

/**
 * 波音登录Event
 */
public class LoginBoYinEvent {
   private String loginJson;

    public LoginBoYinEvent(String loginJson) {
        this.loginJson = loginJson;
    }

    public String getLoginJson() {
        return loginJson;
    }

    public void setLoginJson(String loginJson) {
        this.loginJson = loginJson;
    }
}
