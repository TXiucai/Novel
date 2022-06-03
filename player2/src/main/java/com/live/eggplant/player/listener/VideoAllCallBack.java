package com.live.eggplant.player.listener;

/**
 * Created by Nathen，参考jiecao结构，在其基础上修改
 * On 2016/04/04 22:13
 */
public interface VideoAllCallBack {

    /**
     * 开始加载，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onStartPrepared(String url, Object... objects);

    /**
     * 加载成功，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onPrepared(String url, Object... objects);

    /**
     * 点击了开始按键播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickStartIcon(String url, Object... objects);

    /**
     * 点击了错误状态下的开始按键，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickStartError(String url, Object... objects);

    /**
     * 点击了播放状态下的开始按键--->停止，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickStop(String url, Object... objects);

    /**
     * 点击了全屏播放状态下的开始按键--->停止，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickStopFullscreen(String url, Object... objects);

    /**
     * 点击了暂停状态下的开始按键--->播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickResume(String url, Object... objects);

    /**
     * 点击了全屏暂停状态下的开始按键--->播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickResumeFullscreen(String url, Object... objects);

    /**
     * 点击了空白弹出seekbar，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickSeekbar(String url, Object... objects);

    /**
     * 点击了全屏的seekbar，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickSeekbarFullscreen(String url, Object... objects);

    /**
     * 播放完了，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onAutoComplete(String url, Object... objects);

    /**
     * 进去全屏，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onEnterFullscreen(String url, Object... objects);

    /**
     * 退出全屏，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onQuitFullscreen(String url, Object... objects);

    /**
     * 进入小窗口，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onQuitSmallWidget(String url, Object... objects);

    /**
     * 退出小窗口，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onEnterSmallWidget(String url, Object... objects);

    /**
     * 触摸调整声音，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onTouchScreenSeekVolume(String url, Object... objects);

    /**
     * 触摸调整进度，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onTouchScreenSeekPosition(String url, Object... objects);

    /**
     * 触摸调整亮度，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onTouchScreenSeekLight(String url, Object... objects);

    /**
     * 播放错误，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param what
     * @param url
     * @param objects
     */
    void onPlayError(int what, String url, Object... objects);

    /**
     * 点击了空白区域开始播放，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickStartThumb(String url, Object... objects);

    /**
     * 点击了播放中的空白区域，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickBlank(String url, Object... objects);

    /**
     * 点击了全屏播放中的空白区域，objects[0]是title，object[1]是当前所处播放器（全屏或非全屏）
     *
     * @param url
     * @param objects
     */
    void onClickBlankFullscreen(String url, Object... objects);
}