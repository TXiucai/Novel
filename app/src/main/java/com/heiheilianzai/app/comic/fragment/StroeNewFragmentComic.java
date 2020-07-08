package com.heiheilianzai.app.comic.fragment;

import com.heiheilianzai.app.eventbus.StoreComicEvent;
import com.heiheilianzai.app.fragment.StroeNewFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class StroeNewFragmentComic extends StroeNewFragment {
    public static boolean PRODUCT = false;//小说true; 漫画false 进入分享页面使用

    @Override
    protected void initView() {
        EventBus.getDefault().register(this);
        super.initView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void storeComicEvent(StoreComicEvent storeEvent) {
        setStoreSearchView(storeEvent);
    }

    @Override
    protected boolean getProduct() {
        return PRODUCT;
    }
}
