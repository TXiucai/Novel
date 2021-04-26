package com.heiheilianzai.app.ui.activity.comic;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicChapterCatalogAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.fragment.comic.ComicinfoMuluFragment;
import com.heiheilianzai.app.utils.DialogComicChapter;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 漫画详情-目录
 * Created by scb on 2018/6/9.
 */
public class ComicinfoMuluActivity extends BaseButterKnifeActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.fragment_comicinfo_mulu_xu_img)
    public ImageView fragment_comicinfo_mulu_xu_img;
    @BindView(R.id.fragment_comicinfo_mulu_list)
    public XRecyclerView fragment_comicinfo_mulu_list;
    @BindView(R.id.fragment_comicinfo_mulu_layout)
    public RelativeLayout fragment_comicinfo_mulu_layout;
    @BindView(R.id.fragment_comicinfo_mulu_zhiding_img)
    public ImageView fragment_comicinfo_mulu_zhiding_img;
    @BindView(R.id.fragment_comicinfo_mulu_zhiding_text)
    public TextView fragment_comicinfo_mulu_zhiding_text;

    String currentChapter_id;
    int Size;
    boolean shunxu, isHttp;
    ComicChapterCatalogAdapter comicChapterCatalogAdapter;
    List<ComicChapter> comicChapterCatalogs;
    boolean orentation;
    private String comic_id;
    private int mPageNum = 1;//页数
    private int orderby = 1;//1 正序 2 倒序
    private int mTotalPage, size;

    @OnClick(value = {R.id.titlebar_back,
            R.id.fragment_comicinfo_mulu_dangqian, R.id.fragment_comicinfo_mulu_zhiding
            , R.id.fragment_comicinfo_mulu_layout})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_comicinfo_mulu_layout:
                shunxu = !shunxu;
                if (!shunxu) {
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.comic_up);
                    orderby = 1;
                    mPageNum = 1;
                } else {
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.comic_down);
                    orderby = 2;
                    mPageNum = 1;
                }
                httpData(activity, comic_id);
                break;
            case R.id.titlebar_back:
                activity.finish();
                break;
            case R.id.fragment_comicinfo_mulu_dangqian:
                fragment_comicinfo_mulu_list.scrollToPosition(comicChapterCatalogAdapter.getCurrentPosition());
                break;
            case R.id.fragment_comicinfo_mulu_zhiding:
                if (orentation) {
                    fragment_comicinfo_mulu_list.scrollToPosition(0);
                } else {
                    fragment_comicinfo_mulu_list.scrollToPosition(comicChapterCatalogs.size());
                }
                break;
        }
    }

    @Override
    public int initContentView() {
        return R.layout.activity_comicinfo_mulu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    public void initViews() {
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_comicinfo_mulu_list.setLayoutManager(layoutManager);
        comicChapterCatalogs = new ArrayList<>();
        Intent intent = getIntent();
        currentChapter_id = intent.getStringExtra("currentChapter_id");
        comic_id = intent.getStringExtra("comic_id");
        //添加滑动监听
        fragment_comicinfo_mulu_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//获取LayoutManager
                if (manager != null && manager instanceof LinearLayoutManager) {
                    //第一个可见的位置
                    int firstPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                    //如果 dx>0 则表示 右滑 ,dx<0 表示 左滑,dy <0 表示 上滑, dy>0 表示下滑
                    if (dy < 0) {
                        //上滑监听
                        orentation = true;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gototop);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoding));
                    } else {
                        //下滑监听
                        orentation = false;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gotobottom);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daodi));
                    }
                }
            }
        });
        fragment_comicinfo_mulu_list.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPageNum = 1;
                httpData(activity, comic_id);
            }

            @Override
            public void onLoadMore() {
                if (mTotalPage >= mPageNum) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                    httpData(activity, comic_id);
                } else {
                    fragment_comicinfo_mulu_list.loadMoreComplete();
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        });
        comicChapterCatalogs =new ArrayList<>();
        comicChapterCatalogAdapter = new ComicChapterCatalogAdapter(false, null, activity, currentChapter_id, comicChapterCatalogs, ImageUtil.dp2px(activity, 96));
        fragment_comicinfo_mulu_list.setAdapter(comicChapterCatalogAdapter);
        httpData(activity,comic_id);
    }

    public void httpData(Activity activity, String comic_id) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("page", "" + mPageNum);
        params.putExtraParams("orderby", "" + orderby);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_catalog, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JsonParser jsonParser = new JsonParser();
                            mTotalPage = jsonObject.getInt("total_page");
                            JsonArray jsonElements = jsonParser.parse(jsonObject.getString("chapter_list")).getAsJsonArray();//获取JsonArray对象
                            if (mPageNum == 1) {
                                comicChapterCatalogs.clear();
                            }
                            for (JsonElement jsonElement : jsonElements) {
                                ComicChapter comicChapter = new Gson().fromJson(jsonElement, ComicChapter.class);
                                comicChapter.comic_id = comic_id;

                                if (App.isVip(activity)) {
                                    if (comicChapter.getAd_image() == null) {
                                        comicChapterCatalogs.add(comicChapter);
                                    }
                                } else {
                                    comicChapterCatalogs.add(comicChapter);
                                }
                            }
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                int comicCatalogsSize = comicChapterCatalogs.size();
                                if (mPageNum == 1) {
                                    size = comicChapterCatalogs.size();
                                    fragment_comicinfo_mulu_list.refreshComplete();
                                    comicChapterCatalogAdapter.notifyDataSetChanged();
                                } else {
                                    int t = size + comicCatalogsSize;
                                    fragment_comicinfo_mulu_list.loadMoreComplete();
                                    comicChapterCatalogAdapter.notifyItemRangeRemoved(size + 2, comicCatalogsSize);
                                    size = t;
                                }
                                mPageNum++;
                            }

                        } catch (Exception E) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        fragment_comicinfo_mulu_list.refreshComplete();
                        if (ex != null && ex.equals("nonet")) {
                            MyToash.Log("nonet", "11");
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
    }
}
