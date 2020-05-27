package com.heiheilianzai.app.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.Recommend;
import com.heiheilianzai.app.book.been.BaseBook;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.dialog.WaitDialog;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;

import org.litepal.LitePal;
import org.litepal.crud.callback.SaveCallback;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 首次开启推荐漫画，小说页面
 * Created by abc on 2016/11/4.
 */
public class FirstStartActivity extends BaseButterKnifeActivity {
    @BindView(R2.id.activity_home_viewpager_book_next)
    public TextView activity_home_viewpager_book_next;
    @BindView(R2.id.activity_home_viewpager_book_ok)
    public Button activity_home_viewpager_book_ok;
    @BindView(R2.id.activity_home_viewpager_book_GridView)
    public GridView activity_home_viewpager_book_GridView;
    int WIDTH, HEIGHT;
    private WaitDialog waitDialog;
    boolean bookadd = false, comicadd = false;
    Recommend recommend;
    List<Recommend.RecommendProduc> recommendProducs = new ArrayList<>();
    List<Recommend.RecommendProduc> addrecommendProducs = new ArrayList<>();

    @Override
    public int initContentView() {
        return R.layout.activity_firststart;
    }

    @OnClick(value = {R.id.activity_home_viewpager_book_next, R.id.activity_home_viewpager_book_ok})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.activity_home_viewpager_book_next:
                ShareUitls.putString(activity, "isfirst", "no");
                startMainActivity(activity);
                break;
            case R.id.activity_home_viewpager_book_ok:
                if (addrecommendProducs.isEmpty()) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.firstStartactivity_choosebooks));
                } else {
                    waitDialog.showDailog();
                    activity_home_viewpager_book_ok.setClickable(false);
                    List<BaseBook> list = new ArrayList<>();
                    List<BaseComic> comics = new ArrayList<>();
                    for (Recommend.RecommendProduc addrecommendProducs : addrecommendProducs) {
                        if (addrecommendProducs.book_id != null) {
                            BaseBook mBaseBook = new BaseBook();
                            mBaseBook.setBook_id(addrecommendProducs.book_id);
                            mBaseBook.setName(addrecommendProducs.name);
                            mBaseBook.setCover(addrecommendProducs.cover);
                            mBaseBook.setRecentChapter(addrecommendProducs.total_chapter);
                            mBaseBook.setTotal_Chapter(addrecommendProducs.total_chapter);
                            mBaseBook.setDescription(addrecommendProducs.description);
                            mBaseBook.setName(addrecommendProducs.name);
                            mBaseBook.setAddBookSelf(1);
                            mBaseBook.saveIsexist(1);
                            list.add(mBaseBook);
                        } else {
                            BaseComic baseComic = new BaseComic();
                            baseComic.setComic_id(addrecommendProducs.comic_id);
                            baseComic.setName(addrecommendProducs.name);
                            baseComic.setVertical_cover(addrecommendProducs.cover);
                            baseComic.setRecentChapter(addrecommendProducs.total_chapter);
                            baseComic.setTotal_chapters(addrecommendProducs.total_chapter);
                            baseComic.setDescription(addrecommendProducs.description);
                            baseComic.setAddBookSelf(true);
                            baseComic.saveIsexist(true);
                            comics.add(baseComic);
                        }
                    }
                    LitePal.saveAllAsync(list).listen(new SaveCallback() {
                        @Override
                        public void onFinish(boolean success) {
                            bookadd = true;
                            if (comicadd) {
                                bookadd = false;
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.putExtra("mBaseBooks", (ArrayList<? extends Serializable>) list);
                                intent.putExtra("mBaseComics", (ArrayList<? extends Serializable>) comics);
                                startActivity(intent);
                                waitDialog.dismissDialog();
                            }
                        }
                    });
                    LitePal.saveAllAsync(comics).listen(new SaveCallback() {
                        @Override
                        public void onFinish(boolean success) {
                            comicadd = true;
                            if (bookadd) {
                                comicadd = false;
                                Intent intent = new Intent(activity, MainActivity.class);
                                intent.putExtra("mBaseBooks", (ArrayList<? extends Serializable>) list);
                                intent.putExtra("mBaseComics", (ArrayList<? extends Serializable>) comics);
                                startActivity(intent);
                                waitDialog.dismissDialog();
                            }
                        }
                    });
                    break;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(activity, 30)) / 3;
        HEIGHT = WIDTH * 4 / 3;
        waitDialog = new WaitDialog(activity);
        waitDialog.setCancleable(true);
        getRecommend(1);
    }

    private void setViewPager() {
        activity_home_viewpager_book_GridView.setAdapter(new BaseAdapter() {
            @Override
            public int getCount() {
                return recommendProducs.size();
            }

            @Override
            public Recommend.RecommendProduc getItem(int i) {
                return recommendProducs.get(i);
            }

            @Override
            public long getItemId(int i) {
                return i;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null) {
                    view = LayoutInflater.from(activity).inflate(R.layout.activity_home_viewpager_classfy_gridview_item, null);
                }
                ImageView activity_home_viewpager_classfy_GridView_img = view.findViewById(R.id.activity_home_viewpager_classfy_GridView_img);
                TextView activity_home_flag = view.findViewById(R.id.activity_home_flag);
                RelativeLayout activity_home_viewpager_classfy_GridView_laiout = view.findViewById(R.id.activity_home_viewpager_classfy_GridView_laiout);
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) activity_home_viewpager_classfy_GridView_laiout.getLayoutParams();
                layoutParams.width = WIDTH;
                layoutParams.height = HEIGHT;
                activity_home_viewpager_classfy_GridView_laiout.setLayoutParams(layoutParams);
                final CheckBox activity_home_viewpager_classfy_GridView_box = view.findViewById(R.id.activity_home_viewpager_classfy_GridView_box);
                final Recommend.RecommendProduc recommendProduc = getItem(i);
                int def = R.mipmap.book_def_v;
                if (recommendProduc.book_id != null) {
                    activity_home_flag.setVisibility(View.GONE);
                    def = R.mipmap.book_def_v;
                } else {
                    activity_home_flag.setVisibility(View.VISIBLE);
                    def = R.mipmap.comic_def_v;
                }
                MyPicasso.GlideImage(activity, recommendProduc.cover, activity_home_viewpager_classfy_GridView_img, WIDTH, HEIGHT, def);
                activity_home_viewpager_classfy_GridView_laiout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (recommendProduc.isChoose) {
                            recommendProduc.isChoose = false;
                            activity_home_viewpager_classfy_GridView_box.setChecked(false);
                            addrecommendProducs.remove(recommendProduc);
                            if (addrecommendProducs.isEmpty()) {
                                activity_home_viewpager_book_ok.setBackgroundResource(R.drawable.shape_login_bg);
                                activity_home_viewpager_book_ok.setTextColor(Color.parseColor("#8b8b8c"));
                            }
                        } else {
                            recommendProduc.isChoose = true;
                            activity_home_viewpager_classfy_GridView_box.setChecked(true);
                            addrecommendProducs.add(recommendProduc);
                            if (!addrecommendProducs.isEmpty()) {
                                activity_home_viewpager_book_ok.setBackgroundResource(R.drawable.shape_login_enable_bg);
                                activity_home_viewpager_book_ok.setTextColor(Color.WHITE);
                            }
                        }
                    }
                });
                return view;
            }
        });
    }

    private void getRecommend(int flag) {
        ReaderParams readerParams = new ReaderParams(activity);
        readerParams.putExtraParams("channel_id", flag + "");
        String json = readerParams.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.start_recommend, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(String response) {
                        ShareUitls.putString(activity, "isfirst", "no");
                        recommend = new Gson().fromJson(response, Recommend.class);
                        if (recommend != null) {
                            recommendProducs.addAll(recommend.book);
                            recommendProducs.addAll(recommend.comic);
                            setViewPager();
                        } else {
                            startMainActivity(activity);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        startMainActivity(activity);
                    }
                }
        );
    }

    public void startMainActivity(Activity activity) {
        startActivity(new Intent(activity, MainActivity.class));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public interface Save_recommend {
        void saveSuccess();
    }

    public static void save_recommend(final Activity activity, final Save_recommend save_recommend) {
    }
}
