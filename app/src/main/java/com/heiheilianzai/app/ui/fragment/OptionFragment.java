package com.heiheilianzai.app.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.OptionRecyclerViewAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaoyueItem;
import com.heiheilianzai.app.model.BaoyueUser;
import com.heiheilianzai.app.model.CategoryItem;
import com.heiheilianzai.app.model.OptionBeen;
import com.heiheilianzai.app.model.OptionItem;
import com.heiheilianzai.app.model.SearchBox;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.heiheilianzai.app.view.MyRadioButton;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.heiheilianzai.app.constant.BookConfig.free_time;
import static com.heiheilianzai.app.constant.BookConfig.mBaoyueIndexUrl;
import static com.heiheilianzai.app.constant.BookConfig.mBaoyueUrl;
import static com.heiheilianzai.app.constant.BookConfig.mFinishUrl;
import static com.heiheilianzai.app.constant.BookConfig.mFreeTimeUrl;
import static com.heiheilianzai.app.constant.BookConfig.mRankListUrl;
import static com.heiheilianzai.app.constant.BookConfig.mRecommendUrl;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_baoyue;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_baoyue_list;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_finish;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_free_time;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_list;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_rank_list;
import static com.heiheilianzai.app.constant.ComicConfig.COMIC_recommend;
import static com.heiheilianzai.app.constant.ReaderConfig.BAOYUE;
import static com.heiheilianzai.app.constant.ReaderConfig.BAOYUE_SEARCH;
import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.constant.ReaderConfig.MIANFEI;
import static com.heiheilianzai.app.constant.ReaderConfig.PAIHANG;
import static com.heiheilianzai.app.constant.ReaderConfig.SHUKU;
import static com.heiheilianzai.app.constant.ReaderConfig.WANBEN;

/**
 * Created by abc on 2016/11/4.
 */

public class OptionFragment extends BaseButterKnifeFragment {


    @Override
    public int initContentView() {
        return R.layout.fragment_option;
    }

    @BindView(R.id.fragment_option_noresult)
    public LinearLayout fragment_option_noresult;

    /*   @BindView(R.id.fragment_option_listview)
       public PullToRefreshListView fragment_option_listview;
       */
    @BindView(R.id.fragment_option_listview)
    public XRecyclerView fragment_option_listview;


    boolean PRODUCT;
    int SEX, OPTION;
    String httpUrl;
    Gson gson = new Gson();
    int total_page, current_page = 1;
    OptionRecyclerViewAdapter optionAdapter;
    List<OptionBeen> optionBeenList;
    LinearLayout temphead;
    LayoutInflater layoutInflater;
    Map<String, String> map;

    //普通列表
    @SuppressLint("ValidFragment")
    public OptionFragment(boolean PRODUCT, int OPTION, int SEX) {
        MyToash.Log("OptionFragment", PRODUCT + "  " + OPTION);
        this.PRODUCT = PRODUCT;
        this.SEX = SEX;
        this.OPTION = OPTION;

    }

    public TextView titlebar_text;
    String recommend_id;

    //推荐列表
    @SuppressLint("ValidFragment")
    public OptionFragment(boolean PRODUCT, int OPTION, String recommend_id, TextView titlebar_text) {
        this.PRODUCT = PRODUCT;
        this.recommend_id = recommend_id;
        this.OPTION = OPTION;
        this.titlebar_text = titlebar_text;
    }


    //排行列表
    String rank_type;

    @SuppressLint("ValidFragment")
    public OptionFragment(boolean PRODUCT, int OPTION, String rank_type, int SEX) {
        this.PRODUCT = PRODUCT;
        this.rank_type = rank_type;
        this.OPTION = OPTION;
        this.SEX = SEX;
    }

    public OptionFragment() {
    }

    BaoyueHeadHolder baoyueHeadHolder;

    class BaoyueHeadHolder {

        public BaoyueHeadHolder(View view) {
            ButterKnife.bind(this, view);
        }

        @BindView(R.id.activity_baoyue_ok)
        public Button activity_baoyue_ok;
        @BindView(R.id.activity_baoyue_circle_img)
        public CircleImageView activity_baoyue_circle_img;
        @BindView(R.id.activity_baoyue_nickname)
        public TextView activity_baoyue_nickname;
        @BindView(R.id.activity_baoyue_desc)
        public TextView activity_baoyue_desc;

        public String mAvatar;

        @OnClick(value = {R.id.activity_baoyue_circle_Left_layout, R.id.activity_baoyue_circle_right_layout, R.id.activity_baoyue_ok})
        public void getEvent(View view) {
            Intent intent = new Intent();
            switch (view.getId()) {
                case R.id.activity_baoyue_circle_Left_layout:
                    intent.setClass(activity, BaseOptionActivity.class);
                    intent.putExtra("PRODUCT", PRODUCT);
                    intent.putExtra("OPTION", BAOYUE_SEARCH);
                    intent.putExtra("title", LanguageUtil.getString(activity, R.string.BaoyueActivity_baoyueshuku));
                    startActivity(intent);
                    break;
                case R.id.activity_baoyue_circle_right_layout:
                case R.id.activity_baoyue_ok:
                    intent.setClass(activity, AcquireBaoyueActivity.class);
                    intent.putExtra("avatar", mAvatar);
                    startActivityForResult(intent, 1);
                    break;
            }
        }


    }

    private void initHttpUrl() {

        temphead = (LinearLayout) layoutInflater.inflate(R.layout.item_list_head, null);
        optionBeenList = new ArrayList<>();
        if (!PRODUCT) {

            switch (OPTION) {
                case MIANFEI:
                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_free_time;
                    break;
                case WANBEN:
                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_finish;
                    break;
                case SHUKU:
                    temphead = new LinearLayout(activity);
                    temphead.setOrientation(LinearLayout.VERTICAL);

                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_list;
                    map = new <String, String>HashMap();
                    break;
                case PAIHANG:
                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_rank_list;
                    break;
                case BAOYUE_SEARCH:
                    temphead = new LinearLayout(activity);
                    temphead.setOrientation(LinearLayout.VERTICAL);

                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_baoyue_list;
                    map = new <String, String>HashMap();
                    break;
                case BAOYUE:
                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_baoyue;
                    temphead = (LinearLayout) layoutInflater.inflate(R.layout.header_baoyue, null, false);
                    baoyueHeadHolder = new BaoyueHeadHolder(temphead);
                    break;

                case LOOKMORE:
                    httpUrl = ReaderConfig.getBaseUrl() + COMIC_recommend;
                    break;


            }
        } else {
            switch (OPTION) {
                case MIANFEI:
                    httpUrl = ReaderConfig.getBaseUrl() + mFreeTimeUrl;
                    break;
                case WANBEN:
                    httpUrl = ReaderConfig.getBaseUrl() + mFinishUrl;
                    break;
                case SHUKU:
                    temphead = new LinearLayout(activity);
                    temphead.setOrientation(LinearLayout.VERTICAL);
                    httpUrl = ReaderConfig.getBaseUrl() + BookConfig.mCategoryIndexUrl;
                    map = new <String, String>HashMap();
                    break;
                case PAIHANG:
                    httpUrl = ReaderConfig.getBaseUrl() + mRankListUrl;
                    break;
                case BAOYUE_SEARCH:
                    temphead = new LinearLayout(activity);
                    temphead.setOrientation(LinearLayout.VERTICAL);

                    httpUrl = ReaderConfig.getBaseUrl() + mBaoyueIndexUrl;
                    map = new <String, String>HashMap();
                    break;
                case BAOYUE:
                    httpUrl = ReaderConfig.getBaseUrl() + mBaoyueUrl;
                    temphead = (LinearLayout) layoutInflater.inflate(R.layout.header_baoyue, null, false);
                    baoyueHeadHolder = new BaoyueHeadHolder(temphead);
                    break;

                case LOOKMORE:
                    if (recommend_id != null) {
                        httpUrl = ReaderConfig.getBaseUrl() + mRecommendUrl;
                    } else {
                        httpUrl = ReaderConfig.getBaseUrl() + free_time;
                    }
                    break;
            }

        }


        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_option_listview.setLayoutManager(layoutManager);
        if (OPTION != SHUKU && OPTION != BAOYUE_SEARCH) {
            fragment_option_listview.addHeaderView(temphead);
        }
        optionAdapter = new OptionRecyclerViewAdapter(activity, optionBeenList, OPTION, PRODUCT, layoutInflater, onItemClick);
        fragment_option_listview.setAdapter(optionAdapter);
        HttpData();
    }

    int LoadingListener = 0;
    boolean isRefarshHead;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        layoutInflater = LayoutInflater.from(activity);
        fragment_option_listview.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                if (OPTION == BAOYUE_SEARCH || OPTION == SHUKU) {
                    isRefarshHead = true;
                    map.clear();
                }
                LoadingListener = -1;
                current_page = 1;
                HttpData();
            }

            @Override
            public void onLoadMore() {
                // load more data here
                LoadingListener = 1;
                HttpData();
            }
        });

        if (OPTION == BAOYUE) {
            fragment_option_listview.setLoadingMoreEnabled(false);
        }

        initHttpUrl();
    }

    int style = -1;
    OptionRecyclerViewAdapter.OnItemClick onItemClick = new OptionRecyclerViewAdapter.OnItemClick() {
        @Override
        public void OnItemClick(int position, OptionBeen optionBeen) {
            //  if (position >= 2) {
            // OptionBeen optionBeen = optionBeenList.get(position - 2);
            Intent intent = new Intent();
            if (PRODUCT) {
                intent.setClass(activity, BookInfoActivity.class);
                intent.putExtra("book_id", optionBeen.getBook_id());
            } else {
                intent.setClass(activity, ComicInfoActivity.class);
                intent.putExtra("comic_id", optionBeen.getComic_id());
            }
            startActivity(intent);
            //   }
        }
    };

    private void initInfo(String result) throws Exception {
        if (OPTION == BAOYUE) {//包月首页
            BaoyueItem baoyueItem = gson.fromJson(result, BaoyueItem.class);
            BaoyueUser baoyueUser = baoyueItem.user;
            if (Utils.isLogin(activity)) {
                baoyueHeadHolder.mAvatar = baoyueUser.avatar;
                baoyueHeadHolder.activity_baoyue_nickname.setText(baoyueUser.nickname);
                if (baoyueUser.expiry_date.length() == 0) {
                    baoyueHeadHolder.activity_baoyue_desc.setText(baoyueUser.vip_desc);
                } else {
                    baoyueHeadHolder.activity_baoyue_desc.setText(baoyueUser.expiry_date);
                }
                MyPicasso.IoadImage(activity, baoyueHeadHolder.mAvatar, R.mipmap.hold_user_avatar, baoyueHeadHolder.activity_baoyue_circle_img);
                if (baoyueUser.baoyue_status == 0) {
                    baoyueHeadHolder.activity_baoyue_ok.setText(LanguageUtil.getString(activity, R.string.BaoyueActivity_kaitong));
                } else {
                    baoyueHeadHolder.activity_baoyue_ok.setText(LanguageUtil.getString(activity, R.string.BaoyueActivity_yikaitong));
                }

            } else {
                baoyueHeadHolder.activity_baoyue_circle_img.setBackgroundResource(R.mipmap.hold_user_avatar);
                baoyueHeadHolder.activity_baoyue_nickname.setText(LanguageUtil.getString(activity, R.string.BaoyueActivity_nologin));

            }
            optionBeenList.addAll(baoyueItem.list);

            optionAdapter.notifyDataSetChanged();
        } else if (OPTION == BAOYUE_SEARCH || SHUKU == OPTION) {
            CategoryItem categoryItem = gson.fromJson(result, CategoryItem.class);
            int optionItem_list_size = categoryItem.list.list.size();
            if (current_page == 1) {
                if (isRefarshHead || temphead.getChildCount() == 0) {
                    if (isRefarshHead) {
                        temphead.removeAllViews();
                    }
                    int raw = 0;
                    for (SearchBox searchBox : categoryItem.search_box) {
                        ++raw;
                        LinearLayout linearLayout = (LinearLayout) layoutInflater.inflate(R.layout.serach_head, null, false);
                        RadioGroup srach_head_RadioGroup = linearLayout.findViewById(R.id.srach_head_RadioGroup);
                        int id = 0;
                        for (SearchBox.SearchBoxLabe searchBoxLabe : searchBox.list) {
                            final MyRadioButton radioButton = (MyRadioButton) layoutInflater.inflate(R.layout.activity_radiobutton, null, false);
                            radioButton.setId(id);

                            radioButton.setfield(searchBox.field);
                            radioButton.setRaw(raw);
                            radioButton.setBackgroundResource(R.drawable.selector_search_box_item);
                            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, ImageUtil.dp2px(activity, 25));
                            params.rightMargin = 30;
                            radioButton.setText(searchBoxLabe.getDisplay());
                            srach_head_RadioGroup.addView(radioButton, params);
                            if (PRODUCT) {
                                if (searchBoxLabe.checked == 1) {
                                    map.put(searchBox.field, searchBoxLabe.value);
                                    radioButton.setChecked(true);
                                }
                            } else {
                                if (id == 0) {
                                    map.put(searchBox.field, searchBoxLabe.value);
                                    radioButton.setChecked(true);
                                }
                            }
                            srach_head_RadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    MyToash.Log("RadioGroup", radioButton.getField() + "  ");
                                    if (PRODUCT) {//小说 男女频道下 分类选项不一样 要重新刷新
                                        if (radioButton.getField().equals("channel_id")) {
                                            isRefarshHead = true;
                                            map.clear();
                                        } else {
                                            isRefarshHead = false;
                                        }
                                    }
                                    map.put(searchBox.field, searchBox.list.get(checkedId).value);
                                    current_page = 1;
                                    LoadingListener = 0;
                                    HttpData();
                                }
                            });
                            ++id;
                        }

                        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) fragment_option_noresult.getLayoutParams();
                        layoutParams.topMargin = ImageUtil.dp2px(activity, 55) * raw;
                        fragment_option_noresult.setLayoutParams(layoutParams);

                        temphead.addView(linearLayout);
                    }
                    optionBeenList.clear();
                    optionBeenList.addAll(categoryItem.list.list);

                    if (!isRefarshHead) {
                        fragment_option_listview.addHeaderView(temphead);
                    }
                    optionAdapter.notifyDataSetChanged();
                    if (optionItem_list_size == 0) {
                        fragment_option_noresult.setVisibility(View.VISIBLE);
                        // MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                    } else {
                        fragment_option_noresult.setVisibility(View.GONE);
                    }

                } else {
                    optionBeenList.clear();
                    optionBeenList.addAll(categoryItem.list.list);
                    optionAdapter.notifyDataSetChanged();
                    if (optionItem_list_size == 0) {
                        fragment_option_noresult.setVisibility(View.VISIBLE);
                        if (current_page > 1) {
                            MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
                        }
                    } else {
                        fragment_option_noresult.setVisibility(View.GONE);
                    }
                }
                Size = optionItem_list_size;
                current_page = categoryItem.list.current_page;
                ++current_page;
            } else if (current_page <= categoryItem.list.current_page) {
                optionBeenList.addAll(categoryItem.list.list);
                int t = Size + optionItem_list_size;
                //   MyToash.Log("notifyItemRangeInserted", optionAdapter.getItemCount() + " " + Size + "  " + optionItem_list_size + "  " + t);
                optionAdapter.notifyItemRangeInserted(Size + 2, optionItem_list_size);
                //  optionAdapter.notifyDataSetChanged();
                Size = t;
                current_page = categoryItem.list.current_page;
                ++current_page;
            } else {
                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
            }

        } else if (OPTION == LOOKMORE) {
            if (recommend_id != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if (current_page == 1) {
                        String title = jsonObject.getJSONObject("recommend").getString("title");
                        titlebar_text.setText(title);
                    }
                    CommonData(jsonObject.getString("list"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                CommonData(result);
            }

        } else {
            CommonData(result);

        }

    }

    int Size;

    private void CommonData(String result) {
        OptionItem optionItem = gson.fromJson(result, OptionItem.class);
        total_page = optionItem.total_page;
        int optionItem_list_size = optionItem.list.size();
        if (current_page <= total_page && optionItem_list_size != 0) {
            if (current_page == 1) {
                optionBeenList.clear();
                optionBeenList.addAll(optionItem.list);
                Size = optionItem_list_size;
                optionAdapter.notifyDataSetChanged();
            } else {
                MyToash.Log("optionBeenList44", current_page + "   " + optionBeenList.size() + "");
                optionBeenList.addAll(optionItem.list);
                int t = Size + optionItem_list_size;
                optionAdapter.notifyItemRangeInserted(Size + 2, optionItem_list_size);
                Size = t;
                // optionAdapter.notifyDataSetChanged();
            }
            MyToash.Log("optionBeenList55", current_page + "   " + optionBeenList.size() + "");
            current_page = optionItem.current_page;
            ++current_page;

        } else {
            if (optionBeenList.isEmpty()) {
                fragment_option_noresult.setVisibility(View.VISIBLE);
            }
            if (current_page > 1) {
                MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.ReadActivity_chapterfail));
            }
        }
    }


    private void HttpData() {
        if (httpUrl == null) {
            return;
        }
        ReaderParams params = new ReaderParams(activity);
        switch (OPTION) {
            case LOOKMORE:
                if (recommend_id != null) {
                    params.putExtraParams("recommend_id", recommend_id + "");
                } else {
                    params.putExtraParams("channel_id", SEX + "");
                }
                params.putExtraParams("page_num", current_page + "");
                break;
            case BAOYUE:
                break;
            case PAIHANG:
                params.putExtraParams("channel_id", SEX + "");
                params.putExtraParams("rank_type", rank_type);
                params.putExtraParams("page_num", current_page + "");
                break;
            case BAOYUE_SEARCH:
            case SHUKU:
                for (Map.Entry<String, String> entry : map.entrySet()) {
                    params.putExtraParams(entry.getKey(), entry.getValue());
                }
                params.putExtraParams("page_num", current_page + "");
                break;
            default:
                params.putExtraParams("channel_id", SEX + "");
                params.putExtraParams("page_num", current_page + "");
                break;
        }
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(httpUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            initInfo(result);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        try {
                            if (LoadingListener == -1) {
                                fragment_option_listview.refreshComplete();
                            } else if (LoadingListener == 1) {
                                fragment_option_listview.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }

                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        try {
                            if (LoadingListener == -1) {
                                fragment_option_listview.refreshComplete();
                            } else if (LoadingListener == 1) {
                                fragment_option_listview.loadMoreComplete();
                            }
                        } catch (Exception e) {
                        }

                    }
                }

        );
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (fragment_option_listview != null) {
            fragment_option_listview.destroy(); // this will totally release XR's memory
            fragment_option_listview = null;
        }
    }
}
