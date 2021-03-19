/*
 *Copyright (C) TWL-Systems All Rights Reserved.
 *Author: Francesco Bernardis
 *Date: 12/07/2019
 *
 ***************** History *************
 *
 ***************** 0.0 *****************
 * DATE_VER   23/07/2019
 * bugfix notification crash, realm crash
 *
 ***************************************
 *
 ***************** 1.0 *****************
 * DATE_VER   13/09/2019
 * layout review supervisore, impostazioni, toolbar supervisore
 * pills reminder
 * bugfix submit/realm, autoconnection, background jobs, drugs alarm, layout supervisore, resume after crash
 *
 ***************** 1.1 *****************
 * DATE_VER   DA DEFINIRE
 * bug fix
 * lingua italiano/inglese, bug fix connection
 * new watch ecg, new layout, new function, request battery optimization, auto connect for watch ecg, view chose watch, add some method and field in User.Java
 */
package com.francesco.pickem.NotificationsService;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.Activities.MainActivities.SettingsActivity;
import com.francesco.pickem.BuildConfig;
import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
import com.francesco.pickem.Services.JsonPlaceHolderAPI_Elo;
import com.francesco.pickem.Services.JsonPlaceHolderAPI_Summoner;
import com.francesco.pickem.Services.Post_Elo;
import com.francesco.pickem.Services.Post_Summoner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "AlarmReceiver";
    private static final String NOTIFICATION_ID = "1";
    private static final String CHANNEL_ID = "1";
    public static String PACKAGE_NAME;
    ArrayList<String> allTomorrowsMatches;
    PicksActivity picksActivity;
    BackgroundTasks backgroundTasks;
    ArrayList<String> userRegionsNotifications;
    ArrayList<RegionNotifications> userFullRegionsNotifications;
    ArrayList<String> regionsOfTeamsNotifications;
    ArrayList<TeamNotification> userTeamsNotifications;
    ArrayList<String> arrayListOFRegionOfUserTeamsNotifications;
    DatabaseHelper databaseHelper;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    Calendar myCalendar;
    String year;
    ArrayList <String> todayMatches;
    ArrayList <MatchDetails> allTodayMatches;


    ArrayList matchListSplit ;
    ArrayList matchesForThisDate ;


    @Override
    public void onReceive(Context context, Intent intent) {
            databaseHelper = new DatabaseHelper(context);
            backgroundTasks = new BackgroundTasks();
            myCalendar = Calendar.getInstance();
            year = String.valueOf(myCalendar.get(Calendar.YEAR));
            createNotificationChannel(context);

            int notificationID = new Random().nextInt();
             PACKAGE_NAME = context.getApplicationContext().getPackageName();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String notification_type = intent.getStringExtra("TYPE");
            Log.d(TAG, " AlarmReciever ha ricevuto:  "+notification_type);

                if (notification_type.equals("7AMTASK")){
                    String hour = intent.getStringExtra("HOUR");

                     //se non hai network, torna a chiamrare la stessa cosa tra mezzora

                     ConnectivityManager connMgr = (ConnectivityManager) context
                             .getSystemService(Context.CONNECTIVITY_SERVICE);
                     NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
                     
                     if (networkInfo != null && networkInfo.isConnected()) {
                         Log.d(TAG, " network ON");

                         // un allarme che ogni mattina alle 7+0.5h AM
                         // prendi le region per cui lo user ha NoChoicheMade>0
                         userFullRegionsNotifications = new ArrayList<>();
                         regionsOfTeamsNotifications = new ArrayList<>();


                         // che regioni giocano oggi?
                         ArrayList<String> playingRegions = databaseHelper.getPlayingRegions(year, backgroundTasks.getTodayDate());
                         Log.d(TAG, " quante regioni hanno partite in programma per oggi? "+playingRegions.size());
                         DatabaseReference notificationRegionReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                 .child(context.getString(R.string.firebase_user_notification))
                                 .child(context.getString(R.string.firebase_user_notification_region));

                         notificationRegionReference.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                                 int count = (int) (dataSnapshot.getChildrenCount());
                                 int counter =0;
                                 for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                     counter++;
                                     RegionNotifications regionNotifications = snapshot.getValue(RegionNotifications.class);
                                     if (regionNotifications !=null){
                                         userFullRegionsNotifications.add(regionNotifications);
                                     }
                                 }
                                 if (counter == count){
                                     Log.d(TAG, " ho finito di fare il gathering delle regioni per cui lo user vuole ricevere notifiche");
                                     thisUserRegionsNotificationPreference(userFullRegionsNotifications, context, playingRegions);
                                 }
                             }
                             @Override
                             public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                             }
                         });
                        //teams
                         DatabaseReference notificationTeamsReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                                 .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                 .child(context.getString(R.string.firebase_user_notification))
                                 .child(context.getString(R.string.firebase_teams));

                         notificationTeamsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                                 int count = (int) (dataSnapshot.getChildrenCount());
                                 int counter =0;
                                 for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                     counter++;
                                     String regionForTeamsNotifications = snapshot.getKey().toString();
                                     //String regionForTeamsNotifications = snapshot.getValue(String.class);
                                     regionsOfTeamsNotifications.add(regionForTeamsNotifications);

                                     if (counter == count){
                                         Log.d(TAG, " ho finito di fare il gathering delle regioni dei temas per cui lo user vuole ricevere notifiche");
                                         thisUserTeamsNotificationPreference(regionsOfTeamsNotifications, context, playingRegions);
                                     }
                                 }
                             }
                             @Override
                             public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                             }
                         });




                     }else{
                         Log.d(TAG, " network off");

                         AlarmManager alarmMgr0 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                         Intent intent1 = new Intent(context, AlarmReceiver.class);
                         intent1.putExtra("TYPE", "7AMTASK");

                         alarmMgr0 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                         PendingIntent pendingIntent0 = PendingIntent.getBroadcast( context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                         alarmMgr0.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (15*60*1000), pendingIntent0);
                     }

                 }
                else if (notification_type.equals("FIRST_MATCH")){

                    String region = intent.getStringExtra("REGION");
                    String time = intent.getStringExtra("TIME");

                    String imagepath = context.getFilesDir().getAbsolutePath() + "/images/regions/" +region.replace(" ", "") +".png";

                    RemoteViews collapsedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_region_collapsed);
                    RemoteViews expandedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_region_expanded);

                            collapsedView.setTextViewText(R.id.custom_notif_textview_action, "Starting soon: ");
                            collapsedView.setTextViewText(R.id.custom_notif_region_regionname_collapsed, region);
                            //collapsedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo_collapsed, BitmapFactory.decodeFile(imagepath));
                            collapsedView.setImageViewResource(R.id.custom_notif_region_regionlogo_collapsed, R.drawable.logo_lck);

                            collapsedView.setTextViewText(R.id.custom_notif_textview_action, "Starting soon: ");
                            expandedView.setTextViewText(R.id.custom_notif_time, time);
                            expandedView.setTextViewText(R.id.custom_notif_region_regionname, region);
                            expandedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo, BitmapFactory.decodeFile(imagepath));
                            //expandedView.setImageViewResource(R.id.custom_notif_region_regionlogo, R.drawable.logo_lck);

                            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                    .setSmallIcon(R.drawable.ic_p)
                                    .setColor(context.getResources().getColor(R.color.blue_light))
                                    .setCustomContentView(collapsedView)
                                    .setCustomBigContentView(expandedView)
                                    .setAutoCancel(true)
                                    .build();

                            notificationManagerCompat.notify(1, notification);


                }
                else if (notification_type.equals("ALL_TMATCHES")){
                     allTomorrowsMatches = intent.getStringArrayListExtra("ALL_T_MATCHES");
                    Log.d(TAG, "onReceive: ALL_TMATCHES, lunghezza della lista ricevuta: "+ allTomorrowsMatches.size());
                     Integer size = allTomorrowsMatches.size();
                     if (size>7){
                         size=7;
                     }
                     String title ="Today's Matches";
                     switch (size){
                         case 1:
                             Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification);
                             break;
                         case 2:
                             Notification notification2 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification2);
                             break;
                         case 3:
                             Notification notification3 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                             .addLine(allTomorrowsMatches.get(2))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification3);
                             break;
                         case 4:
                             Notification notification4 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                             .addLine(allTomorrowsMatches.get(2))
                                             .addLine(allTomorrowsMatches.get(3))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification4);
                             break;
                         case 5:
                             Notification notification5 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                             .addLine(allTomorrowsMatches.get(2))
                                             .addLine(allTomorrowsMatches.get(3))
                                             .addLine(allTomorrowsMatches.get(4))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification5);
                             break;
                         case 6:
                             Notification notification6 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                             .addLine(allTomorrowsMatches.get(2))
                                             .addLine(allTomorrowsMatches.get(3))
                                             .addLine(allTomorrowsMatches.get(4))
                                             .addLine(allTomorrowsMatches.get(5))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification6);
                             break;
                         case 7:
                             Notification notification7 = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                     .setSmallIcon(R.drawable.ic_p)
                                     .setColor(context.getResources().getColor(R.color.blue_light))
                                     .setStyle(new NotificationCompat.InboxStyle()
                                             .addLine(allTomorrowsMatches.get(0))
                                             .addLine(allTomorrowsMatches.get(1))
                                             .addLine(allTomorrowsMatches.get(2))
                                             .addLine(allTomorrowsMatches.get(3))
                                             .addLine(allTomorrowsMatches.get(4))
                                             .addLine(allTomorrowsMatches.get(5))
                                             .addLine(allTomorrowsMatches.get(6))
                                     )
                                     .setContentTitle(title)
                                     .setPriority(NotificationCompat.PRIORITY_LOW)
                                     .setAutoCancel(true)
                                     .build();



                             notificationManagerCompat.notify(1, notification7);
                             break;
                         default:
                             break;

                     }





                 }
                else if (notification_type.equals("UNPICKED")){
                     // questo allarme controlla se lo user ha fatto i pick per la region arrivata, se non l'ha fatto, setta la notifica insta
                     String region = intent.getStringExtra("REGION");
                    Log.d(TAG, " verifico se lo user ha fatto tutti i pick prima dell'inizio della partita");


                     // controlla i pick dello user


                     ArrayList <String> userPicks = new ArrayList<>();
                     todayMatches = new ArrayList<>();

                     todayMatches = databaseHelper.matchesForRegion_Date(region, backgroundTasks.getTodayDate());

                     // se esistono match per questa giornata, controllo se l'utente ha fatto i pick per tutti i match sotto Userpicks

                     DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users")
                             .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                             .child(context.getResources().getString(R.string.firebase_users_picks))
                             .child(region)
                             .child(region+year);

                     for(int j=0; j<todayMatches.size(); j++){

                         reference.child(todayMatches.get(j)).addListenerForSingleValueEvent(new ValueEventListener() {
                             @Override
                             public void onDataChange(@NonNull DataSnapshot snapshot) {
                                 String match = snapshot.getValue(String.class);
                                 if (match!=null){
                                     userPicks.add(match);
                                 }
                             }

                             @Override
                             public void onCancelled(@NonNull DatabaseError error) {

                             }
                         });

                     }

                     //wait for gather all info
                     Handler handler = new Handler();
                     handler.postDelayed(new Runnable() {
                         public void run() {

                             //se il numero di picks dello user è minore del numero di match della giornata, setto la notifica
                             if (userPicks.size()<todayMatches.size()){

                                 Log.d(TAG, " lo user non ha fatto tutti i pick, setto la notifica");

                                 //intent per andare a PicksActivity quando si fa tap sulla notifica
                                 Intent resultIntent = new Intent(context, PicksActivity.class);
                                 PendingIntent goToPickem = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                                 NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                         .setSmallIcon(R.drawable.ic_p)
                                         .setColor(context.getResources().getColor(R.color.blue_light))
                                         .setContentTitle("Missed Picks alert:")
                                         .setContentText("You have not picked all matches of today for "+region +", Hurry up!")
                                         .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                         .setContentIntent(goToPickem)
                                         .setAutoCancel(true);

                                 notificationManagerCompat.notify(notificationID, notificationBuilder.build());

                             }else {
                                 Log.d(TAG, " lo user ha fatto tutti i pick di oggi.");
                             }

                         }
                     }, 5000);




                 }
                else if( notification_type.equals("TEAM_MORNING")){
                     String team = intent.getStringExtra("TEAM");
                     String time = intent.getStringExtra("TIME");


                     //expandedView.setImageViewResource(R.id.custom_notif_region_regionlogo, R.drawable.logo_lck);



                     NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                             .setSmallIcon(R.drawable.ic_p)
                             .setColor(context.getResources().getColor(R.color.blue_light))
                             .setContentText( team + " is gonna play today at " + time +":00" )
                             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                             .setAutoCancel(true);

                     notificationManagerCompat.notify(notificationID, notificationBuilder.build());
                 }
                else if( notification_type.equals("AS_TEAM_PLAY")){
                     String team = intent.getStringExtra("TEAM");
                     String time = intent.getStringExtra("TIME");
                
                     NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                             .setSmallIcon(R.drawable.ic_p)
                             .setColor(context.getResources().getColor(R.color.blue_light))
                             .setContentText( team +" will play soon!" )
                             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                             .setAutoCancel(true);

                     notificationManagerCompat.notify(notificationID, notificationBuilder.build());
                 }
                else if (notification_type.equals("CHECK_ELO")){
                    Log.d(TAG, "onReceive: CHECK_ELO");
                    String ora = intent.getStringExtra("HOUR");
                    String minuto = intent.getStringExtra("MINUTE");
                    // check elo del player + risetta allarme per domani

                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(context.getString(R.string.firebase_users_generealities));

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                            UserGeneralities userProfile = dataSnapshot.getValue(UserGeneralities.class);
                            if (userProfile !=null){

                                String summonerName = userProfile.getSummoner_name();
                                String summoner_server = userProfile.getSummoner_server();

                                // get summoner ID from summoner name + server
                                // https://     euw1    .api.riotgames.com      /lol/summoner/v4/summoners/by-name/         DEMACIA%20REICH         ?api_key=       RGAPI-632893d3-8938-4031-a32e-4aa92062d229

                                String address = context.getString(R.string.HTTP) + summoner_server + context.getString(R.string.riot_api_address);
                                /// https://euw1.api.riotgames.com

                                //summoner name + api_key_path + API key
                                String end_path = summonerName + context.getString(R.string.key_request)+ BuildConfig.RIOT_API_KEY;


                                Log.d(TAG, "saveFirstElotracker: address: " +address + "/lol/summoner/v4/summoners/by-name/" +end_path);

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(address)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                //https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0
                                //https://euw1.api.riotgames.com/lol/summoner/v4/summoners/by-name/DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0

                                JsonPlaceHolderAPI_Summoner jsonPlaceHolderAPI_summoner = retrofit.create(JsonPlaceHolderAPI_Summoner.class);
                                Log.d(TAG, "saveFirstElotracker: passando a getPost: "+end_path);
                                // devi passare a GetPost:    DEMACIA%20REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0
                                Log.d(TAG, "saveFirstElotracker: deve essere = "+"DEMACIA REICH?api_key=RGAPI-3c834326-87d8-479f-acb9-bf94f64212e0");
                                Call<Post_Summoner> callSummoner =  jsonPlaceHolderAPI_summoner.getPost(summonerName, BuildConfig.RIOT_API_KEY) ;

                                callSummoner.enqueue(new Callback<Post_Summoner>() {
                                    @Override
                                    public void onResponse(Call<Post_Summoner> call, Response<Post_Summoner> response) {
                                        if (!response.isSuccessful()){
                                            Log.d(TAG, "onResponse: "+response.code());
                                            Toast.makeText(context, "Something went wrong, check Summoner Name and Region selected!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                        Toast.makeText(context, "Success! Current Elo registered, will update every 24 hours!", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onResponse: "+ response.body().getId() +" summonerLevel:" + response.body().getSummonerLevel() + " summoner Name: "+ response.body().getName());
                                        //ora che hai lo il summonerID puoi fare l'altra call all'API

                                        JsonPlaceHolderAPI_Elo jsonPlaceHolderAPIElo = retrofit.create(JsonPlaceHolderAPI_Elo.class);

                                        Call<List<Post_Elo>> callElo = jsonPlaceHolderAPIElo.getPost(response.body().getId(), BuildConfig.RIOT_API_KEY );
                                        callElo.enqueue(new Callback<List<Post_Elo>>() {
                                            @Override
                                            public void onResponse(Call<List<Post_Elo>> call, Response<List<Post_Elo>> response) {
                                                if (!response.isSuccessful()){
                                                    Log.d(TAG, "onResponse: "+response.code());
                                                    return;
                                                }

                                                List<Post_Elo> postElos = response.body();

                                                for (Post_Elo postElo : postElos){
                                                    if (postElo.getQueueType().equals("RANKED_SOLO_5x5")){
                                                        Log.d(TAG, "onResponse: "+ postElo.getSummonerName() +" elo: " + postElo.getTier() + " " + postElo.getRank() +" " + postElo.getLeaguePoints()+ "LP");

                                                        String elo = postElo.getTier().substring(0, 1).toUpperCase() + postElo.getTier().substring(1).toLowerCase();

                                                        EloTracker eloTracker = new EloTracker(
                                                                backgroundTasks.getTodayDate(),
                                                                backgroundTasks.getTodayDate(),
                                                                elo+" "+(postElo.getRank()),
                                                                postElo.getLeaguePoints());

                                                        Calendar calendar = Calendar.getInstance();
                                                        int year = calendar.get(Calendar.YEAR);
                                                        Log.d(TAG, "onClick: year:"+year);


                                                        FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                                                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                                .child(context.getString(R.string.firebase_users_elotracker))
                                                                .child(String.valueOf(year))
                                                                .child(backgroundTasks.getTodayDate() )
                                                                .setValue(eloTracker);


                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<List<Post_Elo>> call, Throwable t) {
                                                Log.d(TAG, "onFailure: ERROR COMINCATING WITH API: "+t.getMessage());
                                            }
                                        });

                                    }

                                    @Override
                                    public void onFailure(Call<Post_Summoner> call, Throwable t) {
                                        Log.d(TAG, "onFailure: ERROR COMINCATING WITH API: "+t.getMessage());
                                        Toast.makeText(context, "Something went wrong server-side, contact helpdesk", Toast.LENGTH_SHORT).show();
                                    }
                                });




                            }
                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                        }
                    });




                }





    }

    private void thisUserRegionsNotificationPreference(ArrayList<RegionNotifications> fullUserRegionsNotifications, Context context, ArrayList <String> playingTodayRegions ) {
        Log.d(TAG, " region notification dellu user: "+fullUserRegionsNotifications.size() +" regioni che giocano oggi: "+playingTodayRegions.size());

        //intersezione tra userRegionsNotifications e playingTodayRegions
        // -> ArrayList con le squadre che giocano oggi di cui ti interessa sapere qualcosa

        ArrayList <String> interestRegionThatPlaysToday = new ArrayList<>();
            for(int i=0; i<fullUserRegionsNotifications.size(); i++){
                interestRegionThatPlaysToday.add(fullUserRegionsNotifications.get(i).getRegion_name());
            }
        //arrayList di String delle regioni che giocano oggi che gli frega all'utente
        playingTodayRegions.retainAll(interestRegionThatPlaysToday);

        CurrentRegion currentRegion = new CurrentRegion();
        ArrayList <String> regionsWithMorningReminder = new ArrayList<>();
        for(int i=0; i<fullUserRegionsNotifications.size(); i++){


            currentRegion.setRegion(fullUserRegionsNotifications.get(i).getRegion_name());
            //se oggi gioca questa regione di interesse
            if (playingTodayRegions.contains(fullUserRegionsNotifications.get(i).getRegion_name())){
                String firstMatchOfToday = databaseHelper.firstMatchForRegion_Date_PastCurrentTime(currentRegion.getRegion(), backgroundTasks.getTodayDate());
                Log.d(TAG, " il primo match della giornata per " +currentRegion.getRegion() +" è: "+firstMatchOfToday);

                if (fullUserRegionsNotifications.get(i).getNo_choice_made()>0) {
                    Log.d(TAG, " getNo_choice_made()>0 for: "+currentRegion.getRegion());
                    // se c'è un match per quella regione setta un allarme 1h prima dell'inizio del primo match SUCCESSIVO ALL'ORA ATTUALE

                    if (!firstMatchOfToday.equals("0")){

                        Intent intent = new Intent(context, AlarmReceiver.class);
                        intent.putExtra("TYPE", "UNPICKED");
                        intent.putExtra("REGION", currentRegion.getRegion());
                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                        try {
                            calendar.setTime(formatter.parse(firstMatchOfToday));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.add(Calendar.HOUR, -1);

                        Log.d(TAG, " allarme un'ora prima dell'inizio del primo match: "+ calendar.getTime());
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                        //testing
                        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+2000, alarmIntent);
                    }

                }
                if (fullUserRegionsNotifications.get(i).getNotification_first_match_otd()>0) {
                    Log.d(TAG, " getNotification_first_match_otd()>0 for: "+currentRegion.getRegion());
                    if (!firstMatchOfToday.equals("0")){

                        Intent intent = new Intent(context, AlarmReceiver.class);
                        intent.putExtra("TYPE", "FIRST_MATCH");
                        intent.putExtra("REGION", currentRegion.getRegion());
                        intent.putExtra("TIME", getTimeFromDateTime(firstMatchOfToday) );
                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        Calendar calendar = Calendar.getInstance();
                        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");


                        try {
                            calendar.setTime(formatter.parse(firstMatchOfToday));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        calendar.add(Calendar.MINUTE, -5);

                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        Log.d(TAG, "thisUserRegionsNotificationPreference: setto una notifica 5 minuti prima che inizi la priam partita del giorno: "+firstMatchOfToday+ "che dovrebbe essere: "+calendar.getTimeInMillis() + " == " +calendar.getTime());
                        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                        //testing
                        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+2000, alarmIntent);
                    }
                }
                if (fullUserRegionsNotifications.get(i).getNotification_morning_reminder()>0) {
                    Log.d(TAG, "thisUserNotificationPreference: found region that want morning reminder, adding :" +fullUserRegionsNotifications.get(i).getRegion_name());
                    regionsWithMorningReminder.add(fullUserRegionsNotifications.get(i).getRegion_name());
                }

            }
        }

        for(int i=0; i<regionsWithMorningReminder.size(); i++){
            Log.d(TAG, " quante regions con Morning Reminder? : "+regionsWithMorningReminder.size() +" current: "+ regionsWithMorningReminder.get(i));
            CurrentRegion currentRegion1 = new CurrentRegion();
            currentRegion1.setRegion(regionsWithMorningReminder.get(i));
            allTodayMatches = new ArrayList<>();
            ArrayList <String> allTodayMatchesForThisRegion = new ArrayList<>();
            allTodayMatchesForThisRegion=(databaseHelper.matchesForRegion_Date(regionsWithMorningReminder.get(i), backgroundTasks.getTodayDate()));
            Log.d(TAG, "thisUserNotificationPreference: allTodayMatchesForThisRegion.size(): "+allTodayMatchesForThisRegion.size());
            for(int j=0; j<allTodayMatchesForThisRegion.size(); j++){
                Log.d(TAG, "regionsWithMorningReminder: cycling: "+j+" -> "+allTodayMatchesForThisRegion.get(j));
                //query per avere per ognuno dei datetime un MatchDetails

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference
                        (context.getResources().getString(R.string.firebase_Matches))
                        .child(currentRegion1.getRegion())
                        .child(currentRegion1.getRegion()+year)
                        .child(allTodayMatchesForThisRegion.get(j));

                readData(reference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        Log.d(TAG, "onSuccess: ");
                        MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);

                        if (matchDetails!=null){

                            matchDetails.setWinner(currentRegion1.getRegion());
                            Log.d(TAG, "onSuccess: "+matchDetails.getDatetime() +" team: "+ matchDetails.getWinner());
                            matchDetails.setDatetime(convertDatetimeZtoLocale(matchDetails.getDatetime()));
                            allTodayMatches.add(matchDetails);
                        }

                    }

                    @Override
                    public void onStart() {
                        //Log.d(TAG, "onStart: ");
                    }

                    @Override
                    public void onFailure() {
                        //Log.d(TAG, "onFailure: ");
                    }
                });



            }

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    Log.d(TAG, "run: allTodayMatches????????????????????? "+allTodayMatches.size());
                    if (allTodayMatches.size()>0){


                        Intent intent = new Intent(context, AlarmReceiver.class);
                        intent.putExtra("TYPE", "ALL_TMATCHES");

                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        ArrayList<String> tomorrowStringArrayMatches = new ArrayList<>();

                        Collections.sort(allTodayMatches, new MatchDetails.ByDatetime());

                        for (int i=0; i<allTodayMatches.size(); i++){
                            tomorrowStringArrayMatches.add(allTodayMatches.get(i).getWinner()+" " +getTimeFromDateTime(allTodayMatches.get(i).getDatetime()) +" -  [ " + allTodayMatches.get(i).getTeam1() + " vs " + allTodayMatches.get(i).getTeam2() + " ]");
                        }

                        Log.d(TAG, " setto una notifica instantanea con questo numero di match: "+tomorrowStringArrayMatches.size());
                        intent.putStringArrayListExtra("ALL_T_MATCHES", tomorrowStringArrayMatches);
                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);
                        //testing

                    }
                }
            }, 10000);

        }



    }

    private void thisUserTeamsNotificationPreference(ArrayList<String> regionOfuserTeamsNotifications, Context context, ArrayList <String> playingTodayRegions) {
        Log.d(TAG, "thisUserTeamsNotificationPreference: regionOfuserTeamsNotifications: "+ regionOfuserTeamsNotifications.size() +" playingTodayRegions: "+playingTodayRegions.size());
        //devo verificare se uno dei match di oggi contiene la squadra scelta
        String today = backgroundTasks.getTodayDate();

        //arrayList di String delle regioni dei teams che giocano oggi che gli frega all'utente
        playingTodayRegions.retainAll(regionOfuserTeamsNotifications);
        //per ognuna delle regioni di temas che giocano oggi prendi i teams su cui fai le notifiche
        Log.d(TAG, "thisUserTeamsNotificationPreference: quante regioni ci sono per i teams per cui lo user vuole ricevere notifiche? " + playingTodayRegions.size());
        for(int i=0; i<playingTodayRegions.size(); i++){

            DatabaseReference notificationTeamsReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(context.getString(R.string.firebase_user_notification))
                    .child(context.getString(R.string.firebase_teams))
                    .child(playingTodayRegions.get(i));

            notificationTeamsReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    int count = (int) (dataSnapshot.getChildrenCount());
                    int counter =0;
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        counter++;
                        TeamNotification userTeamNotifications = snapshot.getValue(TeamNotification.class);
                        //ho un TeamNotification di una squadra la cui regione gioca oggi
                        // la squadra gioca oggi?
                        //ci sono teams nelle partite di oggi?
                        if (databaseHelper.teamsInsertedForRegionDay(userTeamNotifications.getRegion(), today)){
                            String timeAtTeamPlays = databaseHelper.timeAtTeamPlaysThisDay(userTeamNotifications.getRegion(), today, userTeamNotifications.getTeam());
                            if (timeAtTeamPlays.equals("0")){
                                Log.d(TAG, "onDataChange: la suqdra "+userTeamNotifications.getTeam() +" non gioca oggi");
                            }else {
                                if (!timeAtTeamPlays.equals("")){
                                    //hai trovato l'ora a cui questo team gioca oggi, setta le notifiche
                                    if (userTeamNotifications.getNotification_team_as_team_plays()>0){
                                        //setta la notifica 4 minuti prima dello start
                                        Log.d(TAG, "onDataChange: setto la notifica per TEAM  as team play");

                                        Intent intent = new Intent(context, AlarmReceiver.class);
                                        intent.putExtra("TYPE", "AS_TEAM_PLAY");
                                        intent.putExtra("TEAM", userTeamNotifications.getTeam());
                                        intent.putExtra("TIME", timeAtTeamPlays);
                                        alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                                        Log.d(TAG, "addTeamNotification: timeAtThisTeamPlays: "+timeAtTeamPlays);
                                        Calendar matchStartCalendart = Calendar.getInstance();

                                        matchStartCalendart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeAtTeamPlays));
                                        matchStartCalendart.set(Calendar.MINUTE, 0);
                                        matchStartCalendart.set(Calendar.SECOND, 0);
                                        matchStartCalendart.add(Calendar.MINUTE, -4);

                                        Log.d(TAG, "addTeamNotification: "+matchStartCalendart.getTime());
                                        Log.d(TAG, "addTeamNotification: "+matchStartCalendart.getTimeInMillis() +" "+userTeamNotifications.getTeam() +" " +userTeamNotifications.getRegion());

                                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                        alarmManager.set(AlarmManager.RTC_WAKEUP, (matchStartCalendart.getTimeInMillis()), alarmIntent);
                                        //testing
                                        //laarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);


                                    }
                                    if (userTeamNotifications.getNotification_team_morning_reminder()>0){
                                        Log.d(TAG, "onDataChange: TEST setto la notifica per TEAM morning reminder y");
                                        //setta la notifica instantanea con l'ora a cui gioca
                                        Intent intent = new Intent(context, AlarmReceiver.class);
                                        intent.putExtra("TYPE", "TEAM_MORNING");
                                        intent.putExtra("TEAM", userTeamNotifications.getTeam());
                                        intent.putExtra("TIME", timeAtTeamPlays);

                                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                                        alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);
                                    }
                                }else {
                                    Log.d(TAG, "onDataChange: la suqdra "+userTeamNotifications.getTeam() +" non gioca oggi");
                                }
                            }

                        }else {
                            // inserisci i teams in sqlite per la giornata di oggi
                            Log.d(TAG, "onDataChange: non ci sono i team inseriti per le partite di oggi, rimedio.");

                            matchesForThisDate = new ArrayList<>();
                            // query sqlite per avere gli ID dei match di quel giorno di quella regione,
                            matchesForThisDate = databaseHelper.getMatchIdsForThisDate(year, userTeamNotifications.getRegion(), today);
                            Log.d(TAG, "onDataChange: numero di partite di oggi: "+matchesForThisDate.size());

                            // query firebase per avere gli oggetti MatchDetails
                            for (int i = 0; i < matchesForThisDate.size(); i++){
                                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_Matches))
                                        .child(userTeamNotifications.getRegion())
                                        .child(userTeamNotifications.getRegion() + year)
                                        .child(matchesForThisDate.get(i).toString());


                                Log.d(TAG, "path: "+userTeamNotifications.getRegion()+"/"+userTeamNotifications.getRegion() + year+"/"+userTeamNotifications.getRegion() + year +"/"+matchesForThisDate.get(i));

                                readData(reference, new OnGetDataListener() {
                                    @Override
                                    public void onSuccess(DataSnapshot dataSnapshot) {
                                        //Log.d(TAG, "onSuccess: ");
                                        MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                                        //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                                        if (matchDetails!=null){
                                           databaseHelper.insertMatchDetails(userTeamNotifications.getRegion(),  matchDetails.getDatetime() , matchDetails.getTeam1(), matchDetails.getTeam2() );
                                        }
                                    }

                                    @Override
                                    public void onStart() {
                                        //Log.d(TAG, "onStart: ");
                                    }

                                    @Override
                                    public void onFailure() {
                                        //Log.d(TAG, "onFailure: ");
                                    }
                                });
                            }

                        }

                    }
                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                }
            });
        }




    }

    private String getTimeFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("HH:mm");

        String localDatetime = dateFormatter.format(value);

        return localDatetime;
    }

    private void createNotificationChannel(Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_pickem);
            String description = context.getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void readData(DatabaseReference ref, final OnGetDataListener listener) {
        listener.onStart();
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot snapshot) {

                listener.onSuccess(snapshot);
            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError error) {
                listener.onFailure();
            }
        });

    }

    public String convertDatetimeZtoLocale(String datetime){
        //Log.d(TAG, "convertDatetimeZtoLocale: eating this: "+datetime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date strDate = null;

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);

        //Log.d(TAG, "convertDatetimeZtoLocale: shitting this: "+formattedDate.toString());

        return formattedDate.toString();
    }

    public String getYesterdayDate(){

        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, -1);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String selectedDate = sdf.format(c.getTime());
        return selectedDate;
    }


}


