package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.sdk.android.ams.common.util.FileUtil;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.FeedBackTypeAdapter;
import com.heiheilianzai.app.adapter.ImagePickerAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ComplaitTypeBean;
import com.heiheilianzai.app.utils.FileUtils;
import com.heiheilianzai.app.utils.GlideImageLoader;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


/**
 * 个人中心-意见反馈
 * Created by scb on 2018/7/14.
 */
public class FeedBackActivity extends BaseActivity implements ShowTitle, ImagePickerAdapter.OnRecyclerViewItemClickListener {
    private final String TAG = FeedBackActivity.class.getSimpleName();
    /**
     * 意见内容
     */
    private EditText mEditTextContent;

    /**
     * "提交"外层布局
     */
    private TextView mSubmit;

    /**
     * "提交"外层布局
     */
    private LinearLayout comment_titlebar_add_feedback;

    @BindView(R.id.tabRecycler)
    RecyclerView mTbRecycleView;
    @BindView(R.id.recyclerView)
    RecyclerView mPicRecycleView;
    @BindView(R.id.iv)
    ImageView iv;

    private FeedBackTypeAdapter mTypeAdapter;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    private ImagePickerAdapter mImagePickerAdapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 6;               //允许选择图片最大数
    private int mCount = 0;
    private String mFaceBackImg;

    @Override
    public int initContentView() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        return R.layout.activity_feedback;
    }

    @Override
    public void initView() {
        initTitleBarView(LanguageUtil.getString(this, R.string.FeedBackActivity_title));
        initImagePicker();
        mEditTextContent = findViewById(R.id.activity_feedback_content);
        mEditTextContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        mSubmit = findViewById(R.id.submit);
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (int i = 0; i < selImageList.size(); i++) {
                    String bitmap = FileUtils.imageToBase64(selImageList.get(i).path);
                    addFeedback(bitmap);
                }
            }
        });

        comment_titlebar_add_feedback = findViewById(R.id.comment_titlebar_add_comment);
        comment_titlebar_add_feedback.setVisibility(View.GONE);

        mTbRecycleView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mTypeAdapter = new FeedBackTypeAdapter(this);
        mTbRecycleView.setAdapter(mTypeAdapter);

        //图片
        selImageList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        mImagePickerAdapter.setOnItemClickListener(this);
        mPicRecycleView.setLayoutManager(new GridLayoutManager(this, 3));
        mPicRecycleView.setHasFixedSize(true);
        mPicRecycleView.setAdapter(mImagePickerAdapter);
    }

    @Override
    public void initData() {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mFeedBackType, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ComplaitTypeBean complaitTypeBean = new Gson().fromJson(result, ComplaitTypeBean.class);
                        List<ComplaitTypeBean.ComplaitListBean> typeBeanList = complaitTypeBean.getList();
                        mTypeAdapter.setNewData(typeBeanList);
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    /**
     * 发请求
     */

    public void addFeedback(String bitmap) {
        if (!MainHttpTask.getInstance().Gotologin(FeedBackActivity.this)) {
            return;
        }
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("feed_back_img ", bitmap);

        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUpPicture, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                        MyToash.Log(result);
                        mCount = mCount + 1;
                        if (mCount == selImageList.size()) {
                            mFaceBackImg = result;
                            Toast.makeText(FeedBackActivity.this, "chenggong", Toast.LENGTH_SHORT);
                        } else {
                            mFaceBackImg = result + ",";
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );

    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
        MyToash.ToashSuccess(FeedBackActivity.this, "反馈成功");
        finish();

    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(false);                      //显示拍照按钮
        imagePicker.setCrop(true);                           //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(maxImgCount);              //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(v -> finish());
        mTitle.setText(text);
    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                //打开选择,本次允许选择的数量
                ImagePicker.getInstance().setSelectLimit(maxImgCount - selImageList.size());
                Intent intent1 = new Intent(FeedBackActivity.this, ImageGridActivity.class);
                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                break;
        }
    }

    @Override
    public void onItemDeleteClick(View view, int position) {
        selImageList.remove(position);
        if (selImageList != null) {
            mImagePickerAdapter.setImages(selImageList);
        }
    }

    ArrayList<ImageItem> images = null;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            //添加图片返回
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                images = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (images != null) {
                    selImageList.addAll(images);
                    mImagePickerAdapter.setImages(selImageList);
                }
            }
        }
    }
}