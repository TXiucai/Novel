package com.heiheilianzai.app.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.holder.Holder;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

public class MineAdBannerHolderView implements Holder<BaseAd> {

    private ImageView mImgAD;
    Activity mActivity;

    public MineAdBannerHolderView(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public View createView(Context context, int size) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_ad_banner_holderview, null);
        mImgAD = view.findViewById(R.id.img_ad);
        ViewGroup.LayoutParams layoutParams = mImgAD.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(mActivity).getScreenWidth() - ImageUtil.dp2px(mActivity, 30);
        layoutParams.height = layoutParams.width / 4;
        mImgAD.setLayoutParams(layoutParams);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, BaseAd data) {
        MyPicasso.GlideImageNoSize(mActivity, data.getAd_image(), mImgAD);
    }
}
