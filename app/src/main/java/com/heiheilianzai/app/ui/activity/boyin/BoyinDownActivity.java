package com.heiheilianzai.app.ui.activity.boyin;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.boyin.BasePhonicDownAdapter;
import com.heiheilianzai.app.adapter.boyin.BoyinDownAdapter;
import com.heiheilianzai.app.adapter.boyin.DownPhonicChapterDeleteAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.base.BaseDownMangerFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.DownloadBoyinService;
import com.heiheilianzai.app.constant.BoyinConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.model.event.BoyinDownloadEvent;
import com.heiheilianzai.app.model.event.DownMangerDeleteAllChapterEvent;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 有声小说下载页面 有声小说下载章节管理
 */
public class BoyinDownActivity extends BaseButterKnifeActivity {
    private static final String CHAPTER_SEPARATOR = ",";
    @BindView(R.id.titlebar_text)
    public TextView mTvTitlebar;
    @BindView(R.id.activity_comicdown_choose_count)
    public TextView mTvChooseCount;
    @BindView(R.id.fragment_comicinfo_mulu_zhuangtai)
    public TextView mTvMuluStatu;
    @BindView(R.id.activity_comicdown_down)
    public TextView mTvBoyinDown;
    @BindView(R.id.activity_comicdown_gridview)
    public GridView mBoyinGv;
    @BindView(R.id.fragment_bookshelf_noresult)
    public LinearLayout mLlNoResult;
    @BindView(R.id.fragment_comicinfo_mulu_xu)
    public TextView mTvMuluXu;
    @BindView(R.id.fragment_comicinfo_mulu_xu_img)
    public ImageView mIvXu;
    @BindView(R.id.fragment_comicinfo_mulu_layout)
    public RelativeLayout mRvMulu;
    boolean mShunxu;
    List<BoyinChapterBean> mBoyinChapterBeans;//boyin  章节list
    List<BoyinChapterBean> mBoyinChapterDownBeans;//boyin  章节list
    BasePhonicDownAdapter mBoyDownAdapter;
    Gson gson = new Gson();
    String mPhonicId;//有声小说Id
    private BoyinInfoBean mBoyInfoBean;//播音小说详情
    boolean mIsDown = true; //true-有声小说下载 false 有声下载到本地的章节删除
    boolean mIsDeleteItem;//是否删除过有声小说章节

    @Override
    public int initContentView() {
        return R.layout.activity_comicdown;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        startDownloadService();
        mIsDown = getIntent().getBooleanExtra("isDown", true);
        mBoyinChapterBeans = new ArrayList<>();
        initView();
        initData();
    }

    private void initView() {
        if (mIsDown) {
            mBoyDownAdapter = new BoyinDownAdapter(activity, mBoyinChapterBeans, mTvChooseCount, mTvBoyinDown);
            mTvBoyinDown.setText(LanguageUtil.getString(activity, R.string.BookInfoActivity_downn));
        } else {
            mBoyDownAdapter = new DownPhonicChapterDeleteAdapter(activity, mBoyinChapterBeans, mTvChooseCount, mTvBoyinDown);
            mTvBoyinDown.setText(LanguageUtil.getString(activity, R.string.BookInfoActivity_down_delete));
        }
        mTvBoyinDown.setClickable(false);
        mTvTitlebar.setText(mIsDown ? LanguageUtil.getString(activity, R.string.BoyinDownActivity_title) : LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger));
        mBoyinGv.setAdapter(mBoyDownAdapter);
    }

    private void initData() {
        mPhonicId = String.valueOf(getIntent().getIntExtra("nid", 0));
        if (mIsDown) {
            getDownBoyinChapter(mPhonicId);
        } else {
            localData(mPhonicId);
        }
    }

    @OnClick(value = {R.id.titlebar_back, R.id.activity_comicdown_quanxuan, R.id.activity_comicdown_down, R.id.fragment_comicinfo_mulu_layout})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.activity_comicdown_quanxuan:
                if (mBoyDownAdapter != null) {
                    mBoyDownAdapter.selectAll(mIsDown, mPhonicId);
                }
                break;
            case R.id.fragment_comicinfo_mulu_layout:
                mShunxu = !mShunxu;
                if (!mShunxu) {
                    mTvMuluXu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_zhengxu));
                    mIvXu.setImageResource(R.mipmap.positive_order);
                } else {
                    mTvMuluXu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoxu));
                    mIvXu.setImageResource(R.mipmap.reverse_order);
                }
                Collections.reverse(mBoyinChapterBeans);
                if (mBoyDownAdapter != null) {
                    mBoyDownAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.activity_comicdown_down:
                if (mIsDown) {
                    downInfo();
                } else {
                    deleteDownInfo();
                }
                break;
        }
    }

    /**
     * 下载选中有声小说章节
     */
    private void downInfo() {
        judgmentService();
        if (mBoyDownAdapter != null) {
            if (mBoyinChapterDownBeans == null) {
                mBoyinChapterDownBeans = new ArrayList<>();
            }
            mBoyinChapterDownBeans.clear();
            for (BoyinChapterBean boyinChapterBean : mBoyDownAdapter.mChooseBoyinList) {
                if (!boyinChapterBean.getUrl().isEmpty() && (boyinChapterBean.getDownloadStatus() == 0 || boyinChapterBean.getDownloadStatus() == 3)) {
                    boyinChapterBean.setSavePath(FileManager.getBoyinSDCardRoot() + File.separator + boyinChapterBean.getNid() + File.separator + boyinChapterBean.getChapter_id() + "_" + boyinChapterBean.getChapter_name() + ".mp3");
                    mBoyinChapterDownBeans.add(boyinChapterBean);
                }
            }
            if (mBoyinChapterDownBeans.size() > 0) {
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.BookInfoActivity_down_adddown));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        EventBus.getDefault().post(new BoyinDownloadEvent(BoyinDownloadEvent.EventTag.START_DOWNLOAD, mBoyinChapterDownBeans));
                    }
                }, 500);
            } else {
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.BookInfoActivity_down_noadddown));
            }
            MyToash.Log("FileDownloader", "下载章节数量：" + mBoyinChapterDownBeans.size());
        }
    }

    /**
     * 开启下载服务器
     */
    private void judgmentService() {
        if (!DownloadBoyinService.isServiceRunning(this, "DownloadBoyinService")) {
            Intent downloadVideoServiceIntent = new Intent(this, DownloadBoyinService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(downloadVideoServiceIntent);
            } else {
                startService(downloadVideoServiceIntent);
            }
        }
    }

    /**
     * 删除下载章节
     */
    private void deleteDownInfo() {
        if (mBoyDownAdapter.mChooseBoyinList != null && mBoyDownAdapter.mChooseBoyinList.size() > 0) {
            mIsDeleteItem = true;
            for (BoyinChapterBean info : mBoyDownAdapter.mChooseBoyinList) {
                LitePal.deleteAll(BoyinChapterBean.class, "nid = ? and chapter_id = ?", String.valueOf(info.getNid()), String.valueOf(info.getChapter_id()));//删除有声章节数据
            }
            mBoyDownAdapter.mChooseBoyinList.clear();
            initData();
        }
    }

    /**
     * 只加载本地数据用于删除
     *
     * @param nid
     */
    private void localData(String nid) {
        mBoyinChapterBeans.clear();
        List<BoyinChapterBean> boyinChapterBeanList = LitePal.where("nid = ?", nid).find(BoyinChapterBean.class);
        if (boyinChapterBeanList != null && boyinChapterBeanList.size() > 0) {
            mBoyinChapterBeans.addAll(boyinChapterBeanList);
            List<BoyinInfoBean> infoBeanList = LitePal.where("nid != ?", nid).find(BoyinInfoBean.class);
            if (infoBeanList != null && infoBeanList.size() > 0) {
                mBoyInfoBean = infoBeanList.get(0);
                setCatalogStatus(mBoyInfoBean);
            }
            ContentValues values1 = new ContentValues();
            values1.put("down_chapter", mBoyinChapterBeans.size());
            LitePal.updateAll(BoyinInfoBean.class, values1, "nid = ?", nid);
        } else {
            LitePal.deleteAll(BoyinInfoBean.class, "nid = ?", String.valueOf(nid));
            finish();
            return;
        }
        mBoyDownAdapter.notifyDataSetChanged();
    }

    /**
     * 从服务器获取有声下载章节
     */
    private void getDownBoyinChapter(String nid) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("nid", nid);
        params.putExtraParams("mobile", App.getUserInfoItem(activity).getMobile());
        params.putExtraParams("user_source", BuildConfig.app_source_boyin);
        params.putExtraParams("hhlz_uid", String.valueOf(App.getUserInfoItem(activity).getUid()));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BoyinConfig.DOWN_BOYIN, json, true, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject = null;
                try {
                    jsonObject = new JSONObject(response);
                    JsonParser jsonParser = new JsonParser();
                    JSONObject data = jsonObject.getJSONObject("data");
                    JsonArray jsonElements = jsonParser.parse(data.getString("chapter_list")).getAsJsonArray();//获取JsonArray对象
                    String novel_info = data.getString("novel_info");
                    mBoyInfoBean = gson.fromJson(novel_info, BoyinInfoBean.class);
                    for (JsonElement jsonElement : jsonElements) {
                        BoyinChapterBean boyinChapterBean = gson.fromJson(jsonElement, BoyinChapterBean.class);
                        mBoyinChapterBeans.add(boyinChapterBean);
                    }
                    mBoyDownAdapter.notifyDataSetChanged();
                    getSplChapter(nid);
                    saveAllChapter();
                    setCatalogStatus(mBoyInfoBean);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onErrorResponse(String ex) {
            }
        });
    }

    private void notifyChapter(int chapter) {
        //从H5交互过来单章默认选中
        if (chapter == -1) {
            mBoyDownAdapter.selectAll(mIsDown, mPhonicId);
        } else {
            for (BoyinChapterBean boyinChapterBean : mBoyinChapterBeans) {
                if (boyinChapterBean.getChapter_id() == chapter) {
                    mBoyDownAdapter.mChooseBoyinList.add(boyinChapterBean);
                    break;
                }
            }
            mBoyDownAdapter.refreshBtn(1);
            mBoyDownAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 设置当前小说目录状态
     *
     * @param boyInfoBean
     */
    private void setCatalogStatus(BoyinInfoBean boyInfoBean) {
        int update_status = boyInfoBean.getUpdate_status();
        int numbers = boyInfoBean.getNumbers();
        String format = String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_total), numbers);
        mTvMuluStatu.setText(update_status == 1 ? "连载中" + format : "已完结" + format);
    }

    private void saveAllChapter() {
        try {
            for (int i = 0; i < mBoyinChapterBeans.size(); i++) {
                BoyinChapterBean boyinChapterBean = mBoyinChapterBeans.get(i);
                int chapter_id = boyinChapterBean.getChapter_id();
                if (!LitePal.isExist(BoyinChapterBean.class, "chapter_id = ?", String.valueOf(chapter_id))) {
                    boyinChapterBean.save();
                    MyToash.Log("FileDownloader", "保存章节" + chapter_id);
                } else {
                    MyToash.Log("FileDownloader", "不保存章节" + chapter_id);
                }

            }
            if (!LitePal.isExist(BoyinInfoBean.class, "nid = ?", String.valueOf(mBoyInfoBean.getId()))) {
                mBoyInfoBean.save();
                MyToash.Log("FileDownloader", "保存小说" + mBoyInfoBean.getId());
            } else {
                MyToash.Log("FileDownloader", "不保存小说" + mBoyInfoBean.getId());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从数据库获取数据
     *
     * @param nid
     */
    private void getSplChapter(String nid) {
        LitePal.where("nid = ?", nid).findAsync(BoyinChapterBean.class).listen(new FindMultiCallback<BoyinChapterBean>() {
            @Override
            public void onFinish(List<BoyinChapterBean> list) {
                int min = Math.min(list.size(), mBoyinChapterBeans.size());
                for (int i = 0; i < min; i++) {
                    BoyinChapterBean boyinChapterLocalBean = list.get(i);
                    for (int j = 0; j < min; j++) {
                        BoyinChapterBean boyinChapterNetBean = mBoyinChapterBeans.get(j);
                        if (boyinChapterLocalBean.getChapter_id() == boyinChapterNetBean.getChapter_id()) {
                            boyinChapterNetBean.setDownloadStatus(boyinChapterLocalBean.getDownloadStatus());
                            break;
                        }
                    }
                }
                mBoyDownAdapter.notifyDataSetChanged();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void downBoyinEvenbus(BoyinDownloadEvent downloadEvent) {
        MyToash.Log("FileDownloader", "更新状态：" + downloadEvent.getTag());
        if (downloadEvent.getTag() == BoyinDownloadEvent.EventTag.COMPLETE_DOWNLOAD) {
            if (downloadEvent.getDownComplete() == mBoyinChapterDownBeans.size()) {
                MyToash.ToashSuccess(activity, String.format(LanguageUtil.getString(activity, R.string.BookInfoActivity_down_downcompleteSize), downloadEvent.getDownComplete()));
            }
        } else if (downloadEvent.getTag() == BoyinDownloadEvent.EventTag.ERROR) {
            MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.BookInfoActivity_down_downeror));
        } else if (downloadEvent.getTag() == BoyinDownloadEvent.EventTag.INTERRUPT) {
            MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.BookInfoActivity_down_downinterrupt));
        }
        BoyinChapterBean boyinChapterBean = downloadEvent.getDownloadTaskList().get(0);
        setAdapterItemData(boyinChapterBean, mBoyDownAdapter.mChooseBoyinList);
        setAdapterItemData(boyinChapterBean, mBoyDownAdapter.comicDownOptionList);
        mBoyDownAdapter.notifyDataSetChanged();
    }

    void setAdapterItemData(BoyinChapterBean boyinChapterBean, List<BoyinChapterBean> list) {
        int position = list.indexOf(boyinChapterBean);
        if (position >= 0 && position < list.size()) {
            list.set(position, boyinChapterBean);
        }
    }

    /**
     * 启动下载服务
     */
    public void startDownloadService() {
        Intent downloadVideoServiceIntent;
        downloadVideoServiceIntent = new Intent(this, DownloadBoyinService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(downloadVideoServiceIntent);
        } else {
            startService(downloadVideoServiceIntent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        judgmentService();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mIsDeleteItem) {
            EventBus.getDefault().post(new DownMangerDeleteAllChapterEvent(BaseDownMangerFragment.PhONIC_SON_TYPE));
        }
    }
}