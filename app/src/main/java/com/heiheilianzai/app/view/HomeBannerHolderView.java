package com.heiheilianzai.app.view;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.heiheilianzai.app.banner.holder.Holder;
import com.heiheilianzai.app.bean.BannerItem;
import com.heiheilianzai.app.config.ReaderApplication;
import com.nostra13.universalimageloader.core.ImageLoader;


/**
 * 轮播图Holder
 */
public class HomeBannerHolderView implements Holder<BannerItem> {
    private ImageView imageView;

    @Override
    public View createView(Context context, int size) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context,int position, BannerItem data) {
        ImageLoader.getInstance().displayImage(data.getImage(), imageView, ReaderApplication.getOptions());
    }
}
