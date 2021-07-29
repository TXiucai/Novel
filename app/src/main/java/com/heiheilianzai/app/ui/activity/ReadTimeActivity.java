package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ReadTimeBean;
import com.heiheilianzai.app.model.TaskCenter;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.ToastUtil;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.heiheilianzai.app.view.ArcView;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.ReadTimeView;
import com.heiheilianzai.app.view.StepView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

public class ReadTimeActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout mBack;
    @BindView(R.id.titlebar_text)
    public TextView mTittle;
    @BindView(R.id.read_circle_img)
    public CircleImageView mImg;
    @BindView(R.id.read_name)
    public TextView mTxName;
    @BindView(R.id.read_min)
    public TextView mTxMin;
    @BindView(R.id.read_coupon)
    public TextView mTxCoupon;
    @BindView(R.id.read_time)
    public ReadTimeView mReadTime;
    @BindView(R.id.read_coupon_accept)
    public TextView mTxAccept;
    @BindView(R.id.read_rules)
    public TextView mTxRules;
    @BindView(R.id.av_time)
    public ArcView mAvTime;
    @BindView(R.id.tx_read_time_tip)
    public TextView mTxReadTip;
    private Activity mActivity;
    private int mAward;
    private int mMin;
    private List<ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean> mListAwards;
    private String mH5Url;

    @Override
    public int initContentView() {
        return R.layout.activity_read_time;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        setStatusTextColor(false, mActivity);
        mTittle.setText(LanguageUtil.getString(mActivity, R.string.string_read_time_tittle));
        getData();
        getGiftData();
    }

    private void getGiftData() {
        final ReaderParams params = new ReaderParams(this);
        params.putExtraParams("page_num", "1");
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.EXCHANGE_GIFT, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            mH5Url = jsonObject.getString("exchange_h5_url");
                            ShareUitls.putString(mActivity, "exchange_gift", result);
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

    private void getData() {
        final ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.READ_TIME, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            ReadTimeBean readTimeBean = new Gson().fromJson(result, ReadTimeBean.class);
                            initReadTime(readTimeBean);
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void initReadTime(ReadTimeBean readTimeBean) {
        if (readTimeBean != null) {
            ReadTimeBean.ListBean.AwardInfoBean award_info = readTimeBean.getList().getAward_info();
            String desc = readTimeBean.getList().getDesc();
            mMin = readTimeBean.getUser_today_read_total();
            mListAwards = award_info.getTask_daily_list();
            for (int i = 0; i < mListAwards.size(); i++) {
                ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean taskDailyListBean = mListAwards.get(i);
                taskDailyListBean.setMinute(taskDailyListBean.getMinute() + "分钟");
            }
            mAward = mMin % 10;
            ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean taskDailyListBean;
            if (mAward > 0) {
                if (mAward > 5) {
                    taskDailyListBean = mListAwards.get(5);
                } else {
                    taskDailyListBean = mListAwards.get(mAward);
                }
                int current_task_status = taskDailyListBean.getCurrent_task_status();
                if (current_task_status == 0 && taskDailyListBean.getAward() != null && Integer.valueOf(taskDailyListBean.getAward()) > 0) {
                    mTxAccept.setClickable(true);
                    mTxAccept.setBackground(getDrawable(R.drawable.shape_ff8350_20));
                } else {
                    mTxAccept.setClickable(false);
                    mTxAccept.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                }
                mTxReadTip.setVisibility(View.GONE);
            } else {
                mTxAccept.setClickable(false);
                mTxAccept.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                taskDailyListBean = mListAwards.get(mAward);
                mTxReadTip.setText(String.format(getResources().getString(R.string.string_continue_accept_coupon), 10 - mMin));
                mTxReadTip.setVisibility(View.VISIBLE);
            }
            ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean zeroBean = new ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean();
            zeroBean.setMinute(getString(R.string.string_zero_min));
            mListAwards.add(0, zeroBean);
            mReadTime.setStepNum(mListAwards, mAward, mMin);
            mTxAccept.setText(String.format(getResources().getString(R.string.string_read_time_coupon_accept), taskDailyListBean.getAward()));
            mTxCoupon.setText(String.format(getResources().getString(R.string.string_read_time_coupon), readTimeBean.getUser_history_award()));
            mTxMin.setText(String.valueOf(mMin));
            mTxRules.setText(desc);
            UserInfoItem userInfoItem = App.getUserInfoItem(mActivity);
            if (userInfoItem != null) {
                MyPicasso.IoadImage(this, userInfoItem.getAvatar(), R.mipmap.hold_user_avatar, mImg);
                mTxName.setText(String.valueOf(userInfoItem.getUid()));
            }
            mAvTime.setValues(1, 62, mMin, "");
        }
    }


    @OnClick(value = {R.id.read_coupon_accept, R.id.titlebar_back, R.id.read_exchange_go})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.read_coupon_accept:
                if (Utils.isLogin(mActivity)) {
                    acceptCoupon();
                } else {
                    MainHttpTask.getInstance().Gotologin(mActivity);
                }
                break;
            case R.id.read_exchange_go:
                if (Utils.isLogin(mActivity) && mH5Url != null) {
                    startActivity(new Intent(mActivity, AboutActivity.class).
                            putExtra("url", mH5Url).putExtra("type", "boyin").putExtra("title", getString(R.string.string_exchage_gift)));
                } else {
                    MainHttpTask.getInstance().Gotologin(mActivity);
                }
                break;
        }
    }


    private void acceptCoupon() {
        final ReaderParams params = new ReaderParams(this);
        params.putExtraParams("read_minute", String.valueOf(mMin));
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.READ_TIME_ACCEPT, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            mTxAccept.setClickable(false);
                            mTxAccept.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                            mAward++;
                            ReadTimeBean.ListBean.AwardInfoBean.TaskDailyListBean taskDailyListBean;
                            if (mAward > 5) {
                                taskDailyListBean = mListAwards.get(5);
                            } else {
                                taskDailyListBean = mListAwards.get(mAward);
                            }
                            mTxAccept.setText(String.format(getResources().getString(R.string.string_read_time_coupon_accept), taskDailyListBean.getAward()));
                            MyToash.Toash(mActivity, getString(R.string.string_accept_coupon_success));
                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }
}
