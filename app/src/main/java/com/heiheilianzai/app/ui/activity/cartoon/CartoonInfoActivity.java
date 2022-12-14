package com.heiheilianzai.app.ui.activity.cartoon;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.cartoon.CartoonChapterAdapter;
import com.heiheilianzai.app.adapter.cartoon.StoreCartoonAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.model.cartoon.CartoonChapter;
import com.heiheilianzai.app.model.cartoon.CartoonInfo;
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogCouponNotMore;
import com.heiheilianzai.app.utils.DialogRegister;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.ToastUtil;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.live.eggplant.player.GSYVideoManager;
import com.live.eggplant.player.listener.GSYSampleCallBack;
import com.live.eggplant.player.listener.LockClickListener;
import com.live.eggplant.player.player.IjkPlayerManager;
import com.live.eggplant.player.player.PlayerFactory;
import com.live.eggplant.player.utils.OrientationUtils;
import com.live.eggplant.player.video.StandardGSYVideoPlayer;
import com.live.eggplant.widget.video.GSYVideoOptionHelper;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CartoonInfoActivity extends BaseButterKnifeActivity {
    private static String CARTOON_ID_EXT_KAY = "CARTOON_ID";
    private static String CARTOON_HiSTORY_EXT_KAY = "CARTOON_HISTORY";
    @BindView(R.id.img_back)
    public ImageView mImgBack;
    @BindView(R.id.ll_gold)
    public LinearLayout mLlGold;
    @BindView(R.id.tx_gold_title)
    public TextView mTxGoldTitle;
    @BindView(R.id.tx_gold_num)
    public TextView mTxGoldNum;
    @BindView(R.id.tx_gold_open)
    public TextView mTxGoldOpen;
    @BindView(R.id.tb_open)
    public ToggleButton mTbOpen;
    @BindView(R.id.ll_vip)
    public LinearLayout mLlVip;
    @BindView(R.id.tx_gold_charge)
    public TextView mTxGoldCharge;
    @BindView(R.id.ry_chapter)
    public RecyclerView mRyChapter;
    @BindView(R.id.tx_title_cartoon)
    public TextView mTxCartoonTitle;
    @BindView(R.id.tx_description_cartoon)
    public TextView mTxCartoonDes;
    @BindView(R.id.tx_tag_cartoon)
    public TextView mTxCartoonTag;
    @BindView(R.id.tx_time_cartoon)
    public TextView mTxCartoonTime;
    @BindView(R.id.gv_guess)
    public AdaptionGridView mGv;
    @BindView(R.id.video)
    public StandardGSYVideoPlayer mVideoPlayer;
    private String mCartoonId;
    private Activity mActivity;
    private CartoonInfo mCartoonInfo;
    private Gson mGson;
    private List<CartoonChapter> mCartoonChapters;
    private CartoonChapterAdapter mCartoonChapterAdapter;
    private String mPrice;
    private int mGoldNum;
    private CartoonChapter mChapterItem;
    private OrientationUtils mOrientationUtils;
    private CartoonChapter mHistoryCartoonChapter;

    @Override
    public int initContentView() {
        return R.layout.activity_cartooninfo;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
        mGson = new Gson();
        mActivity = this;
        mCartoonChapters = new ArrayList<>();
        mCartoonId = getIntent().getStringExtra(CARTOON_ID_EXT_KAY);
        mHistoryCartoonChapter = (CartoonChapter) getIntent().getSerializableExtra(CARTOON_HiSTORY_EXT_KAY);
        if (mHistoryCartoonChapter != null) {
            mCartoonId = mHistoryCartoonChapter.getVideo_id();
        }
        initView();
        getInfo();
    }

    private void initView() {
        mGoldNum = AppPrefs.getSharedInt(mActivity, PrefConst.COUPON, 0);
        mTxGoldNum.setText(String.valueOf(mGoldNum));
        if (AppPrefs.getSharedBoolean(mActivity, "comicOpen_ToggleButton", false)) {
            mTbOpen.setToggleOn();
        } else {
            mTbOpen.setToggleOff();
        }
        MyContentLinearLayoutManager linearLayoutManager = new MyContentLinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRyChapter.setLayoutManager(linearLayoutManager);
        mCartoonChapterAdapter = new CartoonChapterAdapter(mCartoonChapters, mActivity);
        mRyChapter.setAdapter(mCartoonChapterAdapter);

        mVideoPlayer.getFullscreenButton().setOnClickListener(v -> {
            mOrientationUtils.resolveByClick();
            //?????????true??????????????????actionbar????????????true??????????????????statusbar
            mVideoPlayer.startWindowFullscreen(mActivity, true, true);
        });
        mVideoPlayer.getBackButton().setOnClickListener(v -> {
            if (mOrientationUtils != null && mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
                mVideoPlayer.getFullscreenButton().performClick();
            } else {
                finish();
            }
        });
        mCartoonChapterAdapter.setmOnBackChapterListener((cartoonChapter, position) -> {
            for (int i = 0; i < mCartoonChapters.size(); i++) {
                CartoonChapter chapter;
                chapter = mCartoonChapters.get(i);
                if (i != position) {
                    chapter.setSelect(false);
                } else {
                    chapter.setSelect(true);
                }
            }
            mChapterItem = cartoonChapter;
            mCartoonChapterAdapter.notifyDataSetChanged();
            mVideoPlayer.release();
            checkIsCoupon(cartoonChapter);
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        getInfo();
    }

    @SuppressLint("NonConstantResourceId")
    @OnClick(value = {R.id.tx_vip_charge, R.id.tx_gold_charge, R.id.tx_gold_open, R.id.img_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_back:
                finish();
                break;
            case R.id.tx_vip_charge:
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(mActivity, LanguageUtil.getString(mActivity, R.string.refer_page_mine), 4);
                myIntent.putExtra("isvip", Utils.isLogin(mActivity));
                myIntent.putExtra("type", 0);
                mActivity.startActivity(myIntent);
                break;
            case R.id.tx_gold_charge:
                Intent intent = AcquireBaoyueActivity.getMyIntent(mActivity, LanguageUtil.getString(mActivity, R.string.refer_page_mine), 4);
                intent.putExtra("isvip", Utils.isLogin(mActivity));
                if (TextUtils.equals(mChapterItem.getIs_vip(), "1")) {
                    intent.putExtra("type", 0);
                } else {
                    intent.putExtra("type", 1);
                }
                mActivity.startActivity(intent);
                break;
            case R.id.tx_gold_open:
                openCoupon(mChapterItem, mPrice, mGoldNum);
                break;
        }
    }

    private void getChapter() {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_chapter, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initChapter(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mVideoPlayer.setVisibility(View.GONE);
                        mImgBack.setVisibility(View.VISIBLE);
                    }
                }
        );
    }

    @SuppressLint("NotifyDataSetChanged")
    private void initChapter(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            mCartoonChapters.clear();
            mPrice = jsonObject.getString("coupon_pay_price");
            mCartoonChapters.addAll(mGson.fromJson(jsonObject.getString("chapter_list"), new TypeToken<List<CartoonChapter>>() {
            }.getType()));
            if (mCartoonChapters != null && mCartoonChapters.size() > 0) {
                if (mHistoryCartoonChapter != null) {
                    for (int i = 0; i < mCartoonChapters.size(); i++) {
                        CartoonChapter chapter = mCartoonChapters.get(i);
                        if (TextUtils.equals(mHistoryCartoonChapter.getChapter_id(), chapter.getChapter_id())) {
                            chapter.setPlay_node(mHistoryCartoonChapter.getPlay_node());
                            mChapterItem = chapter;
                            chapter.setSelect(true);
                            break;
                        }
                    }
                } else {
                    mChapterItem = mCartoonChapters.get(0);
                    mChapterItem.setSelect(true);
                }
                mCartoonChapterAdapter.notifyDataSetChanged();
                checkIsCoupon(mChapterItem);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void playVideo(CartoonChapter cartoonChapter) {
        if (TextUtils.isEmpty(cartoonChapter.getContent())) {
            mVideoPlayer.setVisibility(View.GONE);
            mImgBack.setVisibility(View.VISIBLE);
            MyToash.ToashError(mActivity, getString(R.string.play_video_error));
            return;
        }
        if (GSYVideoManager.instance().isPlaying()) {
            mVideoPlayer.release();
            GSYVideoManager.instance().stop();
        }
        mImgBack.setVisibility(View.GONE);
        mLlVip.setVisibility(View.GONE);
        mLlGold.setVisibility(View.GONE);
        mVideoPlayer.setVisibility(View.VISIBLE);
        updateRecord(cartoonChapter);
        PlayerFactory.setPlayManager(IjkPlayerManager.class);
        //????????????
        ImageView imageView = new ImageView(this);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setImageDrawable(getResources().getDrawable(R.mipmap.cartoon_play_bg));
        //MyPicasso.GlideImageNoSize(mActivity, mCartoonInfo.getAuthor(), imageView);
        //????????????????????????????????????
        mOrientationUtils = new OrientationUtils(this, mVideoPlayer);
        //?????????????????????????????????
        mOrientationUtils.setEnable(false);
        GSYVideoOptionHelper.INSTANCE.getGSYVideoOptionBuilder(cartoonChapter.getContent(), cartoonChapter.getChapter_title(), true)
                .setSeekOnStart(cartoonChapter.getPlay_node() * 1000)
                .setThumbImageView(imageView)
                .setVideoAllCallBack(new GSYSampleCallBack() {
                    @Override
                    public void onAutoComplete(String url, Object... objects) {
                        super.onAutoComplete(url, objects);
                        GSYVideoManager.releaseAllVideos();
                    }

                    @Override
                    public void onPrepared(String url, Object... objects) {
                        super.onPrepared(url, objects);
                        //????????????????????????????????????
                        mOrientationUtils.setEnable(mVideoPlayer.isRotateWithSystem());
                    }

                    @Override
                    public void onClickStop(String url, Object... objects) {
                        updateDetailRecord(cartoonChapter);
                        super.onClickStop(url, objects);
                    }

                    @Override
                    public void onPlayError(int what, String url, Object... objects) {
                        super.onPlayError(what, url, objects);
                        MyToash.ToashError(mActivity, getString(R.string.play_video_error));
                        mVideoPlayer.setVisibility(View.GONE);
                        mImgBack.setVisibility(View.VISIBLE);
                    }
                }).setLockClickListener(new LockClickListener() {
            @Override
            public void onClick(View view, boolean lock) {
                if (mOrientationUtils != null) {
                    //???????????????onConfigurationChanged
                    mOrientationUtils.setEnable(!lock);
                }
            }
        }).build(mVideoPlayer);
        mVideoPlayer.startPlayLogic();
    }

    private void checkIsVip(CartoonChapter chapterItem) {
        String is_vip = chapterItem.getIs_vip();
        if (is_vip != null && is_vip.equals("1") && !App.isVip(mActivity)) {
            mLlVip.setVisibility(View.VISIBLE);
            mLlGold.setVisibility(View.GONE);
            mVideoPlayer.setVisibility(View.GONE);
            mImgBack.setVisibility(View.VISIBLE);
        } else {
            playVideo(chapterItem);
        }
    }

    private void checkIsCoupon(CartoonChapter chapterItem) {
        mImgBack.setVisibility(View.VISIBLE);
        String is_book_coupon_pay = chapterItem.getIs_book_coupon_pay();
        String is_vip = chapterItem.getIs_vip();
        String is_limited_free = chapterItem.getIs_limited_free();
        if (Utils.isLogin(mActivity)) {
            if ((!StringUtils.isEmpty(is_limited_free) && TextUtils.equals(is_limited_free, "1")) || (TextUtils.equals(is_vip, "0") && TextUtils.equals(is_book_coupon_pay, "0"))) {
                playVideo(chapterItem);
            } else {
                if (chapterItem.isIs_buy_status()) {
                    playVideo(chapterItem);
                } else {
                    if ((is_book_coupon_pay != null && is_book_coupon_pay.equals("1"))) {//????????????
                        if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {//????????????
                            if (App.isVip(mActivity)) {
                                playVideo(chapterItem);
                            } else {
                                showLimitDialog(chapterItem);
                            }
                        } else {
                            showLimitDialog(chapterItem);
                        }
                    } else {
                        checkIsVip(chapterItem);
                    }
                }
            }
        } else {
            DialogRegister dialogRegister = new DialogRegister();
            dialogRegister.setFinish(true);
            dialogRegister.getDialogLoginPop(mActivity);
            dialogRegister.setmRegisterBackListener(isSuccess -> {
                if (isSuccess) {
                    checkIsCoupon(chapterItem);
                }
            });
        }
    }

    private void showLimitDialog(CartoonChapter chapterItem) {
        if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {
            mTxGoldTitle.setText(getResources().getString(R.string.dialog_tittle_cartoon_coupon_vip));
            SpannableString stringVip = new SpannableString(activity.getResources().getString(R.string.dialog_tittle_cartoon_coupon_vip));
            UnderlineSpan span = new UnderlineSpan();
            stringVip.setSpan(span, 8, 12, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            stringVip.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.color_ff8350)), 8, 12, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            mTxGoldTitle.setText(stringVip);
            mTxGoldCharge.setText(getResources().getString(R.string.AcquireBaoyueActivity_title_vip));
        } else {
            mTxGoldTitle.setText(getResources().getString(R.string.dialog_tittle_cartoon_coupon));
            mTxGoldCharge.setText(getResources().getString(R.string.AcquireBaoyueActivity_title_gold));
        }
        String format = String.format(mActivity.getResources().getString(R.string.dialog_coupon_open), mPrice);
        SpannableString spannableString = new SpannableString(format);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        mTxGoldOpen.setText(spannableString);

        //????????????????????????????????????
        if (AppPrefs.getSharedBoolean(mActivity, "comicOpen_ToggleButton", false)) {
            if (mGoldNum >= Integer.valueOf(mPrice)) {
                openCoupon(chapterItem, mPrice, mGoldNum);
            } else {
                DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                dialogCouponNotMore.getDialogVipPop(mActivity, true);
            }
        } else {
            mLlGold.setVisibility(View.VISIBLE);
            mLlVip.setVisibility(View.GONE);
            mVideoPlayer.setVisibility(View.GONE);
            mImgBack.setVisibility(View.VISIBLE);
        }
        mTbOpen.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(mActivity, "comicOpen_ToggleButton", on);
                if (on) {
                    if (!Utils.isLogin(mActivity)) {
                        MainHttpTask.getInstance().Gotologin(mActivity);
                    }
                }
            }
        });
    }
    private void getInfo() {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_info, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_click_log, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void initInfo(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONObject video = jsonObject.getJSONObject("video");
            mCartoonInfo = mGson.fromJson(video.toString(), CartoonInfo.class);
            if (mCartoonInfo != null) {
                getChapter();
                mTxCartoonTitle.setText(mCartoonInfo.getName());
                mTxCartoonDes.setText(mCartoonInfo.getDescription());
                mTxCartoonTime.setText(mCartoonInfo.getUpdated_at());
                StringBuilder tags = new StringBuilder();
                if (mCartoonInfo.getTag() != null && mCartoonInfo.getTag().size() > 0) {
                    for (int i = 0; i < mCartoonInfo.getTag().size(); i++) {
                        String tag = mCartoonInfo.getTag().get(i);
                        tags.append("#").append(tag).append(" ");
                    }
                    mTxCartoonTag.setText(tags.toString());
                    mTxCartoonTag.setVisibility(View.VISIBLE);
                } else {
                    mTxCartoonTag.setVisibility(View.GONE);
                }
            }
            List<StroreCartoonLable.Cartoon> cartoonList = mGson.fromJson(jsonObject.getString("list"), new TypeToken<List<StroreCartoonLable.Cartoon>>() {
            }.getType());
            if (cartoonList != null) {
                int width = ScreenSizeUtils.getInstance(mActivity).getScreenWidth() / 2;
                int height = width * 2 / 3;
                StoreCartoonAdapter storeCartoonAdapter = new StoreCartoonAdapter(cartoonList, mActivity, width, height);
                mGv.setAdapter(storeCartoonAdapter);
                mGv.setOnItemClickListener((parent, view, position, id) -> {
                    mVideoPlayer.release();
                    mCartoonId = cartoonList.get(position).video_id;
                    getInfo();
                });
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * ?????????????????????????????????
     *
     * @param referPage ???????????????????????????????????????
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String videoId) {
        Intent intent = new Intent(context, CartoonInfoActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(CARTOON_ID_EXT_KAY, videoId);
        return intent;
    }

    public static Intent getHistoryIntent(Context context, CartoonChapter cartoonChapter) {
        Intent intent = new Intent(context, CartoonInfoActivity.class);
        intent.putExtra(CARTOON_HiSTORY_EXT_KAY, cartoonChapter);
        return intent;
    }


    public void openCoupon(CartoonChapter chapterItem, String couponPrice, int couponNum) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("video_id", mCartoonId);
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_chapter_unlock, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                        AppPrefs.putSharedInt(mActivity, PrefConst.COUPON, couponNum - Integer.parseInt(couponPrice));
                        playVideo(chapterItem);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }
        );
    }

    private void updateRecord(CartoonChapter cartoonChapter) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("video_id", mCartoonId);
        params.putExtraParams("chapter_id", cartoonChapter.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_play_log, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_play_num_log, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Override
    public void onBackPressed() {
        //?????????????????????
        if (mOrientationUtils != null && mOrientationUtils.getScreenType() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            mVideoPlayer.getFullscreenButton().performClick();
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            finish();
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            }, 500);
        }
    }

    private void exitVideo() {
        //????????????
        if (mVideoPlayer != null) {
            mVideoPlayer.release();
            mVideoPlayer.setVideoAllCallBack(null);
        }
        GSYVideoManager.releaseAllVideos();
        if (mOrientationUtils != null)
            mOrientationUtils.releaseListener();
    }

    @Override
    protected void onStop() {
        updateDetailRecord(mChapterItem);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        exitVideo();
        super.onDestroy();
    }

    /**
     * ??????????????????
     */
    private void updateDetailRecord(CartoonChapter cartoonChapter) {
        if (cartoonChapter != null) {
            ReaderParams params = new ReaderParams(mActivity);
            params.putExtraParams("video_id", mCartoonId);
            params.putExtraParams("chapter_id", cartoonChapter.getChapter_id());
            long currentPosition = GSYVideoManager.instance().getCurrentPosition();
            int playTime = (int) currentPosition / 1000;
            params.putExtraParams("play_node", String.valueOf(playTime));
            String json = params.generateParamsJson();
            HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + CartoonConfig.CARTOON_play_node, json, false, new HttpUtils.ResponseListener() {
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
}
