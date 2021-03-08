package com.francesco.pickem.Services;

import android.content.ContentValues;
import android.content.Context;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.francesco.pickem.Models.RegionStats;
import com.francesco.pickem.Models.ImageValidator;
import com.francesco.pickem.Models.Sqlite_Match;
import com.francesco.pickem.Models.Sqlite_MatchDay;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static String TAG = "DatabaseHelper: ";

    private static final String DB_NAME = "Pickem_LocalDB";
    private static final int DB_VERSION = 7;

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
    private static final String TEAM1 = "TEAM1";
    private static final String TEAM2 = "TEAM2";
    private static final String PREDICTION = "PREDICTION";
    private static final String WINNER = "WINNER";


    static final String TABLE_MATCHES = "TABLE_MATCHES";
    private static final String MATCH_ID = "MATCH_ID";


    public DatabaseHelper(Context context) {
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
                + YEAR + " TEXT ,  "
                + REGION + " TEXT ,  "
                + DAY + " TEXT ,  "
                + MATCH_ID + " TEXT ,  "
                + TEAM1 + " TEXT ,  "
                + TEAM2 + " TEXT ,  "
                + PREDICTION+ " TEXT ,  "
                + WINNER + " TEXT  );";

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
        Log.d(TAG, "insertMatchDay: inserito: Year: "+matchDay.getYear() +" Region: " +matchDay.getRegion()+" Date: " +matchDay.getMatch_day());
        db.close();

    }

    public void insertMatch(Sqlite_Match match){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(YEAR, match.getYear());
        cv.put(REGION, match.getRegion());
        cv.put(DAY, match.getDay_id()  );
        cv.put(MATCH_ID, match.getMatch_datetime()  );
        cv.put(TEAM1, "");
        cv.put(TEAM2, "");
        cv.put(PREDICTION, "");
        cv.put(WINNER, "");

        db.insert(TABLE_MATCHES, null, cv);
        Log.d(TAG, "insertMatch: inserito: Year:  "+match.getYear()+ " Region: "+match.getRegion() +" Date: " +match.getDay_id()+" Datetime: " +match.getMatch_datetime());
        db.close();

    }

    public void insertMatchDetails(String region, String dateTime, String team1, String team2){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(TEAM1, team1);
        cv.put(TEAM2, team2);

        db.update(TABLE_MATCHES,  cv, REGION +" = ? AND "+ MATCH_ID + "= ? ", new String []{region, dateTime});
        Log.d(TAG, "insertMatchDetails: inserito: Region: "+region +" DateTime: " +dateTime +" team1: " +team1 + " team2: "+ team2);
        db.close();

    }

    public ArrayList<String> getTeamsForRegionMatchID(String region, String date){
        Log.d(TAG, "getTeamsForRegionMatchID: "+region +" date: " +date);
        ArrayList <String> teams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String team1 = "";
        String team2 = "";
        //                                 0            1
        String selectQuery = "SELECT "+ TEAM1 +" , " +TEAM2 +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + MATCH_ID + " = ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});
        //Log.d(TAG, "getTeamsForRegionMatchID: "+cursor.getCount());
        if (cursor.moveToFirst()) {
            do {

                team1 = cursor.getString(0);
                team2 = cursor.getString(1);
                teams.add(team1);
                teams.add(team2);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return teams;
    }
/*
    public ArrayList<String> getIdMatchesForRegionsDate(ArrayList<String> regions, String date){

        ArrayList <String> matchIds = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String team1 = "";

        //
        //                                 0            1
        for(int i=0; i<regions.size(); i++){
            String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? " ;
            Cursor cursor = db.rawQuery(selectQuery, new String []{regions.get(i), date});
            //Log.d(TAG, "getTeamsForRegionMatchID: "+cursor.getCount());
            if (cursor.moveToFirst()) {
                do {

                    team1 = cursor.getString(0);

                    matchIds.add(team1);


                } while (cursor.moveToNext());
            }
        }


        cursor.close();
        db.close();

        return matchIds;
    }*/

    public Boolean teamsInsertedForRegionDateTime(String region, String datetime){
        Log.d(TAG, "teamsInsertedForRegionDay: "+ region + " datetime: "+ datetime);

        SQLiteDatabase db = this.getReadableDatabase();
        String team1 = "";

        //                                 0            1
        String selectQuery = "SELECT "+ TEAM1 +" , " +TEAM2 +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + MATCH_ID + " = ? LIMIT 1" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, datetime});

        if (cursor.moveToFirst()) {
            do {

                team1 = cursor.getString(0);
                if (team1.equals("")){
                    Log.d(TAG, "teamsInsertedForRegionDay: - FALSE - THERE ARE NOT TEAMS INSERTED FOR THIS DATE");
                    return false;
                }else {
                    Log.d(TAG, "teamsInsertedForRegionDay: - TRUE -  THERE ARE TEAMS INSERTED FOR THIS DATE");
                    return true;
                }

            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        Log.d(TAG, "teamsInsertedForRegionDay: ERROR teamsInsertedForRegionDay");
        return false;

    }

    public Boolean teamsInsertedForRegionDay(String region, String date){
        Log.d(TAG, "teamsInsertedForRegionDay: "+ region + " date: "+ date);

        SQLiteDatabase db = this.getReadableDatabase();
        String team1 = "";

        //                                 0            1
        String selectQuery = "SELECT "+ TEAM1 +" , " +TEAM2 +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? LIMIT 1" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});

        if (cursor.moveToFirst()) {
            do {

                team1 = cursor.getString(0);
                if (team1.equals("")){
                    Log.d(TAG, "teamsInsertedForRegionDay: - FALSE - THERE ARE NOT TEAMS INSERTED FOR THIS DATE");
                    return false;
                }else {
                    Log.d(TAG, "teamsInsertedForRegionDay: - TRUE -  THERE ARE TEAMS INSERTED FOR THIS DATE");
                    return true;
                }

            } while (cursor.moveToNext());
        }


        cursor.close();
        db.close();
        Log.d(TAG, "teamsInsertedForRegionDay: ERROR teamsInsertedForRegionDay");
        return false;

    }

    public String timeAtTeamPlaysThisDay(String region, String date, String team){
        Log.d(TAG, "timeAtTeamPlaysThisDay: at what time does "+ team +" from "+ region +" date: "+ date + " plays?");
        ArrayList <String> teams = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String datetime = "";

        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID + " FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ?  AND + " + TEAM1 + " = ? OR + " + TEAM2 + " = ? LIMIT 1" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date, team , team});

        if (cursor.moveToFirst()) {
            do {
                if (cursor!=null){
                    //Log.d(TAG, "timeAtTeamPlaysThisDay: NON HO TROVATO NULLA");
                    datetime = cursor.getString(0);
                }else return "0";
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        //Log.d(TAG, "timeAtTeamPlaysThisDay: "+datetime);
        if (datetime.equals("")){
            Log.d(TAG, "timeAtTeamPlaysThisDay: "+ team +" from "+ region +" date: "+ date + " DOESNT PLAY TODAY");
            return "";
        }else if (!getLocalDateFromDateTime(datetime).equals(date) ){
            Log.d(TAG, "timeAtTeamPlaysThisDay: "+ team +" from "+ region +" date: "+ date + " DOESNT PLAY TODAY");
            return "0";
        }
        else {
            return getLocalHourFromDateTime(datetime);
        }


    }

    public ArrayList<String> getPlayingRegions (String currentYear, String date ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> regions = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ REGION +" FROM "+ TABLE_MATCH_DAYS +" WHERE "+ YEAR +" = ? AND " + DAY + " = ?" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{currentYear, date});
        if (cursor.moveToFirst()) {
            do {
                regions .add( cursor.getString(0)) ;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return regions;
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

    public ArrayList<String> getMatchIdsForThisDate(String year, String regionSelected, String day_ID ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_ids = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ YEAR +" = ? AND " +REGION +"= ? AND " + DAY + " = ?" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{year, regionSelected, day_ID});
        if (cursor.moveToFirst()) {
            do {
                match_ids .add( cursor.getString(0)) ;
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return match_ids;
    }

    public ArrayList<String> getAllMatchIds(String year, String regionSelected ){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_ids = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ YEAR +" = ? AND " +REGION +"= ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{year, regionSelected});
        if (cursor.moveToFirst()) {
            do {
                match_ids .add( cursor.getString(0)) ;
            } while (cursor.moveToNext());
        }
        //Log.d(TAG, "getAllMatchIds: size? "+match_ids.size());
        cursor.close();
        db.close();
        return match_ids;
    }

    public RegionStats getRegionStats(String year, String regionSelected ){
        SQLiteDatabase db = this.getReadableDatabase();

        //la data deve essere anteriore alla data attuale, aggiungi un IF statement

        //ArrayList<String> match_ids = new ArrayList<>();
        int counter_total=0;
        int counter_right=0;
        //                                 0                1
        String selectQuery = "SELECT "+ PREDICTION +","+ WINNER +" FROM "+ TABLE_MATCHES +" WHERE "+ YEAR +" = ? AND " +REGION +"= ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{year, regionSelected});
        if (cursor.moveToFirst()) {
            do {
                String prediction = cursor.getString(0);
                String winner = cursor.getString(1);

                if (!prediction.equals("") && !winner.equals("")){
                    counter_total+=1;

                    if (prediction.equals(winner)){
                        counter_right+=1;
                    }
                }


            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        //Log.d(TAG, "getCorrectPicksPercentageForRegion: "+regionSelected +": "+ counter_right+"/"+counter_total);
        return new RegionStats(regionSelected ,counter_right, counter_total);
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

    public void updatePrediction(String region, String match_ID, String prediction){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(PREDICTION, prediction);

        db.update(TABLE_MATCHES,  cv, REGION +" = ? AND "+ MATCH_ID + "= ? ", new String []{region, match_ID});
        Log.d(TAG, "updatePrediction: done: "+region +" " + match_ID +" "+ prediction);
        db.close();
    }

    public void updateWinner(String region, String match_ID, String winner){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(WINNER, winner);

        db.update(TABLE_MATCHES,  cv, REGION +" = ? AND "+ MATCH_ID + "= ? ", new String []{region, match_ID});
        Log.d(TAG, "updateWinner: done: "+region +" " + match_ID +" "+ winner);
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

    public void removeFromDatabase(String regionToRemove) {

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = REGION+"=?";
        String[] whereArgs = new String[] { regionToRemove};
        db.delete(TABLE_MATCH_DAYS, whereClause, whereArgs);
        db.delete(TABLE_MATCHES, whereClause, whereArgs);


    }

    public void removePastYear(String pastYear) {

        SQLiteDatabase db = this.getWritableDatabase();

        String whereClause = YEAR+"=?";
        String[] whereArgs = new String[] { pastYear};
        db.delete(TABLE_MATCH_DAYS, whereClause, whereArgs);
        db.delete(TABLE_MATCHES, whereClause, whereArgs);


    }

    public String firstMatchForRegion_Date(String region, String date){

        SQLiteDatabase db = this.getReadableDatabase();
        String match_id = "";
        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? LIMIT 1" ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});
        cursor.close();
        db.close();
        if (match_id.equals("")){return "0";
        } else {
            Log.d(TAG, "firstMatchForRegion_Date: match_id: "+match_id);
            return getLocalDateTimeFromDateTime(match_id);
        }
    }

    public String firstMatchForRegion_Date_PastCurrentTime(String region, String date){
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_ids = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});
        if (cursor.moveToFirst()) {
            do {
                match_ids.add( getLocalDateTimeFromDateTime( cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        // ora vedi quale di quei datetime è past il datetime attuale

        for (int i=0; i< match_ids.size();i++){
            // Log.d(TAG, "loadMatchDays: "+matchDays.get(i));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            Date strDate = null;
            try {
                strDate = sdf.parse(match_ids.get(i));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;
            long matchTimeMillis = strDate.getTime();
            // Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
            if (System.currentTimeMillis() <= matchTimeMillis) {
                return match_ids.get(i);
            }
        }

        return "0";

    }

    public ArrayList<String> matchesForRegion_Date(String region, String date){
        Log.d(TAG, "matchesForRegion_Date: region: "+region + " date: "+date);

        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<String> match_id = new ArrayList<>();
        //                                 0
        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});
        if (cursor.moveToFirst()) {
            do {
                //match_id.add( getLocalDateTimeFromDateTime( cursor.getString(0)));
                match_id.add( ( cursor.getString(0)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        Log.d(TAG, "matchesForRegion_Date: match_id: "+match_id);
            return match_id;

    }

    private String getLocalDateTimeFromDateTime(String datetime) {
        //Log.d(TAG, "getLocalDateTimeFromDateTime: datetime: " + datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);

        return localDatetime;
    }

    public Integer numberOfMatchesForThisRegion_date(String region, String date){

        SQLiteDatabase db = this.getReadableDatabase();
        Integer count = 0;

        String selectQuery = "SELECT "+ MATCH_ID +" FROM "+ TABLE_MATCHES +" WHERE "+ REGION +" = ? AND " + DAY + " = ? " ;
        Cursor cursor = db.rawQuery(selectQuery, new String []{region, date});
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;

    }

    private String getLocalHourFromDateTime(String datetime) {
        Log.d(TAG, "getLocalHourFromDateTime: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);

        return localDatetime;
    }

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        Log.d(TAG, "getLocalDateFromDateTime: localDatetime: "+localDatetime);

        return localDatetime;
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

