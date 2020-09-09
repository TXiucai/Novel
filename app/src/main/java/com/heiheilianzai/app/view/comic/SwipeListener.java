package com.heiheilianzai.app.view.comic;

import com.heiheilianzai.app.view.ZoomListView;

import java.util.EventListener;

public interface SwipeListener extends EventListener {
    public void OnAction(ZoomListView.Action action);
}
