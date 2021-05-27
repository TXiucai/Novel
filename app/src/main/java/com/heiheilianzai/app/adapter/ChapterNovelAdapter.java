package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChapterNovelAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public Context mContext;
    public List<ChapterItem> mList;
    public String coupon_pay_price;
    public String current_chapter_id;//选中的item章节id
    private OnChapterListener onChapterListener;

    public void setOnChapterListener(OnChapterListener onChapterListener) {
        this.onChapterListener = onChapterListener;
    }

    public ChapterNovelAdapter(Context mContext, List<ChapterItem> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    public void setCoupon_pay_price(String coupon_pay_price) {
        this.coupon_pay_price = coupon_pay_price;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(mContext).inflate(R.layout.item_chapter_catalog, null);
        return new ViewHolderChapter(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderChapter viewHolder = (ViewHolderChapter) holder;
        ChapterItem chapterItem = mList.get(position);
        if (chapterItem.getChapter_id().equals(current_chapter_id)) {
            viewHolder.title.setTextColor(mContext.getResources().getColor(R.color.color_ff8350));
        } else {
            viewHolder.title.setTextColor(Color.BLACK);
        }
        viewHolder.title.setText(chapterItem.getChapter_title());
        viewHolder.coupon.setText(coupon_pay_price + mContext.getResources().getString(R.string.coupon_open));
        if (!StringUtils.isEmpty(chapterItem.getIs_limited_free()) && TextUtils.equals(chapterItem.getIs_limited_free(), "1")) {
            viewHolder.vip.setBackgroundResource(R.mipmap.limited_free);
            viewHolder.coupon.setVisibility(View.GONE);
        } else {
            if (App.isVip(mContext)) {
                if (TextUtils.equals(chapterItem.getIs_vip(), "1") || TextUtils.equals(chapterItem.getIs_book_coupon_pay(), "1")) {
                    viewHolder.vip.setBackgroundResource(R.mipmap.comic_chapter_open);
                    viewHolder.coupon.setVisibility(View.GONE);
                } else {
                    viewHolder.vip.setBackgroundResource(R.mipmap.category_free);
                    viewHolder.coupon.setVisibility(View.GONE);
                }
            } else {
                if (TextUtils.equals(chapterItem.getIs_vip(), "0")) {
                    if (TextUtils.equals(chapterItem.getIs_book_coupon_pay(), "0")) {
                        viewHolder.vip.setBackgroundResource(R.mipmap.category_free);
                        viewHolder.coupon.setVisibility(View.GONE);
                    } else {
                        if (chapterItem.isIs_buy_status()) {
                            viewHolder.vip.setBackgroundResource(R.mipmap.comic_chapter_open);
                            viewHolder.coupon.setVisibility(View.GONE);
                        } else {
                            viewHolder.vip.setBackgroundResource(R.mipmap.category_vip);
                            viewHolder.coupon.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (TextUtils.equals(chapterItem.getIs_book_coupon_pay(), "1")) {
                        if (chapterItem.isIs_buy_status()) {
                            viewHolder.vip.setBackgroundResource(R.mipmap.comic_chapter_open);
                            viewHolder.coupon.setVisibility(View.GONE);
                        } else {
                            viewHolder.vip.setBackgroundResource(R.mipmap.category_vip);
                            viewHolder.coupon.setVisibility(View.VISIBLE);
                        }
                    } else {
                        viewHolder.vip.setBackgroundResource(R.mipmap.category_vip);
                        viewHolder.coupon.setVisibility(View.VISIBLE);
                    }
                }

            }
        }
        viewHolder.itemChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChapterListener != null) {
                    onChapterListener.onChapterSelect(chapterItem, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolderChapter extends RecyclerView.ViewHolder {
        @BindView(R.id.item_chapter_catalog_title)
        TextView title;
        @BindView(R.id.item_chapter_catalog_vip)
        TextView vip;
        @BindView(R.id.item_chapter_coupon)
        TextView coupon;
        @BindView(R.id.item_chapter)
        LinearLayout itemChapter;

        public ViewHolderChapter(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnChapterListener {
        void onChapterSelect(ChapterItem chapterItem, int position);
    }
}
