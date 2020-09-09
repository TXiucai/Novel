package com.heiheilianzai.app.ui.activity;

//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshCommentListEvent;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import org.greenrobot.eventbus.EventBus;

/**
 * 写作品评论，由"评论详情"页调起的
 * Created by scb on 2018/8/15.
 */
public class ListAddCommentActivity extends AddCommentActivity {
    @Override
    public void initInfo(String json) {
        MyToash.ToashSuccess(ListAddCommentActivity.this, LanguageUtil.getString(this, R.string.CommentListActivity_success));
        EventBus.getDefault().post(new RefreshCommentListEvent());
        EventBus.getDefault().post(new RefreshBookInfoEvent());
        finish();

    }


}
