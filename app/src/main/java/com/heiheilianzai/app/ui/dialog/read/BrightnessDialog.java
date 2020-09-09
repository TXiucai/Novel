package com.heiheilianzai.app.ui.dialog.read;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.SeekBar;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.ReadingConfig;
import com.heiheilianzai.app.utils.BrightnessUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/8/30 0030.
 */
public class BrightnessDialog extends Dialog {
    @BindView(R.id.dialog_bright_seekBar)
    SeekBar dialog_bright_seekBar;
    private Activity mContext;
    private ReadingConfig config;

    public BrightnessDialog(Context context) {
        this(context, R.style.setting_dialog);
        mContext = (Activity) context;
    }

    public BrightnessDialog(Context context, int themeResId) {
        super(context, themeResId);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setGravity(Gravity.BOTTOM);
        setContentView(R.layout.dialog_brightness);
        // 初始化View注入
        ButterKnife.bind(this);
        config = ReadingConfig.getInstance();
        WindowManager m = getWindow().getWindowManager();
        Display d = m.getDefaultDisplay();
        WindowManager.LayoutParams p = getWindow().getAttributes();
        p.width = d.getWidth();
        getWindow().setAttributes(p);
        setBrightness(config.getLight());
        dialog_bright_seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress > 1) {
                    changeBright(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    //改变亮度
    public void changeBright(int brightness) {
        float light = (float) (brightness / 100.0);
        config.setSystemLight(false);
        config.setLight(light);
        BrightnessUtil.setBrightness(mContext, brightness);
    }

    //设置亮度
    public void setBrightness(float brightness) {
        dialog_bright_seekBar.setProgress((int) (brightness * 100));
    }
}
