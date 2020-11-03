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

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BasePhonicDownAdapter extends BaseAdapter {
    Activity activity;
    public List<BoyinChapterBean> comicDownOptionList;
    public List<BoyinChapterBean> mChooseBoyinList;
    LayoutInflater layoutInflater;
    TextView activity_comicdown_choose_count, activity_comicdown_down;

    public BasePhonicDownAdapter(Activity activity, List<BoyinChapterBean> comicDownOptionList, TextView activity_comicdown_choose_count, TextView activity_comicdown_down) {
        this.activity = activity;
        this.activity_comicdown_choose_count = activity_comicdown_choose_count;
        this.activity_comicdown_down = activity_comicdown_down;
        this.comicDownOptionList = comicDownOptionList;
        this.layoutInflater = LayoutInflater.from(activity);
        mChooseBoyinList = new ArrayList<>();
        refreshBtn(0);
    }

    @Override
    public int getCount() {
        return comicDownOptionList.size();
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
        BoyinChapterBean info = getItem(position);
        viewHolder.item_comicdownoption_text.setText(info.getNumbers() + "");
        return getMyView(position, convertView, parent, viewHolder, info);
    }

    public void selectAll(boolean isDown, String nid) {
        int mSize = 0;
        if (isDown) {
            mSize = comicDownOptionList.size() - LitePal.where("nid = ? and downloadStatus = ?", nid, "1").find(BoyinChapterBean.class).size();
        } else {
            mSize = comicDownOptionList.size();
        }
        if (mSize != mChooseBoyinList.size()) {
            mChooseBoyinList.clear();
            if (isDown) {
                for (BoyinChapterBean info : comicDownOptionList) {
                    if (info.getDownloadStatus() != 1) {
                        mChooseBoyinList.add(info);
                    }
                }
            } else {
                mChooseBoyinList.addAll(comicDownOptionList);
            }
            refreshBtn(mChooseBoyinList.size());
        } else {
            mChooseBoyinList.clear();
            refreshBtn(0);
        }
        notifyDataSetChanged();
    }

    abstract View getMyView(int position, View convertView, ViewGroup parent, ViewHolder viewHolder, BoyinChapterBean info);

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

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }
}