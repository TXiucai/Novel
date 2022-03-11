package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abc on 2017/4/28.
 */
public class OptionRecyclerViewAdapter extends RecyclerView.Adapter<OptionRecyclerViewAdapter.ViewHolder> {
    Activity activity;
    LayoutInflater layoutInflater;
    List<OptionBeen> optionBeenList;
    int OPTION, HEIGHT, WIDTH;
    boolean PRODUCT;

    public interface OnItemClick {
        void OnItemClick(int position, OptionBeen optionBeen);
    }

    OnItemClick onItemClick;

    public OptionRecyclerViewAdapter(Activity activity, List<OptionBeen> optionBeenList, int OPTION, boolean PRODUCT, LayoutInflater layoutInflater, OnItemClick onItemClick) {
        this.activity = activity;
        this.optionBeenList = optionBeenList;
        this.OPTION = OPTION;
        this.PRODUCT = PRODUCT;
        this.onItemClick = onItemClick;
        this.layoutInflater = layoutInflater;
        WIDTH = ImageUtil.dp2px(activity, 66);
        HEIGHT = WIDTH * 4 / 3;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.item_option, parent, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {

        OptionBeen optionBeen = optionBeenList.get(position);

        if (optionBeen.ad_type == 0) {
            viewHolder.item_store_label_male_vertical_layout.setVisibility(View.VISIBLE);
            viewHolder.list_ad_view_layout.setVisibility(View.GONE);
            viewHolder.item_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyToash.Log("onBindViewHolder", position);
                    onItemClick.OnItemClick(position, optionBeen);
                }
            });
            if (PRODUCT) {
                MyPicasso.GlideImage(activity, optionBeen.getCover(), viewHolder.imageView, WIDTH, HEIGHT, R.mipmap.book_def_v);
                viewHolder.author.setText(String.valueOf(optionBeen.getViews()));
            } else {
                MyPicasso.GlideImage(activity, optionBeen.getCover(), viewHolder.imageView, WIDTH, HEIGHT, R.mipmap.comic_def_v);
                viewHolder.author.setText(String.valueOf(optionBeen.getTotal_views()));
            }
            viewHolder.name.setText(optionBeen.getName());
            viewHolder.description.setText(optionBeen.getDescription());
            viewHolder.item_store_label_male_horizontal_tag.removeAllViews();
            for (int i = 0; i < optionBeen.tag.size(); i++) {
                if (i < 3) {
                    BaseTag baseTag = optionBeen.tag.get(i);
                    String str = "&nbsp&nbsp<font color='" + baseTag.color + "'>" + baseTag.tab.trim() + "</font>";
                    TextView textView = new TextView(activity);
                    textView.setTextSize(10);
                    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    layoutParams.setMargins(10, 0, 10, 0);
                    textView.setPadding(10, 5, 20, 10);
                    textView.setLayoutParams(layoutParams);
                    textView.setText(Html.fromHtml(str));
                    textView.setBackground(activity.getDrawable(R.drawable.shape_announce));
                    viewHolder.item_store_label_male_horizontal_tag.addView(textView);
                }
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.item_store_label_male_vertical_layout.getLayoutParams();
            layoutParams.height = HEIGHT;
            viewHolder.item_store_label_male_vertical_layout.setLayoutParams(layoutParams);
        } else {
            viewHolder.item_store_label_male_vertical_layout.setVisibility(View.GONE);
            viewHolder.list_ad_view_layout.setVisibility(View.VISIBLE);
            ViewGroup.LayoutParams layoutParams = viewHolder.list_ad_view_img.getLayoutParams();

            layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
            layoutParams.height = layoutParams.width / 3;
            viewHolder.list_ad_view_img.setLayoutParams(layoutParams);
            if (PRODUCT) {
                MyPicasso.GlideImageNoSize(activity, optionBeen.ad_image, viewHolder.list_ad_view_img, R.mipmap.book_def_v);
            } else {
                MyPicasso.GlideImageNoSize(activity, optionBeen.ad_image, viewHolder.list_ad_view_img, R.mipmap.comic_def_v);
            }
            viewHolder.list_ad_view_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, WebViewActivity.class);
                    intent.putExtra("url", optionBeen.ad_skip_url);
                    intent.putExtra("title", optionBeen.ad_title);
                    intent.putExtra("advert_id", optionBeen.advert_id);
                    intent.putExtra("ad_url_type", optionBeen.ad_url_type);
                    activity.startActivity(intent);
                }
            });
        }

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
        //  @BindView(R.id.item_store_label_male_horizontal_flag)
        //  TextView flag;
        @BindView(R.id.item_store_label_male_horizontal_description)
        TextView description;
        @BindView(R.id.item_store_label_male_horizontal_author)
        TextView author;
        @BindView(R.id.item_store_label_male_horizontal_tag)
        LinearLayout item_store_label_male_horizontal_tag;
        @BindView(R.id.item_store_label_male_vertical_layout)
        LinearLayout item_store_label_male_vertical_layout;
        @BindView(R.id.item_layout)
        RelativeLayout item_layout;
        @BindView(R.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R.id.list_ad_view_img)
        ImageView list_ad_view_img;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}



