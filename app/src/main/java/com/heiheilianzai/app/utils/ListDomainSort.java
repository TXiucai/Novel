package com.heiheilianzai.app.utils;

import com.lzy.okgo.utils.IOUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ListDomainSort {
    //域名测速
    public static final int DOMAIN_TYPE = 0x1009;
    //ip ping
    public static final int PING_TYPE = 0x1010;

    public int threadNum = 0;//执行任务的子线程数量
    public int workNum = 0;//任务数量
    private ExecutorService service; //线程池
    private ArrayBlockingQueue<String> blockingQueue; //结果列表
    private CountDownLatch latch; //任务锁
    private int acceptCount; //返回数量
    private boolean ignoreError = true; //忽略失败
    private int TYPE = DOMAIN_TYPE; //测速类型
    private static ListDomainSort listHostSort;


    private List<String> oldList = new ArrayList<>();//原数据

    private CallHostsDomain callHosts; //接口回调

    private ListDomainSort() {
    }

    public static ListDomainSort getSortUtil() {
        if (listHostSort == null) {
            listHostSort = new ListDomainSort();
        }
        return listHostSort;
    }

    /**
     * 执行测速
     *
     * @param oldList     需要测速的列表
     * @param acceptCount 按速度排序之后需要的前几位数据
     * @param ignoreError 是否忽略失败的 默认是ture
     * @param callHosts   回调接口
     */
    public void startSort(List<String> oldList, int acceptCount, boolean ignoreError, int type, CallHostsDomain callHosts) {
        this.callHosts = callHosts;
        this.TYPE = type;
        this.acceptCount = acceptCount;
        this.oldList = oldList;
        this.ignoreError = ignoreError;
        //1.創建和列表数量相同的线程
        setUp();
        //2.抽取做工单元
        for (int i = 0; i < workNum; i++) {
            service.execute(new WorkRunnable(oldList.get(i)));
        }
        //3.等待所有工作线程执行完成
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    latch.await();//等待子线程的所有任务完成
                    //停止所有线程
                    service.shutdownNow();
                    callHosts.callHostList(copyIterator(blockingQueue.iterator()));
                } catch (Exception e) {
                    callHosts.callHostList(copyIterator(blockingQueue.iterator()));
                }
            }
        }).start();
    }

    //单个线程逻辑
    class WorkRunnable implements Runnable {
        private String host;

        public WorkRunnable(String ip) {
            host = ip;
        }

        @Override
        public void run() {
            //4.对每个线程执行回来的结果进行依次加入列表
            try {
                if (ignoreError) {
                    countExeuct(host);
                    blockingQueue.put(host);
                    //完成一个任务就调用一次
                    latch.countDown();
                } else {
                    if (countExeuct(host)) {
                        blockingQueue.put(host);
                        //完成一个任务就调用一次
                        latch.countDown();
                    }
                    if (workNum == acceptCount) {
                        //完成一个任务就调用一次
                        latch.countDown();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 执行测速单元
     *
     * @param host 需要测速的域名或IP
     * @return
     */
    private boolean countExeuct(String host) {

        if (TYPE == DOMAIN_TYPE) {
            return testDomain(host);
        } else if (TYPE == PING_TYPE) {
            return testPing(host);
        } else {
            return false;
        }
    }

    private boolean testPing(String host) {
        Runtime runtime = Runtime.getRuntime();
        try {
            if (host.equals("0")) {
                return false;
            }
            //ping 1次 超时3秒 数据包512 TTL 255
            String prog = "/system/bin/ping -w 1 -c 1 -i 1 -s 128 -t 255 " + host;
            Process mIpAddrProcess = runtime.exec(prog);
            int mExitValue = mIpAddrProcess.waitFor();
            return mExitValue == 1;
        } catch (Exception ignore) {
        }
        return false;
    }

    private boolean testDomain(String host) {
        String result = httpGet(host);
        if (result != null) {
            try {
                JSONObject object = new JSONObject(result);
                int code = object.optInt("code");
                return code == 123;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    private void setUp() {
        this.workNum = oldList.size();
        this.threadNum = oldList.size();
        //创建线程池
        service = Executors.newFixedThreadPool(threadNum);
        //计数器的值为任务的数量
        latch = new CountDownLatch(acceptCount);
        blockingQueue = new ArrayBlockingQueue<>(acceptCount);
    }

    public interface CallHostsDomain {
        void callHostList(List<String> newList);
    }

    private <T> List<T> copyIterator(Iterator<T> iter) {
        List<T> copy = new ArrayList<T>();
        while (iter.hasNext())
            copy.add(iter.next());
        return copy;
    }

    /**
     * get的方式请求
     */
    private String httpGet(String host) {
        //get的方式提交就是url拼接的方式
        try {
            URL url = new URL(host + "/ping.php");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.setRequestMethod("GET");
            //获得结果码
            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                //请求成功 获得返回的流
                InputStream is = connection.getInputStream();
                return IOUtils.toString(is);
            } else {
                //请求失败
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
