package com.heiheilianzai.app.component.task;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.text.TextUtils;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.model.event.BoyinDownloadEvent;
import com.heiheilianzai.app.model.event.comic.BoyinInfoEvent;
import com.heiheilianzai.app.utils.MyToash;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadLargeFileListener;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloadQueueSet;
import com.liulishuo.filedownloader.FileDownloader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import static androidx.core.app.NotificationCompat.PRIORITY_MIN;
import static com.heiheilianzai.app.model.event.BoyinDownloadEvent.EventTag.COMPLETE_DOWNLOAD;
import static com.heiheilianzai.app.model.event.BoyinDownloadEvent.EventTag.ERROR;
import static com.heiheilianzai.app.model.event.BoyinDownloadEvent.EventTag.TASK_STATUS;

/**
 * 有声小说下载 Service
 */
public class DownloadBoyinService extends Service implements NetStateChangeReceiver.NetStateChangeObserver {

    private static final int ID_NOTIFICATION = 101;
    private List<BoyinChapterBean> mAllDownloadList = new CopyOnWriteArrayList<>();
    private FileDownloadListener mDownloadListener;
    private FileDownloadQueueSet mQueueSet;
    private int mComplteChapter;
    private Notification notification;
    private NotificationManager manager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        NetStateChangeReceiver.registerObserver(this);
        // 兼容Android O 版本处理
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId = "1";
            String channelServiceName = getString(R.string.app_name) + "下载服务";
            NotificationChannel channel =
                    new NotificationChannel(channelId,
                            channelServiceName,
                            NotificationManager.IMPORTANCE_LOW);

            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (manager == null) {
                return;
            }
            manager.createNotificationChannel(channel);
            notification = getNotification(channelId, "运行中");
            startForeground(ID_NOTIFICATION, notification);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        NetStateChangeReceiver.unregisterObserver(this);
        // 进程被杀死时，把正在下载任务状态改成暂停
        pauseAllDownloadTask();
        mAllDownloadList.clear();
        mComplteChapter = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            manager.cancel(ID_NOTIFICATION);
            stopForeground(true);
            MyToash.Log("FileDownloader", "下载服务退出8.0：");
        }
        MyToash.Log("FileDownloader", "下载服务退出：");
        super.onDestroy();
    }

    private FileDownloadLargeFileListener createListener() {
        return new FileDownloadLargeFileListener() {

            @Override
            protected void pending(BaseDownloadTask task,
                                   long soFarBytes,
                                   long totalBytes) {
                // 之所以加这句判断，是因为有些异步任务在pause以后，
                // 会持续回调pause回来，而有些任务在pause之前已经完成，
                // 但是通知消息还在线程池中还未回调回来，这里可以优化
                // 后面所有在回调中加这句都是这个原因
                if (task.getListener() != mDownloadListener) {
                    return;
                }
            }

            @Override
            protected void progress(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                MyToash.Log("FileDownloader", "下载中");
            }

            @Override
            protected void completed(BaseDownloadTask task) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                BoyinChapterBean downloadTaskModel =
                        getDownloadTaskByUrl(String.valueOf(task.getTag()));
                if (downloadTaskModel == null) {
                    return;
                }
                MyToash.Log("FileDownloader", "下载完成");
                downloadCompleted(downloadTaskModel);
            }

            @Override
            protected void paused(BaseDownloadTask task, long soFarBytes, long totalBytes) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                BoyinChapterBean downloadTaskModel =
                        getDownloadTaskByUrl(String.valueOf(task.getTag()));
                if (downloadTaskModel == null) {
                    return;
                }
            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                if (task.getListener() != mDownloadListener) {
                    return;
                }
                BoyinChapterBean downloadTaskModel =
                        getDownloadTaskByUrl(String.valueOf(task.getTag()));
                if (downloadTaskModel == null) {
                    return;
                }
                MyToash.Log("FileDownloader", "下载失败");
                downloadTaskModel.setDownloadStatus(BoyinChapterBean.STATUS_DOWNLOAD_ERROR);
                ContentValues contentValues = new ContentValues();
                contentValues.put("downloadstatus", BoyinChapterBean.STATUS_DOWNLOAD_ERROR);
                LitePal.updateAll(BoyinChapterBean.class, contentValues, "chapter_id = ?", String.valueOf(downloadTaskModel.getChapter_id()));
                EventBus.getDefault().post(new BoyinDownloadEvent(ERROR, Arrays.asList(downloadTaskModel)));
                startNextDownloadTask();
            }

            @Override
            protected void warn(BaseDownloadTask task) {
                MyToash.Log("FileDownloader", "下载警告");
            }
        };
    }

    private void downloadCompleted(BoyinChapterBean downloadTask) {
        // 下载完成
        mComplteChapter++;
        downloadTask.setDownloadStatus(BoyinChapterBean.STATUS_COMPLETE);
        // 更新数据库数据状态
        ContentValues contentValues = new ContentValues();
        contentValues.put("savepath", downloadTask.getSavePath());
        contentValues.put("downloadstatus", BoyinChapterBean.STATUS_COMPLETE);
        LitePal.updateAll(BoyinChapterBean.class, contentValues, "chapter_id = ?", String.valueOf(downloadTask.getChapter_id()));
        MyToash.Log("FileDownloader", "完成 更新章节数据库");
        List<BoyinChapterBean> boyinChapterBeans = LitePal.where("nid = ? and downloadstatus = ?", String.valueOf(downloadTask.getNid()), "1").find(BoyinChapterBean.class);
        if (boyinChapterBeans != null && boyinChapterBeans.size() > 0) {
            contentValues.clear();
            contentValues.put("down_chapter", boyinChapterBeans.size());
            LitePal.updateAll(BoyinInfoBean.class, contentValues, "nid = ?", String.valueOf(downloadTask.getNid()));
        }
        BoyinDownloadEvent boyinDownloadEvent = new BoyinDownloadEvent(COMPLETE_DOWNLOAD, Arrays.asList(downloadTask));
        boyinDownloadEvent.setDownComplete(mComplteChapter);
        EventBus.getDefault().post(boyinDownloadEvent);
        // 当前任务处理完成，判断是否有等待下载任务需要下载
        startNextDownloadTask();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventReceived(BoyinDownloadEvent downloadEvent) {
        if (downloadEvent.getTag() == TASK_STATUS) {
            return;
        }
        List<BoyinChapterBean> downloadEntityList = downloadEvent.getDownloadTaskList();
        // 其他状态不能没数据
        if (downloadEntityList == null || downloadEntityList.size() == 0) {
            return;
        }
        switch (downloadEvent.getTag()) {
            case START_DOWNLOAD:
                for (BoyinChapterBean downloadTaskModel : downloadEntityList) {
                    addDownloadTask(downloadTaskModel);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 添加任务
     *
     * @param downloadEntity
     */
    private void addDownloadTask(BoyinChapterBean downloadEntity) {
        if (downloadEntity.getUrl() == null || !downloadEntity.getUrl().startsWith("http")) {
            return;
        }
        // 校验任务是否已经存在,如已存在不进行下面操作
        if (getDownloadTaskByUrl(downloadEntity.getUrl()) == null) {
            downloadEntity.setDownloadStatus(0);
            mAllDownloadList.add(downloadEntity);
        }
        // 判断当前队列中是否正在下载数据，有则等待，无则开始下载
        if (!LitePal.isExist(BoyinChapterBean.class, "downloadstatus = ?", "2")) {
            startDownloadTask(downloadEntity);
            MyToash.Log("FileDownloader", "开始下载");
        }
    }

    /**
     * 获取下一个任务下载
     *
     * @return
     */
    private BoyinChapterBean getNextTask() {
        BoyinChapterBean nextEntity = null;
        for (int i = 0; i < mAllDownloadList.size(); i++) {
            BoyinChapterBean entity = mAllDownloadList.get(i);
            if (entity.getDownloadStatus() == 0) {
                nextEntity = entity;
                break;
            }
        }
        return nextEntity;
    }

    /**
     * 开始下一个任务
     */
    private void startNextDownloadTask() {
        BoyinChapterBean nextTask = getNextTask();
        if (nextTask == null) {
            MyToash.Log("FileDownloader", "全部下载结束");
            EventBus.getDefault().post(new BoyinInfoEvent());
            notificationContent("运行中");
            return;
        }
        startDownloadTask(nextTask);
    }

    /**
     * 开始任务下载
     *
     * @param downloadEntity
     */
    private void startDownloadTask(BoyinChapterBean downloadEntity) {
        notificationContent("下载中");
        downloadEntity.setDownloadStatus(BoyinChapterBean.STATUS_DOWNLOADING);
        int position = mAllDownloadList.indexOf(downloadEntity);//oldId对应的index
        if (position >= 0 && position < mAllDownloadList.size()) {
            mAllDownloadList.set(position, downloadEntity);
        }
        List<BoyinChapterBean> list = new ArrayList<BoyinChapterBean>();
        list.add(downloadEntity);
        EventBus.getDefault().post(new BoyinDownloadEvent(TASK_STATUS, list));
        // 更新数据库数据状态
        ContentValues contentValues = new ContentValues();
        contentValues.put("downloadstatus", BoyinChapterBean.STATUS_DOWNLOADING);
        LitePal.updateAll(BoyinChapterBean.class, contentValues, "chapter_id = ?", String.valueOf(downloadEntity.getChapter_id()));
        start(downloadEntity);
    }


    private void start(BoyinChapterBean downloadEntity) {
        if (mDownloadListener == null) {
            mDownloadListener = createListener();
        }
        if (mQueueSet == null) {
            mQueueSet = new FileDownloadQueueSet(mDownloadListener);
        }
        BaseDownloadTask task = FileDownloader.getImpl().create(downloadEntity.getUrl())
                .setTag(downloadEntity.getUrl())
                .setPath(downloadEntity.getSavePath());
        downloadEntity.setDownloadId(task.getId());
        MyToash.Log("FileDownloader", "isServiceConnected: " + FileDownloader.getImpl().isServiceConnected());
        // 避免掉帧
        FileDownloader.enableAvoidDropFrame();
        // auto retry 3 time if download fail
        mQueueSet.setAutoRetryTimes(1);
        mQueueSet.downloadSequentially(task);
        mQueueSet.start();
    }

    /**
     * 通过Url校验任务是否已经存在
     *
     * @param url
     * @return
     */
    private BoyinChapterBean getDownloadTaskByUrl(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (mAllDownloadList == null || mAllDownloadList.size() == 0) {
            return null;
        }
        for (BoyinChapterBean downloadTaskModel : mAllDownloadList) {
            if (url.equals(downloadTaskModel.getUrl())) {
                return downloadTaskModel;
            }
        }
        return null;
    }

    @Override
    public void onNetDisconnected() {
        pauseAllDownloadTask();
    }

    @Override
    public void onNetConnected() {
        startNextDownloadTask();
    }

    /**
     * 进程被杀死前保存状态到数据库
     */
    private void pauseAllDownloadTask() {
        FileDownloader.getImpl().pause(mDownloadListener);
        for (int i = 0; i < mAllDownloadList.size(); i++) {
            if (mAllDownloadList.get(i).getDownloadStatus() == BoyinChapterBean.STATUS_DOWNLOADING) {
                mAllDownloadList.get(i).setDownloadStatus(BoyinChapterBean.STATUS_DOWNLOAD_ERROR);
                ContentValues contentValues = new ContentValues();
                contentValues.put("downloadstatus", BoyinChapterBean.STATUS_DOWNLOAD_ERROR);
                LitePal.updateAll(BoyinChapterBean.class, contentValues, "nid = ?", String.valueOf(mAllDownloadList.get(i).getNid()));
            }
        }
        notificationContent("运行中");
    }

    /**
     * 获取通知管理器
     *
     * @return
     */
    private NotificationManager getNotificationManager() {
        return (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notificationContent(String content) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = getNotification("1", content);
            getNotificationManager().notify(101, notification);
        }
    }

    private Notification getNotification(String channelId, String title) {
        return new NotificationCompat.Builder(this, channelId)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setSmallIcon(R.mipmap.appicon)
                .setPriority(PRIORITY_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setOngoing(true)
                .setPriority(NotificationManager.IMPORTANCE_LOW)
                .build();
    }

    /**
     * 判断服务是否开启
     *
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        if (TextUtils.isEmpty(serviceName)) {
            return false;
        }
        ActivityManager manager = (ActivityManager) context
                .getSystemService(Context.ACTIVITY_SERVICE);
        ArrayList<ActivityManager.RunningServiceInfo> runningService =
                (ArrayList<ActivityManager.RunningServiceInfo>) manager
                        .getRunningServices(30);
        for (ActivityManager.RunningServiceInfo info : runningService) {
            if (info.service.getClassName().endsWith(serviceName)) {
                return true;
            }
        }
        return false;
    }
}