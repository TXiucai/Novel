package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

public class CommentVipAdapter extends BaseAdapter {
    Activity mActivity;
    List<OptionBeen> mList;
    LayoutInflater mLayoutInflater;
    private int WIDTH, HEIGHT;

    public CommentVipAdapter(Activity mActivity, List<OptionBeen> mList, int WIDTH, int HEIGHT) {
        this.mActivity = mActivity;
        this.mList = mList;
        this.mLayoutInflater = LayoutInflater.from(mActivity);
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public OptionBeen getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View contentView = null;
        OptionBeen optionBeen = mList.get(position);
        contentView = mLayoutInflater.inflate(R.layout.item_store_label_male_vertical, null, false);
        ImageView imageView = contentView.findViewById(R.id.item_store_label_male_vertical_img);
        LinearLayout item_store_label_male_vertical_layout = contentView.findViewById(R.id.item_store_label_male_vertical_layout);
        TextView name = contentView.findViewById(R.id.item_store_label_male_vertical_text2);
        TextView flag = contentView.findViewById(R.id.item_store_label_male_vertical_text);
        flag.setVisibility(View.GONE);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) item_store_label_male_vertical_layout.getLayoutParams();
        layoutParams.height = HEIGHT + ImageUtil.dp2px(mActivity, 35);
        item_store_label_male_vertical_layout.setPadding(ImageUtil.dp2px(mActivity, 5), ImageUtil.dp2px(mActivity, 4), ImageUtil.dp2px(mActivity, 5), ImageUtil.dp2px(mActivity, 4));
        item_store_label_male_vertical_layout.setBackground(null);
        item_store_label_male_vertical_layout.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParamsIm = (LinearLayout.LayoutParams) imageView.getLayoutParams();
        layoutParamsIm.height = HEIGHT;
        layoutParamsIm.width = WIDTH;
        imageView.setLayoutParams(layoutParamsIm);
        String book_id = optionBeen.book_id;
        if (book_id != null) {
            MyPicasso.GlideImageNoSize(mActivity, optionBeen.getCover(), imageView, R.mipmap.book_def_v);
        } else {
            MyPicasso.GlideImageNoSize(mActivity, optionBeen.getCover(), imageView, R.mipmap.comic_def_v);
        }
        name.setTextColor(mActivity.getResources().getColor(R.color.color_323334));
        name.setPadding(0, ImageUtil.dp2px(mActivity, 4), 0, 0);
        name.setVisibility(View.VISIBLE);
        name.setText(optionBeen.getName());
        return contentView;
    }
}
