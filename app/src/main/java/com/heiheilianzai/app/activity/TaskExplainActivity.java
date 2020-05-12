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
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
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
//.http.RequestParams;
//.view.annotation.ContentView;
//.view.annotation.Event;
//.view.annotation.ViewInject;
//.x;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by abc on 2016/11/4.
 */

public class TaskExplainActivity extends BaseButterKnifeActivity {
    @Override
    public int initContentView() {
        return R.layout.activity_taskexplain;
    }
    @BindView(R2.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R2.id.titlebar_text)
    public TextView titlebar_text;

    @BindView(R2.id.activity_taskcenter_listview)
    public ListView activity_taskcenter_listview;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titlebar_text.setText(LanguageUtil.getString(this, R.string.SigninActivity_rule));
        final String[] sign_rules = getIntent().getStringArrayExtra("sign_rules");

        BaseAdapter baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                return sign_rules.length;
            }

            @Override
            public String getItem(int i) {
                return sign_rules[i];
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
               TextView textView=new TextView(TaskExplainActivity.this);
                textView.setText(getItem( i));
                return textView;
            }
        };
        activity_taskcenter_listview.setAdapter(baseAdapter);

    }

    @OnClick(value = {R.id.titlebar_back})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;

        }
    }


}
