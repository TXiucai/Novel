package com.heiheilianzai.app.view;

import android.content.Context;
import android.util.AttributeSet;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MyContentLinearLayoutManager extends LinearLayoutManager {

    public MyContentLinearLayoutManager(Context context) {
        super(context);
    }

    public MyContentLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public MyContentLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public void onLayoutChildren(RecyclerView.Recycler recycler, RecyclerView.State state) {
        try {
            super.onLayoutChildren(recycler, state);
        } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
            indexOutOfBoundsException.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}