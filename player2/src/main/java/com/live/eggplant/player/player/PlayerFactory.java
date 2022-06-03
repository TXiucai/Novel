package com.live.eggplant.player.player;

/**
 * 播放内核工厂
 * Created by guoshuyu on 2018/5/21.
 */
public class PlayerFactory {

    private static Class<? extends IPlayerManager> sPlayerManager;

    public static void setPlayManager(Class<? extends IPlayerManager> playManager) {
        sPlayerManager = playManager;
    }

    public static IPlayerManager getPlayManager() {
        if (sPlayerManager == null) {
           // sPlayerManager = Exo2PlayerManager.class;
           // sPlayerManager = SystemPlayerManager.class;
            sPlayerManager = IjkPlayerManager.class;
        }
        try {
            return sPlayerManager.newInstance();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

}
