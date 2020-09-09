package com.heiheilianzai.app.ui.activity.comic;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.comic.ComicChapterCatalogAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.ui.fragment.comic.ComicinfoMuluFragment;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.view.MyContentLinearLayoutManager;
import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 漫画详情-目录
 * Created by scb on 2018/6/9.
 */
public class ComicinfoMuluActivity extends BaseButterKnifeActivity {
    @BindView(R2.id.titlebar_back)
    public LinearLayout titlebar_back;
    @BindView(R2.id.fragment_comicinfo_mulu_xu)
    public TextView fragment_comicinfo_mulu_xu;
    @BindView(R2.id.fragment_comicinfo_mulu_xu_img)
    public ImageView fragment_comicinfo_mulu_xu_img;
    @BindView(R2.id.fragment_comicinfo_mulu_list)
    public XRecyclerView fragment_comicinfo_mulu_list;
    @BindView(R2.id.fragment_comicinfo_mulu_layout)
    public RelativeLayout fragment_comicinfo_mulu_layout;
    @BindView(R2.id.fragment_comicinfo_mulu_zhiding_img)
    public ImageView fragment_comicinfo_mulu_zhiding_img;
    @BindView(R2.id.fragment_comicinfo_mulu_zhiding_text)
    public TextView fragment_comicinfo_mulu_zhiding_text;

    String currentChapter_id;
    int Size;
    boolean shunxu, isHttp;
    ComicChapterCatalogAdapter comicChapterCatalogAdapter;
    List<ComicChapter> comicChapterCatalogs;
    boolean orentation;

    @OnClick(value = {R.id.titlebar_back,
            R.id.fragment_comicinfo_mulu_dangqian, R.id.fragment_comicinfo_mulu_zhiding
            , R.id.fragment_comicinfo_mulu_layout})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_comicinfo_mulu_layout:
                shunxu = !shunxu;
                if (!shunxu) {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_zhengxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.positive_order);
                } else {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.reverse_order);
                }
                Collections.reverse(comicChapterCatalogs);
                comicChapterCatalogAdapter.notifyDataSetChanged();
                break;
            case R.id.titlebar_back:
                activity.finish();
                break;
            case R.id.fragment_comicinfo_mulu_dangqian:
                fragment_comicinfo_mulu_list.scrollToPosition(comicChapterCatalogAdapter.getCurrentPosition());
                break;
            case R.id.fragment_comicinfo_mulu_zhiding:
                if (orentation) {
                    fragment_comicinfo_mulu_list.scrollToPosition(0);
                } else {
                    fragment_comicinfo_mulu_list.scrollToPosition(comicChapterCatalogs.size());
                }
                break;
        }
    }

    @Override
    public int initContentView() {
        return R.layout.activity_comicinfo_mulu;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initViews();
    }

    public void initViews() {
        fragment_comicinfo_mulu_list.setLoadingMoreEnabled(false);
        fragment_comicinfo_mulu_list.setPullRefreshEnabled(false);
        MyContentLinearLayoutManager layoutManager = new MyContentLinearLayoutManager(activity);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragment_comicinfo_mulu_list.setLayoutManager(layoutManager);
        comicChapterCatalogs = new ArrayList<>();
        //添加滑动监听
        fragment_comicinfo_mulu_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();//获取LayoutManager
                if (manager != null && manager instanceof LinearLayoutManager) {
                    //第一个可见的位置
                    int firstPosition = ((LinearLayoutManager) manager).findFirstVisibleItemPosition();
                    //如果 dx>0 则表示 右滑 ,dx<0 表示 左滑,dy <0 表示 上滑, dy>0 表示下滑
                    if (dy < 0) {
                        //上滑监听
                        orentation = true;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gototop);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoding));
                    } else {
                        //下滑监听
                        orentation = false;
                        fragment_comicinfo_mulu_zhiding_img.setImageResource(R.mipmap.comicdetail_gotobottom);
                        fragment_comicinfo_mulu_zhiding_text.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daodi));
                    }
                }
            }
        });
        comicChapterCatalogAdapter = new ComicChapterCatalogAdapter(false, null, activity, currentChapter_id, comicChapterCatalogs, ImageUtil.dp2px(activity, 96));
        fragment_comicinfo_mulu_list.setAdapter(comicChapterCatalogAdapter);
        Intent intent = getIntent();
        currentChapter_id = intent.getStringExtra("currentChapter_id");
        String comic_id = intent.getStringExtra("comic_id");
        ComicinfoMuluFragment.GetCOMIC_catalog(activity, comic_id, new ComicinfoMuluFragment.GetCOMIC_catalogList() {
            @Override
            public void GetCOMIC_catalogList(List<ComicChapter> comicChapterList) {
                if (comicChapterList == null) {
                    return;
                }
                comicChapterCatalogs.clear();
                comicChapterCatalogs.addAll(comicChapterList);
                if (comicChapterCatalogs != null && !comicChapterCatalogs.isEmpty()) {
                    Size = comicChapterCatalogs.size();
                    comicChapterCatalogAdapter.notifyDataSetChanged();
                }
            }
        });
    }
}
