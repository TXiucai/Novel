package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.comic.DiscoveryComic;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ComicDiscoveryAdapter extends BaseAdapter {
    Activity activity;
    List<DiscoveryComic> comicList;
    int size;
    LayoutInflater layoutInflater;
    int width, height, DP10;
    int CurrentPosition;
    Resources resources;

    public ComicDiscoveryAdapter(Activity activity, List<DiscoveryComic> comicList, int width, int height) {
        this.activity = activity;
        resources = activity.getResources();
        size = comicList.size();
        layoutInflater = LayoutInflater.from(activity);
        this.comicList = comicList;
        this.width = width - ImageUtil.dp2px(activity, 2);
        this.height = height - ImageUtil.dp2px(activity, 2);
        DP10 = ImageUtil.dp2px(activity, 15);
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public DiscoveryComic getItem(int i) {
        return comicList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public int getCurrentPosition() {
        return CurrentPosition;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (contentView == null) {
            contentView = LayoutInflater.from(activity).inflate(R.layout.item_discovery_comci, null, false);
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }
        final DiscoveryComic comic = getItem(i);
        if (comic.ad_type == 0) {
            viewHolder.list_ad_view_noAD.setVisibility(View.VISIBLE);
            viewHolder.list_ad_view_layout.setVisibility(View.GONE);
            MyPicasso.GlideImageRoundedCorners(8, activity, comic.cover, viewHolder.item_discovery_comic_img, width, height, R.mipmap.comic_def_cross);
            viewHolder.item_discovery_comic_title.setText(comic.title);
            viewHolder.item_discovery_comic_flag.setText(comic.flag);
            viewHolder.item_discovery_comic_tag.removeAllViews();
            if (comic.tag != null && comic.tag.size() > 0) {
                for (BaseTag tag : comic.tag) {
                    try {
                        TextView textView = new TextView(activity);
                        textView.setText(tag.getTab());
                        textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                        textView.setLines(1);
                        textView.setPadding(10, 5, 10, 5);
                        textView.setTextColor(Color.parseColor(tag.getColor()));//resources.getColor(R.color.comic_info_tag_text)
                        GradientDrawable drawable = new GradientDrawable();
                        drawable.setCornerRadius(10);
                        drawable.setColor(Color.parseColor("#1A" + tag.getColor().substring(1)));
                        textView.setBackground(drawable);
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        layoutParams.rightMargin = DP10;
                        layoutParams.gravity = Gravity.CENTER_VERTICAL;
                        viewHolder.item_discovery_comic_tag.addView(textView, layoutParams);
                    } catch (Exception e) {
                    }
                }
            }
        } else {
            MyPicasso.GlideImageRoundedCorners(8, activity, comic.ad_image, viewHolder.item_discovery_comic_img, width, height);
            viewHolder.item_discovery_comic_title.setText(comic.ad_title);
            viewHolder.item_discovery_comic_flag.setText("");
        }
        return contentView;
    }

    class ViewHolder {
        @BindView(R.id.item_discovery_comic_img)
        public ImageView item_discovery_comic_img;
        @BindView(R.id.item_discovery_comic_title)
        public TextView item_discovery_comic_title;
        @BindView(R.id.item_discovery_comic_flag)
        public TextView item_discovery_comic_flag;
        @BindView(R.id.item_discovery_comic_tag)
        public LinearLayout item_discovery_comic_tag;
        @BindView(R.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R.id.list_ad_view_img)
        ImageView list_ad_view_img;
        @BindView(R.id.list_ad_view_noAD)
        public LinearLayout list_ad_view_noAD;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
