package com.francesco.pickem.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.Models.RegionNotifications;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper: ";


    private static final String DB_NAME = "Pickem_LocalDB";
    private static final int DB_VERSION = 1;

    //tabella per le preferenze dello user
    static final String TABLE_ACCOUNT_SETTINGS = "TABLE_ACCOUNT_SETTINGS";
    private static final String EMAIL = "EMAIL";
    private static final String USERNAME = "USERNAME";
    private static final String LEAGUES_OF_INTEREST = "LEAGUES_OF_INTEREST";

    //tabella teams
    static final String TABLE_TEAMS = "TABLE_TEAMS";
    private static final String TEAM_ID = "TEAM_ID";
    private static final String TEAM_REGION = "TEAM_REGION";
    private static final String TEAM_NAME = "TEAM_NAME";
    private static final String TEAM_LOGO = "TEAM_LOGO";

    //tabella notifiche Regions
    static final String TABLE_NOTIFICATIONS_REGION = "TABLE_NOTIFICATIONS_REGION";
    private static final String NOTIFICATIONS_REGION_NAME  = "NOTIFICATIONS_REGION_NAME";
    private static final String NOTIFICATION_FIRST_MATCH_OTD  = "NOTIFICATION_FIRST_MATCH_OTD";
    private static final String NOTIFICATION_MORNING     = "NOTIFICATION_MORNING";
    private static final String NOTIFICATION_NO_CHOICE_MADE  = "NOTIFICATION_NO_CHOICE_MADE";

    //tabella notifiche Teams
    static final String TABLE_NOTIFICATIONS_TEAM = "TABLE_NOTIFICATIONS_TEAM";
    private static final String NOTIFICATIONS_TEAM_NAME  = "NOTIFICATIONS_TEAM_NAME";
    private static final String NOTIFICATION_MORNING_REMINDER  = "NOTIFICATION_MORNING_REMINDER";
    private static final String NOTIFICATION_AS_TEAM_PLAYS = "NOTIFICATION_AS_TEAM_PLAYS";

    //tabella dei match
    private static final String TABLE_MATCH = "TABLE_MATCH";
    private static final String MATCH_DATE = "MATCH_DATE";
    private static final String MATCH_TIME = "MATCH_TIME";
    private static final String MATCH_ID = "MATCH_ID";
    private static final String MATCH_SEASON = "MATCH_SEASON";
    private static final String MATCH_TEAM_1 = "MATCH_TEAM_1";
    private static final String MATCH_TEAM_2 = "MATCH_TEAM_2";
    private static final String MATCH_REGION = "MATCH_REGION";
    private static final String MATCH_PREDICTION = "MATCH_PREDICTION";
    private static final String MATCH_WINNER = "MATCH_WINNER";

    //tabella delle Regions
    private static final String TABLE_REGIONS = "TABLE_REGIONS";
    private static final String REGION_ID= "REGION_ID";
    private static final String REGION_NAME= "REGION_NAME";
    private static final String REGION_NAME_EXT= "REGION_NAME_EXT";
    private static final String REGION_LOGO = "REGION_LOGO";



    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null,DB_VERSION);
    }

    private void createTables(SQLiteDatabase db){
        //creating tables
        String CREATE_TABLE_ACCOUNT_SETTINGS = "CREATE TABLE " + TABLE_ACCOUNT_SETTINGS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + EMAIL + " TEXT ,  "
                + USERNAME + " TEXT ,  "
                + LEAGUES_OF_INTEREST + " TEXT  );";

        String CREATE_TABLE_TEAMS = "CREATE TABLE " + TABLE_TEAMS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TEAM_ID + " TEXT ,  "
                + TEAM_REGION + " TEXT , "
                + TEAM_NAME + " TEXT , "
                + TEAM_LOGO + " TEXT   );";

        String CREATE_TABLE_NOTIFICATIONS_REGION = "CREATE TABLE " + TABLE_NOTIFICATIONS_REGION +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOTIFICATIONS_REGION_NAME            + " TEXT UNIQUE, "
                + NOTIFICATION_FIRST_MATCH_OTD              + " INTEGER , "
                + NOTIFICATION_MORNING      + " INTEGER , "
                + NOTIFICATION_NO_CHOICE_MADE        + " INTEGER );";

        String CREATE_TABLE_NOTIFICATIONS_TEAM = "CREATE TABLE " + TABLE_NOTIFICATIONS_TEAM +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NOTIFICATIONS_TEAM_NAME            + " TEXT , "
                + NOTIFICATION_MORNING_REMINDER              + " INTEGER , "
                + NOTIFICATION_AS_TEAM_PLAYS        + " INTEGER );";

        String CREATE_TABLE_MATCH = "CREATE TABLE " + TABLE_MATCH +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MATCH_SEASON + " TEXT NOT NULL, "
                + MATCH_REGION + " TEXT NOT NULL,  "
                + MATCH_ID + " INTEGER UNIQUE NOT NULL ,  "
                + MATCH_DATE + " TEXT , "
                + MATCH_TIME + " TEXT , "
                + MATCH_TEAM_1 + " TEXT , "
                + MATCH_TEAM_2 + " TEXT , "
                + MATCH_PREDICTION + " TEXT , "
                + MATCH_WINNER + " TEXT   );";

        String CREATE_TABLE_REGIONS = "CREATE TABLE " + TABLE_REGIONS +
                "(" + "ID" + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + REGION_ID + " TEXT ,  "
                + REGION_LOGO + " TEXT , "
                + REGION_NAME_EXT + " TEXT , "
                + REGION_NAME + " TEXT   );";

        db.execSQL(CREATE_TABLE_ACCOUNT_SETTINGS);
        db.execSQL(CREATE_TABLE_TEAMS);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_REGION);
        db.execSQL(CREATE_TABLE_NOTIFICATIONS_TEAM);
        db.execSQL(CREATE_TABLE_MATCH);
        db.execSQL(CREATE_TABLE_REGIONS);

    }



    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        createTables(sqLiteDatabase);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTIFICATIONS_REGION);
        onCreate(sqLiteDatabase);
    }

    public void initializeTableRegions(){



    }

    public void insertRegion(RegionDetails region){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(REGION_ID, region.getRegion_ID());
        cv.put(REGION_LOGO, region.getRegion_logo()  );
        cv.put(REGION_NAME, region.getRegion_name());
        cv.put(REGION_NAME_EXT, region.getRegion_name_ext());


        db.insert(TABLE_REGIONS, null, cv);
        db.close();

    }



    public void initializeNotificationRegion(String regionName ){
        Log.d(TAG, "initializeNotificationRegion: initializing notification region for region: "+regionName);
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTIFICATIONS_REGION_NAME, regionName);
        cv.put(NOTIFICATION_FIRST_MATCH_OTD, 0);
        cv.put(NOTIFICATION_NO_CHOICE_MADE, 1  );
        cv.put(NOTIFICATION_MORNING, 0  );

        db.insert(TABLE_NOTIFICATIONS_REGION, null, cv);
        db.close();
    }

    public void initializeNotificationTeams(TeamNotification teamNotification){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTIFICATIONS_TEAM_NAME, teamNotification.getTeam_name());
        cv.put(NOTIFICATION_MORNING_REMINDER, 0  );
        cv.put(NOTIFICATION_AS_TEAM_PLAYS, 0  );

        db.insert(TABLE_NOTIFICATIONS_REGION, null, cv);
        db.close();
    }

    public void initializeAccountSettings(UserGeneralities userGeneralities){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(EMAIL, userGeneralities.getEmail());
        cv.put(USERNAME, userGeneralities.getUsername()  );
        cv.put(LEAGUES_OF_INTEREST, fromArrayListToString(userGeneralities.getChoosen_regions())   );

        db.insert(TABLE_ACCOUNT_SETTINGS, null, cv);
        db.close();
    }

    public void updateSettingsNotification(RegionNotifications regionNotifications){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(NOTIFICATIONS_REGION_NAME, regionNotifications.getRegion_name());
        cv.put(NOTIFICATION_FIRST_MATCH_OTD, regionNotifications.getNotification_first_match_otd());
        cv.put(NOTIFICATION_NO_CHOICE_MADE, regionNotifications.getNo_choice_made()  );
        cv.put(NOTIFICATION_MORNING, regionNotifications.getNotification_morning_reminder()  );

        db.update(TABLE_NOTIFICATIONS_REGION, cv, null, null);
        db.close();
    }

    public void updateGeneralSettings(UserGeneralities sgo){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(USERNAME, sgo.getUsername());
        cv.put(LEAGUES_OF_INTEREST, fromArrayListToString(sgo.getChoosen_regions()));
        db.update(TABLE_NOTIFICATIONS_TEAM, cv, null, null);
        db.close();
    }

    public UserGeneralities getgeneralSettings (){


        UserGeneralities userGeneralities = new UserGeneralities();
        SQLiteDatabase db = this.getReadableDatabase();
            //                                 0              1                    2
            String selectQuery = "SELECT " + EMAIL +", "+ USERNAME +", " + LEAGUES_OF_INTEREST +" FROM " + TABLE_ACCOUNT_SETTINGS;
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {

                    UserGeneralities userGeneralitiesDatabase  = new UserGeneralities(cursor.getString(0), cursor.getString(1), fromStringToArrayList(cursor.getString(2)));
                    userGeneralities = userGeneralitiesDatabase;


                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
        Log.d(TAG, "getgeneralSettings: "+userGeneralities.getEmail());
        Log.d(TAG, "set_gen.getChoosen_regions().size(): "+userGeneralities.getChoosen_regions().size());
            return userGeneralities;

    }

    public String fromArrayListToString (ArrayList<String> arrayList){

        String string_of_chosen_regions = "";
        for (int i=0; i<arrayList.size(); i++){
            string_of_chosen_regions = string_of_chosen_regions + arrayList.get(i)+ " ";
        }
        return  string_of_chosen_regions;

    }

    public ArrayList<String> fromStringToArrayList(String stringStart){

        ArrayList <String> arrayList = new ArrayList<>();
        Log.d(TAG, "fromStringToArrayList: stringStart: "+stringStart);
        StringTokenizer stringTokenizer = new StringTokenizer(stringStart, " ");
        while (stringTokenizer.hasMoreTokens()){
            arrayList.add(stringTokenizer.nextToken());
        }

        Log.d(TAG, "fromStringToArrayList: arrayList.size: "+arrayList.size());

        return arrayList;


    }



    // dopo che hai modificato sqlite, modifica/update  il firebase


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
    /*private String fromRegionToTable(Integer region){
        String table_name="";

        switch(region) {

            case R.string.lec:
                table_name = TABLE_LEC;
            break;

            case R.string.lcs:
                table_name = TABLE_LCS;
            break;

            case R.string.lck:
                table_name = TABLE_LCK;
            break;

            case R.string.lpl:
                table_name = TABLE_LPL;
            break;

            case R.string.tcl:
                table_name = TABLE_TCL;
            break;

            case R.string.cblol:
                table_name = TABLE_CBLOL;
            break;

            case R.string.opl:
                table_name = TABLE_OPL;
            break;

            case R.string.lla:
                table_name = TABLE_LLA;
            break;

            case R.string.pcs:
                table_name = TABLE_PCS;
            break;

            case R.string.ljl:
                table_name = TABLE_LJL;
            break;

            case R.string.lcs_academy:
                table_name = TABLE_LCSA;
            break;

            case R.string.eum:
                table_name = TABLE_EUM;
            break;

        }
        return table_name;
    }*/

    public boolean checkifDataExisit() {
        SQLiteDatabase db = this.getReadableDatabase();
        String Query = "Select * from " + TABLE_MATCH ;
        Cursor cursor = db.rawQuery(Query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();
        return true;
    }

    public ArrayList<Cursor> getData(String Query){
        //get writable database
        SQLiteDatabase sqlDB = this.getWritableDatabase();
        String[] columns = new String[] { "message" };
        //an array list of cursor to save two cursors one has results from the query
        //other cursor stores error message if any errors are triggered
        ArrayList<Cursor> alc = new ArrayList<Cursor>(2);
        MatrixCursor Cursor2= new MatrixCursor(columns);
        alc.add(null);
        alc.add(null);

        try{
            String maxQuery = Query ;
            //execute the query results will be save in Cursor c
            Cursor c = sqlDB.rawQuery(maxQuery, null);

            //add value to cursor2
            Cursor2.addRow(new Object[] { "Success" });

            alc.set(1,Cursor2);
            if (null != c && c.getCount() > 0) {

                alc.set(0,c);
                c.moveToFirst();

                return alc ;
            }
            return alc;
        } catch(SQLException sqlEx){
            Log.d("printing exception", sqlEx.getMessage());
            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+sqlEx.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        } catch(Exception ex){
            Log.d("printing exception", ex.getMessage());

            //if any exceptions are triggered save the error message to cursor an return the arraylist
            Cursor2.addRow(new Object[] { ""+ex.getMessage() });
            alc.set(1,Cursor2);
            return alc;
        }
    }
}


