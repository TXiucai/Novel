package com.heiheilianzai.app.comic.been;

import android.app.Activity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.Task.InstructTask;
import com.heiheilianzai.app.Task.TaskManager;
import com.heiheilianzai.app.comic.view.LargeImageView;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;

import java.io.File;
import java.io.FileInputStream;

import static com.heiheilianzai.app.utils.FileManager.GlideCopy;
import static com.heiheilianzai.app.utils.FileManager.getManhuaSDCardRoot;


/**
 * Created by scb on 2018/12/14.
 */

public class MyGlide {
    static int WIDTH;
    static int HEIGHT;
    private static MyGlide myGlide;

    private MyGlide() {
    }

    public static MyGlide getMyGlide(Activity activity, int w, int h) {
        WIDTH = w;
        HEIGHT = h;
        if (myGlide == null) {
            synchronized (MyGlide.class) {
                if (myGlide == null)
                    myGlide = new MyGlide();
            }
        }
        return myGlide;
    }

    private static TaskManager mTaskManager;

    public static void GlideImageSubsamplingScaleImageView(Activity activity, BaseComicImage baseComicImage, LargeImageView imageView) {
        try {
            MyToash.Log("baseComicIBB", "1");
            File localPathFile;
            String ImgName = "";
            String localPath = getManhuaSDCardRoot().concat(baseComicImage.comic_id + "/").concat(baseComicImage.chapter_id + "/");
            if (baseComicImage.image == null) {
                return;
            }
            MyToash.Log("baseComicIBB", "2");
            if (baseComicImage.image.contains(".jpg")) {
                ImgName = baseComicImage.image_id + ".jpg";
            } else if (baseComicImage.image.contains(".jpeg")) {
                ImgName = baseComicImage.image_id + ".jpeg";
            } else if (baseComicImage.image.contains(".png")) {
                ImgName = baseComicImage.image_id + ".png";
            } else {
                return;
            }
            MyToash.Log("baseComicIBB", "3");
            localPathFile = new File(localPath.concat(ImgName));
            if (localPathFile.exists()) {
                MyToash.Log("baseComicIBB", localPathFile.getAbsolutePath());
                try {
                    imageView.setInputStream(new FileInputStream(localPathFile));
                } catch (Exception e) {
                } catch (Error e) {
                }
            } else {
                localPathFile.mkdirs();
                MyToash.Log("baseComicIBB", localPathFile.getAbsolutePath());
                if (mTaskManager == null) {
                    mTaskManager = new TaskManager();
                }
                mTaskManager.addQueueTask(new InstructTask<String, String>(null) {

                    @Override
                    public String doRun(String s) {
                        try {
                            File filee = Glide.with(activity)
                                    .load(baseComicImage.image)
                                    .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                    .get();
                            GlideCopy(filee, localPathFile);
                            MyToash.Log("baseComicIBBA", filee.getAbsolutePath() + "  " + localPathFile.getAbsolutePath());
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        imageView.setInputStream(new FileInputStream(localPathFile));
                                    } catch (Exception e) {
                                    } catch (Error e) {
                                    }
                                }
                            });
                        } catch (Exception e) {
                        } catch (Error e) {
                        }
                        return null;
                    }
                });
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    public static void GlideImage(Activity activity, BaseComicImage baseComicImage, ImageView imageView, int width, int height) {
        try {
            MyToash.Log("baseComicImagea", baseComicImage.toString());
            File localPathFile = FileManager.getManhuaSDCardRootImg(baseComicImage);
            if (localPathFile != null) {
                MyToash.Log("baseComicImage--", localPathFile.getAbsolutePath());
                RequestOptions options = new RequestOptions()
                        .placeholder(R.mipmap.icon_comic_def)        //加载成功之前占位图
                        .error(R.mipmap.icon_comic_def)        //加载错误之后的错误图
                        //指定图片的尺寸 WIDTH * height / width
                        .override(WIDTH, height)
                        .centerCrop()
                        .skipMemoryCache(false)
                        .diskCacheStrategy(DiskCacheStrategy.ALL);    //bu缓存
                try {
                    Glide.with(activity).load(localPathFile).apply(options).into(imageView);
                } catch (Exception e) {
                }
            } else {
                String url = baseComicImage.image;
                if (url == null || url.length() == 0) {
                    return;
                } else {
                    RequestOptions options = new RequestOptions()
                            .placeholder(R.mipmap.icon_comic_def)        //加载成功之前占位图
                            .error(R.mipmap.icon_comic_def)        //加载错误之后的错误图
                            //指定图片的尺寸
                            .override(WIDTH, height)
                            .centerCrop()//
                            .diskCacheStrategy(DiskCacheStrategy.ALL);    //缓存所有版本的图像
                    try {
                        Glide.with(activity).load(url).apply(options).into(imageView);
                    } catch (Exception e) {
                    } catch (Error e) {
                    }
                }
            }
        } catch (Exception e) {
        } catch (Error e) {
        }
    }

    public static void GlideImagePalette(Activity activity, String url, ImageView imageView, int width, int height) {
        GlideImagePalette(activity, url, imageView, width, height, R.mipmap.icon_comic_def);
    }

    public static void GlideImagePalette(Activity activity, String url, ImageView imageView, int width, int height, int def) {
        if (url == null || url.length() == 0) {
            return;
        } else {
            imageView.setImageResource(def);
            RequestOptions options = new RequestOptions()
                    .placeholder(def)        //加载成功之前占位图
                    .error(def)        //加载错误之后的错误图
                    //指定图片的尺寸
                    .override(WIDTH, WIDTH * height / width)
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);    //缓存所有版本的图像
            try {
                Glide.with(activity).load(url).apply(options).into(imageView);
            } catch (Exception e) {
            }
        }
    }
}





