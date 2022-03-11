package com.heiheilianzai.app.adapter.boyin;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.ui.activity.boyin.BoyinPlayerActivity;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 有声小说下载管理 Adapter
 */
public class DownMangerPhonicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BoyinInfoBean> list;
    private Activity activity;
    private LinearLayout fragment_bookshelf_noresult;
    private int WIDTH;
    private List<BoyinInfoBean> mSelectList;
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

    public DownMangerPhonicAdapter(Activity activity, List<BoyinInfoBean> list, LinearLayout fragment_bookshelf_noresult) {
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
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        BoyinInfoBean boyinInfoBean = list.get(position);
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = WIDTH;
        setIsEditView(viewHolder, mIsEditOpen);
        setIsSelectAllView(viewHolder, mIsSelectAll);
        viewHolder.mRlCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mCheckBox.setChecked(!viewHolder.mCheckBox.isChecked());
                if (viewHolder.mCheckBox.isChecked()) {
                    mSelectList.add(boyinInfoBean);
                } else {
                    mSelectList.remove(boyinInfoBean);
                }
                mGetSelectItems.getSelectItems(mSelectList);
            }
        });

        viewHolder.item_dowmmanger_open.setOnClickListener(new View.OnClickListener() {//下载播放
            @Override
            public void onClick(View view) {
                startBoyinPlayer(String.valueOf(boyinInfoBean.getId()));
            }
        });
        viewHolder.item_dowmmanger_open.setText(LanguageUtil.getString(activity, R.string.boyin_selections_play));
        viewHolder.item_dowmmanger_info.setOnClickListener(new View.OnClickListener() {//下载章节管理 因为需求这里改为了进入有声本地播放
            @Override
            public void onClick(View view) {
                startBoyinPlayer(String.valueOf(boyinInfoBean.getId()));
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
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
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

    private void startBoyinPlayer(String nid) {
        Intent intent = new Intent(activity, BoyinPlayerActivity.class);
        intent.putExtra("nid", nid);
        activity.startActivity(intent);
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
        void getSelectItems(List<BoyinInfoBean> selectLists);
    }
}