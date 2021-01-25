package com.francesco.pickem.NotificationsService;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.MatchNotification;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import static com.google.android.gms.tasks.Tasks.await;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class SetNotificationFor24Hours extends JobService {
    private static final String CHANNEL_ID = "1";
    String TAG = "SetNotificationFor24Hours";
    ArrayList<RegionNotifications> userRegionsNotifications;
    ArrayList<TeamNotification> userTeamsNotifications;
    ArrayList <MatchNotification> tomorrowsMatches;
    ArrayList <String> tomorrowUserunpickedMatches;
    ArrayList <MatchNotification> notPickedMatchIDs;
    Calendar myCalendar;
    String year , today, tomorrow;
    private DatabaseReference reference;
    Integer positionJ;
    Integer positionI;
    private boolean jobCancelled = false;
    TaskUtil taskUtil;
    String region_test;
    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;
    private Context context;

    //il servizio viene chiamato ogni volta che si apre l'app, ogni volta che si riprende la rete, reboot e ogni 24 ore
    //il servizio setta alarmManager in base ai NotificationSettings scelti che rientrano nelle prossime 24 ore

    public SetNotificationFor24Hours(Context context){
        this.context=context;
    }

    public SetNotificationFor24Hours(){

    }


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));
        today = getTodayDate();
        tomorrow = getTomorrowsDate();



        // per gli 1 fai una query delle partite di quella regione con data attuale o sucessiva
        //setta gli alarm


        //get userNotificationSettings Region, crea un arrayList di oggetti
        loadSettingsForThisRegion(jobParameters);
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        Log.d(TAG, "onStopJob: ");
        jobCancelled = true;
        return false;
    }



    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_pickem);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void loadSettingsForThisRegion(JobParameters parameters ) {
        if (jobCancelled){return;}
        createNotificationChannel();
        userRegionsNotifications= new ArrayList<>();

        DatabaseReference notificationRegionReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_user_notification_region));

        notificationRegionReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    RegionNotifications regionNotifications = snapshot.getValue(RegionNotifications.class);
                    if (regionNotifications !=null){
                        userRegionsNotifications.add(regionNotifications);
                    }

                }
                getUserUnpickedMatches(userRegionsNotifications);

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

        jobFinished(parameters, false);
    }


/*    private void setUserRegionNotifications(ArrayList <RegionNotifications> userRegionsNotifications){

        String today_date = getTodayDate();

        // prendi tutti gli id delle partite, splitta T e mantieni solo quelli della giornata giusta
        //setta gli alarmManager per quelli
        for (int i=0; i<userRegionsNotifications.size(); i++){
            region_test = userRegionsNotifications.get(i).getRegion_name();
            Log.d(TAG, "setAlarmRegion: region_test: "+region_test);
            MatchNotification mdwr = new MatchNotification();
            mdwr.setRegion(region_test);
            // se l'utente vuol che per questa regione gli sia mandata la notifica se si è dimenticato di fare picks
            // (fai il controllo per la data di domani e imposta la notifica per domani mattina)
            if (userRegionsNotifications.get(i).getNo_choice_made()>0){
                Log.d(TAG, "setUserRegionNotifications: "+userRegionsNotifications.get(i).getNo_choice_made() +" region : "+ userRegionsNotifications.get(i).getRegion_name());


                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                        .child(userRegionsNotifications.get(i).getRegion_name())
                        .child(userRegionsNotifications.get(i).getRegion_name() + year);


                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        int count = (int) dataSnapshot.getChildrenCount();
                        tomorrowsMatches= new ArrayList<>();
                        int current_cycle_number = 0;

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            MatchNotification mdwr2 = new MatchNotification();
                            current_cycle_number++;
                            MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);

                            if (matchDetails!=null){
                                String[] datetime = matchDetails.getDatetime().split("T");
                                String data =datetime[0];
                            // for test purpose lascio today, da cambiare con tomorrow per lo scopo della funzione
                                if (data.equals(today)){
                                    mdwr2.setRegion(mdwr.getRegion());
                                    mdwr2.setId(matchDetails.getId());
                                    mdwr2.setDatetime(matchDetails.getDatetime());
                                    mdwr2.setTeam1(matchDetails.getTeam1());
                                    mdwr2.setTeam2(matchDetails.getTeam2());

                                    tomorrowsMatches.add(mdwr2);
                                }
                            }

                            if (current_cycle_number == count){
                                    compareToUserPickedMatchesTomorrow(tomorrowsMatches, userRegionsNotifications);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

            }
        }
    }

    private void compareToUserPickedMatchesTomorrow(ArrayList<MatchNotification> tomorrowsMatches, ArrayList <RegionNotifications> userRegionsNotifications) {

        for (int i=0; i<userRegionsNotifications.size(); i++){

            // prendi tutti i pick dell'utente di domani

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(userRegionsNotifications.get(i).getRegion_name())
                    .child(userRegionsNotifications.get(i).getRegion_name() + year);

            Log.d(TAG, "compareToUserPickedMatchesTomorrow: querying in: "+userRegionsNotifications.get(i).getRegion_name());


            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    int count = (int) dataSnapshot.getChildrenCount();
                    tomorrowUserPickedMatches= new ArrayList<>();
                    int current_cycle_number = 0;

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        current_cycle_number++;

                        String match_ID = snapshot.getKey().toString();
                        Log.d(TAG, "onDataChange: "+match_ID);

                        String[] datetime = match_ID.split("T");
                        String data =datetime[0];
                        // for test purpose lascio today, da cambiare con tomorrow per lo scopo della funzione
                        if (data.equals(today)){
                            tomorrowUserPickedMatches.add(match_ID);
                        }

                        Log.d(TAG, "onDataChange: "+current_cycle_number);
                        Log.d(TAG, "onDataChange: "+count);
                        if (current_cycle_number == count){
                            getJustUnpickedMatchesForTomorrow(tomorrowsMatches, tomorrowUserPickedMatches);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }





    }*/

    private void setAlarmsBecauseThisMatchesHasNotBeenPicked(ArrayList<String> tomorrowUserUnpickedMatches) {
        //Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: ");
        /*for (int i=0; i<tomorrowUserUnpickedMatches.size(); i++){
            Log.d(TAG, "compareToUserPickedMatchesTomorrow: region: "+tomorrowUserUnpickedMatches.get(i) +" datetime: "+tomorrowUserUnpickedMatches.get(i));
        }*/

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("TYPE", "NOT_PICKED");
        intent.putExtra("MATCHES", tomorrowUserUnpickedMatches);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: "+tomorrow+" 7:00:00");
        try {
            calendar.setTime(formatter.parse(tomorrow+"T07:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: millis:"+calendar.getTimeInMillis());
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);


    }

    private void getUserUnpickedMatches( ArrayList<RegionNotifications> regionNotifications)  {
        tomorrowUserunpickedMatches = new ArrayList<>();


            for (int i=0; i< regionNotifications.size(); i++){

                if (regionNotifications.get(i).getNo_choice_made()>0){

                reference = FirebaseDatabase.getInstance().getReference("Users")
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getResources().getString(R.string.firebase_users_picks))
                        .child(regionNotifications.get(i).getRegion_name())
                        .child(regionNotifications.get(i).getRegion_name()+year);

                readData(reference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {

                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            String match_ID = snapshot.getKey().toString();
                            String matchUserResult = snapshot.getValue(String.class);

                            String[] datetime = match_ID.split("T");
                            String data =datetime[0];
                            // for test purpose lascio today, da cambiare con tomorrow per lo scopo della funzione
                            if (data.equals(tomorrow)&&matchUserResult.equals("unpicked")){
                                tomorrowUserunpickedMatches.add(match_ID);
                            }

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

        //getTheEarliestNotPickedMatchDate(notPickedMatchIDs);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                if(tomorrowUserunpickedMatches.size()>0){
                    setAlarmsBecauseThisMatchesHasNotBeenPicked(tomorrowUserunpickedMatches);
                }

            }
        }, 3000);



    }

    /*private void getTheEarliestNotPickedMatchDate(ArrayList<String> match_IDs) throws ParseException {
        Log.d(TAG, "setAlarmForTheRecentestMatchOfThose: size: "+match_IDs.size());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String earliest_not_picked_match = year +"-12-30T23:59:59";



        for(int i = 0; i < match_IDs.size(); i++){
            Date earliest_date = sdf.parse(earliest_not_picked_match);
            Date strDate = sdf.parse(match_IDs.get(i));
            if (strDate.before(earliest_date)) {
                earliest_not_picked_match = match_IDs.get(i).toString();
            }

        }

        setAlarm1hBeforeThisDateTime(earliest_not_picked_match);

    }*/


    private String getTodayDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        return date;
    }

    private String getTomorrowsDate(){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date tomorrow = calendar.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        String tomorrowAsString = sdf.format(tomorrow);
        return tomorrowAsString;
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





}
