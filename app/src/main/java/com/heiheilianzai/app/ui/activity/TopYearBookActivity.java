package com.heiheilianzai.app.ui.activity;

import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.book.HomeStoreBookAdapter;
import com.heiheilianzai.app.adapter.comic.HomeStoreComicAdapter;
import com.heiheilianzai.app.base.BaseTopYearActivity;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.utils.LanguageUtil;

import org.json.JSONObject;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class TopYearBookActivity extends BaseTopYearActivity<StroreBookcLable> {
    @BindView(R.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @Override
    public void initView() {
        titlebar_text.setText(LanguageUtil.getString(this, R.string.top_year_tittle));
        adapter = new HomeStoreBookAdapter(TopYearBookActivity.this, listData,true);
        super.initView();

    }

    @Override
    protected void getStockData() {
        getStockData("TopYearBookMan", ReaderConfig.mTopYearBookUrl);
    }

    @Override
    protected void getCacheStockData() {
        getCacheStockData("TopYearBookMan");
    }

    @Override
    protected void dealData(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            initWaterfall(jsonObject.getString("label"));
        } catch (Exception e) {
            Log.e("", e.getMessage());
        }
        postAsyncHttpEngine_ing = false;
    }

    @Override
    protected void initWaterfall(String jsonObject) {
        initWaterfall(jsonObject, new TypeToken<List<StroreBookcLable>>() {
        }.getType());
    }

    @Override
    public int initContentView() {
        return R.layout.activity_top_year;
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
