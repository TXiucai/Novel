package com.heiheilianzai.app.ui.fragment.book;

import android.content.Intent;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.adapter.book.ReadHistoryRecyclerViewAdapter;
import com.heiheilianzai.app.base.BaseReadHistoryFragment;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.book.ReadHistory;
import com.heiheilianzai.app.model.event.RefreshReadHistory;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.List;

/**
 * 阅读历史-小说
 * Created by scb on 2018/12/21.
 */
public class ReadHistoryBookFragment extends BaseReadHistoryFragment<ReadHistory.ReadHistoryBook> {
    private List<ReadHistory.ReadHistoryBook> mSelectLists;

    @Override
    protected void initData() {
        dataUrl = BookConfig.read_log;
        initdata();
    }

    @Override
    protected void initView() {
        mSonType = BOOK_SON_TYPE;
        optionAdapter = new ReadHistoryRecyclerViewAdapter(activity, optionBeenList, getPosition);
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
                        String book_id = mSelectLists.get(i).getBook_id();
                        if (i != mSelectLists.size() - 1) {
                            book_id += ",";
                        }
                        mSelectID += book_id;
                    }
                    deleteMoreHistory(mSelectID, BookConfig.del_read_log_MORE);
                }
            }
        });
        super.initView();
    }

    BaseReadHistoryAdapter.GetPosition getPosition = new BaseReadHistoryAdapter.GetPosition<ReadHistory.ReadHistoryBook>() {
        @Override
        public void getPosition(int falg, ReadHistory.ReadHistoryBook readHistoryBook, int position) {
            Intent intent;
            switch (falg) {
                case 1:
                    BaseBook basebooks = LitePal.where("book_id = ?", readHistoryBook.getBook_id()).findFirst(BaseBook.class);
                    if (basebooks != null) {
                        basebooks.setCurrent_chapter_id(readHistoryBook.chapter_id);
                        if (activity != null) {
                            activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_read_history));
                            ChapterManager.getInstance(activity).openBook(basebooks, readHistoryBook.getBook_id(), readHistoryBook.chapter_id);
                        }
                    } else {
                        BaseBook baseBook;
                        baseBook = new BaseBook();
                        baseBook.setBook_id(readHistoryBook.getBook_id());
                        baseBook.setName(readHistoryBook.getName());
                        baseBook.setCover(readHistoryBook.getCover());
                        baseBook.setCurrent_chapter_id(readHistoryBook.chapter_id);
                        baseBook.setDescription(readHistoryBook.getDescription());
                        baseBook.setAddBookSelf(0);
                        baseBook.setCurrent_chapter_id(readHistoryBook.chapter_id);
                        baseBook.saveIsexist(0);
                        ChapterManager.getInstance(activity).openBook(baseBook, readHistoryBook.getBook_id(), readHistoryBook.chapter_id);
                    }
                    break;
                case 0:
                    activity.startActivity(BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_read_history), readHistoryBook.getBook_id()));
                    break;
                case 2:
                    GetDialog.IsOperation(activity, LanguageUtil.getString(activity, R.string.ReadHistoryFragment_qurenshanchu), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            if (readHistoryBook.ad_type == 0) {
                                delad(readHistoryBook.log_id, BookConfig.del_read_log);
                            }
                            deladItemRefresh(position);
                        }
                    });
                    break;
            }
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshReadHistory refreshMine) {
        if (refreshMine.isPRODUCT()) {
            current_page = 1;
            initdata();
        }
    }

    @Override
    public void handData(String result) {
        try {
            final ReadHistory optionItem = gson.fromJson(result, ReadHistory.class);
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
