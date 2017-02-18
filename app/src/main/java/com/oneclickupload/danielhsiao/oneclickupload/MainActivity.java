package com.oneclickupload.danielhsiao.oneclickupload;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ExpandableListView;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.Sharer;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter;
import com.wdullaer.swipeactionadapter.SwipeActionAdapter.SwipeActionListener;
import com.wdullaer.swipeactionadapter.SwipeDirection;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    //Request Codes for Intents
    private static final int REQUEST_PERMISSION = 101;
    private static final int PICK_IMAGE = 102;
    private static final int REQUEST_IMAGE_CAPTURE = 103;
    private static final int REQUEST_PROFILE = 104;
    private static final int TWEET_CODE = 105;
    private static final int REQUEST_EDIT_PROFILES = 106;

    //Activity Variables
    private Context context;
    private int hasPermission_ES_W; //Permission for External Storage Write
    private List<Profile> profiles;
    private LinkedList<Upload> uploadsList = new LinkedList<>();

    //UI Variables
    //Buttons
    private ImageButton buttonCamera;
    private ImageButton buttonAdd;
    //Drawers
    private ExpandableListView drawerEListView;
    private ExpandableListAdapter drawerEListAdapter;
    private ImageButton buttonSetting;
    private DrawerLayout drawerLayout;
    TextView drawerHeader;
    LinearLayout drawerHeaderImages;
    //ListView Variables
    private ListView listViewUploads;
    private ArrayAdapter<String> adapterUploads;
    private ArrayList<String> stringsUploads = new ArrayList<>();
    private SwipeActionAdapter adapterSwipe;
    //Database
    private DatabaseHelper db;

    //Facebook API
    private LoginManager mLoginManager;
    private CallbackManager mCallbackManager;
    private List<String> facebookPermissions= Arrays.asList("publish_actions");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Setting up Activity Variables
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_select);
        context = getApplicationContext();

        //Setting up Twitter API
        TwitterAuthConfig authConfig = new TwitterAuthConfig(getResources().getString(R.string.twitterApiID), getResources().getString(R.string.twitterSecretID));
        Fabric.with(this, new Twitter(authConfig), new TweetComposer());

        //Setting up Facebook API
        FacebookSdk.sdkInitialize(context);
        AppEventsLogger.activateApp(getApplication());
        mCallbackManager = CallbackManager.Factory.create();
        mLoginManager = LoginManager.getInstance();
        mLoginManager.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("Facebook Login", "Log in successful");
            }

            @Override
            public void onCancel() {
                Log.d("Facebook Login", "Log in failed");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("Facebook Login", "Log in Error");
            }
        });
        mLoginManager.logInWithPublishPermissions(this, facebookPermissions);


        //Check if App has permission
        checkPermission();

        //Setup DB
        db = new DatabaseHelper(context);
        profiles = db.getProfiles();

        drawerHeader = (TextView) findViewById(R.id.selectedProfile);
        drawerHeaderImages = (LinearLayout) findViewById(R.id.linearHeaderImages);

        //Setup Drawer
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerEListView = (ExpandableListView) findViewById(R.id.left_drawer);
        drawerEListAdapter = new ExpandableListAdapter(this, profiles, drawerHeader, drawerHeaderImages);
        drawerEListView.setAdapter(drawerEListAdapter);


        //Setup Main Screen List
        listViewUploads = (ListView) findViewById(R.id.listView1);
        setUpSwipeList();

        buttonCamera = (ImageButton) findViewById(R.id.buttonCamera);
        initializeButton(buttonCamera, R.id.buttonCamera);
        buttonAdd = (ImageButton) findViewById(R.id.buttonAdd);
        initializeButton(buttonAdd, R.id.buttonAdd);
        buttonSetting = (ImageButton) findViewById(R.id.buttonSetting);
        initializeButton(buttonSetting, R.id.buttonSetting);

    }

    /**
     *  The main method to set up the Swipe Action List that contains the upload list
     */
    private void setUpSwipeList(){
        //Setup the String Adapter to contain the strings of the list
        adapterUploads = new ArrayAdapter<>(
                this,
                R.layout.row_big,
                R.id.text,
                stringsUploads
        );
        //Setup the Swipe Adapter to handle swipe actions to elements of the list
        adapterSwipe = new SwipeActionAdapter(adapterUploads);
        adapterSwipe.setSwipeActionListener(new SwipeActionListener(){
            // Return true for directions for permitted swipe
            // Return false to forbid swipes for certain direction.
            @Override
            public boolean hasActions(int position, SwipeDirection direction){
                if(uploadsList.get(position).getStartTime() != null && !uploadsList.get(position).isUploadComplete()){
                    return false;
                } else if(uploadsList.get(position).getStartTime() != null && uploadsList.get(position).isUploadComplete()){
                    return direction == direction.DIRECTION_FAR_RIGHT;
                } else {
                    return (direction == direction.DIRECTION_FAR_LEFT || direction == direction.DIRECTION_FAR_RIGHT);
                }
            }

            // Return true for directions to dismiss the item in the list
            @Override
            public boolean shouldDismiss(int position, SwipeDirection direction){
                return direction == SwipeDirection.DIRECTION_FAR_RIGHT;
            }
            // Triggered by Swipe Action. Implements the actions for swipes in different directions.
            @Override
            public void onSwipe(int[] positionList, SwipeDirection[] directionList){
                for(int i=0;i<positionList.length;i++) {
                    SwipeDirection direction = directionList[i];
                    int position = positionList[i];

                    switch (direction) {
                        case DIRECTION_FAR_LEFT:
                            startUpload(position);
                            break;
                        case DIRECTION_NORMAL_LEFT:
                            break;
                        case DIRECTION_FAR_RIGHT:
                            uploadsList.remove(position);
                            stringsUploads.remove(position);
                            break;
                        case DIRECTION_NORMAL_RIGHT:
                            break;
                    }
                    adapterSwipe.notifyDataSetChanged();
                }
            }
        });
        adapterSwipe.setDimBackgrounds(true);
        adapterSwipe.setListView(listViewUploads);
        listViewUploads.setAdapter(adapterSwipe);
        // Set the background of the swipe directions
        adapterSwipe.addBackground(SwipeDirection.DIRECTION_FAR_LEFT,R.layout.row_bg_left)
                .addBackground(SwipeDirection.DIRECTION_FAR_RIGHT,R.layout.row_bg_right);
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

    /**
     * Start the Activity Page for Settings
     */
    public void startSettingIntent(View view){
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }
    /**
     * Start the Activity Page for Editing profiles
     */
    public void startEditProfilesIntent(View view){
        Intent intent = new Intent(this, ProfilesActivity.class);
        startActivityForResult(intent, REQUEST_EDIT_PROFILES);
    }
    /**
     * Start the Activity Page for adding new profiles
     */
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
                        Upload upload = new Upload(profiles.get(drawerEListAdapter.getSelectedIndex()), selectedImageUri);
                        uploadsList.add(upload);
                        stringsUploads.add(getResources().getString(R.string.initial_upload_string) + "\nProfile: " + upload.getProfile().getName());
                        adapterUploads.notifyDataSetChanged();
                    }
                }
                return;
            }
            //Handles the intent result of creating new profile
            case REQUEST_PROFILE: {
                if(resultCode == RESULT_OK){
                    if(data != null){
                        int profileID = data.getIntExtra("ProfileID", -1);
                        Profile p = db.getProfileByID(profileID);
                        drawerEListAdapter.addProfile(p);
                    }
                }
                return;
            }
            //Handles the intent result of tweeting selected photo
            case TWEET_CODE: {
                int index = 0;
                double minTimeStamp = Double.MAX_VALUE;
                Upload upload;
                //Get the earliest unfinished upload
                for (int i = 0; i < uploadsList.size(); i++) {
                    upload = uploadsList.get(i);
                    if (!upload.isUploadComplete() && upload.getStartTime() != null && upload.getStartTime() < minTimeStamp) {
                        index = i;
                        minTimeStamp = uploadsList.get(i).getStartTime();
                    }
                }
                if(resultCode == RESULT_OK) {
                    setCompleted(index, Account.TWITTER_ACCOUNT);
                } else if(resultCode == RESULT_CANCELED){
                    updateText(index, Account.TWITTER_ACCOUNT, Upload.FAILED);
                }
            }
            //Handles the intent result of editing profiles
            case REQUEST_EDIT_PROFILES: {
                if(resultCode == RESULT_OK) {
                    int rowCount = drawerEListAdapter.setProfiles(db.getProfiles());
                    if(rowCount == 0){
                        drawerHeader.setText("");
                        drawerHeaderImages.removeAllViews();
                    }
                }
            }
        }
        //Handles result of posting photo on facebook
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        return;
    }

    /**
     * Starts the selected upload based on selected profile
     * @param index : the index of selected upload task in the upload list
     */
    private void startUpload(int index){
        Upload upload = uploadsList.get(index);
        upload.setStartTime();
        Profile p = upload.getProfile();
        for(Account a : p.getAccounts()){
            switch(a.getAccountType()){
                case Account.FACEBOOK_ACCOUNT:
                    publishPhotoToFacebook(upload.getImageURI(), index, p.getText());
                    break;
                case Account.TWITTER_ACCOUNT:
                    publishPhotoToTwitter(upload.getImageURI(), index, p.getText());
                    break;
            }
        }
    }

    /**
     * Tweet a photo using the Twitter API
     * @param uri   : URI of the selected image
     * @param index : Index of the selected upload task in the upload list view
     * @param text : Text of the photo
     */
    private void publishPhotoToTwitter(Uri uri, final int index, String text){
        //Update the text on the listview to Twitter Status: Uploading
        updateText(index, Account.TWITTER_ACCOUNT, Upload.UPLOADING);
        Intent tweetIntent = new TweetComposer.Builder(this)
                .text(text)
                .image(uri)
                .createIntent();
        startActivityForResult(tweetIntent, TWEET_CODE);
    }

    /**
     *  Post a Facebook photo using Facebook API
     * @param uri   : URI of the selected image
     * @param index : Index of the selected upload task ni the upload list view
     * @param text : Text of the photo
     */
    private void publishPhotoToFacebook(Uri uri, final int index, String text){
        final int position = index;
        //Update the text on the listview to Facebook Status: Uploading
        updateText(index, Account.FACEBOOK_ACCOUNT, Upload.UPLOADING);
        //Get the bitmap of the image through the URI
        Bitmap bitmap = getBitmap(uri);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(bitmap)
                .setCaption(text)
                .build();

        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();
        ShareApi.share(content, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                //Update the text on the listview to Facebook Status: Uploaded
                setCompleted(position, Account.FACEBOOK_ACCOUNT);
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    /**
     * Set the text on the listview to Status: Uploaded. If all tasks are complete within a Upload
     * Change the text to Photo Uploaded to All Accounts
     * @param index         : Index of the listview
     * @param accountType   : Which account status to update
     */
    private void setCompleted(int index, int accountType){
        updateText(index, accountType, Upload.UPLOADED);
        uploadsList.get(index).setUploaded(accountType, Boolean.valueOf(true));
        if(uploadsList.get(index).isUploadComplete()){
            updateText(index, Account.ALL_ACCOUNTS, Upload.UPLOADED);
        }
    }

    /**
     * Update the text on the listview
     * @param position      : The index of the uploads in the listview
     * @param accountType   : Which account status to update
     * @param status        : Which status to update to (Uploaded, Uploading, Failed)
     */
    private void updateText(int position, int accountType, int status){
        String oldString = stringsUploads.get(position);
        String newString = oldString;
        if(accountType == Account.ALL_ACCOUNTS && status == Upload.UPLOADED) {
            newString = newString + "\n" + getResources().getString(R.string.all_uploaded);
        } else if(status == Upload.UPLOADED){
            if(accountType == Account.FACEBOOK_ACCOUNT){
                newString = oldString.replace(getResources().getString(R.string.start_facebook_upload), getResources().getString(R.string.finish_facebook_upload));
            } else if (accountType == Account.TWITTER_ACCOUNT){
                newString = oldString.replace(getResources().getString(R.string.start_twitter_upload), getResources().getString(R.string.finish_twitter_upload));
            }
        } else if (status == Upload.UPLOADING) {
            if(!oldString.contains("Status")){
                if(accountType == Account.FACEBOOK_ACCOUNT){
                    newString = getResources().getString(R.string.start_facebook_upload);
                } else if (accountType == Account.TWITTER_ACCOUNT){
                    newString = getResources().getString(R.string.start_twitter_upload);
                }
            } else {
                if(accountType == Account.FACEBOOK_ACCOUNT){
                    newString = oldString + "\n" + getResources().getString(R.string.start_facebook_upload);
                } else if (accountType == Account.TWITTER_ACCOUNT){
                    newString = oldString + "\n" + getResources().getString(R.string.start_twitter_upload);
                }
            }
        } else if (status == Upload.FAILED){
            if(accountType == Account.FACEBOOK_ACCOUNT){
                newString = oldString.replace(getResources().getString(R.string.start_facebook_upload), getResources().getString(R.string.failed_facebook_upload));
            } else if (accountType == Account.TWITTER_ACCOUNT){
                newString = oldString.replace(getResources().getString(R.string.start_twitter_upload), getResources().getString(R.string.failed_twitter_upload));
            }
        }
        stringsUploads.set(position, newString);
        adapterUploads.notifyDataSetChanged();
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
