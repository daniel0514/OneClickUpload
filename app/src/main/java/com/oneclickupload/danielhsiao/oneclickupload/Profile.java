package com.oneclickupload.danielhsiao.oneclickupload;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Daniel Hsiao on 2017-01-15.
 */

public class Profile {
    private int profileID;
    private String profileName;
    private List<Account> accounts;

    public Profile(int profileID, String name){
        this.profileID = profileID;
        profileName = name;
        accounts = new ArrayList<>();
    }

    public int getCount (){
        return accounts.size();
    }

    public void addAccount(Account a){
        accounts.add(a);
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
