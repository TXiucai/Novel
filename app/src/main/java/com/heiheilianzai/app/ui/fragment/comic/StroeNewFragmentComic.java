package com.heiheilianzai.app.ui.fragment.comic;

import android.view.View;

import com.heiheilianzai.app.model.event.StoreComicEvent;
import com.heiheilianzai.app.model.event.TaskRedPointEvent;
import com.heiheilianzai.app.ui.fragment.StroeNewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StroeNewFragmentComic extends StroeNewFragment {
    public static int PRODUCT = 2;//小说1; 漫画2 动漫3

    @Override
    protected void initView() {
        super.initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeComicEvent(StoreComicEvent storeEvent) {
        setStoreSearchView(storeEvent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void showTaskRdPoint(TaskRedPointEvent taskPointEvent) {
        mRedPointImg.setVisibility(View.GONE);
    }

    @Override
    protected int getProduct() {
        return PRODUCT;
    }
}
