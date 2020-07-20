package com.heiheilianzai.app.utils.decode;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;
import com.github.piasy.biv.loader.ImageLoader;
import com.github.piasy.biv.loader.glide.GlideLoaderException;
import com.github.piasy.biv.loader.glide.GlideProgressSupport;
import com.github.piasy.biv.loader.glide.ImageDownloadTarget;
import com.github.piasy.biv.loader.glide.PrefetchTarget;
import com.github.piasy.biv.metadata.ImageInfoExtractor;
import com.heiheilianzai.app.utils.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;

/**
 * 自定义ImageLoader主要在漫画阅读加载图片使用。（新需求图片要显示加载进度）
 */
public class GlideEncypeImageLoader implements ImageLoader {
    protected final RequestManager mRequestManager;

    private final Map<Integer, ImageDownloadTarget> mFlyingRequestTargets = new HashMap<>(3);

    protected GlideEncypeImageLoader(Context context, OkHttpClient okHttpClient) {
        GlideProgressSupport.init(Glide.get(context), okHttpClient);
        mRequestManager = Glide.with(context);
    }

    public static GlideEncypeImageLoader with(Context context) {
        return with(context, null);
    }

    public static GlideEncypeImageLoader with(Context context, OkHttpClient okHttpClient) {
        return new GlideEncypeImageLoader(context, okHttpClient);
    }

    @Override
    public void loadImage(final int requestId, final Uri uri, final Callback callback) {
        final boolean[] cacheMissed = new boolean[1];
        ImageDownloadTarget target = new ImageDownloadTarget(uri.toString()) {
            @Override
            public void onResourceReady(@NonNull File resource, Transition<? super File> transition) {
                super.onResourceReady(resource, transition);
                File file = null;
                try {
                    if (StringUtils.isHttpPrefix(uri.toString())) {//网络图片解密，本地图片不解密。（所有本地图片均为未加密图片）
                        InputStream inputStream = new FileInputStream(resource);
                        file = AESUtil.decryptFile(AESUtil.key, inputStream, AESUtil.desFile + resource.getName());
                    } else {
                        file = resource;
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                if(cacheMissed!=null&&cacheMissed.length>0){
                    if (cacheMissed[0]) {
                        callback.onCacheMiss(ImageInfoExtractor.getImageType(file), file);
                    } else {
                        callback.onCacheHit(ImageInfoExtractor.getImageType(file), file);
                    }
                }
                callback.onSuccess(file);
            }

            @Override
            public void onLoadFailed(final Drawable errorDrawable) {
                super.onLoadFailed(errorDrawable);
                callback.onFail(new GlideLoaderException(errorDrawable));
            }

            @Override
            public void onDownloadStart() {
                cacheMissed[0] = true;
                callback.onStart();
            }

            @Override
            public void onProgress(int progress) {
                callback.onProgress(progress);
            }

            @Override
            public void onDownloadFinish() {
                callback.onFinish();
            }
        };
        cancel(requestId);
        rememberTarget(requestId, target);
        downloadImageInto(uri, target);
    }

    @Override
    public void prefetch(Uri uri) {
        downloadImageInto(uri, new PrefetchTarget());
    }

    @Override
    public synchronized void cancel(int requestId) {
        clearTarget(mFlyingRequestTargets.remove(requestId));
    }

    @Override
    public synchronized void cancelAll() {
        List<ImageDownloadTarget> targets = new ArrayList<>(mFlyingRequestTargets.values());
        for (ImageDownloadTarget target : targets) {
            clearTarget(target);
        }
    }

    protected void downloadImageInto(Uri uri, Target<File> target) {
        mRequestManager
                .downloadOnly()
                .load(uri)
                .into(target);
    }

    private synchronized void rememberTarget(int requestId, ImageDownloadTarget target) {
        mFlyingRequestTargets.put(requestId, target);
    }

    private void clearTarget(ImageDownloadTarget target) {
        if (target != null) {
            mRequestManager.clear(target);
        }
    }
}
