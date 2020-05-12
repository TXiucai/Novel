package com.heiheilianzai.app.push;

import android.content.Context;

import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.Utils;

import java.util.Set;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

public class JPushUtil {

    public static void init(Context context) {
        JPushInterface.setDebugMode(true);
        JPushInterface.init(context);
    }

    public static void setAlias(Context context) {
        if (Utils.isLogin(context)) {
            String alias = Utils.getUID(context) + "PUSH";
            JPushInterface.setAlias(context, alias, new TagAliasCallback() {
                @Override
                public void gotResult(int i, String s, Set<String> set) {
                    MyToash.Log("设置别名 i=" + i + ",s=" + s);
                }
            });
        }
    }
}
