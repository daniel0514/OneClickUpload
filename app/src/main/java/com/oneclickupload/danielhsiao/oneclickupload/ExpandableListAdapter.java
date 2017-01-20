package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;


/**
 * Created by Daniel Hsiao on 2017-01-12.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Profile> profiles;
    private int selectedIndex;

    public ExpandableListAdapter(Context context, List<Profile> profiles) {
        this.context = context;
        this.profiles = profiles;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return profiles.get(groupPosition).getAccount(childPosition);
    }

    public void addProfile(Profile p){
        profiles.add(p);
        notifyDataSetChanged();
    }


    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        Switch switchAccount = (Switch) convertView.findViewById(R.id.switchListItem);
        switchAccount.setChecked(true);

        Account a = profiles.get(groupPosition).getAccount(childPosition);
        txtListChild.setText(a.getAPIName(a.getAccountType()));

        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return profiles.get(groupPosition).getAccounts().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return profiles.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return profiles.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(profiles.get(groupPosition).getName());

        RadioButton rb = (RadioButton) convertView.findViewById(R.id.radioButton);
        rb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedIndex = groupPosition;
                notifyDataSetChanged();
            }
        });
        rb.setChecked(selectedIndex == groupPosition);

        return convertView;
    }


    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
