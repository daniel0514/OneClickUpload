package com.oneclickupload.danielhsiao.oneclickupload;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Hsiao on 2017-01-17.
 */

public class NewProfileAdapter extends BaseAdapter {
    private Context context;
    private List<String> accounts = new ArrayList<>();;

    public NewProfileAdapter(Context context){
        this.context = context;
    }

    public NewProfileAdapter(Context context, ArrayList<String> accounts){
        this.context = context;
        this.accounts = accounts;
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void addAccount(String account){
        accounts.add(account);
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_account_item, null);
        }
        return convertView;
    }
}
