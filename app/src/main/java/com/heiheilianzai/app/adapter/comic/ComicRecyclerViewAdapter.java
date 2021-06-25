package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
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

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.piasy.biv.view.BigImageView;
import com.github.piasy.biv.view.GlideImageViewFactory;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.comic.BaseComicImage;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.utils.CommonUtil;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.view.comic.DanmuRelativeLayout;
import com.heiheilianzai.app.view.comic.ProgressPieIndicator;
import com.liulishuo.filedownloader.i.IFileDownloadIPCCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.constant.ComicConfig.IS_OPEN_DANMU;
import static com.heiheilianzai.app.constant.ReaderConfig.getMAXheigth;

/**
 * 阅读漫画Adapter
 * Created by abc on 2017/4/28.
 */
public class ComicRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    int WIDTH;
    int HEIGHT;
    int MAXheigth;
    int ad = 1;
    private List<BaseComicImage> list;
    private Activity activity;
    int tocao_bgcolor;
    int size;
    View activity_comic_look_foot;
    ComicLookActivity.ItemOnclick itemOnclick;
    public List<DanmuRelativeLayout> relativeLayoutsDanmu = new ArrayList<>();
    private boolean mIsAlbum;
    private int mSmall;//0未开启小图模式，1为大图，2为中图，3为小图

    public int getmSmall() {
        return mSmall;
    }

    public void setmSmall(int mSmall) {
        this.mSmall = mSmall;
        notifyDataSetChanged();
    }

    public void setmIsAlbum(boolean mIsAlbum) {
        this.mIsAlbum = mIsAlbum;
    }

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
        } else if (position == 0 && list.get(0).getAd() == 1) {
            return ad;
        } else {
            return super.getItemViewType(position);
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 888) {
            return new MyViewHolderFoot(activity_comic_look_foot);
        } else if (viewType == ad) {
            View rootView = LayoutInflater.from(activity).inflate(R.layout.item_comic_recyclerview_ad, parent, false);
            return new MyAdViewHolder(rootView);
        } else {
            View rootView = LayoutInflater.from(activity).inflate(R.layout.item_comic_recyclerview_, parent, false);
            return new MyViewHolder(rootView);
        }

    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holderr, final int position) {
        try {
            if (holderr instanceof MyViewHolder) {
                if (position < size) {
                    MyViewHolder holder = (MyViewHolder) holderr;
                    final BaseComicImage baseComicImage = list.get(position);
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holder.item_comic_recyclerview_layout.getLayoutParams();
                    if (mSmall == 0) {
                        int trueHeigth = Math.min(MAXheigth, WIDTH * baseComicImage.height / baseComicImage.width);
                        layoutParams.setMargins(0, 0, 0, 0);
                        layoutParams.height = trueHeigth;//默认
                        layoutParams.width = WIDTH;
                    } else if (mSmall == 1) {
                        int trueHeigth = Math.min(MAXheigth, WIDTH * baseComicImage.height / baseComicImage.width);
                        layoutParams.setMargins(0, 0, 0, 0);
                        layoutParams.height = trueHeigth;//默认
                        layoutParams.width = WIDTH;
                    } else if (mSmall == 2) {
                        int trueHeigth = (HEIGHT - (int) CommonUtil.convertDpToPixel(activity, 20)) / 2;
                        layoutParams.setMargins(0, 0, 0, (int) CommonUtil.convertDpToPixel(activity, 10));
                        layoutParams.height = trueHeigth;
                        layoutParams.width = trueHeigth;
                    } else if (mSmall == 3) {
                        int trueHeigth = (HEIGHT - (int) CommonUtil.convertDpToPixel(activity, 30)) / 3;
                        layoutParams.setMargins(0, 0, 0, (int) CommonUtil.convertDpToPixel(activity, 10));
                        layoutParams.height = trueHeigth;
                        layoutParams.width = trueHeigth;
                    }

                    if (mIsAlbum) {
                        layoutParams.setMargins(0, 0, 0, (int) CommonUtil.convertDpToPixel(activity, 7));
                    }
                    Uri uri = getUri(baseComicImage);
                    if (uri != null) {
                        holder.recyclerview_img.showImage(uri);
                    }
                    //阻隔BigImageView控件与父控件事件冲突
                    holder.ban_touch_float.setOnClickListener(null);
                    holder.item_comic_recyclerview_layout.setLayoutParams(layoutParams);
                    holder.item_comic_recyclerview_danmu.setPosition(position);
                    relativeLayoutsDanmu.add(holder.item_comic_recyclerview_danmu);
                    if (IS_OPEN_DANMU(activity) && holder.item_comic_recyclerview_danmu.getPosition() == position) {
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
                    holder.recyclerview_img.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (itemOnclick != null && mSmall > 0) {
                                itemOnclick.onClick(position, baseComicImage);
                            }
                        }
                    });

                }
            } else if (holderr instanceof MyAdViewHolder) {
                MyAdViewHolder holderAd = (MyAdViewHolder) holderr;
                BaseComicImage comicImage = list.get(position);
                MyPicasso.GlideImageNoSize(activity, comicImage.getImage(), holderAd.ivAD);
                if (comicImage.getAd() == 1) {
                    holderAd.ivAD.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.setClass(activity, WebViewActivity.class);
                            intent.putExtra("url", comicImage.getAd_skip_url());
                            intent.putExtra("ad_url_type", comicImage.getAd_url_type());
                            activity.startActivity(intent);
                        }
                    });
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
        @BindView(R.id.item_comic_recyclerview_photoview)
        BigImageView recyclerview_img;
        @BindView(R.id.item_comic_recyclerview_danmu)
        DanmuRelativeLayout item_comic_recyclerview_danmu;
        @BindView(R.id.item_comic_recyclerview_layout)
        FrameLayout item_comic_recyclerview_layout;
        @BindView(R.id.ban_touch_float)
        View ban_touch_float;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            recyclerview_img.setProgressIndicator(new ProgressPieIndicator());//自定义加载进度条
            recyclerview_img.setImageViewFactory(new GlideImageViewFactory());//兼容漫画图片内部有GIF编码问题
        }
    }

    class MyAdViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.item_comic_ad)
        ImageView ivAD;

        public MyAdViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private Uri getUri(BaseComicImage baseComicImage) {
        File localPathFile = FileManager.getManhuaSDCardRootImg(baseComicImage);
        if (localPathFile != null) {
            return FileManager.getImageContentUri(activity.getApplicationContext(), localPathFile);
        }
        String url = baseComicImage.image;
        if (!StringUtils.isEmpty(url)) {
            return Uri.parse(url);
        }
        return null;
    }
}