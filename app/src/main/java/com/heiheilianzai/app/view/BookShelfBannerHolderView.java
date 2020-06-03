package com.heiheilianzai.app.view;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.banner.holder.Holder;
import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.utils.MyPicasso;


/**
 * 书架小说轮播图Holder
 */
public class BookShelfBannerHolderView implements Holder<BaseBook> {
    private ImageView item_BookShelfBannerHolderView_img;
    TextView item_BookShelfBannerHolderView_title;
    TextView item_BookShelfBannerHolderView_des;
    int size;
    Activity mActivity;

    public BookShelfBannerHolderView(Activity mActivity) {
        this.mActivity = mActivity;
    }

    @Override
    public View createView(Context context, int size) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_bookshelfbannerholderview, null);
        this.size = size;
        item_BookShelfBannerHolderView_img = view.findViewById(R.id.item_BookShelfBannerHolderView_img);
        item_BookShelfBannerHolderView_img.setImageResource(R.mipmap.book_def_v);
        item_BookShelfBannerHolderView_title = view.findViewById(R.id.item_BookShelfBannerHolderView_title);
        item_BookShelfBannerHolderView_des = view.findViewById(R.id.item_BookShelfBannerHolderView_des);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, BaseBook data) {
        MyPicasso.GlideImageNoSize(mActivity, data.getCover(), item_BookShelfBannerHolderView_img,R.mipmap.book_def_v);
        item_BookShelfBannerHolderView_title.setText(data.getName());
        item_BookShelfBannerHolderView_des.setText(data.getDescription());
    }
}
