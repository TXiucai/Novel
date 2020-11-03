package com.heiheilianzai.app.ui.fragment.comic;

import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.DownMangerComicAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.DownMangerDeleteAllChapterEvent;

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
    @BindView(R.id.activity_downmanger_list)
    public ListView activity_downmanger_list;
    @BindView(R.id.fragment_bookshelf_noresult)
    public LinearLayout fragment_bookshelf_noresult;

    public static boolean DownMangerComicFragment;

    @Override
    public int initContentView() {
        return R.layout.fragment_downmanger;
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
    protected BaseAdapter getAeAdapter() {
        return new DownMangerComicAdapter(activity, baseList, fragment_bookshelf_noresult);
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