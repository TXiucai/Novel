package com.heiheilianzai.app.ui.dialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.ChapterManager;
import com.heiheilianzai.app.component.http.DownloadUtil;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChapterContent;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.Downoption;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.ui.dialog.read.PurchaseDialog;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.SensorsDataHelper;
import com.heiheilianzai.app.utils.ShareUitls;
import com.wang.avi.AVLoadingIndicatorView;
import com.wang.avi.indicators.LineSpinFadeLoaderIndicator;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static com.heiheilianzai.app.constant.ReaderConfig.DOWN;

/**
 * 下载小说对话框
 */
public class DownDialog {
    public static boolean showOpen;
    Dialog bottomDialog;
    AVLoadingIndicatorView item_dialog_downadapter_RotationLoadingView;
    private ChapterItem ChapterItem;
    public static boolean isreaderbook;
    public boolean Getdown_urling = false;
    public Activity Activity;

    public void showDownoption(final Activity activity, final List<Downoption> downoption) {
        bottomDialog = new Dialog(activity, R.style.BottomDialog);
        if (item_dialog_downadapter_RotationLoadingView != null) {
            item_dialog_downadapter_RotationLoadingView.setVisibility(View.GONE);
        }
        bottomDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                return true;
            }
        });
        View view = LayoutInflater.from(activity).inflate(R.layout.dialog_down, null);
        final TextView dialog_dowu_manger = view.findViewById(R.id.dialog_dowu_manger);
        final ListView dialog_dowu_list = view.findViewById(R.id.dialog_dowu_list);
        Dialog_downAdapter dialog_downAdapter = new Dialog_downAdapter(activity, downoption);
        dialog_dowu_list.setAdapter(dialog_downAdapter);
        final boolean[] noclick = {false};
        dialog_dowu_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int i, long l) {
                if (!downoption.get(i).isdown && !noclick[0]) {
                    noclick[0] = true;
                    item_dialog_downadapter_RotationLoadingView = v.findViewById(R.id.item_dialog_downadapter_RotationLoadingView);
                    item_dialog_downadapter_RotationLoadingView.setVisibility(View.VISIBLE);
                    Downoption downoption1 = downoption.get(i);
                    ChapterManager.downChapter(activity, downoption1.book_id, new ChapterManager.DownChapter() {
                        @Override
                        public void success() {
                            getdown_url(activity, downoption1);
                        }

                        @Override
                        public void fail() {
                            MyToash.ToashError(activity, "下载异常,请确认网络连接");
                            bottomDialog.dismiss();
                        }
                    });
                }
            }
        });
        dialog_dowu_manger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomDialog.dismiss();
                activity.startActivity(new Intent(activity, BaseOptionActivity.class).putExtra("OPTION", DOWN).putExtra("title", LanguageUtil.getString(activity, R.string.BookInfoActivity_down_manger)));
            }
        });
        bottomDialog.setContentView(view);
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
        params.width = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        view.setLayoutParams(params);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.BottomDialog_Animation);
        bottomDialog.show();
        bottomDialog.onWindowFocusChanged(false);
        bottomDialog.setCanceledOnTouchOutside(true);
    }

    class Dialog_downAdapter extends BaseAdapter {
        List<Downoption> list;
        Activity activity;

        public Dialog_downAdapter(Activity activity, List<Downoption> list) {
            this.list = list;
            this.activity = activity;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Downoption getItem(int i) {
            return list.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View contentView = LayoutInflater.from(activity).inflate(R.layout.item_dialog_downadapter, null, false);
            TextView item_dialog_downadapter_lable = contentView.findViewById(R.id.item_dialog_downadapter_lable);
            TextView item_dialog_downadapter_tag = contentView.findViewById(R.id.item_dialog_downadapter_tag);
            TextView item_dialog_downadapter_isxiazai = contentView.findViewById(R.id.item_dialog_downadapter_isxiazai);
            AVLoadingIndicatorView item_dialog_downadapter_RotationLoadingView = contentView.findViewById(R.id.item_dialog_downadapter_RotationLoadingView);
            item_dialog_downadapter_RotationLoadingView.setIndicator(new LineSpinFadeLoaderIndicator());
            item_dialog_downadapter_lable.setText(getItem(i).label);
            if (getItem(i).tag.length() > 0) {
                item_dialog_downadapter_tag.setText(getItem(i).tag);
                item_dialog_downadapter_tag.setVisibility(View.VISIBLE);
            } else {
                item_dialog_downadapter_tag.setVisibility(View.GONE);
            }
            if (getItem(i).isdown) {
                item_dialog_downadapter_isxiazai.setVisibility(View.VISIBLE);
                item_dialog_downadapter_RotationLoadingView.setVisibility(View.GONE);
            } else {
                item_dialog_downadapter_isxiazai.setVisibility(View.GONE);
            }
            return contentView;
        }
    }

    /**
     * 获取小说下载的章节列表
     */
    public void getDownoption(final Activity activity, final BaseBook baseBook, ChapterItem chapterItem) {
        if (!MainHttpTask.getInstance().Gotologin(activity)) {
            return;
        }
        if (Getdown_urling) {
            return;
        }
        if (chapterItem == null) {
            isreaderbook = false;
        } else {
            isreaderbook = true;
        }
        ChapterItem = chapterItem;
        String chapter_id = chapterItem != null ? chapterItem.getChapter_id() : (baseBook.getCurrent_chapter_id() == null ? "" : baseBook.getCurrent_chapter_id());
        MyToash.Log("chapter_iddd", (chapterItem == null) + "   " + chapter_id);
        ReaderParams readerParams = new ReaderParams(activity);
        readerParams.putExtraParams("book_id", baseBook.getBook_id());
        readerParams.putExtraParams("chapter_id", chapter_id);
        String json = readerParams.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + "/chapter/down-option", json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject JsonObject = new JSONObject(result);
                            List<Downoption> downoptions = LitePal.where("book_id= ?", baseBook.getBook_id()).find(Downoption.class);
                            JSONArray jsonArray = JsonObject.getJSONArray("down_option");
                            if (jsonArray.length() > 0) {
                                List<Downoption> list = new ArrayList<>();
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    Downoption downoption = new Downoption();
                                    downoption.label = jsonObject.getString("label");
                                    downoption.tag = jsonObject.getString("tag");
                                    downoption.s_chapter = jsonObject.getString("s_chapter");
                                    downoption.down_num = jsonObject.getInt("down_num");
                                    downoption.file_name = jsonObject.getString("file_name");
                                    downoption.tag = jsonObject.getString("tag");
                                    downoption.book_id = baseBook.getBook_id();
                                    downoption.cover = baseBook.getCover();
                                    downoption.bookname = baseBook.getName();
                                    downoption.description = baseBook.getDescription();
                                    downoption.start_order = jsonObject.getInt("start_order");
                                    downoption.end_order = jsonObject.getInt("end_order");
                                    downoption.isdown = false;
                                    for (Downoption downoption1 : downoptions) {//去重 起始 和结束序号 包含在已下载内 设为已下载
                                        if (downoption1.start_order <= downoption.start_order && downoption.end_order <= downoption1.end_order) {
                                            downoption.isdown = true;
                                        }
                                    }
                                    list.add(downoption);
                                }
                                showDownoption(activity, list);
                            }
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

    /**
     * 下载某段章节到本地
     */
    public void getdown_url(final Activity activity, final Downoption downoption) {
        setXSWorkDownloadEvent(downoption.book_id, downoption.s_chapter, String.valueOf(downoption.down_num));
        Getdown_urling = true;
        ReaderParams readerParams = new ReaderParams(activity);
        readerParams.putExtraParams("book_id", downoption.book_id);
        readerParams.putExtraParams("chapter_id", downoption.s_chapter);
        readerParams.putExtraParams("num", downoption.down_num + "");
        String json = readerParams.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + "/chapter/down-url", json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        Getdown_urling = false;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            final String file_url = ReaderConfig.getBaseUrl() + jsonObject.getString("file_url");
                            final String file_name = jsonObject.getString("file_name");
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    downFile(activity, file_url, file_name, downoption);
                                }
                            }).start();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        Getdown_urling = false;
                        if (ex != null && ex.equals("701")) {
                            if (bottomDialog != null) {
                                bottomDialog.dismiss();
                            }
                            PurchaseDialog mPurchaseDialog = new PurchaseDialog(activity, true);
                            String str = downoption.label;
                            if (ChapterItem != null) {
                                str = ChapterItem.getChapter_title() + " (" + str + ")";
                            }
                            mPurchaseDialog.initData(downoption.book_id, downoption.s_chapter, downoption, str);
                            mPurchaseDialog.show();
                        }
                    }
                }
        );
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    MyToash.ToashSuccess(Activity, LanguageUtil.getString(Activity, R.string.BookInfoActivity_down_adddown));
                    if (item_dialog_downadapter_RotationLoadingView != null) {
                        item_dialog_downadapter_RotationLoadingView.setVisibility(View.GONE);
                    }
                    if (bottomDialog != null) {
                        bottomDialog.dismiss();
                    }
                    break;
                case 1:
                    MyToash.ToashSuccess(Activity, LanguageUtil.getString(Activity, R.string.BookInfoActivity_down_downcomplete));
                    break;
                case 2:
                    break;
            }
        }
    };

    private void downFile(final Activity activity, String url, String file_name, final Downoption downoption) {
        DownloadUtil.get().download(url, FileManager.getSDCardRoot() + "downoption/", file_name,
                new DownloadUtil.OnDownloadListener() {
                    @Override
                    public void onDownloadSuccess(File file) {
                        Activity = activity;
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                        downoption.downoption_date = formatter.format(System.currentTimeMillis());
                        ShareUitls.putDown(activity, downoption.book_id, downoption.start_order + "-" + downoption.end_order);
                        List<Downoption> downoptions = LitePal.where("book_id= ?", downoption.book_id).find(Downoption.class);
                        for (Downoption downoption1 : downoptions) {//当前开始序号 包含在 已经下载的 某条序号记录内   重新记录最新的下载记录 为两个叠加
                            if (downoption.start_order <= downoption1.start_order && downoption1.end_order <= downoption.end_order) {
                                LitePal.deleteAll(Downoption.class, "file_name=?", downoption1.file_name);
                            } else if (downoption1.start_order <= downoption.start_order && downoption.start_order <= downoption1.end_order && downoption.end_order > downoption1.end_order) {
                                LitePal.deleteAll(Downoption.class, "file_name=?", downoption1.file_name);
                                downoption.start_order = downoption1.start_order;
                            } else {
                                if (downoption1.start_order <= downoption.end_order && downoption.end_order <= downoption1.end_order && downoption.start_order < downoption1.start_order) {
                                    LitePal.deleteAll(Downoption.class, "file_name=?", downoption1.file_name);
                                    downoption.end_order = downoption1.end_order;
                                }
                            }
                        }
                        downoption.save();
                        handler.sendEmptyMessage(0);
                        EventBus.getDefault().post(downoption);//通知进度更新
                        int size = 0;
                        String txt2String = FileManager.txt2String(file);
                        JsonParser jsonParser = new JsonParser();
                        JsonArray jsonElements = jsonParser.parse(txt2String).getAsJsonArray();//获取JsonArray对象
                        Gson gson = new Gson();
                        int temp = 0;
                        for (JsonElement jsonElement : jsonElements) {
                            ChapterContent chapterContent = gson.fromJson(jsonElement, ChapterContent.class);//解析
                            String filepath = FileManager.getSDCardRoot().concat("Reader/book/").concat(downoption.book_id + "/").concat(chapterContent.getChapter_id() + "/").concat(chapterContent.getIs_preview() + "/").concat(chapterContent.getIs_new_content() + "/").concat(chapterContent.getUpdate_time()).concat(".txt");
                            FileManager.createFile(filepath, chapterContent.getContent().getBytes());
                            size += chapterContent.getWords() * 3;
                            ++temp;
                            downoption.down_cunrrent_num = temp;
                            EventBus.getDefault().post(downoption);//通知进度更新
                        }
                        downoption.downoption_size = (int) (((float) size / 1048576f) * 100) / 100f + "M";
                        downoption.isdown = true;
                        ContentValues values = new ContentValues();
                        values.put("downoption_size", (int) (((float) size / 1048576f) * 100) / 100f + "M");
                        values.put("isdown", true);
                        LitePal.updateAll(Downoption.class, values, "file_name = ?", downoption.file_name);
                        EventBus.getDefault().post(downoption);
                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onDownloading(int totle, int progress) {
                    }

                    @Override
                    public void onDownloadFailed(Exception e) {
                    }
                });
    }

    /**
     * 神策埋点 小说下载 (由于小说下载并非按照章节,暂定为埋点 chapter_id num)
     */
    private void setXSWorkDownloadEvent(String workId, String chapter_id, String num) {
        try {
            List<String> chapterList = new ArrayList<>();
            chapterList.add(chapter_id);
            chapterList.add(num);
            SensorsDataHelper.setXSWorkDownloadEvent(Integer.valueOf(workId),//漫画iD
                    chapterList);//选中下载章节
        } catch (Exception e) {
        }
    }
}