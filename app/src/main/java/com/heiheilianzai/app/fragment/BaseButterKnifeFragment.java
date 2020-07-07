package com.heiheilianzai.app.fragment;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.utils.MyToash;
import com.umeng.analytics.MobclickAgent;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.utils.StatusBarUtil;

import java.lang.reflect.Field;

import butterknife.ButterKnife;

/**
 * 懒加载 因为项目重构原因 为不影响以前的代码 没有使用 abstract方法
 * 想用懒加载子类重写 {@link #initView()}{@link #initData()}
 *
 */
public abstract class BaseButterKnifeFragment extends Fragment {
    public FragmentActivity activity;
    //Fragment的View加载完毕的标记
    private boolean isViewCreated;
    //Fragment对用户可见的标记
    private boolean isUIVisible;

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
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isUIVisible = true;
            lazyLoad();
        } else {
            isUIVisible = false;
        }
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        isViewCreated = true;
        lazyLoad();
    }

    private void lazyLoad() {
        if (isViewCreated && isUIVisible) {
            initData();
            //数据加载完毕,恢复标记,防止重复加载
            isViewCreated = false;
            isUIVisible = false;
        }
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
        activity = null;
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
        for (View view : views) {
            view.setVisibility(BuildConfig.free_charge ? View.GONE : View.VISIBLE);
        }
    }

    protected void initView() {
    }

    protected void initData() {
    }
}
