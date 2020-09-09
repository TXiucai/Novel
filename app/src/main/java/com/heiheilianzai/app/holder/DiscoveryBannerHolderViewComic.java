package com.heiheilianzai.app.holder;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.utils.MyPicasso;


/**
 * 轮播图Holder
 */
public class DiscoveryBannerHolderViewComic implements Holder<BannerItemStore> {
    Activity activity;
    int width, height;
    private ImageView banner_discovery_new_img;

    public DiscoveryBannerHolderViewComic(Activity activity, int width) {
        this.activity = activity;
        this.width = width;
        this.height = width/3;
    }

    public DiscoveryBannerHolderViewComic() {
    }

    @Override
    public View createView(Context context, int size) {
        View contentView = LayoutInflater.from(activity).inflate(R.layout.banner_discovery_new, null, false);

        banner_discovery_new_img = contentView.findViewById(R.id.banner_discovery_new_img);

      /*  RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) banner_discovery_new_img.getLayoutParams();
        layoutParams.height = height;
        banner_discovery_new_img.setLayoutParams(layoutParams);*/
        return contentView;
    }


    @Override
    public void UpdateUI(Context context, int position, BannerItemStore data) {//- ImageUtil.dp2px(activity, 20)
        MyPicasso.GlideImageRoundedCorners(8,activity, data.getImage(),banner_discovery_new_img,width  , height);
    }
}
