package com.heiheilianzai.app.base;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.ui.activity.LoginActivity;
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
 * 阅读历史记录基类
 */
public abstract class BaseReadHistoryFragment<T> extends BaseButterKnifeFragment {
    @BindView(R.id.fragment_readhistory_readhistory)
    public XRecyclerView fragment_option_listview;
    @BindView(R.id.fragment_readhistory_pop)
    public LinearLayout fragment_readhistory_pop;
    @BindView(R.id.fragment_bookshelf_go_shelf)
    public Button fragment_bookshelf_go_shelf;
    @BindView(R.id.fragment_bookshelf_text)
    public TextView fragment_bookshelf_text;
    public Gson gson = new Gson();
    public BaseReadHistoryAdapter<T> optionAdapter;
    public List<T> optionBeenList = new ArrayList<>();
    public LinearLayout temphead;
    LayoutInflater layoutInflater;
    public int LoadingListener = 0;
    public static final int RefarchrequestCodee = 890;
    public int RefarchrequestCode = 891;//登录成功返回刷新数
    public int current_page = 1;//页数
    public int size;//总条数
    public String dataUrl;
    @Override
    public int initContentView() {
        return R.layout.fragment_readhistory;
    }

    @Override
    protected void initView() {
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
                initData(dataUrl);
            }

            @Override
            public void onLoadMore() {
                LoadingListener = 1;
                initData(dataUrl);
            }
        });
        fragment_option_listview.setAdapter(optionAdapter);
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
            initData(dataUrl);
        } else {
            fragment_bookshelf_text.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_loginlook));
            fragment_bookshelf_go_shelf.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_gologin));
        }
        setBeenListEmptyView(!Utils.isLogin(activity));
    }

    public void initData(String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("page_num", current_page + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        handData(result);
                        restoreRecyclerView();
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        restoreRecyclerView();
                    }
                }
        );
    }

    private void restoreRecyclerView(){
        try {
            if (LoadingListener == -1) {
                fragment_option_listview.refreshComplete();
            } else if (LoadingListener == 1) {
                fragment_option_listview.loadMoreComplete();
            }
        } catch (Exception e) {
        }
    }

    public abstract void handData(String result);

    /**
     * 删除漫画阅读记录
     *
     * @param log_id
     */
    public void delad(String log_id, String url) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("log_id", log_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    protected void setNullDataView(){
        if (optionBeenList.isEmpty()) {
            fragment_bookshelf_text.setText("还未阅读任何漫画");
            fragment_bookshelf_go_shelf.setText(LanguageUtil.getString(activity, R.string.noverfragment_gostore));
        }
        setBeenListEmptyView(optionBeenList.isEmpty());
        if (current_page > 1) {
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
        }
    }

    private void setBeenListEmptyView(boolean isEmpty){
        fragment_option_listview.setVisibility(isEmpty?View.GONE:View.VISIBLE);
        fragment_readhistory_pop.setVisibility(isEmpty?View.VISIBLE:View.GONE);
    }
}
