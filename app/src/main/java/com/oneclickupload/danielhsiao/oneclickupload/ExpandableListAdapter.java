package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;


/**
 * Created by Daniel Hsiao on 2017-01-12.
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<Profile> profiles;
    private int selectedIndex;
    private LinearLayout headerImages;
    private TextView headerTextView;

    public ExpandableListAdapter(Context context, List<Profile> profiles, TextView headerTextView, LinearLayout headerImages) {
        this.context = context;
        this.profiles = profiles;
        this.headerTextView = headerTextView;
        this.headerImages = headerImages;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return profiles.get(groupPosition).getAccount(childPosition);
    }

    public void addProfile(Profile p){
        profiles.add(p);
        notifyDataSetChanged();
    }

    public int getSelectedIndex(){
        return selectedIndex;
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
        LinearLayout listItemChildContent = (LinearLayout) convertView.findViewById(R.id.listItemChildContent);

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
                setHeader();
                notifyDataSetChanged();
            }
        });
        rb.setChecked(selectedIndex == groupPosition);
        setHeader();
        return convertView;
    }

    private void setHeader(){
        headerTextView.setText(profiles.get(selectedIndex).getName());
        headerImages.removeAllViews();
        for(Account a : profiles.get(selectedIndex).getAccounts()){
            ImageView imageView = new ImageView(context);
            if(a.getAccountType() == Account.FACEBOOK_ACCOUNT){
                imageView.setBackgroundResource(R.drawable.facebookon);
            } else if(a.getAccountType() == Account.TWITTER_ACCOUNT){
                imageView.setBackgroundResource(R.drawable.twitteron);
            }
            imageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100, 1));
            headerImages.addView(imageView);
        }
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
