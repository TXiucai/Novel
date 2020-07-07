package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Base阅读历史记录 Adapter
 *
 * @param <T>
 */
public abstract class BaseReadHistoryAdapter<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    protected List<T> list;
    protected GetPosition<T> getPosition;
    protected Activity activity;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View rootView = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerview_item_readhistory, viewGroup, false);
        return new MyViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        onMyBindViewHolder((MyViewHolder) viewHolder, i);
    }

    protected abstract void onMyBindViewHolder(@NonNull MyViewHolder holder, int position);

    public BaseReadHistoryAdapter(Activity activity, List<T> list, GetPosition<T> getPosition) {
        this.list = list;
        this.activity = activity;
        this.getPosition = getPosition;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.recyclerview_item_readhistory_img)
        public ImageView recyclerview_item_readhistory_img;
        @BindView(R2.id.recyclerview_item_readhistory_name)
        public TextView recyclerview_item_readhistory_name;
        @BindView(R2.id.recyclerview_item_readhistory_des)
        public TextView recyclerview_item_readhistory_des;
        @BindView(R2.id.recyclerview_item_readhistory_time)
        public TextView recyclerview_item_readhistory_time;
        @BindView(R2.id.recyclerview_item_readhistory_goon)
        public Button recyclerview_item_readhistory_goon;
        @BindView(R2.id.list_ad_view_layout)
        public FrameLayout list_ad_view_layout;
        @BindView(R2.id.list_ad_view_img)
        public ImageView list_ad_view_img;
        @BindView(R2.id.recyclerview_item_readhistory_book)
        public LinearLayout recyclerview_item_readhistory_book;
        @BindView(R2.id.recyclerview_item_readhistory_del)
        public TextView recyclerview_item_readhistory_del;
        @BindView(R2.id.recyclerview_item_readhistory_HorizontalScrollView)
        public HorizontalScrollView recyclerview_item_readhistory_HorizontalScrollView;

        public MyViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) recyclerview_item_readhistory_book.getLayoutParams();
            layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
            recyclerview_item_readhistory_book.setLayoutParams(layoutParams);
        }
    }

    protected void setView(MyViewHolder holder) {
        holder.recyclerview_item_readhistory_HorizontalScrollView.scrollTo(0, 0);
        holder.recyclerview_item_readhistory_HorizontalScrollView.setVisibility(View.VISIBLE);
        holder.list_ad_view_layout.setVisibility(View.GONE);
        holder.recyclerview_item_readhistory_img.setImageResource(R.mipmap.book_def_v);
    }

    protected void setAdView(MyViewHolder holder) {
        holder.recyclerview_item_readhistory_HorizontalScrollView.setVisibility(View.GONE);
        holder.list_ad_view_layout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = holder.list_ad_view_img.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
        layoutParams.height = layoutParams.width / 3;
        holder.list_ad_view_img.setLayoutParams(layoutParams);
    }

    public interface GetPosition<T> {
        void getPosition(int falg, T t, int position);
    }
}
