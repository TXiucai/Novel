package com.heiheilianzai.app.read;

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

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.activity.CatalogInnerActivity;
import com.heiheilianzai.app.activity.CommentListActivity;
import com.heiheilianzai.app.activity.WebViewActivity;
import com.heiheilianzai.app.bean.BaseAd;
import com.heiheilianzai.app.bean.ChapterItem;
import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.book.config.BookConfig;
import com.heiheilianzai.app.config.MainHttpTask;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.dialog.DownDialog;
import com.heiheilianzai.app.eventbus.CloseAnimation;
import com.heiheilianzai.app.eventbus.RefreshBookInfo;
import com.heiheilianzai.app.eventbus.RefreshBookSelf;
import com.heiheilianzai.app.eventbus.RefreshMine;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.read.dialog.AutoProgress;
import com.heiheilianzai.app.read.dialog.AutoSettingDialog;
import com.heiheilianzai.app.read.dialog.BrightnessDialog;
import com.heiheilianzai.app.read.dialog.SettingDialog;
import com.heiheilianzai.app.read.manager.ChapterManager;
import com.heiheilianzai.app.read.util.BrightnessUtil;
import com.heiheilianzai.app.read.util.PageFactory;
import com.heiheilianzai.app.read.view.PageWidget;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.MScrollView;
import com.heiheilianzai.app.view.ScrollEditText;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.book.fragment.NovelFragmentNew.BookShelfOpen;
import static com.heiheilianzai.app.config.ReaderConfig.READBUTTOM_HEIGHT;
import static com.heiheilianzai.app.config.ReaderConfig.USE_AD;
import static com.heiheilianzai.app.config.ReaderConfig.XIAOSHUO;

/**
 * Created by Administrator on 2016/7/15 0015.
 */
public class ReadActivity extends BaseReadActivity {
    private final static String EXTRA_BOOK = "book";
    private final static String EXTRA_CHAPTER = "chapter";
    @BindView(R2.id.bookpage)
    PageWidget bookpage;
    @BindView(R2.id.activity_read_top_back_view)
    LinearLayout activity_read_top_back_view;
    @BindView(R2.id.activity_read_top_menu)
    View activity_read_top_menu;
    @BindView(R2.id.tv_directory)
    TextView tv_directory;
    @BindView(R2.id.tv_brightness)
    TextView tv_brightness;
    @BindView(R2.id.tv_comment)
    TextView tv_comment;
    @BindView(R2.id.tv_setting)
    TextView tv_setting;
    @BindView(R2.id.bookpop_bottom)
    RelativeLayout bookpop_bottom;
    @BindView(R2.id.activity_read_bottom_view)
    RelativeLayout activity_read_bottom_view;
    @BindView(R2.id.activity_read_change_day_night)
    ImageView activity_read_change_day_night;
    @BindView(R2.id.titlebar_share)
    RelativeLayout titlebar_share;
    @BindView(R2.id.titlebar_down)
    RelativeLayout titlebar_down;
    @BindView(R2.id.activity_read_firstread)
    ImageView activity_read_firstread;
    @BindView(R2.id.auto_read_progress_bar)
    ProgressBar auto_read_progress_bar;
    @BindView(R2.id.bookpage_scroll)
    MScrollView bookpage_scroll;
    @BindView(R2.id.bookpage_scroll_text)
    ScrollEditText bookpage_scroll_text;
    @BindView(R2.id.list_ad_view_layout)
    public FrameLayout insert_todayone2;
    @BindView(R2.id.activity_read_buttom_ad_layout)
    public FrameLayout activity_read_buttom_ad_layout;
    @BindView(R2.id.tv_noad)
    TextView tv_noad;
    @BindView(R2.id.activity_read_shangyizhang)
    Button activity_read_shangyizhang;
    @BindView(R2.id.activity_read_xiayizhang)
    Button activity_read_xiayizhang;
    @BindView(R2.id.activity_read_purchase_layout)
    LinearLayout activity_read_purchase_layout;
    @BindView(R2.id.activity_read_purchase_layout2)
    public LinearLayout activity_read_purchase_layout2;

    public static boolean USE_BUTTOM_AD;
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
            if (USE_BUTTOM_AD && is_vip == 1) {
                ReaderConfig.USE_AD = false;
                USE_BUTTOM_AD = false;
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bookpage.getLayoutParams();
                layoutParams.height = mScreenHeight;
                bookpage.setLayoutParams(layoutParams);
                activity_read_buttom_ad_layout.setVisibility(View.GONE);
                insert_todayone2.setVisibility(View.GONE);
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
        MyToash.Log("frameLayoutToday", ReaderConfig.USE_AD + "");
    }

    @Override
    public void initView() {
        if (!NotchScreen.hasNotchScreen(this)) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) activity_read_top_menu.getLayoutParams();
            layoutParams.height = ImageUtil.dp2px(this, 50);
            activity_read_top_menu.setLayoutParams(layoutParams);
        }
        uiFreeCharge();
    }

    @Override
    public void initData() {
        if (USE_BUTTOM_AD) {
            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bookpage.getLayoutParams();
            layoutParams.height = mScreenHeight - ImageUtil.dp2px(activity, 60);
            bookpage.setLayoutParams(layoutParams);
            tv_noad.setVisibility(View.VISIBLE);
            getWebViewAD(activity);
            handler.sendEmptyMessageDelayed(1, 30000);
        } else {
            activity_read_buttom_ad_layout.setVisibility(View.GONE);
        }
        if (!USE_AD) {
            insert_todayone2.setVisibility(View.GONE);
            tv_noad.setVisibility(View.GONE);
        }
        bookpage.setADview(insert_todayone2);
        next();
    }

    private void next() {
        config = ReadingConfig.getInstance();
        //  阅读管理器
        //获取intent中的携带的信息
        Intent intent = getIntent();
        chapter = (ChapterItem) intent.getSerializableExtra(EXTRA_CHAPTER);
        baseBook = (BaseBook) intent.getSerializableExtra(EXTRA_BOOK);
        pageFactory = new PageFactory(baseBook, bookpage_scroll, bookpage_scroll_text, insert_todayone2, this);
        pageFactory.setPurchaseLayout(activity_read_purchase_layout, activity_read_purchase_layout2);
        if (ReaderConfig.USE_AD) {
            pageFactory.getWebViewAD(ReadActivity.this);//获取广告
        }
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
                    String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(mBookId + "/").concat(nextChapterId + "/").concat(querychapterItem.getIs_preview() + "/").concat(querychapterItem.getUpdate_time()).concat(".txt");
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
                        ChapterManager.notfindChapter(pageFactory.chapterItem, mBookId, pageFactory.chapterItem.getChapter_id(), new ChapterManager.ChapterDownload() {
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

        pageFactory.setPageEvent(new PageFactory.PageEvent() {
            @Override
            public void changeProgress(float progress) {
            }
        });
        bookpage.setTouchListener(new PageWidget.TouchListener() {
            @Override
            public void center() {
                if (AutoProgress.getInstance().isStarted()) {
                    if (!mAutoSettingDialog.isShowing()) {
                        AutoProgress.getInstance().pause();
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
                if (AutoProgress.getInstance().isStarted()) {
                    AutoProgress.getInstance().pause();
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
                if (AutoProgress.getInstance().isStarted()) {
                    AutoProgress.getInstance().pause();
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
                new DownDialog().getDownoption(ReadActivity.this, baseBook, pageFactory.chapterItem);
            }
        });
    }

    public PageWidget getBookPage() {
        return bookpage;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (AutoProgress.getInstance().isStarted()) {
                if (!mAutoSettingDialog.isShowing()) {
                    AutoProgress.getInstance().pause();
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
                EventBus.getDefault().post(new RefreshBookInfo(true));
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
        if (!AutoProgress.getInstance().isStop()) {
            AutoProgress.getInstance().stop();
        }
        if (mSettingDialog.isShowing()) {
            mSettingDialog.dismiss();
        }
    }

    public static boolean openBook(final BaseBook baseBook, final ChapterItem chapterItem, Activity context) {
        Intent intent = new Intent(context, ReadActivity.class);
        intent.putExtra(EXTRA_CHAPTER, chapterItem);
        intent.putExtra(EXTRA_BOOK, baseBook);
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

    //设置菜单
    private void showReadSetting() {
        isShow = true;
        Animation bottomAnim = AnimationUtils.loadAnimation(this, R.anim.menu_ins);
        Animation topAnim = AnimationUtils.loadAnimation(this, R.anim.menu_in);
        activity_read_bottom_view.startAnimation(topAnim);
        activity_read_top_menu.startAnimation(bottomAnim);
        activity_read_bottom_view.setVisibility(View.VISIBLE);
        activity_read_top_menu.setVisibility(View.VISIBLE);
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

    @OnClick({R.id.tv_noad, R.id.tv_brightness, R.id.activity_read_top_back_view, R.id.tv_directory, R.id.tv_comment, R.id.tv_setting, R.id.bookpop_bottom, R.id.activity_read_bottom_view, R.id.activity_read_change_day_night})
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
                if (ReaderConfig.USE_AD) {
                    if (MainHttpTask.getInstance().Gotologin(activity)) {
                        startActivityForResult(new Intent(activity, AcquireBaoyueActivity.class), 301);
                    }
                }
                break;
        }
    }

    public static void handleAnimation() {
        try {
            if (BookShelfOpen) {
                EventBus.getDefault().post(new CloseAnimation());
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
            if (USE_AD) {
                handler.removeMessages(1);
            }
        } catch (Exception e) {
        }
        pageFactory.clear();
        bookpage = null;
        unregisterReceiver(myReceiver);
        isSpeaking = false;
    }

    public void getWebViewAD(Activity activity) {
        if (baseAd == null) {
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
                                if (baseAd.ad_type == 1) {
                                    ReadActivity.USE_BUTTOM_AD = true;
                                    insert_todayone2.setOnClickListener(new View.OnClickListener() {
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
                                    if (list_ad_view_img == null) {
                                        list_ad_view_img = new ImageView(activity);
                                        list_ad_view_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                                        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ImageUtil.dp2px(activity, READBUTTOM_HEIGHT));
                                        activity_read_buttom_ad_layout.addView(list_ad_view_img, params);
                                    }
                                    ImageLoader.getInstance().displayImage(baseAd.ad_image, list_ad_view_img, ReaderApplication.getOptions());
                                } else {
                                    ReadActivity.USE_BUTTOM_AD = false;
                                }
                            } catch (Exception e) {
                                ReadActivity.USE_BUTTOM_AD = false;
                            }
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        } else {
            if (baseAd.ad_type == 1) {
                insert_todayone2.setOnClickListener(new View.OnClickListener() {
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
                if (list_ad_view_img == null) {
                    list_ad_view_img = new ImageView(activity);
                    list_ad_view_img.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ImageUtil.dp2px(activity, READBUTTOM_HEIGHT));
                    insert_todayone2.addView(list_ad_view_img, params);
                }
                ImageLoader.getInstance().displayImage(baseAd.ad_image, list_ad_view_img, ReaderApplication.getOptions());
            }
        }
    }

    /**
     * 根据是否免费调整UI变化
     */
    private void uiFreeCharge() {
        uiFreeCharge(tv_noad);
    }
}
