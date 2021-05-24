package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.CouponAcceptBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponAcceptAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<CouponAcceptBean.ListBean> mAcceptLists;

    public CouponAcceptAdapter(Activity mActivity, List<CouponAcceptBean.ListBean> mAcceptLists) {
        this.mActivity = mActivity;
        this.mAcceptLists = mAcceptLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_coupon_accept, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CouponAcceptBean.ListBean listBean = mAcceptLists.get(position);
        viewHolder.tittle.setText(listBean.getArticle());
        viewHolder.time.setText(listBean.getDate());
        viewHolder.num.setText(listBean.getSilver_cost() + mActivity.getResources().getString(R.string.string_coupon));
    }

    @Override
    public int getItemCount() {
        return mAcceptLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_coupon_tittle)
        TextView tittle;
        @BindView(R.id.item_coupon_time)
        TextView time;
        @BindView(R.id.item_coupon_num)
        TextView num;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
