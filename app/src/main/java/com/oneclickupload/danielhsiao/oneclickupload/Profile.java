package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.ArrayList;
import java.util.List;

/**
 * The Profile class to contain Profile information
 */

public class Profile {
    //The profile ID in the database
    private Integer profileID;
    //The profile name
    private String profileName;
    //The list of accounts in the profile
    private List<Account> accounts;
    //Post Text
    private String text;
    //Is profile Modified
    private Boolean isModified = false;

    /**
     * Constructor for profile that is already in the database
     * @param profileID : The ID of the profile in the database
     * @param name      : The profile name
     */
    public Profile(int profileID, String name, String text){
        this.profileID = profileID;
        profileName = name;
        accounts = new ArrayList<>();
        this.text = text;
    }

    /**
     * Constructor for profile that is not yet persisted
     * @param name : The name of the profile
     */
    public Profile(String name, String text){
        this.profileID = null;
        profileName = name;
        accounts = new ArrayList<>();
        this.text = text;
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

    /**
     * Adding an account to the profile
     * @param a : Account to be added
     */
    public void addAccount(Account a){
        this.accounts.add(a);
    }

    public String getText(){
        return text;
    }

    public void setText(String text){
        this.text = text;
    }

    /**
     * Replace the whole list of accounts in the profile
     * @param accounts  : The list of accounts
     */
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

    public Boolean isModified(){
        return isModified;
    }
    public void setIsModified(Boolean isModified){
        this.isModified = isModified;
    }
}
