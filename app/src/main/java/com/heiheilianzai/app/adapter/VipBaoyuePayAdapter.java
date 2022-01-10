package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
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
import com.heiheilianzai.app.model.AcquirePayItem;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.view.MarqueeView;

import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipBaoyuePayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AcquirePayItem> list;
    private OnPayItemClickListener onPayItemClickListener;
    private int selectPosition = 0;

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
        notifyDataSetChanged();
    }

    public VipBaoyuePayAdapter(Context context, List<AcquirePayItem> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.my_member_price, null, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AcquirePayItem acquirePayItem = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;
        if (selectPosition == position) {
            viewHolder.mLlGift.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mLlGift.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(acquirePayItem.getGoods_label())) {
            viewHolder.mTxLabel.setVisibility(View.VISIBLE);
            viewHolder.mTxLabel.setText(acquirePayItem.getGoods_label());
        } else {
            viewHolder.mTxLabel.setVisibility(View.GONE);
        }
        viewHolder.mTxTittle.setText(acquirePayItem.getTitle());
        viewHolder.mTxSubTittle.setText(acquirePayItem.getSub_title());
        viewHolder.mTxPrice.setText(String.valueOf(acquirePayItem.getPrice()));
        if (acquirePayItem.getOriginal_price() != 0) {
            viewHolder.mTxOriginalPrice.setVisibility(View.VISIBLE);
            viewHolder.mTxOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.mTxOriginalPrice.setText("¥" + acquirePayItem.getOriginal_price());
        } else {
            viewHolder.mTxOriginalPrice.setVisibility(View.GONE);
        }
        if (onPayItemClickListener != null) {
            viewHolder.mRlItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPayItemClickListener.onPayItemClick(acquirePayItem, position);
                }
            });
        }
        if (acquirePayItem.getPrivilege_list_name() != null && acquirePayItem.getPrivilege_list_name().size() > 0) {
            for (int i = 0; i < acquirePayItem.getPrivilege_list_name().size(); i++) {
                if (i == 0) {
                    viewHolder.mTxGift1.setText(acquirePayItem.getPrivilege_list_name().get(0));
                    viewHolder.mTxGift1.setVisibility(View.VISIBLE);
                } else if (i == 1) {
                    viewHolder.mTxGift2.setText(acquirePayItem.getPrivilege_list_name().get(1));
                    viewHolder.mTxGift2.setVisibility(View.VISIBLE);
                }
            }
        } else {
            viewHolder.mTxGift1.setVisibility(View.GONE);
            viewHolder.mTxGift2.setVisibility(View.GONE);
        }
        if (acquirePayItem.getEnd_time() != 0) {
            viewHolder.mTxTime.setText(DateUtils.getDistanceTime(DateUtils.getTodayTimeHM(), DateUtils.timeStampToDate(acquirePayItem.getEnd_time(), "yyyy-MM-dd HH:mm")));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public interface OnPayItemClickListener {
        void onPayItemClick(AcquirePayItem item, int positon);
    }

    public void setOnPayItemClickListener(OnPayItemClickListener onPayItemClickListener) {
        this.onPayItemClickListener = onPayItemClickListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_newer_label)
        public TextView mTxLabel;
        @BindView(R.id.tv_limit_time)
        public TextView mTxTime;
        @BindView(R.id.iv_member_label)
        public TextView mTxTittle;
        @BindView(R.id.tv_label_01)
        public TextView mTxSubTittle;
        @BindView(R.id.tv_current_price)
        public TextView mTxPrice;
        @BindView(R.id.tv_original_price)
        public TextView mTxOriginalPrice;
        @BindView(R.id.tv_gift_1)
        public TextView mTxGift1;
        @BindView(R.id.tv_gift_2)
        public TextView mTxGift2;
        @BindView(R.id.ll_gift_list)
        public LinearLayout mLlGift;
        @BindView(R.id.rl_newer_member)
        public RelativeLayout mRlItem;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
