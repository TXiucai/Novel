package com.heiheilianzai.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicVChapterCatalogAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogComicChapter {
    Gson gson = new Gson();
    private int mPageNum = 1;//页数
    private int orderby = 1;//1 正序 2 倒序
    private int mTotalPage, size;
    public ComicVChapterCatalogAdapter comicChapterCatalogAdapter;
    List<ComicChapter> comicChapterCatalogs;

    @SuppressLint("UseCompatLoadingForDrawables")
    public Dialog getDialogVipPop(Activity activity, BaseComic baseComic) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_comic_chapter, null);
        Dialog popupWindow = new Dialog(activity, R.style.userInfo_avatar);
        Window window = popupWindow.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = activity.getWindowManager().getDefaultDisplay().getWidth();
        popupWindow.getWindow().setAttributes(attributes);
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        VipHolder vipHolder = new VipHolder(view);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        vipHolder.ryChapter.setLayoutManager(layoutManager);
        comicChapterCatalogs = new ArrayList<>();
        comicChapterCatalogAdapter = new ComicVChapterCatalogAdapter(baseComic, activity, comicChapterCatalogs);
        vipHolder.ryChapter.setAdapter(comicChapterCatalogAdapter);
        httpData(activity, baseComic.getComic_id(), vipHolder);
        if (orderby == 1) {
            vipHolder.imgSequence.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_up));
        } else {
            vipHolder.imgSequence.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_down));
        }
        vipHolder.imgSequence.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (orderby == 1) {
                    vipHolder.imgSequence.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_down));
                    orderby = 2;
                    mPageNum = 1;
                } else {
                    vipHolder.imgSequence.setImageDrawable(activity.getResources().getDrawable(R.mipmap.comic_up));
                    orderby = 1;
                    mPageNum = 1;
                }
                httpData(activity, baseComic.getComic_id(), vipHolder);
            }
        });
        vipHolder.ryChapter.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                mPageNum = 1;
                httpData(activity, baseComic.getComic_id(), vipHolder);
            }

            @Override
            public void onLoadMore() {
                if (mTotalPage >= mPageNum) {
                    httpData(activity, baseComic.getComic_id(), vipHolder);
                } else {
                    vipHolder.ryChapter.loadMoreComplete();
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                }
            }
        });

        vipHolder.imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    private void initChapterAd(String result) {
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(result);
            JsonParser jsonParser = new JsonParser();
            String is_limited_free = jsonObject.getString("is_limited_free");
            JsonArray jsonElements = jsonParser.parse(jsonObject.getString("chapter_list")).getAsJsonArray();//获取JsonArray对象
            ArrayList<ComicChapter> comicChapters = new ArrayList<>();
            for (JsonElement jsonElement : jsonElements) {
                ComicChapter comicChapter = new Gson().fromJson(jsonElement, ComicChapter.class);
                comicChapter.setIs_limited_free(is_limited_free);
                comicChapters.add(comicChapter);
            }
            if (ReaderConfig.CHAPTER_COMIC_AD != null) {
                int size = comicChapters.size();
                int count = size % 5;
                int adNum = count == 0 ? size / 5 : size / 5 + 1;
                for (int i = 0; i < adNum; i++) {
                    if (size > 5 && i < adNum - 1) {
                        comicChapters.add((i + 1) * 5 + i, ReaderConfig.CHAPTER_COMIC_AD);
                    } else {
                        comicChapters.add(ReaderConfig.CHAPTER_COMIC_AD);
                    }
                }
            }
            comicChapterCatalogs.addAll(comicChapters);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void httpData(Activity activity, String comic_id, VipHolder vipHolder) {
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
                            mTotalPage = jsonObject.getInt("total_page");
                            if (mPageNum == 1) {
                                comicChapterCatalogs.clear();
                            }
                            initChapterAd(result);
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                int comicCatalogsSize = comicChapterCatalogs.size();
                                if (mPageNum == 1) {
                                    size = comicChapterCatalogs.size();
                                    vipHolder.ryChapter.refreshComplete();
                                    comicChapterCatalogAdapter.notifyDataSetChanged();
                                } else {
                                    int t = size + comicCatalogsSize;
                                    vipHolder.ryChapter.loadMoreComplete();
                                    comicChapterCatalogAdapter.notifyItemChanged(size + 2, comicCatalogsSize);
                                    size = t;
                                }
                                mPageNum++;
                            }

                        } catch (Exception E) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        if (mPageNum == 1) {
                            vipHolder.ryChapter.refreshComplete();
                        } else {
                            vipHolder.ryChapter.loadMoreComplete();
                        }
                        if (ex != null && ex.equals("nonet")) {
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
        );
    }

    class VipHolder {

        @BindView(R.id.fragment_comicinfo_mulu_xu)
        public ImageView imgSequence;
        @BindView(R.id.fragment_comicinfo_mulu_xu_img)
        public ImageView imgClose;
        @BindView(R.id.fragment_comicinfo_mulu_list)
        public XRecyclerView ryChapter;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
