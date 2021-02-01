package com.francesco.pickem.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francesco.pickem.Models.ImageValidator;

public class SQLite extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper: ";


    private static final String DB_NAME = "Pickem_LocalDB";
    private static final int DB_VERSION = 2;

    //tabella per le validazioni/update delle immagini
    static final String TABLE_IMAGE_REGIONS = "TABLE_IMAGE_REGIONS";
    static final String TABLE_IMAGE_TEAMS = "TABLE_IMAGE_TEAMS";
    private static final String NAME = "NAME";
    private static final String DATE = "DATE";

    public SQLite(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }



    private void createTables(SQLiteDatabase db){
        //creating tables
        String CREATE_TABLE_IMAGE_REGIONS = "CREATE TABLE " + TABLE_IMAGE_REGIONS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT ,  "
                + DATE + " TEXT  );";

        String CREATE_TABLE_IMAGE_TEAMS = "CREATE TABLE " + TABLE_IMAGE_TEAMS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME + " TEXT ,  "
                + DATE + " TEXT  );";

        db.execSQL(CREATE_TABLE_IMAGE_REGIONS);
        db.execSQL(CREATE_TABLE_IMAGE_TEAMS);


    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_REGIONS);
        //sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_TEAMS);
        onCreate(sqLiteDatabase);
    }

    public void initializeTableRegions(){

    }

    public void insertImageRegion(ImageValidator imageValidator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, imageValidator.getName());
        cv.put(DATE, imageValidator.getDate()  );

        db.insert(TABLE_IMAGE_REGIONS, null, cv);
        db.close();

    }

    public void insertImageTeam(ImageValidator imageValidator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, imageValidator.getName());
        cv.put(DATE, imageValidator.getDate()  );

        db.insert(TABLE_IMAGE_TEAMS, null, cv);
        db.close();

    }



    public void updateImageRegion(ImageValidator imageValidator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, imageValidator.getName());
        cv.put(DATE, imageValidator.getDate()  );

        db.update(TABLE_IMAGE_REGIONS, cv, null, null);
        db.close();
    }

    public void updateImageTeams(ImageValidator imageValidator){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NAME, imageValidator.getName());
        cv.put(DATE, imageValidator.getDate()  );

        db.update(TABLE_IMAGE_TEAMS, cv, null, null);
        Log.d(TAG, "updateImageTeams: done.");
        db.close();
    }



    public long getMillisCreationRegionImage ( String team_name){
        SQLiteDatabase db = this.getReadableDatabase();
        long image_creation_date =0L;
        //                                 0
        String selectQuery = "SELECT "+ DATE +" FROM "+ TABLE_IMAGE_REGIONS +" WHERE "+ NAME +" = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String []{team_name});
        if (cursor.moveToFirst()) {
            do {
                image_creation_date = Long.parseLong( cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return image_creation_date;
    }

    public long getMillisCreationTeamImage ( String team_name){
        SQLiteDatabase db = this.getReadableDatabase();
        long image_creation_date =0L;
        //                                 0
        String selectQuery = "SELECT "+ DATE +" FROM "+ TABLE_IMAGE_TEAMS +" WHERE "+ NAME +" = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String []{team_name});
        if (cursor.moveToFirst()) {
            do {
                image_creation_date = Long.parseLong( cursor.getString(0));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return image_creation_date;
    }

}

