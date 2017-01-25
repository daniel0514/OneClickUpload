package com.oneclickupload.danielhsiao.oneclickupload;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by Daniel Hsiao on 2017-01-24.
 */

public class Upload {
    public static final int UPLOADED = 2;
    public static final int UPLOADING = 1;
    private Profile profile;
    private Uri imageURI;
    private ArrayList<Boolean> uploadStatus = new ArrayList<>();

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
}
