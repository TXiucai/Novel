package com.heiheilianzai.app.ui.fragment.comic;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.HomeStoreComicAdapter;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.utils.MyToash;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.ComicConfig.COMIC_Detail_channel_no_limit;
import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;


/**
 * 首页漫画
 * Created by scb on 2018/6/9.
 */
public class NewStoreComicFragment extends BaseHomeStoreFragment<StroreComicLable> {


    private String mChannelId;
    private String mChannelRecommendId;
    private int mPosition;

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }


    public void initViews() {
        adapter = new HomeStoreComicAdapter(activity, listData, false);
        super.initViews();
        if (getArguments() != null) {
            ChannelBean.ListBean channelBean = (ChannelBean.ListBean) getArguments().getSerializable("channel");
            mChannelId = channelBean.getId();
            mChannelRecommendId = getRecommendChannelId(channelBean.getRecommend_id_list());
            mPosition = getArguments().getInt("position", 0);
        }
    }

    @Override
    protected boolean isLabelNoLimit() {
        if (listData != null && listData.size() > 0 && listData.get(listData.size() - 1).work_num_type == 2) {
            return true;
        }
        return false;
    }

    @Override
    protected void setPosition() {
        setPosition(mPosition);
    }

    @Override
    protected void setChannelId() {
        setmChannelId(mChannelId, mChannelRecommendId);
    }


    public static NewStoreComicFragment newInstance(ChannelBean.ListBean channelBean, int position) {
        NewStoreComicFragment newStoreComicFragment = new NewStoreComicFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel", channelBean);
        args.putInt("position", position);
        newStoreComicFragment.setArguments(args);
        return newStoreComicFragment;
    }

    @Override
    protected void getChannelDetailData() {
        if (!mIsFresh && listData != null && listData.size() > 0 && listData.get(listData.size() - 1).work_num_type == 2) {
            getChannelDetailData(ComicConfig.COMIC_Detail_channel_no_limit, 2, listData.get(listData.size() - 1).getRecommend_id());
        } else {
            getChannelDetailData(ComicConfig.COMIC_Detail_channel, 2);
        }
    }

    @Override
    protected void getSdkLableAd() {
        getSdkLableAd(2);
    }

    @Override
    protected void initLable(Object type) {
        if (type != null) {
            StroreComicLable stroreBookcLable = (StroreComicLable) type;
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getAd_type() == 1) {
                    listData.remove(i);
                }
            }
            int sizeData = listData.size();
            //添加广告
            for (int i = 0; i < sizeData; i++) {
                listData.add(2 * i + 1, stroreBookcLable);
            }
            //横一样式最后一个不需要广告
            if (listData.get(listData.size() - 2).work_num_type == 2) {
                listData.remove(listData.size() - 1);
            }
            adapter.notifyDataSetChanged();
        }
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
       /* if (alpha == 0.0) {
            EventBus.getDefault().post(new StoreComicEvent(true, 0));
        } else if (alpha == 255) {
            EventBus.getDefault().post(new StoreComicEvent(true, ReaderConfig.REFRESH_HEIGHT + 1));
        }*/
    }

    /**
     * 获取头部Banner数据
     */
    @Override
    public void getBannerData() {
        getBannerData("StoreComicBannerData", ComicConfig.COMIC_STORE_BANNER, 2);
    }

    @Override
    public void getCacheBannerData() {
        getCacheBannerData("StoreComicBannerData", 2);
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
    protected void getHomeRecommend() {
        getHomeRecommend(2);
    }
}