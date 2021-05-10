package com.heiheilianzai.app.ui.fragment.book;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ChapterNovelAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
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
import com.heiheilianzai.app.utils.StringUtils;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NovelMuluFragment extends BaseButterKnifeFragment {
    @BindView(R.id.list_novel_mulu)
    public XRecyclerView mListView;
    private String mBookId;
    private BaseBook baseBook;
    public List<ChapterItem> mItemList = new ArrayList<>();
    public ChapterNovelAdapter mAdapter;
    private String coupon_pay_price;
    private int chapterItemSelect;
    private int mCurrentPage = 1;
    private int mLoading = -1;
    private int mTotalPage, mSize;

    @Override
    public int initContentView() {
        return R.layout.fragment_novel_mulu;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new ChapterNovelAdapter(getContext(), mItemList);
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
                    MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
                }
            }
        });
        mAdapter.setOnChapterListener(new ChapterNovelAdapter.OnChapterListener() {
            @Override
            public void onChapterSelect(ChapterItem chapterItem, int position) {
                ReaderConfig.CatalogInnerActivityOpen = true;
                chapterItemSelect = position;
                ChapterManager.getInstance(getActivity()).openBook(baseBook, mBookId, chapterItem.getChapter_id(), null);
            }
        });
    }

    public void sendData(BaseBook baseBook) {
        this.baseBook = baseBook;
        mBookId = baseBook.getBook_id();
        getChapters();
    }

    protected void getChapters() {
        ReaderParams params = new ReaderParams(getContext());
        params.putExtraParams("book_id", mBookId);
        params.putExtraParams("page", String.valueOf(mCurrentPage));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
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
                    MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception E) {
            if (mCurrentPage > 1) {
                MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
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
    public void onResume() {
        super.onResume();
        if (!StringUtils.isEmpty(mBookId)) {
            mLoading = -1;
            mCurrentPage = 1;
            getChapters();
        }
    }
}
