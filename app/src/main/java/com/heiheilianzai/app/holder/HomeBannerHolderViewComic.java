package com.heiheilianzai.app.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyGlide;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.mobi.xad.bean.AdInfo;

/**
 * 轮播图Holder
 */
public class HomeBannerHolderViewComic implements Holder<BannerItemStore> {
    private ImageView item_store_entrance_comic_bg, item_store_entrance_comic_img;
    View item_store_entrance_comic_bgVIEW;
    Activity activity;
    int width, width2, height, flag;

    public HomeBannerHolderViewComic(int flag) {
        this.flag = flag;
    }

    @Override
    public View createView(Context context, int size) {
        activity = (Activity) context;
        View contentView = LayoutInflater.from(activity).inflate(R.layout.item_store_entrance_comic, null, false);
        width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        width2 = width - ImageUtil.dp2px(activity, 20);
        height = width * 3 / 5;
        item_store_entrance_comic_img = contentView.findViewById(R.id.item_store_entrance_comic_img);
        item_store_entrance_comic_bg = contentView.findViewById(R.id.item_store_entrance_comic_bg);
        item_store_entrance_comic_bgVIEW = contentView.findViewById(R.id.item_store_entrance_comic_bgVIEW);
        ViewGroup.LayoutParams layoutParams = item_store_entrance_comic_bg.getLayoutParams();
        layoutParams.height = height;
        layoutParams.width = width;
        item_store_entrance_comic_bg.setLayoutParams(layoutParams);
        return contentView;
    }

    @Override
    public void UpdateUI(Context context, int position, BannerItemStore data) {//- ImageUtil.dp2px(activity, 20)
        //取消背景颜色
        // int[] colors = {0xE6FFFFFF, Color.parseColor(data.color)};
        //GradientDrawable g = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
        //item_store_entrance_comic_bgVIEW.setBackground(g);
        if (flag == 0) {
            AdInfo adInfo = BaseSdkAD.newAdInfo(data);
            if (adInfo != null) {
                MyPicasso.glideSdkAd(activity, adInfo, data.getImage(), item_store_entrance_comic_img, width2, height, R.mipmap.book_def_cross);
            } else {
                MyPicasso.GlideImage(activity, data.getImage(), item_store_entrance_comic_img, width2, height, R.mipmap.book_def_cross);
            }
            MyGlide.GlideImagePalette(activity, data.getImage(), item_store_entrance_comic_bg, width, height, R.mipmap.book_def_cross);
        } else {
            MyPicasso.GlideImage(activity, data.getImage(), item_store_entrance_comic_img, width2, height, R.mipmap.comic_def_cross);
            MyGlide.GlideImagePalette(activity, data.getImage(), item_store_entrance_comic_bg, width, height, R.mipmap.comic_def_cross);
        }
    }
}