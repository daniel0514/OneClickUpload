package com.oneclickupload.danielhsiao.oneclickupload;


/**
 * Account Class to host information of Social Media Account in a profile
 */

public class Account{
    public final static int FACEBOOK_ACCOUNT = 1;
    public final static int TWITTER_ACCOUNT = 2;
    public final static int ALL_ACCOUNTS = 0;
    //The type of social media account
    private int accountType;

    private Integer id;
    //Account Username and Password for login. Not used now
    private String account_id;
    private String password;

    /**
     * Constructor for Account Class. Used to create an account before persistence.
     * Account ID is not yet assigned by the database
     * @param accountType   : Type of social media account
     * @param account_id    : Account Username
     * @param password      : Account Password
     */
    public Account(int accountType, String account_id, String password){
        this.accountType = accountType;
        this.account_id = account_id;
        this.password = password;
    }

    /**
     * Constructor for Account Class. Used to create an account that is already in the database
     * @param id            : Account ID in the database
     * @param accountType   : Type of social media account
     * @param account_id    : Account Username
     * @param password      : Account Password
     */
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

    /**
     * Return the Name of the Social Media the account is belonged to
     * @param id
     * @return
     */
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
