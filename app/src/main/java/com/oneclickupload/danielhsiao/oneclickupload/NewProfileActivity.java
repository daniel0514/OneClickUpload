package com.oneclickupload.danielhsiao.oneclickupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

public class NewProfileActivity extends AppCompatActivity {
    ImageButton buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_profile);

        buttonAdd = (ImageButton) findViewById(R.id.addAccountButton);


        ListView listView = (ListView) findViewById(R.id.listViewAccount);
        final NewProfileAdapter accountAdapter = new NewProfileAdapter(this);
        listView.setAdapter(accountAdapter);

        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                accountAdapter.addAccount("New Account");
                accountAdapter.notifyDataSetChanged();
            }
        });
    }
}
