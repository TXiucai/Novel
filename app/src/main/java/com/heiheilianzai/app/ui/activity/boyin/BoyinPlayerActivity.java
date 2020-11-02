package com.heiheilianzai.app.ui.activity.boyin;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.boyin.BoyinPlayerAdapter;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.model.boyin.BoyinInfoBean;
import com.heiheilianzai.app.model.event.BoyinPlayerEvent;
import com.heiheilianzai.app.utils.DateUtils;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.boyinplayer.MediaPlayFunctionListener;
import com.heiheilianzai.app.utils.boyinplayer.MediaPlayInfoListener;
import com.heiheilianzai.app.utils.boyinplayer.MediaPlayerUtils;
import com.heiheilianzai.app.view.CircleImageView;

import org.greenrobot.eventbus.EventBus;
import org.litepal.LitePal;
import org.litepal.crud.callback.FindCallback;
import org.litepal.crud.callback.FindMultiCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

public class BoyinPlayerActivity extends BaseButterKnifeActivity implements MediaPlayFunctionListener, MediaPlayInfoListener, SeekBar.OnSeekBarChangeListener {
    @BindView(R.id.titlebar_text)
    public TextView mTvTittle;
    @BindView(R.id.tv_chapter)
    public TextView mTvChapter;
    @BindView(R.id.iv_icon)
    public CircleImageView mIvIcon;
    @BindView(R.id.tv_start_time)
    public TextView mTvStartTime;
    @BindView(R.id.tv_end_time)
    public TextView mTvEndTime;
    @BindView(R.id.seekbar_progress)
    public SeekBar mSbProgress;
    @BindView(R.id.iv_last)
    public ImageView mIvLast;
    @BindView(R.id.iv_status)
    public ImageView mIvStatus;
    @BindView(R.id.iv_next)
    public ImageView mIvNext;
    @BindView(R.id.ry_chapter)
    public RecyclerView mRyChapter;
    private String mNid;
    private MediaPlayerUtils mMediaPlayerUtils;
    private BoyinChapterBean mChooseChapterBean;
    private boolean mIsPlay = true;
    private boolean mIsResume = false;//是否继续
    private int mCureentIndex = 0;
    private int mPreIndex = 0;
    private boolean mIsFirstPlay = true;
    private List<BoyinChapterBean> mBoyinChapterBeans;
    private ObjectAnimator mObjectAnimator;
    private BoyinPlayerAdapter mBoyinPlayerAdapter;

    @Override
    public int initContentView() {
        return R.layout.activity_boyinplayer;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().post(new BoyinPlayerEvent(true));
        initView();
        initAnimation();
        mMediaPlayerUtils = new MediaPlayerUtils();
        mMediaPlayerUtils.setMediaPlayFunctionListener(this);
        mMediaPlayerUtils.setMediaPlayInfoListener(this);
        mSbProgress.setOnSeekBarChangeListener(this);
        mBoyinChapterBeans = new ArrayList<>();
        mBoyinPlayerAdapter = new BoyinPlayerAdapter(mBoyinChapterBeans, BoyinPlayerActivity.this);
        mRyChapter.setLayoutManager(new LinearLayoutManager(BoyinPlayerActivity.this));
        mRyChapter.setAdapter(mBoyinPlayerAdapter);
        mBoyinPlayerAdapter.setOnChapterItemListener(new BoyinPlayerAdapter.OnChapterItemListener() {
            @Override
            public void onItemListener(BoyinChapterBean boyinChapterBean, int position) {
                if (mIsFirstPlay) {
                    mPreIndex = mCureentIndex;
                    mChooseChapterBean = boyinChapterBean;
                    mCureentIndex = position;
                    mIsPlay = true;
                    mIsResume = false;
                    startOrpause();
                } else {
                    if (mCureentIndex != position) {
                        mPreIndex = mCureentIndex;
                        mChooseChapterBean = boyinChapterBean;
                        mCureentIndex = position;
                        if (mMediaPlayerUtils.isRunning()) {
                            mMediaPlayerUtils.stop();
                        }
                        mIsPlay = true;
                        mIsResume = false;
                        setChapterInfo();
                        startOrpause();
                    }
                }
                MyToash.Log("FileDownloader", "mIsFirstPlay:" + mIsFirstPlay + "   mCurentIndex:" + mCureentIndex + "    mPreIndex:" + mPreIndex);
            }
        });
    }

    @SuppressLint("ObjectAnimatorBinding")
    private void initAnimation() {
        mObjectAnimator = ObjectAnimator.ofFloat(mIvIcon, "rotation", 0f, 360f);
        mObjectAnimator.setDuration(20000);//设置动画时间
        mObjectAnimator.setInterpolator(new LinearInterpolator());//动画时间线性渐变
        mObjectAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mObjectAnimator.setRepeatMode(ObjectAnimator.RESTART);
    }

    @OnClick(value = {R.id.back, R.id.iv_last, R.id.iv_status, R.id.iv_next})
    public void clickLisener(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.iv_last:
                palyLast();
                break;
            case R.id.iv_status:
                startOrpause();
                break;
            case R.id.iv_next:
                playNext();
                break;
        }
    }

    private void palyLast() {
        mPreIndex = mCureentIndex;
        mCureentIndex--;
        if (mCureentIndex >= 0 && mBoyinChapterBeans != null && mBoyinChapterBeans.size() > 0) {
            if (mMediaPlayerUtils.isRunning()) {
                mMediaPlayerUtils.stop();
            }
            mChooseChapterBean = mBoyinChapterBeans.get(mCureentIndex);
            setChapterInfo();
            mIsPlay = true;
            mIsResume = false;
            startOrpause();
        } else {
            mCureentIndex = 0;
            MyToash.Toash(this, getString(R.string.no_last_chapter));
        }
    }

    private void playNext() {
        mPreIndex = mCureentIndex;
        mCureentIndex++;
        if (mCureentIndex >= 0 && mBoyinChapterBeans != null && mBoyinChapterBeans.size() > 0 && mCureentIndex < mBoyinChapterBeans.size()) {
            if (mMediaPlayerUtils.isRunning()) {
                mMediaPlayerUtils.stop();
            }
            mChooseChapterBean = mBoyinChapterBeans.get(mCureentIndex);
            setChapterInfo();
            mIsPlay = true;
            mIsResume = false;
            startOrpause();
        } else {
            mCureentIndex--;
            MyToash.Toash(this, getString(R.string.no_next_chapter));
        }
    }

    private void setChapterInfo() {
        mTvChapter.setText(mChooseChapterBean.getChapter_name());
        mTvEndTime.setText(DateUtils.formatTime(mChooseChapterBean.getChapter_play_time()));
    }


    private void initView() {
        Intent intent = getIntent();
        mNid = intent.getStringExtra("nid");
        LitePal.where("nid = ?", mNid).findFirstAsync(BoyinInfoBean.class).listen(new FindCallback<BoyinInfoBean>() {
            @Override
            public void onFinish(BoyinInfoBean boyinInfoBean) {
                if (boyinInfoBean != null) {
                    MyPicasso.GlideImageNoSize(BoyinPlayerActivity.this, boyinInfoBean.getImg(), mIvIcon);
                    mTvTittle.setText(boyinInfoBean.getName());
                }
            }
        });

        LitePal.where("nid = ?  and downloadstatus = ?", "1", mNid).findAsync(BoyinChapterBean.class).listen(new FindMultiCallback<BoyinChapterBean>() {
            @Override
            public void onFinish(List<BoyinChapterBean> list) {
                if (list != null && list.size() > 0) {
                    mBoyinChapterBeans.clear();
                    mBoyinChapterBeans.addAll(list);
                    mCureentIndex = 0;
                    mChooseChapterBean = list.get(0);
                    setChapterInfo();
                    mBoyinPlayerAdapter.notifyDataSetChanged();
                }
            }
        });
        mSbProgress.setMax(100);
    }

    public void startOrpause() {
        if (mIsPlay) {
            if (mChooseChapterBean != null) {
                mIvStatus.setImageResource(R.mipmap.ic_play);
                if (mIsResume) {
                    mChooseChapterBean.setPlay(true);
                    mBoyinPlayerAdapter.notifyItemChanged(mCureentIndex);
                    mIsResume = false;
                    mMediaPlayerUtils.resume();
                    mObjectAnimator.resume();
                } else {
                    mMediaPlayerUtils.setFilePlay(new File(mChooseChapterBean.getSavePath()));
                    mMediaPlayerUtils.start();
                    mIsResume = true;
                }
                mIsPlay = false;
            }
        } else {
            mIvStatus.setImageResource(R.mipmap.ic_stop);
            if (mMediaPlayerUtils.isPlaying()) {
                mMediaPlayerUtils.pause();
            }
            mIsPlay = true;
        }
        mIsFirstPlay = false;
        MyToash.Log("FileDownloader", "播放状态    mIsResume：" + mIsResume + "      mIsPlay:" + mIsPlay);
    }

    @Override
    public void prepared() {
        MyToash.Log("FileDownloader", "准备完毕自动开始播放");
    }

    @Override
    public void start() {
        //开始播放
        //设置进度条最大值
        mSbProgress.setMax(mMediaPlayerUtils.getDuration());
        mChooseChapterBean.setPlay(true);
        mBoyinPlayerAdapter.notifyItemChanged(mCureentIndex);
        mIsResume = false;
        mObjectAnimator.start();
        MyToash.Log("FileDownloader", "开始播放 mCurrent:" + mCureentIndex);
    }

    @Override
    public void pause() {
        mChooseChapterBean.setPlay(false);
        mBoyinPlayerAdapter.notifyItemChanged(mCureentIndex);
        mIsResume = true;
        mObjectAnimator.pause();
        MyToash.Log("FileDownloader", "暂停播放 mCurrent:" + mCureentIndex);
    }

    @Override
    public void stop() {
        mTvStartTime.setText(DateUtils.formatTime(0));
        mBoyinChapterBeans.get(mPreIndex).setPlay(false);
        mBoyinPlayerAdapter.notifyItemChanged(mPreIndex);
        mIsResume = true;
        mObjectAnimator.end();
        MyToash.Log("FileDownloader", "结束播放 mPreIndex:" + mPreIndex);
    }

    @Override
    public void reset() {
        MyToash.Log("FileDownloader", "rest播放");
    }


    @Override
    public void onError(MediaPlayer mp, int what, int extra) {
        MyToash.Log("FileDownloader", "播放错误");
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MyToash.Log("FileDownloader", "播放完成");
        playNext();
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mediaPlayer, int i) {

    }

    @Override
    public void onSeekComplete(MediaPlayer mediaPlayer) {
        MyToash.Log("FileDownloader", "指定位置播放完成");
    }

    @Override
    public void onSeekBarProgress(int progress) {
        if (!mIsResume) {
            mSbProgress.setProgress(progress);
            mTvStartTime.setText(DateUtils.formatTime(progress / 1000));
        }
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        //拖动条进度改变的时候调用
        int time = seekBar.getProgress() / 1000;
        mTvStartTime.setText(DateUtils.formatTime(time));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //拖动条开始拖动的时候调用

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //拖动条停止拖动的时候调用
        mChooseChapterBean.setPlay(true);
        mBoyinPlayerAdapter.notifyItemChanged(mCureentIndex);
        if (mIsPlay) {
            mIvStatus.setImageResource(R.mipmap.ic_play);
            if (mIsResume) {
                mChooseChapterBean.setPlay(true);
                mBoyinPlayerAdapter.notifyItemChanged(mCureentIndex);
                mIsResume = false;
                mMediaPlayerUtils.resume();
                mObjectAnimator.resume();
            } else {
                mMediaPlayerUtils.setFilePlay(new File(mChooseChapterBean.getSavePath()));
                mMediaPlayerUtils.start();
                mIsResume = true;
            }
            mIsPlay = false;
        }
        mIsFirstPlay = false;
        mMediaPlayerUtils.seekTo(seekBar.getProgress());
        MyToash.Log("FileDownloader", "seekbar weizhi :" + seekBar.getProgress() + "    时间播放：" + seekBar.getProgress());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMediaPlayerUtils != null) {
            mMediaPlayerUtils.destory();
        }
        EventBus.getDefault().post(new BoyinPlayerEvent(false));
    }
}
