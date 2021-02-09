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

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.R;

import java.util.ArrayList;
import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "Drugs Alarm";
    private static String NOTIFICATION_ID = "1";
    public static String PACKAGE_NAME;
    ArrayList<String> allTomorrowsMatches;
    //Bitmap bitmap;
    //Bitmap bitmap_region;




    @Override
    public void onReceive(Context context, Intent intent) {

            int notificationID = new Random().nextInt();
             PACKAGE_NAME = context.getApplicationContext().getPackageName();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String notification_type = intent.getStringExtra("TYPE");
            Log.d(TAG, "onReceive: "+notification_type);

                 if (notification_type.equals("NOT_PICKED")){
                     Log.d(TAG, "onReceive: NOT_PICKED NOT_PICKED NOT_PICKED NOT_PICKED NOT_PICKED NOT_PICKED");
                     String regionOfMatchesNotPicked = intent.getStringExtra("REGION");
                    //intent per andare a PicksActivity quando si fa tap sulla notifica
                    Intent resultIntent = new Intent(context, PicksActivity.class);
                    PendingIntent goToPickem = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                            .setSmallIcon(R.drawable.ic_p)
                            .setColor(context.getResources().getColor(R.color.blue_light))
                            .setContentTitle("Pick EM!")
                            .setContentText("You have not picked all matches of today for: "+regionOfMatchesNotPicked +", Hurry up!")
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                            .setContentIntent(goToPickem)
                            .setAutoCancel(true);

                    notificationManagerCompat.notify(notificationID, notificationBuilder.build());
                    //notificationManagerCompat.cancel(200);
                }
                else if (notification_type.equals("TEST1")){

                    RemoteViews collapsedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_match_collapsed);
                    RemoteViews expandedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_match_expanded);


                    collapsedView.setTextViewText(R.id.custom_textview_collapsed_notification_time, "20:00");
                    collapsedView.setImageViewResource(R.id.custom_imageview_notification_collapsed_team1, R.drawable.logo_lck);
                    collapsedView.setImageViewResource(R.id.custom_imageview_collapsed_notification_team2, R.drawable.logo_lcs);
                    expandedView.setImageViewResource(R.id.custom_imageview_notification_team1, R.drawable.logo_lck);
                    expandedView.setImageViewResource(R.id.custom_imageview_notification_team2, R.drawable.logo_lcs);

                    Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                            .setSmallIcon(R.drawable.ic_p)
                            .setColor(context.getResources().getColor(R.color.blue_light))
                            .setCustomContentView(collapsedView)
                            .setCustomBigContentView(expandedView)
                            .setAutoCancel(true)
                            .build();
                    notificationManagerCompat.notify(1, notification);
                }
                else if (notification_type.equals("FIRST_MATCH")){

                    String region = intent.getStringExtra("REGION");

                    String imagepath = context.getFilesDir().getAbsolutePath() + "/images/regions/" +region +".png";

                    RemoteViews collapsedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_region_collapsed);
                    RemoteViews expandedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.notification_region_expanded);


                            expandedView.setTextViewText(R.id.custom_notif_region_regionname, region);
                            collapsedView.setTextViewText(R.id.custom_notif_region_regionname_collapsed, region);
                            //collapsedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo_collapsed, BitmapFactory.decodeFile(imagepath));
                            //expandedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo, BitmapFactory.decodeFile(imagepath));

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
                     Log.d(TAG, "onReceive: size: "+ size);

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



            }





}

