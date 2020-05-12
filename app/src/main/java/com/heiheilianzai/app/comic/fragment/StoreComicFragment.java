package com.heiheilianzai.app.comic.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.BaseOptionActivity;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.adapter.EntranceAdapter;
import com.heiheilianzai.app.adapter.ReaderBaseAdapter;
import com.heiheilianzai.app.adapter.StoreComicAdapter;
import com.heiheilianzai.app.banner.ConvenientBanner;
import com.heiheilianzai.app.bean.BannerItemStore;
import com.heiheilianzai.app.bean.EntranceItem;
import com.heiheilianzai.app.comic.activity.ComicInfoActivity;
import com.heiheilianzai.app.comic.been.StroreComicLable;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.comic.eventbus.StoreEventbus;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.heiheilianzai.app.view.PullToRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.config.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.config.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.config.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.config.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.config.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.config.ReaderConfig.WANBEN;


/**
 * Created by scb on 2018/6/9.
 */


public class StoreComicFragment extends BaseButterKnifeFragment {

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store;
    }

    public RelativeLayout fragment_newbookself_top;
    public static boolean postAsyncHttpEngine_ing;//正在刷新数据
    LayoutInflater layoutInflater;
    public int WIDTH, WIDTHH, WIDTH_MAIN_AD, HEIGHT, H55, H30;
    int currentSex = 1;
    StroeNewFragmentComic.Hot_word hot_word;

    @SuppressLint("ValidFragment")
    public StoreComicFragment(RelativeLayout fragment_newbookself_top, StroeNewFragmentComic.Hot_word hot_word) {
        this.fragment_newbookself_top = fragment_newbookself_top;

        this.hot_word = hot_word;
    }

    public StoreComicFragment() {

    }

    Gson gson = new Gson();
    /**
     * 最外层布局
     */
    @BindView(R2.id.fragment_store_comic_content_commend)
    public LinearLayout mContainerMale;
    /**
     * banner控件-男频
     */
    @BindView(R2.id.store_banner_male)
    public ConvenientBanner<BannerItemStore> mStoreBannerMale;
    //   @BindView(R2.id.store_banner_line)
    // public ImageView store_banner_line;

    /**
     * 男频banner下方的gridview
     */
    @BindView(R2.id.store_entrance_grid_male)
    public AdaptionGridView mEntranceGridMale;
    /**
     * 男频scrollview
     */
    @BindView(R2.id.scrollViewMale)
    public ObservableScrollView mScrollViewMale;
    @BindView(R2.id.refreshLayoutMale)
    PullToRefreshLayout malePullLayout;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  EventBus.getDefault().register(this);
        initViews();
        postAsyncHttpEngine(currentSex);
    }

    public void initViews() {
        layoutInflater = LayoutInflater.from(activity);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTHH = WIDTH;
        WIDTH_MAIN_AD = WIDTH;
        H30 = WIDTH / 5;
        int dp10 = ImageUtil.dp2px(activity, 10);
        WIDTH = WIDTH - dp10 * 2;
        H55 = ImageUtil.dp2px(activity, 55);
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mContainerMale.getLayoutParams();
        layoutParams.leftMargin = dp10;
        layoutParams.rightMargin = dp10;
        mContainerMale.setLayoutParams(layoutParams);

        HEIGHT = ScreenSizeUtils.getInstance(activity).getScreenHeight();
        malePullLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                String StoreBoo = (currentSex == 1) ? "StoreComicMan" : "StoreComicWoMan";
                MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_home_stock, StoreBoo, new MainHttpTask.GetHttpData() {
                    @Override
                    public void getHttpData(String result) {
                        if (result != null) {
                            initInfo(result);
                        }

                        if (pullToRefreshLayout != null) {
                            pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
                        }
                    }
                });
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {

            }
        });

        //男频下拉监听,改变channelbar的整体透明度
        malePullLayout.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onPulling(float y) {
/*                float ratio = Math.min(Math.max(y, 0), REFRESH_HEIGHT) / REFRESH_HEIGHT;
                fragment_newbookself_top.setAlpha(1 - ratio);*/
            }

        });

        mScrollViewMale.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                EventBus.getDefault().post(new StoreEventbus(true, y));
            }
        });


    }


    public void postAsyncHttpEngine(int flag) {
        if (flag == 1) {
            MainHttpTask.getInstance().getResultString(activity, "StoreComicMan", new MainHttpTask.GetHttpData() {
                @Override
                public void getHttpData(String result) {
                    initInfo(result);
                }
            });
        } else {
            MainHttpTask.getInstance().getResultString(activity, "StoreComicWoMan", new MainHttpTask.GetHttpData() {
                @Override
                public void getHttpData(String result) {
                    initInfo(result);
                }
            });
        }
    }

    public void initInfo(String json) {
        initEntranceGrid();
        try {
            JSONObject jsonObject = new JSONObject(json);

            //1.初始化男频banner控件数据
            ConvenientBanner.initbanner(activity, gson, jsonObject.getString("banner"), mStoreBannerMale, 5000, 1);
            initWaterfall(jsonObject.getString("label"));
            if (hot_word != null) {
                hot_word.hot_word(gson.fromJson(jsonObject.getString("hot_word"), String[].class));
                hot_word = null;
            }
            postAsyncHttpEngine_ing = false;
        } catch (Exception e) {
            e.printStackTrace();
            MyToash.LogE("nan_result", e.getMessage());
        }
    }


    /**
     *
     */
    public void initEntranceGrid() {
        List<EntranceItem> mEntranceItemListMale = new ArrayList<>();
        if (ReaderConfig.USE_PAY) {
            EntranceItem entranceItem1 = new EntranceItem();
            entranceItem1.setName(LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
            entranceItem1.setResId(R.mipmap.comic_classification);

            EntranceItem entranceItem2 = new EntranceItem();
            entranceItem2.setName(LanguageUtil.getString(activity, R.string.storeFragment_paihang));
            entranceItem2.setResId(R.mipmap.comic_ranking);

            EntranceItem entranceItem3 = new EntranceItem();
            entranceItem3.setName(LanguageUtil.getString(activity, R.string.storeFragment_baoyue));
            entranceItem3.setResId(R.mipmap.comic_member);

            EntranceItem entranceItem4 = new EntranceItem();
            entranceItem4.setName(LanguageUtil.getString(activity, R.string.storeFragment_wanben));
            entranceItem4.setResId(R.mipmap.comic_finished);

            EntranceItem entranceItem5 = new EntranceItem();
            entranceItem5.setName(LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
            entranceItem5.setResId(R.mipmap.comic_limitfree);

            mEntranceItemListMale.add(entranceItem5);
            mEntranceItemListMale.add(entranceItem4);
            mEntranceItemListMale.add(entranceItem1);
            mEntranceItemListMale.add(entranceItem2);
            if (!BuildConfig.free_charge) {
                mEntranceItemListMale.add(entranceItem3);
            } else {
                mEntranceGridMale.setNumColumns(4);
            }
        } else {
            EntranceItem entranceItem5 = new EntranceItem();
            entranceItem5.setName(LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
            entranceItem5.setResId(R.mipmap.comic_limitfree);

            mEntranceItemListMale.add(entranceItem5);


            EntranceItem entranceItem1 = new EntranceItem();
            entranceItem1.setName(LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
            entranceItem1.setResId(R.mipmap.entrance1);

            EntranceItem entranceItem2 = new EntranceItem();
            entranceItem2.setName(LanguageUtil.getString(activity, R.string.storeFragment_paihang));
            entranceItem2.setResId(R.mipmap.entrance2);
            EntranceItem entranceItem4 = new EntranceItem();
            entranceItem4.setName(LanguageUtil.getString(activity, R.string.storeFragment_wanben));
            entranceItem4.setResId(R.mipmap.entrance4);
            mEntranceItemListMale.add(entranceItem1);
            mEntranceItemListMale.add(entranceItem2);
            mEntranceItemListMale.add(entranceItem4);
            mEntranceGridMale.setNumColumns(4);
        }
        ReaderBaseAdapter entranceAdapter = new EntranceAdapter(activity, mEntranceItemListMale, mEntranceItemListMale.size());
        mEntranceGridMale.setAdapter(entranceAdapter);

        mEntranceGridMale.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(activity, BaseOptionActivity.class);
                intent.putExtra("PRODUCT", false);


                if (!ReaderConfig.USE_PAY) {
                    if (position == 0) {
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
                    } else if (position == 1) {//分类
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
                    } else if (position == 2) {
                        //排行
                        intent.putExtra("OPTION", PAIHANGINSEX);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_paihang));
                    } else if (position == 3) {
                        //完本
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_wanben));
                    }
                } else {
                    MyToash.Log("position", position + "");
                    if (position == 0) {
                        intent.putExtra("OPTION", MIANFEI);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
                    } else if (position == 1) {
                        intent.putExtra("OPTION", WANBEN);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_wanben));
                    } else if (position == 2) {
                        intent.putExtra("OPTION", SHUKU);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
                    } else if (position == 3) {
                        intent.putExtra("OPTION", PAIHANGINSEX);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.storeFragment_paihang));
                    } else if (position == 4) {
                        intent.putExtra("OPTION", BAOYUE);
                        intent.putExtra("title", LanguageUtil.getString(activity, R.string.BaoyueActivity_title));
                    }
                }

                startActivity(intent);
            }
        });
    }

    public void initWaterfall(String jsonObject) {
        mContainerMale.removeAllViews();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(jsonObject).getAsJsonArray();//获取JsonArray对象
        for (JsonElement jsonElement : jsonElements) {
            StroreComicLable stroreComicLable = gson.fromJson(jsonElement, StroreComicLable.class);//解析
            if (stroreComicLable.ad_type != 0) {
                FrameLayout list_ad_view = (FrameLayout) layoutInflater.inflate(R.layout.list_ad_view, null, false);
                FrameLayout list_ad_view_layout = list_ad_view.findViewById(R.id.list_ad_view_layout);
                list_ad_view_layout.setVisibility(View.VISIBLE);
                ImageView list_ad_view_img = list_ad_view.findViewById(R.id.list_ad_view_img);
                ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
                layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
                layoutParams.height = layoutParams.width / 3;
                list_ad_view_img.setLayoutParams(layoutParams);

                MyPicasso.GlideImageNoSize(activity, stroreComicLable.ad_image, list_ad_view_img);


                list_ad_view_img.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, WebViewActivity.class);
                        intent.putExtra("url", stroreComicLable.ad_skip_url);
                        intent.putExtra("title", stroreComicLable.ad_title);
                        intent.putExtra("advert_id", stroreComicLable.advert_id);
                        intent.putExtra("ad_url_type", stroreComicLable.ad_url_type);

                        activity.startActivity(intent);
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 10, 0, 0);
                params.bottomMargin = ImageUtil.dp2px(activity, 20);
                mContainerMale.addView(list_ad_view, params);
                continue;
            }

            List<StroreComicLable.Comic> comicList = stroreComicLable.list;

            LinearLayout type1 = (LinearLayout) layoutInflater.inflate(R.layout.fragment_store_comic_layout, null, false);

            TextView lable = type1.findViewById(R.id.fragment_store_gridview1_text);
            lable.setText(stroreComicLable.label);
            LinearLayout fragment_store_gridview1_more = type1.findViewById(R.id.fragment_store_gridview1_more);
            LinearLayout fragment_store_gridview_huanyihuan = type1.findViewById(R.id.fragment_store_gridview_huanyihuan);
            View fragment_store_gridview1_view1 = type1.findViewById(R.id.fragment_store_gridview1_view1);
            View fragment_store_gridview1_view2 = type1.findViewById(R.id.fragment_store_gridview1_view2);
            View fragment_store_gridview1_view3 = type1.findViewById(R.id.fragment_store_gridview1_view3);
            AdaptionGridViewNoMargin fragment_store_gridview1_gridview = type1.findViewById(R.id.fragment_store_gridview1_gridview);

            AdaptionGridViewNoMargin liem_store_comic_style1_style3 = type1.findViewById(R.id.liem_store_comic_style1_style3);

            if (stroreComicLable.can_refresh.equals("true")) {
                fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postHuanyihuan(stroreComicLable.recommend_id, stroreComicLable.style, fragment_store_gridview1_gridview, liem_store_comic_style1_style3);
                    }
                });
            } else {
                fragment_store_gridview_huanyihuan.setVisibility(View.GONE);
            }
            if (stroreComicLable.can_more.equals("true")) {
                fragment_store_gridview1_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyToash.Log("LOOKMORE", stroreComicLable.recommend_id);
                        try {
                            startActivity(new Intent(activity, BaseOptionActivity.class)
                                    .putExtra("OPTION", LOOKMORE)
                                    .putExtra("PRODUCT", false)
                                    .putExtra("recommend_id", stroreComicLable.recommend_id)
                            );
                        } catch (Exception E) {
                        }

                    }
                });

            } else {
                fragment_store_gridview1_more.setVisibility(View.GONE);
            }
            if (comicList.isEmpty()) {
                fragment_store_gridview1_gridview.setVisibility(View.GONE);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            int ItemHeigth = setItemData(stroreComicLable.style, comicList, fragment_store_gridview1_gridview, liem_store_comic_style1_style3);
            if (comicList.isEmpty()) {
                ItemHeigth = 0;
                fragment_store_gridview1_gridview.setVisibility(View.GONE);
            }
            params.height = ItemHeigth + H55 + ImageUtil.dp2px(activity, 40);
            if (!comicList.isEmpty()) {
                if (stroreComicLable.style == 3) {
                    params.height += H55 + WIDTH * 5 / 9;
                }
            }
            //params.setMargins(0, 20, 0, 0);

            if (!stroreComicLable.can_more.equals("true") && !stroreComicLable.can_refresh.equals("true")) {
                params.height -= H55;
            } else if (!(stroreComicLable.can_more.equals("true") && stroreComicLable.can_refresh.equals("true"))) {
                buttomonlyOne(fragment_store_gridview1_view1, fragment_store_gridview1_view2, fragment_store_gridview1_view3);
            }

            mContainerMale.addView(type1, params);

        }

    }


    private int setItemData(int style, List<StroreComicLable.Comic> comicList, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin liem_store_comic_style1_style3) {

        fragment_store_gridview1_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = new Intent(activity, ComicInfoActivity.class);
                if (style != 3) {
                    intent.putExtra("comic_id", comicList.get(position).comic_id);
                } else {
                    intent.putExtra("comic_id", comicList.get(position + 1).comic_id);
                }
                activity.startActivity(intent);
            }
        });

        int width, height = 80, raw = 2;
        StoreComicAdapter storeComicAdapter = null;
        switch (style) {
            case 1:

                width = WIDTH / 2;
                height = width * 2 / 3;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size1 = Math.min(4, comicList.size());
                raw = (int) (Math.ceil(size1 / 2d));
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size1), activity, style, width, height);
                // params.height = height * raw + height1 * raw + height1;
                break;
            case 2:
                double size = Math.min(6, comicList.size());
                raw = (int) (Math.ceil(size / 3d));
                width = WIDTH / 3;
                height = width * 4 / 3;
                fragment_store_gridview1_gridview.setNumColumns(3);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size), activity, style, width, height);
                //  params.height = height * raw + height1 * raw + height1;
                break;
            case 3:
                if (!comicList.isEmpty()) {
                    liem_store_comic_style1_style3.setVisibility(View.VISIBLE);
                    StoreComicAdapter storeComicAdapter3 = new StoreComicAdapter(comicList.subList(0, 1), activity, style, WIDTH, WIDTH * 5 / 9);
                    liem_store_comic_style1_style3.setAdapter(storeComicAdapter3);
                    liem_store_comic_style1_style3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Intent intent = new Intent(activity, ComicInfoActivity.class);
                            intent.putExtra("comic_id", comicList.get(0).comic_id);
                            activity.startActivity(intent);
                        }
                    });
                    width = WIDTH / 3;
                    height = width * 4 / 3;
                    raw = 1;
                    fragment_store_gridview1_gridview.setNumColumns(3);
                    storeComicAdapter = new StoreComicAdapter(comicList.subList(1, Math.min(4, comicList.size())), activity, 2, width, height);
                }
                break;


        }


        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_store_gridview1_gridview.getLayoutParams();
        fragment_store_gridview1_gridview.setAdapter(storeComicAdapter);

        layoutParams.height = (height + H55) * raw;


        layoutParams.height = (height + H55) * raw;
        fragment_store_gridview1_gridview.setLayoutParams(layoutParams);


        return layoutParams.height;
    }

    private void buttomonlyOne(View fragment_store_gridview1_view1, View fragment_store_gridview1_view2, View fragment_store_gridview1_view3) {
        fragment_store_gridview1_view2.setVisibility(View.GONE);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_store_gridview1_view1.getLayoutParams();
        layoutParams.width = H30;
        fragment_store_gridview1_view1.setLayoutParams(layoutParams);
        LinearLayout.LayoutParams layoutParams3 = (LinearLayout.LayoutParams) fragment_store_gridview1_view3.getLayoutParams();
        layoutParams3.width = H30;
        fragment_store_gridview1_view1.setLayoutParams(layoutParams3);
    }

    public void postHuanyihuan(String recommend_id, int style, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin type1) {

        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_id", recommend_id + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_home_refresh, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        List<StroreComicLable.Comic> comicList = null;
                        try {
                            comicList = new Gson().fromJson(new JSONObject(result).getString("list"), new TypeToken<List<StroreComicLable.Comic>>() {
                            }.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // List<StroreComicLable.Comic> comicList = (gson.fromJson(result, Huanyihuan.class)).list;
                        if (comicList != null && !comicList.isEmpty()) {
                            setItemData(style, comicList, fragment_store_gridview1_gridview, type1);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );


    }
}
