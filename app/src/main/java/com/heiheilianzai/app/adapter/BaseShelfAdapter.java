package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.eventbus.ToStore;
import com.heiheilianzai.app.fragment.BookshelfFragment;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseShelfAdapter<T> extends BaseAdapter {
    public int ListSize = 0;
    public int WIDTH, HEIGHT;
    public List<T> mBookList;
    public ImageView shelfitem_img_first;
    public boolean mIsDeletable = false;//是否是处于编辑状态，可删除
    public List<T> checkedBookList = new ArrayList<>();
    public Activity mActivity;
    public TextView mDeleteBtn;
    public int mPosition;

    public BaseShelfAdapter(int WIDTH, int HEIGHT, List<T> mBookList, Activity mActivity) {
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

    @Override
    public View getView(int position, View contentView, ViewGroup parent) {
        try {
            ViewHolder viewHolder = null;
            ViewHolder2 viewHolder2 = null;
            if (contentView == null) {
                if (position < ListSize) {
                    contentView = LayoutInflater.from(mActivity).inflate(R.layout.shelfitem, null);
                    viewHolder = new ViewHolder(contentView);
                    viewHolder.shelfitem_img.setLayoutParams(getLayoutParams(viewHolder.shelfitem_img));
                    viewHolder.shelfitem_check_container.setLayoutParams(getLayoutParams(viewHolder.shelfitem_check_container));
                    contentView.setTag(viewHolder);
                } else {
                    contentView = LayoutInflater.from(mActivity).inflate(R.layout.nover_add, null);
                    viewHolder2 = new ViewHolder2(contentView);
                    viewHolder2.listview_item_nover_add_layout.setLayoutParams(getLayoutParams(viewHolder2.listview_item_nover_add_layout));
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
                T t = mBookList.get(position);
                final ViewHolder finalViewHolder = viewHolder;
                viewHolder.shelfitem_check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            if (!checkedBookList.contains(t)) {
                                checkedBookList.add(t);
                            }
                        } else {
                            if (checkedBookList.contains(t)) {
                                checkedBookList.remove(t);
                            }
                        }
                        finalViewHolder.shelfitem_check_container.setBackgroundColor(isChecked ? Color.parseColor("#00ffffff") : Color.parseColor("#7fffffff"));
                        refreshBtn();
                    }
                });
                getView(viewHolder, mBookList.get(position));
                if (mIsDeletable) {
                    final ViewHolder finalViewHolder1 = viewHolder;
                    viewHolder.shelfitem_check_container.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finalViewHolder1.shelfitem_check.toggle();
                        }
                    });
                    viewHolder.shelfitem_check.setChecked(checkedBookList.contains(t));
                }
                viewHolder.shelfitem_check.setVisibility(mIsDeletable ? View.VISIBLE : View.GONE);
                viewHolder.shelfitem_check_container.setVisibility(mIsDeletable ? View.VISIBLE : View.GONE);
            } else {
                viewHolder2.listview_item_nover_add_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mIsDeletable) {
                            EventBus.getDefault().post(new ToStore(2));
                        }
                    }
                });
                viewHolder2.listview_item_nover_add_image.setImageResource(mIsDeletable ? 0 : R.mipmap.icon_addbook);
                viewHolder2.listview_item_nover_add_layout.setBackgroundColor(mIsDeletable ? 0 : -526087);
            }

        } catch (Exception e) {
        }
        return contentView;
    }

    protected abstract void getView(ViewHolder viewHolder, T t);

    protected abstract void deleteBook(final String bookIdArr);

    protected abstract StringBuilder checkedBooks();

    public void setDeletable(final boolean deletable, TextView btn, int position, final BookshelfFragment.DeleteBook delete) {
        MyToash.Log("refreshBtn q", (mDeleteBtn == null) + "");
        checkedBookList.clear();
        mIsDeletable = deletable;
        mPosition = position;
        mDeleteBtn = btn;
        mDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkedBookList.isEmpty()) {
                    String str = checkedBooks().toString();
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

    public void refreshBtn() {
        MyToash.Log("refreshBtn", (mDeleteBtn == null) + "");
        if (mDeleteBtn == null) {
            return;
        }
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

    public void selectAll(boolean selectAll) {
        checkedBookList.clear();
        if (selectAll) {
            checkedBookList.addAll(mBookList);
        }
        refreshBtn();
    }

    public void setDeletable(boolean deletable) {
        checkedBookList.clear();
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

    private ViewGroup.LayoutParams getLayoutParams(View view) {
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = WIDTH;
        layoutParams.height = HEIGHT;
        return layoutParams;
    }
}
