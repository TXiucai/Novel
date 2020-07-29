package com.heiheilianzai.app.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.CommentItem;
import com.heiheilianzai.app.bean.PayGoldDetail;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by abc on 2017/4/28.
 */
public class MyCommentRecyclerViewAdapter extends RecyclerView.Adapter<MyCommentRecyclerViewAdapter.ViewHolder> {
    Activity activity;
    LayoutInflater layoutInflater;
    List<CommentItem> optionBeenList;


    public interface OnItemClick {
        void OnItemClick(int position, PayGoldDetail optionBeen);
    }
    OnItemClick onItemClick;
    public MyCommentRecyclerViewAdapter(Activity activity, List<CommentItem> optionBeenList, LayoutInflater layoutInflater, OnItemClick onItemClick) {
        this.activity = activity;
        this.optionBeenList = optionBeenList;
        this.onItemClick = onItemClick;
        this.layoutInflater = layoutInflater;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View convertView = layoutInflater.inflate(R.layout.activity_book_info_content_comment_item, null, false);
        return new ViewHolder(convertView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        CommentItem optionBeen = optionBeenList.get(position);
        MyPicasso.IoadImage(activity,optionBeen.getAvatar(), R.mipmap.icon_def_head,viewHolder.imageView);
        viewHolder.content.setText(optionBeen.getContent());
        viewHolder.replay.setText(optionBeen.getReply_info());
        viewHolder.replay.setVisibility(TextUtils.isEmpty(optionBeen.getReply_info()) ? View.GONE : View.VISIBLE);
        viewHolder.nickname.setText(optionBeen.getNickname());
        viewHolder.time.setText(optionBeen.getTime());
        viewHolder.comment_item_isvip.setVisibility(optionBeen.getIs_vip()==1 ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return optionBeenList.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R2.id.activity_book_info_content_comment_item_avatar)
        ImageView imageView;
        @BindView(R2.id.activity_book_info_content_comment_item_content)
        TextView content;
        @BindView(R2.id.activity_book_info_content_comment_item_reply_info)
        TextView replay;
        @BindView(R2.id.activity_book_info_content_comment_item_nickname)
        TextView nickname;
        @BindView(R2.id.activity_book_info_content_comment_item_time)
        TextView time;
        @BindView(R2.id.comment_item_isvip)
        View comment_item_isvip;
        public ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

    }
}



