package com.heiheilianzai.app.comic.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
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
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.BaseOptionActivity;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.adapter.StoreComicAdapter;
import com.heiheilianzai.app.bean.CommentItem;
import com.heiheilianzai.app.comic.activity.ComicInfoActivity;
import com.heiheilianzai.app.comic.been.StroreComicLable;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.config.ReaderConfig.LOOKMORE;

/**
 * 首页漫画 Adapter
 */
public class HomeStoreComicAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    List<StroreComicLable> listData;
    public int WIDTH, WIDTHH, WIDTH_MAIN_AD, HEIGHT, H55, H30;

    public HomeStoreComicAdapter(Activity activity, List<StroreComicLable> listData) {
        this.activity = activity;
        this.listData = listData;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTHH = WIDTH;
        WIDTH_MAIN_AD = WIDTH;
        H30 = WIDTH / 5;
        H55 = ImageUtil.dp2px(activity, 55);
    }

    @Override
    public int getItemViewType(int position) {
        if (listData.get(position).ad_type != 0) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getItemCount() {
        return listData.size();
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
        MyPicasso.GlideImageNoSize(activity, stroreComicLable.ad_image, holder.list_ad_view_img);
        holder.list_ad_view_img.setOnClickListener(new View.OnClickListener() {
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
    }

    class AdViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R2.id.list_ad_view_img)
        ImageView list_ad_view_img;

        public AdViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    private void setComicViewHolder(ComicViewHolder holder, final int position, StroreComicLable stroreComicLable){
        List<StroreComicLable.Comic> comicList = stroreComicLable.list;
        holder.lable.setText(stroreComicLable.label);
        if (stroreComicLable.can_refresh.equals("true")) {
            holder.fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postHuanyihuan(stroreComicLable.recommend_id, stroreComicLable.style, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
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
                                .putExtra("PRODUCT", false)
                                .putExtra("recommend_id", stroreComicLable.recommend_id)
                        );
                    } catch (Exception E) {
                    }
                }
            });
        } else {
            holder.fragment_store_gridview1_more.setVisibility(View.GONE);
        }
        if (comicList.isEmpty()) {
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int ItemHeigth = setItemData(stroreComicLable.style, comicList, holder.fragment_store_gridview1_gridview, holder.liem_store_comic_style1_style3);
        if (comicList.isEmpty()) {
            ItemHeigth = 0;
            holder.fragment_store_gridview1_gridview.setVisibility(View.GONE);
        }
        params.height = ItemHeigth + H55 + ImageUtil.dp2px(activity, 40);
        if (!comicList.isEmpty()) {
            if (stroreComicLable.style == 3) {
                params.height += H55 + WIDTH * 5 / 9;
            }
        }
        if (!stroreComicLable.can_more.equals("true") && !stroreComicLable.can_refresh.equals("true")) {
            params.height -= H55;
        } else if (!(stroreComicLable.can_more.equals("true") && stroreComicLable.can_refresh.equals("true"))) {
            buttomonlyOne(holder.fragment_store_gridview1_view1, holder.fragment_store_gridview1_view2, holder.fragment_store_gridview1_view3);
        }
        int dp10 = ImageUtil.dp2px(activity, 10);
        params.setMargins(dp10,0,dp10,0);
        holder.itemView.setLayoutParams(params);
    }

    class ComicViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.fragment_store_gridview1_text)
        TextView lable;
        @BindView(R2.id.fragment_store_gridview1_more)
        LinearLayout fragment_store_gridview1_more;
        @BindView(R2.id.fragment_store_gridview_huanyihuan)
        LinearLayout fragment_store_gridview_huanyihuan;
        @BindView(R2.id.fragment_store_gridview1_view1)
        View fragment_store_gridview1_view1;
        @BindView(R2.id.fragment_store_gridview1_view2)
        View fragment_store_gridview1_view2;
        @BindView(R2.id.fragment_store_gridview1_view3)
        View fragment_store_gridview1_view3;
        @BindView(R2.id.fragment_store_gridview1_gridview)
        AdaptionGridViewNoMargin fragment_store_gridview1_gridview;
        @BindView(R2.id.liem_store_comic_style1_style3)
        AdaptionGridViewNoMargin liem_store_comic_style1_style3;

        public ComicViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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
                break;
            case 2:
                double size = Math.min(6, comicList.size());
                raw = (int) (Math.ceil(size / 3d));
                width = WIDTH / 3;
                height = width * 4 / 3;
                fragment_store_gridview1_gridview.setNumColumns(3);
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size), activity, style, width, height);
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
