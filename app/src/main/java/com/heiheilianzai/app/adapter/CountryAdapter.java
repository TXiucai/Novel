package com.heiheilianzai.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.boyin.BoyinPlayerAdapter;
import com.heiheilianzai.app.model.CountryBean;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CountryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<CountryBean.ListBean> mListBeans;
    private OnItemListener onItemListener;

    public void setOnItemListener(OnItemListener onItemListener) {
        this.onItemListener = onItemListener;
    }

    public CountryAdapter(Context mContext, List<CountryBean.ListBean> mListBeans) {
        this.mContext = mContext;
        this.mListBeans = mListBeans;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_country_code, parent, false);
        return new CountryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        CountryViewHolder countryViewHolder = (CountryViewHolder) holder;
        CountryBean.ListBean listBean = mListBeans.get(position);
        countryViewHolder.mTxName.setText(listBean.getCountry_name());
        countryViewHolder.mTxCode.setText("+" + String.valueOf(listBean.getCounter_code()));
        countryViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onItemListener != null) {
                    onItemListener.onItemListener(listBean, position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListBeans == null ? 0 : mListBeans.size();
    }

    public class CountryViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tx_name)
        public TextView mTxName;
        @BindView(R.id.tx_code)
        public TextView mTxCode;

        public CountryViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemListener {
        void onItemListener(CountryBean.ListBean listBean, int position);
    }
}
