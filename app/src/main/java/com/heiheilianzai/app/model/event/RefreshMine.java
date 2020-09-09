package com.heiheilianzai.app.model.event;

import com.heiheilianzai.app.model.UserInfoItem;

/**
 * Created by scb on 2018/8/8.
 */
public class RefreshMine {
  public   UserInfoItem userInfoItem;

    public RefreshMine(UserInfoItem userInfoItem) {
        this.userInfoItem = userInfoItem;
    }
}
