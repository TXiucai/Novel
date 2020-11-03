package com.heiheilianzai.app.ui.fragment.boyin;

import android.widget.BaseAdapter;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.boyin.DownMangerPhonicAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.model.event.DownMangerDeleteAllChapterEvent;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

/**
 * 下载缓存 有声管理页面 Fragment
 */
public class DownMangerPhonicFragment extends BaseDownMangerFragment<BoyinInfoBean> {

    @Override
    public int initContentView() {
        return R.layout.fragment_downmanger;
    }


    @Override
    protected void initData() {
        try {
            LitePal.where("down_chapter != ?", "0").findAsync(BoyinInfoBean.class).listen(new FindMultiCallback<BoyinInfoBean>() {
                @Override
                public void onFinish(List<BoyinInfoBean> list) {
                    refreshData(list);
                }
            });
        } catch (Exception e) {
        }
    }

    @Override
    protected BaseAdapter getAeAdapter() {
        return new DownMangerPhonicAdapter(activity, baseList, fragment_bookshelf_noresult);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshProcess(DownMangerDeleteAllChapterEvent downoption) {
        if (downoption.type == PhONIC_SON_TYPE) {
            initData();
        }
    }
}