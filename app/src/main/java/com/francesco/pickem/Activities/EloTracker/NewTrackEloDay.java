package com.francesco.pickem.Activities.EloTracker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.francesco.pickem.Models.EloTracker;
import com.francesco.pickem.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class NewTrackEloDay extends AppCompatActivity {

    String elotracker_id;
    String elotracker_date;
    String elotracker_wins;
    String elotracker_losses;
    String elotracker_elo;
    String elotracker_lps;
    private String TAG ="NewTrackEloActivity: ";
    private int mYear, mMonth, mDay, mHour, mMinute;
    Context context;
    EloActivity eloService;
    Calendar calendar;
    String year;
    TextView add_eloday_date;
    EditText add_eloday_wins, add_eloday_losses, add_eloday_lps;
    Spinner add_eloday_spinner;
    Button button_save_today_elotracking;
    Boolean newDay;
    ArrayList<String> allEloTrackedDays;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_track_elo_day);
        add_eloday_date = findViewById(R.id.add_eloday_date);
        add_eloday_wins = findViewById(R.id.add_eloday_wins);
        add_eloday_losses = findViewById(R.id.add_eloday_losses);
        add_eloday_lps = findViewById(R.id.add_eloday_lps);
        button_save_today_elotracking = findViewById(R.id.button_save_today_elotracking);
        add_eloday_spinner = findViewById(R.id.add_eloday_spinner);
        context = this;
        eloService = new EloActivity();
        calendar = Calendar.getInstance();
        year = String.valueOf(calendar.get(Calendar.YEAR));
        fillSpinner();
        newDay = false;




        /*ArrayList <Elo> listOfElos = new ArrayList<>();
        listOfElos.add()
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.spinner_item,list);*/

        setDate();


        Intent intent = getIntent();
        if (intent.hasExtra( getResources().getString(R.string.elotracker_id) )){
            button_save_today_elotracking.setText("Update Data");
            newDay = false;

            elotracker_id = intent.getStringExtra       (getResources().getString(R.string.elotracker_id) );
            elotracker_date = intent.getStringExtra     (getResources().getString(R.string.elotracker_date) );
            elotracker_wins = intent.getStringExtra     (getResources().getString(R.string.elotracker_wins) );
            elotracker_losses = intent.getStringExtra   (getResources().getString(R.string.elotracker_losses) );
            elotracker_elo = intent.getStringExtra      (getResources().getString(R.string.elotracker_elo) );
            elotracker_lps = intent.getStringExtra      (getResources().getString(R.string.elotracker_lps) );

            Log.d(TAG, "onCreate: elotracker_wins:"+elotracker_wins);
            Log.d(TAG, "onCreate: elotracker_losses:"+elotracker_losses);



            add_eloday_date.setText(elotracker_date);
            add_eloday_wins.setText(elotracker_wins.toString());
            add_eloday_losses.setText(elotracker_losses.toString());
            add_eloday_lps.setText(elotracker_lps.toString());

            ArrayList<String> allElos = new ArrayList<>();
            allElos = eloService.getAllElos(context);

            for (int i=0; i<allElos.size(); i++){
                if (allElos.get(i).equals(elotracker_elo)){
                    add_eloday_spinner.setSelection(i);
                }
            }





        }else if(intent.hasExtra( "EX_ELO" )){

            newDay=true;
            String ex_elo = intent.getStringExtra("EX_ELO");
            Log.d(TAG, "onCreate: ex_elo: "+ex_elo);
            ArrayList<String> allElos = new ArrayList<>();
            allElos = eloService.getAllElos(context);

            for (int i=0; i<allElos.size(); i++){
                if (allElos.get(i).equals(ex_elo)){
                    add_eloday_spinner.setSelection(i);
                }
            }

        }

        button_save_today_elotracking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                allEloTrackedDays = new ArrayList<>();


                String date = add_eloday_date.getText().toString();
                elotracker_id = date;
                String wins = add_eloday_wins.getText().toString();
                String losses = add_eloday_losses.getText().toString();
                String lps = add_eloday_lps.getText().toString();
                String elo = add_eloday_spinner.getSelectedItem().toString();

                // se la data che hai messo è già esistente, avverti
                DatabaseReference reference = FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                        .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .child(getString(R.string.firebase_users_elotracker))
                        .child(year);

                if (newDay){
                    reference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@androidx.annotation.NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot snapshot : dataSnapshot.getChildren()){

                                // prendi tutti i giorni disponibili per quell'anno per quella regione per quello split
                                String matchDayDetails = (snapshot.getKey().toString());
                                allEloTrackedDays.add((matchDayDetails));


                            }

                            if (allEloTrackedDays.contains(date)){
                                Toast.makeText(context, "This Date is already set!", Toast.LENGTH_SHORT).show();
                            }else {
                                // ---------------------------
                                if (wins.isEmpty()){add_eloday_wins.setError("Please add wins");} else
                                if (losses.isEmpty()){add_eloday_losses.setError("Please add wins");}else
                                if (lps.isEmpty()){add_eloday_lps.setError("Please add wins");}else{



                                    EloTracker eloTracker = new EloTracker(
                                            elotracker_id,
                                            date,
                                            Integer.parseInt(wins),
                                            Integer.parseInt(losses),
                                            elo,
                                            Integer.parseInt(lps));

                                    Calendar calendar = Calendar.getInstance();
                                    int year = calendar.get(Calendar.YEAR);
                                    Log.d(TAG, "onClick: year:"+year);


                                    FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_users))
                                            .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .child(getString(R.string.firebase_users_elotracker))
                                            .child(String.valueOf(year))
                                            .child(date )
                                            .setValue(eloTracker).addOnCompleteListener(task2 -> {

                                        if (task2.isSuccessful()){
                                            Log.d(TAG, "added: Notification Region "+elotracker_id);

                                        }else{
                                            Log.d(TAG, "ERROR: registered: Notification Region "+elotracker_id);
                                        }
                                    });
                                    Intent intent = new Intent(NewTrackEloDay.this, EloTrackerActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }


                        }

                        @Override
                        public void onCancelled(@androidx.annotation.NonNull DatabaseError databaseError) {

                        }
                    });

                }else {

                    EloTracker eloTracker = new EloTracker(
                            elotracker_id,
                            date,
                            Integer.parseInt(wins),
                            Integer.parseInt(losses),
                            elo,
                            Integer.parseInt(lps));

                    reference.child(elotracker_id)
                    .setValue(eloTracker);
                    Intent intent = new Intent(NewTrackEloDay.this, EloTrackerActivity.class);
                    startActivity(intent);
                    finish();

                }










            }
        });

    }

    private void fillSpinner() {

        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, R.layout.spinner_item, eloService.getAllElos(context));
        //adapter.setDropDownViewResource( android.R.layout.simple_spinner_item);

        add_eloday_spinner.setAdapter(adapter);

    }

    private void setDate() {

        Date c = Calendar.getInstance().getTime();
        System.out.println("Current time => " + c);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String formattedDate = df.format(c);
        elotracker_id = formattedDate;
        add_eloday_date.setText(formattedDate);

        add_eloday_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Get Current Date
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                String formatted = String.format("%02d", monthOfYear + 1);
                                add_eloday_date.setText(year + "-" + formatted + "-" +dayOfMonth );

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

    }
}