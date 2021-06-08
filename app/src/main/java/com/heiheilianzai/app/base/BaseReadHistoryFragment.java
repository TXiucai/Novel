package com.heiheilianzai.app.base;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.BoyinConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.EditEvent;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.LinearLayoutManager;

import butterknife.BindView;

/**
 * 阅读历史记录基类
 */
public abstract class BaseReadHistoryFragment<T> extends BaseButterKnifeFragment {
    public static final int BOOK_SON_TYPE = 1;//小说
    public static final int COMIC_SON_TYPE = 2;//漫画
    public static final int PhONIC_SON_TYPE = 3;//有声

    @BindView(R.id.fragment_readhistory_readhistory)
    public XRecyclerView fragment_option_listview;
    @BindView(R.id.fragment_readhistory_pop)
    public LinearLayout fragment_readhistory_pop;
    @BindView(R.id.fragment_bookshelf_go_shelf)
    public Button fragment_bookshelf_go_shelf;
    @BindView(R.id.fragment_bookshelf_text)
    public TextView fragment_bookshelf_text;
    @BindView(R.id.ll_edit)
    public LinearLayout mLlEdit;
    @BindView(R.id.img_select)
    public ImageView mImgSelect;
    @BindView(R.id.tx_select)
    public TextView mTxSelect;
    @BindView(R.id.img_delete)
    public ImageView mImgDelete;
    @BindView(R.id.tx_delete)
    public TextView mTxDelete;
    @BindView(R.id.ll_select)
    public LinearLayout mLlSelect;
    @BindView(R.id.ll_delete)
    public LinearLayout mLlDelete;

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
    public String mSelectID = "";
    public int mSonType;//1小说 2漫画 3有声
    public boolean mIsSelectAll = false;
    private boolean mIsEditOpen = false;

    @Override
    public int initContentView() {
        return R.layout.fragment_readhistory;
    }

    @Override
    protected void initView() {
        layoutInflater = LayoutInflater.from(activity);
        EventBus.getDefault().register(this);
        temphead = (LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_option_listview.setLayoutManager(layoutManager);
        fragment_option_listview.addHeaderView(temphead);
        mLlSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mIsSelectAll = !mIsSelectAll;
                optionAdapter.setmSelectAll(mIsSelectAll);
                setLlSelectAllView(mIsSelectAll);
                setLlDeleteView(mIsSelectAll);
            }
        });
        fragment_bookshelf_go_shelf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Utils.isLogin(activity)) {
                    EventBus.getDefault().post(new ToStore(mSonType));
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

    public void setLlSelectAllView(boolean isSelectAll) {
        if (isSelectAll) {
            mImgSelect.setImageDrawable(activity.getDrawable(R.mipmap.check_all_no));
            mTxSelect.setText(activity.getResources().getString(R.string.string_cancel_all));
        } else {
            mImgSelect.setImageDrawable(activity.getDrawable(R.mipmap.check_all_yes));
            mTxSelect.setText(activity.getResources().getString(R.string.string_all_select));
        }
    }

    public void setLlDeleteView(boolean isDelete) {
        if (isDelete) {
            mImgDelete.setImageDrawable(activity.getDrawable(R.mipmap.delet_yes));
            mTxDelete.setTextColor(activity.getResources().getColor(R.color.color_3b3b3b));
            mLlDelete.setClickable(true);
        } else {
            mImgDelete.setImageDrawable(activity.getDrawable(R.mipmap.delete_no));
            mTxDelete.setTextColor(activity.getResources().getColor(R.color.color_9a9a9a));
            mLlDelete.setClickable(false);
        }
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
        if (BoyinConfig.PHONIC_AUDIO_READ_LOG.equals(url)) {// 有声阅读历史
            params.putExtraParams("mobile", App.getUserInfoItem(activity).getMobile());
            params.putExtraParams("user_source", BuildConfig.app_source_boyin);
        }
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

    private void restoreRecyclerView() {
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
     * 删除漫画阅读记录(小说，漫画)
     *
     * @param log_id 作品ID
     */
    public void delad(String log_id, String url) {
        delad(log_id, null, null, url);
    }

    /**
     * 删除漫画阅读记录(有声)
     *
     * @param nid        作品ID
     * @param chapter_id 章节ID
     */

    public void delad(String nid, String chapter_id, String url) {
        delad(null, nid, chapter_id, url);
    }

    public void delad(String log_id, String nid, String chapter_id, String url) {
        ReaderParams params = new ReaderParams(activity);
        if (BoyinConfig.PHONIC_REMOVE_AUDIO_READ_LOG.equals(url)) {//有声删除接口
            params.putExtraParams("mobile", App.getUserInfoItem(activity).getMobile());
            params.putExtraParams("user_source", BuildConfig.app_source_boyin);
            params.putExtraParams("nid", nid);
            params.putExtraParams("chapter_id", chapter_id);
        } else {//小说 漫画
            params.putExtraParams("log_id", log_id);
        }
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

    public void deleteMoreHistory(String log_id, String url) {
        ReaderParams params = new ReaderParams(activity);
        if (BoyinConfig.PHONIC_REMOVE_AUDIO_READ_LOG_MORE.equals(url)) { //有声删除接口多个
            params.putExtraParams("mobile", App.getUserInfoItem(activity).getMobile());
            params.putExtraParams("user_source", BuildConfig.app_source_boyin);
            params.putExtraParams("nid", log_id);
        } else if (BookConfig.del_read_log_MORE.equals(url)) { //小说
            params.putExtraParams("book_id", log_id);
        } else { //漫画
            params.putExtraParams("comic_id", log_id);
        }
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        mSelectID = "";
                        MyToash.ToashSuccess(activity, activity.getResources().getString(R.string.string_delete_success));
                        current_page = 1;
                        initData(dataUrl);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }


    protected void setNullDataView() {
        if (optionBeenList.isEmpty()) {
            fragment_bookshelf_text.setText("还未阅读任何" + getTitle());
            fragment_bookshelf_go_shelf.setText(LanguageUtil.getString(activity, R.string.noverfragment_gostore));
        }
        setBeenListEmptyView(optionBeenList.isEmpty());
        if (current_page > 1) {
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
        }
    }

    private void setBeenListEmptyView(boolean isEmpty) {
        fragment_option_listview.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        fragment_readhistory_pop.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        mLlEdit.setVisibility(!isEmpty && mIsEditOpen ? View.VISIBLE : View.GONE);
    }

    /**
     * 删除 item 后刷新页面  (不重新拉数据，只是本地数据操作)
     *
     * @param position
     */
    protected void deladItemRefresh(int position) {
        if (optionBeenList != null && optionBeenList.size() > position) {
            optionBeenList.remove(position);
            if (optionAdapter != null) {
                optionAdapter.notifyDataSetChanged();
            }
            if (optionBeenList.size() == 0) {
                setNullDataView();
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void getIsEditOpen(EditEvent editEvent) {
        mIsEditOpen = editEvent.isEditOpen();
        mLlEdit.setVisibility(mIsEditOpen ? View.VISIBLE : View.GONE);
        optionAdapter.setmIsEditOpen(mIsEditOpen);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 根据type 获取  小说 漫画 有声
     */
    private String getTitle() {
        int stringId = R.string.noverfragment_xiaoshuo;
        switch (mSonType) {
            case BOOK_SON_TYPE:
                stringId = R.string.noverfragment_xiaoshuo;
                break;
            case COMIC_SON_TYPE:
                stringId = R.string.noverfragment_manhua;
                break;
            case PhONIC_SON_TYPE:
                stringId = R.string.MainActivity_boyin;
                break;
            default:
                break;
        }
        return LanguageUtil.getString(activity, stringId);
    }
}