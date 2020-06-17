package com.heiheilianzai.app.book.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
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
import com.heiheilianzai.app.activity.BookInfoActivity;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.adapter.EntranceAdapter;
import com.heiheilianzai.app.adapter.ReaderBaseAdapter;
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.banner.ConvenientBanner;
import com.heiheilianzai.app.bean.BannerItemStore;
import com.heiheilianzai.app.bean.EntranceItem;
import com.heiheilianzai.app.book.been.StoreEventbusBook;
import com.heiheilianzai.app.book.been.StroreBookcLable;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.BuyLoginSuccess;
import com.heiheilianzai.app.fragment.BaseButterKnifeFragment;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.heiheilianzai.app.view.PullToRefreshLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.book.config.BookConfig.book_refresh;
import static com.heiheilianzai.app.config.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.config.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.config.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.config.ReaderConfig.PAIHANGINSEX;
import static com.heiheilianzai.app.config.ReaderConfig.REFRESH_HEIGHT;
import static com.heiheilianzai.app.config.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.config.ReaderConfig.WANBEN;


/**
 * 首页小说
 * Created by scb on 2018/6/9.
 */
public class StoreBookFragment extends BaseButterKnifeFragment {

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store;
    }

    public int WIDTH, WIDTHH, WIDTH_MAIN_AD, WIDTHV, HEIGHT, HEIGHTV, HorizontalSpacing, H100, H50, H20, H30;

    Gson gson = new Gson();
    public RelativeLayout fragment_newbookself_top;
    public boolean postAsyncHttpEngine_ing;//正在刷新数据
    LayoutInflater layoutInflater;
    StroeNewFragmentBook.Hot_word_Book hot_word;

    @SuppressLint("ValidFragment")
    public StoreBookFragment(RelativeLayout fragment_newbookself_top, StroeNewFragmentBook.Hot_word_Book hot_word) {
        this.fragment_newbookself_top = fragment_newbookself_top;
        this.hot_word = hot_word;
    }

    public StoreBookFragment() {
    }

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
    /**
     * 男频banner下方的gridview
     */
    @BindView(R2.id.store_entrance_grid_male)
    public AdaptionGridView mEntranceGridMale;
    public List<EntranceItem> mEntranceItemListMale;
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
        EventBus.getDefault().register(this);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTHH = WIDTH;
        WIDTH_MAIN_AD = WIDTH - ImageUtil.dp2px(activity, 24);
        H30 = WIDTH / 5;
        layoutInflater = LayoutInflater.from(activity);
        H20 = ImageUtil.dp2px(activity, 20);
        WIDTH = (WIDTH - H20) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HorizontalSpacing = ImageUtil.dp2px(activity, 3);//横间距
        WIDTHV = WIDTH - ImageUtil.dp2px(activity, 26);//竖向 图片宽度
        HEIGHTV = (int) (((float) WIDTHV * 4f / 3f));//
        H100 = ImageUtil.dp2px(activity, 100);
        H50 = ImageUtil.dp2px(activity, 55);
        initViews();
        getData(null);
    }

    public void initViews() {
        malePullLayout.setOnRefreshListener(new PullToRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh(PullToRefreshLayout pullToRefreshLayout) {
                getData(pullToRefreshLayout);
            }

            @Override
            public void onLoadMore(PullToRefreshLayout pullToRefreshLayout) {
            }
        });
        //下拉监听,改变 头部搜索整体透明度
        malePullLayout.setOnPullListener(new PullToRefreshLayout.OnPullListener() {
            @Override
            public void onPulling(float y) {
                try {
                    float ratio = Math.min(Math.max(y, 0), REFRESH_HEIGHT) / REFRESH_HEIGHT;
                    fragment_newbookself_top.setAlpha(1 - ratio);
                } catch (Exception e) {
                }
            }
        });

        mScrollViewMale.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                EventBus.getDefault().post(new StoreEventbusBook(true, y));
            }
        });
    }

    public void initInfo(String json) {
        initEntranceGrid();
        try {
            JSONObject jsonObject = new JSONObject(json);
            initWaterfall(jsonObject.getString("label"));
            if (hot_word != null) {
                hot_word.hot_word(gson.fromJson(jsonObject.getString("hot_word"), String[].class));
                hot_word = null;
            }
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        postAsyncHttpEngine_ing = false;
    }

    public void initEntranceGrid() {
        mEntranceItemListMale = new ArrayList<>();
        if (ReaderConfig.USE_PAY) {
            EntranceItem entranceItem1 = new EntranceItem();
            entranceItem1.setName(LanguageUtil.getString(activity, R.string.storeFragment_fenlei));
            entranceItem1.setResId(R.mipmap.entrance1);
            EntranceItem entranceItem2 = new EntranceItem();
            entranceItem2.setName(LanguageUtil.getString(activity, R.string.storeFragment_paihang));
            entranceItem2.setResId(R.mipmap.entrance2);
            EntranceItem entranceItem3 = new EntranceItem();
            entranceItem3.setName(LanguageUtil.getString(activity, R.string.storeFragment_baoyue));
            entranceItem3.setResId(R.mipmap.entrance3);
            EntranceItem entranceItem4 = new EntranceItem();
            entranceItem4.setName(LanguageUtil.getString(activity, R.string.storeFragment_wanben));
            entranceItem4.setResId(R.mipmap.entrance4);
            EntranceItem entranceItem5 = new EntranceItem();
            entranceItem5.setName(LanguageUtil.getString(activity, R.string.storeFragment_xianmian));
            entranceItem5.setResId(R.mipmap.entrance5);
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
            entranceItem5.setResId(R.mipmap.entrance5);
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
                intent.putExtra("PRODUCT", true);
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

    /**
     * 瀑布流形式展示男频书籍
     *
     * @param jsonObject
     */
    public void initWaterfall(String jsonObject) {
        mContainerMale.removeAllViews();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(jsonObject).getAsJsonArray();//获取JsonArray对象
        for (JsonElement jsonElement : jsonElements) {
            StroreBookcLable stroreComicLable = gson.fromJson(jsonElement, StroreBookcLable.class);//解析
            if (stroreComicLable.ad_type != 0) {
                FrameLayout list_ad_view = (FrameLayout) layoutInflater.inflate(R.layout.list_ad_view, null, false);
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
                        intent.putExtra("ad_url_type", stroreComicLable.ad_url_type);
                        activity.startActivity(intent);
                    }
                });
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.rightMargin = ImageUtil.dp2px(activity, 10);
                params.leftMargin = ImageUtil.dp2px(activity, 10);
                params.bottomMargin = ImageUtil.dp2px(activity, 20);
                mContainerMale.addView(list_ad_view, params);
                continue;
            }
            int size = stroreComicLable.list.size();
            if (size == 0) {
                continue;
            }
            View type3 = layoutInflater.inflate(R.layout.fragment_store_book_layout, null, false);
            TextView fragment_store_gridview3_text = type3.findViewById(R.id.fragment_store_gridview3_text);
            fragment_store_gridview3_text.setText(stroreComicLable.label);
            AdaptionGridView fragment_store_gridview3_gridview_first = type3.findViewById(R.id.fragment_store_gridview3_gridview_first);
            fragment_store_gridview3_gridview_first.setHorizontalSpacing(HorizontalSpacing);
            AdaptionGridView fragment_store_gridview3_gridview_second = type3.findViewById(R.id.fragment_store_gridview3_gridview_second);
            AdaptionGridView fragment_store_gridview3_gridview_fore = type3.findViewById(R.id.fragment_store_gridview3_gridview_fore);
            View fragment_store_gridview1_view1 = type3.findViewById(R.id.fragment_store_gridview1_view1);
            View fragment_store_gridview1_view2 = type3.findViewById(R.id.fragment_store_gridview1_view2);
            View fragment_store_gridview1_view3 = type3.findViewById(R.id.fragment_store_gridview1_view3);
            LinearLayout fragment_store_gridview1_more = type3.findViewById(R.id.fragment_store_gridview1_more);
            fragment_store_gridview1_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyToash.Log("LOOKMORE", stroreComicLable.recommend_id);
                    try {
                        startActivity(new Intent(activity, BaseOptionActivity.class)
                                .putExtra("OPTION", LOOKMORE)
                                .putExtra("PRODUCT", true)
                                .putExtra("recommend_id", stroreComicLable.recommend_id));
                    } catch (Exception E) {
                    }
                }
            });
            LinearLayout fragment_store_gridview_huanyihuan = type3.findViewById(R.id.fragment_store_gridview_huanyihuan);
            if (stroreComicLable.can_refresh) {
                fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postHuanyihuan(stroreComicLable.recommend_id, stroreComicLable.style, fragment_store_gridview3_gridview_first, fragment_store_gridview3_gridview_second, fragment_store_gridview3_gridview_fore);
                    }
                });
            } else {
                fragment_store_gridview_huanyihuan.setVisibility(View.GONE);
            }
            int ItemHeigth = Huanyihuan(stroreComicLable.style, stroreComicLable.list, fragment_store_gridview3_gridview_first, fragment_store_gridview3_gridview_second, fragment_store_gridview3_gridview_fore);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.height = ItemHeigth;
            if (!stroreComicLable.can_more && !stroreComicLable.can_refresh) {
                params.height = ItemHeigth - H50;
            } else if (!(stroreComicLable.can_more && stroreComicLable.can_refresh)) {
                buttomonlyOne(fragment_store_gridview1_view1, fragment_store_gridview1_view2, fragment_store_gridview1_view3);
            }
            mContainerMale.addView(type3, params);
        }
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

    private int Huanyihuan(int style, List<StroreBookcLable.Book> bookList, AdaptionGridView fragment_store_gridview3_gridview_first, AdaptionGridView fragment_store_gridview3_gridview_second, AdaptionGridView fragment_store_gridview3_gridview_fore) {
        int size = bookList.size();
        int minSize = 0;
        int ItemHeigth = 0, raw = 0, start = 0;
        if (style == 1) {
            minSize = Math.min(size, 3);
            ItemHeigth = H100 + HEIGHT + H50;
        } else if (style == 2) {
            minSize = Math.min(size, 6);
            if (minSize > 3) {
                raw = 2;
                ItemHeigth = H100 + (HEIGHT + H50) * 2;
            } else {
                raw = 1;
                ItemHeigth = H100 + HEIGHT + H50;
            }
        } else if (style == 3) {
            minSize = Math.min(size, 3);
            ItemHeigth = H100 + HEIGHT + H50;
            if (size > 3) {
                ItemHeigth = H100 + HEIGHT + H50 + (HEIGHTV + HorizontalSpacing) * 3;
                fragment_store_gridview3_gridview_second.setVisibility(View.VISIBLE);
                final List<StroreBookcLable.Book> secondList = bookList.subList(3, Math.min(size, 6));
                VerticalAdapter horizontalAdapter = new VerticalAdapter(activity, secondList, WIDTHV, HEIGHTV, true);
                fragment_store_gridview3_gridview_second.setAdapter(horizontalAdapter);
                fragment_store_gridview3_gridview_second.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(activity, BookInfoActivity.class);
                        intent.putExtra("book_id", secondList.get(position).getBook_id());
                        activity.startActivity(intent);
                    }
                });
            }
        } else if (style == 4) {
            start = 1;
            minSize = Math.min(size, 4);
            ItemHeigth = H100 + HEIGHT + H50 + (HEIGHTV + HorizontalSpacing);
            fragment_store_gridview3_gridview_fore.setVisibility(View.VISIBLE);
            final List<StroreBookcLable.Book> secondList = bookList.subList(0, 1);
            VerticalAdapter horizontalAdapter = new VerticalAdapter(activity, secondList, WIDTHV, HEIGHTV, true);
            fragment_store_gridview3_gridview_fore.setAdapter(horizontalAdapter);
            fragment_store_gridview3_gridview_fore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, BookInfoActivity.class);
                    intent.putExtra("book_id", secondList.get(position).getBook_id());
                    activity.startActivity(intent);
                }
            });
        }
        List<StroreBookcLable.Book> firstList = bookList.subList(start, minSize);
        VerticalAdapter verticalAdapter = new VerticalAdapter(activity, firstList, WIDTH, HEIGHT, false);
        fragment_store_gridview3_gridview_first.setAdapter(verticalAdapter);
        fragment_store_gridview3_gridview_first.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    Intent intent = new Intent(activity, BookInfoActivity.class);
                    if (style < 4) {
                        intent.putExtra("book_id", bookList.get(position).getBook_id());
                    } else intent.putExtra("book_id", bookList.get(position + 1).getBook_id());
                    activity.startActivity(intent);
                } catch (Exception e) {
                }
            }
        });
        return ItemHeigth;
    }

    public void postHuanyihuan(String recommend_id, int style, AdaptionGridView fragment_store_gridview3_gridview_first, AdaptionGridView fragment_store_gridview3_gridview_second, AdaptionGridView fragment_store_gridview3_gridview_fore) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_id", recommend_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + book_refresh, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            List<StroreBookcLable.Book> bookList = gson.fromJson(new JSONObject(result).getString("list"), new TypeToken<List<StroreBookcLable.Book>>() {
                            }.getType());
                            if (!bookList.isEmpty()) {
                                int ItemHeigth = Huanyihuan(style, bookList, fragment_store_gridview3_gridview_first, fragment_store_gridview3_gridview_second, fragment_store_gridview3_gridview_fore);
                            }
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

    void getData(PullToRefreshLayout pullToRefreshLayout) {
        getBookStore(pullToRefreshLayout);
        getBannerData();
    }

    /**
     * 获取小说推荐数据
     *
     * @param pullToRefreshLayout
     */
    private void getBookStore(PullToRefreshLayout pullToRefreshLayout) {
        MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ReaderConfig.mBookStoreUrl, "StoreBookMan", new MainHttpTask.GetHttpData() {
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

    /**
     * 获取顶部Banner数据
     */
    private void getBannerData() {
        MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ReaderConfig.BOOK_STORE_BANNER, "StoreBookMan", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                if (!StringUtils.isEmpty(result)) {
                    ConvenientBanner.initbanner(activity, gson, result, mStoreBannerMale, 5000, 0);
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(BuyLoginSuccess buyLoginSuccess) {
        getBannerData();
    }
}
