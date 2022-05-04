package com.heiheilianzai.app.ui.activity.comic;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.StoreComicAdapter;
import com.heiheilianzai.app.adapter.comic.ComicHChapterCatalogAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseWarmStartActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BookInfoComment;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.ComicInfo;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.model.event.comic.ComicChapterEventbus;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.ui.activity.ReplyCommentActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogComicChapter;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.BlurImageview;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.heiheilianzai.app.view.foldtextview.ExpandableTextView;
import com.jaeger.library.StatusBarUtil;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作品详情
 */
public class ComicInfoActivity extends BaseWarmStartActivity {
    public static final String COMIC_ID_EXT_KAY = "comic_id";//进小说简介 传入小说id
    @BindView(R.id.titlebar_share)
    RelativeLayout titlebar_share;
    @BindView(R.id.book_info_titlebar_container_shadow)
    public View book_info_titlebar_container_shadow;
    @BindView(R.id.book_info_titlebar_container)
    public RelativeLayout book_info_titlebar_container;
    @BindView(R.id.activity_book_info_scrollview)
    public ObservableScrollView activity_book_info_scrollview;
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.back)
    public ImageView back;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.activity_comic_cover_bg)
    public ImageView activity_comic_cover_bg;
    @BindView(R.id.rl_comic_vip)
    public RelativeLayout rl_comic_vip;
    @BindView(R.id.tx_comic_name)
    public TextView tx_comic_name;
    @BindView(R.id.tx_comic_description)
    public ExpandableTextView etv;
    @BindView(R.id.tx_comic_status)
    public TextView tx_comic_status;
    @BindView(R.id.tx_comic_num)
    public TextView tx_comic_num;
    @BindView(R.id.ll_comic_category)
    public LinearLayout ll_comic_category;
    @BindView(R.id.ry_comic_category)
    public RecyclerView ry_comic_category;
    @BindView(R.id.tx_add_comment)
    public TextView tx_add_comment;
    @BindView(R.id.ll_comment_container)
    public LinearLayout ll_comment_container;
    @BindView(R.id.ll_label_container)
    public LinearLayout ll_label_container;
    @BindView(R.id.img_comic_collect)
    public ImageView img_comic_collect;
    @BindView(R.id.tx_comic_add)
    public TextView tx_comic_add;
    @BindView(R.id.tx_comic_start_read)
    public TextView tx_comic_start_read;
    @BindView(R.id.tx_comic_down)
    public TextView tx_comic_down;
    @BindView(R.id.list_ad_view_layout)
    FrameLayout activity_book_info_ad;
    @BindView(R.id.tx_comic_flag)
    public TextView tx_comic_flag;
    @BindView(R.id.list_ad_view_img)
    ImageView list_ad_view_img;
    Activity activity;
    BaseComic baseComic, baseComicLocal;
    String comic_id;
    Gson gs = new Gson();
    StroreComicLable stroreComicLable;
    List<BookInfoComment> bookInfoComment;
    StroreComicLable.Comic comic;
    BaseAd baseAd;
    List<ComicChapter> comicChapter = new ArrayList<>();
    Drawable activity_comic_info_topbarD;
    boolean refreshComment;
    ComicInfo mComicInfo;//漫画具体信息
    public int WIDTH, HEIGHT, mPageNum = 1, mTotalPage;
    ComicHChapterCatalogAdapter comicChapterCatalogAdapter;
    private int size;
    private int lastVisibleItemPosition;
    private boolean isShowSdkAd = false;
    private Dialog mDialogChapter;

    @OnClick(value = {R.id.tx_comic_start_read, R.id.titlebar_back, R.id.list_ad_view_layout,
            R.id.ll_comic_collect, R.id.ll_comic_down, R.id.ll_comic_category, R.id.rl_comic_vip})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.tx_comic_start_read:
                if (baseComic != null && comicChapter != null) {
                    baseComic.saveIsexist(false);
                    Intent intent = ComicLookActivity.getMyIntent(activity, baseComic, LanguageUtil.getString(this, R.string.refer_page_info));
                    intent.putExtra(ComicLookActivity.FORM_INFO_EXT_KAY, true);
                    startActivity(intent);
                } else {
                    MyToash.ToashError(activity, "漫画正在更新中~");
                }
                break;
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.ll_comic_collect:
                if (!baseComic.isAddBookSelf()) {
                    baseComic.saveIsexist(true, Utils.getUID(activity));
                    tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
                    img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect));
                    MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
                    addSelfCollect();
                    EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                } else {
                    MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.fragment_comic_info_delshoucang));
                    tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_shoucang));
                    img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect_no));
                    LitePal.delete(BaseComic.class, baseComic.getId());
                    baseComic.setAddBookSelf(false);
                    EventBus.getDefault().post(new RefreshComic(baseComic, 0));
                }
                break;
            case R.id.ll_comic_down:
                if (App.isVip(activity)) {
                    if (baseComic != null && comicChapter != null) {
                        baseComic.saveIsexist(false);
                        Intent intent = new Intent(activity, ComicDownActivity.class);
                        intent.putExtra("baseComic", baseComic);
                        startActivity(intent);
                    } else {
                        MyToash.ToashError(activity, getString(R.string.comic_loading));
                    }
                } else {
                    MyToash.Toash(activity, getString(R.string.down_toast_msg));
                }
                break;
            case R.id.ll_comic_category:
                DialogComicChapter dialogComicChapter = new DialogComicChapter();
                if (mDialogChapter != null && mDialogChapter.isShowing()) {
                    return;
                }
                mDialogChapter = dialogComicChapter.getDialogVipPop(activity, baseComic);
                break;
            case R.id.rl_comic_vip:
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 5);
                myIntent.putExtra("isvip", Utils.isLogin(activity));
                activity.startActivity(myIntent);
                break;
            case R.id.list_ad_view_layout:
                if (baseAd != null) {
                    if (!TextUtils.isEmpty(baseAd.getAdId())) {
                        AdInfo adInfo = new AdInfo();
                        adInfo.setAdId(baseAd.getAdId());
                        adInfo.setAdPosId(baseAd.getAdPosId());
                        adInfo.setAdPosId(baseAd.getRequestId());
                        XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                    }
                    Intent intent = new Intent();
                    intent.setClass(activity, WebViewActivity.class);
                    String ad_skip_url = baseAd.ad_skip_url;
                    if (Utils.isLogin(activity) && TextUtils.equals(baseAd.getUser_parame_need(), "2") && !ad_skip_url.contains("&uid=")) {
                        ad_skip_url += "&uid=" + Utils.getUID(activity);
                    }
                    intent.putExtra("url", ad_skip_url);
                    intent.putExtra("title", baseAd.ad_title);
                    intent.putExtra("advert_id", baseAd.advert_id);
                    intent.putExtra("ad_url_type", baseAd.ad_url_type);
                    activity.startActivity(intent);
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        HEIGHT = ScreenSizeUtils.getInstance(activity).getScreenHeight();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //侵染状态栏
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_comicinfo);
        ButterKnife.bind(this);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {                                  //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));                   //需要在setContentView()方法后面执行
        }
        EventBus.getDefault().register(this);
        MyContentLinearLayoutManager linearLayoutManager = new MyContentLinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        ry_comic_category.setLayoutManager(linearLayoutManager);
        ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
        layoutParams.height = layoutParams.width / 3;
        list_ad_view_img.setLayoutParams(layoutParams);
        init();
    }

    public void init() {
        if (!ReaderConfig.USE_SHARE) {
            titlebar_share.setVisibility(View.GONE);
        }
        activity_book_info_scrollview.setScrollViewListener(new ObservableScrollView.ScrollViewListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
                if (y <= 0) {
                    setStatusTextColor(false, activity);
                    back.setBackgroundResource(R.mipmap.back_white);
                } else {
                    setStatusTextColor(true, activity);
                    back.setBackgroundResource(R.mipmap.back_black);
                }
                final float ratio = (float) Math.min(Math.max(y, 0), 120) / 120;
                float alpha = (int) (ratio * 255);
                book_info_titlebar_container.setBackgroundColor(Color.argb((int) alpha, 255, 255, 255));
                titlebar_text.setAlpha(ratio);
                book_info_titlebar_container_shadow.setAlpha(ratio);
            }
        });

        ry_comic_category.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                }
                if (comicChapterCatalogAdapter != null && newState == RecyclerView.SCROLL_STATE_IDLE && lastVisibleItemPosition >= comicChapter.size() - 1) {
                    if (mTotalPage >= mPageNum) {
                        getDataCatalogInfo();
                    } else {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                    }
                }
            }
        });
        httpData();
    }

    public void handdata() {
        if (!refreshComment) {
            MyPicasso.GlideImageRoundedCorners(12, activity, comic.vertical_cover, activity_comic_cover_bg, ImageUtil.dp2px(activity, 135), ImageUtil.dp2px(activity, 180), R.mipmap.comic_def_v);
            if (comic.horizontal_cover.length() > 0) {
                MyPicasso.GlideImage(activity, comic.horizontal_cover, activity_comic_cover_bg, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ImageUtil.dp2px(activity, 205), R.mipmap.comic_def_cross);
            } else {
                MyPicasso.GlideImageRoundedGasoMohu(activity, comic.vertical_cover, activity_comic_cover_bg, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ImageUtil.dp2px(activity, 205), R.mipmap.comic_def_cross);
            }
            try {
                if (activity != null && !activity.isFinishing()) {
                    Glide.with(this).asBitmap().load(comic.horizontal_cover).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            activity_comic_info_topbarD = BlurImageview.reloadCoverBg(activity, resource);
                        }
                    });
                }
            } catch (Exception e) {
            }
            tx_comic_name.setText(comic.name);
            String[] split = comic.hot_num.split(" ");
            if (split.length > 1) {
                tx_comic_num.setText(split[1]);
            } else {
                tx_comic_num.setText(comic.hot_num);
            }

            int viewWidth = getWindowManager().getDefaultDisplay().getWidth() - ImageUtil.dp2px(ComicInfoActivity.this, 20f);
            etv.initWidth(viewWidth);
            etv.setMaxLines(2);
            etv.setHasAnimation(false);
            etv.setCloseInNewLine(true);
            etv.setOpenSuffixColor(getResources().getColor(R.color.white));
            etv.setCloseSuffixColor(getResources().getColor(R.color.white));
            etv.setOriginalText(comic.description);

            tx_comic_status.setText(comic.tag.get(0).getTab());
            tx_comic_flag.setText(String.format(getString(R.string.comicinfo_total_chapter), comic.total_chapters));
            titlebar_text.setText(comic.name);
            titlebar_text.setAlpha(0);

            book_info_titlebar_container_shadow.setAlpha(0);
            baseComic.setVertical_cover(comic.vertical_cover);
            baseComic.setHorizontal_cover(comic.horizontal_cover);
            baseComic.setName(comic.name);
            baseComic.setAuthor(comic.author);
            baseComic.setDescription(comic.description);
            baseComic.setFlag(comic.flag);
            baseComic.setFinished(comic.is_finish);
            baseComic.setAuthor(comic.author);
            baseComic.setFlag(comic.flag);
            baseComic.setTotal_chapters(comic.total_chapters);
            getSdkAd();
            ll_comment_container.removeAllViews();
            ll_label_container.removeAllViews();
            try {
                if (bookInfoComment != null || !bookInfoComment.isEmpty()) {
                    for (BookInfoComment bookInfoComment : bookInfoComment) {
                        LinearLayout commentView = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.activity_book_info_content_comment_item, null, false);
                        CircleImageView activity_book_info_content_comment_item_avatar = commentView.findViewById(R.id.activity_book_info_content_comment_item_avatar);
                        TextView activity_book_info_content_comment_item_nickname = commentView.findViewById(R.id.activity_book_info_content_comment_item_nickname);
                        TextView activity_book_info_content_comment_item_content = commentView.findViewById(R.id.activity_book_info_content_comment_item_content);
                        TextView activity_book_info_content_comment_item_reply = commentView.findViewById(R.id.activity_book_info_content_comment_item_reply_info);
                        TextView activity_book_info_content_comment_item_time = commentView.findViewById(R.id.activity_book_info_content_comment_item_time);
                        View comment_item_isvip = commentView.findViewById(R.id.comment_item_isvip);
                        MyPicasso.IoadImage(activity, bookInfoComment.getAvatar(), R.mipmap.icon_def_head, activity_book_info_content_comment_item_avatar);
                        activity_book_info_content_comment_item_nickname.setText(bookInfoComment.getNickname());
                        activity_book_info_content_comment_item_content.setText(bookInfoComment.getContent());
                        activity_book_info_content_comment_item_reply.setText(bookInfoComment.getReply_info());
                        activity_book_info_content_comment_item_reply.setVisibility(TextUtils.isEmpty(bookInfoComment.getReply_info()) ? View.GONE : View.VISIBLE);
                        activity_book_info_content_comment_item_time.setText(bookInfoComment.getTime());
                        comment_item_isvip.setVisibility(bookInfoComment.getIs_vip() == 1 ? View.VISIBLE : View.GONE);
                        //评论点击的处理
                        commentView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(activity, ReplyCommentActivity.class);
                                intent.putExtra("comic_id", baseComic.getComic_id());
                                intent.putExtra("comment_id", bookInfoComment.getComment_id());
                                intent.putExtra("avatar", bookInfoComment.getAvatar());
                                intent.putExtra("nickname", bookInfoComment.getNickname());
                                intent.putExtra("origin_content", bookInfoComment.getContent());
                                startActivity(intent);
                            }
                        });
                        ll_comment_container.addView(commentView);
                    }
                }
                //"查看全部评论"
                String moreText;
                if (comic.total_comment > 0) {
                    moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_lookpinglun);
                } else {
                    moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_nopinglun);
                }
                LinearLayout commentMoreView = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.activity_book_info_content_comment_more, null, false);
                TextView activity_book_info_content_comment_more_text = commentMoreView.findViewById(R.id.activity_book_info_content_comment_more_text);
                activity_book_info_content_comment_more_text.setText(String.format(moreText, comic.total_comment + ""));
                tx_add_comment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(
                                new Intent(activity, ComicCommentActivity.class).
                                        putExtra("comic_id", comic.comic_id).
                                        putExtra("IsBook", false), 11);
                    }
                });
                commentMoreView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(activity, ComicCommentActivity.class).putExtra("comic_id", comic.comic_id).putExtra("IsBook", false), 11);
                    }
                });
                ll_comment_container.addView(commentMoreView);
                if (stroreComicLable != null && !stroreComicLable.list.isEmpty()) {
                    List<StroreComicLable.Comic> comicList = stroreComicLable.list;
                    View type1 = LayoutInflater.from(activity).inflate(R.layout.fragment_store_comic_layout, null, false);
                    TextView lable = type1.findViewById(R.id.fragment_store_gridview1_text);
                    lable.setText(stroreComicLable.label);
                    LinearLayout fragment_store_gridview1_huanmore = type1.findViewById(R.id.fragment_store_gridview1_huanmore);
                    fragment_store_gridview1_huanmore.setVisibility(View.GONE);
                    AdaptionGridViewNoMargin fragment_store_gridview1_gridview = type1.findViewById(R.id.fragment_store_gridview1_gridview);
                    StoreComicAdapter storeComicAdapter;
                    fragment_store_gridview1_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            String comic_id = comicList.get(position).comic_id;
                            activity.startActivity(ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_info) + " " + comic_id, comic_id));
                        }
                    });
                    fragment_store_gridview1_gridview.setNumColumns(3);
                    int width = WIDTH / 3;
                    int height = width * 4 / 3;
                    double size = Math.min(6, comicList.size());
                    storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size), activity, 2, width, height);
                    fragment_store_gridview1_gridview.setAdapter(storeComicAdapter);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(0, 20, 0, 0);
                    params.height = height * (int) (Math.ceil(size / 3d)) + ImageUtil.dp2px(activity, 170);
                    ll_label_container.addView(type1, params);
                }
            } catch (Exception e) {
            }
        }
    }

    private void getSdkAd() {
        //替换第三方广告
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "7") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                isShowSdkAd = true;
                XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_DETAIL_DEBUG : BuildConfig.XAD_EVN_POS_COMIC_DETAIL, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
                    @Override
                    public void onRequestOk(List<AdInfo> list) {
                        try {
                            AdInfo adInfo = list.get(0);
                            if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                                if (baseAd == null) {
                                    baseAd = new BaseAd();
                                }
                                baseAd.setRequestId(adInfo.getRequestId());
                                baseAd.setAdPosId(adInfo.getAdPosId());
                                baseAd.setAdId(adInfo.getAdId());
                                baseAd.setAd_skip_url(adInfo.getOperation().getValue());
                                baseAd.setAd_title(adInfo.getMaterial().getTitle());
                                baseAd.setAd_image(adInfo.getMaterial().getImageUrl());
                                baseAd.setUser_parame_need("1");
                                baseAd.setAd_url_type(adInfo.getOperation().getType());
                                MyPicasso.glideSdkAd(activity, adInfo, baseAd.ad_image, list_ad_view_img);
                                activity_book_info_ad.setVisibility(View.VISIBLE);
                            } else {
                                activity_book_info_ad.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            localAd();
                        }
                    }

                    @Override
                    public void onRequestFailed(int i, String s) {
                        localAd();
                    }
                });
                return;
            }
        }
        if (!isShowSdkAd) {
            localAd();
        }
    }

    private void localAd() {
        if (baseAd != null) {
            activity_book_info_ad.setVisibility(View.VISIBLE);
            MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
        } else {
            activity_book_info_ad.setVisibility(View.GONE);
        }
    }

    public void httpData() {
        Intent intent = getIntent();
        comic_id = intent.getStringExtra(COMIC_ID_EXT_KAY);
        if (comic_id == null) {
            baseComic = (BaseComic) intent.getSerializableExtra("baseComic");
            if (baseComic != null) {
                comic_id = baseComic.getComic_id();
            } else {
                return;
            }
        } else {
            baseComic = new BaseComic();
            baseComic.setComic_id(comic_id);
            baseComicLocal = LitePal.where("comic_id = ?", comic_id).findFirst(BaseComic.class);
            if (baseComicLocal != null) {
                baseComic.setAddBookSelf(baseComicLocal.isAddBookSelf());
                baseComic.setCurrent_chapter_id(baseComicLocal.getCurrent_chapter_id());
                baseComic.setCurrent_display_order(baseComicLocal.getCurrent_display_order());
                baseComic.setCurrent_chapter_name(baseComicLocal.getCurrent_chapter_name());
                baseComic.setChapter_text(baseComicLocal.getChapter_text());
                baseComic.setDown_chapters(baseComicLocal.getDown_chapters());
                baseComic.setId(baseComicLocal.getId());
                MyToash.Log("baseComicid", baseComic.getId() + "  " + baseComic.getCurrent_display_order());
            } else {
                baseComic.setAddBookSelf(false);
            }
        }
        comicChapterCatalogAdapter = new ComicHChapterCatalogAdapter(baseComic, activity, comicChapter);
        ry_comic_category.setAdapter(comicChapterCatalogAdapter);
        if (baseComic.isAddBookSelf()) {
            tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
            img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect));
        } else {
            tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_shoucang));
            img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect_no));
        }
        httpData2(false);
    }

    private void httpData2(boolean refreshComment) {
        this.refreshComment = refreshComment;
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_info, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            updateRecord();
                            mComicInfo = gs.fromJson(result, ComicInfo.class);
                            if (mComicInfo != null) {
                                comic = mComicInfo.comic;
                                stroreComicLable = mComicInfo.label.get(0);
                                bookInfoComment = mComicInfo.comment;
                                baseAd = mComicInfo.advert;
                            }
                            handdata();
                            setMHIntroPageEvent();
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
        getDataCatalogInfo();//获取小说目录
    }

    private void updateRecord() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_info_record, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void getDataCatalogInfo() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("page", "" + mPageNum);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_catalog, json, true, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(final String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JsonParser jsonParser = new JsonParser();
                    String coupon_pay_price = jsonObject.getString("coupon_pay_price");
                    String is_limited_free = jsonObject.getString("is_limited_free");
                    AppPrefs.putSharedString(activity, PrefConst.COUPON_COMICI_PRICE, coupon_pay_price);
                    mTotalPage = jsonObject.getInt("total_page");
                    JsonArray jsonElements = jsonParser.parse(jsonObject.getString("chapter_list")).getAsJsonArray();//获取JsonArray对象
                    for (JsonElement jsonElement : jsonElements) {
                        ComicChapter comicChapter1 = new Gson().fromJson(jsonElement, ComicChapter.class);
                        comicChapter1.setIs_limited_free(is_limited_free);
                        if (comicChapter1.getAd_image() == null) {
                            comicChapter.add(comicChapter1);
                        }
                    }
                    if (comicChapter != null && !comicChapter.isEmpty()) {
                        if (mPageNum == 1) {
                            comicChapterCatalogAdapter.notifyDataSetChanged();
                        } else {
                            comicChapterCatalogAdapter.notifyItemRangeInserted(size, comicChapter.size());
                        }
                        mPageNum++;
                    }
                    size = comicChapter.size();
                } catch (Exception E) {
                }
            }

            @Override
            public void onErrorResponse(String ex) {
                if (ex != null && ex.equals("nonet")) {
                    MyToash.Log("nonet", "11");
                    if (comicChapter != null && !comicChapter.isEmpty()) {
                        comicChapterCatalogAdapter.notifyDataSetChanged();
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        mPageNum = 1;
        comicChapter.clear();
        getDataCatalogInfo();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreashComicInfoActivity refreshBookInfo) {
        if (refreshBookInfo.isSave) {
            baseComic.setAddBookSelf(true);
            tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
            img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect));
            addSelfCollect();
        } else {
            httpData2(false);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(ComicChapterEventbus comicChapterte) {//更新当前目录集合的 最近阅读图片记录
        ComicChapter comicChaptert = comicChapterte.comicChapter;
        ComicChapter c = comicChapter.get(comicChaptert.display_order);
        switch (comicChapterte.Flag) {
            case 0://更新最近阅读章节图片
                c.current_read_img_order = comicChaptert.current_read_img_order;
                break;
            case 1://更新下载数据
                c.setISDown(1);
                c.setImagesText(comicChaptert.ImagesText);
                ContentValues values = new ContentValues();
                values.put("ImagesText", comicChaptert.ImagesText);
                values.put("ISDown", "1");
                int i = LitePal.update(ComicChapter.class, values, c.getId());
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)//更新当前书籍的阅读进度
    public void refreshBasecomic(BaseComic baseComic1) {
        baseComic.setCurrent_display_order(baseComic1.getCurrent_display_order());
        baseComic.setCurrent_chapter_id(baseComic1.getCurrent_chapter_id());
        baseComic.setCurrent_chapter_name(baseComic1.getCurrent_chapter_name());
        if (!baseComic.isAddBookSelf() && baseComic1.isAddBookSelf()) {
            baseComic.setAddBookSelf(true);
            tx_comic_add.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
            img_comic_collect.setImageDrawable(getResources().getDrawable(R.mipmap.comic_collect));
            addSelfCollect();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (MainActivity.activity == null) {
                startActivity(new Intent(ComicInfoActivity.this, MainActivity.class));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void addSelfCollect() {
        ContentValues values = new ContentValues();
        values.put("isAddBookSelf", baseComic.isAddBookSelf());
        LitePal.update(BaseComic.class, values, baseComic.getId());
    }


    /**
     * 进入漫画简介页必传参数
     *
     * @param context
     * @param referPage 神策埋点数据从哪个页面进入
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String comicId) {
        Intent intent = new Intent(context, ComicInfoActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(COMIC_ID_EXT_KAY, comicId);
        return intent;
    }

    /**
     * 神策埋点 进入漫画简介页
     */
    private void setMHIntroPageEvent() {
        try {
            SensorsDataHelper.setMHIntroPageEvent(getPropIdList(),//属性ID
                    getTagIdList(),//分类ID
                    getIntent().getStringExtra(SaVarConfig.REFER_PAGE_VAR),//前向页面
                    Integer.valueOf(StringUtils.isEmpty(comic_id) ? "0" : comic_id),//小说ID
                    comic == null ? 0 : Integer.valueOf(comic.total_chapters),//小说总章节
                    0);//作者ID
        } catch (Exception e) {
        }
    }

    /**
     * 神策埋点 获取prop_id属性数据
     */
    private List<String> getPropIdList() {
        return ComicLookActivity.getPropIdList(mComicInfo);
    }

    /**
     * 神策埋点 获取tag_id分类信息
     */
    private List<String> getTagIdList() {
        return ComicLookActivity.getTagIdList(mComicInfo);
    }
}