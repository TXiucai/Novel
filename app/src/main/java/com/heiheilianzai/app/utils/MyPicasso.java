package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.heiheilianzai.app.R;

import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

/**
 * Created by scb on 2018/12/14.
 */
public class MyPicasso {

    public static void IoadImage(Activity activity, String url, int error, ImageView imageView) {
        GlideImageNoSize(activity, url, imageView);
    }

    public static void GlideImageNoSize(Activity activity, String url, ImageView imageView) {
        GlideImageNoSize(activity, url, imageView, R.mipmap.icon_comic_def);
    }

    public static void GlideImageNoSize(Activity activity, String url, ImageView imageView, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = new RequestOptions()
                    .placeholder(def)    //加载成功之前占位图
                    .error(def)    //加载错误之后的错误图
                    .skipMemoryCache(true)        //
                    .diskCacheStrategy(DiskCacheStrategy.ALL);    //缓存所有版本的图像
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImage(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = new RequestOptions()
                    .placeholder(def)    //加载成功之前占位图
                    .error(def)    //加载错误之后的错误图
                    //指定图片的尺寸
                    .override(width, height)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);    //缓存所有版本的图像
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }

    public static void GlideImageRoundedCorners(int radius, Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImageRoundedCorners(radius, activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImageRoundedCorners(int radius, Activity activity, String url, ImageView imageView, int width, int height, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = new RequestOptions()
                    .error(def)    //加载错误之后的错误图
                    .override(width, height)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)        //缓存所有版本的图像
                    .transforms(new CenterCrop(), new RoundedCornersTransformation(ImageUtil.dp2px(activity, radius), 0));
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }

    public static void GlideImageRoundedGasoMohu(Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImageRoundedGasoMohu(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImageRoundedGasoMohu(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = new RequestOptions()
                    .placeholder(def)    //加载成功之前占位图
                    .error(def)    //加载错误之后的错误图
                    .override(width, height) //指定图片的尺寸
                    .centerCrop()
                    .transform(new BlurTransformation())
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL);
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }
}





