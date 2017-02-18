package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.ArrayList;
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
 * Adapter for the Expandable List in the drawer
 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {
    private Context context;
    //List of Profiles for the drawer to display
    private List<Profile> profiles;
    //Current selected index, represented by the selected radio button
    private int selectedIndex;
    //The LinearLayout to contain the social media icons of the selected profile
    private LinearLayout headerImages;
    //The TextView to display selected profile name
    private TextView headerTextView;

    /**
     * Constructor
     * @param context       : Application Context
     * @param profiles      : The profiles to be displayed
     * @param headerTextView: The TextView of the drawer header
     * @param headerImages  : The LinearLayout of the drawer header
     */
    public ExpandableListAdapter(Context context, List<Profile> profiles, TextView headerTextView, LinearLayout headerImages) {
        this.context = context;
        this.profiles = profiles;
        this.headerTextView = headerTextView;
        this.headerImages = headerImages;
    }

    /**
     * The child list is the account list. The method is used to get the account
     * of the selected profile based on group index and child index
     * @param groupPosition : The index of the selected group
     * @param childPosition : The index of the selected child
     * @return              : The Account Object
     */
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return profiles.get(groupPosition).getAccount(childPosition);
    }

    /**
     * Adding a profile to the list
     * @param p : The profile to be added
     */
    public void addProfile(Profile p){
        profiles.add(p);
        notifyDataSetChanged();
    }

    public void setProfiles(List<Profile> profiles){
        this.profiles = profiles;
        notifyDataSetChanged();
    }

    /**
     * Get the index of the selected radio button
     * @return  : The index
     */
    public int getSelectedIndex(){
        return selectedIndex;
    }

    /**
     * Get the selected child index (ID)
     * @param groupPosition : Group Index
     * @param childPosition : Child Index
     * @return  : Child Index
     */
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    /**
     * Get the view of the selected child
     * @param groupPosition : The selected group index
     * @param childPosition : The selected child index
     * @param isLastChild   : If the child is the last in the list
     * @param convertView   : Non-null if the old view is available for reuse.
     * @param parent        : The parent view group of the child view
     * @return              : The view of the selected child
     */
    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        //If old view is not available or not created, inflate the view
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, null);
        }

        // The text view to contain the Account Type Text
        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        // Get the accounts from the profile and populate the textview
        Account a = profiles.get(groupPosition).getAccount(childPosition);
        txtListChild.setText(a.getAPIName(a.getAccountType()));

        return convertView;
    }

    /**
     * Get the number of child in the group
     * @param groupPosition : Index of the group
     * @return              : The number of child views in the group
     */
    @Override
    public int getChildrenCount(int groupPosition) {
        return profiles.get(groupPosition).getAccounts().size();
    }

    /**
     * Get the profile of the selected group index
     * @param groupPosition : The group index
     * @return              : The corresponding profile
     */
    @Override
    public Object getGroup(int groupPosition) {
        return profiles.get(groupPosition);
    }

    /**
     * Get the number of groups
     * @return  : The number of groups/profiles
     */
    @Override
    public int getGroupCount() {
        return profiles.size();
    }

    /**
     * Get the selected group position/index
     * @param groupPosition : Group position
     * @return              : Group Index
     */
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    /**
     * Get the view of the selected group
     * @param groupPosition : The group index
     * @param isExpanded    : Is the group expanded
     * @param convertView   : Non-null if the old view is available for reuse.
     * @param parent        : The parent view group of the selected group
     * @return              : The view of the selected group[
     */
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_group, null);
        }

        //The TextView of the group.
        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        //Set the text of the TextView to Profile Name
        lblListHeader.setText(profiles.get(groupPosition).getName());

        // The radio button for selecting active profiles
        RadioButton rb = (RadioButton) convertView.findViewById(R.id.radioButton);
        // The OnClickListener functions as a RadioGroup since adding radio buttons
        // from different view to a RadioGroup (ViewGroup) is impossible
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

    /**
     * Set the header TextView and Images LinearLayout
     */
    private void setHeader(){
        //Set the text in the TextView to selected profile name
        headerTextView.setText(profiles.get(selectedIndex).getName());
        //Clear out the images in the headerImages linear layout
        headerImages.removeAllViews();
        //Adding Social Media Icons one by one
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

    /**
     * Returns true if the child view is selectable
     * @param groupPosition : Group index
     * @param childPosition : Child index
     * @return  : Boolean; whether the child view is selectable
     */
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
