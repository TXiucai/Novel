package com.heiheilianzai.app.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.event.EditEvent;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;

/**
 * 下载缓存页面 小说、漫画、有声基类
 */
public abstract class BaseDownMangerFragment<T> extends BaseButterKnifeFragment {
    public static final int BOOK_SON_TYPE = 1;//小说
    public static final int COMIC_SON_TYPE = 2;//漫画
    public static final int PhONIC_SON_TYPE = 3;//有声

    @BindView(R.id.activity_downmanger_list)
    public XRecyclerView activity_downmanger_list;
    @BindView(R.id.fragment_bookshelf_noresult)
    public LinearLayout fragment_bookshelf_noresult;
    @BindView(R.id.ll_edit)
    public LinearLayout mLlEdit;
    @BindView(R.id.img_select)
    public ImageView mImgSelect;
    @BindView(R.id.tx_select)
    public TextView mTxSelect;
    @BindView(R.id.img_delete)
    public ImageView mImgDelete;
    @BindView(R.id.tx_delete)
    public TextView mTxDelete;
    @BindView(R.id.ll_select)
    public LinearLayout mLlSelect;
    @BindView(R.id.ll_delete)
    public LinearLayout mLlDelete;
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> downMangerAdapter;
    protected List<T> baseList;
    public boolean mIsSelectAll = false;
    private boolean mIsEditOpen = false;

    @Override
    public int initContentView() {
        return R.layout.fragment_downmanger;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        View temphead = LayoutInflater.from(activity).inflate(R.layout.item_list_head, null);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        activity_downmanger_list.setLayoutManager(layoutManager);
        activity_downmanger_list.addHeaderView(temphead);
        activity_downmanger_list.setPullRefreshEnabled(false);
        activity_downmanger_list.setLoadingMoreEnabled(false);
        baseList = new ArrayList<>();
        downMangerAdapter = getAeAdapter();
        activity_downmanger_list.setAdapter(downMangerAdapter);
        mLlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSelectAll = !mIsSelectAll;
                setClickSeleckAll(mIsSelectAll);
                setLlSelectAllView(mIsSelectAll);
                setLlDeleteView(mIsSelectAll);
            }
        });
        initData();
    }

    protected abstract void initData();

    protected abstract RecyclerView.Adapter<RecyclerView.ViewHolder> getAeAdapter();

    protected abstract void getIsEditOpen(boolean isEditOpen);

    protected abstract void setClickSeleckAll(boolean mIsSelectAll);

    public void setLlSelectAllView(boolean isSelectAll) {
        if (isSelectAll) {
            mImgSelect.setImageDrawable(activity.getDrawable(R.mipmap.check_all_no));
            mTxSelect.setText(activity.getResources().getString(R.string.string_cancel_all));
        } else {
            mImgSelect.setImageDrawable(activity.getDrawable(R.mipmap.check_all_yes));
            mTxSelect.setText(activity.getResources().getString(R.string.string_all_select));
        }
    }

    public void setLlDeleteView(boolean isDelete) {
        if (isDelete) {
            mImgDelete.setImageDrawable(activity.getDrawable(R.mipmap.delet_yes));
            mTxDelete.setTextColor(activity.getResources().getColor(R.color.color_3b3b3b));
        } else {
            mImgDelete.setImageDrawable(activity.getDrawable(R.mipmap.delete_no));
            mTxDelete.setTextColor(activity.getResources().getColor(R.color.color_9a9a9a));
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getIsEditOpen(EditEvent editEvent) {
        mIsEditOpen = editEvent.isEditOpen();
        mLlEdit.setVisibility(mIsEditOpen ? View.VISIBLE : View.GONE);
        getIsEditOpen(mIsEditOpen);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(activity);
    }

    protected void setNoresultView() {
        fragment_bookshelf_noresult.setVisibility(baseList.isEmpty() ? View.VISIBLE : View.GONE);
    }

    public void refreshData(List list) {
        baseList.clear();
        baseList.addAll(list);
        if (downMangerAdapter != null) {
            downMangerAdapter.notifyDataSetChanged();
        }
        setNoresultView();
    }
}