package com.heiheilianzai.app.activity;

import android.Manifest;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.SystemUtil;

import org.json.JSONObject;


import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by abc on 2016/11/4.
 */

public class AboutUsActivity extends BaseButterKnifeActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_aboutus;
    }

    @BindView(R2.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R2.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R2.id.activity_about_appversion)
    public TextView activity_about_appversion;

    @BindView(R2.id.activity_about_email)
    public RelativeLayout activity_about_email;
    @BindView(R2.id.activity_about_phone)
    public RelativeLayout activity_about_phone;
    @BindView(R2.id.activity_about_qq)
    public RelativeLayout activity_about_qq;

    @BindView(R2.id.activity_about_emailText)
    public TextView activity_about_emailText;
    @BindView(R2.id.activity_about_phoneText)
    public TextView activity_about_phoneText;
    @BindView(R2.id.activity_about_qqText)
    public TextView activity_about_qqText;
    @BindView(R2.id.activity_about_wechatText)
    public TextView activity_about_wechatText;
    @BindView(R2.id.activity_about_wechat)
    public RelativeLayout activity_about_wechat;


    @BindView(R2.id.activity_about_company)
    public TextView activity_about_company;
    public boolean flag;//判断是否支持自定义状态栏


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titlebar_text.setText(LanguageUtil.getString(this, R.string.AboutActivity_title2));
        String str = getResources().getString(R.string.app_version);
        activity_about_appversion.setText(str + SystemUtil.getAppVersionName(this));
        getData();

    }

    @OnClick(value = {R.id.titlebar_back, R.id.activity_about_user,
            R.id.activity_about_qq, R.id.activity_about_phone,
            R.id.activity_about_email,R.id.activity_about_wechat})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.activity_about_user:
                about();
                break;
            case R.id.activity_about_email:
                if (activity_about_emailText.getText().toString().length() > 0) {
                    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cm.setText(activity_about_emailText.getText());
                    MyToash.ToashSuccess(AboutUsActivity.this, LanguageUtil.getString(this, R.string.AboutActivity_Email));
                }
                break;
            case R.id.activity_about_phone:
                if (activity_about_phoneText.getText().toString().length() > 0) {
                    ClipboardManager cmqq = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cmqq.setText(activity_about_phoneText.getText().toString());
                    MyToash.ToashSuccess(AboutUsActivity.this, LanguageUtil.getString(this, R.string.AboutActivity_telephone));
                }

                break;
            case R.id.activity_about_qq:
                if (activity_about_qqText.getText().toString().length() > 0) {
                    ClipboardManager cmqq = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cmqq.setText(activity_about_qqText.getText().toString());

                    MyToash.ToashSuccess(AboutUsActivity.this, LanguageUtil.getString(this, R.string.AboutActivity_QQ));
                }
                break;
            case R.id.activity_about_wechat:
                if (activity_about_qqText.getText().toString().length() > 0) {
                    ClipboardManager cmqq = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    // 将文本内容放到系统剪贴板里。
                    cmqq.setText(activity_about_qqText.getText().toString());
                    MyToash.ToashSuccess(AboutUsActivity.this, LanguageUtil.getString(this, R.string.AboutActivity_weixin2));
                }
                break;

        }
    }


    /**
     * 软件许可及服务协议
     */
    public void about() {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void getData() {


        final ReaderParams params = new ReaderParams(this);


        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl()+ReaderConfig.aBoutUs, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String telphone = jsonObject.getString("telphone");

                            String email = jsonObject.getString("email");
                            String qq = jsonObject.getString("qq");
                            String company = jsonObject.getString("company");
                            String weixin = jsonObject.getString("wechat");


                            if (email.length() == 0) {
                                activity_about_email.setVisibility(View.GONE);
                            } else
                                activity_about_emailText.setText(email);
                            if (telphone.length() == 0) {
                                activity_about_phone.setVisibility(View.GONE);
                            } else
                                activity_about_phoneText.setText(telphone);
                            if (qq.length() == 0) {
                                activity_about_qq.setVisibility(View.GONE);
                            } else
                                activity_about_qqText.setText(qq);
                            if (weixin.length() == 0) {
                                activity_about_wechat.setVisibility(View.GONE);
                            } else
                                activity_about_wechatText.setText(weixin);

                            activity_about_company.setText(company);
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
