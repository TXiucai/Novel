package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.SearchItem;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.List;

/**
 * 搜索页下方的图书的adapter
 */
public class SearchVerticalAdapter extends ReaderBaseAdapter<SearchItem> {
    public int WIDTH, HEIGHT, H20, H33, CARTOON_WIDTH, CARTOON_HEIGHT, H10;
    int PRODUCT;
    Activity mActivity;

    public SearchVerticalAdapter(Activity activity, List<SearchItem> list, int PRODUCT) {
        super(activity, list, list.size());
        mActivity = activity;
        int screenWidth = ScreenSizeUtils.getInstance(mContext).getScreenWidth();
        H20 = ImageUtil.dp2px(mContext, 40);
        H10 = ImageUtil.dp2px(mContext, 20);
        H33 = ImageUtil.dp2px(mContext, 28);
        WIDTH = (screenWidth - H20) / 3;//横向排版 图片宽度
        CARTOON_WIDTH = (screenWidth - H10) / 2;
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        CARTOON_HEIGHT = CARTOON_WIDTH * 2 / 3;
        this.PRODUCT = PRODUCT;
    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        if (PRODUCT != 3) {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_store_label_male_vertical, null, false);
            int defaultImgr = R.mipmap.book_def_v;
            if (PRODUCT == 1) {
                defaultImgr = R.mipmap.book_def_v;
            } else if (PRODUCT == 2) {
                defaultImgr = R.mipmap.comic_def_v;
            }
            ImageView imageView = contentView.findViewById(R.id.item_store_label_male_vertical_img);
            TextView name = contentView.findViewById(R.id.item_store_label_male_vertical_text);
            LinearLayout item_store_label_male_vertical_layout = contentView.findViewById(R.id.item_store_label_male_vertical_layout);
            RelativeLayout.LayoutParams layoutParamss = (RelativeLayout.LayoutParams) item_store_label_male_vertical_layout.getLayoutParams();
            layoutParamss.width = WIDTH;
            layoutParamss.height = HEIGHT + H33;
            item_store_label_male_vertical_layout.setLayoutParams(layoutParamss);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = WIDTH;
            layoutParams.height = HEIGHT;
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyPicasso.GlideImageNoSize(mActivity, mList.get(position).getCover(), imageView, defaultImgr);
            name.setText(mList.get(position).getName());
            return contentView;
        } else {
            View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_store_cartoon_style, null, false);
            ImageView imageView = contentView.findViewById(R.id.liem_store_comic_style1_img);
            TextView name = contentView.findViewById(R.id.liem_store_comic_style1_name);
            LinearLayout item_store_label_male_vertical_layout = contentView.findViewById(R.id.liem_store_comic_style1_layout);
            RelativeLayout.LayoutParams layoutParamss = (RelativeLayout.LayoutParams) item_store_label_male_vertical_layout.getLayoutParams();
            layoutParamss.width = CARTOON_WIDTH;
            layoutParamss.height = CARTOON_HEIGHT + H20;
            item_store_label_male_vertical_layout.setLayoutParams(layoutParamss);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
            layoutParams.width = CARTOON_WIDTH;
            layoutParams.height = CARTOON_HEIGHT;
            imageView.setLayoutParams(layoutParams);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyPicasso.GlideImageNoSize(mActivity, mList.get(position).getCover(), imageView, R.mipmap.cartoon_def_v);
            name.setText(mList.get(position).getName());
            return contentView;
        }
    }
}
