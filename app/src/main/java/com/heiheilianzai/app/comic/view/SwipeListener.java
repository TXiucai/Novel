package com.heiheilianzai.app.comic.view;

import com.heiheilianzai.app.view.ZoomListView;

import java.util.EventListener;

public interface SwipeListener extends EventListener {
    public void OnAction(ZoomListView.Action action);
}
