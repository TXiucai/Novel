package com.heiheilianzai.app.adapter.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画详情目录列表Adapter
 */
public class ComicHChapterCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<ComicChapter> comicChapterCatalogList;
    BaseComic baseComic;

    public ComicHChapterCatalogAdapter(BaseComic baseComic, Activity activity, List<ComicChapter> comicChapterCatalogList) {
        this.activity = activity;
        this.baseComic = baseComic;
        this.comicChapterCatalogList = comicChapterCatalogList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(activity).inflate(R.layout.item_comic_chapter_catalog_h, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        final ComicChapter comicChapterCatalog = comicChapterCatalogList.get(i);
        ViewHolder holder = (ViewHolder) viewHolder;
        holder.item_comicchaptercatalog_current_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comicChapterCatalog != null) {
                    baseComic.setCurrent_chapter_id(comicChapterCatalog.getChapter_id());
                    activity.startActivity(ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(activity, R.string.refer_page_info_catalog)));
                }
            }
        });
        holder.item_comicchaptercatalog_name.setText(comicChapterCatalog.chapter_title);
        holder.item_chapter_catalog_time.setText(DateUtils.getStringToDate(comicChapterCatalog.getUpdate_time()));
        if (comicChapterCatalog.isRead()) {
            holder.item_comicchaptercatalog_name.setTextColor(activity.getResources().getColor(R.color.color_9a9a9a));
        } else {
            holder.item_comicchaptercatalog_name.setTextColor(activity.getResources().getColor(R.color.color_1a1a1a));
        }
        MyPicasso.GlideImageRoundedCorners(6, activity, comicChapterCatalog.small_cover, holder.item_comicchaptercatalog_img, 110, 60, R.mipmap.comic_def_cross);
        if (!StringUtils.isEmpty(comicChapterCatalog.getIs_limited_free()) && TextUtils.equals(comicChapterCatalog.getIs_limited_free(), "1")) {
            holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
            holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_unlock));
            holder.item_comic_chapter_bg.setVisibility(View.GONE);
        } else {
            if (App.isVip(activity)) {
                if (comicChapterCatalog.getIs_vip() == 1 || TextUtils.equals(comicChapterCatalog.getIs_book_coupon_pay(), "1")) {
                    holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                    holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_unlock));
                    holder.item_comic_chapter_bg.setVisibility(View.GONE);
                } else {
                    holder.item_comic_chapter_lock.setVisibility(View.GONE);
                    holder.item_comic_chapter_bg.setVisibility(View.GONE);
                }

            } else {
                if (comicChapterCatalog.getIs_vip() == 0) {//免费
                    if (TextUtils.equals(comicChapterCatalog.getIs_book_coupon_pay(), "0")) {
                        holder.item_comic_chapter_lock.setVisibility(View.GONE);
                        holder.item_comic_chapter_bg.setVisibility(View.GONE);
                    } else {
                        if (comicChapterCatalog.isIs_buy_status()) {
                            holder.item_comic_chapter_bg.setVisibility(View.GONE);
                            holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_unlock));
                            holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                        } else {
                            holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_lock));
                            holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                            holder.item_comic_chapter_bg.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (TextUtils.equals(comicChapterCatalog.getIs_book_coupon_pay(), "1")) {
                        if (comicChapterCatalog.isIs_buy_status()) {
                            holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_unlock));
                            holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                            holder.item_comic_chapter_bg.setVisibility(View.GONE);
                        } else {
                            holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_lock));
                            holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                            holder.item_comic_chapter_bg.setVisibility(View.VISIBLE);
                        }
                    } else {
                        holder.item_comic_chapter_lock.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_lock));
                        holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                        holder.item_comic_chapter_bg.setVisibility(View.VISIBLE);
                    }
                }
            }
        }

    }

    @Override
    public int getItemCount() {
        return comicChapterCatalogList == null ? 0 : comicChapterCatalogList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_comicchaptercatalog_current_bg)
        public LinearLayout item_comicchaptercatalog_current_bg;
        @BindView(R.id.item_comicchaptercatalog_img)
        public ImageView item_comicchaptercatalog_img;
        @BindView(R.id.item_comicchaptercatalog_name)
        public TextView item_comicchaptercatalog_name;
        @BindView(R.id.item_chapter_catalog_time)
        public TextView item_chapter_catalog_time;
        @BindView(R.id.item_comicchaptercatalog_needbuy)
        public RelativeLayout item_comicchaptercatalog_needbuy;
        @BindView(R.id.item_comic_chapter_lock)
        public ImageView item_comic_chapter_lock;
        @BindView(R.id.item_comicchaptercatalog_bg)
        public ImageView item_comic_chapter_bg;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
