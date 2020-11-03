package com.heiheilianzai.app.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.heiheilianzai.app.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import butterknife.BindView;

/**
 * 下载缓存页面 小说、漫画、有声基类
 */
public abstract class BaseDownMangerFragment<T> extends BaseButterKnifeFragment {
    public static final int BOOK_SON_TYPE = 1;//小说
    public static final int COMIC_SON_TYPE = 2;//漫画
    public static final int PhONIC_SON_TYPE = 3;//有声

    @BindView(R.id.activity_downmanger_list)
    public ListView activity_downmanger_list;
    @BindView(R.id.fragment_bookshelf_noresult)
    public LinearLayout fragment_bookshelf_noresult;
    protected BaseAdapter downMangerAdapter;
    protected List<T> baseList;

    @Override
    public int initContentView() {
        return R.layout.fragment_downmanger;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        View temphead = LayoutInflater.from(activity).inflate(R.layout.item_list_head, null);
        activity_downmanger_list.addHeaderView(temphead, null, false);
        activity_downmanger_list.setHeaderDividersEnabled(true);
        baseList = new ArrayList<>();
        downMangerAdapter = getAeAdapter();
        activity_downmanger_list.setAdapter(downMangerAdapter);
        initData();
    }

    protected abstract void initData();

    protected abstract BaseAdapter getAeAdapter();

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