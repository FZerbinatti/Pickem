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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;
import java.util.concurrent.ExecutionException;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "Drugs Alarm";
    private static String NOTIFICATION_ID = "1";
    public static String PACKAGE_NAME;
    //Bitmap bitmap;
    //Bitmap bitmap_region;




    @Override
    public void onReceive(Context context, Intent intent) {

            int notificationID = new Random().nextInt();
             PACKAGE_NAME = context.getApplicationContext().getPackageName();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String notification_type = intent.getStringExtra("TYPE");

                 if (notification_type.equals("NOT_PICKED")){

                    //intent per andare a PicksActivity quando si fa tap sulla notifica
                    Intent resultIntent = new Intent(context, PicksActivity.class);
                    PendingIntent goToPickem = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                            .setSmallIcon(R.mipmap.ic_launcher_round)

                            .setContentTitle("Pick EM!")
                            .setContentText("Dont forget to do your picks for today's matches!")
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
                            R.layout.custom_notif_region_collapsed);
                    RemoteViews expandedView = new RemoteViews(PACKAGE_NAME,
                            R.layout.custom_notif_region);


                            expandedView.setTextViewText(R.id.custom_notif_region_regionname, region);
                            collapsedView.setTextViewText(R.id.custom_notif_region_regionname_collapsed, region);
                            collapsedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo_collapsed, BitmapFactory.decodeFile(imagepath));
                            expandedView.setImageViewBitmap(R.id.custom_notif_region_regionlogo, BitmapFactory.decodeFile(imagepath));

                            Notification notification = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                                    .setSmallIcon(R.drawable.ic_p)
                                    .setColor(context.getResources().getColor(R.color.blue_light))
                                    .setCustomContentView(collapsedView)
                                    .setCustomBigContentView(expandedView)
                                    .setAutoCancel(true)
                                    .build();

                            notificationManagerCompat.notify(1, notification);


                }



            }





}

