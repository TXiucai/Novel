package com.heiheilianzai.app.adapter;

import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;

import android.annotation.SuppressLint;
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
import com.heiheilianzai.app.model.BaseAd;
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
public class OptionRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Activity activity;
    LayoutInflater layoutInflater;
    List<OptionBeen> optionBeenList;
    int OPTION, HEIGHT, WIDTH, PRODUCT, CARTOON_WIDTH, CARTOON_HEIGHT, H50;

    public interface OnItemClick {
        void OnItemClick(int position, OptionBeen optionBeen);
    }

    OnItemClick onItemClick;

    public OptionRecyclerViewAdapter(Activity activity, List<OptionBeen> optionBeenList, int OPTION, int PRODUCT, LayoutInflater layoutInflater, OnItemClick onItemClick) {
        this.activity = activity;
        this.optionBeenList = optionBeenList;
        this.OPTION = OPTION;
        this.PRODUCT = PRODUCT;
        this.onItemClick = onItemClick;
        this.layoutInflater = layoutInflater;
        WIDTH = ImageUtil.dp2px(activity, 66);
        H50 = ImageUtil.dp2px(activity, 50);
        HEIGHT = WIDTH * 4 / 3;
        CARTOON_WIDTH = (ScreenSizeUtils.getInstance(activity).getScreenWidth() - WIDTH / 2) / 2;
        CARTOON_HEIGHT = CARTOON_WIDTH * 2 / 3;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (OPTION == LOOKMORE && PRODUCT == 3) {
            View convertView = layoutInflater.inflate(R.layout.item_store_cartoon_style, null, false);
            return new ViewHolderCartoon(convertView);
        } else {
            View convertView = layoutInflater.inflate(R.layout.item_option, parent, false);
            return new ViewHolder(convertView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder1, @SuppressLint("RecyclerView") int position) {
        OptionBeen optionBeen = optionBeenList.get(position);
        if (OPTION == LOOKMORE && PRODUCT == 3) {
            ViewHolderCartoon viewHolderCartoon = (ViewHolderCartoon) viewHolder1;
            RelativeLayout.LayoutParams layoutParamss = (RelativeLayout.LayoutParams) viewHolderCartoon.liem_store_comic_style1_layout.getLayoutParams();
            layoutParamss.width = CARTOON_WIDTH;
            layoutParamss.height = CARTOON_HEIGHT + H50;
            viewHolderCartoon.liem_store_comic_style1_layout.setLayoutParams(layoutParamss);

            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolderCartoon.liem_store_comic_style1_img.getLayoutParams();
            layoutParams.width = CARTOON_WIDTH;
            layoutParams.height = CARTOON_HEIGHT;
            viewHolderCartoon.liem_store_comic_style1_img.setLayoutParams(layoutParams);
            viewHolderCartoon.liem_store_comic_style1_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            MyPicasso.GlideImageNoSize(activity, optionBeen.getCover(), viewHolderCartoon.liem_store_comic_style1_img, R.mipmap.cartoon_def_v);
            viewHolderCartoon.liem_store_comic_style1_name.setText(optionBeen.getName());
            viewHolderCartoon.liem_store_comic_style1_layout.setOnClickListener(v -> onItemClick.OnItemClick(position, optionBeen));
        } else {
            ViewHolder viewHolder = (ViewHolder) viewHolder1;
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
                if (PRODUCT == 1) {
                    MyPicasso.GlideImage(activity, optionBeen.getCover(), viewHolder.imageView, WIDTH, HEIGHT, R.mipmap.book_def_v);
                    viewHolder.author.setText(String.valueOf(optionBeen.getViews()));
                } else if (PRODUCT == 2) {
                    MyPicasso.GlideImage(activity, optionBeen.getCover(), viewHolder.imageView, WIDTH, HEIGHT, R.mipmap.comic_def_v);
                    viewHolder.author.setText(String.valueOf(optionBeen.getTotal_views()));
                } else {
                    MyPicasso.GlideImage(activity, optionBeen.getCover(), viewHolder.imageView, WIDTH, HEIGHT, R.mipmap.cartoon_def_v);
                    viewHolder.author.setText(String.valueOf(optionBeen.view_counts));
                }
                viewHolder.name.setText(optionBeen.getName());
                viewHolder.description.setText(optionBeen.getDescription());
                viewHolder.item_store_label_male_horizontal_tag.removeAllViews();
                if (optionBeen.tag != null && optionBeen.tag.size() > 0) {
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
                if (PRODUCT == 1) {
                    MyPicasso.GlideImageNoSize(activity, optionBeen.ad_image, viewHolder.list_ad_view_img, R.mipmap.book_def_v);
                } else if (PRODUCT == 2) {
                    MyPicasso.GlideImageNoSize(activity, optionBeen.ad_image, viewHolder.list_ad_view_img, R.mipmap.comic_def_v);
                } else {
                    MyPicasso.GlideImageNoSize(activity, optionBeen.ad_image, viewHolder.list_ad_view_img, R.mipmap.cartoon_def_v);
                }
                viewHolder.list_ad_view_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        BaseAd.jumpADInfo(optionBeen, activity);
                    }
                });
            }
        }

    }

    @Override
    public int getItemCount() {
        return optionBeenList == null ? 0 : optionBeenList.size();
    }

    class ViewHolderCartoon extends RecyclerView.ViewHolder {
        @BindView(R.id.liem_store_comic_style1_layout)
        LinearLayout liem_store_comic_style1_layout;
        @BindView(R.id.liem_store_comic_style1_img)
        ImageView liem_store_comic_style1_img;
        @BindView(R.id.liem_store_comic_style1_flag)
        TextView liem_store_comic_style1_flag;
        @BindView(R.id.liem_store_comic_style1_name)
        TextView liem_store_comic_style1_name;
        @BindView(R.id.liem_store_comic_style1_description)
        TextView liem_store_comic_style1_description;
        @BindView(R.id.item_store_corner)
        TextView item_corner;

        public ViewHolderCartoon(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
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



