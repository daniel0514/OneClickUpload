package com.oneclickupload.danielhsiao.oneclickupload;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 *  Activity Class for launching the new profile screen
 */
public class NewProfileActivity extends AppCompatActivity {
    private Context context;
    private Button buttonCancel;
    private Button buttonSave;
    private EditText editTextProfileName;
    private EditText editTextUploadText;
    private ListView accountListView;
    private List<ItemAccount> accountItems = new ArrayList<>();
    private ItemsAccountListAdapter itemsAccountListAdapter;
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
        buttonCancel = (Button) findViewById(R.id.buttonCancel);
        buttonSave = (Button) findViewById(R.id.buttonSave);
        editTextProfileName = (EditText) findViewById(R.id.editText);
        editTextUploadText = (EditText) findViewById(R.id.editTextText);
        accountListView = (ListView) findViewById(R.id.accountListView);
        //Database for persistence
        db = new DatabaseHelper(context);

        //Initialize Accounts Checkboxes
        initAccountItems();
        itemsAccountListAdapter = new ItemsAccountListAdapter(this, accountItems);
        accountListView.setAdapter(itemsAccountListAdapter);


        //Cancel button for exiting the activity
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(NewProfileActivity.this)
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
                if(isRequiredFieldsFilled()) {
                    int id = createProfile();
                    setResult(Activity.RESULT_OK, getIntent().putExtra("ProfileID", id));
                    finish();
                } else {
                    Toast.makeText(context, getResources().getString(R.string.required_field_alert), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private boolean isRequiredFieldsFilled(){
        return (editTextProfileName.getText().toString() != null && isAnyAccountTypeSelected());
    }

    private boolean isAnyAccountTypeSelected(){
        for(ItemAccount i : accountItems){
            if(i.isChecked()) return true;
        }
        return false;
    }

    /**
     * Read the data from the spinners and textviews and create a new profile
     * @return  : The ID of the persisted profile
     */
    public int createProfile(){
        String profileName = editTextProfileName.getText().toString();
        String text = editTextUploadText.getText().toString();
        Profile p = new Profile(profileName, text);
        for (int i = 0; i < accountItems.size(); i++){
            if(accountItems.get(i).isChecked()){
                int accountType = i + 1;
                Account a = new Account(accountType, "", "");
                p.addAccount(a);
            }
        }
        //Persist the profile
        int profileID = db.addProfile(p);
        return profileID;
    }

    private void initAccountItems(){
        TypedArray arrayDrawables = getResources().obtainTypedArray(R.array.accountIcons);
        TypedArray arrayText = getResources().obtainTypedArray(R.array.accountType);
        for(int i = 0; i < arrayDrawables.length(); i++){
            ItemAccount item = new ItemAccount(false, arrayDrawables.getDrawable(i), arrayText.getString(i));
            accountItems.add(item);
        }
        arrayDrawables.recycle();
        arrayText.recycle();
    }
}
