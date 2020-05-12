package com.heiheilianzai.app.book.adapter;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.heiheilianzai.app.banner.holder.Holder;
import com.heiheilianzai.app.bean.BannerItem;
import com.heiheilianzai.app.bean.BannerItemStore;
import com.heiheilianzai.app.config.ReaderApplication;


/**
 * 轮播图Holder
 */
public class DiscoverBannerHolderViewBook implements Holder<BannerItemStore> {
    private ImageView imageView;

    @Override
    public View createView(Context context, int size) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context,int position, BannerItemStore data) {
        ImageLoader.getInstance().displayImage(data.getImage(), imageView, ReaderApplication.getOptions());
//        ImageLoader.getInstance().displayImage("https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1527943969400&di=7bd62bbf863c30d260808e64cc0776a1&imgtype=0&src=http%3A%2F%2Fwww.taopic.com%2Fuploads%2Fallimg%2F140403%2F240438-1404030P31712.jpg", imageView, ReaderApplication.getOptions());
    }
}
