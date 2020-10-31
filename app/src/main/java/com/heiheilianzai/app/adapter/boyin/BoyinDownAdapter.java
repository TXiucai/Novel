package com.heiheilianzai.app.adapter.boyin;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heiheilianzai.app.R;
import com.heiheilianzai.app.model.boyin.BoyinChapterBean;
import com.heiheilianzai.app.utils.LanguageUtil;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 有声小说下载 Adapter
 */
public class BoyinDownAdapter extends BaseAdapter {
    Activity activity;
    public List<BoyinChapterBean> comicDownOptionList;
    public List<BoyinChapterBean> mChooseBoyinList;
    LayoutInflater layoutInflater;
    TextView activity_comicdown_choose_count, activity_comicdown_down;
    public int mSize;

    public BoyinDownAdapter(Activity activity, List<BoyinChapterBean> comicDownOptionList, TextView activity_comicdown_choose_count, TextView activity_comicdown_down) {
        this.activity = activity;
        this.activity_comicdown_choose_count = activity_comicdown_choose_count;
        this.activity_comicdown_down = activity_comicdown_down;
        this.comicDownOptionList = comicDownOptionList;
        this.layoutInflater = LayoutInflater.from(activity);
        mSize = comicDownOptionList.size();
        mChooseBoyinList = new ArrayList<>();
        refreshBtn(0);
    }

    @Override
    public int getCount() {
        return mSize;
    }

    @Override
    public BoyinChapterBean getItem(int position) {
        return comicDownOptionList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.item_comicdownoption, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        BoyinChapterBean comicDownOption = getItem(position);
        viewHolder.item_comicdownoption_text.setText(comicDownOption.getNumbers()+ "");
        int status = comicDownOption.getDownloadStatus();
        if (status == 0 || status == 3) {
            viewHolder.item_comicdownoption_downstatus.setVisibility(View.GONE);
            if (mChooseBoyinList.contains(comicDownOption)) {
                viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_maincolor);
                viewHolder.item_comicdownoption_text.setTextColor(Color.WHITE);
            } else {
                viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_white);
                viewHolder.item_comicdownoption_text.setTextColor(Color.BLACK);
            }
            if (status == 3) {
                viewHolder.item_comicdownoption_downstatus.setVisibility(View.VISIBLE);
                viewHolder.item_comicdownoption_downstatus.setText(LanguageUtil.getString(activity, R.string.ComicDownActivity_downfail));
            } else {
                viewHolder.item_comicdownoption_downstatus.setVisibility(View.GONE);
            }
        } else {
            viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_gray);
            viewHolder.item_comicdownoption_text.setTextColor(Color.BLACK);
            viewHolder.item_comicdownoption_downstatus.setVisibility(View.VISIBLE);
            if (status == 2) {
                viewHolder.item_comicdownoption_downstatus.setText(LanguageUtil.getString(activity, R.string.ComicDownActivity_downing));
            } else {
                viewHolder.item_comicdownoption_downstatus.setText(LanguageUtil.getString(activity, R.string.ComicDownActivity_local));
            }
        }
        viewHolder.item_comicdownoption_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (status == 0 || status == 3) {
                    if (!mChooseBoyinList.contains(comicDownOption)) {
                        mChooseBoyinList.add(comicDownOption);
                        viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_maincolor);
                        viewHolder.item_comicdownoption_text.setTextColor(Color.WHITE);
                    } else {
                        mChooseBoyinList.remove(comicDownOption);
                        viewHolder.item_comicdownoption_text.setBackgroundResource(R.drawable.shape_comicdownoption_white);
                        viewHolder.item_comicdownoption_text.setTextColor(Color.BLACK);
                    }
                    refreshBtn(mChooseBoyinList.size());
                }
            }
        });
        return convertView;
    }

    public void selectAll() {
        if (mSize != mChooseBoyinList.size()) {
            mChooseBoyinList.clear();
            mChooseBoyinList.addAll(comicDownOptionList);
            refreshBtn(mSize);
        } else {
            mChooseBoyinList.clear();
            refreshBtn(0);
        }
        notifyDataSetChanged();
    }

    public void refreshBtn(int size) {
        if (size == 0) {
            activity_comicdown_down.setClickable(false);
            activity_comicdown_down.setTextColor(Color.GRAY);
        } else {
            activity_comicdown_down.setTextColor(Color.BLACK);
            activity_comicdown_down.setClickable(true);
        }
        activity_comicdown_choose_count.setText(String.format(LanguageUtil.getString(activity, R.string.ComicDownActivity_choosecount), size));
    }

    class ViewHolder {
        @BindView(R.id.item_comicdownoption_downstatus)
        TextView item_comicdownoption_downstatus;
        @BindView(R.id.item_comicdownoption_text)
        TextView item_comicdownoption_text;

        public ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}