package com.heiheilianzai.app.ui.fragment.comic;

import android.content.Intent;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.adapter.comic.ReadHistoryRecyclerViewComicAdapter;
import com.heiheilianzai.app.base.BaseReadHistoryFragment;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicReadHistory;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import java.util.List;

/**
 * 阅读历史-漫画
 * Created by scb on 2018/12/21.
 */
public class ReadHistoryComicFragment extends BaseReadHistoryFragment<ComicReadHistory.ReadHistoryComic> {
    private List<ComicReadHistory.ReadHistoryComic> mSelectLists;
    @Override
    protected void initData() {
        dataUrl = ComicConfig.COMIC_read_log;
        initdata();
    }

    @Override
    protected void initView() {
        mSonType = COMIC_SON_TYPE;
        optionAdapter = new ReadHistoryRecyclerViewComicAdapter(activity, optionBeenList, getPosition);
        optionAdapter.setmGetSelectItems(new BaseReadHistoryAdapter.getSelectItems() {
            @Override
            public void getSelectItems(List selectLists) {
                if (selectLists != null) {
                    if (selectLists.size() > 0) {
                        setLlDeleteView(true);
                        mSelectLists = selectLists;
                        if (selectLists.size() == optionBeenList.size()) {
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
                        String book_id = mSelectLists.get(i).getComic_id();
                        if (i != mSelectLists.size() - 1) {
                            book_id += ",";
                        }
                        mSelectID += book_id;
                    }
                    deleteMoreHistory(mSelectID, ComicConfig.COMIC_read_log_del_MORE);
                }
            }
        });
        super.initView();
    }

    BaseReadHistoryAdapter.GetPosition getPosition = new BaseReadHistoryAdapter.GetPosition<ComicReadHistory.ReadHistoryComic>() {
        @Override
        public void getPosition(int falg, ComicReadHistory.ReadHistoryComic readHistoryBook, int position) {
            Intent intent;
            switch (falg) {
                case 1:
                    BaseComic baseComic = new BaseComic();
                    baseComic.setComic_id(readHistoryBook.comic_id);
                    baseComic.setCurrent_chapter_id(readHistoryBook.chapter_id);
                    baseComic.setCurrent_display_order(readHistoryBook.chapter_index);
                    baseComic.setTotal_chapters(readHistoryBook.total_chapters);
                    baseComic.setName(readHistoryBook.name);
                    baseComic.setDescription(readHistoryBook.description);
                    intent = ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(activity, R.string.refer_page_read_history));
                    intent.putExtra(ComicLookActivity.FORM_READHISTORY_EXT_KAY, true);
                    startActivityForResult(intent, RefarchrequestCode);
                    break;
                case 0:
                    intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_read_history), readHistoryBook.getComic_id());
                    startActivityForResult(intent, RefarchrequestCode);
                    break;
                case 2:
                    GetDialog.IsOperation(activity, LanguageUtil.getString(activity, R.string.ReadHistoryFragment_qurenshanchu), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            if (readHistoryBook.ad_type == 0 && Utils.isLogin(activity)) {
                                delad(readHistoryBook.log_id, ComicConfig.COMIC_read_log_del);
                            }
                            deladItemRefresh(position);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public void handData(String result) {
        try {
            final ComicReadHistory optionItem = gson.fromJson(result, ComicReadHistory.class);
            int total_page = optionItem.total_page;
            int optionItem_list_size = optionItem.list.size();
            if (current_page <= total_page && optionItem_list_size != 0) {
                if (current_page == 1) {
                    optionBeenList.clear();
                    optionBeenList.addAll(optionItem.list);
                    size = optionItem_list_size;
                    optionAdapter.notifyDataSetChanged();
                } else {
                    MyToash.Log("optionBeenList44", current_page + "   " + optionBeenList.size() + "");
                    optionBeenList.addAll(optionItem.list);
                    int t = size + optionItem_list_size;
                    optionAdapter.notifyItemRangeInserted(size + 2, optionItem_list_size);
                    size = t;
                }
                MyToash.Log("optionBeenList55", current_page + "   " + optionBeenList.size() + "");
                current_page = optionItem.current_page;
                ++current_page;
            } else {
                setNullDataView();
            }
        } catch (Exception e) {
        }
    }
}
