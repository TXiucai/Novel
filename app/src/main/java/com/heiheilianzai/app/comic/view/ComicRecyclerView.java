package com.heiheilianzai.app.comic.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.comic.been.ComicLookImageHeigth;
import com.heiheilianzai.app.utils.MyToash;

import java.util.List;

import static android.view.MotionEvent.ACTION_DOWN;

public class ComicRecyclerView extends RecyclerView {

    private OnTouchListener touchListener;



    public void setTouchListener(OnTouchListener touchListener) {
        this.touchListener = touchListener;
    }

    public ComicRecyclerView(Context context) {
        super(context);
        init(null);
    }

    public ComicRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public ComicRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attr) {

    }



    @Override
    public boolean onTouchEvent(@NonNull final MotionEvent ev) {
        switch (ev.getAction()) {
            case ACTION_DOWN: {
                if (touchListener != null) {
                    touchListener.clickScreen(0, ev.getY(), ev.getRawY());
                }
                break;
            }
        }
        return super.onTouchEvent(ev);
    }








    public interface OnTouchListener {
        void clickScreen(float x, float y, float RawY);
    }
    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {

       // scrollViewListener.onScroll(l, t, oldl, oldt);
      //  MyToash.Log("onScrollChanged  --",getScrollY());
        super.onScrollChanged(l, t, oldl, oldt);
    }

    @Override
    public void onNestedScroll(View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        MyToash.Log("onScrollChanged  11",dxConsumed+"   "+dyConsumed+"  "+dxUnconsumed+"  "+dyUnconsumed);
        super.onNestedScroll(target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed);
    }


    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }

    OnScrollListener onScrollListener=new OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            MyToash.Log("onScrollChanged  00",newState);

        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            MyToash.Log("onScrollChanged  11", dy+"  "+ImageSize+"   "+(comicLookImageHeigths.get(ImageSize-1)).toString());

        }
    };
    @Override
    public void addOnScrollListener(@NonNull OnScrollListener listener) {
        super.addOnScrollListener(onScrollListener);
    }

    public interface ScrollViewListener {
        void onScroll(int l, int t, int oldl, int oldt);
    }

    private ScrollViewListener scrollViewListener;

    public void setScrollViewListener(ScrollViewListener scrollViewListener) {
        this.scrollViewListener = scrollViewListener;
    }

    private int ImageSize;
    List<ComicLookImageHeigth> comicLookImageHeigths;
    public void setComicLookImageHeigths(List<ComicLookImageHeigth> comicLookImageHeigths, int ImageSize) {
        this.comicLookImageHeigths = comicLookImageHeigths;
        this.ImageSize = ImageSize;
    }
}
