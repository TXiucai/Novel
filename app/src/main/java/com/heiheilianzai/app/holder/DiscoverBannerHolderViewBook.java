package com.heiheilianzai.app.holder;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.utils.MyPicasso;

/**
 * 轮播图Holder
 */
public class DiscoverBannerHolderViewBook implements Holder<BannerItemStore> {
    private ImageView imageView;
    private Activity activity;

    public DiscoverBannerHolderViewBook(Activity activity) {
        this.activity = activity;
    }

    @Override
    public View createView(Context context, int size) {
        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        return imageView;
    }

    @Override
    public void UpdateUI(Context context,int position, BannerItemStore data) {
        if(activity!=null){
            MyPicasso.GlideImageNoSize(activity, data.getImage(), imageView);
        }
    }
}
