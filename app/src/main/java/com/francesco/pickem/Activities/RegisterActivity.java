package com.francesco.pickem.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Models.User;
import com.francesco.pickem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {
    TextView go_to_login;
    private FirebaseAuth mAuth;
    EditText register_username, register_email, register_password, register_repeat_password;
    CheckBox checkbox_lec,checkbox_lck,checkbox_lpl,checkbox_lcs;
    Button button_register;
    ProgressBar register_progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        go_to_login = findViewById(R.id.go_to_login);
        register_username = findViewById(R.id.register_username);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_repeat_password = findViewById(R.id.register_repeat_password);

        checkbox_lec = findViewById(R.id.checkbox_lec);
        checkbox_lck = findViewById(R.id.checkbox_lck);
        checkbox_lpl = findViewById(R.id.checkbox_lpl);
        checkbox_lcs = findViewById(R.id.checkbox_lcs);
        register_progressbar = findViewById(R.id.register_progressbar);
        button_register = findViewById(R.id.button_register);
        mAuth = FirebaseAuth.getInstance();

        changeNavBarColor();
        goToLogin();
        registration();




    }

    private void registration() {
        button_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = register_username.getText().toString();
                String email = register_email.getText().toString();
                String password = register_password.getText().toString();
                String repeated_password = register_repeat_password.getText().toString();
                ArrayList <String> choosen_regions = new ArrayList<>();
                if (checkbox_lck.isChecked()){choosen_regions.add("LCK");}
                if (checkbox_lec.isChecked()){choosen_regions.add("LEC");}
                if (checkbox_lpl.isChecked()){choosen_regions.add("LPL");}
                if (checkbox_lcs.isChecked()){choosen_regions.add("LCS");}


                if (username.isEmpty()||email.isEmpty()||password.isEmpty()||repeated_password.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "Fill all the fields!", Toast.LENGTH_SHORT).show();
                    if  (username.isEmpty()){register_username.setError("Username Required"); register_username.requestFocus();}else
                    if  (email.isEmpty()){register_email.setError("Email Required"); register_email.requestFocus();}
                }else if (!password.equals(repeated_password)){
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                }else if (password.length()<6){
                    Toast.makeText(RegisterActivity.this, "Password too short! At least 6 characters", Toast.LENGTH_SHORT).show();
                }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                    register_email.requestFocus();
                }else if(!checkbox_lck.isChecked()&&!checkbox_lcs.isChecked()&&!checkbox_lec.isChecked()&&!checkbox_lpl.isChecked()){
                    Toast.makeText(RegisterActivity.this, "You must chose at least one Region to follow", Toast.LENGTH_SHORT).show();
                }else {
                    register_progressbar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                        if (task.isSuccessful()){
                            User user = new User( username, email, choosen_regions);
                            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "User has been registered Successfully", Toast.LENGTH_SHORT).show();
                                            register_progressbar.setVisibility(View.GONE);

                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            if (firebaseUser.isEmailVerified()){
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            }else {
                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(RegisterActivity.this, "Email verification sent, check your email!", Toast.LENGTH_LONG).show();
                                            }


                                        }else{
                                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                                            register_progressbar.setVisibility(View.GONE);

                                        }
                                    });
                        }else {
                            Log.d("TAG", "onClick: "+task.toString());
                            Toast.makeText(RegisterActivity.this, "Registration Error 2", Toast.LENGTH_SHORT).show();
                            register_progressbar.setVisibility(View.GONE);
                        }
                    });
                }


            }
        });


    }

    private void goToLogin() {
        go_to_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent( RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });
    }

    private void changeNavBarColor() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setNavigationBarColor(getResources().getColor(R.color.background_dark));
            getWindow().setStatusBarColor(getResources().getColor(R.color.background_dark));
        }
    }
}