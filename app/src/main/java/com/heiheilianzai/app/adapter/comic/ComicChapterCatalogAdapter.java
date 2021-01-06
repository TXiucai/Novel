package com.heiheilianzai.app.adapter.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
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
import com.heiheilianzai.app.model.comic.BaseComicImage;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.activity.CatalogActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画详情目录列表Adapter
 */
public class ComicChapterCatalogAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    public List<ComicChapter> comicChapterCatalogList;
    public int size;
    LayoutInflater layoutInflater;
    int width, height, H96;
    private String currentChapterId;
    private int CurrentPosition;
    BaseComic baseComic;
    boolean shunxu;
    boolean flag;
    private int ad = 1;

    public ComicChapterCatalogAdapter(boolean flag, BaseComic baseComic, Activity activity, String currentChapterId, List<ComicChapter> comicChapterCatalogList, int H96) {
        this.activity = activity;
        this.flag = flag;
        this.baseComic = baseComic;
        size = comicChapterCatalogList.size();
        layoutInflater = LayoutInflater.from(activity);
        this.comicChapterCatalogList = comicChapterCatalogList;
        width = ImageUtil.dp2px(activity, 133);
        height = ImageUtil.dp2px(activity, 80);
        this.H96 = H96;
        if (currentChapterId != null) {
            this.currentChapterId = currentChapterId;
        } else {
            if (size > 0) {
                this.currentChapterId = comicChapterCatalogList.get(0).chapter_id;
            }
        }

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == ad) {
            View rootView = LayoutInflater.from(activity).inflate(R.layout.item_comic_cotegory_ad, null, false);
            return new MyAdViewHolder(rootView);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_comicchaptercatalog, null);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        final ComicChapter comicChapterCatalog = comicChapterCatalogList.get(i);
        if (viewHolder instanceof ViewHolder) {
            ViewHolder holder = (ViewHolder) viewHolder;
            holder.item_comicchaptercatalog_current_bg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (comicChapterCatalog != null) {
                        if (flag) {
                            int is_vip = comicChapterCatalog.getIs_vip();
                            if ( is_vip==1 && !App.isVip(activity)) {
                                DialogVip dialogVip = new DialogVip();
                                dialogVip.getDialogVipPop(activity, false);
                                return;
                            }
                            baseComic.setCurrent_display_order(comicChapterCatalog.getDisplay_order());
                            baseComic.saveIsexist(false);
                            activity.startActivity(ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(activity, R.string.refer_page_info_catalog)));
                        } else {
                            Intent intent = new Intent();
                            intent.putExtra("currentChapter_id", comicChapterCatalog.chapter_id);
                            activity.setResult(222, intent);
                            activity.finish();
                        }
                    }
                }
            });
            ViewGroup.LayoutParams layoutInflater = holder.item_comicchaptercatalog_img.getLayoutParams();
            layoutInflater.width = width;
            layoutInflater.height = height;
            holder.item_comicchaptercatalog_img.setLayoutParams(layoutInflater);
            MyPicasso.GlideImageNoSize(activity, comicChapterCatalog.small_cover, holder.item_comicchaptercatalog_img, R.mipmap.comic_def_cross);
            holder.item_comicchaptercatalog_time.setText(comicChapterCatalog.subtitle);
            holder.item_comicchaptercatalog_name.setText(comicChapterCatalog.chapter_title);
            if (comicChapterCatalog.is_preview == 0) {
                holder.item_comicchaptercatalog_needbuy.setVisibility(View.GONE);
            } else {
                holder.item_comicchaptercatalog_needbuy.setVisibility(View.VISIBLE);
            }
            if (comicChapterCatalog.isRead()) {
                holder.item_comicchaptercatalog_current_bg.setBackgroundColor(activity.getResources().getColor(R.color.lightgray2));
            } else {
                holder.item_comicchaptercatalog_current_bg.setBackgroundColor(Color.WHITE);

            }
            if (comicChapterCatalog.chapter_id.equals(currentChapterId)) {
                CurrentPosition = i;
                holder.item_comicchaptercatalog_current.setVisibility(View.VISIBLE);
            } else {
                holder.item_comicchaptercatalog_current.setVisibility(View.GONE);
            }
            if (comicChapterCatalog.getIs_vip() == 0) {//免费
                holder.item_comicchaptercatalog_vip.setBackgroundResource(R.mipmap.category_free);
            } else {
                holder.item_comicchaptercatalog_vip.setBackgroundResource(R.mipmap.category_vip);
            }
        } else if (viewHolder instanceof MyAdViewHolder) {
            MyAdViewHolder myAdViewHolder = (MyAdViewHolder) viewHolder;
            MyPicasso.GlideImageNoSize(activity, comicChapterCatalog.getAd_image(), myAdViewHolder.ivAD);
            myAdViewHolder.ivAD.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, WebViewActivity.class);
                    intent.putExtra("url", comicChapterCatalog.getAd_skip_url());
                    intent.putExtra("ad_url_type", comicChapterCatalog.getAd_url_type());
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return comicChapterCatalogList.size();
    }

    public int getCurrentPosition() {
        return CurrentPosition;
    }

    @Override
    public int getItemViewType(int position) {
        ComicChapter comicChapter = comicChapterCatalogList.get(position);
        String comic_id = comicChapter.getAd_image();
        if (comic_id != null) {
            return ad;
        } else {
            return super.getItemViewType(position);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_comicchaptercatalog_current_bg)
        public LinearLayout item_comicchaptercatalog_current_bg;
        @BindView(R.id.item_comicchaptercatalog_img)
        public ImageView item_comicchaptercatalog_img;
        @BindView(R.id.item_comicchaptercatalog_current)
        public ImageView item_comicchaptercatalog_current;
        @BindView(R.id.item_comicchaptercatalog_name)
        public TextView item_comicchaptercatalog_name;
        @BindView(R.id.item_comicchaptercatalog_time)
        public TextView item_comicchaptercatalog_time;
        @BindView(R.id.item_chapter_catalog_vip)
        public TextView item_comicchaptercatalog_vip;
        @BindView(R.id.item_comicchaptercatalog_needbuy)
        public RelativeLayout item_comicchaptercatalog_needbuy;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    class MyAdViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_comic_ad)
        ImageView ivAD;

        public MyAdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public void setShunxu(boolean shunxu) {
        this.shunxu = shunxu;
    }

    public void setCurrentChapterId(String currentChapterId) {
        this.currentChapterId = currentChapterId;
    }
}
