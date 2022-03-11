package com.heiheilianzai.app;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.comic.DisplayUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChannelBean.ListBean> mList;
    private Activity mContext;
    private int mType;
    private int mSelection = 0;
    private OnChannelItemClickListener mOnChannelItemClickListener;

    public void setSelection(int mSelection) {
        this.mSelection = mSelection;
        notifyDataSetChanged();
    }

    public void setOnChannelItemClickListener(OnChannelItemClickListener mOnChannelItemClickListener) {
        this.mOnChannelItemClickListener = mOnChannelItemClickListener;
    }

    public ChannelAdapter(List<ChannelBean.ListBean> mList, Activity mContext, int mType) {
        this.mList = mList;
        this.mContext = mContext;
        this.mType = mType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (mType == 0) {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_channel_home, parent, false);
            return new ChannelHomeHolder(inflate);
        } else {
            View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_channel_detail, parent, false);
            return new ChannelDetailHolder(inflate);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (mType == 0) {
            ChannelHomeHolder homeHolder = (ChannelHomeHolder) holder;
            if (mSelection == position) {
                homeHolder.mTxChannel.setTextSize(20);
                homeHolder.mImgBackground.setVisibility(View.VISIBLE);
            } else {
                homeHolder.mTxChannel.setTextSize(14);
                homeHolder.mImgBackground.setVisibility(View.GONE);
            }
            homeHolder.mTxChannel.setText(mList.get(position).getChannel_name());
            if (mOnChannelItemClickListener != null) {
                homeHolder.mTxChannel.setOnClickListener((v) -> mOnChannelItemClickListener.onChannelItemClick(mList.get(position), position));
            }
        } else {
            ChannelDetailHolder detailHolder = (ChannelDetailHolder) holder;
            ViewGroup.LayoutParams layoutParams = detailHolder.mRlItem.getLayoutParams();
            layoutParams.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px(mContext, 20) * 2) / 3;
            layoutParams.height = layoutParams.width;
            detailHolder.mRlItem.setLayoutParams(layoutParams);
            ViewGroup.LayoutParams mCirImgLayoutParams = detailHolder.mCirImg.getLayoutParams();
            mCirImgLayoutParams.width = layoutParams.width * 1 / 2;
            mCirImgLayoutParams.height = mCirImgLayoutParams.width;
            detailHolder.mCirImg.setLayoutParams(mCirImgLayoutParams);
            MyPicasso.GlideImageNoSize(mContext, mList.get(position).getChannel_icon(), detailHolder.mCirImg);
            detailHolder.mTxTittle.setText(mList.get(position).getChannel_name());
            if (mOnChannelItemClickListener != null) {
                detailHolder.mRlItem.setOnClickListener((v -> mOnChannelItemClickListener.onChannelItemClick(mList.get(position), position)));
            }
        }
    }

    @Override
    public int getItemCount() {
        return mList == null ? 0 : mList.size();
    }

    public class ChannelHomeHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tx_channel)
        TextView mTxChannel;
        @BindView(R.id.img_background)
        View mImgBackground;

        public ChannelHomeHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public class ChannelDetailHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_head)
        CircleImageView mCirImg;
        @BindView(R.id.tx_tittle)
        TextView mTxTittle;
        @BindView(R.id.rl_item)
        RelativeLayout mRlItem;

        public ChannelDetailHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnChannelItemClickListener {
        void onChannelItemClick(ChannelBean.ListBean item, int positon);
    }
}
