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
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画详情目录列表Adapter
 */
public class ComicChapterCatalogAdapter extends RecyclerView.Adapter<ComicChapterCatalogAdapter.ViewHolder> {
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(activity).inflate(R.layout.item_comicchaptercatalog, null);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ComicChapterCatalogAdapter.ViewHolder viewHolder, @SuppressLint("RecyclerView") int i) {
        final ComicChapter comicChapterCatalog = comicChapterCatalogList.get(i);
        viewHolder.item_comicchaptercatalog_current_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (comicChapterCatalog != null) {
                    if (flag) {
                        baseComic.setCurrent_display_order(comicChapterCatalog.getDisplay_order());
                        baseComic.saveIsexist(false);
                        Intent intent = new Intent(activity, ComicLookActivity.class);
                        intent.putExtra("baseComic", baseComic);
                        activity.startActivity(intent);
                    } else {
                        Intent intent = new Intent();
                        intent.putExtra("currentChapter_id", comicChapterCatalog.chapter_id);
                        activity.setResult(222, intent);
                        activity.finish();
                    }
                }
            }
        });
        ViewGroup.LayoutParams layoutInflater = viewHolder.item_comicchaptercatalog_img.getLayoutParams();
        layoutInflater.width = width;
        layoutInflater.height = height;
        viewHolder.item_comicchaptercatalog_img.setLayoutParams(layoutInflater);
        MyPicasso.GlideImageNoSize(activity, comicChapterCatalog.small_cover, viewHolder.item_comicchaptercatalog_img, R.mipmap.comic_def_cross);
        viewHolder.item_comicchaptercatalog_time.setText(comicChapterCatalog.subtitle);
        viewHolder.item_comicchaptercatalog_name.setText(comicChapterCatalog.chapter_title);
        if (comicChapterCatalog.is_preview == 0) {
            viewHolder.item_comicchaptercatalog_needbuy.setVisibility(View.GONE);
        } else {
            viewHolder.item_comicchaptercatalog_needbuy.setVisibility(View.VISIBLE);
        }
        if (comicChapterCatalog.isRead()) {
            viewHolder.item_comicchaptercatalog_current_bg.setBackgroundColor(activity.getResources().getColor(R.color.lightgray2));
        } else {
            viewHolder.item_comicchaptercatalog_current_bg.setBackgroundColor(Color.WHITE);

        }
        if (comicChapterCatalog.chapter_id.equals(currentChapterId)) {
            CurrentPosition = i;
            viewHolder.item_comicchaptercatalog_current.setVisibility(View.VISIBLE);
        } else {
            viewHolder.item_comicchaptercatalog_current.setVisibility(View.GONE);
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
        @BindView(R.id.item_comicchaptercatalog_needbuy)
        public RelativeLayout item_comicchaptercatalog_needbuy;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public void setShunxu(boolean shunxu) {
        this.shunxu = shunxu;
    }

    public void setCurrentChapterId(String currentChapterId) {
        this.currentChapterId = currentChapterId;
    }
}
