package com.heiheilianzai.app.ui.fragment.cartoon;

import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_COMICS;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.cartoon.HomeStoreCartoonAdapter;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.constant.CartoonConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.model.cartoon.StroreCartoonLable;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class NewStoreCartoonFragment extends BaseHomeStoreFragment<StroreCartoonLable> {
    private String mChannelId;
    private String mChannelRecommendId;
    private int mPosition;

    public void initViews() {
        adapter = new HomeStoreCartoonAdapter(activity, listData, false);
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

    public static NewStoreCartoonFragment newInstance(ChannelBean.ListBean channelBean, int position) {
        NewStoreCartoonFragment newStoreComicFragment = new NewStoreCartoonFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel", channelBean);
        args.putInt("position", position);
        newStoreComicFragment.setArguments(args);
        return newStoreComicFragment;
    }

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }

    @Override
    protected void setPosition() {
        setPosition(mPosition);
    }

    @Override
    protected void setChannelId() {
        setmChannelId(mChannelId, mChannelRecommendId);
    }

    @Override
    protected void getChannelDetailData() {
        if (!mIsFresh && listData != null && listData.size() > 0 && listData.get(listData.size() - 1).work_num_type == 2) {
            getChannelDetailData(CartoonConfig.CARTOON_Channel_Detail_no_limit, 3, listData.get(listData.size() - 1).getRecommend_id());
        } else {
            getChannelDetailData(CartoonConfig.CARTOON_Channel_Detail, 3);
        }
    }

    @Override
    protected void getSdkLableAd() {
        getSdkLableAd(3);
    }

    @Override
    protected void initLable(Object type) {
        if (type != null) {
            StroreCartoonLable stroreCartoonLable = (StroreCartoonLable) type;
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getAd_type() == 1) {
                    listData.remove(i);
                }
            }
            int sizeData = listData.size();
            for (int i = 0; i < sizeData; i++) {
                listData.add(2 * i + 1, stroreCartoonLable);
            }
            //横一样式最后一个不需要广告
            if (listData.get(listData.size() - 2).work_num_type == 2) {
                listData.remove(listData.size() - 1);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void getBannerData() {
        getBannerData("StoreCartoonBannerData", CartoonConfig.CARTOON_STORE_BANNER, 3);
    }

    @Override
    protected void getStockData() {

    }

    @Override
    protected void getCacheBannerData() {
        getCacheBannerData("StoreCartoonBannerData", 3);
    }

    @Override
    protected void getCacheStockData() {

    }

    @Override
    protected void initInfo(String data) {
        try {
            JSONObject jsonObject = new JSONObject(data);
            initWaterfall(jsonObject.getString("label"));
            if (hot_word != null) {
                hot_word.hot_word(gson.fromJson(jsonObject.getString("hot_word"), String[].class));
                hot_word = null;
            }
        } catch (Exception e) {
        }
        postAsyncHttpEngine_ing = false;
    }

    @Override
    protected void initWaterfall(String jsonObject) {
        initWaterfall(jsonObject, new TypeToken<List<StroreCartoonLable>>() {
        }.getType());
    }

    @Override
    protected void postEvent(float alpha) {

    }

    @Override
    protected void onMyScrollStateChanged(int position) {
        try {
            List<String> columnId = new ArrayList<>();
            List<StroreCartoonLable> list = new ArrayList<>();
            list.addAll(listData.subList(0, (position + 1) >= listData.size() ? listData.size() : (position + 1)));
            for (StroreCartoonLable stroreComicLable : list) {
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
        getHomeRecommend(3);
    }
}
