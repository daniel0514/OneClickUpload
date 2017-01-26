package com.oneclickupload.danielhsiao.oneclickupload;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

/**
 * Upload Class to contain upload related informations
 */

public class Upload {
    // The upload status
    public static final int UPLOADED = 2;
    public static final int UPLOADING = 1;
    public static final int FAILED = 3;
    //The selected profile for the upload
    private Profile profile;
    //The selected image to be uploaded
    private Uri imageURI;
    //The upload status of each account in the profile
    private ArrayList<Boolean> uploadStatus = new ArrayList<>();
    //The start time of the upload
    private Long startTime = null;

    /**
     * Constructor
     * @param profile   : The profile to be used for the upload
     * @param imageUri  : The URI of the image to be uploaded
     */
    public Upload(Profile profile, Uri imageUri){
        this.profile = profile;
        this.imageURI = imageUri;
        for(Account a : profile.getAccounts()){
            uploadStatus.add(Boolean.valueOf(false));
        }
    }


    public Uri getImageURI(){
        return imageURI;
    }

    public Profile getProfile(){
        return profile;
    }

    public void setStartTime(){
        startTime = (new Date()).getTime();
    }

    public Long getStartTime(){
        return startTime;
    }

    /**
     * Set the uploaded status of the matching account type
     * @param accountType   : Which account the status is set
     * @param isUploaded    : Which status is set to
     */
    public void setUploaded(int accountType, boolean isUploaded){
        for(int i = 0; i < uploadStatus.size(); i++){
            if(profile.getAccount(i).getAccountType() == accountType){
                uploadStatus.set(i, Boolean.valueOf(isUploaded));
            }
        }
    }

    /**
     * Returns true if all uploaded status in the Upload is Uploaded
     * @return  : is the upload complete
     */
    public boolean isUploadComplete(){
        for(Boolean bool : uploadStatus){
            if(bool == false){
                return false;
            }
        }
        return true;
    }
}
