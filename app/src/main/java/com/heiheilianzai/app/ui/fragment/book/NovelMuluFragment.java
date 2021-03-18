package com.heiheilianzai.app.ui.fragment.book;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ChapterAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.ui.activity.CatalogInnerActivity;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.view.NestedListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NovelMuluFragment extends BaseButterKnifeFragment {
    @BindView(R.id.list_novel_mulu)
    public NestedListView mListView;
    private String mBookId;
    private BaseBook baseBook;
    public List<ChapterItem> mItemList;
    public ChapterAdapter mAdapter;

    @Override
    public int initContentView() {
        return R.layout.fragment_novel_mulu;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    public void sendData(BaseBook baseBook) {
        this.baseBook = baseBook;
        mBookId = baseBook.getBook_id();
        initData();
    }

    protected void initData() {
        ReaderParams params = new ReaderParams(getContext());
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    public void initInfo(final String json) {
        Gson gson = new Gson();
        mItemList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray chapterListArr = jsonObject.getJSONArray("chapter_list");
            int size = chapterListArr.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = chapterListArr.getJSONObject(i);
                ChapterItem chapterItem1 = new ChapterItem();
                BaseTag tag = gson.fromJson(jsonObject1.getString("tag"), BaseTag.class);
                chapterItem1.setChaptertab(tag.getTab());
                chapterItem1.setChaptercolor(tag.getColor());
                chapterItem1.setIs_vip(jsonObject1.getString("is_vip"));
                chapterItem1.setChapter_title(jsonObject1.getString("chapter_title"));
                chapterItem1.setChapter_id(jsonObject1.getString("chapter_id"));
                mItemList.add(chapterItem1);
            }
            if (!mItemList.isEmpty()) {
                mAdapter = new ChapterAdapter(getContext(), mItemList, mItemList.size());
                mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
                mAdapter.mDisplayOrder = 0;
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ReaderConfig.CatalogInnerActivityOpen = true;
                        if (activity != null) {
                            activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_catalog));
                            String is_vip = mItemList.get(position).getIs_vip();
                            if (is_vip != null && is_vip.equals("1") && !App.isVip(getContext())) {
                                DialogVip dialogVip = new DialogVip();
                                dialogVip.getDialogVipPop(getActivity(), false);
                                return;
                            }
                            ChapterManager.getInstance(getActivity()).openBook(baseBook, mBookId, mItemList.get(position).getChapter_id(), json);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
