package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.RankItem;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<RankItem> mRankLists;
    private int mSelect = 0;
    private OnSelectTopListItemListener mOnSelectTopListItemListener;

    public void setmSelect(int mSelect) {
        this.mSelect = mSelect;
    }

    public void setmOnSelectTopListItemListener(OnSelectTopListItemListener mOnSelectTopListItemListener) {
        this.mOnSelectTopListItemListener = mOnSelectTopListItemListener;
    }

    public TopListAdapter(Context mContext, List<RankItem> mRankLists) {
        this.mContext = mContext;
        this.mRankLists = mRankLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_top_list, null);
        return new TopViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        TopViewHolder topViewHolder = (TopViewHolder) holder;
        RankItem rankItem = mRankLists.get(position);
        topViewHolder.mTittle.setText(rankItem.getList_name());
        if (position == mSelect) {
            topViewHolder.mTittle.setBackground(mContext.getDrawable(R.mipmap.top_orange_bg));
            topViewHolder.mTittle.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            topViewHolder.mTittle.setBackground(mContext.getDrawable(R.mipmap.top_white_bg));
            topViewHolder.mTittle.setTextColor(mContext.getResources().getColor(R.color.color_666666));
        }
        topViewHolder.mTittle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectTopListItemListener != null) {
                    mOnSelectTopListItemListener.onSelctTopListItem(rankItem, position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mRankLists == null ? 0 : mRankLists.size();
    }

    class TopViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_item_top_list_tittle)
        public TextView mTittle;

        public TopViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnSelectTopListItemListener {
        void onSelctTopListItem(RankItem rankItem, int positon);
    }
}
