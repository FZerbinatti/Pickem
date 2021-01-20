package com.francesco.pickem.Activities.AccountActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Activities.MainActivities.PicksActivity;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.PreferencesData;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.StorageReference;

public class LoginActivity extends AppCompatActivity {
    Button login_button;
    TextView go_to_registration, forgot_password;
    EditText login_email, login_password;
    ProgressBar login_progressbar;
    private StorageReference storageReference;
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
        forgot_password = findViewById(R.id.forgot_password);

        login_email = findViewById(R.id.login_email);
        login_password = findViewById(R.id.login_password);
        login_progressbar = findViewById(R.id.login_progressbar);
        mAuth = FirebaseAuth.getInstance();

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

                                   login_progressbar.setVisibility(View.GONE);
                                   PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                   Intent intent = new Intent( LoginActivity.this, PicksActivity.class);
                                   PreferencesData.setUserLoggedInStatus(getApplicationContext(),true);
                                   startActivity(intent);


                                
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