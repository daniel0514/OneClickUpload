package com.oneclickupload.danielhsiao.oneclickupload;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

/**
 *  Activity Class for launching the new profile screen
 */
public class NewProfileActivity extends AppCompatActivity {
    private Context context;
    private ImageButton buttonAdd;
    private Button buttonCancel;
    private Button buttonSave;
    private EditText editTextProfileName;
    private LinearLayout scrollLinear;
    private List<Spinner> accountTypes = new ArrayList<>();
    private List<EditText> accountIDs = new ArrayList<>();
    private List<EditText> passwords = new ArrayList<>();
    private DatabaseHelper db;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    /**
     * Method to be called when the activity is created
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        //Setting Variables
        context = getApplicationContext();
        buttonAdd = (ImageButton) findViewById(R.id.addAccountButton);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextProfileName = (EditText) findViewById(R.id.editText);
        scrollLinear = (LinearLayout) findViewById(R.id.linearScrollView);
        //Database for persistence
        db = new DatabaseHelper(context);

        //Button to Add a new Account in the profile
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Get the inflator from the System Service to inflate a view
                LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = inflater.inflate(R.layout.list_account_item, null);
                Spinner accountType = (Spinner) view.findViewById(R.id.spinnerAccount);
                EditText accountID = (EditText) view.findViewById(R.id.editTextAccountID);
                EditText password = (EditText) view.findViewById(R.id.editTextPassword);
                accountTypes.add(accountType);
                accountIDs.add(accountID);
                passwords.add(password);
                scrollLinear.addView(view);
            }
        });
        //Cancel button for exiting the activity
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NewProfileActivity.this)
                        .setTitle("Discarding Changes")
                        .setMessage("Are you sure you want to close this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                setResult(Activity.RESULT_CANCELED);
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        //Save the newly created profile into the database
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = createProfile();
                setResult(Activity.RESULT_OK, getIntent().putExtra("ProfileID", id));
                finish();
            }
        });
    }

    /**
     * Read the data from the spinners and textviews and create a new profile
     * @return  : The ID of the persisted profile
     */
    public int createProfile(){
        String profileName = editTextProfileName.getText().toString();
        Profile p = new Profile(profileName);
        int count = accountTypes.size();
        for(int i = 0; i < count; i++){
            int accountType = accountTypes.get(i).getSelectedItemPosition() + 1;
            String accountID = accountIDs.get(i).getText().toString();
            String password = passwords.get(i).getText().toString();
            Account a = new Account(accountType, accountID, password);
            p.addAccount(a);
        }
        //Persist the profile
        int profileID = db.addProfile(p);
        return profileID;
    }
}
