package com.heiheilianzai.app.ui.fragment.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicChapterCatalogAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.comic.ComicinfoMuluBuy;
import com.heiheilianzai.app.ui.activity.comic.ComicActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.heiheilianzai.app.view.OnRcvScrollListener;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindMultiCallback;
import org.litepal.crud.callback.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * ????????????
 * Created by scb on 2018/6/9.
 */

public class ComicinfoMuluFragment extends BaseButterKnifeFragment {
    @Override
    public int initContentView() {
        return R.layout.fragment_comicinfo_mulu;
    }

    BaseComic baseComic;
    String comic_id;
    StroreComicLable.Comic comic;
    boolean shunxu, isHttp;
    @BindView(R.id.fragment_comicinfo_mulu_zhuangtai)
    public TextView fragment_comicinfo_mulu_zhuangtai;
    @BindView(R.id.fragment_comicinfo_mulu_xu)
    public TextView fragment_comicinfo_mulu_xu;
    @BindView(R.id.fragment_comicinfo_mulu_xu_img)
    public ImageView fragment_comicinfo_mulu_xu_img;
    @BindView(R.id.fragment_comicinfo_mulu_list)
    public XRecyclerView fragment_comicinfo_mulu_list;
    @BindView(R.id.fragment_comicinfo_mulu_layout)
    public RelativeLayout fragment_comicinfo_mulu_layout;
    ComicActivity.MuluLorded muluLorded;
    int Current_chapter_displayOrder;
    public ImageView fragment_comicinfo_mulu_zhiding_img;
    public TextView fragment_comicinfo_mulu_zhiding_text;
    boolean orentation;//fasle ??????????????????
    int H96;
    Gson gson = new Gson();
    boolean isLoadingData = false;//?????????????????????
    private int mPageNum = 1;//??????
    private int orderby = 1;//1 ?????? 2 ??????
    boolean isLoadOverHintShow = false;//?????????????????????????????????
    public ComicChapterCatalogAdapter comicChapterCatalogAdapter;
    List<ComicChapter> comicChapterCatalogs;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshMine refreshMine) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_down_option, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JsonParser jsonParser = new JsonParser();
                            JsonArray jsonElements = jsonParser.parse(jsonObject.getString("down_list")).getAsJsonArray();//??????JsonArray??????
                            int i = 0;
                            for (JsonElement jsonElement : jsonElements) {
                                ComicChapter comicChapter = gson.fromJson(jsonElement, ComicChapter.class);
                                comicChapterCatalogs.get(i).is_preview = comicChapter.is_preview;
                                ++i;
                            }
                            comicChapterCatalogAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @SuppressLint("ValidFragment")
    public ComicinfoMuluFragment(ComicActivity.MuluLorded muluLorded, ImageView fragment_comicinfo_mulu_zhiding_img, TextView fragment_comicinfo_mulu_zhiding_text) {
        this.muluLorded = muluLorded;
        this.fragment_comicinfo_mulu_zhiding_img = fragment_comicinfo_mulu_zhiding_img;
        this.fragment_comicinfo_mulu_zhiding_text = fragment_comicinfo_mulu_zhiding_text;
    }

    public ComicinfoMuluFragment() {
    }

    public void OnclickDangqian(boolean dangqian) {
        if (dangqian) {
            try {
                fragment_comicinfo_mulu_list.scrollToPosition(Current_chapter_displayOrder);
            } catch (Exception e) {
            }
        } else {
            if (orentation) {
                fragment_comicinfo_mulu_list.scrollToPosition(0);
            } else {
                if (comicChapterCatalogs != null) {
                    fragment_comicinfo_mulu_list.scrollToPosition(comicChapterCatalogs.size());
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        EventBus.getDefault().register(this);
        H96 = ImageUtil.dp2px(activity, 96);
        initViews();
        fragment_comicinfo_mulu_list.setLoadingMoreEnabled(false);
        fragment_comicinfo_mulu_list.setPullRefreshEnabled(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(ComicinfoMuluBuy comicinfoMuluBuy) {
        try {
            int i = 0;
            for (String id : comicinfoMuluBuy.ids) {
                if (i != 0) {
                    ++i;
                    comicChapterCatalogs.get(i).is_preview = 0;
                } else {
                    FLAG:
                    for (ComicChapter comicChapter : comicChapterCatalogs) {
                        if (id.equals(comicChapter.chapter_id)) {
                            i = comicChapter.display_order;
                            comicChapter.is_preview = 0;
                            break FLAG;
                        }
                    }
                }
            }
            comicChapterCatalogAdapter.notifyDataSetChanged();
        } catch (Exception e) {
        }
    }

    public void initViews() {
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_comicinfo_mulu_list.setLayoutManager(layoutManager);
        comicChapterCatalogs = new ArrayList<>();
        fragment_comicinfo_mulu_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                shunxu = !shunxu;
                if (!shunxu) {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_zhengxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.positive_order);
                    orderby = 1;
                } else {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.reverse_order);
                    orderby = 2;
                }
                if(comicChapterCatalogAdapter!=null){//????????????????????????Adapter????????????
                    comicChapterCatalogAdapter.setShunxu(shunxu);
                }
                mPageNum = 1;
                httpData();
            }
        });
        fragment_comicinfo_mulu_list.addOnScrollListener(new OnRcvScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//??????LayoutManager
                if (manager != null && manager instanceof LinearLayoutManager) {
                    int firstPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                    if (dy < 0) {
                        //????????????
                        orentation = true;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gototop);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoding));
                    } else {
                        //????????????
                        orentation = false;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gotobottom);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daodi));
                    }
                }
            }

            @Override
            public void onBottom() {
                MyToash.toashSuccessLoadMore(getContext(), isLoadingData, isLoadOverHintShow, new MyToash.MyToashLoadMoreListener() {
                    @Override
                    public void onLoadingData() {
                        httpData();
                    }

                    @Override
                    public void onState(boolean isLoadingData, boolean isLoadOver) {
                        ComicinfoMuluFragment.this.isLoadingData = isLoadingData;
                        isLoadOverHintShow = isLoadOver;
                    }
                });
            }
        });
    }

    public void senddata(BaseComic baseComic, StroreComicLable.Comic comic) {
        try {
            if (activity == null) {
                activity = getActivity();
            }
            this.baseComic = baseComic;
            comic_id = baseComic.getComic_id();
            if (comic != null) {
                this.comic = comic;
                if (comic.is_finish == 1) {
                    fragment_comicinfo_mulu_zhuangtai.setText(comic.flag);
                } else {
                    fragment_comicinfo_mulu_zhuangtai.setText(comic.flag);
                }
            }
            if(fragment_comicinfo_mulu_layout!=null){
                fragment_comicinfo_mulu_layout.setVisibility(View.VISIBLE);
            }
            comicChapterCatalogAdapter = new ComicChapterCatalogAdapter(true, baseComic, activity, baseComic.getCurrent_chapter_id(), comicChapterCatalogs, H96);
            fragment_comicinfo_mulu_list.setAdapter(comicChapterCatalogAdapter);
            httpData();
        } catch (Exception e) {
        }
    }

    public void httpData() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("page", "" + mPageNum);
        params.putExtraParams("orderby", "" + orderby);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_catalog, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            if (mPageNum == 1) {
                                comicChapterCatalogs.clear();
                            }
                            if (isLoadingData) {
                                mPageNum += 1;
                            }
                            JSONObject jsonObject = new JSONObject(result);
                            JsonParser jsonParser = new JsonParser();
                            JsonArray jsonElements = jsonParser.parse(jsonObject.getString("chapter_list")).getAsJsonArray();//??????JsonArray??????

                            for (JsonElement jsonElement : jsonElements) {
                                ComicChapter comicChapter = gson.fromJson(jsonElement, ComicChapter.class);
                                comicChapter.comic_id = comic_id;
                                comicChapterCatalogs.add(comicChapter);
                            }
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                isLoadingData = comicChapterCatalogs.size() == jsonObject.getInt("total_chapter");
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                            muluLorded.getReadChapterItem(comicChapterCatalogs);
                        } catch (Exception E) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        if (ex != null && ex.equals("nonet")) {
                            MyToash.Log("nonet", "11");
                            if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                            muluLorded.getReadChapterItem(comicChapterCatalogs);
                        }
                    }
                }
        );
    }

    public interface GetCOMIC_catalogList {
        void GetCOMIC_catalogList(List<ComicChapter> comicChapterList);
    }

    public static void GetCOMIC_catalog(Activity activity, String comic_id, GetCOMIC_catalogList getCOMIC_catalogList) {
        LitePal.where("comic_id = ?", comic_id).findAsync(ComicChapter.class).listen(new FindMultiCallback<ComicChapter>() {
            @Override
            public void onFinish(List<ComicChapter> comicChapterList) {
                ReaderParams params = new ReaderParams(activity);
                params.putExtraParams("comic_id", comic_id);
                String json = params.generateParamsJson();
                HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_catalog, json, true, new HttpUtils.ResponseListener() {
                            @Override
                            public void onResponse(final String result) {
                                boolean isEmpty = comicChapterList.isEmpty();
                                List<ComicChapter> httpcomicChapterList = new ArrayList<>();
                                try {
                                    JSONObject jsonObject = new JSONObject(result);
                                    JsonParser jsonParser = new JsonParser();
                                    JsonArray jsonElements = jsonParser.parse(jsonObject.getString("chapter_list")).getAsJsonArray();//??????JsonArray??????
                                    for (JsonElement jsonElement : jsonElements) {
                                        ComicChapter chapterItem = new Gson().fromJson(jsonElement, ComicChapter.class);
                                        chapterItem.comic_id = comic_id;
                                        if (!isEmpty) {
                                            flag:
                                            for (ComicChapter chapterItem1 : comicChapterList) {
                                                if (chapterItem.equals(chapterItem1)) {//???????????????????????? ?????????????????? ??????????????????????????? ??????????????????????????????
                                                    chapterItem.setCurrent_read_img_image_id(chapterItem1.getCurrent_read_img_image_id());
                                                    chapterItem.setCurrent_read_img_order(chapterItem1.getCurrent_read_img_order());
                                                    chapterItem.IsRead = chapterItem1.IsRead;
                                                    chapterItem.setImagesText(chapterItem1.ImagesText);
                                                    chapterItem.setId(chapterItem1.getId());
                                                    break flag;//???????????????
                                                }
                                            }
                                        }
                                        httpcomicChapterList.add(chapterItem);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                if (isEmpty) {
                                    LitePal.saveAllAsync(httpcomicChapterList).listen(new SaveCallback() {
                                        @Override
                                        public void onFinish(boolean success) {
                                            getCOMIC_catalogList.GetCOMIC_catalogList(httpcomicChapterList);
                                        }
                                    });
                                } else {
                                    getCOMIC_catalogList.GetCOMIC_catalogList(httpcomicChapterList);
                                }
                            }

                            @Override
                            public void onErrorResponse(String ex) {
                                if (comicChapterList.isEmpty()) {
                                    getCOMIC_catalogList.GetCOMIC_catalogList(null);
                                } else {
                                    getCOMIC_catalogList.GetCOMIC_catalogList(comicChapterList);
                                }
                            }
                        }
                );
            }
        });
    }
}
