package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.RankItem;
import com.heiheilianzai.app.ui.fragment.TopFragment;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.view.AndroidWorkaround;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

import static com.heiheilianzai.app.constant.BookConfig.mRankUrl;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_rank_index;
import static com.heiheilianzai.app.utils.StatusBarUtil.setStatusTextColor;

public class TopNewActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.img_background)
    public ImageView mImgBackground;
    @BindView(R.id.img_back)
    public ImageView mImgBack;
    @BindView(R.id.tb_tittle)
    public TabLayout mTab;
    @BindView(R.id.vp_top)
    public ViewPager mVp;
    @BindView(R.id.tx_tittle)
    public TextView mTxTittle;
    @BindView(R.id.tx_description)
    public TextView mTxDescription;
    private Activity mActivity;
    private boolean PRODUCT;
    private String mHttpTopUrlList;
    private List<RankItem> mRankItemList;
    private List<String> mTittlesList = new ArrayList<>();
    private List<String> mImgList = new ArrayList<>();
    private List<Fragment> mFragmentList = new ArrayList<>();
    private ViewHolder mHolder = null;
    private int mPosition = 0;

    @Override
    public int initContentView() {
        return R.layout.activity_top_new;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        //setStatusTextColor(false, mActivity);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {//适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));//需要在setContentView()方法后面执行
        }
        mTab.setSelectedTabIndicatorHeight(0);
        PRODUCT = getIntent().getBooleanExtra("PRODUCT", false);
        if (!PRODUCT) {
            mHttpTopUrlList = ReaderConfig.getBaseUrl() + COMIC_rank_index;
        } else {
            mHttpTopUrlList = ReaderConfig.getBaseUrl() + mRankUrl;
        }
        initData();
        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void initData() {
        if (mHttpTopUrlList == null) {
            return;
        }
        ReaderParams params = new ReaderParams(mActivity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(mHttpTopUrlList, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            initTopList(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void initTopList(String result) {
        mRankItemList = new Gson().fromJson(result, new TypeToken<List<RankItem>>() {
        }.getType());
        if (mRankItemList == null || mRankItemList.isEmpty()) {
            return;
        } else {
            for (int i = 0; i < mRankItemList.size(); i++) {
                RankItem rankItem = mRankItemList.get(i);
                mTittlesList.add(rankItem.getList_name());
                mImgList.add(rankItem.getBg_img());
                TopFragment topFragment = TopFragment.newInstance(rankItem.getRank_type(), PRODUCT, rankItem.getStatist_way());
                mFragmentList.add(topFragment);
            }
            mVp.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {
                @NonNull
                @Override
                public Fragment getItem(int position) {
                    return mFragmentList.get(position);
                }

                @Override
                public int getCount() {
                    return mFragmentList.size();
                }

                @Nullable
                @Override
                public CharSequence getPageTitle(int position) {
                    return mTittlesList.get(position);
                }
            });
            mTab.setupWithViewPager(mVp);
            int tabCount = mTab.getTabCount();
            for (int i = 0; i < tabCount; i++) {
                TabLayout.Tab tabAt = mTab.getTabAt(i);
                tabAt.setCustomView(R.layout.top_tittle_customer);
                mHolder = new ViewHolder(tabAt.getCustomView());
                mHolder.txTopItem.setText(mTittlesList.get(i));
                if (i == 0) {
                    mHolder.txTopItem.setTextSize(15);
                    mHolder.txTopItem.setSelected(true);
                } else {
                    mHolder.txTopItem.setSelected(false);
                    mHolder.txTopItem.setTextSize(13);
                }
            }
            mTab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    mPosition = tab.getPosition();
                    mVp.setCurrentItem(mPosition);
                    mTxTittle.setText(mTittlesList.get(mPosition));
                    mTxDescription.setText(mRankItemList.get(mPosition).getDescription());
                    MyPicasso.GlideImageNoSize(mActivity, mImgList.get(mPosition), mImgBackground);
                    mHolder = new ViewHolder(tab.getCustomView());
                    mHolder.txTopItem.setSelected(true);
                    mHolder.txTopItem.setTextSize(15);
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    mHolder = new ViewHolder(tab.getCustomView());
                    mHolder.txTopItem.setSelected(false);
                    mHolder.txTopItem.setTextSize(13);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            mTab.getTabAt(0).select();
            mTxTittle.setText(mTittlesList.get(0));
            mTxDescription.setText(mRankItemList.get(0).getDescription());
            MyPicasso.GlideImageNoSize(mActivity, mImgList.get(0), mImgBackground);
        }
    }

    class ViewHolder {
        TextView txTopItem;

        public ViewHolder(View view) {
            txTopItem = view.findViewById(R.id.tv_top_item);
        }
    }
}
