package com.francesco.pickem.NotificationsService;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.MatchNotification;
import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.SimpleRegion;
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

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class SetNotificationFor24Hours extends JobService {
    private static final String CHANNEL_ID = "1";
    String TAG = "SetNotificationFor24Hours";
    ArrayList<RegionNotifications> userRegionsNotifications;
    ArrayList<TeamNotification> userTeamsNotifications;
    ArrayList <MatchNotification> tomorrowsMatches;
    ArrayList <String> tomorrowUserunpickedMatches;
    ArrayList <MatchDetails> tomorrowMatches;
    ArrayList <MatchDetails> firstsMatchOfTheDay;
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
    CurrentRegion currentRegion;
    String primo_match_otd;
    Bitmap bitmap;

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
                        Log.d(TAG, "onDataChange: "+regionNotifications.getRegion_name());
                    }

                }
                thisUserNotificationPreference(userRegionsNotifications);

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


    private void thisUserNotificationPreference(ArrayList<RegionNotifications> regionNotifications)  {
        Log.d(TAG, "thisUserNotificationPreference: ?????????????????????????????"+regionNotifications.size());
        tomorrowUserunpickedMatches = new ArrayList<>();
        tomorrowMatches = new ArrayList<>();




        for (int i=0; i< regionNotifications.size(); i++){
            CurrentRegion currentRegion = new CurrentRegion();
            currentRegion.setRegion(regionNotifications.get(i).getRegion_name());

                if (regionNotifications.get(i).getNo_choice_made()>0) {


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

                if (regionNotifications.get(i).getNotification_first_match_otd()>0){
                    //prendi il primo match di domani e piazza un alarm manager all'ora di inizio
                    reference = FirebaseDatabase.getInstance().getReference
                            (getResources().getString(R.string.firebase_Matches))
                            .child(regionNotifications.get(i).getRegion_name())
                            .child(regionNotifications.get(i).getRegion_name()+year);

                    readData(reference, new OnGetDataListener() {
                        @Override
                        public void onSuccess(DataSnapshot dataSnapshot) {

                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                                String match_ID = snapshot.getKey().toString();
                                MatchDetails match = snapshot.getValue(MatchDetails.class);

                                String[] datetime = match_ID.split("T");
                                String data =datetime[0];
                                // for test purpose lascio today, da cambiare con tomorrow per lo scopo della funzione
                                if (data.equals(tomorrow)){
                                    match.setWinner(currentRegion.getRegion());
                                    tomorrowMatches.add(match);
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
                if(tomorrowMatches.size()>0){
                    setToTheFirstOfThisMatches(tomorrowMatches);
                }

            }
        }, 3000);



    }

    private void setToTheFirstOfThisMatches(ArrayList<MatchDetails> tomorrowMatches) {
        Integer ora_minima =24;
        primo_match_otd = "";
        CurrentRegion currentRegion =new CurrentRegion();

        firstsMatchOfTheDay= new ArrayList<>();
        // dell'AL arrivato, dividi nelle regioni diverse e per ognuna trova la data piu recente
        for (int i=0; i<userRegionsNotifications.size();i++){
            currentRegion.setRegion(userRegionsNotifications.get(i).getRegion_name());
            for (int j=0; j<tomorrowMatches.size();j++){
                if(tomorrowMatches.get(j).getWinner().equals(userRegionsNotifications.get(j).getRegion_name())){

                    String[] datetime = tomorrowMatches.get(j).getDatetime().split("T");
                    String data =datetime[0];
                    if (data.length()==2){
                        String[] datetime2 = data.split(":");

                        String ora ="";
                        String minuto = "";
                        String secondo = "";


                        if (datetime.length ==3){
                            ora = datetime[0];
                            Integer int_ora =Integer.parseInt(ora);
                            if (int_ora < ora_minima){
                                ora_minima = int_ora;
                                primo_match_otd = tomorrowMatches.get(j).getDatetime();
                            }

                        }
                    }




                }
            }

            DatabaseReference regionImageReference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions))
                    .child(userRegionsNotifications.get(i).getRegion_name())
                    .child(getString(R.string.regions_image));

            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
            regionImageReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String url_image = dataSnapshot.getValue(String.class);
                    Log.d(TAG, "onDataChange: url_image:"+url_image);
                     bitmap = null;
                    RequestOptions options = new RequestOptions()
                            .fitCenter()
                            .error(R.drawable.ic_load);

                    AsyncTask.execute(new Runnable() {
                        @Override
                        public void run() {

                            FutureTarget futureTarget = Glide.with(getApplicationContext()).asBitmap().load(url_image).apply(options).submit();
                            try {
                                bitmap = (Bitmap) futureTarget.get();
                                Log.d(TAG, "run: bitmap:"+bitmap);
                                ByteArrayOutputStream bs = new ByteArrayOutputStream();
                                bitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
                                intent.putExtra("BITMAP", bs.toByteArray());
                            } catch (ExecutionException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });


                    intent.putExtra(getResources().getString(R.string.TYPE) ,getResources().getString(R.string.FIRST_MATCH));
                    intent.putExtra("REGION", currentRegion.getRegion() );

                    alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                    alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                    try {
                        calendar.setTime(formatter.parse(primo_match_otd));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG, "*******************************questo è il primo match di domani: millis:"+calendar.getTimeInMillis() +" region: "+currentRegion.getRegion() +" time: ");
                    alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }




    }

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
