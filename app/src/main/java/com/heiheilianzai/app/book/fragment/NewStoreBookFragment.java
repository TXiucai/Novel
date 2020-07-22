package com.heiheilianzai.app.book.fragment;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.book.adapter.HomeStoreBookAdapter;
import com.heiheilianzai.app.book.been.StroreBookcLable;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.StoreBookEvent;
import com.heiheilianzai.app.fragment.BaseHomeStoreFragment;
import com.heiheilianzai.app.view.AdaptionGridView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;


/**
 * 首页小说
 * Created by scb on 2018/6/9.
 */
public class NewStoreBookFragment extends BaseHomeStoreFragment<StroreBookcLable> {
    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }

    @Override
    public void initViews() {
        adapter = new HomeStoreBookAdapter(activity, listData);
        super.initViews();
    }

    @Override
    public void initInfo(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            initWaterfall(jsonObject.getString("label"));
            if (hot_word != null) {
                hot_word.hot_word(gson.fromJson(jsonObject.getString("hot_word"), String[].class));
                hot_word = null;
            }
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
    protected void postEvent(float alpha) {
        if (alpha == 0.0) {
            EventBus.getDefault().post(new StoreBookEvent(true, (int) 0));
        } else if (alpha == 255) {
            EventBus.getDefault().post(new StoreBookEvent(true, (int) ReaderConfig.REFRESH_HEIGHT + 1));
        }
    }

    /**
     * 获取头部Banner数据
     */
    @Override
    public void getBannerData() {
        getBannerData("StoreBookBannerData", ReaderConfig.BOOK_STORE_BANNER);
    }

    @Override
    public void getCacheBannerData() {
        getCacheBannerData("StoreBookBannerData");
    }

    @Override
    public void getStockData() {
        getStockData("HomeStoreBookMan", ReaderConfig.mBookStoreUrl);
    }

    @Override
    public void getCacheStockData() {
        getCacheStockData("HomeStoreBookMan");
    }

    /**
     * 设置头部中间按钮
     *
     * @param mEntranceGridMale
     */
    public void initEntranceGrid(AdaptionGridView mEntranceGridMale) {
        initEntranceGrid(mEntranceGridMale, true, R.mipmap.entrance1, R.mipmap.entrance2, R.mipmap.entrance3, R.mipmap.entrance4, R.mipmap.entrance5);
    }
}

