package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

/**
 * 作品章节目录的adapter
 */
public class ChapterAdapter extends ReaderBaseAdapter<ChapterItem> {
    public String current_chapter_id;//选中的item章节id
    public int mDisplayOrder;//选中item章节的位置
    public String coupon_pay_price;

    public void setCoupon_pay_price(String coupon_pay_price) {
        this.coupon_pay_price = coupon_pay_price;
    }

    public ChapterAdapter(Context context, List<ChapterItem> list, int count) {
        super(context, list, count);
    }

    public ChapterAdapter(Context context, List<ChapterItem> list, int count, boolean close) {
        super(context, list, count, close);
    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_chapter_catalog, null, false);
            viewHolder.title = convertView.findViewById(R.id.item_chapter_catalog_title);
            viewHolder.vip = convertView.findViewById(R.id.item_chapter_catalog_vip);
            viewHolder.coupon=convertView.findViewById(R.id.item_chapter_coupon);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChapterItem chapterItem = (mList.get(position));//chapterItem.getChapter_id().equals(current_chapter_id)
        if (chapterItem.getChapter_id().equals(current_chapter_id)) {
            viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.mainColor));
        } else {
            viewHolder.title.setTextColor(Color.BLACK);
        }
        viewHolder.title.setText(chapterItem.getChapter_title());
        if (!StringUtils.isEmpty(chapterItem.getIs_book_coupon_pay()) && TextUtils.equals(chapterItem.getIs_book_coupon_pay(),"1")) {//免费
            viewHolder.coupon.setVisibility(View.VISIBLE);
            viewHolder.coupon.setText(coupon_pay_price+mContext.getResources().getString(R.string.coupon_open));
        } else {
            viewHolder.coupon.setVisibility(View.GONE);
        }
        if (!StringUtils.isEmpty(chapterItem.getIs_vip()) && TextUtils.equals(chapterItem.getIs_vip(),"0")) {//免费
            viewHolder.vip.setBackgroundResource(R.mipmap.category_free);
        } else {
            if (App.isVip(mContext)){
                viewHolder.vip.setBackgroundResource(R.mipmap.comic_chapter_open);
                viewHolder.coupon.setVisibility(View.GONE);
            }else {
                viewHolder.vip.setBackgroundResource(R.mipmap.category_vip);
                viewHolder.coupon.setVisibility(View.VISIBLE);
            }
        }
        return convertView;
    }

    class ViewHolder {
        TextView title;
        TextView vip;
        TextView coupon;
    }
}
