package com.heiheilianzai.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.ComplaitTypeBean;

public class ShareRecordAdapter extends BaseQuickAdapter<ComplaitTypeBean.ComplaitListBean, BaseViewHolder> {
    private Context mContent;

    public ShareRecordAdapter(Context context) {
        super(R.layout.item_share);
        this.mContent = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(BaseViewHolder helper, ComplaitTypeBean.ComplaitListBean item) {
        helper.setText(R.id.tv_type_name, item.getTitle());
    }
}