package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;

import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.view.AndroidWorkaround;

import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

public class CouponRecordActivity extends BaseButterKnifeTransparentActivity {
    private Activity mActivity;

    @Override
    public int initContentView() {
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setStatusTextColor(false, mActivity);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
    }
}
