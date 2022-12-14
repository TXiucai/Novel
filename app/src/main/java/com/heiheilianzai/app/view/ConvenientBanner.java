package com.heiheilianzai.app.view;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.banner.CBPageAdapter;
import com.heiheilianzai.app.base.BaseOptionActivity;
import com.heiheilianzai.app.callback.CBPageChangeListener;
import com.heiheilianzai.app.callback.OnItemClickListener;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.holder.CBViewHolderCreator;
import com.heiheilianzai.app.holder.DiscoverBannerHolderViewBook;
import com.heiheilianzai.app.holder.DiscoveryBannerHolderViewComic;
import com.heiheilianzai.app.holder.HomeBannerHolderViewComic;
import com.heiheilianzai.app.model.BannerItemStore;
import com.heiheilianzai.app.model.BaseSdkAD;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.FeedBackActivity;
import com.heiheilianzai.app.ui.activity.LoginActivity;
import com.heiheilianzai.app.ui.activity.RechargeActivity;
import com.heiheilianzai.app.ui.activity.TaskCenterActivity;
import com.heiheilianzai.app.ui.activity.cartoon.CartoonInfoActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.ui.activity.setting.AboutActivity;
import com.heiheilianzai.app.ui.activity.setting.SettingsActivity;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.utils.Utils;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;

import org.greenrobot.eventbus.EventBus;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.PageTransformer;

import static com.heiheilianzai.app.constant.ReaderConfig.BAOYUE;

/**
 * banner?????????????????????????????????????????????????????????
 */
public class ConvenientBanner<T> extends LinearLayout {
    private List<T> mDatas;
    private int[] page_indicatorId;
    private ArrayList<ImageView> mPointViews = new ArrayList<ImageView>();
    private CBPageChangeListener pageChangeListener;
    private ViewPager.OnPageChangeListener onPageChangeListener;
    private CBPageAdapter pageAdapter;
    private CBLoopViewPager viewPager;
    private ViewPagerScroller scroller;
    private ViewGroup loPageTurningPoint;
    private long autoTurningTime;
    private boolean turning;
    private boolean canTurn = false;
    private boolean manualPageable = true;
    private boolean canLoop = true;

    public enum PageIndicatorAlign {
        ALIGN_PARENT_LEFT, ALIGN_PARENT_RIGHT, CENTER_HORIZONTAL
    }

    private AdSwitchTask adSwitchTask;

    public ConvenientBanner(Context context) {
        super(context);
        init(context);
    }

    public ConvenientBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public ConvenientBanner(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ConvenientBanner);
        canLoop = a.getBoolean(R.styleable.ConvenientBanner_canLoop, true);
        a.recycle();
        init(context);
    }

    private void init(Context context) {
        View hView = LayoutInflater.from(context).inflate(R.layout.include_viewpager, this, true);
        viewPager = hView.findViewById(R.id.cbLoopViewPager);
        Field field = null; // ??????ViewPager???????????????????????????????????????????????????
        try {
            field = ViewPager.class.getDeclaredField("mTouchSlop");
            field.setAccessible(true); // ??????Java??????????????????
            field.setInt(viewPager, 1); // ???????????????????????????????????????ViewPager???????????????????????????????????????1px????????????ViewPager???????????????
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        loPageTurningPoint = hView.findViewById(R.id.loPageTurningPoint);
        initViewPagerScroll();
        adSwitchTask = new AdSwitchTask(this);
    }

    static class AdSwitchTask implements Runnable {
        private final WeakReference<ConvenientBanner> reference;

        AdSwitchTask(ConvenientBanner convenientBanner) {
            this.reference = new WeakReference<ConvenientBanner>(convenientBanner);
        }

        @Override
        public void run() {
            ConvenientBanner convenientBanner = reference.get();

            if (convenientBanner != null) {
                if (convenientBanner.viewPager != null && convenientBanner.turning) {
                    int page = convenientBanner.viewPager.getCurrentItem() + 1;
                    convenientBanner.viewPager.setCurrentItem(page);
                    convenientBanner.postDelayed(convenientBanner.adSwitchTask, convenientBanner.autoTurningTime);
                }
            }
        }
    }

    public ConvenientBanner setPages(CBViewHolderCreator holderCreator, List<T> datas) {
        this.mDatas = datas;
        pageAdapter = new CBPageAdapter(holderCreator, mDatas);
        viewPager.setAdapter(pageAdapter, canLoop);
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
        return this;
    }

    /**
     * ?????????????????? ???????????????????????????????????? notifyDataSetAdd()
     */
    public void notifyDataSetChanged() {
        viewPager.getAdapter().notifyDataSetChanged();
        if (page_indicatorId != null)
            setPageIndicator(page_indicatorId);
    }

    /**
     * ?????????????????????????????????
     *
     * @param visible
     */
    public ConvenientBanner setPointViewVisible(boolean visible) {
        loPageTurningPoint.setVisibility(visible ? View.VISIBLE : View.GONE);
        return this;
    }

    /**
     * ???????????????????????????
     *
     * @param page_indicatorId
     */
    public ConvenientBanner setPageIndicator(int[] page_indicatorId) {
        loPageTurningPoint.removeAllViews();
        mPointViews.clear();
        this.page_indicatorId = page_indicatorId;
        if (mDatas == null)
            return this;
        for (int count = 0; count < mDatas.size(); count++) {
            // ??????????????????
            ImageView pointView = new ImageView(getContext());
            pointView.setPadding(5, 0, 5, 0);
            if (mPointViews.isEmpty())
                pointView.setImageResource(page_indicatorId[1]);
            else
                pointView.setImageResource(page_indicatorId[0]);
            mPointViews.add(pointView);
            loPageTurningPoint.addView(pointView);
        }
        pageChangeListener = new CBPageChangeListener(mPointViews, page_indicatorId);
        viewPager.setOnPageChangeListener(pageChangeListener);
        pageChangeListener.onPageSelected(viewPager.getRealItem());
        if (onPageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    /**
     * ??????????????????
     *
     * @param align ????????????????????? ???RelativeLayout.ALIGN_PARENT_LEFT????????????
     *              ???RelativeLayout.CENTER_HORIZONTAL????????????
     *              ???RelativeLayout.ALIGN_PARENT_RIGHT???
     * @return
     */
    public ConvenientBanner setPageIndicatorAlign(PageIndicatorAlign align) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) loPageTurningPoint.getLayoutParams();
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT, align == PageIndicatorAlign.ALIGN_PARENT_LEFT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, align == PageIndicatorAlign.ALIGN_PARENT_RIGHT ? RelativeLayout.TRUE : 0);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, align == PageIndicatorAlign.CENTER_HORIZONTAL ? RelativeLayout.TRUE : 0);
        loPageTurningPoint.setLayoutParams(layoutParams);
        return this;
    }

    /***
     * ?????????????????????
     *
     * @return
     */
    public boolean isTurning() {
        return turning;
    }

    /***
     * ????????????
     *
     * @param autoTurningTime ??????????????????
     * @return
     */
    public ConvenientBanner startTurning(long autoTurningTime) {
        //????????????????????????????????????
        if (turning) {
            stopTurning();
        }
        //?????????????????????????????????
        canTurn = true;
        this.autoTurningTime = autoTurningTime;
        turning = true;
        postDelayed(adSwitchTask, autoTurningTime);
        return this;
    }

    public void stopTurning() {
        turning = false;
        removeCallbacks(adSwitchTask);
    }

    /**
     * ???????????????????????????
     *
     * @param transformer
     * @return
     */
    public ConvenientBanner setPageTransformer(PageTransformer transformer) {
        viewPager.setPageTransformer(true, transformer);
        return this;
    }

    /**
     * ??????ViewPager???????????????
     */
    private void initViewPagerScroll() {
        try {
            Field mScroller = null;
            mScroller = ViewPager.class.getDeclaredField("mScroller");
            mScroller.setAccessible(true);
            scroller = new ViewPagerScroller(viewPager.getContext());
            mScroller.set(viewPager, scroller);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isManualPageable() {
        return viewPager.isCanScroll();
    }

    public void setManualPageable(boolean manualPageable) {
        viewPager.setCanScroll(manualPageable);
    }

    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        ViewParent parent = getParent();
        int action = ev.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_OUTSIDE) {
            PullToRefreshLayout.REFRESH_FLAG = true;
            // ????????????
            if (canTurn)
                startTurning(autoTurningTime);
        } else if (action == MotionEvent.ACTION_DOWN) {
            mLastMotionX = ev.getX();
            mLastMotionY = ev.getY();
            // ????????????
            if (canTurn)
                stopTurning();
        } else if (action == MotionEvent.ACTION_MOVE) {
            final float x = ev.getX();
            final float dx = x - mLastMotionX;
            final float xDiff = Math.abs(dx);
            final float y = ev.getY();
            final float yDiff = Math.abs(y - mLastMotionY);
            if (xDiff > yDiff) {
                PullToRefreshLayout.REFRESH_FLAG = false;
                mLastMotionX = x;
                mLastMotionY = y;
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean mIsBeingDragged;
    private float mLastMotionX;
    private float mLastMotionY;

    //?????????????????????index
    public int getCurrentItem() {
        if (viewPager != null) {
            return viewPager.getRealItem();
        }
        return -1;
    }

    //?????????????????????index
    public void setcurrentitem(int index) {
        if (viewPager != null) {
            viewPager.setCurrentItem(index);
        }
    }

    public ViewPager.OnPageChangeListener getOnPageChangeListener() {
        return onPageChangeListener;
    }

    /**
     * ?????????????????????
     *
     * @param onPageChangeListener
     * @return
     */
    public ConvenientBanner setOnPageChangeListener(ViewPager.OnPageChangeListener onPageChangeListener) {
        this.onPageChangeListener = onPageChangeListener;
        //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        if (pageChangeListener != null)
            pageChangeListener.setOnPageChangeListener(onPageChangeListener);
        else
            viewPager.setOnPageChangeListener(onPageChangeListener);
        return this;
    }

    public boolean isCanLoop() {
        return viewPager.isCanLoop();
    }

    /**
     * ??????item??????
     *
     * @param onItemClickListener
     */
    public ConvenientBanner setOnItemClickListener(OnItemClickListener onItemClickListener) {
        if (onItemClickListener == null) {
            viewPager.setOnItemClickListener(null);
            return this;
        }
        viewPager.setOnItemClickListener(onItemClickListener);
        return this;
    }

    /**
     * ??????ViewPager???????????????
     *
     * @param scrollDuration
     */
    public void setScrollDuration(int scrollDuration) {
        scroller.setScrollDuration(scrollDuration);
    }

    public int getScrollDuration() {
        return scroller.getScrollDuration();
    }

    public CBLoopViewPager getViewPager() {
        return viewPager;
    }

    public void setCanLoop(boolean canLoop) {
        this.canLoop = canLoop;
        viewPager.setCanLoop(canLoop);
    }


    public static void initbanner(Activity activity, Gson gson, String jsonObject, ConvenientBanner<BannerItemStore> mStoreBannerMale, int time, int flag) {
        final List<BannerItemStore> mBannerItemListMale = new ArrayList<>();
        JsonParser jsonParser = new JsonParser();
        JsonArray jsonElements = jsonParser.parse(jsonObject).getAsJsonArray();//??????JsonArray??????
        for (JsonElement jsonElement : jsonElements) {
            BannerItemStore bannerItemStore = gson.fromJson(jsonElement, BannerItemStore.class);//??????
            mBannerItemListMale.add(bannerItemStore);
        }
        if (flag == 0 || flag == 1 || flag == 2 || flag == 3) {
            if (!mBannerItemListMale.isEmpty()) {
                mStoreBannerMale.setVisibility(View.VISIBLE);
                mStoreBannerMale.setPages(new CBViewHolderCreator<HomeBannerHolderViewComic>() {
                    @Override
                    public HomeBannerHolderViewComic createHolder() {
                        return new HomeBannerHolderViewComic(flag);
                    }
                }, mBannerItemListMale).setPageIndicator(new int[]{R.mipmap.banner_indicator, R.mipmap.banner_indicator_focused})
                        .setOnItemClickListener(new OnItemClickListener() {

                            @Override
                            public void onItemClick(int position) {
                                Onclick(mBannerItemListMale.get(position), activity, flag);
                            }
                        });
            } else {
                mStoreBannerMale.setVisibility(View.GONE);
            }
        } else {
            if (!mBannerItemListMale.isEmpty()) {
                mStoreBannerMale.setVisibility(View.VISIBLE);
                final int WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
                if (flag == 4) {
                    LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mStoreBannerMale.getLayoutParams();
                    layoutParams.width = WIDTH;
                    layoutParams.height = layoutParams.width / 4;
                    mStoreBannerMale.setLayoutParams(layoutParams);
                }
                if (flag == 4) {
                    mStoreBannerMale.setPages(new CBViewHolderCreator<DiscoverBannerHolderViewBook>() {
                        @Override
                        public DiscoverBannerHolderViewBook createHolder() {
                            return new DiscoverBannerHolderViewBook(activity);
                        }
                    }, mBannerItemListMale).setPageIndicator(new int[]{R.mipmap.banner_indicator, R.mipmap.banner_indicator_focused})
                            .setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(int position) {
                                    Onclick(mBannerItemListMale.get(position), activity, flag);
                                }
                            });
                } else {
                    mStoreBannerMale.setPages(new CBViewHolderCreator<DiscoveryBannerHolderViewComic>() {
                        @Override
                        public DiscoveryBannerHolderViewComic createHolder() {
                            return new DiscoveryBannerHolderViewComic(activity, WIDTH);
                        }
                    }, mBannerItemListMale).setPageIndicator(new int[]{R.mipmap.banner_indicator, R.mipmap.banner_indicator_focused})
                            .setOnItemClickListener(new OnItemClickListener() {

                                @Override
                                public void onItemClick(int position) {
                                    Onclick(mBannerItemListMale.get(position), activity, flag);
                                }
                            });
                }

            } else {
                mStoreBannerMale.setVisibility(View.GONE);
            }
        }
        mStoreBannerMale.startTurning(time);

    }

    private static void Onclick(BannerItemStore bannerItemStore, Activity activity, int flag) {
        AdInfo adInfo = BaseSdkAD.newAdInfo(bannerItemStore);
        if (adInfo != null) {
            XRequestManager.INSTANCE.requestEventClick(activity, adInfo);
        }
        String content = bannerItemStore.getContent();
        switch (bannerItemStore.getAction()) {
            case 1:
                Intent intent;
                if (flag == 1 || flag == 0) {
                    intent = BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), content);
                } else if (flag == 2) {
                    intent = ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), content);
                } else {
                    intent = CartoonInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_home_ad), content);
                }
                activity.startActivity(intent);
                break;
            case 2:
                Intent scheme = new Intent();
                switch (content) {
                    case "sigin":  //     => '',
                        scheme.setClass(activity, TaskCenterActivity.class);
                        activity.startActivity(scheme);
                        break;
                    case "task":  //     => '',
                        scheme.setClass(activity, TaskCenterActivity.class);
                        activity.startActivity(scheme);
                        break;
                    case "recharge":  //     => '''',
                        scheme.setClass(activity, RechargeActivity.class);
                        activity.startActivity(scheme);
                        break;
                    case "feedback":  //     => '''',
                        scheme.setClass(activity, FeedBackActivity.class);
                        activity.startActivity(scheme);
                        break;
                    case "setting":  //     => '''',
                        scheme.setClass(activity, SettingsActivity.class);
                        activity.startActivity(scheme);
                        break;
                    case "vip":  //     => '''',
                        scheme.setClass(activity, BaseOptionActivity.class);
                        scheme.putExtra("OPTION", BAOYUE);
                        scheme.putExtra("title", LanguageUtil.getString(activity, R.string.BaoyueActivity_title));
                        activity.startActivity(scheme);
                        break;
                    case "login":  //     => '''',
                        //scheme.setClass(activity, LoginActivity.class);
                        MainHttpTask.getInstance().Gotologin(activity);
                        break;
                }
                break;
            //3,4.5???????????????
            case 3:
            case 4:
            case 5:
                if (TextUtils.equals(bannerItemStore.getRedirect_type(), "1")) {
                    activity.startActivity(new Intent(activity, AboutActivity.class).putExtra("url", content));
                } else {
                    activity.startActivity(new Intent(activity, AboutActivity.class).
                            putExtra("url", content)
                            .putExtra("style", "4"));
                }
                break;
            case 6:
                EventBus.getDefault().post(new SkipToBoYinEvent(content));
                break;
        }
    }
}