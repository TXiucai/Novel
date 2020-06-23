package com.heiheilianzai.app.utils.decode;

import android.content.Context;
import android.support.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.Registry;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.bitmap_recycle.LruBitmapPool;
import com.bumptech.glide.load.engine.cache.InternalCacheDiskCacheFactory;
import com.bumptech.glide.load.engine.cache.LruResourceCache;
import com.bumptech.glide.load.model.FileLoader;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@GlideModule
public class MyGlideModule extends AppGlideModule {

    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        int cacheSize = 1024 * 1024 * 500;//500MB
        builder.setMemoryCache(new LruResourceCache(cacheSize));  //指定内存缓存大小
        // 指定位置在packageName/cache/glide_cache,大小为cacheSize的磁盘缓存
        builder.setDiskCache(new InternalCacheDiskCacheFactory(context, "glide_cache", cacheSize));
        //设置BitmapPool缓存内存大小
        builder.setBitmapPool(new LruBitmapPool(cacheSize));
        //设置解码格式RGB_565，该格式解码的Bitmap不支持透明度
        builder.setDefaultRequestOptions(new RequestOptions().format(DecodeFormat.PREFER_RGB_565).disallowHardwareConfig());
    }

    @Override
    public void registerComponents(@NonNull Context context, @NonNull Glide glide, @NonNull Registry registry) {
        super.registerComponents(context, glide, registry);
        registry.append(File.class, InputStream.class,
                new FileLoader.Factory<InputStream>(new FileLoader.FileOpener<InputStream>() {

                    @Override
                    public InputStream open(File file) throws FileNotFoundException {
                        InputStream inputStream = new FileInputStream(file);
                        return new FileInputStream(AESUtil.decryptFile(AESUtil.key, inputStream, AESUtil.desFile + file.getName()));
                    }

                    @Override
                    public void close(InputStream inputStream) throws IOException {
                        inputStream.close();
                    }

                    @Override
                    public Class<InputStream> getDataClass() {
                        return InputStream.class;
                    }
                }));
    }

    @Override
    public boolean isManifestParsingEnabled() {
        return false;
    }
}
