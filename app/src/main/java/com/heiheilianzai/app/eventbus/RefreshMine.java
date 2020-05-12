package com.heiheilianzai.app.eventbus;

import com.heiheilianzai.app.bean.UserInfoItem;

/**
 * Created by scb on 2018/8/8.
 */
public class RefreshMine {
  public   UserInfoItem userInfoItem;

    public RefreshMine(UserInfoItem userInfoItem) {
        this.userInfoItem = userInfoItem;
    }
}
