package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Hsiao on 2017-01-15.
 */

public class Profile {
    private Integer profileID;
    private String profileName;
    private List<Account> accounts;

    public Profile(int profileID, String name){
        this.profileID = profileID;
        profileName = name;
        accounts = new ArrayList<>();
    }
    public Profile(String name){
        this.profileID = null;
        profileName = name;
        accounts = new ArrayList<>();
    }

    public int getProfileID(){
        return profileID;
    }

    public void setProfileID(Integer id){
        profileID = id;
    }

    public int getCount (){
        return accounts.size();
    }

    public void addAccount(Account a){
        this.accounts.add(a);
    };

    public void setAccounts(List<Account> accounts){
        this.accounts = accounts;
    }

    public String getName(){
        return profileName;
    }

    public List<Account> getAccounts(){
        return accounts;
    }

    public Account getAccount(int position){
        return accounts.get(position);
    }
}
