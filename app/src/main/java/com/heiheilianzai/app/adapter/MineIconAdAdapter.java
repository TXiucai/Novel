package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.utils.MyPicasso;

public class MineIconAdAdapter extends BaseQuickAdapter<BaseAd, BaseViewHolder> {
    private Activity mContent;

    public MineIconAdAdapter(Activity activity) {
        super(R.layout.item_mine_icon_ad);
        this.mContent = activity;

    }

    @Override
    public int getItemCount() {
        return super.getItemCount() > 10 ? 10 : super.getItemCount();
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, BaseAd item) {
        if (item.getAd_image() != null) {
            MyPicasso.GlideImageNoSize(mContent, item.getAd_image(), helper.getView(R.id.img_ad));
        }
        if (item.getAd_title() != null) {
            helper.setText(R.id.tx_ad, item.getAd_title());
        }

        if (item.getAd_subtitle() != null) {
            helper.setText(R.id.tx_ad_sub, item.getAd_subtitle());
        }
    }
}
