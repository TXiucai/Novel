package com.heiheilianzai.app.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyGlide;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

/**
 * 轮播图Holder
 */
public class HomeBannerHolderViewComic implements Holder<BannerItemStore> {
    private ImageView item_store_entrance_comic_bg, item_store_entrance_comic_img;
    View  item_store_entrance_comic_bgVIEW;
    Activity activity;
    int width,width2, height,flag;

    public HomeBannerHolderViewComic(int flag) {
         this.flag=flag;
    }

    @Override
    public View createView(Context context, int size) {
        activity = (Activity) context;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.item_store_entrance_comic, null, false);
        width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        width2=width- ImageUtil.dp2px(activity, 20);
        height = width2 * 3 / 5;
        item_store_entrance_comic_img = contentView.findViewById(R.id.item_store_entrance_comic_img);
        item_store_entrance_comic_bg = contentView.findViewById(R.id.item_store_entrance_comic_bg);
        item_store_entrance_comic_bgVIEW = contentView.findViewById(R.id.item_store_entrance_comic_bgVIEW);
        return contentView;
    }

    @Override
    public void UpdateUI(Context context, int position, BannerItemStore data) {//- ImageUtil.dp2px(activity, 20)
        int[] colors = {0xE6FFFFFF, Color.parseColor(data.color)};
        GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
        item_store_entrance_comic_bgVIEW.setBackground(g);
        if(flag==0){
            MyPicasso.GlideImageRoundedCorners(6,activity, data.getImage(), item_store_entrance_comic_img,width2  , height,R.mipmap.book_def_cross);
            MyGlide.GlideImagePalette(activity, data.getImage(), item_store_entrance_comic_bg,width, ImageUtil.dp2px(activity, 310),R.mipmap.book_def_cross);
        }else {
            MyPicasso.GlideImageRoundedCorners(6,activity, data.getImage(), item_store_entrance_comic_img,width2  , height,R.mipmap.comic_def_cross);
            MyGlide.GlideImagePalette(activity, data.getImage(), item_store_entrance_comic_bg,width, ImageUtil.dp2px(activity, 310),R.mipmap.comic_def_cross);
        }
    }
}