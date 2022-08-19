package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.StoreComicAdapter;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;

/**
 * 首页漫画 Adapter
 */
public class HomeStoreComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    List<StroreComicLable> listData;
    public int WIDTH, HEIGHT, H55, H30;
    public static final int COMIC_UI_STYLE_1 = 1;//风格1
    public static final int COMIC_UI_STYLE_2 = 2;//风格2
    public static final int COMIC_UI_STYLE_3 = 3;//风格3
    public static final int COMIC_UI_STYLE_4 = 4;//横4
    public static final int COMIC_UI_STYLE_5 = 5;//横6
    public static final int COMIC_UI_STYLE_6 = 7;// 横一如果不是3个可以无线下拉（只能配置在最下面）
    private boolean isTopYear;

    public HomeStoreComicAdapter(Activity activity, List<StroreComicLable> listData, boolean isTopYear) {
        this.activity = activity;
        this.listData = listData;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        H30 = WIDTH / 5;
        this.isTopYear = isTopYear;
        H55 = ImageUtil.dp2px(activity, 55);
    }

    @Override
    public int getItemViewType(int position) {
        StroreComicLable stroreComicLable = listData.get(position);
        if (stroreComicLable.ad_type != 0) {
            return 1;
        } else {
            switch (stroreComicLable.style) {//小说展示风格有4种 防止不同风格UI复用用ViewType区分。
                case COMIC_UI_STYLE_1:
                    return 2;
                case COMIC_UI_STYLE_2:
                    return 3;
                case COMIC_UI_STYLE_3:
                    return 4;
            }
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder;
        View view;
        switch (viewType) {
            case 1:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_ad_view, parent, false);
                viewHolder = new AdViewHolder(view);
                return viewHolder;
            case 2:
            case 3:
            case 4:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_store_comic_layout, parent, false);
                viewHolder = new ComicViewHolder(view);
                return viewHolder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        StroreComicLable stroreComicLable = listData.get(position);
        if (holder instanceof AdViewHolder) {
            setAdViewHolder(((AdViewHolder) holder), position, stroreComicLable);
        } else if (holder instanceof ComicViewHolder) {
            setComicViewHolder(((ComicViewHolder) holder), position, stroreComicLable);
        }
    }

    private void setAdViewHolder(AdViewHolder holder, final int position, StroreComicLable stroreComicLable) {
        holder.list_ad_view_layout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = holder.list_ad_view_img.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
        layoutParams.height = layoutParams.width / 3;
        holder.list_ad_view_img.setLayoutParams(layoutParams);
        AdInfo adInfo = BaseSdkAD.newAdInfo(stroreComicLable);
        if (adInfo != null) {
            MyPicasso.glideSdkAd(activity, adInfo, stroreComicLable.ad_image, holder.list_ad_view_img);
        } else {
            MyPicasso.GlideImageNoSize(activity, stroreComicLable.ad_image, holder.list_ad_view_img);
        }

        holder.list_ad_view_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adInfo != null) {
                    XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                }
                BaseAd.jumpADInfo(stroreComicLable, activity);
            }
        });
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R.id.list_ad_view_img)
        ImageView list_ad_view_img;

        public AdViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void setComicViewHolder(ComicViewHolder holder, final int position, StroreComicLable stroreComicLable) {
        List<StroreComicLable.Comic> comicList = stroreComicLable.list;
        //横一无限不需要更多以及换一换
        if (stroreComicLable.style == COMIC_UI_STYLE_6 && stroreComicLable.work_num_type == 2) {
            holder.mLlBar.setVisibility(View.GONE);
        } else {
            holder.mLlBar.setVisibility(View.VISIBLE);
            if (stroreComicLable.can_refresh.equals("true")) {
                holder.fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postHuanyihuan(stroreComicLable, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
                    }
                });
            } else {
                holder.fragment_store_gridview_huanyihuan.setVisibility(View.GONE);
            }
            if (stroreComicLable.can_more.equals("true")) {
                holder.fragment_store_gridview1_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyToash.Log("LOOKMORE", stroreComicLable.recommend_id);
                        try {
                            activity.startActivity(new Intent(activity, BaseOptionActivity.class)
                                    .putExtra("OPTION", LOOKMORE)
                                    .putExtra("PRODUCT", 2)
                                    .putExtra("IS_TOP_YEAR", isTopYear)
                                    .putExtra("title", LanguageUtil.getString(activity, R.string.refer_page_more) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + stroreComicLable.recommend_id)
                                    .putExtra("recommend_id", stroreComicLable.recommend_id)
                            );
                        } catch (Exception E) {
                        }
                    }
                });
            } else {
                holder.fragment_store_gridview1_more.setVisibility(View.GONE);
            }
        }

        if (comicList.isEmpty()) {
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int ItemHeigth = setItemData(stroreComicLable, comicList, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
        if (comicList.isEmpty()) {
            ItemHeigth = 0;
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }
        params.height = ItemHeigth + H55 + ImageUtil.dp2px(activity, 40);
        if (!comicList.isEmpty()) {
            if (stroreComicLable.style == COMIC_UI_STYLE_3) {
                params.height += H55 + WIDTH * 5 / 9;
            }
        }
        if (!TextUtils.isEmpty(stroreComicLable.label)) {
            holder.lable.setText(stroreComicLable.label);
            holder.mLlTitle.setVisibility(View.VISIBLE);
        } else {
            holder.mLlTitle.setVisibility(View.GONE);
            params.height -= ImageUtil.dp2px(activity, 40);
        }
        if (!stroreComicLable.can_more.equals("true") && !stroreComicLable.can_refresh.equals("true")) {
            params.height -= H55;
        } else if (!(stroreComicLable.can_more.equals("true") && stroreComicLable.can_refresh.equals("true"))) {
            buttomonlyOne(holder.fragment_store_gridview1_view1, holder.fragment_store_gridview1_view2, holder.fragment_store_gridview1_view3);
        }
        int dp10 = ImageUtil.dp2px(activity, 10);
        params.setMargins(dp10, 0, dp10, 0);
        holder.itemView.setLayoutParams(params);
    }

    class ComicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fragment_store_gridview1_text)
        TextView lable;
        @BindView(R.id.fragment_store_gridview1_more)
        LinearLayout fragment_store_gridview1_more;
        @BindView(R.id.fragment_store_gridview_huanyihuan)
        LinearLayout fragment_store_gridview_huanyihuan;
        @BindView(R.id.fragment_store_gridview1_view1)
        View fragment_store_gridview1_view1;
        @BindView(R.id.fragment_store_gridview1_view2)
        View fragment_store_gridview1_view2;
        @BindView(R.id.fragment_store_gridview1_view3)
        View fragment_store_gridview1_view3;
        @BindView(R.id.fragment_store_gridview1_gridview)
        AdaptionGridViewNoMargin fragment_store_gridview1_gridview;
        @BindView(R.id.liem_store_comic_style1_style3)
        AdaptionGridViewNoMargin liem_store_comic_style1_style3;
        @BindView(R.id.fragment_store_gridview1_huanmore)
        LinearLayout mLlBar;
        @BindView(R.id.ll_title_bar)
        LinearLayout mLlTitle;

        public ComicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private int setItemData(StroreComicLable stroreComicLable, List<StroreComicLable.Comic> comicList, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin liem_store_comic_style1_style3) {
        String recommend_id = stroreComicLable.recommend_id;
        int style = stroreComicLable.style;
        int width, height = 80, raw = 2;
        StoreComicAdapter storeComicAdapter = null;
        switch (style) {
            case COMIC_UI_STYLE_1:
                width = WIDTH / 2;
                height = width * 3 / 5;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size1 = Math.min(4, comicList.size());
                raw = (int) (Math.ceil(size1 / 2d));
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size1), activity, style, width, height);
                break;
            case COMIC_UI_STYLE_2:
                double size = Math.min(6, comicList.size());
                raw = (int) (Math.ceil(size / 3d));
                width = WIDTH / 3;
                height = width * 4 / 3;
                fragment_store_gridview1_gridview.setNumColumns(3);
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size), activity, style, width, height);
                break;
            case COMIC_UI_STYLE_3:
                if (!comicList.isEmpty()) {
                    liem_store_comic_style1_style3.setVisibility(View.VISIBLE);
                    fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                    StoreComicAdapter storeComicAdapter3 = new StoreComicAdapter(comicList.subList(0, 1), activity, style, WIDTH, WIDTH * 5 / 9);
                    liem_store_comic_style1_style3.setAdapter(storeComicAdapter3);
                    liem_store_comic_style1_style3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            activity.startActivity(ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, comicList.get(0).comic_id));
                        }
                    });
                    width = WIDTH / 3;
                    height = width * 4 / 3;
                    raw = 1;
                    fragment_store_gridview1_gridview.setNumColumns(3);
                    storeComicAdapter = new StoreComicAdapter(comicList.subList(1, Math.min(4, comicList.size())), activity, 2, width, height);
                }
                break;
            case COMIC_UI_STYLE_4:
                width = WIDTH / 2;
                height = width * 3 / 5;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size2 = Math.min(4, comicList.size());
                raw = (int) (Math.ceil(size2 / 2d));
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size2), activity, style, width, height);
                break;
            case COMIC_UI_STYLE_5:
                width = WIDTH / 2;
                height = width * 3 / 5;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size3 = Math.min(6, comicList.size());
                raw = (int) (Math.ceil(size3 / 2d));
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size3), activity, style, width, height);
                break;
            case COMIC_UI_STYLE_6:
                StoreComicAdapter storeComicAdapter3;
                if (comicList.size() > 0) {
                    fragment_store_gridview1_gridview.setVisibility(View.GONE);
                    liem_store_comic_style1_style3.setVisibility(View.VISIBLE);
                    if (stroreComicLable.work_num_type != 2) {
                        raw = Math.min(comicList.size(), 3);
                        storeComicAdapter3 = new StoreComicAdapter(comicList.subList(0, raw), activity, style, WIDTH, WIDTH * 5 / 9);
                    } else {
                        storeComicAdapter3 = new StoreComicAdapter(comicList, activity, style, WIDTH, WIDTH * 5 / 9);
                        raw = comicList.size();
                    }
                    liem_store_comic_style1_style3.setAdapter(storeComicAdapter3);
                    liem_store_comic_style1_style3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            activity.startActivity(ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, comicList.get(position).comic_id));
                        }
                    });
                    height = WIDTH * 5 / 9;
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) liem_store_comic_style1_style3.getLayoutParams();
                    layoutParams.height = (height + H55) * raw;
                    liem_store_comic_style1_style3.setLayoutParams(layoutParams);
                    return layoutParams.height;
                }
                break;
        }
        fragment_store_gridview1_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String comic_id;
                if (style != COMIC_UI_STYLE_3) {
                    comic_id = comicList.get(position).comic_id;
                } else {
                    comic_id = comicList.get(position + 1).comic_id;
                }
                Intent intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, comic_id);
                activity.startActivity(intent);
            }
        });
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_store_gridview1_gridview.getLayoutParams();
        fragment_store_gridview1_gridview.setAdapter(storeComicAdapter);
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

    public void postHuanyihuan(StroreComicLable stroreComicLable, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin type1) {
        String recommend_id = stroreComicLable.recommend_id;
        int style = stroreComicLable.style;
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_id", recommend_id + "");
        String json = params.generateParamsJson();
        String url;
        if (isTopYear) {
            url = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_TOP_YEAR_refresh;
        } else {
            url = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_home_refresh;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        List<StroreComicLable.Comic> comicList = null;
                        try {
                            comicList = new Gson().fromJson(new JSONObject(result).getString("list"), new TypeToken<List<StroreComicLable.Comic>>() {
                            }.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (comicList != null && !comicList.isEmpty()) {
                            setItemData(stroreComicLable, comicList, fragment_store_gridview1_gridview, type1);
                            setChangeRecommendationEvent(recommend_id, style, comicList);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * 点击换一换 加入神策埋点
     *
     * @param column_id
     * @param comics
     */
    private void setChangeRecommendationEvent(String column_id, int style, List<StroreComicLable.Comic> comics) {
        try {
            List<StroreComicLable.Comic> subListComics = new ArrayList<>();
            switch (style) {
                case COMIC_UI_STYLE_1:
                case COMIC_UI_STYLE_3:
                    subListComics.addAll(comics.subList(0, Math.min(4, comics.size())));
                    break;
                case COMIC_UI_STYLE_2:
                    subListComics.addAll(comics.subList(0, Math.min(6, comics.size())));
                    break;
            }
            List<String> workId = new ArrayList<>();
            for (StroreComicLable.Comic comic : subListComics) {
                workId.add(comic.comic_id);
            }
            SensorsDataHelper.setChangeRecommendationEvent(WORKS_TYPE_COMICS, Integer.valueOf(column_id), workId);
        } catch (Exception e) {
        }
    }
}