package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.base.BaseWarmStartActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.jaeger.library.StatusBarUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 我的分享页面（侵入式状态栏）
 */
public class ShareActivity extends BaseWarmStartActivity {
    @BindView(R2.id.share_back)
    View share_back;
    @BindView(R2.id.qr_code_img)
    ImageView qr_code_img;
    @BindView(R2.id.share_url_t)
    TextView share_url_t;
    @BindView(R2.id.share_title_t)
    TextView share_title_t;
    @BindView(R2.id.share_content_layout)
    View share_content_layout;

    @OnClick({R.id.share_save_qr_code, R.id.share_save, R.id.share_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.share_save_qr_code: //保存二维码到本地
                FileManager.saveBitmap(getApplicationContext(), ImageUtil.viewToBitmap(share_content_layout), getString(R.string.ShareActivity_qr_code_file_name));
                MyToash.ToashSuccess(ShareActivity.this, "已保存二维码到相册");
                break;
            case R.id.share_save://地址复制到粘贴板
                StringUtils.setStringInClipboard(ShareActivity.this, share_url_t.getText().toString());
                MyToash.ToashSuccess(ShareActivity.this, getString(R.string.ShareActivity_save_toast));
                break;
            case R.id.share_back:
                finish();
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_share);
        ButterKnife.bind(this);
        initData();
    }

    public void initView(String title, String link, String qr_url) {
        if (!StringUtils.isEmpty(title)) {
            share_title_t.setText(title);
        }
        if (!StringUtils.isEmpty(link)) {
            share_url_t.setText(link);
            MyPicasso.GlideImageNoSize(ShareActivity.this, ReaderConfig.getBaseUrl()+qr_url, qr_code_img);
        }
    }

    public void initData() {
        ShareAPP(this);
    }

    public void ShareAPP(Activity activity) {
        final ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mQrcodeIndex, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            initView(jsonObject.getString("title"), jsonObject.getString("link"), jsonObject.getString("qr_link"));
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
}
