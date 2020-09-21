package com.heiheilianzai.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.callback.LoginResultCallback;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.model.event.AppUpdateLoadOverEvent;
import com.heiheilianzai.app.model.event.LoginBoYinEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.FeedBackActivity;
import com.heiheilianzai.app.ui.activity.RechargeActivity;
import com.heiheilianzai.app.ui.activity.ShareActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.UserInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.activity.setting.SettingsActivity;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.DOWN;
import static com.heiheilianzai.app.constant.ReaderConfig.LIUSHUIJIELU;
import static com.heiheilianzai.app.constant.ReaderConfig.MYCOMMENT;
import static com.heiheilianzai.app.constant.ReaderConfig.READHISTORY;
import static com.heiheilianzai.app.constant.ReaderConfig.USE_AD_FINAL;
import static com.heiheilianzai.app.constant.ReaderConfig.getCurrencyUnit;
import static com.heiheilianzai.app.constant.ReaderConfig.getSubUnit;

/**
 * 我的
 */
public class MineNewFragment extends BaseButterKnifeFragment {
    public String TAG = MineNewFragment.class.getSimpleName();
    @BindView(R.id.fragment_mine_user_info_avatar)
    public CircleImageView fragment_mine_user_info_avatar;
    @BindView(R.id.fragment_mine_user_info_sex)
    public ImageView fragment_mine_user_info_sex;
    @BindView(R.id.fragment_mine_user_info_nickname)
    public TextView fragment_mine_user_info_nickname;
    @BindView(R.id.fragment_mine_user_info_id)
    public TextView fragment_mine_user_info_id;
    @BindView(R.id.fragment_mine_user_info_nologin)
    public LinearLayout fragment_mine_user_info_nologin;
    @BindView(R.id.fragment_mine_user_info_gold_unit)
    public TextView fragment_mine_user_info_gold_unit;
    @BindView(R.id.fragment_mine_user_info_shuquan_unit)
    public TextView fragment_mine_user_info_shuquan_unit;
    @BindView(R.id.fragment_mine_user_info_recharge_song)
    public TextView fragment_mine_user_info_recharge_song;
    @BindView(R.id.fragment_mine_user_info_friends_song)
    public TextView fragment_mine_user_info_friends_song;
    @BindView(R.id.fragment_mine_user_info_recharge_text)
    public TextView fragment_mine_user_info_recharge_text;
    @BindView(R.id.fragment_mine_user_info_money_layout)
    public LinearLayout fragment_mine_user_info_money_layout;
    @BindView(R.id.fragment_mine_user_info_paylayout_history)
    public LinearLayout fragment_mine_user_info_paylayout_history;
    @BindView(R.id.fragment_mine_user_info_paylayout_downmanager)
    public LinearLayout fragment_mine_user_info_paylayout_downmanager;
    @BindView(R.id.fragment_mine_user_info_gold)
    public TextView fragment_mine_user_info_gold;
    @BindView(R.id.fragment_mine_user_info_shuquan)
    public TextView fragment_mine_user_info_shuquan;
    @BindView(R.id.fragment_mine_user_info_tasklayout_task)
    public TextView fragment_mine_user_info_tasklayout_task;
    @BindView(R.id.fragment_mine_user_info_isvip)
    public ImageView fragment_mine_user_info_isvip;
    @BindView(R.id.fragment_mine_user_info_paylayout)
    public LinearLayout fragment_mine_user_info_paylayout;
    @BindView(R.id.fragment_mine_user_info_paylayout_view)
    public View fragment_mine_user_info_paylayout_view;
    @BindView(R.id.fragment_mine_user_info_gold_layout)
    public View fragment_mine_user_info_gold_layout;
    @BindView(R.id.fragment_mine_user_info_paylayout_recharge)
    public View fragment_mine_user_info_paylayout_recharge;
    @BindView(R.id.fragment_mine_user_info_paylayout_recharge_lv)
    public View fragment_mine_user_info_paylayout_recharge_lv;
    @BindView(R.id.fragment_mine_user_info_paylayout_vip)
    public View fragment_mine_user_info_paylayout_vip;
    @BindView(R.id.fragment_mine_user_info_paylayout_vip_lv)
    public View fragment_mine_user_info_paylayout_vip_lv;

    String connect_us;
    Gson gson = new Gson();
    public UserInfoItem mUserInfo;

    @Override
    public int initContentView() {
        return R.layout.fragment_mine_new;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fragment_mine_user_info_gold_unit.setText(getCurrencyUnit(activity));
        fragment_mine_user_info_shuquan_unit.setText(getSubUnit(activity));
        fragment_mine_user_info_recharge_song.setText(String.format(LanguageUtil.getString(activity, R.string.MineNewFragment_song), getSubUnit(activity)));
        fragment_mine_user_info_friends_song.setText(String.format(LanguageUtil.getString(activity, R.string.MineNewFragment_song), getSubUnit(activity)));
        fragment_mine_user_info_recharge_text.setText(String.format(LanguageUtil.getString(activity, R.string.MineNewFragment_chongzhi), getCurrencyUnit(activity)));
        if (!ReaderConfig.USE_PAY) {
            fragment_mine_user_info_money_layout.setVisibility(View.GONE);
            fragment_mine_user_info_paylayout.setVisibility(View.GONE);
            fragment_mine_user_info_paylayout_view.setVisibility(View.GONE);
        }
        MainHttpTask.getInstance().getResultString(activity, "Mine", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                initInfo(result, null);
            }
        });
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) fragment_mine_user_info_isvip.getLayoutParams();
        layoutParams.height = ImageUtil.dp2px(activity, 14);
        layoutParams.width = layoutParams.height * 138 / 48;
        fragment_mine_user_info_isvip.setLayoutParams(layoutParams);
        uiFreeCharge();
        setVipView();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (ReaderConfig.REFREASH_USERCENTER) {
            refreshData();
            ReaderConfig.REFREASH_USERCENTER = false;
        }
    }

    public void initInfo(final String info, UserInfoItem userInfoItem) {
        if (!Utils.isLogin(activity)) {
            fragment_mine_user_info_nologin.setVisibility(View.GONE);
            return;
        } else {
            fragment_mine_user_info_nologin.setVisibility(View.VISIBLE);
        }
        try {
            if (userInfoItem != null) {
                mUserInfo = userInfoItem;
            } else
                mUserInfo = gson.fromJson(info, UserInfoItem.class);
            if (mUserInfo.getIs_vip() == 1) {
                fragment_mine_user_info_isvip.setImageResource(R.mipmap.icon_isvip);
                if (USE_AD_FINAL) {
                    ReaderConfig.USE_AD = false;
                }
            } else {
                fragment_mine_user_info_isvip.setImageResource(R.mipmap.icon_novip);
                if (USE_AD_FINAL) {
                    ReaderConfig.USE_AD = ReaderConfig.ad_switch == 1;
                }
            }
            if (mUserInfo.getAuto_sub() == 0) {
                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, false);
            } else {
                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, true);
            }
            String sharedUserInfo = StringUtils.isEmpty(info) ? (userInfoItem != null ? gson.toJson(userInfoItem) : "") : info;
            if (!StringUtils.isEmpty(sharedUserInfo)) {
                AppPrefs.putSharedString(activity, PrefConst.USER_INFO_KAY, sharedUserInfo);
            }
            MyPicasso.GlideImageNoSize(activity, mUserInfo.getAvatar(), fragment_mine_user_info_avatar, R.mipmap.icon_def_head);
            fragment_mine_user_info_nickname.setText(mUserInfo.getNickname());
            fragment_mine_user_info_id.setText("ID:  " + mUserInfo.getUid());
            fragment_mine_user_info_tasklayout_task.setText(mUserInfo.getTask_list().getFinish_num() + "/" + mUserInfo.getTask_list().getMission_num());
            fragment_mine_user_info_gold.setText(mUserInfo.getGoldRemain() + " ");
            fragment_mine_user_info_shuquan.setText(mUserInfo.getSilverRemain() + " ");
            String mobile = mUserInfo.getMobile();
            if (!StringUtils.isEmpty(mobile)) {
                loginBoYin(mobile);
                ShareUitls.putString(activity, PrefConst.USER_MOBILE_KAY, mobile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        ReaderConfig.REFREASH_USERCENTER = false;
        if (!Utils.isLogin(activity)) {//
            fragment_mine_user_info_nickname.setText(LanguageUtil.getString(activity, R.string.user_login));
            fragment_mine_user_info_id.setText("");
            fragment_mine_user_info_gold.setText("--");
            fragment_mine_user_info_shuquan.setText("--");
            fragment_mine_user_info_tasklayout_task.setText("--");
            fragment_mine_user_info_avatar.setImageResource(R.mipmap.hold_user_avatar);
            fragment_mine_user_info_isvip.setImageResource(R.mipmap.icon_novip);
            fragment_mine_user_info_nologin.setVisibility(View.GONE);
            return;
        } else {
            fragment_mine_user_info_nologin.setVisibility(View.VISIBLE);
        }
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUserCenterUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initInfo(result, null);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @OnClick(value = {R.id.fragment_mine_user_info_avatar,
            R.id.fragment_mine_user_info_paylayout_recharge, R.id.fragment_mine_user_info_paylayout_vip,// R.id.fragment_mine_user_info_paylayout_rechargenotes,
            R.id.fragment_mine_user_info_tasklayout_mybookcomment,
            R.id.fragment_mine_user_info_tasklayout_feedback, R.id.fragment_mine_user_info_tasklayout_set,
            R.id.fragment_mine_user_info_tasklayout_friends, R.id.fragment_mine_user_info_nickname,
            R.id.fragment_mine_user_info_tasklayout_layout, R.id.fragment_mine_user_info_tasklayout_layout2, R.id.fragment_mine_user_info_shuquan_layout,
            R.id.fragment_mine_user_info_gold_layout, R.id.fragment_mine_user_info_paylayout_downmanager,
            R.id.fragment_mine_user_info_paylayout_history, R.id.fragment_mine_user_info_lianxi, R.id.fragment_mine_user_info_tasklayout_share
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_mine_user_info_lianxi:
                if (connect_us == null) {
                    ReaderParams params = new ReaderParams(activity);
                    String json = params.generateParamsJson();
                    HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + "/user/connect-us", json, true, new HttpUtils.ResponseListener() {
                                @Override
                                public void onResponse(final String result) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(result);
                                        connect_us = jsonObject.getString("connect_us");
                                        startActivity(new Intent(activity, AboutActivity.class).
                                                putExtra("url", connect_us).
                                                putExtra("title", "联系我们"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onErrorResponse(String ex) {
                                }
                            }
                    );
                } else {
                    startActivity(new Intent(activity, AboutActivity.class).
                            putExtra("url", connect_us).
                            putExtra("title", "联系我们"));
                }
                break;
            case R.id.fragment_mine_user_info_avatar:

            case R.id.fragment_mine_user_info_nickname:
                if (!Utils.isLogin(activity)) {//登录状态跳个人资料
                    MainHttpTask.getInstance().Gotologin(activity);
                } else {
                    HandleOnclick(view, "fragment_mine_user_info_avatar");
                }
                break;
            case R.id.fragment_mine_user_info_gold_layout:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_rechargenotes");
                break;
            case R.id.fragment_mine_user_info_shuquan_layout:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_rechargenotes2");
                break;
            case R.id.fragment_mine_user_info_paylayout_recharge:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_recharge");
                break;
            case R.id.fragment_mine_user_info_paylayout_vip:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_vip");
                break;
            case R.id.fragment_mine_user_info_tasklayout_layout2:
            case R.id.fragment_mine_user_info_tasklayout_layout:
                HandleOnclick(view, "fragment_mine_user_info_tasklayout_layout");
                break;
            case R.id.fragment_mine_user_info_tasklayout_mybookcomment:
                HandleOnclick(view, "fragment_mine_user_info_tasklayout_mybookcomment");
                break;
            case R.id.fragment_mine_user_info_tasklayout_feedback:
                if (!Utils.isLogin(activity)) {
                    GetDialog.IsOperation(activity, "登录后才可以进行反馈哦~", "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            MainHttpTask.getInstance().Gotologin(activity);
                        }
                    });
                } else
                    HandleOnclick(view, "fragment_mine_user_info_tasklayout_feedback");
                break;
            case R.id.fragment_mine_user_info_tasklayout_set:
                SettingsActivity.chengeLangaupage = false;
                startActivity(new Intent(activity, SettingsActivity.class));
                break;
            case R.id.fragment_mine_user_info_tasklayout_friends:
                MyShare.ShareAPP(activity);
                break;
            case R.id.fragment_mine_user_info_paylayout_history:
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", READHISTORY).putExtra("title", LanguageUtil.getString(activity, R.string.noverfragment_yuedulishi)));
                break;
            case R.id.fragment_mine_user_info_paylayout_downmanager:
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", DOWN).putExtra("title", LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger)));
                break;
            case R.id.fragment_mine_user_info_tasklayout_share:
                startActivity(new Intent(activity, ShareActivity.class));
                break;
        }
    }

    public void HandleOnclick(View v, String flag) {//Utils.isLogin(activity)
        if (true) {//登录状态跳个人资料
            Intent intent = new Intent();
            switch (flag) {
                case "fragment_mine_user_info_avatar":
                    intent.setClass(activity, UserInfoActivity.class);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_paylayout_recharge":
                    intent.setClass(activity, RechargeActivity.class);
                    intent.putExtra("isvip", false);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_paylayout_vip":
                    if (Utils.isLogin(activity)) {
                        intent.setClass(activity, AcquireBaoyueActivity.class);
                        intent.putExtra("isvip", true);
                        startActivity(intent);
                    } else {
                        GetDialog.IsOperation(activity, getString(R.string.MineNewFragment_nologin_prompt), "", new GetDialog.IsOperationInterface() {
                            @Override
                            public void isOperation() {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                        });
                    }
                    break;
                case "fragment_mine_user_info_paylayout_rechargenotes":
                    intent.setClass(activity, BaseOptionActivity.class).putExtra("OPTION", LIUSHUIJIELU).putExtra("title", LanguageUtil.getString(activity, R.string.liushuijilu_title)).putExtra("Extra", false);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_paylayout_rechargenotes2":
                    intent.setClass(activity, BaseOptionActivity.class).putExtra("title", LanguageUtil.getString(activity, R.string.liushuijilu_title)).putExtra("OPTION", LIUSHUIJIELU).putExtra("Extra", true);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_tasklayout_layout":
                    if (Utils.isLogin(activity)) {
                        intent.setClass(activity, TaskCenterActivity.class);
                        startActivity(intent);
                    } else {
                        MainHttpTask.getInstance().Gotologin(activity);
                    }
                    break;
                case "fragment_mine_user_info_tasklayout_mybookcomment":
                    startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", MYCOMMENT).putExtra("title", LanguageUtil.getString(activity, R.string.MineNewFragment_shuping)));
                    break;
                case "fragment_mine_user_info_tasklayout_feedback":
                    intent.setClass(activity, FeedBackActivity.class);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_tasklayout_set":
                    intent.setClass(activity, SettingsActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshMine refreshMine) {
        if (refreshMine.userInfoItem == null) {
            refreshData();
        } else {
            initInfo(null, refreshMine.userInfoItem);
        }
    }

    /**
     * 根据 本地配置 判断是否免费调整UI变化
     */
    private void uiFreeCharge() {
        uiFreeCharge(fragment_mine_user_info_paylayout_recharge, fragment_mine_user_info_gold_layout, fragment_mine_user_info_isvip, fragment_mine_user_info_paylayout_recharge_lv, fragment_mine_user_info_paylayout_vip, fragment_mine_user_info_paylayout_vip_lv);
    }

    /**
     * 登录成功 或其他地方改变用户数据 刷新波音数据
     *
     * @param mobile
     */
    void loginBoYin(String mobile) {
        LoginModel loginModel = new LoginModel(getActivity());
        loginModel.loginBoYin(mobile, new LoginResultCallback() {
            @Override
            public void getResult(String jsonStr) {
                EventBus.getDefault().post(new LoginBoYinEvent(jsonStr));
            }
        });
    }

    /**
     * 根据 后台配置 付费开关判断是否开启
     */
    void setVipView() {
        uiFreeCharge(ReaderConfig.newInstance().app_free_charge, fragment_mine_user_info_isvip, fragment_mine_user_info_paylayout_recharge_lv, fragment_mine_user_info_paylayout_vip, fragment_mine_user_info_paylayout_vip_lv);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void eventAppUpdateLoadOver(AppUpdateLoadOverEvent event) {
        if (event != null) {
            setVipView();
        }
    }
}