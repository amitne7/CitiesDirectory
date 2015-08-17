package com.panaceasoft.citiesdirectory.models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Panacea-Soft on 8/17/15.
 * Contact Email : teamps.is.cool@gmail.com
 */


public class FavouriteHelper extends SQLiteOpenHelper {

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "PSDatabase";

    // Table Name
    private static final String TABLE_NAME = "favourites";

    //Users Table Columns
    private static final String KEY_ID = "id";
    private static final String KEY_FAVOURITE = "favourite";

    //Users Table Create Statements
    private static final String CREATE_TABLE = "CREATE TABLE "
            + TABLE_NAME + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_FAVOURITE + " TEXT"
            + ")";

    public FavouriteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_NAME);

        onCreate(db);
    }

    //Add New
    public void add(int id, String favourite) {
        SQLiteDatabase db = this.getReadableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_FAVOURITE, favourite);

        //Insert Into DB
        db.insert(TABLE_NAME, null,values);
        db.close();

    }

    //Get By ID
    public String get(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_NAME, new String[]{KEY_FAVOURITE},
                KEY_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);

        if(cursor != null) {
            cursor.moveToFirst();
        }

        return cursor.getString(1);

    }

    //Update
    public int update(int id, String favourite) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ID, id);
        values.put(KEY_FAVOURITE, favourite);

        return db.update(TABLE_NAME, values,KEY_ID + " =?",
                new String[] { String.valueOf(id)});

    }

    //Delete User
    public void delete(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, KEY_ID + " =?", new String[]{String.valueOf(id)});
        db.close();
    }

    //Delete All User
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
        db.close();
    }

    //Get User Count
    public int getCount() {
        String countQuery = "Select * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();

        return count;
    }
}
