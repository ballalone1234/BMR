package com.example.bmr;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHandler extends SQLiteOpenHelper {
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "BMRdata";
    // Contacts table name
    private static final String TABLE_HISTORY = "contacts";
    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "time";
    private static final String KEY_BMR = "BMR";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_HISTORY + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_BMR + " TEXT" + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database (drop older table if existed, & create table again)
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);
        onCreate(db);
    }
    public List<historys> getAllContacts() {
        List<historys> contactList = new ArrayList<historys>();
        String selectQuery = "SELECT * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
// looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                historys contact = new historys();
                contact.setID(Integer.parseInt(cursor.getString(0)));
                contact.setName(cursor.getString(1));
                contact.setPhoneNumber(cursor.getString(2));
// Adding contact to list
                Log.i("DatabaseHandler", "getAllContacts: " + contact.getName());
                contactList.add(contact);
            } while (cursor.moveToNext());
        }
        Log.i("DatabaseHandler", "getAllContacts: " + contactList);
// return contact list
        return contactList;
    }

    public void addContact(historys contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_BMR, contact.getPhoneNumber());
// Inserting Row
        db.insert(TABLE_HISTORY, null, values);
        db.close(); // Closing database connection
    }

    public void deleteContact(historys contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[] { String.valueOf(contact.getID()) });
        db.close();
    }

    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("delete from "+ TABLE_HISTORY);
        db.close();
    }

}


