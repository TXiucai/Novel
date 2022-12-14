package com.heiheilianzai.app.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.AddressBean;
import com.heiheilianzai.app.model.JsonBean;
import com.heiheilianzai.app.utils.GetJsonDataUtil;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class AddressActivity extends BaseButterKnifeActivity {
    @BindView(R.id.titlebar_text)
    public TextView mTittle;
    @BindView(R.id.titlebar_back)
    public LinearLayout mLlBack;
    @BindView(R.id.et_name)
    public EditText mEtName;
    @BindView(R.id.et_address)
    public EditText mEtAddress;
    @BindView(R.id.et_phone)
    public EditText mEtPhone;
    @BindView(R.id.et_postal)
    public EditText mEtPostal;
    @BindView(R.id.tx_address)
    public TextView mTxAdrress;
    @BindView(R.id.tx_save)
    public TextView mTxSave;
    @BindView(R.id.rl_address)
    public RelativeLayout mRlAddress;
    private Activity mActivity;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private Thread mThread;
    private List<JsonBean> mOptions1Items = new ArrayList<>();
    private ArrayList<ArrayList<String>> mOptions2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> mOptions3Items = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (mThread == null) {//????????????????????????????????????????????????
                        mThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // ?????????????????????????????????
                                initJsonData();
                            }
                        });
                        mThread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    break;
                case MSG_LOAD_FAILED:
                    MyToash.Toash(mActivity, getString(R.string.string_address_fail));
                    break;
            }
        }
    };
    private String mProince;
    private String mCity;
    private String mArea;
    private boolean mIsFist = true;

    @Override
    public int initContentView() {
        return R.layout.adrress_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        mTittle.setText(LanguageUtil.getString(activity, R.string.AddressActivity_title));
        getAddress();
        setListener();
    }

    private void setListener() {
        mTxSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddress();
            }
        });
        mLlBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mRlAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPickerView();
            }
        });
        mEtName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mTxSave.setBackground(getDrawable(R.drawable.shape_ff8350_20));
                mTxSave.setClickable(true);
            }
        });
        mEtPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mTxSave.setBackground(getDrawable(R.drawable.shape_ff8350_20));
                mTxSave.setClickable(true);

            }
        });
        mEtAddress.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mTxSave.setBackground(getDrawable(R.drawable.shape_ff8350_20));
                mTxSave.setClickable(true);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        mEtPostal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                mTxSave.setBackground(getDrawable(R.drawable.shape_ff8350_20));
                mTxSave.setClickable(true);

            }
        });
    }

    private void saveAddress() {
        String postalText = mEtPostal.getText().toString();
        String addressText = mEtAddress.getText().toString();
        String phoneText = mEtPhone.getText().toString();
        String nameText = mEtName.getText().toString();
        if (postalText.length() > 0 && addressText.length() > 0 && phoneText.length() > 0 && nameText.length() > 0 && mProince.length() > 0 && mCity.length() > 0 && mArea.length() > 0) {
            if (nameText.length() > 15) {
                MyToash.Toash(mActivity, getString(R.string.string_edit_address_error));
                return;
            }
            if (postalText.length() != 6) {
                MyToash.Toash(mActivity, getString(R.string.string_edit_address_error));
                return;
            }
            if (phoneText.length() != 11) {
                MyToash.Toash(mActivity, getString(R.string.string_edit_address_error));
                return;
            }
            final ReaderParams params = new ReaderParams(this);
            params.putExtraParams("province", mProince);
            params.putExtraParams("city", mCity);
            params.putExtraParams("town", mArea);
            params.putExtraParams("address_details", addressText.toString());
            params.putExtraParams("receiver_name", nameText.toString());
            params.putExtraParams("receiver_mobile", phoneText.toString());
            params.putExtraParams("post_code", postalText.toString());
            String json = params.generateParamsJson();
            String url;
            if (mIsFist) {
                url = ReaderConfig.ADD_ADDRESS;
            } else {
                url = ReaderConfig.EDIT_ADDRESS;
            }
            HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + url, json, true, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            MyToash.Toash(mActivity, getString(R.string.string_edit_address_success));
                            mTxSave.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
                            mTxSave.setClickable(false);
                            getAddress();
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        } else {
            MyToash.Toash(mActivity, getString(R.string.string_edit_address_error));
        }

    }

    private void getAddress() {
        final ReaderParams params = new ReaderParams(this);
        String json = params.generateParamsJson();
        HttpUtils.getInstance(this).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.ACCEPT_ADDRESS, json, true, new HttpUtils.ResponseListener() {
                    @Override
                    public void onResponse(final String result) {
                        try {
                            ShareUitls.putString(mActivity, "address", result);
                            AddressBean addressBean = new Gson().fromJson(result, AddressBean.class);
                            initAddress(addressBean);
                        } catch (Exception e) {
                            mIsFist = true;
                        }
                    }

                    @Override
                    public void onErrorResponse(String ex) {
                        mIsFist = true;
                    }
                }
        );
    }

    private void initAddress(AddressBean addressBean) {
        if (addressBean != null) {
            mIsFist = false;
            AddressBean.UserAddressBean user_address = addressBean.getUser_address();
            String receiver_name = user_address.getReceiver_name();
            mEtName.setText(receiver_name);
            String address_details = user_address.getAddress_details();
            mEtAddress.setText(address_details);
            String receiver_mobile = user_address.getReceiver_mobile();
            mEtPhone.setText(receiver_mobile);
            mProince = user_address.getProvince();
            mCity = user_address.getCity();
            mArea = user_address.getTown();
            String post_code = user_address.getPost_code();
            mEtPostal.setText(post_code);
            mTxAdrress.setText(mProince + mCity + mArea);
            mTxSave.setBackground(getDrawable(R.drawable.shape_e6e6e6_20));
            mTxSave.setClickable(false);
        }
    }

    private void showPickerView() {// ???????????????

        OptionsPickerView pvOptions = new OptionsPickerBuilder(this, new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //?????????????????????????????????????????????
                mProince = mOptions1Items.size() > 0 ?
                        mOptions1Items.get(options1).getPickerViewText() : "";

                mCity = mOptions2Items.size() > 0
                        && mOptions2Items.get(options1).size() > 0 ?
                        mOptions2Items.get(options1).get(options2) : "";

                mArea = mOptions2Items.size() > 0
                        && mOptions3Items.get(options1).size() > 0
                        && mOptions3Items.get(options1).get(options2).size() > 0 ?
                        mOptions3Items.get(options1).get(options2).get(options3) : "";

                String tx = mProince + mCity + mArea;
                mTxAdrress.setText(tx);
            }
        })

                .setTitleText("????????????")
                .setDividerColor(Color.BLACK)
                .setTextColorCenter(Color.BLACK) //???????????????????????????
                .setContentTextSize(20)
                .build();
        pvOptions.setPicker(mOptions1Items, mOptions2Items, mOptions3Items);//???????????????
        pvOptions.show();
    }

    private void initJsonData() {//????????????

        /**
         * ?????????assets ????????????Json??????????????????????????????????????????????????????
         * ???????????????????????????
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(this, "province.json");//??????assets????????????json????????????

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//???Gson ????????????

        /**
         * ??????????????????
         *
         * ???????????????????????????JavaBean????????????????????????????????? IPickerViewData ?????????
         * PickerView?????????getPickerViewText????????????????????????????????????
         */
        mOptions1Items = jsonBean;

        for (int i = 0; i < jsonBean.size(); i++) {//????????????
            ArrayList<String> cityList = new ArrayList<>();//????????????????????????????????????
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//??????????????????????????????????????????

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//??????????????????????????????
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//????????????
                ArrayList<String> city_AreaList = new ArrayList<>();//??????????????????????????????
                //??????????????????????????????????????????????????????????????????null ?????????????????????????????????????????????
                if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//??????????????????????????????
            }

            /**
             * ??????????????????
             */
            mOptions2Items.add(cityList);

            /**
             * ??????????????????
             */
            mOptions3Items.add(province_AreaList);
        }
        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);
    }

    public ArrayList<JsonBean> parseData(String result) {//Gson ??????
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
            mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
        }
        return detail;
    }
}
