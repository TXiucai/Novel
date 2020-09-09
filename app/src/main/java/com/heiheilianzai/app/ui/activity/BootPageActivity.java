package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.umeng.analytics.MobclickAgent;

import butterknife.BindView;

//.view.annotation.ContentView;
//.view.annotation.ViewInject;
//.x;

/**
 * Created by scb on 2018/12/10.
 */
//@ContentView(R.layout.activity_bootpage)
public class BootPageActivity extends BaseButterKnifeActivity {


    @BindView(R2.id.activity_bootpage)
    public ViewPager activity_bootpage;
    @BindView(R2.id.bootpage_viewpage_item_im)
    public ImageView bootpage_viewpage_item_im;

    @Override
    public int initContentView() {
        return R.layout.activity_bootpage;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        activity_bootpage.setAdapter(baseAdapterBook);

        bootpage_viewpage_item_im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BootPageActivity.this, FirstStartActivity.class));
                finish();
            }
        });
    }


    PagerAdapter baseAdapterBook = new PagerAdapter() {


        @Override
        public int getCount() {
            return 3;
        }

        //判断是否是否为同一张图片，这里返回方法中的两个参数做比较就可以
        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        //设置viewpage内部东西的方法，如果viewpage内没有子空间滑动产生不了动画效果
        @Override
        public Object instantiateItem(ViewGroup container, int i) {
            View view = LayoutInflater.from(BootPageActivity.this).inflate(R.layout.bootpage_viewpage_item, null);
            // ImageView bootpage_viewpage_item_im = view.findViewById(R.id.bootpage_viewpage_item_im);
            RelativeLayout bootpage_viewpage_item_re = view.findViewById(R.id.bootpage_viewpage_item_re);
            // ImageView  bootpage_viewpage_item_img=view.findViewById(R.id.bootpage_viewpage_item_img);
            //   TextView   bootpage_viewpage_item_img=view.findViewById(R.id.bootpage_viewpage_item_img);

            switch (i) {
            /*    case 0:
                    bootpage_viewpage_item_re.setBackgroundResource(R.drawable.activity_bootpage1);
                    break;
                case 1:
                    bootpage_viewpage_item_re.setBackgroundResource(R.drawable.activity_bootpage2);
                    break;
                case 2:
                    bootpage_viewpage_item_re.setBackgroundResource(R.drawable.activity_bootpage3);
                    break;*/
                case 0:
                    //  bootpage_viewpage_item_re.setBackgroundResource(R.mipmap.bootpage1);
                    break;
                case 1:
                    //  bootpage_viewpage_item_re.setBackgroundResource(R.mipmap.bootpage2);
                    break;
                case 2:
                    //bootpage_viewpage_item_re.setBackgroundResource(R.mipmap.bootpage3);
                    break;
             /*   case 0:
                    //  bootpage_viewpage_item_img.setBackgroundResource(R.mipmap.bootpage1);
                    bootpage_viewpage_item_img.setImageResource(R.mipmap.bootpage1);
                    break;
                case 1:
                 bootpage_viewpage_item_img.setImageResource(R.mipmap.bootpage2);
                    //   bootpage_viewpage_item_img.setBackgroundResource(R.mipmap.bootpage2);
                    break;
                case 2:
                 bootpage_viewpage_item_img.setImageResource(R.mipmap.bootpage3);
                    //    bootpage_viewpage_item_img.setBackgroundResource(R.mipmap.bootpage3);
                    break;*/
            }
/*
            bootpage_viewpage_item_im.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(BootPageActivity.this, FirstStartActivity.class));
                }
            });*/
            //最后要返回的是控件本身
            container.addView(view);
            return view;
        }

        //因为它默认是看三张图片，第四张图片的时候就会报错，还有就是不要返回父类的作用
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
            //         super.destroyItem(container, position, object);
        }
    };


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // 基础指标统计，不能遗漏
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // 基础指标统计，不能遗漏
    }
}
