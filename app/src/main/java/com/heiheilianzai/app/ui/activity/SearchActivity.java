package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.HotWordsAdapter;
import com.heiheilianzai.app.adapter.OptionRecyclerViewAdapter;
import com.heiheilianzai.app.adapter.SearchVerticalAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.model.SearchItem;
import com.heiheilianzai.app.model.SerachItem;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import androidx.recyclerview.widget.LinearLayoutManager;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 搜索首页
 * Created by scb on 2018/7/9.
 */
public class SearchActivity extends BaseButterKnifeActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_search;
    }

    @BindView(R.id.activity_search_keywords_listview)
    public XRecyclerView fragment_option_listview;
    /**
     * 搜索框
     */
    @BindView(R.id.activity_search_keywords)
    public EditText activity_search_keywords;
    /**
     * cancel
     */
    @BindView(R.id.activity_search_cancel)
    public TextView activity_search_cancel;
    /**
     * 热词grid
     */
    @BindView(R.id.activity_search_hotwords_grid)
    public AdaptionGridViewNoMargin activity_search_hotwords_grid;
    /**
     * 热搜榜grid
     */
    @BindView(R.id.activity_search_book_grid)
    public AdaptionGridViewNoMargin activity_search_book_grid;
    @BindView(R.id.activity_search_keywords_listview_noresult)
    public LinearLayout activity_search_keywords_listview_noresult;
    @BindView(R.id.activity_search_keywords_scrollview)
    public ObservableScrollView activity_search_keywords_scrollview;

    public String mKeyWord, mKeyWordHint;
    Gson gson = new Gson();
    int total_page, current_page = 1;
    OptionRecyclerViewAdapter optionAdapter;
    LayoutInflater layoutInflater;
    List<OptionBeen> optionBeenList;
    int PRODUCT;//1小说  2漫画 3动漫
    boolean mIsHotSearch = false;
    int Size;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    OptionRecyclerViewAdapter.OnItemClick onItemClick = new OptionRecyclerViewAdapter.OnItemClick() {
        @Override
        public void OnItemClick(int position, OptionBeen optionBeen) {
            upHotWord(optionBeen);
            Intent intent = null;
            int stringId = mIsHotSearch ? R.string.refer_page_hot_search : R.string.refer_page_search;
            if (PRODUCT == 1) {
                intent = BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, stringId), optionBeen.getBook_id());
            } else if (PRODUCT == 2) {
                intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, stringId), optionBeen.getComic_id());
            } else {
                intent = CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, stringId), optionBeen.getVideo_id());
            }
            startActivity(intent);
        }
    };

    private void upHotWord(OptionBeen optionBeen) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("hot_word", optionBeen.getName());
        String json = params.generateParamsJson();
        String url;
        if (PRODUCT == 1) {
            url = ReaderConfig.getBaseUrl() + BookConfig.mUpBookWord;
        } else if (PRODUCT == 2) {
            url = ReaderConfig.getBaseUrl() + ComicConfig.mUpComicWord;
        } else {
            url = ReaderConfig.getBaseUrl() + CartoonConfig.mUpCartoonWord;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
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

    public void initView() {
        optionBeenList = new ArrayList<>();
        layoutInflater = LayoutInflater.from(activity);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_option_listview.setLayoutManager(layoutManager);
        fragment_option_listview.addHeaderView((LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null));
        PRODUCT = getIntent().getIntExtra("PRODUCT", 0);
        mKeyWord = getIntent().getStringExtra("mKeyWord");
        if (mKeyWord != null) {
            mKeyWordHint = mKeyWord;
            activity_search_keywords.setHint(mKeyWordHint);
        }
        fragment_option_listview.setPullRefreshEnabled(false);
        fragment_option_listview.setLoadingMoreEnabled(true);
        fragment_option_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadMore() {
                if (mKeyWord != null) {
                    if (current_page <= total_page) {
                        gotoSearch(mKeyWord);
                    } else {
                        fragment_option_listview.loadMoreComplete();
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                    }

                }
            }
        });
        activity_search_keywords.clearFocus();
        activity_search_keywords.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String KeyWord = activity_search_keywords.getText().toString() + "";
                    if (TextUtils.isEmpty(KeyWord) && Pattern.matches("\\s*", KeyWord)) {
                        mKeyWord = mKeyWordHint;
                    } else {
                        mKeyWord = KeyWord;
                    }
                    mIsHotSearch = false;
                    current_page = 1;
                    gotoSearch(mKeyWord);
                    return true;
                }
                return false;
            }
        });
        activity_search_keywords.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    fragment_option_listview.setVisibility(View.GONE);
                    activity_search_keywords_scrollview.setVisibility(View.VISIBLE);
                }
            }
        });
        initData();
    }

    public void initData() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        String url;
        if (PRODUCT == 1) {
            url = ReaderConfig.getBaseUrl() + BookConfig.mSearchIndexUrl;
        } else if (PRODUCT == 2) {
            url = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_search_index;
        } else {
            url = ReaderConfig.getBaseUrl() + CartoonConfig.mUpCartoonWord;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
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

    public void initInfo(String json) {
        SerachItem serachItem = gson.fromJson(json, SerachItem.class);
        if (serachItem.hot_word.length != 0) {
            if (mKeyWordHint == null) {
                activity_search_keywords.setHint(serachItem.hot_word[0]);
            }
            HotWordsAdapter hotWordsAdapter = new HotWordsAdapter(activity, serachItem.hot_word);
            activity_search_hotwords_grid.setAdapter(hotWordsAdapter);
            activity_search_hotwords_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mKeyWord = serachItem.hot_word[position];
                    activity_search_keywords.setText(mKeyWord);
                    mIsHotSearch = true;
                    current_page = 1;
                    gotoSearch(mKeyWord);
                }
            });
        }
        SearchVerticalAdapter adapter = new SearchVerticalAdapter(activity, serachItem.list, PRODUCT);
        activity_search_book_grid.setAdapter(adapter);
        activity_search_book_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = null;
                if (PRODUCT == 1) {
                    intent = BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_hot_search_list), serachItem.list.get(position).getBook_id());
                } else if (PRODUCT == 2) {
                    intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_hot_search_list), serachItem.list.get(position).getComic_id());
                } else {
                    intent = CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_hot_search_list), serachItem.list.get(position).getVideo_id());
                }
                startActivity(intent);
            }
        });
        setSearchRecommendationEvent(serachItem);
    }

    @OnClick(value = {R.id.activity_search_cancel})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_search_cancel:
                finish();
                break;
        }
    }

    /**
     * 开始搜索
     *
     * @param keyword
     */
    public void gotoSearch(String keyword) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("keyword", keyword);
        params.putExtraParams("page_num", current_page + "");
        String json = params.generateParamsJson();
        String url;
        if (PRODUCT == 1) {
            url = ReaderConfig.getBaseUrl() + BookConfig.mSearchUrl;
        } else if (PRODUCT == 2) {
            url = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_search;
        } else {
            url = ReaderConfig.getBaseUrl() + CartoonConfig.mSearchUrl;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initNextInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void initNextInfo(String result) {
        OptionItem optionItem = gson.fromJson(result, OptionItem.class);
        total_page = optionItem.total_page;
        MyToash.Log("initNextInfo", optionItem.toString());
        if (total_page != 0 && (current_page <= total_page && !optionItem.list.isEmpty())) {
            int optionItem_list_size = optionItem.list.size();
            if (current_page == 1) {
                optionBeenList.clear();
                optionBeenList.addAll(optionItem.list);
                optionAdapter = null;
                Size = optionItem_list_size;
                optionAdapter = new OptionRecyclerViewAdapter(activity, optionBeenList, 0, PRODUCT, layoutInflater, onItemClick);
                fragment_option_listview.setAdapter(optionAdapter);
                activity_search_keywords_listview_noresult.setVisibility(View.GONE);
                fragment_option_listview.setVisibility(View.VISIBLE);
                activity_search_keywords_scrollview.setVisibility(View.GONE);
            } else {
                fragment_option_listview.loadMoreComplete();//加载完成必须更改状态不然loadmore不执行
                optionBeenList.addAll(optionItem.list);
                int t = Size + optionItem_list_size;
                optionAdapter.notifyItemRangeInserted(Size + 1, t);
                Size = t;
            }
            current_page = optionItem.current_page;
            ++current_page;
        } else {
            if (current_page != 1) {
                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
            }
        }
        if (current_page == 1) {
            if (total_page == 0 || optionBeenList.isEmpty()) {
                activity_search_keywords_listview_noresult.setVisibility(View.VISIBLE);
                fragment_option_listview.setVisibility(View.GONE);
            } else {
                fragment_option_listview.setVisibility(View.VISIBLE);
                activity_search_keywords_listview_noresult.setVisibility(View.GONE);
            }
        }
        activity_search_keywords_scrollview.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        // 判断时间间隔
        if (fragment_option_listview.getVisibility() == View.VISIBLE || activity_search_keywords_listview_noresult.getVisibility() == View.VISIBLE) {
            fragment_option_listview.setVisibility(View.GONE);
            activity_search_keywords_listview_noresult.setVisibility(View.GONE);
            activity_search_keywords_scrollview.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    /**
     * 神策埋点  搜索推荐
     */
    private void setSearchRecommendationEvent(SerachItem serachItem) {
        if (serachItem == null) {
            return;
        }
        List<String> hotWord = new ArrayList<>();
        List<String> hotList = new ArrayList<>();
        if (serachItem.hot_word != null) {
            hotWord.addAll(Arrays.asList(serachItem.hot_word));
            setSearchRecommendationEvent(LanguageUtil.getString(activity, R.string.refer_page_hot_search), hotWord);
        }
        if (serachItem.list != null) {
            for (SearchItem searchItem : serachItem.list) {
                hotList.add(PRODUCT == 1 ? searchItem.book_id : searchItem.comic_id);
            }
            setSearchRecommendationEvent(LanguageUtil.getString(activity, R.string.refer_page_hot_search_list), hotList);
        }

    }

    private void setSearchRecommendationEvent(String recommendType, List<String> work_id) {
        SensorsDataHelper.setSearchRecommendationEvent(PRODUCT == 1 ? SaVarConfig.WORKS_TYPE_BOOK : SaVarConfig.WORKS_TYPE_COMICS, recommendType, work_id);
    }
}