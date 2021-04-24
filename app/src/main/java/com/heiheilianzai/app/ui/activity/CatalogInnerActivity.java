package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ChapterAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.DialogNovelCoupon;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 作品章节目录(从读书页开启的)
 * Created by scb on 2018/8/11.
 */
public class CatalogInnerActivity extends BaseActivity implements ShowTitle {
    public final String TAG = CatalogActivity.class.getSimpleName();
    public ListView mListView;
    public LinearLayout mBack;
    public TextView mTitle;
    public String mBookId;
    public List<ChapterItem> mItemList;
    public ChapterAdapter mAdapter;
    BaseBook baseBook;
    private int mDisplayOrder;
    private String current_chapter_id;
    public static Activity activity;
    private String coupon_pay_price;

    @Override
    public int initContentView() {
        return R.layout.activity_chapter_catalog;
    }

    @Override
    public void initView() {
        activity = this;
        mListView = findViewById(R.id.activity_chapter_catalog_listview);
    }

    @Override
    public void initData() {

    }

    @Override
    protected void onStart() {
        super.onStart();
        initDatA();
    }

    public void initDatA() {
        mBookId = getIntent().getStringExtra("book_id");
        baseBook = (BaseBook) getIntent().getSerializableExtra("book");
        try {
            BaseBook baseBook = LitePal.where("book_id = ?", mBookId).findFirst(BaseBook.class);
            mDisplayOrder = baseBook.getCurrent_chapter_displayOrder();
            current_chapter_id = baseBook.getCurrent_chapter_id();
            baseBook.setCurrent_chapter_displayOrder(mDisplayOrder);
            baseBook.setCurrent_chapter_id(current_chapter_id);
        } catch (Exception e) {
        }
        if (mBookId.contains("/")) {
            List<ChapterItem> chapterList = LitePal.where("book_id = ?", mBookId).find(ChapterItem.class);
            if (!chapterList.isEmpty()) {
                mItemList = chapterList;
                try {
                    initTitleBarView(mItemList.get(0).getBook_name());
                } catch (Exception e) {
                }
                mAdapter = new ChapterAdapter(this, mItemList, mItemList.size());
                mAdapter.current_chapter_id = current_chapter_id;
                mAdapter.mDisplayOrder = mDisplayOrder;
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        baseBook.saveIsexist(1);
                        if (mItemList.get(position) != null) {
                            String book_id = mItemList.get(position).getBook_id();
                            if (activity != null) {
                                activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_catalog));
                                ChapterManager.getInstance(activity).openBook(baseBook, book_id, mItemList.get(position).getChapter_id());
                            }
                        }
                        ReaderConfig.integerList.add(1);
                    }
                });

                if (mDisplayOrder < mItemList.size()) {
                    mListView.setSelection(mDisplayOrder);
                }


                return;
            }
        }
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
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

    @Override
    public void initInfo(final String json) {
        super.initInfo(json);
        Gson gson = new Gson();
        mItemList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray chapterListArr = jsonObject.getJSONArray("chapter_list");
            initTitleBarView(jsonObject.getString("name"));
            coupon_pay_price = jsonObject.getString("coupon_pay_price");
            int size = chapterListArr.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = chapterListArr.getJSONObject(i);
                ChapterItem chapterItem1 = new ChapterItem();
                BaseTag tag = gson.fromJson(jsonObject1.getString("tag"), BaseTag.class);
                chapterItem1.setChaptertab(tag.getTab());
                chapterItem1.setChaptercolor(tag.getColor());
                chapterItem1.setBook_id(jsonObject.getString("book_id"));
                chapterItem1.setIs_vip(jsonObject1.getString("is_vip"));
                chapterItem1.setChapter_title(jsonObject1.getString("chapter_title"));
                chapterItem1.setChapter_id(jsonObject1.getString("chapter_id"));
                chapterItem1.setIs_book_coupon_pay(jsonObject1.getString("is_book_coupon_pay"));
                mItemList.add(chapterItem1);
            }
            if (!mItemList.isEmpty()) {
                mAdapter = new ChapterAdapter(this, mItemList, mItemList.size());
                mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
                mAdapter.mDisplayOrder = 0;
                mAdapter.setCoupon_pay_price(coupon_pay_price);
                mListView.setAdapter(mAdapter);
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        ReaderConfig.CatalogInnerActivityOpen = true;
                        if (activity != null) {
                            activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_catalog));
                            ChapterItem chapterItem = mItemList.get(position);
                            ChapterManager.getInstance(CatalogInnerActivity.this).openBook(baseBook, mBookId, chapterItem.getChapter_id(), json);

                        }
                    }
                });
                if (mDisplayOrder < mItemList.size()) {
                    mListView.setSelection(mDisplayOrder);
                }
            }

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
        mBtn = findViewById(R.id.titlebar_order);
        mBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    buttonView.setBackgroundResource(R.mipmap.asc);
                    Collections.reverse(mItemList);
                } else {
                    buttonView.setBackgroundResource(R.mipmap.dsc);
                    Collections.reverse(mItemList);
                }
                mAdapter.notifyDataSetChanged();
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
}
