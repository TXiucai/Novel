package com.heiheilianzai.app.ui.activity.comic;

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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.github.piasy.biv.BigImageViewer;
import com.github.piasy.biv.loader.glide.GlideImageLoader;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicRecyclerViewAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.BaseComicImage;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.ComicChapterItem;
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
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.utils.decode.GlideEncypeImageLoader;
import com.heiheilianzai.app.view.comic.DanmuRelativeLayout;
import com.heiheilianzai.app.view.comic.ZoomRecyclerView;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.io.Serializable;
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
    @BindView(R.id.activity_comiclook_lording)
    public RelativeLayout activity_comiclook_lording;
    @BindView(R.id.activity_comiclook_lording_img)
    public ImageView activity_comiclook_lording_img;
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.activity_comiclook_RecyclerView)
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
    @BindView(R.id.item_dialog_downadapter_RotationLoadingView)
    public AVLoadingIndicatorView item_dialog_downadapter_RotationLoadingView;
    @BindView(R.id.activity_comiclook_danmu_dangqianhua)
    public TextView activity_comiclook_danmu_dangqianhua;
    @BindView(R.id.activity_comiclook_tucao_layout)
    public FrameLayout activity_comiclook_tucao_layout;
    @BindView(R.id.activity_comiclook_share)
    public RelativeLayout activity_comiclook_share;
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
    int current_display_order, ComicChapterSize;
    boolean first = true;
    ComicChapter CurrentComicChapter;//无网时候 当前的阅读章节
    LinearLayoutManager linearLayoutManager;
    boolean FORM_INFO, FORM_READHISTORY;
    View activity_comic_look_foot;

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

    @OnClick(value = {R.id.titlebar_back, R.id.activity_comiclook_shoucang, R.id.activity_comiclook_refresh, R.id.activity_comiclook_dingbu, R.id.activity_comiclook_danmu_layout,
            R.id.activity_comiclook_danmu_fashe, R.id.activity_comiclook_xiayihua_layout, R.id.activity_comiclook_shangyihua_layout, R.id.activity_comiclook_set,
            R.id.activity_comiclook_tucao_layout, R.id.activity_comiclook_share, R.id.activity_comiclook_xiazai, R.id.activity_comiclook_quanji,
            R.id.activity_comiclook_danmu_img2, R.id.activity_comiclook_foot})
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
                if (comicChapterItem != null && !comicChapterItem.next_chapter.equals("0")) {
                    getData(activity, comic_id, comicChapterItem.next_chapter, true);
                } else {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_end));
                }
                break;
            case R.id.activity_comiclook_shangyihua_layout:
                if (comicChapterItem != null && !comicChapterItem.last_chapter.equals("0")) {
                    getData(activity, comic_id, comicChapterItem.last_chapter, true);
                } else {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_start));
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
                showMenu(false);
                break;
            case R.id.activity_comiclook_xiazai:
                Intent intentxiazai = new Intent(activity, ComicDownActivity.class);
                intentxiazai.putExtra("baseComic", baseComic);
                intentxiazai.putExtra("comicChapter", (Serializable) comicChapter);
                startActivity(intentxiazai);
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
                    activity_comiclook_RecyclerView.smoothScrollBy(0, -800);
                    showMenu(false);
                }
            } else if (y <= HEIGHT * 2 / 3) {
                showMenu(!MenuSHOW);
            } else {
                if (AppPrefs.getSharedBoolean(activity, "fanye_ToggleButton", true)) {
                    activity_comiclook_RecyclerView.smoothScrollBy(0, 800);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            EventBus.getDefault().register(this);
            initViews();
            showMenu(true);
            Intent intent = getIntent();
            tocao_bgcolor = Color.parseColor("#4d000000");
            baseComic = (BaseComic) intent.getSerializableExtra("baseComic");
            comic_id = baseComic.getComic_id();
            current_display_order = baseComic.getCurrent_display_order();
            FORM_INFO = intent.getBooleanExtra("FORM_INFO", false);//是否是漫画详情界面过来的
            FORM_READHISTORY = intent.getBooleanExtra("FORM_READHISTORY", false);//阅读历史过来的 需要校验 服务端和本地记录
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
                    if (comicChapterList != null && !comicChapterList.isEmpty()) {
                        comicChapter = comicChapterList;
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
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    private void initData() {
        if (comicChapter != null) {
            ComicChapterSize = comicChapter.size();
            CurrentComicChapter = getCurrentComicChapter(current_display_order);
            if(CurrentComicChapter!=null){
                Chapter_id = CurrentComicChapter.getChapter_id();
                current_read_img_order = CurrentComicChapter.current_read_img_order;//本章最近阅读图片
            }
        }
        if (baseComic.isAddBookSelf()) {
            activity_comiclook_shoucang.setVisibility(View.GONE);
        }
        baseComicImages = new ArrayList<>();
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
        linearLayoutManager = new LinearLayoutManager(activity);
        activity_comiclook_RecyclerView.setLayoutManager(linearLayoutManager);
        activity_comiclook_RecyclerView.setTouchListener(onTouchListener);
        activity_comiclook_RecyclerView.setEnableScale(true);
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
                            baseComicImage = baseComicImages.get(current_display_order);
                        }
                        Last_read_img_order = (LastCompletelyVisibleItemPosition <= 0) ? LastVisibleItemPosition : LastCompletelyVisibleItemPosition;
                        if (Last_read_img_order == baseComicImagesSize) {
                            showMenu(true);
                        }
                    }
                } catch (Exception e) {
                }
                showMenu(false);
            }
        });
    }

    public void getData(Activity activity, String comic_id, String chapter_id, boolean HandleData) {
        if (!"0".equals(chapter_id)&&!StringUtils.isEmpty(chapter_id)) {
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
                                ComicChapter CurrentComicChapter = getCurrentComicChapter(current_display_order + 1);
                                if(CurrentComicChapter!=null){
                                    CurrentComicChapter.setImagesText(result);
                                    ContentValues values = new ContentValues();
                                    values.put("ImagesText", result);
                                    LitePal.update(ComicChapter.class, values, CurrentComicChapter.getId());
                                }
                            }
                        }

                        @Override
                        public void onErrorResponse(String ex) {
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

    private ComicChapter getCurrentComicChapter(int comicChapterSize) {
        ComicChapter comicChaptertemp=null;
        if(comicChapter!=null){
            if (comicChapterSize < ComicChapterSize&&comicChapter.size()>comicChapterSize) {
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


    private void HandleData(ComicChapterItem comicChapterItem, String chapter_id, String comic_id, Activity activity) {
        try {
            this.comicChapterItem = comicChapterItem;
            if (comicChapterItem != null && !comicChapterItem.image_list.isEmpty()) {
                setBigImageImageLoader(comicChapterItem.image_list.get(0));
                titlebar_text.setText(comicChapterItem.chapter_title);
                Chapter_title = comicChapterItem.chapter_title;
                current_display_order = comicChapterItem.display_order;
                CurrentComicChapter = getCurrentComicChapter(current_display_order);
                Chapter_id = comicChapterItem.chapter_id;
                if (comicChapterItem.is_preview == 1) {
                    getBuy();
                }
                baseComic.setCurrent_chapter_name(Chapter_title);
                baseComic.setCurrent_chapter_id(Chapter_id);
                baseComic.setCurrent_display_order(current_display_order);
                baseComicImages.clear();
                baseComicImagesSize = 0;
                for (BaseComicImage baseComicImage : comicChapterItem.image_list) {
                    baseComicImage.chapter_id = chapter_id;
                    baseComicImage.comic_id = comic_id;
                    ++baseComicImagesSize;
                }
                baseComicImages.addAll(comicChapterItem.image_list);
                if (first) {
                    comicChapterCatalogAdapter = new ComicRecyclerViewAdapter(activity, WIDTH, HEIGHT, baseComicImages, activity_comic_look_foot, baseComicImagesSize, itemOnclick);
                    activity_comiclook_RecyclerView.setAdapter(comicChapterCatalogAdapter);
                    activity_comiclook_RecyclerView.scrollToPosition(current_read_img_order);
                    activity_comiclook_lording.setVisibility(View.GONE);
                    if (comicChapterItem.total_comment != 0) {
                        total_count = comicChapterItem.total_comment;
                        activity_comiclook_pinglunshu.setVisibility(View.VISIBLE);
                        activity_comiclook_pinglunshu.setText(comicChapterItem.total_comment + "");
                    } else {
                        activity_comiclook_pinglunshu.setVisibility(View.GONE);
                    }
                    showMenu(false);
                    first = false;
                } else {
                    comicChapterCatalogAdapter.NotifyDataSetChanged(baseComicImagesSize);
                    linearLayoutManager.scrollToPositionWithOffset(0, 0);
                }
                //更新该本书的阅读记录
                ContentValues values = new ContentValues();
                values.put("current_chapter_id", chapter_id);
                values.put("current_display_order", current_display_order);
                values.put("current_chapter_name", Chapter_title);
                LitePal.update(BaseComic.class, values, baseComic.getId());
                EventBus.getDefault().post(baseComic);
                //更新数据库该章节已读
                ContentValues CurrentComicChapterIsRead = new ContentValues();
                CurrentComicChapterIsRead.put("IsRead", true);
                LitePal.updateAsync(ComicChapter.class, CurrentComicChapterIsRead, CurrentComicChapter.getId());
                CurrentComicChapter.IsRead = true;
                item_dialog_downadapter_RotationLoadingView.setVisibility(View.GONE);
                activity_comiclook_danmu_dangqianhua.setVisibility(View.VISIBLE);
                if (comicChapterItem.next_chapter.equals("0")) {
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
                if (ReaderConfig.USE_AD) {
                    holderFoot.list_ad_view_layout.setVisibility(View.VISIBLE);
                    getWebViewAD(activity);
                }
                ComicReadHistory.addReadHistory(FORM_READHISTORY, activity, comic_id, chapter_id);
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    public void sendTucao(String image_id, String content, boolean CommentFlag) {
        if (!MainHttpTask.getInstance().Gotologin(activity)) {
            return;
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 111) {
            int total_countt = data.getIntExtra("total_count", 0);
            total_count = total_countt;
            activity_comiclook_pinglunshu.setText(total_count + "");
        } else if (requestCode == 222) {
            if (resultCode == 222) {
                Chapter_id = data.getStringExtra("currentChapter_id");
                getData(activity, comic_id, Chapter_id, true);
            }
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
                                holderFoot.list_ad_view_layout.setOnClickListener(new View.OnClickListener() {
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
                                ViewGroup.LayoutParams layoutParams = holderFoot.list_ad_view_img.getLayoutParams();
                                layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
                                layoutParams.height = layoutParams.width / 3;
                                holderFoot.list_ad_view_img.setLayoutParams(layoutParams);
                                MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, holderFoot.list_ad_view_img);
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
    protected void onDestroy() {
        super.onDestroy();
        map.clear();
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
        @BindView(R.id.list_ad_view_layout)
        FrameLayout list_ad_view_layout;
        @BindView(R.id.list_ad_view_img)
        ImageView list_ad_view_img;

        public MyViewHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(value = {R.id.activity_comic_look_foot_shangyihua, R.id.activity_comic_look_foot_xiayihua, R.id.list_ad_view_layout})
        public void getEvent(View view) {
            switch (view.getId()) {
                case R.id.activity_comic_look_foot_xiayihua:
                    isclickScreen = false;
                    if (comicChapterItem != null && !comicChapterItem.next_chapter.equals("0")) {
                        getData(activity, comic_id, comicChapterItem.next_chapter, true);
                    } else {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_end));
                    }
                    break;
                case R.id.activity_comic_look_foot_shangyihua:
                    isclickScreen = false;
                    if (comicChapterItem != null && !comicChapterItem.last_chapter.equals("0")) {
                        getData(activity, comic_id, comicChapterItem.last_chapter, true);
                    } else {
                        MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ComicLookActivity_start));
                    }
                    break;
                case R.id.list_ad_view_layout:
                    break;
            }
        }
    }

    /**
     * 询问是否加入书架
     */
    private void askIsNeedToAddShelf() {
        if (baseComic.isAddBookSelf()) {
            finish();
            return;
        }
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
        dialog.show();
    }

    /**
     * 拿到数据后 据数据判断图片地址是否需要解密。(只在ComicLookActivity使用)
     * @param bigImageImageLoader
     */
    void setBigImageImageLoader(BaseComicImage bigImageImageLoader){
        String imageStr = bigImageImageLoader.getImage();
        setBigImageImageLoader(StringUtils.isImgeUrlEncryptPostfix(imageStr));
    }

    void setBigImageImageLoader(boolean isEncrype){
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
}