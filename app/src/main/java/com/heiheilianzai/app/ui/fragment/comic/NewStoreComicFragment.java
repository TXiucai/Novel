package com.heiheilianzai.app.ui.fragment.comic;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.HomeStoreComicAdapter;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.model.event.StoreComicEvent;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.view.AdaptionGridView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_BOOK;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;


/**
 * 首页漫画
 * Created by scb on 2018/6/9.
 */
public class NewStoreComicFragment extends BaseHomeStoreFragment<StroreComicLable> {


    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }


    public void initViews() {
        adapter = new HomeStoreComicAdapter(activity, listData,false);
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


    @Override
    protected void onMyScrollStateChanged(int position) {
        try {
            List<String> columnId = new ArrayList<>();
            List<StroreComicLable> list = new ArrayList<>();
            list.addAll(listData.subList(0, (position + 1) >= listData.size() ? listData.size() : (position + 1)));
            for (StroreComicLable stroreComicLable : list) {
                if (stroreComicLable.ad_type == 0) {
                    columnId.add(stroreComicLable.recommend_id);
                }
            }
            setHomeRecommendationEvent(WORKS_TYPE_COMICS, columnId);
        } catch (Exception e) {
        }
    }

    @Override
    protected void getHomeRecommend(RecyclerView recyclerView) {
        getHomeRecommend(recyclerView, 1);
    }
}