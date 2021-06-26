package com.heiheilianzai.app.ui.activity;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.TopDetailAdapter;
import com.heiheilianzai.app.adapter.TopListAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.model.RankItem;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.constant.BookConfig.mRankListUrl;
import static com.heiheilianzai.app.constant.BookConfig.mRankUrl;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_rank_index;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_rank_list;

public class TopActivity extends BaseActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout mBack;
    @BindView(R.id.titlebar_text)
    public TextView mTitle;
    @BindView(R.id.ry_top_list)
    public RecyclerView mRyTopList;
    @BindView(R.id.ry_top_detail)
    public XRecyclerView mRyTopDetail;
    @BindView(R.id.fragment_option_noresult)
    public LinearLayout mLlNoResult;
    private boolean PRODUCT;
    private String httpTopUrlList;
    List<RankItem> mRankItemList;
    private TopListAdapter mTopListAdapter;
    private int current_page = 1;
    int LoadingListener = 0;
    private String mRankType;
    private int mTotalPage;
    private int Size;
    List<OptionBeen> mOptionBeenList = new ArrayList<>();
    private TopDetailAdapter mTopDetailAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_top;
    }

    @Override
    public void initView() {
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(TopActivity.this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRyTopList.setLayoutManager(layoutManager);
        mTitle.setText(R.string.string_top_tittle);
        PRODUCT = getIntent().getBooleanExtra("PRODUCT", false);
        MyContentLinearLayoutManager layoutManagerDetail = new MyContentLinearLayoutManager(TopActivity.this);
        layoutManagerDetail.setOrientation(LinearLayoutManager.VERTICAL);
        mRyTopDetail.setLayoutManager(layoutManagerDetail);
        mTopDetailAdapter = new TopDetailAdapter(TopActivity.this, mOptionBeenList, PRODUCT,"0");
        mRyTopDetail.setAdapter(mTopDetailAdapter);
        if (!PRODUCT) {
            httpTopUrlList = ReaderConfig.getBaseUrl() + COMIC_rank_index;
        } else {
            httpTopUrlList = ReaderConfig.getBaseUrl() + mRankUrl;
        }
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTopDetailAdapter.setmOnSelectTopListItemListener(new TopDetailAdapter.OnSelectTopListItemListener() {
            @Override
            public void onSelctTopListItem(OptionBeen rankItem, int positon) {
                Intent intent = new Intent();
                if (PRODUCT) {
                    intent = BookInfoActivity.getMyIntent(TopActivity.this, "", rankItem.getBook_id());
                } else {
                    intent = ComicInfoActivity.getMyIntent(TopActivity.this, "", rankItem.getComic_id());
                }
                startActivity(intent);
            }
        });

        mRyTopDetail.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                LoadingListener = -1;
                current_page = 1;
                if (!StringUtils.isEmpty(mRankType)) {
                    requstTopDetailData(mRankType);
                }
            }

            @Override
            public void onLoadMore() {
                LoadingListener = 1;
                if (!StringUtils.isEmpty(mRankType)) {
                    if (mTotalPage >= current_page) {
                        requstTopDetailData(mRankType);
                    } else {
                        mRyTopDetail.loadMoreComplete();
                        MyToash.ToashError(TopActivity.this, LanguageUtil.getString(TopActivity.this, R.string.ReadActivity_chapterfail));
                    }
                }
            }
        });
    }

    @Override
    public void initData() {
        if (httpTopUrlList == null) {
            return;
        }
        ReaderParams params = new ReaderParams(TopActivity.this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(TopActivity.this).sendRequestRequestParams3(httpTopUrlList, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            initTopList(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {


                    }
                }
        );
    }

    private void initTopList(String result) {
        mRankItemList = new Gson().fromJson(result, new TypeToken<List<RankItem>>() {
        }.getType());
        if (mRankItemList == null || mRankItemList.isEmpty()) {
            return;
        } else {
            mTopListAdapter = new TopListAdapter(TopActivity.this, mRankItemList);
            mRyTopList.setAdapter(mTopListAdapter);
            mRankType = mRankItemList.get(0).getRank_type();
            requstTopDetailData(mRankType);
        }
        mTopListAdapter.setmOnSelectTopListItemListener(new TopListAdapter.OnSelectTopListItemListener() {
            @Override
            public void onSelctTopListItem(RankItem rankItem, int positon) {
                mRankType = rankItem.getRank_type();
                current_page = 1;
                requstTopDetailData(mRankType);
                mTopListAdapter.setmSelect(positon);
                mTopListAdapter.notifyDataSetChanged();
            }
        });
    }

    private void requstTopDetailData(String rank_type) {
        if (!PRODUCT) {
            httpTopUrlList = ReaderConfig.getBaseUrl() + COMIC_rank_list;
        } else {
            httpTopUrlList = ReaderConfig.getBaseUrl() + mRankListUrl;
        }
        if (httpTopUrlList == null) {
            return;
        }
        ReaderParams params = new ReaderParams(TopActivity.this);
        params.putExtraParams("rank_type", rank_type);
        params.putExtraParams("page_num", current_page + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(TopActivity.this).sendRequestRequestParams3(httpTopUrlList, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            initTopDeatal(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (LoadingListener == -1) {
                                mRyTopDetail.refreshComplete();
                            } else if (LoadingListener == 1) {
                                mRyTopDetail.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        try {
                            if (LoadingListener == -1) {
                                mRyTopDetail.refreshComplete();
                            } else if (LoadingListener == 1) {
                                mRyTopDetail.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }
                    }
                }
        );
    }

    private void initTopDeatal(String result) {
        try {
            OptionItem optionItem = new Gson().fromJson(result, OptionItem.class);
            mRyTopDetail.setVisibility(View.VISIBLE);
            mLlNoResult.setVisibility(View.GONE);
            mTotalPage = optionItem.total_page;
            int optionItem_list_size = optionItem.list.size();
            if (current_page <= mTotalPage && optionItem_list_size != 0) {
                if (current_page == 1) {
                    mOptionBeenList.clear();
                    mOptionBeenList.addAll(optionItem.list);
                    Size = optionItem_list_size;
                    mTopDetailAdapter.notifyDataSetChanged();
                } else {
                    mOptionBeenList.addAll(optionItem.list);
                    int t = Size + optionItem_list_size;
                    mTopDetailAdapter.notifyItemRangeInserted(Size + 2, optionItem_list_size);
                    Size = t;
                }
                current_page = optionItem.current_page;
                ++current_page;

            } else {
                if (mOptionBeenList.isEmpty()) {
                    mLlNoResult.setVisibility(View.VISIBLE);
                }
                if (current_page > 1) {
                    MyToash.ToashError(TopActivity.this, LanguageUtil.getString(TopActivity.this, R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception E) {
            mRyTopDetail.setVisibility(View.GONE);
            mLlNoResult.setVisibility(View.VISIBLE);
        }
    }
}