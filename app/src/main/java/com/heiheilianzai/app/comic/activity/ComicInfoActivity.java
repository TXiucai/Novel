package com.heiheilianzai.app.comic.activity;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.MainActivity;
import com.heiheilianzai.app.activity.BaseWarmStartActivity;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.bean.BaseAd;
import com.heiheilianzai.app.bean.BaseTag;
import com.heiheilianzai.app.bean.BookInfoComment;
import com.heiheilianzai.app.comic.been.AppBarStateChangeListener;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.comic.been.ComicChapter;
import com.heiheilianzai.app.comic.been.ComicInfo;
import com.heiheilianzai.app.comic.been.RefreashComicInfoActivity;
import com.heiheilianzai.app.comic.been.StroreComicLable;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.comic.eventbus.ComicChapterEventbus;
import com.heiheilianzai.app.comic.eventbus.RefreshComic;
import com.heiheilianzai.app.comic.fragment.ComicinfoCommentFragment;
import com.heiheilianzai.app.comic.fragment.ComicinfoMuluFragment;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.utils.decode.AESUtil;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.BlurImageview;
import com.heiheilianzai.app.view.UnderlinePageIndicatorHalf;
import com.jaeger.library.StatusBarUtil;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 作品详情
 */
public class ComicInfoActivity extends BaseWarmStartActivity {
    @BindView(R2.id.fragment_comicinfo_viewpage)
    public ViewPager fragment_comicinfo_viewpage;
    @BindView(R2.id.activity_comic_info_topbar_downlayout)
    public RelativeLayout activity_comic_info_topbar_downlayout;
    @BindView(R2.id.activity_comic_info_topbar_sharelayout)
    public RelativeLayout activity_comic_info_topbar_sharelayout;
    @BindView(R2.id.fragment_comicinfo_current_chaptername)
    public TextView fragment_comicinfo_current_chaptername;
    @BindView(R2.id.fragment_comicinfo_current_goonread)
    public TextView fragment_comicinfo_current_goonread;
    @BindView(R2.id.activity_book_info_content_xiangqing_text)
    public TextView activity_book_info_content_xiangqing_text;
    @BindView(R2.id.activity_book_info_content_mulu_text)
    public TextView activity_book_info_content_mulu_text;
    @BindView(R2.id.activity_comic_info_top_bookname)
    public TextView activity_comic_info_top_bookname;
    @BindView(R2.id.activity_book_info_content_cover)
    public ImageView activity_book_info_content_cover;
    @BindView(R2.id.activity_book_info_content_name)
    public TextView activity_book_info_content_name;
    @BindView(R2.id.activity_book_info_content_author)
    public TextView activity_book_info_content_author;
    @BindView(R2.id.activity_book_info_content_total_hot)
    public TextView activity_book_info_content_total_hot;
    @BindView(R2.id.activity_book_info_content_shoucang)
    public TextView activity_book_info_content_shoucang;
    @BindView(R2.id.activity_book_info_content_shoucannum)
    public TextView activity_book_info_content_shoucannum;
    @BindView(R2.id.activity_book_info_content_tag)
    public LinearLayout activity_book_info_content_tag;
    @BindView(R2.id.activity_book_info_content_xiangqing)
    public RelativeLayout activity_book_info_content_xiangqing;
    @BindView(R2.id.activity_book_info_content_mulu)
    public RelativeLayout activity_book_info_content_mulu;
    @BindView(R2.id.activity_book_info_content_xiangqing_view)
    public View activity_book_info_content_xiangqing_view;
    @BindView(R2.id.activity_book_info_content_mulu_view)
    public View activity_book_info_content_mulu_view;
    @BindView(R2.id.channel_bar_indicator)
    public UnderlinePageIndicatorHalf channel_bar_indicator;
    @BindView(R2.id.activity_book_info_content_cover_bg)
    public ImageView activity_book_info_content_cover_bg;
    @BindView(R2.id.activity_comic_info_AppBarLayout)
    public AppBarLayout activity_comic_info_AppBarLayout;
    @BindView(R2.id.activity_comic_info_CollapsingToolbarLayout)
    public CollapsingToolbarLayout activity_comic_info_CollapsingToolbarLayout;
    @BindView(R2.id.fragment_comicinfo_mulu_dangqian)
    public LinearLayout fragment_comicinfo_mulu_dangqian;
    @BindView(R2.id.fragment_comicinfo_mulu_zhiding)
    public LinearLayout fragment_comicinfo_mulu_zhiding;
    @BindView(R2.id.fragment_comicinfo_mulu_zhiding_img)
    public ImageView fragment_comicinfo_mulu_zhiding_img;
    @BindView(R2.id.fragment_comicinfo_mulu_zhiding_text)
    public TextView fragment_comicinfo_mulu_zhiding_text;
    @BindView(R2.id.fragment_comicinfo_mulu_dangqian_layout)
    public RelativeLayout fragment_comicinfo_mulu_dangqian_layout;
    @BindView(R2.id.activity_comic_info_comment_layout)
    public LinearLayout activity_comic_info_comment_layout;
    @BindView(R2.id.activity_comic_info_topbar)
    public RelativeLayout activity_comic_info_topbar;
    Resources resources;
    FragmentActivity activity;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    BaseComic baseComic, baseComicLocal;
    String comic_id;
    public boolean chooseWho;
    Gson gs = new Gson();
    StroreComicLable stroreComicLable;
    List<BookInfoComment> bookInfoComment;
    StroreComicLable.Comic comic;
    BaseAd baseAd;
    List<ComicChapter> comicChapter;
    ComicinfoCommentFragment comicFragment;
    ComicinfoMuluFragment muluFragment;
    Drawable activity_comic_info_topbarD;
    boolean refreshComment;
    MuluLorded muluLorded = new MuluLorded() {
        @Override
        public void getReadChapterItem(List<ComicChapter> comicChapter1) {
            if (comicChapter1 != null && !comicChapter1.isEmpty()) {
                comicChapter = comicChapter1;
                baseComic.setTotal_chapters(comicChapter1.size());
                if (baseComic.getCurrent_chapter_name() == null) {
                    fragment_comicinfo_current_chaptername.setText(comicChapter.get(0).chapter_title);
                }
            }
        }
    };

    public interface MuluLorded {
        void getReadChapterItem(List<ComicChapter> comicChapter);
    }

    @OnClick(value = {R.id.fragment_comicinfo_current_goonread, R.id.titlebar_back,
            R.id.activity_comic_info_topbar_sharelayout, R.id.activity_comic_info_topbar_downlayout,
            R.id.activity_book_info_content_xiangqing, R.id.activity_book_info_content_mulu,
            R.id.fragment_comicinfo_mulu_dangqian, R.id.fragment_comicinfo_mulu_zhiding
            , R.id.activity_book_info_content_shoucang})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_comicinfo_current_goonread:
                if (baseComic != null && comicChapter != null) {
                    baseComic.saveIsexist(false);
                    MyToash.Log("fragment_comicinfo_current_goonread", comic_id);
                    Intent intent = new Intent(activity, ComicLookActivity.class);
                    intent.putExtra("baseComic", baseComic);
                    intent.putExtra("FORM_INFO", true);
                    //intent.putExtra("comicChapter", (Serializable) comicChapter);
                    startActivity(intent);
                } else {
                    MyToash.ToashError(activity, "漫画正在更新中~");
                }
                break;
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.activity_comic_info_topbar_sharelayout:
                String url = ReaderConfig.getBaseUrl() + "/site/share?uid=" + Utils.getUID(activity) + "&comic_id=" + comic_id + "&osType=2&product=1";
                UMWeb web = new UMWeb(url);
                web.setTitle(baseComic.getName());//标题
                web.setThumb(new UMImage(activity, baseComic.getVertical_cover()));  //缩略图
                web.setDescription(baseComic.getDescription());//描述
                MyShare.Share(activity, "", web);
                break;
            case R.id.activity_book_info_content_xiangqing:
                MyToash.Log("activity_book_info_content_xiangqing", "" + chooseWho);
                if (chooseWho) {
                    fragment_comicinfo_viewpage.setCurrentItem(0);
                    chooseWho = false;
                }
                break;
            case R.id.activity_book_info_content_mulu:
                MyToash.Log("activity_book_info_content_mulu", "" + chooseWho);
                if (!chooseWho) {
                    fragment_comicinfo_viewpage.setCurrentItem(1);
                    chooseWho = true;
                }
                break;
            case R.id.activity_book_info_content_shoucang:
                if (!baseComic.isAddBookSelf()) {
                    baseComic.saveIsexist(true);
                    activity_book_info_content_shoucang.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
                    MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
                    addSelfCollect();
                    EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                } else {
                    MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.fragment_comic_info_delshoucang));
                    activity_book_info_content_shoucang.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_shoucang));
                    LitePal.delete(BaseComic.class, baseComic.getId());
                    baseComic.setAddBookSelf(false);
                    EventBus.getDefault().post(new RefreshComic(baseComic, 0));
                }
                break;
            case R.id.activity_comic_info_topbar_downlayout:
                if (baseComic != null && comicChapter != null) {
                    baseComic.saveIsexist(false);
                    Intent intent = new Intent(activity, ComicDownActivity.class);
                    intent.putExtra("baseComic", baseComic);
                    startActivity(intent);
                } else {
                    MyToash.ToashError(activity, "漫画正在更新中~");
                }
                break;
            case R.id.fragment_comicinfo_mulu_dangqian://baseComic.getCurrent_chapter_displayOrder()
                muluFragment.OnclickDangqian(true);
                break;
            case R.id.fragment_comicinfo_mulu_zhiding:
                muluFragment.OnclickDangqian(false);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
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
        resources = getResources();
        init();
    }

    public void init() {
        if (!ReaderConfig.USE_SHARE) {
            activity_comic_info_topbar_sharelayout.setVisibility(View.GONE);
        }
        muluFragment = new <Fragment>ComicinfoMuluFragment(muluLorded, fragment_comicinfo_mulu_zhiding_img, fragment_comicinfo_mulu_zhiding_text);
        comicFragment = new <Fragment>ComicinfoCommentFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(comicFragment);
        fragmentList.add(muluFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        fragment_comicinfo_viewpage.setAdapter(myFragmentPagerAdapter);
        channel_bar_indicator.setViewPager(fragment_comicinfo_viewpage);
        channel_bar_indicator.setFades(false);
        bookInfoComment = new ArrayList<>();
        activity_comic_info_AppBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
            @Override
            public void onStateChanged(AppBarLayout appBarLayout, State state) {
                MyToash.Log("appBarLayout", state + "");
                if (state == State.EXPANDED) {
                    activity_comic_info_top_bookname.setVisibility(View.GONE);
                    activity_comic_info_topbar.setBackground(null);
                } else if (state == State.COLLAPSED) {
                    activity_comic_info_top_bookname.setVisibility(View.VISIBLE);
                    activity_comic_info_topbar.setVisibility(View.VISIBLE);
                    if (activity_comic_info_topbar != null) {
                        activity_comic_info_topbar.setBackground(activity_comic_info_topbarD);
                        Drawable drawable = new Drawable() {
                            @Override
                            public void draw(Canvas canvas) {
                            }

                            @Override
                            public void setAlpha(int i) {
                            }

                            @Override
                            public void setColorFilter(ColorFilter colorFilter) {
                            }

                            @SuppressLint("WrongConstant")
                            @Override
                            public int getOpacity() {
                                return 0;
                            }
                        };
                        drawable.setAlpha(0);
                        activity_comic_info_CollapsingToolbarLayout.setContentScrim(drawable);
                    }
                } else {
                    activity_comic_info_top_bookname.setVisibility(View.GONE);
                    activity_comic_info_topbar.setBackground(null);
                }
            }
        });
        fragment_comicinfo_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                chooseWho = position == 1;
                if (!chooseWho) {
                    activity_book_info_content_mulu_text.setTextColor(Color.BLACK);
                    activity_book_info_content_xiangqing_text.setTextColor(ContextCompat.getColor(activity, R.color.mainColor));
                    fragment_comicinfo_mulu_dangqian_layout.setVisibility(View.GONE);
                } else {
                    activity_book_info_content_xiangqing_text.setTextColor(Color.BLACK);
                    activity_book_info_content_mulu_text.setTextColor(ContextCompat.getColor(activity, R.color.mainColor));
                    fragment_comicinfo_mulu_dangqian_layout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        httpData();
    }

    public void handdata() {
        if (!refreshComment) {
            MyPicasso.GlideImageRoundedCorners(12, activity, comic.vertical_cover, activity_book_info_content_cover, ImageUtil.dp2px(activity, 135), ImageUtil.dp2px(activity, 180), R.mipmap.comic_def_v);
            if (comic.horizontal_cover.length() > 0) {
                MyPicasso.GlideImage(activity, comic.horizontal_cover, activity_book_info_content_cover_bg, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ImageUtil.dp2px(activity, 205), R.mipmap.comic_def_cross);
            } else {
                MyPicasso.GlideImageRoundedGasoMohu(activity, comic.vertical_cover, activity_book_info_content_cover_bg, ScreenSizeUtils.getInstance(activity).getScreenWidth(), ImageUtil.dp2px(activity, 205), R.mipmap.comic_def_cross);
            }
            try {
                Glide.with(this).asFile().load(comic.horizontal_cover)
                        .into(new SimpleTarget<File>() {
                            @Override
                            public void onResourceReady(@NonNull File resource, @Nullable Transition<? super File> transition) {
                                AESUtil.getDecideFile(resource, comic.horizontal_cover, new AESUtil.OnFileResourceListener() {
                                    @Override
                                    public void onFileResource(File f) {
                                        Glide.with(ComicInfoActivity.this).asBitmap().load(f).into(new SimpleTarget<Bitmap>() {
                                            @Override
                                            public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                                                try {
                                                    activity_comic_info_topbarD = BlurImageview.reloadCoverBg(activity, resource);
                                                } catch (Exception e) {
                                                } catch (Error r) {
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        });
            } catch (Exception e) {
            }
            activity_book_info_content_name.setText(comic.name);
            fragment_comicinfo_current_chaptername.setText(baseComic.getCurrent_chapter_name() == null ? "" : baseComic.getCurrent_chapter_name());
            activity_book_info_content_author.setText(comic.author);
            activity_book_info_content_total_hot.setText(comic.hot_num);
            activity_book_info_content_shoucannum.setText(comic.total_favors);
            activity_comic_info_top_bookname.setText(comic.name);
            int dp6 = ImageUtil.dp2px(activity, 6);
            int dp3 = ImageUtil.dp2px(activity, 3);
            for (BaseTag tag : comic.tag) {
                TextView textView = new TextView(activity);
                textView.setText(tag.getTab());
                textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
                textView.setLines(1);
                textView.setGravity(Gravity.CENTER);
                textView.setPadding(dp6, dp3, dp6, dp3);
                textView.setTextColor(Color.parseColor(tag.getColor()));//resources.getColor(R.color.comic_info_tag_text)
                GradientDrawable drawable = new GradientDrawable();
                drawable.setCornerRadius(10);
                drawable.setColor(Color.parseColor("#1A" + tag.getColor().substring(1)));
                textView.setBackground(drawable);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.rightMargin = ImageUtil.dp2px(activity, 10);
                layoutParams.gravity = Gravity.CENTER_VERTICAL;
                activity_book_info_content_tag.addView(textView, layoutParams);
            }
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
            muluFragment.senddata(baseComic, comic);
        }
        comicFragment.senddata(comic, bookInfoComment, stroreComicLable, baseAd);
    }

    public void httpData() {
        Intent intent = getIntent();
        comic_id = intent.getStringExtra("comic_id");
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
        if (baseComic.isAddBookSelf()) {
            activity_book_info_content_shoucang.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
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
                            ComicInfo comicInfo = gs.fromJson(result, ComicInfo.class);
                            if (comicInfo != null) {
                                comic = comicInfo.comic;
                                stroreComicLable = comicInfo.label.get(0);
                                bookInfoComment = comicInfo.comment;
                                baseAd = comicInfo.advert;
                            }
                            handdata();
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        if (ex != null && ex.equals("nonet")) {
                            muluFragment.senddata(baseComic, comic);
                        }
                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreashComicInfoActivity refreshBookInfo) {
        if (refreshBookInfo.isSave) {
            baseComic.setAddBookSelf(true);
            activity_book_info_content_shoucang.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
            addSelfCollect();
        } else {
            httpData2(true);
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
        fragment_comicinfo_current_chaptername.setText(baseComic1.getCurrent_chapter_name());
        if (muluFragment.comicChapterCatalogAdapter != null) {
            muluFragment.comicChapterCatalogAdapter.setCurrentChapterId(baseComic1.getCurrent_chapter_id());
            if (baseComic1.getCurrent_display_order() < muluFragment.comicChapterCatalogAdapter.comicChapterCatalogList.size()) {
                muluFragment.comicChapterCatalogAdapter.comicChapterCatalogList.get(baseComic1.getCurrent_display_order()).IsRead = true;
            }
            muluFragment.comicChapterCatalogAdapter.notifyDataSetChanged();
        }
        if (!baseComic.isAddBookSelf() && baseComic1.isAddBookSelf()) {
            baseComic.setAddBookSelf(true);
            activity_book_info_content_shoucang.setText(LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
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
}
