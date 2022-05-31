package com.heiheilianzai.app.adapter.cartoon;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.cartoon.CartoonChapter;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CartoonChapterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<CartoonChapter> mCartoonChapters;
    private Context mContext;
    private OnBackChapterListener mOnBackChapterListener;

    public void setmOnBackChapterListener(OnBackChapterListener mOnBackChapterListener) {
        this.mOnBackChapterListener = mOnBackChapterListener;
    }

    public CartoonChapterAdapter(List<CartoonChapter> mCartoonChapters, Context mContext) {
        this.mCartoonChapters = mCartoonChapters;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cartoon_chapter, parent, false);
        return new ViewHolderChapter(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CartoonChapter cartoonChapter = mCartoonChapters.get(position);
        ViewHolderChapter holderChapter = (ViewHolderChapter) holder;
        if (cartoonChapter.isSelect()) {
            holderChapter.mTxChapter.setBackground(mContext.getDrawable(R.drawable.shape_cartoon_chapter_select));
            holderChapter.mTxChapter.setTextColor(mContext.getResources().getColor(R.color.white));
        } else {
            holderChapter.mTxChapter.setBackground(mContext.getDrawable(R.drawable.shape_cartoon_chapter_normal));
            holderChapter.mTxChapter.setTextColor(mContext.getResources().getColor(R.color.color_35363B));
        }
        holderChapter.mTxChapter.setText(cartoonChapter.getChapter_title());
        holderChapter.mTxChapter.setOnClickListener(v -> {
            if (mOnBackChapterListener != null) {
                mOnBackChapterListener.onBackChapter(cartoonChapter, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCartoonChapters.size();
    }

    public interface OnBackChapterListener {
        void onBackChapter(CartoonChapter cartoonChapter, int position);
    }

    class ViewHolderChapter extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_chapter)
        public TextView mTxChapter;

        public ViewHolderChapter(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
