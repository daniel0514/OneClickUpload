package com.oneclickupload.danielhsiao.oneclickupload;

import android.net.Uri;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Daniel Hsiao on 2017-01-24.
 */

public class Upload {
    public static final int UPLOADED = 2;
    public static final int UPLOADING = 1;
    public static final int FAILED = 3;
    private Profile profile;
    private Uri imageURI;
    private ArrayList<Boolean> uploadStatus = new ArrayList<>();
    private long startTime;

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

    public long getStartTime(){
        return startTime;
    }

    public void setUploaded(int accountType, boolean isUploaded){
        for(int i = 0; i < uploadStatus.size(); i++){
            if(profile.getAccount(i).getAccountType() == accountType){
                uploadStatus.set(i, Boolean.valueOf(isUploaded));
            }
        }
    }

    public boolean isUploadComplete(){
        for(Boolean bool : uploadStatus){
            if(bool == false){
                return false;
            }
        }
        return true;
    }
}
