package com.heiheilianzai.app.comic.adapter;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.comic.activity.ComicLookActivity;
import com.heiheilianzai.app.comic.been.BaseComicImage;
import com.heiheilianzai.app.comic.been.MyGlide;
import com.heiheilianzai.app.comic.view.DanmuRelativeLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.comic.config.ComicConfig.IS_OPEN_DANMU;
import static com.heiheilianzai.app.config.ReaderConfig.getMAXheigth;

/**
 * Created by abc on 2017/4/28.
 */
public class ComicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int WIDTH;
    int HEIGHT;
    int MAXheigth;
    private List<BaseComicImage> list;
    private Activity activity;
    int tocao_bgcolor;
    int size;
    View activity_comic_look_foot;
    ComicLookActivity.ItemOnclick itemOnclick;
    public List<DanmuRelativeLayout> relativeLayoutsDanmu = new ArrayList<>();

    public ComicRecyclerViewAdapter(Activity activity, int WIDTH, int HEIGHT, List<BaseComicImage> list, View activity_comic_look_foot, int Size, ComicLookActivity.ItemOnclick itemOnclick) {
        this.list = list;
        size = Size;
        this.activity = activity;
        this.activity_comic_look_foot = activity_comic_look_foot;
        this.itemOnclick = itemOnclick;
        this.WIDTH = WIDTH;
        this.HEIGHT = HEIGHT;
        MAXheigth = getMAXheigth();
        tocao_bgcolor = Color.parseColor("#4d000000");
    }

    public void NotifyDataSetChanged(int Size) {
        relativeLayoutsDanmu.clear();
        size = Size;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (position == size) {
            return 888;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 888) {
            return new MyViewHolderFoot(activity_comic_look_foot);
        }
        View rootView = LayoutInflater.from(activity).inflate(R.layout.item_comic_recyclerview_, parent, false);
        return new MyViewHolder(rootView);
    }


    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderr, final int position) {
        try {
            if (position < size) {
                MyViewHolder holder = (MyViewHolder) holderr;
                final BaseComicImage baseComicImage = list.get(position);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.item_comic_recyclerview_layout.getLayoutParams();
               int trueHeigth=Math.min(MAXheigth,WIDTH * baseComicImage.height / baseComicImage.width);
                layoutParams.height = trueHeigth;//默认
                holder.item_comic_recyclerview_layout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        itemOnclick.onClick(position, baseComicImage);
                    }
                });
                MyGlide.getMyGlide(activity, WIDTH, HEIGHT).GlideImage(activity, baseComicImage, holder.recyclerview_img, baseComicImage.width,layoutParams.height);
                holder.item_comic_recyclerview_layout.setLayoutParams(layoutParams);
                holder.item_comic_recyclerview_danmu.setPosition(position);
                relativeLayoutsDanmu.add(holder.item_comic_recyclerview_danmu);
                if (IS_OPEN_DANMU(activity)&&holder.item_comic_recyclerview_danmu.getPosition()==position) {
                    holder.item_comic_recyclerview_danmu.removeAllViews();
                    holder.item_comic_recyclerview_danmu.setVisibility(View.VISIBLE);
                    if (baseComicImage.tucao != null && !baseComicImage.tucao.isEmpty()) {
                        for (BaseComicImage.Tucao tucao : baseComicImage.tucao) {
                            if (!TextUtils.isEmpty(tucao.content)) {
                                TextView textView2 = new TextView(activity);
                                textView2.setTextColor(Color.WHITE);
                                textView2.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 13);
                                textView2.setLines(1);
                                textView2.setText(tucao.content);
                                textView2.setPadding(8, 4, 8, 4);
                                GradientDrawable drawable2 = new GradientDrawable();
                                //drawable2.setStroke(1, Color.WHITE);
                                drawable2.setColor(tocao_bgcolor);
                                drawable2.setCornerRadius(20);
                                textView2.setBackgroundDrawable(drawable2);
                                RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                int x = (int) (Math.random() * WIDTH);
                                int y = (int) (Math.random() * (layoutParams.height));
                                layoutParams2.setMargins(x, y, 0, 0);
                                layoutParams2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                holder.item_comic_recyclerview_danmu.addView(textView2, layoutParams2);
                            }
                        }
                    }
                } else {
                    holder.item_comic_recyclerview_danmu.setVisibility(View.GONE);
                }
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    @Override
    public int getItemCount() {
        return size + 1;
    }

    class MyViewHolderFoot extends RecyclerView.ViewHolder {
        public MyViewHolderFoot(@NonNull View itemView) {
            super(itemView);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.item_comic_recyclerview_photoview)
        ImageView recyclerview_img;
        @BindView(R2.id.item_comic_recyclerview_danmu)
        DanmuRelativeLayout item_comic_recyclerview_danmu;
        @BindView(R2.id.item_comic_recyclerview_layout)
        FrameLayout item_comic_recyclerview_layout;
        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}



