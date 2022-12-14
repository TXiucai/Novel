package com.heiheilianzai.app.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.MineIconAdAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.callback.LoginResultCallback;
import com.heiheilianzai.app.callback.OnItemClickListener;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.holder.CBViewHolderCreator;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.LoginModel;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.AcceptMineFragment;
import com.heiheilianzai.app.model.event.FreshUrlEvent;
import com.heiheilianzai.app.model.event.InviteCodeEvent;
import com.heiheilianzai.app.model.event.LoginBoYinEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.AddressActivity;
import com.heiheilianzai.app.ui.activity.AnnounceActivity;
import com.heiheilianzai.app.ui.activity.BookSelfActivity;
import com.heiheilianzai.app.ui.activity.FeedBackActivity;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.ui.activity.MyShareActivity;
import com.heiheilianzai.app.ui.activity.ReadTimeActivity;
import com.heiheilianzai.app.ui.activity.RechargeActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.UserInfoActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.setting.SettingsActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.ConvenientBannerBookShelf;
import com.heiheilianzai.app.view.MarqueeView;
import com.heiheilianzai.app.view.MineAdBannerHolderView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.ReaderConfig.DOWN;
import static com.heiheilianzai.app.constant.ReaderConfig.LIUSHUIJIELU;
import static com.heiheilianzai.app.constant.ReaderConfig.MYCOMMENT;
import static com.heiheilianzai.app.constant.ReaderConfig.READHISTORY;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * ??????
 */
public class MineNewFragment extends BaseButterKnifeFragment {
    public String TAG = MineNewFragment.class.getSimpleName();
    @BindView(R.id.fragment_mine_user_info_avatar)
    public CircleImageView fragment_mine_user_info_avatar;
    @BindView(R.id.fragment_mine_user_info_nickname)
    public TextView fragment_mine_user_info_nickname;
    @BindView(R.id.fragment_mine_user_info_id)
    public TextView fragment_mine_user_info_id;
    @BindView(R.id.fragment_mine_user_info_money_layout)
    public LinearLayout fragment_mine_user_info_money_layout;
    @BindView(R.id.fragment_mine_user_info_shuquan)
    public TextView fragment_mine_user_info_shuquan;
    @BindView(R.id.fragment_mine_user_type)
    public TextView fragment_mine_user_type;
    @BindView(R.id.fragment_mine_user_time)
    public TextView fragment_mine_user_time;
    @BindView(R.id.fragment_mine_user_info_isvip)
    public ImageView fragment_mine_user_info_isvip;
    @BindView(R.id.fragment_mine_user_info_paylayout_recharge)
    public View fragment_mine_user_info_paylayout_recharge;
    @BindView(R.id.fragment_mine_marquee)
    public MarqueeView fragment_mine_marquee;
    @BindView(R.id.fragment_mine_announce_layout)
    public LinearLayout fragment_mine_announce_layout;
    @BindView(R.id.fragment_sign_invite_code)
    public TextView fragment_invite_code;
    @BindView(R.id.fragment_mine_user_info_tasklayout_share)
    public LinearLayout mLlRead;
    @BindView(R.id.fragment_mine_banner)
    public ConvenientBannerBookShelf mBanner;
    @BindView(R.id.ry_icon_ad)
    public RecyclerView mRyIconAd;
    Gson gson = new Gson();
    public UserInfoItem mUserInfo;
    private UserInfoItem.Luobo_notice luobo_notice;
    private List<BaseBook> bookLists;
    private List<BaseComic> comicList;

    public List<BaseBook> getBookLists() {
        return bookLists;
    }

    public void setBookLists(List<BaseBook> bookLists) {
        this.bookLists = bookLists;
    }

    public List<BaseComic> getComicList() {
        return comicList;
    }

    public void setComicList(List<BaseComic> comicList) {
        this.comicList = comicList;
    }

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
        if (!ReaderConfig.USE_PAY) {
            fragment_mine_user_info_money_layout.setVisibility(View.GONE);
        }
        boolean invite_code = ShareUitls.getBoolean(activity, "invite_code", false);
        fragment_invite_code.setVisibility(invite_code ? View.GONE : View.VISIBLE);
        MainHttpTask.getInstance().getResultString(activity, "Mine", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                initInfo(result, null);
            }
        });
    }

    private void showGuide() {
        if (BuildConfig.free_charge) {
            return;
        }
        NewbieGuide.with(activity)
                .setLabel("guideMine")
                .setShowCounts(1)//????????????
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(mLlRead)
                        .addHighLight(fragment_mine_user_info_paylayout_recharge)
                        .setLayoutRes(R.layout.mine_guide, R.id.img_know)
                        .setEverywhereCancelable(false))
                .show();
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
        try {
            if (userInfoItem != null) {
                mUserInfo = userInfoItem;
            } else {
                mUserInfo = gson.fromJson(info, UserInfoItem.class);
            }
            luobo_notice = mUserInfo.getLuobo_notice();
            initAnounce();
            initAD();
            if (!Utils.isLogin(activity)) {
                fragment_mine_user_info_nickname.setText(LanguageUtil.getString(activity, R.string.user_login));
                fragment_mine_user_info_id.setVisibility(View.GONE);
                fragment_mine_user_info_isvip.setVisibility(View.GONE);
                fragment_mine_user_info_shuquan.setText("--");
                fragment_mine_user_info_avatar.setImageResource(R.mipmap.icon_def_head);
                fragment_mine_user_type.setText(getString(R.string.become_vip));
                fragment_mine_user_time.setText(getString(R.string.become_vip_tip));
                AppPrefs.putSharedString(activity, ReaderConfig.UID, "");
                AppPrefs.putSharedString(activity, ReaderConfig.BOYIN_LOGIN_TOKEN, "");
                AppPrefs.putSharedString(activity, PrefConst.USER_INFO_KAY, "");
                return;
            }
            if (mUserInfo.getIs_vip() == 1) {
                fragment_mine_user_info_isvip.setImageResource(R.mipmap.icon_isvip);
                fragment_mine_user_info_isvip.setVisibility(View.VISIBLE);
                fragment_mine_user_time.setText(mUserInfo.getVip_end_time());
                fragment_mine_user_type.setText(mUserInfo.getUser_buy_item().equals("") ? getString(R.string.string_unknow_user_type) : mUserInfo.getUser_buy_item());
            } else {
                fragment_mine_user_type.setText(getString(R.string.become_vip));
                fragment_mine_user_time.setText(getString(R.string.become_vip_tip));
                fragment_mine_user_info_isvip.setVisibility(View.VISIBLE);
                fragment_mine_user_info_isvip.setImageResource(R.mipmap.icon_novip);
            }
            fragment_mine_user_info_isvip.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
            if (mUserInfo.getAuto_sub() == 0) {
                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, false);
            } else {
                AppPrefs.putSharedBoolean(activity, ReaderConfig.AUTOBUY, true);
            }
            AppPrefs.putSharedInt(activity, PrefConst.COUPON, mUserInfo.getSilverRemain());
            String sharedUserInfo = StringUtils.isEmpty(info) ? (userInfoItem != null ? gson.toJson(userInfoItem) : "") : info;
            if (!StringUtils.isEmpty(sharedUserInfo)) {
                AppPrefs.putSharedString(activity, PrefConst.USER_INFO_KAY, sharedUserInfo);
                SensorsDataHelper.profileSet(activity);
            }
            MyPicasso.GlideImageNoSize(activity, mUserInfo.getAvatar(), fragment_mine_user_info_avatar, R.mipmap.icon_def_head);
            fragment_mine_user_info_id.setVisibility(View.VISIBLE);
            fragment_mine_user_info_nickname.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            fragment_mine_user_info_nickname.setText(mUserInfo.getNickname().trim());
            fragment_mine_user_info_id.setText("ID:  " + mUserInfo.getUid());
            fragment_mine_user_info_shuquan.setText(String.format(getString(R.string.golden_balance), mUserInfo.getSilverRemain()));
            loginBoYin();
            getGameLink();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void refreshData() {
        ReaderConfig.REFREASH_USERCENTER = false;
        initAnounce();
        if (!Utils.isLogin(activity)) {//
            fragment_mine_user_info_nickname.setText(LanguageUtil.getString(activity, R.string.user_login));
            fragment_mine_user_info_id.setText("");
            fragment_mine_user_info_shuquan.setText("--");
            fragment_mine_user_info_avatar.setImageResource(R.mipmap.hold_user_avatar);
            fragment_mine_user_info_isvip.setVisibility(View.GONE);
            fragment_mine_user_info_id.setVisibility(View.GONE);
            fragment_mine_user_type.setText(getString(R.string.become_vip));
            fragment_mine_user_time.setText(getString(R.string.become_vip_tip));
            return;
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

    private void initAD() {
        if (mUserInfo != null) {
            UserInfoItem.My_center_ad my_center_ad = mUserInfo.getMy_center_ad();
            UserInfoItem.My_center_small_icon_ad my_center_small_icon_ad = mUserInfo.getMy_center_small_icon_ad();
            if (my_center_ad != null && !my_center_ad.getList().isEmpty()) {
                if (my_center_ad.getList().size() > 12) {
                    my_center_ad.getList().subList(0, 12);
                }
                mBanner.setVisibility(View.VISIBLE);
                mBanner.setPages(new CBViewHolderCreator<MineAdBannerHolderView>() {
                    @Override
                    public MineAdBannerHolderView createHolder() {
                        return new MineAdBannerHolderView(activity);
                    }
                }, my_center_ad.getList()).setPointViewVisible(true).setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        JumpAd(my_center_ad.getList().get(position));
                    }
                });
                mBanner.startTurning(2000);
            } else {
                mBanner.setVisibility(View.GONE);
            }
            if (my_center_small_icon_ad != null && !my_center_small_icon_ad.getList().isEmpty()) {
                initIconAd(my_center_small_icon_ad);
                mRyIconAd.setVisibility(View.VISIBLE);
            } else {
                mRyIconAd.setVisibility(View.GONE);
            }
        }
    }

    private void initIconAd(UserInfoItem.My_center_small_icon_ad smallIconAd) {
        mRyIconAd.setLayoutManager(new GridLayoutManager(activity, 4));
        MineIconAdAdapter mineIconAdAdapter = new MineIconAdAdapter(activity);
        mRyIconAd.setAdapter(mineIconAdAdapter);
        mineIconAdAdapter.setNewData(smallIconAd.getList());
        mineIconAdAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                BaseAd baseAd = (BaseAd) adapter.getData().get(position);
                JumpAd(baseAd);
            }
        });
    }

    private void JumpAd(BaseAd baseAd) {
        BaseAd.jumpADInfo(baseAd, activity);
    }

    private void initAnounce() {
        if (luobo_notice != null) {
            final String content = luobo_notice.getContent();
            if (content != null && !StringUtils.isEmpty(content)) {
                fragment_mine_marquee.setRndDuration(15000);
                fragment_mine_marquee.setText(content);
                fragment_mine_marquee.startScroll();
                fragment_mine_marquee.invalidate();
                fragment_mine_marquee.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, AnnounceActivity.class);
                        intent.putExtra("announce_content", luobo_notice.getTitle() + "/-/" + content);
                        startActivity(intent);
                    }
                });
                fragment_mine_announce_layout.setVisibility(View.VISIBLE);
            } else {
                fragment_mine_announce_layout.setVisibility(View.GONE);
            }
        } else {
            fragment_mine_announce_layout.setVisibility(View.GONE);
        }
    }

    @OnClick(value = {R.id.fragment_mine_user_info_avatar, R.id.fragment_mine_book_self,
            R.id.fragment_mine_user_info_paylayout_recharge, R.id.fragment_mine_user_layout,
            R.id.fragment_mine_user_info_tasklayout_mybookcomment, R.id.fragment_mine_user_info_tasklayout_taskcenter,
            R.id.fragment_mine_user_info_tasklayout_feedback, R.id.fragment_mine_user_info_tasklayout_set,
            R.id.fragment_mine_user_info_tasklayout_friends, R.id.fragment_mine_user_info_nickname,
            R.id.fragment_mine_user_info_shuquan_layout, R.id.fragment_mine_user_info_paylayout_downmanager,
            R.id.fragment_mine_user_info_paylayout_history, R.id.fragment_mine_user_info_tasklayout_share
    })
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_mine_user_info_avatar:

            case R.id.fragment_mine_user_info_nickname:
                if (!Utils.isLogin(activity)) {//???????????????????????????
                    MainHttpTask.getInstance().Gotologin(activity);
                } else {
                    HandleOnclick(view, "fragment_mine_user_info_avatar");
                }
                break;
            case R.id.fragment_mine_user_info_shuquan_layout:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_rechargenotes2");
                break;
            case R.id.fragment_mine_user_info_paylayout_recharge:
                //HandleOnclick(view, "fragment_mine_user_info_paylayout_recharge");
                if (!Utils.isLogin(activity)) {//???????????????????????????
                    MainHttpTask.getInstance().Gotologin(activity);
                } else {
                    startActivity(new Intent(activity, AddressActivity.class));
                }
                break;
            case R.id.fragment_mine_user_layout:
                HandleOnclick(view, "fragment_mine_user_info_paylayout_vip");
                break;
            case R.id.fragment_mine_user_info_tasklayout_taskcenter:
                HandleOnclick(view, "fragment_mine_user_info_tasklayout_layout");
                break;
            case R.id.fragment_mine_user_info_tasklayout_mybookcomment:
                HandleOnclick(view, "fragment_mine_user_info_tasklayout_mybookcomment");
                break;
            case R.id.fragment_mine_user_info_tasklayout_feedback:
                if (!Utils.isLogin(activity)) {
                    MainHttpTask.getInstance().Gotologin(activity);
                } else
                    HandleOnclick(view, "fragment_mine_user_info_tasklayout_feedback");
                break;
            case R.id.fragment_mine_user_info_tasklayout_set:
                SettingsActivity.chengeLangaupage = false;
                startActivity(new Intent(activity, SettingsActivity.class));
                break;
            case R.id.fragment_mine_user_info_tasklayout_friends:
                //MyShare.ShareAPP(activity);
                startActivity(new Intent(activity, MyShareActivity.class));
                break;
            case R.id.fragment_mine_user_info_paylayout_history:
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", READHISTORY).putExtra("title", LanguageUtil.getString(activity, R.string.noverfragment_yuedulishi)));
                break;
            case R.id.fragment_mine_user_info_paylayout_downmanager:
                startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", DOWN).putExtra("title", LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger)));
                break;
            case R.id.fragment_mine_user_info_tasklayout_share:
                if (!Utils.isLogin(activity)) {//???????????????????????????
                    MainHttpTask.getInstance().Gotologin(activity);
                } else {
                    startActivity(new Intent(activity, ReadTimeActivity.class));
                }
                break;
            case R.id.fragment_mine_book_self:
                Intent intent = new Intent(activity, BookSelfActivity.class);
                intent.putExtra("mBaseBooks", (ArrayList<? extends Serializable>) getBookLists());
                intent.putExtra("mBaseComics", (ArrayList<? extends Serializable>) getComicList());
                startActivity(intent);
                break;
        }
    }

    public void HandleOnclick(View v, String flag) {//Utils.isLogin(activity)
        if (true) {//???????????????????????????
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
                    intent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 11);
                    intent.putExtra("isvip", Utils.isLogin(activity));
                    intent.putExtra("type", 0);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_paylayout_rechargenotes":
                    intent.setClass(activity, BaseOptionActivity.class).putExtra("OPTION", LIUSHUIJIELU).putExtra("title", LanguageUtil.getString(activity, R.string.liushuijilu_title)).putExtra("Extra", false);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_paylayout_rechargenotes2":
                    intent = AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_mine), 11);
                    intent.putExtra("isvip", Utils.isLogin(activity));
                    intent.putExtra("type", 1);
                    startActivity(intent);
                    break;
                case "fragment_mine_user_info_tasklayout_layout":
                    intent.setClass(activity, TaskCenterActivity.class);
                    startActivity(intent);
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
        refreshData();
        Log.e("game_link", "???????????????????????????");
        getGameLink();
    }

    /**
     * ???????????????????????????
     */
    private void getGameLink() {
        ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.GAME_LINK, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObj = new JSONObject(result);
                            String url = jsonObj.getString("game_link");
                            ShareUitls.putString(activity, "game_link", url);
                            EventBus.getDefault().post(new FreshUrlEvent(url));

                        } catch (Exception e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void acceptMine(AcceptMineFragment refreshMine) {
        MainHttpTask.getInstance().getResultString(activity, "Mine", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                initInfo(result, null);
                showGuide();
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void iSInviteCode(InviteCodeEvent inviteCodeEvent) {
        if (inviteCodeEvent.isInvite) {
            fragment_invite_code.setVisibility(View.GONE);
        }
    }

    /**
     * ???????????? ????????????????????????????????? ??????????????????
     */
    void loginBoYin() {
        LoginModel loginModel = new LoginModel(getActivity());
        loginModel.loginBoYin(new LoginResultCallback() {
            @Override
            public void getResult(String jsonStr) {
                EventBus.getDefault().post(new LoginBoYinEvent(jsonStr));
            }
        });
    }
}