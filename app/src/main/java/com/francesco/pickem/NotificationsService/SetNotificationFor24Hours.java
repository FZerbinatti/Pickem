package com.francesco.pickem.NotificationsService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.MatchNotification;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SetNotificationFor24Hours extends JobService {
    private static final String CHANNEL_ID = "1";
    String TAG = "SetNotificationFor24Hours";
    ArrayList<RegionNotifications> userRegionsNotifications;
    ArrayList<TeamNotification> userTeamsNotifications;
    ArrayList <MatchNotification> matchDetailsList;
    Calendar myCalendar;
    String year;
    private DatabaseReference reference;
    Integer positionJ;
    Integer positionI;
    private boolean jobCancelled = false;

    //il servizio viene chiamato ogni volta che si apre l'app, ogni volta che si riprende la rete, reboot e ogni 24 ore
    //il servizio setta alarmManager in base ai NotificationSettings scelti che rientrano nelle prossime 24 ore


    @Override
    public boolean onStartJob(JobParameters jobParameters) {
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));

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
                setAlarmRegion(userRegionsNotifications);

            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

        jobFinished(parameters, false);
    }

    private void setAlarmRegion(ArrayList <RegionNotifications> userRegionsNotifications){
        Log.d(TAG, "setAlarmRegion: ");
        matchDetailsList= new ArrayList<>();
        String today_date = getTodayDate();

        // prendi tutti gli id delle partite, splitta T e mantieni solo quelli della giornata giusta
        //setta gli alarmManager per quelli
        for (Integer i=0; i<userRegionsNotifications.size(); i++){

            if (userRegionsNotifications.get(i).getNo_choice_made()>0){
                positionI = i;

                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                        .child(userRegionsNotifications.get(i).getRegion_name())
                        .child(userRegionsNotifications.get(i).getRegion_name() + year);

                //Log.d(TAG, "loadRecyclerView: "+selected_region_name+"/"+selected_region_name + year+"/"+selected_region_name + year + split+"/"+loadThisMatchesID.get(i));

                readData(reference, new OnGetDataListener() {
                    @Override
                    public void onSuccess(DataSnapshot dataSnapshot) {
                        //Log.d(TAG, "onSuccess: ");
                        int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                        for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                            MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);

                            //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                            if (matchDetails!=null){

                                String[] datetime = matchDetails.getDatetime().split("T");

                                String data =datetime[0];


                                Log.d(TAG, "onSuccess: today_date:"+today_date);
                                Log.d(TAG, "onSuccess: data:"+data);
                                if (data.equals(today_date)){
                                    MatchNotification mdwr = new MatchNotification();
                                    Log.d(TAG, "onSuccess: true");

                                    mdwr.setRegion(userRegionsNotifications.get(positionI).getRegion_name());
                                    mdwr.setId(matchDetails.getId());
                                    mdwr.setDatetime(matchDetails.getDatetime());
                                    mdwr.setTeam1(matchDetails.getTeam1());
                                    mdwr.setTeam2(matchDetails.getTeam2());

                                    matchDetailsList.add(mdwr);
                                }



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

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                // Actions to do after 10 seconds
                Log.d(TAG, "setAlarmRegion: matchDetailsList: "+matchDetailsList.size());
                areThisMatchesBeenPicked(matchDetailsList, userRegionsNotifications);
            }
        }, 3000);




    }

    private void areThisMatchesBeenPicked(ArrayList<MatchNotification> matchDetailsList, ArrayList<RegionNotifications> regionNotifications) {

        // sono arrivate le partite della data odierna, se rientrano nei picks apposto
        Log.d(TAG, "setAlarmsForThisMatches: "+matchDetailsList.size());

        for (int i=0; i< regionNotifications.size(); i++){
            Log.d(TAG, "loadRecyclerView: "+getResources().getString(R.string.firebase_users_picks)+"/"+regionNotifications.get(i).getRegion_name() +"/"+ regionNotifications.get(i).getRegion_name() + year);

            reference = FirebaseDatabase.getInstance().getReference("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getResources().getString(R.string.firebase_users_picks))
                    .child(regionNotifications.get(i).getRegion_name())
                    .child(regionNotifications.get(i).getRegion_name()+year);

            if (regionNotifications.get(i).getNo_choice_made()>0){
                // prendi il valore della partita nei pick dell'utente, se non c'è, setta l'alarm manager

                for (int j=0; j< matchDetailsList.size(); j++){

                    positionJ = j;

                    if (matchDetailsList.get(j).getRegion().equals(regionNotifications.get(i).getRegion_name())){

                        readData(reference.child(matchDetailsList.get(j).getId()), new OnGetDataListener() {
                            @Override
                            public void onSuccess(DataSnapshot dataSnapshot) {
                                String match_ID = dataSnapshot.getKey().toString();
                                String user_pick = dataSnapshot.getValue(String.class);
                                    //Log.d(TAG, "onSuccess: "+matchDetails.getDatetime());
                                    if (user_pick ==null){
                                        // (prendi l'ora della prima partita del giorno, togli 1)
                                        Log.d(TAG, "onSuccess: il pick non esiste per questo ID: "+match_ID);

                                    }else {
                                        //String datetime_not_picked_match = matchDetailsList.get(positionJ).getDatetime();
                                        Log.d(TAG, "onSuccess: il pick esiste per questo id: "+match_ID +" e lo user ha pickkato: "+user_pick);

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


    }

    private String getTodayDate(){

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Log.d(TAG, "getTodayMatchID: ID begins with: :"+date);

        return date;
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
