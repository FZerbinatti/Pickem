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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.R;

import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "Drugs Alarm";
    private static String NOTIFICATION_ID = "1";




    @Override
    public void onReceive(Context context, Intent intent) {

            int notificationID = new Random().nextInt();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

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





}

