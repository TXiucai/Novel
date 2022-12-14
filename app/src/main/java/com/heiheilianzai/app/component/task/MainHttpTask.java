package com.heiheilianzai.app.component.task;

import android.app.Activity;
import android.content.Intent;

import com.google.gson.Gson;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.BookConfig;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.utils.AppPrefs;
import com.heiheilianzai.app.utils.DialogRegister;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;

//主界面接口数据缓存
public class MainHttpTask {

    private MainHttpTask() {
    }

    private static final MainHttpTask mainHttpTask = new MainHttpTask();

    public static MainHttpTask getInstance() {
        return mainHttpTask;
    }

    private String ShelfBook;
    private String ShelfComic;

    private String StoreBookMan;
    private String StoreBookWoMan;

    private String StoreComicMan;
    private String StoreComicWoMan;

    private String DiscoverBook;
    private String DiscoverComic;

    public interface GetHttpData {
        void getHttpData(String result);
    }

    /**
     * 根据不同参数 对涉及到小说和漫画的 请求 url body参数封装
     * 现在服务器只返回男频小说漫画，不排除以后要加的可能 未删除女频相关代码
     *
     * @param activity
     * @param url
     * @param Option      小说漫画 男频 女频
     * @param getHttpData
     */
    public void httpSend(Activity activity, String url, String Option, GetHttpData getHttpData) {
        ReaderParams params = new ReaderParams(activity);//StoreBookWoMan
        if (Option.equals("StoreBookMan") || Option.equals("StoreComicMan")) {//小说漫画男频
            params.putExtraParams("channel_id", "1");
        } else if (Option.equals("StoreBookWoMan") || Option.equals("StoreComicWoMan")) {//小说漫画女频
            params.putExtraParams("channel_id", "2");
        }
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(url, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            switch (Option) {
                                case "ShelfBook":
                                    ShelfBook = result;
                                    break;
                                case "ShelfComic":
                                    ShelfComic = result;
                                    MyToash.Log("QQhandleResult", result);
                                    break;
                                case "StoreBookMan":
                                    StoreBookMan = result;
                                    break;
                                case "StoreBookWoMan":
                                    StoreBookWoMan = result;
                                    break;
                                case "StoreComicMan":
                                    StoreComicMan = result;
                                    break;
                                case "StoreComicWoMan":
                                    StoreComicWoMan = result;
                                    break;
                                case "DiscoverBook":
                                    DiscoverBook = result;
                                    break;
                                case "DiscoverComic":
                                    DiscoverComic = result;
                                    break;
                                case "Mine":
                                    ReaderConfig.REFREASH_USERCENTER = false;
                                    UserInfoItem mUserInfo = new Gson().fromJson(result, UserInfoItem.class);
                                    AppPrefs.putSharedString(activity, ReaderConfig.TOKEN, mUserInfo.getUser_token());
                                    break;
                            }
                            ShareUitls.putMainHttpTaskString(activity, Option, result);
                            if (getHttpData != null) {
                                getHttpData.getHttpData(result);
                            }
                        } catch (Exception e) {
                        } catch (Error e) {
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        try {
                            if (getHttpData != null) {
                                getHttpData.getHttpData(null);
                            }
                        } catch (Exception e) {
                        } catch (Error e) {
                        }
                    }
                }
        );
    }

    public void getResultString(Activity activity, String Option, GetHttpData getHttpData) {
        try {
            switch (Option) {
                case "ShelfBook":
                    if (ShelfBook == null) {
                        ShelfBook = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (ShelfBook != null) {
                        getHttpData.getHttpData(ShelfBook);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + BookConfig.mBookCollectUrl, Option, getHttpData);
                    }
                    break;
                case "ShelfComic":
                    if (ShelfComic == null) {
                        ShelfComic = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (ShelfComic != null) {
                        getHttpData.getHttpData(ShelfComic);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF, Option, getHttpData);
                    }
                    break;
                case "StoreBookMan":
                    if (StoreBookMan == null) {
                        StoreBookMan = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (StoreBookMan != null) {
                        getHttpData.getHttpData(StoreBookMan);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + BookConfig.mBookStoreUrl, Option, getHttpData);
                    }
                    break;
                case "StoreBookWoMan":
                    if (StoreBookWoMan == null) {
                        StoreBookWoMan = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (StoreBookWoMan != null) {
                        getHttpData.getHttpData(StoreBookWoMan);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + BookConfig.mBookStoreUrl, Option, getHttpData);
                    }
                    break;
                case "StoreComicMan":
                    if (StoreComicMan == null) {
                        StoreComicMan = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (StoreComicMan != null) {
                        getHttpData.getHttpData(StoreComicMan);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_home_stock, Option, getHttpData);
                    }
                    break;
                case "StoreComicWoMan":
                    if (StoreComicWoMan == null) {
                        StoreComicWoMan = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (StoreComicWoMan != null) {
                        getHttpData.getHttpData(StoreComicWoMan);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_home_stock, Option, getHttpData);
                    }
                    break;
                case "DiscoverBook":
                    if (DiscoverBook == null) {
                        DiscoverBook = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (DiscoverBook != null) {
                        getHttpData.getHttpData(DiscoverBook);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + BookConfig.mDiscoveryUrl, Option, getHttpData);
                    }
                    break;
                case "DiscoverComic":
                    if (DiscoverComic == null) {
                        DiscoverComic = ShareUitls.getMainHttpTaskString(activity, Option, null);
                    }
                    if (DiscoverComic != null) {
                        getHttpData.getHttpData(DiscoverComic);
                    } else {
                        httpSend(activity, ReaderConfig.getBaseUrl() + ComicConfig.COMIC_featured, Option, getHttpData);
                    }
                    break;
                case "Mine":
                    httpSend(activity, ReaderConfig.getBaseUrl() + ReaderConfig.mUserCenterUrl, Option, getHttpData);
                    break;
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    public boolean Gotologin(Activity activity) {
        if (!Utils.isLogin(activity)) {
            DialogRegister dialogRegister = new DialogRegister();
            dialogRegister.getDialogLoginPop(activity);
            return false;
        }
        return true;
    }

    public boolean GoLogin(Activity activity) {
        if (!Utils.isLogin(activity)) {
            Intent intent = new Intent();
            intent.setClass(activity, LoginActivity.class);
            activity.startActivity(intent);
            return false;
        }
        return true;
    }
}
