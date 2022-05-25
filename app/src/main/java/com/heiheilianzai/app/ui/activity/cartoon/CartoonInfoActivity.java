package com.heiheilianzai.app.ui.activity.cartoon;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseWarmStartActivity;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;

public class CartoonInfoActivity extends BaseWarmStartActivity {
    private static String COMIC_ID_EXT_KAY = "CARTOON_ID";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cartooninfo);
    }
    /**
     * 进入动漫简介页必传参数
     *
     * @param context
     * @param referPage 神策埋点数据从哪个页面进入
     * @return Intent
     */
    public static Intent getMyIntent(Context context, String referPage, String videoId) {
        Intent intent = new Intent(context, ComicInfoActivity.class);
        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, referPage);
        intent.putExtra(COMIC_ID_EXT_KAY, videoId);
        return intent;
    }

}
