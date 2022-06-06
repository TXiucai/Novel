package com.heiheilianzai.app.view.video;

import android.content.Context;
import android.util.AttributeSet;

import com.live.eggplant.player.video.StandardGSYVideoPlayer;

public class CustomVideoPlayer extends StandardGSYVideoPlayer {
    public CustomVideoPlayer(Context context, Boolean fullFlag) {
        super(context, fullFlag);
    }

    public CustomVideoPlayer(Context context) {
        super(context);
    }

    public CustomVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

}
