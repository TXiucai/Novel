package com.heiheilianzai.app.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.banner.holder.Holder;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.config.ReaderApplication;


/**
 * 轮播图Holder
 */
public class ComicShelfBannerHolderView implements Holder<BaseComic> {
    private ImageView item_BookShelfBannerHolderView_img;
    TextView item_BookShelfBannerHolderView_title;
    TextView item_BookShelfBannerHolderView_des;
    int size;

    @Override
    public View createView(Context context, int size) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookshelfbannerholderview, null);
        this.size = size;
        item_BookShelfBannerHolderView_img = view.findViewById(R.id.item_BookShelfBannerHolderView_img);
        item_BookShelfBannerHolderView_img.setImageResource(R.mipmap.comic_def_v);
        item_BookShelfBannerHolderView_title = view.findViewById(R.id.item_BookShelfBannerHolderView_title);
        item_BookShelfBannerHolderView_des = view.findViewById(R.id.item_BookShelfBannerHolderView_des);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, BaseComic data) {
        ImageLoader.getInstance().displayImage(data.getVertical_cover(), item_BookShelfBannerHolderView_img, ReaderApplication.getOptions());
        item_BookShelfBannerHolderView_title.setText(data.getName());
        item_BookShelfBannerHolderView_des.setText(data.getDescription());
    }
}
