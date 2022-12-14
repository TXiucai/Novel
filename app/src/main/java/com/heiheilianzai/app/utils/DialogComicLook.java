package com.heiheilianzai.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.UnderlineSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapterItem;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicinfoMuluActivity;
import com.heiheilianzai.app.ui.activity.comic.RefreashComicInfoActivity;
import com.zcw.togglebutton.ToggleButton;


import org.greenrobot.eventbus.EventBus;


import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogComicLook {
    private DialogNovelCoupon.OnOpenCouponListener onOpenCouponListener;

    public void setOnOpenCouponListener(DialogNovelCoupon.OnOpenCouponListener onOpenCouponListener) {
        this.onOpenCouponListener = onOpenCouponListener;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public Dialog getDialogVipPop(Activity activity, ComicChapterItem chapterItem, BaseComic baseComic, boolean isCoupon) {
        Dialog popupWindow = new Dialog(activity, R.style.fullScreen);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_comic_look, null);
        VipHolder vipHolder = new VipHolder(view);
        int couponNum = AppPrefs.getSharedInt(activity, PrefConst.COUPON, 0);
        String couponPrice = AppPrefs.getSharedString(activity, PrefConst.COUPON_COMICI_PRICE);
        // ?????????????????????????????????????????????????????????????????????
        String format = String.format(activity.getResources().getString(R.string.dialog_coupon_open), couponPrice);
        SpannableString spannableString = new SpannableString(format);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vipHolder.txTittle.setText(chapterItem.getChapter_title());
        vipHolder.txChapter.setText(spannableString);
        vipHolder.txNum.setText(String.valueOf(couponNum));
        if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {
            SpannableString stringVip = new SpannableString(activity.getResources().getString(R.string.dialog_tittle_comic_coupon_vip));
            UnderlineSpan span = new UnderlineSpan();
            stringVip.setSpan(span, 8, 12, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            stringVip.setSpan(new ForegroundColorSpan(activity.getResources().getColor(R.color.color_ff8350)), 8, 12, Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            vipHolder.txTitle.setText(stringVip);
            vipHolder.txSubTitle.setText(activity.getResources().getString(R.string.coupon_vip_open));
            vipHolder.txVip.setText(activity.getResources().getString(R.string.AcquireBaoyueActivity_title_vip));
        } else {
            vipHolder.txTitle.setText(activity.getResources().getString(R.string.dialog_tittle_novel_coupon));
            vipHolder.txSubTitle.setText(activity.getResources().getString(R.string.coupon_open));
            vipHolder.txVip.setText(activity.getResources().getString(R.string.AcquireBaoyueActivity_title_gold));
        }
        if (AppPrefs.getSharedBoolean(activity, "comicOpen_ToggleButton", false)) {
            vipHolder.tbOpen.setToggleOn();
        } else {
            vipHolder.tbOpen.setToggleOff();
        }
        vipHolder.tbOpen.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(activity, "comicOpen_ToggleButton", on);
                if (onOpenCouponListener != null && on) {
                    if (!Utils.isLogin(activity)) {//???????????????????????????
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        MainHttpTask.getInstance().Gotologin(activity);
                    }/*else {//???????????????????????????????????????
                        if (couponNum >= Integer.valueOf(couponPrice)) {
                            openCoupon(activity, chapterItem, couponPrice, couponNum);
                        } else {
                            DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                            dialogCouponNotMore.getDialogVipPop(activity, true);
                        }
                    }*/
                }
            }
        });
        vipHolder.txQuanji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, ComicinfoMuluActivity.class);
                intent.putExtra("comic_id", chapterItem.comic_id);
                intent.putExtra("currentChapter_id", chapterItem.getChapter_id());
                activity.startActivityForResult(intent, 222);
            }
        });
        vipHolder.txChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOpenCouponListener != null) {
                    if (!Utils.isLogin(activity)) {//???????????????????????????
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        MainHttpTask.getInstance().Gotologin(activity);
                    } else {
                        if (couponNum >= Integer.valueOf(couponPrice)) {
                            openCoupon(activity, chapterItem, couponPrice, couponNum);
                        } else {
                            DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                            dialogCouponNotMore.getDialogVipPop(activity, true);
                        }
                    }
                }
            }
        });
        vipHolder.txVip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 4);
                myIntent.putExtra("isvip", Utils.isLogin(activity));
                if (TextUtils.equals(chapterItem.getIs_vip(), "1")) {
                    myIntent.putExtra("type", 0);
                } else {
                    myIntent.putExtra("type", 1);
                }
                activity.startActivity(myIntent);
                activity.finish();
            }
        });
        vipHolder.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                    askIsNeedToAddShelf(activity, baseComic);
                }
            }
        });
        if (!isCoupon) {
            vipHolder.llCounpon.setVisibility(View.INVISIBLE);
            vipHolder.txChapter.setVisibility(View.INVISIBLE);
        } else {
            vipHolder.llCounpon.setVisibility(View.VISIBLE);
            vipHolder.txChapter.setVisibility(View.VISIBLE);
        }

        popupWindow.setContentView(view);
        //????????????setcontentview??????????????????
        Window window = popupWindow.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width = WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height = WindowManager.LayoutParams.MATCH_PARENT;
        //??????????????????
        window.setGravity(Gravity.CENTER);
        window.setAttributes(attributes);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    class VipHolder {

        @BindView(R.id.tx_coupon_num)
        public TextView txNum;
        @BindView(R.id.ll_coupon)
        public LinearLayout llCounpon;
        @BindView(R.id.tx_vip)
        public TextView txVip;
        @BindView(R.id.tx_coupon_chapter)
        public TextView txChapter;
        @BindView(R.id.titlebar_back)
        public LinearLayout llBack;
        @BindView(R.id.titlebar_text)
        public TextView txTittle;
        @BindView(R.id.tx_quanji)
        public TextView txQuanji;
        @BindView(R.id.tb_open)
        public ToggleButton tbOpen;
        @BindView(R.id.dialog_title)
        public TextView txTitle;
        @BindView(R.id.dialog_title_sub)
        public TextView txSubTitle;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    /**
     * ????????????????????????
     *
     * @param activity
     * @param baseComic
     */
    private void askIsNeedToAddShelf(Activity activity, BaseComic baseComic) {
        if (baseComic.isAddBookSelf()) {
            activity.finish();
            return;
        }
        final Dialog dialog = new Dialog(activity, R.style.NormalDialogStyle);
        View view = View.inflate(activity, R.layout.dialog_add_shelf, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        dialog.setContentView(view);
        //????????????????????????
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(activity).getScreenHeight() * 0.2f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(activity).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activity.finish();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                baseComic.saveIsexist(true, Utils.getUID(activity));
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.fragment_comic_info_yishoucang));
                EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                EventBus.getDefault().post(new RefreashComicInfoActivity(true));
                activity.finish();
            }
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    public interface OnOpenCouponListener {
        void onOpenCoupon(boolean isBuy);
    }

    public void openCoupon(Activity activity, ComicChapterItem chapterItem, String couponPrice, int couponNum) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", chapterItem.getComic_id());
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_chapter_open, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                        onOpenCouponListener.onOpenCoupon(true);
                        AppPrefs.putSharedInt(activity, PrefConst.COUPON, couponNum - Integer.valueOf(couponPrice));
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        onOpenCouponListener.onOpenCoupon(false);
                    }
                }

        );
    }
}
