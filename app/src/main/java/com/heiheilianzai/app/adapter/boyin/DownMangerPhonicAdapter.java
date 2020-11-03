package com.heiheilianzai.app.adapter.boyin;

import android.app.Activity;
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
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.ui.activity.boyin.BoyinDownActivity;
import com.heiheilianzai.app.ui.activity.boyin.BoyinPlayerActivity;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;

import org.litepal.LitePal;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 有声小说下载管理 Adapter
 */
public class DownMangerPhonicAdapter extends BaseAdapter {
    private List<BoyinInfoBean> list;
    private Activity activity;
    private LinearLayout fragment_bookshelf_noresult;
    private int WIDTH;

    public DownMangerPhonicAdapter(Activity activity, List<BoyinInfoBean> list, LinearLayout fragment_bookshelf_noresult) {
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
    public BoyinInfoBean getItem(int i) {
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
        final BoyinInfoBean boyinInfoBean = getItem(i);
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = WIDTH;
        viewHolder.item_dowmmanger_open.setOnClickListener(new View.OnClickListener() {//下载播放
            @Override
            public void onClick(View view) {
                startBoyinPlayer(String.valueOf(boyinInfoBean.getId()));
            }
        });
        viewHolder.item_dowmmanger_open.setText(LanguageUtil.getString(activity, R.string.boyin_selections_play));
        viewHolder.item_dowmmanger_info.setOnClickListener(new View.OnClickListener() {//下载章节管理
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BoyinDownActivity.class);
                intent.putExtra("nid", boyinInfoBean.getId());
                intent.putExtra("isDown", false);
                activity.startActivity(intent);
            }
        });
        viewHolder.item_dowmmanger_cover.setOnClickListener(new View.OnClickListener() {//下载播放
            @Override
            public void onClick(View view) {
                startBoyinPlayer(String.valueOf(boyinInfoBean.getId()));
            }
        });
        viewHolder.item_dowmmanger_delete.setOnClickListener(new View.OnClickListener() {//删除整本本小说及所有章节
            @Override
            public void onClick(View view) {
                List<BoyinChapterBean> boyinChapterList = LitePal.where("nid = ?", String.valueOf(boyinInfoBean.getId())).find(BoyinChapterBean.class);
                for (BoyinChapterBean comicChapter : boyinChapterList) {//删除本地保存的所有有声音频
                    if (!StringUtils.isEmpty(comicChapter.getSavePath())) {
                        FileManager.deleteFile(comicChapter.getSavePath());
                    }
                }
                LitePal.deleteAll(BoyinChapterBean.class, "nid = ?", String.valueOf(boyinInfoBean.getId()));//删除有声章节数据
                LitePal.deleteAll(BoyinInfoBean.class, "nid = ?", String.valueOf(boyinInfoBean.getId()));//删除有声小说数据
                list.remove(boyinInfoBean);
                notifyDataSetChanged();
                if (list.isEmpty()) {
                    fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                }
            }
        });
        viewHolder.item_dowmmanger_HorizontalScrollView.setScrollX(0);
        MyPicasso.GlideImageRoundedCorners(15, activity, boyinInfoBean.getImg(), viewHolder.item_dowmmanger_cover, ImageUtil.dp2px(activity, 113), ImageUtil.dp2px(activity, 150), R.mipmap.comic_def_v);
        viewHolder.item_dowmmanger_name.setText(boyinInfoBean.getName());
        viewHolder.item_dowmmanger_xiazaiprocess.setText(String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_downprocess), boyinInfoBean.getDown_chapter() + "/" + boyinInfoBean.getNumbers()));
        return contentView;
    }

    class ViewHolder {
        @BindView(R.id.item_dowmmanger_HorizontalScrollView)
        HorizontalScrollView item_dowmmanger_HorizontalScrollView;
        @BindView(R.id.item_dowmmanger_LinearLayout2)
        public LinearLayout item_dowmmanger_LinearLayout2;
        @BindView(R.id.item_dowmmanger_cover)
        public ImageView item_dowmmanger_cover;
        @BindView(R.id.item_dowmmanger_name)
        public TextView item_dowmmanger_name;
        @BindView(R.id.item_dowmmanger_open)
        public TextView item_dowmmanger_open;
        @BindView(R.id.item_dowmmanger_delete)
        public TextView item_dowmmanger_delete;
        @BindView(R.id.item_dowmmanger_xiazaiprocess)
        public TextView item_dowmmanger_xiazaiprocess;
        @BindView(R.id.item_dowmmanger_info)
        public RelativeLayout item_dowmmanger_info;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void startBoyinPlayer(String nid) {
        Intent intent = new Intent(activity, BoyinPlayerActivity.class);
        intent.putExtra("nid", nid);
        activity.startActivity(intent);
    }
}