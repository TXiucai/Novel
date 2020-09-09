package com.heiheilianzai.app.ui.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.utils.LanguageUtil;

import butterknife.BindView;

/**
 * 公告activity
 * Created by scb on 2018/8/16.
 */
public class AnnounceActivity extends BaseActivity implements ShowTitle {
    private final String TAG = AnnounceActivity.class.getSimpleName();
  @BindView(R.id.activity_announce_content)
    TextView activity_announce_content;
    @BindView(R.id.activity_announce_title)
    TextView activity_announce_title;

    private String mContent;

    @Override
    public int initContentView() {
        return R.layout.activity_announce;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.AnnounceActivity_title));
        try {
            mContent = getIntent().getStringExtra("announce_content");
            String str[] = mContent.split("/-/");
            activity_announce_title.setText(str[0]);
            activity_announce_content.setText(str[1]);
        } catch (Exception e) {
        }
    }

    @Override
    public void initData() {

    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);

    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }
}
