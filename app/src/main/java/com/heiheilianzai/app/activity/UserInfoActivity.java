package com.heiheilianzai.app.activity;

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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
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

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.R2;
import com.heiheilianzai.app.bean.UserInfoItem;
import com.heiheilianzai.app.config.ReaderApplication;
import com.heiheilianzai.app.config.ReaderConfig;
import com.heiheilianzai.app.eventbus.RefreshBookSelf;
import com.heiheilianzai.app.eventbus.RefreshMine;
import com.heiheilianzai.app.eventbus.RefreshUserInfo;
import com.heiheilianzai.app.http.ReaderParams;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ScreenSizeUtils;
import com.heiheilianzai.app.view.CircleImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
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

import static com.heiheilianzai.app.config.ReaderConfig.USE_WEIXIN;

//import com.squareup.okhttp.Request;
//import com.squareup.okhttp.Response;
//.http.RequestParams;

/**
 * 用户个人资料页
 */
public class UserInfoActivity extends BaseActivity implements View.OnClickListener, ShowTitle {
    private final String TAG = UserInfoActivity.class.getSimpleName();
    @BindView(R2.id.user_info_avatar_container)
    View user_info_avatar_container;
    @BindView(R2.id.user_info_avatar)
    CircleImageView user_info_avatar;

    @BindView(R2.id.user_info_nickname_container)
    View user_info_nickname_container;
    @BindView(R2.id.user_info_nickname)
    TextView user_info_nickname;

    @BindView(R2.id.user_info_uid)
    TextView user_info_uid;

    @BindView(R2.id.user_info_phone_container)
    View user_info_phone_container;
    @BindView(R2.id.user_info_phone)
    TextView user_info_phone;

    @BindView(R2.id.user_info_weixin_container)
    View user_info_weixin_container;
    @BindView(R2.id.user_info_weixin)
    TextView user_info_weixin;
    @BindView(R2.id.user_info_phone_jiantou)
    ImageView user_info_phone_jiantou;


    @BindView(R2.id.user_info_sex)
    TextView user_info_sex;
    @BindView(R2.id.user_info_nickname_sex)
    RelativeLayout user_info_nickname_sex;


    private EditText mEdit;
    private UserInfoItem mUserInfo;
    /**
     * 图片文件名字
     */

    private final int GALLERY = 1000000;
    private final int CAMERA = 1000001;

    @Override
    public int initContentView() {
        return R.layout.activity_user_info;
    }

    Gson gson = new Gson();

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
            //头像
            if (mUserInfo.getAvatar() != null) {
                ImageLoader.getInstance().displayImage(mUserInfo.getAvatar(), user_info_avatar, ReaderApplication.getOptions());
            }
            //昵称
            user_info_nickname.setText(mUserInfo.getNickname());
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

            // user_info_weixin.setText(mUserInfo.getBind_list().size() > 1 ? mUserInfo.getBind_list().get(1).getDisplay() : "");

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

        /** 从相册选择 */
        TextView checkImgGallery = view.findViewById(R.id.checkimg_gallery);
        /** 照相机照相 */
        TextView checkImgCamera = view.findViewById(R.id.checkimg_camera);
        /** 取消 */
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
                    modifyNickname(1);
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
                    modifyNickname(2);
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
     * 从相册中获取图片
     */
    protected void checkFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivityForResult(intent, GALLERY);
    }

    /**
     * 从照相机中获取图片
     */

    private File cameraSavePath;//拍照照片路径
    private Uri uri;//照片uri

    //激活相机操作
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
                //拍照  相册 上传图片
                checkUserImg(true);
                break;
            case R.id.user_info_nickname_sex:

                checkUserImg(false);
                break;
            case R.id.user_info_nickname_container:
                //修改昵称
                modifyNicknameDialog();
                break;
            case R.id.user_info_phone_container:
                //绑定手机号
                if (user_info_phone.getText().length() == 0) {
                    bindPhone();
                }

                break;
            case R.id.user_info_weixin_container:
                if (user_info_weixin.getText().length() == 0) {
                    UMShareAPI.get(activity).getPlatformInfo(activity, SHARE_MEDIA.WEIXIN, authListener);
                }
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
            Glide.with(activity).load(uri).into(user_info_avatar);

            // MyToash.Log("onActivityResult", uri);

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
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            uploadImg(file);
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
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

            Cursor cursor = getContentResolver().query(uri, proj, null, null,

                    null);

            int nPhotoColumn = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

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
     * 将图片转换成Base64编码的字符串
     *
     * @param file
     * @return base64编码的字符串
     */
    public static String imageToBase64(File file) {
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try {
            is = new FileInputStream(file);
            //创建一个字符流大小的数组。
            data = new byte[is.available()];
            //写入数组
            is.read(data);
            //用默认的编码格式进行编码
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
     * 上传图片
     */

    public void uploadImg(File file) {
        String info = "data:image/jpeg;base64," + imageToBase64(file);

        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("avatar", info);
        String json = params.generateParamsJson();
        ;
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetAvatarUrl, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        //  initData(true);
                        EventBus.getDefault().post(new RefreshMine(null));
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    /**
     * 修改昵称对话框
     */
    private void modifyNicknameDialog() {
        final Dialog dialog = new Dialog(this, R.style.NormalDialogStyle);
        View view = View.inflate(this, R.layout.dialog_modify_nickname, null);
        TextView cancel = view.findViewById(R.id.cancel);
        TextView confirm = view.findViewById(R.id.confirm);
        mEdit = view.findViewById(R.id.modify_nickname_edit);
        dialog.setContentView(view);
        dialog.setCanceledOnTouchOutside(true);
        //设置对话框的大小
        view.setMinimumHeight((int) (ScreenSizeUtils.getInstance(this).getScreenHeight() * 0.23f));
        Window dialogWindow = dialog.getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        lp.width = (int) (ScreenSizeUtils.getInstance(this).getScreenWidth() * 0.75f);
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.gravity = Gravity.CENTER;
        dialogWindow.setAttributes(lp);
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

                modifyNickname(0);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    /**
     * 修改昵称
     */
    public void modifyNickname(final int flag) {
        String requestParams;
        if (flag == 0) {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetNicknameUrl;
        } else {
            requestParams = ReaderConfig.getBaseUrl() + ReaderConfig.mUserSetGender;
        }
        ReaderParams params = new ReaderParams(this);
        if (flag == 0) {
            params.putExtraParams("nickname", mEdit.getText().toString());
        } else {
            params.putExtraParams("gender", flag + "");
        }
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(requestParams, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        initData(true);
                       /* if(flag==0) {
                            EventBus.getDefault().post(new RefreshMine());
                        }*/
                    }

                    @Override
                    public void onErrorResponse(String ex) {

                    }
                }

        );
    }

    /**
     * 绑定手机号
     */
    public void bindPhone() {
        startActivityForResult(new Intent(this, BindPhoneActivity.class), 111);
    }

    /**
     * 绑定微信，跳微信登录
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
                //  ToastUtils.toast("您手机尚未安装微信，请安装后再登录");
                return;
            }
            iwxapi.registerApp(ReaderConfig.WEIXIN_PAY_APPID);
            SendAuth.Req req = new SendAuth.Req();
            req.scope = "snsapi_userinfo";
            req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验

            iwxapi.sendReq(req);
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            //  Toast.makeText(LoginActivity.this, "失败：" + t.getMessage(), Toast.LENGTH_LONG).show();
            MyToash.Log("SHARE_MEDIA 2   " + t.getMessage());
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            //   Toast.makeText(LoginActivity.this, "取消了", Toast.LENGTH_LONG).show();
        }
    };

    Activity activity;

    public void getWeiXinAppUserInfo(final String str) {
        ReaderParams params = new ReaderParams(this);
        params.putExtraParams("info", str);
        String json = params.generateParamsJson();

        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.bind_wechat, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {

                        // MyToash.Log("bind_wechat",result);
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
                ImageLoader.getInstance().displayImage(mUserInfo.getAvatar(), user_info_avatar, ReaderApplication.getOptions());
            }
            //昵称
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

}