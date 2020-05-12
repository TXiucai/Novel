package com.heiheilianzai.app.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.TextView;

import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.domain.DynamicDomainManager;

public class InitDomainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TextView textView = new TextView(this);
        textView.setText("正在选择最佳路线...");
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(36);
        textView.setTextColor(Color.GRAY);
        setContentView(textView);

        DynamicDomainManager dynamicDomainManager = new DynamicDomainManager(this, new DynamicDomainManager.OnCompleteListener() {
            @Override
            public void onComplete(String domain) {
                ReaderApplication.setBaseUrl(domain);
                Intent intent = new Intent();
                intent.setClass(InitDomainActivity.this, BookInfoActivity.class);
                intent.putExtras(getIntent());
                startActivity(intent);
                finish();
            }
        });
        dynamicDomainManager.start();
    }
}
