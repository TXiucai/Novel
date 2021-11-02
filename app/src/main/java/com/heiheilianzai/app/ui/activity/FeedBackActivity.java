package com.heiheilianzai.app.ui.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.FeedBackSubTypeAdapter;
import com.heiheilianzai.app.adapter.FeedBackTypeAdapter;
import com.heiheilianzai.app.adapter.ImagePickerAdapter;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.component.task.MainHttpTask;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.ComplaitTypeBean;
import com.heiheilianzai.app.model.event.FeedSubTypeMode;
import com.heiheilianzai.app.utils.FileUtils;
import com.heiheilianzai.app.utils.GlideImageLoader;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.StringUtils;
import com.heiheilianzai.app.utils.ToastUtil;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

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
    @BindView(R.id.tabSubRecycler)
    RecyclerView mTabSubRecycleView;
    @BindView(R.id.iv)
    ImageView iv;
    @BindView(R.id.activity_feedback_content)
    EditText activity_feedback_content;

    private FeedBackTypeAdapter mTypeAdapter;
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    private ImagePickerAdapter mImagePickerAdapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private int maxImgCount = 6;               //允许选择图片最大数
    private int mCount = 0;
    private StringBuffer mFaceBackImg = new StringBuffer();
    private String mTagId;
    private List<ComplaitTypeBean.ComplaitListBean> mTypeBeanList;
    private FeedBackSubTypeAdapter mFeedBackSubTypeAdapter;
    private List<ComplaitTypeBean.ComplaitSubListBean> mSelectSubList;
    private List<FeedSubTypeMode> mSubTypeModeList = new ArrayList<>();

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
                if (selImageList.size() > 0) {
                    for (int i = 0; i < selImageList.size(); i++) {
                        String bitmap = "data:image/jpeg;base64," + FileUtils.imageToBase64(selImageList.get(i).path);
                        addFeedback(bitmap);
                    }
                } else {
                    putAllMessage("");
                }

            }
        });

        comment_titlebar_add_feedback = findViewById(R.id.comment_titlebar_add_comment);
        comment_titlebar_add_feedback.setVisibility(View.GONE);

        mTbRecycleView.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mTypeAdapter = new FeedBackTypeAdapter(this);
        mTbRecycleView.setAdapter(mTypeAdapter);

        mTabSubRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mFeedBackSubTypeAdapter = new FeedBackSubTypeAdapter();
        mTabSubRecycleView.setAdapter(mFeedBackSubTypeAdapter);

        mTypeAdapter.setOnItemClickListener((adapter, view, position) -> {
            mTypeAdapter.setCurrentPosition(position);
            if (!mTypeBeanList.isEmpty()) {
                mTagId = mTypeBeanList.get(position).getId();
                mSelectSubList = mTypeBeanList.get(position).getSubList();
                inintSubList();
                if (mSelectSubList != null && !mSelectSubList.isEmpty()) {
                    mTabSubRecycleView.setVisibility(View.VISIBLE);
                    mFeedBackSubTypeAdapter.setNewData(mSelectSubList);
                } else {
                    mTabSubRecycleView.setVisibility(View.GONE);
                }
            }
        });
        mFeedBackSubTypeAdapter.setmOnBackSubTypeListener(new FeedBackSubTypeAdapter.OnBackSubTypeListener() {
            @Override
            public void onBackSubType(String id, String type) {
                for (ComplaitTypeBean.ComplaitSubListBean model : mSelectSubList) {
                    if (TextUtils.equals(model.getId(), id)) {
                        model.setContent(type);
                    }
                }
                for (FeedSubTypeMode model : mSubTypeModeList) {
                    if (TextUtils.equals(model.getId(), id)) {
                        model.setContent(type);
                    }
                }
            }
        });
        //图片
        selImageList = new ArrayList<>();
        mImagePickerAdapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        mImagePickerAdapter.setOnItemClickListener(this);
        mPicRecycleView.setLayoutManager(new GridLayoutManager(this, 3));
        mPicRecycleView.setHasFixedSize(true);
        mPicRecycleView.setAdapter(mImagePickerAdapter);
    }

    private void inintSubList() {
        mSubTypeModeList.clear();
        if (mSelectSubList != null && !mSelectSubList.isEmpty()) {
            for (int i = 0; i < mSelectSubList.size(); i++) {
                FeedSubTypeMode feedSubTypeMode = new FeedSubTypeMode();
                ComplaitTypeBean.ComplaitSubListBean complaitSubListBean = mSelectSubList.get(i);
                feedSubTypeMode.setContent("");
                feedSubTypeMode.setId(complaitSubListBean.getId());
                mSubTypeModeList.add(feedSubTypeMode);
            }
        }
    }

    @Override
    public void initData() {
        if (!MainHttpTask.getInstance().Gotologin(FeedBackActivity.this)) {
            return;
        }
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mFeedBackType, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        ComplaitTypeBean complaitTypeBean = new Gson().fromJson(result, ComplaitTypeBean.class);
                        mTypeBeanList = complaitTypeBean.getList();
                        mTypeAdapter.setNewData(mTypeBeanList);
                        mTypeAdapter.setCurrentPosition(0);
                        mTagId = mTypeBeanList.get(0).getId();
                        mSelectSubList = mTypeBeanList.get(0).getSubList();
                        inintSubList();
                        if (mSelectSubList != null && !mSelectSubList.isEmpty()) {
                            mTabSubRecycleView.setVisibility(View.VISIBLE);
                            mFeedBackSubTypeAdapter.setNewData(mSelectSubList);
                        } else {
                            mTabSubRecycleView.setVisibility(View.GONE);
                        }
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
        params.putExtraParams("feed_back_img", bitmap);

        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUpPicture, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        String results = result.replace("\"", "");
                        mCount = mCount + 1;
                        if (mCount != selImageList.size()) {
                            mFaceBackImg.append(results + ",");
                        } else {
                            mFaceBackImg.append(results);
                            //提交全部类容
                            putAllMessage(mFaceBackImg.toString());
                            mFaceBackImg.delete(0, mFaceBackImg.length());
                            mCount = 0;
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );

    }

    private void putAllMessage(String feedBackImg) {
        if (!MainHttpTask.getInstance().Gotologin(FeedBackActivity.this)) {
            return;
        }
        if (TextUtils.isEmpty(activity_feedback_content.getText())) {
            ToastUtil.getInstance().showShortT(R.string.FeedBackActivity_some);
            return;
        }

        if (TextUtils.isEmpty(mTagId)) {
            ToastUtil.getInstance().showShortT(R.string.FeedBackActivity_some2);
            return;
        }
        if (mSelectSubList != null && !mSelectSubList.isEmpty()) {
            for (int i = 0; i < mSelectSubList.size(); i++) {
                ComplaitTypeBean.ComplaitSubListBean complaitSubListBean = mSelectSubList.get(i);
                String isNeed = complaitSubListBean.getIsNeed();
                if (TextUtils.equals(isNeed, "1") && StringUtils.isEmpty(complaitSubListBean.getContent())) {
                    ToastUtil.getInstance().showShortT(String.format(getString(R.string.FeedBackActivity_sub_type), complaitSubListBean.getTitle()));
                    return;
                }
            }
        }

        ReaderParams params = new ReaderParams(this);
        if (!TextUtils.isEmpty(feedBackImg)) {
            params.putExtraParams("feed_back_img", feedBackImg);
        }

        params.putExtraParams("quesion_type", mTagId);
        params.putExtraParams("content", activity_feedback_content.getText().toString() + "");
        params.putExtraParams("quesion_type_child", new Gson().toJson(mSubTypeModeList));
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUpAll, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        if (!TextUtils.isEmpty(feedBackImg)) {
                            mFaceBackImg.delete(0, mFaceBackImg.length());
                        }
                        mCount = 0;
                        try {
                            JSONObject jsonObject = new JSONObject(result);
                            String status = jsonObject.getString("status");
                            if (status.equals("1")) {
                                ToastUtil.getInstance().showShortT(R.string.FeedBackActivity_success);
                                finish();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mFaceBackImg.delete(0, mFaceBackImg.length());
                        mCount = 0;
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