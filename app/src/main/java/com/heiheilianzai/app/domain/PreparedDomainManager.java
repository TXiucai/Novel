package com.heiheilianzai.app.domain;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.heiheilianzai.app.utils.MyToash;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Response;

//import com.squareup.okhttp.Response;


public class PreparedDomainManager implements Runnable {

    private PreparedDomain preparedDomain;

    public PreparedDomainManager(Context context) {
        preparedDomain = new PreparedDomain(context);
    }

    public void start() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        if (shouldUpdateDomains()) {
            updateDomains();
        }


    }

    private void updateDomains() {
        try {
            String text = OkGo.get(AppConfig.getServerUrl() + AppConfig.SERVER_PREPARED_DOMAIN);
            Bean bean = null;
            try {
                bean = JSON.parseObject(text, Bean.class);
            } catch (JSONException e) {
                MyToash.LogE("PreparedDomainManager parse error", e.getMessage());
                return;
            }

            if (bean.data != null && !bean.data.isEmpty()) {
                List<String> set = new ArrayList<>();
                for (Bean.DataBean b : bean.data) {
                    set.add(b.name);
                }
                preparedDomain.saveDomain(set);

            }
        } catch (IOException e) {
            MyToash.LogE("PreparedDomainManager run error", e.getMessage());
        }
    }

    private boolean shouldUpdateDomains() {
        List<String> set = preparedDomain.getDomain();
        for (String domain : set) {
            boolean success = ApiTest.synTestConnected(domain);

            try {
                Response response = OkGo.post(AppConfig.getServerUrl() + AppConfig.SERVER_PREPARED_RECORD, domain, success ? "success" : "fail");
            } catch (IOException ee) {
                MyToash.LogE(domain + " --> error", ee.getMessage());
            }

            if (success) {
                return false;
            }

        }

        return true;
    }
}
