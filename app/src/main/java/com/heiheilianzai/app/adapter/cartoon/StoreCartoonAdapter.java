package com.heiheilianzai.app.adapter.cartoon;

import android.app.Activity;
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
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.StringUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 漫画Adapter
 * Created by scb on 2018/10/28.
 */
public class StoreCartoonAdapter extends BaseAdapter {
    private List<StroreCartoonLable.Cartoon> taskCenter2s;
    private Activity activity;
    private LayoutInflater layoutInflater;
    private int WIDTH, HEIGHT, height;

    public StoreCartoonAdapter(List<StroreCartoonLable.Cartoon> taskCenter2s, Activity activity, int WIDTH, int HEIGHT) {
        this.taskCenter2s = taskCenter2s;
        this.layoutInflater = LayoutInflater.from(activity);
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        height = ImageUtil.dp2px(activity, 55);
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return taskCenter2s.size();
    }

    @Override
    public StroreCartoonLable.Cartoon getItem(int i) {
        return taskCenter2s.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_store_cartoon_style, null, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        StroreCartoonLable.Cartoon cartoon = getItem(i);
        viewHolder.liem_store_comic_style1_img.setImageResource(R.mipmap.cartoon_def_v);
        MyPicasso.GlideImageNoSize(activity, cartoon.cover, viewHolder.liem_store_comic_style1_img, R.mipmap.cartoon_def_v);
        ViewGroup.LayoutParams layoutParams11 = viewHolder.liem_store_comic_style1_img.getLayoutParams();
        layoutParams11.height = HEIGHT;
        layoutParams11.width = WIDTH;
        viewHolder.liem_store_comic_style1_img.setLayoutParams(layoutParams11);
        //暂时隐藏更新到多少话的ui
        /*if (!TextUtils.isEmpty(comic.flag)) {
            viewHolder.liem_store_comic_style1_flag.setVisibility(View.VISIBLE);
            viewHolder.liem_store_comic_style1_flag.setText(comic.flag);
        } else {
            viewHolder.liem_store_comic_style1_flag.setVisibility(View.GONE);
        }*/
        String jiao_biao = cartoon.jiao_biao;
        if (jiao_biao != null && !StringUtils.isEmpty(jiao_biao)) {
            viewHolder.item_corner.setText(jiao_biao);
            if (jiao_biao.contains("新书")) {
                viewHolder.item_corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_new));
            } else if (jiao_biao.contains("乱伦")) {
                viewHolder.item_corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_luanlun));
            } else if (jiao_biao.contains("人妻")) {
                viewHolder.item_corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_wife));
            } else if (jiao_biao.contains("完结")) {
                viewHolder.item_corner.setBackground(activity.getDrawable(R.mipmap.home_novel_corner_finish));
            }
        }
        viewHolder.liem_store_comic_style1_name.setText(cartoon.name);
        if (cartoon.description != null) {
            viewHolder.liem_store_comic_style1_description.setText(cartoon.description);
        } else if (cartoon.tag != null && !cartoon.tag.isEmpty()) {
            String str = "";
            for (BaseTag tag : cartoon.tag) {
                str += tag.tab + "  ";
            }
            viewHolder.liem_store_comic_style1_description.setText(str);
        } else {
            viewHolder.liem_store_comic_style1_description.setVisibility(View.GONE);
        }
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder.liem_store_comic_style1_layout.getLayoutParams();
        layoutParams.height = HEIGHT + height;
        viewHolder.liem_store_comic_style1_layout.setLayoutParams(layoutParams);
        return convertView;
    }

    class ViewHolder {
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

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
