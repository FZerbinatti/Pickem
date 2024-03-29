package com.dreamsphere.pickem.Activities.AccountActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.dreamsphere.pickem.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    EditText edittext_enter_email;
    Button button_reset_password;
    FirebaseAuth auth;
    ProgressBar reset_pass__progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);


        edittext_enter_email = findViewById(R.id.edittext_enter_email);
        button_reset_password = findViewById(R.id.button_reset_password);
        auth = FirebaseAuth.getInstance();

        button_reset_password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = edittext_enter_email.getText().toString().trim();


                if (!email.isEmpty()){
                    reset_pass__progressbar.setVisibility(View.VISIBLE);
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(ResetPasswordActivity.this, "Check your email to reset password", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ResetPasswordActivity.this, LoginActivity.class);
                                startActivity(intent);
                            }else {
                                Toast.makeText(ResetPasswordActivity.this, "Error resetting password", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(ResetPasswordActivity.this, "Enter an email adress", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }
}