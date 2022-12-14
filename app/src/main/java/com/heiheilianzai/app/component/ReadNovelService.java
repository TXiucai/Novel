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
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
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
import com.heiheilianzai.app.model.event.NovelOpenOtherEvent;
import com.heiheilianzai.app.model.event.SetTimerEvent;
import com.heiheilianzai.app.model.event.StartOtherNovel;
import com.heiheilianzai.app.ui.activity.read.ReadActivity;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.FileUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.TRPage;
import com.heiheilianzai.app.utils.manager.ReadSpeakManager;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.List;

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
    public int delayMins = -1;
    private TimerCount mTtimerCount;
    private Handler mHandler = new Handler() {
        @SuppressLint("HandlerLeak")
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    Intent intentNext = new Intent();
                    intentNext.setAction(ReadActivity.TURN_NEXT);
                    intentNext.putExtra("book_id", mBaseBook.getBook_id());
                    sendBroadcast(intentNext);
                    break;
                case 2:
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction(ReadActivity.UPDATE_BG);
                    intentUpdate.putExtra("line", mReadLine);
                    sendBroadcast(intentUpdate);
                    break;
                case 3:
                    Intent intentClose = new Intent();
                    intentClose.setAction(ReadActivity.CLOSE_SERVICE);
                    sendBroadcast(intentClose);
                    break;
            }
        }
    };
    private ChapterItem mChapterItem;
    private BaseBook mBaseBook;
    private ChapterManager mChapterManager;
    private String mPageContent;//??????????????????????????????
    private ReadSpeakManager mReadSpeakManager;
    private TRPage mCurrentPage;
    private long mBegin;//????????????????????????
    private int mReadPage = 0;//???????????????????????????
    private int mReadLine = 0;//?????????????????????
    private List<TRPage> mTrPages;
    private ReadReceiver mNotificationReceiver;
    private NotificationManager mNotificationManager;
    private int mResultStatus;
    private AudioManager mAudioManager;
    private OnAudioFocusChangeListener mFocusChangeListener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mReadSpeakManager = ReadSpeakManager.getInstance().initReadSetting();
        mNotificationReceiver = new ReadReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(STATUS_PLAY_PAUSE_ACTION);
        intentFilter.addAction(STATUS_CLOSE_SERVICE_ACTION);
        registerReceiver(mNotificationReceiver, intentFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        SERVICE_IS_LIVE = true;
        if (intent != null && intent.getExtras() != null) {
            mChapterItem = (ChapterItem) intent.getExtras().get(EXTRA_CHAPTER);
            mBegin = mChapterItem.getBegin();
            mBaseBook = (BaseBook) intent.getExtras().get(EXTRA_BOOK);
            mTittle = mBaseBook.getName();
            mChapterTittle = mChapterItem.getChapter_title();
            mImgUrl = mBaseBook.getCover();
            mPageContent = (String) intent.getExtras().get(EXTRA_PAGE);
            mChapterManager = new ChapterManager();
            mChapterManager.getChapterList(mBaseBook.getBook_id());
            mReadSpeakManager.setReadSpeakStateCallback(new ReadSpeakManager.ReadSpeakStateCallback() {
                @Override
                public void readSpeakState(int state) {
                    switch (state) {
                        case 1: // ????????????
                            mHandler.sendEmptyMessage(3);
                            closeService(getApplicationContext());
                            break;
                        case 2://????????????
                            break;
                        case 3://?????????
                            break;
                        case 4://?????????
                            mReadPage++;
                            readBook();
                            mHandler.sendEmptyMessage(1);
                            break;
                    }
                }
            });
            mAudioManager = (AudioManager) getApplication().getSystemService(Context.AUDIO_SERVICE);
            mFocusChangeListener = new OnAudioFocusChangeListener() {
                public void onAudioFocusChange(int focusChange) {
                    if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                        mReadSpeakManager.stopReadBook(1);
                        closeService(getApplicationContext());
                    } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                        mReadSpeakManager.stopReadBook(1);
                        closeService(getApplicationContext());
                    }
                }
            };
            mResultStatus = mAudioManager.requestAudioFocus(mFocusChangeListener,
                    // Use the music stream.
                    AudioManager.STREAM_MUSIC,
                    // Request permanent focus.
                    AudioManager.AUDIOFOCUS_GAIN);

            if (mResultStatus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                getChapterContent(mBaseBook.getBook_id(), mChapterItem.getChapter_id(), new GetChapterContent() {
                    @Override
                    public void onSuccessChapterContent(List<TRPage> pages) {
                        if (pages != null && pages.size() > 0) {
                            mTrPages = pages;
                            mCurrentPage = mTrPages.get(mReadPage);
                            mIsPlay = true;
                            readBook();
                        }
                    }

                    @Override
                    public void onFailChapterContent() {
                        MyToash.ToashError(getApplication(), getResources().getString(R.string.string_read_book_error));
                    }
                });
            }
        }
        return START_STICKY;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setTimer(SetTimerEvent timer) {
        final long unitML = 60 * 1000L;
        int mins = timer.getTime();
        ReadNovelService.this.delayMins = mins;
        if (mTtimerCount != null) {
            mTtimerCount.cancel();
            mTtimerCount = null;
        }
        if (mins > 0) {
            long millisInFuture = mins * unitML;
            mTtimerCount = new TimerCount(millisInFuture, 1000);
            mTtimerCount.start();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void setOtherNovelRead(NovelOpenOtherEvent otherNovelRead) {
        BaseBook baseBook = otherNovelRead.getmBaseBook();
        ChapterItem chapterItem = otherNovelRead.getmChapterItem();
        if (!TextUtils.equals(mBaseBook.getBook_id(), baseBook.getBook_id()) || !TextUtils.equals(mChapterItem.getChapter_id(), chapterItem.getChapter_id())) {
            StartOtherNovel startOtherNovel = new StartOtherNovel();
            startOtherNovel.setStartNovel(true);
            EventBus.getDefault().post(startOtherNovel);
        }
    }

    private void readBook() {
        if (mTrPages.size() > mReadPage) {
            mCurrentPage = mTrPages.get(mReadPage);
            mChapterItem.setBegin(mCurrentPage.getBegin());
            String currentPageText = mCurrentPage.getLineToString();
            String bookText = FileUtils.getFilter2TTS(currentPageText);
            mReadSpeakManager.playReadBook(bookText);
            setNotification();
        } else {
            if (TextUtils.equals(mChapterItem.getNext_chapter_id(), "-2")) {//??????????????????????????????
                mReadSpeakManager.stopReadBook(1);
                closeService(getApplicationContext());
            } else {
                reset();
                getChapterContent(mBaseBook.getBook_id(), mChapterItem.getNext_chapter_id(), new GetChapterContent() {
                    @Override
                    public void onSuccessChapterContent(List<TRPage> content) {
                        mTrPages.addAll(content);
                        readBook();
                    }

                    @Override
                    public void onFailChapterContent() {
                        mReadSpeakManager.stopReadBook(1);
                        MyToash.ToashError(getApplication(), getResources().getString(R.string.string_read_book_error));
                    }
                });
            }

        }
    }

    private void reset() {
        mTrPages.clear();
        mReadPage = 0;
        mReadLine = 0;
        mBegin = 0;
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
        if (mReadSpeakManager != null) {
            mReadSpeakManager.stopReadBook(1);
        }
        if (mAudioManager != null && mFocusChangeListener != null) {
            mAudioManager.abandonAudioFocus(mFocusChangeListener);
        }
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mNotificationReceiver);
        stopForeground(true);
        super.onDestroy();
    }

    private class ReadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case STATUS_PLAY_PAUSE_ACTION:
                    if (mIsPlay) {
                        mReadSpeakManager.stopReadBook(2);
                    } else {
                        mReadSpeakManager.readPauseBook();
                    }
                    mIsPlay = !mIsPlay;
                    setNotification();
                    break;
                case STATUS_CLOSE_SERVICE_ACTION:
                    mReadSpeakManager.stopReadBook(1);
                    mHandler.sendEmptyMessage(3);
                    closeService(context);
                    break;
            }

        }
    }

    //        ????????????????????????????????????/????????????
    private void setNotification() {
        try {
            mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            android 8 ????????????NotificationChannel???????????????????????????
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                @SuppressLint("WrongConstant") NotificationChannel channel = new NotificationChannel("heihei", getResources().getString(R.string.string_name), NotificationManager.IMPORTANCE_HIGH);
                mNotificationManager.createNotificationChannel(channel);
            }
//           2????????????????????????????????????????????????
            mRemoteView = new RemoteViews(getPackageName(), R.layout.novel_notifacation);
            Intent intentPause = new Intent(STATUS_PLAY_PAUSE_ACTION);
            PendingIntent broadcast = PendingIntent.getBroadcast(this, 2, intentPause, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notification_play, broadcast);

            Intent intentClose = new Intent(STATUS_CLOSE_SERVICE_ACTION);
            PendingIntent closeService = PendingIntent.getBroadcast(this, 3, intentClose, PendingIntent.FLAG_UPDATE_CURRENT);
            mRemoteView.setOnClickPendingIntent(R.id.notification_close, closeService);

            upDateNotifacation();
//            3??????????????????????????????????????????
            Intent intent = new Intent(this, ReadActivity.class);
            mChapterItem.setBegin(mCurrentPage.getBegin());
            intent.putExtra(EXTRA_BOOK, mBaseBook);
            intent.putExtra(EXTRA_CHAPTER, mChapterItem);
            PendingIntent pendingActivity = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//             ???Builder???????????????Notification

            mNotification = new NotificationCompat.Builder(this, "heihei")
                    .setContentTitle(mTittle)
                    .setSmallIcon(R.mipmap.launcher_icon) // ??????alpha??????
                    .setContentIntent(pendingActivity) // ????????????????????????????????????
                    .setContent(mRemoteView)   // ????????????????????????
                    .setSound(null)
                    .build();
            mNotification.flags = Notification.FLAG_NO_CLEAR; // ???????????????
            startForeground(mNotifyID, mNotification);
        } catch (Exception e) {
        }
    }

    private void upDateNotifacation() {
        mRemoteView.setTextViewText(R.id.notification_chapter, mChapterItem.getChapter_title());
        mRemoteView.setTextViewText(R.id.notification_tittle, mTittle);
        Glide.with(this).asBitmap().load(mImgUrl).into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                mRemoteView.setImageViewBitmap(R.id.notification_logo, resource);
            }
        });
        if (!mIsPlay) {
            mRemoteView.setImageViewResource(R.id.notification_play, R.mipmap.ic_stop);
        } else {
            mRemoteView.setImageViewResource(R.id.notification_play, R.mipmap.ic_play);
        }
    }

    public void closeService(Context context) {
        Intent intentService = new Intent(context, ReadNovelService.class);
        stopService(intentService);
        mAudioManager.abandonAudioFocus(mFocusChangeListener);
        if (mNotificationManager != null) {
            mNotificationManager.cancel(mNotifyID);
        }
    }

    class TimerCount extends CountDownTimer {

        public TimerCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long l) {

        }

        @Override
        public void onFinish() {
            // ????????????
            mReadSpeakManager.stopReadBook(1);
            closeService(getApplicationContext());
        }
    }

}
