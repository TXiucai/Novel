package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.book.ReadHistory;
import com.heiheilianzai.app.model.event.RefreshTopbook;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.view.MScrollView;
import com.heiheilianzai.app.view.read.PageWidget;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.heiheilianzai.app.constant.ReaderConfig.READBUTTOM_HEIGHT;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;

/**
 * ???????????????????????? ?????????????????????????????????
 * Created by Administrator on 2016/7/20 0020.
 */
public class PageFactory {

    public static boolean close_AD;//????????????
    int AD_PAGE;//
    private ReadingConfig config;
    //?????????????????????????????????????????????????????????
    private static final float RATIO = 1.02f;
    private static final int OFFSET = 2;
    //?????????
    private int mWidth;
    private int PercentWidth;
    //?????????
    private int mHeight, MHeight;
    //??????????????????
    private float m_fontSize;
    //????????????
    private SimpleDateFormat sdf;
    //??????
    private String date;
    //????????????
    private DecimalFormat df;
    //??????????????????
    private float mBorderWidth;
    // ????????????????????????
    private float marginHeight;
    //???????????????????????????
    private float BookNameTop;
    // ????????????????????????
    private float measureMarginWidth;
    // ????????????????????????
    private float marginWidth;
    //???????????????????????????
    private float statusMarginBottom;
    //?????????
    private float lineSpace;
    //??????
    private Typeface typeface;
    //????????????
    private Paint mPaint;
    //??????????????????
    private Paint mPaintLine;
    //????????????
    private Paint waitPaint;
    //????????????
    private int m_textColor = Color.rgb(50, 65, 78);
    private int m_lineBgColor = Color.rgb(248, 248, 255);// Color.rgb(142, 137, 136);
    // ??????????????????
    private float mVisibleHeight;
    // ??????????????????
    private float mVisibleWidth;
    // ???????????????????????????
    private int mLineCount;
    //????????????
    private Paint mBatteryPaint;
    //?????????????????? ????????? ?????????????????????
    private Paint mChapterPaint;
    //?????????
    // private Paint mBookNamePaint;
    //????????????
    private Bitmap m_book_bg = null;
    private Intent batteryInfoIntent;
    //?????????????????????
    private float mBatteryPercentage;
    //???????????????
    private RectF rect1 = new RectF();
    //???????????????
    private RectF rect2 = new RectF();
    //????????????????????????
    private boolean m_isfirstPage;
    //???????????????????????????
    private boolean m_islastPage;
    //????????????
    private int page = 0;
    //??????widget
    private PageWidget mBookPageWidget;
    //????????????
    private String bookName = "";
    //????????????
    private String chapterTitle;
    public ChapterItem chapterItem;
    //????????????
    private int level = 0;
    private BookUtil mBookUtil;
    private PageEvent mPageEvent;
    private TRPage currentPage;
    private TRPage prePage;
    private TRPage cancelPage;
    ContentValues values = new ContentValues();
    private String book_id;
    private String chapter_id;
    //????????????????????????1 ????????????  2 ???????????????
    private String mIsPreview;
    private static Status mStatus = Status.OPENING;
    private boolean hasNotchScreen;
    private float tendp, chapterRight;
    int Chapter_height;
    float reading_shangxia_textsize;
    Resources resources;
    long aapo = 0;
    BaseAd baseAd;
    ImageView list_ad_view_img;
    private Activity mActivity;
    TextView bookpage_scroll_text;
    FrameLayout insert_todayone2;
    MScrollView bookpage_scroll;
    BaseBook baseBook;
    FrameLayout insert_todayone_button;
    int button_ad_heigth, bg_color, color;
    public boolean IS_CHAPTERLast = true;
    public boolean IS_CHAPTERFirst = true;
    public boolean mNextPage = true;//????????????????????????
    private String is_new_content;
    private Dialog mDialogVip;
    private boolean mIsSdkAd = false;
    private List<String> mTipStrings;

    public enum Status {
        OPENING,
        FINISH,
        FAIL,
    }

    public PageFactory(BaseBook baseBook, MScrollView bookpage_scroll, TextView bookpage_scroll_text, FrameLayout insert_todayone2, Context context) {
        mActivity = (Activity) context;
        this.bookpage_scroll_text = bookpage_scroll_text;
        this.baseBook = baseBook;
        this.bookpage_scroll = bookpage_scroll;
        this.insert_todayone2 = insert_todayone2;
        mBookUtil = new BookUtil();
        config = ReadingConfig.getInstance();
        resources = context.getResources();
        //??????????????????
        MHeight = ScreenSizeUtils.getInstance(mActivity).getScreenHeight();
        mHeight = MHeight;
        button_ad_heigth = ImageUtil.dp2px(mActivity, READBUTTOM_HEIGHT);
        long display_ad_days_novel = AppPrefs.getSharedLong(mActivity, "display_ad_days_novel", 0);
        if (ReaderConfig.TOP_READ_AD != null && System.currentTimeMillis() > display_ad_days_novel) {
            mHeight -= button_ad_heigth;
        }
        if (ReaderConfig.BOTTOM_READ_AD != null && System.currentTimeMillis() > display_ad_days_novel) {
            mHeight -= button_ad_heigth;
        }
        mWidth = ScreenSizeUtils.getInstance(mActivity).getScreenWidth();
        sdf = new SimpleDateFormat("HH:mm");//HH:mm???24?????????,hh:mm???12?????????
        date = sdf.format(new java.util.Date());
        df = new DecimalFormat("#0.0");
        marginWidth = resources.getDimension(R.dimen.readingMarginWidth);
        lineSpace = resources.getDimension(R.dimen.reading_line_spacing_medium);
        if (hasNotchScreen = NotchScreen.hasNotchScreen(mActivity)) {
            tendp = ImageUtil.dp2px(mActivity, 15);
            statusMarginBottom = resources.getDimension(R.dimen.reading_status_margin_bottom_hasNotchScreen);
            marginHeight = resources.getDimension(R.dimen.readingMarginHeight);
            BookNameTop = ImageUtil.dp2px(mActivity, 12);
        } else {
            statusMarginBottom = resources.getDimension(R.dimen.reading_status_margin_bottom);
            marginHeight = resources.getDimension(R.dimen.readingMarginHeightNotchScreen);
            BookNameTop = ImageUtil.dp2px(mActivity, 15);
        }
        reading_shangxia_textsize = 0;
        chapterRight = ImageUtil.dp2px(mActivity, 15);
        mVisibleWidth = mWidth - marginWidth * 2;
        mVisibleHeight = mHeight - marginHeight * 2;
        typeface = config.getTypeface();
        m_fontSize = config.getFontSize();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// ??????
        mPaint.setTextAlign(Paint.Align.LEFT);// ?????????
        mPaint.setTextSize(m_fontSize);// ????????????
        mPaint.setColor(m_textColor);// ????????????
        mPaint.setTypeface(typeface);
        mPaint.setSubpixelText(true);// ???????????????true????????????????????????LCD????????????????????????
        mPaintLine = new Paint(Paint.ANTI_ALIAS_FLAG);// ??????
        mPaintLine.setColor(m_lineBgColor);// ????????????
        waitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// ??????
        waitPaint.setTextAlign(Paint.Align.LEFT);// ?????????
        waitPaint.setTextSize(resources.getDimension(R.dimen.reading_max_text_size));// ????????????
        waitPaint.setColor(m_textColor);// ????????????
        waitPaint.setTypeface(typeface);
        waitPaint.setSubpixelText(true);// ???????????????true????????????????????????LCD????????????????????????
        mBorderWidth = resources.getDimension(R.dimen.reading_board_battery_border_width);
        mBatteryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBatteryPaint.setTextSize(CommonUtil.sp2px(context, 12));
        mBatteryPaint.setTypeface(typeface);
        mBatteryPaint.setTextAlign(Paint.Align.LEFT);
        mBatteryPaint.setColor(m_textColor);
        PercentWidth = (int) mBatteryPaint.measureText("999.9%") + 1;
        mChapterPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mChapterPaint.setTextSize(RATIO * m_fontSize);
        mChapterPaint.setFakeBoldText(true);
        mChapterPaint.setTextAlign(Paint.Align.LEFT);
        mChapterPaint.setColor(m_textColor);
        batteryInfoIntent = context.getApplicationContext().registerReceiver(null,
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//????????????,?????????????????????????????????
        initBg(config.getDayOrNight());
        measureMarginWidth();
    }

    private void measureMarginWidth() {
        float wordWidth = mPaint.measureText("\u3000");
        float width = mVisibleWidth % wordWidth;
        measureMarginWidth = marginWidth + width / 2;
    }

    private void calculateLineCount() {
        try {
            if (currentPage == null || currentPage.getBegin() == 0) {
                calculateLineCount1();
            } else {
                calculateLineCount2();
            }
        } catch (Exception e) {
            calculateLineCount1();
        }
    }

    private void calculateLineCount1() {
        mLineCount = (int) ((mVisibleHeight - OFFSET * marginHeight) / (m_fontSize + lineSpace));// ??????????????????
    }

    private void calculateLineCount2() {
        mLineCount = (int) (mVisibleHeight / (m_fontSize + lineSpace));// ??????????????????
    }

    public void onDraw(Bitmap bitmap, List<String> m_lines, Boolean updateChapter) {
        try {
            Canvas c = new Canvas(bitmap);
            try {
                c.drawBitmap(getBgBitmap(), 0, 0, null);
            } catch (Exception e) {
                c.drawBitmap(getBgBitmap2(), 0, 0, null);
            }
            mPaint.setTextSize(getFontSize());
            mPaint.setColor(getTextColor());
            mBatteryPaint.setColor(getTextColor());
            mChapterPaint.setColor(getTextColor());
            mChapterPaint.setTextSize(RATIO * getFontSize());
            mPaint.setColor(getTextColor());
            float y = 0;
            if (!m_lines.isEmpty()) {
                if (currentPage.getBegin() == 0) {
                    y = (OFFSET + 1) * marginHeight - Chapter_height;
                } else {
                    y = marginHeight;
                }
                DrawText:
                for (String strLine : m_lines) {
                    y += m_fontSize + lineSpace;
                    c.drawText(changeJIanfan(strLine), measureMarginWidth, y, mPaint);
                }
            }
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//??????
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getEnd()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//??????
            // ????????????
            String strPercent = df.format(myfPercent * 100);//????????????
            c.drawText(strPercent + "%", mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y????????????
            baseBook.setAllPercent(strPercent);
            if (baseBook.isAddBookSelf() == 1) {
                ContentValues values = new ContentValues();
                values.put("allPercent", strPercent);
                LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
                EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent));
            }
            if (currentPage != null && chapterItem != null) {
                ChapterManager.getInstance(mActivity).getCurrentChapter().setChapteritem_begin(currentPage.getBegin());
                ContentValues values1 = new ContentValues();
                values1.put("chapteritem_begin", currentPage.getBegin());
                LitePal.updateAll(ChapterItem.class, values1, "book_id = ? and chapter_id = ?", book_id, chapter_id);
            }
            // ?????????
            drawBatteryAndDate(c);
            //?????????
            c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
            if (currentPage.getBegin() == 0) {
                mChapterPaint.setTextSize(RATIO * getFontSize());
                for (int i = 100; i > 0; i--) {
                    int With = (int) (mChapterPaint.measureText(chapterTitle));
                    if (With < mWidth - chapterRight) {
                        break;
                    }
                    float s = (float) i / 100;
                    mChapterPaint.setTextSize((s * getFontSize()));
                }
                c.drawText(chapterTitle, marginWidth, 2 * marginHeight + Chapter_height, mChapterPaint);
            }
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    /**
     * @param bitmap
     * @param m_lines
     * @param updateChapter
     */
    public void onDraw1(String mIsPreview, Bitmap bitmap, String chapterTitle, List<String> m_lines, Boolean updateChapter) {
        try {
            //?????????????????????
            if (currentPage != null && chapterItem != null) {
                ContentValues values = new ContentValues();
                values.put("chapteritem_begin", currentPage.getBegin());
                ChapterManager.getInstance(mActivity).getCurrentChapter().setChapteritem_begin(currentPage.getBegin());
                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, chapter_id);
            }
            Canvas c = new Canvas(bitmap);
            try {
                c.drawBitmap(getBgBitmap(), 0, 0, null);
            } catch (Exception e) {
                c.drawBitmap(getBgBitmap2(), 0, 0, null);
            }
            mPaint.setTextSize(getFontSize());
            mPaint.setColor(getTextColor());
            mBatteryPaint.setColor(getTextColor());
            mChapterPaint.setColor(getTextColor());
            mChapterPaint.setTextSize(RATIO * getFontSize());
            mPaint.setColor(getTextColor());
            if (m_lines.size() == 0) {
                return;
            }
            if (m_lines.size() > 0) {
                float y = (OFFSET + 1) * marginHeight - Chapter_height;
                DrawText:
                for (String strLine : m_lines) {
                    y += m_fontSize + lineSpace;
                    c.drawText(strLine, measureMarginWidth, y, mPaint);
                }
            }
            //??????????????????
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//??????
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//??????
            // ????????????
            String strPercent = df.format(myfPercent * 100) + "%";//????????????
            c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y????????????
            baseBook.setAllPercent(strPercent);
            if (baseBook.isAddBookSelf() == 1) {
                ContentValues values = new ContentValues();
                values.put("allPercent", strPercent);
                LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
                EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent));
            }
            drawBatteryAndDate(c);
            //?????????
            c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
            //??????
            mChapterPaint.setTextSize(RATIO * getFontSize());
            for (int i = 100; i > 0; i--) {
                int With = (int) (mChapterPaint.measureText(chapterTitle));
                if (With < mWidth - chapterRight) {
                    break;
                }
                float s = (float) i / 100;
                mChapterPaint.setTextSize((s * getFontSize()));
            }
            c.drawText(chapterTitle, marginWidth, 2 * marginHeight + Chapter_height, mChapterPaint);
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    int AD_text_WIDTH;

    public void setTipStrings(List<String> mTipStrings) {
        this.mTipStrings = mTipStrings;
    }

    private void drawBatteryAndDate(Canvas c) {
        if (c == null) {
            return;
        }
        if (mTipStrings != null && mTipStrings.size() > 0) {
            int i = new Random().nextInt(mTipStrings.size());
            String tip = mTipStrings.get(i);
            if (tip.length() > 20) {
                tip = tip.substring(0, 17) + "...";
            }
            AD_text_WIDTH = (int) (mBatteryPaint.measureText(tip));
            c.drawText(tip, (mWidth - AD_text_WIDTH) / 2, mHeight - statusMarginBottom, mBatteryPaint);
        } else {
            String AD_text = resources.getString(R.string.app_name);
            if (AD_text_WIDTH == 0) {
                AD_text_WIDTH = (int) (mBatteryPaint.measureText(AD_text));//;
            }

            c.drawText(AD_text, (mWidth - AD_text_WIDTH) / 2, mHeight - statusMarginBottom, mBatteryPaint);
        }
        float width = CommonUtil.convertDpToPixel(mActivity, 18) - mBorderWidth;
        float height = CommonUtil.convertDpToPixel(mActivity, 9);
        int dateWith = (int) (mBatteryPaint.measureText(date) + mBorderWidth);//????????????
        c.drawText(date, marginWidth, mHeight - statusMarginBottom, mBatteryPaint);
        // ?????????
        level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 100);
        mBatteryPercentage = (float) level / scale;
        float rect1Left = marginWidth + dateWith + statusMarginBottom;//????????????left??????
        //???????????????
        rect1.set(rect1Left, mHeight - height - statusMarginBottom, rect1Left + width, mHeight - statusMarginBottom);
        rect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - statusMarginBottom, rect1Left + width - mBorderWidth, mHeight - mBorderWidth - statusMarginBottom);
        c.save();
        c.clipRect(rect2, Region.Op.DIFFERENCE);
        c.drawRect(rect1, mBatteryPaint);
        c.restore();
        //???????????????
        rect2.left += mBorderWidth;
        rect2.right -= mBorderWidth;
        rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
        rect2.top += mBorderWidth;
        rect2.bottom -= mBorderWidth;
        c.drawRect(rect2, mBatteryPaint);
        //????????????
        int poleHeight = (int) CommonUtil.convertDpToPixel(mActivity, 10) / 2;
        rect2.left = rect1.right;
        rect2.top = rect2.top + poleHeight / 4;
        rect2.right = rect1.right + mBorderWidth;
        rect2.bottom = rect2.bottom - poleHeight / 4;
        c.drawRect(rect2, mBatteryPaint);
    }

    /**
     * @param bitmap
     * @param m_lines
     * @param updateChapter
     */
    public void onDraw2(String mIsPreview, Bitmap bitmap, String chapterTitle, List<String> m_lines, String process, Boolean updateChapter) {
        try {
            if (updateChapter) {
                String str = m_lines.get(0);
                str = " " + str;
                m_lines.set(0, str);
                onDraw1(mIsPreview, bitmap, chapterTitle, m_lines, true);
                return;
            }
            if (currentPage != null && chapterItem != null) {
                ContentValues values = new ContentValues();
                values.put("chapteritem_begin", currentPage.getBegin());
                ChapterManager.getInstance(mActivity).getCurrentChapter().setChapteritem_begin(currentPage.getBegin());
                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, chapter_id);
            }
            Canvas c = new Canvas(bitmap);
            try {
                c.drawBitmap(getBgBitmap(), 0, 0, null);
            } catch (Exception e) {
                c.drawBitmap(getBgBitmap2(), 0, 0, null);
            }
            mPaint.setTextSize(getFontSize());
            mPaint.setColor(getTextColor());
            mBatteryPaint.setColor(getTextColor());
            mChapterPaint.setColor(getTextColor());
            mChapterPaint.setTextSize(RATIO * getFontSize());
            mPaint.setColor(getTextColor());
            if (m_lines.size() == 0) {
                return;
            }
            float y = 0;
            if (m_lines.size() > 0) {
                y = marginHeight;
                DrawText:
                for (String strLine : m_lines) {
                    y += m_fontSize + lineSpace;
                    c.drawText(strLine, measureMarginWidth, y, mPaint);
                }
            }
            //??????????????????
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//??????
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//??????
            // ????????????
            String strPercent = df.format(myfPercent * 100) + "%";//????????????
            c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y????????????
            baseBook.setAllPercent(strPercent);
            if (baseBook.isAddBookSelf() == 1) {
                ContentValues values = new ContentValues();
                values.put("allPercent", strPercent);
                LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
                EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent));
            }
            drawBatteryAndDate(c);
            //?????????
            c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    private View ADview2;
    public int Y;
    boolean AD_SHOW, CANCLE_AD_SHOW;
    Map<String, String> bookpage_scroll_text_Map = new HashMap<>();

    public void drawScroll() {
        float strPercent = (float) (chapterItem.getDisplay_order() + 1) / (float) ChapterManager.getInstance(mActivity).mChapterList.size();
        MyToash.Log("strPercent", strPercent + "  " + chapterItem.getDisplay_order() + "  " + ChapterManager.getInstance(mActivity).mChapterList.size());
        strPercent = (float) ((int) (strPercent * 10000)) / 100;
        baseBook.setAllPercent(strPercent + "");
        if (baseBook.isAddBookSelf() == 1) {
            ContentValues values = new ContentValues();
            values.put("allPercent", strPercent);
            LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
            EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent + ""));
        }
        bookpage_scroll.scrollTo(0, 0);
        bookpage_scroll_text.setText("");
        mBookPageWidget.setVisibility(View.GONE);
        bookpage_scroll.setVisibility(View.VISIBLE);
        if (ADview2 != null) {
            if (!close_AD) {
                ADview2.setVisibility(View.VISIBLE);
            } else {
                ADview2.setVisibility(View.GONE);
            }
        }
        bookpage_scroll_text.setLineSpacing(lineSpace, 1);
        changeBookBg(config.getBookBgType());
        bookpage_scroll_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, m_fontSize - reading_shangxia_textsize);
        String str = "";
        if (bookpage_scroll_text_Map.containsKey(chapter_id + "" + mIsPreview)) {
            str = bookpage_scroll_text_Map.get(chapter_id + "" + mIsPreview);
        } else {
            String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(chapter_id + "/").concat(mIsPreview).concat(is_new_content).concat(".txt");
            str = chapterTitle + "\n\n" + FileManager.txt2String(new File(path));
            bookpage_scroll_text_Map.put(chapter_id + "" + mIsPreview, str);
        }
        if (hasNotchScreen) {
            str = "\n\n" + str;
        }
        bookpage_scroll_text.setText(str);
        ChapterManager.getInstance(mActivity).setCurrentChapter(chapterItem);
    }

    //????????????
    public void prePage() {
        boolean last_chapter = currentPage.getBegin() <= 0;
        mNextPage = false;

        if (last_chapter) {
            if (!ChapterManager.getInstance(mActivity).hasPreChapter()) {
                if (!m_isfirstPage) {
                    MyToash.ToashError(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_startpage));
                }
                m_isfirstPage = true;
                return;
            }

            if (!close_AD && IS_CHAPTERFirst) {
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                drawAD(mBookPageWidget.getNextPage());
                mBookPageWidget.setOnSwitchPreListener(new PageWidget.OnSwitchPreListener() {
                    @Override
                    public void switchPreChapter() {
                        insert_todayone2.setVisibility(View.VISIBLE);
                        IS_CHAPTERFirst = false;
                    }
                });
                return;
            }
            try {
                ChapterItem chapterItem = ChapterManager.getInstance(mActivity).mCurrentChapter;
                final String preChapterId = chapterItem.getPre_chapter_id();
                ChapterManager.getInstance(mActivity).getChapter(chapterItem.getDisplay_order() - 1, preChapterId, new ChapterManager.QuerychapterItemInterface() {
                    @Override
                    public void success(final ChapterItem querychapterItem) {
                        if (querychapterItem.getChapter_path() == null) {
                            String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(preChapterId + "/").concat(querychapterItem.getIs_preview() + "/").concat(querychapterItem.getIs_new_content() + "/").concat(querychapterItem.getUpdate_time()).concat(".txt");
                            if (FileManager.isExist(path)) {
                                ContentValues values = new ContentValues();
                                values.put("chapter_path", path);
                                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, preChapterId);
                                querychapterItem.setChapter_path(path);
                            } else {
                                ChapterManager.notfindChapter(ShareUitls.getString(mActivity, PrefConst.NOVEL_API, "") + ReaderConfig.chapter_text, querychapterItem, book_id, preChapterId, new ChapterManager.ChapterDownload() {
                                    @Override
                                    public void finish() {
                                        drawLastChapter(querychapterItem, preChapterId);
                                    }
                                });
                                return;
                            }
                        }
                        drawLastChapter(querychapterItem, preChapterId);
                    }

                    @Override
                    public void fail() {
                        MyToash.ToashError(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_chapterfail));
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                m_isfirstPage = true;
            }
        } else {
            MyToash.Log("IS_CHAPTERLastTT", IS_CHAPTERLast + "");
            if (IS_CHAPTERLast) {
                m_isfirstPage = false;
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                currentPage = getPrePage();
                onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
            } else {
                IS_CHAPTERLast = true;
                drawAD(mBookPageWidget.getCurPage());
                onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
            }
        }
    }

    //????????????
    public void nextPage() {
        boolean nextChapter = currentPage.getEnd() >= mBookUtil.getBookLen();
        mNextPage = true;
        if (currentPage == null) {
            return;
        }
        if (nextChapter) {//???????????????
            if (!m_islastPage) {
            }
            if (!ChapterManager.getInstance(mActivity).hasNextChapter()) {
                if (!m_islastPage) {
                    MyToash.ToashError(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_endpage));
                }
                m_islastPage = true;
                return;
            }

            if (!close_AD && IS_CHAPTERLast && mBookPageWidget.Current_Page > 5) {
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                prePage = currentPage;
                drawAD(mBookPageWidget.getNextPage());
                mBookPageWidget.setOnSwitchNextListener(new PageWidget.OnSwitchNextListener() {
                    @Override
                    public void switchNextChapter() {
                        insert_todayone2.setVisibility(View.VISIBLE);
                        IS_CHAPTERLast = false;
                    }
                });
            } else {
                try {
                    final String nextChapterId = mBookUtil.getCurrentChapter().getNext_chapter_id();
                    ChapterManager.getInstance(mActivity).getChapter(mBookUtil.getCurrentChapter().getDisplay_order() + 1, nextChapterId, new ChapterManager.QuerychapterItemInterface() {
                        @Override
                        public void success(final ChapterItem querychapterItem) {
                            if (querychapterItem.getChapter_path() == null) {
                                String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(nextChapterId + "/").concat(querychapterItem.getIs_preview() + "/").concat(querychapterItem.getIs_new_content() + "/").concat(querychapterItem.getUpdate_time()).concat(".txt");
                                if (FileManager.isExist(path)) {
                                    ContentValues values = new ContentValues();
                                    values.put("chapter_path", path);
                                    LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, nextChapterId);
                                    querychapterItem.setChapter_path(path);
                                } else {
                                    ChapterManager.notfindChapter(ShareUitls.getString(mActivity, PrefConst.NOVEL_API, "") + ReaderConfig.chapter_text, querychapterItem, book_id, nextChapterId, new ChapterManager.ChapterDownload() {
                                        @Override
                                        public void finish() {
                                            drawNextChapter(querychapterItem, nextChapterId);
                                        }
                                    });
                                    return;
                                }
                            }
                            drawNextChapter(querychapterItem, nextChapterId);
                        }

                        @Override
                        public void fail() {
                            MyToash.ToashError(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_chapterfail));
                        }
                    });
                } catch (Exception e) {
                    ChapterManager.getInstance(mActivity).addDownloadTaskWithoutAutoBuy(mBookUtil.getCurrentChapter(), new ChapterManager.ChapterDownload() {
                        @Override
                        public void finish() {
                            ChapterManager.getInstance(mActivity).mCurrentChapter = mBookUtil.getCurrentChapter();
                            ReadActivity.openBook(baseBook, ChapterManager.getInstance(mActivity).mCurrentChapter, mActivity);
                        }
                    });
                }
            }
        } else {
            if (IS_CHAPTERFirst) {
                m_islastPage = false;
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                prePage = currentPage;
                mLineCount = (int) (mVisibleHeight / (m_fontSize + lineSpace));
                currentPage = getNextPage();
                onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
            } else {
                IS_CHAPTERFirst = true;
                drawAD(mBookPageWidget.getCurPage());
                onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
            }
        }
    }

    public TRPage getCurrentPage() {
        return currentPage;
    }

    public void cancelPage() {
        currentPage = cancelPage;
        if (!close_AD) {
            if (IS_CHAPTERLast && IS_CHAPTERFirst) {
                insert_todayone2.setVisibility(View.INVISIBLE);
            } else {
                insert_todayone2.setVisibility(View.VISIBLE);
            }
        }
    }

    public void openBook(int isfirst, ChapterItem chapterItem, BookUtil bookUtil) {
        if (isfirst == 0 || isfirst == 3) {
            initBg(config.getDayOrNight());
        }
        this.chapterItem = chapterItem;
        bookName = chapterItem.getBook_name();
        chapterTitle = chapterItem.getChapter_title();
        book_id = chapterItem.getBook_id();
        chapter_id = chapterItem.getChapter_id();
        mIsPreview = chapterItem.getIs_preview();
        is_new_content = chapterItem.getIs_new_content();
        baseBook.setCurrent_chapter_displayOrder(chapterItem.getDisplay_order());
        baseBook.setCurrent_chapter_id(chapter_id);
        ContentValues values = new ContentValues();
        values.put("current_chapter_id", chapter_id);
        values.put("current_chapter_displayOrder", chapterItem.getDisplay_order());
        LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
        if (baseBook.isAddBookSelf() == 1) {
            EventBus.getDefault().post(new RefreshTopbook(book_id, chapter_id, true));
        }
        if (mPageEvent != null && mBookUtil != null) {
            mPageEvent.onSwitchChapter(mBookUtil);
        }
        if (isfirst != 4) {
            ViewUtils.setVisibility(mBookPageWidget, View.VISIBLE);
            ViewUtils.setVisibility(bookpage_scroll, View.GONE);
            mStatus = Status.OPENING;
            MyToash.Log("getPageForBegin0", "");
            if (bookUtil == null) {
                try {
                    mBookUtil.openBook(mActivity, chapterItem, book_id, chapter_id);
                    PageFactory.mStatus = PageFactory.Status.FINISH;
                    if (chapterItem.getBegin() == 0) {
                        calculateLineCount1();
                    } else {
                        calculateLineCount2();
                    }
                    currentPage = getPageForBegin(chapterItem.getBegin());
                    onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                mBookUtil = bookUtil;
                PageFactory.mStatus = PageFactory.Status.FINISH;
                currentPage = getPageForBegin(chapterItem.getBegin());
            }
        } else {
            drawScroll();
        }
        checkIsCoupon(chapterItem);
        ReadHistory.addReadHistory(true, mActivity, book_id, chapter_id);//?????????????????? ????????????????????????????????????
    }

    private void updateRecord() {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("book_id", book_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mReadRecord, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public TRPage getNextPage() {
        mBookUtil.setPostition(currentPage.getEnd());
        long position = currentPage.getEnd() + 1;
        TRPage trPage = new TRPage();
        trPage.setBegin(currentPage.getEnd() + 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPrePage() {
        mBookUtil.setPostition(currentPage.getBegin());
        TRPage trPage = new TRPage();
        trPage.setEnd(mBookUtil.getPosition() - 1);
        trPage.setLines(getPreLines());
        trPage.setBegin(mBookUtil.getPosition());
        return trPage;
    }

    public TRPage getPageForBegin(long begin) {
        TRPage trPage = new TRPage();
        trPage.setBegin(begin);
        mBookUtil.setPostition(begin - 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        return trPage;
    }

    public List<String> getNextLines() {
        List<String> lines = new ArrayList<>();
        float width = 0;
        float height = 0;
        String line = "";
        while (mBookUtil.next(true) != -1) {
            char word = (char) mBookUtil.next(false);
            //??????????????????
            if ((word + "").equals("\r") && (((char) mBookUtil.next(true)) + "").equals("\n")) {
                mBookUtil.next(false);
                if (!line.isEmpty()) {
                    lines.add(line);
                    line = "";
                    width = 0;
                    if (lines.size() == mLineCount) {
                        break;
                    }
                }
            } else {
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    lines.add(line);
                    line = word + "";
                } else {
                    line += word;
                }
            }
            if (lines.size() == mLineCount) {
                if (!line.isEmpty()) {
                    mBookUtil.setPostition(mBookUtil.getPosition() - 1);
                }
                break;
            }
        }
        if (!line.isEmpty() && lines.size() < mLineCount) {
            lines.add(line);
        }
        return lines;
    }

    public List<String> getPreLines() {
        List<String> lines = new ArrayList<>();
        float width = 0;
        String line = "";
        char[] par = mBookUtil.preLine();
        while (par != null) {
            List<String> preLines = new ArrayList<>();
            for (int i = 0; i < par.length; i++) {
                char word = par[i];
                float widthChar = mPaint.measureText(word + "");
                width += widthChar;
                if (width > mVisibleWidth) {
                    width = widthChar;
                    preLines.add(line);
                    line = word + "";
                } else {
                    line += word;
                }
            }
            if (!line.isEmpty()) {
                preLines.add(line);
            }
            lines.addAll(0, preLines);
            if (lines.size() >= mLineCount) {
                break;
            }
            width = 0;
            line = "";
            par = mBookUtil.preLine();
        }
        List<String> reLines = new ArrayList<>();
        int num = 0;
        for (int i = lines.size() - 1; i >= 0; i--) {
            if (reLines.size() < mLineCount) {
                reLines.add(0, lines.get(i));
            } else {
                num = num + lines.get(i).length();
            }
        }
        if (num > 0) {
            if (mBookUtil.getPosition() > 0) {
                mBookUtil.setPostition(mBookUtil.getPosition() + num + 2);
            } else {
                mBookUtil.setPostition(mBookUtil.getPosition() + num);
            }
        }
        return reLines;
    }

    //??????????????????
    public void currentPage(Boolean updateChapter) {
        if (!close_AD && (!IS_CHAPTERFirst || !IS_CHAPTERLast)) {
        } else {
            onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), updateChapter);
        }
    }

    //????????????
    public void updateBattery(int mLevel) {
        if (config.getPageMode() != 4) {
            if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
                if (level != mLevel) {
                    level = mLevel;
                    currentPage(false);
                }
            }
        }
    }

    public void updateTime() {
        if (config.getPageMode() != 4) {
            if (currentPage != null && mBookPageWidget != null && !mBookPageWidget.isRunning()) {
                String mDate = sdf.format(new java.util.Date());
                if (date != mDate) {
                    date = mDate;
                    currentPage(false);
                }
            }
        }
    }

    //??????????????????
    public void changeFontSize(int fontSize) {
        this.m_fontSize = fontSize;
        mPaint.setTextSize(m_fontSize);
        if (config.getPageMode() != 4) {
            calculateLineCount1();
            measureMarginWidth();
            currentPage = getPageForBegin(0);
            currentPage(true);
        } else {
            bookpage_scroll_text.setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize - reading_shangxia_textsize);
        }
    }

    //??????????????????
    public void changeLineSpacing(int mode) {
        if (mode == ReadingConfig.LINE_SPACING_BIG) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_big);
        } else if (mode == ReadingConfig.LINE_SPACING_MEDIUM) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_medium);
        } else if (mode == ReadingConfig.LINE_SPACING_SMALL) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_small);
        }
        if (config.getPageMode() != 4) {
            calculateLineCount1();
            measureMarginWidth();
            currentPage = getPageForBegin(0);
            currentPage(true);
        } else {
            bookpage_scroll_text.setLineSpacing(lineSpace, 1);
        }
    }

    public void setLineSpacingMode(int mode) {
        if (mode == ReadingConfig.LINE_SPACING_BIG) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_big);
        } else if (mode == ReadingConfig.LINE_SPACING_MEDIUM) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_medium);
        } else if (mode == ReadingConfig.LINE_SPACING_SMALL) {
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_small);
        }
    }

    public void setFontSize(int fontSize) {
        this.m_fontSize = fontSize;
    }

    boolean jianfan;

    public String changeJIanfan(String s) {
        return s;
    }

    //????????????
    public void changeTypeface(Typeface typeface) {
        jianfan = !jianfan;
        calculateLineCount1();
        measureMarginWidth();
        currentPage = getPageForBegin(0);
        currentPage(true);
    }

    //????????????
    public void changeBookBg(int type) {
        if (config.getPageMode() != 4) {
            setBookBg(type);
            currentPage(false);
        } else {
            int color = 0;
            int bgcolor = 0;
            switch (type) {
                case ReadingConfig.BOOK_BG_DEFAULT:
                    color = resources.getColor(R.color.read_font_1);
                    bgcolor = resources.getColor(R.color.read_bg_default);
                    break;
                case ReadingConfig.BOOK_BG_1:
                    bgcolor = resources.getColor(R.color.read_bg_1);
                    color = resources.getColor(R.color.read_font_1);
                    break;
                case ReadingConfig.BOOK_BG_2:
                    bgcolor = resources.getColor(R.color.read_bg_2);
                    color = resources.getColor(R.color.read_font_2);
                    break;
                case ReadingConfig.BOOK_BG_3:
                    bgcolor = resources.getColor(R.color.read_bg_3);
                    color = resources.getColor(R.color.read_font_3);
                    break;
                case ReadingConfig.BOOK_BG_4:
                    bgcolor = resources.getColor(R.color.read_bg_4);
                    color = resources.getColor(R.color.read_font_4);
                    break;
                case ReadingConfig.BOOK_BG_7:
                    bgcolor = resources.getColor(R.color.read_bg_7);
                    color = resources.getColor(R.color.read_font_7);
                    setBookPageBg(resources.getColor(R.color.read_bg_7));
                    break;
                case ReadingConfig.BOOK_BG_8:
                    bgcolor = resources.getColor(R.color.read_bg_8);
                    color = resources.getColor(R.color.read_font_8);
                    break;
            }
            bookpage_scroll.setBackgroundColor(bgcolor);
            bookpage_scroll_text.setTextColor(color);
        }
    }

    //???????????????
    private void initBg(Boolean isNight) {
        if (!isNight) {
            setBookBg(config.getBookBgType());
        } else {
            setBookBg(ReadingConfig.BOOK_BG_8);
        }
    }

    //?????????????????????
    public void setBookBg(int type) {
        try {
            Bitmap bitmap = Bitmap.createBitmap(mWidth, mHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            int color = 0, bg_color = 0;
            switch (type) {
                case ReadingConfig.BOOK_BG_DEFAULT:
                    bg_color = resources.getColor(R.color.read_bg_default);
                    color = resources.getColor(R.color.read_font_1);
                    break;
                case ReadingConfig.BOOK_BG_1:
                    bg_color = resources.getColor(R.color.read_bg_1);
                    color = resources.getColor(R.color.read_font_1);
                    break;
                case ReadingConfig.BOOK_BG_2:
                    bg_color = resources.getColor(R.color.read_bg_2);
                    color = resources.getColor(R.color.read_font_2);
                    break;
                case ReadingConfig.BOOK_BG_3:
                    bg_color = resources.getColor(R.color.read_bg_3);
                    color = resources.getColor(R.color.read_font_3);
                    break;
                case ReadingConfig.BOOK_BG_4:
                    bg_color = resources.getColor(R.color.read_bg_4);
                    color = resources.getColor(R.color.read_font_4);
                    break;
                case ReadingConfig.BOOK_BG_7:
                    bg_color = resources.getColor(R.color.read_bg_7);
                    color = resources.getColor(R.color.read_font_7);
                    break;
                case ReadingConfig.BOOK_BG_8:
                    bg_color = resources.getColor(R.color.read_bg_8);
                    color = resources.getColor(R.color.read_font_8);
                    break;
            }
            canvas.drawColor(bg_color);
            setBookPageBg(bg_color);
            this.bg_color = bg_color;
            this.color = color;
            insert_todayone2.setBackgroundColor(bg_color);
            setBgBitmap(bitmap);
            //??????????????????
            setM_textColor(color);
        } catch (Error e) {
        }
    }

    public void setBookPageBg(int color) {
        if (mBookPageWidget != null) {
            mBookPageWidget.setBgColor(color);
        }
    }

    //??????????????????????????????
    public void setDayOrNight(Boolean isNgiht) {
        initBg(isNgiht);
        if (config.getPageMode() != 4) {
            currentPage(false);
        } else {
            changeBookBg(config.getBookBgType());
        }
    }

    public void clear() {
        try {
            if (m_book_bg != null && !m_book_bg.isRecycled()) {
                m_book_bg.recycle();      //scaleBitmap ??????
                m_book_bg = null;
            }
        } catch (Exception e) {
        }
        try {
            if (mBookPageWidget.getCurPage() != null && !mBookPageWidget.getCurPage().isRecycled()) {
                mBookPageWidget.getCurPage().recycle();      //scaleBitmap ??????
            }
        } catch (Exception e) {
        }
        try {
            if (mBookPageWidget.getNextPage() != null && !mBookPageWidget.getNextPage().isRecycled()) {
                mBookPageWidget.getNextPage().recycle();      //scaleBitmap ??????
            }
        } catch (Exception e) {
        }
        bookName = "";
        chapterItem = null;
        mBookPageWidget = null;
        mPageEvent = null;
        cancelPage = null;
        prePage = null;
        currentPage = null;
    }

    public float getmVisibleWidth() {
        return mVisibleWidth;
    }

    public int getmLineCount() {
        return mLineCount;
    }

    public int getmLineCount1() {
        return (int) ((mVisibleHeight - OFFSET * marginHeight) / (m_fontSize + lineSpace));// ??????????????????
    }

    public int getmLineCount2() {
        return (int) ((mVisibleHeight) / (m_fontSize + lineSpace));// ??????????????????
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public static Status getStatus() {
        return mStatus;
    }

    //??????????????????
    public boolean isfirstPage() {
        return m_isfirstPage;
    }

    //?????????????????????
    public boolean islastPage() {
        return m_islastPage;
    }

    //??????????????????
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    //??????????????????
    public Bitmap getBgBitmap() {
        return m_book_bg;
    }

    public Bitmap getBgBitmap2() {
        initBg(config.getDayOrNight());
        return m_book_bg;
    }

    //??????????????????
    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }

    //??????????????????
    public int getTextColor() {
        return this.m_textColor;
    }

    //??????????????????
    public float getFontSize() {
        return this.m_fontSize;
    }

    public float getChapterFontSize() {
        return this.m_fontSize * RATIO;
    }

    public void setPageWidget(PageWidget mBookPageWidget) {
        this.mBookPageWidget = mBookPageWidget;
    }

    public PageWidget getPageWidget() {
        return mBookPageWidget;
    }

    public void setPageEvent(PageEvent pageEvent) {
        this.mPageEvent = pageEvent;
    }

    public interface PageEvent {
        void changeProgress(float progress);

        void onSwitchChapter(BookUtil oldBookUtil);
    }

    public BookUtil getBookUtil() {
        return mBookUtil;
    }

    private void drawLastChapter(ChapterItem querychapterItem, String preChapterId) {
        final ChapterItem preChapter = querychapterItem;
        BookUtil bookUtiltemp = getBookUtil(querychapterItem, preChapterId);
        final BookUtil bookUtil = bookUtiltemp;
        TRPage page = new TRPage();
        for (int i = 1; (bookUtil.getPosition() < bookUtil.getBookLen() - 2); i++) {
            aapo = bookUtil.getPosition();
            bookUtil.getNextLines1(PageFactory.this, i);
        }
        long lastPageStartPosition = aapo + 1;
        MyToash.Log("lastPageStartPosition", lastPageStartPosition + "");
        bookUtil.setPostition(lastPageStartPosition - 1);
        calculateLineCount2();
        page.setLines(bookUtil.getNextLines(PageFactory.this));
        page.setEnd(bookUtil.getPosition());
        preChapter.setBegin(lastPageStartPosition);
        page.setBegin(lastPageStartPosition);
        mBookPageWidget.setOnSwitchPreListener(new PageWidget.OnSwitchPreListener() {
            @Override
            public void switchPreChapter() {
                try {
                    //????????????????????????????????????????????????begin???0
                    ContentValues values = new ContentValues();
                    values.put("chapteritem_begin", 0);
                    LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, chapter_id);
                    chapterItem.setChapteritem_begin(0);
                    openBook(1, preChapter, bookUtil);
                    IS_CHAPTERFirst = true;
                    ChapterManager.getInstance(mActivity).setCurrentChapter(preChapter);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        if (!close_AD) {
            drawAD(mBookPageWidget.getCurPage());
        } else
            onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
        float fPercent = (float) (page.getBegin() * 1.0 / bookUtil.getBookLen());//??????
        String strPercent = df.format(fPercent * 100) + "%";//????????????
        onDraw2("", mBookPageWidget.getNextPage(), preChapter.getChapter_title(), page.getLines(), strPercent, false);
        if (AD_SHOW) {
            CANCLE_AD_SHOW = true;
        } else {
            CANCLE_AD_SHOW = false;
        }
        m_isfirstPage = false;
        ChapterManager.getInstance(mActivity).addDownloadTask(true, preChapter.getPre_chapter_id(), new ChapterManager.ChapterDownload() {
            @Override
            public void finish() {
            }
        });
    }

    private BookUtil getBookUtil(ChapterItem querychapterItem, String preChapterId) {
        BookUtil bookUtiltemp = null;
        bookUtiltemp = new BookUtil();
        try {
            bookUtiltemp.openBook(mActivity, querychapterItem, book_id, preChapterId);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bookUtiltemp;
    }

    private void drawNextChapter(ChapterItem querychapterItem, String nextChapterId) {
        final ChapterItem nextChapter = querychapterItem;
        BookUtil bookUtiltemp = getBookUtil(nextChapter, nextChapterId);
        final BookUtil bookUtil = bookUtiltemp;
        TRPage page = new TRPage();
        bookUtil.setPostition(-1);
        page.setBegin(0);
        calculateLineCount1();
        page.setLines(bookUtil.getNextLines(PageFactory.this));
        page.setEnd(bookUtil.getPosition());
        mBookPageWidget.setOnSwitchNextListener(new PageWidget.OnSwitchNextListener() {
            @Override
            public void switchNextChapter() {
                //????????????????????????????????????????????????begin???0
                ContentValues values = new ContentValues();
                values.put("chapteritem_begin", 0);
                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", nextChapter.getBook_id(), nextChapter.getChapter_id());
                nextChapter.setChapteritem_begin(0);
                if (mActivity != null) {
                    openBook(2, nextChapter, bookUtil);
                    ChapterManager.getInstance(mActivity).setCurrentChapter(nextChapter);
                    IS_CHAPTERLast = true;
                    if (!close_AD) {
                        getWebViewAD(mActivity);//????????????
                    }
                }
            }
        });
        if (!close_AD) {
            drawAD(mBookPageWidget.getCurPage());
        } else
            onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
        onDraw1(nextChapter.getIs_preview(), mBookPageWidget.getNextPage(), nextChapter.getChapter_title(), page.getLines(), true);
        m_islastPage = false;
        ChapterManager.getInstance(mActivity).addDownloadTask(true, nextChapter.getNext_chapter_id(), new ChapterManager.ChapterDownload() {
            @Override
            public void finish() {
            }
        });
    }

    //??????webview ??????
    public void getWebViewAD(Activity activity) {
        if (baseAd == null) {
            for (int i = 0; i < ReaderConfig.NOVEL_SDK_AD.size(); i++) {
                AppUpdate.ListBean listBean = ReaderConfig.NOVEL_SDK_AD.get(i);
                if (TextUtils.equals(listBean.getPosition(), "8") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                    mIsSdkAd = true;
                    sdkAd(activity);
                    return;
                }
            }
            if (!mIsSdkAd) {
                localAd(activity);
            }
        } else {
            clickAd(activity);
        }
    }

    private void sdkAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_NOVEL_END_DEBUG : BuildConfig.XAD_EVN_POS_NOVEL_END, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        baseAd = new BaseAd();
                        baseAd.setAdId(adInfo.getAdId());
                        baseAd.setRequestId(adInfo.getRequestId());
                        baseAd.setAdPosId(adInfo.getAdPosId());
                        baseAd.setAd_skip_url(adInfo.getOperation().getValue());
                        baseAd.setAd_title(adInfo.getMaterial().getTitle());
                        baseAd.setAd_image(adInfo.getMaterial().getImageUrl());
                        baseAd.setUser_parame_need("1");
                        baseAd.setAd_url_type(adInfo.getOperation().getType());
                        baseAd.setAd_type(1);
                        clickAd(activity);
                    } else {
                        close_AD = true;
                    }
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
        params.putExtraParams("position", "8");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            baseAd = new Gson().fromJson(result, BaseAd.class);
                            clickAd(activity);
                        } catch (Exception e) {
                            close_AD = true;
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        close_AD = true;
                    }
                }
        );
    }

    private void clickAd(Activity activity) {
        if (baseAd != null && baseAd.ad_type == 1) {
            if (list_ad_view_img == null) {
                list_ad_view_img = insert_todayone2.findViewById(R.id.list_ad_view_img);
                ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
                layoutParams.width = mWidth;
                layoutParams.height = layoutParams.width;
//                int top = (MHeight - mWidth) / 2;
//                Rect mDestRect = new Rect(0, top, mWidth, top + mWidth);
//                list_ad_view_img.setLayoutParams(layoutParams);
//                insert_todayone2.setLayoutParams();
            }
            close_AD = false;
            insert_todayone2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!TextUtils.isEmpty(baseAd.getAdId())) {
                        AdInfo adInfo = new AdInfo();
                        adInfo.setAdId(baseAd.getAdId());
                        adInfo.setAdPosId(baseAd.getAdPosId());
                        adInfo.setAdPosId(baseAd.getRequestId());
                        XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
                    }
                    BaseAd.jumpADInfo(baseAd, activity);
                }
            });
            MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
        } else {
            close_AD = true;
        }
    }

    private void drawAD(Bitmap bitmap) {
        Canvas c = new Canvas(bitmap);
        try {
            c.drawBitmap(getBgBitmap(), 0, 0, null);
        } catch (Exception e) {
            c.drawBitmap(getBgBitmap2(), 0, 0, null);
        }
        Rect mSrcRect = new Rect(0, 0, mWidth, mWidth);
        Paint mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        int top = (ScreenSizeUtils.getInstance(mActivity).getScreenHeight() - ScreenSizeUtils.getInstance(mActivity).getScreenWidth()) / 2;
        Bitmap bitmapAD = ViewToBitmapUtil.convertViewToBitmap(insert_todayone2, top, mWidth);
        MyToash.Log("ViewToBitmapUtil", (bitmapAD == null) + "  " + top);
        Rect mDestRect = new Rect(0, top, ScreenSizeUtils.getInstance(mActivity).getScreenWidth(), top + ScreenSizeUtils.getInstance(mActivity).getScreenWidth());
        //?????????????????????
        //c.drawBitmap(bitmapAD, mSrcRect, mDestRect, mBitPaint);
        //??????????????????
        float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//??????
        if (mPageEvent != null) {
            mPageEvent.changeProgress(fPercent);
        }
        float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//??????
        // ????????????
        String strPercent = df.format(myfPercent * 100) + "%";//????????????
        c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y????????????
        // ?????????
        drawBatteryAndDate(c);
        //?????????
        c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
        mBookPageWidget.postInvalidate();
    }

    private void checkIsVip(ChapterItem chapterItem) {
        String is_vip = chapterItem.getIs_vip();
        if (is_vip != null && is_vip.equals("1") && !App.isVip(mActivity)) {
            DialogVip dialogVip = new DialogVip();
            dialogVip.getDialogVipPop(mActivity, mActivity.getResources().getString(R.string.dialog_tittle_vip), true);
        } else {
            updateRecord();
        }
    }

    public void checkIsCoupon(ChapterItem chapterItem) {
        if (Utils.isLogin(mActivity)) {
            if (!StringUtils.isEmpty(chapterItem.getIs_limited_free()) && TextUtils.equals(chapterItem.getIs_limited_free(), "1")) {
                updateRecord();
            } else {
                checkIsBuyCoupon(mActivity, chapterItem, ShareUitls.getString(mActivity, PrefConst.NOVEL_API, "") + ReaderConfig.chapter_text);
            }
        } else {
            DialogRegister dialogRegister = new DialogRegister();
            dialogRegister.setFinish(true);
            dialogRegister.getDialogLoginPop(mActivity);
            dialogRegister.setmRegisterBackListener(new DialogRegister.RegisterBackListener() {
                @Override
                public void onRegisterBack(boolean isSuccess) {
                    if (isSuccess) {
                        checkIsCoupon(chapterItem);
                    }
                }
            });
        }
    }

    private void checkIsBuyCoupon(Activity activity, ChapterItem chapterItem, String api) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("book_id", chapterItem.getBook_id());
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String paramString = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(api, paramString, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ChapterContent chapterContent = new Gson().fromJson(result, ChapterContent.class);
                        String is_book_coupon_pay = chapterItem.getIs_book_coupon_pay();
                        if (!chapterContent.isIs_buy_status()) {//?????????
                            if (TextUtils.equals(is_book_coupon_pay, "1")) {//??????????????????
                                if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {
                                    if (App.isVip(activity)) {
                                        updateRecord();
                                    } else {
                                        showGoldDialog(chapterItem);
                                    }
                                } else {
                                    showGoldDialog(chapterItem);
                                }
                            } else {
                                checkIsVip(chapterItem);
                            }
                        } else {
                            updateRecord();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        checkIsBuyCoupon(mActivity, chapterItem, ReaderConfig.getBaseUrl() + ReaderConfig.chapter_text);
                    }
                }
        );
    }

    private void showGoldDialog(ChapterItem chapterItem) {
        DialogNovelCoupon dialogNovelCoupon = new DialogNovelCoupon();
        if (AppPrefs.getSharedBoolean(mActivity, "novelOpen_ToggleButton", false)) {
            int couponNum = AppPrefs.getSharedInt(mActivity, PrefConst.COUPON, 0);
            String couponPrice = AppPrefs.getSharedString(mActivity, PrefConst.COUPON_PRICE);
            if (couponNum >= Integer.valueOf(couponPrice)) {
                dialogNovelCoupon.openCoupon(mActivity, chapterItem, couponPrice, couponNum);
            } else {
                DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                dialogCouponNotMore.getDialogVipPop(mActivity, true);
            }
        } else {
            if (mDialogVip == null || !mDialogVip.isShowing()) {
                mDialogVip = dialogNovelCoupon.getDialogVipPop(mActivity, chapterItem, true);
            }
        }
        dialogNovelCoupon.setOnOpenCouponListener(new DialogNovelCoupon.OnOpenCouponListener() {
            @Override
            public void onOpenCoupon(boolean isBuy) {
                if (mDialogVip != null) {
                    mDialogVip.dismiss();
                }
                chapterItem.setIs_buy_status(isBuy);
                updateRecord();
            }
        });
    }
}