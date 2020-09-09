package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.model.Downoption;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;

import org.litepal.LitePal;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class DownMangerAdapter extends BaseAdapter {
    List<Downoption> list;
    Activity activity;
    LinearLayout fragment_bookshelf_noresult;
    List<String> stringList;

    public DownMangerAdapter(Activity activity, List<Downoption> list, LinearLayout fragment_bookshelf_noresult) {
        this.list = list;
        this.fragment_bookshelf_noresult = fragment_bookshelf_noresult;
        this.activity = activity;
        stringList = new ArrayList<>();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Downoption getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View contentView, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        contentView = LayoutInflater.from(activity).inflate(R.layout.item_downmanger, null, false);
        viewHolder = new ViewHolder(contentView);
        contentView.setTag(viewHolder);
        final Downoption downoption = getItem(i);
        if (!downoption.showHead) {
            viewHolder.item_dowmmanger_HorizontalScrollView.setVisibility(View.GONE);
        } else {
            viewHolder.item_dowmmanger_HorizontalScrollView.setVisibility(View.VISIBLE);
        }
        viewHolder.item_dowmmanger_LinearLayout2.getLayoutParams().width = ScreenSizeUtils.getInstance(activity).getScreenWidth(); /*+ holder.rl_left.getLayoutParams().width*/
        ;
        viewHolder.item_dowmmanger_LinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BaseBook basebooks = LitePal.where("book_id = ?", downoption.book_id).findFirst(BaseBook.class);
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
        viewHolder.item_dowmmanger_open.setVisibility(View.VISIBLE);
        return contentView;
    }

    class ViewHolder {
        @BindView(R2.id.item_dowmmanger_HorizontalScrollView)
        public HorizontalScrollView item_dowmmanger_HorizontalScrollView;
        @BindView(R2.id.item_dowmmanger_LinearLayout1)
        public LinearLayout item_dowmmanger_LinearLayout1;
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
        @BindView(R2.id.item_dowmmanger_Downoption_title)
        public TextView item_dowmmanger_Downoption_title;
        @BindView(R2.id.item_dowmmanger_Downoption_date)
        public TextView item_dowmmanger_Downoption_date;
        @BindView(R2.id.item_dowmmanger_Downoption_size)
        public TextView item_dowmmanger_Downoption_size;
        @BindView(R2.id.item_dowmmanger_Downoption_yixizai)
        public TextView item_dowmmanger_Downoption_yixizai;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
