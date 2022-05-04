package com.heiheilianzai.app.ui.dialog.comic;

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
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.PurchaseItem;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.event.comic.ComicinfoMuluBuy;
import com.heiheilianzai.app.ui.activity.setting.SettingsActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;
import com.zcw.togglebutton.ToggleButton;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.heiheilianzai.app.constant.ComicConfig.COMIC_buy_buy;
import static com.heiheilianzai.app.constant.ReaderConfig.getCurrencyUnit;

//import com.squareup.okhttp.internal.Util;

/**
 * 漫画章节购买弹框
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

    /**
     * 用以标识是去充值还是去购买
     */
    private int mFlag = 0;
    private Activity mContext;
    private int mNum;
    boolean isdown;
    BuySuccess buySuccess;
    Gson gson = new Gson();
    boolean CanceledOnTouchOutside;

    public interface BuySuccess {
        void buySuccess(String[] ids, int num);
    }

    public PurchaseDialog(Activity context, boolean isdown, BuySuccess buySuccess, boolean CanceledOnTouchOutside) {
        this(context, R.style.BottomDialog);
        this.isdown = isdown;
        this.buySuccess = buySuccess;
        this.CanceledOnTouchOutside = CanceledOnTouchOutside;
        mContext = context;
    }

    public PurchaseDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_purchase_some_comic);
        setCanceledOnTouchOutside(CanceledOnTouchOutside);
        // 初始化View注入
        ButterKnife.bind(this);
        if (isdown) {
            dialog_purchase_auto.setVisibility(View.GONE);
            dialog_purchase_HorizontalScrollView.setVisibility(View.GONE);
        }
        dialog_purchase_some_original_price.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
        dialog_purchase_some_remain.setText("0" + getCurrencyUnit(mContext));
    }

    public void initData(final String comic_id, final String chapterId) {
        String requestParams = ReaderConfig.getBaseUrl() + ComicConfig.COMIC_buy_index;
        ReaderParams params = new ReaderParams(mContext);
        if (isdown) {
            params.putExtraParams("page_from", "down");
        } else {
            params.putExtraParams("page_from", "read");
        }
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("chapter_id", chapterId);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mContext).sendRequestRequestParams3(requestParams, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject dataObjJ = new JSONObject(result);
                            JSONObject dataObj = dataObjJ.getJSONObject("base_info");
                            final int remainNum = dataObj.getInt("remain");
                            final String remainNum1 = dataObj.getString("unit");
                            final String remainNum2 = dataObj.getString("subUnit");
                            final int remainNum3 = dataObj.getInt("silver_remain");
                            dialog_purchase_some_remain.setText(remainNum3 + remainNum2);
                            final List<PurchaseItem> list = new ArrayList<PurchaseItem>();
                            JSONArray optionArr = dataObjJ.getJSONArray("buy_option");
                            for (int i = 0; i < optionArr.length(); i++) {
                                PurchaseItem item = gson.fromJson(optionArr.getString(i), PurchaseItem.class);
                                list.add(item);
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
                                    if (k == 0) {
                                        radioButton.setChecked(true);
                                    }
                                    radioButton.setText(list.get(k).getLabel());
                                    dialog_purchase_some_select_rgs.addView(radioButton, params);
                                }
                            } else {
                                dialog_purchase_some_tite.setText(list.get(0).getLabel());
                            }
                            PurchaseItem moren = null;
                            moren = list.get(0);
                            if (moren != null) {
                                dialog_purchase_some_total_price.setText(moren.actual_cost.gold_cost + remainNum2);
                                dialog_purchase_some_original_price.setText(moren.original_cost.gold_cost + remainNum2);
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
                                    dialog_purchase_some_total_price.setText(list.get(checkedId).actual_cost.gold_cost + remainNum2);
                                    dialog_purchase_some_original_price.setText(list.get(checkedId).original_cost.gold_cost + remainNum2);
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
                            if (dataObj.getInt("auto_sub") == 1) {
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
                                    if (Utils.isLogin(mContext)) {
                                        purchaseSingleChapter(comic_id, chapterId, mNum);
                                    } else {
                                        MainHttpTask.getInstance().Gotologin(mContext);
                                    }
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

    /**
     * 漫画章节购买
     */
    public void purchaseSingleChapter(final String comic_id, final String chapter_id, final int num) {
        ReaderParams params = new ReaderParams(mContext);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("chapter_id", chapter_id);
        params.putExtraParams("num", num + "");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mContext).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + COMIC_buy_buy, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            String[] strings = gson.fromJson(object.getString("chapter_ids"), String[].class);
                            MyToash.ToashSuccess(mContext, LanguageUtil.getString(mContext, R.string.ReadActivity_buysuccess));
                            if (strings.length > 0) {
                                for (String id : strings) {
                                    ContentValues values = new ContentValues();
                                    values.put("is_preview", 0);
                                    LitePal.updateAll(ComicChapter.class, values, "comic_id = ? and chapter_id = ?", comic_id, id);
                                }
                            }
                            buySuccess.buySuccess(strings, num);
                            EventBus.getDefault().post(new ComicinfoMuluBuy(num, strings));
                            dismiss();
                        } catch (
                                JSONException e) {
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
