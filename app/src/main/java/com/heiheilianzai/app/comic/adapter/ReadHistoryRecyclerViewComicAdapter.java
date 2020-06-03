package com.heiheilianzai.app.comic.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.comic.been.ComicReadHistory;
import com.heiheilianzai.app.comic.fragment.ReadHistoryComicFragment;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画阅读历史记录
 * Created by abc on 2017/4/28.
 */
public class ReadHistoryRecyclerViewComicAdapter extends RecyclerView.Adapter<ReadHistoryRecyclerViewComicAdapter.MyViewHolder> {
    private List<ComicReadHistory.ReadHistoryComic> list;
    private ReadHistoryComicFragment.GetPosition getPosition;
    private Activity activity;

    public ReadHistoryRecyclerViewComicAdapter(Activity activity, List<ComicReadHistory.ReadHistoryComic> list, ReadHistoryComicFragment.GetPosition getPosition) {
        this.list = list;
        this.activity = activity;
        this.getPosition = getPosition;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item_readhistory, parent, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        final ComicReadHistory.ReadHistoryComic readHistoryBook = list.get(position);
        if (readHistoryBook.ad_type == 0) {
            holder.recyclerview_item_readhistory_HorizontalScrollView.scrollTo(0, 0);
            holder.recyclerview_item_readhistory_HorizontalScrollView.setVisibility(View.VISIBLE);
            holder.list_ad_view_layout.setVisibility(View.GONE);
            holder.recyclerview_item_readhistory_name.setText(readHistoryBook.getName());
            holder.recyclerview_item_readhistory_des.setText(readHistoryBook.chapter_title);
            holder.recyclerview_item_readhistory_time.setText(readHistoryBook.getLast_chapter_time() + "  " + String.format(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_total_chapter), readHistoryBook.getTotal_chapters()));
            holder.recyclerview_item_readhistory_img.setImageResource(R.mipmap.comic_def_v);
            MyPicasso.GlideImageNoSize(activity,readHistoryBook.getVertical_cover(), holder.recyclerview_item_readhistory_img,R.mipmap.comic_def_v);
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
            holder.recyclerview_item_readhistory_HorizontalScrollView.setVisibility(View.GONE);
            holder.list_ad_view_layout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = holder.list_ad_view_img.getLayoutParams();
            layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
            layoutParams.height = layoutParams.width / 3;
            holder.list_ad_view_img.setLayoutParams(layoutParams);
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

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.recyclerview_item_readhistory_img)
        ImageView recyclerview_item_readhistory_img;
        @BindView(R2.id.recyclerview_item_readhistory_name)
        TextView recyclerview_item_readhistory_name;
        @BindView(R2.id.recyclerview_item_readhistory_des)
        TextView recyclerview_item_readhistory_des;
        @BindView(R2.id.recyclerview_item_readhistory_time)
        TextView recyclerview_item_readhistory_time;
        @BindView(R2.id.recyclerview_item_readhistory_goon)
        Button recyclerview_item_readhistory_goon;
        @BindView(R2.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R2.id.list_ad_view_img)
        ImageView list_ad_view_img;
        @BindView(R2.id.recyclerview_item_readhistory_book)
        LinearLayout recyclerview_item_readhistory_book;
        @BindView(R2.id.recyclerview_item_readhistory_del)
        TextView recyclerview_item_readhistory_del;
        @BindView(R2.id.recyclerview_item_readhistory_HorizontalScrollView)
        HorizontalScrollView recyclerview_item_readhistory_HorizontalScrollView;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerview_item_readhistory_book.getLayoutParams();
            layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
            recyclerview_item_readhistory_book.setLayoutParams(layoutParams);
        }
    }
}

