package com.heiheilianzai.app.ui.fragment.book;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.book.HomeStoreBookAdapter;
import com.heiheilianzai.app.base.BaseHomeStoreFragment;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.event.StoreBookEvent;
import com.heiheilianzai.app.ui.fragment.comic.NewStoreComicFragment;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.sa.SaVarConfig.WORKS_TYPE_BOOK;

/**
 * 首页小说
 * Created by scb on 2018/6/9.
 */
public class NewStoreBookFragment extends BaseHomeStoreFragment<StroreBookcLable> {

    private String mChannelId;
    private String mChannelRecommendId;
    private int mPosition;

    @Override
    public int initContentView() {
        return R.layout.fragment_comic_store_new;
    }

    @Override
    public void initViews() {
        adapter = new HomeStoreBookAdapter(activity, listData, false);
        super.initViews();
        if (getArguments() != null) {
            ChannelBean.ListBean channelBean = (ChannelBean.ListBean) getArguments().getSerializable("channel");
            mChannelId = channelBean.getId();
            mChannelRecommendId = getRecommendChannelId(channelBean.getRecommend_id_list());
            mPosition = getArguments().getInt("position", 0);
        }
    }

    @Override
    protected void setPosition() {
        setPosition(mPosition);
    }

    @Override
    protected void setChannelId() {
        setmChannelId(mChannelId, mChannelRecommendId);
    }

    public static NewStoreBookFragment newInstance(ChannelBean.ListBean channelBean, int position) {
        NewStoreBookFragment newStoreComicFragment = new NewStoreBookFragment();
        Bundle args = new Bundle();
        args.putSerializable("channel", channelBean);
        args.putInt("position", position);
        newStoreComicFragment.setArguments(args);
        return newStoreComicFragment;
    }

    @Override
    protected void getChannelDetailData() {
        getChannelDetailData(ReaderConfig.mBookChannelDetailUrl);
    }

    @Override
    protected void getSdkLableAd() {
        getSdkLableAd(0);
    }

    @Override
    protected void initLable(Object type) {
        if (type != null) {
            StroreBookcLable stroreBookcLable = (StroreBookcLable) type;
            for (int i = 0; i < listData.size(); i++) {
                if (listData.get(i).getAd_type() == 1) {
                    listData.remove(i);
                }
            }
            int sizeData = listData.size();
            for (int i = 0; i < sizeData; i++) {
                listData.add(2 * i + 1, stroreBookcLable);
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
    protected void getHomeRecommend() {
        getHomeRecommend(0);
    }
}