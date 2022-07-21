package com.heiheilianzai.app.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.heiheilianzai.app.ChannelAdapter;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseWarmStartActivity;
import com.heiheilianzai.app.model.ChannelBean;
import com.heiheilianzai.app.view.AndroidWorkaround;
import com.jaeger.library.StatusBarUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChannelActivity extends BaseWarmStartActivity {
    @BindView(R.id.rl_channel_head)
    public RelativeLayout mRlHead;
    @BindView(R.id.img_back)
    public ImageView mImgBack;
    @BindView(R.id.ry_channel)
    public RecyclerView mRyChannel;
    private Activity mActivity;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
        //侵染状态栏
        StatusBarUtil.setTransparent(this);
        setContentView(R.layout.activity_channel);
        ButterKnife.bind(this);
        if (AndroidWorkaround.checkDeviceHasNavigationBar(this)) {                                  //适配华为手机虚拟键遮挡tab的问题
            AndroidWorkaround.assistActivity(findViewById(android.R.id.content));                   //需要在setContentView()方法后面执行
        }

        Bundle extras = getIntent().getExtras();
        int produce = extras.getInt("PRODUCE", 0);
        ChannelBean channelBean = (ChannelBean) extras.getSerializable("CHANNEL");
        if (produce == 1) {
            mRlHead.setBackground(getResources().getDrawable(R.mipmap.channel_novel_background));
        } else if (produce == 2) {
            mRlHead.setBackground(getResources().getDrawable(R.mipmap.channel_comic_background));
        } else {
            mRlHead.setBackground(getResources().getDrawable(R.mipmap.channel_cartoon_background));
        }
        mImgBack.setOnClickListener((v -> finish()));
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, 3);
        mRyChannel.setItemAnimator(null);
        mRyChannel.setLayoutManager(gridLayoutManager);
        ChannelAdapter channelAdapter = new ChannelAdapter(channelBean.getList(), mActivity, 1);
        mRyChannel.setAdapter(channelAdapter);
        channelAdapter.setOnChannelItemClickListener(new ChannelAdapter.OnChannelItemClickListener() {
            @Override
            public void onChannelItemClick(ChannelBean.ListBean item, int positon) {
                Intent intent = new Intent();
                intent.putExtra("CHANNEL", item);
                intent.putExtra("PRODUCE", produce);
                intent.putExtra("POSITION", positon);
                setResult(2, intent);
                finish();
            }
        });
    }
}
