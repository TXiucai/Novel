package com.heiheilianzai.app.adapter.cartoon;

import static com.heiheilianzai.app.constant.CartoonConfig.CARTOON_TOP_YEAR_refresh;
import static com.heiheilianzai.app.constant.CartoonConfig.CARTOON_home_refresh;
import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;

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

import androidx.recyclerview.widget.RecyclerView;

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
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
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

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页漫画 Adapter
 */
public class HomeStoreCartoonAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    List<StroreCartoonLable> listData;
    public int WIDTH, HEIGHT, H55, H30;
    public static final int COMIC_UI_STYLE_1 = 1;//风格1 横2*2
    public static final int COMIC_UI_STYLE_2 = 2;//风格2 横2*3
    public static final int COMIC_UI_STYLE_3 = 3;//风格3 横1+ 横2*2
    public static final int COMIC_UI_STYLE_4 = 4;//风格4 横1
    private boolean isTopYear;

    public HomeStoreCartoonAdapter(Activity activity, List<StroreCartoonLable> listData, boolean isTopYear) {
        this.activity = activity;
        this.listData = listData;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        H30 = WIDTH / 5;
        this.isTopYear = isTopYear;
        H55 = ImageUtil.dp2px(activity, 55);
    }

    @Override
    public int getItemViewType(int position) {
        StroreCartoonLable stroreCartoonLable = listData.get(position);
        if (stroreCartoonLable.ad_type != 0) {
            return 1;
        } else {
            switch (stroreCartoonLable.style) {//小说展示风格有4种 防止不同风格UI复用用ViewType区分。
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
        StroreCartoonLable stroreCartoonLable = listData.get(position);
        if (holder instanceof AdViewHolder) {
            setAdViewHolder(((AdViewHolder) holder), position, stroreCartoonLable);
        } else if (holder instanceof ComicViewHolder) {
            setComicViewHolder(((ComicViewHolder) holder), position, stroreCartoonLable);
        }
    }

    private void setAdViewHolder(AdViewHolder holder, final int position, StroreCartoonLable stroreCartoonLable) {
        holder.list_ad_view_layout.setVisibility(View.VISIBLE);
        ViewGroup.LayoutParams layoutParams = holder.list_ad_view_img.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
        layoutParams.height = layoutParams.width / 3;
        holder.list_ad_view_img.setLayoutParams(layoutParams);
        AdInfo adInfo = BaseSdkAD.newAdInfo(stroreCartoonLable);
        if (adInfo != null) {
            MyPicasso.glideSdkAd(activity, adInfo, stroreCartoonLable.ad_image, holder.list_ad_view_img);
        } else {
            MyPicasso.GlideImageNoSize(activity, stroreCartoonLable.ad_image, holder.list_ad_view_img);
        }

        holder.list_ad_view_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (adInfo != null) {
                    XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                }
                BaseAd.jumpADInfo(stroreCartoonLable, activity);
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

    private void setComicViewHolder(ComicViewHolder holder, final int position, StroreCartoonLable stroreCartoonLable) {
        List<StroreCartoonLable.Cartoon> cartoonList = stroreCartoonLable.list;
        //横一无限不需要更多以及换一换
        if (stroreCartoonLable.style == COMIC_UI_STYLE_4 && stroreCartoonLable.work_num_type == 2) {
            holder.mLlBar.setVisibility(View.GONE);
        } else {
            holder.mLlBar.setVisibility(View.VISIBLE);
            if (stroreCartoonLable.can_refresh.equals("true")) {
                holder.fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        postHuanyihuan(stroreCartoonLable, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
                    }
                });
            } else {
                holder.fragment_store_gridview_huanyihuan.setVisibility(View.GONE);
            }
            if (stroreCartoonLable.can_more.equals("true")) {
                holder.fragment_store_gridview1_more.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MyToash.Log("LOOKMORE", stroreCartoonLable.recommend_id);
                        try {
                            activity.startActivity(new Intent(activity, BaseOptionActivity.class)
                                    .putExtra("OPTION", LOOKMORE)
                                    .putExtra("PRODUCT", 3)
                                    .putExtra("IS_TOP_YEAR", isTopYear)
                                    .putExtra("title", LanguageUtil.getString(activity, R.string.refer_page_more) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + stroreCartoonLable.recommend_id)
                                    .putExtra("recommend_id", stroreCartoonLable.recommend_id)
                            );
                        } catch (Exception E) {
                        }
                    }
                });
            } else {
                holder.fragment_store_gridview1_more.setVisibility(View.GONE);
            }
        }
        if (cartoonList.isEmpty()) {
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int ItemHeigth = setItemData(stroreCartoonLable, cartoonList, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
        if (cartoonList.isEmpty()) {
            ItemHeigth = 0;
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }
        params.height = ItemHeigth + H55 + ImageUtil.dp2px(activity, 40);
        if (!cartoonList.isEmpty()) {
            if (stroreCartoonLable.style == COMIC_UI_STYLE_3) {
                params.height += H55 + WIDTH * 5 / 9;
            }
        }
        if (!TextUtils.isEmpty(stroreCartoonLable.label)) {
            holder.lable.setText(stroreCartoonLable.label);
            holder.mLlTitle.setVisibility(View.VISIBLE);
        } else {
            holder.mLlTitle.setVisibility(View.GONE);
            params.height -= ImageUtil.dp2px(activity, 40);
        }
        if (!stroreCartoonLable.can_more.equals("true") && !stroreCartoonLable.can_refresh.equals("true")) {
            params.height -= H55;
        } else if (!(stroreCartoonLable.can_more.equals("true") && stroreCartoonLable.can_refresh.equals("true"))) {
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

    private int setItemData(StroreCartoonLable stroreCartoonLable, List<StroreCartoonLable.Cartoon> cartoonList, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin liem_store_comic_style1_style3) {
        String recommend_id = stroreCartoonLable.recommend_id;
        int style = stroreCartoonLable.style;
        fragment_store_gridview1_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String cartoon_id;
                if (style != COMIC_UI_STYLE_3) {
                    cartoon_id = cartoonList.get(position).video_id;
                } else {
                    cartoon_id = cartoonList.get(position + 1).video_id;
                }
                Intent intent = CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, cartoon_id);
                activity.startActivity(intent);
            }
        });
        int width, height = 80, raw = 2;
        StoreCartoonAdapter storeCartoonAdapter = null;
        switch (style) {
            case COMIC_UI_STYLE_1:
                width = WIDTH / 2;
                height = width * 2 / 3;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size1 = Math.min(4, cartoonList.size());
                raw = (int) (Math.ceil(size1 / 2d));
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeCartoonAdapter = new StoreCartoonAdapter(cartoonList.subList(0, (int) size1), activity, width, height);
                break;
            case COMIC_UI_STYLE_2:
                width = WIDTH / 2;
                height = width * 2 / 3;
                fragment_store_gridview1_gridview.setNumColumns(2);
                double size2 = Math.min(6, cartoonList.size());
                raw = (int) (Math.ceil(size2 / 2d));
                fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                liem_store_comic_style1_style3.setVisibility(View.GONE);
                storeCartoonAdapter = new StoreCartoonAdapter(cartoonList.subList(0, (int) size2), activity, width, height);
                break;
            case COMIC_UI_STYLE_3:
                if (!cartoonList.isEmpty()) {
                    liem_store_comic_style1_style3.setVisibility(View.VISIBLE);
                    StoreCartoonAdapter storeCartoonAdapter1 = new StoreCartoonAdapter(cartoonList.subList(0, 1), activity, WIDTH, WIDTH * 5 / 9);
                    liem_store_comic_style1_style3.setAdapter(storeCartoonAdapter1);
                    liem_store_comic_style1_style3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            activity.startActivity(CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, cartoonList.get(0).video_id));
                        }
                    });
                    width = WIDTH / 2;
                    height = width * 2 / 3;
                    double size3 = Math.min(4, cartoonList.size());
                    raw = (int) (Math.ceil(size3 / 2d));
                    fragment_store_gridview1_gridview.setNumColumns(2);
                    fragment_store_gridview1_gridview.setVisibility(View.VISIBLE);
                    storeCartoonAdapter = new StoreCartoonAdapter(cartoonList.subList(1, Math.min(5, cartoonList.size())), activity, width, height);
                }
                break;
            case COMIC_UI_STYLE_4:
                if (cartoonList.size() > 0) {
                    StoreCartoonAdapter storeComicAdapter3;
                    fragment_store_gridview1_gridview.setVisibility(View.GONE);
                    liem_store_comic_style1_style3.setVisibility(View.VISIBLE);
                    if (stroreCartoonLable.work_num_type != 2) {
                        raw = Math.min(cartoonList.size(), 3);
                        storeComicAdapter3 = new StoreCartoonAdapter(cartoonList.subList(0, raw), activity, WIDTH, WIDTH * 5 / 9);
                    } else {
                        storeComicAdapter3 = new StoreCartoonAdapter(cartoonList, activity, WIDTH, WIDTH * 5 / 9);
                        raw = cartoonList.size();
                    }
                    liem_store_comic_style1_style3.setAdapter(storeComicAdapter3);
                    liem_store_comic_style1_style3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            activity.startActivity(CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id, cartoonList.get(position).video_id));
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
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_store_gridview1_gridview.getLayoutParams();
        fragment_store_gridview1_gridview.setAdapter(storeCartoonAdapter);
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

    public void postHuanyihuan(StroreCartoonLable stroreCartoonLable, AdaptionGridViewNoMargin fragment_store_gridview1_gridview, AdaptionGridViewNoMargin type1) {
        String recommend_id = stroreCartoonLable.recommend_id;
        int style = stroreCartoonLable.style;
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_id", recommend_id + "");
        String json = params.generateParamsJson();
        String url;
        if (isTopYear) {
            url = ReaderConfig.getBaseUrl() + CARTOON_TOP_YEAR_refresh;
        } else {
            url = ReaderConfig.getBaseUrl() + CARTOON_home_refresh;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        List<StroreCartoonLable.Cartoon> cartoonList = null;
                        try {
                            cartoonList = new Gson().fromJson(new JSONObject(result).getString("list"), new TypeToken<List<StroreCartoonLable.Cartoon>>() {
                            }.getType());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (cartoonList != null && !cartoonList.isEmpty()) {
                            setItemData(stroreCartoonLable, cartoonList, fragment_store_gridview1_gridview, type1);
                            setChangeRecommendationEvent(recommend_id, style, cartoonList);
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
     * @param
     */
    private void setChangeRecommendationEvent(String column_id, int style, List<StroreCartoonLable.Cartoon> cartoonList) {
        try {
            List<StroreCartoonLable.Cartoon> subListComics = new ArrayList<>();
            switch (style) {
                case COMIC_UI_STYLE_1:
                case COMIC_UI_STYLE_3:
                    subListComics.addAll(cartoonList.subList(0, Math.min(4, cartoonList.size())));
                    break;
                case COMIC_UI_STYLE_2:
                    subListComics.addAll(cartoonList.subList(0, Math.min(6, cartoonList.size())));
                    break;
            }
            List<String> workId = new ArrayList<>();
            for (StroreCartoonLable.Cartoon cartoon : subListComics) {
                workId.add(cartoon.video_id);
            }
            SensorsDataHelper.setChangeRecommendationEvent(WORKS_TYPE_COMICS, Integer.valueOf(column_id), workId);
        } catch (Exception e) {
        }
    }
}