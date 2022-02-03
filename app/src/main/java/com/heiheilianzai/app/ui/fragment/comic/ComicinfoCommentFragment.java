package com.heiheilianzai.app.ui.fragment.comic;

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
import com.heiheilianzai.app.base.BaseButterKnifeFragment;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.BaseAd;
import com.heiheilianzai.app.model.BookInfoComment;
import com.heiheilianzai.app.model.comic.StroreComicLable;
import com.heiheilianzai.app.ui.activity.ReplyCommentActivity;
import com.heiheilianzai.app.ui.activity.WebViewActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicCommentActivity;
import com.heiheilianzai.app.ui.activity.comic.ComicInfoActivity;
import com.heiheilianzai.app.utils.ImageUtil;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.AdaptionGridViewNoMargin;
import com.heiheilianzai.app.view.CircleImageView;
import com.heiheilianzai.app.view.ObservableScrollView;
import com.heiheilianzai.app.view.foldtextview.ExpandableTextView;

import java.util.List;

import butterknife.BindView;

/**
 * 漫画详情评论
 * Created by scb on 2018/6/9.
 */
public class ComicinfoCommentFragment extends BaseButterKnifeFragment {
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
    public ExpandableTextView etv;

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
    public int WIDTH, HEIGHT;

    StroreComicLable.Comic baseComic;

    public void senddata(StroreComicLable.Comic baseComic, List<BookInfoComment> bookInfoComments, StroreComicLable stroreComicLable, BaseAd baseAd) {
        MyToash.Log("http_utaa", bookInfoComments.toString());
        this.baseComic = baseComic;

        int viewWidth = getActivity().getWindowManager().getDefaultDisplay().getWidth()-ImageUtil.dp2px(getActivity(),20f);
        etv.initWidth(viewWidth);
        etv.setMaxLines(2);
        etv.setHasAnimation(false);
        etv.setCloseInNewLine(true);
        etv.setOpenSuffixColor(getResources().getColor(R.color.white));
        etv.setCloseSuffixColor(getResources().getColor(R.color.white));
        etv.setOriginalText(baseComic.description);

        if ( baseAd != null) {
            activity_book_info_ad.setVisibility(View.VISIBLE);
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
                            intent.putExtra("comic_id", baseComic.comic_id);
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
            if (baseComic.total_comment > 0) {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_lookpinglun);
            } else {
                moreText = LanguageUtil.getString(activity, R.string.BookInfoActivity_nopinglun);
            }
            LinearLayout commentMoreView = (LinearLayout) LayoutInflater.from(activity).inflate(R.layout.activity_book_info_content_comment_more, null, false);
            TextView activity_book_info_content_comment_more_text = commentMoreView.findViewById(R.id.activity_book_info_content_comment_more_text);
            activity_book_info_content_comment_more_text.setText(String.format(moreText, baseComic.total_comment + ""));
            activity_book_info_content_add_comment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(
                            new Intent(activity, ComicCommentActivity.class).
                                    putExtra("comic_id", baseComic.comic_id).
                                    putExtra("IsBook", false), 11);
                }
            });
            commentMoreView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivityForResult(new Intent(activity, ComicCommentActivity.class).putExtra("comic_id", baseComic.comic_id).putExtra("IsBook", false), 11);
                }
            });
            activity_book_info_content_comment_container.addView(commentMoreView);
            if (stroreComicLable != null && !stroreComicLable.list.isEmpty()) {
                List<StroreComicLable.Comic> comicList = stroreComicLable.list;
                View type1 = LayoutInflater.from(activity).inflate(R.layout.fragment_store_comic_layout, null, false);
                TextView lable = type1.findViewById(R.id.fragment_store_gridview1_text);
                lable.setText(stroreComicLable.label);
                LinearLayout fragment_store_gridview1_huanmore = type1.findViewById(R.id.fragment_store_gridview1_huanmore);
                fragment_store_gridview1_huanmore.setVisibility(View.GONE);
                AdaptionGridViewNoMargin fragment_store_gridview1_gridview = type1.findViewById(R.id.fragment_store_gridview1_gridview);
                StoreComicAdapter storeComicAdapter;
                fragment_store_gridview1_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String comic_id = comicList.get(position).comic_id;
                        activity.startActivity(ComicInfoActivity.getMyIntent(activity, LanguageUtil.getString(activity, R.string.refer_page_info) + " " + comic_id, comic_id));
                    }
                });
                fragment_store_gridview1_gridview.setNumColumns(3);
                int width = WIDTH / 3;
                int height = width * 4 / 3;
                double size = Math.min(6, comicList.size());
                storeComicAdapter = new StoreComicAdapter(comicList.subList(0, (int) size), activity, 2, width, height);
                fragment_store_gridview1_gridview.setAdapter(storeComicAdapter);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 20, 0, 0);
                params.height = height * (int) (Math.ceil(size / 3d)) + ImageUtil.dp2px(activity, 170);
                activity_book_info_content_label_container.addView(type1, params);
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
