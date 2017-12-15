package com.deewankshi.demo;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Dell on 12/14/2017.
 */

public class DatabaseHandler extends SQLiteOpenHelper {

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "Brandslist";

    // Contacts table name
    private static final String TABLE_BRANDS = "Brands";

    // Contacts Table Columns names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_DESC = "desc";
    private static final String KEY_TIME = "timeframe";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Creating Tables
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_BRANDS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT,"
                + KEY_DESC + " TEXT," + KEY_TIME + " TEXT" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    // Upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BRANDS);
        // Create tables again
        onCreate(db);
    }

    /**
     * All CRUD(Create, Read, Update, Delete) Operations
     */

    // Adding new brnads
    void adddata(DataModel brands) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, brands.getTitle()); // Brand Name
        values.put(KEY_ID, brands.getId()); // Brand Name
        values.put(KEY_DESC, brands.getDescription()); // Brand Desc
        values.put(KEY_TIME, brands.getTimeframe()); // Brand Create ON

        // Inserting Row
        db.insert(TABLE_BRANDS, null, values);
        db.close(); // Closing database connection
    }

    // Getting single brand
    DataModel getdata(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_BRANDS, new String[]{KEY_ID,
                        KEY_NAME, KEY_DESC, KEY_TIME}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null)
            cursor.moveToFirst();

        DataModel contact = new DataModel(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3));
        // return contact
        return contact;
    }

    // Getting All Brnads
    public List<DataModel> getAllBrands() {
        List<DataModel> contactList = new ArrayList<DataModel>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_BRANDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                DataModel model = new DataModel();
                model.setId(Integer.parseInt(cursor.getString(0)));
                model.setTitle(cursor.getString(1));
                model.setDescription(cursor.getString(2));
                model.setTimeframe(cursor.getString(3));
                // Adding brand to list
                contactList.add(model);
            } while (cursor.moveToNext());
        }

        // return brnad list
        return contactList;
    }

    // Updating single brands
    public int updateBrand(DataModel brand) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, brand.getTitle());
        values.put(KEY_DESC, brand.getDescription());
        values.put(KEY_TIME, brand.getTimeframe());

        // updating row
        return db.update(TABLE_BRANDS, values, KEY_ID + " = ?",
                new String[]{String.valueOf(brand.getId())});
    }

    // Deleting single brands
    public void deleteBrands(DataModel contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_BRANDS, KEY_ID + " = ?",
                new String[]{String.valueOf(contact.getId())});
        db.close();
    }


    public boolean checkAlreadyExist(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  id FROM " + TABLE_BRANDS + " where id=?";
        Cursor cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(id)});
        boolean exists = (cursor.getCount() > 0);
        return exists;
    }


}
