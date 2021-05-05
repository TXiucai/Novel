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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.book.ReadHistory;
import com.heiheilianzai.app.model.event.RefreshTopbook;
import com.heiheilianzai.app.ui.activity.CatalogActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.ui.dialog.read.PurchaseDialog;
import com.heiheilianzai.app.view.BorderTextView;
import com.heiheilianzai.app.view.MScrollView;
import com.heiheilianzai.app.view.read.PageWidget;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.heiheilianzai.app.constant.ReaderConfig.READBUTTOM_HEIGHT;
import static com.heiheilianzai.app.constant.ReaderConfig.USE_AD;
import static com.heiheilianzai.app.constant.ReaderConfig.XIAOSHUO;
import static com.heiheilianzai.app.ui.activity.read.ReadActivity.USE_BUTTOM_AD;

/**
 * 小说翻页核心类。 重绘与监听都在这里实现
 * Created by Administrator on 2016/7/20 0020.
 */
public class PageFactory {

    public static boolean close_AD;//关闭广告
    int AD_PAGE;//
    private ReadingConfig config;
    //章节名称字体大小是正文字体大小的多少倍
    private static final float RATIO = 1.02f;
    private static final int OFFSET = 2;
    //页面宽
    private int mWidth;
    private int PercentWidth;
    //页面高
    private int mHeight, MHeight;
    //文字字体大小
    private float m_fontSize;
    //时间格式
    private SimpleDateFormat sdf;
    //时间
    private String date;
    //进度格式
    private DecimalFormat df;
    //电池边界宽度
    private float mBorderWidth;
    // 上下与边缘的距离
    private float marginHeight;
    //书名距离顶部的高度
    private float BookNameTop;
    // 左右与边缘的距离
    private float measureMarginWidth;
    // 左右与边缘的距离
    private float marginWidth;
    //状态栏距离底部高度
    private float statusMarginBottom;
    //行间距
    private float lineSpace;
    //字体
    private Typeface typeface;
    //文字画笔
    private Paint mPaint;
    //加载画笔
    private Paint waitPaint;
    //文字颜色
    private int m_textColor = Color.rgb(50, 65, 78);
    // 绘制内容的宽
    private float mVisibleHeight;
    // 绘制内容的宽
    private float mVisibleWidth;
    // 每页可以显示的行数
    private int mLineCount;
    //电池画笔
    private Paint mBatteryPaint;
    //章节名称画笔 粗体字 字号较文章偏大
    private Paint mChapterPaint;
    //画书名
    // private Paint mBookNamePaint;
    //背景图片
    private Bitmap m_book_bg = null;
    private Intent batteryInfoIntent;
    //电池电量百分比
    private float mBatteryPercentage;
    //电池外边框
    private RectF rect1 = new RectF();
    //电池内边框
    private RectF rect2 = new RectF();
    //当前是否为第一页
    private boolean m_isfirstPage;
    //当前是否为最后一页
    private boolean m_islastPage;
    //向后翻页
    private int page = 0;
    //书本widget
    private PageWidget mBookPageWidget;
    //书本名字
    private String bookName = "";
    //章节名字
    private String chapterTitle;
    public ChapterItem chapterItem;
    //当前电量
    private int level = 0;
    private BookUtil mBookUtil;
    private PageEvent mPageEvent;
    private TRPage currentPage;
    private TRPage prePage;
    private TRPage cancelPage;
    ContentValues values = new ContentValues();
    private String book_id;
    private String chapter_id;
    //是否是预览章节，1 预览章节  2 非预览章节
    private String mIsPreview;
    private static Status mStatus = Status.OPENING;
    private LinearLayout mPurchaseLayout;
    private BorderTextView mPurchaseOne;
    private BorderTextView mPurchaseSome;
    private TextView mSupport;
    private View activity_read_line_left, activity_read_line_right;
    private LinearLayout mPurchaseLayout2;
    private BorderTextView mPurchaseOne2;
    private BorderTextView mPurchaseSome2;
    private TextView mSupport2;
    private View activity_read_line_left2, activity_read_line_right2;
    private boolean hasNotchScreen;
    private float tendp, chapterRight;
    int Chapter_height;
    float reading_shangxia_textsize;
    Resources resources;
    long aapo = 0;
    BaseAd baseAd;
    ImageView list_ad_view_img;
    private Activity mActivity;
    private PurchaseDialog mPurchaseDialog;
    TextView bookpage_scroll_text;
    FrameLayout insert_todayone2;
    MScrollView bookpage_scroll;
    BaseBook baseBook;
    FrameLayout insert_todayone_button;
    int button_ad_heigth, bg_color, color;
    public boolean IS_CHAPTERLast = true;
    public boolean IS_CHAPTERFirst = true;
    public boolean mNextPage = true;//变化是否向后翻页
    private String is_new_content;

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
        mPurchaseDialog = new PurchaseDialog(context, false);
        mBookUtil = new BookUtil();
        config = ReadingConfig.getInstance();
        resources = context.getResources();
        //获取屏幕宽高
        MHeight = ScreenSizeUtils.getInstance(mActivity).getScreenHeight();
        mHeight = MHeight;
        button_ad_heigth = ImageUtil.dp2px(mActivity, READBUTTOM_HEIGHT);
        /*if (USE_BUTTOM_AD) {
            mHeight -= button_ad_heigth; //暂时浮在页面上
        }*/
        mWidth = ScreenSizeUtils.getInstance(mActivity).getScreenWidth();
        sdf = new SimpleDateFormat("HH:mm");//HH:mm为24小时制,hh:mm为12小时制
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
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        mPaint.setTextSize(m_fontSize);// 字体大小
        mPaint.setColor(m_textColor);// 字体颜色
        mPaint.setTypeface(typeface);
        mPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果
        waitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
        waitPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
        waitPaint.setTextSize(resources.getDimension(R.dimen.reading_max_text_size));// 字体大小
        waitPaint.setColor(m_textColor);// 字体颜色
        waitPaint.setTypeface(typeface);
        waitPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果
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
                new IntentFilter(Intent.ACTION_BATTERY_CHANGED));//注册广播,随时获取到电池电量信息
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
        mLineCount = (int) ((mVisibleHeight - OFFSET * marginHeight) / (m_fontSize + lineSpace));// 可显示的行数
    }

    private void calculateLineCount2() {
        mLineCount = (int) (mVisibleHeight / (m_fontSize + lineSpace));// 可显示的行数
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
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getEnd()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//进度
            // 画白分例
            String strPercent = df.format(myfPercent * 100);//进度文字
            c.drawText(strPercent + "%", mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y为坐标值
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
            // 画时间
            drawBatteryAndDate(c);
            //画书名
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
            //更新购买view的文字颜色
            mSupport(mIsPreview);
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    private void mSupport(String mIsPreview) {
        if (mIsPreview.equals("1")) {
            activity_read_line_left.setBackgroundColor(mPaint.getColor());
            activity_read_line_right.setBackgroundColor(mPaint.getColor());
            mSupport.setTextColor(mPaint.getColor());
            mPurchaseOne.setBorder(mPaint.getColor(), new int[]{2, 2, 2, 2}, new int[]{6, 6, 6, 6});
            mPurchaseSome.setBorder(mPaint.getColor(), new int[]{2, 2, 2, 2}, new int[]{6, 6, 6, 6});
        }
    }

    private void mSupport2(int color) {
        if (mIsPreview.equals("1")) {
            mPurchaseLayout2.setVisibility(View.VISIBLE);
            activity_read_line_left2.setBackgroundColor(color);
            activity_read_line_right2.setBackgroundColor(color);
            mSupport2.setTextColor(color);
            mPurchaseOne2.setBorder(color, new int[]{2, 2, 2, 2}, new int[]{6, 6, 6, 6});
            mPurchaseSome2.setBorder(color, new int[]{2, 2, 2, 2}, new int[]{6, 6, 6, 6});
        } else {
            mPurchaseLayout2.setVisibility(View.GONE);
        }
    }

    public void setPurchaseLayout(LinearLayout view, LinearLayout view2) {
        mPurchaseLayout = view;
        mPurchaseOne = mPurchaseLayout.findViewById(R.id.activity_read_purchase_one);
        mPurchaseSome = mPurchaseLayout.findViewById(R.id.activity_read_purchase_some);
        mSupport = mPurchaseLayout.findViewById(R.id.activity_read_support);
        activity_read_line_left = mPurchaseLayout.findViewById(R.id.activity_read_line_left);
        activity_read_line_right = mPurchaseLayout.findViewById(R.id.activity_read_line_right);
        mPurchaseOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseSingleChapter(book_id, chapter_id, 1);
            }
        });
        mPurchaseSome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPurchaseDialog.initData(book_id, chapter_id, null, null);
                mPurchaseDialog.show();
            }
        });
        mPurchaseLayout2 = view2;
        mPurchaseOne2 = mPurchaseLayout2.findViewById(R.id.activity_read_purchase_one);
        mPurchaseSome2 = mPurchaseLayout2.findViewById(R.id.activity_read_purchase_some);
        mSupport2 = mPurchaseLayout2.findViewById(R.id.activity_read_support);
        activity_read_line_left2 = mPurchaseLayout2.findViewById(R.id.activity_read_line_left);
        activity_read_line_right2 = mPurchaseLayout2.findViewById(R.id.activity_read_line_right);
        mPurchaseOne2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseSingleChapter(book_id, chapter_id, 1);
            }
        });
        mPurchaseSome2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPurchaseDialog.initData(book_id, chapter_id, null, null);
                mPurchaseDialog.show();
            }
        });
    }

    /**
     * @param bitmap
     * @param m_lines
     * @param updateChapter
     */
    public void onDraw1(String mIsPreview, Bitmap bitmap, String chapterTitle, List<String> m_lines, Boolean updateChapter) {
        try {
            //更新数据库进度
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
            //画进度及时间
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//进度
            // 画白分例
            String strPercent = df.format(myfPercent * 100) + "%";//进度文字
            c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y为坐标值
            baseBook.setAllPercent(strPercent);
            if (baseBook.isAddBookSelf() == 1) {
                ContentValues values = new ContentValues();
                values.put("allPercent", strPercent);
                LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
                EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent));
            }
            drawBatteryAndDate(c);
            //画书名
            c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
            //画章
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
            //更新购买view的文字颜色
            mSupport(mIsPreview);
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    int AD_text_WIDTH;

    private void drawBatteryAndDate(Canvas c) {
        if (c == null) {
            return;
        }
        String AD_text = resources.getString(R.string.app_name);
        if (ReaderConfig.USE_AD) {
            if (AD_text_WIDTH == 0) {
                AD_text_WIDTH = (int) (mBatteryPaint.measureText(AD_text));//;
            }
            c.drawText(AD_text, (mWidth - AD_text_WIDTH) / 2, mHeight - statusMarginBottom, mBatteryPaint);
        }
        int dateWith = (int) (mBatteryPaint.measureText(date) + mBorderWidth);//时间宽度
        c.drawText(date, marginWidth, mHeight - statusMarginBottom, mBatteryPaint);
        // 画电池
        level = batteryInfoIntent.getIntExtra("level", 0);
        int scale = batteryInfoIntent.getIntExtra("scale", 100);
        mBatteryPercentage = (float) level / scale;
        float rect1Left = marginWidth + dateWith + statusMarginBottom;//电池外框left位置
        //画电池外框
        float width = CommonUtil.convertDpToPixel(mActivity, 20) - mBorderWidth;
        float height = CommonUtil.convertDpToPixel(mActivity, 10);
        rect1.set(rect1Left, mHeight - height - statusMarginBottom, rect1Left + width, mHeight - statusMarginBottom);
        rect2.set(rect1Left + mBorderWidth, mHeight - height + mBorderWidth - statusMarginBottom, rect1Left + width - mBorderWidth, mHeight - mBorderWidth - statusMarginBottom);
        c.save();
        c.clipRect(rect2, Region.Op.DIFFERENCE);
        c.drawRect(rect1, mBatteryPaint);
        c.restore();
        //画电量部分
        rect2.left += mBorderWidth;
        rect2.right -= mBorderWidth;
        rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
        rect2.top += mBorderWidth;
        rect2.bottom -= mBorderWidth;
        c.drawRect(rect2, mBatteryPaint);
        //画电池头
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
                    if (mIsPreview.equals("1")) {
                        break DrawText;
                    }
                }
            }
            //画进度及时间
            float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
            if (mPageEvent != null) {
                mPageEvent.changeProgress(fPercent);
            }
            float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//进度
            // 画白分例
            String strPercent = df.format(myfPercent * 100) + "%";//进度文字
            c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y为坐标值
            baseBook.setAllPercent(strPercent);
            if (baseBook.isAddBookSelf() == 1) {
                ContentValues values = new ContentValues();
                values.put("allPercent", strPercent);
                LitePal.updateAll(BaseBook.class, values, "book_id = ?", book_id);
                EventBus.getDefault().post(new RefreshTopbook(book_id, strPercent));
            }
            drawBatteryAndDate(c);
            //画书名
            c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
            //更新购买view的文字颜色
            mSupport(mIsPreview);
            mBookPageWidget.postInvalidate();
        } catch (Exception e) {
        }
    }

    private View ADview2;
    public int Y;
    int Insert_todayone2;
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
        mSupport.setVisibility(View.GONE);
        mPurchaseLayout.setVisibility(View.GONE);
        if (ADview2 != null) {
            if (ReaderConfig.USE_AD && !close_AD) {
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

    //向前翻页
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

            if (ReaderConfig.USE_AD && !close_AD && IS_CHAPTERFirst) {
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                drawAD(mBookPageWidget.getNextPage());
                mBookPageWidget.setOnSwitchPreListener(new PageWidget.OnSwitchPreListener() {
                    @Override
                    public void switchPreChapter() {
                        insert_todayone2.setVisibility(View.VISIBLE);
                        IS_CHAPTERFirst = false;
                        mPurchaseLayout.setVisibility(View.GONE);
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
                                ChapterManager.notfindChapter(querychapterItem, book_id, preChapterId, new ChapterManager.ChapterDownload() {
                                    @Override
                                    public void finish() {
                                        drawLastChapter(querychapterItem, preChapterId);
                                    }
                                });
                                return;
                            }
                        }
                        drawLastChapter(querychapterItem, preChapterId);
                        checkIsCoupon(querychapterItem);
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

    //向后翻页
    public void nextPage() {
        boolean nextChapter = currentPage.getEnd() >= mBookUtil.getBookLen();
        mNextPage = true;
        if (currentPage == null) {
            return;
        }
        if (nextChapter) {//开启新章节
            if (!m_islastPage) {
            }
            if (!ChapterManager.getInstance(mActivity).hasNextChapter()) {
                if (!m_islastPage) {
                    MyToash.ToashError(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_endpage));
                }
                m_islastPage = true;
                return;
            }

            if (ReaderConfig.USE_AD && !close_AD && IS_CHAPTERLast && mBookPageWidget.Current_Page > 5) {
                cancelPage = currentPage;
                onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
                prePage = currentPage;
                drawAD(mBookPageWidget.getNextPage());
                mBookPageWidget.setOnSwitchNextListener(new PageWidget.OnSwitchNextListener() {
                    @Override
                    public void switchNextChapter() {
                        insert_todayone2.setVisibility(View.VISIBLE);
                        IS_CHAPTERLast = false;
                        mPurchaseLayout.setVisibility(View.GONE);
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
                                    ChapterManager.notfindChapter(querychapterItem, book_id, nextChapterId, new ChapterManager.ChapterDownload() {
                                        @Override
                                        public void finish() {
                                            drawNextChapter(querychapterItem, nextChapterId);
                                        }
                                    });
                                    return;
                                }
                            }
                            drawNextChapter(querychapterItem, nextChapterId);
                            checkIsCoupon(querychapterItem);
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

    public void cancelPage() {
        currentPage = cancelPage;
        if (ReaderConfig.USE_AD && !close_AD) {
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
                    if (mIsPreview.equals("1")) {
                        ViewUtils.setVisibility(mPurchaseLayout, View.VISIBLE);
                    } else if (mIsPreview.equals("0")) {
                        ViewUtils.setVisibility(mPurchaseLayout, View.GONE);
                    }
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
                if (mIsPreview.equals("1")) {
                    ViewUtils.setVisibility(mPurchaseLayout, View.VISIBLE);
                } else if (mIsPreview.equals("0")) {
                    ViewUtils.setVisibility(mPurchaseLayout, View.GONE);
                }
                PageFactory.mStatus = PageFactory.Status.FINISH;
                currentPage = getPageForBegin(chapterItem.getBegin());
            }
        } else {
            drawScroll();
        }
        checkIsCoupon(chapterItem);
        ReadHistory.addReadHistory(true, mActivity, book_id, chapter_id);//阅读历史上传 没看一个新章节都上传一次
    }

    /**
     * 单章购买
     */
    public void purchaseSingleChapter(final String book_id, final String chapter_id, int num) {
        if (!MainHttpTask.getInstance().Gotologin(mActivity)) {
            return;
        }
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("book_id", book_id);
        params.putExtraParams("chapter_id", chapter_id);
        params.putExtraParams("num", num + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterBuy, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ReaderConfig.REFREASH_USERCENTER = true;
                        downloadWithoutAutoBuy(book_id, chapter_id);
                        ReaderConfig.integerList.add(1);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void downloadWithoutAutoBuy(final String book_id, final String chapter_id) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("book_id", book_id);
        params.putExtraParams("chapter_id", chapter_id);
        params.putExtraParams("num", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterDownUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONArray jsonArray = new JSONArray(result);
                            ChapterContent chapterContent = new Gson().fromJson(jsonArray.getString(0), ChapterContent.class);
                            chapterItem.setIs_preview(chapterContent.getIs_preview());
                            chapterItem.setUpdate_time(chapterContent.getUpdate_time());
                            ContentValues values = new ContentValues();
                            String filepath = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(chapter_id + "/").concat(chapterItem.getIs_preview() + "/").concat(chapterItem.getIs_new_content() + "/").concat(chapterItem.getUpdate_time()).concat(".txt");
                            FileManager.createFile(filepath, chapterContent.getContent().getBytes());
                            values.put("chapteritem_begin", 0);
                            values.put("chapter_path", filepath);
                            values.put("is_preview", "0");
                            values.put("update_time", chapterContent.getUpdate_time());
                            LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, chapter_id);
                            if (mActivity != null) {
                                MyToash.ToashSuccess(mActivity, LanguageUtil.getString(mActivity, R.string.ReadActivity_buysuccess));
                                ChapterManager.getInstance(mActivity).getCurrentChapter().setIs_preview("0");
                                ChapterManager.getInstance(mActivity).getCurrentChapter().setChapter_path(filepath);
                                ChapterManager.getInstance(mActivity).getCurrentChapter().setUpdate_time(chapterContent.getUpdate_time());
                                if (config.getPageMode() != 4) {
                                    ChapterManager.getInstance(mActivity).openCurrentChapter(chapter_id);
                                } else {
                                    openBook(4, ChapterManager.getInstance(mActivity).getCurrentChapter(), null);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
            //判断是否换行
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

    //绘制当前页面
    public void currentPage(Boolean updateChapter) {
        if (ReaderConfig.USE_AD && !close_AD && (!IS_CHAPTERFirst || !IS_CHAPTERLast)) {
        } else {
            onDraw(mBookPageWidget.getNextPage(), currentPage.getLines(), updateChapter);
        }
    }

    //更新电量
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

    //改变字体大小
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

    //改变文字间距
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

    //改变字体
    public void changeTypeface(Typeface typeface) {
        jianfan = !jianfan;
        calculateLineCount1();
        measureMarginWidth();
        currentPage = getPageForBegin(0);
        currentPage(true);
    }

    //改变背景
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
            mSupport2(color);
        }
    }

    //初始化背景
    private void initBg(Boolean isNight) {
        if (!isNight) {
            setBookBg(config.getBookBgType());
        } else {
            setBookBg(ReadingConfig.BOOK_BG_8);
        }
    }

    //设置页面的背景
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
            //设置字体颜色
            setM_textColor(color);
        } catch (Error e) {
        }
    }

    public void setBookPageBg(int color) {
        if (mBookPageWidget != null) {
            mBookPageWidget.setBgColor(color);
        }
    }

    //设置日间或者夜间模式
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
                m_book_bg.recycle();      //scaleBitmap 回收
                m_book_bg = null;
            }
        } catch (Exception e) {
        }
        try {
            if (mBookPageWidget.getCurPage() != null && !mBookPageWidget.getCurPage().isRecycled()) {
                mBookPageWidget.getCurPage().recycle();      //scaleBitmap 回收
            }
        } catch (Exception e) {
        }
        try {
            if (mBookPageWidget.getNextPage() != null && !mBookPageWidget.getNextPage().isRecycled()) {
                mBookPageWidget.getNextPage().recycle();      //scaleBitmap 回收
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
        return (int) ((mVisibleHeight - OFFSET * marginHeight) / (m_fontSize + lineSpace));// 可显示的行数
    }

    public int getmLineCount2() {
        return (int) ((mVisibleHeight) / (m_fontSize + lineSpace));// 可显示的行数
    }

    public Paint getmPaint() {
        return mPaint;
    }

    public static Status getStatus() {
        return mStatus;
    }

    //是否是第一页
    public boolean isfirstPage() {
        return m_isfirstPage;
    }

    //是否是最后一页
    public boolean islastPage() {
        return m_islastPage;
    }

    //设置页面背景
    public void setBgBitmap(Bitmap BG) {
        m_book_bg = BG;
    }

    //设置页面背景
    public Bitmap getBgBitmap() {
        return m_book_bg;
    }

    public Bitmap getBgBitmap2() {
        initBg(config.getDayOrNight());
        return m_book_bg;
    }

    //设置文字颜色
    public void setM_textColor(int m_textColor) {
        this.m_textColor = m_textColor;
    }

    //获取文字颜色
    public int getTextColor() {
        return this.m_textColor;
    }

    //获取文字大小
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
        if (preChapter.getIs_preview().equals("0")) {
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
        } else {
            bookUtil.setPostition(-1);
            page.setBegin(0);
            calculateLineCount1();
            page.setLines(bookUtil.getNextLines(PageFactory.this));
            page.setEnd(bookUtil.getBookLen());
        }
        mBookPageWidget.setOnSwitchPreListener(new PageWidget.OnSwitchPreListener() {
            @Override
            public void switchPreChapter() {
                try {
                    //打开上一章节之前，更新当前章节的begin为0
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
        if (!close_AD && USE_AD) {
            drawAD(mBookPageWidget.getCurPage());
        } else
            onDraw(mBookPageWidget.getCurPage(), currentPage.getLines(), true);
        float fPercent = (float) (page.getBegin() * 1.0 / bookUtil.getBookLen());//进度
        String strPercent = df.format(fPercent * 100) + "%";//进度文字
        if (preChapter.getIs_preview().equals("1")) {
            onDraw2("1", mBookPageWidget.getNextPage(), preChapter.getChapter_title(), page.getLines(), strPercent, true);
        } else {
            onDraw2("", mBookPageWidget.getNextPage(), preChapter.getChapter_title(), page.getLines(), strPercent, false);
        }
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
                //打开下一章节之前，更新当前章节的begin为0
                ContentValues values = new ContentValues();
                values.put("chapteritem_begin", 0);
                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", nextChapter.getBook_id(), nextChapter.getChapter_id());
                nextChapter.setChapteritem_begin(0);
                if (mActivity != null) {
                    openBook(2, nextChapter, bookUtil);
                    ChapterManager.getInstance(mActivity).setCurrentChapter(nextChapter);
                    IS_CHAPTERLast = true;
                    if (ReaderConfig.USE_AD && !close_AD) {
                        getWebViewAD(mActivity);//获取广告
                    }
                }
            }
        });
        if (!close_AD && USE_AD) {
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

    //加载webview 广告
    public void getWebViewAD(Activity activity) {
        if (baseAd == null) {
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
                                if (baseAd.ad_type == 1) {
                                    close_AD = false;
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
                                        list_ad_view_img = insert_todayone2.findViewById(R.id.list_ad_view_img);
                                        ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
                                        layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
                                        layoutParams.height = layoutParams.width;
                                        Insert_todayone2 = layoutParams.width;
                                        list_ad_view_img.setLayoutParams(layoutParams);
                                    }
                                    MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
                                } else {
                                    close_AD = true;
                                }
                            } catch (Exception e) {
                                close_AD = true;
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
                    list_ad_view_img = insert_todayone2.findViewById(R.id.list_ad_view_img);
                    ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
                    layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
                    layoutParams.height = layoutParams.width;
                    list_ad_view_img.setLayoutParams(layoutParams);
                }
                MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
            }
        }
    }

    private void drawAD(Bitmap bitmap) {
        Canvas c = new Canvas(bitmap);
        try {
            c.drawBitmap(getBgBitmap(), 0, 0, null);
        } catch (Exception e) {
            c.drawBitmap(getBgBitmap2(), 0, 0, null);
        }
        Rect mSrcRect = new Rect(0, 0, mWidth, Insert_todayone2);
        Paint mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitPaint.setFilterBitmap(true);
        mBitPaint.setDither(true);
        int top = (MHeight - Insert_todayone2) / 2;
        Bitmap bitmapAD = ViewToBitmapUtil.convertViewToBitmap(insert_todayone2, top, Insert_todayone2);
        MyToash.Log("ViewToBitmapUtil", (bitmapAD == null) + "  " + top);
        Rect mDestRect = new Rect(0, top, mWidth, top + Insert_todayone2);
        c.drawBitmap(bitmapAD, mSrcRect, mDestRect, mBitPaint);
        //画进度及时间
        float fPercent = (float) (currentPage.getBegin() * 1.0 / mBookUtil.getBookLen());//进度
        if (mPageEvent != null) {
            mPageEvent.changeProgress(fPercent);
        }
        float myfPercent = (float) ((mBookUtil.getBookLen() * (chapterItem.getDisplay_order()) + currentPage.getBegin()) * 1.0 / (mBookUtil.getBookLen() * ChapterManager.getInstance(mActivity).mChapterList.size()));//进度
        // 画白分例
        String strPercent = df.format(myfPercent * 100) + "%";//进度文字
        c.drawText(strPercent, mWidth - PercentWidth, mHeight - statusMarginBottom, mBatteryPaint);//x y为坐标值
        // 画时间
        drawBatteryAndDate(c);
        //画书名
        c.drawText(chapterTitle, marginWidth, statusMarginBottom + BookNameTop + tendp, mBatteryPaint);
        mBookPageWidget.postInvalidate();
    }

    private void checkIsVip(ChapterItem chapterItem) {
        String is_vip = chapterItem.getIs_vip();
        if (is_vip != null && is_vip.equals("1") && !App.isVip(mActivity)) {
            DialogVip dialogVip = new DialogVip();
            dialogVip.getDialogVipPop(mActivity, true);
            return;
        }
    }

    private void checkIsCoupon(ChapterItem chapterItem) {
        if (!StringUtils.isEmpty(chapterItem.getIs_limited_free()) && TextUtils.equals(chapterItem.getIs_limited_free(), "1")) {
            return;
        } else {
            if (Utils.isLogin(mActivity)) {
                checkIsBuyCoupon(mActivity, chapterItem);
            } else {
                if (TextUtils.equals(chapterItem.getIs_book_coupon_pay(), "1") || TextUtils.equals(chapterItem.getIs_vip(), "1")) {
                    DialogLogin dialogLogin = new DialogLogin();
                    dialogLogin.getDialogLoginPop(mActivity);
                }
            }
        }
    }

    private void checkIsBuyCoupon(Activity activity, ChapterItem chapterItem) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("book_id", chapterItem.getBook_id());
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String paramString = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.chapter_text, paramString, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ChapterContent chapterContent = new Gson().fromJson(result, ChapterContent.class);
                        String is_book_coupon_pay = chapterItem.getIs_book_coupon_pay();
                        if (!chapterContent.isIs_buy_status()) {
                            if (is_book_coupon_pay != null && is_book_coupon_pay.equals("1") && !App.isVip(mActivity)) {
                                DialogNovelCoupon dialogNovelCoupon = new DialogNovelCoupon();
                                Dialog dialogVipPop = dialogNovelCoupon.getDialogVipPop(mActivity, chapterItem, true);
                                dialogNovelCoupon.setOnOpenCouponListener(new DialogNovelCoupon.OnOpenCouponListener() {
                                    @Override
                                    public void onOpenCoupon(boolean isBuy) {
                                        if (dialogVipPop != null) {
                                            dialogVipPop.dismiss();
                                        }
                                        chapterItem.setIs_buy_status(true);
                                    }
                                });
                                return;
                            }
                            checkIsVip(chapterItem);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}