package com.heiheilianzai.app.ui.fragment.cartoon;

import android.content.Intent;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.adapter.cartoon.ReadHistoryRecycleViewCartoonAdapter;
import com.heiheilianzai.app.base.BaseReadHistoryFragment;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.model.cartoon.CartoonChapter;
import com.heiheilianzai.app.model.cartoon.CartoonReadHistory;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import java.util.List;

public class ReadHistoryCartoonFragment extends BaseReadHistoryFragment<CartoonChapter> {
    private List<CartoonChapter> mSelectLists;

    @Override
    protected void initData() {
        dataUrl = CartoonConfig.CARTOON_history_log;
        initdata();
    }

    @Override
    protected void initView() {
        super.initView();
        mSonType = COMIC_SON_TYPE;
        optionAdapter = new ReadHistoryRecycleViewCartoonAdapter(activity, optionBeenList, getPosition);
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
                        String video_id = mSelectLists.get(i).getVideo_id();
                        if (i != mSelectLists.size() - 1) {
                            video_id += ",";
                        }
                        mSelectID += video_id;
                    }
                    deleteMoreHistory(mSelectID, CartoonConfig.CARTOON_read_log_del_MORE);
                }
            }
        });
    }

    BaseReadHistoryAdapter.GetPosition getPosition = new BaseReadHistoryAdapter.GetPosition<CartoonChapter>() {
        @Override
        public void getPosition(int falg, CartoonChapter cartoonChapter, int position) {
            Intent intent;
            switch (falg) {
                case 1:
                case 0:
                    intent = CartoonInfoActivity.getHistoryIntent(activity,cartoonChapter);
                    startActivityForResult(intent, RefarchrequestCode);
                    break;
                case 2:
                    GetDialog.IsOperation(activity, LanguageUtil.getString(activity, R.string.ReadHistoryFragment_qurenshanchu), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            if (cartoonChapter.getAd_type() == 0 && Utils.isLogin(activity)) {
                                delad(cartoonChapter.getLog_id(), CartoonConfig.CARTOON_read_log_del);
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
            final CartoonReadHistory optionItem = gson.fromJson(result, CartoonReadHistory.class);
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
