package com.francesco.pickem.Activities.AccountActivities;

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

import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.Models.CurrentNumber;
import com.francesco.pickem.Models.ImageValidator;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
import com.francesco.pickem.Services.SQLite;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    Button login_button;
    TextView go_to_registration, forgot_password;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    private String TAG ="LoginActivity: ";
    ConstraintLayout personalized_dialog_background;
    TextView personalized_dialog_description, personalized_dialog_percentage1, personalized_dialog_percentage2;
    ProgressBar personalized_dialog_progressbar1, personalized_dialog_progressbar2;
    ColorStateList colorStateListGreen, colorStateListYellow;

    // databse watcher
    TextView see_database;
    SQLite sqLite;



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
        mAuth = FirebaseAuth.getInstance();
        colorStateListYellow = ContextCompat.getColorStateList(this, R.color.y1);
        colorStateListGreen = ContextCompat.getColorStateList(this, R.color.g2);

        personalized_dialog_background = findViewById(R.id.personalized_dialog_background);
        personalized_dialog_description = findViewById(R.id.personalized_dialog_description);
        personalized_dialog_percentage1 = findViewById(R.id.personalized_dialog_percentage1);
        personalized_dialog_percentage2 = findViewById(R.id.personalized_dialog_percentage2);
        personalized_dialog_progressbar1 = findViewById(R.id.personalized_dialog_progressbar1);
        personalized_dialog_progressbar2 = findViewById(R.id.personalized_dialog_progressbar2);

        sqLite = new SQLite(this);


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

                                Log.d(TAG, "onComplete: QUI");

                                File rootfolder = new File(getFilesDir()
                                        + "/images");
                                if (!rootfolder.exists()) {

                                    personalized_dialog_background.setVisibility(View.VISIBLE);
                                    personalized_dialog_progressbar1.setProgressTintList(colorStateListYellow);
                                    personalized_dialog_progressbar2.setProgressTintList(colorStateListGreen);

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
                                                Log.d(TAG, "run: file_name:"+file_name);
                                                File file = new File(folderRegions+"/"+file_name);
                                                Log.d(TAG, "localImageDirectoryUpdated: cloud:"+ file_cloud_path);
                                                StorageReference gsReference = storage.getReferenceFromUrl(regions_images.getResult().getItems().get(i).toString());
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
                                                        sqLite.insertImageRegion(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
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
                                                        personalized_dialog_description.setText("waiting for morgana's Q to end ");
                                                        personalized_dialog_progressbar2.setProgress(currentNumber2.getNumber(),true);
                                                        personalized_dialog_percentage2.setText(Math.round(currentNumber2.getNumber()*costante2)+"%");
                                                        currentNumber2.setNumber(currentNumber2.getNumber()+1);

                                                        if (currentNumber2.getNumber() == teams_image_size){


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

                                                        sqLite.insertImageTeam(new ImageValidator(file_name, storageMetadata.getCreationTimeMillis()));
                                                    }
                                                });

                                            }



                                        }
                                    }, 2000);

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




    public void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }
}