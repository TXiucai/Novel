package com.heiheilianzai.app.ui.activity;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MyFragmentPagerAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.InfoBook;
import com.heiheilianzai.app.model.InfoBookItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.ui.dialog.DownDialog;
import com.heiheilianzai.app.ui.fragment.book.NovelInfoCommentFragment;
import com.heiheilianzai.app.ui.fragment.book.NovelMuluFragment;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.BlurImageview;
import com.umeng.socialize.UMShareAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * ???????????????
 */
public class BookInfoActivity extends BaseButterKnifeTransparentActivity {
    public final String TAG = BookInfoActivity.class.getSimpleName();
    public static final String BOOK_ID_EXT_KAY = "book_id";//??????????????? ????????????id
    @BindView(R.id.activity_book_info_add_shelf)
    public TextView activity_book_info_add_shelf;
    @BindView(R.id.activity_book_info_start_read)
    public TextView activity_book_info_start_read;
    @BindView(R.id.fragment_comicinfo_viewpage)
    public ViewPager fragment_comicinfo_viewpage;
    @BindView(R.id.activity_book_info_content_xiangqing_text)
    public TextView activity_book_info_content_xiangqing_text;
    @BindView(R.id.activity_book_info_content_mulu_text)
    public TextView activity_book_info_content_mulu_text;
    @BindView(R.id.activity_book_info_content_xiangqing_view)
    public View activity_book_info_content_xiangqing_view;
    @BindView(R.id.activity_book_info_content_mulu_view)
    public View activity_book_info_content_mulu_view;
    @BindView(R.id.activity_book_info_content_mulu_flag)
    public TextView activity_book_info_content_mulu_flag;
    @BindView(R.id.activity_comic_info_comment_layout)
    public RelativeLayout activity_comic_info_comment_layout;
    @BindView(R.id.activity_book_info_content_cover)
    public ImageView activity_book_info_content_cover;
    @BindView(R.id.activity_book_info_content_cover_bg)
    public ImageView activity_book_info_content_cover_bg;
    @BindView(R.id.activity_comic_info_AppBarLayout)
    public AppBarLayout activity_comic_info_AppBarLayout;
    @BindView(R.id.activity_comic_info_CollapsingToolbarLayout)
    public CollapsingToolbarLayout activity_comic_info_CollapsingToolbarLayout;
    @BindView(R.id.activity_book_info_content_display_label)
    public TextView activity_book_info_content_display_label;
    @BindView(R.id.activity_book_info_content_views)
    public TextView activity_book_info_content_views;
    @BindView(R.id.activity_comic_info_top_bookname)
    public TextView activity_comic_info_top_bookname;
    @BindView(R.id.ll_down)
    public LinearLayout mLldown;

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
    public boolean chooseWho;
    List<Fragment> fragmentList;
    MyFragmentPagerAdapter myFragmentPagerAdapter;
    private NovelMuluFragment novelMuluFragment;
    private NovelInfoCommentFragment novelInfoCommentFragment;

    @Override
    public int initContentView() {
        return R.layout.activity_book_info;
    }

    @OnClick(value = {R.id.titlebar_back, R.id.ll_add, R.id.activity_book_info_start_read,
            R.id.ll_down, R.id.activity_book_info_content_xiangqing, R.id.activity_book_info_content_mulu})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                if (MainActivity.activity == null) {
                    startActivity(new Intent(BookInfoActivity.this, MainActivity.class));
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
            case R.id.ll_add:
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
            case R.id.ll_down:
                if (App.isVip(activity)) {
                    DownDialog downDialog = new DownDialog();
                    downDialog.getDownoption(BookInfoActivity.this, mBaseBook, null);
                    DownDialog.showOpen = true;
                } else {
                    MyToash.Toash(activity, getString(R.string.down_toast_msg));
                }
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
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//?????????????????????????????????tab?????????
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//?????????setContentView()??????????????????
        }
        EventBus.getDefault().register(this);
        //mLldown.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
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
        novelMuluFragment = new <Fragment>NovelMuluFragment();
        novelInfoCommentFragment = new <Fragment>NovelInfoCommentFragment();
        fragmentList = new ArrayList<>();
        fragmentList.add(novelInfoCommentFragment);
        fragmentList.add(novelMuluFragment);
        myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), fragmentList);
        fragment_comicinfo_viewpage.setAdapter(myFragmentPagerAdapter);
        fragment_comicinfo_viewpage.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                chooseWho = position == 1;
                if (!chooseWho) {
                    activity_book_info_content_mulu_text.setTextColor(Color.BLACK);
                    activity_book_info_content_mulu_flag.setTextColor(Color.BLACK);
                    activity_book_info_content_xiangqing_text.setTextColor(ContextCompat.getColor(activity, R.color.color_ff8350));
                } else {
                    activity_book_info_content_xiangqing_text.setTextColor(Color.BLACK);
                    activity_book_info_content_mulu_text.setTextColor(ContextCompat.getColor(activity, R.color.color_ff8350));
                    activity_book_info_content_mulu_flag.setTextColor(ContextCompat.getColor(activity, R.color.color_ff8350));
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
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
            activity_comic_info_top_bookname.setText(infoBook.name);
            activity_book_info_content_display_label.setText(infoBook.display_label);
            activity_book_info_content_views.setText(infoBook.total_favors);
            activity_book_info_content_mulu_flag.setText("(" + infoBook.getTag().get(0).getTab() + ")");
            MyPicasso.GlideImageNoSize(activity, infoBook.cover, activity_book_info_content_cover, R.mipmap.book_def_v);
            novelMuluFragment.sendData(mBaseBook);
            novelInfoCommentFragment.senddata(mBaseBook, infoBookItem.comment, infoBookItem.label.get(0), infoBookItem.advert);
            try {
                if (activity != null && !activity.isFinishing()) {
                    Glide.with(this).asBitmap().load(infoBook.cover).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            try {
                                activity_book_info_content_cover_bg.setBackground(BlurImageview.BlurImages2(resource, BookInfoActivity.this));
                            } catch (Exception e) {
                            } catch (Error r) {
                            }
                        }

                        @Override
                        public void onLoadFailed(@Nullable Drawable errorDrawable) {
                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.comic_def_cross, null);
                            activity_book_info_content_cover_bg.setBackground(BlurImageview.BlurImages2(bitmap, BookInfoActivity.this));
                        }
                    });
                }

            } catch (Exception e) {
                MyToash.Log("", e.getMessage());
            } catch (Error r) {
                MyToash.Log("", r.getMessage());
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
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.color_666666));
            activity_book_info_start_read.setText(getString(R.string.noverfragment_goonread));
            activity_book_info_add_shelf.setEnabled(false);
        } else {
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujia));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.color_1a1a1a));
            activity_book_info_add_shelf.setEnabled(true);
        }
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(BookInfoActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookInfoUrl, json, falseDialg, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            initInfo(result);
                            updateRecord();
                        } catch (Exception e) {
                            MyToash.Log("", e.getMessage());
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void updateRecord() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(BookInfoActivity.this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookInfoRecordUrl, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void addBookToLocalShelf() {
        if (mBaseBook.isAddBookSelf() == 0) {
            MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            mBaseBook.saveIsexist(1);
            activity_book_info_add_shelf.setText(LanguageUtil.getString(this, R.string.BookInfoActivity_jiarushujias));
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.color_666666));
            activity_book_info_add_shelf.setEnabled(false);
            activity_book_info_start_read.setText(getString(R.string.noverfragment_goonread));
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
            activity_book_info_add_shelf.setTextColor(ContextCompat.getColor(activity, R.color.color_666666));
            activity_book_info_add_shelf.setEnabled(false);
            activity_book_info_start_read.setText(getString(R.string.noverfragment_goonread));
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
                startActivity(new Intent(BookInfoActivity.this, MainActivity.class));
            }
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
    }

    /**
     * ?????????????????????????????????
     *
     * @param context
     * @param referPage ???????????????????????????????????????
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String bookId) {
        Intent intent = new Intent(context, BookInfoActivity.class);
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