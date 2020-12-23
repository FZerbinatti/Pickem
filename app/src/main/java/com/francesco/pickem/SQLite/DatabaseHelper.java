package com.francesco.pickem.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class DatabaseHelper extends SQLiteOpenHelper {


    private static final String DB_NAME = "Pickem_LocalDB";
    private static final int DB_VERSION = 1;

    static final String TABLE_LEC = "TABLE_LEC";
    static final String TABLE_LCS = "TABLE_LCS";
    static final String TABLE_LPL = "TABLE_LPL";
    static final String TABLE_LCK = "TABLE_LCK";
    static final String TABLE_PREFERENCES = "TABLE_PREFERENCES";

    private static final String LEAGUE_TYPE = "LEAGUE_TYPE";

    //tabelle per stagioni regolari delle principali leghe
    private static final String MATCH_DATE = "LEAGUE_TYPE";
    private static final String MATCH_1_TIME = "MATCH_1_TIME";

    private static final String TEAM_1 = "TEAM_1";
    private static final String TEAM_2 = "TEAM_2";
    private static final String TEAM_3 = "TEAM_3";
    private static final String TEAM_4 = "TEAM_4";
    private static final String TEAM_5 = "TEAM_5";
    private static final String TEAM_6 = "TEAM_6";
    private static final String TEAM_7 = "TEAM_7";
    private static final String TEAM_8 = "TEAM_8";
    private static final String TEAM_9 = "TEAM_9";
    private static final String TEAM_10 = "TEAM_10";

    private static final String MATCH_1_PREDICTION = "MATCH_1_PREDICTION";
    private static final String MATCH_2_PREDICTION = "MATCH_2_PREDICTION";
    private static final String MATCH_3_PREDICTION = "MATCH_3_PREDICTION";
    private static final String MATCH_4_PREDICTION = "MATCH_4_PREDICTION";
    private static final String MATCH_5_PREDICTION = "MATCH_5_PREDICTION";

    //tabella per le preferenze dello user
    private static final String NOTIFICATION_LEC = "NOTIFICATION_LEC";
    private static final String NOTIFICATION_LCS = "NOTIFICATION_LCS";
    private static final String NOTIFICATION_LPL = "NOTIFICATION_LPL";
    private static final String NOTIFICATION_LCK = "NOTIFICATION_LCK";
    private static final String NOTIFICATION_MATCH_START = "NOTIFICATION_MATCH_START";
    private static final String NOTIFICATION_5_MINUTES = "NOTIFICATION_5_MINUTES";
    private static final String NOTIFICATION_ALL_GAMES = "NOTIFICATION_ALL_GAMES";
    private static final String NOTIFICATION_MORNING = "NOTIFICATION_MORNING";

    //array di acronimi delle leghe ceh l'use vuole seguire
    private static final String LEAGUES_OF_INTEREST = "LEAGUES_OF_INTEREST";
    private static final String USERNAME = "USERNAME";



 // array di squadre che vincono

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }



    private void createTables(SQLiteDatabase db){
        //creating tables
        String CREATE_TABLE_MOVIES = "CREATE TABLE " + TABLE_PREFERENCES +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + USERNAME + " TEXT ,  "
                + LEAGUES_OF_INTEREST + " TEXT , "
                + "MOVIE_DESCRIPTION" + " TEXT , "
                + "MOVIE_FAVOURITE" + " TEXT , "
                + "MOVIE_POSTER" + " TEXT   );";

        db.execSQL(CREATE_TABLE_MOVIES);

    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_PREFERENCES);
        onCreate(sqLiteDatabase);
    }


    /*public void addMovie ( Movie movie ){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("MOVIE_TITLE", movie.getMovie_title());
        values.put("MOVIE_YEAR", movie.getMovie_year());
        values.put("MOVIE_DESCRIPTION", movie.getMovie_plot());
        values.put("MOVIE_POSTER", movie.getMovie_poster());
        values.put("MOVIE_FAVOURITE", 0);


        db.insert(TABLE_PICKEM, null, values);
        db.close();
    }

    public void updateFavourite(Boolean favourite, String movie_name){
        SQLiteDatabase db = this.getWritableDatabase();
        String bool ="0";
        ContentValues cv = new ContentValues();
        if (favourite){
            bool = "1";
        }else {
            bool = "0";
        }
        cv.put("MOVIE_FAVOURITE", bool);
        db.update(TABLE_PICKEM, cv, "MOVIE_TITLE" + "= ?", new String[] {movie_name});
        db.close();
        Log.d(TAG, "updateFavourite: now movie parameter for facourite is  "+ bool);
    }

    public Boolean getFavourite ( String movie_title){
        SQLiteDatabase db = this.getReadableDatabase();
        String fav="0";
        //                                 0
        String selectQuery = "SELECT  MOVIE_FAVOURITE FROM MOVIES WHERE MOVIE_TITLE = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String []{movie_title});
        if (cursor.moveToFirst()) {
            do {
                fav = cursor.getString(0);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        if (fav.equals("0")){
            Log.d(TAG, "getFavourite: " + movie_title + " is NOT a fav");
            return false;
        }else {
            Log.d(TAG, "getFavourite: " + movie_title + " is a fav");
            return true;
        }


    }


    public void deleteMovie (String movie_title){
        SQLiteDatabase db = this.getWritableDatabase();
        String table = "TABLE_MOVIES";
        String whereClause = "MOVIE_TITLE=?";
        String[] whereArgs = new String[] { String.valueOf(movie_title) };
        db.delete(table, whereClause, whereArgs);
    }


    public ArrayList<Movie> getOfflineMovies() {
        Log.d(TAG, "getOrdini: entered query");

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Movie> listMovies = new ArrayList<Movie>();
        //                                 0           1              2                    3
        String selectQuery = "SELECT  MOVIE_TITLE,  MOVIE_YEAR, MOVIE_DESCRIPTION, MOVIE_POSTER FROM MOVIES";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor.getString(3), cursor.getString(2), cursor.getString(1), cursor.getString(0));
                listMovies.add(movie);
                Log.d(TAG, "getMovies: movie: "+movie);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return listMovies;

    }

    public Integer getTotalOfflineMovies (){
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, "MOVIES");
        db.close();
        Integer i = (int) (long) count;

        return i;
    }

    public boolean movieExist(String movie_name) {
        Log.d(TAG, "movieExist: entered query");

        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT * FROM MOVIES WHERE MOVIE_TITLE = ?";
        Cursor cursor = db.rawQuery(selectQuery, new String []{movie_name});
        if (cursor.moveToFirst()) {
            do {
                return  true;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return false;
    }

    public ArrayList<Movie> searchMovies(String movie_searched) {
        Log.d(TAG, "searchMovies: entered query");

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Movie> listMovies = new ArrayList<Movie>();
        //                                 0           1              2                    3
       // String selectQuery = "SELECT  MOVIE_TITLE,  MOVIE_YEAR, MOVIE_DESCRIPTION, MOVIE_POSTER FROM MOVIES";


        Cursor cursor = db.query(TABLE_PICKEM, new String[] {"MOVIE_TITLE", "MOVIE_YEAR", "MOVIE_DESCRIPTION", "MOVIE_POSTER" },
                "MOVIE_TITLE" + " LIKE ?", new String[] {"%" + movie_searched + "%"},
                null, null, null);



        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor.getString(3), cursor.getString(2), cursor.getString(1), cursor.getString(0));
                listMovies.add(movie);
                Log.d(TAG, "getMovies: movie: "+movie);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return listMovies;

    }


    public ArrayList<Movie> getFavouriteMovies() {
        Log.d(TAG, "getOrdini: entered query");

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList <Movie> listMovies = new ArrayList<Movie>();
        //                                 0           1              2                    3
        String selectQuery = "SELECT MOVIE_TITLE,  MOVIE_YEAR, MOVIE_DESCRIPTION, MOVIE_POSTER FROM MOVIES WHERE MOVIE_FAVOURITE = ?";

        Cursor cursor = db.rawQuery(selectQuery, new String []{"1"});
        if (cursor.moveToFirst()) {
            do {
                Movie movie = new Movie(cursor.getString(3), cursor.getString(2), cursor.getString(1), cursor.getString(0));
                listMovies.add(movie);
                Log.d(TAG, "getMovies: movie: "+movie);
            } while (cursor.moveToNext());
        }
        Log.d(TAG, "getFavouriteMovies: listMovies.size " +listMovies.size());
        cursor.close();
        db.close();
        return listMovies;

    }

*/


}
