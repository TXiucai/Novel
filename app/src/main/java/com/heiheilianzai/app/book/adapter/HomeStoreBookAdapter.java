package com.heiheilianzai.app.book.adapter;

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
import com.heiheilianzai.app.activity.BookInfoActivity;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.book.been.StroreBookcLable;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.AdaptionGridView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.book.config.BookConfig.book_refresh;
import static com.heiheilianzai.app.config.ReaderConfig.LOOKMORE;

/**
 * 首页小说 Adapter
 */
public class HomeStoreBookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Activity activity;
    List<StroreBookcLable> listData;
    public int WIDTH, WIDTHV, HEIGHT, HEIGHTV, HorizontalSpacing, H100, H50, H20, H30;

    public HomeStoreBookAdapter(Activity activity, List<StroreBookcLable> listData) {
        this.activity = activity;
        this.listData = listData;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        H30 = WIDTH / 5;
        H20 = ImageUtil.dp2px(activity, 20);
        WIDTH = (WIDTH - H20) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HorizontalSpacing = ImageUtil.dp2px(activity, 3);//横间距
        WIDTHV = WIDTH - ImageUtil.dp2px(activity, 26);//竖向 图片宽度
        HEIGHTV = (int) (((float) WIDTHV * 4f / 3f));//
        H100 = ImageUtil.dp2px(activity, 100);
        H50 = ImageUtil.dp2px(activity, 55);
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
                viewHolder = new HomeStoreBookAdapter.AdViewHolder(view);
                return viewHolder;
            case 2:
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
                            .putExtra("PRODUCT", true)
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
        int ItemHeigth = Huanyihuan(stroreComicLable.style, stroreComicLable.list, holder.fragment_store_gridview3_gridview_first, holder.fragment_store_gridview3_gridview_second, holder.fragment_store_gridview3_gridview_fore);

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
        @BindView(R2.id.fragment_store_gridview3_text)
        TextView fragment_store_gridview3_text;
        @BindView(R2.id.fragment_store_gridview3_gridview_first)
        AdaptionGridView fragment_store_gridview3_gridview_first;
        @BindView(R2.id.fragment_store_gridview3_gridview_second)
        AdaptionGridView fragment_store_gridview3_gridview_second;
        @BindView(R2.id.fragment_store_gridview3_gridview_fore)
        AdaptionGridView fragment_store_gridview3_gridview_fore;
        @BindView(R2.id.fragment_store_gridview1_view1)
        View fragment_store_gridview1_view1;
        @BindView(R2.id.fragment_store_gridview1_view2)
        View fragment_store_gridview1_view2;
        @BindView(R2.id.fragment_store_gridview1_view3)
        View fragment_store_gridview1_view3;
        @BindView(R2.id.fragment_store_gridview1_more)
        LinearLayout fragment_store_gridview1_more;
        @BindView(R2.id.fragment_store_gridview_huanyihuan)
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
                            Gson gson = new Gson();
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
}
