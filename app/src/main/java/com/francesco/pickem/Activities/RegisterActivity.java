package com.francesco.pickem.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    TextView go_to_login;
    private FirebaseAuth mAuth;
    EditText register_username, register_email, register_password, register_repeat_password;
    CheckBox checkbox_lec,checkbox_lck,checkbox_lpl,checkbox_lcs,checkbox_tcl,checkbox_cblol;
    CheckBox checkbox_opl,checkbox_ljl,checkbox_pcs,checkbox_eum,checkbox_lcsa,checkbox_lla;
    private  static String TAG ="RegisterActivity: ";

    Button button_register;
    ProgressBar register_progressbar;
    ImageButton registration_show_regions;
    ConstraintLayout collapsable_box_registration;
    Integer dropdown_status;
    private DatabaseReference reference;
    private DatabaseReference user_preferences_reference;
    private FirebaseUser user;
    private FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        go_to_login = findViewById(R.id.go_to_login);
        register_username = findViewById(R.id.register_username);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_repeat_password = findViewById(R.id.register_repeat_password);
        registration_show_regions = findViewById(R.id.registration_show_regions);
        collapsable_box_registration = findViewById(R.id.collapsable_box_registration);


        checkbox_lec = findViewById(R.id.checkbox_lec);
        checkbox_lck = findViewById(R.id.checkbox_lck);
        checkbox_lpl = findViewById(R.id.checkbox_lpl);
        checkbox_lcs = findViewById(R.id.checkbox_cblol);

        checkbox_tcl = findViewById(R.id.checkbox_tcl);
        checkbox_cblol = findViewById(R.id.checkbox_cblol);
        checkbox_opl = findViewById(R.id.checkbox_opl);
        checkbox_ljl = findViewById(R.id.checkbox_ljl);

        checkbox_pcs = findViewById(R.id.checkbox_pcs);
        checkbox_eum = findViewById(R.id.checkbox_eum);
        checkbox_lcsa = findViewById(R.id.checkbox_lcsa);
        checkbox_lla = findViewById(R.id.checkbox_lla);


        register_progressbar = findViewById(R.id.register_progressbar);
        button_register = findViewById(R.id.button_register);
        mAuth = FirebaseAuth.getInstance();
        dropdown_status=0;


        changeNavBarColor();
        goToLogin();
        registration();
        dropdownMenu();

    }

    private void dropdownMenu() {

        registration_show_regions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dropdown_status == 0){
                    collapsable_box_registration.setVisibility(View.VISIBLE);
                    registration_show_regions.setImageResource(R.drawable.ic_dropup);
                    dropdown_status =1;
                    hideKeyboard(RegisterActivity.this);
                }else {
                    collapsable_box_registration.setVisibility(View.GONE);
                    registration_show_regions.setImageResource(R.drawable.ic_dropdown);
                    dropdown_status =0;
                }

            }
        });

    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

                if (checkbox_lck.isChecked()){choosen_regions.add(getString(R.string.lck));}
                if (checkbox_lec.isChecked()){choosen_regions.add(getString(R.string.lec));}
                if (checkbox_lpl.isChecked()){choosen_regions.add(getString(R.string.lpl));}
                if (checkbox_lcs.isChecked()){choosen_regions.add(getString(R.string.lcs));}

                if (checkbox_tcl.isChecked()){choosen_regions.add(getString(R.string.tcl));}
                if (checkbox_cblol.isChecked()){choosen_regions.add(getString(R.string.cblol));}
                if (checkbox_opl.isChecked()){choosen_regions.add(getString(R.string.opl));}
                if (checkbox_ljl.isChecked()){choosen_regions.add(getString(R.string.ljl));}

                if (checkbox_pcs.isChecked()){choosen_regions.add(getString(R.string.pcs));}
                if (checkbox_eum.isChecked()){choosen_regions.add(getString(R.string.eu_masters));}
                if (checkbox_lcsa.isChecked()){choosen_regions.add(getString(R.string.lcs_academy));}
                if (checkbox_lla.isChecked()){choosen_regions.add(getString(R.string.lla));}

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
                }else if(choosen_regions.isEmpty()){
                    Toast.makeText(RegisterActivity.this, "You must chose at least one Region to follow", Toast.LENGTH_SHORT).show();
                }else {
                     /*UID = FirebaseAuth.getInstance().getCurrentUser())).getUid();
                    Log.d(TAG, "onClick: UID:"+UID);*/

                    register_progressbar.setVisibility(View.VISIBLE);
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                        if (task.isSuccessful()){
                            UserGeneralities user = new UserGeneralities( email, username, choosen_regions);
                            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .child(getString(R.string.firebase_users_generealities))
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()){
                                            Toast.makeText(RegisterActivity.this, "User has been registered Successfully", Toast.LENGTH_SHORT).show();

                                            for (int i=0; i<choosen_regions.size(); i++){


                                                RegionNotifications regionNotifications = new RegionNotifications(choosen_regions.get(i),0,0,1);
                                                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                        .child(getString(R.string.firebase_user_notification))
                                                        .child(getString(R.string.firebase_user_notification_region))
                                                        .child(choosen_regions.get(i))
                                                        .setValue(regionNotifications).addOnCompleteListener(task2 -> {
                                                    if (task2.isSuccessful()){
                                                        Log.d(TAG, "registered: Notification Region ");

                                                    }else{
                                                        Log.d(TAG, "ERROR: registered: Notification Region ");
                                                    }
                                                });

                                            } 

                                            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                            Log.d(TAG, "onClick: firebaseUser: "+firebaseUser);
                                            Log.d(TAG, "onClick: firebaseUser = "+firebaseUser.getUid());
                                            if (!firebaseUser.isEmailVerified()){




                                                firebaseUser.sendEmailVerification();
                                                Toast.makeText(RegisterActivity.this, "Email verification sent, check your email!", Toast.LENGTH_LONG).show();
                                                register_progressbar.setVisibility(View.GONE);
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();

                                            }

                                        }else{
                                            Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "onClick: TASK 1 INSUCCESS");
                                            register_progressbar.setVisibility(View.GONE);

                                        }
                                    });
                        }else {
                            Log.d(TAG, "onClick: "+task.toString());
                            Toast.makeText(RegisterActivity.this, "Registration Error, Contact HelpDesk", Toast.LENGTH_SHORT).show();
                            register_progressbar.setVisibility(View.GONE);
                        }
                    });

                    //crea anche la sezione delle notifiche  di default per le region scelte
                    // -------------------------------------
                    // da loopare per ogni regione scelta






/*                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .child(getString(R.string.firebase_user_notification))
                            .child(getString(R.string.firebase_user_notification_teams))
                            .setValue(regionNotifications).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()){
                            Log.d(TAG, "registered: Notification Region ");

                        }else{
                            Log.d(TAG, "ERROR: registered: Notification Region ");
                        }
                    });*/



                    // -------------------------------------

                    // questo crea sotto Users un UsersPreferences, un ID, e sotto 3 caselle di cui una lista
/*                    reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users));
                    user_preferences_reference = reference.child(getString(R.string.firebase_preferences));
                    user_preferences_reference.push().setValue(new UserGeneralities("","" , choosen_regions));*/

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