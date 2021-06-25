package com.heiheilianzai.app.ui.fragment.comic;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.DownMangerComicAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.event.DownMangerDeleteAllChapterEvent;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.List;

import butterknife.BindView;

/**
 * 下载缓存页面 漫画
 */
public class DownMangerComicFragment extends BaseDownMangerFragment<BaseComic> {
    public static boolean DownMangerComicFragment;
    private List<BaseComic> mSelectLists;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DownMangerComicAdapter downMangerAdapter = (DownMangerComicAdapter) this.downMangerAdapter;
        downMangerAdapter.setmGetSelectItems(new DownMangerComicAdapter.GetSelectItems() {
            @Override
            public void getSelectItems(List<BaseComic> selectLists) {
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
                        BaseComic baseComic = mSelectLists.get(i);
                        ContentValues values1 = new ContentValues();
                        values1.put("down_chapters", 0);
                        LitePal.update(BaseComic.class, values1, baseComic.getId());
                        List<ComicChapter> comicChapterList = LitePal.where().find(ComicChapter.class);
                        for (ComicChapter comicChapter : comicChapterList) {
                            ShareUitls.putComicDownStatus(activity, comicChapter.chapter_id, 0);
                        }
                        String localPath = FileManager.getManhuaSDCardRoot().concat(baseComic.getComic_id());
                        FileManager.deleteFile(localPath);
                        baseList.remove(baseComic);
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
        DownMangerComicFragment = true;
        LitePal.where("down_chapters != ?", "0").findAsync(BaseComic.class).listen(new FindMultiCallback<BaseComic>() {
            @Override
            public void onFinish(List<BaseComic> list) {
                refreshData(list);
            }
        });
    }

    @Override
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAeAdapter() {
        return new DownMangerComicAdapter(activity, baseList, fragment_bookshelf_noresult);
    }

    @Override
    protected void getIsEditOpen(boolean isEditOpen) {
        DownMangerComicAdapter aeAdapter = (DownMangerComicAdapter) this.downMangerAdapter;
        aeAdapter.setmIsEditOpen(isEditOpen);
    }

    @Override
    protected void setClickSeleckAll(boolean mIsSelectAll) {
        DownMangerComicAdapter aeAdapter = (DownMangerComicAdapter)this.downMangerAdapter;
        aeAdapter.setmIsSelectAll(mIsSelectAll);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshProcess(BaseComic baseComic) {
        if (!baseList.isEmpty()) {
            for (BaseComic baseComic1 : baseList) {
                if (baseComic1.getComic_id().equals(baseComic.getComic_id())) {
                    baseComic1.setCurrent_chapter_id(baseComic.getCurrent_chapter_id());
                    baseComic1.setCurrent_display_order(baseComic.getCurrent_display_order());
                    baseComic1.setDown_chapters(baseComic.getDown_chapters());
                    baseComic1.setTotal_chapters(baseComic.getTotal_chapters());
                    downMangerAdapter.notifyDataSetChanged();
                    return;
                }
            }
        } else {
            baseList.add(baseComic);
            downMangerAdapter = new DownMangerComicAdapter(activity, baseList, fragment_bookshelf_noresult);
            activity_downmanger_list.setAdapter(downMangerAdapter);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshProcess(DownMangerDeleteAllChapterEvent downoption) {
        if (downoption.type == COMIC_SON_TYPE) {
            initData();
        }
    }

    @Override
    public void onDestroy() {
        DownMangerComicFragment = false;
        super.onDestroy();
    }
}