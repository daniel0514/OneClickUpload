package com.oneclickupload.danielhsiao.oneclickupload;

import android.preference.PreferenceFragment;
import android.os.Bundle;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.frag_settings_layout);
    }
}
