package com.heiheilianzai.app.component;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.utils.BookUtil;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.NotchScreen;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.TRPage;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.ReaderConfig.READBUTTOM_HEIGHT;

public class ChapterPageManager {
    private ReadingConfig config;
    //章节名称字体大小是正文字体大小的多少倍
    private static final int OFFSET = 2;
    //页面宽
    private int mWidth;
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
    //文字画笔
    private Paint mPaint;
    //文字颜色
    private int m_textColor = Color.rgb(50, 65, 78);
    // 绘制内容的宽
    private float mVisibleHeight;
    // 绘制内容的宽
    private float mVisibleWidth;
    // 每页可以显示的行数
    private int mLineCount;
    public ChapterItem chapterItem;
    private BookUtil mBookUtil;
    private TRPage currentPage;
    Resources resources;
    int button_ad_heigth;
    private float tendp;
    private List<TRPage> mChapterPages;

    public ChapterPageManager(Context mActivity, ChapterItem querychapterItem) {
        try {
            mChapterPages = new ArrayList<>();
            mBookUtil = new BookUtil();
            mBookUtil.openBook(mActivity, querychapterItem, querychapterItem.getBook_id(), querychapterItem.getChapter_id());
            config = ReadingConfig.getInstance();
            resources = mActivity.getResources();
            //获取屏幕宽高
            MHeight = ScreenSizeUtils.getInstance(mActivity).getScreenHeight();
            mHeight = MHeight;
            button_ad_heigth = ImageUtil.dp2px(mActivity, READBUTTOM_HEIGHT);
            if (ReaderConfig.TOP_READ_AD != null) {
                mHeight -= button_ad_heigth;
            }
            if (ReaderConfig.BOTTOM_READ_AD != null) {
                mHeight -= button_ad_heigth;
            }
            mWidth = ScreenSizeUtils.getInstance(mActivity).getScreenWidth();
            sdf = new SimpleDateFormat("HH:mm");//HH:mm为24小时制,hh:mm为12小时制
            date = sdf.format(new java.util.Date());
            df = new DecimalFormat("#0.0");
            marginWidth = resources.getDimension(R.dimen.readingMarginWidth);
            lineSpace = resources.getDimension(R.dimen.reading_line_spacing_medium);
            mVisibleWidth = mWidth - marginWidth * 2;
            mVisibleHeight = mHeight - marginHeight * 2;
            if (ReaderConfig.BANG_SCREEN) {
                tendp = ImageUtil.dp2px(mActivity, 15);
                statusMarginBottom = resources.getDimension(R.dimen.reading_status_margin_bottom_hasNotchScreen);
                marginHeight = resources.getDimension(R.dimen.readingMarginHeight);
                BookNameTop = ImageUtil.dp2px(mActivity, 12);
            } else {
                statusMarginBottom = resources.getDimension(R.dimen.reading_status_margin_bottom);
                marginHeight = resources.getDimension(R.dimen.readingMarginHeightNotchScreen);
                BookNameTop = ImageUtil.dp2px(mActivity, 15);
            }
            m_fontSize = config.getFontSize();
            mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);// 画笔
            mPaint.setTextAlign(Paint.Align.LEFT);// 左对齐
            mPaint.setTextSize(m_fontSize);// 字体大小
            mPaint.setColor(m_textColor);// 字体颜色
            mPaint.setTypeface(config.getTypeface());
            mPaint.setSubpixelText(true);// 设置该项为true，将有助于文本在LCD屏幕上的显示效果
        } catch (Exception e) {
        }
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

    public TRPage getNextPage() {
        mBookUtil.setPostition(currentPage.getEnd());
        TRPage trPage = new TRPage();
        trPage.setBegin(currentPage.getEnd() + 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        currentPage = trPage;
        return trPage;
    }

    public TRPage getPageForBegin(long begin) {
        TRPage trPage = new TRPage();
        trPage.setBegin(begin);
        mBookUtil.setPostition(begin - 1);
        trPage.setLines(getNextLines());
        trPage.setEnd(mBookUtil.getPosition());
        currentPage = trPage;
        return trPage;
    }

    public List<TRPage> getPages(long begin) {
        calculateLineCount();
        mChapterPages.clear();
        TRPage pageForBegin = getPageForBegin(begin);
        mChapterPages.add(pageForBegin);
        while (mBookUtil.next(true) != 1) {
            TRPage nextPage = getNextPage();
            if (nextPage.getEnd() < mBookUtil.getBookLen()) {
                mChapterPages.add(nextPage);
            } else {
                currentPage.setBegin(0);
                break;
            }
        }
        return mChapterPages;
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
}
