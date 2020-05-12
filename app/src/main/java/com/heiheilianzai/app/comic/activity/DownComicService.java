package com.heiheilianzai.app.comic.activity;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.comic.been.BaseComic;
import com.heiheilianzai.app.comic.been.BaseComicImage;
import com.heiheilianzai.app.comic.been.ComicChapter;
import com.heiheilianzai.app.comic.been.ComicChapterItem;
import com.heiheilianzai.app.comic.eventbus.DownComicEvenbus;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static com.heiheilianzai.app.comic.fragment.DownMangerComicFragment.DownMangerComicFragment;
import static com.heiheilianzai.app.utils.FileManager.GlideCopy;

public class DownComicService extends IntentService {
    Context activity;
    Gson gson = new Gson();
    int TotalChapter, process;

    public DownComicService() {
        super("sss");
    }

    public DownComicService(String name) {
        super(name);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        activity = this;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Down(intent);
    }

    private void Down(Intent intent) {
        DownComicEvenbus downComicEvenbus = new DownComicEvenbus();
        List<ComicChapter> comicChapterCatalogs;
        BaseComic baseComic;
        int down_chapters;
        long id;
        String result;
        Bundle bundle2 = intent.getBundleExtra("downcomic");
        baseComic = (BaseComic) bundle2.getSerializable("baseComic");
        result = bundle2.getString("result");
        comicChapterCatalogs = (List<ComicChapter>) (bundle2.getSerializable("comicChapter"));
        downComicEvenbus.baseComic = baseComic;
        String comic_id = baseComic.getComic_id();
        id = baseComic.getId();
        down_chapters = baseComic.getDown_chapters();
        TotalChapter = 0;
        List<ComicChapterItem> comicChapterItemList = gson.fromJson(result, new TypeToken<List<ComicChapterItem>>() {
        }.getType());

        for (ComicChapterItem comicChapterItem : comicChapterItemList) {
            ++TotalChapter;
            String s = gson.toJson(comicChapterItem);
            String chapter_id = comicChapterItem.chapter_id;
            process = 0;
            int lengthTemp = comicChapterItem.image_list.size();
            MyToash.Log("XXomicChapter22", chapter_id + "   " + process + "   " + lengthTemp);
            Flag:
            for (BaseComicImage baseComicImage : comicChapterItem.image_list) {
                baseComicImage.comic_id = comic_id;
                baseComicImage.chapter_id = chapter_id;
                MyToash.Log("XXomicChapter33", baseComicImage.image);
                String ImgName = "";
                try {
                    String localPath = FileManager.getManhuaSDCardRoot().concat(comic_id + "/").concat(chapter_id + "/");
                    if (baseComicImage.image.contains(".jpg")) {
                        ImgName = baseComicImage.image_id + ".jpg";
                    } else if (baseComicImage.image.contains(".jpeg")) {
                        ImgName = baseComicImage.image_id + ".jpeg";
                    } else if (baseComicImage.image.contains(".png")) {
                        ImgName = baseComicImage.image_id + ".png";
                    }
                    if (ImgName.equals("")) {
                        break Flag;
                    }
                    File file = new File(localPath.concat(ImgName));
                    MyToash.Log("XXomicChapter44", localPath.concat(ImgName));
                    if (file.exists()) {
                        process++;
                        if (process == lengthTemp) {
                            ShareUitls.putComicDownStatus(this, chapter_id, 1);
                            downComicEvenbus.flag = false;
                            EventBus.getDefault().post(downComicEvenbus);
                        }
                    } else {
                        File dir = new File(localPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        try {
                            file.createNewFile();
                            try {
                                File filee = Glide.with(activity)
                                        .load(baseComicImage.image)
                                        .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                        .get();
                                GlideCopy(filee, file);
                                process++;
                                if (process == lengthTemp) {
                                    ShareUitls.putComicDownStatus(activity, chapter_id, 1);
                                    downComicEvenbus.flag = false;
                                    EventBus.getDefault().post(downComicEvenbus);
                                }
                            } catch (Exception e) {
                                ShareUitls.putComicDownStatus(activity, chapter_id, 3);
                                e.printStackTrace();
                            }
                        } catch (IOException e) {
                            ShareUitls.putComicDownStatus(activity, chapter_id, 3);
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    ShareUitls.putComicDownStatus(activity, chapter_id, 3);
                }
            }
            ++down_chapters;
            ContentValues values1 = new ContentValues();
            values1.put("down_chapters", down_chapters);
            LitePal.update(BaseComic.class, values1, id);
            if (DownMangerComicFragment) {
                baseComic.setDown_chapters(down_chapters);
                EventBus.getDefault().post(baseComic);//更新上一界面的 数据
            }
            ContentValues values = new ContentValues();
            values.put("ImagesText", s);
            values.put("ISDown", "1");
            long chapterid = comicChapterCatalogs.get(comicChapterItem.display_order).getId();
            int i;
            if (id == 0) {
                i = LitePal.updateAll(ComicChapter.class, values, "chapter_id = ?", chapter_id);
            } else {
                i = LitePal.update(ComicChapter.class, values, chapterid);
            }
        }
        downComicEvenbus.flag = true;
        downComicEvenbus.Down_Size = TotalChapter;
        EventBus.getDefault().post(downComicEvenbus);
    }
}
