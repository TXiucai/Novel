package com.heiheilianzai.app.component;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.BaseComicImage;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.model.comic.ComicChapterItem;
import com.heiheilianzai.app.model.event.comic.DownComicEvenbus;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;
import com.heiheilianzai.app.utils.Utils;
import com.heiheilianzai.app.utils.decode.AESUtil;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import static com.heiheilianzai.app.ui.fragment.comic.DownMangerComicFragment.DownMangerComicFragment;
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
        down(intent);
    }

    private void down(Intent intent) {
        DownComicEvenbus downComicEvenbus = new DownComicEvenbus();
        List<ComicChapter> comicChapterCatalogs;
        BaseComic baseComic;
        int down_chapters;
        long id;
        Bundle bundle2 = intent.getBundleExtra("downcomic");
        baseComic = (BaseComic) bundle2.getSerializable("baseComic");
        String result = ShareUitls.getString(activity, "comicChapterCatalogs", "");
        if (TextUtils.isEmpty(result)) {
            return;
        }
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
            downComicEvenbus.chapter_id = chapter_id;
            process = 0;
            int lengthTemp = comicChapterItem.image_list.size();
            Flag:
            for (BaseComicImage baseComicImage : comicChapterItem.image_list) {
                baseComicImage.comic_id = comic_id;
                baseComicImage.chapter_id = chapter_id;
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
                            downComicEvenbus.flag = false;
                            EventBus.getDefault().post(downComicEvenbus);
                        }
                    } else {
                        File dir = new File(localPath);
                        if (!dir.exists()) {
                            dir.mkdirs();
                        }
                        file.createNewFile();
                        File filee = Glide.with(activity)
                                .load(baseComicImage.image)
                                .downloadOnly(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                                .get();
                        if (baseComicImage.image.substring(baseComicImage.image.length() - 2, baseComicImage.image.length()).equals(ReaderConfig.IMG_CRYPTOGRAPHIC_POSTFIX)) {//??????????????????
                            InputStream inputStream = new FileInputStream(filee);
                            filee = AESUtil.decryptFile(AESUtil.key, inputStream, AESUtil.desFile + filee.getName());
                        }
                        GlideCopy(filee, file);
                        process++;
                        if (process == lengthTemp) {
                            downComicEvenbus.flag = false;
                            downComicEvenbus.status = 1;
                            EventBus.getDefault().post(downComicEvenbus);
                        }
                    }
                } catch (Exception e) {
                    downComicEvenbus.flag = false;
                    downComicEvenbus.status = 3;
                    EventBus.getDefault().post(downComicEvenbus);
                }
            }
            ++down_chapters;
            ContentValues values1 = new ContentValues();
            values1.put("down_chapters", down_chapters);
            values1.put("current_chapter_id", comicChapterItem.getChapter_id());
            LitePal.update(BaseComic.class, values1, id);
            LitePal.updateAll(BaseComic.class, values1, "comic_id = ? and uid = ?", comic_id, Utils.getUID(activity));
            if (DownMangerComicFragment) {
                baseComic.setDown_chapters(down_chapters);
                EventBus.getDefault().post(baseComic);//????????????????????? ??????
            }
            ContentValues values = new ContentValues();
            values.put("ImagesText", s);
            values.put("ISDown", "1");
            LitePal.updateAll(ComicChapter.class, values, "comic_id = ? and chapter_id = ? and uid = ?", comicChapterItem.getComic_id(), chapter_id, Utils.getUID(activity));
        }
        //??????????????????????????????
        List<ComicChapter> comicChapters = LitePal.where("ISDown != ? and comic_id = ? and uid = ?", "2", comic_id, Utils.getUID(activity)).find(ComicChapter.class);
        if (comicChapters.size() == comicChapterItemList.size()) {
            downComicEvenbus.flag = true;
            downComicEvenbus.Down_Size = TotalChapter;
            EventBus.getDefault().post(downComicEvenbus);
        }
    }
}
