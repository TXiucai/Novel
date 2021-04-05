package com.heiheilianzai.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.ComplaitTypeBean;

public class FeedBackTypeAdapter extends BaseQuickAdapter<ComplaitTypeBean.ComplaitListBean, BaseViewHolder> {
    private Context mContent;
    private int mCurrentPosition = -1;

    public FeedBackTypeAdapter(Context context) {
        super(R.layout.item_feedback_type);
        this.mContent = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(BaseViewHolder helper, ComplaitTypeBean.ComplaitListBean item) {
        helper.setText(R.id.tv_type_name, item.getTitle());

        if (mCurrentPosition == helper.getAdapterPosition()) {
            helper.setBackgroundRes(R.id.tv_type_name, R.drawable.shape_bottom_feedback);
            helper.setTextColor(R.id.tv_type_name, Color.parseColor("#ffffff"));
        } else {
            helper.setBackgroundRes(R.id.tv_type_name, R.drawable.shape_bottom_feedback_white);
            helper.setTextColor(R.id.tv_type_name, Color.parseColor("#606060"));
        }
    }

    public void setCurrentPosition(int position) {
        this.mCurrentPosition = position;
        notifyDataSetChanged();
    }
}