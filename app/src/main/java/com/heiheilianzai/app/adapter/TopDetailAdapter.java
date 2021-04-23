package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TopDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    private List<OptionBeen> optionBeenList;
    private boolean PRODUCT;
    private OnSelectTopListItemListener mOnSelectTopListItemListener;

    public void setmOnSelectTopListItemListener(OnSelectTopListItemListener mOnSelectTopListItemListener) {
        this.mOnSelectTopListItemListener = mOnSelectTopListItemListener;
    }

    public TopDetailAdapter(Activity activity, List<OptionBeen> optionBeenList, boolean PRODUCT) {
        this.activity = activity;
        this.optionBeenList = optionBeenList;
        this.PRODUCT = PRODUCT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View convertView = LayoutInflater.from(activity).inflate(R.layout.item_top_detail, null, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        OptionBeen optionBeen = optionBeenList.get(position);
        viewHolder.item_store_label_male_vertical_layout.setVisibility(View.VISIBLE);
        viewHolder.item_store_label_male_vertical_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnSelectTopListItemListener != null) {
                    mOnSelectTopListItemListener.onSelctTopListItem(optionBeen, position);
                }
            }
        });
        if (PRODUCT) {
            MyPicasso.GlideImageNoSize(activity, optionBeen.getCover(), viewHolder.imageView, R.mipmap.book_def_v);
        } else {
            MyPicasso.GlideImageNoSize(activity, optionBeen.getCover(), viewHolder.imageView, R.mipmap.comic_def_v);
        }
        if (position < 3) {
            viewHolder.imgTop.setVisibility(View.VISIBLE);
            if (position == 0) {
                viewHolder.imgTop.setImageDrawable(activity.getResources().getDrawable(R.mipmap.top_one));
            } else if (position == 1) {
                viewHolder.imgTop.setImageDrawable(activity.getResources().getDrawable(R.mipmap.top_two));
            } else if (position == 2) {
                viewHolder.imgTop.setImageDrawable(activity.getResources().getDrawable(R.mipmap.top_three));
            }
        } else {
            viewHolder.imgTop.setVisibility(View.GONE);
        }
        viewHolder.name.setText(optionBeen.getName());
        viewHolder.description.setText(optionBeen.getDescription());
        viewHolder.author.setText(String.valueOf(optionBeen.getTotal_views()));
        String str = "";
        for (BaseTag tag : optionBeen.tag) {
            str += tag.tab;
        }
        viewHolder.item_store_label_male_horizontal_tag.setText(str);

    }

    @Override
    public int getItemCount() {
        return optionBeenList == null ? 0 : optionBeenList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_store_label_male_horizontal_img)
        ImageView imageView;
        @BindView(R.id.item_store_label_male_horizontal_name)
        TextView name;
        @BindView(R.id.item_store_label_male_horizontal_description)
        TextView description;
        @BindView(R.id.item_store_label_male_horizontal_author)
        TextView author;
        @BindView(R.id.item_store_label_male_horizontal_tag)
        TextView item_store_label_male_horizontal_tag;
        @BindView(R.id.item_store_label_male_vertical_layout)
        LinearLayout item_store_label_male_vertical_layout;
        @BindView(R.id.item_top_img)
        ImageView imgTop;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public interface OnSelectTopListItemListener {
        void onSelctTopListItem(OptionBeen rankItem, int positon);
    }
}
