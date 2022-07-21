package com.heiheilianzai.app.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.Startpage;
import com.heiheilianzai.app.ui.activity.AdvertisementActivity;
import com.mobi.xad.XAdManager;
import com.mobi.xad.XRequestManager;
import com.mobi.xad.bean.AdInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.wasabeef.glide.transformations.BlurTransformation;

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
        imageView.setImageResource(def);
        if (url == null || url.length() == 0) {
            return;
        } else {
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).load(url).apply(getRequestOptions(def, imageView)).into(imageView);
            }
        }
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImage(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImage(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        imageView.setImageResource(def);
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = getRequestOptions(width, height, def, true, false, imageView);
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).load(url).apply(options).into(imageView);
            }
        }
    }

    public static void GlideImageRoundedCorners(int radius, Activity activity, String url, ImageView imageView, int width, int height) {
        if (activity != null && !activity.isFinishing()) {
            GlideImageRoundedCorners(radius, activity, url, imageView, width, height, R.mipmap.icon_comic_def);
        }
    }

    public static void GlideImageRoundedCorners(int radius, Activity activity, String url, ImageView imageView, int width, int height, int def) {
        imageView.setImageResource(def);
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = getRequestOptions(width, height, def, radius, activity, imageView);
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).load(url).apply(options).into(imageView);
            }
        }
    }

    public static void GlideImageRoundedGasoMohu(Activity activity, String url, ImageView imageView, int width, int height) {
        if (activity != null && !activity.isFinishing())
            GlideImageRoundedGasoMohu(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImageRoundedGasoMohu(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        imageView.setImageResource(def);
        if (url == null || url.length() == 0) {
            return;
        } else {
            RequestOptions options = getRequestOptions(width, height, def, true, true, imageView);
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).load(url).apply(options).into(imageView);
            }
        }
    }

    public static void loadLocalImage(Activity activity, int drawable, ImageView imageView) {
        if (activity != null && !activity.isFinishing()) {
            Glide.with(activity)
                    .setDefaultRequestOptions(new RequestOptions().set(GifOptions.DECODE_FORMAT, DecodeFormat.PREFER_ARGB_8888))//处理gif动图黑底的情况
                    .load(drawable).into(imageView);
        }
    }

    public static RequestOptions getRequestOptions(int def, ImageView imageView) {
        return getRequestOptions(0, 0, def, 0, null, false, false, imageView);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, boolean centerCrop, boolean transform, ImageView imageView) {
        return getRequestOptions(width, height, def, 0, null, centerCrop, transform, imageView);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, int radius, Activity activity, ImageView imageView) {
        return getRequestOptions(width, height, def, radius, activity, false, false, imageView);
    }

    public static RequestOptions getRequestOptions(int width, int height, int def, int radius, Activity activity, boolean centerCrop, boolean transform, ImageView imageView) {
        RequestOptions options = new RequestOptions()
                .placeholder(def)    //加载成功之前占位图
                .error(def)    //加载错误之后的错误图
                .priority(Priority.LOW);
        if (width > 0 && height > 0) {
            options = options.override(width, height);
        }
        if (radius > 0 && activity != null) {
            ImageUtil.setImageViewCenterCrop(width, height, imageView);
        }
        if (centerCrop) {
            ImageUtil.setImageViewCenterCrop(width, height, imageView);
        }
        if (transform) {
            options = options.transform(new BlurTransformation());
        }
        return options;
    }

    /**
     * 加载广告图片专用
     *
     * @param imageView
     * @param startpage
     * @param activity
     * @param listener
     */
    public static void intoAdImage(ImageView imageView, Startpage startpage, Activity activity, AdvertisementActivity.OnAdImageListener listener) {
        if (activity != null && !activity.isFinishing()) {
            RequestBuilder builder = Glide.with(activity).load(startpage.image).listener(new RequestListener<Drawable>() {
                @Override
                public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                    listener.onFailed();
                    return false;
                }

                @Override
                public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                    listener.onAnimationEnd();
                    return false;
                }
            });
            if (Rom.isVivo()) {
                imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            }
            builder.into(imageView);
        }
    }

    /**
     * 广告sdk 朴光以及错误的上报
     *
     * @param context
     * @param adInfo
     * @param imageView
     */
    public static void glideSdkAd(Context context, AdInfo adInfo, String imgUrl, ImageView imageView) {
        Glide.with(context).load(imgUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                XRequestManager.INSTANCE.requestErrorLoadImage(context, adInfo, "获取sdk图片失败" + e);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                XRequestManager.INSTANCE.requestEventExposure(context, adInfo);
                return false;
            }
        }).into(imageView);
    }

    public static void glideSdkAd(Context context, AdInfo adInfo, String imgUrl, ImageView imageView, int width, int height, int def) {
        imageView.setImageResource(def);
        RequestOptions options = getRequestOptions(width, height, def, true, false, imageView);
        Glide.with(context).load(imgUrl).addListener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                XRequestManager.INSTANCE.requestErrorLoadImage(context, adInfo, "获取sdk图片失败" + e);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                XRequestManager.INSTANCE.requestEventExposure(context, adInfo);
                return false;
            }
        }).into(imageView);
    }

    /**
     * 下载广告图片到本地做默认图
     *
     * @param iconUrl
     * @param activity
     * @param onDwnloadImg
     */
    public static void downloadIMG(String iconUrl, Activity activity, OnDwnloadImg onDwnloadImg) {
        int myWidth = Target.SIZE_ORIGINAL;
        int myHeight = Target.SIZE_ORIGINAL;
        Glide.with(activity)
                .asBitmap()
                .load(iconUrl)
                .into(new SimpleTarget<Bitmap>(myWidth, myHeight) {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        if (resource != null) {
                            String name = saveIMG(resource);
                            if (onDwnloadImg != null) {
                                onDwnloadImg.onFile(name);
                            }
                        }
                    }
                });
    }

    public interface OnDwnloadImg {
        void onFile(String nameFile);
    }

    /**
     * 保存图片到本地
     *
     * @param bitmap
     */
    private static String saveIMG(Bitmap bitmap) {
        //可访问的图片文件夹
        String file = FileManager.getManhuaSDCardRoot();
        File appDir = new File(file);
        if (!appDir.exists()) {
            appDir.mkdirs();
        }
        //命名图片并保存
        String picName = "advertising.jpg";
        File currentFile = new File(appDir, picName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(currentFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);//质量为100表示设置压缩率为0
            fos.flush();
            return file + picName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}