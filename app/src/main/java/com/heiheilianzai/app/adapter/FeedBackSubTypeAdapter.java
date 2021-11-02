package com.heiheilianzai.app.adapter;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.ComplaitTypeBean;

public class FeedBackSubTypeAdapter extends BaseQuickAdapter<ComplaitTypeBean.ComplaitSubListBean, BaseViewHolder> {
    private OnBackSubTypeListener mOnBackSubTypeListener;

    public void setmOnBackSubTypeListener(OnBackSubTypeListener mOnBackSubTypeListener) {
        this.mOnBackSubTypeListener = mOnBackSubTypeListener;
    }

    public FeedBackSubTypeAdapter() {
        super(R.layout.item_feed_back_sub);
    }

    @Override
    protected void convert(@NonNull BaseViewHolder helper, ComplaitTypeBean.ComplaitSubListBean item) {
        String isNeed = item.getIsNeed();
        if (TextUtils.equals(isNeed, "1")) {
            helper.setText(R.id.tx_tittle, item.getTitle() + "*");
        } else {
            helper.setText(R.id.tx_tittle, item.getTitle());
        }
        if (mOnBackSubTypeListener != null) {
            EditText editText = helper.getView(R.id.et_content);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    mOnBackSubTypeListener.onBackSubType(item.getId(), s.toString());
                }
            });
        }
    }

    public interface OnBackSubTypeListener {
        void onBackSubType(String id, String type);
    }
}
