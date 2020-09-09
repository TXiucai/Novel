package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseShelfAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ComicConfig;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.model.comic.ComicChapter;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyPicasso;

import org.litepal.LitePal;

import java.util.List;

/**
 * 书架编辑漫画Adapter
 */
public class ComicAdapterNew extends BaseShelfAdapter<BaseComic> {

    public ComicAdapterNew(int WIDTH, int HEIGHT, List<BaseComic> mBookList, Activity mActivity) {
        super(WIDTH, HEIGHT, mBookList, mActivity,2);
    }

    @Override
    protected void getView(ViewHolder viewHolder, BaseComic baseComic) {
        viewHolder.shelfitem_title.setText(baseComic.getName());
        viewHolder.shelfitem_img.setImageResource(R.mipmap.comic_def_v);
        if (baseComic.getRecentChapter() != 0 && (baseComic.getRecentChapter() != baseComic.getTotal_chapters())) {
            viewHolder.shelfitem_top_newchapter.setVisibility(View.VISIBLE);
            viewHolder.shelfitem_top_newchapter.setText(baseComic.getRecentChapter() + "");
        } else {
            viewHolder.shelfitem_top_newchapter.setVisibility(View.GONE);
        }
        MyPicasso.GlideImageNoSize(mActivity, baseComic.getVertical_cover(), viewHolder.shelfitem_img, R.mipmap.comic_def_v);
    }

    /**
     * 删除书架书籍
     */
    public void deleteBook(final String bookIdArr) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("comic_id", bookIdArr);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ComicConfig.COMIC_SHELF_DEL, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Override
    protected StringBuilder checkedBooks() {
        StringBuilder builder = new StringBuilder();
        for (BaseComic BaseComic : checkedBookList) {
            builder.append("," + BaseComic.getComic_id());
            if (BaseComic.getId() != 0) {
                LitePal.delete(BaseComic.class, BaseComic.getId());
            } else {
                LitePal.deleteAll(BaseComic.class, "comic_id = ?", BaseComic.getComic_id());
            }
            LitePal.deleteAllAsync(ComicChapter.class, "comic_id = ?", BaseComic.getComic_id());
            String filepath = FileManager.getSDCardRoot().concat("Reader/comic/").concat(BaseComic.getComic_id() + "/");
            FileManager.deleteFile(filepath);
        }
        return builder;
    }
}
