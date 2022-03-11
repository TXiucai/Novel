package com.heiheilianzai.app.ui.fragment.book;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.DownMangerAdapter;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.model.Downoption;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

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
    private List<Downoption> mSelectLists;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        DownMangerAdapter downMangerAdapter = (DownMangerAdapter) this.downMangerAdapter;
        downMangerAdapter.setmGetSelectItems(new DownMangerAdapter.GetSelectItems() {
            @Override
            public void getSelectItems(List<Downoption> selectLists) {
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
                        Downoption downoption = mSelectLists.get(i);
                        LitePal.deleteAll(Downoption.class, "book_id=?", downoption.book_id);
                        ShareUitls.putDown(activity, downoption.book_id, "0-0");
                        List<Downoption> downoptions = new ArrayList<>();
                        for (Downoption downoption1 : baseList) {
                            if (downoption1.book_id.equals(downoption.book_id)) {
                                downoptions.add(downoption1);
                            }
                        }
                        baseList.removeAll(downoptions);
                        MyToash.ToashSuccess(activity, activity.getResources().getString(R.string.string_delete_success));
                    }
                    downMangerAdapter.notifyDataSetChanged();
                    if (baseList.size() == 0) {
                        fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

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
    protected RecyclerView.Adapter<RecyclerView.ViewHolder> getAeAdapter() {
        return new DownMangerAdapter(activity, baseList, fragment_bookshelf_noresult);
    }

    @Override
    protected void getIsEditOpen(boolean isEditOpen) {
        DownMangerAdapter downMangerAdapter = (DownMangerAdapter) this.downMangerAdapter;
        downMangerAdapter.setmIsEditOpen(isEditOpen);
    }

    @Override
    protected void setClickSeleckAll(boolean mIsSelectAll) {
        DownMangerAdapter downMangerAdapter = (DownMangerAdapter) this.downMangerAdapter;
        downMangerAdapter.setmIsSelectAll(mIsSelectAll);
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