package com.heiheilianzai.app.ui.fragment.comic;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.adapter.comic.ComicAdapterNew;
import com.heiheilianzai.app.callback.OnItemClickListener;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.holder.CBViewHolderCreator;
import com.heiheilianzai.app.model.Announce;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.ToStore;
import com.heiheilianzai.app.model.event.comic.RefreshComic;
import com.heiheilianzai.app.ui.activity.AnnounceActivity;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicLookActivity;
import com.heiheilianzai.app.ui.fragment.BookshelfFragment;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.InternetUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.ComicShelfBannerHolderView;
import com.heiheilianzai.app.view.ConvenientBannerBookShelf;
import com.heiheilianzai.app.view.MarqueeTextView;
import com.heiheilianzai.app.view.MarqueeTextViewClickListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 书架漫画
 */
public class ComicshelfFragment extends Fragment {
    @BindView(R2.id.fragment_bookshelf_scrollview)
    NestedScrollView fragment_comicshelf_scrollview;
    @BindView(R2.id.fragment_bookshelf_head)
    public LinearLayout fragment_bookshelf_head;
    @BindView(R2.id.fragment_shelf_banner_male)
    public ConvenientBannerBookShelf fragment_discovery_banner_male;
    @BindView(R2.id.fragment_shelf_banner_layout)
    public LinearLayout fragment_shelf_banner_layout;
    @BindView(R2.id.fragment_bookshelf_noresult)
    public LinearLayout fragment_bookshelf_noresult;
    @BindView(R2.id.fragment_bookshelf_marquee_layout)
    public LinearLayout fragment_bookshelf_marquee_layout;
    @BindView(R2.id.fragment_bookshelf_marquee)
    public MarqueeTextView fragment_bookshelf_marquee;
    @BindView(R2.id.fragment_comicshelf_AdaptionGridViewNoMargin)
    public AdaptionGridViewNoMargin bookShelf;
    public List<BaseComic> bookLists;
    public int WIDTH, HEIGHT, HorizontalSpacing, H40;
    public ComicAdapterNew adapter;
    private TextView mDeleteBtn;
    LinearLayout shelf_book_delete_btn;
    View MainActivityNavigationView;
    Activity activity;
    public TextView fragment_novel_allchoose;
    public TextView fragment_novel_cancle;
    boolean showGuangbo;
    Gson gson = new Gson();
    long time1;

    public void AllchooseAndCancleOnclick(boolean flag) {
        if (flag) {
            if (adapter.checkedBookList.size() == bookLists.size()) {
                adapter.selectAll(false);
            } else {
                adapter.selectAll(true);
            }
            adapter.notifyDataSetChanged();
        } else {
            if (adapter != null && adapter.isDeletable()) {
                adapter.setDeletable(false);
                adapter.notifyDataSetChanged();
                MainActivityNavigationView.setVisibility(View.VISIBLE);
                shelf_book_delete_btn.setVisibility(View.GONE);
                fragment_bookshelf_head.setVisibility(View.VISIBLE);
                if (showGuangbo) {
                    fragment_bookshelf_marquee_layout.setVisibility(View.VISIBLE);
                }
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = getActivity();
        EventBus.getDefault().register(this);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(activity, 40)) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HorizontalSpacing = ImageUtil.dp2px(activity, 5);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comicshelf, null);
        ButterKnife.bind(this, view);
        bookShelf.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position < bookLists.size()) {
                    if (!adapter.isDeletable()) {
                        BaseComic baseComic = bookLists.get(position);
                        baseComic.setAddBookSelf(true);
                        Intent intent = new Intent(activity, ComicLookActivity.class);
                        intent.putExtra("baseComic", baseComic);
                        startActivity(intent);
                        if (position != 0) {
                            openBaseComic = baseComic;
                            openPosition = position;
                            handler.sendEmptyMessageDelayed(3, 2000);
                        }
                    }
                }

            }
        });
        bookShelf.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                setLongClickListener(position);
                return true;
            }
        });
        MainActivityNavigationView = ((MainActivity) activity).getNavigationView();
        return view;
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (bookLists == null || bookLists.isEmpty()) {
            bookLists = LitePal.where("isAddBookSelf = ?", "1").find(BaseComic.class);
            Collections.sort(bookLists);// 排序
        }
        setAdapter();
        if (bookLists.isEmpty()) {
            fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
            bookShelf.setVisibility(View.GONE);
        } else {
            fragment_bookshelf_noresult.setVisibility(View.GONE);
            bookShelf.setVisibility(View.VISIBLE);
        }
        MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF, "ShelfComic", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                handleResult(result);
            }
        });
    }

    @OnClick(value = {R.id.fragment_bookshelf_go_shelf, R.id.fragment_bookshelf_sign})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.fragment_bookshelf_go_shelf:
                //去书城逛逛
                EventBus.getDefault().post(new ToStore(2));
                break;
            case R.id.fragment_bookshelf_sign:
                if (Utils.isLogin(activity)) {
                    startActivity(new Intent(activity, TaskCenterActivity.class));
                } else {
                    MainHttpTask.getInstance().Gotologin(activity);
                }
                break;
        }
    }

    private void setAdapter() {
        adapter = new ComicAdapterNew(WIDTH, HEIGHT, bookLists, activity);
        bookShelf.setAdapter(adapter);
    }

    public void exchangePosition(BaseComic onclickbook, int position) {
        BaseComic mTopBook = bookLists.get(0);
        ContentValues values2 = new ContentValues();
        onclickbook.setBookselfPosition(10000);
        values2.put("bookselfPosition", 10000);
        if (onclickbook.getId() == 0) {
            LitePal.updateAll(BaseComic.class, values2, "comic_id = ?", onclickbook.getComic_id());
        } else {
            LitePal.update(BaseComic.class, values2, onclickbook.getId());
        }
        ContentValues values = new ContentValues();
        mTopBook.setBookselfPosition(0);
        values.put("bookselfPosition", 0);
        if (mTopBook.getId() == 0) {
            LitePal.updateAll(BaseComic.class, values, "comic_id = ?", mTopBook.getComic_id());
        } else {
            LitePal.update(BaseComic.class, values, mTopBook.getId());
        }
        bookLists.add(0, onclickbook);
        bookLists.remove(position + 1);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBookSelf(RefreshComic s) {
        if (System.currentTimeMillis() - time1 <= 2000) {
            return;
        }
        time1 = System.currentTimeMillis();
        if (s.flag) {//退出登录集合数据 刷新
            adapter.notifyDataSetChanged();
        } else if (s.baseComics == null && s.baseComic == null) {//登录
            init();
        } else {//加书架
            MyToash.Log("setColor---1", " " + bookLists.size());
            if (s.ADD == -1) {
                for (BaseComic BaseComic : s.baseComics) {//批量添加
                    MyToash.Log("BaseComic", BaseComic.vertical_cover);
                    if (!bookLists.contains(BaseComic)) {
                        bookLists.add(BaseComic);
                    }
                }
                String bookid = "";
                for (BaseComic BaseComic : s.baseComics) {
                    bookid += "," + BaseComic.getComic_id();
                }
                bookid = bookid.substring(1);
                addHttpBookself(bookid);
            } else if (s.ADD == 0) {//删除
                if (bookLists.contains(s.baseComic)) {
                    bookLists.remove(s.baseComic);
                }
                adapter.deleteBook(s.baseComic.getComic_id());
            } else {//添加
                if (!bookLists.contains(s.baseComic)) {
                    bookLists.add(s.baseComic);
                }
                addHttpBookself(s.baseComic.getComic_id());
            }
            setAdapter();
            if (!bookLists.isEmpty()) {
                fragment_bookshelf_noresult.setVisibility(View.GONE);
                bookShelf.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setDataBaseData(boolean IsSncyWebBookShelf) {
        if (bookLists != null && bookLists.isEmpty()) {
            fragment_bookshelf_noresult.setVisibility(View.VISIBLE);
            bookShelf.setVisibility(View.GONE);
        } else {
            fragment_bookshelf_noresult.setVisibility(View.GONE);
            bookShelf.setVisibility(View.VISIBLE);
            setBookSelfBooks();
            if (IsSncyWebBookShelf) {
                addHttpBookself(null);
            }
        }
    }

    private void setBookSelfBooks() {
        setAdapter();
    }

    private void addHttpBookself(String bookid) {
        if (Utils.isLogin(activity) && InternetUtils.internett(activity)) {
            if (bookid == null) {
                for (BaseComic BaseComic : bookLists) {
                    bookid += "," + BaseComic.getComic_id();
                }
                bookid = bookid.substring(1);
            }
            addBookToShelf(bookid, activity, new AddBookToShelf() {
                @Override
                public void addSuccess() {
                }

                @Override
                public void addFail() {
                }
            });
        }
    }

    public void handleResult(String result) {
        try {
            JSONObject obj = new JSONObject(result);
            setBanner(obj);
            if (Utils.isLogin(activity)) {
                final JSONArray bookArr = obj.getJSONArray("list");
                if (bookArr.length() != 0) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            MyToash.Log("QQhandleResult", bookArr.toString());
                            try {
                                String uid = Utils.getUID(activity);
                                for (int i = 0; i < bookArr.length(); i++) {
                                    BaseComic BaseComic = new BaseComic();
                                    JSONObject jsonObject = bookArr.getJSONObject(i);
                                    BaseComic.setName(jsonObject.getString("name"));
                                    BaseComic.setComic_id(jsonObject.getString("comic_id"));
                                    BaseComic.setVertical_cover(jsonObject.getString("vertical_cover"));
                                    int total_chapter = jsonObject.getInt("total_chapters");
                                    BaseComic.setRecentChapter(total_chapter);
                                    BaseComic.setTotal_chapters(total_chapter);
                                    BaseComic.setUid(uid);
                                    boolean isflag = false;//是否存在
                                    FALG:
                                    for (BaseComic BaseComict : bookLists) {
                                        if (BaseComict.getComic_id().equals(BaseComic.getComic_id())) {
                                            isflag = true;
                                            BaseComict.setUid(uid);
                                            if (total_chapter > BaseComict.getTotal_chapters()) {//更新最新章节数目
                                                BaseComict.setTotal_chapters(total_chapter);
                                                BaseComict.setRecentChapter(total_chapter - BaseComict.getRecentChapter());
                                            }
                                            BaseComict.setName(BaseComic.getName());
                                            BaseComict.setVertical_cover(BaseComic.getVertical_cover());
                                            break FALG;
                                        }
                                    }
                                    if (!isflag) {
                                        BaseComic.setAddBookSelf(BaseComic.saveIsexist(true));
                                        BaseComic.setAddBookSelf(true);
                                        bookLists.add(BaseComic);
                                    }
                                }
                                for (BaseComic BaseComict : bookLists) {
                                    if (!BaseComict.isAddBookSelf()) {
                                        BaseComict.setAddBookSelf(true);
                                    } else {
                                        ContentValues values = new ContentValues();
                                        values.put("uid", uid);
                                        values.put("total_chapters", BaseComict.getTotal_chapters());
                                        values.put("name", BaseComict.getName());
                                        values.put("vertical_cover", BaseComict.getVertical_cover());
                                        if (BaseComict.getId() == 0) {
                                            try {
                                                LitePal.updateAll(BaseComic.class, values, "comic_id = ?", BaseComict.getComic_id());
                                            } catch (Exception E) {
                                                BaseComict.saveIsexist(true);
                                            }
                                        } else {
                                            LitePal.update(BaseComic.class, values, BaseComict.getId());
                                        }
                                    }
                                }
                                handler.sendEmptyMessage(0);
                            } catch (Exception e) {
                                MyToash.Log("QQhandleResultfff", e.getMessage());
                            }
                        }
                    }).start();
                }
            }
        } catch (JSONException E) {
        }
    }

    private void setBanner(JSONObject obj) throws JSONException {
        JSONArray recomment = obj.getJSONArray("recommend_list");
        List<BaseComic> mBannerItemListMale = new ArrayList<>();
        for (int i = 0; i < recomment.length(); i++) {
            final JSONObject labelObj = recomment.getJSONObject(i);
            BaseComic BaseComic = new BaseComic();
            BaseComic.setComic_id(labelObj.getString("comic_id"));
            BaseComic.setName(labelObj.getString("name"));
            BaseComic.setVertical_cover(labelObj.getString("vertical_cover"));
            BaseComic.setDescription(labelObj.getString("description"));
            mBannerItemListMale.add(BaseComic);
        }
        if (!mBannerItemListMale.isEmpty()) {
            fragment_discovery_banner_male.setPages(new CBViewHolderCreator<ComicShelfBannerHolderView>() {
                @Override
                public ComicShelfBannerHolderView createHolder() {
                    return new ComicShelfBannerHolderView(activity);
                }
            }, mBannerItemListMale).setPageIndicator(new int[]{R.mipmap.ic_shelf_yes, R.mipmap.ic_shelf_no})
                    .setOnItemClickListener(new OnItemClickListener() {

                        @Override
                        public void onItemClick(int position) {
                            Intent intent = new Intent(getActivity(), ComicInfoActivity.class);
                            intent.putExtra("comic_id", mBannerItemListMale.get(position).getComic_id());
                            startActivity(intent);
                        }
                    });
            fragment_discovery_banner_male.startTurning(2000);
        } else {
            fragment_shelf_banner_layout.setVisibility(View.GONE);
        }
        JSONArray announceArr = obj.getJSONArray("announcement");
        if (announceArr.length() > 0) {
            final List<Announce> announceList = new ArrayList<>();
            for (int i = 0; i < announceArr.length(); i++) {
                announceList.add(gson.fromJson(announceArr.getJSONObject(i).toString(), Announce.class));
            }
            fragment_bookshelf_marquee.setTextArraysAndClickListener(announceList, new MarqueeTextViewClickListener() {
                @Override
                public void onClick(View view, int position) {
                    Intent intent = new Intent(getActivity(), AnnounceActivity.class);
                    intent.putExtra("announce_content", announceList.get(position).getTitle() + "/-/" + announceList.get(position).getContent());
                    startActivity(intent);
                }
            });
            showGuangbo = true;
        } else {
            showGuangbo = false;
            fragment_bookshelf_marquee_layout.setVisibility(View.GONE);
        }
    }

    BaseComic openBaseComic;
    int openPosition;
    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                setDataBaseData(true);
            } else {
                exchangePosition(openBaseComic, openPosition);
                setBookSelfBooks();
                fragment_comicshelf_scrollview.scrollTo(0, 0);
            }
        }
    };

    public void init() {
        MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF, "ShelfComic", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                handleResult(result);
            }
        });
    }

    public void refarsh() {
        MainHttpTask.getInstance().httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF, "ShelfComic", new MainHttpTask.GetHttpData() {
            @Override
            public void getHttpData(String result) {
                try {
                    JSONObject obj = new JSONObject(result);
                    setBanner(obj);
                } catch (Exception e) {
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        fragment_bookshelf_marquee.releaseResources();
        super.onDestroy();
    }

    private void setLongClickListener(int position) {
        fragment_bookshelf_head.setVisibility(View.GONE);
        MainActivityNavigationView.setVisibility(View.INVISIBLE);
        shelf_book_delete_btn.setVisibility(View.VISIBLE);
        fragment_novel_allchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllchooseAndCancleOnclick(true);
            }
        });
        fragment_novel_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AllchooseAndCancleOnclick(false);
            }
        });
        adapter.setDeletable(true, mDeleteBtn, position, new BookshelfFragment.DeleteBook() {
            @Override
            public void success() {
                fragment_bookshelf_head.setVisibility(View.VISIBLE);
                MainActivityNavigationView.setVisibility(View.VISIBLE);
                shelf_book_delete_btn.setVisibility(View.GONE);
                if (showGuangbo) {
                    fragment_bookshelf_marquee_layout.setVisibility(View.VISIBLE);
                }
                if (adapter != null && adapter.isDeletable()) {
                    adapter.setDeletable(false);
                }
                if (bookLists.isEmpty()) {
                    setDataBaseData(false);
                } else {
                    setBookSelfBooks();
                }
            }

            @Override
            public void fail() {
            }
        });
    }

    public interface AddBookToShelf {
        void addSuccess();

        void addFail();
    }

    public void addBookToShelf(String Book_id, final Activity activity, final AddBookToShelf addBookToShelf) {
        ReaderParams params = new ReaderParams(activity);
        params.putExtraParams("comic_id", Book_id);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF_ADD, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        String UID = Utils.getUID(activity);
                        if (ShareUitls.getFirstAddShelf(activity, UID, true)) {
                            EventBus.getDefault().post(new RefreshMine(null));
                            ShareUitls.putFirstAddShelf(activity, UID, false);
                        }
                        addBookToShelf.addSuccess();
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        addBookToShelf.addFail();
                    }
                }
        );
    }

    public void setBookLists(List<BaseComic> bookLists) {
        if (bookLists != null) {
            this.bookLists = bookLists;
        }
    }

    public void setShelf_book_delete_btn(LinearLayout shelf_book_delete_btn) {
        this.shelf_book_delete_btn = shelf_book_delete_btn;
        this.fragment_novel_allchoose = shelf_book_delete_btn.findViewById(R.id.fragment_novel_allchoose);
        this.fragment_novel_cancle = shelf_book_delete_btn.findViewById(R.id.fragment_novel_cancle);
        mDeleteBtn = shelf_book_delete_btn.findViewById(R.id.shelf_book_delete_del);
    }
}
