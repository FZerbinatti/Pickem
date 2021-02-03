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
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.ImageValidator;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.MatchNotification;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.SQLite;
import com.google.android.gms.common.api.internal.TaskUtil;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import static com.google.android.gms.tasks.Tasks.await;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
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
    ArrayList <MatchDetails> tomorrowMatches, allTomorrowMatches;
    ArrayList <MatchDetails> firstsMatchOfTheDay;
    ArrayList <MatchNotification> notPickedMatchIDs;
    Calendar myCalendar;
    String year, today, tomorrow;
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
    RegionNotifications regionNotifications;
    SQLite sqLite;

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
        Integer jobID = jobParameters.getJobId();
        //Log.d(TAG, "onStartJob: jobID: "+jobID);
        switch (jobID){
            case 1:
                loadSettingsForThisRegion(jobParameters);
                break;
            case 2:
                checkIfLocalImageFolderIsUpdated();
                break;
        }

        return true;
    }

    private void checkIfLocalImageFolderIsUpdated() {
        //Log.d(TAG, "checkIfLocalImageFolderIsUpdated: ");

    // 1- prendi la lista delle immagini presenti in locale,
        // se 0 scarica tutto
    // 2- prendi la lista delle immagini nelle cartelle cloud
        // se differiscono riscarica le differenze
        FirebaseStorage storage = FirebaseStorage.getInstance();
        sqLite = new SQLite(getApplicationContext());

        //immagini regions sul cloud
        StorageReference regionReference = storage.getReference("region_img");
        ArrayList<ImageValidator> cloud_regions_images = new ArrayList<>();
        Task<ListResult> regions_images = regionReference.listAll();
        //IMMAGINI TEAMS SUL CLOUD
        StorageReference teamsReference = storage.getReference("team_img");
        ArrayList<ImageValidator> cloud_teams_images = new ArrayList<>();
        Task<ListResult> teams_images = teamsReference.listAll();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                // immagini regions in cloud
                Integer region_image_size = regions_images.getResult().getItems().size();
                for (int i = 0; i < region_image_size; i++) {
                    String file_cloud_path = regions_images.getResult().getItems().get(i).toString();
                    String[] datetime =file_cloud_path.split("region_img/");
                    String file_name =datetime[1];
                    StorageReference regionImageReference = regionReference.child(file_name);
                    regionImageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            cloud_regions_images.add(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                        }
                    });
                }
                // immagini region in locale

                ArrayList<String> local_regions_images = new ArrayList<>();
                File folderRegions = new File(getFilesDir().getAbsolutePath()
                        + (getString(R.string.folder_regions_images)));
                File[] files_regions = folderRegions.listFiles();
                if(files_regions != null){
                    for(File f : files_regions){
                        String fileName = f.getName();
                        //Log.d(TAG, "immagini region locale: "+fileName);
                        local_regions_images.add(fileName);
                    }
                }

                //immagini teams sul cloud
                Integer teams_image_size = teams_images.getResult().getItems().size();
                for (int i = 0; i < teams_image_size; i++) {
                    String file_cloud_path = teams_images.getResult().getItems().get(i).toString();
                    String[] datetime =file_cloud_path.split("team_img/");
                    String file_name =datetime[1];
                    StorageReference teamsImageReference = teamsReference.child(file_name);
                    teamsImageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            cloud_teams_images.add(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                        }
                    });
                }
                // immagini teams in locale
                ArrayList<String> local_teams_images = new ArrayList<>();
                File folderTeams = new File(getFilesDir().getAbsolutePath()
                        +getString(R.string.folder_teams_images));
                File[] files_teams = folderTeams.listFiles();
                if(files_teams != null){
                    for(File f : files_teams){ // loop and print all file
                        String fileName = f.getName(); // this is file name
                        local_teams_images.add(fileName);
                    }
                }

                // se ci sono piu immagini regions sul cloud di quelle locali
                ArrayList<String> moreRegions = new ArrayList<>();
                Integer cloud_regions_images_size =cloud_regions_images.size();
                if (cloud_regions_images_size>local_regions_images.size()){
                    for (int i=0; i<cloud_regions_images_size; i++){
                        if (!local_regions_images.contains(cloud_regions_images.get(i).getName()) ){
                            moreRegions.add(cloud_regions_images.get(i).getName());
                        }
                    }
                    for (int i=0; i<moreRegions.size(); i++){
                        // devo creare un file e scaricarci dentro l'immagine
                        StorageReference regionReference = storage.getReference("region_img");
                        File file = new File(folderRegions+"/"+moreRegions.get(i));
                        StorageReference gsReference = storage.getReferenceFromUrl(regionReference+"/"+cloud_regions_images.get(i));
                        gsReference.getFile(file);
                    }
                }

                // se ci sono piu immagini regions sul cloud di quelle locali
                ArrayList<String> moreTeams = new ArrayList<>();
                Integer cloud_teams_images_size =cloud_teams_images.size();
                if (cloud_teams_images_size>local_teams_images.size()){
                    for (int i=0; i<cloud_teams_images_size; i++){
                        if (!local_teams_images.contains(cloud_teams_images.get(i).getName()) ){
                            moreTeams.add(cloud_teams_images.get(i).getName());
                        }
                    }
                    for (int i=0; i<moreTeams.size(); i++){
                        // devo creare un file e scaricarci dentro l'immagine
                        StorageReference teamsReference = storage.getReference("team_img");
                        File file = new File(folderTeams+"/"+moreTeams.get(i));
                        StorageReference gsReference = storage.getReferenceFromUrl(teamsReference+"/"+cloud_teams_images.get(i));
                        gsReference.getFile(file);
                    }
                }




                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        //controlla che le immagini presenti siano le stesse delle immagini sul cloud
                        for (int i=0; i<cloud_regions_images.size(); i++){
                            String region_name = cloud_regions_images.get(i).getName();
                            Long local_region_image = sqLite.getMillisCreationRegionImage(region_name);
                            if (local_region_image >0){
                                if (!cloud_regions_images.get(i).getDate().toString().equals(local_region_image.toString())){
                                    Log.d(TAG, "run: NOT EQUALS: "+cloud_regions_images.get(i).getDate() +" == "+ local_region_image +" for: "+cloud_regions_images.get(i).getName());
                                    //elimina l'immagine presente in locale
                                    File folderRegionsImage = new File(getFilesDir() + getString(R.string.folder_regions_images)+ cloud_regions_images.get(i).getName() );
                                    if (folderRegionsImage.exists()) {
                                        if (folderRegionsImage.delete()) {
                                            Log.d(TAG, "run: deleted: "+folderRegionsImage);
                                        } else {
                                            Log.d(TAG, "run: error, not found: "+folderRegionsImage);
                                        }

                                    }else {
                                        Log.d(TAG, "NOT EXIST: path:"+folderRegionsImage.getAbsolutePath());
                                    }
                                    //scarica l'immagine nello stesso path
                                    StorageReference gsReference = storage.getReferenceFromUrl(regionReference+"/"+cloud_regions_images.get(i).getName());
                                    gsReference.getFile(folderRegionsImage);
                                    gsReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {
                                            //Log.d(TAG, "onSuccess: modifico nel db questi dati: "+ region_name + " creationTimeMillis: "+ storageMetadata.getCreationTimeMillis());
                                            sqLite.updateImageRegion(new ImageValidator(region_name,storageMetadata.getCreationTimeMillis()));
                                        }
                                    });


                                }
                            }


                        }
                        // controllo sincronizzazione immagini localie  cloud per Teams
                        for (int i=0; i<cloud_teams_images.size(); i++){
                            String team_name = cloud_teams_images.get(i).getName();
                            Long local_teams_image = sqLite.getMillisCreationTeamImage(team_name);
                            if (local_teams_image>0){

                                if (!cloud_teams_images.get(i).getDate().toString().equals(local_teams_image.toString())){
                                    Log.d(TAG, "run: NOT EQUALS: "+cloud_teams_images.get(i).getDate() +" == "+ local_teams_image);
                                    //elimina l'immagine presente in locale
                                    File folderTeamsImage = new File(getFilesDir() + getString(R.string.folder_teams_images)+ cloud_teams_images.get(i).getName() );
                                    if (folderTeamsImage.exists()) {
                                        folderTeamsImage.deleteOnExit();
                                        if (folderTeamsImage.delete()) {
                                            Log.d(TAG, "run: deleted: "+folderTeamsImage);
                                        } else {
                                            Log.d(TAG, "run: error, not found: "+folderTeamsImage);
                                        }
                                    }else {
                                        Log.d(TAG, "NOT EXIST: path:"+folderTeamsImage.getAbsolutePath());
                                    }
                                    //scarica l'immagine nello stesso path

                                    StorageReference gsReference = storage.getReferenceFromUrl(teamsReference+"/"+cloud_teams_images.get(i).getName());
                                    //Log.d(TAG, "run: cerco di scaricare l'immagine da: "+gsReference + " ||||| "+teamsReference+"/"+cloud_teams_images.get(i).getName());
                                    gsReference.getFile(folderTeamsImage);
                                    gsReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {
                                            //Log.d(TAG, "onSuccess: scaricato,  modifico nel db questi dati: "+ team_name + " creationTimeMillis: "+ storageMetadata.getCreationTimeMillis());
                                            sqLite.updateImageTeams(new ImageValidator(team_name,storageMetadata.getCreationTimeMillis()));
                                        }
                                    });
                                }
                            }

                        }


                    }
                }, 3000);



            }
        }, 3000);


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
                        //Log.d(TAG, "onDataChange: "+regionNotifications.getRegion_name());
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




    private void thisUserNotificationPreference(ArrayList<RegionNotifications> regionNotifications)  {
        //Log.d(TAG, "thisUserNotificationPreference: ?????????????????????????????"+regionNotifications.size());
        tomorrowUserunpickedMatches = new ArrayList<>();
        tomorrowMatches = new ArrayList<>();
        allTomorrowMatches = new ArrayList<>();


        /*for (int i=0; i< regionNotifications.size(); i++){
            Log.d(TAG, "thisUserNotificationPreference: region: "+regionNotifications.get(i).getRegion_name() );
            Log.d(TAG, "thisUserNotificationPreference: getNotification_first_match_otd: "+regionNotifications.get(i).getNotification_first_match_otd());
            Log.d(TAG, "thisUserNotificationPreference: getNotification_morning_reminder: "+regionNotifications.get(i).getNotification_morning_reminder());
            Log.d(TAG, "thisUserNotificationPreference: getNo_choice_made: "+regionNotifications.get(i).getNo_choice_made());
        }*/


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
                    tomorrowMatches = new ArrayList<>();
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
                                    match.setDatetime(convertDatetimeZtoLocale(match.getDatetime()));
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

                if (regionNotifications.get(i).getNotification_morning_reminder()>0){
                    // prendi tutti i match di domani (se ce ne sono) e crea una notifica da displayare domani mattina
                    allTomorrowMatches = new ArrayList<>();
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
                                    match.setDatetime(convertDatetimeZtoLocale(match.getDatetime()));
                                    allTomorrowMatches.add(match);
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

            //getTheEarliestNotPickedMatchDate(notPickedMatchIDs);
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    if(tomorrowUserunpickedMatches.size()>0){
                        Log.d(TAG, "run: trigger: unpicked");
                        setAlarmsBecauseThisMatchesHasNotBeenPicked(tomorrowUserunpickedMatches);
                    }
                    if(tomorrowMatches.size()>0){
                        Log.d(TAG, "run: trigger at first match start");
                        setToTheFirstOfThisMatches(tomorrowMatches);
                    }

                    if (allTomorrowMatches.size()>0){
                        Log.d(TAG, "run: trigger morning reminder");
                        setNotificationWithTomorrowMatches(allTomorrowMatches);
                    }

                }
            }, 3000);

        }





    }

    private void setNotificationWithTomorrowMatches(ArrayList<MatchDetails> allTomorrowMatches) {
        //Log.d(TAG, "setNotificationWithTomorrowMatches: "+allTomorrowMatches.size());
        ArrayList<String> tomorrowStringArrayMatches = new ArrayList<>();

        Collections.sort(allTomorrowMatches, new MatchDetails.ByDatetime());

        for (int i=0; i<allTomorrowMatches.size(); i++){
            tomorrowStringArrayMatches.add(allTomorrowMatches.get(i).getWinner()+" " +getTimeMinutesFromDatetime(allTomorrowMatches.get(i).getDatetime()) +" -  [ " + allTomorrowMatches.get(i).getTeam1() + " vs " + allTomorrowMatches.get(i).getTeam2() + " ]");
        }
        //Log.d(TAG, "setNotificationWithTomorrowMatches: tomorrowStringArrayMatches.size:"+tomorrowStringArrayMatches.size());

        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        intent.putExtra("TYPE", "ALL_TMATCHES");
        intent.putStringArrayListExtra("ALL_T_MATCHES", tomorrowStringArrayMatches);
        alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        //Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: "+tomorrow+" 7:00:00");
        try {
            calendar.setTime(formatter.parse(tomorrow+"T07:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "********************************************************* setNotificationWithTomorrowMatches: millis:"+calendar.getTimeInMillis());
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        // testing purpose
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, alarmIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+6000, alarmIntent);

    }

    private void setToTheFirstOfThisMatches(ArrayList<MatchDetails> tomorrowMatches) {
        Integer ora_minima =24;
        primo_match_otd = "";
        CurrentRegion currentRegion =new CurrentRegion();


        firstsMatchOfTheDay= new ArrayList<>();
        //Log.d(TAG, "setToTheFirstOfThisMatches: userRegionsNotifications.size():"+userRegionsNotifications.size());
        // dell'AL arrivato, dividi nelle regioni diverse e per ognuna trova la data piu recente
        for (int i=0; i<userRegionsNotifications.size();i++){
            currentRegion.setRegion(userRegionsNotifications.get(i).getRegion_name());
            //Log.d(TAG, "setToTheFirstOfThisMatches: userRegionsNotifications.size(): "+userRegionsNotifications.size());
            for (int j=0; j<tomorrowMatches.size();j++){
                if(tomorrowMatches.get(j).getWinner().equals(userRegionsNotifications.get(i).getRegion_name())){
                    //Log.d(TAG, "setToTheFirstOfThisMatches:  tomorrowMatches.get(j).getDatetime():"+ tomorrowMatches.get(j).getDatetime());

                    Integer int_ora =Integer.parseInt(getTimeFromDatetime(tomorrowMatches.get(j).getDatetime()));
                    if (int_ora < ora_minima){
                        ora_minima = int_ora;
                        primo_match_otd = tomorrowMatches.get(j).getDatetime();
                        //Log.d(TAG, "setToTheFirstOfThisMatches: tomorrowMatches.get(j).getDatetime():" +tomorrowMatches.get(j).getDatetime() +" region: "+ tomorrowMatches.get(j).getWinner());
                    }

                }
            }

            if (!primo_match_otd.equals("")){
                //Log.d(TAG, "setToTheFirstOfThisMatches: primo_match_otd:"+primo_match_otd);

                Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

                intent.putExtra(getResources().getString(R.string.TYPE) ,getResources().getString(R.string.FIRST_MATCH));
                intent.putExtra("REGION", currentRegion.getRegion() );

                alarmManager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                alarmIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                try {
                    calendar.setTime(formatter.parse(primo_match_otd));
                    calendar.add(Calendar.MINUTE,5);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "******************************* questo è il primo match di domani: millis:"+calendar.getTimeInMillis()+" region: "+currentRegion.getRegion());
                //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
                alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+3000, alarmIntent);
            }
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
        //Log.d(TAG, "setAlarmsBecauseThisMatchesHasNotBeenPicked: "+tomorrow+" 7:00:00");
        try {
            calendar.setTime(formatter.parse(tomorrow+"T07:00:00"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "********************************************* setAlarmsBecauseThisMatchesHasNotBeenPicked: millis:"+calendar.getTimeInMillis());
        //Log.d(TAG, "********************************************* setAlarmsBecauseThisMatchesHasNotBeenPicked: millis:"+(System.currentTimeMillis()+2000));
        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);
        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+2000, alarmIntent);



    }


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

    public String convertDatetimeZtoLocale(String datetime){
        //Log.d(TAG, "convertDatetimeZtoLocale: eating this: "+datetime);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
        Date strDate = null;

        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date = null;
        try {
            date = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.setTimeZone(TimeZone.getDefault());
        String formattedDate = sdf.format(date);

        //Log.d(TAG, "convertDatetimeZtoLocale: shitting this: "+formattedDate.toString());

        return formattedDate.toString();
    }

    private String getTimeFromDatetime(String datetimeInput){

        String ora = "";
        String[] datetime = datetimeInput.split("T");

        if (datetime.length==2) {
            String time = datetime[1];

            String[] time_array = time.split(":");

            if (time_array.length == 3) {
                ora = time_array[0];
            }
        }

        return ora;
    }

    private String getTimeMinutesFromDatetime(String datetimeInput){

        String time="";
        String ora = "";
        String minuto = "";
        String[] datetime = datetimeInput.split("T");

        if (datetime.length==2) {
            time = datetime[1];

            String[] time_array = time.split(":");

            if (time_array.length == 3) {
                ora = time_array[0];
                minuto = time_array[1];
            }
        }

        return ora+":"+minuto;
    }

}
