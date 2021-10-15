package com.heiheilianzai.app.ui.activity.read;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.constant.sa.SaEventConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.InfoBookItem;
import com.heiheilianzai.app.model.NovelBoyinModel;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.event.CloseAnimationEvent;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.CatalogInnerActivity;
import com.heiheilianzai.app.ui.activity.CommentListActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.dialog.DownDialog;
import com.heiheilianzai.app.ui.dialog.read.AutoProgressBar;
import com.heiheilianzai.app.ui.dialog.read.AutoSettingDialog;
import com.heiheilianzai.app.ui.dialog.read.BrightnessDialog;
import com.heiheilianzai.app.ui.dialog.read.SettingDialog;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.BookUtil;
import com.heiheilianzai.app.utils.BrightnessUtil;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.PageFactory;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.MScrollView;
import com.heiheilianzai.app.view.ScrollEditText;
import com.heiheilianzai.app.view.read.PageWidget;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.READBUTTOM_HEIGHT;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.ui.fragment.book.NewNovelFragment.BookShelfOpen;

/**
 * 小说阅读 Activity
 * Created by Administrator on 2016/7/15 0015.
 */
public class ReadActivity extends BaseReadActivity {
    private final static String EXTRA_BOOK = "book";
    private final static String EXTRA_CHAPTER = "chapter";
    //神策埋点数据从哪个页面进入
    public static final String REFER_PAGE_EXT_KAY = "referPage";
    @BindView(R.id.bookpage)
    PageWidget bookpage;
    @BindView(R.id.activity_read_top_back_view)
    LinearLayout activity_read_top_back_view;
    @BindView(R.id.activity_read_top_menu)
    View activity_read_top_menu;
    @BindView(R.id.tv_directory)
    TextView tv_directory;
    @BindView(R.id.tv_brightness)
    TextView tv_brightness;
    @BindView(R.id.tv_comment)
    TextView tv_comment;
    @BindView(R.id.tv_setting)
    TextView tv_setting;
    @BindView(R.id.bookpop_bottom)
    RelativeLayout bookpop_bottom;
    @BindView(R.id.activity_read_bottom_view)
    RelativeLayout activity_read_bottom_view;
    @BindView(R.id.activity_read_change_day_night)
    ImageView activity_read_change_day_night;
    @BindView(R.id.titlebar_share)
    RelativeLayout titlebar_share;
    @BindView(R.id.titlebar_down)
    RelativeLayout titlebar_down;
    @BindView(R.id.activity_read_firstread)
    ImageView activity_read_firstread;
    @BindView(R.id.auto_read_progress_bar)
    ProgressBar auto_read_progress_bar;
    @BindView(R.id.bookpage_scroll)
    MScrollView bookpage_scroll;
    @BindView(R.id.bookpage_scroll_text)
    ScrollEditText bookpage_scroll_text;
    @BindView(R.id.list_ad_view_layout)
    public FrameLayout insert_todayone2;
    @BindView(R.id.activity_read_buttom_ad_layout)
    public RelativeLayout activity_read_buttom_ad_layout;
    @BindView(R.id.activity_read_buttom_ad_iv)
    ImageView mIvAd;
    @BindView(R.id.activity_read_buttom_ad_close)
    ImageView mIvClose;
    @BindView(R.id.tv_noad)
    TextView tv_noad;
    @BindView(R.id.activity_read_shangyizhang)
    Button activity_read_shangyizhang;
    @BindView(R.id.activity_read_xiayizhang)
    Button activity_read_xiayizhang;
    @BindView(R.id.activity_read_purchase_layout)
    LinearLayout activity_read_purchase_layout;
    @BindView(R.id.activity_read_purchase_layout2)
    public LinearLayout activity_read_purchase_layout2;
    @BindView(R.id.activity_read_buttom_boyin_item)
    public RelativeLayout activity_read_buttom_boyin_item;
    @BindView(R.id.activity_read_buttom_boyin_img)
    public ImageView activity_read_buttom_boyin_img;
    @BindView(R.id.activity_read_buttom_boyin_tittle)
    public TextView activity_read_buttom_boyin_tittle;
    @BindView(R.id.activity_read_buttom_boyin_go)
    public TextView activity_read_buttom_boyin_go;
    @BindView(R.id.titlebar_boyin)
    public RelativeLayout titlebar_boyin;

    private ReadingConfig config;
    private WindowManager.LayoutParams lp;
    private ChapterItem chapter;
    private String mBookId;
    private PageFactory pageFactory;
    private Boolean isShow = false;
    private BrightnessDialog mBrightDialog;
    private SettingDialog mSettingDialog;
    private AutoSettingDialog mAutoSettingDialog;
    private Boolean mDayOrNight;
    private boolean isSpeaking = false;
    BaseBook baseBook;
    BaseAd baseAd;
    ImageView list_ad_view_img;
    InfoBookItem mInfoBookItem;
    String mReferPage;//从哪个页面打开小说阅读(神策埋点数据)
    long mOpenCurrentTime;//打开小说阅读页的当前时间(每次翻动一个章节，改变一次时间)
    int visible = -1;//每间隔多少也显示底部广告
    private NovelBoyinModel soundBookInfoBean;
    // 接收电池信息更新的广播
    private BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_BATTERY_CHANGED)) {
                int level = intent.getIntExtra("level", 0);
                pageFactory.updateBattery(level);
            } else if (intent.getAction().equals(Intent.ACTION_TIME_TICK)) {
                pageFactory.updateTime();
            }
        }
    };
    private long mReadStarTime;
    private long mReadEndTime;
    private boolean mIsSdkAd = false;
    private boolean mIsActive = true;//是否处于前台


    @Override
    public int initContentView() {
        return R.layout.activity_read;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (requestCode == 301) {
            int is_vip = data.getIntExtra("is_vip", 0);
            if (is_vip == 1) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bookpage.getLayoutParams();
                layoutParams.height = mScreenHeight;
                bookpage.setLayoutParams(layoutParams);
                tv_noad.setVisibility(View.GONE);
                try {
                    pageFactory.openBook(0, pageFactory.chapterItem, null);
                    handler.removeMessages(1);
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mReadStarTime = System.currentTimeMillis();
        //首次阅读 显示引导图
        if (ShareUitls.getString(ReadActivity.this, "FirstRead", "yes").equals("yes")) {
            ShareUitls.putString(ReadActivity.this, "FirstRead", "no");
            activity_read_firstread.setVisibility(View.VISIBLE);
            activity_read_firstread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity_read_firstread.setVisibility(View.GONE);
                }
            });
        }
        SensorsDataAPI.sharedInstance().trackTimerStart(SaEventConfig.XS_CONTENT_PAGE_EVENT);
    }

    @Override
    public void initView() {
        if (!NotchScreen.hasNotchScreen(this)) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) activity_read_top_menu.getLayoutParams();
            layoutParams.height = ImageUtil.dp2px(this, 50);
            activity_read_top_menu.setLayoutParams(layoutParams);
        }
        if (list_ad_view_img == null) {
            list_ad_view_img = new ImageView(activity);
            list_ad_view_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ImageUtil.dp2px(activity, READBUTTOM_HEIGHT));
            insert_todayone2.addView(list_ad_view_img, params);
        }
        setOpenCurrentTime();
        uiFreeCharge();
        setButtonADVisible();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mIsActive) {
            initData();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Utils.isAppOnForeground(activity)) {
            mIsActive = true;
        } else {
            mIsActive = false;
        }
    }

    @Override
    public void initData() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bookpage.getLayoutParams();
        //layoutParams.height = mScreenHeight - ImageUtil.dp2px(activity, 60);
        layoutParams.height = mScreenHeight;
        bookpage.setLayoutParams(layoutParams);
        getWebViewAD(activity);
        bookpage.setADview(insert_todayone2);
        next();
        acceptNovelBoyin(activity, chapter.getBook_name());
        getBookInfo();
    }

    private void next() {
        config = ReadingConfig.getInstance();
        //  阅读管理器
        //获取intent中的携带的信息
        Intent intent = getIntent();
        chapter = (ChapterItem) intent.getSerializableExtra(EXTRA_CHAPTER);
        baseBook = (BaseBook) intent.getSerializableExtra(EXTRA_BOOK);
        mReferPage = intent.getStringExtra(REFER_PAGE_EXT_KAY);
        pageFactory = new PageFactory(baseBook, bookpage_scroll, bookpage_scroll_text, insert_todayone2, this);
        pageFactory.setPurchaseLayout(activity_read_purchase_layout, activity_read_purchase_layout2);

        pageFactory.getWebViewAD(ReadActivity.this);//获取广告

        IntentFilter mfilter = new IntentFilter();
        mfilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        mfilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(myReceiver, mfilter);
        mSettingDialog = new SettingDialog(this);
        mSettingDialog.setPageFactory(pageFactory);
        mBrightDialog = new BrightnessDialog(this);
        mAutoSettingDialog = new AutoSettingDialog(this);
        mAutoSettingDialog.setSettingDialog(mSettingDialog);
        //获取屏幕宽高
        WindowManager manage = getWindowManager();
        Display display = manage.getDefaultDisplay();
        Point displaysize = new Point();
        display.getSize(displaysize);
        //保持屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //改变屏幕亮度
        if (!config.isSystemLight()) {
            BrightnessUtil.setBrightness(this, config.getLight());
        }
        mBookId = chapter.getBook_id();
        //壳子SD本地书籍
        if (mBookId.contains("/")) {
            tv_comment.setVisibility(View.GONE);
            titlebar_share.setVisibility(View.GONE);
        }
        bookpage.setPageMode(config.getPageMode());
        pageFactory.setPageWidget(bookpage);
        pageFactory.setLineSpacingMode(config.getLineSpacingMode());
        pageFactory.setFontSize((int) config.getFontSize());
        if (config.getPageMode() != 4) {
            pageFactory.openBook(0, chapter, null);
        } else {
            SettingDialog.scroll = true;
            pageFactory.openBook(4, chapter, null);
        }
        ReadTwoBook();
        initDayOrNight();
        initListener();
        bookpage_scroll_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }
        });
        activity_read_shangyizhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chengeChapter(false);
            }
        });
        activity_read_xiayizhang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chengeChapter(true);
            }
        });
    }

    private void chengeChapter(boolean flag) {
        int chapter_possition = flag ? (pageFactory.chapterItem.getDisplay_order() + 1) : (pageFactory.chapterItem.getDisplay_order() - 1);
        String chapter_id = flag ? pageFactory.chapterItem.getNext_chapter_id() : pageFactory.chapterItem.getPre_chapter_id();
        ChapterManager.getInstance(ReadActivity.this).getChapter(chapter_possition, chapter_id, new ChapterManager.QuerychapterItemInterface() {
            @Override
            public void success(final ChapterItem querychapterItem) {
                final String nextChapterId = querychapterItem.getChapter_id();
                if (querychapterItem.getChapter_path() == null) {
                    String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(mBookId + "/").concat(nextChapterId + "/").concat(querychapterItem.getIs_preview() + "/").concat(querychapterItem.getIs_new_content()).concat(querychapterItem.getUpdate_time()).concat(".txt");
                    if (FileManager.isExist(path)) {
                        ContentValues values = new ContentValues();
                        values.put("chapter_path", path);
                        LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", mBookId, nextChapterId);
                        querychapterItem.setChapter_path(path);
                        if (SettingDialog.scroll) {
                            SettingDialog.scroll = false;
                            pageFactory.openBook(4, querychapterItem, null);
                            SettingDialog.scroll = true;
                        }
                    } else {
                        ChapterManager.notfindChapter(ShareUitls.getString(activity, PrefConst.NOVEL_API, "") + ReaderConfig.chapter_text, pageFactory.chapterItem, mBookId, pageFactory.chapterItem.getChapter_id(), new ChapterManager.ChapterDownload() {
                            @Override
                            public void finish() {
                                if (SettingDialog.scroll) {
                                    SettingDialog.scroll = false;
                                    pageFactory.openBook(4, querychapterItem, null);
                                    SettingDialog.scroll = true;
                                }
                            }
                        });
                    }
                } else {
                    if (SettingDialog.scroll) {
                        SettingDialog.scroll = false;
                        pageFactory.openBook(4, querychapterItem, null);
                        SettingDialog.scroll = true;
                    }
                }
                ChapterManager.getInstance(ReadActivity.this).addDownloadTask(true, flag ? querychapterItem.getNext_chapter_id() : querychapterItem.getPre_chapter_id(), new ChapterManager.ChapterDownload() {
                    @Override
                    public void finish() {
                    }
                });
            }

            @Override
            public void fail() {
            }
        });
    }

    protected void initListener() {
        mSettingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            }
        });
        mSettingDialog.setSettingListener(new SettingDialog.SettingListener() {
            @Override
            public void changeSystemBright(Boolean isSystem, float brightness) {
                if (!isSystem) {
                    BrightnessUtil.setBrightness(ReadActivity.this, brightness);
                } else {
                    int bh = BrightnessUtil.getScreenBrightness(ReadActivity.this);
                    BrightnessUtil.setBrightness(ReadActivity.this, bh);
                }
            }

            @Override
            public void changeFontSize(int fontSize) {
                pageFactory.changeFontSize(fontSize);
            }

            @Override
            public void changeTypeFace(Typeface typeface) {
                pageFactory.changeTypeface(typeface);
            }

            @Override
            public void changeBookBg(int type) {
                pageFactory.changeBookBg(type);
            }

            @Override
            public void changeLineSpacing(int mode) {
                pageFactory.changeLineSpacing(mode);
            }
        });
        pageFactory.setPageEvent(new PageFactory.PageEvent() {//小说翻页监听
            @Override
            public void changeProgress(float progress) {//翻页
            }

            @Override
            public void onSwitchChapter(BookUtil oldBookUtil) {//章节变化
                if (pageFactory != null) {
                    resetSaData(LanguageUtil.getString(activity, pageFactory.mNextPage ? R.string.refer_page_next_chapter : R.string.refer_page_previous_chapter), oldBookUtil);
                }
            }
        });
        bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                if (AutoProgressBar.getInstance().isStarted()) {
                    if (!mAutoSettingDialog.isShowing()) {
                        AutoProgressBar.getInstance().pause();
                        mAutoSettingDialog.show();
                    }
                    return;
                }
                if (isShow) {
                    hideReadSetting();
                } else {
                    showReadSetting();
                }
            }

            @Override
            public Boolean prePage() {
                if (AutoProgressBar.getInstance().isStarted()) {
                    AutoProgressBar.getInstance().pause();
                }
                if (isShow || isSpeaking) {
                    return false;
                }
                try {
                    pageFactory.prePage();
                } catch (Exception e) {
                }
                return !pageFactory.isfirstPage();
            }

            @Override
            public Boolean nextPage() {
                if (AutoProgressBar.getInstance().isStarted()) {
                    AutoProgressBar.getInstance().pause();
                }
                Utils.printLog("setTouchListener", "nextPage");
                if (isShow || isSpeaking) {
                    return false;
                }
                try {
                    pageFactory.nextPage();
                } catch (Exception e) {
                }
                return !pageFactory.islastPage();
            }

            @Override
            public void cancel() {
                pageFactory.cancelPage();
            }
        });
        if (!ReaderConfig.USE_SHARE) {
            titlebar_share.setVisibility(View.GONE);
        } else {
            titlebar_share.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (baseBook != null) {
                        String url = ReaderConfig.getBaseUrl() + "/site/share?uid=" + Utils.getUID(ReadActivity.this) + "&book_id=" + mBookId + "&osType=2&product=1";
                        UMWeb web = new UMWeb(url);
                        web.setTitle(baseBook.getName());//标题
                        web.setThumb(new UMImage(ReadActivity.this, baseBook.getCover()));  //缩略图
                        web.setDescription(baseBook.getDescription());//描述
                        MyShare.Share(ReadActivity.this, "", web);
                    }
                }
            });
        }
        titlebar_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (App.isVip(activity)) {
                    new DownDialog().getDownoption(ReadActivity.this, baseBook, pageFactory.chapterItem);
                } else {
                    MyToash.Toash(activity, getString(R.string.down_toast_msg));
                }
            }
        });
    }

    public PageWidget getBookPage() {
        return bookpage;
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReadStarTime = System.currentTimeMillis();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (AutoProgressBar.getInstance().isStarted()) {
                if (!mAutoSettingDialog.isShowing()) {
                    AutoProgressBar.getInstance().pause();
                    mAutoSettingDialog.show();
                    return true;
                }
            }
            if (isShow) {
                hideReadSetting();
                return true;
            }
            if (mSettingDialog.isShowing()) {
                mSettingDialog.hide();
                return true;
            }
            if (mBrightDialog.isShowing()) {
                mBrightDialog.hide();
                return true;
            }
            if (baseBook.isAddBookSelf() == 1) {
                handleAnimation();
                finish();
            } else {
                askIsNeedToAddShelf();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 询问是否加入书架
     */
    private void askIsNeedToAddShelf() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_add_shelf, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.2f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                handleAnimation();
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                baseBook.saveIsexist(1);
                baseBook.setAddBookSelf(1);
                List<BaseBook> list = new ArrayList<>();
                list.add(baseBook);
                EventBus.getDefault().post(new RefreshBookSelf(list));
                EventBus.getDefault().post(new RefreshBookInfoEvent(true));
                dialog.dismiss();
                handleAnimation();
                finish();
            }
        });
        dialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!AutoProgressBar.getInstance().isStop()) {
            AutoProgressBar.getInstance().stop();
        }
        if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
        }
        long mReadEndTime = System.currentTimeMillis();
        long readTime = (mReadEndTime - mReadStarTime) / 1000 / 60;
        if (readTime >= 1 && Utils.isLogin(this)) {
            ReaderParams params = new ReaderParams(this);
            params.putExtraParams("minutes", String.valueOf(readTime));
            String json = params.generateParamsJson();
            HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mReadTime, json, false, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(String result) {
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        }
    }

    public static boolean openBook(final BaseBook baseBook, final ChapterItem chapterItem, Activity context) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(EXTRA_CHAPTER, chapterItem);
        intent.putExtra(EXTRA_BOOK, baseBook);
        intent.putExtra(REFER_PAGE_EXT_KAY, StringUtils.isEmpty(context.getTitle().toString()) ? "" : context.getTitle());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        context.overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
        context.startActivity(intent);
        Utils.hideLoadingDialog();
        if (!ReaderConfig.CatalogInnerActivityOpen) {
            PageFactory.close_AD = false;
        }
        ReaderConfig.CatalogInnerActivityOpen = false;
        return true;
    }

    /**
     * 隐藏菜单。沉浸式阅读
     */
    public void initDayOrNight() {
        mDayOrNight = config.getDayOrNight();
        if (mDayOrNight) {
            activity_read_change_day_night.setImageResource(R.mipmap.light_mode);
        } else {
            activity_read_change_day_night.setImageResource(R.mipmap.night_mode);
        }
    }

    //改变显示模式
    public void changeDayOrNight() {
        if (mDayOrNight) {
            mDayOrNight = false;
            activity_read_change_day_night.setImageResource(R.mipmap.night_mode);
        } else {
            mDayOrNight = true;
            activity_read_change_day_night.setImageResource(R.mipmap.light_mode);
        }
        config.setDayOrNight(mDayOrNight);
        pageFactory.setDayOrNight(mDayOrNight);
    }

    private void showNovelGuide() {
        NewbieGuide.with(activity)
                .setLabel("guideNovelOpen")
                .setShowCounts(1)//控制次数
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(bookpop_bottom)
                        .setLayoutRes(R.layout.novel_look_guide, R.id.img_know)
                        .setEverywhereCancelable(false))
                .show();
    }

    //设置菜单
    private void showReadSetting() {
        isShow = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.menu_ins);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.menu_in);
        activity_read_bottom_view.startAnimation(topAnim);
        activity_read_top_menu.startAnimation(bottomAnim);
        activity_read_bottom_view.setVisibility(View.VISIBLE);
        activity_read_top_menu.setVisibility(View.VISIBLE);
        showNovelGuide();
    }

    private void hideReadSetting() {
        isShow = false;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.menu_outs);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.menu_out);
        if (activity_read_bottom_view.getVisibility() == View.VISIBLE) {
            activity_read_bottom_view.startAnimation(topAnim);
        }
        if (activity_read_top_menu.getVisibility() == View.VISIBLE) {
            activity_read_top_menu.startAnimation(bottomAnim);
        }
        activity_read_bottom_view.setVisibility(View.GONE);
        activity_read_top_menu.setVisibility(View.GONE);
    }

    public PageFactory getPageFactory() {
        return pageFactory;
    }

    @OnClick({R.id.tv_noad, R.id.tv_brightness, R.id.activity_read_top_back_view, R.id.tv_directory, R.id.tv_comment, R.id.tv_setting,
            R.id.bookpop_bottom, R.id.activity_read_bottom_view, R.id.activity_read_change_day_night, R.id.activity_read_buttom_boyin_close,
            R.id.activity_read_buttom_boyin_go, R.id.titlebar_boyin})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_directory:
                if (CatalogInnerActivity.activity != null) {
                    CatalogInnerActivity.activity.finish();
                }
                hideReadSetting();
                Intent intent = new Intent(this, CatalogInnerActivity.class);
                intent.putExtra("book_id", mBookId);
                intent.putExtra("book", baseBook);
                intent.putExtra("display_order", pageFactory.chapterItem.getDisplay_order());
                startActivity(intent);
                break;
            case R.id.activity_read_change_day_night:
                changeDayOrNight();
                break;
            case R.id.tv_comment:
                hideReadSetting();
                //打开评论页面
                Intent intentComment = new Intent(this, CommentListActivity.class);
                intentComment.putExtra("book_id", mBookId);
                startActivity(intentComment);
                break;
            case R.id.tv_setting:
                hideReadSetting();
                mSettingDialog.setProgressBar(auto_read_progress_bar);
                mSettingDialog.show();
                break;
            case R.id.bookpop_bottom:
                break;
            case R.id.activity_read_bottom_view:
                break;
            case R.id.activity_read_top_back_view:
                if (baseBook.isAddBookSelf() == 1) {
                    handleAnimation();
                    finish();
                } else {
                    askIsNeedToAddShelf();
                }
                break;
            case R.id.tv_brightness:
                hideReadSetting();
                mBrightDialog.show();
                break;
            case R.id.tv_noad:
                hideReadSetting();

                if (activity != null) {
                    if (MainHttpTask.getInstance().Gotologin(activity)) {
                        startActivityForResult(AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_book_read), 3), 301);
                    }
                }

                break;
            case R.id.activity_read_buttom_boyin_close:
                AppPrefs.putSharedBoolean(activity, mBookId, true);
                activity_read_buttom_boyin_item.setVisibility(View.GONE);
                break;
            case R.id.activity_read_buttom_boyin_go:
                jumpBoyin();
                break;
            case R.id.titlebar_boyin:
                jumpBoyin();
                break;
        }
    }

    private void jumpBoyin() {
        String baseH5Url = App.getBaseH5Url();
        String url = null;
        try {
            if (soundBookInfoBean != null) {
                NovelBoyinModel.SoundBookInfoBean sound_book_info = soundBookInfoBean.getSound_book_info();
                url = baseH5Url + "/player?ncid=" + sound_book_info.getFirst_chapter_id() + "&nid=" + sound_book_info.getNid() + "&acname=" + URLEncoder.encode(sound_book_info.getName(), "UTF-8") + "&platform=native";
                activity.startActivity(new Intent(activity, AboutActivity.class).
                        putExtra("url", url).putExtra("type", "boyin").putExtra("title", sound_book_info.getName()));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void handleAnimation() {
        try {
            if (BookShelfOpen) {
                EventBus.getDefault().post(new CloseAnimationEvent());
            }
        } catch (Exception e) {
        }
    }

    //每日阅读两本书 就上传接口  完成阅读任务
    public void ReadTwoBook() {
        if (!ReaderConfig.USE_PAY) {
            return;
        }
        if (!Utils.isLogin(this)) {
            return;
        }
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");//设置日期格式
        final String ReadTwoBookDate = df.format(new Date());// new Date()为获取当前系统时间，也可使用当前时间戳
        final String flag = ShareUitls.getString(this, "ReadTwoBookDate", ReadTwoBookDate + "-0-0");
        String[] flag2 = flag.split("-");
        if (!flag2[0].equals(ReadTwoBookDate)) {
            ShareUitls.putString(this, "ReadTwoBookDate", ReadTwoBookDate + "-" + mBookId + "-0");
            return;
        }
        if (!flag2[2].equals("0")) {
            return;
        } else if (flag2[1].equals("0")) {
            ShareUitls.putString(this, "ReadTwoBookDate", ReadTwoBookDate + "-" + mBookId + "-0");
            return;
        } else if (flag2[1].equals(mBookId)) {
            return;
        }
        final ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.task_read, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                        ShareUitls.putString(ReadActivity.this, "ReadTwoBookDate", flag + mBookId);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void NoAD(final Activity activity, final PageFactory pageFactory, final ChapterItem chapterItem, final boolean AUTO_NOAD) {
        if (pageFactory.close_AD) {
            MyToash.ToashSuccess(activity, "广告已关闭");
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BookConfig.del_ad, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        if (result.equals("true")) {
                            pageFactory.close_AD = true;
                            EventBus.getDefault().post(new RefreshMine(null));
                            MyToash.ToashSuccess(activity, "广告已关闭");
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void acceptNovelBoyin(Activity activity, String name) {
        MyPicasso.GlideImageNoSize(activity, baseBook.getCover(), activity_read_buttom_boyin_img, R.mipmap.book_def_v);
        activity_read_buttom_boyin_tittle.setText(String.format(getString(R.string.string_novel_boyin_tittle), name));
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("book_name", name);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + BookConfig.novel_boyin, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            if (jsonObject.getJSONObject("sound_book_info").isNull("name")) {
                                activity_read_buttom_boyin_item.setVisibility(View.GONE);
                                titlebar_boyin.setVisibility(View.GONE);
                            } else {
                                boolean isClosed = AppPrefs.getSharedBoolean(activity, mBookId, false);
                                if (!isClosed) {
                                    soundBookInfoBean = new Gson().fromJson(result, NovelBoyinModel.class);
                                    activity_read_buttom_boyin_item.setVisibility(View.VISIBLE);
                                    titlebar_boyin.setVisibility(View.VISIBLE);
                                } else {
                                    activity_read_buttom_boyin_item.setVisibility(View.GONE);
                                }
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

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    getWebViewAD(activity);
                    handler.sendEmptyMessageDelayed(1, 30000);
                    break;
                case 2:
                    break;
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            handler.removeMessages(1);
        } catch (Exception e) {
        }
        if (pageFactory != null) {
            setMHContentPageEvent(pageFactory.getBookUtil());
            pageFactory.clear();
        }
        bookpage = null;
        unregisterReceiver(myReceiver);
        isSpeaking = false;
        dismissAllDialog();
        SensorsDataAPI.sharedInstance().trackTimerEnd(SaEventConfig.XS_CONTENT_PAGE_EVENT);
    }

    public void getWebViewAD(Activity activity) {
        if (baseAd == null) {
            for (int i = 0; i < ReaderConfig.NOVEL_SDK_AD.size(); i++) {
                AppUpdate.ListBean listBean = ReaderConfig.NOVEL_SDK_AD.get(i);
                if (TextUtils.equals(listBean.getPosition(), "12") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                    mIsSdkAd = true;
                    sdkAd(activity);
                    return;
                }
            }
            if (!mIsSdkAd) {
                localAd(activity);
            }
        } else {
            adClick(activity);
        }
    }

    private void sdkAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_NOVEL_BOTTOM_DEEBUG : BuildConfig.XAD_EVN_POS_NOVEL_BOTTOM, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    baseAd = new BaseAd();
                    if (App.isShowSdkAd(activity, adInfo.getAdExtra().get("ad_show_type"))) {
                        baseAd.setAd_skip_url(adInfo.getAdExtra().get("ad_skip_url"));
                        baseAd.setAd_title(adInfo.getMaterial().getTitle());
                        baseAd.setAd_image(adInfo.getMaterial().getImageUrl());
                        baseAd.setUser_parame_need(adInfo.getAdExtra().get("user_parame_need"));
                        baseAd.setAd_url_type(Integer.valueOf(adInfo.getAdExtra().get("ad_url_type")));
                        baseAd.setAdvert_interval(Integer.valueOf(adInfo.getAdExtra().get("advert_interval")));
                        baseAd.setAd_type(Integer.valueOf(adInfo.getAdExtra().get("ad_type")));
                        visible = baseAd.getAdvert_interval();
                        activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
                    } else {
                        activity_read_buttom_ad_layout.setVisibility(View.GONE);
                    }
                    adClick(activity);
                } catch (Exception e) {
                    localAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localAd(activity);
            }
        });
    }

    private void localAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", XIAOSHUO + "");
        params.putExtraParams("position", "12");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            baseAd = new Gson().fromJson(result, BaseAd.class);
                            visible = baseAd.getAdvert_interval();
                            activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
                            adClick(activity);
                        } catch (Exception e) {
                            activity_read_buttom_ad_layout.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        activity_read_buttom_ad_layout.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void adClick(Activity activity) {
        if (baseAd != null && baseAd.ad_type == 1) {
            insert_todayone2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpBookAd(activity);
                }
            });
            mIvAd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    JumpBookAd(activity);
                }
            });
            mIvClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    activity_read_buttom_ad_layout.setVisibility(View.GONE);
                }
            });
            activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
            MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, mIvAd);
        }
    }

    private void JumpBookAd(Activity activity) {
        if (baseAd != null) {
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
    }

    /**
     * 获取小说详情数据
     */
    private void getBookInfo() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookInfoUrl, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String result) {
                try {
                    mInfoBookItem = new Gson().fromJson(result, InfoBookItem.class);
                } catch (Exception e) {
                }
            }

            @Override
            public void onErrorResponse(String ex) {
            }
        });
    }

    /**
     * 根据是否免费调整UI变化
     */
    private void uiFreeCharge() {
        uiFreeCharge(tv_noad);
    }

    /**
     * onDestroy销毁关闭所有对话框
     */
    private void dismissAllDialog() {
        dismissAllDialog(mBrightDialog, mSettingDialog, mAutoSettingDialog);
    }

    /**
     * 神策小说阅读页埋点
     */
    private void setMHContentPageEvent(BookUtil bookUtil) {
        if (mInfoBookItem == null) {
            getBookInfo();
            return;
        }
        if (bookUtil != null) {
            SensorsDataHelper.setXSContentPageEvent(new Long(bookUtil.getBookLen()).intValue(),//当前章总页数
                    new Long(bookUtil.getPosition()).intValue(),//当前章已读页数
                    new Long(DateUtils.getCurrentTimeDifferenceSecond(mOpenCurrentTime)).intValue(),//停留时长
                    getPropIdList(),//属性ID
                    getTagIdList(),//分类ID
                    mReferPage,//前向页面
                    Integer.valueOf(bookUtil.getBook_id()),//小说ID
                    Integer.valueOf(bookUtil.getChapter_id()),//小说当前章节ID
                    ChapterManager.getInstance(ReadActivity.this).getmTotalChapter(), //小说总章节
                    0);//作者ID
        }
    }

    /**
     * 神策埋点 获取prop_id属性数据
     */
    private List<String> getPropIdList() {
        return getPropIdList(mInfoBookItem);
    }

    public static List<String> getPropIdList(InfoBookItem infoBookItem) {
        List<String> propId = new ArrayList<>();
        if (infoBookItem != null) {
            putPropIdL(propId, infoBookItem.book.is_new, "新书");
            putPropIdL(propId, infoBookItem.book.is_hot, "热门");
            putPropIdL(propId, infoBookItem.book.is_yy, "爽文");
            putPropIdL(propId, infoBookItem.book.is_greatest, "精选");
            putPropIdL(propId, infoBookItem.book.is_god, "大神");
        }
        return propId;
    }

    private static void putPropIdL(List<String> list, String PropId, String PropName) {
        if ("1".equals(PropId)) {
            list.add(PropName);
        }
    }

    /**
     * 神策埋点 获取tag_id分类信息
     */
    private List<String> getTagIdList() {
        return getTagIdList(mInfoBookItem);
    }

    public static List<String> getTagIdList(InfoBookItem infoBookItem) {
        List<String> tagId = new ArrayList<>();
        if (infoBookItem != null) {
            tagId.add(infoBookItem.book.cid1);
        }
        return tagId;
    }

    /**
     * 点击上一章 下一章 触发埋点并重置埋点数据
     *
     * @param referPage
     */
    private void resetSaData(String referPage, BookUtil bookUtil) {
        setMHContentPageEvent(bookUtil);
        setOpenCurrentTime();
        mReferPage = String.valueOf(referPage);
    }

    /**
     * 设置打开漫画阅读当前时间
     */
    private void setOpenCurrentTime() {
        mOpenCurrentTime = DateUtils.currentTime();
    }

    /**
     * 控制广告翻多少页后显示
     */
    private void setButtonADVisible() {
        bookpage.setBackPage(new PageWidget.BackPage() {
            @Override
            public void backPage(int page) {
                if (visible > 0) {
                    if (Math.abs(page) % (visible + 1) == 0) {
                        activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
                    } else {
                        activity_read_buttom_ad_layout.setVisibility(View.GONE);
                    }
                } else if (visible == 0) {
                    activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
                } else {
                    activity_read_buttom_ad_layout.setVisibility(View.GONE);
                }
            }
        });
    }
}