package com.heiheilianzai.app.ui.activity;

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
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.OnMultiClickListener;

import org.greenrobot.eventbus.EventBus;

import java.util.regex.Pattern;

//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//.http.RequestParams;

/**
 * 写作品评论
 * Created by scb on 2018/7/8.
 */
public class AddCommentActivity extends BaseActivity implements ShowTitle {
    private final String TAG = AddCommentActivity.class.getSimpleName();
    private LinearLayout mBack;
    private TextView mTitle;
    private String mBookId;
    /**
     * 评论内容
     */
    private EditText activity_add_comment_content;
    /**
     * 字数百分比
     */
    private TextView activity_add_comment_percentage;
    /**
     * "发布"
     */
    private LinearLayout comment_titlebar_add_comment;

    @Override
    public int initContentView() {
        return R.layout.activity_add_comment;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.BookInfoActivity_xieshuping));
        activity_add_comment_content = findViewById(R.id.activity_add_comment_content);
        activity_add_comment_content.addTextChangedListener(new TextWatcher() {
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
                activity_add_comment_percentage.setText(String.format(percentage, lastWordsNum + ""));
            }
        });
        activity_add_comment_percentage = findViewById(R.id.activity_add_comment_percentage);
        comment_titlebar_add_comment = findViewById(R.id.comment_titlebar_add_comment);
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

        if(!MainHttpTask.getInstance().Gotologin(AddCommentActivity.this)){
            return;
        };

        String str = activity_add_comment_content.getText().toString();
        if (TextUtils.isEmpty(str)||Pattern.matches("\\s*", str)) {
            MyToash.ToashError(AddCommentActivity.this, LanguageUtil.getString(AddCommentActivity.this, R.string.CommentListActivity_some));
            return;
        }
        mBookId = getIntent().getStringExtra("book_id");


        ReaderParams readerParams = new ReaderParams(this);
        readerParams.putExtraParams("book_id", mBookId);
        readerParams.putExtraParams("content", str);
        String json = readerParams.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl()+ReaderConfig.mCommentPostUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        if(result!=null&&!result.equals("315")) {
                            MyToash.ToashSuccess(AddCommentActivity.this, "评论成功");
                            EventBus.getDefault().post(new RefreshBookInfoEvent());//刷新小说 详情里的评论列表
                            EventBus.getDefault().post(new RefreshMine(null));
                        }else {

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
                    public void onErrorResponse(String  ex) {

                    }
                }

        );

    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);

    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }


}
