package com.heiheilianzai.app.ui.fragment.book;

import android.widget.BaseAdapter;

import com.heiheilianzai.app.adapter.DownMangerAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.Downoption;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 下载缓存页面 小说
 */
public class DownMangerBookFragment extends BaseDownMangerFragment<Downoption> {

    @Override
    protected void initData() {
        LitePal.where().findAsync(Downoption.class).listen(new FindMultiCallback<Downoption>() {
            @Override
            public void onFinish(List<Downoption> list) {
                List<String> stringList = new ArrayList<>();
                if (list.size() != 0) {
                    Collections.sort(list);//按bookid 排序
                    for (Downoption downoption : list) {
                        if (stringList.contains(downoption.book_id)) {
                            downoption.showHead = false;
                        } else {
                            downoption.showHead = true;
                            stringList.add(downoption.book_id);
                        }
                    }
                }
                refreshData(list);
            }
        });
    }

    @Override
    protected BaseAdapter getAeAdapter() {
        return new DownMangerAdapter(activity, baseList, fragment_bookshelf_noresult);
    }

    @Override
    protected void getIsEditOpen(boolean isEditOpen) {

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshProcess(Downoption downoption) {
        if (!baseList.isEmpty()) {
            for (Downoption downoption1 : baseList) {
                if (downoption1.file_name.equals(downoption.file_name)) {
                    downoption1.down_cunrrent_num = downoption.down_cunrrent_num;
                    downoption1.downoption_size = downoption.downoption_size;
                    downoption1.isdown = downoption.isdown;
                    downMangerAdapter.notifyDataSetChanged();
                    return;
                }
            }
            baseList.add(downoption);
            downMangerAdapter.notifyDataSetChanged();
        } else {
            baseList.add(downoption);
            downMangerAdapter = new DownMangerAdapter(activity, baseList, fragment_bookshelf_noresult);
            activity_downmanger_list.setAdapter(downMangerAdapter);
        }
    }
}