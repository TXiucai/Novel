package com.heiheilianzai.app.adapter;


import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.PayChannel;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import cn.jmessage.support.qiniu.android.utils.StringUtils;

public class PayChannelAdapter extends ReaderBaseAdapter<PayChannel> {
    int selectPosition = 0;
    Activity mActivity;
    public PayChannelAdapter(Activity context, List<PayChannel> list, int count) {
        super(context, list, count);
        mActivity=context;
    }

    public PayChannelAdapter(Activity context, List<PayChannel> list, int count, boolean close) {
        super(context, list, count, close);
        mActivity=context;
    }


    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new PayChannelAdapter.ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_pay_channel, null, false);
            viewHolder.pay_layout= convertView.findViewById(R.id.pay_layout);
            viewHolder.pay_icon = convertView.findViewById(R.id.pay_icon);
            viewHolder.pay_name = convertView.findViewById(R.id.pay_name);
            viewHolder.paytype_img = convertView.findViewById(R.id.paytype_img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        PayChannel rechargeItem = mList.get(position);

        if (position == selectPosition) {
            viewHolder.pay_layout.setBackgroundResource(R.drawable.shape_white_00a0e8_5);
            viewHolder.paytype_img.setBackgroundResource(R.mipmap.pay_selected);
        } else {
            viewHolder.pay_layout.setBackgroundResource(R.drawable.shape_white_grey_5);
            viewHolder.paytype_img.setBackgroundResource(R.mipmap.pay_unselected);
        }

        if (StringUtils.isNullOrEmpty(rechargeItem.getIcon())) {
              if("1".equals(rechargeItem.getType())){
                  viewHolder.pay_icon.setImageResource(R.mipmap.weixin);
              }else {
                  viewHolder.pay_icon.setImageResource(R.mipmap.alipay);
              }
        }else {
            MyPicasso.GlideImage(mActivity,rechargeItem.getIcon(),viewHolder.pay_icon,ImageUtil.dp2px(mContext, 30),ImageUtil.dp2px(mContext, 30));
        }
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
