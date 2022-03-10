package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.HomeRecommendBean;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.view.comic.DisplayUtil;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeRecommendAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<HomeRecommendBean.RecommeListBean> mRecommendLists;
    private OnItemRecommendListener onItemRecommendListener;

    public void setOnItemRecommendListener(OnItemRecommendListener onItemRecommendListener) {
        this.onItemRecommendListener = onItemRecommendListener;
    }

    public HomeRecommendAdapter(Context mContext, List<HomeRecommendBean.RecommeListBean> mRecommendLists) {
        this.mContext = mContext;
        this.mRecommendLists = mRecommendLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_store_entrance_male, parent, false);
        return new RecommendViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecommendViewHolder recommendViewHolder = (RecommendViewHolder) holder;
        ViewGroup.LayoutParams layoutParams = recommendViewHolder.itemView.getLayoutParams();
        if (getItemCount() <= 5) {
            layoutParams.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px(mContext, 10) * 2) / getItemCount();
        } else {
            layoutParams.width = (DisplayUtil.getScreenWidth(mContext) - DisplayUtil.dip2px(mContext, 10) * 2) / 5;
        }
        recommendViewHolder.itemView.setLayoutParams(layoutParams);
        HomeRecommendBean.RecommeListBean recommeListBean = mRecommendLists.get(position);
        recommendViewHolder.mTxName.setText(recommeListBean.getTitle());
        AdInfo adInfo = BaseSdkAD.newAdInfo(recommeListBean);
        if (adInfo != null) {
            MyPicasso.glideSdkAd(mContext, adInfo, recommeListBean.getImg_icon(), recommendViewHolder.mIvIcon);
        } else {
            MyPicasso.GlideImageNoSize((Activity) mContext, recommeListBean.getImg_icon(), recommendViewHolder.mIvIcon);
        }
        recommendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemRecommendListener != null) {
                    if (adInfo != null) {
                        XRequestManager.INSTANCE.requestEventClick(mContext, adInfo);
                    }
                    onItemRecommendListener.onItemRecommendListener(recommeListBean);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mRecommendLists.size() > 10 ? 10 : mRecommendLists.size();
    }

    public class RecommendViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_store_entrance_male_img)
        ImageView mIvIcon;
        @BindView(R.id.item_store_entrance_male_text)
        TextView mTxName;

        public RecommendViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemRecommendListener {
        void onItemRecommendListener(HomeRecommendBean.RecommeListBean recommeListBean);
    }
}
