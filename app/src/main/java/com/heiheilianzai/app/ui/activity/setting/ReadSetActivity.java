package com.heiheilianzai.app.ui.activity.setting;

import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.zcw.togglebutton.ToggleButton;

import butterknife.BindView;

public class ReadSetActivity extends BaseActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout mLlBack;
    @BindView(R.id.titlebar_text)
    public TextView mTxTittle;
    @BindView(R.id.rl_screen_time)
    public RelativeLayout mRlScreenTime;
    @BindView(R.id.tx_screen_time)
    public TextView mTxScreenTime;
    @BindView(R.id.readActivity_voice_button)
    public ToggleButton mBtVoice;
    @BindView(R.id.readActivity_screen_button)
    public ToggleButton mBtScreen;
    @BindView(R.id.readActivity_auto_button)
    public ToggleButton mBtAuto;
    private Context mContext;
    @Override
    public int initContentView() {
        return R.layout.activity_read_set;
    }

    @Override
    public void initView() {
        mContext = this;
        mTxTittle.setText(getResources().getString(R.string.string_read_set));
        String novelTime_screen = AppPrefs.getSharedString(this, "novelTime_Screen", "0");

        if (TextUtils.equals(novelTime_screen, "0")) {//跟随系统时间
            mTxScreenTime.setText(getString(R.string.string_read_time_system));
        } else if (TextUtils.equals(novelTime_screen, "5")) {
            mTxScreenTime.setText(getString(R.string.string_read_time_five));
        } else if (TextUtils.equals(novelTime_screen, "15")) {
            mTxScreenTime.setText(getString(R.string.string_read_time_fivty));
        } else if (TextUtils.equals(novelTime_screen, "30")) {
            mTxScreenTime.setText(getString(R.string.string_read_time_half));
        }

        if (AppPrefs.getSharedBoolean(mContext, "novelVoice_ToggleButton", false)) {
            mBtVoice.setToggleOn();
        } else {
            mBtVoice.setToggleOff();
        }

        if (AppPrefs.getSharedBoolean(mContext, "novelScreen_ToggleButton", false)) {
            mBtScreen.setToggleOn();
        } else {
            mBtScreen.setToggleOff();
        }

        if (AppPrefs.getSharedBoolean(mContext, "novelOpen_ToggleButton", false)) {
            mBtAuto.setToggleOn();
        } else {
            mBtAuto.setToggleOff();
        }

        mRlScreenTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        mBtVoice.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(mContext, "novelVoice_ToggleButton", on);
            }
        });

        mBtScreen.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(mContext, "novelScreen_ToggleButton", on);
            }
        });

        mBtAuto.setOnToggleChanged(new ToggleButton.OnToggleChanged() {
            @Override
            public void onToggle(boolean on) {
                AppPrefs.putSharedBoolean(mContext, "novelOpen_ToggleButton", on);
            }
        });

        mLlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void initData() {

    }
}
