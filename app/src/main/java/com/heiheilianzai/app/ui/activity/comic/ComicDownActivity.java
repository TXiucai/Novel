package com.heiheilianzai.app.ui.activity.comic;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.comic.ComicDownOptionAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.ComicDownOptionData;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.comic.DownComicEvenbus;
import com.heiheilianzai.app.ui.dialog.WaitDialog;
import com.heiheilianzai.app.ui.dialog.comic.PurchaseDialog;
import com.heiheilianzai.app.ui.fragment.comic.ComicinfoMuluFragment;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.StringUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 漫画下载页面
 */
public class ComicDownActivity extends BaseButterKnifeActivity {
    private static final String CHAPTER_SEPARATOR = ",";
    @BindView(R.id.titlebar_text)
    public TextView titlebar_text;
    @BindView(R.id.activity_comicdown_choose_count)
    public TextView activity_comicdown_choose_count;
    @BindView(R.id.fragment_comicinfo_mulu_zhuangtai)
    public TextView fragment_comicinfo_mulu_zhuangtai;
    @BindView(R.id.activity_comicdown_down)
    public TextView activity_comicdown_down;
    @BindView(R.id.activity_comicdown_gridview)
    public GridView activity_comicdown_gridview;
    @BindView(R.id.fragment_bookshelf_noresult)
    public LinearLayout fragment_bookshelf_noresult;
    @BindView(R.id.fragment_comicinfo_mulu_xu)
    public TextView fragment_comicinfo_mulu_xu;
    @BindView(R.id.fragment_comicinfo_mulu_xu_img)
    public ImageView fragment_comicinfo_mulu_xu_img;
    @BindView(R.id.fragment_comicinfo_mulu_layout)
    public RelativeLayout fragment_comicinfo_mulu_layout;
    boolean shunxu, Flag;
    String comic_id;
    List<ComicChapter> comicDownOptionList;
    ComicDownOptionAdapter comicDownOptionAdapter;
    Gson gson = new Gson();
    List<ComicChapter> comicChapterCatalogs;
    BaseComic baseComic;
    int down_chapters;
    long id;
    int Size;
    PurchaseDialog purchaseDialog;

    @Override
    public int initContentView() {
        return R.layout.activity_comicdown;
    }

    @OnClick(value = {R.id.titlebar_back, R.id.activity_comicdown_quanxuan, R.id.activity_comicdown_down, R.id.fragment_comicinfo_mulu_layout})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.activity_comicdown_quanxuan:
                if (comicDownOptionAdapter != null) {
                    comicDownOptionAdapter.selectAll();
                }
                break;
            case R.id.fragment_comicinfo_mulu_layout:
                shunxu = !shunxu;
                if (!shunxu) {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_zhengxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.positive_order);
                } else {
                    fragment_comicinfo_mulu_xu.setText(LanguageUtil.getString(activity, R.string.fragment_comic_info_daoxu));
                    fragment_comicinfo_mulu_xu_img.setImageResource(R.mipmap.reverse_order);
                }
                Collections.reverse(comicDownOptionList);
                if (comicDownOptionAdapter != null) {
                    comicDownOptionAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.activity_comicdown_down://  public List<ComicDownOption> comicDownOptionListChooseDwn;
                if (!Flag) {//下载
                    if (!MainHttpTask.getInstance().Gotologin(activity)) {
                        return;
                    }
                    String Chapter_id = "";
                    if (comicDownOptionAdapter != null) {
                        for (ComicChapter comicDownOption : comicDownOptionAdapter.comicDownOptionListChooseDwn) {
                            Chapter_id += CHAPTER_SEPARATOR + comicDownOption.chapter_id;
                        }
                        if (!StringUtils.isEmpty(Chapter_id)) {
                            httpDownChapter(Chapter_id.substring(1));
                        }
                    }
                } else {//删除
                    if (comicDownOptionAdapter != null) {
                        for (ComicChapter comicDownOption : comicDownOptionAdapter.comicDownOptionListChooseDwn) {
                            ShareUitls.putComicDownStatus(activity, comicDownOption.chapter_id, 0);//
                            ContentValues values = new ContentValues();//设置该章节为 没有下载过
                            values.put("ISDown", "0");
                            LitePal.update(ComicChapter.class, values, comicDownOption.getId());
                            String localPath = FileManager.getManhuaSDCardRoot().concat(baseComic.getComic_id() + "/").concat(comicDownOption.chapter_id);
                            FileManager.deleteFile(localPath);//删除章节的图片
                        }
                        int size = comicDownOptionAdapter.comicDownOptionListChooseDwn.size();
                        int deleteSize = Size - size;
                        if (deleteSize == 0) {
                            fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
                        }
                        baseComic.setDown_chapters(deleteSize);
                        ContentValues values1 = new ContentValues();
                        values1.put("down_chapters", deleteSize);
                        LitePal.update(BaseComic.class, values1, id);
                        EventBus.getDefault().post(baseComic);//更新上一界面的 数据
                        comicDownOptionList.removeAll(comicDownOptionAdapter.comicDownOptionListChooseDwn);
                        comicDownOptionAdapter = new ComicDownOptionAdapter(activity, comicDownOptionList, activity_comicdown_choose_count, activity_comicdown_down, Flag);
                        activity_comicdown_gridview.setAdapter(comicDownOptionAdapter);
                        comicDownOptionAdapter.comicDownOptionListChooseDwn.clear();
                        MyToash.ToashSuccess(activity, String.format(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_yishanchus), size));
                    }
                }
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        Intent intent = getIntent();
        baseComic = (BaseComic) intent.getSerializableExtra("baseComic");
        Flag = intent.getBooleanExtra("flag", false);//是否只显示已下载列表
        comic_id = baseComic.getComic_id();
        down_chapters = baseComic.getDown_chapters();
        id = baseComic.getId();
        MyToash.Log("baseComicid", id + "");
        comicDownOptionList = new ArrayList<>();
        activity_comicdown_down.setClickable(false);
        ComicinfoMuluFragment.GetCOMIC_catalog(activity, comic_id, new ComicinfoMuluFragment.GetCOMIC_catalogList() {
            @Override
            public void GetCOMIC_catalogList(List<ComicChapter> comicChapterList) {
                if (comicChapterList != null && !comicChapterList.isEmpty()) {
                    comicChapterCatalogs = comicChapterList;
                    httpData();
                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshMine refreshMine) {
        if (purchaseDialog != null && purchaseDialog.isShowing()) {
            purchaseDialog.dismiss();
        }
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_down_option, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            JsonParser jsonParser = new JsonParser();
                            JsonArray jsonElements = jsonParser.parse(jsonObject.getString("down_list")).getAsJsonArray();//获取JsonArray对象
                            int i = 0;
                            for (JsonElement jsonElement : jsonElements) {
                                ComicChapter comicChapter = gson.fromJson(jsonElement, ComicChapter.class);
                                comicDownOptionList.get(i).is_preview = comicChapter.is_preview;
                                ++i;
                            }
                            comicDownOptionAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public void httpData() {
        activity_comicdown_down.setClickable(true);
        if (Flag) {//下载缓存
            fragment_comicinfo_mulu_layout.setVisibility(View.GONE);
            comicDownOptionList = LitePal.where("comic_id = ? and ISDown=?", comic_id, "1").find(ComicChapter.class);
            Size = comicDownOptionList.size();
            if (Size != 0) {
                activity_comicdown_down.setText(LanguageUtil.getString(activity, R.string.ReadHistoryFragment_shangchu));
                fragment_comicinfo_mulu_zhuangtai.setText(String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_yixiazai), Size));
                comicDownOptionAdapter = new ComicDownOptionAdapter(activity, comicDownOptionList, activity_comicdown_choose_count, activity_comicdown_down, Flag);
                activity_comicdown_gridview.setAdapter(comicDownOptionAdapter);
            } else {
                fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
            }
            titlebar_text.setText(LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger));
            return;
        }
        titlebar_text.setText(LanguageUtil.getString(activity, R.string.ComicDownActivity_title));
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_down_option, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            ComicDownOptionData.Base_info base_info = gson.fromJson(jsonObject.getString("base_info"), ComicDownOptionData.Base_info.class);
                            fragment_comicinfo_mulu_zhuangtai.setText(base_info.display_label);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        ComicDownOptionData comicDownOptionData = gson.fromJson(result, ComicDownOptionData.class);
                        fragment_comicinfo_mulu_zhuangtai.setText(comicDownOptionData.base_info.display_label);
                        if (!comicDownOptionData.down_list.isEmpty()) {
                            comicDownOptionList.addAll(comicDownOptionData.down_list);
                            comicDownOptionAdapter = new ComicDownOptionAdapter(activity, comicDownOptionList, activity_comicdown_choose_count, activity_comicdown_down, Flag);
                            activity_comicdown_gridview.setAdapter(comicDownOptionAdapter);
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }

        );
    }

    public void httpDownChapter(String chapter_id) {
        setMHWorkDownloadEvent(comic_id, chapter_id);
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", comic_id);
        params.putExtraParams("chapter_id", chapter_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParamsDialog(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_down, json, new HttpUtils.ResponseListenerDialog() {
                    @Override
                    public void onResponse(final String result, WaitDialog waitDialog) {
                        Collections.sort(comicDownOptionAdapter.comicDownOptionListChooseDwn);// 排序
                        for (ComicChapter comicDownOption : comicDownOptionAdapter.comicDownOptionListChooseDwn) {
                            ShareUitls.putComicDownStatus(activity, comicDownOption.chapter_id, 2);
                        }
                        comicDownOptionAdapter.notifyDataSetChanged();
                        comicDownOptionAdapter.comicDownOptionListChooseDwn.clear();
                        comicDownOptionAdapter.refreshBtn(0);
                        MyToash.Log("XXomicChapter22", "333");
                        MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.BookInfoActivity_down_adddown));
                        Intent intent = new Intent();
                        intent.setAction("com.heiheilianzai.app.ui.activity.comic.DownComicService");
                        intent.setPackage(BuildConfig.APPLICATION_ID);
                        Bundle bundle2 = new Bundle();
                        bundle2.putSerializable("baseComic", baseComic);
                        bundle2.putString("result", result);
                        bundle2.putSerializable("comicChapter", (Serializable) comicChapterCatalogs);
                        intent.putExtra("downcomic", bundle2);
                        startService(intent);
                        waitDialog.dismissDialog();
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        if (ex != null && ex.equals("701")) {
                            PurchaseDialog purchaseDialog = new PurchaseDialog(activity, true, new PurchaseDialog.BuySuccess() {
                                @Override
                                public void buySuccess(String[] ids, int num) {
                                    for (ComicChapter comicDownOption : comicDownOptionAdapter.comicDownOptionListChooseDwn) {
                                        comicDownOption.is_preview = 0;
                                    }
                                    comicDownOptionAdapter.notifyDataSetChanged();
                                    httpDownChapter(chapter_id);
                                }
                            }, true);
                            purchaseDialog.initData(comic_id, chapter_id);
                            purchaseDialog.show();
                        }
                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void DownComicEvenbus(DownComicEvenbus downComicEvenbus) {//
        MyToash.Log("DownComicEvenbus", downComicEvenbus.baseComic.getComic_id());
        try {
            if (downComicEvenbus.baseComic.getComic_id().equals(comic_id)) {
                if (downComicEvenbus.flag) {
                    MyToash.ToashSuccess(activity, String.format(LanguageUtil.getString(activity, R.string.BookInfoActivity_down_downcompleteSize), downComicEvenbus.Down_Size));
                } else {
                    comicDownOptionAdapter.notifyDataSetChanged();
                }
            }
        } catch (Exception e) {
        }
    }

    /**
     * 神策埋点 漫画下载
     *
     * @param workId
     * @param chapters
     */
    private void setMHWorkDownloadEvent(String workId, String chapters) {
        try {
            SensorsDataHelper.setMHWorkDownloadEvent(Integer.valueOf(workId),//漫画iD
                    StringUtils.getStringToList(chapters, CHAPTER_SEPARATOR));//选中下载章节
        } catch (Exception e) {
        }
    }
}