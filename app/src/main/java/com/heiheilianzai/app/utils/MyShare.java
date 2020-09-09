package com.heiheilianzai.app.utils;

import android.app.Activity;

import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by scb on 2018/10/16.
 */
public class MyShare {

    public static Activity content;
    public static String Flag;

    public static void Share(Activity activity, String flag, UMWeb umWeb) {
        content = activity;
        Flag = flag;
        if (ReaderConfig.USE_QQ) {
            new ShareAction(activity).withText("hello")
                    .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QQ, SHARE_MEDIA.QZONE)
                    .withMedia(umWeb)
                    .setCallback(umShareListener).open();
        } else {
            new ShareAction(activity).withText("hello")
                    .setDisplayList(SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE)
                    .withMedia(umWeb)
                    .setCallback(umShareListener).open();
        }
    }

    private static UMShareListener umShareListener = new UMShareListener() {
        /**
         * @descrption 分享开始的回调
         * @param platform 平台类型
         */
        @Override
        public void onStart(SHARE_MEDIA platform) {
        }

        /**
         * @descrption 分享成功的回调
         * @param platform 平台类型
         */
        @Override
        public void onResult(SHARE_MEDIA platform) {
            if (ReaderConfig.USE_PAY) {
                getGold(content);
            }
        }

        /**
         * @descrption 分享失败的回调
         * @param platform 平台类型
         * @param t 错误原因
         */
        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
            MyToash.ToashError(content, "分享失败");
        }

        /**
         * @descrption 分享取消的回调
         * @param platform 平台类型
         */
        @Override
        public void onCancel(SHARE_MEDIA platform) {
            MyToash.ToashError(content, "分享取消");
        }
    };

    public static void getGold(Activity activity) {
        final ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.ShareAddGold, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String tip = jsonObject.getString("tip");
                            EventBus.getDefault().post(new RefreshMine(null));
                            MyToash.ToashSuccess(content, tip);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    public static void ShareAPP(Activity activity) {
        final ReaderParams params = new ReaderParams(activity);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.APP_SHARE, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String url = jsonObject.getString("link") + "?uid=" + Utils.getUID(activity) + "&osType=2&product=1";
                            UMWeb web = new UMWeb(url);
                            web.setTitle(jsonObject.getString("title"));//标题
                            web.setThumb(new UMImage(activity, jsonObject.getString("imgUrl")));  //缩略图
                            web.setDescription(jsonObject.getString("desc"));//描述*/
                            Share(activity, "", web);
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
}
