package com.oneclickupload.danielhsiao.oneclickupload;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_RESULT = 2;
    private static final int PICK_IMAGE = 3;
    private Context c;
    int permissionCheck;

    private Button buttonCamera;
    private Button buttonAdd;
    private DrawerLayout layoutDrawer;
    private ListView listDrawer;
    private ListView listUploads;
    private String[] groups;
    private SwipeActionAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        c = getApplicationContext();

        checkPermission();

        layoutDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listDrawer = (ListView) findViewById(R.id.left_drawer);

        listUploads = (ListView) findViewById(R.id.listView1);
        setUpSwipeList();


        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        initializeButton(buttonCamera, R.id.buttonCamera);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        initializeButton(buttonAdd, R.id.buttonAdd);
    }

    private void setUpSwipeList(){
        String[] content = new String[20];
        for (int i=0;i<20;i++) content[i] = "Row "+(i+1);
        ArrayAdapter<String> stringAdapter = new ArrayAdapter<>(
                this,
                R.layout.row_big,
                R.id.text,
                new ArrayList<>(Arrays.asList(content))
        );
        mAdapter = new SwipeActionAdapter(stringAdapter);
        mAdapter.setSwipeActionListener(new SwipeActionListener(){
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(direction.isLeft()) return true;
                if(direction.isRight()) return true;
                return false;
            }

            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){
                return direction == SwipeDirection.DIRECTION_NORMAL_LEFT;
            }
            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    int position = positionList[i];
                    String dir = "";

                    switch (direction) {
                        case DIRECTION_FAR_LEFT:
                            dir = "Far left";
                            break;
                        case DIRECTION_NORMAL_LEFT:
                            dir = "Left";
                            break;
                        case DIRECTION_FAR_RIGHT:
                            dir = "Far right";
                            break;
                        case DIRECTION_NORMAL_RIGHT:
                            dir = "Right";
                            break;
                    }
                    Toast.makeText(
                            c,
                            dir + " swipe Action triggered on " + mAdapter.getItem(position),
                            Toast.LENGTH_SHORT
                    ).show();
                    mAdapter.notifyDataSetChanged();
                }
            }
        });
        mAdapter.setDimBackgrounds(true);
        mAdapter.setListView(listUploads);
        listUploads.setAdapter(mAdapter);
        mAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
    }

    private boolean checkPermission(){
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_RESULT);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (permissionCheck == PackageManager.PERMISSION_GRANTED);
    }

    private void initializeButton(Button button, int id){
        switch(id) {
            case R.id.buttonCamera :{
                button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        if(c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            dispatchTakePictureIntent();
                        } else {
                            createNoCameraDialog();
                        }
                    }
                });
                break;
            }
            case R.id.buttonAdd :{
                button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        runOpenGalleryIntent();
                    }
                });
                break;
            }
        }
    }

    private void createNoCameraDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This device does not have a default camera app. Please install a camera app from the Play Store.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void runOpenGalleryIntent(){
        Intent intentGallery = new Intent();
        intentGallery.setType("image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentGallery, PICK_IMAGE);
    }

    String mCurrentPhotoPath;

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File storageDir = new File(root.getAbsolutePath() + "/CAMERA/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    static final int REQUEST_TAKE_PHOTO = 1;
    private void dispatchTakePictureIntent(){
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentTakePicture.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException ex){
                Toast.makeText(c, "IOException in making image file", Toast.LENGTH_LONG);
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(c, "com.oneclickupload.danielhsiao.fileprovider", photoFile);
                System.out.println(photoURI.getPath());
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intentTakePicture, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    mCurrentPhotoPath = null;
                }
            }
            case PICK_IMAGE: {
                if(resultCode == RESULT_OK){
                    if(data != null && data.getData() != null){
                        Uri selectedImageUri = data.getData();
                        Bitmap bitmap = getBitmap(selectedImageUri);
                    }
                }
            }
        }
    }

    private Bitmap getBitmap(Uri uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            return bitmap;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
