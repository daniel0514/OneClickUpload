package com.oneclickupload.danielhsiao.oneclickupload;

import android.content.Context;
import android.content.DialogInterface;
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

public class NewProfileActivity extends AppCompatActivity {
    Context context;
    ImageButton buttonAdd;
    Button buttonCancel;
    Button buttonSave;
    EditText editTextProfileName;
    LinearLayout scrollLinear;
    List<Spinner> accountTypes = new ArrayList<>();
    List<EditText> accountIDs = new ArrayList<>();
    List<EditText> passwords = new ArrayList<>();
    DatabaseHelper db;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);
        context = getApplicationContext();
        buttonAdd = (ImageButton) findViewById(R.id.addAccountButton);
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextProfileName = (EditText) findViewById(R.id.editText);
        scrollLinear = (LinearLayout) findViewById(R.id.linearScrollView);
        db = new DatabaseHelper(context);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NewProfileActivity.this)
                        .setTitle("Discarding Changes")
                        .setMessage("Are you sure you want to close this activity?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton("No", null)
                        .show();
            }
        });
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Profile p = createProfile();
                finish();
            }
        });
    }

    public Profile createProfile(){
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
        db.addProfile(p);
        return p;
    }
}
