package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.TaskCenterAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.TaskCenter;
import com.heiheilianzai.app.model.event.InviteCodeEvent;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.ui.dialog.MyPoPwindow;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyShare;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

/**
 * 任务中心
 * Created by abc on 2016/11/4.
 */
public class TaskCenterActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.titlebar_right)
    public RelativeLayout titlebar_right;
    @BindView(R.id.titlebar_right_img)
    public RelativeLayout titlebar_right_img;
    @BindView(R.id.activity_taskcenter_listview)
    public ListView activity_taskcenter_listview;
    TaskCenter.Sign_info sign_info;
    public Holder holder;
    List<TaskCenter.TaskCenter2.Taskcenter> task_list = new ArrayList();
    TaskCenter taskCenter;
    private List<String> mCouponLists = new ArrayList<>();
    public Activity activity;

    @Override
    public int initContentView() {
        return R.layout.activity_taskcenter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = this;
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        setStatusTextColor(false, activity);
        EventBus.getDefault().register(this);
        titlebar_text.setText(LanguageUtil.getString(activity, R.string.TaskCenterActivity_titl));
        View view = LayoutInflater.from(this).inflate(R.layout.listview_item_taskcenter_head_new, null);
        holder = new Holder(view);
        activity_taskcenter_listview.addHeaderView(view, null, false);
        activity_taskcenter_listview.setHeaderDividersEnabled(true);
        activity_taskcenter_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent();
                TaskCenter.TaskCenter2.Taskcenter taskCenter2 = task_list.get(i - 1);
                MyToash.Log("TaskCenter2", taskCenter2.toString());
                if (taskCenter2.getTask_state() != 1) {
                    switch (taskCenter2.getTask_action()) {
                        case "finish_info":
                            if (Utils.isLogin(activity)) {
                                intent.setClass(activity, UserInfoActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                            break;
                        case "add_book":
                        case "read_book":
                        case "comment_book":
                            EventBus.getDefault().post(new ToStore(1));
                            finish();
                            break;
                        case "recharge":
                            if (Utils.isLogin(activity)) {
                                intent.setClass(activity, RechargeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                            break;
                        case "vip":
                            if (Utils.isLogin(activity)) {
                                startActivity(AcquireBaoyueActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_task)));
                                finish();
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                            break;
                        case "share_app":
                            if (Utils.isLogin(activity)) {
                                MyShare.ShareAPP(activity);
                            } else {
                                MainHttpTask.getInstance().Gotologin(activity);
                            }
                            break;
                    }
                }
            }
        });
        getData();
    }

    @OnClick(value = {R.id.titlebar_back, R.id.titlebar_right})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.titlebar_right:
                if (sign_info != null && sign_info.sign_rules != null) {
                    startActivity(new Intent(activity, TaskExplainActivity.class).putExtra("sign_rules", sign_info.sign_rules));
                }
                break;
        }
    }

    public class Holder {
        @BindView(R.id.activity_taskcenter_lianxuday)
        public TextView activity_taskcenter_lianxuday;
        @BindView(R.id.activity_taskcenter_sign)
        public ImageView activity_taskcenter_sign;
        @BindView(R.id.activity_taskcenter_getshuquan)
        public TextView activity_taskcenter_getshuquan;
        @BindView(R.id.rl_task_invite)
        public LinearLayout activity_taskcenter_invite;
        @BindView(R.id.tx_task_invite_go)
        public TextView activity_taskcenter_invite_go;

        public Holder(View view) {
            ButterKnife.bind(this, view);
        }

        @OnClick(value = {R.id.activity_taskcenter_sign, R.id.tx_task_invite_go})
        public void getEvent(View view) {
            switch (view.getId()) {
                case R.id.activity_taskcenter_sign:
                    if (Utils.isLogin(activity)) {
                        signHttp(activity);
                    } else {
                        MainHttpTask.getInstance().Gotologin(activity);
                    }
                    break;
                case R.id.tx_task_invite_go:
                    if (Utils.isLogin(activity)) {
                        startActivity(new Intent().setClass(activity, InviteCodeActivity.class));
                    } else {
                        MainHttpTask.getInstance().Gotologin(activity);
                    }
                    break;
            }
        }
    }

    public void signHttp(final Activity activity) {
        MyToash.Log("sign_info", (sign_info != null) + " sss ");
        if (sign_info != null && sign_info.sign_status != 1) {
            final ReaderParams params = new ReaderParams(activity);
            String json = params.generateParamsJson();
            HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.sIgninhttp, json, true, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            sign_info.sign_status = 1;
                            holder.activity_taskcenter_sign.setImageResource(R.mipmap.icon_sign);
                            ShareUitls.putString(activity, "sign_pop", result);
                            holder.activity_taskcenter_lianxuday.setText(sign_info.sign_days + 1 + "");
                            new MyPoPwindow().getSignPop(activity);
                            EventBus.getDefault().post(new RefreshMine(null));
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        }
    }

    public void getData() {
        final ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.taskcenter, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initSignData(result);
                        TaskCenter taskCenter = new Gson().fromJson(result, TaskCenter.class);
                        setData(taskCenter);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void initSignData(String result) {
        try {
            mCouponLists.clear();
            JSONObject jsonObject = new JSONObject(result);
            JSONObject sign_info = jsonObject.getJSONObject("sign_info");
            JSONArray sign_day_score_list = sign_info.getJSONArray("sign_day_score_list");
            for (int i = 0; i < 7; i++) {
                String temp = "day_" + (i + 1);
                String coupon = sign_day_score_list.getJSONObject(i).getString(temp);
                mCouponLists.add("+" + coupon);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void setData(TaskCenter taskCenter) {
        this.taskCenter = taskCenter;
        if (taskCenter != null) {
            sign_info = taskCenter.sign_info;
            if (sign_info.sign_status == 1) {
                holder.activity_taskcenter_sign.setImageResource(R.mipmap.icon_sign);
            } else {
                holder.activity_taskcenter_sign.setImageResource(R.mipmap.icon_unsign);
            }
            holder.activity_taskcenter_lianxuday.setText(sign_info.sign_days + "");
            holder.activity_taskcenter_getshuquan.setText(sign_info.max_award + "" + sign_info.unit);
            task_list.addAll(taskCenter.getTask_menu().get(0).getTask_list());
            task_list.addAll(taskCenter.getTask_menu().get(1).getTask_list());
            TaskCenterAdapter taskCenterAdapter = new TaskCenterAdapter(task_list, this, taskCenter.getTask_menu().get(0).getTask_list().size(), taskCenter.getTask_menu().get(0).getTask_title(), taskCenter.getTask_menu().get(1).getTask_title());
            activity_taskcenter_listview.setAdapter(taskCenterAdapter);
        }
    }

    public static void SignHttp(final Activity activity) {
        if (Utils.isLogin(activity)) {
            final ReaderParams params = new ReaderParams(activity);
            String json = params.generateParamsJson();
            HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.sIgninhttp, json, true, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            ShareUitls.putString(activity, "sign_pop", result);
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        }
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void iSInviteCode(InviteCodeEvent inviteCodeEvent) {
        if (inviteCodeEvent.isInvite) {
            if (holder != null) {
                holder.activity_taskcenter_invite.setVisibility(View.GONE);
            }
        }
    }
}
