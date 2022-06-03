package com.live.eggplant.player.cache;


import com.live.eggplant.player.utils.Debuger;
import com.live.eggplant.player.videocache.headers.HeaderInjector;

import java.util.HashMap;
import java.util.Map;

/**
 * for android video cache header
 */
public class ProxyCacheUserAgentHeadersInjector implements HeaderInjector {

    public final static Map<String, String> mMapHeadData = new HashMap<>();

    @Override
    public Map<String, String> addHeaders(String url) {
        Debuger.printfLog("****** proxy addHeaders ****** " + mMapHeadData.size());
        return mMapHeadData;
    }
}