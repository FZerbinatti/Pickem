package com.francesco.pickem.NotificationsService;

import android.app.IntentService;
import android.content.Intent;

import androidx.annotation.Nullable;

public class SetNotificationFor24Hours extends IntentService {
    public SetNotificationFor24Hours(String name) {
        super(name);
    }

    //il servizio viene chiamato ogni volta che si apre l'app, ogni volta che si riprende la rete, reboot e ogni 24 ore
    //il servizio setta alarmManager in base ai NotificationSettings scelti che rientrano nelle prossime 24 ore

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        //get userNotificationSettings

    }



}
