package com.heiheilianzai.app.adapter.book;

import android.app.Activity;
import android.view.View;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseShelfAdapter;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ChapterItem;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.utils.FileManager;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.MyPicasso;

import org.litepal.LitePal;

import java.util.List;

/**
 * 书架编辑小说Adapter
 */
public class NewShelfAdapter extends BaseShelfAdapter<BaseBook> {

    public NewShelfAdapter(int WIDTH, int HEIGHT, List<BaseBook> mBookList, Activity mActivity) {
        super(WIDTH, HEIGHT, mBookList, mActivity, 1);
    }

    @Override
    protected void getView(ViewHolder viewHolder, BaseBook baseBook) {
        viewHolder.shelfitem_title.setText(baseBook.getName());
        viewHolder.shelfitem_img.setImageResource(R.mipmap.book_def_v);
        if (baseBook.getRecentChapter() != 0 && (baseBook.getRecentChapter() != baseBook.getTotal_Chapter())) {
            viewHolder.shelfitem_top_newchapter.setVisibility(View.VISIBLE);
            viewHolder.shelfitem_top_newchapter.setText(baseBook.getRecentChapter() + "");
        } else {
            viewHolder.shelfitem_top_newchapter.setVisibility(View.GONE);
        }
        MyPicasso.GlideImageNoSize(mActivity, baseBook.getCover(), viewHolder.shelfitem_img, R.mipmap.book_def_v);
    }

    /**
     * 删除书架书籍
     */
    public void deleteBook(final String bookIdArr) {
        ReaderParams params = new ReaderParams(mActivity);
        params.putExtraParams("book_id", bookIdArr);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(mActivity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mBookDelCollectUrl, json, false, new HttpUtils.ResponseListener() {
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
        for (BaseBook baseBook : checkedBookList) {
            builder.append("," + baseBook.getBook_id());
            if (baseBook.getId() != 0) {
                LitePal.delete(BaseBook.class, baseBook.getId());
            } else {
                LitePal.deleteAll(BaseBook.class, "book_id = ?", baseBook.getBook_id());
            }
            LitePal.deleteAllAsync(ChapterItem.class, "book_id = ?", baseBook.getBook_id());
            String filepath = FileManager.getSDCardRoot().concat("Reader/book/").concat(baseBook.getBook_id() + "/");
            FileManager.deleteFile(filepath);
        }
        return builder;
    }
}