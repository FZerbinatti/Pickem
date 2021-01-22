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


import java.util.Random;

public class AlarmReceiver extends BroadcastReceiver{
    private String TAG = "Drugs Alarm";
    private static String NOTIFICATION_ID = "1";




    @Override
    public void onReceive(Context context, Intent intent) {

            int notificationID = new Random().nextInt();

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);

            String nome_medicina = intent.getStringExtra("nome");
            String dose_medicina = intent.getStringExtra("dose");
            String descrizione_medicina =  intent.getStringExtra("descr");
            String ora_medicina = intent.getStringExtra("ora");
            String giorno_medicina = intent.getStringExtra("giorno");
            Integer med_presa = intent.getIntExtra("presa",-1);
            Integer alarmSet = intent.getIntExtra("alarmSet",-1);

/*            String id = intent.getStringExtra("id");
            Log.d(TAG, "onReceive: 000000000000000000000000000000000000000000000000000000000000000000000000000000000");
            Log.d(TAG, "onReceive: id: "+id);
            Log.d(TAG, "onReceive: alarmSet: "+alarmSet);
            Log.d(TAG, "onReceive: presa: "+med_presa);
            Log.d(TAG, "onReceive: ora: "+ora_medicina);
            DrugMed sampleDrug = new DrugMed();
            sampleDrug.setgDiPartenza(giorno_medicina);
            sampleDrug.setOra(ora_medicina);



            if (med_presa ==-1 && ss.isThisDrugAmmissible(sampleDrug)){
                Log.d(TAG, "onReceive: YES ");
                Log.d(TAG, "onReceive: "+sampleDrug.getgDiPartenza());
                Log.d(TAG, "onReceive: "+sampleDrug.getOra());



        *//*            Intent i = new Intent(context, ModalDrugDialog.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    i.putExtra("id", id );
                    i.putExtra("ora", intent.getStringExtra("ora"));
                    i.putExtra("nome", nome_medicina);
                    i.putExtra("dose", dose_medicina);
                    i.putExtra("descr",descrizione_medicina);*//*

                //intent per andare a TabletMod quando si fa tap sulla notifica
                Intent resultIntent = new Intent(context, TabletMod.class);
                PendingIntent goToTabletModClassIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // intent per mandare al server la notifica che la medicina è stata presa e contrassegnata come tale presa==1
                Intent broadcastMedTakenIntent = new Intent(context, Broadcastreceiver_FromNotification.class);
                broadcastMedTakenIntent.setAction("DRUG_TAKEN");
                broadcastMedTakenIntent.putExtra("ID",id);
                broadcastMedTakenIntent.putExtra("NOTIFICATION_ID",notificationID);
                PendingIntent medTakenPendingIntent = PendingIntent.getBroadcast(context, 0, broadcastMedTakenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                // intent per rimandare la notifica di 10m
                *//*Intent broadcastPosticipateMed = new Intent(context, Broadcastreceiver_FromNotification.class);
                broadcastPosticipateMed.setAction("REMIND_LATER");
                PendingIntent posticipateMedPendingIntent = PendingIntent.getBroadcast(context, 0, broadcastPosticipateMed, PendingIntent.FLAG_UPDATE_CURRENT);*//*


                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, NOTIFICATION_ID)
                        .setSmallIcon(R.drawable.logo_rhs)

                        .setContentTitle("Reminder Medicina")
                        .setStyle(new NotificationCompat.InboxStyle()
                                .addLine(context.getString(R.string.notif_nome) + nome_medicina)
                                .addLine(context.getString(R.string.notif_descrizione)  + descrizione_medicina)
                                .addLine(context.getString(R.string.notif_dose) + dose_medicina)
                                .addLine(context.getString(R.string.notif_ora)  +ora_medicina))
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setContentIntent(goToTabletModClassIntent)
                        .addAction(R.drawable.take, context.getString(R.string.Mep),
                                medTakenPendingIntent)
                        // aggiunge un pulsante alla notifica
                        *//*.addAction(R.drawable.delete, context.getString(R.string.Post),
                                posticipateMedPendingIntent)*//*
                        .setAutoCancel(true);



                notificationManagerCompat.notify(notificationID, notificationBuilder.build());
                //notificationManagerCompat.cancel(200);


                    //context.startActivity(i);
            }
*/

    }



}

