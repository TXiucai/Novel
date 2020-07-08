package com.heiheilianzai.app.comic.fragment;

import android.annotation.SuppressLint;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.comic.adapter.HomeStoreComicAdapter;
import com.heiheilianzai.app.comic.been.StroreComicLable;
import com.heiheilianzai.app.comic.config.ComicConfig;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.StoreComicEvent;
import com.heiheilianzai.app.fragment.BaseHomeStoreFragment;
import com.heiheilianzai.app.fragment.StroeNewFragment;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.AdaptionGridView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.List;


/**
 * 首页漫画
 * Created by scb on 2018/6/9.
 */
public class NewStoreComicFragment extends BaseHomeStoreFragment<StroreComicLable> {

    StroeNewFragment.MyHotWord hot_word;

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }

    @SuppressLint("ValidFragment")
    public NewStoreComicFragment(RelativeLayout fragment_newbookself_top, StroeNewFragment.MyHotWord hot_word) {
        this.fragment_newbookself_top = fragment_newbookself_top;
        this.hot_word = hot_word;
    }

    public NewStoreComicFragment() {
    }

    public void initViews() {
        adapter = new HomeStoreComicAdapter(activity, listData);
        super.initViews();
    }

    public void initInfo(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            initWaterfall(jsonObject.getString("label"));
            if (hot_word != null) {
                hot_word.hot_word(gson.fromJson(jsonObject.getString("hot_word"), String[].class));
                hot_word = null;
            }
            postAsyncHttpEngine_ing = false;
        } catch (Exception e) {
            e.printStackTrace();
            MyToash.LogE("nan_result", e.getMessage());
        }
    }

    @Override
    protected void initWaterfall(String jsonObject) {
        initWaterfall(jsonObject, new TypeToken<List<StroreComicLable>>() {
        }.getType());
    }

    @Override
    protected void postEvent(float alpha) {
        if (alpha == 0.0) {
            EventBus.getDefault().post(new StoreComicEvent(true, 0));
        } else if (alpha == 255) {
            EventBus.getDefault().post(new StoreComicEvent(true, ReaderConfig.REFRESH_HEIGHT + 1));
        }
    }

    /**
     * 获取头部Banner数据
     */
    @Override
    public void getBannerData() {
        getBannerData("StoreComicBannerData", ComicConfig.COMIC_STORE_BANNER);
    }

    @Override
    public void getCacheBannerData() {
        getCacheBannerData("StoreComicBannerData");
    }

    @Override
    public void getStockData() {
        getStockData("HomeStoreComicMan", ComicConfig.COMIC_home_stock);
    }

    @Override
    public void getCacheStockData() {
        getCacheStockData("HomeStoreComicMan");
    }

    /**
     * 设置头部中间按钮
     *
     * @param mEntranceGridMale
     */
    @Override
    public void initEntranceGrid(AdaptionGridView mEntranceGridMale) {
        initEntranceGrid(mEntranceGridMale, false, R.mipmap.comic_classification, R.mipmap.comic_ranking, R.mipmap.comic_member, R.mipmap.comic_finished, R.mipmap.comic_limitfree);
    }
}
