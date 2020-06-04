package com.heiheilianzai.app.domain;

import android.content.Context;
import android.content.SharedPreferences;

import com.heiheilianzai.app.config.RabbitConfig;
import com.heiheilianzai.app.utils.MyToash;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PreparedDomain {

    private SharedPreferences sharedPreferences;

    public PreparedDomain(Context context) {
        sharedPreferences = context.getSharedPreferences("domains", Context.MODE_PRIVATE);
    }

    public void putString(String key, String value) {
        sharedPreferences.edit().putString(key, value).commit();
    }

    public String getString(String key, String def) {
        return sharedPreferences.getString(key, def);
    }

    public void putBoolean(String key, boolean value) {
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean def) {
        return sharedPreferences.getBoolean(key, def);
    }

    public void putLong(String key, long value) {
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public long getLong(String key, long def) {
        return sharedPreferences.getLong(key, def);
    }

    public void putInt(String key, int value) {
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public int getInt(String key, int def) {
        return sharedPreferences.getInt(key, def);
    }

    public void setDefaultDomain(String domain) {
        sharedPreferences.edit().putString("defdomain", domain).commit();
    }

    public String getDefaultDomain() {
        String def = sharedPreferences.getString("defdomain", RabbitConfig.BASE_URL);
        return def;
    }

    public List<String> getDomain() {

        List domains = getStringList("domains");
        if (domains.isEmpty()) {
            return getDefDomains();
        } else {
        }
        return domains;
    }


    public void saveDomain(List<String> domains) {
        putStringList("domains", domains);
    }

    private List<String> getDefDomains() {
        List<String> set = new ArrayList<>();
        set.add("https://www.boshanguan.cn");
        set.add("https://www.tcsygnlw.com");
        return set;
    }

    private void putStringList(String key, List<String> values) {
        StringBuilder builder = new StringBuilder();
        for (int index = 0; index < values.size(); index++) {
            builder.append(values.get(index));
            if (index != values.size() - 1) {
                builder.append(",");
            }
        }
        String value = builder.toString();
        sharedPreferences.edit().putString(key, value).commit();
    }

    private List<String> getStringList(String key) {
        String value = sharedPreferences.getString(key, "");
        if ("".equals(value)) {
            return new ArrayList<>(0);
        }
        String[] array = value.split(",");
        List<String> values = Arrays.asList(array);
        if (values.isEmpty()) {
            MyToash.Log("getStringList is empty");
        } else {
            for (String v : values) {
                MyToash.Log("getStringList " + v);
            }
        }
        return values;
    }
}
