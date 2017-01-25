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
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;
import com.wdullaer.swipeactionadapter.SwipeDirection;


import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    //Request Codes for Intents
    private static final int REQUEST_PERMISSION = 1;
    private static final int PICK_IMAGE = 2;
    private static final int REQUEST_IMAGE_CAPTURE = 3;
    private static final int REQUEST_PROFILE = 4;

    //Activity Variables
    private Context context;
    int hasPermission_ES_W; //Permission for External Storage Write

    //UI Variables
        //Buttons
    private Button buttonCamera;
    private Button buttonAdd;
        //Drawers
    private DrawerLayout layoutDrawer;
    private ExpandableListView listDrawer;
    private ImageButton buttonSetting;
        //ListView Variables
    private ListView listUploads;
    private ArrayAdapter<String> stringAdapter;
    private SwipeActionAdapter swipeAdapter;
        //Drawer Data Variable
    private ExpandableListAdapter listAdapter;
        //Database
    private DatabaseHelper db;

    //Facebook API
    LoginManager mLoginManager;
    CallbackManager mCallbackManager;
    List<String> facebookPermissions= Arrays.asList("publish_actions");

    private List<Profile> profiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        context = getApplicationContext();

        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitterApiID), getResources().getString(R.string.twitterSecretID));
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());

        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(getApplication());
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(context, "Successfully Logged In", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
        mLoginManager.logInWithPublishPermissions(this, facebookPermissions);

        //Check if App has permission
        checkPermission();

        //Setup DB
        db = new DatabaseHelper(context);
        profiles = db.getProfiles();

        TextView drawerHeader = (TextView) findViewById(R.id.selectedProfile);
        LinearLayout drawerHeaderImages = (LinearLayout) findViewById(R.id.linearHeaderImages);

        //Setup Drawer
        layoutDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        listDrawer = (ExpandableListView) findViewById(R.id.left_drawer);
        listAdapter = new ExpandableListAdapter(this, profiles, drawerHeader, drawerHeaderImages);
        listDrawer.setAdapter(listAdapter);


        //Setup Main Screen List
        listUploads = (ListView) findViewById(R.id.listView1);
        setUpSwipeList();

        buttonCamera = (Button) findViewById(R.id.buttonCamera);
        initializeButton(buttonCamera, R.id.buttonCamera);
        buttonAdd = (Button) findViewById(R.id.buttonAdd);
        initializeButton(buttonAdd, R.id.buttonAdd);
        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        initializeButton(buttonSetting, R.id.buttonSetting);
    }

    /**
     *  The main method to set up the Swipe Action List that contains the upload list
     */
    private void setUpSwipeList(){
        //Setup the String Adapter to contain the strings of the list
        stringAdapter = new ArrayAdapter<>(
                this,
                R.layout.row_big,
                R.id.text,
                new ArrayList<String>()
        );
        //Setup the Swipe Adapter to handle swipe actions to elements of the list
        swipeAdapter = new SwipeActionAdapter(stringAdapter);
        swipeAdapter.setSwipeActionListener(new SwipeActionListener(){
            // Return true for directions for permitted swipe
            // Return false to forbid swipes for certain direction.
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                return (direction.isLeft() || direction.isRight());
            }

            // Return true for directions to dismiss the item in the list
            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){
                return direction == SwipeDirection.DIRECTION_NORMAL_RIGHT;
            }

            // Triggered by Swipe Action. Implements the actions for swipes in different directions.
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
                            context,
                            dir + " swipe Action triggered on " + swipeAdapter.getItem(position),
                            Toast.LENGTH_SHORT
                    ).show();
                    swipeAdapter.notifyDataSetChanged();
                }
            }
        });
        swipeAdapter.setDimBackgrounds(true);
        swipeAdapter.setListView(listUploads);
        listUploads.setAdapter(swipeAdapter);
        // Set the background of the swipe directions
        swipeAdapter.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right_far)
                .addBackground(SwipeDirection.DIRECTION_NORMAL_RIGHT,R.layout.row_bg_right);
    }

    /**
     * Check if the App has appropriate permissions. If not, request permission. The result of the request will be
     * saved in hasPermission_ES_W variable.
     * @return Boolean : TRUE if Permission Granted; FALSE otherwise.
     */
    private boolean checkPermission(){
        hasPermission_ES_W = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(hasPermission_ES_W != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
        }
        hasPermission_ES_W = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return (hasPermission_ES_W == PackageManager.PERMISSION_GRANTED);
    }

    /**
     *  Initialize buttons by adding appropriate listeners
     * @param button the button to be initialized
     * @param id    the id of the button
     */
    private void initializeButton(View button, int id){
        switch(id) {
            case R.id.buttonCamera :{
                //Setting the onClickListener for the Camera Button to open default camera app
                button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                            runIntentImageCapture();
                        } else {
                            createNoCameraDialog();
                        }
                    }
                });
                break;
            }
            case R.id.buttonAdd :{
                //Setting the onClickListener for the Add Button to open the default gallery app for image selection
                button.setOnClickListener(new View.OnClickListener(){

                    @Override
                    public void onClick(View v) {
                        runIntentOpenGallery();
                    }
                });
                break;
            }
        }
    }

    public void startSettingIntent(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    public void startNewProfileIntent(View view){
        Intent intent = new Intent(this, NewProfileActivity.class);
        startActivityForResult(intent, REQUEST_PROFILE);
    }

    /**
     * Method for creating a dialog to show the users that no default camera app is installed on the device
     */
    private void createNoCameraDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This device does not have a default camera app. Please install a camera app from the Play Store.");
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Create an Intent to open the gallery app.
     */
    private void runIntentOpenGallery(){
        Intent intentGallery = new Intent();
        Uri uri = Uri.parse(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getPath() + "/CAMERA/");
        intentGallery.setDataAndType(uri, "image/*");
        intentGallery.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intentGallery, PICK_IMAGE);
    }

    // The path of the image captured by the camera app
    private String mCurrentPhotoPath;

    /**
     *  The method for creating a temporary image file that is captured by default camera app
     * @return File: The image file captured by the camera pp
     * @throws IOException : thrown when the camera fails to save the captured image
     */
    private File createImageFile() throws IOException {
        //timeStamp is used for unique file name
        String timeStamp = new SimpleDateFormat("yyyMMdd_HHmmss").format(new Date());
        String imageFileName = "IMG_" + timeStamp;
        //Use getExternalStoragePublicDirectory instead of getExternalStorageDirectory so the image captured by
        //the camera actually saved to the SDCard instead of the app directory.
        File root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File storageDir = new File(root.getAbsolutePath() + "/CAMERA/");
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        //Sets mCurrentPhotoPath for other uses.
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    /**
     * Creates an intent to launch the default camera app.
     */
    private void runIntentImageCapture(){
        Intent intentTakePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(intentTakePicture.resolveActivity(getPackageManager()) != null){
            File photoFile = null;
            try{
                photoFile = createImageFile();
            } catch (IOException ex){
                Toast.makeText(context, "IOException in making image file", Toast.LENGTH_LONG);
            }
            if(photoFile != null){
                Uri photoURI = FileProvider.getUriForFile(context, "com.oneclickupload.danielhsiao.fileprovider", photoFile);
                intentTakePicture.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(intentTakePicture, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Method for handling activity results
     * @param requestCode   The Request Code indicting which intent the request is for
     * @param resultCode    The result code; whether the result is successful or not
     * @param data          The data of the result
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Handles the image capture intent by adding the captured image to the gallery
            case REQUEST_IMAGE_CAPTURE: {
                if (resultCode == RESULT_OK) {
                    galleryAddPic();
                    //No Longer need the photo path so we set it to null
                    mCurrentPhotoPath = null;
                }
                return;
            }
            // Handles the image selection intent
            case PICK_IMAGE: {
                if(resultCode == RESULT_OK){
                    if(data != null && data.getData() != null){
                        Uri selectedImageUri = data.getData();
                        Bitmap bitmap = getBitmap(selectedImageUri);
                        String name = selectedImageUri.getLastPathSegment();
                        publishPhotoToFacebook(bitmap);
                        publishPhotoToTwitter(selectedImageUri);
                    }
                }
                return;
            }
            case REQUEST_PROFILE: {
                if(resultCode == RESULT_OK){
                    if(data != null){
                        int profileID = data.getIntExtra("ProfileID", -1);
                        Profile p = db.getProfileByID(profileID);
                        listAdapter.addProfile(p);
                    }
                }
                return;
            }
        }
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        return;
    }

    private void publishPhotoToTwitter(Uri uri){
        TweetComposer.Builder builder = new TweetComposer.Builder(this)
                .text("Test Upload")
                .image(uri);
        builder.show();
    }

    private void publishPhotoToFacebook(Bitmap bitmap){
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption("Test Upload")
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, null);
    }

    /**
     * Method to transform URI into Bitmap
     * @param uri : the URI of the bitmap file
     * @return Bitmap : The bitmap file
     */
    private Bitmap getBitmap(Uri uri){
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            return bitmap;
        } catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Method for adding captured image to the gallery
     */
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

}
