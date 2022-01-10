package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AcquirePrivilegeItem;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.List;

/**
 * 包月特权
 * Created by scb on 2018/8/12.
 */
public class AcquireBaoyuePrivilegeAdapter extends BaseQuickAdapter<AcquirePrivilegeItem, BaseViewHolder> {
    Activity mActivity;

    public AcquireBaoyuePrivilegeAdapter(Activity mActivity) {
        super(R.layout.item_acquire_privilege);
        this.mActivity = mActivity;
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, AcquirePrivilegeItem item) {
        ViewGroup.LayoutParams layoutParams = helper.itemView.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(mActivity).getScreenWidth() / 5;
        helper.itemView.setLayoutParams(layoutParams);
        helper.setText(R.id.item_acquire_privilege_title, item.getLabel());
        MyPicasso.GlideImageNoSize(mActivity, item.getIcon(), helper.getView(R.id.item_acquire_privilege_img));
    }
}
