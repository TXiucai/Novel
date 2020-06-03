package com.heiheilianzai.app.book.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.ChapterItem;
import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.ToStore;
import com.heiheilianzai.app.fragment.BookshelfFragment;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 书架编辑Adapter
 */
public class ShelfAdapterNew extends BaseAdapter {
    public int WIDTH, HEIGHT;
    public ImageView shelfitem_img_first;
    private List<BaseBook> mBookList;
    private Activity mActivity;
    private TextView mDeleteBtn;
    int ListSize = 0;
    private boolean mIsDeletable = false;//是否是处于编辑状态，可删除
    public List<BaseBook> checkedBookList = new ArrayList<>();
    int mPosition;

    public ShelfAdapterNew(int WIDTH, int HEIGHT, List<BaseBook> mBookList, Activity mActivity) {
        MyToash.Log("ShelfAdapterNew1", WIDTH + "   " + HEIGHT + "  " + mBookList.size());
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        this.mBookList = mBookList;
        ListSize = mBookList.size();
        this.mActivity = mActivity;
    }

    @Override
    public int getCount() {
        return ListSize + 1;
    }

    @Override
    public Object getItem(int position) {
        if (position < ListSize) {
            return mBookList.get(position);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setDeletable(final boolean deletable, TextView btn, int position, final BookshelfFragment.DeleteBook delete) {
        checkedBookList.clear();
        mIsDeletable = deletable;
        mPosition = position;
        mDeleteBtn = btn;
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkedBookList.isEmpty()) {
                    StringBuilder builder = new StringBuilder();
                    for (BaseBook baseBook : checkedBookList) {
                        builder.append("," + baseBook.getBook_id());
                        if (baseBook.getId() != 0) {
                            LitePal.delete(BaseBook.class, baseBook.getId());
                        } else {
                            LitePal.deleteAll(BaseBook.class, "book_id = ?", baseBook.getBook_id());
                        }
                        LitePal.deleteAllAsync(ChapterItem.class, "book_id = ?", baseBook.getBook_id());
                        String filepath = FileManager.getSDCardRoot().concat("Reader/book/").concat(baseBook.getBook_id() + "/");
                        FileManager.deleteFile(filepath);
                    }
                    String str = builder.toString();
                    String bookIdArr = str.substring(1);
                    mBookList.removeAll(checkedBookList);
                    checkedBookList.clear();
                    delete.success();
                    if (Utils.isLogin(mActivity)) {
                        deleteBook(bookIdArr);
                    }
                }
            }
        });
        notifyDataSetChanged();
        refreshBtn();
    }

    public void selectAll(boolean selectAll) {
        checkedBookList.clear();
        if (selectAll) {
            checkedBookList.addAll(mBookList);
        }
        refreshBtn();
    }

    public void refreshBtn() {
        int size = checkedBookList.size();
        if (size == 0) {
            mDeleteBtn.setBackgroundColor(ContextCompat.getColor(mActivity, R.color.lightgray2));
            mDeleteBtn.setTextColor(Color.GRAY);
            mDeleteBtn.setText(String.format(LanguageUtil.getString(mActivity, R.string.noverfragment_shanchushuji2), 0));
        } else {
            mDeleteBtn.setText(String.format(LanguageUtil.getString(mActivity, R.string.noverfragment_shanchushuji2), size));
            mDeleteBtn.setTextColor(Color.WHITE);
            mDeleteBtn.setBackgroundColor(Color.RED);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    @Override
    public View getView(final int position, View contentView, ViewGroup arg2) {
        try {
            ViewHolder viewHolder = null;
            ViewHolder2 viewHolder2 = null;
            if (contentView == null) {
                if (position < ListSize) {
                    contentView = LayoutInflater.from(mActivity).inflate(R.layout.shelfitem, null);
                    viewHolder = new ViewHolder(contentView);
                    FrameLayout.LayoutParams layoutParams2 = (FrameLayout.LayoutParams) viewHolder.shelfitem_img.getLayoutParams();
                    layoutParams2.width = WIDTH;
                    layoutParams2.height = HEIGHT;
                    viewHolder.shelfitem_img.setLayoutParams(layoutParams2);
                    contentView.setTag(viewHolder);
                    FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) viewHolder.shelfitem_check_container.getLayoutParams();
                    layoutParams.width = WIDTH;
                    layoutParams.height = HEIGHT;
                    viewHolder.shelfitem_check_container.setLayoutParams(layoutParams);
                } else {
                    contentView = LayoutInflater.from(mActivity).inflate(R.layout.nover_add, null);
                    viewHolder2 = new ViewHolder2(contentView);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) viewHolder2.listview_item_nover_add_layout.getLayoutParams();
                    layoutParams.width = WIDTH;
                    layoutParams.height = HEIGHT;
                    viewHolder2.listview_item_nover_add_layout.setLayoutParams(layoutParams);
                    contentView.setTag(viewHolder2);
                }
            } else {
                if (position < ListSize)
                    viewHolder = (ViewHolder) contentView.getTag();
                else
                    viewHolder2 = (ViewHolder2) contentView.getTag();
            }
            if (position == 0 && shelfitem_img_first == null) {
                if (ListSize > 0) {
                    shelfitem_img_first = viewHolder.shelfitem_img;
                } else {
                    shelfitem_img_first = viewHolder2.listview_item_nover_add_image;
                }
            }
            if (position < ListSize) {
                final BaseBook baseBook = mBookList.get(position);
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.shelfitem_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!checkedBookList.contains(baseBook)) {
                                checkedBookList.add(baseBook);
                            }
                            finalViewHolder.shelfitem_check_container.setBackgroundColor(Color.parseColor("#00ffffff"));
                        } else {
                            finalViewHolder.shelfitem_check_container.setBackgroundColor(Color.parseColor("#7fffffff"));
                            if (checkedBookList.contains(baseBook)) {
                                checkedBookList.remove(baseBook);
                            }
                        }
                        refreshBtn();
                    }
                });
                viewHolder.shelfitem_title.setText(baseBook.getName());
                viewHolder.shelfitem_img.setImageResource(R.mipmap.book_def_v);
                if (baseBook.getRecentChapter() != 0 && (baseBook.getRecentChapter() != baseBook.getTotal_Chapter())) {
                    viewHolder.shelfitem_top_newchapter.setVisibility(View.VISIBLE);
                    viewHolder.shelfitem_top_newchapter.setText(baseBook.getRecentChapter() + "");
                } else {
                    viewHolder.shelfitem_top_newchapter.setVisibility(View.GONE);
                }
                MyPicasso.GlideImageNoSize(mActivity, baseBook.getCover(), viewHolder.shelfitem_img, R.mipmap.book_def_v);
                if (mIsDeletable) {
                    viewHolder.shelfitem_check.setVisibility(View.VISIBLE);
                    viewHolder.shelfitem_check_container.setVisibility(View.VISIBLE);
                    final ViewHolder finalViewHolder1 = viewHolder;
                    viewHolder.shelfitem_check_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalViewHolder1.shelfitem_check.toggle();
                        }
                    });
                    if (checkedBookList.contains(baseBook)) {
                        viewHolder.shelfitem_check.setChecked(true);
                    } else {
                        viewHolder.shelfitem_check.setChecked(false);
                    }
                } else {
                    viewHolder.shelfitem_check.setVisibility(View.GONE);
                    viewHolder.shelfitem_check_container.setVisibility(View.GONE);
                }
            } else {
                viewHolder2.listview_item_nover_add_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsDeletable) {
                            EventBus.getDefault().post(new ToStore(1));
                        }
                    }
                });
                if (mIsDeletable) {
                    viewHolder2.listview_item_nover_add_image.setImageResource(0);
                    viewHolder2.listview_item_nover_add_layout.setBackgroundColor(0);
                } else {
                    viewHolder2.listview_item_nover_add_image.setImageResource(R.mipmap.icon_addbook);
                    viewHolder2.listview_item_nover_add_layout.setBackgroundColor(-526087);
                }
            }
        } catch (Exception e) {
        }
        return contentView;
    }

    /**
     * 删除书架书籍
     */
    public void deleteBook(final String bookIdArr) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("book_id", bookIdArr);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookDelCollectUrl, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }


    public void setDeletable(boolean deletable) {
        checkedBookList.clear();
        refreshBtn();
        mIsDeletable = deletable;
    }


    public boolean isDeletable() {
        return mIsDeletable;
    }

    public class ViewHolder {
        @BindView(R2.id.shelfitem_check)
        public CheckBox shelfitem_check;
        @BindView(R2.id.shelfitem_title)
        public TextView shelfitem_title;
        @BindView(R2.id.shelfitem_img)
        public ImageView shelfitem_img;
        @BindView(R2.id.shelfitem_layout)
        public LinearLayout shelfitem_layout;
        @BindView(R2.id.shelfitem_check_container)
        public View shelfitem_check_container;
        @BindView(R2.id.shelfitem_top_newchapter)
        public TextView shelfitem_top_newchapter;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public class ViewHolder2 {
        @BindView(R2.id.listview_item_nover_add_image)
        public ImageView listview_item_nover_add_image;
        @BindView(R2.id.listview_item_nover_add_layout)
        public RelativeLayout listview_item_nover_add_layout;

        public ViewHolder2(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
