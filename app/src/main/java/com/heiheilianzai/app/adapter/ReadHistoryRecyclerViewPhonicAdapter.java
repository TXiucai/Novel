package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.PhonicReadHistory;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import androidx.annotation.NonNull;

/**
 * 有声阅读历史 Adapter
 */
public class ReadHistoryRecyclerViewPhonicAdapter extends BaseReadHistoryAdapter<PhonicReadHistory.PhonicInfo> {

    public ReadHistoryRecyclerViewPhonicAdapter(Activity activity, List<PhonicReadHistory.PhonicInfo> list, GetPosition<PhonicReadHistory.PhonicInfo> getPosition) {
        super(activity, list, getPosition);
    }

    @Override
    protected void onMyBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final PhonicReadHistory.PhonicInfo phonicInfo = list.get(position);
        setView(holder);
        holder.recyclerview_item_readhistory_name.setText(phonicInfo.getName());
        holder.recyclerview_item_readhistory_des.setText(phonicInfo.getRecord_title());
        holder.recyclerview_item_readhistory_time.setText(phonicInfo.getLast_chapter_time() + "  " + String.format(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_total_chapter), phonicInfo.getTotal_numbers()));
        MyPicasso.GlideImageNoSize(activity, phonicInfo.getImg(), holder.recyclerview_item_readhistory_img, R.mipmap.comic_def_v);
        holder.recyclerview_item_readhistory_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPosition.getPosition(0, phonicInfo, position);
            }
        });
        holder.recyclerview_item_readhistory_goon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPosition.getPosition(1, phonicInfo, position);
            }
        });
        holder.recyclerview_item_readhistory_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPosition.getPosition(2, phonicInfo, position);
            }
        });
    }
}