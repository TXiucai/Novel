package com.heiheilianzai.app.localPush;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.component.http.OkHttpEngine;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.http.ResultCallback;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.constant.sa.SaVarConfig;
import com.heiheilianzai.app.ui.activity.AcquireBaoyueActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.MainActivity;
import com.heiheilianzai.app.ui.activity.SplashActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;

import static com.heiheilianzai.app.constant.ReaderConfig.LOOKMORE;
import static com.heiheilianzai.app.ui.activity.BookInfoActivity.BOOK_ID_EXT_KAY;
import static com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity.COMIC_ID_EXT_KAY;

import okhttp3.Request;

public class AlarmReceive extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intentReceive) {
        LoaclPushBean obj = (LoaclPushBean) intentReceive.getSerializableExtra("localPush");
        int push_target = Integer.valueOf(obj.getPush_target());//推送目标  0默认（默认功能为唤醒APP并进入）   1链接   2页面
        Intent intent = null;
        switch (push_target) {
            case 1: //链接
                int redirect_type = Integer.valueOf(obj.getRedirect_type());//默认1    跳转类型  1内置浏览器 2外部浏览器
                int user_parame_need = Integer.parseInt(obj.getUser_parame_need());//是否需要用户参数（拼接uid） 默认为1 不需要  2为 需要
                String jump_url = obj.getJump_url();
                if (Utils.isLogin(context) && user_parame_need == 2 && !jump_url.contains("&uid=")) {
                    jump_url += "&uid=" + Utils.getUID(context);
                }
                switch (redirect_type) {
                    case 1:
                        intent = new Intent(context, AboutActivity.class).putExtra("url", jump_url);
                        break;
                    case 2:
                        intent = new Intent(context, AboutActivity.class).putExtra("url", jump_url).putExtra("style", "4");
                        break;
                }
                break;
            case 2: //页面
                int jump_page_type = Integer.valueOf(obj.getJump_page_type());//跳转页面类型  1书架  2小说首页 3漫画首页  4我的页面  5会员充值页
                switch (jump_page_type) {
                    case 1://书架
                        ShareUitls.putTab(context, "LastFragment", 0);
                        intent = new Intent(context, MainActivity.class);
                        break;
                    case 2://小说
                        ShareUitls.putTab(context, "LastFragment", 1);
                        intent = new Intent(context, MainActivity.class);
                        break;
                    case 3://漫画
                        ShareUitls.putTab(context, "LastFragment", 2);
                        intent = new Intent(context, MainActivity.class);
                        break;
                    case 4://我的
                        ShareUitls.putTab(context, "LastFragment", 4);
                        intent = new Intent(context, MainActivity.class);
                        break;
                    case 5://充值
                        intent = new Intent(context, AcquireBaoyueActivity.class);
                        break;
                }
                break;
            case 3:
                int jump_content_type = Integer.valueOf(obj.getJump_content_type());//跳转内容类型   1小说  2漫画  3小说栏目   4漫画栏目  5小说频道  6漫画频道
                String jump_content_id = obj.getJump_content_id();
                switch (jump_content_type) {//内容
                    case 1:// 1小说
                        intent = new Intent(context, BookInfoActivity.class);
                        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, LanguageUtil.getString(context, R.string.refer_page_nove_push));
                        intent.putExtra(BOOK_ID_EXT_KAY, jump_content_id);
                        break;
                    case 2://2漫画
                        intent = new Intent(context, ComicInfoActivity.class);
                        intent.putExtra(SaVarConfig.REFER_PAGE_VAR, LanguageUtil.getString(context, R.string.refer_page_nove_push));
                        intent.putExtra(COMIC_ID_EXT_KAY, jump_content_id);
                        break;
                    case 3://3小说栏目
                        intent = new Intent(context, BaseOptionActivity.class)
                                .putExtra("OPTION", LOOKMORE)
                                .putExtra("IS_TOP_YEAR", false)
                                .putExtra("PRODUCT", true)
                                .putExtra("title", LanguageUtil.getString(context, R.string.refer_page_more) + " " + LanguageUtil.getString(context, R.string.refer_page_column_id) + jump_content_id)
                                .putExtra("recommend_id", jump_content_id);
                        break;
                    case 4://4漫画栏目
                        intent = new Intent(context, BaseOptionActivity.class)
                                .putExtra("OPTION", LOOKMORE)
                                .putExtra("PRODUCT", false)
                                .putExtra("IS_TOP_YEAR", false)
                                .putExtra("title", LanguageUtil.getString(context, R.string.refer_page_more) + " " + LanguageUtil.getString(context, R.string.refer_page_column_id) + jump_content_id)
                                .putExtra("recommend_id", jump_content_id);
                        break;
                    case 5://5小说频道
                        ShareUitls.putTab(context, "LastFragment", 1);
                        ShareUitls.putString(context, "NOVEL_CHANNEL_ID", jump_content_id);
                        break;
                    case 6://6漫画频道
                        ShareUitls.putTab(context, "LastFragment", 2);
                        ShareUitls.putString(context, "COMIC_CHANNEL_ID", jump_content_id);
                        intent = new Intent(context, MainActivity.class);
                        break;
                }
                break;
            default:
                intent = new Intent(context, SplashActivity.class);
                break;
        }
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            upDataClick(context, obj);
        }
    }

    private void upDataClick(Context context, LoaclPushBean obj) {
        ReaderParams params = new ReaderParams(context);
        params.putExtraParams("local_push_id", String.valueOf(obj.getId()));
        String json = params.generateParamsJson();
        OkHttpEngine.getInstance(context).postAsyncHttp(ReaderConfig.getBaseUrl() + ReaderConfig.LOCAL_PUSH_ClICK, json, new ResultCallback() {
            @Override
            public void onError(Request request, Exception e) {

            }

            @Override
            public void onResponse(String response) {

            }
        });
    }
}
