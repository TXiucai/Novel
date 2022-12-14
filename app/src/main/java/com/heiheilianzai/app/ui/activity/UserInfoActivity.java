package com.heiheilianzai.app.ui.activity;

import static com.heiheilianzai.app.constant.ReaderConfig.USE_WEIXIN;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heiheilianzai.app.BuildConfig;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseActivity;
import com.heiheilianzai.app.callback.ShowTitle;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.UserInfoItem;
import com.heiheilianzai.app.model.event.RefreshBookSelf;
import com.heiheilianzai.app.model.event.RefreshMine;
import com.heiheilianzai.app.model.event.RefreshUserInfo;
import com.heiheilianzai.app.utils.FileUtils;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyPicasso;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.CircleImageView;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import butterknife.BindView;
import top.zibin.luban.CompressionPredicate;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * ?????????????????????
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener, ShowTitle {
    @BindView(R.id.user_info_avatar_container)
    View user_info_avatar_container;
    @BindView(R.id.user_info_password_container)
    View user_info_password_container;
    @BindView(R.id.user_info_avatar)
    CircleImageView user_info_avatar;
    @BindView(R.id.user_info_nickname_container)
    View user_info_nickname_container;
    @BindView(R.id.user_info_nickname)
    TextView user_info_nickname;
    @BindView(R.id.user_info_uid)
    TextView user_info_uid;
    @BindView(R.id.user_info_phone_container)
    View user_info_phone_container;
    @BindView(R.id.user_info_phone)
    TextView user_info_phone;
    @BindView(R.id.user_info_weixin_container)
    View user_info_weixin_container;
    @BindView(R.id.user_info_weixin)
    TextView user_info_weixin;
    @BindView(R.id.user_info_phone_jiantou)
    ImageView user_info_phone_jiantou;
    @BindView(R.id.user_info_sex)
    TextView user_info_sex;
    @BindView(R.id.user_info_nickname_sex)
    RelativeLayout user_info_nickname_sex;
    @BindView(R.id.user_info_password)
    TextView user_info_password;
    private EditText mEdit;
    private UserInfoItem mUserInfo;
    Gson gson = new Gson();
    Activity activity;
    /**
     * ???????????????????????????
     */
    private File cameraSavePath;//??????????????????
    private Uri uri;//??????uri
    /**
     * ??????????????????
     */
    private final int GALLERY = 1111;
    private final int CAMERA = 1112;
    private EditText mEtPassword;

    @Override
    public int initContentView() {
        return R.layout.activity_user_info;
    }

    @Override
    public void initView() {
        activity = this;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
        initTitleBarView(LanguageUtil.getString(activity, R.string.UserInfoActivity_title));
        if (!USE_WEIXIN) {
            user_info_weixin_container.setVisibility(View.GONE);
        }
        user_info_avatar_container.setOnClickListener(this);
        user_info_nickname_container.setOnClickListener(this);
        user_info_phone_container.setOnClickListener(this);
        user_info_weixin_container.setOnClickListener(this);
        user_info_nickname_sex.setOnClickListener(this);
        user_info_password_container.setOnClickListener(this);
    }


    @Override
    public void initData() {
        initData(false);
    }

    @Override
    public void initInfo(String json) {
        super.initInfo(json);
        try {
            mUserInfo = gson.fromJson(json, UserInfoItem.class);
            //??????
            if (mUserInfo.getAvatar() != null) {
                MyPicasso.GlideImageNoSize(activity, mUserInfo.getAvatar(), user_info_avatar);
            }
            //??????
            user_info_nickname.setText(mUserInfo.getNickname());
            user_info_password.setText(mUserInfo.getUser_default_password());
            if (mUserInfo.getGender() == 0) {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_weizhi));
            } else if (mUserInfo.getGender() == 2) {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_boy));
            } else {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_gril));
            }
            user_info_uid.setText(mUserInfo.getUid() + "");
            if (!mUserInfo.getBind_list().isEmpty()) {
                user_info_phone_jiantou.setVisibility(View.GONE);
                user_info_phone.setText(mUserInfo.getBind_list().get(0).getDisplay());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initData(final boolean flag) {
        ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUserInfoUrl, json, false, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initInfo(result);
                        if (flag) {
                            EventBus.getDefault().post(new RefreshMine(gson.fromJson(result, UserInfoItem.class)));
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void checkUserImg(final boolean flag) {
        final Dialog dialog = new Dialog(this, R.style.userInfo_avatar);
        View view = View.inflate(this, R.layout.user_img_dialog, null);
        /** ??????????????? */
        TextView checkImgGallery = view.findViewById(R.id.checkimg_gallery);
        /** ??????????????? */
        TextView checkImgCamera = view.findViewById(R.id.checkimg_camera);
        /** ?????? */
        View checkImgCancel = view.findViewById(R.id.checkimg_cancel);
        if (!flag) {
            checkImgGallery.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_gril));
            checkImgCamera.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_boy));
        }
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        checkImgGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (flag) {
                    String status = Environment.getExternalStorageState();
                    if (status.equals(Environment.MEDIA_MOUNTED)) {
                        checkFromGallery();
                    }
                } else {
                    modify(1);
                }
            }
        });
        checkImgCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                if (flag) {
                    checkFromCamera();
                } else {
                    modify(2);
                }
            }
        });
        checkImgCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * ????????????????????????
     */
    protected void checkFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivityForResult(intent, GALLERY);
    }

    //??????????????????
    private void checkFromCamera() {
        cameraSavePath = new File(getPath() + System.currentTimeMillis() + ".jpg");
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileProvider", cameraSavePath);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(cameraSavePath);
        }
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.user_info_avatar_container:
                //??????  ?????? ????????????
                checkUserImg(true);
                break;
            case R.id.user_info_nickname_sex:

                checkUserImg(false);
                break;
            case R.id.user_info_nickname_container:
                //????????????
                modifyNicknameDialog();
                break;
            case R.id.user_info_phone_container:
                //???????????????
                if (mUserInfo.getBind_list() != null && mUserInfo.getBind_list().get(0).getStatus() == 0) {
                    bindPhone();
                }
                break;
            case R.id.user_info_weixin_container:
                if (user_info_weixin.getText().length() == 0) {
                    UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, authListener);
                }
                break;
            case R.id.user_info_password_container:
                //????????????
                modifyPasswordDialog();
                break;
        }
    }

    @Override
    public void initTitleBarView(String text) {
        LinearLayout mBack;
        TextView mTitle;
        mBack = findViewById(R.id.titlebar_back);
        mTitle = findViewById(R.id.titlebar_text);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitle.setText(text);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        MyToash.Log("onActivityResult", requestCode + "  " + resultCode);
        if (requestCode == 111) {
            initData();
        } else {//uri
            if (resultCode == RESULT_OK) {
                if (requestCode == CAMERA) {
                    Handle(cameraSavePath.getAbsolutePath());
                } else if (requestCode == GALLERY) {
                    Uri uri = data.getData();
                    Handle(getImagePath(uri));
                }
            }
        }
    }

    private void Handle(String uri) {
        try {
            if (activity != null && !activity.isFinishing()) {
                Glide.with(activity).load(uri).into(user_info_avatar);
            }
            Luban.with(this)
                    .load(uri)
                    .ignoreBy(100)
                    .setTargetDir(getPath())
                    .filter(new CompressionPredicate() {
                        @Override
                        public boolean apply(String path) {
                            return !(TextUtils.isEmpty(path) || path.toLowerCase().endsWith(".gif"));
                        }
                    })
                    .setCompressListener(new OnCompressListener() {
                        @Override
                        public void onStart() {
                            // TODO ???????????????????????????????????????????????? loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO ??????????????????????????????????????????????????????
                            uploadImg(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO ????????????????????????????????????
                        }
                    }).launch();
        } catch (Exception e) {
        }
    }

    private String getImagePath(Uri uri) {
        if (null == uri) {
            MyToash.Log("getImagePath", "uri return null");
            return null;
        }
        MyToash.Log("getImagePath", uri.toString());
        String path = null;

        final String scheme = uri.getScheme();
        if (null == scheme) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            path = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
            int nPhotoColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            if (null != cursor) {
                cursor.moveToFirst();
                path = cursor.getString(nPhotoColumn);
            }
            cursor.close();
        }
        return path;
    }

    private String getPath() {
        String path = Environment.getExternalStorageDirectory() + "/heiheilianzai/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }

    /**
     * ??????????????????Base64??????????????????
     *
     * @param file
     * @return base64??????????????????
     */
    public static String imageToBase64(File file) {
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(file);
            //???????????????????????????????????????
            data = new byte[is.available()];
            //????????????
            is.read(data);
            //????????????????????????????????????
            result = Base64.encodeToString(data, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    /**
     * ????????????
     */
    public void uploadImg(File file) {
        String info = "data:image/jpeg;base64," + imageToBase64(file);
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("avatar", info);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetAvatarUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        EventBus.getDefault().post(new RefreshMine(null));
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    private void modifyPasswordDialog() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_modify_password, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        mEtPassword = view.findViewById(R.id.modify_password_edit);
        EditText etPasswordSure = view.findViewById(R.id.modify_password_edit_sure);
        TextView txPasswordError = view.findViewById(R.id.modify_password_error);
        TextView txPasswordErrorSure = view.findViewById(R.id.modify_password_error_sure);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //????????????????????????
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        String math = "[a-zA-Z\\d]{4,20}";
        mEtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(math)) {
                    txPasswordError.setVisibility(View.INVISIBLE);
                    if (etPasswordSure.getText().toString() != null && TextUtils.equals(mEtPassword.getText().toString(), etPasswordSure.getText().toString())) {
                        confirm.setBackground(getDrawable(BuildConfig.free_charge ? R.drawable.shape_read_bg : R.drawable.shape_ff8350_20));
                        confirm.setClickable(true);
                    } else {
                        confirm.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                        confirm.setClickable(false);
                    }
                }
            }
        });
        etPasswordSure.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(math)) {
                    txPasswordErrorSure.setVisibility(View.INVISIBLE);
                    if (mEtPassword.getText().toString() != null && TextUtils.equals(mEtPassword.getText().toString(), etPasswordSure.getText().toString())) {
                        confirm.setBackground(getDrawable(BuildConfig.free_charge ? R.drawable.shape_read_bg : R.drawable.shape_ff8350_20));
                        confirm.setClickable(true);
                    } else {
                        confirm.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                        confirm.setClickable(false);
                    }
                }
            }
        });
        mEtPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!mEtPassword.getText().toString().matches(math)) {
                        txPasswordError.setVisibility(View.VISIBLE);
                        txPasswordError.setText(activity.getResources().getString(R.string.string_password_error));
                    }
                }
            }
        });
        etPasswordSure.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.equals(etPasswordSure.getText().toString(), mEtPassword.getText().toString())) {
                        txPasswordErrorSure.setVisibility(View.VISIBLE);
                        txPasswordErrorSure.setText(activity.getResources().getString(R.string.string_password_error_sure));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!TextUtils.equals(mEtPassword.getText().toString(), etPasswordSure.getText().toString())) {
                    txPasswordErrorSure.setVisibility(View.VISIBLE);
                    txPasswordErrorSure.setText(activity.getResources().getString(R.string.string_password_error_sure));
                    confirm.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                    confirm.setClickable(false);
                    return;
                }
                modify(3);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * ?????????????????????
     */
    private void modifyNicknameDialog() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_modify_nickname, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        mEdit = view.findViewById(R.id.modify_nickname_edit);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //????????????????????????
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
        String name = mEdit.getText().toString();
        String math = "[\\u4e00-\\u9fa5_a-zA-Z0-9_]{2,20}";
        mEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().matches(math) && FileUtils.isSimpleOrComplex(name)) {
                    confirm.setBackground(getDrawable(BuildConfig.free_charge ? R.drawable.shape_read_bg : R.drawable.shape_ff8350_20));
                    confirm.setClickable(true);
                }
            }
        });
        mEdit.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (name.matches(math) && FileUtils.isSimpleOrComplex(name)) {
                        confirm.setBackground(getDrawable(BuildConfig.free_charge ? R.drawable.shape_read_bg : R.drawable.shape_ff8350_20));
                        confirm.setClickable(true);
                    } else {
                        confirm.setBackground(getDrawable(BuildConfig.free_charge ? R.drawable.shape_read_bg : R.drawable.shape_e6e6e6_20));
                        confirm.setClickable(false);
                        MyToash.Toash(activity, activity.getResources().getString(R.string.string_register_error));
                    }
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mEdit.getText().toString())) {
                    MyToash.ToashError(activity, LanguageUtil.getString(activity, R.string.UserInfoActivity_namenonull));
                    return;
                }
                modify(0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * ????????????
     */
    public void modify(final int flag) {
        String requestParams;
        if (flag == 0) {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetNickname;
        } else if (flag == 3) {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetPassword;
        } else {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetGender;
        }
        ReaderParams params = new ReaderParams(this);
        if (flag == 0) {
            params.putExtraParams("user_name", mEdit.getText().toString());
        } else if (flag == 3) {
            params.putExtraParams("user_password", mEtPassword.getText().toString());
        } else {
            params.putExtraParams("gender", flag + "");
        }
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(requestParams, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initData(true);
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    /**
     * ???????????????
     */
    public void bindPhone() {
        startActivityForResult(new Intent(this, BindPhoneActivity.class), 111);
    }

    /**
     * ??????????????????????????????
     */
    public IWXAPI iwxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iwxapi = WXAPIFactory.createWXAPI(this, ReaderConfig.WEIXIN_PAY_APPID, true);
        iwxapi.registerApp(ReaderConfig.WEIXIN_PAY_APPID);
    }

    UMAuthListener authListener = new UMAuthListener() {
        @Override
        public void onStart(SHARE_MEDIA platform) {
            MyToash.Log("SHARE_MEDIA1   " + platform.toString());
        }

        @Override
        public void onComplete(SHARE_MEDIA platform, int action, Map<String, String> data) {
            if (iwxapi == null) {
                iwxapi = WXAPIFactory.createWXAPI(activity, ReaderConfig.WEIXIN_PAY_APPID, true);
            }
            if (!iwxapi.isWXAppInstalled()) {
                return;
            }
            iwxapi.registerApp(ReaderConfig.WEIXIN_PAY_APPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_xb_live_state";//????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????csrf?????????????????????????????????????????????????????????????????????????????????????????????????????????session????????????
            iwxapi.sendReq(req);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            MyToash.Log("SHARE_MEDIA 2   " + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
        }
    };

    public void getWeiXinAppUserInfo(final String str) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("info", str);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.bind_wechat, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initInfo(result);
                        MyToash.ToashSuccess(activity, LanguageUtil.getString(activity, R.string.UserInfoActivity_bangdingyes));
                        EventBus.getDefault().post(new RefreshBookSelf(null));
                        EventBus.getDefault().post(new RefreshMine(gson.fromJson(result, UserInfoItem.class)));
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                    }
                }
        );
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refresh(RefreshUserInfo refreshUserInfo) {
        mUserInfo = refreshUserInfo.UserInfo;
        new UpdateData().invoke();
    }

    private class UpdateData {
        public void invoke() {
            if (mUserInfo.getAvatar() != null) {
                MyPicasso.GlideImageNoSize(activity, mUserInfo.getAvatar(), user_info_avatar);
            }
            //??????
            user_info_nickname.setText(mUserInfo.getNickname());
            if (mUserInfo.getGender() == 0) {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_weizhi));
            } else if (mUserInfo.getGender() == 2) {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_boy));
            } else {
                user_info_sex.setText(LanguageUtil.getString(activity, R.string.UserInfoActivity_gril));
            }
            user_info_uid.setText(mUserInfo.getUid() + "");
            user_info_phone.setText(mUserInfo.getBind_list().size() > 0 ? mUserInfo.getBind_list().get(0).getDisplay() : "");
            user_info_weixin.setText(mUserInfo.getBind_list().size() > 1 ? mUserInfo.getBind_list().get(1).getDisplay() : "");
        }
    }

    public static Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = true;//???????????????
    }
}