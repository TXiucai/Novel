package com.heiheilianzai.app.adapter.book;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.read.RoundRectImageView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LableAdapterH extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<StroreBookcLable.Book> mBookList;
    private Activity mContext;
    private int WIDTH, HEIGHT, height;

    public LableAdapterH(List<StroreBookcLable.Book> mBookList, Activity mContext, int WIDTH, int HEIGHT) {
        this.mBookList = mBookList;
        this.mContext = mContext;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        height = ImageUtil.dp2px(mContext, 50);
    }

    public LableAdapterH(List<StroreBookcLable.Book> mBookList, Activity mContext) {
        this.mBookList = mBookList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_store_label_male_vertical, parent, false);
        return new LableHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        LableHolder lableHolder = (LableHolder) holder;
        StroreBookcLable.Book book = mBookList.get(position);
        String jiao_biao = book.getJiao_biao();
        if (jiao_biao != null && !StringUtils.isEmpty(jiao_biao)) {
            lableHolder.mTxCorner.setText(jiao_biao);
            if (jiao_biao.contains("新书")) {
                lableHolder.mTxCorner.setBackground(mContext.getDrawable(R.mipmap.home_novel_corner_new));
            } else if (jiao_biao.contains("乱伦")) {
                lableHolder.mTxCorner.setBackground(mContext.getDrawable(R.mipmap.home_novel_corner_luanlun));
            } else if (jiao_biao.contains("人妻")) {
                lableHolder.mTxCorner.setBackground(mContext.getDrawable(R.mipmap.home_novel_corner_wife));
            } else if (jiao_biao.contains("完结")) {
                lableHolder.mTxCorner.setBackground(mContext.getDrawable(R.mipmap.home_novel_corner_finish));
            }
        }
        if (!book.tag.isEmpty()) {
            String tagString = "";
            for (BaseTag tag : book.tag) {
                tagString += " " + tag.getTab();
            }
            lableHolder.mTxTip.setText(tagString.substring(1));
            lableHolder.mTxTip.setVisibility(View.VISIBLE);
        } else {
            lableHolder.mTxTip.setVisibility(View.GONE);
        }
        lableHolder.mTxTitle.setText(book.getName());
        ViewGroup.LayoutParams layoutParams11 = lableHolder.mImg.getLayoutParams();
        layoutParams11.height = HEIGHT;
        layoutParams11.width = WIDTH;
        lableHolder.mImg.setLayoutParams(layoutParams11);
        MyPicasso.GlideImageNoSize(mContext, book.getCover(), lableHolder.mImg, R.mipmap.book_def_v);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) lableHolder.mLlItem.getLayoutParams();
        layoutParams.height = HEIGHT + height;
        lableHolder.mLlItem.setLayoutParams(layoutParams);
        lableHolder.itemView.setLayoutParams(layoutParams);
        lableHolder.itemView.setOnClickListener(v ->
                mContext.startActivity(BookInfoActivity.getMyIntent(mContext, "2", book.getBook_id())));
    }

    @Override
    public int getItemCount() {
        return mBookList.size();
    }

    class LableHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_store_label_male_vertical_layout)
        LinearLayout mLlItem;
        @BindView(R.id.item_store_label_male_vertical_img)
        ImageView mImg;
        @BindView(R.id.item_store_label_male_vertical_text)
        TextView mTxTitle;
        @BindView(R.id.item_store_label_male_vertical_text2)
        TextView mTxTip;
        @BindView(R.id.item_store_corner)
        TextView mTxCorner;

        public LableHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
