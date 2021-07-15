package com.dreamsphere.pickem.Activities.AccountActivities;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.dreamsphere.pickem.Activities.MainActivities.PicksActivity;
import com.dreamsphere.pickem.Interfaces.OnGetDataListener;
import com.dreamsphere.pickem.Models.CurrentNumber;
import com.dreamsphere.pickem.Models.CurrentRegion;
import com.dreamsphere.pickem.Models.EloTrackerNotifications;
import com.dreamsphere.pickem.Models.ImageValidator;
import com.dreamsphere.pickem.Models.MatchDetails;
import com.dreamsphere.pickem.Models.RegionNotifications;
import com.dreamsphere.pickem.Models.SqliteMatch;
import com.dreamsphere.pickem.Models.Sqlite_MatchDay;
import com.dreamsphere.pickem.Models.TeamNotification;
import com.dreamsphere.pickem.Models.UserGeneralities;
import com.dreamsphere.pickem.R;
import com.dreamsphere.pickem.Services.PreferencesData;
import com.dreamsphere.pickem.Services.DatabaseHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class LoginActivity extends AppCompatActivity {
    Button login_button;
    TextView go_to_registration, forgot_password;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String TAG ="LoginActivity: ";
    ConstraintLayout personalized_dialog_background;
    TextView personalized_dialog_description, personalized_dialog_percentage1, personalized_dialog_percentage2, personalized_dialog_percentage3;
    ProgressBar personalized_dialog_progressbar1, personalized_dialog_progressbar2, personalized_dialog_progressbar3;
    ColorStateList colorStateListGreen, colorStateListYellow;
    String year;
    Calendar myCalendar;

    // databse watcher
    TextView see_database;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_login);
        go_to_registration = findViewById(R.id.go_to_registration);
        forgot_password = findViewById(R.id.forgot_password);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        FirebaseApp.initializeApp(LoginActivity.this);
        mAuth = FirebaseAuth.getInstance();
        colorStateListYellow = ContextCompat.getColorStateList(this, R.color.y1);
        colorStateListGreen = ContextCompat.getColorStateList(this, R.color.g2);

        personalized_dialog_background = findViewById(R.id.personalized_dialog_background);
        personalized_dialog_description = findViewById(R.id.personalized_dialog_description);
        personalized_dialog_percentage1 = findViewById(R.id.personalized_dialog_percentage1);
        personalized_dialog_percentage2 = findViewById(R.id.personalized_dialog_percentage2);
        personalized_dialog_progressbar1 = findViewById(R.id.personalized_dialog_progressbar1);
        personalized_dialog_progressbar2 = findViewById(R.id.personalized_dialog_progressbar2);

        databaseHelper = new DatabaseHelper(this);
        myCalendar = Calendar.getInstance();
        year = String.valueOf(myCalendar.get(Calendar.YEAR));


        forgot_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
                startActivity(intent);
            }
        });



        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = login_email.getText().toString();
                String password = login_password.getText().toString();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();


                if (email.isEmpty()){
                    login_email.setError("email address required to login");
                }else if(password.isEmpty()){
                    login_password.setError("password required to login");
                }else if (password.length() <6){
                    Toast.makeText(LoginActivity.this, "Password too short!", Toast.LENGTH_SHORT).show();
                }else {
                    login_progressbar.setVisibility(View.VISIBLE);
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful() && (firebaseUser != null && (firebaseUser).isEmailVerified())){

                                downloadUserRegions();
                                downloadUSerNotificationPreferences();
                                downloadUserElotrackerInfo();

                                Log.d(TAG, "onComplete: QUI");

                                File rootfolder = new File(getFilesDir()
                                        + "/images");
                                if (!rootfolder.exists()) {

                                    personalized_dialog_background.setVisibility(View.VISIBLE);
                                    personalized_dialog_progressbar1.setProgressTintList(colorStateListYellow);
                                    personalized_dialog_progressbar2.setProgressTintList(colorStateListGreen);
                                    databaseHelper.setAllNotificationFields();


                                    login_button.setEnabled(false);
                                    login_email.setEnabled(false);
                                    login_password.setEnabled(false);
                                    go_to_registration.setEnabled(false);
                                    forgot_password.setEnabled(false);


                                    Log.d(TAG, "onComplete: root non esiste");

                                    //show toast / alertDialog

                                    rootfolder.mkdir();
                                    File folderRegions = new File(getFilesDir()
                                            + "/images/regions");
                                    File folderTeams = new File(getFilesDir()
                                            + "/images/teams");
                                    folderRegions.mkdir();
                                    folderTeams.mkdir();

                                    FirebaseStorage storage = FirebaseStorage.getInstance();
                                    StorageReference regionReference = storage.getReference("region_img");
                                    StorageReference teamsReference = storage.getReference("team_img");
                                    Task<ListResult> regions_images = regionReference.listAll();
                                    Task<ListResult> teams_images = teamsReference.listAll();

                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {
                                        public void run() {

                                            Integer region_image_size = regions_images.getResult().getItems().size();
                                            float costante = 100f/region_image_size;

                                            CurrentNumber currentNumber =new CurrentNumber();
                                            currentNumber.setNumber(1);
                                            personalized_dialog_progressbar1.setMax(region_image_size);
                                            for (int i = 0; i < region_image_size; i++)
                                            {

                                                String file_cloud_path = regions_images.getResult().getItems().get(i).toString();
                                                String[] datetime =file_cloud_path.split("region_img/");
                                                String file_name =datetime[1];
                                                File file = new File(folderRegions+"/"+file_name);
                                                StorageReference gsReference = storage.getReferenceFromUrl(regions_images.getResult().getItems().get(i).toString());
                                                 gsReference.getFile(file).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "onFailure: ", e );
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        personalized_dialog_description.setText("downloading faker");
                                                        personalized_dialog_progressbar1.setProgress(currentNumber.getNumber(),true);
                                                        personalized_dialog_percentage1.setText(Math.round(currentNumber.getNumber()*costante)+"%");
                                                        currentNumber.setNumber(currentNumber.getNumber()+1);
                                                    }
                                                });

                                                gsReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                                    @Override
                                                    public void onSuccess(StorageMetadata storageMetadata) {
                                                        Log.d(TAG, "onSuccess: inserisco nel db questi dati: "+ file_name + " creationTimeMillis: "+ storageMetadata.getCreationTimeMillis());
                                                        databaseHelper.insertImageRegion(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                                                    }
                                                });

                                            }

                                            Integer teams_image_size = teams_images.getResult().getItems().size();
                                            float costante2 = 100f/teams_image_size;
                                            CurrentNumber currentNumber2 =new CurrentNumber();
                                            currentNumber2.setNumber(1);
                                            personalized_dialog_progressbar2.setMax(teams_image_size);
                                            Log.d(TAG, "run: teams_image_size:"+teams_image_size);
                                            for (int i = 0; i < teams_image_size; i++)
                                            {

                                                String file_cloud_path = teams_images.getResult().getItems().get(i).toString();
                                                String[] datetime =file_cloud_path.split("team_img/");
                                                String file_name =datetime[1];
                                                Log.d(TAG, "run: file_name:"+file_name);
                                                File file = new File(folderTeams+"/"+file_name);
                                                Log.d(TAG, "localImageDirectoryUpdated: cloud:"+ file_cloud_path);
                                                StorageReference gsReference = storage.getReferenceFromUrl(teams_images.getResult().getItems().get(i).toString());
                                                //StorageReference gsReference = storage.getReferenceFromUrl(regions_images.getResult().getItems().get(i).toString().replace("S%20", " "));
                                                gsReference.getFile(file).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Log.e(TAG, "onFailure: ", e );
                                                    }
                                                }).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                                    @RequiresApi(api = Build.VERSION_CODES.N)
                                                    @Override
                                                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                                        personalized_dialog_description.setText("Waiting for Morgana's Q to end ");
                                                        personalized_dialog_progressbar2.setProgress(currentNumber2.getNumber(),true);
                                                        Integer bar_percentage = Math.round(currentNumber2.getNumber()*costante2);
                                                        personalized_dialog_percentage2.setText(bar_percentage+"%");
                                                        currentNumber2.setNumber(currentNumber2.getNumber()+1);
                                                        Log.d(TAG, "onSuccess: current number: " +currentNumber2.getNumber() +" / " +teams_image_size);

                                                        if (bar_percentage == 100){
                                                            Log.d(TAG, "onSuccess: access granted");
                                                            login_progressbar.setVisibility(View.GONE);
                                                            PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                                            Intent intent = new Intent( LoginActivity.this, PicksActivity.class);
                                                            PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                                            startActivity(intent);

                                                        }
                                                    }
                                                });
                                                gsReference.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                                    @Override
                                                    public void onSuccess(StorageMetadata storageMetadata) {
                                                        /*String[] datetime =file_name.split(".");
                                                        String name =datetime[0];*/
                                                        Log.d(TAG, "onSuccess: inserisco nel db questi dati: "+ file_name + " creationTimeMillis: "+ storageMetadata.getCreationTimeMillis());

                                                        databaseHelper.insertImageTeam(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                                                    }
                                                });

                                            }

                                            downloadUserPicksForStatsActivity();

                                        }
                                    }, 3000);

                                }else {
                                    Log.d(TAG, "onComplete: il root esiste!");
                                    login_progressbar.setVisibility(View.GONE);
                                    PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                    Intent intent = new Intent( LoginActivity.this, PicksActivity.class);
                                    PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                    startActivity(intent);

                                }

                            }else {
                                Toast.makeText(LoginActivity.this, "Login Error, Have you verified your email? If so, Try again", Toast.LENGTH_SHORT).show();
                                login_progressbar.setVisibility(View.GONE);
                            }
                        }
                    });
                }

            }
        });

        go_to_registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( LoginActivity.this, RegisterActivity.class);
                startActivity(intent);

            }
        });

        changeNavBarColor();
    }

    private void downloadUserElotrackerInfo() {

        DatabaseReference referenceGeneralities = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities));

        referenceGeneralities.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserGeneralities elotracker_info = snapshot.getValue(UserGeneralities.class);

                databaseHelper.update_ELOTRACKER(new EloTrackerNotifications(elotracker_info.getSummoner_elotracker(), elotracker_info.getSummoner_name(), elotracker_info.getSummoner_server(), elotracker_info.getTime_elotracker()));

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    private void downloadUSerNotificationPreferences() {


        DatabaseReference referenceTeam = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_user_notification_teams));

        referenceTeam.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    String userTeam = snapshot.getKey();
                    TeamNotification teamNotifications = snapshot.getValue(TeamNotification.class);
                    if (teamNotifications.getNotification_team_as_team_plays()>0){
                        databaseHelper.insert_TEAMS_AS_TEAM_PLAYS(userTeam);
                    }
                    if (teamNotifications.getNotification_team_morning_reminder()>0){
                        databaseHelper.insert_TEAMS_MORNING_REMINDER(userTeam);
                    }
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

        DatabaseReference referenceRegion = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_user_notification))
                .child(getString(R.string.firebase_user_notification_region));

        referenceRegion.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userRegion = snapshot.getKey();

                    RegionNotifications regionNotifications = snapshot.getValue(RegionNotifications.class);
                    if (regionNotifications.getNo_choice_made()>0){
                        databaseHelper.insert_REGIONS_NO_CHOICE_MADE(userRegion);
                    }
                    if ((regionNotifications.getNotification_first_match_otd()>0)){
                        databaseHelper.insert_REGIONS_FIRST_MATCH_ODT(userRegion);
                    }
                    if ((regionNotifications.getNotification_morning_reminder()>0)){
                        databaseHelper.insert_REGIONS_MORNING_REMINDER(userRegion);
                    }


                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });

    }

    private void downloadUserPicksForStatsActivity() {
        CurrentNumber global_counter = new CurrentNumber();
        global_counter.setNumber(0);
        ArrayList <String> userRegionPicks = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_picks));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                //prendi tutte le regioni di interesse dello user
                int number_of_userPicksRegions = (int) dataSnapshot.getChildrenCount();

                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userRegionPick = snapshot.getKey();
                    //francString userRegionPick = snapshot.getValue(String.class);
                    global_counter.setNumber(global_counter.getNumber()+1);
                    userRegionPicks.add(userRegionPick);
                    if (global_counter.getNumber()==number_of_userPicksRegions){
                        downloadMatchDetailsForUserPicks(userRegionPicks);
                    }
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    public void downloadMatchDetailsForUserPicks(ArrayList<String> userRegionPicks) {

        CurrentNumber sum_all_matches_all_regions = new CurrentNumber();
        CurrentNumber global_counter = new CurrentNumber();
        sum_all_matches_all_regions.setNumber(0);
        global_counter.setNumber(0);

        for(int i=0; i<userRegionPicks.size(); i++){
            CurrentRegion currentRegion = new CurrentRegion();
            currentRegion.setRegion(userRegionPicks.get(i));
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .child(getString(R.string.firebase_users_picks))
                    .child(userRegionPicks.get(i))
                    .child(userRegionPicks.get(i)+year);

            readData(reference, new OnGetDataListener() {
                @Override
                public void onSuccess(DataSnapshot dataSnapshot) {

                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                        String match_ID = snapshot.getKey();
                        String userMatchPick = snapshot.getValue(String.class);

                        databaseHelper.updatePrediction(currentRegion.getRegion(), match_ID, userMatchPick);
                        //getWinnerOfThisMatch(currentRegion.getRegion(), match_ID);
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

/*    private void getWinnerOfThisMatch(String region, String match_id) {
        //if datetime elapsed, get winner of this match
        if (matchAlreadyElapsedQuestionMark(match_id)){
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_Matches))
                    .child(region)
                    .child(region+year)
                    .child(match_id);

            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {

                    MatchDetails matchDetails = dataSnapshot.getValue(MatchDetails.class);
                    databaseHelper.updateWinner(region, match_id, matchDetails.getWinner());

                }
                @Override
                public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
                }
            });
        }



    }*/

    public boolean matchAlreadyElapsedQuestionMark(String datetime) {

        //Log.d(TAG, "matchAlreadyElapsedQuestionMark: (matchDays.getDatetime(): "+(matchDays.getDatetime()));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        Date strDate = null;
        try {
            strDate = sdf.parse(datetime);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        // Log.d(TAG, "selectMatchDay: System.currentTimeMillis(): "+System.currentTimeMillis());;

        long matchTimeMillis = strDate.getTime();

        // Log.d(TAG, "selectMatchDay: matchTimeMillis:"+matchTimeMillis);
        if (System.currentTimeMillis() < matchTimeMillis) {
            return false;
        }else {
            return true;}


    }


    private void downloadUserRegions() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(getString(R.string.firebase_users_generealities))
                .child(getString(R.string.firebase_user_choosen_regions));

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                //prendi tutte le regioni di interesse dello user
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    String userRegion = snapshot.getValue(String.class);
                    //per ognuna delle regioni trovate, ti scarichi i matchDays di quella regione
                    downloadMatchDays(userRegion);
                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {
            }
        });
    }

    public void downloadMatchDays(String selected_region){
        String firebase_section = getString(R.string.firebase_Matches);
        CurrentRegion currentRegion = new CurrentRegion();
        currentRegion.setRegion(selected_region);



        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(firebase_section)
                .child(selected_region)
                .child(selected_region + year);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                String current_date = "";
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // prendi tutti i giorni disponibili per quell'anno per quella regione
                    String match_id = snapshot.getKey();
                    MatchDetails matchDetails = snapshot.getValue(MatchDetails.class);
                    String date =getLocalDateFromDateTime(match_id);
                    // pusha i match nell'SQL locale tabella Matches
                    databaseHelper.insertMatch( new SqliteMatch(year, currentRegion.getRegion(), date, match_id, matchDetails.getTeam1(), matchDetails.getTeam2(), matchDetails.getWinner()) );
                    //filtra tutti i match e ottieni solo i matchdays univoci

                    if (!date.equals(current_date)){
                        databaseHelper.insertMatchDay(new Sqlite_MatchDay(year, currentRegion.getRegion(), date));
                        current_date=date;
                    }

                }
            }
            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });

    }

    private String getLocalDateFromDateTime(String datetime) {
        Log.d(TAG, "getLocalDateFromDateTime: datetime: "+datetime);
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
        Log.d(TAG, "getLocalDateFromDateTime: localDatetime: "+localDatetime);

        return localDatetime;
    }

    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
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