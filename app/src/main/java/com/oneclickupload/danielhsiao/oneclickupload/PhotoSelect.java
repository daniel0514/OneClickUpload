package com.oneclickupload.danielhsiao.oneclickupload;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

public class PhotoSelect extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);

        GridView photoGrid = (GridView) findViewById(R.id.gridview);
        photoGrid.setAdapter(new ImageAdapter(this));

        photoGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(PhotoSelect.this, "" + position, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
