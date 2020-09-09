package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.model.UserInfoItem;

/**
 * Created by scb on 2018/8/8.
 */
public class RefreshUserInfo {
    public UserInfoItem UserInfo;

    public RefreshUserInfo(UserInfoItem userInfo) {
        UserInfo = userInfo;
    }
}
