package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.utils.decode.AESUtil;

import java.io.File;

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
            imageView.setImageResource(def);
            Glide.with(activity).load(url).apply(getRequestOptions(def)).into(imageView);
        }
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImage(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            imageView.setImageResource(def);
            RequestOptions options = getRequestOptions(width, height, def, true, false);
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
            imageView.setImageResource(def);
            RequestOptions options = getRequestOptions(width, height, def, radius, activity);
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
            imageView.setImageResource(def);
            RequestOptions options = getRequestOptions(width, height, def, true, true);
            Glide.with(activity).load(url).apply(options).into(imageView);
        }
    }

    public static RequestOptions getRequestOptions(int def) {
        return getRequestOptions(0, 0, def, 0, null, false, false);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, boolean centerCrop, boolean transform) {
        return getRequestOptions(width, height, def, 0, null, centerCrop, transform);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, int radius, Activity activity) {
        return getRequestOptions(width, height, def, radius, activity, false, false);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, int radius, Activity activity, boolean centerCrop, boolean transform) {
        RequestOptions options = new RequestOptions()
                .placeholder(def)    //加载成功之前占位图
                .error(def)    //加载错误之后的错误图
                .diskCacheStrategy(DiskCacheStrategy.ALL);
        if (width > 0 && height > 0) {
            options = options.override(width, height);
        }
        if (radius > 0 && activity != null) {
            options = options.transforms(new CenterCrop(), new RoundedCornersTransformation(ImageUtil.dp2px(activity, radius), 0));
        }
        if (centerCrop) {
            options = options.centerCrop();
        }
        if (transform) {
            options = options.transform(new BlurTransformation());
        }
        return options;
    }
}





