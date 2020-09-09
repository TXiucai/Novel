package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.AcquirePayItem;
import com.heiheilianzai.app.ui.activity.PayActivity;

import java.util.List;

/**
 * 老支付 需选择支取渠道 跳转本地页面{@link PayActivity}
 * 新支付 无需选择支付渠道 请求支付跳转 H5(浏览器)
 * 如需要切换回老支付 查看PayActivity 需传参数 goods_id price kefu_online 切换回老跳转代码
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyuePayAdapter extends ReaderBaseAdapter<AcquirePayItem> {
    OnPayItemClickListener onPayItemClickListener;

    public AcquireBaoyuePayAdapter(Context context, List<AcquirePayItem> list, int count) {
        super(context, list, count);
    }

    public AcquireBaoyuePayAdapter(Context context, List<AcquirePayItem> list, int count, boolean close) {
        super(context, list, count, close);
    }

    @Override
    public View getOwnView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_acquire_pay, null, false);
            viewHolder.item_acquire_pay_title = convertView.findViewById(R.id.item_acquire_pay_title);
            viewHolder.item_acquire_pay_title_tag = convertView.findViewById(R.id.item_acquire_pay_title_tag);
            viewHolder.item_acquire_pay_note = convertView.findViewById(R.id.item_acquire_pay_note);
            viewHolder.item_acquire_pay_price = convertView.findViewById(R.id.item_acquire_pay_price);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.item_acquire_pay_title.setText(mList.get(position).getTitle());
        if (mList.get(position).getTag().size() != 0) {
            viewHolder.item_acquire_pay_title_tag.setVisibility(View.VISIBLE);
            viewHolder.item_acquire_pay_title_tag.setText(mList.get(position).getTag().get(0).getTab());
        } else {
            viewHolder.item_acquire_pay_title_tag.setVisibility(View.GONE);
        }
        viewHolder.item_acquire_pay_note.setText(mList.get(position).getNote());
        viewHolder.item_acquire_pay_price.setText("¥ " + mList.get(position).getPrice());
        viewHolder.item_acquire_pay_price.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onPayItemClickListener != null) {
                    if (mList != null && mList.size() > position) {
                        onPayItemClickListener.onPayItemClick(mList.get(position));
                    }
                }
            }
        });
        return convertView;
    }

    class ViewHolder {
        TextView item_acquire_pay_title;
        TextView item_acquire_pay_title_tag;
        TextView item_acquire_pay_note;
        TextView item_acquire_pay_price;
    }

    public interface OnPayItemClickListener {
        void onPayItemClick(AcquirePayItem item);
    }

    public void setOnPayItemClickListener(OnPayItemClickListener onPayItemClickListener) {
        this.onPayItemClickListener = onPayItemClickListener;
    }
}