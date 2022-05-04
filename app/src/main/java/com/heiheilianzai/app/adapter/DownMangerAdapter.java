package com.heiheilianzai.app.adapter;

import android.app.Activity;
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
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.model.Downoption;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DownMangerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    List<Downoption> list;
    Activity activity;
    LinearLayout fragment_bookshelf_noresult;
    List<String> stringList;
    private List<Downoption> mSelectList;
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

    public DownMangerAdapter(Activity activity, List<Downoption> list, LinearLayout fragment_bookshelf_noresult) {
        mSelectList = new ArrayList<>();
        this.list = list;
        this.fragment_bookshelf_noresult = fragment_bookshelf_noresult;
        this.activity = activity;
        stringList = new ArrayList<>();
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflate = LayoutInflater.from(activity).inflate(R.layout.item_downmanger, null, false);
        return new ViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        final Downoption downoption = list.get(position);
        viewHolder.item_dowmmanger_HorizontalScrollView.setVisibility(View.VISIBLE);
        setIsEditView(viewHolder, mIsEditOpen);
        setIsSelectAllView(viewHolder, mIsSelectAll);
        viewHolder.mRlCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.mCheckBox.setChecked(!viewHolder.mCheckBox.isChecked());
                if (viewHolder.mCheckBox.isChecked()) {
                    mSelectList.add(downoption);
                } else {
                    mSelectList.remove(downoption);
                }
                mGetSelectItems.getSelectItems(mSelectList);
            }
        });
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        viewHolder.item_dowmmanger_LinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseBook basebooks = LitePal.where("book_id = ?", downoption.book_id).findFirst(BaseBook.class);
                if (activity != null) {
                    activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_down));
                    if (basebooks != null) {
                        ChapterManager.getInstance(activity).openBook(basebooks, downoption.book_id, basebooks.getCurrent_chapter_id());
                    } else {
                        BaseBook baseBook;
                        baseBook = new BaseBook();
                        baseBook.setBook_id(downoption.book_id);
                        baseBook.setName(downoption.bookname);
                        baseBook.setCover(downoption.cover);
                        baseBook.setDescription(downoption.description);
                        baseBook.setAddBookSelf(0);
                        baseBook.saveIsexist(0);
                        ChapterManager.getInstance(activity).openBook(baseBook, downoption.book_id, null);
                    }
                }
            }
        });
        viewHolder.item_dowmmanger_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LitePal.deleteAll(Downoption.class, "book_id=?", downoption.book_id);
                ShareUitls.putDown(activity, downoption.book_id, "0-0");
                List<Downoption> downoptions = new ArrayList<>();
                for (Downoption downoption1 : list) {
                    if (downoption1.book_id.equals(downoption.book_id)) {
                        downoptions.add(downoption1);
                    }
                }
                list.removeAll(downoptions);
                notifyDataSetChanged();
                if (list.size() == 0) {
                    fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                }
            }
        });
        viewHolder.item_dowmmanger_cover.setImageResource(R.mipmap.book_def_v);
        MyPicasso.GlideImageNoSize(activity, downoption.cover, viewHolder.item_dowmmanger_cover, R.mipmap.book_def_v);
        viewHolder.item_dowmmanger_name.setText(downoption.bookname);
        viewHolder.item_dowmmanger_Downoption_title.setText(downoption.start_order + "-" + downoption.end_order + "章");
        viewHolder.item_dowmmanger_Downoption_date.setText(downoption.downoption_date);
        viewHolder.item_dowmmanger_Downoption_size.setText(downoption.downoption_size);
        if (downoption.isdown) {
            viewHolder.item_dowmmanger_Downoption_yixizai.setText("已下载");
        } else {
            BigDecimal b = new BigDecimal(((float) downoption.down_cunrrent_num / (float) downoption.down_num));
            viewHolder.item_dowmmanger_Downoption_yixizai.setText(b.setScale(2, BigDecimal.ROUND_HALF_UP).floatValue() + "%");
        }
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
        public HorizontalScrollView item_dowmmanger_HorizontalScrollView;
        @BindView(R.id.item_dowmmanger_LinearLayout1)
        public LinearLayout item_dowmmanger_LinearLayout1;
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
        @BindView(R.id.item_dowmmanger_Downoption_title)
        public TextView item_dowmmanger_Downoption_title;
        @BindView(R.id.item_dowmmanger_Downoption_date)
        public TextView item_dowmmanger_Downoption_date;
        @BindView(R.id.item_dowmmanger_Downoption_size)
        public TextView item_dowmmanger_Downoption_size;
        @BindView(R.id.item_dowmmanger_Downoption_yixizai)
        public TextView item_dowmmanger_Downoption_yixizai;
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
        void getSelectItems(List<Downoption> selectLists);
    }
}
