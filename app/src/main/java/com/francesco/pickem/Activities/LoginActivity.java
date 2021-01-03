package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Models.RegionDetails;
import com.francesco.pickem.R;
import com.francesco.pickem.SQLite.AndroidDatabaseManager;
import com.francesco.pickem.SQLite.DatabaseHelper;
import com.francesco.pickem.Services.PreferencesData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;

import static android.os.Environment.DIRECTORY_DOWNLOADS;

public class LoginActivity extends AppCompatActivity {
    Button login_button;
    TextView go_to_registration;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private StorageReference storageReference;
    DatabaseHelper databaseHelper;
    private FirebaseAuth mAuth;
    private String TAG ="LoginActivity: ";

    // databse watcher
    TextView see_database;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        login_button = findViewById(R.id.button_login);
        go_to_registration = findViewById(R.id.go_to_registration);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        mAuth = FirebaseAuth.getInstance();
        databaseHelper = new DatabaseHelper(this);

        //database watcher:
        see_database = findViewById(R.id.see_database);
        see_database.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(LoginActivity.this, AndroidDatabaseManager.class);
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
                            if (task.isSuccessful() && firebaseUser.isEmailVerified()){
                                //check if sqlite has data, if not, download it from firebase
                               if (databaseHelper.checkifDataExisit()){
                                   Log.d(TAG, "onComplete: SQLITE HAS DATA");
                                   login_progressbar.setVisibility(View.GONE);
                                   PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                   Intent intent = new Intent( LoginActivity.this, PicksActivity.class);
                                   startActivity(intent);
                               }else{
                                   login_progressbar.setVisibility(View.GONE);
                                   Log.d(TAG, "onComplete: SQLITE HAS NO DATA");
                                   PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                   Intent intent = new Intent( LoginActivity.this, DownloadActivity.class);
                                   startActivity(intent);
                               }

                                
                            }else {
                                Toast.makeText(LoginActivity.this, "Login Error, Have you verified your email?", Toast.LENGTH_SHORT).show();
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