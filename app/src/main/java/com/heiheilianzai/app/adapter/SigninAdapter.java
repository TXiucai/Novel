package com.heiheilianzai.app.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.heiheilianzai.app.R;

/**
 * Created by scb on 2018/10/28.
 */

public class SigninAdapter extends BaseAdapter {

    private  String [] SigninRuleLsit;
    LayoutInflater layoutInflater;

    public SigninAdapter(String[] signinRuleLsit, LayoutInflater layoutInflater) {
        SigninRuleLsit = signinRuleLsit;
        this.layoutInflater = layoutInflater;
    }

    @Override
    public int getCount() {
        return SigninRuleLsit.length;
    }

    @Override
    public String getItem(int i) {
        return SigninRuleLsit[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view==null){
            view=layoutInflater.inflate(R.layout.listview_item_signinrule, null);

        }
      TextView listview_item_signinrule_rule= view.findViewById(R.id.listview_item_signinrule_rule);
        listview_item_signinrule_rule.setText(getItem(i));
        return view;
    }
}
