package com.dreamsphere.pickem.NotificationsService;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.dreamsphere.pickem.Activities.MainActivities.PicksActivity;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.DatabaseHelper;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class ForegroundService extends Service {

    DatabaseHelper databaseHelper;

    public static final String TAG ="ForegroundService";
    @Override
    public void onCreate() {
        super.onCreate();
        databaseHelper = new DatabaseHelper(getBaseContext());
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String input = intent.getStringExtra("inputExtra");
        Intent notificationIntent = new Intent(this, PicksActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, "1")
                .setContentTitle("Notification Engine")
                .setContentText(input)
                .setSmallIcon(R.drawable.ic_p)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();

        setAlarmManager();

        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }



    public String getTodayDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        return date;
    }

    public String currentYear(){
        Calendar myCalendar;
        String year;
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        return year;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    private void setAlarmManager() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        intent.putExtra("TYPE", "ENGINE");
        PendingIntent sender = PendingIntent.getBroadcast(this, 2, intent, 0);
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        /*long l = new Date().getTime();
        if (l < new Date().getTime()) {
            l += 86400000; // start at next 24 hour
        }*/
        //   86400000
        am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60*60000, sender); // 86400000
    }
}

