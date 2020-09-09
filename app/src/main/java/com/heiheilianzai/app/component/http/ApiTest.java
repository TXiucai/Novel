package com.heiheilianzai.app.component.http;


import java.io.IOException;

public class ApiTest {

    public static boolean synTestConnected(String url) {
        if (null == url || !url.startsWith("http") || !url.startsWith("https")) {
            return false;
        }
        try {
            return OkGo.getResponse(url).isSuccessful();
        } catch (IOException e) {
        }

        return false;
    }
}
