package com.oneclickupload.danielhsiao.oneclickupload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Database Class to access the database in the app
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "Database.db";
    public static final String TABLE_NAME_PROFILE = "PROFILE";
    public static final String TABLE_NAME_PROFILE_CHILD = "ACCOUNT";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "PROFILE_NAME";
    public static final String COL_TEXT = "TEXT";
    public static final String COL_PROFILE_ID = "PROFILE_ID";
    public static final String COL_API = "API";
    public static final String COL_ACCOUNT = "ACCOUNT";
    public static final String COL_PASSWORD = "PASSWORD";

    private static final String CREATE_TABLES1 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROFILE + "("    + COL_ID + " INTEGER PRIMARY KEY,"
                                                                                                            + COL_NAME + " TEXT NOT NULL,"
                                                                                                            + COL_TEXT + " TEXT);";
    private static final String CREATE_TABLES2 =  "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROFILE_CHILD + "("  + COL_PROFILE_ID + " INTEGER,"
                                                                                                                + COL_ID + " INTEGER PRIMARY KEY, "
                                                                                                                + COL_API + " INTEGER NOT NULL, "
                                                                                                                + COL_ACCOUNT + " TEXT NOT NULL,"
                                                                                                                + COL_PASSWORD + " TEXT NOT NULL,"
                                                                                                                + " CONSTRAINT fk_profile_id FOREIGN KEY(" + COL_ID + ") " +
                                                                                                                                            "REFERENCES " + TABLE_NAME_PROFILE + "(" + COL_ID + "));";

    /**
     * Constructor of the DatabaseHelper class
     * @param context : Application Context
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    /**
     * Method to be called when the class is created
     * @param db    :   The database object
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES1);
        db.execSQL(CREATE_TABLES2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Method to add a newly created profile to the database for persistence
     * @param p : The profile to be added
     * @return  : The profile ID in the database
     */
    public Integer addProfile(Profile p){
        Integer profileID = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        values.put(COL_TEXT, p.getText());
        db.insert(TABLE_NAME_PROFILE, null, values);
        profileID = getLastInsertedRowID(TABLE_NAME_PROFILE);

        for(Account a : p.getAccounts()){
            addAccount(a, profileID);
        }
        p.setProfileID(profileID);
        return profileID;
    }

    /**
     * Get the latest autoincremented ID of the table
     * @param table : The table of the Last Inserted Row
     * @return  : The ID
     */
    private Integer getLastInsertedRowID(String table){
        Integer id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectID = "SELECT last_insert_rowid() FROM " + table;
        Cursor cursor = db.rawQuery(selectID, null);
        if(cursor.moveToFirst()){
            id = cursor.getInt(0);
        }
        return id;
    }

    /**
     * Method to add a newly created account to the database for persistence
     * @param a             : The account to be added
     * @param profile_ID    : the profile ID that account belongs to.
     * @return
     */
    public Integer addAccount(Account a, int profile_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_ID, profile_ID);
        values.put(COL_API, a.getAccountType());
        values.put(COL_ACCOUNT, a.getAccount_id());
        values.put(COL_PASSWORD, a.getPassword());
        db.insert(TABLE_NAME_PROFILE_CHILD, null, values);

        String selectID = "SELECT * FROM " + TABLE_NAME_PROFILE_CHILD;
        Cursor cursor = db.rawQuery(selectID, null);
        int count = cursor.getCount();

        Integer id = getLastInsertedRowID(TABLE_NAME_PROFILE_CHILD);
        a.setID(id);
        return id;
    }

    /**
     * Get all the profile information from the database and unmarshall them into objects
     * @return  : The list of all profiles in the database
     */
    public List<Profile> getProfiles(){
        List<Profile> profiles = new ArrayList<Profile>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SELECT * FROM " + TABLE_NAME_PROFILE;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String text = cursor.getString(2);
                Profile profile = new Profile(id, name, text);
                profile.setAccounts(getAccountsByProfileID(id));
                profiles.add(profile);

            } while(cursor.moveToNext());
        }
        return profiles;
    }

    /**
     * Get the profile last inserted into the database
     * @return  : The profile
     */
    public Profile getLastInsertedProfile(){
        int profileID = getLastInsertedRowID(TABLE_NAME_PROFILE);
        Profile p = getProfileByID(profileID);
        return p;
    }

    /**
     * Get the profile based on profile ID
     * @param id    : The ID of the profile to be retrieved
     * @return      : The profile
     */
    public Profile getProfileByID(int id){
        Profile p;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor profileCursor = db.query(TABLE_NAME_PROFILE, new String[]{COL_ID, COL_NAME, COL_TEXT}, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null,null);
        if(profileCursor.moveToFirst()){
            String profileName = profileCursor.getString(1);
            String text = profileCursor.getString(2);
            p = new Profile(id, profileName, text);

            List<Account> accounts = getAccountsByProfileID(id);
            p.setAccounts(accounts);
            return p;
        } else {
            return null;
        }
    }

    /**
     * Get the accounts information from the databased of a certain profile and unmarshall the accounts into objects
     * @param profileID : The profile ID the accounts belong to
     * @return          : A list of accounts
     */
    public List<Account> getAccountsByProfileID(int profileID){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Account> accounts = new ArrayList<>();
        Cursor accountCursor = db.query(TABLE_NAME_PROFILE_CHILD, new String[]{COL_ID, COL_API, COL_ACCOUNT, COL_PASSWORD, COL_PROFILE_ID}, COL_PROFILE_ID + "=?", new String[]{String.valueOf(profileID)}, null, null, null,null);
        if(accountCursor.moveToFirst()){
            do{
                int aID = accountCursor.getInt(0);
                int accountType = accountCursor.getInt(1);
                String accountID = accountCursor.getString(2);
                String password = accountCursor.getString(3);
                accounts.add(new Account(aID, accountType, accountID, password));
            } while(accountCursor.moveToNext());
        }
        return accounts;
    }

    public void removeProfile(Profile p){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            db.delete(TABLE_NAME_PROFILE_CHILD, COL_PROFILE_ID + "=?", new String[]{Integer.toString(p.getProfileID())});
            db.delete(TABLE_NAME_PROFILE, COL_ID + "=?", new String[]{Integer.toString(p.getProfileID())});
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void updateProfile(Profile p){
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(COL_NAME, p.getName());
            values.put(COL_TEXT, p.getText());
            db.update(TABLE_NAME_PROFILE, values, COL_ID + "=?", new String[]{Integer.toString(p.getProfileID())});
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
