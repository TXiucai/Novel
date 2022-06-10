package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.ArrayList;
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
    protected List<T> mSelectList;
    protected GetPosition<T> getPosition;
    protected Activity activity;
    public getSelectItems mGetSelectItems;
    public boolean mIsEditOpen;
    public boolean mIsSelectAll;

    public void setmGetSelectItems(getSelectItems mGetSelectItems) {
        this.mGetSelectItems = mGetSelectItems;
    }

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

    public abstract void setmIsEditOpen(boolean mIsEditOpen);

    public abstract void setmSelectAll(boolean mIsSelectAll);


    public BaseReadHistoryAdapter(Activity activity, List<T> list, GetPosition<T> getPosition) {
        mSelectList = new ArrayList<>();
        this.list = list;
        this.activity = activity;
        this.getPosition = getPosition;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.recyclerview_item_readhistory_img)
        public ImageView recyclerview_item_readhistory_img;
        @BindView(R.id.recyclerview_item_readhistory_name)
        public TextView recyclerview_item_readhistory_name;
        @BindView(R.id.recyclerview_item_readhistory_des)
        public TextView recyclerview_item_readhistory_des;
        @BindView(R.id.recyclerview_item_readhistory_time)
        public TextView recyclerview_item_readhistory_time;
        @BindView(R.id.recyclerview_item_readhistory_goon)
        public Button recyclerview_item_readhistory_goon;
        @BindView(R.id.list_ad_view_layout)
        public FrameLayout list_ad_view_layout;
        @BindView(R.id.list_ad_view_img)
        public ImageView list_ad_view_img;
        @BindView(R.id.recyclerview_item_readhistory_book)
        public LinearLayout recyclerview_item_readhistory_book;
        @BindView(R.id.recyclerview_item_readhistory_del)
        public TextView recyclerview_item_readhistory_del;
        @BindView(R.id.recyclerview_item_readhistory_HorizontalScrollView)
        public HorizontalScrollView recyclerview_item_readhistory_HorizontalScrollView;
        @BindView(R.id.recyclerview_item_readhistory_check)
        public CheckBox mCheckBox;
        @BindView(R.id.recyclerview_item_readhistory_check_rl)
        public RelativeLayout mRlCheckBox;
        @BindView(R.id.recyclerview_item_readhistory_tag)
        public TextView mTxTag;
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

    protected void setIsEditView(MyViewHolder holder, boolean isEditOpen) {
        if (isEditOpen) {
            holder.mRlCheckBox.setVisibility(View.VISIBLE);
            holder.recyclerview_item_readhistory_goon.setVisibility(View.GONE);
        } else {
            holder.mRlCheckBox.setVisibility(View.GONE);
            holder.recyclerview_item_readhistory_goon.setVisibility(View.VISIBLE);
        }
    }

    protected void setIsSelectAllView(MyViewHolder holder, boolean isSelectAll) {
        mSelectList.clear();
        if (isSelectAll) {
            mSelectList.addAll(list);
        } else {
            mSelectList.clear();
        }
        holder.mCheckBox.setChecked(isSelectAll);
        mGetSelectItems.getSelectItems(mSelectList);
    }

    public interface GetPosition<T> {
        void getPosition(int falg, T t, int position);
    }

    public interface getSelectItems<T> {
        void getSelectItems(List<T> selectLists);
    }
}
