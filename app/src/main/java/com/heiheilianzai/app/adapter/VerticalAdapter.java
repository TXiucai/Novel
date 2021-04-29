package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

/**
 * 垂直布局的adapter，包括"大神推荐" "新书力推" "宅男最爱"上半部分 "小编最爱"
 */
public class VerticalAdapter extends BaseAdapter {
    Activity activity;
    List<StroreBookcLable.Book> bookList;
    LayoutInflater layoutInflater;
    private int WIDTH, HEIGHT, height;
    boolean orientation;
    boolean isBackground;
    private boolean isHorizontal;
    boolean isNeedBackground;

    public void setNeedBackground(boolean needBackground) {
        isNeedBackground = needBackground;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public void setBackground(boolean background) {
        isBackground = background;
    }

    public VerticalAdapter(Activity activity, List<StroreBookcLable.Book> bookListint, int WIDTH, int HEIGHT, boolean orientation) {
        this.activity = activity;
        this.bookList = bookListint;
        this.layoutInflater = LayoutInflater.from(activity);
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.orientation = orientation;
        height = ImageUtil.dp2px(activity, 50);
        MyToash.Log("VerticalAdapter", WIDTH + "  " + HEIGHT + "  " + height + "  " + orientation);
    }

    @Override
    public int getCount() {
        return bookList.size();
    }

    @Override
    public StroreBookcLable.Book getItem(int position) {
        return bookList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        StroreBookcLable.Book book = getItem(position);
        View contentView = null;
        if (!orientation) {
            contentView = layoutInflater.inflate(R.layout.item_store_label_male_vertical, null, false);
            ImageView imageView = contentView.findViewById(R.id.item_store_label_male_vertical_img);
            LinearLayout item_store_label_male_vertical_layout = contentView.findViewById(R.id.item_store_label_male_vertical_layout);
            TextView name = contentView.findViewById(R.id.item_store_label_male_vertical_text);
            TextView item_store_label_male_vertical_text2 = contentView.findViewById(R.id.item_store_label_male_vertical_text2);
            TextView corner = contentView.findViewById(R.id.item_store_corner);
            String jiao_biao = book.getJiao_biao();
            if (jiao_biao != null && !StringUtils.isEmpty(jiao_biao)) {
                corner.setText(jiao_biao);
                if (jiao_biao.contains("新书")) {
                    corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_new));
                } else if (jiao_biao.contains("乱伦")) {
                    corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_luanlun));
                } else if (jiao_biao.contains("人妻")) {
                    corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_wife));
                } else if (jiao_biao.contains("完结")) {
                    corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_finish));
                }
            }
            if (!book.tag.isEmpty()) {
                String tagString = "";
                for (BaseTag tag : book.tag) {
                    tagString += " " + tag.getTab();
                }
                item_store_label_male_vertical_text2.setText(tagString.substring(1));
                item_store_label_male_vertical_text2.setVisibility(View.VISIBLE);
            } else {
                item_store_label_male_vertical_text2.setVisibility(View.GONE);
            }
            name.setText(book.getName());
            ViewGroup.LayoutParams layoutParams11 = imageView.getLayoutParams();
            layoutParams11.height = HEIGHT;
            layoutParams11.width = WIDTH;
            imageView.setLayoutParams(layoutParams11);
            if (isHorizontal) {
                MyPicasso.GlideImageNoSize(activity, book.getH_cover(), imageView, R.mipmap.book_def_cross);
            } else {
                MyPicasso.GlideImageNoSize(activity, book.getCover(), imageView, R.mipmap.book_def_v);
            }
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) item_store_label_male_vertical_layout.getLayoutParams();
            layoutParams.height = HEIGHT + height;
            item_store_label_male_vertical_layout.setLayoutParams(layoutParams);
        } else {
            contentView = layoutInflater.inflate(R.layout.item_store_label_male_horizontal2, null, false);
            ImageView imageView = contentView.findViewById(R.id.item_store_label_male_horizontal_img);
            TextView name = contentView.findViewById(R.id.item_store_label_male_horizontal_name);
            TextView flag = contentView.findViewById(R.id.item_store_label_male_horizontal_flag);
            TextView description = contentView.findViewById(R.id.item_store_label_male_horizontal_description);
            TextView author = contentView.findViewById(R.id.item_store_label_male_horizontal_author);
            TextView item_store_label_male_horizontal_tag = contentView.findViewById(R.id.item_store_label_male_horizontal_tag);
            LinearLayout item_store_label_male_vertical_layout = contentView.findViewById(R.id.item_store_label_male_vertical_layout);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) item_store_label_male_vertical_layout.getLayoutParams();
            if (isNeedBackground){
                layoutParams.height = HEIGHT + ImageUtil.dp2px(activity, 28);
                item_store_label_male_vertical_layout.setPadding(20,14,20,14);
                if (isBackground) {
                    item_store_label_male_vertical_layout.setBackground(activity.getDrawable(R.mipmap.home_novel_13_red));
                    isBackground = false;
                } else {
                    item_store_label_male_vertical_layout.setBackground(activity.getDrawable(R.mipmap.home_novel_13_green));
                    isBackground = true;
                }
                description.setTextColor(activity.getResources().getColor(R.color.white));
                name.setTextColor(activity.getResources().getColor(R.color.white));
            }else {
                layoutParams.height = HEIGHT;
                item_store_label_male_vertical_layout.setPadding(20,4,4,14);
                item_store_label_male_vertical_layout.setBackground(null);
                description.setTextColor(activity.getResources().getColor(R.color.color_666666));
                name.setTextColor(activity.getResources().getColor(R.color.color_1a1a1a));
            }
            item_store_label_male_vertical_layout.setLayoutParams(layoutParams);
            LinearLayout.LayoutParams layoutParamsIm = (LinearLayout.LayoutParams) imageView.getLayoutParams();
            layoutParamsIm.height = HEIGHT;
            layoutParamsIm.width = WIDTH;
            imageView.setLayoutParams(layoutParamsIm);
            MyPicasso.GlideImageNoSize(activity, book.getCover(), imageView, R.mipmap.book_def_v);
            name.setText(book.getName());
            if (TextUtils.isEmpty(book.getFlag())) {
                flag.setVisibility(View.GONE);
            } else {
                flag.setVisibility(View.VISIBLE);
            }
            flag.setText(book.getFlag());
            description.setText(book.getDescription());
            author.setText(book.getAuthor());
            String str = "";
            for (BaseTag tag : book.tag) {
                str = tag.tab;
            }
            item_store_label_male_horizontal_tag.setText(str);
        }
        return contentView;
    }
}
