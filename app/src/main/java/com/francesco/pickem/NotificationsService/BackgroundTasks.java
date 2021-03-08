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
import android.os.SystemClock;
import android.text.format.Time;
import android.util.Log;

import androidx.annotation.LongDef;
import androidx.annotation.NonNull;

import com.francesco.pickem.Interfaces.OnGetDataListener;
import com.francesco.pickem.Models.CurrentRegion;
import com.francesco.pickem.Models.ImageValidator;
import com.francesco.pickem.Models.MatchDetails;
import com.francesco.pickem.Models.MatchNotification;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.Sqlite_Match;
import com.francesco.pickem.Models.Sqlite_MatchDay;
import com.francesco.pickem.Models.TeamNotification;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.DatabaseHelper;
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
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BackgroundTasks extends JobService {
    private static final String CHANNEL_ID = "1";
    String TAG = "BackgroundTasks";
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
    RegionNotifications regionNotifications;
    DatabaseHelper databaseHelper;
    BackgroundTasks backgroundTasks;
    private final Integer FIREBASE_STORAGE_RESPONSE_TIME = 10000;
    ArrayList <String> todayMatches;
    ArrayList <String> localmatchIDs;


    public BackgroundTasks(Context context){
        this.context=context;
    }

    public BackgroundTasks(){}


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
                //loadSettingsForThisRegion(jobParameters);
                break;
            case 2:
                checkIfLocalImageFolderIsUpdated(jobParameters);
                break;
            case 3:
                checkIfCurrentUserMatchDaysUpdated(jobParameters);

                break;
            case 4:
                startAlarmManager7AM();
                break;
        }

        return true;
    }

    private void startAlarmManager7AM() {
        Log.d(TAG, "startAlarmManager7AM: ");
        // un allarme che ogni mattina alle 7 controlla i match della giornata per le regioni scelte
        // se c'è un match e lo user ha getNo_choice_made()>0 per quella regione setta un allarme 1h prima dell'inizio del primo match
        // questo allarme controlla se lo user ha fatto il pick, se non l'ha fatto, setta la notifica insta

        AlarmManager alarmMgr0 = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent intent0 = new Intent(this, AlarmReceiver.class);
        intent0.putExtra("TYPE", "7AMTASK");
        PendingIntent pendingIntent0 = PendingIntent.getBroadcast(this, 1, intent0, 0);
        Calendar task7AM = Calendar.getInstance();


        task7AM.set(Calendar.HOUR_OF_DAY, 7);
        task7AM.set(Calendar.MINUTE, 0);
        task7AM.set(Calendar.SECOND, 0);
        //COMMENTED FOR TESTING PURPOSE
        task7AM.add(Calendar.DAY_OF_MONTH,1);

        task7AM.setTimeZone(TimeZone.getDefault());
        Log.d(TAG, "startAlarmManager7AM: task7AM.getTimeInMillis() "+task7AM.getTimeInMillis());

        alarmMgr0 .setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                SystemClock.elapsedRealtime() + AlarmManager.INTERVAL_DAY,
                AlarmManager.INTERVAL_DAY, pendingIntent0);

        //alarmMgr0.set(AlarmManager.RTC_WAKEUP, task7AM.getTimeInMillis(), pendingIntent0);
        //alarmMgr0.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), pendingIntent0);

    }

    private void checkIfCurrentUserMatchDaysUpdated(JobParameters parameters){
        Log.d(TAG, "checkIfCurrentUserMatchDaysUpdated: ");

        // get user choosen regions
        reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions));

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                int int_user_regions_selected = (int) dataSnapshot.getChildrenCount();
                int counter = 0;
                ArrayList <String> userRegions = new ArrayList<>();

                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    counter++;
                    String userRegion = snapshot.getValue(String.class);
                    userRegions.add(userRegion);

                }
                if (counter==int_user_regions_selected){
                    // get all matches for that region/year

                    downloadMatchDays(userRegions);

                }



            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });



    }

    public void downloadMatchDays(ArrayList<String> userSelectedRegions){
        Log.d(TAG, "downloadMatchDays: user regions: "+userSelectedRegions.size());
        String firebase_section = getString(R.string.firebase_Matches);
        // get all matches for that region/year

        for(int i=0; i<userSelectedRegions.size(); i++){

            CurrentRegion currentRegion = new CurrentRegion();
            currentRegion.setRegion(userSelectedRegions.get(i));
            Log.d(TAG, "downloadMatchDays: under inspection: "+currentRegion.getRegion());

            ArrayList <String> cloudmatchDays = new ArrayList<>();

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(firebase_section)
                    .child(currentRegion.getRegion())
                    .child(currentRegion.getRegion() + year);

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                    cloudmatchDays.clear();
                    int matches_available = (int) dataSnapshot.getChildrenCount();
                    Log.d(TAG, "onDataChange: cloud matches number: "+matches_available);
                    int counter = 0;
                    String current_date = "";
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        counter++;
                        String match_id = (snapshot.getKey());
                        if (match_id!=null){
                            cloudmatchDays.add(match_id);
                        }
                    }

                    if (counter==matches_available){
                        Log.d(TAG, "onDataChange: "+counter +" == " +matches_available);
                        // get local matches for region/year
                        localmatchIDs = new ArrayList<>();
                        localmatchIDs = databaseHelper.getAllMatchIds(year, currentRegion.getRegion());
                        Log.d(TAG, "onDataChange: "+year +" "+ currentRegion.getRegion());
                        Log.d(TAG, "onDataChange: local matches number: " + localmatchIDs.size());
                        Log.d(TAG, "onDataChange: cloud matches number: "+cloudmatchDays.size());

                        //quando hai scaricato tutti i match togli da cloud_match tutti i local match

                        Integer difference_Cloud_Local = cloudmatchDays.size() - localmatchIDs.size();
                        Log.d(TAG, "onDataChange: cloud - local = " +difference_Cloud_Local);
                        if (difference_Cloud_Local!=0){
                            Log.d(TAG, "onDataChange: DISTRUGGO TUTTO");
                            //se ci sono differenze tra il db locale e il db cloud matches, cancella i record del db locale per quella regione e ripopolalo
                            /*for(int i=0; i<cloudmatchDays.size(); i++){
                                Log.d(TAG, "onDataChange: i:"+i);
                                String date = getLocalDateFromDateTime(cloudmatchDays.get(i));
                                databaseHelper.insertMatch( new Sqlite_Match(year, currentRegion.getRegion(), date, cloudmatchDays.get(i)));
                                if (!date.equals(current_date)){
                                    databaseHelper.insertMatchDay(new Sqlite_MatchDay(year, currentRegion.getRegion(), date));
                                    current_date=date;
                                }
                            }*/
                            databaseHelper.removeFromDatabase(currentRegion.getRegion());
                            // quindi ripopolalo con i nuovi records
                            Log.d(TAG, "onDataChange: RIPOPOLO TUTTO");
                            for(int i=0; i<cloudmatchDays.size(); i++){
                                Log.d(TAG, "onDataChange: "+i);

                                String date = getLocalDateFromDateTime(cloudmatchDays.get(i));
                                // pusha i match nell'SQL locale tabella Matches
                                databaseHelper.insertMatch( new Sqlite_Match(year, currentRegion.getRegion(), date, cloudmatchDays.get(i)));
                                //filtra tutti i match e ottieni solo i matchdays univoci

                                if (!date.equals(current_date)){
                                    databaseHelper.insertMatchDay(new Sqlite_MatchDay(year, currentRegion.getRegion(), date));
                                    current_date=date;
                                }

                            }

                        }


                    }

                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void checkIfLocalImageFolderIsUpdated(JobParameters parameters) {
        //Log.d(TAG, "checkIfLocalImageFolderIsUpdated: */**********************************************************" );

        // 1- prendi la lista delle immagini presenti in locale,
        // se 0 scarica tutto
        // 2- prendi la lista delle immagini nelle cartelle cloud
        // se differiscono riscarica le differenze
        FirebaseStorage storage = FirebaseStorage.getInstance();
        databaseHelper = new DatabaseHelper(getApplicationContext());

        //immagini regions sul cloud
        StorageReference regionReference = storage.getReference("region_img");
        ArrayList<ImageValidator> cloud_regions_images = new ArrayList<>();
        Task<ListResult> regions_images = regionReference.listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
            @Override
            public void onSuccess(ListResult listResult) {
                //listResult.getItems().size();
            }
        });


        //IMMAGINI TEAMS SUL CLOUD
        StorageReference teamsReference = storage.getReference("team_img");
        ArrayList<ImageValidator> cloud_teams_images = new ArrayList<>();
        Task<ListResult> teams_images = teamsReference.listAll();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {

                // immagini regions in cloud
                int region_image_size = regions_images.getResult().getItems().size();
                //Log.d(TAG, "checkIfLocalImageFolderIsUpdated: immagini regions in cloud */**********************************************************" +region_image_size);
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



                        ArrayList<String> local_regions_images = new ArrayList<>();
                        File folderRegions = new File(getFilesDir().getAbsolutePath()
                                + (getString(R.string.folder_regions_images)));
                        File[] files_regions = folderRegions.listFiles();
                        //Log.d(TAG, "checkIfLocalImageFolderIsUpdated: immagini region in locale*/**********************************************************" +files_regions.length);
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


                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            public void run() {

                        // se ci sono piu immagini regions sul cloud di quelle locali
                        ArrayList<String> moreRegions = new ArrayList<>();
                        //Integer cloud_regions_images_size =cloud_regions_images.size();
                        //Log.d(TAG, "run:******************************************************* is: "+region_image_size +" > "+local_regions_images.size() +" ?");
                        if (region_image_size>local_regions_images.size()){
                            //Log.d(TAG, "run:*******************************************************  TRUE");
                            for (int i=0; i<region_image_size; i++){
                                if (!local_regions_images.contains(cloud_regions_images.get(i).getName()) ){
                                    Log.d(TAG, "run: NOT FOUND: "+cloud_regions_images.get(i).getName());
                                    moreRegions.add(cloud_regions_images.get(i).getName());
                                }

                            }
                            for (int i=0; i<moreRegions.size(); i++){
                                // devo creare un file e scaricarci dentro l'immagine
                                StorageReference regionReference = storage.getReference("region_img");
                                File file = new File(folderRegions+"/"+moreRegions.get(i));
                                StorageReference gsReference = storage.getReferenceFromUrl(regionReference+"/"+moreRegions.get(i));
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
                                    Long local_region_image = databaseHelper.getMillisCreationRegionImage(region_name);
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
                                                    databaseHelper.updateImageRegion(new ImageValidator(region_name,storageMetadata.getCreationTimeMillis()));
                                                }
                                            });


                                        }
                                    }


                                }
                                // controllo sincronizzazione immagini localie  cloud per Teams
                                for (int i=0; i<cloud_teams_images.size(); i++){
                                    String team_name = cloud_teams_images.get(i).getName();
                                    Long local_teams_image = databaseHelper.getMillisCreationTeamImage(team_name);
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
                                                    databaseHelper.updateImageTeams(new ImageValidator(team_name,storageMetadata.getCreationTimeMillis()));
                                                }
                                            });
                                        }
                                    }

                                }


                            }
                        }, FIREBASE_STORAGE_RESPONSE_TIME);



                    }
                }, FIREBASE_STORAGE_RESPONSE_TIME);
                // immagini region in locale



            }
        }, FIREBASE_STORAGE_RESPONSE_TIME);


        jobFinished(parameters, false);


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

    // -------------------------------------------------------------------------------------------------------- NOTIFICATION -------------------------------------------------------------------------------------------------------- //


    public String getTodayDate(){

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

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTimeeee: datetime: "+datetime);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date value = null;
        try {
            value = formatter.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

        dateFormatter.setTimeZone(TimeZone.getDefault());

        String localDatetime = dateFormatter.format(value);
        Log.d(TAG, "getLocalDateFromDateTimeeeee: localDatetime: "+localDatetime);

        return localDatetime;
    }

    public long utcToLocal(long utcTime) {
        Log.d(TAG, "utcToLocal: utcTime enter: "+utcTime);
        try {

            Time timeFormat = new Time();
            timeFormat.set(utcTime + TimeZone.getDefault().getOffset(utcTime));
            Log.d(TAG, "utcToLocal: timeFormat.toMillis(true): "+timeFormat.toMillis(true));
            return timeFormat.toMillis(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "utcToLocal: return utcTime: "+utcTime);
        return utcTime;
    }


}
