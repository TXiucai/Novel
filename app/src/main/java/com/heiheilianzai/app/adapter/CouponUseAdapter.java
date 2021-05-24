package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.CouponUseBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CouponUseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity mActivity;
    private List<CouponUseBean.ListBean> mUseLists;

    public CouponUseAdapter(Activity mActivity, List<CouponUseBean.ListBean> mAcceptLists) {
        this.mActivity = mActivity;
        this.mUseLists = mAcceptLists;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(mActivity).inflate(R.layout.item_coupon_uset, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        CouponUseBean.ListBean listBean = mUseLists.get(position);
        viewHolder.tittle.setText(listBean.getTitle_name());
        viewHolder.time.setText(listBean.getDate_name());
        viewHolder.num.setText("-" + listBean.getSilver_cost() + mActivity.getResources().getString(R.string.string_coupon));
        viewHolder.type.setText(listBean.getData_type_name() + " | " + listBean.getNote());
    }

    @Override
    public int getItemCount() {
        return mUseLists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_coupon_tittle)
        TextView tittle;
        @BindView(R.id.item_coupon_time)
        TextView time;
        @BindView(R.id.item_coupon_num)
        TextView num;
        @BindView(R.id.item_coupon_type)
        TextView type;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

}
