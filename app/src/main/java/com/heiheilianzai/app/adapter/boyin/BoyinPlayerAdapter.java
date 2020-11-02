package com.heiheilianzai.app.adapter.boyin;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.MyPicasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BoyinPlayerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<BoyinChapterBean> mBoyinChapterBeans;
    private Context mContext;
    private OnChapterItemListener onChapterItemListener;

    public void setOnChapterItemListener(OnChapterItemListener onChapterItemListener) {
        this.onChapterItemListener = onChapterItemListener;
    }

    public BoyinPlayerAdapter(List<BoyinChapterBean> mBoyinChapterBeans, Context mContext) {
        this.mBoyinChapterBeans = mBoyinChapterBeans;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_player_chapter,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        BoyinChapterBean boyinChapterBean = mBoyinChapterBeans.get(position);
        ViewHolder viewHolder= (ViewHolder) holder;
        viewHolder.mTvChapter.setText(boyinChapterBean.getChapter_name());
        viewHolder.mTvTime.setText(DateUtils.formatTime(boyinChapterBean.getChapter_play_time()));
        if (boyinChapterBean.isPlay()){
           MyPicasso.loadLocalImage((Activity) mContext,R.drawable.ic_speaker,viewHolder.mIvSpeaker);
        }else {
            MyPicasso.loadLocalImage((Activity) mContext,R.mipmap.ic_speaker3,viewHolder.mIvSpeaker);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onChapterItemListener!=null){
                    onChapterItemListener.onItemListener(boyinChapterBean,position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mBoyinChapterBeans.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.iv_speaker)
        public ImageView mIvSpeaker;
        @BindView(R.id.tv_chapter)
        public TextView mTvChapter;
        @BindView(R.id.tv_start_time)
        public TextView mTvTime;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
    }
    public interface OnChapterItemListener{
        void onItemListener(BoyinChapterBean boyinChapterBean,int position);
    }
}
