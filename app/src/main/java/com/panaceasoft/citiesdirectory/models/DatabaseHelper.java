package com.panaceasoft.citiesdirectory.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.panaceasoft.citiesdirectory.models.Users;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Panacea-Soft on 30/7/15.
 * Contact Email : teamps.is.cool@gmail.com
 */
public class DatabaseHelper extends SQLiteOpenHelper {


    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PSDatabase";

    // Table Name
    private static final String TABLE_USERS = "users";

    //Users Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_ABOUT_ME = "about_me";
    private static final String KEY_IS_BANDED = "is_banded";
    private static final String KEY_PROFILE_PHOTO = "profile_photo";
    private static final String KEY_BG_PHOTO = "background_photo";
    private static final String KEY_BILL_ADDRESS = "billing_address";
    private static final String KEY_DEL_ADDRESS = "delivery_address";

    //Users Table Create Statements
    private static final String CREATE_TABLE_USERS = "CREATE TABLE "
            + TABLE_USERS + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_USER_NAME + " TEXT,"
            + KEY_EMAIL + " TEXT," + KEY_ABOUT_ME + " TEXT," + KEY_IS_BANDED + " TEXT,"
            + KEY_PROFILE_PHOTO + " BLOB," + KEY_BG_PHOTO + " BLOB," + KEY_BILL_ADDRESS + " TEXT,"
            + KEY_DEL_ADDRESS + " TEXT" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USERS);

        onCreate(db);
    }

    //Add New User
    public void addUser(Users user) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_USER_NAME, user.getUser_name());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_ABOUT_ME, user.getAbout_me());
        values.put(KEY_IS_BANDED, user.getIs_banded());
        values.put(KEY_PROFILE_PHOTO, user.getProfile_photo());

        //Insert Into DB
        db.insert(TABLE_USERS, null,values);
        db.close();

    }

    //Get User By ID
    public Users getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_USERS, new String[]{KEY_USER_NAME, KEY_EMAIL, KEY_ABOUT_ME,
                        KEY_IS_BANDED, KEY_PROFILE_PHOTO, KEY_BG_PHOTO, KEY_BILL_ADDRESS, KEY_DEL_ADDRESS},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        Users user = new Users(Integer.parseInt(cursor.getString(0)),cursor.getString(1), cursor.getString(2), cursor.getString(3),
                Integer.parseInt(cursor.getString(4)),cursor.getString(5));

        return user;

    }

    //Get All
    public ArrayList<Users> getAllUsers() {
        ArrayList<Users> usersList = new ArrayList<Users>();
        String query = "Select * from " + TABLE_USERS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if(cursor.moveToFirst()) {
            do{
                Users user = new Users();
                user.setId(Integer.parseInt(cursor.getString(0)));
                user.setUser_name(cursor.getString(1));
                user.setEmail(cursor.getString(2));
                user.setAbout_me(cursor.getString(3));
                user.setIs_banded(Integer.parseInt(cursor.getString(4)));
                user.setProfile_photo(cursor.getString(5));

                usersList.add(user);
            } while (cursor.moveToNext());
        }

        return usersList;
    }

    //Update User
    public int updateUser(Users user) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, user.getId());
        values.put(KEY_USER_NAME, user.getUser_name());
        values.put(KEY_EMAIL, user.getEmail());
        values.put(KEY_ABOUT_ME, user.getAbout_me());
        values.put(KEY_IS_BANDED, user.getIs_banded());
        values.put(KEY_PROFILE_PHOTO, user.getProfile_photo());

        return db.update(TABLE_USERS, values,KEY_ID + " =?",
                new String[] { String.valueOf(user.getId())});

    }

    //Delete User
    public void deleteUser(Users user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_USERS, KEY_ID + " =?", new String[]{String.valueOf(user.getId())});
        db.close();
    }

    //Delete All User
    public void deleteAllUser(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_USERS);
        db.close();
    }

    //Get User Count
    public int getUserCount() {
        String countQuery = "Select * FROM " + TABLE_USERS;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
