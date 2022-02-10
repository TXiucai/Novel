package com.heiheilianzai.app.ui.activity.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicRecyclerViewAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaEventConfig;
import com.heiheilianzai.app.model.AppUpdate;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.BaseComicImage;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.ComicChapterItem;
import com.heiheilianzai.app.model.comic.ComicInfo;
import com.heiheilianzai.app.model.comic.ComicReadHistory;
import com.heiheilianzai.app.model.event.BuyLoginSuccessEvent;
import com.heiheilianzai.app.model.event.comic.ComicChapterEventbus;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.dialog.comic.LookComicSetDialog;
import com.heiheilianzai.app.ui.dialog.comic.PurchaseDialog;
import com.heiheilianzai.app.ui.fragment.comic.ComicinfoMuluFragment;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.BrightnessUtil;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.DialogComicLook;
import com.heiheilianzai.app.utils.DialogCouponNotMore;
import com.heiheilianzai.app.utils.DialogNovelCoupon;
import com.heiheilianzai.app.utils.DialogRegister;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.utils.decode.GlideEncypeImageLoader;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.heiheilianzai.app.view.comic.DanmuRelativeLayout;
import com.heiheilianzai.app.view.comic.ZoomRecyclerView;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;
import com.mobi.xad.bean.AdType;
import com.mobi.xad.net.XAdRequestListener;
import com.sensorsdata.analytics.android.sdk.SensorsDataAPI;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.OkHttpClient;

import static com.heiheilianzai.app.constant.ComicConfig.IS_OPEN_DANMU;
import static com.heiheilianzai.app.constant.ComicConfig.SET_OPEN_DANMU;
import static com.heiheilianzai.app.constant.ReaderConfig.MANHAU;

/**
 * 阅读漫画页面
 * Created by abc on 2016/11/4.
 */
public class ComicLookActivity extends BaseButterKnifeActivity {
    //漫画数据
    public static final String BASE_COMIC_EXT_KAY = "baseComic";
    //是否从漫画详情页进入
    public static final String FORM_INFO_EXT_KAY = "FORM_INFO";
    //是否从阅读历史页进入
    public static final String FORM_READHISTORY_EXT_KAY = "FORM_READHISTORY";
    //神策埋点数据从哪个页面进入
    public static final String REFER_PAGE_EXT_KAY = "referPage";

    @BindView(R.id.activity_comiclook_lording)
    public LinearLayout activity_comiclook_lording;
    @BindView(R.id.activity_comiclook_lording_img)
    public ImageView activity_comiclook_lording_img;
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.activity_comiclook_recyclerView)
    public ZoomRecyclerView activity_comiclook_RecyclerView;
    @BindView(R.id.activity_comiclook_head)
    public RelativeLayout activity_comiclook_head;
    @BindView(R.id.activity_comiclook_foot)
    public LinearLayout activity_comiclook_foot;
    @BindView(R.id.activity_comiclook_shoucang)
    public ImageView activity_comiclook_shoucang;
    @BindView(R.id.activity_comiclook_refresh)
    public ImageView activity_comiclook_refresh;
    @BindView(R.id.activity_comiclook_dingbu)
    public ImageView activity_comiclook_dingbu;
    @BindView(R.id.activity_comiclook_danmu_layout)
    public LinearLayout activity_comiclook_danmu_layout;
    @BindView(R.id.fragment_comicinfo_mulu_dangqian_layout)
    public View fragment_comicinfo_mulu_dangqian_layout;
    @BindView(R.id.activity_comiclook_danmu_img)
    public ImageView activity_comiclook_danmu_img;
    @BindView(R.id.activity_comiclook_danmu_text)
    public TextView activity_comiclook_danmu_text;
    @BindView(R.id.activity_comiclook_danmu_img2)
    public ImageView activity_comiclook_danmu_img2;
    @BindView(R.id.activity_comiclook_danmu_edit)
    public EditText activity_comiclook_danmu_edit;
    @BindView(R.id.activity_comiclook_danmu_fashe)
    public TextView activity_comiclook_danmu_fashe;
    @BindView(R.id.activity_comiclook_pinglunshu)
    public TextView activity_comiclook_pinglunshu;
    @BindView(R.id.activity_comiclook_xiayihua)
    public ImageView activity_comiclook_xiayihua;
    @BindView(R.id.activity_comiclook_shangyihua)
    public ImageView activity_comiclook_shangyihua;
    @BindView(R.id.item_dialog_downadapter_rotationLoadingView)
    public AVLoadingIndicatorView item_dialog_downadapter_RotationLoadingView;
    @BindView(R.id.activity_comiclook_danmu_dangqianhua)
    public TextView activity_comiclook_danmu_dangqianhua;
    @BindView(R.id.activity_comiclook_tucao_layout)
    public FrameLayout activity_comiclook_tucao_layout;
    @BindView(R.id.activity_comiclook_share)
    public RelativeLayout activity_comiclook_share;
    @BindView(R.id.rl_foot_set)
    public RelativeLayout mRlFootSet;
    @BindView(R.id.rl_comic_small)
    public RelativeLayout mRlSmall;
    @BindView(R.id.comic_big_back)
    public ImageView mImgBigBack;
    @BindView(R.id.comic_rb_small)
    public RadioButton mRbSmall;
    @BindView(R.id.comic_rb_mid)
    public RadioButton mRbMid;
    @BindView(R.id.comic_rb_big)
    public RadioButton mRbBig;
    @BindView(R.id.img_big)
    public ImageView mImgBig;
    @BindView(R.id.rl_comic_rb)
    public RelativeLayout mRlRb;
    @BindView(R.id.sv_big)
    public ScrollView mSvBig;
    @BindView(R.id.img_top_ad)
    public ImageView mImgTopAd;
    @BindView(R.id.img_bottom_ad)
    public ImageView mImgBottomAd;

    Map<String, ComicChapterItem> map = new HashMap();//临时存储章节数据
    PurchaseDialog purchaseDialog;
    MyViewHolder holderFoot;
    ComicRecyclerViewAdapter comicChapterCatalogAdapter;
    List<BaseComicImage> baseComicImages;
    int baseComicImagesSize;
    int total_count;
    boolean MenuSHOW, CommentFlag;
    ComicChapterItem comicChapterItem;
    Gson gson = new Gson();
    BaseComic baseComic, baseComicLocal;
    List<ComicChapter> comicChapter;
    int tocao_bgcolor;
    int HEIGHT;
    int WIDTH;
    int FOOT_HEIGTH;
    BaseComicImage baseComicImage;
    int current_read_img_order;//当前的图片位置
    int Last_read_img_order;//当前可见的最后一张图
    String comic_id;
    String Chapter_id, Chapter_title = "";
    int ComicChapterSize;
    boolean first = true;
    ComicChapter CurrentComicChapter;//无网时候 当前的阅读章节
    LinearLayoutManager linearLayoutManager;
    boolean FORM_INFO, FORM_READHISTORY;
    View activity_comic_look_foot;
    String mReferPage;//从哪个页面打开漫画阅读(神策埋点数据)
    long mOpenCurrentTime;//打开漫画阅读页的当前时间
    ComicInfo mComicInfo;//漫画具体信息
    private Dialog dialogVipPop;
    private boolean mIsSmall;
    private long mReadStarTime;
    private boolean mIsSdkAd = false;
    private boolean mIsSdkBottomAd = false;
    private boolean mIsSdkChapterAd = false;
    private boolean mIsShowChapterAd = false;
    public static boolean mIsSipAd = false;
    private boolean mIsShowChapter = false;
    private BaseAd mSdkTopAd;
    private BaseAd mChapterBaseAd;

    /**
     * 当前是第几页
     */
    private int mPageIndex;

    @Override
    public int initContentView() {
        return R.layout.activity_comiclook;
    }

    private void getBuy() {
        purchaseDialog = new PurchaseDialog(activity, false, new PurchaseDialog.BuySuccess() {
            @Override
            public void buySuccess(String[] ids, int num) {
                final ReaderParams params = new ReaderParams(activity);
                params.putExtraParams("comic_id", comic_id);
                params.putExtraParams("chapter_id", Chapter_id);
                String json = params.generateParamsJson();
                HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_chapter, json, false, new HttpUtils.ResponseListener() {
                            @Override
                            public void onResponse(final String result) {
                                ComicChapterItem comicChapterItem = gson.fromJson(result, ComicChapterItem.class);
                                BaseAd comicAdvert = comicChapterItem.getAdvert();
                                HandleData(comicChapterItem, Chapter_id, comic_id, activity);
                                map.put(Chapter_id, comicChapterItem);
                                CurrentComicChapter.setImagesText(result);
                                ContentValues values = new ContentValues();
                                values.put("ImagesText", result);
                                LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                                purchaseDialog.dismiss();
                            }

                            @Override
                            public void onErrorResponse(String ex) {
                                purchaseDialog.dismiss();
                            }
                        }
                );
            }
        }, false);
        purchaseDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                try {
                    ContentValues values = new ContentValues();
                    values.put("current_read_img_order", current_read_img_order);
                    LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                    if (FORM_INFO) {
                        CurrentComicChapter.current_read_img_order = current_read_img_order;
                        EventBus.getDefault().post(new ComicChapterEventbus(0, CurrentComicChapter));//更新上一界面的 数据
                    }
                } catch (Exception r) {
                }
                finish();
                return true;
            }
        });
        purchaseDialog.initData(comic_id, Chapter_id);
        purchaseDialog.show();
    }

    @SuppressLint("NotifyDataSetChanged")
    @OnClick(value = {R.id.titlebar_back, R.id.activity_comiclook_shoucang, R.id.activity_comiclook_refresh, R.id.activity_comiclook_dingbu, R.id.activity_comiclook_danmu_layout,
            R.id.activity_comiclook_danmu_fashe, R.id.activity_comiclook_xiayihua_layout, R.id.activity_comiclook_shangyihua_layout, R.id.activity_comiclook_set,
            R.id.activity_comiclook_tucao_layout, R.id.activity_comiclook_share, R.id.activity_comiclook_xiazai, R.id.activity_comiclook_quanji,
            R.id.activity_comiclook_danmu_img2, R.id.activity_comiclook_foot, R.id.comic_rb_small, R.id.comic_rb_mid, R.id.comic_rb_big, R.id.comic_big_back, R.id.rl_big})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                try {
                    ContentValues values = new ContentValues();
                    values.put("current_read_img_order", current_read_img_order);
                    LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                    if (FORM_INFO) {
                        CurrentComicChapter.current_read_img_order = current_read_img_order;
                        EventBus.getDefault().post(new ComicChapterEventbus(0, CurrentComicChapter));//更新上一界面的 数据
                    }
                } catch (Exception e) {
                }
                askIsNeedToAddShelf();
            case R.id.activity_comiclook_foot:
                break;
            case R.id.activity_comiclook_refresh: //刷新页面
                initData();
                break;
            case R.id.activity_comiclook_shoucang:
                baseComic.saveIsexist(true);
                activity_comiclook_shoucang.setVisibility(View.GONE);
                MyToash.ToashSuccess(activity, LanguageUtil.getString(this, R.string.fragment_comic_info_yishoucang));
                EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                EventBus.getDefault().post(new RefreashComicInfoActivity(true));
                break;
            case R.id.activity_comiclook_dingbu:
                activity_comiclook_RecyclerView.scrollToPosition(0);
                break;
            case R.id.activity_comiclook_danmu_layout:
                if (IS_OPEN_DANMU(activity)) {
                    SET_OPEN_DANMU(activity, false);
                    activity_comiclook_danmu_img.setImageResource(R.mipmap.comic_danmu_no);
                    if (comicChapterCatalogAdapter != null) {
                        for (DanmuRelativeLayout relativeLayout : comicChapterCatalogAdapter.relativeLayoutsDanmu) {
                            relativeLayout.setVisibility(View.GONE);
                        }
                    }
                } else {
                    SET_OPEN_DANMU(activity, true);
                    activity_comiclook_danmu_img.setImageResource(R.mipmap.comic_danmu);
                    if (comicChapterCatalogAdapter != null) {
                        for (RelativeLayout relativeLayout : comicChapterCatalogAdapter.relativeLayoutsDanmu) {
                            relativeLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
                break;
            case R.id.activity_comiclook_danmu_fashe:
                String danmutext = activity_comiclook_danmu_edit.getText().toString();
                if (TextUtils.isEmpty(danmutext) || Pattern.matches("\\s*", danmutext)) {
                    MyToash.ToashError(activity, LanguageUtil.getString(this, R.string.CommentListActivity_some));
                    return;
                }
                if (!CommentFlag) {
                    if (baseComicImage != null) {
                        BaseComicImage.Tucao tucao = new BaseComicImage.Tucao(danmutext);
                        if (baseComicImage.tucao == null) {
                            baseComicImage.tucao = new ArrayList<BaseComicImage.Tucao>();
                        }
                        baseComicImage.tucao.add(tucao);
                        if (IS_OPEN_DANMU(activity)) {
                            try {
                                DanmuRelativeLayout item_comic_recyclerview_danmu = comicChapterCatalogAdapter.relativeLayoutsDanmu.get(current_read_img_order);
                                if (item_comic_recyclerview_danmu.getPosition() == current_read_img_order) {
                                    item_comic_recyclerview_danmu.setVisibility(View.VISIBLE);
                                    TextView textView2 = new TextView(activity);
                                    textView2.setTextColor(Color.WHITE);
                                    textView2.setText(tucao.content);
                                    textView2.setPadding(8, 4, 8, 4);
                                    GradientDrawable drawable2 = new GradientDrawable();
                                    drawable2.setColor(tocao_bgcolor);
                                    drawable2.setCornerRadius(20);
                                    textView2.setBackgroundDrawable(drawable2);
                                    RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                                    int x = (int) (Math.random() * WIDTH);
                                    int y = (int) (Math.random() * (WIDTH * baseComicImage.height / baseComicImage.width));
                                    layoutParams2.setMargins(x, y, 0, 0);
                                    layoutParams2.width = ViewGroup.LayoutParams.WRAP_CONTENT;
                                    item_comic_recyclerview_danmu.addView(textView2, layoutParams2);
                                }
                            } catch (Exception E) {
                            }
                        }
                        sendTucao(baseComicImage.image_id, danmutext, false);
                    }
                } else {
                    ComicCommentActivity.sendComment(activity, comic_id, danmutext, new ComicCommentActivity.SendSuccess() {
                        @Override
                        public void Success(String result) {
                            if (result != null && !result.equals("315")) {
                                ++total_count;
                            }
                            activity_comiclook_pinglunshu.setText(total_count + "");
                        }
                    });
                }
                activity_comiclook_danmu_edit.setText("");
                //键盘收起
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                showMenu(false);
                break;
            case R.id.activity_comiclook_xiayihua_layout:
                if (mIsShowChapterAd) {
                    initChapterAd();
                } else {
                    if (comicChapterItem != null && !comicChapterItem.next_chapter.equals("0")) {
                        resetSaData(LanguageUtil.getString(activity, R.string.refer_page_next_chapter));
                        getData(activity, comic_id, comicChapterItem.next_chapter, true);
                    } else {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_end));
                    }
                }
                break;
            case R.id.activity_comiclook_shangyihua_layout:
                if (mIsShowChapterAd) {
                    initChapterAd();
                } else {
                    if (comicChapterItem != null && !comicChapterItem.last_chapter.equals("0")) {
                        resetSaData(LanguageUtil.getString(activity, R.string.refer_page_previous_chapter));
                        getData(activity, comic_id, comicChapterItem.last_chapter, true);
                    } else {
                        if (mChapterBaseAd != null && !mIsShowChapter) {
                            getData(activity, comic_id, comicChapterItem.chapter_id, true);
                            mIsShowChapter = true;
                        } else {
                            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_start));
                            mIsShowChapter = false;
                        }
                    }
                }
                break;
            case R.id.activity_comiclook_tucao_layout:
                Intent intent1 = new Intent(activity, ComicCommentActivity.class).putExtra("comic_id", comic_id).putExtra("IsBook", false);
                startActivityForResult(intent1, 111);
                overridePendingTransition(R.anim.activity_open, 0);
                showMenu(false);
                break;
            case R.id.activity_comiclook_share:
                String url = ReaderConfig.getBaseUrl() + "/site/share?uid=" + Utils.getUID(activity) + "&comic_id=" + comic_id + "&osType=2&product=1";
                UMWeb web = new UMWeb(url);
                web.setTitle(baseComic.getName());//标题
                web.setThumb(new UMImage(activity, baseComic.getVertical_cover()));  //缩略图
                web.setDescription(baseComic.getDescription());//描述
                MyShare.Share(activity, "", web);
                showMenu(false);
                break;
            case R.id.activity_comiclook_set:
                LookComicSetDialog.getLookComicSetDialog(activity);
                LookComicSetDialog.setmOnBackSmallTypeListener(new LookComicSetDialog.OnBackSmallTypeListener() {
                    @Override
                    public void backSmallType(boolean isOn) {
                        if (isOn) {
                            if (comicChapterCatalogAdapter != null) {
                                comicChapterCatalogAdapter.setmSmall(3);
                                mRlSmall.setVisibility(View.VISIBLE);
                                mRlRb.setVisibility(View.VISIBLE);
                                mImgBigBack.setVisibility(View.GONE);
                                activity_comiclook_RecyclerView.setBackgroundColor(getResources().getColor(R.color.color_0e0705));
                                mRbMid.setChecked(false);
                                mRbBig.setChecked(false);
                                mRbSmall.setChecked(true);
                                activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                                mIsSmall = true;
                                activity_comiclook_RecyclerView.setEnableScale(false);
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                        } else {
                            if (comicChapterCatalogAdapter != null) {
                                comicChapterCatalogAdapter.setmSmall(0);
                                mRlSmall.setVisibility(View.GONE);
                                activity_comiclook_RecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
                                activity_comiclook_RecyclerView.setEnableScale(true);
                                mIsSmall = false;
                                comicChapterCatalogAdapter.notifyDataSetChanged();
                            }
                        }
                        mSvBig.setVisibility(View.GONE);
                    }
                });
                showMenu(false);
                break;
            case R.id.activity_comiclook_xiazai:
                if (App.isVip(activity)) {
                    baseComic.saveIsexist(false);
                    Intent intentxiazai = new Intent(activity, ComicDownActivity.class);
                    intentxiazai.putExtra("baseComic", baseComic);
                    startActivity(intentxiazai);
                } else {
                    MyToash.Toash(activity, getString(R.string.down_toast_msg));
                }
                break;
            case R.id.activity_comiclook_danmu_img2:
                CommentFlag = !CommentFlag;
                if (!CommentFlag) {
                    activity_comiclook_danmu_img2.setImageResource(R.mipmap.comic_tan);
                    activity_comiclook_danmu_edit.setHint(LanguageUtil.getString(activity, R.string.ComicLookActivity_fadanmu));
                    activity_comiclook_danmu_fashe.setText(LanguageUtil.getString(activity, R.string.ComicLookActivity_fashe));
                } else {
                    activity_comiclook_danmu_img2.setImageResource(R.mipmap.ic_comment);
                    activity_comiclook_danmu_edit.setHint(LanguageUtil.getString(activity, R.string.ComicLookActivity_fapinglun));
                    activity_comiclook_danmu_fashe.setText(LanguageUtil.getString(activity, R.string.ComicLookActivity_fasong));
                }
                break;
            case R.id.activity_comiclook_quanji:
                Intent intent = new Intent(activity, ComicinfoMuluActivity.class);
                intent.putExtra("comic_id", comic_id);
                intent.putExtra("currentChapter_id", Chapter_id);
                startActivityForResult(intent, 222);
                showMenu(false);
                break;
            case R.id.comic_rb_big:
                if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
                    if (comicChapterCatalogAdapter != null) {
                        comicChapterCatalogAdapter.setmSmall(1);
                        mRbMid.setChecked(false);
                        mRbBig.setChecked(true);
                        mRbSmall.setChecked(false);
                        activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                        comicChapterCatalogAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.comic_rb_mid:
                if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
                    if (comicChapterCatalogAdapter != null) {
                        comicChapterCatalogAdapter.setmSmall(2);
                        mRbMid.setChecked(true);
                        mRbBig.setChecked(false);
                        mRbSmall.setChecked(false);
                        activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                        comicChapterCatalogAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.comic_rb_small:
                if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
                    if (comicChapterCatalogAdapter != null) {
                        comicChapterCatalogAdapter.setmSmall(3);
                        mRbMid.setChecked(false);
                        mRbBig.setChecked(false);
                        mRbSmall.setChecked(true);
                        activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                        comicChapterCatalogAdapter.notifyDataSetChanged();
                    }
                }
                break;
            case R.id.comic_big_back:
                mImgBig.setVisibility(View.GONE);
                mImgBigBack.setVisibility(View.GONE);
                mRlRb.setVisibility(View.VISIBLE);
                mSvBig.setVisibility(View.GONE);
                break;
            case R.id.rl_big:
                showMenu(false);
                break;
        }
    }

    public interface ItemOnclick {
        void onClick(int position, BaseComicImage baseComicImage);
    }

    private ItemOnclick itemOnclick = new ItemOnclick() {
        @Override
        public void onClick(int position, BaseComicImage baseComicImagee) {
            current_read_img_order = position;
            baseComicImage = baseComicImagee;
            int i = comicChapterCatalogAdapter.getmSmall();
            if (i == 2 || i == 3) {
                mImgBig.setVisibility(View.VISIBLE);
                mImgBigBack.setVisibility(View.VISIBLE);
                mRlRb.setVisibility(View.GONE);
                mSvBig.setVisibility(View.VISIBLE);
                MyPicasso.GlideImageNoSize(activity, baseComicImagee.getImage(), mImgBig);
            }
        }
    };
    boolean isclickScreen = true;
    private ZoomRecyclerView.OnTouchListener onTouchListener = new ZoomRecyclerView.OnTouchListener() {
        @Override
        public void clickScreen(float x, float y, float RawY) {
            if (!isclickScreen) {
                isclickScreen = true;
                return;
            }
            MyToash.Log("clickScreen", HEIGHT + "  " + y + "  " + RawY);
            if (y <= HEIGHT / 3) {
                if (AppPrefs.getSharedBoolean(activity, "fanye_ToggleButton", true)) {
                    if (!mIsSmall) {
                        activity_comiclook_RecyclerView.smoothScrollBy(0, -800);
                    }
                    showMenu(false);
                }
            } else if (y <= HEIGHT * 2 / 3) {
                showMenu(!MenuSHOW);
            } else {
                if (AppPrefs.getSharedBoolean(activity, "fanye_ToggleButton", true)) {
                    if (!mIsSmall) {
                        activity_comiclook_RecyclerView.smoothScrollBy(0, 800);
                    }
                    showMenu(false);
                }
            }
        }
    };

    public void showMenu(boolean VISIBLE) {
        if (VISIBLE) {
            if (activity_comiclook_head.getVisibility() == View.GONE) {
                MenuSHOW = true;
                activity_comiclook_head.setVisibility(View.VISIBLE);
                activity_comiclook_foot.setVisibility(View.VISIBLE);
                activity_comiclook_danmu_layout.setVisibility(View.VISIBLE);
                fragment_comicinfo_mulu_dangqian_layout.setVisibility(View.VISIBLE);
                showComicGuide();
            }
        } else {
            if (activity_comiclook_head.getVisibility() == View.VISIBLE) {
                activity_comiclook_danmu_layout.setVisibility(View.GONE);
                fragment_comicinfo_mulu_dangqian_layout.setVisibility(View.GONE);
                MenuSHOW = false;
                activity_comiclook_head.setVisibility(View.GONE);
                activity_comiclook_foot.setVisibility(View.GONE);
            }
        }
    }

    private void showComicGuide() {
        NewbieGuide.with(activity)
                .setLabel("guideComicOpen")
                .setShowCounts(1)//控制次数
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(mRlFootSet)
                        .setLayoutRes(R.layout.comic_look_guide, R.id.img_know)
                        .setEverywhereCancelable(false))
//                .addGuidePage(GuidePage.newInstance()
//                        .setLayoutRes(R.layout.comic_look_guide_two, R.id.img_know)
//                        .setEverywhereCancelable(false))
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EventBus.getDefault().register(this);
            mReadStarTime = System.currentTimeMillis();
            getWebViewAD(activity);
            getChapterAD(activity);
            getTopAD(activity);
            initViews();
            showMenu(false);
            if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
                mRlSmall.setVisibility(View.VISIBLE);
                mRlRb.setVisibility(View.VISIBLE);
                mImgBigBack.setVisibility(View.GONE);
                mRbMid.setChecked(false);
                mRbBig.setChecked(false);
                mRbSmall.setChecked(true);
                mIsSmall = true;
                activity_comiclook_RecyclerView.setBackgroundColor(getResources().getColor(R.color.color_0e0705));
                activity_comiclook_RecyclerView.setEnableScale(false);
            } else {
                mRlSmall.setVisibility(View.GONE);
                activity_comiclook_RecyclerView.setBackgroundColor(getResources().getColor(R.color.white));
                activity_comiclook_RecyclerView.setEnableScale(true);
                mIsSmall = false;
            }
            Intent intent = getIntent();
            tocao_bgcolor = Color.parseColor("#4d000000");
            baseComic = (BaseComic) intent.getSerializableExtra(BASE_COMIC_EXT_KAY);
            comic_id = baseComic.getComic_id();
            String current_chapter_id = baseComic.getCurrent_chapter_id();
            if (current_chapter_id != null && !TextUtils.isEmpty(current_chapter_id)) {
                Chapter_id = current_chapter_id;
            }
            comicChapter = new ArrayList<>();
            FORM_INFO = intent.getBooleanExtra(FORM_INFO_EXT_KAY, false);//是否是漫画详情界面过来的
            FORM_READHISTORY = intent.getBooleanExtra(FORM_READHISTORY_EXT_KAY, false);//阅读历史过来的 需要校验 服务端和本地记录
            mReferPage = intent.getStringExtra(REFER_PAGE_EXT_KAY);
            setOpenCurrentTime();
            if (FORM_READHISTORY) {
                baseComicLocal = LitePal.where("comic_id = ?", comic_id).findFirst(BaseComic.class);
                if (baseComicLocal != null) {
                    baseComic.setAddBookSelf(baseComicLocal.isAddBookSelf());
                    baseComic.setChapter_text(baseComicLocal.getChapter_text());
                    baseComic.setId(baseComicLocal.getId());
                } else {
                    baseComic.setAddBookSelf(false);
                    baseComic.saveIsexist(false);
                }
            }
            ComicinfoMuluFragment.GetCOMIC_catalog(activity, comic_id, new ComicinfoMuluFragment.GetCOMIC_catalogList() {
                @Override
                public void GetCOMIC_catalogList(List<ComicChapter> comicChapterList) {
                    if (comicChapterList != null && !comicChapterList.isEmpty() && comicChapterList.size() > 0) {
                        for (int i = 0; i < comicChapterList.size(); i++) {
                            ComicChapter comicChapterTemp = comicChapterList.get(i);
                            if (comicChapterTemp.getAd_image() == null) {
                                comicChapter.add(comicChapterTemp);
                            }
                        }
                        try {
                            initData();
                        } catch (Exception E) {
                        }
                    } else {
                        MyToash.ToashError(activity, "章节还在更新中~");
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                finish();
                            }
                        }.sendEmptyMessageDelayed(0, 1500);
                    }
                }
            });
            httpDataComicInfo();
            SensorsDataAPI.sharedInstance().trackTimerStart(SaEventConfig.MH_CONTENT_PAGE_EVENT);
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    private void initData() {
        if (comicChapter != null && comicChapter.size() > 0) {
            ComicChapterSize = comicChapter.size();
            if (TextUtils.isEmpty(Chapter_id)) {
                ComicChapter currentComicChapter = getCurrentComicChapter(0);
                if (currentComicChapter != null) {
                    Chapter_id = currentComicChapter.getChapter_id();
                }
            }
            CurrentComicChapter = getCurrentComicChapter(Chapter_id);
            if (CurrentComicChapter != null) {
                current_read_img_order = CurrentComicChapter.current_read_img_order;//本章最近阅读图片
            }
        }
        if (baseComic.isAddBookSelf()) {
            activity_comiclook_shoucang.setVisibility(View.GONE);
        }
        if (baseComicImages == null) {
            baseComicImages = new ArrayList<>();
        }
        getData(activity, comic_id, Chapter_id, true);
    }

    public void initViews() {
        Glide.with(activity).load(R.drawable.bianfu).format(DecodeFormat.PREFER_ARGB_8888).into(activity_comiclook_lording_img);
        HEIGHT = ScreenSizeUtils.getInstance(activity).getScreenHeight();
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        initRecyclerview();
        item_dialog_downadapter_RotationLoadingView.setIndicator(new LineSpinFadeLoaderIndicator());
        if (AppPrefs.getSharedBoolean(activity, "yejian_ToggleButton", false)) {
            BrightnessUtil.setBrightness(this, 0);
        }
    }

    private void initRecyclerview() {
        activity_comic_look_foot = LayoutInflater.from(this).inflate(R.layout.activity_comic_look_foot, null);
        holderFoot = new MyViewHolder(activity_comic_look_foot);
        linearLayoutManager = new MyContentLinearLayoutManager(activity);
        activity_comiclook_RecyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.RecycledViewPool recycledViewPool = activity_comiclook_RecyclerView.getRecycledViewPool();
        //图片不加载，滑动后图片恢复的解决办法
        if (recycledViewPool != null) {
            recycledViewPool.setMaxRecycledViews(0, 10);
            activity_comiclook_RecyclerView.setRecycledViewPool(recycledViewPool);
        }
        activity_comiclook_RecyclerView.setTouchListener(onTouchListener);
        activity_comiclook_RecyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                activity_comiclook_RecyclerView.stopScroll();
            }
        });
        activity_comiclook_RecyclerView.setScrollViewListener(new ZoomRecyclerView.ScrollViewListener() {
            @Override
            public void onScroll(int FirstCompletelyVisibleItemPosition, int FirstVisibleItemPosition, int LastCompletelyVisibleItemPosition, int LastVisibleItemPosition) {
                try {
                    if (!first) {
                        int current_read_img_orderr = (FirstCompletelyVisibleItemPosition <= 0) ? FirstVisibleItemPosition : FirstCompletelyVisibleItemPosition;
                        if (current_read_img_orderr <= baseComicImagesSize) {
                            current_read_img_order = current_read_img_orderr;
                            baseComicImage = baseComicImages.get(current_read_img_order);
                        }
                        Last_read_img_order = (LastCompletelyVisibleItemPosition <= 0) ? LastVisibleItemPosition : LastCompletelyVisibleItemPosition;
                        if (Last_read_img_order == baseComicImagesSize) {
                            showMenu(true);
                        }
                        showMenu(false);
                    } else {
                        first = false;
                    }
                } catch (Exception e) {
                }

            }
        });
    }

    public void getData(Activity activity, String comic_id, String chapter_id, boolean HandleData) {
        if (!"0".equals(chapter_id) && !StringUtils.isEmpty(chapter_id)) {
            MyToash.Log("COMIC_chapter", "COMIC_chapter");
            if (HandleData) {
                item_dialog_downadapter_RotationLoadingView.setVisibility(View.VISIBLE);
                activity_comiclook_danmu_dangqianhua.setVisibility(View.GONE);
            }
            ComicChapterItem comicChapterItem = map.get(chapter_id);
            if (comicChapterItem != null && comicChapterItem.is_preview == 0) {
                if (HandleData) {
                    HandleData(comicChapterItem, chapter_id, comic_id, activity);
                }
                return;
            }
            final ReaderParams params = new ReaderParams(this);
            params.putExtraParams("comic_id", comic_id);
            if (chapter_id != null) {
                params.putExtraParams("chapter_id", chapter_id);
            }
            String json = params.generateParamsJson();
            HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_chapter, json, false, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            updateRecord();
                            if (HandleData) {
                                ComicChapterItem comicChapterItem = gson.fromJson(result, ComicChapterItem.class);
                                HandleData(comicChapterItem, chapter_id, comic_id, activity);
                                map.put(chapter_id, comicChapterItem);
                                CurrentComicChapter.setImagesText(result);
                                ContentValues values = new ContentValues();
                                values.put("ImagesText", result);
                                values.put("IsRead", true);
                                LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                            } else {
                                ComicChapterItem comicChapterItem = gson.fromJson(result, ComicChapterItem.class);
                                map.put(chapter_id, comicChapterItem);
                                ComicChapter CurrentComicChapter = getCurrentComicChapter(chapter_id);
                                if (CurrentComicChapter != null) {
                                    CurrentComicChapter.setImagesText(result);
                                    ContentValues values = new ContentValues();
                                    values.put("ImagesText", result);
                                    LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                                }
                            }
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                            SensorsDataHelper.setMHFailEvent(ex);
                            if (ex != null) {
                                if (ex.equals("nonet")) {
                                    String s = getCurrentComicChapter(chapter_id).ImagesText;
                                    if (s != null) {
                                        MyToash.Log("nonet", s);
                                        ComicChapterItem comicChapterItem = gson.fromJson(s, ComicChapterItem.class);
                                        if (HandleData) {
                                            HandleData(comicChapterItem, chapter_id, comic_id, activity);
                                        }
                                        map.put(chapter_id, comicChapterItem);
                                    }
                                }
                            }
                        }
                    }
            );
        } else {
            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
        }
    }

    private void updateRecord() {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_info_read_record, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private ComicChapter getCurrentComicChapter(int comicChapterSize) {
        ComicChapter comicChaptertemp = null;
        if (comicChapter != null && ComicChapterSize > 0) {
            if (comicChapterSize < ComicChapterSize && comicChapter.size() > comicChapterSize) {
                comicChaptertemp = comicChapter.get(comicChapterSize);
            } else {
                comicChaptertemp = comicChapter.get(0);
            }
        }
        return comicChaptertemp;
    }

    private ComicChapter getCurrentComicChapter(String chapter_id) {
        for (ComicChapter comicChapterr : comicChapter) {
            if (comicChapterr.chapter_id.equals(chapter_id)) {
                return comicChapterr;
            }
        }
        return null;
    }

    private void initChapterAd() {
        if (mChapterBaseAd != null) {
            titlebar_text.setText("");
            baseComicImages.clear();
            BaseComicImage baseComicImage = new BaseComicImage();
            baseComicImagesSize = 0;
            baseComicImage.setAd(1);
            baseComicImage.setAd_skip_url(mChapterBaseAd.getAd_skip_url());
            baseComicImage.setImage(mChapterBaseAd.getAd_image());
            baseComicImage.setAd_type(mChapterBaseAd.getAd_type());
            baseComicImage.setAd_url_type(mChapterBaseAd.getAd_url_type());
            if (!TextUtils.isEmpty(mChapterBaseAd.getAdId())) {
                baseComicImage.setAdId(mChapterBaseAd.getAdId());
                baseComicImage.setRequestId(mChapterBaseAd.getRequestId());
                baseComicImage.setAdPosId(mChapterBaseAd.getAdPosId());
            }

            baseComicImages.add(0, baseComicImage);
            ++baseComicImagesSize;
            comicChapterCatalogAdapter.setmIsAlbum(TextUtils.equals(comicChapterItem.getIs_album(), "2"));
            comicChapterCatalogAdapter.NotifyDataSetChanged(baseComicImagesSize);
            linearLayoutManager.scrollToPositionWithOffset(0, 0);
            mIsShowChapterAd = false;
        }
    }

    private void HandleData(ComicChapterItem comicChapterItem, String chapter_id, String comic_id, Activity activity) {
        try {
            this.comicChapterItem = comicChapterItem;
            if (comicChapterItem != null && !comicChapterItem.image_list.isEmpty()) {
                checkIsCoupon(comicChapterItem);
                setBigImageImageLoader(comicChapterItem.image_list.get(0));
                titlebar_text.setText(comicChapterItem.chapter_title);
                Chapter_title = comicChapterItem.chapter_title;
                CurrentComicChapter = getCurrentComicChapter(comicChapterItem.chapter_id);
                Chapter_id = comicChapterItem.chapter_id;
                if (comicChapterItem.is_preview == 1) {
                    getBuy();
                }
                baseComic.setCurrent_chapter_name(Chapter_title);
                baseComic.setCurrent_chapter_id(Chapter_id);
                baseComicImages.clear();
                baseComicImagesSize = 0;
                for (BaseComicImage baseComicImage : comicChapterItem.image_list) {
                    baseComicImage.chapter_id = chapter_id;
                    baseComicImage.comic_id = comic_id;
                    ++baseComicImagesSize;
                }
                baseComicImages.addAll(comicChapterItem.image_list);
                if (mChapterBaseAd != null) {
                    mIsShowChapterAd = true;
                }
                if (first) {
                    comicChapterCatalogAdapter = new ComicRecyclerViewAdapter(activity, WIDTH, HEIGHT, baseComicImages, activity_comic_look_foot, baseComicImagesSize, itemOnclick);
                    comicChapterCatalogAdapter.setmIsAlbum(TextUtils.equals(comicChapterItem.getIs_album(), "2"));
                    activity_comiclook_RecyclerView.setAdapter(comicChapterCatalogAdapter);
                    if (AppPrefs.getSharedBoolean(activity, "small_ToggleButton", false)) {
                        comicChapterCatalogAdapter.setmSmall(3);//默认开启小图模式
                    }
                    activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                    activity_comiclook_lording.setVisibility(View.GONE);
                    if (comicChapterItem.total_comment != 0) {
                        total_count = comicChapterItem.total_comment;
                        activity_comiclook_pinglunshu.setVisibility(View.VISIBLE);
                        activity_comiclook_pinglunshu.setText(comicChapterItem.total_comment + "");
                    } else {
                        activity_comiclook_pinglunshu.setVisibility(View.GONE);
                    }
                } else {
                    comicChapterCatalogAdapter.setmIsAlbum(TextUtils.equals(comicChapterItem.getIs_album(), "2"));
                    comicChapterCatalogAdapter.NotifyDataSetChanged(baseComicImagesSize);
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                }
                //更新该本书的阅读记录
                ContentValues values = new ContentValues();
                values.put("current_chapter_id", chapter_id);
                values.put("current_chapter_name", Chapter_title);
                LitePal.update(BaseComic.class, values, baseComic.getId());
                EventBus.getDefault().post(baseComic);
                if (baseComic.isAddBookSelf()) {
                    EventBus.getDefault().post(new RefreshComic(baseComic, 2));
                }
                //更新数据库该章节已读
                ContentValues CurrentComicChapterIsRead = new ContentValues();
                CurrentComicChapterIsRead.put("IsRead", true);
                LitePal.updateAsync(ComicChapter.class, CurrentComicChapterIsRead, CurrentComicChapter.getId());
                CurrentComicChapter.IsRead = true;
                item_dialog_downadapter_RotationLoadingView.setVisibility(View.GONE);
                activity_comiclook_danmu_dangqianhua.setVisibility(View.VISIBLE);
                if (comicChapterItem.next_chapter.equals("0") && mChapterBaseAd == null) {
                    holderFoot.activity_comiclook_xiayihua_foot.setImageResource(R.mipmap.right_gray);
                    activity_comiclook_xiayihua.setImageResource(R.mipmap.right_gray);
                } else {
                    holderFoot.activity_comiclook_xiayihua_foot.setImageResource(R.mipmap.right_black);
                    activity_comiclook_xiayihua.setImageResource(R.mipmap.right_black);
                    if (comicChapterItem.is_preview == 0) {
                        getData(activity, comic_id, comicChapterItem.next_chapter, false);
                    }
                }
                if (comicChapterItem.last_chapter.equals("0")) {
                    holderFoot.activity_comiclook_shangyihua_foot.setImageResource(R.mipmap.left_gray);
                    activity_comiclook_shangyihua.setImageResource(R.mipmap.left_gray);
                } else {
                    holderFoot.activity_comiclook_shangyihua_foot.setImageResource(R.mipmap.left_black);
                    activity_comiclook_shangyihua.setImageResource(R.mipmap.left_black);
                    if (comicChapterItem.is_preview == 0) {
                        getData(activity, comic_id, comicChapterItem.next_chapter, false);
                    }
                }
                ComicReadHistory.addReadHistory(FORM_READHISTORY, activity, comic_id, chapter_id);
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    private void initTopAd(ComicChapterItem comicChapterItem) {
        if (mIsSdkAd) {
            if (mSdkTopAd != null) {
                mImgTopAd.setVisibility(View.VISIBLE);
                MyPicasso.GlideImageNoSize(activity, mSdkTopAd.getAd_image(), mImgTopAd);
                mIsSipAd = true;
                mImgTopAd.setOnClickListener((v) -> skipWeb(mSdkTopAd, activity));
            } else {
                mImgTopAd.setVisibility(View.GONE);
            }
        } else {
            BaseAd advert = comicChapterItem.getAdvert();
            if (advert != null) {
                mImgTopAd.setVisibility(View.VISIBLE);
                MyPicasso.GlideImageNoSize(activity, mSdkTopAd.getAd_image(), mImgTopAd);
                mIsSipAd = true;
                mImgTopAd.setOnClickListener((v) -> skipWeb(advert, activity));
            } else {
                mImgTopAd.setVisibility(View.GONE);
            }
        }
    }

    public void sendTucao(String image_id, String content, boolean CommentFlag) {
        if (!MainHttpTask.getInstance().Gotologin(activity)) {
            return;
        }
        setMHDMEvent(image_id, Chapter_id, comic_id);
        final ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("chapter_id", Chapter_id);
        params.putExtraParams("image_id", image_id);
        params.putExtraParams("content", content);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_tucao, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * 获取该本漫画详情
     */
    private void httpDataComicInfo() {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_info, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            MyToash.LogE("httpDataComicInfo", result);
                            ComicInfo comicInfo = gson.fromJson(result, ComicInfo.class);
                            if (comicInfo != null) {
                                mComicInfo = comicInfo;
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            int total_countt = data.getIntExtra("total_count", 0);
            total_count = total_countt;
            activity_comiclook_pinglunshu.setText(total_count + "");
        } else if (requestCode == 222) {
            if (resultCode == 222) {
                if (dialogVipPop != null) {
                    dialogVipPop.dismiss();
                }
                Chapter_id = data.getStringExtra("currentChapter_id");
                getData(activity, comic_id, Chapter_id, true);
            }
        }
    }

    @Override
    protected void onPause() {
        long mReadEndTime = System.currentTimeMillis();
        long readTime = (mReadEndTime - mReadStarTime) / 1000 / 60;
        if (readTime >= 1 && Utils.isLogin(this) && !isNeedToAdvertisement) {
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
        isNeedToAdvertisement = !isNeedToAdvertisement;
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mReadStarTime = System.currentTimeMillis();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isActive && !mIsSipAd) {
            initData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(ComicChapterEventbus comicChapterte) {//更新当前目录集合的 最近阅读图片记录
        ComicChapter comicChaptert = comicChapterte.comicChapter;
        ComicChapter c = comicChapter.get(comicChaptert.display_order);
        switch (comicChapterte.Flag) {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshComicChapterList(BuyLoginSuccessEvent buyLoginSuccessEvent) {//
        if (purchaseDialog != null && purchaseDialog.isShowing()) {
            purchaseDialog.dismiss();
            getData(activity, comic_id, Chapter_id, true);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            try {
                ContentValues values = new ContentValues();
                values.put("current_read_img_order", current_read_img_order);
                LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                if (FORM_INFO) {
                    CurrentComicChapter.current_read_img_order = current_read_img_order;
                    EventBus.getDefault().post(new ComicChapterEventbus(0, CurrentComicChapter));//更新上一界面的 数据
                }
            } catch (Exception e) {
            }
            askIsNeedToAddShelf();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getWebViewAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "8") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkBottomAd = true;
                sdkBottomAd();
                return;
            }
        }
        if (!mIsSdkBottomAd) {
            localBotoomAd(activity);
        }
    }

    private void getTopAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "13") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkAd = true;
                sdkAd(activity);
                return;
            }
        }
        if (!mIsSdkAd) {
            localTopAd(activity);
        }
    }

    private void getChapterAD(Activity activity) {
        for (int i = 0; i < ReaderConfig.COMIC_SDK_AD.size(); i++) {
            AppUpdate.ListBean listBean = ReaderConfig.COMIC_SDK_AD.get(i);
            if (TextUtils.equals(listBean.getPosition(), "18") && TextUtils.equals(listBean.getSdk_switch(), "2")) {
                mIsSdkChapterAd = true;
                sdkChapterAd();
                return;
            }
        }
        if (!mIsSdkChapterAd) {
            localChapterAd(activity);
        }
    }

    private void sdkAd(Activity activity) {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_TOP_DEEBUG : BuildConfig.XAD_EVN_POS_COMIC_TOP, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    getData(activity, comic_id, Chapter_id, true);
                    AdInfo adInfo = list.get(0);
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        mSdkTopAd = new BaseAd();
                        mSdkTopAd.setAdId(adInfo.getAdId());
                        mSdkTopAd.setAdPosId(adInfo.getAdPosId());
                        mSdkTopAd.setRequestId(adInfo.getRequestId());
                        mSdkTopAd.setAd_image(adInfo.getMaterial().getImageUrl());
                        mSdkTopAd.setAd_skip_url(adInfo.getOperation().getValue());
                        mSdkTopAd.setAd_title(adInfo.getMaterial().getTitle());
                        mSdkTopAd.setUser_parame_need("1");
                        mSdkTopAd.setAd_url_type(adInfo.getOperation().getType());
                        mSdkTopAd.setAd_type(1);
                        if (mSdkTopAd != null) {
                            mImgTopAd.setVisibility(View.VISIBLE);
                            MyPicasso.GlideImageNoSize(activity, mSdkTopAd.getAd_image(), mImgTopAd);
                            mIsSipAd = true;
                            mImgTopAd.setOnClickListener((v) -> skipWeb(mSdkTopAd, activity));
                        } else {
                            mImgTopAd.setVisibility(View.GONE);
                        }
                    } else {
                        localTopAd(activity);
                    }
                } catch (Exception e) {
                    localTopAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localTopAd(activity);
            }
        });
    }

    private void localTopAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", MANHAU + "");
        params.putExtraParams("position", "13");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            BaseAd baseAd = gson.fromJson(result, BaseAd.class);
                            if (baseAd != null) {
                                mImgTopAd.setVisibility(View.VISIBLE);
                                MyPicasso.GlideImageNoSize(activity, baseAd.getAd_image(), mImgTopAd);
                                mIsSipAd = true;
                                mImgTopAd.setOnClickListener((v) -> skipWeb(baseAd, activity));
                            } else {
                                mImgTopAd.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            mImgTopAd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mImgTopAd.setVisibility(View.GONE);
                    }
                }
        );
    }

    private void localChapterAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", MANHAU + "");
        params.putExtraParams("position", "18");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            mChapterBaseAd = gson.fromJson(result, BaseAd.class);
                            mIsShowChapterAd = true;
                        } catch (Exception e) {
                            mIsShowChapterAd = false;
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mIsShowChapterAd = false;
                    }
                }
        );
    }

    private void sdkChapterAd() {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_CHAPTER_INTERVAL_DEBUG : BuildConfig.XAD_EVN_POS_COMIC_CHAPTER_INTERVAL, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    if (mChapterBaseAd == null) {
                        mChapterBaseAd = new BaseAd();
                    }
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        mIsShowChapterAd = true;
                        mChapterBaseAd.setRequestId(adInfo.getRequestId());
                        mChapterBaseAd.setAdPosId(adInfo.getAdPosId());
                        mChapterBaseAd.setAdId(adInfo.getAdId());
                        mChapterBaseAd.setAd_skip_url(adInfo.getOperation().getValue());
                        mChapterBaseAd.setAd_title(adInfo.getMaterial().getTitle());
                        mChapterBaseAd.setAd_image(adInfo.getMaterial().getImageUrl());
                        mChapterBaseAd.setUser_parame_need("1");
                        mChapterBaseAd.setAd_url_type(adInfo.getOperation().getType());
                        mChapterBaseAd.setAd_type(1);
                    } else {
                        localChapterAd(activity);
                    }
                } catch (Exception e) {
                    localChapterAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localChapterAd(activity);
            }
        });
    }

    private void sdkBottomAd() {
        XRequestManager.INSTANCE.requestAd(activity, BuildConfig.DEBUG ? BuildConfig.XAD_EVN_POS_COMIC_END_DEBUG : BuildConfig.XAD_EVN_POS_COMIC_END, AdType.CUSTOM_TYPE_DEFAULT, 1, new XAdRequestListener() {
            @Override
            public void onRequestOk(List<AdInfo> list) {
                try {
                    AdInfo adInfo = list.get(0);
                    BaseAd baseAd = new BaseAd();
                    if (App.isShowSdkAd(activity, adInfo.getMaterial().getShowType())) {
                        baseAd.setAdId(adInfo.getAdId());
                        baseAd.setRequestId(adInfo.getRequestId());
                        baseAd.setAdPosId(adInfo.getAdPosId());
                        baseAd.setAd_skip_url(adInfo.getOperation().getValue());
                        baseAd.setAd_title(adInfo.getMaterial().getTitle());
                        baseAd.setAd_image(adInfo.getMaterial().getImageUrl());
                        baseAd.setUser_parame_need("1");
                        baseAd.setAd_url_type(adInfo.getOperation().getType());
                        baseAd.setAd_type(1);
                        if (baseAd != null) {
                            mImgBottomAd.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    skipWeb(baseAd, activity);
                                }
                            });
                            mImgBottomAd.setVisibility(View.VISIBLE);
                            MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, mImgBottomAd);
                        }
                    } else {
                        mImgBottomAd.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    localBotoomAd(activity);
                }
            }

            @Override
            public void onRequestFailed(int i, String s) {
                localBotoomAd(activity);
            }
        });
    }

    private void skipWeb(BaseAd baseAd, Activity activity) {
        if (!TextUtils.isEmpty(baseAd.getAdId())) {
            AdInfo adInfo = new AdInfo();
            adInfo.setAdId(baseAd.getAdId());
            adInfo.setAdPosId(baseAd.getAdPosId());
            adInfo.setAdPosId(baseAd.getRequestId());
            XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
        }
        Intent intent = new Intent();
        intent.setClass(activity, WebViewActivity.class);
        intent.putExtra("url", baseAd.ad_skip_url);
        intent.putExtra("title", baseAd.ad_title);
        intent.putExtra("advert_id", baseAd.advert_id);
        intent.putExtra("ad_url_type", baseAd.ad_url_type);
        activity.startActivity(intent);
    }


    private void localBotoomAd(Activity activity) {
        ReaderParams params = new ReaderParams(activity);
        String requestParams = ReaderConfig.getBaseUrl() + "/advert/info";
        params.putExtraParams("type", MANHAU + "");
        params.putExtraParams("position", "8");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            BaseAd baseAd = gson.fromJson(result, BaseAd.class);
                            if (baseAd != null) {
                                mImgBottomAd.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        skipWeb(baseAd, activity);
                                    }
                                });
                                MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, mImgBottomAd);
                                mImgBottomAd.setVisibility(View.VISIBLE);
                            } else {
                                mImgBottomAd.setVisibility(View.GONE);
                            }
                        } catch (Exception e) {
                            mImgBottomAd.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mImgBottomAd.setVisibility(View.GONE);
                    }
                }
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        map.clear();
        setMHContentPageEvent();
        SensorsDataAPI.sharedInstance().trackTimerEnd(SaEventConfig.MH_CONTENT_PAGE_EVENT);
    }

    class MyViewHolder {
        @BindView(R.id.activity_comic_look_foot_shangyihua)
        public LinearLayout activity_comic_look_foot_shangyihua;
        @BindView(R.id.activity_comic_look_foot_xiayihua)
        public LinearLayout activity_comic_look_foot_xiayihua;
        @BindView(R.id.activity_comiclook_shangyihua_foot)
        public ImageView activity_comiclook_shangyihua_foot;
        @BindView(R.id.activity_comiclook_xiayihua_foot)
        public ImageView activity_comiclook_xiayihua_foot;

        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(value = {R.id.activity_comic_look_foot_shangyihua, R.id.activity_comic_look_foot_xiayihua})
        public void getEvent(View view) {
            switch (view.getId()) {
                case R.id.activity_comic_look_foot_xiayihua:
                    isclickScreen = false;
                    if (mIsShowChapterAd) {
                        initChapterAd();
                    } else {
                        if (comicChapterItem != null && !comicChapterItem.next_chapter.equals("0")) {
                            mPageIndex++;
                            resetSaData(LanguageUtil.getString(activity, R.string.refer_page_next_chapter));
                            getData(activity, comic_id, comicChapterItem.next_chapter, true);
                        } else {
                            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_end));
                        }
                    }
                    break;
                case R.id.activity_comic_look_foot_shangyihua:
                    isclickScreen = false;
                    if (mPageIndex == 0) {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_start));
                        return;
                    }
                    if (mIsShowChapterAd) {
                        initChapterAd();
                    } else {
                        if (comicChapterItem != null && !comicChapterItem.last_chapter.equals("0")) {
                            mPageIndex--;
                            resetSaData(LanguageUtil.getString(activity, R.string.refer_page_previous_chapter));
                            getData(activity, comic_id, comicChapterItem.last_chapter, true);
                        } else {
                            if (mChapterBaseAd != null && !mIsShowChapter) {
                                mPageIndex--;
                                getData(activity, comic_id, comicChapterItem.chapter_id, true);
                                mIsShowChapter = true;
                            } else {
                                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_start));
                                mIsShowChapter = false;
                            }
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 询问是否加入书架
     */
    private void askIsNeedToAddShelf() {
        if (baseComic != null && baseComic.isAddBookSelf()) {
            finish();
            return;
        }
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_add_shelf, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        dialog.setContentView(view);
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
                finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                baseComic.saveIsexist(true);
                MyToash.ToashSuccess(activity, LanguageUtil.getString(getApplicationContext(), R.string.fragment_comic_info_yishoucang));
                EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                EventBus.getDefault().post(new RefreashComicInfoActivity(true));
                finish();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * 拿到数据后 据数据判断图片地址是否需要解密。(只在ComicLookActivity使用)
     *
     * @param bigImageImageLoader
     */
    void setBigImageImageLoader(BaseComicImage bigImageImageLoader) {
        String imageStr = bigImageImageLoader.getImage();
        setBigImageImageLoader(StringUtils.isImgeUrlEncryptPostfix(imageStr));
    }

    void setBigImageImageLoader(boolean isEncrype) {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        if (isEncrype) {
            BigImageViewer.initialize(GlideEncypeImageLoader.with(App.getAppContext(), okHttpClient));
        } else {
            BigImageViewer.initialize(GlideImageLoader.with(App.getAppContext(), okHttpClient));
        }
    }

    /**
     * 神策吐槽某一页漫画埋点
     *
     * @param page_id    该页漫画ID
     * @param chapter_id 漫画章节ID
     * @param work_id    漫画 ID
     */
    private void setMHDMEvent(String page_id, String chapter_id, String work_id) {
        try {
            SensorsDataHelper.setMHDMEvent(Integer.valueOf(page_id), Integer.valueOf(chapter_id), Integer.valueOf(work_id));
        } catch (Exception e) {
        }
    }

    /**
     * 神策漫画阅读页埋点
     */
    private void setMHContentPageEvent() {
        if (mComicInfo == null) {
            httpDataComicInfo();
            return;
        }
        SensorsDataHelper.setMHContentPageEvent(baseComicImagesSize,//当前章总页数
                Last_read_img_order,//当前章已读页数
                new Long(DateUtils.getCurrentTimeDifferenceSecond(mOpenCurrentTime)).intValue(),//停留时长
                getPropIdList(),//属性ID
                getTagIdList(),//分类ID
                mReferPage,//前向页面
                Integer.valueOf(comicChapterItem == null ? "0" : comicChapterItem.comic_id),//漫画ID
                Integer.valueOf(comicChapterItem == null ? "0" : comicChapterItem.chapter_id),//漫画当前章节ID
                mComicInfo == null ? 0 : mComicInfo.comic.total_chapters, //漫画总章节
                0);//作者ID
    }

    /**
     * 进入漫漶详情必传参数
     *
     * @param context
     * @param baseComic 漫画数据
     * @param referPage 神策埋点数据从哪个页面进入
     * @return Intent
     */
    public static Intent getMyIntent(Context context, BaseComic baseComic, String referPage) {
        Intent intent = new Intent(context, ComicLookActivity.class);
        intent.putExtra(ComicLookActivity.BASE_COMIC_EXT_KAY, baseComic);
        intent.putExtra(ComicLookActivity.REFER_PAGE_EXT_KAY, referPage);
        return intent;
    }

    /**
     * 点击上一章 下一章 触发埋点并重置埋点数据
     *
     * @param referPage
     */
    private void resetSaData(String referPage) {
        setMHContentPageEvent();
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
     * 神策埋点 获取prop_id属性数据
     */
    private List<String> getPropIdList() {
        return getPropIdList(mComicInfo);
    }

    public static List<String> getPropIdList(ComicInfo comicInfo) {
        List<String> propId = new ArrayList<>();
        if (comicInfo != null) {
            if ("1".equals(comicInfo.comic.is_hot)) {
                propId.add("热门");
            }
            if ("1".equals(comicInfo.comic.is_recommend)) {
                propId.add("推荐");
            }
        }
        return propId;
    }

    /**
     * 神策埋点 获取tag_id分类信息
     */
    private List<String> getTagIdList() {
        return getTagIdList(mComicInfo);
    }

    public static List<String> getTagIdList(ComicInfo comicInfo) {
        List<String> tagId = new ArrayList<>();
        if (comicInfo != null) {
            List<String> list = StringUtils.getStringToList(comicInfo.comic.sorts, ",");
            if (list != null) {
                tagId.addAll(list);
            }
        }
        return tagId;
    }

    private void checkIsVip(ComicChapterItem chapterItem) {
        String is_vip = chapterItem.getIs_vip();
        if (is_vip != null && is_vip.equals("1") && !App.isVip(activity)) {
            DialogVip dialogVip = new DialogVip();
            dialogVip.getDialogVipPop(activity, true);
            return;
        }
    }

    private void checkIsCoupon(ComicChapterItem chapterItem) {
        String is_book_coupon_pay = chapterItem.getIs_book_coupon_pay();
        String is_vip = chapterItem.getIs_vip();
        String is_limited_free = chapterItem.getIs_limited_free();
        if (!StringUtils.isEmpty(is_limited_free) && TextUtils.equals(is_limited_free, "1")) {
            return;
        }
        if (Utils.isLogin(activity)) {
            boolean isCoupon = false;
            if (chapterItem.isIs_buy_status()) {
                return;
            }
            if (is_book_coupon_pay != null && is_book_coupon_pay.equals("1")) {
                isCoupon = true;
            }
            if ((is_book_coupon_pay != null && is_book_coupon_pay.equals("1") || is_vip != null && is_vip.equals("1")) && !App.isVip(activity)) {
                if (Utils.isLogin(activity)) {
                    DialogComicLook dialogNovelCoupon = new DialogComicLook();
                    //开启自动解锁并需要书券时
                    if (AppPrefs.getSharedBoolean(activity, "comicOpen_ToggleButton", false) && isCoupon) {
                        int couponNum = AppPrefs.getSharedInt(activity, PrefConst.COUPON, 0);
                        String couponPrice = AppPrefs.getSharedString(activity, PrefConst.COUPON_COMICI_PRICE);
                        if (couponNum >= Integer.valueOf(couponPrice)) {
                            dialogNovelCoupon.openCoupon(activity, chapterItem, couponPrice, couponNum);
                        } else {
                            DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                            dialogCouponNotMore.getDialogVipPop(activity, true);
                        }
                    } else {
                        dialogVipPop = dialogNovelCoupon.getDialogVipPop(activity, chapterItem, baseComic, isCoupon);
                    }
                    dialogNovelCoupon.setOnOpenCouponListener(new DialogNovelCoupon.OnOpenCouponListener() {
                        @Override
                        public void onOpenCoupon(boolean isBuy) {
                            if (dialogVipPop != null) {
                                dialogVipPop.dismiss();
                            }
                            chapterItem.setIs_book_coupon_pay("0");
                        }
                    });
                }
            }
        } else {
            if (TextUtils.equals(is_book_coupon_pay, "1") || TextUtils.equals(is_vip, "1")) {
                DialogRegister dialogRegister = new DialogRegister();
                dialogRegister.setFinish(true);
                dialogRegister.getDialogLoginPop(activity);
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
    }
}