package com.heiheilianzai.app.ui.activity.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.comic.CommentAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.ComicComment;
import com.heiheilianzai.app.ui.activity.ReplyCommentActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.OnClick;

public class ComicCommentActivity extends BaseButterKnifeActivity {

    @BindView(R2.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R2.id.titlebar_text)
    public TextView titlebar_text;

    @BindView(R2.id.activity_finish_listview_noresult)
    public LinearLayout mNoResult;
    @BindView(R2.id.activity_finish_listview)
    public ListView mListView;
    @BindView(R2.id.activity_comment_list_add_comment)
    public EditText activity_comment_list_add_comment;

    // public RelativeLayout mSearchLayout;

    boolean IsBook;
    int mCurrentPage = 1, total_count;
    Gson gson = new Gson();
    List<ComicComment.Comment> commentList;
    CommentAdapter commentAdapter;
    String comic_id;

    @OnClick(value = {R.id.titlebar_back, R.id.activity_comment_list_add_comment
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                Intent intent = new Intent();
                intent.putExtra("total_count", total_count);
                setResult(112, intent);
                finish();
                break;
            case R.id.activity_comment_list_add_comment:

                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            Intent intent = new Intent();
            intent.putExtra("total_count", total_count);
            setResult(112, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public int initContentView() {
        return R.layout.activity_comiccomment;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        init();

    }

    private void init() {
        activity_comment_list_add_comment.setHorizontallyScrolling(false);
        activity_comment_list_add_comment.setMaxLines(Integer.MAX_VALUE);
        titlebar_text.setText(LanguageUtil.getString(this, R.string.CommentListActivity_title));
        activity_comment_list_add_comment.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String str = activity_comment_list_add_comment.getText().toString();
                    if (TextUtils.isEmpty(str)) {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.CommentListActivity_some));
                        return true;
                    }
                    if (Pattern.matches("\\s*", str)) {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.CommentListActivity_some));
                        return true;
                    }
                    sendComment(activity, comic_id, str, new SendSuccess() {
                        @SuppressLint("HandlerLeak")
                        @Override
                        public void Success(String code) {
                            activity_comment_list_add_comment.setText("");
                            mCurrentPage = 1;
                            if (code != null && !code.equals("315")) {
                                total_count++;
                                EventBus.getDefault().post(new RefreashComicInfoActivity(false));//刷新漫画详情 漫画评论页等   评论列表
                            }
                            new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    Intent intent = new Intent();
                                    intent.putExtra("total_count", total_count);
                                    setResult(112, intent);
                                    finish();
                                }
                            }.sendEmptyMessageDelayed(0, 1500);
                        }
                    });
                    return true;
                }
                return false;
            }

        });
        commentList = new ArrayList<>();

        IsBook = getIntent().getBooleanExtra("IsBook", false);

        comic_id = getIntent().getStringExtra("comic_id");
        //   View temphead = LayoutInflater.from(activity).inflate(R.layout.item_list_head, null);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(activity, ReplyCommentActivity.class);
                intent.putExtra("comic_id", comic_id);
                intent.putExtra("comment_id", commentList.get(position).getComment_id());
                intent.putExtra("avatar", commentList.get(position).getAvatar());
                intent.putExtra("nickname", commentList.get(position).getNickname());
                intent.putExtra("origin_content", commentList.get(position).getContent());
                startActivity(intent);
            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isLoad && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    getHttp();
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLoad = ((firstVisibleItem + visibleItemCount) == totalItemCount);
            }
        });

        getHttp();
    }

    boolean isLoad;

    public void getHttp() {


        String requestParams;
        ReaderParams params = new ReaderParams(activity);
        if (!IsBook) {
            requestParams = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_comment_list;
        } else {
            requestParams = ReaderConfig.getBaseUrl()+ReaderConfig.mFinishUrl;
            params.putExtraParams("channel", "1");
        }

        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("page_num", mCurrentPage + "");

        String json = params.generateParamsJson();

        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!IsBook) {
                            ComicComment comicComment = gson.fromJson(result, ComicComment.class);
                            //    MyToash.Log("comicComment", mCurrentPage+"   "+comicComment.toString());
                            if ((mCurrentPage > comicComment.total_page)) {
                                if (mCurrentPage > 1) {
                                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                                }
                                return;
                            }
                            if (mCurrentPage == 1) {
                                total_count = comicComment.total_count;
                            }

                            if (!comicComment.list.isEmpty()) {

                                if (mCurrentPage == 1) {
                                    commentList.clear();
                                    commentList.addAll(comicComment.list);
                                    commentAdapter = new CommentAdapter(activity, commentList, commentList.size(), false);
                                    mListView.setAdapter(commentAdapter);
                                } else {
                                    commentList.addAll(comicComment.list);
                                    commentAdapter.notifyDataSetChanged();
                                }

                                mCurrentPage = comicComment.current_page;
                                mCurrentPage++;
                            }
                            if (commentList.isEmpty()) {
                                mNoResult.setVisibility(View.VISIBLE);
                                mListView.setVisibility(View.GONE);
                            } else {
                                mNoResult.setVisibility(View.GONE);
                                mListView.setVisibility(View.VISIBLE);
                            }
                        }

                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    interface SendSuccess {
        void Success(String code);
    }

    public static void sendComment(Activity activity, String comic_id, String content, SendSuccess sendSuccess) {

        if (!MainHttpTask.getInstance().Gotologin(activity)) {
            return;
        }
        ;
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_sendcomment;
        params.putExtraParams("comic_id", comic_id);
        //params.putExtraParams("comment_id", comic_id);
        params.putExtraParams("content", content);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        //  MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.CommentListActivity_success));


                        if (sendSuccess != null) {
                            sendSuccess.Success(result);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    /*    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        @Override
        protected void onDestroy() {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                mSearchLayout.getViewTreeObserver().removeGlobalOnLayoutListener(mOnGlobalLayoutListener);
            } else {
                mSearchLayout.getViewTreeObserver().removeOnGlobalLayoutListener(mOnGlobalLayoutListener);
            }
            super.onDestroy();
        }*/


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreashComicInfoActivity refreshBookInfo) {
        if (!refreshBookInfo.isSave) {
            mCurrentPage = 1;
            getHttp();
        }
    }

    @Override
    public void finish() {
        // TODO Auto-generated method stub
        super.finish();
        //关闭窗体动画显示
        this.overridePendingTransition(R.anim.activity_close, 0);
    }
}
