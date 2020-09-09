package com.heiheilianzai.app.base;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.heiheilianzai.app.BuildConfig;
import com.umeng.analytics.MobclickAgent;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * 因为项目重构原因 为不影响以前的代码 没有使用 abstract方法
 */
public abstract class BaseButterKnifeFragment extends Fragment {
    public FragmentActivity activity;

    public abstract int initContentView();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = getActivity();
        View view = inflater.inflate(initContentView(), container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }


    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(getClass().getSimpleName());
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(getClass().getSimpleName());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 根据免费参数判断哪些View隐藏
     */
    protected void uiFreeCharge(View... views) {
        uiFreeCharge(BuildConfig.free_charge , views);
    }

    protected void uiFreeCharge(boolean isGone ,View... views) {
        for (View view : views) {
            view.setVisibility(isGone ? View.GONE : View.VISIBLE);
        }
    }

    protected void initView() {
    }

    protected void initData() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            activity = (FragmentActivity) context;
        } catch (Exception e) {
        }

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                this.activity = (FragmentActivity) activity;
            }
        } catch (Exception e) {
        }
    }
}
