package com.heiheilianzai.app.ui.fragment.boyin;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.adapter.BaseReadHistoryAdapter;
import com.heiheilianzai.app.adapter.ReadHistoryRecyclerViewPhonicAdapter;
import com.heiheilianzai.app.base.BaseReadHistoryFragment;
import com.heiheilianzai.app.constant.BoyinConfig;
import com.heiheilianzai.app.model.boyin.PhonicReadHistory;
import com.heiheilianzai.app.model.event.SkipToBoYinEvent;
import com.heiheilianzai.app.ui.dialog.GetDialog;
import com.heiheilianzai.app.utils.LanguageUtil;
import com.heiheilianzai.app.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

/**
 * 有声阅读历史 Fragment
 */
public class ReadHistoryPhonicFragment extends BaseReadHistoryFragment<PhonicReadHistory.PhonicInfo> {

    @Override
    protected void initData() {
        dataUrl = BoyinConfig.PHONIC_AUDIO_READ_LOG;
        initdata();
    }

    @Override
    protected void initView() {
        mSonType = PhONIC_SON_TYPE;
        optionAdapter = new ReadHistoryRecyclerViewPhonicAdapter(activity, optionBeenList, getPosition);
        super.initView();
    }

    BaseReadHistoryAdapter.GetPosition getPosition = new BaseReadHistoryAdapter.GetPosition<PhonicReadHistory.PhonicInfo>() {
        @Override
        public void getPosition(int falg, PhonicReadHistory.PhonicInfo phonicInfo, int position) {
            switch (falg) {
                case 1:
                case 0:
                    SkipToBoYinEvent skipToBoYinEvent = new SkipToBoYinEvent(new SkipToBoYinEvent.PhonicSkipInfo(String.valueOf(phonicInfo.getLast_read_chapter_id()), String.valueOf(phonicInfo.getId()), phonicInfo.getName(), phonicInfo.getCurrent_time()));
                    EventBus.getDefault().post(skipToBoYinEvent);
                    getActivity().finish();
                    break;
                case 2:
                    GetDialog.IsOperation(activity, LanguageUtil.getString(activity, R.string.ReadHistoryFragment_qurenshanchu), "", new GetDialog.IsOperationInterface() {
                        @Override
                        public void isOperation() {
                            if (Utils.isLogin(activity)) {
                                delad(String.valueOf(phonicInfo.getId()), String.valueOf(phonicInfo.getLast_read_chapter_id()), BoyinConfig.PHONIC_REMOVE_AUDIO_READ_LOG);
                            }
                            deladItemRefresh(position);
                        }
                    });
                    break;
            }
        }
    };

    @Override
    public void handData(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            final PhonicReadHistory optionItem = gson.fromJson(jsonObj.getString("data"), PhonicReadHistory.class);
            int total_page = optionItem.total_page;
            int optionItem_list_size = optionItem.data_list.size();
            if (current_page <= total_page && optionItem_list_size != 0) {
                if (current_page == 1) {
                    optionBeenList.clear();
                    optionBeenList.addAll(optionItem.data_list);
                    size = optionItem_list_size;
                    optionAdapter.notifyDataSetChanged();
                } else {
                    optionBeenList.addAll(optionItem.data_list);
                    int t = size + optionItem_list_size;
                    optionAdapter.notifyItemRangeInserted(size + 2, optionItem_list_size);
                    size = t;
                }
                current_page = optionItem.current_page;
                ++current_page;
            } else {
                setNullDataView();
            }
        } catch (Exception e) {
            if (current_page == 1) {
                setNullDataView();
            }
        }
    }
}