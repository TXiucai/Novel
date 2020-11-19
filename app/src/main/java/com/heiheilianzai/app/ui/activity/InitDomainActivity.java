package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.heiheilianzai.app.component.http.DynamicDomainManager;
import com.heiheilianzai.app.utils.ConcurrentUrlhelpterKt;
import com.heiheilianzai.app.utils.OnCompletUrl;
import com.heiheilianzai.app.utils.OnError;

import org.jetbrains.annotations.NotNull;

import java.util.List;

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
            public void onComplete(List<String> apiUrl) {
                ConcurrentUrlhelpterKt.getFastUrl(apiUrl, new OnCompletUrl() {
                    @Override
                    public void onComplteApi(@NotNull String api) {
                        Intent intent = new Intent();
                        intent.setClass(InitDomainActivity.this, BookInfoActivity.class);
                        intent.putExtras(getIntent());
                        startActivity(intent);
                        finish();
                    }
                }, new OnError() {
                    @Override
                    public void onError() {

                    }
                });
            }
        });
        dynamicDomainManager.start();
    }
}
