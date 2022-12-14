package com.heiheilianzai.app.ui.activity;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.BookInfoComment;
import com.heiheilianzai.app.model.InfoBook;
import com.heiheilianzai.app.model.InfoBookItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.ui.dialog.DownDialog;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.BlurImageview;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ???????????????
 */
public class NovelActivity extends BaseButterKnifeTransparentActivity {
    public final String TAG = NovelActivity.class.getSimpleName();
    public static final String BOOK_ID_EXT_KAY = "book_id";//??????????????? ????????????id
    @BindView(R.id.book_info_titlebar_container)
    public RelativeLayout book_info_titlebar_container;
    @BindView(R.id.book_info_titlebar_container_shadow)
    public View book_info_titlebar_container_shadow;
    @BindView(R.id.activity_book_info_scrollview)
    public ObservableScrollView activity_book_info_scrollview;
    @BindView(R.id.activity_book_info_content_cover_bg)
    public RelativeLayout activity_book_info_content_cover_bg;
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.back)
    public ImageView back;
    @BindView(R.id.titlebar_share)
    RelativeLayout titlebar_share;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.activity_book_info_content_name)
    public TextView activity_book_info_content_name;
    @BindView(R.id.activity_book_info_content_author)
    public TextView activity_book_info_content_author;
    @BindView(R.id.activity_book_info_content_display_label)
    public TextView activity_book_info_content_display_label;
    @BindView(R.id.activity_book_info_content_total_comment)
    public TextView activity_book_info_content_total_comment;
    @BindView(R.id.activity_book_info_content_total_shoucanshu)
    public TextView activity_book_info_content_total_shoucanshu;
    @BindView(R.id.activity_book_info_content_cover)
    public ImageView activity_book_info_content_cover;
    @BindView(R.id.activity_book_info_content_description)
    public TextView activity_book_info_content_description;
    @BindView(R.id.activity_book_info_content_last_chapter_time)
    public TextView activity_book_info_content_last_chapter_time;
    @BindView(R.id.activity_book_info_content_last_chapter)
    public TextView activity_book_info_content_last_chapter;
    @BindView(R.id.activity_book_info_content_comment_container)
    public LinearLayout activity_book_info_content_comment_container;
    @BindView(R.id.activity_book_info_content_label_container)
    public LinearLayout activity_book_info_content_label_container;
    @BindView(R.id.activity_book_info_content_category_layout)
    public RelativeLayout activity_book_info_content_category_layout;
    @BindView(R.id.activity_book_info_add_shelf)
    public TextView activity_book_info_add_shelf;
    @BindView(R.id.activity_book_info_start_read)
    public TextView activity_book_info_start_read;
    @BindView(R.id.activity_book_info_tag)
    LinearLayout activity_book_info_tag;
    @BindView(R.id.list_ad_view_layout)
    FrameLayout activity_book_info_ad;
    @BindView(R.id.list_ad_view_img)
    ImageView list_ad_view_img;

    public String mBookId;
    public BaseBook mBaseBook;
    InfoBookItem mInfoBookItem;
    public int WIDTH, WIDTHV, HEIGHT, HEIGHTV, HorizontalSpacing, H100, H50, H20;
    LayoutInflater layoutInflater;
    Activity activity;
    Gson gson = new Gson();
    boolean falseDialg;
    BaseBook basebooks;
    boolean onclickTwo = false;
    int mTotalchapter = -1; //??????????????????
    boolean mXSIntroPageEvent;//??????????????????????????????

    @Override
    public int initContentView() {
        return R.layout.activity_novel_info;
    }

    @OnClick(value = {R.id.titlebar_back, R.id.activity_book_info_content_category_layout,
            R.id.activity_book_info_add_shelf, R.id.activity_book_info_start_read,
            R.id.activity_book_info_down, R.id.titlebar_share
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                if (MainActivity.activity == null) {
                    startActivity(new Intent(NovelActivity.this, MainActivity.class));
                }
                finish();
                break;
            case R.id.activity_book_info_content_category_layout:
                if (!onclickTwo) {
                    onclickTwo = true;
                    Intent intent = new Intent(this, CatalogActivity.class);
                    intent.putExtra("book_id", mBookId);
                    intent.putExtra("book", mBaseBook);
                    startActivity(intent);
                    onclickTwo = false;
                }
                break;
            case R.id.activity_book_info_add_shelf:
                addBookToLocalShelf();
                break;
            case R.id.activity_book_info_start_read:
                if (!onclickTwo) {
                    onclickTwo = true;
                    mBaseBook.saveIsexist(0);
                    if (activity != null) {
                        activity.setTitle(LanguageUtil.getString(activity, R.string.refer_page_info));
                        ChapterManager.getInstance(activity).openBook(mBaseBook, mBookId);
                    }
                    onclickTwo = false;
                    ReaderConfig.integerList.add(1);
                }
                break;
            case R.id.activity_book_info_down:
                if (App.isVip(activity)) {
                    DownDialog downDialog = new DownDialog();
                    downDialog.getDownoption(NovelActivity.this, mBaseBook, null);
                    DownDialog.showOpen = true;
                } else {
                    MyToash.Toash(activity, getString(R.string.down_toast_msg));
                }
                break;
            case R.id.titlebar_share:
                String url = ReaderConfig.getBaseUrl() + "/site/share?uid=" + Utils.getUID(NovelActivity.this) + "&book_id=" + mBookId + "&osType=2&product=1";
                UMWeb web = new UMWeb(url);
                web.setTitle(mBaseBook.getName());//??????
                web.setThumb(new UMImage(NovelActivity.this, mBaseBook.getCover()));  //?????????
                web.setDescription(mBaseBook.getDescription());//??????
                MyShare.Share(NovelActivity.this, "", web);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        setStatusTextColor(false, activity);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//?????????????????????????????????tab?????????
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//?????????setContentView()??????????????????
        }
        EventBus.getDefault().register(this);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        layoutInflater = LayoutInflater.from(activity);
        WIDTH = (WIDTH - ImageUtil.dp2px(activity, 40)) / 3;//???????????? ????????????
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HorizontalSpacing = ImageUtil.dp2px(activity, 5);//?????????
        WIDTHV = WIDTH - HorizontalSpacing;//?????? ????????????
        HEIGHTV = (int) (((float) WIDTHV * 4f / 3f));//
        H50 = ImageUtil.dp2px(activity, 50);
        H100 = H50; //  ???????????? ??????????????? ????????????
        H20 = ImageUtil.dp2px(activity, 12);
        initView();
    }

    public void initView() {
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
        initData();
    }

    public void initInfo(String json) {
        try {
            InfoBookItem infoBookItem = gson.fromJson(json, InfoBookItem.class);
            mInfoBookItem = infoBookItem;
            InfoBook infoBook = infoBookItem.book;
            mBaseBook.setName(infoBook.name);
            mBaseBook.setCover(infoBook.cover);
            mBaseBook.setAuthor(infoBook.author);
            mBaseBook.setDescription(infoBook.description);
            mBaseBook.setTotal_Chapter(infoBook.total_chapter);
            mBaseBook.setRecentChapter(infoBook.total_chapter);
            mBaseBook.setName(infoBook.name);
            mBaseBook.setUid(Utils.getUID(activity));
            activity_book_info_content_name.setText(infoBook.name);
            activity_book_info_content_author.setText(infoBook.author);
            activity_book_info_content_display_label.setText(infoBook.display_label);
            activity_book_info_content_total_comment.setText(infoBook.hot_num);
            MyPicasso.GlideImageNoSize(activity, infoBook.cover, activity_book_info_content_cover, R.mipmap.book_def_v);
            activity_book_info_content_description.setText(infoBook.description);
            activity_book_info_content_last_chapter_time.setText(infoBook.last_chapter_time);
            activity_book_info_content_last_chapter.setText(infoBook.last_chapter);
            titlebar_text.setText(infoBook.name);
            titlebar_text.setAlpha(0);
            book_info_titlebar_container_shadow.setAlpha(0);
            try {
                if (activity != null && !activity.isFinishing()) {

                    Glide.with(this).asBitmap().load(infoBook.cover).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            try {
                                activity_book_info_content_cover_bg.setBackground(BlurImageview.BlurImages2(resource, NovelActivity.this));
                            } catch (Exception e) {
                            } catch (Error r) {
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.comic_def_cross, null);
                            activity_book_info_content_cover_bg.setBackground(BlurImageview.BlurImages2(bitmap, NovelActivity.this));
                        }
                    });

                }
            } catch (Exception e) {
                MyToash.Log("", e.getMessage());
            } catch (Error r) {
                MyToash.Log("", r.getMessage());
            }
            int dp6 = ImageUtil.dp2px(activity, 6);
            int dp3 = ImageUtil.dp2px(activity, 3);
            for (BaseTag tag : infoBook.tag) {
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
                activity_book_info_tag.addView(textView, layoutParams);
            }
            if (!infoBookItem.comment.isEmpty()) {
                for (BookInfoComment bookInfoComment : infoBookItem.comment) {
                    LinearLayout commentView = (LinearLayout) layoutInflater.inflate(R.layout.activity_book_info_content_comment_item, null, false);
                    CircleImageView activity_book_info_content_comment_item_avatar = commentView.findViewById(R.id.activity_book_info_content_comment_item_avatar);
                    TextView activity_book_info_content_comment_item_nickname = commentView.findViewById(R.id.activity_book_info_content_comment_item_nickname);
                    TextView activity_book_info_content_comment_item_content = commentView.findViewById(R.id.activity_book_info_content_comment_item_content);
                    TextView activity_book_info_content_comment_item_reply = commentView.findViewById(R.id.activity_book_info_content_comment_item_reply_info);
                    TextView activity_book_info_content_comment_item_time = commentView.findViewById(R.id.activity_book_info_content_comment_item_time);
                    View comment_item_isvip = commentView.findViewById(R.id.comment_item_isvip);
                    MyPicasso.IoadImage(this, bookInfoComment.getAvatar(), R.mipmap.icon_def_head, activity_book_info_content_comment_item_avatar);
                    activity_book_info_content_comment_item_nickname.setText(bookInfoComment.getNickname());
                    activity_book_info_content_comment_item_content.setText(bookInfoComment.getContent());
                    activity_book_info_content_comment_item_reply.setText(bookInfoComment.getReply_info());
                    activity_book_info_content_comment_item_reply.setVisibility(TextUtils.isEmpty(bookInfoComment.getReply_info()) ? View.GONE : View.VISIBLE);
                    activity_book_info_content_comment_item_time.setText(bookInfoComment.getTime());
                    comment_item_isvip.setVisibility(bookInfoComment.getIs_vip() == 1 ? View.VISIBLE : View.GONE);
                    //?????????????????????
                    commentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(NovelActivity.this, ReplyCommentActivity.class);
                            intent.putExtra("book_id", mBookId);
                            intent.putExtra("comment_id", bookInfoComment.getComment_id());
                            intent.putExtra("avatar", bookInfoComment.getAvatar());
                            intent.putExtra("nickname", bookInfoComment.getNickname());
                            intent.putExtra("origin_content", bookInfoComment.getContent());
                            startActivity(intent);
                        }
                    });
                    activity_book_info_content_comment_container.addView(commentView);
                }
            }
            String moreText;
            if (infoBook.total_comment > 0) {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_lookpinglun);
            } else {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_nopinglun);
            }
            LinearLayout commentMoreView = (LinearLayout) layoutInflater.inflate(R.layout.activity_book_info_content_comment_more, null, false);
            TextView activity_book_info_content_comment_more_text = commentMoreView.findViewById(R.id.activity_book_info_content_comment_more_text);
            activity_book_info_content_comment_more_text.setText(String.format(moreText, infoBook.total_comment));
            TextView activity_book_info_content_add_comment = findViewById(R.id.activity_book_info_content_add_comment);
            activity_book_info_content_add_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //?????????
                    Intent intent = new Intent(NovelActivity.this, AddCommentActivity.class);
                    intent.putExtra("book_id", mBookId);
                    startActivity(intent);
                }
            });
            commentMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(NovelActivity.this, CommentListActivity.class);
                    intent.putExtra("book_id", mBookId);
                    startActivity(intent);
                }
            });
            activity_book_info_content_comment_container.addView(commentMoreView);
            initWaterfall(infoBookItem.label);
            if (basebooks != null) {
                ContentValues values = new ContentValues();
                values.put("total_chapter", infoBook.total_chapter);
                values.put("name", infoBook.name);
                values.put("cover", infoBook.cover);
                values.put("author", infoBook.author);
                values.put("description", infoBook.description);
                LitePal.updateAsync(BaseBook.class, values, basebooks.getId());
            }
            if (infoBookItem.advert != null) {
                BaseAd baseAd = infoBookItem.advert;
                activity_book_info_ad.setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
                layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
                layoutParams.height = layoutParams.width / 3;
                list_ad_view_img.setLayoutParams(layoutParams);
                MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
                activity_book_info_ad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.setClass(activity, WebViewActivity.class);
                        intent.putExtra("url", baseAd.ad_skip_url);
                        intent.putExtra("title", baseAd.ad_title);
                        intent.putExtra("advert_id", baseAd.advert_id);
                        intent.putExtra("ad_url_type", baseAd.ad_url_type);
                        activity.startActivity(intent);
                    }
                });
            } else {
                activity_book_info_ad.setVisibility(View.GONE);
            }
            if (mTotalchapter != -1 && !mXSIntroPageEvent) {
                setXSIntroPageEvent();
            }
        } catch (Exception e) {
            MyToash.Log("", e.getMessage());
        }
    }

    public void initData() {
        if (mBookId == null) {
            falseDialg = true;
            try {
                mBookId = getIntent().getStringExtra(BOOK_ID_EXT_KAY);
            } catch (Exception e) {
            }
        } else {
            falseDialg = false;
        }
        if (mBookId == null) {
            return;
        }
        mBaseBook = new BaseBook();
        mBaseBook.setBook_id(mBookId);
        basebooks = LitePal.where("book_id = ?", mBookId).findFirst(BaseBook.class);
        if (basebooks != null) {
            mBaseBook.setAddBookSelf(basebooks.isAddBookSelf());
            mBaseBook.setCurrent_chapter_id(basebooks.getCurrent_chapter_id());
            mBaseBook.setChapter_text(basebooks.getChapter_text());
            mBaseBook.setId(basebooks.getId());
        } else {
            mBaseBook.setAddBookSelf(0);
        }
        if (mBaseBook.isAddBookSelf() == 1) {
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.yijianrushujia));
            activity_book_info_add_shelf.setEnabled(false);
        } else {
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujia));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.mainColor));
            activity_book_info_add_shelf.setEnabled(true);
        }
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(NovelActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookInfoUrl, json, falseDialg, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            initInfo(result);
                        } catch (Exception e) {
                            MyToash.Log("", e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
        getDataCatalogInfo();//??????????????????
    }

    public void addBookToLocalShelf() {
        if (mBaseBook.isAddBookSelf() == 0) {
            MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            mBaseBook.saveIsexist(1);
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.yijianrushujia));
            activity_book_info_add_shelf.setEnabled(false);
            List<BaseBook> list = new ArrayList<>();
            list.add(mBaseBook);
            EventBus.getDefault().post(new RefreshBookSelf(list));
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        BaseBook basebooks = LitePal.where("book_id = ?", mBookId).findFirst(BaseBook.class);
        if (basebooks != null) {
            mBaseBook.setCurrent_chapter_id(basebooks.getCurrent_chapter_id());
            mBaseBook.setChapter_text(basebooks.getChapter_text());
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshBookInfoEvent refreshBookInfoEvent) {
        if (refreshBookInfoEvent.isSave && mBaseBook.getCover() != null) {
            mBaseBook.setAddBookSelf(1);
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.yijianrushujia));
            activity_book_info_add_shelf.setEnabled(false);
        } else {
            activity_book_info_content_label_container.removeAllViews();
            activity_book_info_content_comment_container.removeAllViews();
            //??????
            initData();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onStart() {
        super.onStart();
        onclickTwo = false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if (MainActivity.activity == null) {
                startActivity(new Intent(NovelActivity.this, MainActivity.class));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public void initWaterfall(List<StroreBookcLable> stroreBookcLables) {
        activity_book_info_content_label_container.removeAllViews();
        for (StroreBookcLable stroreComicLable : stroreBookcLables) {
            int Size = stroreComicLable.list.size();
            if (Size == 0) {
                continue;
            }
            View type3 = layoutInflater.inflate(R.layout.lable_bookinfo_layout, null, false);
            TextView fragment_store_gridview3_text = type3.findViewById(R.id.fragment_store_gridview3_text);
            fragment_store_gridview3_text.setText(stroreComicLable.label);
            AdaptionGridView fragment_store_gridview3_gridview_first = type3.findViewById(R.id.fragment_store_gridview3_gridview_first);
            fragment_store_gridview3_gridview_first.setHorizontalSpacing(HorizontalSpacing);
            fragment_store_gridview3_gridview_first.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    if (activity != null) {
                        String bookId = stroreComicLable.list.get(position).getBook_id();
                        activity.startActivity(NovelActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_info) + " " + bookId, bookId));
                    }
                }
            });
            int minSize = 0;
            int ItemHeigth = 0, start = 0;
            if (stroreComicLable.style == 2) {
                minSize = Math.min(Size, 6);
                if (minSize > 3) {
                    ItemHeigth = H100 + (HEIGHT + H50) * 2;
                } else {
                    ItemHeigth = H100 + HEIGHT + H50;
                }
            } else {
                minSize = Math.min(Size, 3);
                ItemHeigth = H100 + HEIGHT + H50;
            }
            List<StroreBookcLable.Book> firstList = stroreComicLable.list.subList(start, minSize);
            VerticalAdapter verticalAdapter = new VerticalAdapter(activity, firstList, WIDTH, HEIGHT, false);
            fragment_store_gridview3_gridview_first.setAdapter(verticalAdapter);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, H20, 0, 0);
            params.height = ItemHeigth + H20;
            activity_book_info_content_label_container.addView(type3, params);
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    /**
     * ??????????????????  ??????????????????????????????
     * ??????????????????(/book/info)?????????????????????????????????????????????????????????????????????????????????
     */
    void getDataCatalogInfo() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            mTotalchapter = jsonObject.getInt("total_chapter");
                            if (mInfoBookItem != null && !mXSIntroPageEvent) {
                                setXSIntroPageEvent();
                            }
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * ?????????????????????????????????
     *
     * @param context
     * @param referPage ???????????????????????????????????????
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String bookId) {
        Intent intent = new Intent(context, NovelActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(BOOK_ID_EXT_KAY, bookId);
        return intent;
    }

    /**
     * ???????????? ?????????????????????
     */
    private void setXSIntroPageEvent() {
        try {
            mXSIntroPageEvent = true;
            SensorsDataHelper.setXSIntroPageEvent(getPropIdList(),//??????ID
                    getTagIdList(),//??????ID
                    getIntent().getStringExtra(SaVarConfig.REFER_PAGE_VAR),//????????????
                    Integer.valueOf(StringUtils.isEmpty(mBookId) ? "0" : mBookId),//??????ID
                    Integer.valueOf(mTotalchapter),//???????????????
                    0);//??????ID
        } catch (Exception e) {
        }
    }

    /**
     * ???????????? ??????prop_id????????????
     */
    private List<String> getPropIdList() {
        return ReadActivity.getPropIdList(mInfoBookItem);
    }

    /**
     * ???????????? ??????tag_id????????????
     */
    private List<String> getTagIdList() {
        return ReadActivity.getTagIdList(mInfoBookItem);
    }
}