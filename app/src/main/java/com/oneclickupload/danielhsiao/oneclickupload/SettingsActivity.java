package com.oneclickupload.danielhsiao.oneclickupload;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.preference.PreferenceActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SettingsActivity extends PreferenceActivity {
    Fragment settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //Setting up Fragments
        settings = new SettingsFragment();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        if(savedInstanceState == null){
            fragmentTransaction.add(R.id.activity_settings, settings, "settings_fragment");
            fragmentTransaction.commit();
        } else {
            settings = getFragmentManager().findFragmentByTag("settings_fragment");
        }
    }
    @Override
    protected boolean isValidFragment(String fragmentName){
        return SettingsFragment.class.getName().equals(fragmentName);
    }
}
