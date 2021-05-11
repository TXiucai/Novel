package com.heiheilianzai.app.ui.dialog;

import android.app.Activity;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.UnderlineSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.Recommend;
import com.heiheilianzai.app.model.SigninSuccess;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 首页弹框推荐小说
 * Created by scb on 2018/10/31.
 */
public class MyPoPwindow {
    public void getSignPop(final Activity activity) {
        try {
            final String result = ShareUitls.getString(activity, "sign_pop", "");
            if (result.length() == 0) {
                return;
            }
            final SigninSuccess signinSuccess = new Gson().fromJson(result, SigninSuccess.class);
            if (signinSuccess == null) {
                return;
            }
            final List<Recommend.RecommendProduc> signBookList = signinSuccess.getBook();
            if (signBookList == null) {
                return;
            }
            int size = signBookList.size();
            View view = LayoutInflater.from(activity).inflate(R.layout.dialog_sign, null);
            final PopupWindow popupWindow;
            HolderSign holderSign = new HolderSign(view);
            popupWindow = new PopupWindow(view);
            popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
            popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
            if (size == 0) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holderSign.popwindow_sign_layout.getLayoutParams();
                layoutParams.height = ImageUtil.dp2px(activity, 283);
                layoutParams.width = ImageUtil.dp2px(activity, 160);
                holderSign.popwindow_sign_layout.setLayoutParams(layoutParams);
            } else {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) holderSign.popwindow_sign_layout.getLayoutParams();
                layoutParams.height = ImageUtil.dp2px(activity, 360);
                layoutParams.width = ImageUtil.dp2px(activity, 260);
                holderSign.popwindow_sign_layout.setLayoutParams(layoutParams);
            }
            SpannableString spannableString = new SpannableString(activity.getResources().getString(R.string.SigninActivity_no));
            UnderlineSpan underlineSpan = new UnderlineSpan();
            spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holderSign.popwindow_sign_no.setText(spannableString);
            spannableString.setSpan(underlineSpan, 0, spannableString.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
            holderSign.popwindow_sign_golds.setText(signinSuccess.getAward());
            holderSign.popwindow_sign_golds_tomorrow.setText(signinSuccess.getTomorrow_award());
            if (signBookList != null) {
                if (size == 0) {
                    holderSign.popwindow_sign_book_havebook.setVisibility(View.GONE);
                    holderSign.popwindow_sign_nobook.setVisibility(View.VISIBLE);
                    holderSign.popwindow_sign_nobook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });
                } else {
                    switch (size) {
                        case 1:
                            holderSign.popwindow_sign_book_layout2.setVisibility(View.GONE);
                            holderSign.popwindow_sign_book_layout3.setVisibility(View.GONE);
                            holderSign.popwindow_sign_view2.setVisibility(View.GONE);
                            holderSign.popwindow_sign_view3.setVisibility(View.GONE);
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(0).cover, holderSign.popwindow_sign_book1, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname1.setText(signBookList.get(0).name);
                            break;
                        case 2:
                            holderSign.popwindow_sign_book_layout3.setVisibility(View.GONE);
                            holderSign.popwindow_sign_view3.setVisibility(View.GONE);
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(0).cover, holderSign.popwindow_sign_book1, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname1.setText(signBookList.get(0).name);
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(1).cover, holderSign.popwindow_sign_book2, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname2.setText(signBookList.get(1).name);
                            break;
                        case 3:
                        default:
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(0).cover, holderSign.popwindow_sign_book1, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname1.setText(signBookList.get(0).name);
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(1).cover, holderSign.popwindow_sign_book2, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname2.setText(signBookList.get(1).name);
                            MyPicasso.GlideImageNoSize(activity, signBookList.get(2).cover, holderSign.popwindow_sign_book3, R.mipmap.book_def_v);
                            holderSign.popwindow_sign_bookname3.setText(signBookList.get(2).name);
                            break;
                    }
                    holderSign.popwindow_sign_no.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            popupWindow.dismiss();
                        }
                    });
                    holderSign.popwindow_sign_alladd.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            List<BaseBook> list = new ArrayList<>();
                            List<BaseComic> comics = new ArrayList<>();
                            for (Recommend.RecommendProduc addrecommendProducs : signBookList) {
                                if (addrecommendProducs.book_id != null) {
                                    BaseBook mBaseBook = new BaseBook();
                                    mBaseBook.setBook_id(addrecommendProducs.book_id);
                                    mBaseBook.setName(addrecommendProducs.name);
                                    mBaseBook.setCover(addrecommendProducs.cover);
                                    mBaseBook.setRecentChapter(addrecommendProducs.total_chapter);
                                    mBaseBook.setTotal_Chapter(addrecommendProducs.total_chapter);
                                    mBaseBook.setDescription(addrecommendProducs.description);
                                    mBaseBook.setName(addrecommendProducs.name);
                                    mBaseBook.saveIsexist(1);
                                    mBaseBook.setAddBookSelf(1);
                                    list.add(mBaseBook);
                                } else {
                                    BaseComic baseComic = new BaseComic();
                                    baseComic.setComic_id(addrecommendProducs.comic_id);
                                    baseComic.setName(addrecommendProducs.name);
                                    baseComic.setVertical_cover(addrecommendProducs.cover);
                                    baseComic.setRecentChapter(addrecommendProducs.total_chapter);
                                    baseComic.setTotal_chapters(addrecommendProducs.total_chapter);
                                    baseComic.setDescription(addrecommendProducs.description);
                                    baseComic.saveIsexist(true);
                                    baseComic.setAddBookSelf(true);
                                    comics.add(baseComic);
                                }
                            }
                            if (!list.isEmpty()) {
                                EventBus.getDefault().post(new RefreshBookSelf(list));
                            }
                            if (!comics.isEmpty()) {
                                EventBus.getDefault().post(new RefreshComic(comics));
                            }
                            popupWindow.dismiss();
                        }
                    });
                }
            }
            popupWindow.setFocusable(true);
            popupWindow.setOutsideTouchable(false);
            popupWindow.setAnimationStyle(R.style.sign_pop_anim);
            popupWindow.showAtLocation(activity.getWindow().getDecorView(), Gravity.CENTER, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        } catch (Exception e) {
        }
        ShareUitls.putString(activity, "sign_pop", "");
    }

    class HolderSign {
        @BindView(R.id.popwindow_sign_golds)
        public TextView popwindow_sign_golds;
        @BindView(R.id.popwindow_sign_golds_tomorrow)
        public TextView popwindow_sign_golds_tomorrow;
        @BindView(R.id.popwindow_sign_layout)
        public FrameLayout popwindow_sign_layout;
        @BindView(R.id.popwindow_sign_bookname1)
        public TextView popwindow_sign_bookname1;
        @BindView(R.id.popwindow_sign_bookname2)
        public TextView popwindow_sign_bookname2;
        @BindView(R.id.popwindow_sign_bookname3)
        public TextView popwindow_sign_bookname3;
        @BindView(R.id.popwindow_sign_book_layout)
        public LinearLayout popwindow_sign_book_layout;
        @BindView(R.id.popwindow_sign_book_layout1)
        public LinearLayout popwindow_sign_book_layout1;
        @BindView(R.id.popwindow_sign_book_layout2)
        public LinearLayout popwindow_sign_book_layout2;
        @BindView(R.id.popwindow_sign_book_layout3)
        public LinearLayout popwindow_sign_book_layout3;
        @BindView(R.id.popwindow_sign_book_havebook)
        public LinearLayout popwindow_sign_book_havebook;
        @BindView(R.id.popwindow_sign_view2)
        public View popwindow_sign_view2;
        @BindView(R.id.popwindow_sign_view3)
        public View popwindow_sign_view3;
        @BindView(R.id.popwindow_sign_book1)
        public ImageView popwindow_sign_book1;
        @BindView(R.id.popwindow_sign_book2)
        public ImageView popwindow_sign_book2;
        @BindView(R.id.popwindow_sign_book3)
        public ImageView popwindow_sign_book3;
        @BindView(R.id.popwindow_sign_no)
        public Button popwindow_sign_no;
        @BindView(R.id.popwindow_sign_alladd)
        public Button popwindow_sign_alladd;
        @BindView(R.id.popwindow_sign_nobook)
        public Button popwindow_sign_nobook;

        public HolderSign(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
