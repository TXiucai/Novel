package com.heiheilianzai.app.comic.view;

import android.view.LayoutInflater;
import android.view.View;

import com.filippudak.ProgressPieView.ProgressPieView;
import com.github.piasy.biv.indicator.ProgressIndicator;
import com.github.piasy.biv.view.BigImageView;
import com.heiheilianzai.app.R;

import java.util.Locale;

/**
 * 漫画图片自定义加载进度
 */
public class ProgressPieIndicator implements ProgressIndicator {
    private ProgressPieView mProgressPieView;

    @Override
    public View getView(BigImageView parent) {
        mProgressPieView = (ProgressPieView) LayoutInflater.from(parent.getContext()).inflate(R.layout.ui_progress_pie_indicator, parent, false);
        return mProgressPieView;
    }

    @Override
    public void onStart() {
    }

    @Override
    public void onProgress(int progress) {
        if (progress < 0 || progress > 100 || mProgressPieView == null) {
            return;
        }
        mProgressPieView.setProgress(progress);
        mProgressPieView.setText(String.format(Locale.getDefault(), "%d%%", progress));
    }

    @Override
    public void onFinish() {
    }
}
