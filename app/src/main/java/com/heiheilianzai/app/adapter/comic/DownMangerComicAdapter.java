package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.activity.comic.ComicDownActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 下载历史 漫画Adapter
 */
public class DownMangerComicAdapter extends BaseAdapter {
    private List<BaseComic> list;
    private Activity activity;
    private LinearLayout fragment_bookshelf_noresult;
    private int WIDTH;

    public DownMangerComicAdapter(Activity activity, List<BaseComic> list, LinearLayout fragment_bookshelf_noresult) {
        this.list = list;
        this.fragment_bookshelf_noresult = fragment_bookshelf_noresult;
        this.activity = activity;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public BaseComic getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (contentView == null) {
            contentView = LayoutInflater.from(activity).inflate(R.layout.item_downmangercomic, null, false);
            viewHolder = new ViewHolder(contentView);
            contentView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) contentView.getTag();
        }
        final BaseComic baseComic = getItem(i);
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = WIDTH;/*+ holder.rl_left.getLayoutParams().width*/
        viewHolder.item_dowmmanger_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ComicLookActivity.class);
                intent.putExtra("baseComic", baseComic);
                activity.startActivity(intent);
            }
        });
        viewHolder.item_dowmmanger_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ComicDownActivity.class);
                intent.putExtra("baseComic", baseComic);
                intent.putExtra("flag", true);//只查看已下载
                activity.startActivity(intent);
            }
        });
        viewHolder.item_dowmmanger_cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ComicInfoActivity.class);
                intent.putExtra("baseComic", baseComic);
                activity.startActivity(intent);
            }
        });
        viewHolder.item_dowmmanger_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues values1 = new ContentValues();
                values1.put("down_chapters", 0);
                LitePal.update(BaseComic.class, values1, baseComic.getId());
                List<ComicChapter> comicChapterList = LitePal.where().find(ComicChapter.class);
                for (ComicChapter comicChapter : comicChapterList) {
                    ShareUitls.putComicDownStatus(activity, comicChapter.chapter_id, 0);
                }
                String localPath = FileManager.getManhuaSDCardRoot().concat(baseComic.getComic_id());
                FileManager.deleteFile(localPath);
                list.remove(baseComic);
                notifyDataSetChanged();
                if (list.isEmpty()) {
                    fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                }
            }
        });
        viewHolder.item_dowmmanger_HorizontalScrollView.setScrollX(0);
        MyPicasso.GlideImageRoundedCorners(15, activity, baseComic.vertical_cover, viewHolder.item_dowmmanger_cover, ImageUtil.dp2px(activity, 113), ImageUtil.dp2px(activity, 150), R.mipmap.comic_def_v);
        viewHolder.item_dowmmanger_name.setText(baseComic.getName());
        viewHolder.item_dowmmanger_xiazaiprocess.setText(String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_downprocess), baseComic.getDown_chapters() + "/" + baseComic.getTotal_chapters()));
        return contentView;
    }

    class ViewHolder {
        @BindView(R2.id.item_dowmmanger_HorizontalScrollView)
        HorizontalScrollView item_dowmmanger_HorizontalScrollView;
        @BindView(R2.id.item_dowmmanger_LinearLayout2)
        public LinearLayout item_dowmmanger_LinearLayout2;
        @BindView(R2.id.item_dowmmanger_cover)
        public ImageView item_dowmmanger_cover;
        @BindView(R2.id.item_dowmmanger_name)
        public TextView item_dowmmanger_name;
        @BindView(R2.id.item_dowmmanger_open)
        public TextView item_dowmmanger_open;
        @BindView(R2.id.item_dowmmanger_delete)
        public TextView item_dowmmanger_delete;
        @BindView(R2.id.item_dowmmanger_xiazaiprocess)
        public TextView item_dowmmanger_xiazaiprocess;
        @BindView(R2.id.item_dowmmanger_info)
        public RelativeLayout item_dowmmanger_info;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
