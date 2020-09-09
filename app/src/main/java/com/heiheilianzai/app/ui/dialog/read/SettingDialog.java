package com.heiheilianzai.app.ui.dialog.read;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.PageFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/7/26 0026.
 */
public class SettingDialog extends Dialog {
    @BindView(R.id.tv_subtract)
    View tv_subtract;
    @BindView(R.id.tv_size)
    TextView tv_size;
    @BindView(R.id.tv_add)
    View tv_add;
    @BindView(R.id.iv_bg_default)
    View iv_bg_default;
    @BindView(R.id.iv_bg_1)
    View iv_bg1;
    @BindView(R.id.iv_bg_2)
    View iv_bg2;
    @BindView(R.id.iv_bg_3)
    View iv_bg3;
    @BindView(R.id.iv_bg_4)
    View iv_bg4;
    @BindView(R.id.iv_bg_7)
    View iv_bg7;
    @BindView(R.id.iv_bg_8)
    View iv_bg8;
    @BindView(R.id.tv_simulation)
    TextView tv_simulation;
    @BindView(R.id.tv_cover)
    TextView tv_cover;
    @BindView(R.id.tv_slide)
    TextView tv_slide;
    @BindView(R.id.tv_none)
    TextView tv_none;
    @BindView(R.id.tv_shangxia)
    TextView tv_shangxia;
    @BindView(R.id.margin_big)
    View margin_big;
    @BindView(R.id.margin_medium)
    View margin_medium;
    @BindView(R.id.margin_small)
    View margin_small;
    @BindView(R.id.margin_big_tv)
    View margin_big_tv;
    @BindView(R.id.margin_medium_tv)
    View margin_medium_tv;
    @BindView(R.id.margin_small_tv)
    View margin_small_tv;
    @BindView(R.id.auto_read_layout)
    View auto_read_layout;
    @BindView(R.id.tv_jianfan)
    View tv_jianfan;

    private ReadActivity mContext;
    private ReadingConfig config;
    private Boolean isSystem;
    private SettingListener mSettingListener;
    private int FONT_SIZE_MIN;
    private int FONT_SIZE_MAX;
    private int currentFontSize;
    private ProgressBar auto_read_progress_bar;
    public static boolean scroll;
    PageFactory pageFactory;

    private SettingDialog(Context context, boolean flag, OnCancelListener listener) {
        super(context, flag, listener);
    }

    public SettingDialog(Context context) {
        this(context, R.style.setting_dialog);
        mContext = (ReadActivity) context;
    }

    public SettingDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setPageFactory(PageFactory pageFactory) {
        this.pageFactory = pageFactory;
    }

    private void setBG(int i) {
        GradientDrawable gd = new GradientDrawable();
        gd.setCornerRadius(10f);
        gd.setGradientType(GradientDrawable.RECTANGLE);
        switch (i) {
            case 0:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_default));
                iv_bg_default.setBackground(gd);
                break;
            case 1:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_1));
                iv_bg1.setBackground(gd);
                break;
            case 2:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_2));
                iv_bg2.setBackground(gd);
                break;
            case 3:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_3));
                iv_bg3.setBackground(gd);
                break;
            case 4:
                gd.setStroke(1, mContext.getResources().getColor(R.color.gray2));//描边的颜色和宽度
                gd.setColor(Color.WHITE);
                iv_bg4.setBackground(gd);
                break;
            case 7:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_7));
                iv_bg7.setBackground(gd);
                break;
            case 8:
                gd.setColor(mContext.getResources().getColor(R.color.read_bg_8));
                iv_bg8.setBackground(gd);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_setting);
        // 初始化View注入
        ButterKnife.bind(this);
        setBG(0);
        setBG(3);
        setBG(1);
        setBG(2);
        setBG(3);
        setBG(4);
        setBG(7);
        setBG(8);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
        FONT_SIZE_MIN = (int) getContext().getResources().getDimension(R.dimen.reading_min_text_size);
        FONT_SIZE_MAX = (int) getContext().getResources().getDimension(R.dimen.reading_max_text_size);
        config = ReadingConfig.getInstance();
        //是上下滑动模式
        if (config.getPageMode() == 4) {
            scroll = true;
        }
        //初始化亮度
        isSystem = config.isSystemLight();
        //初始化字体大小
        currentFontSize = (int) config.getFontSize();
        tv_size.setText(currentFontSize + "");
        selectFontSize(config.getFontSize());
        selectBg(config.getBookBgType());
        selectPageMode(ReadingConfig.getInstance().getPageMode());
        selectLineSpacing(config.getLineSpacingMode());
    }

    public void selectFontSize(float size) {
        currentFontSize = (int) size;
        tv_size.setText(currentFontSize + "");
    }

    //选择翻页
    private void selectPageMode(int pageMode) {
        if (pageMode == ReadingConfig.PAGE_MODE_SIMULATION) {
            setTextViewSelect(tv_simulation, true);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
            setTextViewSelect(tv_shangxia, false);
        } else if (pageMode == ReadingConfig.PAGE_MODE_COVER) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, true);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, false);
            setTextViewSelect(tv_shangxia, false);
        } else if (pageMode == ReadingConfig.PAGE_MODE_SLIDE) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, true);
            setTextViewSelect(tv_none, false);
            setTextViewSelect(tv_shangxia, false);
        } else if (pageMode == ReadingConfig.PAGE_MODE_SCROLL) {
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_shangxia, true);
            setTextViewSelect(tv_none, false);
        } else if (pageMode == ReadingConfig.PAGE_MODE_NONE) {
            setTextViewSelect(tv_shangxia, false);
            setTextViewSelect(tv_simulation, false);
            setTextViewSelect(tv_cover, false);
            setTextViewSelect(tv_slide, false);
            setTextViewSelect(tv_none, true);
        }
    }

    //设置按钮选择的背景
    private void setLineSpacingViewSelect(View view, View tv, Boolean isSelect, int selectedRes, int unSelectedRes) {
        if (isSelect) {
            view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_setting_dialog_font_bg_pressed));
            tv.setBackgroundResource(selectedRes);
        } else {
            view.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_setting_dialog_font_bg_unpressed));
            tv.setBackgroundResource(unSelectedRes);
        }
    }

    //选择间距
    private void selectLineSpacing(int marginMode) {
        if (marginMode == ReadingConfig.LINE_SPACING_BIG) {
            setLineSpacingViewSelect(margin_big, margin_big_tv, true, R.mipmap.line_spacing_big_select, R.mipmap.line_spacing_big);
            setLineSpacingViewSelect(margin_medium, margin_medium_tv, false, R.mipmap.line_spacing_medium_select, R.mipmap.line_spacing_medium);
            setLineSpacingViewSelect(margin_small, margin_small_tv, false, R.mipmap.line_spacing_small_select, R.mipmap.line_spacing_small);
        } else if (marginMode == ReadingConfig.LINE_SPACING_MEDIUM) {
            setLineSpacingViewSelect(margin_big, margin_big_tv, false, R.mipmap.line_spacing_big_select, R.mipmap.line_spacing_big);
            setLineSpacingViewSelect(margin_medium, margin_medium_tv, true, R.mipmap.line_spacing_medium_select, R.mipmap.line_spacing_medium);
            setLineSpacingViewSelect(margin_small, margin_small_tv, false, R.mipmap.line_spacing_small_select, R.mipmap.line_spacing_small);
        } else if (marginMode == ReadingConfig.LINE_SPACING_SMALL) {
            setLineSpacingViewSelect(margin_big, margin_big_tv, false, R.mipmap.line_spacing_big_select, R.mipmap.line_spacing_big);
            setLineSpacingViewSelect(margin_medium, margin_medium_tv, false, R.mipmap.line_spacing_medium_select, R.mipmap.line_spacing_medium);
            setLineSpacingViewSelect(margin_small, margin_small_tv, true, R.mipmap.line_spacing_small_select, R.mipmap.line_spacing_small);
        }
    }

    //选择背景（这段代码可能因为需求原因，注释了可用）
    private void selectBg(int type) {
//        switch (type) {
//            case ReadingConfig.BOOK_BG_DEFAULT:
//                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
//                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                break;
//            case ReadingConfig.BOOK_BG_1:
//                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
//                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                break;
//            case ReadingConfig.BOOK_BG_2:
//                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
//                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                break;
//            case ReadingConfig.BOOK_BG_3:
//                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
//                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                break;
//            case ReadingConfig.BOOK_BG_4:
//                iv_bg_default.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg1.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg2.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg3.setBorderWidth(DisplayUtils.dp2px(getContext(), 0));
//                iv_bg4.setBorderWidth(DisplayUtils.dp2px(getContext(), 2));
//                break;
//        }
    }

    //设置背景
    public void setBookBg(int type) {
        config.setBookBg(type);
        if (mSettingListener != null) {
            mSettingListener.changeBookBg(type);
        }
    }

    //设置按钮选择的背景
    private void setTextViewSelect(TextView textView, Boolean isSelect) {
        if (isSelect) {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_setting_dialog_font_bg_pressed));
            textView.setTextColor(getContext().getResources().getColor(R.color.white));
        } else {
            textView.setBackgroundDrawable(getContext().getResources().getDrawable(R.drawable.shape_setting_dialog_font_bg_unpressed));
            textView.setTextColor(getContext().getResources().getColor(R.color.black));
        }
    }

    private void applyCompat() {
        if (Build.VERSION.SDK_INT < 19) {
            return;
        }
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//去掉信息栏
    }

    public void setProgressBar(ProgressBar bar) {
        auto_read_progress_bar = bar;
    }

    public Boolean isShow() {
        return isShowing();
    }

    @OnClick({R.id.tv_subtract, R.id.tv_add, R.id.iv_bg_default,
            R.id.iv_bg_1, R.id.iv_bg_2, R.id.iv_bg_3,
            R.id.iv_bg_4, /*R.id.iv_bg_5, R.id.iv_bg_6,*/
            R.id.iv_bg_7, R.id.iv_bg_8, R.id.tv_simulation,
            R.id.tv_cover, R.id.tv_slide, R.id.tv_none, R.id.tv_shangxia,
            R.id.margin_big, R.id.margin_medium, R.id.margin_small,
            R.id.auto_read_layout, R.id.tv_jianfan})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_jianfan:
                mSettingListener.changeTypeFace(null);
                break;
            case R.id.tv_subtract:
                subtractFontSize();
                break;
            case R.id.tv_add:
                addFontSize();
                break;
            case R.id.iv_bg_default:
                setBookBg(ReadingConfig.BOOK_BG_DEFAULT);
                selectBg(ReadingConfig.BOOK_BG_DEFAULT);
                break;
            case R.id.iv_bg_1:
                setBookBg(ReadingConfig.BOOK_BG_1);
                selectBg(ReadingConfig.BOOK_BG_1);
                break;
            case R.id.iv_bg_2:
                setBookBg(ReadingConfig.BOOK_BG_2);
                selectBg(ReadingConfig.BOOK_BG_2);
                break;
            case R.id.iv_bg_3:
                setBookBg(ReadingConfig.BOOK_BG_3);
                selectBg(ReadingConfig.BOOK_BG_3);
                break;
            case R.id.iv_bg_4:
                setBookBg(ReadingConfig.BOOK_BG_4);
                selectBg(ReadingConfig.BOOK_BG_4);
                break;
            case R.id.iv_bg_7:
                setBookBg(ReadingConfig.BOOK_BG_7);
                selectBg(ReadingConfig.BOOK_BG_7);
                break;
            case R.id.iv_bg_8:
                setBookBg(ReadingConfig.BOOK_BG_8);
                selectBg(ReadingConfig.BOOK_BG_8);
                break;
            case R.id.tv_simulation:
                selectPageMode(ReadingConfig.PAGE_MODE_SIMULATION);
                setPageMode(ReadingConfig.PAGE_MODE_SIMULATION);
                MyToash.Log("openBook", ReadingConfig.getInstance().getPageMode() + "");
                if (scroll) {
                    pageFactory.openBook(3, pageFactory.chapterItem, null);
                    scroll = false;
                }
                break;
            case R.id.tv_cover:
                selectPageMode(ReadingConfig.PAGE_MODE_COVER);
                setPageMode(ReadingConfig.PAGE_MODE_COVER);
                MyToash.Log("openBook", ReadingConfig.getInstance().getPageMode() + "");
                if (scroll) {
                    pageFactory.openBook(3, pageFactory.chapterItem, null);
                    scroll = false;
                }
                break;
            case R.id.tv_slide:
                selectPageMode(ReadingConfig.PAGE_MODE_SLIDE);
                setPageMode(ReadingConfig.PAGE_MODE_SLIDE);
                MyToash.Log("openBook", ReadingConfig.getInstance().getPageMode() + "");
                if (scroll) {
                    pageFactory.openBook(3, pageFactory.chapterItem, null);
                    scroll = false;
                }
                break;
            case R.id.tv_shangxia:
                selectPageMode(ReadingConfig.PAGE_MODE_SCROLL);
                setPageMode(ReadingConfig.PAGE_MODE_SCROLL);
                MyToash.Log("openBook", ReadingConfig.getInstance().getPageMode() + "");
                if (!scroll) {
                    pageFactory.openBook(4, pageFactory.chapterItem, null);
                    scroll = true;
                }
                break;
            case R.id.tv_none:
                selectPageMode(ReadingConfig.PAGE_MODE_NONE);
                setPageMode(ReadingConfig.PAGE_MODE_NONE);
                if (scroll) {
                    pageFactory.openBook(3, pageFactory.chapterItem, null);
                    scroll = false;
                }
                break;
            case R.id.margin_big:
                selectLineSpacing(ReadingConfig.LINE_SPACING_BIG);
                setLineSpacingMode(ReadingConfig.LINE_SPACING_BIG);
                break;
            case R.id.margin_medium:
                selectLineSpacing(ReadingConfig.LINE_SPACING_MEDIUM);
                setLineSpacingMode(ReadingConfig.LINE_SPACING_MEDIUM);
                break;
            case R.id.margin_small:
                selectLineSpacing(ReadingConfig.LINE_SPACING_SMALL);
                setLineSpacingMode(ReadingConfig.LINE_SPACING_SMALL);
                break;
            case R.id.auto_read_layout:
                if (ReadingConfig.getInstance().getPageMode() != 4) {
                    AutoProgressBar.getInstance().setProgressBar(auto_read_progress_bar);
                    AutoProgressBar.getInstance().setTime(config.getAutoSpeed() * 1000);
                    AutoProgressBar.getInstance().start(new AutoProgressBar.ProgressCallback() {
                        @Override
                        public void finish() {
                            if (ChapterManager.getInstance(mContext).hasNextChapter()) {
                                mContext.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mContext.getPageFactory().getPageWidget().next_page();
                                    }
                                });
                            } else {
                                if (!AutoProgressBar.getInstance().isStop()) {
                                    AutoProgressBar.getInstance().stop();
                                }
                            }
                        }
                    });
                    hideSystemUI();
                    hide();
                } else {
                    MyToash.ToashError(mContext, "当前模式不支持自动阅读");
                }
                break;
        }
    }

    private void hideSystemUI() {
    }

    //设置翻页
    public void setPageMode(int pageMode) {
        config.setPageMode(pageMode);
        mContext.getBookPage().setPageMode(pageMode);
    }

    public void setLineSpacingMode(int mode) {
        config.setLineSpacingMode(mode);
        if (mSettingListener != null) {
            mSettingListener.changeLineSpacing(mode);
        }
    }

    //变大书本字体
    private void addFontSize() {
        if (currentFontSize < FONT_SIZE_MAX) {
            currentFontSize += 1;
            tv_size.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    private void defaultFontSize() {
        currentFontSize = (int) getContext().getResources().getDimension(R.dimen.reading_default_text_size);
        tv_size.setText(currentFontSize + "");
        config.setFontSize(currentFontSize);
        if (mSettingListener != null) {
            mSettingListener.changeFontSize(currentFontSize);
        }
    }

    //变小书本字体
    private void subtractFontSize() {
        if (currentFontSize > FONT_SIZE_MIN) {
            currentFontSize -= 1;
            tv_size.setText(currentFontSize + "");
            config.setFontSize(currentFontSize);
            if (mSettingListener != null) {
                mSettingListener.changeFontSize(currentFontSize);
            }
        }
    }

    public void setSettingListener(SettingListener settingListener) {
        this.mSettingListener = settingListener;
    }

    public interface SettingListener {
        void changeSystemBright(Boolean isSystem, float brightness);

        void changeFontSize(int fontSize);

        void changeTypeFace(Typeface typeface);

        void changeBookBg(int type);

        void changeLineSpacing(int mode);
    }
}