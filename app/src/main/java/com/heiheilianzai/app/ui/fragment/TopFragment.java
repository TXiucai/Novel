package com.heiheilianzai.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.TopDetailAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
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
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_rank_list;

public class TopFragment extends BaseButterKnifeFragment {

    @BindView(R.id.ry_top_detail)
    public XRecyclerView mRyTopDetail;
    @BindView(R.id.fragment_option_noresult)
    public LinearLayout mLlNoResult;
    private static final String ARG_PARAM = "itemType";
    private static final String ARG_PARAM2 = "type";
    private String mParam;
    private boolean mType;
    private int mCurrentPage = 1;
    int mLoadingListener = 0;
    private String httpTopUrlList;
    private int mTotalPage;
    private int mSize;
    private List<OptionBeen> mOptionBeenList = new ArrayList<>();
    private TopDetailAdapter mTopDetailAdapter;

    public TopFragment() {
    }

    public static TopFragment newInstance(String typeItem, boolean type) {
        TopFragment fragment = new TopFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, typeItem);
        args.putBoolean(ARG_PARAM2, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
            mType = getArguments().getBoolean(ARG_PARAM2);
        }
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_top;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MyContentLinearLayoutManager layoutManagerDetail = new MyContentLinearLayoutManager(activity);
        layoutManagerDetail.setOrientation(LinearLayoutManager.VERTICAL);
        mRyTopDetail.setLayoutManager(layoutManagerDetail);
        mTopDetailAdapter = new TopDetailAdapter(activity, mOptionBeenList, mType);
        mRyTopDetail.setAdapter(mTopDetailAdapter);
        requstTopDetailData(mParam);
        mTopDetailAdapter.setmOnSelectTopListItemListener(new TopDetailAdapter.OnSelectTopListItemListener() {
            @Override
            public void onSelctTopListItem(OptionBeen rankItem, int positon) {
                Intent intent = new Intent();
                if (mType) {
                    intent = BookInfoActivity.getMyIntent(activity, "", rankItem.getBook_id());
                } else {
                    intent = ComicInfoActivity.getMyIntent(activity, "", rankItem.getComic_id());
                }
                startActivity(intent);
            }
        });

        mRyTopDetail.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mLoadingListener = -1;
                mCurrentPage = 1;
                if (!StringUtils.isEmpty(mParam)) {
                    requstTopDetailData(mParam);
                }
            }

            @Override
            public void onLoadMore() {
                mLoadingListener = 1;
                ++mCurrentPage;
                if (!StringUtils.isEmpty(mParam)) {
                    if (mTotalPage >= mCurrentPage) {
                        requstTopDetailData(mParam);
                    } else {
                        mRyTopDetail.loadMoreComplete();
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                    }
                }
            }
        });
    }

    private void requstTopDetailData(String rank_type) {
        if (!mType) {
            httpTopUrlList = ReaderConfig.getBaseUrl() + COMIC_rank_list;
        } else {
            httpTopUrlList = ReaderConfig.getBaseUrl() + mRankListUrl;
        }
        if (httpTopUrlList == null) {
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("rank_type", rank_type);
        params.putExtraParams("page_num", mCurrentPage + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(httpTopUrlList, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            initTopDeatal(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        try {
                            if (mLoadingListener == -1) {
                                mRyTopDetail.refreshComplete();
                            } else if (mLoadingListener == 1) {
                                mRyTopDetail.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        try {
                            if (mLoadingListener == -1) {
                                mRyTopDetail.refreshComplete();
                            } else if (mLoadingListener == 1) {
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
            if (mCurrentPage <= mTotalPage && optionItem_list_size != 0) {
                if (mCurrentPage == 1) {
                    mOptionBeenList.clear();
                    mOptionBeenList.addAll(optionItem.list);
                    mSize = optionItem_list_size;
                    mTopDetailAdapter.notifyDataSetChanged();
                } else {
                    mOptionBeenList.addAll(optionItem.list);
                    int t = mSize + optionItem_list_size;
                    mTopDetailAdapter.notifyItemRangeInserted(mSize + 2, optionItem_list_size);
                    mSize = t;
                }
                mCurrentPage = optionItem.current_page;
            } else {
                if (mOptionBeenList.isEmpty()) {
                    mLlNoResult.setVisibility(View.VISIBLE);
                }
                if (mCurrentPage > 1) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception E) {
            mRyTopDetail.setVisibility(View.GONE);
            mLlNoResult.setVisibility(View.VISIBLE);
        }
    }
}