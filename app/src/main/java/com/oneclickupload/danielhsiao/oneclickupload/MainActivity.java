package com.oneclickupload.danielhsiao.oneclickupload;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int requestResult = 1;
    private Context c;
    int permissionCheck;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        c = getApplicationContext();

        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestResult);
        }
        permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        final Button buttonCamera = (Button) findViewById(R.id.buttonCamera);
        buttonCamera.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(c.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    dispatchTakePictureIntent();
                } else {
                    createNoCameraDialog();
                }
            }
        });

    }

    private void createNoCameraDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This device does not have a default camera app. Please install a camera app from the Play Store.");
        AlertDialog dialog = builder.create();
        dialog.show();
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
        if(requestCode == REQUEST_TAKE_PHOTO){
            if(resultCode == RESULT_OK){
                galleryAddPic();
                mCurrentPhotoPath = null;
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
