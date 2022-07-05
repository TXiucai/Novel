package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.CommentAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.CommentItem;
import com.heiheilianzai.app.model.event.RefreshCommentListEvent;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


/**
 * 作品评论列表
 * Created by scb on 2018/7/8.
 */
public class CommentListActivity extends BaseActivity implements ShowTitle {
    private final String TAG = CommentListActivity.class.getSimpleName();
    private ListView mListView;
    private String mBookId;
    private List<CommentItem> mItemList;
    private CommentAdapter mAdapter;
    /**
     * 要请求的页码，从0开始
     */
    private String mPageNum;

    /**
     * 总页码
     */
    private int mTotalPage = 2;
    /**
     * 当前页码
     */
    private int mCurrentPage = 1;

    private boolean mLoadFinish = true;
    /**
     * 写评论
     */
    private TextView mAddCommentView;
    private boolean isLoad;
    private LinearLayout activity_comment_list_listview_noresult;

    @Override
    public int initContentView() {
        return R.layout.activity_comment_list;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.CommentListActivity_title));
        mListView = findViewById(R.id.activity_comment_list_listview);
        mAddCommentView = findViewById(R.id.activity_comment_list_add_comment);
        activity_comment_list_listview_noresult = findViewById(R.id.activity_comment_list_listview_noresult);
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (isLoad && scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {

                    mLoadFinish = false;
                    loadNextPage();

                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                isLoad = ((firstVisibleItem + visibleItemCount) == totalItemCount);
            }
        });

        mAddCommentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //写评论
                Intent intent = new Intent(CommentListActivity.this, AddCommentActivity.class);
                intent.putExtra("book_id", mBookId);
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {
        mBookId = getIntent().getStringExtra("book_id");

        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mCommentListUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                        initInfo(result);
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
        // Utils.hideLoadingDialog();
        // Utils.printLog(TAG, "initInfo " + json);

        mItemList = new ArrayList<>();
        try {
            JSONObject jsonObj = new JSONObject(json);
            JSONArray commentListArr = jsonObj.getJSONArray("list");

            if (jsonObj.has("total_page")) {
                mTotalPage = Integer.parseInt(jsonObj.getString("total_page"));
            }

            if (jsonObj.has("current_page")) {
                mCurrentPage = Integer.parseInt(jsonObj.getString("current_page"));
                mCurrentPage++;
            }


            for (int i = 0; i < commentListArr.length(); i++) {
                CommentItem commentItem = gson.fromJson(commentListArr.get(i).toString(), CommentItem.class);
                mItemList.add(commentItem);
            }

            mAdapter = new CommentAdapter(this, mItemList, mItemList.size(), false);
            mListView.setAdapter(mAdapter);

            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(CommentListActivity.this, ReplyCommentActivity.class);
                    intent.putExtra("book_id", mBookId);
                    intent.putExtra("comment_id", mItemList.get(position).getComment_id());
                    intent.putExtra("avatar", mItemList.get(position).getAvatar());
                    intent.putExtra("nickname", mItemList.get(position).getNickname());
                    intent.putExtra("origin_content", mItemList.get(position).getContent());
                    startActivity(intent);
                }
            });

            activity_comment_list_listview_noresult.setVisibility(mItemList.size() == 0 ? View.VISIBLE : View.GONE);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        ToggleButton mBtn;
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

    /**
     * 加载下一页
     */
    public void loadNextPage() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        params.putExtraParams("page_num", mCurrentPage + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mCommentListUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        getNextPage(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );

    }

    /**
     * 获取下一页数据
     *
     * @param nextpage
     */
    Gson gson = new Gson();

    public void getNextPage(String nextpage) {
        Utils.printLog(TAG, "getNextPage " + nextpage);
        try {
            JSONObject resultObj = new JSONObject(nextpage);

            mTotalPage = resultObj.getInt("total_page");
            if (mCurrentPage > mTotalPage) {
                MyToash.ToashError(CommentListActivity.this, LanguageUtil.getString(this, R.string.app_nomore));
                return;
            }
            mCurrentPage = resultObj.getInt("current_page");
            mCurrentPage++;
            JSONArray commentListArr = resultObj.getJSONArray("list");
            //  List<CommentItem> commentItemList = new ArrayList<>();
            Utils.printLog("getNextPage", mItemList.size() + "");
            for (int i = 0; i < commentListArr.length(); i++) {
                CommentItem commentItem = gson.fromJson(commentListArr.get(i).toString(), CommentItem.class);
                mItemList.add(commentItem);
                //  commentItemList.add(commentItem);
            }

            Utils.printLog("getNextPage", mItemList.size() + "");

            mAdapter.notifyDataSetChanged();
            mLoadFinish = true;

        } catch (JSONException e) {
            Utils.printLog("getNextPage", e.getMessage());
            e.printStackTrace();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshCommentListEvent refreshCommentListEvent) {
        //重置
        mTotalPage = 2;
        mCurrentPage = 1;
        mLoadFinish = true;
        //加载
        initData();
    }

}
