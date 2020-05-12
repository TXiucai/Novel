package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.eventbus.RefreshMine;
import com.nostra13.universalimageloader.core.ImageLoader;
//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.CommentAdapter;
import com.heiheilianzai.app.bean.CommentItem;
import com.heiheilianzai.app.comic.been.RefreashComicInfoActivity;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.RefreshBookInfo;
import com.heiheilianzai.app.eventbus.RefreshCommentList;
import com.heiheilianzai.app.http.OkHttpEngine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.http.ResultCallback;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
//.http.RequestParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;

import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.view.OnMultiClickListener;

/**
 * 回复评论页面
 * Created by scb on 2018/8/16.
 */
public class ReplyCommentActivity extends BaseActivity implements ShowTitle {
    private String mBookId, comic_id;
    private String mCommentId;
    private String mAvatar;
    private String mNickName;
    private String mOriginContent;
    /**
     * 评论内容
     */
    @BindView(R2.id.activity_reply_comment_content)
    EditText activity_reply_comment_content;

    /**
     * 原始评论内容
     */
    @BindView(R2.id.activity_reply_origin_comment)
    TextView activity_reply_origin_comment;

    /**
     * 字数百分比
     */
    @BindView(R2.id.activity_reply_comment_percentage)
    TextView activity_reply_comment_percentage;

    /**
     * 头像
     */
    @BindView(R2.id.activity_reply_avatar)
    CircleImageView activity_reply_avatar;

    /**
     * 昵称
     */
    @BindView(R2.id.activity_reply_nickname)
    TextView activity_reply_nickname;

    /**
     * "评论"
     */
    @BindView(R2.id.comment_titlebar_add_comment)
    LinearLayout comment_titlebar_add_comment;

    @Override
    public int initContentView() {
        return R.layout.activity_reply_comment;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.CommentListActivity_pinglun));
        Intent intent = getIntent();

        mBookId = intent.getStringExtra("book_id");
        if (mBookId == null) {
            comic_id = intent.getStringExtra("comic_id");
        }

        mCommentId = intent.getStringExtra("comment_id");
        mAvatar = intent.getStringExtra("avatar");
        mNickName = intent.getStringExtra("nickname");
        mOriginContent = intent.getStringExtra("origin_content");

        ImageLoader.getInstance().displayImage(mAvatar, activity_reply_avatar, ReaderApplication.getOptions());
        activity_reply_nickname.setText(mNickName);
        activity_reply_origin_comment.setText(mOriginContent);
        activity_reply_comment_content.setHint(LanguageUtil.getString(this, R.string.CommentListActivity_huifu) + mNickName);
        activity_reply_comment_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String percentage = "%s/100";
                int lastWordsNum = 100 - s.length();
                activity_reply_comment_percentage.setText(String.format(percentage, lastWordsNum + ""));
            }
        });

        comment_titlebar_add_comment.setOnClickListener(new OnMultiClickListener() {
            @Override
            public void onMultiClick(View v) {
                addComment();
            }
        });

    }

    @Override
    public void initData() {

    }

    /**
     * 发请求
     */
    public void addComment() {

        if(!MainHttpTask.getInstance().Gotologin(ReplyCommentActivity.this)){
            return;
        };
        String str = activity_reply_comment_content.getText().toString();
        if (TextUtils.isEmpty(str)) {
            MyToash.ToashError(ReplyCommentActivity.this, LanguageUtil.getString(ReplyCommentActivity.this, R.string.CommentListActivity_some));
            return;
        }
        if (Pattern.matches("\\s*", str)) {
            MyToash.ToashError(ReplyCommentActivity.this, LanguageUtil.getString(ReplyCommentActivity.this, R.string.CommentListActivity_some));
            return;
        }

        String url = "";
        ReaderParams params = new ReaderParams(this);
        if (mBookId != null) {
            params.putExtraParams("book_id", mBookId);
            url=ReaderConfig.getBaseUrl()+ReaderConfig.mCommentPostUrl;
        } else {
            params.putExtraParams("comic_id", comic_id);
            url=ReaderConfig.getBaseUrl() + ComicConfig.COMIC_sendcomment;
        }
        params.putExtraParams("comment_id", mCommentId);
        params.putExtraParams("content", str);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if(result!=null&&!result.equals("315")) {
                            MyToash.ToashSuccess(ReplyCommentActivity.this, "评论成功");
                            EventBus.getDefault().post(new RefreshBookInfo());//刷新小说 详情里的评论列表
                            EventBus.getDefault().post(new RefreshMine(null));
                            if (mBookId != null) {
                                EventBus.getDefault().post(new RefreshCommentList());//刷新小说 评论列表
                                EventBus.getDefault().post(new RefreshBookInfo());//刷新小说 详情里的评论列表
                            } else {
                                EventBus.getDefault().post(new RefreashComicInfoActivity(false));//刷新漫画详情 漫画评论页等   评论列表
                            }
                        }
                        new Handler(){
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                finish();
                            }
                        }.sendEmptyMessageDelayed(0,1500);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        //finish();
                    }
                }

        );
    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
        try {
            JSONObject jsonObj = new JSONObject(json);
            int status = Integer.parseInt(jsonObj.getString("status"));
            if (status == 1) {
                MyToash.ToashSuccess(ReplyCommentActivity.this, LanguageUtil.getString(this, R.string.CommentListActivity_success));
                if(mBookId!=null) {
                    EventBus.getDefault().post(new RefreshCommentList());//刷新小说 评论列表
                    EventBus.getDefault().post(new RefreshBookInfo());//刷新小说 详情里的评论列表
                }else {
                    EventBus.getDefault().post(new RefreashComicInfoActivity(false));//刷新漫画详情 漫画评论页等   评论列表
                }
                finish();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        TextView mFinish;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mFinish = findViewById(R.id.titlebar_finish);
        mFinish.setText(LanguageUtil.getString(this, R.string.CommentListActivity_pinglun));
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }


}
