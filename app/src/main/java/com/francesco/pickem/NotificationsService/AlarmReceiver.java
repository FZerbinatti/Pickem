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
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
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
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

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

                         // Teams notifications:
//                    7:00: prendi le regioni delle squadre che vuoi seguire in una AL2 e le regioni che vuoi seguire in una AL1
//                    AL1 = REGIONI CHE VUOI SEGUIRE
//                    AL2 REGIONI DELLE SQUADRE CHE SEGUI
//                          prendi le regioni che giocano oggi SELECT REGION WHERE DAY == TODAY IN MATCHDAYS <TODAY PLAYING REGION>
//                          ALR = INTERSEZIONE TRA <TPL> E AL1 == REGIONI CHE SEGUI CHE GIOCANO OGGI
//                          ALT = INTERSEZIONE TRA <TPL> E AL2 == REGIONI DEI TEAM CHE SEGUI CHE GIOCANO OGGI

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
                         /*DatabaseReference notificationTeamsReference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_users))
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
                                         Log.d(TAG, "onDataChange: counter == count");
                                         //thisUserTeamsNotificationPreference(regionsOfTeamsNotifications, context ,false, playingRegions);
                                     }

                                 }


                             }
                             @Override
                             public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                             }
                         });*/




                     }else{
                         Log.d(TAG, " network off");

                         AlarmManager alarmMgr0 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                         Intent intent1 = new Intent(context, AlarmReceiver.class);
                         intent1.putExtra("TYPE", "7AMTASK");

                         alarmMgr0 = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                         PendingIntent pendingIntent0 = PendingIntent.getBroadcast( context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                         alarmMgr0.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + (30*60*1000), pendingIntent0);
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
                                         .setContentTitle("Pick EM!")
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
                             .setContentTitle("Pick EM!")
                             .setContentText( team + "is gonna play today at " + time )
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
                             .setContentTitle("Pick EM!")
                             .setContentText( team +" will play soon!" )
                             .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                             .setAutoCancel(true);

                     notificationManagerCompat.notify(notificationID, notificationBuilder.build());
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
                        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));

                        alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                        //Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: "+tomorrow+" 7:00:00");
                        try {
                            calendar.setTime(formatter.parse(firstMatchOfToday));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        Log.d(TAG, " allarme un'ora prima dell'inizio del primo match. ");
                        alarmManager.set(AlarmManager.RTC_WAKEUP, (calendar.getTimeInMillis()-3600000), alarmIntent);
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
               /* if (fullUserRegionsNotifications.get(i).getNotification_morning_reminder()>0) {
                    Log.d(TAG, "thisUserNotificationPreference: found region that want morning reminder, adding :" +fullUserRegionsNotifications.get(i).getRegion_name());
                    regionsWithMorningReminder.add(fullUserRegionsNotifications.get(i).getRegion_name());
                }*/

            }
        }

/*        for(int i=0; i<regionsWithMorningReminder.size(); i++){
            Log.d(TAG, "thisUserNotificationPreference: <regionsWithMorningReminder.size(): "+regionsWithMorningReminder.size());
            currentRegion.setRegion(regionsWithMorningReminder.get(i));
            allTodayMatches = new ArrayList<>();
            ArrayList <String> allTodayMatchesForThisRegion = new ArrayList<>();
            allTodayMatchesForThisRegion=(databaseHelper.matchesForRegion_Date(regionsWithMorningReminder.get(i), backgroundTasks.getTodayDate()));
            Log.d(TAG, "thisUserNotificationPreference: allTodayMatchesForThisRegion.size(): "+allTodayMatchesForThisRegion.size());
            for(int j=0; j<allTodayMatchesForThisRegion.size(); j++){
                Log.d(TAG, "thisUserNotificationPreference: cycling: "+j+" -> "+allTodayMatchesForThisRegion.get(j)+"Z");
                //query per avere per ognuno dei datetime un MatchDetails

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference
                        (context.getResources().getString(R.string.firebase_Matches))
                        .child(currentRegion.getRegion())
                        .child(currentRegion.getRegion()+year)
                        .child(allTodayMatchesForThisRegion.get(j)+"Z");

                readData(reference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        //Log.d(TAG, "onSuccess: ");
                        MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);

                        if (matchDetails!=null){
                            Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                            matchDetails.setWinner(currentRegion.getRegion());
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
        }*/

/*        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                Log.d(TAG, "run: gathered? ");
                if (allTodayMatches.size()>0){
                    Log.d(TAG, "run: allTodayMatches: "+allTodayMatches.size());

                    Intent intent = new Intent(context, AlarmReceiver.class);
                    intent.putExtra("TYPE", "ALL_TMATCHES");

                    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    ArrayList<String> tomorrowStringArrayMatches = new ArrayList<>();

                    Collections.sort(allTodayMatches, new MatchDetails.ByDatetime());

                    for (int i=0; i<allTodayMatches.size(); i++){
                        tomorrowStringArrayMatches.add(allTodayMatches.get(i).getWinner()+" " +getTimeFromDateTime(allTodayMatches.get(i).getDatetime()) +" -  [ " + allTodayMatches.get(i).getTeam1() + " vs " + allTodayMatches.get(i).getTeam2() + " ]");
                    }
                    //Log.d(TAG, "setNotificationWithTomorrowMatches: tomorrowStringArrayMatches.size:"+tomorrowStringArrayMatches.size());

                    intent.putStringArrayListExtra("ALL_T_MATCHES", tomorrowStringArrayMatches);
                    alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                    alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    //Log.d(TAG, "thisUserNotificationPreference: calendar.getTimeInMillis()-3600000: "+ (calendar.getTimeInMillis()-3600000));
                    alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);
                    //testing
                    //alarmManager.set(AlarmManager.RTC_WAKEUP, , alarmIntent);
                }
            }
        }, 10000);*/

    }

//this
/*
    private void thisUserTeamsNotificationPreference(ArrayList<String> userTeamsNotifications, Context context, Boolean giaControllato, ArrayList <String> playingTodayRegions) {
        Log.d(TAG, "thisUserTeamsNotificationPreference: userTeamsNotifications.size(): "+userTeamsNotifications.size());
        // ho tutti i TeamsNotification dei teams di cui voglio essere notificato: userTeamsNotifications

        //devo verificare se uno dei match di oggi contiene la squadra scelta
        String today = backgroundTasks.getTodayDate();

        for(int i=0; i<userTeamsNotifications.size(); i++){



            if (databaseHelper.teamsInsertedForRegionDay(userTeamsNotifications.get(i).getRegion(), today )){
                //se ci sono teams nei matches della giornata di oggi, check se c'è il team scelto tra quelli
                String timeAtThisTeamPlays = databaseHelper.timeAtTeamPlaysThisDay(userTeamsNotifications.get(i).getRegion(),today, userTeamsNotifications.get(i).getTeam());
                //addTeamNotification (userTeamsNotifications.get(i), timeAtThisTeamPlays, context);

            }else if (!giaControllato) {
                Log.d(TAG, "thisUserTeamsNotificationPreference: ERROR: non ci sono i team nella giornata di oggi, allora query per inserirli");
                //se non ci sono i team nella giornata di oggi, allora query per inserirli
                //downloadMatches(today, userTeamsNotifications.get(i).getRegion(), context , userTeamsNotifications);

            }

        }


    }
*/

/*
    private void addTeamNotification(TeamNotification teamNotification, String timeAtThisTeamPlays, Context context) {

        if (teamNotification.getNotification_team_morning_reminder()>0){
            //setta una notifica istantanea con scritto quando la squadra gioca
            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("TYPE", "TEAM_MORNING");
            intent.putExtra("TEAM", teamNotification.getTeam());
            intent.putExtra("TIME", timeAtThisTeamPlays);

            alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            alarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);

        }
        if (teamNotification.getNotification_team_as_team_plays()>0){
            //setta una notifica un'ora prima che la squadra giochi

            Intent intent = new Intent(context, AlarmReceiver.class);
            intent.putExtra("TYPE", "AS_TEAM_PLAY");
            intent.putExtra("TEAM", teamNotification.getTeam());
            intent.putExtra("TIME", timeAtThisTeamPlays);
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            Log.d(TAG, "addTeamNotification: timeAtThisTeamPlays: "+timeAtThisTeamPlays);
            Calendar matchStartCalendart = Calendar.getInstance();


            matchStartCalendart.set(Calendar.HOUR_OF_DAY, Integer.parseInt(timeAtThisTeamPlays));
            matchStartCalendart.set(Calendar.MINUTE, 0);
            matchStartCalendart.set(Calendar.SECOND, 0);

            Log.d(TAG, "addTeamNotification: "+matchStartCalendart.getTime());
            Log.d(TAG, "addTeamNotification: "+matchStartCalendart.getTimeInMillis() +teamNotification.getTeam() +" " +teamNotification.getRegion());

            alarmIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
            Log.d(TAG, "thisUserNotificationPreference: calendar.getTimeInMillis()-300000: "+ (matchStartCalendart.getTimeInMillis()-300000));
            alarmManager.set(AlarmManager.RTC_WAKEUP, (matchStartCalendart.getTimeInMillis()-300000), alarmIntent);
            //testing
            //laarmManager.set(AlarmManager.RTC_WAKEUP, (System.currentTimeMillis()+2000), alarmIntent);





        }

    }
*/

/*
    private void downloadMatches(String loadMatchesForThisDate, String selected_region_name, Context context, ArrayList<TeamNotification> userTeamsNotifications){
        //Log.d(TAG, "downloadMatches: loading matchs for this day: "+loadMatchesForThisDate +" - region: " +selected_region_name);

        matchListSplit = new ArrayList<>();
        matchesForThisDate = new ArrayList<>();

        // query sqlite per avere gli ID dei match di quel giorno di quella regione,
        matchesForThisDate = databaseHelper.getMatchIdsForThisDate(year, selected_region_name, loadMatchesForThisDate);

        // query firebase per avere gli oggetti MatchDetails
        for (int i = 0; i < matchesForThisDate.size(); i++){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(context.getString(R.string.firebase_Matches))
                    .child(selected_region_name)
                    .child(selected_region_name + year)
                    .child(matchesForThisDate.get(i).toString());

            //Log.d(TAG, "loadRecyclerView: "+selected_region_name+"/"+selected_region_name + year+"/"+selected_region_name + year +"/"+matchesForThisDate.get(i));

            readData(reference, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {
                    //Log.d(TAG, "onSuccess: ");
                    MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                    //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                    if (matchDetails!=null){
                       databaseHelper.insertMatchDetails(selected_region_name, matchDetails.getDatetime() , matchDetails.getTeam1(), matchDetails.getTeam2() );

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

        //wait for insert
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                thisUserTeamsNotificationPreference(userTeamsNotifications, context ,true);

            }
        }, 10000);



    }
*/

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


}


