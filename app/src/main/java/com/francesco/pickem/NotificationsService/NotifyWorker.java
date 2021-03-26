package com.francesco.pickem.NotificationsService;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.francesco.pickem.Annotation.NonNull;
import com.francesco.pickem.R;

import java.util.Calendar;
import java.util.Random;

public class NotifyWorker extends Worker {
    public NotifyWorker(@NonNull Context context, @NonNull WorkerParameters params) {


        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            String NOTIFICATION_ID = "1";
            int notificationID = new Random().nextInt();

            Log.d("TAG","doWork: received. ");

            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_ID)
                    .setSmallIcon(R.drawable.ic_p)
                    .setColor(getApplicationContext().getResources().getColor(R.color.blue_light))
                    .setContentText( "WORKER TEST" )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setAutoCancel(true);

            notificationManagerCompat.notify(notificationID, notificationBuilder.build());

            return Result.success();

        }else {

            return Result.retry();

        }

        // Method to trigger an instant notification
        //
        // triggerNotification();


        // (Returning RETRY tells WorkManager to try this task again
        // later; FAILURE says not to try again.)
    }
}