package com.heiheilianzai.app.ui.activity.read;

import static com.heiheilianzai.app.constant.ReaderConfig.READBUTTOM_HEIGHT;
import static com.heiheilianzai.app.ui.fragment.book.NewNovelFragment.BookShelfOpen;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
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

import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.FragmentManager;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.ReadNovelService;
import com.heiheilianzai.app.component.ScreenOnAndOffReceiver;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.constant.sa.SaEventConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.InfoBookItem;
import com.heiheilianzai.app.model.NovelBoyinModel;
import com.heiheilianzai.app.model.TTSFilterType;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.event.CloseAnimationEvent;
import com.heiheilianzai.app.model.event.NovelOpenOtherEvent;
import com.heiheilianzai.app.model.event.RefreshBookInfoEvent;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.SetTimerEvent;
import com.heiheilianzai.app.model.event.StartOtherNovel;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.CatalogInnerActivity;
import com.heiheilianzai.app.ui.activity.CommentListActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.dialog.DownDialog;
import com.heiheilianzai.app.ui.dialog.read.AutoProgressBar;
import com.heiheilianzai.app.ui.dialog.read.AutoSettingDialog;
import com.heiheilianzai.app.ui.dialog.read.BrightnessDialog;
import com.heiheilianzai.app.ui.dialog.read.SettingDialog;
import com.heiheilianzai.app.ui.fragment.book.ReadSpeakDialogFragment;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.BookUtil;
import com.heiheilianzai.app.utils.BrightnessUtil;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.DialogVip;
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
import com.heiheilianzai.app.utils.manager.ReadSpeakManager;
import com.heiheilianzai.app.view.MScrollView;
import com.heiheilianzai.app.view.ScrollEditText;
import com.heiheilianzai.app.view.read.PageWidget;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * 小说阅读 Activity
 * Created by Administrator on 2016/7/15 0015.
 */
public class ReadActivity extends BaseReadActivity {
    private final static String EXTRA_BOOK = "book";
    private final static String EXTRA_CHAPTER = "chapter";
    private final static String EXTRA_PAGE = "page";
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
    @BindView(R.id.activity_read_top_ad_layout)
    public RelativeLayout mRlTopLayout;
    @BindView(R.id.activity_read_buttom_ad_iv)
    ImageView mIvAd;
    @BindView(R.id.activity_read_buttom_ad_close)
    ImageView mIvClose;
    @BindView(R.id.activity_read_top_ad_close)
    ImageView mIVTopClose;
    @BindView(R.id.tv_noad)
    TextView tv_noad;
    @BindView(R.id.activity_read_shangyizhang)
    Button activity_read_shangyizhang;
    @BindView(R.id.activity_read_xiayizhang)
    Button activity_read_xiayizhang;
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
    @BindView(R.id.activity_read_top_ad_iv)
    public ImageView activity_read_top_ad_iv;
    @BindView(R.id.activity_read_speaker)
    public ImageView activity_read_speaker;
    @BindView(R.id.activity_read_speaker_out)
    public ImageView activity_read_speaker_out;
    @BindView(R.id.activity_read_tittle)
    public TextView activity_read_tittle;
    @BindView(R.id.activity_read_tittle_out)
    public TextView activity_read_tittle_out;
    @BindView(R.id.rl_listen_out)
    public RelativeLayout activity_read_listen_out;
    @BindView(R.id.rl_listen)
    public RelativeLayout activity_read_listen;


    private ReadSpeakManager readSpeakManager;
    private ReadSpeakDialogFragment readSpeakDialogFragment;
    private FragmentManager mFragmentManager;
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
    ImageView list_ad_view_img;
    InfoBookItem mInfoBookItem;
    String mReferPage;//从哪个页面打开小说阅读(神策埋点数据)
    long mOpenCurrentTime;//打开小说阅读页的当前时间(每次翻动一个章节，改变一次时间)
    private NovelBoyinModel soundBookInfoBean;
    private boolean mNovelVoice;
    private boolean mNovelScreen;
    private boolean mNovelOpen;

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
    private boolean mIsActive = true;//是否处于前台

    private DevicePolicyManager devicePolicyManager = null;
    private ComponentName screenReceiver = null;
    private PowerManager powerManager = null;
    private PowerManager.WakeLock wakeLock;
    private int mScreenTime;
    public static final String TURN_NEXT = "turn_next";//翻页
    public static final String UPDATE_BG = "update_bg";//界面每句加阴影
    public static final String CLOSE_SERVICE = "close_service";//关闭服务
    private BroadcastReceiver mNovelReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case UPDATE_BG:
                    if (pageFactory != null) {
                        //每行高亮
                        //pageFactory.onDrawReadLine(bookpage.getCurPage(), pageFactory.getCurrentPage().getLines(), true, mReadLine);
                    }
                    break;
                case TURN_NEXT:
                    if (readSpeakDialogFragment != null && readSpeakDialogFragment.getShowsDialog()) {
                        readSpeakDialogFragment.dimissDialog();
                    }
                    if (isShow) {
                        hideReadSetting();
                    }
                    String book_id = (String) intent.getExtras().get("book_id");
                    if (bookpage != null && TextUtils.equals(baseBook.getBook_id(), book_id)) {
                        bookpage.next_page(true);
                    }
                    break;
                case CLOSE_SERVICE:
                    if (bookpage != null) {
                        bookpage.setmIsOpenService(false);
                        pageFactory.close_AD = false;
                    }
                    break;
            }
        }
    };
    private Handler mHanderTime = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            turnOffScreen();
        }
    };
    private Intent mIntent;
    private long mDisPlayAdTime = 0;
    private ObjectAnimator mAnimatorOut;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        mIntent = intent;
        initData();
    }

    private boolean mHaveScreenPermison = false;

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
        ReaderConfig.BANG_SCREEN = NotchScreen.hasNotchScreen(this);
        mReadStarTime = System.currentTimeMillis();
        int MHeight = ScreenSizeUtils.getInstance(this).getScreenHeight();
        //首次阅读 显示引导图
        if (ShareUitls.getString(ReadActivity.this, "FirstRead", "yes").equals("yes")) {
            ShareUitls.putString(ReadActivity.this, "FirstRead", "no");
            FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) activity_read_firstread.getLayoutParams();
            if (ReaderConfig.TOP_READ_AD != null) {
                MHeight -= ImageUtil.dp2px(this, READBUTTOM_HEIGHT);
            }
            if (ReaderConfig.BOTTOM_READ_AD != null) {
                MHeight -= ImageUtil.dp2px(this, READBUTTOM_HEIGHT);
            }
            layoutParams.height = MHeight;
            layoutParams.width = ScreenSizeUtils.getInstance(this).getScreenWidth();
            layoutParams.setMargins(0, ImageUtil.dp2px(this, READBUTTOM_HEIGHT), 0, ImageUtil.dp2px(this, READBUTTOM_HEIGHT));
            activity_read_firstread.setLayoutParams(layoutParams);
            activity_read_firstread.setImageResource(R.mipmap.icon_firstread);
            activity_read_firstread.setVisibility(View.VISIBLE);
            activity_read_firstread.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    activity_read_firstread.setVisibility(View.GONE);
                }
            });
        }
        SensorsDataAPI.sharedInstance().trackTimerStart(SaEventConfig.XS_CONTENT_PAGE_EVENT);
        //注册广播
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UPDATE_BG);
        intentFilter.addAction(TURN_NEXT);
        intentFilter.addAction(CLOSE_SERVICE);
        registerReceiver(mNovelReceiver, intentFilter);

        readSpeakManager = ReadSpeakManager.getInstance()
                .initReadSetting();
        if (ReadNovelService.SERVICE_IS_LIVE) {
            bookpage.setmIsOpenService(true);
            pageFactory.close_AD = true;
        }
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
        initReadSetting();
        initAd(activity);
        startListenAnim();
    }

    private void startListenAnim() {
        if (!TextUtils.isEmpty(ReaderConfig.guide_text)) {
            activity_read_tittle.setText(ReaderConfig.guide_text);
            activity_read_tittle_out.setText(ReaderConfig.guide_text);
        }
        handler.sendEmptyMessageDelayed(2, ReaderConfig.display_second * 1000);
        ObjectAnimator valueAnimator = ObjectAnimator.ofFloat(activity_read_speaker, "TranslationY", 0, 10, -2, 10, -2, 15);
        valueAnimator.setDuration(3000);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        valueAnimator.setRepeatMode(ValueAnimator.REVERSE);
        valueAnimator.start();
        mAnimatorOut = ObjectAnimator.ofFloat(activity_read_speaker_out, "TranslationY", 0, 10, -2, 10, -2, 15);
        mAnimatorOut.setDuration(3000);
        mAnimatorOut.setRepeatCount(ValueAnimator.INFINITE);
        mAnimatorOut.setRepeatMode(ValueAnimator.REVERSE);
        mAnimatorOut.start();
    }

    private void turnOnScreen() {
        wakeLock = powerManager.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, ReadActivity.class.getSimpleName());
        wakeLock.acquire();
        wakeLock.release();
    }

    private void turnOffScreen() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        devicePolicyManager.lockNow();
    }

    private void initReadSetting() {
        String novelTime_screen = AppPrefs.getSharedString(this, "novelTime_Screen", "0");
        if (!TextUtils.equals(novelTime_screen, "0")) {//跟随系统时间
            screenReceiver = new ComponentName(this, ScreenOnAndOffReceiver.class);
            powerManager = (PowerManager) getSystemService(POWER_SERVICE);
            devicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            mHaveScreenPermison = devicePolicyManager.isAdminActive(screenReceiver);
            if (mHaveScreenPermison) {
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
                mScreenTime = Integer.valueOf(novelTime_screen) * 1000 * 60;
                mHanderTime.postDelayed(mRunnable, mScreenTime);
            } else {
                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, screenReceiver);
                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, getString(R.string.string_read_screen_offoron));
                startActivity(intent);
            }
        }
        mNovelVoice = AppPrefs.getSharedBoolean(activity, "novelVoice_ToggleButton", false);
        mNovelScreen = AppPrefs.getSharedBoolean(activity, "novelScreen_ToggleButton", false);
        mNovelOpen = AppPrefs.getSharedBoolean(activity, "novelOpen_ToggleButton", false);
        bookpage.setmLeftScreen(mNovelScreen);
        // 获取tts 不支持的 字符
        getTTSFilter();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        initReadSetting();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (Utils.isAppOnForeground(activity)) {
            mIsActive = true;
        } else {
            mIsActive = false;
        }
        mHanderTime.removeCallbacks(mRunnable);
    }

    @Override
    public void initData() {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) bookpage.getLayoutParams();
        layoutParams.height = mScreenHeight;
        bookpage.setLayoutParams(layoutParams);
        bookpage.setADview(insert_todayone2);
        next();
        acceptNovelBoyin(activity, baseBook.getName());
        getBookInfo();
    }

    private void next() {
        if (TextUtils.equals(ReaderConfig.TTS_OPEN, "2")) {
            activity_read_listen.setVisibility(View.VISIBLE);
            activity_read_listen_out.setVisibility(View.VISIBLE);
        } else {
            activity_read_listen.setVisibility(View.GONE);
            activity_read_listen_out.setVisibility(View.GONE);
        }

        config = ReadingConfig.getInstance();
        //  阅读管理器
        //获取intent中的携带的信息
        if (mIntent == null) {
            mIntent = getIntent();
        }
        chapter = (ChapterItem) mIntent.getSerializableExtra(EXTRA_CHAPTER);
        baseBook = (BaseBook) mIntent.getSerializableExtra(EXTRA_BOOK);
        mReferPage = mIntent.getStringExtra(REFER_PAGE_EXT_KAY);
        pageFactory = new PageFactory(baseBook, bookpage_scroll, bookpage_scroll_text, insert_todayone2, this);
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
        getTips();
        bookpage_scroll_text.setOnClickListener(view -> {
            if (isShow) {
                hideReadSetting();
            } else {
                showReadSetting();
            }
        });
        activity_read_shangyizhang.setOnClickListener(view -> chengeChapter(false));
        activity_read_xiayizhang.setOnClickListener(view -> chengeChapter(true));
    }

    /**
     * 获取小说底部tips
     */
    private void getTips() {
        if (!ReaderConfig.novel_tips.equals("")) {
            List<String> tips = new ArrayList<>();
            String[] split = ReaderConfig.novel_tips.split("\r");
            for (int i = 0; i < split.length; i++) {
                tips.add(split[i]);
            }
            pageFactory.setTipStrings(tips);
        }
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
                //点击中间提示主动消失
                activity_read_listen_out.setVisibility(View.GONE);
                mAnimatorOut.cancel();
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

            @Override
            public void down() {
                if (mHaveScreenPermison) {
                    mHanderTime.removeCallbacks(mRunnable);
                }
            }

            @Override
            public void up() {
                if (mHaveScreenPermison) {
                    mHanderTime.postDelayed(mRunnable, mScreenTime);
                }
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
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {//音量向上
            try {
                if (!bookpage.ismIsOpenService()) {
                    if (mNovelVoice) {
                        bookpage.next_page(false);
                        return true;
                    }
                }
            } catch (Exception e) {
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {//音量向下
            try {
                if (!bookpage.ismIsOpenService()) {
                    if (mNovelVoice) {
                        bookpage.next_page(true);
                        return true;
                    }
                }
            } catch (Exception e) {
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

    @SuppressLint("NonConstantResourceId")
    @OnClick({R.id.tv_noad, R.id.tv_brightness, R.id.activity_read_top_back_view, R.id.tv_directory, R.id.tv_comment, R.id.tv_setting,
            R.id.bookpop_bottom, R.id.activity_read_bottom_view, R.id.activity_read_change_day_night, R.id.activity_read_buttom_boyin_close,
            R.id.activity_read_buttom_boyin_go, R.id.titlebar_boyin, R.id.rl_listen, R.id.rl_listen_out, R.id.activity_read_top_ad_close, R.id.activity_read_buttom_ad_close})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.activity_read_buttom_ad_close:
            case R.id.activity_read_top_ad_close:
                if (Utils.isLogin(activity)) {
                    if (App.isVip(activity)) {//会员
                        mRlTopLayout.setVisibility(View.GONE);
                        activity_read_buttom_ad_layout.setVisibility(View.GONE);
                        AppPrefs.putSharedLong(activity, "display_ad_days_novel", System.currentTimeMillis() + ReaderConfig.newInstance().display_ad_days_novel * 24 * 60 * 60 * 1000);
                        flushPage();
                    } else {//普通用户
                        new DialogVip().getDialogVipPop(activity, getResources().getString(R.string.dialog_tittle_vip_close_ad), false, false);
                    }
                } else {//未登录
                    MainHttpTask.getInstance().Gotologin(activity);
                }
                break;
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
                if (!bookpage.ismIsOpenService()) {
                    hideReadSetting();
                    mSettingDialog.setProgressBar(auto_read_progress_bar);
                    mSettingDialog.show();
                } else {
                    MyToash.Toash(this, getString(R.string.string_read_service_prohibit));
                }
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
            case R.id.rl_listen:
                updaeListenRecord();
                openPermission();
                break;
            case R.id.rl_listen_out:
                if (mAnimatorOut != null) {
                    mAnimatorOut.cancel();
                }
                updaeListenRecord();
                openPermission();
                break;
        }
    }

    public void openPermission() {
        readSpeakManager.load();
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            //测试要求要给一个弹窗提示
            Utils.showNotificationPermissionTip(activity);
        } else {
            startReadNovelService();
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

    /**
     * readSpeakDialogFragment
     * 听书阅读相关设置
     */
    private void initReadSpeakDialogFragment() {
        if (null == readSpeakDialogFragment && null == mFragmentManager) {
            readSpeakDialogFragment = new ReadSpeakDialogFragment();
            mFragmentManager = getSupportFragmentManager();
        }

        if (!readSpeakDialogFragment.isAdded()) {
            readSpeakDialogFragment.show(mFragmentManager, "readSpeakDialogFragment");
        }

        readSpeakDialogFragment.setDialogCallback(new ReadSpeakDialogFragment.DialogCallback() {
            @Override
            public void readSpeed(int speed) {//音速
                int readSpeed = ShareUitls.getInt(App.getAppContext(), ReadSpeakManager.READ_SPEED, 100);
                if (speed != readSpeed) {
                    readSpeakManager.setReadSpeed(speed);
                    readSpeakManager.stopReadBook(3);
                }
            }

            @Override
            public void readDiao(int diao) {//音调
                int readPitch = ReadSpeakManager.getInstance().getReadPitch();
                if (diao != readPitch) {
                    readSpeakManager.setReadPitch(diao);
                    readSpeakManager.stopReadBook(3);
                }
            }

            @Override
            public void readSe(int se) {//音色
                int readSe = ReadSpeakManager.getInstance().getReadYinSe();
                if (se != readSe) {
                    readSpeakManager.setReadYinSe(se);
                    readSpeakManager.stopReadBook(3);
                }
            }

            @Override
            public void readTimer(int mins) {//定时
                EventBus.getDefault().post(new SetTimerEvent(mins));
            }

            @Override
            public void cancelRead() {
                readSpeakManager.stopReadBook(1);
                if (ReadNovelService.SERVICE_IS_LIVE) {
                    bookpage.setmIsOpenService(false);
                    pageFactory.close_AD = false;
                }
            }
        });

    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    handler.sendEmptyMessageDelayed(1, 30000);
                    break;
                case 2:
                    activity_read_listen_out.setVisibility(View.GONE);
                    mAnimatorOut.cancel();
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
        unregisterReceiver(mNovelReceiver);
        isSpeaking = false;
        dismissAllDialog();
        SensorsDataAPI.sharedInstance().trackTimerEnd(SaEventConfig.XS_CONTENT_PAGE_EVENT);
    }

    private void initAd(Activity activity) {
        boolean isChangeAd = isOrNotchangeAd();
        if (!isChangeAd) {
            mDisPlayAdTime = AppPrefs.getSharedLong(activity, "display_ad_days_novel", 0);
        } else {
            AppPrefs.putSharedLong(activity, "display_ad_days_novel", 0);
        }
        AdInfo adInfo = BaseSdkAD.newAdInfo(ReaderConfig.BOTTOM_READ_AD);
        //广告发生改变即使还未到时间也要显示
        if (ReaderConfig.BOTTOM_READ_AD != null && (isChangeAd || System.currentTimeMillis() > mDisPlayAdTime)) {
            activity_read_buttom_ad_layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adInfo != null) {
                        XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                    }
                    JumpBookAd(activity, ReaderConfig.BOTTOM_READ_AD);
                }
            });
            activity_read_buttom_ad_layout.setVisibility(View.VISIBLE);
            if (adInfo != null) {
                MyPicasso.glideSdkAd(activity, adInfo, ReaderConfig.BOTTOM_READ_AD.ad_image, mIvAd);
            } else {
                MyPicasso.GlideImageNoSize(activity, ReaderConfig.BOTTOM_READ_AD.ad_image, mIvAd);
            }
            AppPrefs.putSharedString(activity, "novel_bottom_ad", ReaderConfig.BOTTOM_READ_AD.ad_image);
        } else {
            activity_read_buttom_ad_layout.setVisibility(View.GONE);
        }
        AdInfo adInfoTop = BaseSdkAD.newAdInfo(ReaderConfig.TOP_READ_AD);
        if (ReaderConfig.TOP_READ_AD != null && (isChangeAd || System.currentTimeMillis() > mDisPlayAdTime)) {
            mRlTopLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (adInfoTop != null) {
                        XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                    }
                    JumpBookAd(activity, ReaderConfig.TOP_READ_AD);
                }
            });
            mRlTopLayout.setVisibility(View.VISIBLE);
            if (adInfoTop != null) {
                MyPicasso.glideSdkAd(activity, adInfoTop, ReaderConfig.TOP_READ_AD.ad_image, activity_read_top_ad_iv);
            } else {
                MyPicasso.GlideImageNoSize(activity, ReaderConfig.TOP_READ_AD.ad_image, activity_read_top_ad_iv);
            }
            AppPrefs.putSharedString(activity, "novel_top_ad", ReaderConfig.TOP_READ_AD.ad_image);
        } else {
            mRlTopLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 广告是否发生改变
     *
     * @return
     */
    private boolean isOrNotchangeAd() {
        String novel_top_ad = AppPrefs.getSharedString(activity, "novel_top_ad", "");
        String novel_bottom_ad = AppPrefs.getSharedString(activity, "novel_bottom_ad", "");
        return ReaderConfig.TOP_READ_AD != null && !TextUtils.equals(novel_top_ad, ReaderConfig.TOP_READ_AD.ad_image) || ReaderConfig.BOTTOM_READ_AD != null && !TextUtils.equals(novel_bottom_ad, ReaderConfig.BOTTOM_READ_AD.ad_image);
    }

    private void flushPage() {
        bookpage.initPage();
        initAd(activity);
        initData();
    }

    private void JumpBookAd(Activity activity, BaseAd baseAd) {
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
        if (readSpeakDialogFragment != null) {
            dismissAllDialog(mBrightDialog, mSettingDialog, mAutoSettingDialog, readSpeakDialogFragment.getDialog());
        } else {
            dismissAllDialog(mBrightDialog, mSettingDialog, mAutoSettingDialog);
        }
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

    private void updaeListenRecord() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("book_id", baseBook.getBook_id());
        String json = params.generateParamsJson();
        OkHttpEngine.getInstance(getApplication()).postAsyncHttp(ReaderConfig.getBaseUrl() + ReaderConfig.LISTENBOOKRECODE, json, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {
            }

            @Override
            public void onResponse(String response) {
            }
        });
    }

    private void startReadNovelService() {
        if (!Utils.isLogin(activity)) {
            MainHttpTask.getInstance().Gotologin(activity);
        } else {
            if (App.isVip(activity)) {
                initReadSpeakDialogFragment();
                ChapterItem currentChapter = ChapterManager.getInstance(this).getCurrentChapter();
                currentChapter.setBegin(pageFactory.getCurrentPage().getBegin());
                //启动服务
                if (!ReadNovelService.SERVICE_IS_LIVE) {
                    bookpage.setmIsOpenService(true);
                    pageFactory.close_AD = true;
                    // Android 8.0使用startForegroundService在前台启动新服务
                    Intent intent = new Intent(this, ReadNovelService.class);
                    intent.putExtra(EXTRA_CHAPTER, currentChapter);
                    intent.putExtra(EXTRA_PAGE, pageFactory.getPageForBegin(chapter.getBegin()).getLineToString());
                    intent.putExtra(EXTRA_BOOK, baseBook);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        startForegroundService(intent);
                    } else {
                        startService(intent);
                    }
                } else {
                    EventBus.getDefault().post(new NovelOpenOtherEvent(currentChapter, baseBook));
                }

                // 上报下载次数i
                postDownloadVerificationCount();
            } else {
                new DialogVip().getDialogVipPop(activity, getResources().getString(R.string.dialog_tittle_vip), false);
            }
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startOtherNovelRead(StartOtherNovel otherNovelRead) {
        if (otherNovelRead.isStartNovel()) {
            if (ReadNovelService.SERVICE_IS_LIVE) {
                bookpage.setmIsOpenService(false);
                pageFactory.close_AD = false;
                Intent intentService = new Intent(getApplicationContext(), ReadNovelService.class);
                stopService(intentService);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(1000);
                            if (!ReadNovelService.SERVICE_IS_LIVE) {
                                startReadNovelService();
                            }
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }
        }
    }

    /**
     * 获取tts 不兼容的 格式字符
     * {"list":["0xC2 0xA0"]}
     */
    private void getTTSFilter() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.TTS_FILTER, json, false, new HttpUtils.ResponseListener() {
            @Override
            public void onResponse(String result) {
                try {
                    TTSFilterType ttsFilterType = new Gson().fromJson(result, TTSFilterType.class);
                    readSpeakManager.setTTSFilterList(ttsFilterType.getList());
                } catch (Exception e) {
                }
            }

            @Override
            public void onErrorResponse(String ex) {
            }
        });

    }

    /**
     * 上报 tts 下载次数
     */
    private void postDownloadVerificationCount() {
        String key = DateUtils.getTodayTime();
        boolean downloaded = ShareUitls.getBoolean(activity, key, false);
        if (!downloaded) {
            ReaderParams params = new ReaderParams(activity);
            String json = params.generateParamsJson();
            HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.TTS_DOWNLOAD_COUNT, json, false, new HttpUtils.ResponseListener() {
                @Override
                public void onResponse(String result) {
                    ShareUitls.putBoolean(activity, key, true);
                }

                @Override
                public void onErrorResponse(String ex) {
                }
            });
        }
    }

}