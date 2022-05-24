package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeTransparentActivity;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.comic.BaseComic;
import com.heiheilianzai.app.ui.fragment.BookshelfFragment;

import java.util.List;

import butterknife.BindView;

public class BookSelfActivity extends BaseButterKnifeTransparentActivity {
    @BindView(R.id.titlebar_back)
    public LinearLayout mBack;
    @BindView(R.id.titlebar_text)
    public TextView mTitle;
    @BindView(R.id.shelf_book_delete_btn)
    public LinearLayout shelf_book_delete_btn;
    private List<BaseBook> bookLists;
    private List<BaseComic> comicList;

    @Override
    public int initContentView() {
        return R.layout.activity_bookself;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTitle.setText(R.string.MainActivity_shujia);
        mBack.setOnClickListener(v -> finish());
        Intent intent = getIntent();
        if (intent != null) {
            bookLists = (List<BaseBook>) (intent.getSerializableExtra("mBaseBooks"));
            comicList = (List<BaseComic>) (intent.getSerializableExtra("mBaseComics"));
        }
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_bookself, new BookshelfFragment(bookLists, comicList, shelf_book_delete_btn)).commit();
    }
}
