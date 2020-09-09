package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AcquirePrivilegeItem;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

/**
 *包月特权
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyuePrivilegeAdapter extends ReaderBaseAdapter<AcquirePrivilegeItem> {
    Activity mActivity;
    public AcquireBaoyuePrivilegeAdapter(Activity context, List<AcquirePrivilegeItem> list, int count) {
        this(context, list, count,true);
    }

    public AcquireBaoyuePrivilegeAdapter(Activity context, List<AcquirePrivilegeItem> list, int count, boolean close) {
        super(context, list, count, close);
        mActivity=context;
    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_acquire_privilege, null, false);
            viewHolder.item_acquire_privilege_img= convertView.findViewById(R.id.item_acquire_privilege_img);
            viewHolder.item_acquire_privilege_title= convertView.findViewById(R.id.item_acquire_privilege_title);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyPicasso.GlideImageNoSize(mActivity, ReaderConfig.getBaseUrl()+mList.get(position).getIcon(), viewHolder.item_acquire_privilege_img);
        viewHolder.item_acquire_privilege_title.setText(mList.get(position).getLabel());
        return convertView;
    }

    class ViewHolder {
        ImageView item_acquire_privilege_img;
        TextView item_acquire_privilege_title;
    }
}
