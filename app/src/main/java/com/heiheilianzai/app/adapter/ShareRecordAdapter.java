package com.heiheilianzai.app.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.ShareRecordBean;
import com.heiheilianzai.app.utils.MyPicasso;

public class ShareRecordAdapter extends BaseQuickAdapter<ShareRecordBean.ShareRecordList, BaseViewHolder> {
    private Context mContent;

    public ShareRecordAdapter(Context context) {
        super(R.layout.item_share);
        this.mContent = context;
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void convert(BaseViewHolder helper, ShareRecordBean.ShareRecordList item) {
        if (item.getHeadPic() != null && !TextUtils.isEmpty(item.getHeadPic())) {
            MyPicasso.GlideImageNoSize((Activity) mContent, item.getHeadPic(), helper.getView(R.id.user_header));
        }

        if (item.getNickname() != null && !TextUtils.isEmpty(item.getNickname())) {
            helper.setText(R.id.tv_userName, item.getNickname());
        }

        if (item.getUid() != null && !TextUtils.isEmpty(item.getUid())) {
            helper.setText(R.id.tv_id, item.getUid());
        }

        if (item.getCreatedAt() != null && !TextUtils.isEmpty(item.getCreatedAt())) {
            helper.setText(R.id.tv_time, item.getCreatedAt());
        }

    }
}