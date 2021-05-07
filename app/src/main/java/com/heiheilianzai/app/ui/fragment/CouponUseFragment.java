package com.heiheilianzai.app.ui.fragment;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.CouponUseAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.CouponUseBean;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class CouponUseFragment extends BaseButterKnifeFragment {
    @BindView(R.id.fragment_coupon_no_result)
    public LinearLayout mLlNoResult;
    @BindView(R.id.fragment_coupon_ry)
    public XRecyclerView mRyCoupon;
    @BindView(R.id.fragment_coupon_day)
    public TextView mTXDay;
    @BindView(R.id.fragment_coupon_month)
    public TextView mTxMonth;
    @BindView(R.id.fragment_coupon_all)
    public TextView mTxAll;
    private String mSelectType = "7";//默认近七天
    private int mCurrentPage = 1;
    private int mLoading = -1;
    private int mTotalPage, mSize;
    private List<CouponUseBean.ListBean> mUseLists = new ArrayList<>();
    private CouponUseAdapter mUseAdapter;

    @Override
    public int initContentView() {
        return R.layout.fragment_coupon;
    }

    @Override
    protected void initData() {
        getAcceptRecord();
    }

    @Override
    protected void initView() {
        super.initView();
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRyCoupon.setLayoutManager(layoutManager);
        mUseAdapter = new CouponUseAdapter(getActivity(), mUseLists);
        mRyCoupon.setAdapter(mUseAdapter);
        mTXDay.setOnClickListener(v -> {
            changeTypeView(1);
            getAcceptRecord();
        });
        mTxMonth.setOnClickListener(v -> {
            changeTypeView(2);
            getAcceptRecord();
        });
        mTxAll.setOnClickListener(v -> {
            changeTypeView(3);
            getAcceptRecord();
        });
        mRyCoupon.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mLoading = -1;
                mCurrentPage = 1;
                if (!StringUtils.isEmpty(mSelectType)) {
                    getAcceptRecord();
                }
            }

            @Override
            public void onLoadMore() {
                mLoading = 1;
                if (!StringUtils.isEmpty(mSelectType)) {
                    if (mTotalPage >= mCurrentPage) {
                        getAcceptRecord();
                    } else {
                        mRyCoupon.loadMoreComplete();
                        MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
                    }
                }
            }
        });
    }

    private void changeTypeView(int i) {
        mCurrentPage = 1;
        if (i == 1) {
            mSelectType = "7";
            mTXDay.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_10));
            mTxMonth.setBackground(null);
            mTxAll.setBackground(null);
            mTXDay.setTextColor(getResources().getColor(R.color.color_1a1a1a));
            mTxMonth.setTextColor(getResources().getColor(R.color.color_666666));
            mTxAll.setTextColor(getResources().getColor(R.color.color_666666));
        } else if (i == 2) {
            mSelectType = "30";
            mTXDay.setBackground(null);
            mTxMonth.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_10));
            mTxAll.setBackground(null);
            mTXDay.setTextColor(getResources().getColor(R.color.color_666666));
            mTxMonth.setTextColor(getResources().getColor(R.color.color_1a1a1a));
            mTxAll.setTextColor(getResources().getColor(R.color.color_666666));
        } else {
            mSelectType = "90";
            mTXDay.setBackground(null);
            mTxMonth.setBackground(null);
            mTxAll.setBackground(getResources().getDrawable(R.drawable.shape_ffffff_10));
            mTXDay.setTextColor(getResources().getColor(R.color.color_666666));
            mTxMonth.setTextColor(getResources().getColor(R.color.color_666666));
            mTxAll.setTextColor(getResources().getColor(R.color.color_1a1a1a));
        }
    }

    private void getAcceptRecord() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("page", mCurrentPage + "");
        params.putExtraParams("days", mSelectType);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.COUPON_USE, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        closeLoading();
                        initInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        closeLoading();
                    }
                }
        );
    }

    private void initInfo(String result) {
        try {
            CouponUseBean optionItem = new Gson().fromJson(result, CouponUseBean.class);
            mRyCoupon.setVisibility(View.VISIBLE);
            mLlNoResult.setVisibility(View.GONE);
            mTotalPage = optionItem.getPage_info().getTotal_page();
            int optionItem_list_size = optionItem.getList().size();
            if (mCurrentPage <= mTotalPage && optionItem_list_size != 0) {
                if (mCurrentPage == 1) {
                    mUseLists.clear();
                    mUseLists.addAll(optionItem.getList());
                    mSize = optionItem_list_size;
                    mUseAdapter.notifyDataSetChanged();
                } else {
                    mUseLists.addAll(optionItem.getList());
                    int t = mSize + optionItem_list_size;
                    mUseAdapter.notifyItemRangeInserted(mSize + 2, optionItem_list_size);
                    mSize = t;
                }
                ++mCurrentPage;

            } else {
                if (mUseLists.isEmpty()) {
                    mLlNoResult.setVisibility(View.VISIBLE);
                }
                if (mCurrentPage > 1) {
                    MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception E) {
            if (mCurrentPage > 1) {
                MyToash.ToashError(getActivity(), LanguageUtil.getString(getContext(), R.string.ReadActivity_chapterfail));
            } else {
                mRyCoupon.setVisibility(View.GONE);
                mLlNoResult.setVisibility(View.VISIBLE);
            }
        }
    }

    private void closeLoading() {
        if (mLoading == -1) {
            mRyCoupon.refreshComplete();
        } else if (mLoading == 1) {
            mRyCoupon.loadMoreComplete();
        }
    }
}
