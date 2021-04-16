package com.heiheilianzai.app.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;import android.content.Intent;
import android.graphics.Color;
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
import com.heiheilianzai.app.ui.activity.comic.RefreashComicInfoActivity;


import org.greenrobot.eventbus.EventBus;


import butterknife.BindView;
import butterknife.ButterKnife;

public class DialogComicLook {
    private DialogNovelCoupon.OnOpenCouponListener onOpenCouponListener;
    public void setOnOpenCouponListener(DialogNovelCoupon.OnOpenCouponListener onOpenCouponListener) {
        this.onOpenCouponListener = onOpenCouponListener;
    }
    @SuppressLint("UseCompatLoadingForDrawables")
    public Dialog getDialogVipPop(Activity activity, ComicChapterItem chapterItem,BaseComic baseComic,boolean isCoupon) {
        Dialog popupWindow = new Dialog(activity, R.style.fullScreen);

        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_comic_look, null);
        VipHolder vipHolder = new VipHolder(view);
        int couponNum = AppPrefs.getSharedInt(activity, PrefConst.COUPON, 0);
        String couponPrice = AppPrefs.getSharedString(activity, PrefConst.COUPON_COMICI_PRICE);
        vipHolder.txChapter.setText(String.format(activity.getResources().getString(R.string.dialog_coupon_open), couponPrice));
        vipHolder.txNum.setText(String.valueOf(couponNum));
        vipHolder.txChapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onOpenCouponListener != null) {
                    if (!Utils.isLogin(activity)) {//登录状态跳个人资料
                        if (popupWindow != null) {
                            popupWindow.dismiss();
                        }
                        MainHttpTask.getInstance().Gotologin(activity);
                    }else {
                        if (couponNum >= Integer.valueOf(couponPrice)) {
                            openCoupon(activity,chapterItem,couponPrice,couponNum);
                        } else {
                            if (popupWindow != null) {
                                popupWindow.dismiss();
                            }
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
                Intent myIntent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine));
                myIntent.putExtra("isvip", Utils.isLogin(activity));
                activity.startActivity(myIntent);
                activity.finish();
            }
        });
        vipHolder.llBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow!=null){
                    popupWindow.dismiss();
                    askIsNeedToAddShelf(activity,baseComic);
                }
            }
        });
        if (!isCoupon){
            vipHolder.llCounpon.setVisibility(View.GONE);
            vipHolder.txChapter.setVisibility(View.GONE);
        }else {
            vipHolder.llCounpon.setVisibility(View.VISIBLE);
            vipHolder.txChapter.setVisibility(View.VISIBLE);
        }

        popupWindow.setContentView(view);
        //全屏放在setcontentview后面才有效果
        Window window = popupWindow.getWindow();
        WindowManager.LayoutParams attributes = window.getAttributes();
        attributes.width=WindowManager.LayoutParams.MATCH_PARENT;
        attributes.height=WindowManager.LayoutParams.MATCH_PARENT;
        //设置弹出位置
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

        public VipHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
    /**
     * 询问是否加入书架
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
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
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
                baseComic.saveIsexist(true);
                MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.fragment_comic_info_yishoucang));
                EventBus.getDefault().post(new RefreshComic(baseComic, 1));
                EventBus.getDefault().post(new RefreashComicInfoActivity(true));
                activity.finish();
            }
        });
        dialog.show();
    }
    public interface OnOpenCouponListener {
        void onOpenCoupon(boolean isBuy);
    }
    private void openCoupon(Activity activity, ComicChapterItem chapterItem, String couponPrice, int couponNum) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", chapterItem.getComic_id());
        params.putExtraParams("chapter_id",chapterItem.getChapter_id());
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_chapter_open, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                        onOpenCouponListener.onOpenCoupon(true);
                        AppPrefs.putSharedInt(activity, PrefConst.COUPON, couponNum-Integer.valueOf(couponPrice));
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        onOpenCouponListener.onOpenCoupon(false);
                    }
                }

        );
    }
}
