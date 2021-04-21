package com.heiheilianzai.app.ui.fragment.book;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.StoreComicAdapter;
import com.heiheilianzai.app.adapter.VerticalAdapter;
import com.heiheilianzai.app.base.App;
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BookInfoComment;
import com.heiheilianzai.app.model.book.BaseBook;
import com.heiheilianzai.app.model.book.StroreBookcLable;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.ui.activity.AddCommentActivity;
import com.heiheilianzai.app.ui.activity.BookInfoActivity;
import com.heiheilianzai.app.ui.activity.CommentListActivity;
import com.heiheilianzai.app.ui.activity.NovelActivity;
import com.heiheilianzai.app.ui.activity.ReplyCommentActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicCommentActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.AdaptionGridView;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.ObservableScrollView;

import java.util.List;

import butterknife.BindView;

/**
 * 漫画详情评论
 * Created by scb on 2018/6/9.
 */
public class NovelInfoCommentFragment extends BaseButterKnifeFragment {
    @Override
    public int initContentView() {
        return R.layout.fragment_comicinfo_comment;
    }

    /**
     * 评论的视图容器
     */
    @BindView(R.id.activity_book_info_content_comment_container)
    public LinearLayout activity_book_info_content_comment_container;
    @BindView(R.id.activity_book_info_scrollview)
    public ObservableScrollView activity_book_info_scrollview;
    @BindView(R.id.activity_book_info_content_comment_des)
    public TextView activity_book_info_content_comment_des;

    /**
     * 精品推荐的视图容器
     */
    @BindView(R.id.activity_book_info_content_label_container)
    public LinearLayout activity_book_info_content_label_container;
    @BindView(R.id.list_ad_view_layout)
    FrameLayout activity_book_info_ad;
    @BindView(R.id.list_ad_view_img)
    ImageView list_ad_view_img;

    @BindView(R.id.activity_book_info_content_add_comment)
    public TextView activity_book_info_content_add_comment;
    public int WIDTH, HEIGHT, HorizontalSpacing, H100, H50, H20;
    BaseBook baseComic;

    public void senddata(BaseBook baseComic, List<BookInfoComment> bookInfoComments, StroreBookcLable stroreComicLable, BaseAd baseAd) {
        MyToash.Log("http_utaa", bookInfoComments.toString());
        this.baseComic = baseComic;
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        WIDTH = (WIDTH - ImageUtil.dp2px(activity, 40)) / 3;//横向排版 图片宽度
        HEIGHT = (int) (((float) WIDTH * 4f / 3f));//
        HorizontalSpacing = ImageUtil.dp2px(activity, 5);//横间距
        H50 = ImageUtil.dp2px(activity, 50);
        H100 = H50; //  相比书城 没有换一换 布局高度
        H20 = ImageUtil.dp2px(activity, 12);
        activity_book_info_content_comment_des.setText(baseComic.getDescription());
        if (ReaderConfig.USE_AD && baseAd != null) {
            if (App.isVip(activity)) {
                activity_book_info_ad.setVisibility(View.GONE);
            } else {
                activity_book_info_ad.setVisibility(View.VISIBLE);
            }
            ViewGroup.LayoutParams layoutParams = list_ad_view_img.getLayoutParams();
            layoutParams.width = ScreenSizeUtils.getInstance(activity).getScreenWidth() - ImageUtil.dp2px(activity, 20);
            layoutParams.height = layoutParams.width / 3;
            list_ad_view_img.setLayoutParams(layoutParams);
            MyPicasso.GlideImageNoSize(activity, baseAd.ad_image, list_ad_view_img);
            activity_book_info_ad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(activity, WebViewActivity.class);
                    intent.putExtra("url", baseAd.ad_skip_url);
                    intent.putExtra("title", baseAd.ad_title);
                    intent.putExtra("advert_id", baseAd.advert_id);
                    intent.putExtra("ad_url_type", baseAd.ad_url_type);
                    activity.startActivity(intent);
                }
            });

        } else {
            activity_book_info_ad.setVisibility(View.GONE);
        }
        activity_book_info_content_label_container.removeAllViews();
        activity_book_info_content_comment_container.removeAllViews();
        try {
            if (bookInfoComments != null || !bookInfoComments.isEmpty()) {
                for (BookInfoComment bookInfoComment : bookInfoComments) {
                    LinearLayout commentView = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.activity_book_info_content_comment_item, null, false);
                    CircleImageView activity_book_info_content_comment_item_avatar = commentView.findViewById(R.id.activity_book_info_content_comment_item_avatar);
                    TextView activity_book_info_content_comment_item_nickname = commentView.findViewById(R.id.activity_book_info_content_comment_item_nickname);
                    TextView activity_book_info_content_comment_item_content = commentView.findViewById(R.id.activity_book_info_content_comment_item_content);
                    TextView activity_book_info_content_comment_item_reply = commentView.findViewById(R.id.activity_book_info_content_comment_item_reply_info);
                    TextView activity_book_info_content_comment_item_time = commentView.findViewById(R.id.activity_book_info_content_comment_item_time);
                    View comment_item_isvip = commentView.findViewById(R.id.comment_item_isvip);
                    MyPicasso.IoadImage(activity, bookInfoComment.getAvatar(), R.mipmap.icon_def_head, activity_book_info_content_comment_item_avatar);
                    activity_book_info_content_comment_item_nickname.setText(bookInfoComment.getNickname());
                    activity_book_info_content_comment_item_content.setText(bookInfoComment.getContent());
                    activity_book_info_content_comment_item_reply.setText(bookInfoComment.getReply_info());
                    activity_book_info_content_comment_item_reply.setVisibility(TextUtils.isEmpty(bookInfoComment.getReply_info()) ? View.GONE : View.VISIBLE);
                    activity_book_info_content_comment_item_time.setText(bookInfoComment.getTime());
                    comment_item_isvip.setVisibility(bookInfoComment.getIs_vip() == 1 ? View.VISIBLE : View.GONE);
                    //评论点击的处理
                    commentView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, ReplyCommentActivity.class);
                            intent.putExtra("book_id", baseComic.getBook_id());
                            intent.putExtra("comment_id", bookInfoComment.getComment_id());
                            intent.putExtra("avatar", bookInfoComment.getAvatar());
                            intent.putExtra("nickname", bookInfoComment.getNickname());
                            intent.putExtra("origin_content", bookInfoComment.getContent());
                            startActivity(intent);
                        }
                    });
                    activity_book_info_content_comment_container.addView(commentView);
                }
            }
            //"查看全部评论"
            String moreText;
            if (baseComic.getTotal_Chapter() > 0) {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_lookpinglun);
            } else {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_nopinglun);
            }
            LinearLayout commentMoreView = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.activity_book_info_content_comment_more, null, false);
            TextView activity_book_info_content_comment_more_text = commentMoreView.findViewById(R.id.activity_book_info_content_comment_more_text);
            activity_book_info_content_comment_more_text.setText(String.format(moreText, baseComic.getTotal_Chapter() + ""));
            activity_book_info_content_add_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //写评论
                    Intent intent = new Intent(activity, AddCommentActivity.class);
                    intent.putExtra("book_id", baseComic.getBook_id());
                    startActivity(intent);
                }
            });
            commentMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(activity, ComicCommentActivity.class).putExtra("book_id", baseComic.getBook_id()).putExtra("IsBook", false), 11);
                }
            });
            activity_book_info_content_comment_container.addView(commentMoreView);
            if (stroreComicLable != null && !stroreComicLable.list.isEmpty()) {
                int Size = stroreComicLable.list.size();
                View type3 = LayoutInflater.from(getContext()).inflate(R.layout.lable_bookinfo_layout, null, false);
                TextView fragment_store_gridview3_text = type3.findViewById(R.id.fragment_store_gridview3_text);
                fragment_store_gridview3_text.setText(stroreComicLable.label);
                AdaptionGridView fragment_store_gridview3_gridview_first = type3.findViewById(R.id.fragment_store_gridview3_gridview_first);
                fragment_store_gridview3_gridview_first.setHorizontalSpacing(HorizontalSpacing);
                fragment_store_gridview3_gridview_first.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (activity != null) {
                            String bookId = stroreComicLable.list.get(position).getBook_id();
                            activity.startActivity(BookInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_info) + " " + bookId, bookId));
                        }
                    }
                });
                int minSize = 0;
                int ItemHeigth = 0, start = 0;
                if (stroreComicLable.style == 2) {
                    minSize = Math.min(Size, 6);
                    if (minSize > 3) {
                        ItemHeigth = H100 + (HEIGHT + H50) * 2;
                    } else {
                        ItemHeigth = H100 + HEIGHT + H50;
                    }
                } else {
                    minSize = Math.min(Size, 3);
                    ItemHeigth = H100 + HEIGHT + H50;
                }
                List<StroreBookcLable.Book> firstList = stroreComicLable.list.subList(start, minSize);
                VerticalAdapter verticalAdapter = new VerticalAdapter(activity, firstList, WIDTH, HEIGHT, false);
                fragment_store_gridview3_gridview_first.setAdapter(verticalAdapter);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, H20, 0, 0);
                params.height = ItemHeigth + H20;
                activity_book_info_content_label_container.addView(type3, params);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        WIDTH = ScreenSizeUtils.getInstance(activity).getScreenWidth();
        HEIGHT = ScreenSizeUtils.getInstance(activity).getScreenHeight();
    }
}
