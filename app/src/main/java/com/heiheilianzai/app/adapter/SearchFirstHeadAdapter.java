package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.SearchBox;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SearchFirstHeadAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<SearchBox.SearchBoxLabe> mLists;
    private List<SearchBox.SearchBoxLabe> mSelectLists;
    private OnBackSelectHeadLists mOnBackSelectHeadLists;
    private boolean mIsFold = true;

    public void setmIsFold(boolean mIsFold) {
        this.mIsFold = mIsFold;
    }

    public void setmOnBackSelectHeadLists(OnBackSelectHeadLists mOnBackSelectHeadLists) {
        this.mOnBackSelectHeadLists = mOnBackSelectHeadLists;
    }

    public SearchFirstHeadAdapter(Activity mActivity, List<SearchBox.SearchBoxLabe> mLists) {
        mSelectLists = new ArrayList<>();
        this.mActivity = mActivity;
        this.mLists = mLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.item_search_head_first, parent, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        SearchBox.SearchBoxLabe searchBoxLabe = mLists.get(position);
        viewHolder.txItem.setText(searchBoxLabe.getDisplay().trim());
        boolean select = searchBoxLabe.getChecked() == 1 ? true : false;
        setSelectView(viewHolder, select, position);
        viewHolder.txItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean select = searchBoxLabe.getChecked() == 1 ? true : false;
                if (position == 0) {
                    mSelectLists.clear();
                    mSelectLists.add(searchBoxLabe);
                } else {
                    select = !select;
                    if (select) {
                        if (mSelectLists.size() >= 3) {
                            mSelectLists.remove(0);
                        } else {
                            if (mSelectLists.size() > 0 && mSelectLists.get(0).getValue().equals("0")) {
                                mSelectLists.remove(0);
                            }
                        }
                        mSelectLists.add(searchBoxLabe);
                    } else {
                        if (mSelectLists != null && mSelectLists.size() > 0) {
                            for (int i = 0; i < mSelectLists.size(); i++) {
                                SearchBox.SearchBoxLabe searchBoxLabe1 = mSelectLists.get(i);
                                if (TextUtils.equals(searchBoxLabe.getValue(), searchBoxLabe1.getValue())) {
                                    mSelectLists.remove(i);
                                }
                            }
                        }
                    }
                }
                mOnBackSelectHeadLists.onBackSelectHeadLists(mSelectLists);
            }
        });
    }

    private void setSelectView(ViewHolder viewHolder, boolean select, int position) {
        if (select) {
            if (position == 0) {
                viewHolder.txItem.setBackground(null);
                viewHolder.txItem.setTextColor(mActivity.getResources().getColor(R.color.color_ff8350));
            } else {
                viewHolder.txItem.setBackground(mActivity.getDrawable(R.drawable.shape_ff8350_20));
                viewHolder.txItem.setTextColor(mActivity.getResources().getColor(R.color.white));
            }
        } else {
            viewHolder.txItem.setBackground(null);
            viewHolder.txItem.setTextColor(mActivity.getResources().getColor(R.color.color_3b3b3b));
        }
    }

    @Override
    public int getItemCount() {
        if (mIsFold) {
            if (mLists.size() > 15) {
                return 14;
            }
        } else {
            if (mLists.size() > 15 && mLists.size() % 5 == 0) {
                return mLists.size() - 1;
            }
        }
        return mLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_item)
        public TextView txItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnBackSelectHeadLists {
        void onBackSelectHeadLists(List<SearchBox.SearchBoxLabe> selectLists);
    }
}
