package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogNovelCoupon {
    private OnOpenCouponListener onOpenCouponListener;

    public void setOnOpenCouponListener(OnOpenCouponListener onOpenCouponListener) {
        this.onOpenCouponListener = onOpenCouponListener;
    }

    public Dialog getDialogVipPop(Activity activity, ChapterItem chapterItem, boolean isExit) {
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_novel_coupon, null);
        Dialog popupWindow = new Dialog(activity, R.style.updateapp);
        Window window = popupWindow.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.CENTER);
        VipHolder vipHolder = new VipHolder(view);
        int couponNum = AppPrefs.getSharedInt(activity, PrefConst.COUPON, 0);
        String couponPrice = AppPrefs.getSharedString(activity, PrefConst.COUPON_PRICE);
        String format = String.format(activity.getResources().getString(R.string.dialog_coupon_open), couponPrice);
        SpannableString spannableString = new SpannableString(format);
        UnderlineSpan underlineSpan = new UnderlineSpan();
        spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        vipHolder.dialog_open.setText(spannableString);
        vipHolder.dialog_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOpenCouponListener != null) {
                    if (!Utils.isLogin(activity)) {//登录状态跳个人资料
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        MainHttpTask.getInstance().Gotologin(activity);
                    } else {
                        if (couponNum >= Integer.valueOf(couponPrice)) {
                            openCoupon(activity, chapterItem, couponPrice, couponNum);
                        } else {
                            DialogCouponNotMore dialogCouponNotMore = new DialogCouponNotMore();
                            dialogCouponNotMore.getDialogVipPop(activity, isExit);
                        }
                    }
                }
            }
        });

        vipHolder.dialog_num.setText(String.valueOf(couponNum));
        vipHolder.dialog_no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExit) {
                    if (popupWindow != null) {
                        popupWindow.dismiss();
                    }
                    activity.finish();
                }
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

        vipHolder.dialog_yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine));
                myIntent.putExtra("isvip", Utils.isLogin(activity));
                activity.startActivity(myIntent);
            }
        });
        popupWindow.setContentView(view);
        popupWindow.setCancelable(false);
        popupWindow.setCanceledOnTouchOutside(false);
        popupWindow.show();
        return popupWindow;
    }

    class VipHolder {

        @BindView(R.id.dialog_no)
        public TextView dialog_no;
        @BindView(R.id.dialog_yes)
        public TextView dialog_yes;
        @BindView(R.id.dialog_coupon_num)
        public TextView dialog_num;
        @BindView(R.id.dialog_coupon_open)
        public TextView dialog_open;

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    public interface OnOpenCouponListener {
        void onOpenCoupon(boolean isBuy);
    }

    private void openCoupon(Activity activity, ChapterItem chapterItem, String couponPrice, int couponNum) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("book_id", chapterItem.getBook_id());
        params.putExtraParams("chapter_id", chapterItem.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterOpenCoupon, json, true, new HttpUtils.ResponseListener() {
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
