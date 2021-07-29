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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipBaoyuePayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AcquirePayItem> list;
    private OnPayItemClickListener onPayItemClickListener;
    private int selectPosition=0;

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
        View inflate = LayoutInflater.from(context).inflate(R.layout.vip_baoyue_pay_item, null, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AcquirePayItem acquirePayItem = list.get(position);
        ViewHolder viewHolder= (ViewHolder) holder;
        if (selectPosition==position){
            viewHolder.ryItem.setBackground(context.getDrawable(R.drawable.shape_d69547_10));
        }else {
            viewHolder.ryItem.setBackground(context.getDrawable(R.drawable.shape_e6e6e6_10));
        }
        if (!TextUtils.isEmpty(acquirePayItem.getGoods_label())) {
            viewHolder.txFlag.setVisibility(View.VISIBLE);
            viewHolder.txFlag.setText(acquirePayItem.getGoods_label());
        } else {
            viewHolder.txFlag.setVisibility(View.GONE);
        }
        viewHolder.txTittle.setText(acquirePayItem.getTitle());
        viewHolder.txPrice.setText(acquirePayItem.getPrice());
        if (acquirePayItem.getOriginal_price()!=null&& !TextUtils.equals(acquirePayItem.getOriginal_price(),"0")){
            viewHolder.txTip.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.txTip.setText("¥"+acquirePayItem.getOriginal_price());
            viewHolder.txTip.setVisibility(View.VISIBLE);
        }else {
            viewHolder.txTip.setVisibility(View.GONE);
        }
        if (acquirePayItem.getNote()!=null){
            viewHolder.txBottomTip.setVisibility(View.VISIBLE);
            viewHolder.txBottomTip.setText(acquirePayItem.getNote());
        }else {
            viewHolder.txBottomTip.setVisibility(View.GONE);
        }
        if (onPayItemClickListener!=null){
            viewHolder.ryItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onPayItemClickListener.onPayItemClick(acquirePayItem,position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    public interface OnPayItemClickListener {
        void onPayItemClick(AcquirePayItem item,int positon);
    }

    public void setOnPayItemClickListener(OnPayItemClickListener onPayItemClickListener) {
        this.onPayItemClickListener = onPayItemClickListener;
    }
    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_vip_flag)
        public TextView txFlag;
        @BindView(R.id.tx_vip_tittle)
        public TextView txTittle;
        @BindView(R.id.tx_vip_price)
        public TextView txPrice;
        @BindView(R.id.tx_vip_tip)
        public TextView txTip;
        @BindView(R.id.rl_vip_item)
        public LinearLayout ryItem;
        @BindView(R.id.tx_bottom_tip)
        public TextView txBottomTip;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
