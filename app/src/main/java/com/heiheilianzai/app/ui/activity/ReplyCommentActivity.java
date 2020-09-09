package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshCommentListEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.comic.RefreashComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.OnMultiClickListener;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Pattern;

import butterknife.BindView;

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
        MyPicasso.GlideImageNoSize(this, mAvatar, activity_reply_avatar);
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

        if (!MainHttpTask.getInstance().Gotologin(ReplyCommentActivity.this)) {
            return;
        }
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
            url = ReaderConfig.getBaseUrl() + ReaderConfig.mCommentPostUrl;
        } else {
            params.putExtraParams("comic_id", comic_id);
            url = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_sendcomment;
        }
        params.putExtraParams("comment_id", mCommentId);
        params.putExtraParams("content", str);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (result != null && !result.equals("315")) {
                            MyToash.ToashSuccess(ReplyCommentActivity.this, "评论成功");
                            EventBus.getDefault().post(new RefreshBookInfoEvent());//刷新小说 详情里的评论列表
                            EventBus.getDefault().post(new RefreshMine(null));
                            if (mBookId != null) {
                                EventBus.getDefault().post(new RefreshCommentListEvent());//刷新小说 评论列表
                                EventBus.getDefault().post(new RefreshBookInfoEvent());//刷新小说 详情里的评论列表
                            } else {
                                EventBus.getDefault().post(new RefreashComicInfoActivity(false));//刷新漫画详情 漫画评论页等   评论列表
                            }
                        }
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                finish();
                            }
                        }.sendEmptyMessageDelayed(0, 1500);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
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
                if (mBookId != null) {
                    EventBus.getDefault().post(new RefreshCommentListEvent());//刷新小说 评论列表
                    EventBus.getDefault().post(new RefreshBookInfoEvent());//刷新小说 详情里的评论列表
                } else {
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
