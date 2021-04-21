package com.heiheilianzai.app.ui.fragment.book;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ChapterNovelAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseTag;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogNovelCoupon;
import com.heiheilianzai.app.utils.DialogVip;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class NovelMuluFragment extends BaseButterKnifeFragment {
    @BindView(R.id.list_novel_mulu)
    public RecyclerView mListView;
    private String mBookId;
    private BaseBook baseBook;
    public List<ChapterItem> mItemList;
    public ChapterNovelAdapter mAdapter;
    private String coupon_pay_price;
    private int chapterItemSelect;

    @Override
    public int initContentView() {
        return R.layout.fragment_novel_mulu;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
    }

    public void sendData(BaseBook baseBook) {
        this.baseBook = baseBook;
        mBookId = baseBook.getBook_id();
        getChapters();
    }

    protected void getChapters() {
        ReaderParams params = new ReaderParams(getContext());
        params.putExtraParams("book_id", mBookId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(getActivity()).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterCatalogUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String result) {
                        initInfo(result);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    public void initInfo(final String json) {
        Gson gson = new Gson();
        mItemList = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray chapterListArr = jsonObject.getJSONArray("chapter_list");
            coupon_pay_price = jsonObject.getString("coupon_pay_price");
            AppPrefs.putSharedString(activity, PrefConst.COUPON_PRICE, coupon_pay_price);
            int size = chapterListArr.length();
            for (int i = 0; i < size; i++) {
                JSONObject jsonObject1 = chapterListArr.getJSONObject(i);
                ChapterItem chapterItem1 = new ChapterItem();
                BaseTag tag = gson.fromJson(jsonObject1.getString("tag"), BaseTag.class);
                chapterItem1.setBook_id(jsonObject.getString("book_id"));
                chapterItem1.setChaptertab(tag.getTab());
                chapterItem1.setChaptercolor(tag.getColor());
                chapterItem1.setIs_vip(jsonObject1.getString("is_vip"));
                chapterItem1.setChapter_title(jsonObject1.getString("chapter_title"));
                chapterItem1.setChapter_id(jsonObject1.getString("chapter_id"));
                chapterItem1.setIs_book_coupon_pay(jsonObject1.getString("is_book_coupon_pay"));
                mItemList.add(chapterItem1);
            }
            if (!mItemList.isEmpty()) {
                mAdapter = new ChapterNovelAdapter(getContext(), mItemList);
                mAdapter.current_chapter_id = mItemList.get(0).getChapter_id();
                mAdapter.setCoupon_pay_price(coupon_pay_price);
                mListView.setAdapter(mAdapter);
                mAdapter.setOnChapterListener(new ChapterNovelAdapter.OnChapterListener() {
                    @Override
                    public void onChapterSelect(ChapterItem chapterItem, int position) {
                        ReaderConfig.CatalogInnerActivityOpen = true;
                        chapterItemSelect = position;
                        if (activity != null) {
                            checkIsBuyCoupon(activity, chapterItem, json);
                        }
                    }
                });
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void checkIsBuyCoupon(Activity activity, ChapterItem chapterItem, String json) {
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
                            if (!StringUtils.isEmpty(is_book_coupon_pay) && is_book_coupon_pay.endsWith("1") && !App.isVip(getContext())) {
                                DialogNovelCoupon dialogNovelCoupon = new DialogNovelCoupon();
                                Dialog dialogVipPop = dialogNovelCoupon.getDialogVipPop(activity, chapterItem, false);
                                dialogNovelCoupon.setOnOpenCouponListener(new DialogNovelCoupon.OnOpenCouponListener() {
                                    @Override
                                    public void onOpenCoupon(boolean isBuy) {
                                        if (isBuy) {
                                            if (dialogVipPop != null) {
                                                dialogVipPop.dismiss();
                                            }
                                            ChapterManager.getInstance(getActivity()).openBook(baseBook, mBookId, chapterItem.getChapter_id(), json);
                                        }
                                    }
                                });
                                return;
                            }
                        }
                        String is_vip = chapterItem.getIs_vip();
                        if (is_vip != null && is_vip.equals("1") && !App.isVip(getContext())) {
                            DialogVip dialogVip = new DialogVip();
                            dialogVip.getDialogVipPop(getActivity(), false);
                            return;
                        }
                        ChapterManager.getInstance(getActivity()).openBook(baseBook, mBookId, chapterItem.getChapter_id(), json);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
