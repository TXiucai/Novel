package com.heiheilianzai.app.ui.fragment.boyin;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.boyin.DownMangerPhonicAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.model.event.DownMangerDeleteAllChapterEvent;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

/**
 * 下载缓存 有声管理页面 Fragment
 */
public class DownMangerPhonicFragment extends BaseDownMangerFragment<BoyinInfoBean> {
    private List<BoyinInfoBean> mSelectLists;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DownMangerPhonicAdapter downMangerAdapter = (DownMangerPhonicAdapter) this.downMangerAdapter;
        downMangerAdapter.setmGetSelectItems(new DownMangerPhonicAdapter.GetSelectItems() {
            @Override
            public void getSelectItems(List<BoyinInfoBean> selectLists) {

                if (selectLists != null) {
                    if (selectLists.size() > 0) {
                        setLlDeleteView(true);
                        mSelectLists = selectLists;
                        if (selectLists.size() == baseList.size()) {
                            setLlSelectAllView(true);
                        } else {
                            setLlSelectAllView(false);
                        }
                    } else {
                        setLlDeleteView(false);
                    }
                }
            }
        });
        mLlDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelectLists != null && mSelectLists.size() > 0) {
                    for (int i = 0; i < mSelectLists.size(); i++) {
                        BoyinInfoBean boyinInfoBean = mSelectLists.get(i);
                        List<BoyinChapterBean> boyinChapterList = LitePal.where("nid = ?", String.valueOf(boyinInfoBean.getId())).find(BoyinChapterBean.class);
                        for (BoyinChapterBean comicChapter : boyinChapterList) {//删除本地保存的所有有声音频
                            if (!StringUtils.isEmpty(comicChapter.getSavePath())) {
                                FileManager.deleteFile(comicChapter.getSavePath());
                            }
                        }
                        LitePal.deleteAll(BoyinChapterBean.class, "nid = ?", String.valueOf(boyinInfoBean.getId()));//删除有声章节数据
                        LitePal.deleteAll(BoyinInfoBean.class, "nid = ?", String.valueOf(boyinInfoBean.getId()));//删除有声小说数据
                        baseList.remove(boyinInfoBean);
                        MyToash.ToashSuccess(activity, activity.getResources().getString(R.string.string_delete_success));
                    }
                    downMangerAdapter.notifyDataSetChanged();
                    if (baseList.isEmpty()) {
                        fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
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
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAeAdapter() {
        return new DownMangerPhonicAdapter(activity, baseList, fragment_bookshelf_noresult);
    }

    @Override
    protected void getIsEditOpen(boolean isEditOpen) {
        DownMangerPhonicAdapter downMangerAdapter = (DownMangerPhonicAdapter) this.downMangerAdapter;
        downMangerAdapter.setmIsEditOpen(isEditOpen);
    }

    @Override
    protected void setClickSeleckAll(boolean mIsSelectAll) {
        DownMangerPhonicAdapter downMangerAdapter = (DownMangerPhonicAdapter) this.downMangerAdapter;
        downMangerAdapter.setmIsSelectAll(mIsSelectAll);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshProcess(DownMangerDeleteAllChapterEvent downoption) {
        if (downoption.type == PhONIC_SON_TYPE) {
            initData();
        }
    }
}