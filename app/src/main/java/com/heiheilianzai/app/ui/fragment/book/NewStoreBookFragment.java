package com.heiheilianzai.app.ui.fragment.book;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.book.HomeStoreBookAdapter;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.event.StoreBookEvent;
import com.heiheilianzai.app.view.AdaptionGridView;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_BOOK;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;


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
        adapter = new HomeStoreBookAdapter(activity, listData, false);
        super.initViews();
    }

    @Override
    protected void getChannelData() {
        getChannelData(ReaderConfig.mBookChannelUrl, true);
    }

    @Override
    protected void getSdkLableAd() {
        getSdkLableAd(0);
    }

    @Override
    protected void initLable(Object type) {
        if (type != null) {
            StroreBookcLable stroreBookcLable = (StroreBookcLable) type;
            for (StroreBookcLable bookcLable : listData) {
                if (bookcLable.ad_type == 1) {
                    bookcLable.setAd_image(stroreBookcLable.getAd_image());
                    bookcLable.setAd_skip_url(stroreBookcLable.getAd_skip_url());
                    bookcLable.setAd_title(stroreBookcLable.getAd_title());
                    bookcLable.setAd_url_type(stroreBookcLable.getAd_url_type());
                }
            }
            adapter.notifyDataSetChanged();
        }
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
        getBannerData("StoreBookBannerData", ReaderConfig.BOOK_STORE_BANNER, 0);
    }

    @Override
    public void getCacheBannerData() {
        getCacheBannerData("StoreBookBannerData", 0);
    }

    @Override
    public void getStockData() {
        getStockData("HomeStoreBookMan", ReaderConfig.mBookStoreUrl);
    }

    @Override
    public void getCacheStockData() {
        getCacheStockData("HomeStoreBookMan");
    }


    @Override
    protected void onMyScrollStateChanged(int position) {
        try {
            List<String> columnId = new ArrayList<>();
            List<StroreBookcLable> list = new ArrayList<>();
            list.addAll(listData.subList(0, (position + 1) >= listData.size() ? listData.size() : (position + 1)));
            for (StroreBookcLable stroreBookcLable : list) {
                if (stroreBookcLable.ad_type == 0) {
                    columnId.add(stroreBookcLable.recommend_id);
                }
            }
            setHomeRecommendationEvent(WORKS_TYPE_BOOK, columnId);
        } catch (Exception e) {
        }
    }

    @Override
    protected void getHomeRecommend(RecyclerView recyclerView) {
        getHomeRecommend(recyclerView, 0);
    }
}