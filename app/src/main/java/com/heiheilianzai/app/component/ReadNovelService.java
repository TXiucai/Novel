package com.heiheilianzai.app.component;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.constant.PrefConst;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.TRPage;
import com.heiheilianzai.app.utils.manager.ReadSpeakManager;

import org.litepal.LitePal;

import java.util.List;

import static android.app.NotificationManager.IMPORTANCE_DEFAULT;

public class ReadNovelService extends Service {
    private final String STATUS_PLAY_PAUSE_ACTION = "service_play_pause";
    private final String STATUS_CLOSE_SERVICE_ACTION = "service_close";
    private final String EXTRA_BOOK = "book";
    private final String EXTRA_CHAPTER = "chapter";
    private final String EXTRA_PAGE = "page";
    private RemoteViews mRemoteView;
    private String mTittle;
    private String mChapterTittle;
    private String mImgUrl;
    private boolean mIsPlay;
    private Notification mNotification;
    private int mNotifyID = 100;
    public static boolean SERVICE_IS_LIVE;
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Intent intentNext = new Intent();
                    intentNext.setAction(ReadActivity.TURN_NEXT);
                    sendBroadcast(intentNext);
                    break;
                case 2:
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction(ReadActivity.UPDATE_BG);
                    intentUpdate.putExtra("line", mReadLine);
                    sendBroadcast(intentUpdate);
                    break;
            }
        }
    };
    private ChapterItem mChapterItem;
    private BaseBook mBaseBook;
    private ChapterManager mChapterManager;
    private String mPageContent;//当前章节当前页的内容
    private ReadSpeakManager mReadSpeakManager;
    private TRPage mCurrentPage;
    private long mBegin;//该章具体的某一页
    private int mReadPage = 0;//当前章节读的那一页
    private int mReadLine = 0;//当前页的那一行
    private List<TRPage> mTrPages;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mReadSpeakManager = new ReadSpeakManager(getApplicationContext());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STATUS_PLAY_PAUSE_ACTION);
        registerReceiver(mNotificationReceiver, intentFilter);
        setNotification();
        startForeground(mNotifyID, mNotification);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SERVICE_IS_LIVE = true;
        mChapterItem = (ChapterItem) intent.getExtras().get(EXTRA_CHAPTER);
        mBegin = mChapterItem.getBegin();
        mBaseBook = (BaseBook) intent.getExtras().get(EXTRA_BOOK);
        mTittle = mChapterItem.getBook_name();
        mChapterTittle = mChapterItem.getChapter_title();
        mImgUrl = mBaseBook.getCover();
        mPageContent = (String) intent.getExtras().get(EXTRA_PAGE);
        mChapterManager = new ChapterManager();
        mChapterManager.openBook(mBaseBook, mBaseBook.getBook_id(), mChapterItem.getChapter_id());
        getChapterContent(mBaseBook.getBook_id(), mChapterItem.getChapter_id(), new GetChapterContent() {
            @Override
            public void onSuccessChapterContent(List<TRPage> pages) {
                mTrPages = pages;
                readBook();
            }

            @Override
            public void onFailChapterContent() {
                MyToash.ToashError(getApplication(), getResources().getString(R.string.string_read_book_error));
            }
        });
        mReadSpeakManager.setReadSpeakStateCallback(new ReadSpeakManager.ReadSpeakStateCallback() {
            @Override
            public void readSpeakState(int state) {
                switch (state) {
                    case 1: // 停止播放
                        break;
                    case 2://暂停播放
                        break;
                    case 3://播放中
                        mHandler.sendEmptyMessage(2);
                        break;
                    case 4://读完了
                        mHandler.sendEmptyMessage(1);
                        mReadLine++;
                        readBook();
                        break;
                }
            }
        });
        upDateNotifacation();
        return super.onStartCommand(intent, flags, startId);
    }

    private void readBook() {
        if (mTrPages != null && mTrPages.size() > 0) {
            if (mTrPages.size() > mReadPage) {
                mCurrentPage = mTrPages.get(mReadPage);
                if (mCurrentPage.getLines() != null && mCurrentPage.getLines().size() > mReadLine) {
                    mReadSpeakManager.playReadBook(mCurrentPage.getLines().get(mReadLine));
                } else {
                    mReadPage++;
                    mReadLine = 0;
                    readBook();
                }
            } else {
                getChapterContent(mBaseBook.getBook_id(), mChapterItem.getNext_chapter_id(), new GetChapterContent() {
                    @Override
                    public void onSuccessChapterContent(List<TRPage> content) {
                        mTrPages = content;
                        readBook();
                    }

                    @Override
                    public void onFailChapterContent() {
                        MyToash.ToashError(getApplication(), getResources().getString(R.string.string_read_book_error));
                    }
                });
            }
        }
    }

    private void getChapterContent(String book_id, String chapter_id, GetChapterContent getChapterContent) {
        mChapterManager.getChapter(chapter_id, new ChapterManager.QuerychapterItemInterface() {
            @Override
            public void success(ChapterItem querychapterItem) {
                mChapterItem = querychapterItem;
                String path = FileManager.getSDCardRoot().concat("Reader/book/").concat(book_id + "/").concat(chapter_id + "/").concat(querychapterItem.getIs_preview() + "/").concat(querychapterItem.getIs_new_content() + "/").concat(querychapterItem.getUpdate_time()).concat(".txt");
                if (querychapterItem.getChapter_path() == null) {
                    if (FileManager.isExist(path)) {
                        ContentValues values = new ContentValues();
                        values.put("chapter_path", path);
                        LitePal.updateAll(ChapterItem.class, values, "book_id = ? and chapter_id = ?", book_id, chapter_id);
                        querychapterItem.setChapter_path(path);
                        try {
                            ChapterPageManager chapterPageManager = new ChapterPageManager(getApplicationContext(), querychapterItem);
                            List<TRPage> pages = chapterPageManager.getPages(mBegin);
                            getChapterContent.onSuccessChapterContent(pages);
                        } catch (Exception e) {
                        }
                    } else {
                        ChapterManager.notfindChapter(ShareUitls.getString(getApplication(), PrefConst.NOVEL_API, "") + ReaderConfig.chapter_text, querychapterItem, book_id, chapter_id, new ChapterManager.ChapterDownload() {
                            @Override
                            public void finish() {
                                try {
                                    ChapterPageManager chapterPageManager = new ChapterPageManager(getApplicationContext(), querychapterItem);
                                    List<TRPage> pages = chapterPageManager.getPages(mBegin);
                                    getChapterContent.onSuccessChapterContent(pages);
                                } catch (Exception e) {

                                }
                            }
                        });
                    }
                } else {
                    try {
                        ChapterPageManager chapterPageManager = new ChapterPageManager(getApplicationContext(), querychapterItem);
                        List<TRPage> pages = chapterPageManager.getPages(mBegin);
                        getChapterContent.onSuccessChapterContent(pages);
                    } catch (Exception e) {
                    }
                }

            }

            @Override
            public void fail() {
                getChapterContent.onFailChapterContent();
            }
        });
    }

    public interface GetChapterContent {
        void onSuccessChapterContent(List<TRPage> content);

        void onFailChapterContent();
    }

    @Override
    public void onDestroy() {
        SERVICE_IS_LIVE = false;
        unregisterReceiver(mNotificationReceiver);
        stopForeground(true);
        super.onDestroy();
    }

    private BroadcastReceiver mNotificationReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case STATUS_PLAY_PAUSE_ACTION:
                    if (mIsPlay) {
                        mReadSpeakManager.stopReadBook();
                    } else {
                        readBook();
                    }
                    mIsPlay = !mIsPlay;
                    upDateNotifacation();
                    break;
                case STATUS_CLOSE_SERVICE_ACTION:
                    Intent intentService = new Intent(context, ReadNovelService.class);
                    stopService(intentService);
                    break;
            }

        }
    };

    //        在通知栏显示，并监听播放/停止按钮
    private void setNotification() {
        try {
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            android 8 以后才有NotificationChannel，所以进行版本判断
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel("heihei", getResources().getString(R.string.string_name), IMPORTANCE_DEFAULT);
                manager.createNotificationChannel(channel);
            }

//           2、自定义通知栏布局，按钮点击事件
            mRemoteView = new RemoteViews(getPackageName(), R.layout.novel_notifacation);
            Intent intentPause = new Intent(STATUS_PLAY_PAUSE_ACTION);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notification_play, broadcast);

            Intent intentClose = new Intent(STATUS_PLAY_PAUSE_ACTION);
            PendingIntent closeService = PendingIntent.getBroadcast(this, 3, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notification_close, closeService);

            upDateNotifacation();
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(STATUS_PLAY_PAUSE_ACTION);
            intentFilter.addAction(STATUS_CLOSE_SERVICE_ACTION);
            registerReceiver(mNotificationReceiver, intentFilter);

//            3、创建通知栏点击时的跳转意图
            Intent intent = new Intent(this, ReadActivity.class);
            mChapterItem.setBegin(mCurrentPage.getBegin());
            intent.putExtra(EXTRA_BOOK, mBaseBook);
            intent.putExtra(EXTRA_CHAPTER, mChapterItem);
            PendingIntent pendingActivity = PendingIntent.getActivity(this, 0, intent, 0);
//             用Builder构造器创建Notification

            mNotification = new NotificationCompat.Builder(this, "heihei")
                    .setContent(mRemoteView)   // 自定义的布局视图
                    .setContentTitle(mTittle)
                    .setSmallIcon(R.mipmap.launcher_icon) // 要用alpha图标
                    .setContentIntent(pendingActivity) // 点击通知栏跳转到播放页面
                    .build();
            mNotification.flags = Notification.FLAG_NO_CLEAR; // 让通知不被清除
            manager.notify(mNotifyID, mNotification);
        } catch (Exception e) {
        }
    }

    private void upDateNotifacation() {
        mRemoteView.setTextViewText(R.id.notification_chapter, mTittle);
        mRemoteView.setTextViewText(R.id.notification_tittle, mTittle);
        Glide.with(this).asBitmap().load(mImgUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mRemoteView.setImageViewBitmap(R.id.notification_logo, resource);
            }
        });
        if (mIsPlay) {
            mRemoteView.setImageViewResource(R.id.notification_play, R.mipmap.ic_play);
        } else {
            mRemoteView.setImageViewResource(R.id.notification_play, R.mipmap.ic_stop);
        }
    }
}
