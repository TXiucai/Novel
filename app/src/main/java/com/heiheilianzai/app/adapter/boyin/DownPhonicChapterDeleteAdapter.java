package com.heiheilianzai.app.adapter.boyin;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.utils.LanguageUtil;

import java.util.List;

/**
 * 有声小说章节删除 Adapter
 */
public class DownPhonicChapterDeleteAdapter extends BasePhonicDownAdapter {

    public DownPhonicChapterDeleteAdapter(Activity activity, List<BoyinChapterBean> comicDownOptionList, TextView activity_comicdown_choose_count, TextView activity_comicdown_down) {
        super(activity, comicDownOptionList, activity_comicdown_choose_count, activity_comicdown_down);
    }

    @Override
    View getMyView(int position, View convertView, ViewGroup parent, ViewHolder viewHolder,BoyinChapterBean info) {
        if (mChooseBoyinList.contains(info)) {
            viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_maincolor);
            viewHolder.item_comicdownoption_text.setTextColor(Color.WHITE);
        } else {
            viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_white);
            viewHolder.item_comicdownoption_text.setTextColor(Color.BLACK);
        }
        viewHolder.item_comicdownoption_downstatus.setVisibility(View.VISIBLE);
        viewHolder.item_comicdownoption_downstatus.setText(LanguageUtil.getString(activity, R.string.ComicDownActivity_local));
        viewHolder.item_comicdownoption_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mChooseBoyinList.contains(info)) {
                    mChooseBoyinList.add(info);
                    viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_maincolor);
                    viewHolder.item_comicdownoption_text.setTextColor(Color.WHITE);
                } else {
                    mChooseBoyinList.remove(info);
                    viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_white);
                    viewHolder.item_comicdownoption_text.setTextColor(Color.BLACK);
                }
                refreshBtn(mChooseBoyinList.size());
            }
        });
        return convertView;
    }
}