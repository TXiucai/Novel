package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.DownMangerAdapter;
import com.heiheilianzai.app.model.Downoption;
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

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 下载历史 漫画Adapter
 */
public class DownMangerComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<BaseComic> list;
    private Activity activity;
    private LinearLayout fragment_bookshelf_noresult;
    private int WIDTH;
    private List<BaseComic> mSelectList;
    private GetSelectItems mGetSelectItems;
    private boolean mIsSelectAll = false;
    private boolean mIsEditOpen = false;

    public void setmIsSelectAll(boolean mIsSelectAll) {
        this.mIsSelectAll = mIsSelectAll;
        notifyDataSetChanged();
    }

    public void setmIsEditOpen(boolean mIsEditOpen) {
        this.mIsEditOpen = mIsEditOpen;
        notifyDataSetChanged();
    }

    public void setmGetSelectItems(GetSelectItems mGetSelectItems) {
        this.mGetSelectItems = mGetSelectItems;
    }

    public DownMangerComicAdapter(Activity activity, List<BaseComic> list, LinearLayout fragment_bookshelf_noresult) {
        mSelectList = new ArrayList<>();
        this.list = list;
        this.fragment_bookshelf_noresult = fragment_bookshelf_noresult;
        this.activity = activity;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(activity).inflate(R.layout.item_downmangercomic, null, false);
        return  new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder= (ViewHolder) holder;
        BaseComic baseComic = list.get(position);
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = WIDTH;
        setIsEditView(viewHolder, mIsEditOpen);
        setIsSelectAllView(viewHolder, mIsSelectAll);
        viewHolder.mRlCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mCheckBox.setChecked(!viewHolder.mCheckBox.isChecked());
                if (viewHolder.mCheckBox.isChecked()) {
                    mSelectList.add(baseComic);
                } else {
                    mSelectList.remove(baseComic);
                }
                mGetSelectItems.getSelectItems(mSelectList);
            }
        });
        viewHolder.item_dowmmanger_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.startActivity(ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(activity, R.string.refer_page_down)));
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
                Intent intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_down), null);
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
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
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
        @BindView(R.id.recyclerview_item_readhistory_check)
        public CheckBox mCheckBox;
        @BindView(R.id.recyclerview_item_readhistory_check_rl)
        public RelativeLayout mRlCheckBox;

        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    protected void setIsEditView(ViewHolder holder, boolean isEditOpen) {
        if (isEditOpen) {
            holder.mRlCheckBox.setVisibility(View.VISIBLE);
            holder.item_dowmmanger_open.setVisibility(View.GONE);
        } else {
            holder.mRlCheckBox.setVisibility(View.GONE);
            holder.item_dowmmanger_open.setVisibility(View.VISIBLE);
        }
    }

    protected void setIsSelectAllView(ViewHolder holder, boolean isSelectAll) {
        mSelectList.clear();
        if (isSelectAll) {
            mSelectList.addAll(list);
        } else {
            mSelectList.clear();
        }
        holder.mCheckBox.setChecked(isSelectAll);
        mGetSelectItems.getSelectItems(mSelectList);
    }

    public interface GetSelectItems {
        void getSelectItems(List<BaseComic> selectLists);
    }
}
