package com.heiheilianzai.app.adapter.cartoon;

import android.app.Activity;
import android.content.Intent;
import android.view.View;

import androidx.annotation.NonNull;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.cartoon.CartoonChapter;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

public class ReadHistoryRecycleViewCartoonAdapter extends BaseReadHistoryAdapter<CartoonChapter> {
    public ReadHistoryRecycleViewCartoonAdapter(Activity activity, List<CartoonChapter> list, GetPosition<CartoonChapter> getPosition) {
        super(activity, list, getPosition);
    }

    @Override
    protected void onMyBindViewHolder(@NonNull MyViewHolder holder, int position) {
        CartoonChapter cartoonChapter = list.get(position);
        if (cartoonChapter.getAd_type() == 0) {
            setView(holder);
            holder.recyclerview_item_readhistory_name.setText(cartoonChapter.getName());
            holder.recyclerview_item_readhistory_des.setText(cartoonChapter.getChapter_title());
            holder.recyclerview_item_readhistory_time.setText(String.format(LanguageUtil.getString(activity, R.string.history_cartoon_play), DateUtils.secToTime(cartoonChapter.getPlay_node())));
            MyPicasso.GlideImageNoSize(activity, cartoonChapter.getCover(), holder.recyclerview_item_readhistory_img, R.mipmap.cartoon_def_v);
            if (cartoonChapter.getTag() != null && !cartoonChapter.getTag().isEmpty()) {
                String str = "";
                for (BaseTag tag : cartoonChapter.getTag()) {
                    str += "#" + tag.tab + " ";
                }
                holder.mTxTag.setText(str);
            }
            setIsEditView(holder, mIsEditOpen);
            setIsSelectAllView(holder, mIsSelectAll);
            holder.recyclerview_item_readhistory_goon.setText(activity.getString(R.string.string_continue_play));
            holder.mRlCheckBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    holder.mCheckBox.setChecked(!holder.mCheckBox.isChecked());
                    if (holder.mCheckBox.isChecked()) {
                        mSelectList.add(cartoonChapter);
                    } else {
                        mSelectList.remove(cartoonChapter);
                    }
                    mGetSelectItems.getSelectItems(mSelectList);
                }
            });
            holder.recyclerview_item_readhistory_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPosition.getPosition(0, cartoonChapter, position);
                }
            });
            holder.recyclerview_item_readhistory_goon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPosition.getPosition(1, cartoonChapter, position);
                }
            });
            holder.recyclerview_item_readhistory_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPosition.getPosition(2, cartoonChapter, position);
                }
            });
        } else {
            setAdView(holder);
            MyPicasso.GlideImageNoSize(activity, cartoonChapter.ad_image, holder.list_ad_view_img);
            holder.list_ad_view_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    BaseAd.jumpADInfo(cartoonChapter, activity);
                }
            });
        }
    }

    @Override
    public void setmIsEditOpen(boolean mIsEditOpen) {
        this.mIsEditOpen = mIsEditOpen;
        notifyDataSetChanged();
    }

    @Override
    public void setmSelectAll(boolean mIsSelectAll) {
        this.mIsSelectAll = mIsSelectAll;
        notifyDataSetChanged();
    }
}
