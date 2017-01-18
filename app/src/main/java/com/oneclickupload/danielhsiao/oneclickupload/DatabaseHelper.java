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
 * Created by Daniel Hsiao on 2017-01-15.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String DATABASE_NAME = "Database.db";
    public static final String TABLE_NAME_PROFILE = "PROFILE";
    public static final String TABLE_NAME_PROFILE_CHILD = "PROFILE_CHILD";
    public static final String COL_PROFILE_ID = "PROFILE_ID";
    public static final String COL_ID = "ID";
    public static final String COL_NAME = "PROFILE_NAME";
    public static final String COL_API = "API";
    public static final String COL_ACCOUNT = "ACCOUNT";
    public static final String COL_PASSWORD = "PASSWORD";

    private static final String CREATE_TABLES = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROFILE + "("    + COL_ID + " INTEGER PRIMARY KEY,"
                                                                                                            + COL_NAME + " TEXT NOT NULL);\n"+
                                                "CREATE TABLE IF NOT EXISTS " + TABLE_NAME_PROFILE_CHILD + "("  + COL_PROFILE_ID + " INTEGER,"
                                                                                                                + COL_ID + " INTEGER PRIMARY KEY, "
                                                                                                                + COL_API + " INTEGER NOT NULL, "
                                                                                                                + COL_ACCOUNT + " TEXT NOT NULL,"
                                                                                                                + COL_PASSWORD + " TEXT NOT NULL,"
                                                                                                                + " CONSTRAINT fk_profile_id FOREIGN KEY(" + COL_ID + ") " +
                                                                                                                                            "REFERENCES " + TABLE_NAME_PROFILE + "(" + COL_ID + "));";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version, DatabaseErrorHandler errorHandler) {
        super(context, name, factory, version, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Integer addProfile(Profile p){
        Integer profileID = null;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_NAME, p.getName());
        db.insert(TABLE_NAME_PROFILE, null, values);
        profileID = getLastInsertedRowID();

        for(Account a : p.getAccounts()){
            addAccount(a, profileID);
        }
        return profileID;
    }

    private Integer getLastInsertedRowID(){
        Integer id = null;
        SQLiteDatabase db = this.getReadableDatabase();
        String selectID = "SELECT SELECT last_insert_rowid()";
        Cursor cursor = db.rawQuery(selectID, null);
        if(cursor.moveToFirst()){
            id = cursor.getInt(1);
        }
        return id;
    }

    public Integer addAccount(Account a, int profile_ID){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_PROFILE_ID, profile_ID);
        values.put(COL_API, a.getAccountType());
        values.put(COL_ACCOUNT, a.getAccount_id());
        values.put(COL_PASSWORD, a.getPassword());
        db.insert(TABLE_NAME_PROFILE_CHILD, null, values);
        Integer id = getLastInsertedRowID();
        a.setID(id);
        return id;
    }

    public List<Profile> getProfiles(){
        List<Profile> profiles = new ArrayList<Profile>();
        SQLiteDatabase db = this.getReadableDatabase();
        String select = "SEELCT * FROM " + TABLE_NAME_PROFILE;
        Cursor cursor = db.rawQuery(select, null);
        if(cursor.moveToFirst()){
            do{
                int id = cursor.getInt(1);
                String name = cursor.getString(2);
                Profile profile = new Profile(id, name);

                Cursor accountCursor = db.query(TABLE_NAME_PROFILE_CHILD, new String[]{COL_ID, COL_API, COL_ACCOUNT, COL_PASSWORD}, COL_ID + "=?", new String[]{String.valueOf(id)}, null, null, null,null);
                if(accountCursor.moveToFirst()){
                    do{
                        int accountType = cursor.getInt(1);
                        String accountID = cursor.getString(2);
                        String password = cursor.getString(3);
                        profile.addAccount(new Account(accountType, accountID, password));
                    } while(accountCursor.moveToNext());
                }
                profiles.add(profile);

            } while(cursor.moveToNext());
        }
        return profiles;
    }

}
