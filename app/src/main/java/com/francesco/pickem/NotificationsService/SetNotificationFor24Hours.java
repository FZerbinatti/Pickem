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
    ArrayList <MatchDetails> tomorrowMatches;
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
        Log.d(TAG, "onStartJob: jobID: "+jobID);
        switch (jobID){
            case 1:
                loadSettingsForThisRegion(jobParameters);
                break;
            case 2:
                checkIfLocalImageFolderIsUpdated();
                break;
        }



        // per gli 1 fai una query delle partite di quella regione con data attuale o sucessiva
        //setta gli alarm


        //get userNotificationSettings Region, crea un arrayList di oggetti

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
        StorageReference teamsReference = storage.getReference("team_img");
        ArrayList<ImageValidator> cloud_teams_images = new ArrayList<>();
        Task<ListResult> teams_images = teamsReference.listAll();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                Integer region_image_size = regions_images.getResult().getItems().size();
                for (int i = 0; i < region_image_size; i++) {

                    String file_cloud_path = regions_images.getResult().getItems().get(i).toString();
                    String[] datetime =file_cloud_path.split("region_img/");
                    String file_name =datetime[1];
                    StorageReference regionImageReference = regionReference.child(file_name);
                    //Log.d(TAG, "run: regionImageReference:"+regionImageReference);
                    regionImageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            cloud_regions_images.add(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                            //Log.d(TAG, "onSuccess: file_name:"+file_name);
                            //Log.d(TAG, "onSuccess: storageMetadata.getCreationTimeMillis():"+storageMetadata.getCreationTimeMillis());
                        }
                    });

                }
                Log.d(TAG, "run: 002");
                // immagini region in locale
                ArrayList<String> local_regions_images = new ArrayList<>();
                File folderRegions = new File(getFilesDir().getAbsolutePath()
                        + "/images/regions");
                File[] files_regions = folderRegions.listFiles();
                Log.d(TAG, "run: "+files_regions.length);
                if(files_regions != null){
                    for(File f : files_regions){ // loop and print all file
                        String fileName = f.getName(); // this is file name
                        Log.d(TAG, "run: presente in locale:"+fileName + "dove? :" +folderRegions);
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
                    //Log.d(TAG, "run: regionImageReference:"+teamsImageReference);
                    teamsImageReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                        @Override
                        public void onSuccess(StorageMetadata storageMetadata) {
                            cloud_teams_images.add(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                            //Log.d(TAG, "onSuccess: file_name:"+file_name);
                            //Log.d(TAG, "onSuccess: storageMetadata.getCreationTimeMillis():"+storageMetadata.getCreationTimeMillis());
                        }
                    });
                }

                // immagini teams in locale
                ArrayList<String> local_teams_images = new ArrayList<>();
                File folderTeams = new File(getFilesDir()
                        + "/images/teams");
                File[] files_teams = folderTeams.listFiles();
                if(files_teams != null){
                    for(File f : files_teams){ // loop and print all file
                        String fileName = f.getName(); // this is file name
                        local_teams_images.add(fileName);
                    }
                }


                ArrayList<String> moreRegions = new ArrayList<>();
                //Log.d(TAG, "run: cloud_regions_images:"+cloud_regions_images.size());
                Integer cloud_regions_images_size =cloud_regions_images.size();
                if (cloud_regions_images_size>local_regions_images.size()){
                    for (int i=0; i<cloud_regions_images_size; i++){
                        //Log.d(TAG, "run: cloud_regions_images.get(i): "+i+"-"+cloud_regions_images.get(i));
                        if (!local_regions_images.contains(cloud_regions_images.get(i)) ){
                            //Log.d(TAG, "run: adding: "+cloud_regions_images.get(i));
                            moreRegions.add(cloud_regions_images.get(i).getName());

                        }
                    }
                   // Log.d(TAG, "run: cloud_regions_images.size():"+cloud_regions_images.size());
                    for (int i=0; i<moreRegions.size(); i++){
                       // Log.d(TAG, "immagine in piu del cloud rispetto locale: "+moreRegions.get(i).toString());
                        // devo creare un file e scaricarci dentro l'immagine
                        StorageReference regionReference = storage.getReference("region_img");
                        File file = new File(folderRegions+"/"+moreRegions.get(i));
                       // Log.d(TAG, "run: URL? = "+regionReference+"/"+moreRegions.get(i));
                        StorageReference gsReference = storage.getReferenceFromUrl(regionReference+"/"+cloud_regions_images.get(i));
                        gsReference.getFile(file);
                    }

                }
                Log.d(TAG, "run: 001");
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        //controlla che le immagini presenti siano le stesse delle immagini sul cloud
                        for (int i=0; i<cloud_regions_images.size(); i++){
                            String region_name = cloud_regions_images.get(i).getName();
                            Log.d(TAG, "run: region_name:"+region_name);
                            Long local_region_image = sqLite.getMillisCreationRegionImage(region_name);


                            if (!cloud_regions_images.get(i).getDate().toString().equals(local_region_image.toString())){
                                Log.d(TAG, "run: NOT EQUALS: "+cloud_regions_images.get(i).getDate() +" == "+ local_region_image);
                                //elimina l'immagine presente in locale

                                File folderRegionsImage = new File(getFilesDir() + "/images/regions/"+ cloud_regions_images.get(i).getName() );


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
                                        Log.d(TAG, "onSuccess: modifico nel db questi dati: "+ region_name + " creationTimeMillis: "+ storageMetadata.getCreationTimeMillis());

                                        sqLite.updateImageRegion(new ImageValidator(region_name,storageMetadata.getCreationTimeMillis()));
                                    }
                                });


                                //dopo che l'ha cancellata fai un for e verifica le immagini presenti in loclae per assicurarti che non abbia fatto il cazzone
/*                                File folderRegions = new File(getFilesDir().getAbsolutePath()
                                        + "/images/regions");
                                File[] files_regions = folderRegions.listFiles();
                                Log.d(TAG, "run: "+files_regions.length);
                                if(files_regions != null){
                                    for(File f : files_regions){ // loop and print all file
                                        String fileName = f.getName(); // this is file name
                                        Log.d(TAG, "run: presente in locale:"+fileName + "dove? :" +folderRegions);

                                    }
                                }*/
                                

                            }/*else {
                                Log.d(TAG, "run: EQUALS: "+cloud_regions_images.get(i).getDate() +" == "+ local_region_image);
                            }*/
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




    private void thisUserNotificationPreference(ArrayList<RegionNotifications> regionNotifications)  {
        Log.d(TAG, "thisUserNotificationPreference: ?????????????????????????????"+regionNotifications.size());
        tomorrowUserunpickedMatches = new ArrayList<>();
        tomorrowMatches = new ArrayList<>();


        for (int i=0; i< regionNotifications.size(); i++){
            Log.d(TAG, "thisUserNotificationPreference: region: "+regionNotifications.get(i).getRegion_name() );
            Log.d(TAG, "thisUserNotificationPreference: getNotification_first_match_otd: "+regionNotifications.get(i).getNotification_first_match_otd());
            Log.d(TAG, "thisUserNotificationPreference: getNotification_morning_reminder: "+regionNotifications.get(i).getNotification_morning_reminder());
            Log.d(TAG, "thisUserNotificationPreference: getNo_choice_made: "+regionNotifications.get(i).getNo_choice_made());

        }



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





    }

    private void setToTheFirstOfThisMatches(ArrayList<MatchDetails> tomorrowMatches) {
        Integer ora_minima =24;
        //primo_match_otd = "";
        CurrentRegion currentRegion =new CurrentRegion();

        firstsMatchOfTheDay= new ArrayList<>();
        Log.d(TAG, "setToTheFirstOfThisMatches: userRegionsNotifications.size():"+userRegionsNotifications.size());
        // dell'AL arrivato, dividi nelle regioni diverse e per ognuna trova la data piu recente
        for (int i=0; i<userRegionsNotifications.size();i++){
            currentRegion.setRegion(userRegionsNotifications.get(i).getRegion_name());
            Log.d(TAG, "setToTheFirstOfThisMatches: userRegionsNotifications.size(): "+userRegionsNotifications.size());
            for (int j=0; j<tomorrowMatches.size();j++){
                if(tomorrowMatches.get(j).getWinner().equals(userRegionsNotifications.get(i).getRegion_name())){
                    Log.d(TAG, "setToTheFirstOfThisMatches:  tomorrowMatches.get(j).getDatetime():"+ tomorrowMatches.get(j).getDatetime());
                    String[] datetime = tomorrowMatches.get(j).getDatetime().split("T");


                    Log.d(TAG, "setToTheFirstOfThisMatches: datetime.length:"+datetime.length);
                    if (datetime.length==2){
                        String time =datetime[1];


                        String[] time_array = time.split(":");

                        String ora ="";
                        String minuto = "";
                        String secondo = "";



                        if (time_array.length ==3){

                            ora = time_array[0];
                            Log.d(TAG, "setToTheFirstOfThisMatches: ora: "+ora);
                            Integer int_ora =Integer.parseInt(ora);
                            if (int_ora < ora_minima){
                                ora_minima = int_ora;
                                primo_match_otd = tomorrowMatches.get(j).getDatetime();
                                Log.d(TAG, "setToTheFirstOfThisMatches: tomorrowMatches.get(j).getDatetime():" +tomorrowMatches.get(j).getDatetime() +" region: "+ tomorrowMatches.get(j).getWinner());
                            }

                        }
                    }

                }
            }

            Log.d(TAG, "setToTheFirstOfThisMatches: primo_match_otd:"+primo_match_otd);

            Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

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
            Log.d(TAG, "******************************* questo è il primo match di domani: millis:"+calendar.getTimeInMillis() +" region: "+currentRegion.getRegion());
            alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), alarmIntent);

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
