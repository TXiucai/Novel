package com.heiheilianzai.app.adapter.comic;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.ReaderBaseAdapter;
import com.heiheilianzai.app.model.comic.ComicComment;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

/**
 * 作品评论列表的adapter
 */
public class CommentAdapter extends ReaderBaseAdapter<ComicComment.Comment> {

    public CommentAdapter(Context context, List<ComicComment.Comment> list, int count, boolean close) {
        super(context, list, count, close);

    }

    @Override
    public View getOwnView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.activity_book_info_content_comment_item, null, false);
            viewHolder.imageView = convertView.findViewById(R.id.activity_book_info_content_comment_item_avatar);
            viewHolder.content = convertView.findViewById(R.id.activity_book_info_content_comment_item_content);
            viewHolder.replay = convertView.findViewById(R.id.activity_book_info_content_comment_item_reply_info);
            viewHolder.nickname = convertView.findViewById(R.id.activity_book_info_content_comment_item_nickname);
            viewHolder.time = convertView.findViewById(R.id.activity_book_info_content_comment_item_time);
            viewHolder.comment_item_isvip = convertView.findViewById(R.id.comment_item_isvip);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MyPicasso.IoadImage((Activity) mContext, mList.get(position).getAvatar(), R.mipmap.icon_def_head, viewHolder.imageView);
        viewHolder.content.setText(mList.get(position).getContent());
        viewHolder.replay.setText(mList.get(position).getReply_info());
        viewHolder.replay.setVisibility(TextUtils.isEmpty(mList.get(position).getReply_info()) ? View.GONE : View.VISIBLE);
        viewHolder.nickname.setText(mList.get(position).getNickname());
        viewHolder.time.setText(mList.get(position).getTime());
        viewHolder.comment_item_isvip.setVisibility(mList.get(position).getIs_vip()==1 ? View.VISIBLE : View.GONE);
        return convertView;
    }

    class ViewHolder {
        ImageView imageView;
        TextView content;
        TextView replay;
        TextView nickname;
        TextView time;
        View comment_item_isvip;
    }
}
