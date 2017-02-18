package com.oneclickupload.danielhsiao.oneclickupload;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ProfilesActivity extends AppCompatActivity {

    private DatabaseHelper db;
    private List<Profile> profiles;
    private LinearLayout scrollLinear;
    private List<Profile> toDelete = new ArrayList<>();
    private Button buttonCancel;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profiles);
        //Database for persistence
        db = new DatabaseHelper(getApplicationContext());
        scrollLinear = (LinearLayout) findViewById(R.id.profilesLinearLayout);
        profiles = db.getProfiles();
        for(final Profile p : profiles){
            //Get the inflator from the System Service to inflate a view
            LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.list_profile_item, null);
            EditText name = (EditText) view.findViewById(R.id.profileName);
            EditText text = (EditText) view.findViewById(R.id.profileText);
            ImageButton remove = (ImageButton) view.findViewById(R.id.delete);
            name.setText(p.getName(), TextView.BufferType.EDITABLE);
            name.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    p.setText(s.toString());
                    p.setIsModified(true);
                }
            });
            text.setText(p.getText(), TextView.BufferType.EDITABLE);
            text.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}

                @Override
                public void afterTextChanged(Editable s) {
                    p.setText(s.toString());
                    p.setIsModified(true);
                }
            });
            remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    profiles.remove(p);
                    toDelete.add(p);
                    scrollLinear.removeView(view);
                }
            });
            scrollLinear.addView(view);
        }
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ProfilesActivity.this)
                        .setTitle(getResources().getString(R.string.discard_changes_title))
                        .setMessage(getResources().getString(R.string.discard_changes_message))
                        .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                            }
                        })
                        .setNegativeButton(getResources().getString(R.string.no), null)
                        .show();
            }
        });
        //Save the newly created profile into the database
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    for (Profile delete : toDelete) {
                        db.removeProfile(delete);
                    }
                    for (Profile p : profiles) {
                        if (p.isModified()) {
                            db.updateProfile(p);
                        }
                    }
                    setResult(Activity.RESULT_OK);
                    finish();
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
    }
}
