package com.heiheilianzai.app.comic.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.LoginActivity;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.comic.activity.ComicInfoActivity;
import com.heiheilianzai.app.comic.activity.ComicLookActivity;
import com.heiheilianzai.app.comic.adapter.ReadHistoryRecyclerViewComicAdapter;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.comic.been.ComicReadHistory;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.dialog.GetDialog;
import com.heiheilianzai.app.eventbus.ToStore;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.fragment.BaseReadHistoryFragment;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * 阅读历史-漫画
 * Created by scb on 2018/12/21.
 */
public class ReadHistoryComicFragment extends BaseReadHistoryFragment<ComicReadHistory.ReadHistoryComic> {

    @Override
    protected void initData() {
       dataUrl=ComicConfig.COMIC_read_log;
       initdata();
    }

    @Override
    protected void initView() {
        optionAdapter = new ReadHistoryRecyclerViewComicAdapter(activity, optionBeenList, getPosition);
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
                    intent = new Intent(activity, ComicLookActivity.class);
                    intent.putExtra("baseComic", baseComic);
                    intent.putExtra("FORM_READHISTORY", true);
                    startActivityForResult(intent, RefarchrequestCode);
                    break;
                case 0:
                    intent = new Intent(activity, ComicInfoActivity.class);
                    intent.putExtra("comic_id", readHistoryBook.getComic_id());
                    startActivityForResult(intent, RefarchrequestCode);
                    break;
                case 2:
                    GetDialog.IsOperation(activity, LanguageUtil.getString(activity, R.string.ReadHistoryFragment_qurenshanchu), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            if (readHistoryBook.ad_type == 0 && Utils.isLogin(activity)) {
                                delad(readHistoryBook.log_id,ComicConfig.COMIC_read_log_del);
                            }
                            if (optionBeenList != null && optionBeenList.size() > position) {
                                optionBeenList.remove(position);
                            }
                            if (optionAdapter != null) {
                                optionAdapter.notifyDataSetChanged();
                            }
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
