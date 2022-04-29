package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.OrderRecordBean;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

public class OrderRecordAdapter extends BaseQuickAdapter<OrderRecordBean, BaseViewHolder> {

    private Context context;
    private List<OrderRecordBean> list;

    public OrderRecordAdapter(Context context, List<OrderRecordBean> list) {
        super(R.layout.adapter_order_record_item, list);
        this.context = context;
        this.list = list;

    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, OrderRecordBean item) {
        TextView tv_type_vip = helper.getView(R.id.tv_type_vip);
        TextView tv_vip_status = helper.getView(R.id.tv_vip_status);
        TextView tv_order_number = helper.getView(R.id.tv_order_number);
        TextView tv_order_time = helper.getView(R.id.tv_order_time);
        TextView tv_pay_time = helper.getView(R.id.tv_pay_time);
        TextView tv_pay_channel = helper.getView(R.id.tv_pay_channel);
        TextView tv_price = helper.getView(R.id.tv_price);
        TextView tv_go_customer = helper.getView(R.id.tv_go_customer);
        TextView tv_copy_order = helper.getView(R.id.tv_copy_order);
        TextView tv_go_pay = helper.getView(R.id.tv_go_pay);

        if (item == null) {
            return;
        }

        if (!TextUtils.isEmpty(item.getUser_vip_name())) {
            tv_type_vip.setText(item.getUser_vip_name());
            tv_type_vip.setVisibility(View.VISIBLE);
        } else {
            tv_type_vip.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getStatus())) {
            String status = getPayStatus(item.getStatus());
            tv_vip_status.setText(status);
            tv_vip_status.setVisibility(View.VISIBLE);

            if ("1".equals(item.getStatus())) {
                tv_go_pay.setVisibility(View.VISIBLE);
                tv_go_pay.setOnClickListener(view -> {
                    if (!TextUtils.isEmpty(item.getGoods_id())) {
                        listener.goPay(item.getGoods_id());
                    }
                });
            } else {
                tv_go_pay.setVisibility(View.GONE);
            }
        } else {
            tv_vip_status.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getTrade_no())) {
            String orderNumber = String.format(context.getResources().getString(R.string.tv_order_number), item.getTrade_no());
            tv_order_number.setText(orderNumber);
            tv_order_number.setVisibility(View.VISIBLE);

            tv_copy_order.setOnClickListener(view -> {
                StringUtils.setStringInClipboard(context, item.getTrade_no());
                MyToash.Toash(context, context.getResources().getString(R.string.order_record_activity_copy));
            });
            tv_copy_order.setVisibility(View.VISIBLE);
        } else {
            tv_order_number.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getCreated_at())) {
            String orderTime = String.format(context.getResources().getString(R.string.tv_order_time), item.getCreated_at());
            tv_order_time.setText(orderTime);
            tv_order_time.setVisibility(View.VISIBLE);
        } else {
            tv_order_time.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getUpdated_at()) && (TextUtils.equals(item.getStatus(), "2") || TextUtils.equals(item.getStatus(), "3"))) {
            String payTime = String.format(context.getResources().getString(R.string.tv_pay_time), item.getUpdated_at());
            tv_pay_time.setText(payTime);
            tv_pay_time.setVisibility(View.VISIBLE);
        } else {
            tv_pay_time.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getPay_channel_name())) {
            String payChannel = String.format(context.getResources().getString(R.string.tv_pay_channel), item.getPay_channel_name());
            tv_pay_channel.setText(payChannel);
            tv_pay_channel.setVisibility(View.VISIBLE);
        } else {
            tv_pay_channel.setVisibility(View.INVISIBLE);
        }

        if (!TextUtils.isEmpty(item.getTotal_fee())) {
            tv_price.setText(Html.fromHtml("<font><small>￥</small></font>" + "<font><big>" + item.getTotal_fee() + "</big></font>"));
            tv_price.setVisibility(View.VISIBLE);
        }

        String kefuOnline = ShareUitls.getString(context, "kefu_online", null);
        if (TextUtils.isEmpty(kefuOnline)) {
            tv_go_customer.setVisibility(View.GONE);
        } else {
            tv_go_customer.setVisibility(View.VISIBLE);
            tv_go_customer.setOnClickListener(view -> {
                if (!TextUtils.isEmpty(kefuOnline)) {
                    listener.goKeFuOnline(kefuOnline);
                }
            });
        }

    }

    private String getPayStatus(String status) {
        switch (status) {
            case "1":
                return "未付款";
            case "2":
                return "付款成功";
            case "3":
                return "付款失败";
            case "4":
                return "已过期";
            default:
                return "付款成功";
        }
    }

    private OrderRecordListener listener;

    public void setOrderRecordKeFuListener(OrderRecordListener listener) {
        this.listener = listener;
    }

    public interface OrderRecordListener {
        void goKeFuOnline(String kefuUrl);

        void goPay(String goodsId, int type);
    }

}
