package com.heiheilianzai.app.ui.fragment.book;

import com.heiheilianzai.app.model.event.StoreBookEvent;
import com.heiheilianzai.app.ui.fragment.StroeNewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StroeNewFragmentBook extends StroeNewFragment {
    public static boolean PRODUCT = true;//小说true; 漫画false 进入分享页面使用

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        super.initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeBookEvent(StoreBookEvent storeEvent) {
        setStoreSearchView(storeEvent);
    }

    @Override
    protected boolean getProduct() {
        return PRODUCT;
    }
}