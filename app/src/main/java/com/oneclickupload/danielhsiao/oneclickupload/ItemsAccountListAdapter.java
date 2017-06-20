package com.oneclickupload.danielhsiao.oneclickupload;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;

/**
 * Created by Daniel Hsiao on 2017-06-19.
 */

public class ItemsAccountListAdapter extends BaseAdapter {
    private Context context;
    private List<ItemAccount> list;

    public ItemsAccountListAdapter(Context c, List<ItemAccount> l){
        this.context = c;
        this.list = l;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        CheckBox checkbox;
        ImageView imageView;
        TextView textView;
        if(rowView == null){
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            rowView = inflater.inflate(R.layout.list_checkbox_account, null);
        }

        checkbox = (CheckBox) rowView.findViewById(R.id.rowCheckBox);
        imageView = (ImageView) rowView.findViewById(R.id.rowImageView);
        textView = (TextView) rowView.findViewById(R.id.rowTextView);

        imageView.setImageDrawable(list.get(position).getDrawable());
        textView.setText(list.get(position).getString());
        checkbox.setChecked(list.get(position).isChecked());
        checkbox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                boolean isChecked = !list.get(position).isChecked();
                list.get(position).setChecked(isChecked);
            }
        });
        return rowView;
    }
}
