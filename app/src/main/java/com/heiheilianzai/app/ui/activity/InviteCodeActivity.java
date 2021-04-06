package com.heiheilianzai.app.ui.activity;


import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.base.BaseButterKnifeActivity;
import com.heiheilianzai.app.component.http.ReaderParams;
import com.heiheilianzai.app.constant.ReaderConfig;
import com.heiheilianzai.app.model.event.InviteCodeEvent;
import com.heiheilianzai.app.utils.HttpUtils;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.MyToash;
import com.heiheilianzai.app.utils.ShareUitls;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.OnClick;

public class InviteCodeActivity extends BaseButterKnifeActivity {
    @BindView(R.id.et_invite)
    public EditText mEtInvite;
    @BindView(R.id.tx_submit)
    public TextView mTxSubmit;
    @BindView(R.id.titlebar_text)
    public TextView mTxTittle;
    @Override
    public int initContentView() {
        return R.layout.activity_invite_code;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTxTittle.setText(LanguageUtil.getString(activity, R.string.invite_title));
    }

    @OnClick(value = {R.id.titlebar_back,R.id.tx_submit})
    public void getEvent(View view) {
        switch (view.getId()) {
            case R.id.titlebar_back:
                finish();
                break;
            case R.id.tx_submit:
                subimit(mEtInvite.getText().toString());
                break;
        }
    }

    private void subimit(String inviteCode) {
        if (inviteCode!=null&&inviteCode.length()>0){
            final ReaderParams params = new ReaderParams(activity);
            params.putExtraParams("invite_code",inviteCode);
            String json = params.generateParamsJson();
            HttpUtils.getInstance(activity).sendRequestRequestParams3(ReaderConfig.getBaseUrl() + ReaderConfig.sIginInVite, json, true, new HttpUtils.ResponseListener() {
                        @Override
                        public void onResponse(final String result) {
                            ShareUitls.putBoolean(activity, "invite_code", true);
                            EventBus.getDefault().post(new InviteCodeEvent(true));
                            MyToash.Toash(activity,LanguageUtil.getString(activity, R.string.invite_code_sucecess));
                            finish();
                        }

                        @Override
                        public void onErrorResponse(String ex) {
                        }
                    }
            );
        }else {
            MyToash.Toash(activity,LanguageUtil.getString(activity, R.string.invite_no_input));
        }

    }
}