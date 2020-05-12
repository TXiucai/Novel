package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.RefreshBookInfo;
import com.heiheilianzai.app.eventbus.RefreshCommentList;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.http.ResultCallback;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * 写作品评论，由"评论详情"页调起的
 * Created by scb on 2018/8/15.
 */
public class ListAddCommentActivity extends AddCommentActivity {
    @Override
    public void initInfo(String json) {
        MyToash.ToashSuccess(ListAddCommentActivity.this, LanguageUtil.getString(this, R.string.CommentListActivity_success));
        EventBus.getDefault().post(new RefreshCommentList());
        EventBus.getDefault().post(new RefreshBookInfo());
        finish();

    }


}
