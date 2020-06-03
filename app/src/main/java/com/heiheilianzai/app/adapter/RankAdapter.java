package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.bean.RankItem;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

/**
 * 书城banner下方的gridview的adapter
 */
public class RankAdapter extends ReaderBaseAdapter<RankItem> {
    boolean PRODUCT;
    Activity mActivity;

    public RankAdapter(Activity activity, List<RankItem> list, int count) {
        super(activity, list, count);
    }

    public RankAdapter(Activity activity, List<RankItem> list, int count, boolean PRODUCT) {
        super(activity, list, count);
        mActivity = activity;
        this.PRODUCT = PRODUCT;
    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        int defaultImgr = R.mipmap.book_def_v;
        if (PRODUCT) {
            defaultImgr = R.mipmap.book_def_v;
        } else {
            defaultImgr = R.mipmap.comic_def_v;
        }
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_store_rank_male, null, false);
        ImageView img1 = contentView.findViewById(R.id.item_store_rank_male_img_1);
        img1.setImageResource(defaultImgr);
        ImageView img2 = contentView.findViewById(R.id.item_store_rank_male_img_2);
        img2.setImageResource(defaultImgr);
        ImageView img3 = contentView.findViewById(R.id.item_store_rank_male_img_3);
        img3.setImageResource(defaultImgr);
        TextView name = contentView.findViewById(R.id.item_store_rank_male_text);
        TextView description = contentView.findViewById(R.id.item_store_rank_male_description);
        if (mList.get(position).getIcon().size() > 0) {
            MyPicasso.GlideImageNoSize(mActivity, mList.get(position).getIcon().get(0), img1, defaultImgr);
        }
        if (mList.get(position).getIcon().size() > 1) {
            MyPicasso.GlideImageNoSize(mActivity, mList.get(position).getIcon().get(1), img2, defaultImgr);
        }
        if (mList.get(position).getIcon().size() > 2) {
            MyPicasso.GlideImageNoSize(mActivity, mList.get(position).getIcon().get(2), img3, defaultImgr);
        }
        name.setText(mList.get(position).getList_name());
        description.setText(mList.get(position).getDescription());
        return contentView;
    }
}
