package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ChapterNovelAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * 作品章节目录(从读书页开启的)
 * Created by scb on 2018/8/11.
 */
public class CatalogInnerActivity extends BaseActivity implements ShowTitle {
    public final String TAG = CatalogActivity.class.getSimpleName();
    public XRecyclerView mListView;
    public LinearLayout mBack;
    public TextView mTitle;
    public String mBookId;
    public List<ChapterItem> mItemList = new ArrayList<>();
    BaseBook baseBook;
    public static Activity activity;
    private String coupon_pay_price;
    private int mCurrentPage = 1;
    private int mLoading = -1;
    private int mOrderby = 1;
    private int mTotalPage, mSize;
    private ChapterNovelAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_chapter_catalog;
    }

    @Override
    public void initView() {
        mBookId = getIntent().getStringExtra("book_id");
        baseBook = (BaseBook) getIntent().getSerializableExtra("book");
        activity = this;
        mListView = findViewById(R.id.activity_chapter_catalog_listview);
        mListView.setLayoutManager(new LinearLayoutManager(activity));
        mAdapter = new ChapterNovelAdapter(activity, mItemList);
        mListView.setAdapter(mAdapter);
        mListView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mLoading = -1;
                mCurrentPage = 1;
                getChapters();
            }

            @Override
            public void onLoadMore() {
                mLoading = 1;
                if (mTotalPage >= mCurrentPage) {
                    getChapters();
                } else {
                    mListView.loadMoreComplete();
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        });
        mAdapter.setOnChapterListener(new ChapterNovelAdapter.OnChapterListener() {
            @Override
            public void onChapterSelect(ChapterItem chapterItem, int position) {
                ReaderConfig.CatalogInnerActivityOpen = true;
                ChapterManager.getInstance(activity).openBook(baseBook, mBookId, chapterItem.getChapter_id());
            }
        });
    }

    @Override
    public void initData() {
        getChapters();
    }

    protected void getChapters() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("book_id", mBookId);
        params.putExtraParams("page", String.valueOf(mCurrentPage));
        params.putExtraParams("orderby", String.valueOf(mOrderby));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfo(result);
                        closeLoading();
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        closeLoading();
                    }
                }

        );
    }

    public void initInfo(final String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray chapterListArr = jsonObject.getJSONArray("chapter_list");
            coupon_pay_price = jsonObject.getString("coupon_pay_price");
            initTitleBarView(jsonObject.getString("name"));
            String is_limited_free = jsonObject.getString("is_limited_free");
            mTotalPage = jsonObject.getInt("total_page");
            AppPrefs.putSharedString(activity, PrefConst.COUPON_PRICE, coupon_pay_price);
            int size = chapterListArr.length();
            if (mCurrentPage == 1) {
                mItemList.clear();
            }
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = chapterListArr.getJSONObject(i);
                ChapterItem chapterItem = new Gson().fromJson(String.valueOf(jsonObject1), ChapterItem.class);
                chapterItem.setIs_limited_free(is_limited_free);
                mItemList.add(chapterItem);
            }
            if (mCurrentPage <= mTotalPage && size != 0) {
                if (mCurrentPage == 1) {
                    mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
                    mAdapter.setCoupon_pay_price(coupon_pay_price);
                    mSize = size;
                    mAdapter.notifyDataSetChanged();
                } else {
                    int t = mSize + size;
                    mAdapter.notifyItemRangeInserted(mSize + 2, size);
                    mSize = t;
                }
                ++mCurrentPage;
            } else {
                if (mCurrentPage > 1) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception E) {
            if (mCurrentPage > 1) {
                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
            }
        }
    }

    private void closeLoading() {
        if (mLoading == -1) {
            mListView.refreshComplete();
        } else if (mLoading == 1) {
            mListView.loadMoreComplete();
        }
    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        ToggleButton mBtn;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBtn = findViewById(R.id.titlebar_order);
        mBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundResource(R.mipmap.asc);
                    mOrderby = 2;
                } else {
                    buttonView.setBackgroundResource(R.mipmap.dsc);
                    mOrderby = 1;
                }
                mCurrentPage = 1;
                getChapters();
            }
        });
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mLoading = -1;
        mCurrentPage = 1;
        getChapters();
    }
}
