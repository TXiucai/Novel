package com.heiheilianzai.app.adapter.book;

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
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.Material;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.constant.BookConfig.book_refresh;
import static com.heiheilianzai.app.constant.BookConfig.book_top_year_refresh;
import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_BOOK;

/**
 * 首页小说 Adapter
 */
public class HomeStoreBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    List<StroreBookcLable> listData;
    public int WIDTH, WIDTHV, HEIGHT, HEIGHTV, HorizontalSpacing, H100, H50, H20, H30, HWIDTH, HHEIGHT, HorizontalHeight;
    public static final int BOOK_UI_STYLE_1 = 1;//风格1
    public static final int BOOK_UI_STYLE_2 = 2;//风格2
    public static final int BOOK_UI_STYLE_3 = 3;//风格3
    public static final int BOOK_UI_STYLE_4 = 4;//风格4
    public static final int BOOK_UI_STYLE_5 = 5;//风格5 横4
    public static final int BOOK_UI_STYLE_6 = 6;//风格6 横六  一排2 3排
    public static final int BOOK_UI_STYLE_8 = 8;// 左右无限滑动
    private boolean isTopYear;
    private boolean isHorizontal = false;
    private boolean isBackground = false;

    public HomeStoreBookAdapter(Activity activity, List<StroreBookcLable> listData, boolean isTopYear) {
        this.activity = activity;
        this.listData = listData;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        HorizontalHeight = ImageUtil.dp2px(activity, 28);
        H30 = WIDTH / 5;
        H20 = ImageUtil.dp2px(activity, 20);
        WIDTH = (WIDTH - H20) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HWIDTH = (ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 10)) / 2;
        HHEIGHT = HWIDTH * 3 / 5;
        HorizontalSpacing = ImageUtil.dp2px(activity, 3);//横间距
        WIDTHV = WIDTH - ImageUtil.dp2px(activity, 26);//竖向 图片宽度
        HEIGHTV = (int) (((float) WIDTHV * 4f / 3f));//
        H100 = ImageUtil.dp2px(activity, 100);
        H50 = ImageUtil.dp2px(activity, 55);
        this.isTopYear = isTopYear;
    }

    @Override
    public int getItemViewType(int position) {
        StroreBookcLable stroreBookcLable = listData.get(position);
        if (stroreBookcLable.ad_type != 0) {
            return 1;
        } else {
            switch (stroreBookcLable.style) {//小说展示风格有4种 防止不同风格UI复用用ViewType区分。
                case BOOK_UI_STYLE_1:
                    return 2;
                case BOOK_UI_STYLE_2:
                    return 3;
                case BOOK_UI_STYLE_3:
                    return 4;
                case BOOK_UI_STYLE_4:
                    return 5;
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
                viewHolder = new HomeStoreBookAdapter.AdViewHolder(view);
                return viewHolder;
            case 2://防止不同风格UI复用
            case 3:
            case 4:
            case 5:
            case 6:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_store_book_layout, parent, false);
                viewHolder = new HomeStoreBookAdapter.BookViewHolder(view);
                return viewHolder;
            default:
                return null;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        StroreBookcLable stroreComicLable = listData.get(position);
        if (holder instanceof HomeStoreBookAdapter.AdViewHolder) {
            setAdViewHolder(((HomeStoreBookAdapter.AdViewHolder) holder), position, stroreComicLable);
        } else if (holder instanceof HomeStoreBookAdapter.BookViewHolder) {
            setComicViewHolder(((HomeStoreBookAdapter.BookViewHolder) holder), position, stroreComicLable);
        }
    }

    private void setAdViewHolder(AdViewHolder holder, final int position, StroreBookcLable stroreComicLable) {
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

    private void setComicViewHolder(BookViewHolder holder, final int position, StroreBookcLable stroreComicLable) {
        holder.fragment_store_gridview3_text.setText(stroreComicLable.label);
        holder.fragment_store_gridview3_gridview_first.setHorizontalSpacing(HorizontalSpacing);
        holder.fragment_store_gridview1_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyToash.Log("LOOKMORE", stroreComicLable.recommend_id);
                try {
                    activity.startActivity(new Intent(activity, BaseOptionActivity.class)
                            .putExtra("OPTION", LOOKMORE)
                            .putExtra("IS_TOP_YEAR", isTopYear)
                            .putExtra("PRODUCT", 1)
                            .putExtra("title", LanguageUtil.getString(activity, R.string.refer_page_more) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + stroreComicLable.recommend_id)
                            .putExtra("recommend_id", stroreComicLable.recommend_id));
                } catch (Exception E) {
                }
            }
        });
        if (stroreComicLable.can_refresh) {
            holder.fragment_store_gridview_huanyihuan.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    postHuanyihuan(stroreComicLable.recommend_id, stroreComicLable.style, holder.fragment_store_gridview3_gridview_first, holder.fragment_store_gridview3_gridview_second, holder.fragment_store_gridview3_gridview_fore);
                }
            });
        } else {
            holder.fragment_store_gridview_huanyihuan.setVisibility(View.GONE);
        }
        int ItemHeigth = Huanyihuan(stroreComicLable.recommend_id, stroreComicLable.style, stroreComicLable.list, holder.fragment_store_gridview3_gridview_first, holder.fragment_store_gridview3_gridview_second, holder.fragment_store_gridview3_gridview_fore);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.height = ItemHeigth;
        if (!stroreComicLable.can_more && !stroreComicLable.can_refresh) {
            params.height = ItemHeigth - H50;
        } else if (!(stroreComicLable.can_more && stroreComicLable.can_refresh)) {
            buttomonlyOne(holder.fragment_store_gridview1_view1, holder.fragment_store_gridview1_view2, holder.fragment_store_gridview1_view3);
        }
        int dp10 = ImageUtil.dp2px(activity, 10);
        params.setMargins(dp10, 0, dp10, 0);
        holder.itemView.setLayoutParams(params);
    }

    class BookViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.fragment_store_gridview3_text)
        TextView fragment_store_gridview3_text;
        @BindView(R.id.fragment_store_gridview3_gridview_first)
        AdaptionGridView fragment_store_gridview3_gridview_first;
        @BindView(R.id.fragment_store_gridview3_gridview_second)
        AdaptionGridView fragment_store_gridview3_gridview_second;
        @BindView(R.id.fragment_store_gridview3_gridview_fore)
        AdaptionGridView fragment_store_gridview3_gridview_fore;
        @BindView(R.id.fragment_store_gridview1_view1)
        View fragment_store_gridview1_view1;
        @BindView(R.id.fragment_store_gridview1_view2)
        View fragment_store_gridview1_view2;
        @BindView(R.id.fragment_store_gridview1_view3)
        View fragment_store_gridview1_view3;
        @BindView(R.id.fragment_store_gridview1_more)
        LinearLayout fragment_store_gridview1_more;
        @BindView(R.id.fragment_store_gridview_huanyihuan)
        LinearLayout fragment_store_gridview_huanyihuan;

        public BookViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
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

    private int Huanyihuan(String recommend_id, int style, List<StroreBookcLable.Book> bookList, AdaptionGridView fragment_store_gridview3_gridview_first, AdaptionGridView fragment_store_gridview3_gridview_second, AdaptionGridView fragment_store_gridview3_gridview_fore) {
        int size = bookList.size();
        int minSize = 0;
        int ItemHeigth = 0, raw = 0, start = 0;
        String referPage = LanguageUtil.getString(activity, R.string.refer_page_home_column) + " " + LanguageUtil.getString(activity, R.string.refer_page_column_id) + recommend_id;
        if (style == BOOK_UI_STYLE_1) {
            isHorizontal = false;
            minSize = Math.min(size, 3);
            ItemHeigth = H100 + HEIGHT + H50;
            fragment_store_gridview3_gridview_first.setNumColumns(3);
        } else if (style == BOOK_UI_STYLE_2) {
            isHorizontal = false;
            minSize = Math.min(size, 6);
            fragment_store_gridview3_gridview_first.setNumColumns(3);
            if (minSize > 3) {
                raw = 2;
                ItemHeigth = H100 + (HEIGHT + H50) * 2;
            } else {
                raw = 1;
                ItemHeigth = H100 + HEIGHT + H50;
            }
        } else if (style == BOOK_UI_STYLE_3) {
            isHorizontal = false;
            minSize = Math.min(size, 3);
            ItemHeigth = H100 + HEIGHT + H50;
            fragment_store_gridview3_gridview_first.setNumColumns(3);
            if (size > 3) {
                final List<StroreBookcLable.Book> secondList = bookList.subList(3, Math.min(size, 6));
                ItemHeigth = H100 + HEIGHT + (HEIGHTV + H50) * secondList.size();
                fragment_store_gridview3_gridview_second.setVisibility(View.VISIBLE);
                VerticalAdapter horizontalAdapter = new VerticalAdapter(activity, secondList, WIDTHV, HEIGHTV, true);
                horizontalAdapter.setNeedBackground(false);
                fragment_store_gridview3_gridview_second.setAdapter(horizontalAdapter);
                fragment_store_gridview3_gridview_second.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        activity.startActivity(BookInfoActivity.getMyIntent(activity, referPage, secondList.get(position).getBook_id()));
                    }
                });
            }
        } else if (style == BOOK_UI_STYLE_4) {
            isHorizontal = false;
            start = 1;
            minSize = Math.min(size, 4);
            ItemHeigth = H100 + HEIGHT + H50 + (HEIGHTV + HorizontalSpacing + HorizontalHeight);
            fragment_store_gridview3_gridview_first.setNumColumns(3);
            fragment_store_gridview3_gridview_fore.setVisibility(View.VISIBLE);
            if (bookList.size() > 0) {
                final List<StroreBookcLable.Book> secondList = bookList.subList(0, 1);
                VerticalAdapter horizontalAdapter = new VerticalAdapter(activity, secondList, WIDTHV, HEIGHTV, true);
                horizontalAdapter.setBackground(isBackground);
                horizontalAdapter.setNeedBackground(true);
                isBackground = !isBackground;
                fragment_store_gridview3_gridview_fore.setAdapter(horizontalAdapter);
                fragment_store_gridview3_gridview_fore.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        activity.startActivity(BookInfoActivity.getMyIntent(activity, referPage, secondList.get(position).getBook_id()));
                    }
                });
            }
        } else if (style == BOOK_UI_STYLE_5) {
            minSize = Math.min(size, 4);
            if (minSize <= 2) {
                ItemHeigth = H100 + H50 + HHEIGHT;
            } else {
                ItemHeigth = H100 + (H50 + HHEIGHT) * 2;
            }
            isHorizontal = true;
            fragment_store_gridview3_gridview_first.setNumColumns(2);
        } else if (style == BOOK_UI_STYLE_6) {
            isHorizontal = true;
            minSize = Math.min(size, 6);
            if (minSize <= 2) {
                ItemHeigth = H100 + (H50 + HHEIGHT) * 1;
            } else if (minSize <= 4 && minSize > 2) {
                ItemHeigth = H100 + (H50 + HHEIGHT) * 2;
            } else {
                ItemHeigth = H100 + (H50 + HHEIGHT) * 3;
            }

            fragment_store_gridview3_gridview_first.setNumColumns(2);
        }
        if (bookList.size() > 0) {
            List<StroreBookcLable.Book> firstList = bookList.subList(start, minSize);
            if (isHorizontal) {
                VerticalAdapter verticalAdapter = new VerticalAdapter(activity, firstList, HWIDTH, HHEIGHT, false);
                verticalAdapter.setHorizontal(isHorizontal);
                fragment_store_gridview3_gridview_first.setAdapter(verticalAdapter);
            } else {
                VerticalAdapter verticalAdapter = new VerticalAdapter(activity, firstList, WIDTH, HEIGHT, false);
                verticalAdapter.setHorizontal(isHorizontal);
                fragment_store_gridview3_gridview_first.setAdapter(verticalAdapter);
            }

            fragment_store_gridview3_gridview_first.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    try {
                        if (style == BOOK_UI_STYLE_4) {//特殊处理风格4（横1竖3）
                            position++;
                        }
                        String book_id = bookList.get(position).getBook_id();
                        activity.startActivity(BookInfoActivity.getMyIntent(activity, referPage, book_id));
                    } catch (Exception e) {
                    }
                }
            });
        }
        return ItemHeigth;
    }

    public void postHuanyihuan(String recommend_id, int style, AdaptionGridView fragment_store_gridview3_gridview_first, AdaptionGridView fragment_store_gridview3_gridview_second, AdaptionGridView fragment_store_gridview3_gridview_fore) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("recommend_id", recommend_id);
        String json = params.generateParamsJson();
        String url;
        if (isTopYear) {
            url = ReaderConfig.getBaseUrl() + book_top_year_refresh;
        } else {
            url = ReaderConfig.getBaseUrl() + book_refresh;
        }
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            Gson gson = new Gson();
                            List<StroreBookcLable.Book> bookList = gson.fromJson(new JSONObject(result).getString("list"), new TypeToken<List<StroreBookcLable.Book>>() {
                            }.getType());
                            if (!bookList.isEmpty()) {
                                Huanyihuan(recommend_id, style, bookList, fragment_store_gridview3_gridview_first, fragment_store_gridview3_gridview_second, fragment_store_gridview3_gridview_fore);
                                setChangeRecommendationEvent(style, recommend_id, bookList);
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

    /**
     * 点击换一换 加入神策埋点
     *
     * @param column_id
     * @param bookList
     */
    private void setChangeRecommendationEvent(int style, String column_id, List<StroreBookcLable.Book> bookList) {
        try {
            int minSize = 0;
            int size = bookList.size();
            List<StroreBookcLable.Book> subListComics = new ArrayList<>();
            switch (style) {
                case BOOK_UI_STYLE_1:
                case BOOK_UI_STYLE_3:
                    minSize = Math.min(size, 3);
                    break;
                case BOOK_UI_STYLE_2:
                    minSize = Math.min(size, 6);
                    break;
                case BOOK_UI_STYLE_4:
                    minSize = Math.min(size, 4);
                    break;
            }
            subListComics.addAll(bookList.subList(0, minSize));
            List<String> workId = new ArrayList<>();
            for (StroreBookcLable.Book book : subListComics) {
                workId.add(book.book_id);
            }
            SensorsDataHelper.setChangeRecommendationEvent(WORKS_TYPE_BOOK, Integer.valueOf(column_id), workId);
        } catch (Exception e) {
        }
    }
}