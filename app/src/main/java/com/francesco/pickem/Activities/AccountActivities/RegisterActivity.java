package com.francesco.pickem.Activities.AccountActivities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Adapters.SimpleRegionRecyclerViewAdapter;
import com.francesco.pickem.Models.RegionNotifications;
import com.francesco.pickem.Models.SimpleRegion;
import com.francesco.pickem.Models.UserGeneralities;
import com.francesco.pickem.R;
import com.francesco.pickem.Services.RecyclerItemClickListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    TextView go_to_login;
    private FirebaseAuth mAuth;
    EditText  register_email, register_password, register_repeat_password;
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
    ScrollView register_scrollview;
    SimpleRegionRecyclerViewAdapter adapter;
    RecyclerView register_recyclerview_regioni;
    ArrayList<String> allRegionsFromBD;
    ArrayList<String> finalRegions;

    TextView textView_selectRegion;
    Context context;
    ArrayList<String> choosen_regions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        go_to_login = findViewById(R.id.go_to_login);
        register_email = findViewById(R.id.register_email);
        register_password = findViewById(R.id.register_password);
        register_repeat_password = findViewById(R.id.register_repeat_password);
        registration_show_regions = findViewById(R.id.registration_show_regions);
        collapsable_box_registration = findViewById(R.id.collapsable_box_registration);
        textView_selectRegion = findViewById(R.id.textView_selectRegion);
        context = this;
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        register_scrollview = findViewById(R.id.register_scrollview);
        register_recyclerview_regioni = findViewById(R.id.register_recyclerview_regioni);

        register_progressbar = findViewById(R.id.register_progressbar);
        button_register = findViewById(R.id.button_register);

        mAuth = FirebaseAuth.getInstance();
        dropdown_status=0;
        allRegionsFromBD= new ArrayList<>();

        register_scrollview.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                findViewById(R.id.register_recyclerview_regioni).getParent().requestDisallowInterceptTouchEvent(false);
                return false;
            }
        });
        register_recyclerview_regioni.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event)
            {

                // Disallow the touch request for parent scroll on touch of child view
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        });


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
                    register_progressbar.setVisibility(View.VISIBLE);
                    textView_selectRegion.setText("Long Click to select");
                    collapsable_box_registration.setVisibility(View.VISIBLE);
                    registration_show_regions.setImageResource(R.drawable.ic_dropup);
                    dropdown_status =1;
                    hideKeyboard(RegisterActivity.this);
                    loadDatabaseRegions();
                }else {
                    register_progressbar.setVisibility(View.GONE);
                    textView_selectRegion.setText("Select Region");
                    collapsable_box_registration.setVisibility(View.GONE);
                    registration_show_regions.setImageResource(R.drawable.ic_dropdown);
                    dropdown_status =0;
                }

            }
        });

    }

    private void loadDatabaseRegions() {
        Log.d(TAG, "loadRegionsWithCheckBox: click");


        ArrayList<SimpleRegion> all_databaseRegions = new ArrayList<>();

        // load da firebase le regioni
        DatabaseReference datareference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_regions));
        datareference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                    // tutti i team di quella regione, prendi solo il nome
                    String regionName = snapshot.getKey().toString();
                    Log.d(TAG, "onDataChange: regionName: "+regionName);
                    allRegionsFromBD.add(regionName);
                    all_databaseRegions.add(new SimpleRegion(regionName, false));
                }

                Log.d(TAG, "onDataChange: "+all_databaseRegions.size());

                loadListviewRegions(all_databaseRegions);

            }

            @Override
            public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

            }
        });


    }

    private void loadListviewRegions(ArrayList<SimpleRegion> all_regions) {

        Log.d(TAG, "loadListviewChooseRegions: "+ all_regions.size());
        register_progressbar.setVisibility(View.GONE);

        finalRegions = new ArrayList<>();

        // set up the RecyclerView

        register_recyclerview_regioni.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SimpleRegionRecyclerViewAdapter(getApplicationContext(), all_regions);
        register_recyclerview_regioni.setAdapter(adapter);


        register_recyclerview_regioni.addOnItemTouchListener(
                new RecyclerItemClickListener(context, register_recyclerview_regioni , new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                    }

                    @Override public void onLongItemClick(View view, int position) {
                        Log.d(TAG, "onLongItemClick: ");
                        // do whatever
                        if (all_regions.get(position).getChecked()){
                            all_regions.get(position).setChecked(false);
                            adapter.notifyDataSetChanged();
                            //Log.d(TAG, "onLongItemClick: false");
                            if (finalRegions.contains(all_regions.get(position).getName())){
                                finalRegions.remove(all_regions.get(position).getName());
                            }
                        }else {
                            all_regions.get(position).setChecked(true);
                            //Log.d(TAG, "onLongItemClick: true");
                            adapter.notifyDataSetChanged();
                            finalRegions.add(all_regions.get(position).getName());
                        }

                        Log.d(TAG, "onLongItemClick: "+all_regions.get(position).getName()+":"+all_regions.get(position).getChecked());


                    }
                })
        );







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
                String email = register_email.getText().toString();
                String password = register_password.getText().toString();
                String repeated_password = register_repeat_password.getText().toString();
                choosen_regions = new ArrayList<>();

                choosen_regions = finalRegions;



                if (choosen_regions.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "You must chose at least one Region to follow", Toast.LENGTH_SHORT).show();
                }else {


                    if (email.isEmpty() || password.isEmpty() || repeated_password.isEmpty()) {
                        Toast.makeText(RegisterActivity.this, "Fill all the fields!", Toast.LENGTH_SHORT).show();
                        if (email.isEmpty()) {
                            register_email.setError("Email Required");
                            register_email.requestFocus();
                        }
                    } else if (!password.equals(repeated_password)) {
                        Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    } else if (password.length() < 6) {
                        Toast.makeText(RegisterActivity.this, "Password too short! At least 6 characters", Toast.LENGTH_SHORT).show();
                    } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        Toast.makeText(RegisterActivity.this, "Please enter a valid email", Toast.LENGTH_SHORT).show();
                        register_email.requestFocus();
                    } else {
                         /*UID = FirebaseAuth.getInstance().getCurrentUser())).getUid();
                        Log.d(TAG, "onClick: UID:"+UID);*/

                        register_progressbar.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {

                            if (task.isSuccessful()) {
                                UserGeneralities user = new UserGeneralities(email, "null", "null", choosen_regions, FirebaseAuth.getInstance().getCurrentUser().getUid());
                                FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                        .child(getString(R.string.firebase_users_generealities))
                                        .setValue(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "User has been registered Successfully", Toast.LENGTH_SHORT).show();

                                        for (int i = 0; i < choosen_regions.size(); i++) {


                                            RegionNotifications regionNotifications = new RegionNotifications(1, 0, 0, choosen_regions.get(i));
                                            FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                    .child(getString(R.string.firebase_user_notification))
                                                    .child(getString(R.string.firebase_user_notification_region))
                                                    .child(choosen_regions.get(i))
                                                    .setValue(regionNotifications).addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    Log.d(TAG, "registered: Notification Region ");

                                                } else {
                                                    Log.d(TAG, "ERROR: registered: Notification Region ");
                                                }
                                            });

                                        }

                                        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                                        Log.d(TAG, "onClick: firebaseUser: " + firebaseUser);
                                        Log.d(TAG, "onClick: firebaseUser = " + firebaseUser.getUid());
                                        if (!firebaseUser.isEmailVerified()) {


                                            firebaseUser.sendEmailVerification();
                                            Toast.makeText(RegisterActivity.this, "Email verification sent, check your email!", Toast.LENGTH_LONG).show();
                                            register_progressbar.setVisibility(View.GONE);
                                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                            startActivity(intent);
                                            finish();

                                        }

                                    } else {
                                        Toast.makeText(RegisterActivity.this, "Registration Error", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onClick: TASK 1 INSUCCESS");
                                        register_progressbar.setVisibility(View.GONE);

                                    }
                                });
                            } else {
                                Log.d(TAG, "onClick: " + task.toString());
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