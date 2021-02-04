package com.francesco.pickem.Services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francesco.pickem.Models.ImageValidator;
import com.francesco.pickem.Models.Sqlite_Match;
import com.francesco.pickem.Models.Sqlite_MatchDay;

import java.util.ArrayList;

public class SQLite extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper: ";


    private static final String DB_NAME = "Pickem_LocalDB";
    private static final int DB_VERSION = 2;

    //tabella per le validazioni/update delle immagini
    static final String TABLE_IMAGE_REGIONS = "TABLE_IMAGE_REGIONS";
    static final String TABLE_IMAGE_TEAMS = "TABLE_IMAGE_TEAMS";
    private static final String NAME = "NAME";
    private static final String DATE = "DATE";

    //tabelle Matches
    static final String TABLE_MATCH_DAYS = "TABLE_MATCH_DAYS";
    private static final String YEAR = "YEAR";
    private static final String REGION = "REGION";
    private static final String DAY = "DAY";

    static final String TABLE_MATCHES = "TABLE_MATCHES";
    private static final String MATCH_ID = "MATCH_ID";


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

        String CREATE_TABLE_MATCH_DAYS = "CREATE TABLE " + TABLE_MATCH_DAYS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + YEAR + " TEXT ,  "
                + REGION + " TEXT ,  "
                + DAY + " TEXT  );";

        String CREATE_TABLE_MATCHES = "CREATE TABLE " + TABLE_MATCHES +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REGION + " TEXT ,  "
                + DAY + " TEXT ,  "
                + MATCH_ID + " TEXT  );";



        db.execSQL(CREATE_TABLE_IMAGE_REGIONS);
        db.execSQL(CREATE_TABLE_IMAGE_TEAMS);
        db.execSQL(CREATE_TABLE_MATCH_DAYS);
        db.execSQL(CREATE_TABLE_MATCHES);


    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_REGIONS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_IMAGE_TEAMS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCH_DAYS);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_MATCHES);
        onCreate(sqLiteDatabase);
    }

    public void insertMatchDay(Sqlite_MatchDay matchDay){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(YEAR, matchDay.getYear());
        cv.put(REGION, matchDay.getRegion()  );
        cv.put(DAY, matchDay.getMatch_day()  );


        db.insert(TABLE_MATCH_DAYS, null, cv);
        db.close();

    }

    public void insertMatch(Sqlite_Match match){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(YEAR, match.getRegion());
        cv.put(REGION, match.getDay_id()  );
        cv.put(DAY, match.getMatch_datetime()  );

        db.insert(TABLE_MATCHES, null, cv);
        db.close();

    }

    public ArrayList<String> getMatchDays (String currentYear, String regionSelected ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_days = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ DAY +" FROM "+ TABLE_MATCH_DAYS +" WHERE "+ YEAR +" = ? AND " + REGION + " = ?" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{currentYear, regionSelected});
        if (cursor.moveToFirst()) {
            do {
                match_days .add( cursor.getString(0)) ;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return match_days;
    }

    public ArrayList<String> getMatchIds (String regionSelected, String day_ID ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_ids = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ DAY +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + MATCH_ID + " = ?" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{regionSelected, day_ID});
        if (cursor.moveToFirst()) {
            do {
                match_ids .add( cursor.getString(0)) ;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return match_ids;
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

