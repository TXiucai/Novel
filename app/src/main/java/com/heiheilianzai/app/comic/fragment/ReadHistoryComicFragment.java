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
public class ReadHistoryComicFragment extends BaseButterKnifeFragment {
    @BindView(R2.id.fragment_readhistory_readhistory)
    public XRecyclerView fragment_option_listview;
    @BindView(R2.id.fragment_readhistory_pop)
    public LinearLayout fragment_readhistory_pop;
    @BindView(R2.id.fragment_bookshelf_go_shelf)
    public Button fragment_bookshelf_go_shelf;
    @BindView(R2.id.fragment_bookshelf_text)
    public TextView fragment_bookshelf_text;
    Gson gson = new Gson();
    ReadHistoryRecyclerViewComicAdapter optionAdapter;
    List<ComicReadHistory.ReadHistoryComic> optionBeenList = new ArrayList<>();
    LinearLayout temphead;
    LayoutInflater layoutInflater;
    int LoadingListener = 0;
    public static final int RefarchrequestCodee = 890;
    int RefarchrequestCode = 891;//登录成功返回刷新数
    int current_page = 1;//页数
    int size;//总条数

    @Override
    public int initContentView() {
        return R.layout.fragment_readhistory;
    }

    public interface GetPosition {
        void getPosition(int falg, ComicReadHistory.ReadHistoryComic readHistoryBook, int position);
    }

    GetPosition getPosition = new GetPosition() {
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
                                delad(readHistoryBook.log_id);
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        layoutInflater = LayoutInflater.from(activity);
        temphead = (LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_option_listview.setLayoutManager(layoutManager);
        fragment_option_listview.addHeaderView(temphead);
        fragment_bookshelf_go_shelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isLogin(activity)) {
                    EventBus.getDefault().post(new ToStore(2));
                    activity.finish();
                } else {
                    activity.startActivityForResult(new Intent(activity, LoginActivity.class), RefarchrequestCodee);
                }
            }
        });
        fragment_option_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                LoadingListener = -1;
                current_page = 1;
                initData();
            }

            @Override
            public void onLoadMore() {
                LoadingListener = 1;
                initData();
            }
        });
        optionAdapter = new ReadHistoryRecyclerViewComicAdapter(activity, optionBeenList, getPosition);
        fragment_option_listview.setAdapter(optionAdapter);
        initdata();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RefarchrequestCode) {
            current_page = 1;
            initdata();
        }
    }

    public void initdata() {
        if (Utils.isLogin(activity)) {
            fragment_option_listview.setVisibility(View.VISIBLE);
            fragment_readhistory_pop.setVisibility(View.GONE);
            initData();
        } else {
            fragment_option_listview.setVisibility(View.GONE);
            fragment_readhistory_pop.setVisibility(View.VISIBLE);
            fragment_bookshelf_text.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_loginlook));
            fragment_bookshelf_go_shelf.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_gologin));
        }
    }

    public void initData() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("page_num", current_page + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_read_log, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        handData(result);
                        try {
                            if (LoadingListener == -1) {
                                fragment_option_listview.refreshComplete();
                            } else if (LoadingListener == 1) {
                                fragment_option_listview.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        try {
                            if (LoadingListener == -1) {
                                fragment_option_listview.refreshComplete();
                            } else if (LoadingListener == 1) {
                                fragment_option_listview.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }
                    }
                }
        );
    }

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
                if (optionBeenList.isEmpty()) {
                    fragment_option_listview.setVisibility(View.GONE);
                    fragment_readhistory_pop.setVisibility(View.VISIBLE);
                    fragment_bookshelf_text.setText("还未阅读任何漫画");
                    fragment_bookshelf_go_shelf.setText(LanguageUtil.getString(activity, R.string.noverfragment_gostore));
                } else {
                    fragment_option_listview.setVisibility(View.VISIBLE);
                    fragment_readhistory_pop.setVisibility(View.GONE);
                }
                if (current_page > 1) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 删除漫画阅读记录
     *
     * @param log_id
     */
    public void delad(String log_id) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("log_id", log_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_read_log_del, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
