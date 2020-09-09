package com.heiheilianzai.app.component.task;

import android.os.Looper;

import com.heiheilianzai.app.utils.Utils;

/**
 * 任务管理
 */
public class DownloadTask {

    private QueueTask mQueueTask = null;

    public DownloadTask() {
        mQueueTask = new QueueTask();
        new MainThread().start();
    }

    /**
     * 添加一个修改属性库/样式库的任务。
     *
     * @param task 一个修改属性库/样式库的任务。
     */
    public void addQueueTask(InstructTask<?, ?> task) {
        mQueueTask.put(task);
    }


    public void onDestroy() {
        mQueueTask.quit();
    }

    class MainThread extends Thread {

        @Override
        public void run() {
            if (Looper.myLooper() == null) {
                Looper.prepare();
            }
            try {
                for (; ; ) {
                    if (mQueueTask.mQuiting) {
                        mQueueTask.clear();
                        mQueueTask = null;
                        return;
                    }
                    InstructTask<?, ?> task = mQueueTask.next();
                    if (task == null) {
                        synchronized (mQueueTask) {
                            try {
                                mQueueTask.wait();
                            } catch (InterruptedException e) {
                                Utils.printException(e);
                            }
                        }
                        if (task == null) {
                            continue;
                        }
                    }
                    task.run();
                }
            } catch (Exception e) {
                Utils.printException(e);
            } finally {
                if (mQueueTask != null && !mQueueTask.mQuiting) {
                    new MainThread().start();
                }
            }
        }
    }
}
