package com.heiheilianzai.app.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Paint;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.SparseArray;
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

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class VipBaoyuePayAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<AcquirePayItem> list;
    private OnPayItemClickListener onPayItemClickListener;
    private int selectPosition = 0;
    //用于退出activity,避免countdown，造成资源浪费。
    private SparseArray<CountDownTimer> countDownMap;

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
        countDownMap = new SparseArray<>();
    }

    /**
     * 清空资源
     */
    public void cancelAllTimers() {
        if (countDownMap == null) {
            return;
        }
        for (int i = 0, length = countDownMap.size(); i < length; i++) {
            CountDownTimer cdt = countDownMap.get(countDownMap.keyAt(i));
            if (cdt != null) {
                cdt.cancel();
            }
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(context).inflate(R.layout.my_member_price, null, false);
        return new ViewHolder(inflate);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        AcquirePayItem acquirePayItem = list.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        List<String> privilegeList = acquirePayItem.getPrivilege_list_name();
        if (selectPosition == position && privilegeList != null && privilegeList.size() > 0) {
            viewHolder.mLlGift.setVisibility(View.VISIBLE);
        } else {
            viewHolder.mLlGift.setVisibility(View.GONE);
        }

        viewHolder.ll_member_have_gift.setBackgroundResource(R.drawable.bg_gift_select);
        if (selectPosition == position) {
            viewHolder.ll_member_have_gift.setBackgroundResource(R.drawable.bg_stroke_ff9f11);
            viewHolder.iv_selected_icon.setVisibility(View.VISIBLE);
        } else {
            viewHolder.ll_member_have_gift.setBackgroundResource(R.drawable.bg_stroke_transparent);
            viewHolder.iv_selected_icon.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(acquirePayItem.getGoods_label())) {
            viewHolder.mTxLabel.setVisibility(View.VISIBLE);
            viewHolder.mTxLabel.setText(acquirePayItem.getGoods_label());
        } else {
            viewHolder.mTxLabel.setVisibility(View.GONE);
        }
        viewHolder.mTxTittle.setText(acquirePayItem.getTitle());
        viewHolder.mTxSubTittle.setText(acquirePayItem.getSub_title());
        viewHolder.mTxPrice.setText("¥" + String.valueOf(acquirePayItem.getPrice()));
        if (acquirePayItem.getOriginal_price() != 0) {
            viewHolder.mTxOriginalPrice.setVisibility(View.VISIBLE);
            viewHolder.mTxOriginalPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
            viewHolder.mTxOriginalPrice.setText(context.getResources().getString(R.string.stirng_orignal_price) + acquirePayItem.getOriginal_price());
        } else {
            viewHolder.mTxOriginalPrice.setVisibility(View.GONE);
        }
        if (onPayItemClickListener != null) {
            viewHolder.ll_member_have_gift.setOnClickListener(new View.OnClickListener() {
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
        //将前一个缓存清除
        if (viewHolder.countDownTimer != null) {
            viewHolder.countDownTimer.cancel();
        }
        long end_time = (long) acquirePayItem.getEnd_time() * 1000;
        if (end_time > 0) {
            long time = (end_time - System.currentTimeMillis());
            viewHolder.countDownTimer = new CountDownTimer(time, 1000) {
                public void onTick(long millisUntilFinished) {
                    viewHolder.mTxTime.setText(DateUtils.getDistanceTime(DateUtils.getTodayTimeHMS(), DateUtils.timeStampToDate(end_time, "yyyy-MM-dd HH:mm:ss")));
                }

                public void onFinish() {
                    viewHolder.mTxTime.setVisibility(View.GONE);
                }
            }.start();

            countDownMap.put(viewHolder.mTxTime.hashCode(), viewHolder.countDownTimer);
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
        @BindView(R.id.ll_member_have_gift)
        public RelativeLayout ll_member_have_gift;
        @BindView(R.id.iv_selected_icon)
        public ImageView iv_selected_icon;
        public CountDownTimer countDownTimer;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
