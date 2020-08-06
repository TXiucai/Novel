package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.bean.PayChannelNew;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;


public class PayChannelNewAdapter extends ReaderBaseAdapter<PayChannelNew> {
    int selectPosition = 0;
    Activity mActivity;

    public PayChannelNewAdapter(Activity context, List<PayChannelNew> list, int count) {
        super(context, list, count);
        mActivity = context;
    }

    public PayChannelNewAdapter(Activity context, List<PayChannelNew> list, int count, boolean close) {
        super(context, list, count, close);
        mActivity = context;
    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pay_channel, null, false);
            viewHolder.pay_layout = convertView.findViewById(R.id.pay_layout);
            viewHolder.pay_icon = convertView.findViewById(R.id.pay_icon);
            viewHolder.pay_name = convertView.findViewById(R.id.pay_name);
            viewHolder.paytype_img = convertView.findViewById(R.id.paytype_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        PayChannelNew rechargeItem = mList.get(position);
        if (position == selectPosition) {
            viewHolder.pay_layout.setBackgroundResource(R.drawable.shape_white_00a0e8_5);
            viewHolder.paytype_img.setBackgroundResource(R.mipmap.pay_selected);
        } else {
            viewHolder.pay_layout.setBackgroundResource(R.drawable.shape_white_grey_5);
            viewHolder.paytype_img.setBackgroundResource(R.mipmap.pay_unselected);
        }
        MyPicasso.GlideImageNoSize(mActivity, rechargeItem.getLocalImg(), viewHolder.pay_icon);
        viewHolder.pay_name.setText(rechargeItem.getName());

        return convertView;
    }

    public int getSelectPosition() {
        return selectPosition;
    }

    public void setSelectPosition(int selectPosition) {
        this.selectPosition = selectPosition;
    }

    class ViewHolder {
        View pay_layout;
        ImageView pay_icon;
        TextView pay_name;
        ImageView paytype_img;
    }
}
