package com.heiheilianzai.app.ui.activity;

import android.view.View;
import android.widget.AbsListView;
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
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.jmessage.support.qiniu.android.utils.StringUtils;

/**
 * 作品章节目录
 * Created by scb on 2018/7/8.
 */
public class CatalogActivity extends BaseActivity implements ShowTitle {
    public final String TAG = CatalogActivity.class.getSimpleName();
    public ListView mListView;
    public LinearLayout mBack;
    public TextView mTitle;
    public String mBookId;
    public List<ChapterItem> mItemList = new ArrayList<>();
    public ChapterAdapter mAdapter;
    BaseBook baseBook;
    private int mDisplayOrder;//位置
    private String current_chapter_id = "-1";//已选择的章节ID
    private String mJson;//跳转阅读详情需要传入的数据
    private int mPageNum = 1;//请求页数
    private boolean isButtom;//是否滑动触底
    private boolean isLoadingData = true;//是否正在加载数据
    private boolean isLoadOverHintShow = false;//是否以显示过完成加载提示
    private int orderby = 1;// 1正序 2倒序

    @Override
    public int initContentView() {
        return R.layout.activity_chapter_catalog;
    }

    @Override
    public void initView() {
        mListView = findViewById(R.id.activity_chapter_catalog_listview);
        mAdapter = new ChapterAdapter(this, mItemList, mItemList.size(), false);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                baseBook.saveIsexist(0);
                String chapter_id = mItemList.get(position).getChapter_id();
                CatalogActivity.this.setTitle(LanguageUtil.getString(CatalogActivity.this, R.string.refer_page_catalog));
                String is_vip = mItemList.get(position).getIs_vip();
                if (is_vip != null && is_vip.equals("1") && !App.isVip(CatalogActivity.this)) {
                    DialogVip dialogVip = new DialogVip();
                    dialogVip.getDialogVipPop(CatalogActivity.this, getResources().getString(R.string.dialog_tittle_vip),false);
                    return;
                }
                ChapterManager.getInstance(CatalogActivity.this).openBook(baseBook, mBookId, chapter_id, mJson);
                ReaderConfig.integerList.add(1);
                mAdapter.mDisplayOrder = position;
                mAdapter.current_chapter_id = chapter_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    if (isButtom) {
                        MyToash.toashSuccessLoadMore(CatalogActivity.this, isLoadingData, isLoadOverHintShow, new MyToash.MyToashLoadMoreListener() {
                            @Override
                            public void onLoadingData() {
                                getDataCatalogInfo();
                            }

                            @Override
                            public void onState(boolean isLoadingData, boolean isLoadOver) {
                                CatalogActivity.this.isLoadingData = isLoadingData;
                                isLoadOverHintShow = isLoadOver;
                            }
                        });

                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem + visibleItemCount == totalItemCount) {
                    isButtom = true;
                } else {
                    isButtom = false;
                }
            }
        });
    }

    @Override
    public void initData() {
        initDatA();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initTitleBack();
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
        if ("-1".equals(current_chapter_id)) {
            if (mItemList.size() > 0) {
                mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
            }
        } else {
            mAdapter.current_chapter_id = current_chapter_id;
        }
        mAdapter.mDisplayOrder = mDisplayOrder;
        getDataCatalogInfo();
    }

    @Override
    public void initInfo(final String json) {
        super.initInfo(json);
        Gson gson = new Gson();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray chapterListArr = jsonObject.getJSONArray("chapter_list");
            initTitleBarView(jsonObject.getString("name"));
            int size = chapterListArr.length();
            if (mPageNum == 1) {
                mItemList.clear();
            }
            if (isLoadingData) {
                mPageNum += 1;
            }
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = chapterListArr.getJSONObject(i);
                ChapterItem chapterItem1 = new ChapterItem();
                BaseTag tag = gson.fromJson(jsonObject1.getString("tag"), BaseTag.class);
                chapterItem1.setChaptertab(tag.getTab());
                chapterItem1.setChaptercolor(tag.getColor());
                chapterItem1.setChapter_id(jsonObject1.getString("chapter_id"));
                chapterItem1.setChapter_title(jsonObject1.getString("chapter_title"));
                chapterItem1.setIs_vip(jsonObject1.getString("is_vip"));
                mItemList.add(chapterItem1);
            }
            mJson = json;
            if (!mItemList.isEmpty()) {
                if ("-1".equals(current_chapter_id) || StringUtils.isNullOrEmpty(current_chapter_id)) {
                    mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
                }
                mAdapter.notifyDataSetChanged();
                if (mPageNum == 2) {
                    if (mDisplayOrder < mItemList.size()) {
                        mListView.setSelection(mDisplayOrder);
                    }
                }
            }
        } catch (Exception e) {
            MyToash.ToashError(getBaseContext(), getString(R.string.load_fail));
            MyToash.LogE("CatalogActivity", e.getMessage());
        }
    }

    @Override
    public void initTitleBarView(String text) {
        TextView mTitle;
        ToggleButton mBtn;
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
        mTitle.setText(text);
    }

    void initTitleBack() {
        LinearLayout mBack;
        mBack = findViewById(R.id.titlebar_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    void getDataCatalogInfo() {
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
}
