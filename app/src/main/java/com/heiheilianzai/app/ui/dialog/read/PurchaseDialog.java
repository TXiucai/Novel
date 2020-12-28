package com.heiheilianzai.app.ui.dialog.read;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.Downoption;
import com.heiheilianzai.app.model.PurchaseItem;
import com.heiheilianzai.app.ui.activity.setting.SettingsActivity;
import com.heiheilianzai.app.ui.dialog.DownDialog;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.zcw.togglebutton.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


/**
 * Created by scb on 2018/8/16
 */
public class PurchaseDialog extends Dialog {
    @BindView(R.id.dialog_purchase_some_select_rgs)
    RadioGroup dialog_purchase_some_select_rgs;
    @BindView(R.id.dialog_purchase_some_remain)
    TextView dialog_purchase_some_remain;
    @BindView(R.id.dialog_purchase_some_auto_buy)
    ToggleButton dialog_purchase_some_auto_buy;
    @BindView(R.id.dialog_purchase_some_total_price)
    TextView dialog_purchase_some_total_price;
    @BindView(R.id.dialog_purchase_some_original_price)
    TextView dialog_purchase_some_original_price;
    @BindView(R.id.dialog_purchase_some_buy)
    Button dialog_purchase_some_buy;
    @BindView(R.id.dialog_purchase_some_tite)
    TextView dialog_purchase_some_tite;
    @BindView(R.id.dialog_purchase_auto)
    RelativeLayout dialog_purchase_auto;
    @BindView(R.id.dialog_purchase_HorizontalScrollView)
    HorizontalScrollView dialog_purchase_HorizontalScrollView;
    //用以标识是去充值还是去购买
    private int mFlag = 0;
    private Activity mContext;
    private int mNum;
    boolean isdown;
    Downoption downoption;
    private ReadingConfig config;

    public PurchaseDialog(Context context, boolean isdown) {
        this(context, R.style.BottomDialog);
        this.isdown = isdown;
        mContext = (Activity) context;
    }

    public PurchaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_purchase_some);
        setCanceledOnTouchOutside(true);
        // 初始化View注入
        ButterKnife.bind(this);
        if (isdown) {
            dialog_purchase_auto.setVisibility(View.GONE);
            dialog_purchase_HorizontalScrollView.setVisibility(View.GONE);
        }
        dialog_purchase_some_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        config = ReadingConfig.getInstance();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
    }

    public void initData(final String bookId, final String chapterId, Downoption downoption, final String title) {
        String requestParams;
        if (isdown) {
            requestParams = ReaderConfig.getBaseUrl() + "/chapter/buy-option";
        } else {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mChapterBuyIndex;
        }
        ReaderParams params = new ReaderParams(mContext);
        if (downoption != null) {
            this.downoption = downoption;
            params.putExtraParams("num", downoption.down_num + "");
        }
        params.putExtraParams("book_id", bookId);
        params.putExtraParams("chapter_id", chapterId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mContext).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            Gson gson = new Gson();
                            JSONObject dataObjJ = new JSONObject(result);
                            JSONObject dataObj = dataObjJ.getJSONObject("base_info");
                            final int remainNum = dataObj.getInt("remain");
                            final String remainNum0 = dataObj.getString("gold_remain");
                            final String remainNum1 = dataObj.getString("unit");
                            final String remainNum2 = dataObj.getString("subUnit");
                            final String remainNum3 = dataObj.getString("silver_remain");
                            dialog_purchase_some_remain.setText(remainNum0 + remainNum1 + " / " + remainNum3 + remainNum2);
                            final List<PurchaseItem> list = new ArrayList<PurchaseItem>();
                            if (!isdown) {
                                JSONArray optionArr = dataObjJ.getJSONArray("buy_option");
                                for (int i = 0; i < optionArr.length(); i++) {
                                    PurchaseItem item = gson.fromJson(optionArr.getString(i), PurchaseItem.class);
                                    list.add(item);
                                }
                            } else {
                                list.add(gson.fromJson(dataObjJ.getString("buy_option"), PurchaseItem.class));
                            }
                            dialog_purchase_some_select_rgs.removeAllViews();
                            dialog_purchase_some_select_rgs.setOrientation(RadioGroup.HORIZONTAL);
                            if (!isdown) {
                                for (int k = 0; k < list.size(); k++) {
                                    RadioButton radioButton = (RadioButton) LayoutInflater.from(mContext).inflate(R.layout.activity_radiobutton_purchase, null, false);
                                    radioButton.setId(k);
                                    radioButton.setBackgroundResource(R.drawable.selector_purchase_some);
                                    RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, ImageUtil.dp2px(mContext, 20));
                                    params.rightMargin = 10;
                                    if (list.size() >= 2) {
                                        if (k == 1) {
                                            radioButton.setChecked(true);
                                        }
                                    } else {
                                        if (k == 0) {
                                            radioButton.setChecked(true);
                                        }
                                    }
                                    radioButton.setText(list.get(k).getLabel());
                                    dialog_purchase_some_select_rgs.addView(radioButton, params);
                                }
                            } else {
                                dialog_purchase_some_tite.setText(title);
                            }
                            PurchaseItem moren = null;
                            if (list.size() == 1) {
                                moren = list.get(0);
                            } else if (list.size() >= 2) {
                                moren = list.get(1);
                            }
                            if (moren != null) {
                                dialog_purchase_some_total_price.setText(moren.actual_cost.gold_cost + remainNum1 + "+" + moren.actual_cost.silver_cost + remainNum2);
                                dialog_purchase_some_original_price.setText(moren.original_cost.gold_cost + remainNum1 + "+" + moren.original_cost.silver_cost + remainNum2);
                                mNum = moren.getBuy_num();
                                //余额不足，提示"充值并购买"
                                if (remainNum < moren.getTotal_price()) {
                                    dialog_purchase_some_buy.setText(LanguageUtil.getString(mContext, R.string.ReadActivity_chongzhibuy));
                                    mFlag = 0;
                                } else {//余额够显示"确认购买"
                                    dialog_purchase_some_buy.setText(LanguageUtil.getString(mContext, R.string.ReadActivity_buy));
                                    mFlag = 1;
                                }
                            }
                            dialog_purchase_some_select_rgs.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    dialog_purchase_some_total_price.setText(list.get(checkedId).actual_cost.gold_cost + remainNum1 + "+" + list.get(checkedId).actual_cost.silver_cost + remainNum2);
                                    dialog_purchase_some_original_price.setText(list.get(checkedId).original_cost.gold_cost + remainNum1 + "+" + list.get(checkedId).original_cost.silver_cost + remainNum2);
                                    mNum = list.get(checkedId).getBuy_num();
                                    if (remainNum < list.get(checkedId).getTotal_price()) {
                                        dialog_purchase_some_buy.setText(LanguageUtil.getString(mContext, R.string.ReadActivity_chongzhibuy));
                                        mFlag = 0;
                                    } else {
                                        dialog_purchase_some_buy.setText(LanguageUtil.getString(mContext, R.string.ReadActivity_buy));
                                        mFlag = 1;
                                    }
                                }
                            });

                            if (AppPrefs.getSharedBoolean(mContext, ReaderConfig.AUTOBUY, true)) {
                                dialog_purchase_some_auto_buy.setToggleOn();
                            } else {
                                dialog_purchase_some_auto_buy.setToggleOff();
                            }
                            dialog_purchase_some_auto_buy.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
                                @Override
                                public void onToggle(boolean on) {
                                    SettingsActivity.Auto_sub(mContext, new SettingsActivity.Auto_subSuccess() {
                                        @Override
                                        public void success(boolean open) {
                                            if (open) {
                                                dialog_purchase_some_auto_buy.setToggleOn();
                                            } else {
                                                dialog_purchase_some_auto_buy.setToggleOff();
                                            }
                                        }
                                    });
                                }
                            });
                            dialog_purchase_some_buy.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    dismiss();
                                    if (!MainHttpTask.getInstance().Gotologin(mContext)) {
                                        return;
                                    }
                                    purchaseSingleChapter(bookId, chapterId, mNum);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void purchaseSingleChapter(final String book_id, final String chapter_id, final int num) {
        ReaderParams params = new ReaderParams(mContext);
        params.putExtraParams("book_id", book_id);
        params.putExtraParams("chapter_id", chapter_id);
        params.putExtraParams("num", num + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mContext).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterBuy, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String[] strings = new Gson().fromJson(object.getString("chapter_ids"), String[].class);
                            for (String chapter_id : strings) {
                                ContentValues values = new ContentValues();
                                values.put("is_preview", "0");
                                LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id,chapter_id);
                                if (DownDialog.isreaderbook || !isdown) {
                                    ChapterManager.getInstance(mContext).getChapter(chapter_id, new ChapterManager.QuerychapterItemInterface() {

                                        @Override
                                        public void success(ChapterItem querychapterItem) {
                                            querychapterItem.setIs_preview("0");
                                        }

                                        @Override
                                        public void fail() {
                                        }
                                    });
                                }
                            }
                            if (!isdown) {
                                downloadWithoutAutoBuy(book_id, chapter_id);
                            } else {
                                if (DownDialog.isreaderbook) {
                                    downloadWithoutAutoBuy(book_id, chapter_id);
                                }
                                MyToash.ToashSuccess(mContext, LanguageUtil.getString(mContext, R.string.ReadActivity_buysuccessyidown));
                                new DownDialog().getdown_url(mContext, downoption);
                            }
                            ReaderConfig.integerList.add(1);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void downloadWithoutAutoBuy(final String book_id, final String chapter_id) {
        ReaderParams params = new ReaderParams(mContext);
        params.putExtraParams("book_id", book_id);
        params.putExtraParams("chapter_id", chapter_id);
        params.putExtraParams("num", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mContext).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mChapterDownUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            MyToash.ToashSuccess(mContext, LanguageUtil.getString(mContext, R.string.ReadActivity_buysuccess));
                            JSONArray jsonArray = new JSONArray(result);
                            ChapterContent chapterContent = new Gson().fromJson(jsonArray.getString(0), ChapterContent.class);
                            ChapterManager.getInstance(mContext).getCurrentChapter().setIs_preview(chapterContent.getIs_preview());
                            ChapterManager.getInstance(mContext).getCurrentChapter().setUpdate_time(chapterContent.getUpdate_time());
                            ContentValues values = new ContentValues();
                            String filepath = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(chapter_id + "/").concat(chapterContent.getIs_preview() + "/").concat(chapterContent.getIs_new_content()+"/").concat(chapterContent.getUpdate_time()).concat(".txt");
                            FileManager.createFile(filepath, chapterContent.getContent().getBytes());
                            values.put("chapteritem_begin", 0);
                            values.put("chapter_path", filepath);
                            values.put("update_time", chapterContent.getUpdate_time());
                            values.put("is_preview", "0");
                            ChapterManager.getInstance(mContext).getCurrentChapter().setIs_preview("0");
                            ChapterManager.getInstance(mContext).getCurrentChapter().setChapter_path(filepath);
                            LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id,chapter_id);
                            ChapterManager.getInstance(mContext).openCurrentChapter(chapter_id);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
