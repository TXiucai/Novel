package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.model.comic.ComicReadHistory;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

/**
 * 漫画阅读历史记录 Adapter
 * Created by abc on 2017/4/28.
 */
public class ReadHistoryRecyclerViewComicAdapter extends BaseReadHistoryAdapter<ComicReadHistory.ReadHistoryComic> {

    public ReadHistoryRecyclerViewComicAdapter(Activity activity, List<ComicReadHistory.ReadHistoryComic> list, GetPosition<ComicReadHistory.ReadHistoryComic> getPosition) {
        super(activity, list, getPosition);
    }

    @Override
    protected void onMyBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ComicReadHistory.ReadHistoryComic readHistoryBook = list.get(position);
        if (readHistoryBook.ad_type == 0) {
            setView(holder);
            holder.recyclerview_item_readhistory_name.setText(readHistoryBook.getName());
            holder.recyclerview_item_readhistory_des.setText(readHistoryBook.chapter_title);
            holder.recyclerview_item_readhistory_time.setText(readHistoryBook.getLast_chapter_time() + "  " + String.format(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_total_chapter), readHistoryBook.getTotal_chapters()));
            MyPicasso.GlideImageNoSize(activity, readHistoryBook.getVertical_cover(), holder.recyclerview_item_readhistory_img, R.mipmap.comic_def_v);
            holder.recyclerview_item_readhistory_book.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getPosition.getPosition(0, readHistoryBook, position);
                }
            });
            holder.recyclerview_item_readhistory_goon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPosition.getPosition(1, readHistoryBook, position);
                }
            });
            holder.recyclerview_item_readhistory_del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getPosition.getPosition(2, readHistoryBook, position);
                }
            });
        } else {
            setAdView(holder);
            MyPicasso.GlideImageNoSize(activity, readHistoryBook.ad_image, holder.list_ad_view_img);
            holder.list_ad_view_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, WebViewActivity.class);
                    intent.putExtra("url", readHistoryBook.ad_skip_url);
                    intent.putExtra("title", readHistoryBook.ad_title);
                    intent.putExtra("advert_id", readHistoryBook.advert_id);
                    activity.startActivity(intent);
                }
            });
        }
    }
}

