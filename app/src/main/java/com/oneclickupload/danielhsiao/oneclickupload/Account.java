package com.oneclickupload.danielhsiao.oneclickupload;

/**
 * Created by Daniel Hsiao on 2017-01-17.
 */

public class Account{
    public final static int FACEBOOK_ACCOUNT = 1;
    public final static int TWITTER_ACCOUNT = 2;
    public final static int ALL_ACCOUNTS = 0;
    private Integer id;
    private String account_id;
    private String password;
    private int accountType;

    public Account(int accountType, String account_id, String password){
        this.accountType = accountType;
        this.account_id = account_id;
        this.password = password;
    }

    public Account(int id, int accountType, String account_id, String password){
        this.id = id;
        this.accountType = accountType;
        this.account_id = account_id;
        this.password = password;
    }

    public void setID(int id){
        this.id = id;
    }

    public int getAccountType(){
        return accountType;
    }

    public String getAccount_id(){
        return account_id;
    }

    public String getPassword(){
        return password;
    }

    public String getAPIName(int id){
        switch(id){
            case(FACEBOOK_ACCOUNT):
                return "Facebook";
            case(TWITTER_ACCOUNT):
                return "Twitter";
            default:
                return "Unknown API";
        }
    }

}
