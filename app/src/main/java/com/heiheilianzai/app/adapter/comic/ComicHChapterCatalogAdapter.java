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
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画详情目录列表Adapter
 */
public class ComicHChapterCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<ComicChapter> comicChapterCatalogList;
    public int size;
    BaseComic baseComic;

    public ComicHChapterCatalogAdapter(BaseComic baseComic, Activity activity, List<ComicChapter> comicChapterCatalogList) {
        this.activity = activity;
        this.baseComic = baseComic;
        size = comicChapterCatalogList.size();
        this.comicChapterCatalogList = comicChapterCatalogList;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(activity).inflate(R.layout.item_comic_chapter_catalog_h, null);
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

                            int is_vip = comicChapterCatalog.getIs_vip();
                            if ( is_vip==1 && !App.isVip(activity)) {
                                DialogVip dialogVip = new DialogVip();
                                dialogVip.getDialogVipPop(activity, false);
                                return;
                            }
                            baseComic.setCurrent_display_order(comicChapterCatalog.getDisplay_order());
                            baseComic.saveIsexist(false);
                            activity.startActivity(ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(activity, R.string.refer_page_info_catalog)));
                    }
                }
            });
            holder.item_comicchaptercatalog_name.setText(comicChapterCatalog.chapter_title);
            holder.item_chapter_catalog_time.setText(comicChapterCatalog.updated_at);
            if (comicChapterCatalog.isRead()) {
                holder.item_comicchaptercatalog_current_bg.setBackgroundColor(activity.getResources().getColor(R.color.lightgray2));
            } else {
                holder.item_comicchaptercatalog_current_bg.setBackgroundColor(Color.WHITE);
            }
            if (comicChapterCatalog.getIs_vip() == 0) {//免费
                if (TextUtils.equals(comicChapterCatalog.getIs_book_coupon_pay(),"0")) {
                    MyPicasso.GlideImageNoSize(activity, comicChapterCatalog.small_cover, holder.item_comicchaptercatalog_img, R.mipmap.comic_def_cross);
                    holder.item_comic_chapter_lock.setVisibility(View.GONE);
                } else {
                    holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
                }
            } else {
                holder.item_comic_chapter_lock.setVisibility(View.VISIBLE);
            }
    }

    @Override
    public int getItemCount() {
        return comicChapterCatalogList.size();
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

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}